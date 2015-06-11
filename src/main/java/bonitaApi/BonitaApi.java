package bonitaApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bonitaClass.Case;
import bonitaClass.Group;
import bonitaClass.Membership;
import bonitaClass.Process;
import bonitaClass.Profile;
import bonitaClass.Role;
import bonitaClass.Task;
import bonitaClass.User;

public class BonitaApi implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BonitaProxy proxy;
	private Boolean correctLogin= false;
	private User user;
	private String version= "6.2";
	
	public BonitaApi(){
		this.proxy= new BonitaProxy();
	}
	
	public BonitaApi(String serverUrl, String userName, String password){
		this.proxy= new BonitaProxy(serverUrl);
		this.proxy.setUserName(userName);
		this.proxy.setPassword(password);
		if(! this.proxy.autentificarse()){
			this.correctLogin= false;
			System.out.println("La autentificacion hacia el servidor bonita ha fallado. Controle el servidor y el usuario y password");
		}else{
			this.correctLogin= true;
		}
	}
	
	/*
	 * Getters y Setters	
	 */
	
	public Boolean getCorrectLogin() {
		return correctLogin;
	}

	public void setCorrectLogin(Boolean correctLogin) {
		this.correctLogin = correctLogin;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getCookie(){
		return this.proxy.getCookieHeader();
	}
	
	public void setCookie(String cookie){
		this.proxy.setCookieHeader(cookie);
	}
	
	public String getServerUrl(){
		return this.proxy.getServerUrl();
	}
	
	public void setServerUrl(String serverUrl){
		this.proxy.setServerUrl(serverUrl);
	}
	
	public String getUserName() {
		return proxy.getUserName();
	}

	public void setUserName(String userName) {
		this.setUserName(userName);
	}

	public String getPassword() {
		return proxy.getPassword();
	}

	public void setPassword(String password) {
		this.setPassword(password);
	}
	
	/*
	 * SECCION USERS-PROFILES-ROLES-GROUPS-MEMBERSHIPS
	 */
	
	@SuppressWarnings("unchecked")
	public User createUser(String userName, String password, String passwordConfirm, String firstName, String lastName){
		String url= "API/identity/user/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("userName", userName);
		json.put("password", password);
		json.put("password_confirm", passwordConfirm);
		json.put("firstname", firstName);
		json.put("lastname", lastName);
		json.put("enabled", "true");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		return this.mapearUser(resultado);
	}
	
	public Boolean deleteUser(long id){
		String url= "API/identity/user/"+Long.toString(id);
		String metodo= "DELETE";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean updateUser(long id, String userName, String password, String passwordConfirm, String firstName, String lastName, Boolean enabled){
		String url= "API/identity/user/"+Long.toString(id);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("userName", userName);
		json.put("password", password);
		json.put("password_confirm", passwordConfirm);
		json.put("firstname", firstName);
		json.put("lastname", lastName);
		if(enabled){json.put("enabled", "true");}
		else{json.put("enabled", "false");}
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");		
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean deactivateUser(long id){
		String url= "API/identity/user/"+Long.toString(id);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("enabled", "false");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean activeUser(long id){
		String url= "API/identity/user/"+Long.toString(id);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("enabled", "true");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	public List<User> users(int page, int amountPerPage){
		String url= "API/identity/user?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearUsers(resultado);
	}
	
	public Boolean hasPreviewPageUsers(int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageUsers(int actualPage, int amountPerPage){
		List<User> users= this.users(actualPage + 1, amountPerPage);
		return (users.size() > 0);
	}
	
	public User user(String userName){
		String url= "API/identity/user?f=userName=" + userName;
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		List<User> users= this.mapearUsers(resultado);
		User user= null;
		if(users.size() > 0){
			user= users.get(0);
		}
		return user;
	}
	
	public User actualUser(){
		if(this.user == null){
			this.user= this.user(this.proxy.getUserName());
		}
		return this.user;
	}
	
	public List<Role> roles(int page, int amountPerPage){
		String url= "API/identity/role?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearRoles(resultado);
	}
	
	public Boolean hasPreviewPageRoles(int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageRoles(int actualPage, int amountPerPage){
		List<Role> roles= this.roles(actualPage + 1, amountPerPage);
		return (roles.size() > 0);
	}
	
	public Role role(long id){
		Role role= null;
		for(Role roleList: this.roles(0,1000)){
			if(roleList.getId() == id){
				role= roleList;
				break;
			}
		}
		return role;
	}
	
	public Role role(String name){
		String url= "API/identity/role?f=name=" + name;
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		List<Role> roles= this.mapearRoles(resultado);
		Role role= null;
		if(roles.size() > 0){
			role= roles.get(0);
		}
		return role;
	}
	
	public List<Group> groups(int page, int amountPerPage){
		String url= "API/identity/group?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearGroups(resultado);
	}
	
	public Boolean hasPreviewPageGroups(int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageGroups(int actualPage, int amountPerPage){
		List<Group> groups= this.groups(actualPage + 1, amountPerPage);
		return (groups.size() > 0);
	}
	
	public Group group(long id){
		Group group= null;
		for(Group groupList: this.groups(0,1000)){
			if(groupList.getId() == id){
				group= groupList;				
				break;
			}
		}
		return group;
	}
	
	public Group group(String name){
		String url= "API/identity/group?f=name=" + name;
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		List<Group> groups= this.mapearGroups(resultado);
		Group group= null;
		if(groups.size() > 0){
			group= groups.get(0);
		}
		return group;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean addMembership(long userId, long roleId, long groupId){
		String url= "API/identity/membership/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("user_id", userId);
		json.put("role_id", roleId);
		json.put("group_id", groupId);
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Membership> memberships(long userId, int page, int amountPerPage){
		String url= "API/identity/membership?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearMemberships(resultado);
	}
	
	public Boolean hasPreviewPageMemberships(long userId, int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageMemberships(long userId, int actualPage, int amountPerPage){
		List<Membership> memberships= this.memberships(userId, actualPage + 1, amountPerPage);
		return (memberships.size() > 0);
	}
	
	public List<Profile> profiles(){
		//String url= "API/portal/profile?p=0&c=100"; desde 6.3 para adelante
		String url= "API/userXP/profile?p=0&c=100";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProfiles(resultado);
	}
	
	public List<Profile> userProfiles(long userId){
		//String url= "API/portal/profile?f=user_id=103"; desde 6.3 para adelante
		String url= "API/userXP/profile?f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProfiles(resultado);
	}
	
	public Boolean hasProfile(long userId, String profileName){
		String url= "API/userXP/profile?f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		List<Profile> profiles= this.mapearProfiles(resultado);
		Boolean has= false;
		for(Profile profile : profiles){
			if(profile.getName().equals(profileName)){
				has=true;
				break;
			}
		}
		return has;
	}
	
	/*
	 * SECCION DEPLOYED PROCESSES - APPLICATIONS
	 */
	
	public List<Process> deployedProcesses(){
		String url= "API/bpm/process?p=0&c=1000&o=displayName%20ASC";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProcesses(resultado);
	}
	
	public Process deployedProcess(long processId){
		String url= "API/bpm/process?p=0&c=1000&o=displayName%20ASC";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		List<Process> processes= this.mapearProcesses(resultado);
		Process process= null;
		for(Process process_for: processes){
			if(process_for.getId() == processId){
				process= process_for;
			}
		}
		return process;
	}
	
	public List<Process> deployedProccessForUser(long userId){
		String url= "API/bpm/process?p=0&c=1000&o=displayName%20ASC&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProcesses(resultado);
	}
	
	@SuppressWarnings("unchecked")
	public Boolean disableProcess(long processId){
		String url= "API/bpm/process/"+Long.toString(processId);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("activationState", "DISABLED");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean enableProcess(long processId){
		String url= "API/bpm/process/"+Long.toString(processId);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("activationState", "ENABLED");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	public Boolean deleteProcess(long processId){
		String url= "API/bpm/process/"+Long.toString(processId);
		String metodo= "DELETE";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * SECCIONES CASES
	 */
	public List<Case> cases(int page, int amountPerPage){
		String url= "API/bpm/case?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	public Boolean hasPreviewPageCases(int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageCases(int actualPage, int amountPerPage){
		List<Case> cases= this.cases(actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	
	public List<Case> cases(long userId, int page, int amountPerPage){
		String url= "API/bpm/case?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	public Boolean hasPreviewPageCases(long userId, int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageCases(long userId, int actualPage, int amountPerPage){
		List<Case> cases= this.cases(userId, actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	@SuppressWarnings("unchecked")
	public Case startCase(long processId){		
		String url= "API/bpm/case/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("processDefinitionId", Long.toString(processId));
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		return this.mapearCase(resultado);
	}
	
	public Boolean deleteCase(long caseId){
		String url= "API/bpm/case/"+Long.toString(caseId);
		String metodo= "DELETE";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Case> archivedCases(long userId, int page, int amountPerPage){
		String url= "API/bpm/archivedCase?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	public Boolean hasPreviewPageArchivedCases(long userId, int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageArchivedCases(long userId, int actualPage, int amountPerPage){
		List<Case> cases= this.archivedCases(userId, actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	/*
	 * SECCION TASKS
	 */
	public List<Task> tasks(int page, int amountPerPage){
		String url= "API/bpm/humanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	public Boolean hasPreviewPageTasks(int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageTasks(int actualPage, int amountPerPage){
		List<Task> tasks= this.tasks(actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
	
	
	public List<Task> tasks(long userId, int page, int amountPerPage){
		String url= "API/bpm/humanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=state=ready&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	public Boolean hasPreviewPageTasks(long userId, int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageTasks(long userId, int actualPage, int amountPerPage){
		List<Task> tasks= this.tasks(userId, actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
	
	public Task task(long taskId){
		String url= "API/bpm/humanTask/" + Long.toString(taskId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTask(resultado);
	}
	
	@Deprecated
	public Task task(long userId, long taskId){
		String url= "API/bpm/humanTask?p=0&c=10000&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		List<Task> tasks= this.mapearTasks(resultado);
		Task task= null;
		for(Task taskA: tasks){
			if(taskA.getId() == taskId){
				task= taskA;
			}
		}
		return task;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean assignTask(long taskId, long userId){
		String url= "API/bpm/humanTask/" + Long.toString(taskId);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("assigned_id", Long.toString(userId));
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Boolean releaseTask(long taskId){
		String url= "API/bpm/humanTask/" + Long.toString(taskId);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("assigned_id", "");
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Task> archivedHumanTask(long userId, int page, int amountPerPage){
		String url= "API/bpm/archivedHumanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=assigned_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	public Boolean hasPreviewPageArchivedHumanTask(long userId, int actualPage, int amountPerPage){
		return this.previewPage(actualPage, amountPerPage);
	}
	
	public Boolean hasNextPageArchivedHumanTask(long userId, int actualPage, int amountPerPage){
		List<Task> tasks= this.archivedHumanTask(userId, actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
		
	
	/*
	 * METODOS PRIVADOS
	 */
	
	private Boolean previewPage(int actualPage, int amountPerPage){
		actualPage--;
		if(actualPage > 0){
			return true;
		}else{
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Process> mapearProcesses(String jsonString){	
		List<Process> processes= new ArrayList<Process>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Process process= new Process();
				process.setId(Long.parseLong((String)userJson.get("id")));
				process.setName((String) userJson.get("name"));
				process.setDescription((String) userJson.get("description"));
				process.setDisplayName((String) userJson.get("displayName"));
				process.setDisplayDescription((String) userJson.get("displayDescription"));
				process.setState((String) userJson.get("activationState"));
				process.setVersion((String) userJson.get("version"));
				processes.add(process);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return processes;
	}
	
	@SuppressWarnings("unchecked")
	private List<User> mapearUsers(String jsonString){	
		List<User> users= new ArrayList<User>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				User user= new User();
				user.setId(Long.parseLong((String)userJson.get("id")));
				user.setFirstName((String) userJson.get("firstname"));
				user.setLastName((String) userJson.get("lastname"));
				user.setUserName((String) userJson.get("userName"));
				user.setEnabled(Boolean.valueOf((String) userJson.get("enabled")));
				users.add(user);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	private User mapearUser(String jsonString){
		try {
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			User user= new User();
			user.setId(Long.parseLong((String)json.get("id")));
			user.setFirstName((String) json.get("firstname"));
			user.setLastName((String) json.get("lastname"));
			user.setUserName((String) json.get("userName"));
			user.setEnabled(Boolean.valueOf((String) json.get("enabled")));
			return user;
		}catch(Exception e){
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Role> mapearRoles(String jsonString){	
		List<Role> roles= new ArrayList<Role>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Role role= new Role();
				role.setId(Long.parseLong((String)userJson.get("id")));
				role.setName((String) userJson.get("name"));
				role.setDisplayName((String) userJson.get("displayName"));
				role.setDescription((String) userJson.get("description"));
				roles.add(role);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return roles;
	}
	
	@SuppressWarnings("unchecked")
	private List<Group> mapearGroups(String jsonString){	
		List<Group> groups= new ArrayList<Group>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Group group= new Group();
				group.setId(Long.parseLong((String)userJson.get("id")));
				group.setName((String) userJson.get("name"));
				group.setDisplayName((String) userJson.get("displayName"));
				group.setDescription((String) userJson.get("description"));
				groups.add(group);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return groups;
	}
	
	@SuppressWarnings("unchecked")
	private List<Membership> mapearMemberships(String jsonString){	
		List<Membership> memberships= new ArrayList<Membership>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Membership membership= new Membership();
				membership.setUserId(Long.parseLong((String)userJson.get("user_id")));
				membership.setRole(this.role(Long.parseLong((String)userJson.get("role_id"))));
				membership.setGroup(this.group(Long.parseLong((String)userJson.get("group_id"))));
				memberships.add(membership);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return memberships;
	}
	
	@SuppressWarnings("unchecked")
	private List<Profile> mapearProfiles(String jsonString){	
		List<Profile> profiles= new ArrayList<Profile>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Profile profile= new Profile();
				profile.setId(Long.parseLong((String)userJson.get("id")));
				profile.setName((String)userJson.get("name"));
				profile.setDescription((String)userJson.get("description"));
				profiles.add(profile);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return profiles;
	}
	
	@SuppressWarnings("unchecked")
	private List<Case> mapearCases(String jsonString){	
		List<Case> cases= new ArrayList<Case>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject caseJson= it.next();
				Case caso= new Case();
				caso.setId(Long.parseLong((String)caseJson.get("id")));
				caso.setState((String) caseJson.get("state"));
				caso.setBeginDate((String) caseJson.get("start"));
				caso.setEndDate((String) caseJson.get("end_date"));
				caso.setStartedBy(Long.parseLong((String)caseJson.get("started_by")));
				caso.setProcess(this.deployedProcess(Long.parseLong((String)caseJson.get("processDefinitionId"))));
				cases.add(caso);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return cases;
	}
	
	private Case mapearCase(String jsonString){
		try{
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			
			Case caso= new Case();
			caso.setId(Long.parseLong((String)json.get("id")));
			caso.setState((String) json.get("state"));
			caso.setBeginDate((String) json.get("start"));
			caso.setEndDate((String) json.get("end_date"));
			caso.setStartedBy(Long.parseLong((String)json.get("started_by")));
			caso.setProcess(this.deployedProcess(Long.parseLong((String)json.get("processDefinitionId"))));
			return caso;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Task> mapearTasks(String jsonString){	
		List<Task> tasks= new ArrayList<Task>();
		
		try {
			JSONParser par= new JSONParser();  		
			JSONArray json= (JSONArray) par.parse(jsonString);
			Iterator<JSONObject> it= json.iterator();
			while(it.hasNext()){
				JSONObject userJson= it.next();
				Task task= new Task();
				task.setId(Long.parseLong((String)userJson.get("id")));
				task.setName((String) userJson.get("name"));
				task.setDescription((String) userJson.get("description"));
				task.setDisplayName((String) userJson.get("displayName"));
				task.setDisplayDescription((String) userJson.get("displayDescription"));
				task.setType((String) userJson.get("type"));
				task.setPriority((String) userJson.get("priority"));				
				task.setState((String) userJson.get("state"));
				task.setDueDate((String) userJson.get("dueDate"));
				task.setExecutedDate((String) userJson.get("reached_state_date"));
				task.setExecutedBy(Long.parseLong((String)userJson.get("executedBy")));				
				task.setCaseId(Long.parseLong((String)userJson.get("caseId")));
				task.setActorId(Long.parseLong((String)userJson.get("actorId")));
				long assignedId= 0;
				if(! ((String)userJson.get("assigned_id")).isEmpty()){
					assignedId= Long.parseLong((String)userJson.get("assigned_id"));
				}
				task.setAssignedId(assignedId);
				task.setProcess(this.deployedProcess(Long.parseLong((String)userJson.get("processId"))));
				tasks.add(task);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return tasks;
	}
	
	private Task mapearTask(String jsonString){
		try{
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			
			Task task= new Task();
			task.setId(Long.parseLong((String)json.get("id")));
			task.setName((String) json.get("name"));
			task.setDescription((String) json.get("description"));
			task.setDisplayName((String) json.get("displayName"));
			task.setDisplayDescription((String) json.get("displayDescription"));
			task.setType((String) json.get("type"));
			task.setPriority((String) json.get("priority"));				
			task.setState((String) json.get("state"));
			task.setDueDate((String) json.get("dueDate"));
			task.setExecutedDate((String) json.get("reached_state_date"));
			task.setExecutedBy(Long.parseLong((String)json.get("executedBy")));				
			task.setCaseId(Long.parseLong((String)json.get("caseId")));
			task.setActorId(Long.parseLong((String)json.get("actorId")));
			long assignedId= 0;
			if(! ((String)json.get("assigned_id")).isEmpty()){
				assignedId= Long.parseLong((String)json.get("assigned_id"));
			}
			task.setAssignedId(assignedId);
			task.setProcess(this.deployedProcess(Long.parseLong((String)json.get("processId"))));
			return task;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
