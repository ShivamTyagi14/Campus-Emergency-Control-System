package com.campus.emergency.service;

import com.campus.emergency.model.*;
import com.campus.emergency.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EmergencyService {
    private final EmergencyRepository emergencies;
    private final TeamRepository teams;
    private final HistoryRepository history;
    private final AssignmentRepository assignments;

    public EmergencyService(EmergencyRepository emergencies,TeamRepository teams,HistoryRepository history,AssignmentRepository assignments){
        this.emergencies=emergencies;this.teams=teams;this.history=history;this.assignments=assignments;
    }

    // PriorityQueue ensures P1 incidents are selected before lower-priority incidents.
    public List<Emergency> getPrioritizedEmergencies(){
        PriorityQueue<Emergency> queue=new PriorityQueue<>(Comparator
            .comparingInt((Emergency e)->priorityRank(e.getPriority()))
            .thenComparing(Emergency::getReportedAt));
        for(Emergency e: emergencies.findAll()) if(e.getStatus()!=EmergencyStatus.RESOLVED) queue.add(e);
        List<Emergency> result=new ArrayList<>();
        while(!queue.isEmpty()) result.add(queue.poll());
        return result;
    }
    private int priorityRank(PriorityLevel p){return p==null?9:switch(p){case P1_CRITICAL->1;case P2_HIGH->2;case P3_MEDIUM->3;case P4_LOW->4;};}

    public PriorityLevel calculatePriority(String type,String severity,Integer people){
        int score=0;
        String t=type.toLowerCase();
        if(t.contains("fire")||t.contains("medical")||t.contains("security")) score+=4;
        if("critical".equalsIgnoreCase(severity)) score+=4;
        else if("high".equalsIgnoreCase(severity)) score+=3;
        else if("medium".equalsIgnoreCase(severity)) score+=2;
        else score+=1;
        if(people!=null && people>=5) score+=2;
        if(score>=8)return PriorityLevel.P1_CRITICAL;
        if(score>=6)return PriorityLevel.P2_HIGH;
        if(score>=4)return PriorityLevel.P3_MEDIUM;
        return PriorityLevel.P4_LOW;
    }

    public EmergencyResponse handlerFor(Emergency e){
        if(e.getType().toLowerCase().contains("fire")) return new FireEmergencyHandler(e);
        if(e.getType().toLowerCase().contains("medical")) return new MedicalEmergencyHandler(e);
        return new GeneralEmergencyHandler(e);
    }

    @Transactional
    public Emergency create(Emergency e){
        e.setPriority(calculatePriority(e.getType(),e.getSeverity(),e.getPeopleAffected()));
        Emergency saved=emergencies.save(e);
        history.save(new EmergencyHistory(saved.getId(),"Emergency reported — "+saved.getPriority(),saved.getReporterEmail()));
        return saved;
    }
    @Transactional
    public Emergency assign(Long emergencyId,Long teamId){
        Emergency e=find(emergencyId);
        EmergencyTeam team=teams.findById(teamId).orElseThrow(()->new IllegalArgumentException("Team not found"));
        if(!team.isAvailable()) throw new IllegalStateException("Selected team is not available");
        team.setAvailable(false); teams.save(team);
        e.setAssignedTeamId(team.getId());e.setAssignedTeamName(team.getTeamName());
        e.setEtaMinutes(team.getEstimatedMinutes());e.setStatus(EmergencyStatus.ASSIGNED);
        Emergency saved=emergencies.save(e);
        assignments.save(new TeamAssignment(e.getId(),team.getId()));
        history.save(new EmergencyHistory(e.getId(),"Assigned to "+team.getTeamName(),"ADMIN"));
        return saved;
    }
    @Transactional
    public Emergency updateStatus(Long id,EmergencyStatus status,String changedBy){
        Emergency e=find(id); e.setStatus(status);
        if(status==EmergencyStatus.RESOLVED){
            e.setResolvedAt(LocalDateTime.now());
            if(e.getAssignedTeamId()!=null) teams.findById(e.getAssignedTeamId()).ifPresent(t->{t.setAvailable(true);teams.save(t);});
        }
        Emergency saved=emergencies.save(e);
        history.save(new EmergencyHistory(id,"Status changed to "+status,changedBy));
        return saved;
    }
    public Emergency find(Long id){return emergencies.findById(id).orElseThrow(()->new EmergencyNotFoundException(id));}
    public List<Emergency> all(){return emergencies.findAllByOrderByReportedAtDesc();}
    public List<Emergency> byReporter(String email){return emergencies.findByReporterEmailOrderByReportedAtDesc(email);}
    public List<EmergencyHistory> history(Long id){return history.findByEmergencyIdOrderByChangedAtDesc(id);}
    public List<EmergencyTeam> availableTeams(){return teams.findByAvailableTrue();}
    public Map<String,Object> stats(){
        List<Emergency> all=emergencies.findAll();
        Map<String,Object> map=new HashMap<>();
        map.put("total",all.size());
        map.put("active",all.stream().filter(e->e.getStatus()!=EmergencyStatus.RESOLVED).count());
        map.put("resolved",all.stream().filter(e->e.getStatus()==EmergencyStatus.RESOLVED).count());
        map.put("critical",all.stream().filter(e->e.getPriority()==PriorityLevel.P1_CRITICAL).count());
        map.put("availableTeams",teams.findByAvailableTrue().size());
        return map;
    }
}
