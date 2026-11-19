let me=null, currentPage='dashboard';
const $=id=>document.getElementById(id);
async function api(url,options={}) {
 const res=await fetch('/api'+url,{...options,credentials:'same-origin',headers:{'Content-Type':'application/json',...(options.headers||{})}});
 const data=await res.json().catch(()=>({}));
 if(!res.ok) throw new Error(data.message||data.error||'Request failed');
 return data;
}
function toast(msg){$('toast').textContent=msg;$('toast').classList.add('show');setTimeout(()=>$('toast').classList.remove('show'),3200)}
function showAuth(which){$('loginForm').classList.toggle('d-none',which!=='login');$('registerForm').classList.toggle('d-none',which!=='register');document.querySelectorAll('.tab').forEach((b,i)=>b.classList.toggle('active',i===(which==='login'?0:1)))}
async function login(e){e.preventDefault();try{me=await api('/auth/login',{method:'POST',body:JSON.stringify({email:$('loginEmail').value,password:$('loginPassword').value})});openApp()}catch(err){toast(err.message)}}
async function register(e){e.preventDefault();try{await api('/auth/register',{method:'POST',body:JSON.stringify({name:$('regName').value,email:$('regEmail').value,password:$('regPassword').value})});toast('Account created. Please sign in.');showAuth('login')}catch(err){toast(err.message)}}
async function logout(){try{await api('/auth/logout',{method:'POST'})}catch(e){}me=null;$('appView').classList.add('d-none');$('authView').classList.remove('d-none')}
async function openApp(){
 $('authView').classList.add('d-none');$('appView').classList.remove('d-none');
 $('profileName').textContent=me.name;$('profileRole').textContent=me.role;$('avatar').textContent=me.name[0].toUpperCase();
 document.querySelectorAll('.admin-only').forEach(el=>el.classList.toggle('d-none',me.role!=='ADMIN'));
 await loadLocations();navigate('dashboard');
}
function navigate(page){
 currentPage=page;['dashboard','report','incidents','teams'].forEach(p=>$(p+'Page').classList.toggle('d-none',p!==page));
 document.querySelectorAll('.nav-btn').forEach(b=>b.classList.toggle('active',b.dataset.page===page));
 $('pageTitle').textContent=({dashboard:'Overview',report:'Report emergency',incidents:'Incident records',teams:'Response teams'})[page];
 if(page==='dashboard')loadDashboard();if(page==='incidents')loadAll();if(page==='teams')loadTeams();
}
async function loadLocations(){try{let locs=await api('/locations');$('location').innerHTML='<option value="">Select location</option>'+locs.map(l=>`<option>${esc(l.name)}</option>`).join('')}catch(e){}}
function priorityClass(p){return p?.startsWith('P1')?'p1':p?.startsWith('P2')?'p2':p?.startsWith('P3')?'p3':'p4'}
function statusClass(s){return s==='RESOLVED'?'s-resolved':'s-active'}
function fmtDate(v){return v?new Date(v).toLocaleString():'—'}
function esc(v){return String(v??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
function priorityName(p){return (p||'PENDING').replace('_',' ')}
async function loadDashboard(){
 try{
  let rows=await api(me.role==='ADMIN'?'/emergencies/prioritized':'/emergencies');
  if(me.role==='ADMIN'){let s=await api('/admin/stats');$('statTotal').textContent=s.total;$('statActive').textContent=s.active;$('statCritical').textContent=s.critical;$('statTeams').textContent=s.availableTeams}
  else{$('statTotal').textContent=rows.length;$('statActive').textContent=rows.filter(x=>x.status!=='RESOLVED').length;$('statCritical').textContent=rows.filter(x=>x.priority==='P1_CRITICAL').length;$('statTeams').textContent='—'}
  $('dashboardRows').innerHTML=rows.slice(0,7).map(e=>`<tr><td><b>#${e.id}</b><span class="sub">${esc(e.type)}</span></td><td>${esc(e.location)}</td><td><span class="priority ${priorityClass(e.priority)}">${priorityName(e.priority)}</span></td><td><span class="status ${statusClass(e.status)}">${e.status}</span></td><td>${fmtDate(e.reportedAt)}</td><td><button class="small-action" onclick="showDetails(${e.id})">Details</button></td></tr>`).join('')||'<tr><td colspan="6" class="empty">No emergency reports yet.</td></tr>';
 }catch(e){toast(e.message)}
}
async function submitEmergency(e){
 e.preventDefault();try{
  let result=await api('/emergencies',{method:'POST',body:JSON.stringify({type:$('type').value,location:$('location').value,severity:$('severity').value,description:$('description').value,peopleAffected:Number($('people').value)})});
  let advice=await api('/instructions/'+encodeURIComponent(result.type));
  alert('REPORT SUBMITTED — '+result.priority+'\n\nImmediate safety guidance:\n'+advice.instruction+'\n\nYour incident ID is #'+result.id);
  $('emergencyForm').reset();navigate('incidents');
 }catch(err){toast(err.message)}
}
async function loadAll(){
 try{
  let rows=await api(me.role==='ADMIN'?'/emergencies/prioritized':'/emergencies');
  $('incidentRows').innerHTML=rows.map(e=>{
   let actions='';
   if(me.role==='ADMIN'&&e.status!=='RESOLVED')actions=`<button class="small-action" onclick="assignTeam(${e.id})">Assign</button>`;
   if(me.role==='TEAM'&&e.status!=='RESOLVED')actions=`<select class="form-select form-select-sm mt-1" onchange="updateStatus(${e.id},this.value)"><option value="">Update status</option><option>ACCEPTED</option><option>ON_THE_WAY</option><option>ARRIVED</option><option>RESOLVED</option></select>`;
   return `<tr><td>#${e.id}</td><td><b>${esc(e.type)}</b><span class="sub">${esc(e.reporterName)}</span></td><td>${esc(e.location)}</td><td><span class="priority ${priorityClass(e.priority)}">${priorityName(e.priority)}</span></td><td>${esc(e.assignedTeamName||'Unassigned')}</td><td>${e.etaMinutes?e.etaMinutes+' min':'—'}</td><td><span class="status ${statusClass(e.status)}">${e.status}</span><div class="mt-2">${actions}</div></td></tr>`;
  }).join('')||'<tr><td colspan="7" class="empty">No records found.</td></tr>';
 }catch(e){toast(e.message)}
}
async function assignTeam(id){
 try{let teams=await api('/teams/available');if(!teams.length){toast('No teams currently available');return}
 let choice=prompt('Available teams:\n'+teams.map(t=>`${t.id}. ${t.teamName} (${t.specialty})`).join('\n')+'\nEnter team ID:');
 if(!choice)return;await api(`/emergencies/${id}/assign`,{method:'POST',body:JSON.stringify({teamId:Number(choice)})});toast('Response team assigned');loadAll();loadDashboard();
 }catch(e){toast(e.message)}
}
async function updateStatus(id,status){if(!status)return;try{await api(`/emergencies/${id}/status`,{method:'POST',body:JSON.stringify({status})});toast('Incident status updated');loadAll()}catch(e){toast(e.message)}}
async function loadTeams(){
 try{let teams=await api('/teams');$('teamGrid').innerHTML=teams.map(t=>`<div class="team-card"><div class="d-flex justify-content-between"><h5>${esc(t.teamName)}</h5><span class="team-status" style="color:${t.available?'#65d69d':'#ff737d'}">${t.available?'● AVAILABLE':'● BUSY'}</span></div><p>${esc(t.specialty)} response unit</p><small class="muted">Estimated response: ${t.estimatedMinutes} minutes</small></div>`).join('')}
 catch(e){toast(e.message)}
}
async function showDetails(id){
 try{let e=await api('/emergencies/'+id);let history=await api(`/emergencies/${id}/history`);
 alert(`INCIDENT #${e.id}\nType: ${e.type}\nLocation: ${e.location}\nPriority: ${e.priority}\nStatus: ${e.status}\nTeam: ${e.assignedTeamName||'Not assigned'}\nETA: ${e.etaMinutes??'—'} minutes\n\nDescription: ${e.description}\n\nHISTORY\n${history.map(h=>h.action+' — '+fmtDate(h.changedAt)).join('\n')}`);
 }catch(err){toast(err.message)}
}
(async()=>{try{me=await api('/auth/me');openApp()}catch(e){}})();
