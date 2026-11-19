package com.campus.emergency.service;
import com.campus.emergency.model.*;
import com.campus.emergency.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    public AuthService(UserRepository users){this.users=users;}
    public AppUser register(String name,String email,String password){
        if(users.findByEmail(email).isPresent())throw new IllegalArgumentException("Email is already registered");
        return users.save(new AppUser(name,email,encoder.encode(password),Role.USER));
    }
    public AppUser login(String email,String password){
        AppUser user=users.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Invalid email or password"));
        if(!encoder.matches(password,user.getPassword()))throw new IllegalArgumentException("Invalid email or password");
        return user;
    }
    public Map<String,Object> safeUser(AppUser u){
        return Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole(),"teamId",u.getTeamId()!=null?u.getTeamId():0);
    }
}