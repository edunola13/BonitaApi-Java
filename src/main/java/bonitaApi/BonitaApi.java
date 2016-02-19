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
	private String version= "6.2";						//Opciones 6.1-6.2-6.3-6.4-6.5
	
	public BonitaApi(){
		this.proxy= new BonitaProxy();
	}
	
	public BonitaApi(String version, String serverUrl, String userName, String password){
		this.version= version;
		this.proxy= new BonitaProxy(serverUrl);
		this.proxy.setUserName(userName);
		this.proxy.setPassword(password);
		if(! this.proxy.autentificarse()){
			this.correctLogin= false;
			System.out.println("La autentificacion hacia el servidor bonita ha fallado. Controle el servidor, el usuario y password");
		}else{
			this.correctLogin= true;
		}
	}
	
	/**
	 * Logout 
	 */
	public Boolean logout(){
		return this.proxy.logout();
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
	
	/**
	 * Create a new User
	 * @param userName
	 * @param password
	 * @param passwordConfirm
	 * @param firstName
	 * @param lastName
	 * @return
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
	
	/**
	 * Delete a User with Id
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Update a User with Id
	 * @param id
	 * @param userName
	 * @param password
	 * @param passwordConfirm
	 * @param firstName
	 * @param lastName
	 * @param enabled
	 * @return
	 */
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
	
	/**
	 * Deactivate a User with Id
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Activate a User with Id
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Paged User list ordered by lastname
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<User> users(int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc) order= "DESC";
		String url= "API/identity/user?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&o=lastname%20" + order + "&s=" + search;
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearUsers(resultado);
	}
	
	/**
	 * Paged User list ordered by lastname
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<User> users(int page, int amountPerPage){
		return users(page, amountPerPage, true, "");
	}
	
	/**
	 * Return if User list has previous page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageUsers(int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if User list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageUsers(int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<User> users= this.users(actualPage + 1, amountPerPage, orderAsc, search);
		return (users.size() > 0);
	}
	
	/**
	 * Return if User list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageUsers(int actualPage, int amountPerPage){
		List<User> users= this.users(actualPage + 1, amountPerPage);
		return (users.size() > 0);
	}
	
	/**
	 * Return a User with UserName
	 * @param userName
	 * @return
	 */
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
	
	/**
	 * Return actual log User
	 * @return
	 */
	public User actualUser(){
		if(this.user == null){
			this.user= this.user(this.proxy.getUserName());
		}
		return this.user;
	}
	
	/**
	 * Paged Role list ordered by displayName
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Role> roles(int page, int amountPerPage){
		String url= "API/identity/role?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&o=displayName%20ASC";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearRoles(resultado);
	}
	
	/**
	 * Return if Role list has previous page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageRoles(int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Role list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageRoles(int actualPage, int amountPerPage){
		List<Role> roles= this.roles(actualPage + 1, amountPerPage);
		return (roles.size() > 0);
	}
	
	/**
	 * Return Role with Id
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Return Role with name
	 * @param name
	 * @return
	 */
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
	
	/**
	 * Create a Role
	 * @param roleName
	 * @param displayName
	 * @param description
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Role createRole(String roleName, String displayName, String description){
		String url= "API/identity/role/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("icon", "");
		json.put("name", roleName);
		json.put("displayName", displayName);
		json.put("description", description);
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		return this.mapearRole(resultado);
	}
	
	/**
	 * Update Role
	 * @param id
	 * @param roleName
	 * @param displayName
	 * @param description
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateRole(long id, String roleName, String displayName, String description){
		String url= "API/identity/role/" + Long.toString(id);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("name", roleName);
		json.put("displayName", displayName);
		json.put("description", description);
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Delete Rule
	 * @param id
	 * @return
	 */
	public Boolean deleteRole(long id){
		String url= "API/identity/role/" + Long.toString(id);
		String metodo= "DELETE";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Paged Group list ordered by displayName
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Group> groups(int page, int amountPerPage){
		String url= "API/identity/group?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&o=displayName%20ASC";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearGroups(resultado);
	}
	
	/**
	 * Return if Group list has previous page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageGroups(int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Group has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageGroups(int actualPage, int amountPerPage){
		List<Group> groups= this.groups(actualPage + 1, amountPerPage);
		return (groups.size() > 0);
	}
	
	/**
	 * Return Group with Id
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Return Group with Name
	 * @param name
	 * @return
	 */
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
	
	/**
	 * Create a Group
	 * @param groupName
	 * @param displayName
	 * @param description
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Group createGroup(String groupName, String displayName, String description){
		String url= "API/identity/group/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("icon", "");
		json.put("name", groupName);
		json.put("displayName", displayName);
		json.put("description", description);
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		return this.mapearGroup(resultado);
	}
	
	/**
	 * Update Role
	 * @param id
	 * @param groupName
	 * @param displayName
	 * @param description
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateGroup(long id, String groupName, String displayName, String description){
		String url= "API/identity/group/" + Long.toString(id);
		String metodo= "PUT";
		JSONObject json= new JSONObject();
		json.put("name", groupName);
		json.put("displayName", displayName);
		json.put("description", description);
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Delete Rule
	 * @param id
	 * @return
	 */
	public Boolean deleteGroup(long id){
		String url= "API/identity/group/" + Long.toString(id);
		String metodo= "DELETE";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		if(! resultado.equals("error")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Add a new Membership
	 * @param userId
	 * @param roleId
	 * @param groupId
	 * @return
	 */
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
	
	/**
	 * Memberships list of a User
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Membership> memberships(long userId, int page, int amountPerPage){
		String url= "API/identity/membership?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId) + "&d=role_id&d=group_id";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearMemberships(resultado);
	}
	
	/**
	 * Return if Membership list has previous page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageMemberships(long userId, int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Membership list has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageMemberships(long userId, int actualPage, int amountPerPage){
		List<Membership> memberships= this.memberships(userId, actualPage + 1, amountPerPage);
		return (memberships.size() > 0);
	}
		
	/**
	 * Profile list
	 * @return
	 */
	public List<Profile> profiles(){
		String url= "API/userXP/profile?p=0&c=100";
		if(this.version.equals("6.1") && this.version.equals("6.2")){
			url= "API/portal/profile?p=0&c=100";
		}
		
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProfiles(resultado);
	}
	
	/**
	 * Profile list of a User
	 * @param userId
	 * @return
	 */
	public List<Profile> userProfiles(long userId){
		String url= "API/userXP/profile?f=user_id=" + Long.toString(userId);
		if(this.version.equals("6.1") && this.version.equals("6.2")){
			url= "API/portal/profile?f=user_id=" + Long.toString(userId);
		}
		
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProfiles(resultado);
	}
	
	/**
	 * Return if a User has a Profile
	 * @param userId
	 * @param profileName
	 * @return
	 */
	public Boolean hasProfile(long userId, String profileName){
		String url= "API/userXP/profile?f=user_id=" + Long.toString(userId);
		if(this.version.equals("6.1") && this.version.equals("6.2")){
			url= "API/portal/profile?f=user_id=" + Long.toString(userId);
		}
		
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
	
	/**
	 * Deployed Process list
	 * @return
	 */
	public List<Process> deployedProcesses(){
		String url= "API/bpm/process?p=0&c=1000&o=displayName%20ASC";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProcesses(resultado);
	}
	
	/**
	 * Return Deployed Process with Id
	 * @param processId
	 * @return
	 */
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
	
	/**
	 * Return Deployed Process for a User
	 * @param userId
	 * @return
	 */
	public List<Process> deployedProccessForUser(long userId){
		String url= "API/bpm/process?p=0&c=1000&o=displayName%20ASC&f=user_id=" + Long.toString(userId);
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		return this.mapearProcesses(resultado);
	}
	
	/**
	 * Disable a Process with Id
	 * @param processId
	 * @return
	 */
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
	
	/**
	 * Enable a Process with Id
	 * @param processId
	 * @return
	 */
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
	
	/**
	 * Delete a Process with Id
	 * @param processId
	 * @return
	 */
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
	
	/**
	 * Paged Cases list ordered by name
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> cases(int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/case?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&o=name%20" + order + "&s=" + search + "&d=processDefinitionId";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	/**
	 * Paged Cases list ordered by name
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> cases(int page, int amountPerPage){
		return this.cases(page, amountPerPage, true, "");
	}
	
	/**
	 * Return if Cases list has previous page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageCases(int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Cases list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageCases(int actualPage, int amountPerPage){
		List<Case> cases= this.cases(actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	/**
	 * Return if Cases list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageCases(int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<Case> cases= this.cases(actualPage + 1, amountPerPage, orderAsc, search);
		return (cases.size() > 0);
	}
	
	/**
	 * Paged Cases list for a User ordered by name
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> cases(long userId, int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/case?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId) + "&o=name%20" + order + "&s=" + search + "&d=processDefinitionId";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	/**
	 * Paged Cases list for a User ordered by name
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> cases(long userId, int page, int amountPerPage){
		return this.cases(userId, page, amountPerPage, true, "");
	}
	
	/**
	 * Return if Cases list for a User has previous page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageCases(long userId, int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Cases list for a User has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageCases(long userId, int actualPage, int amountPerPage){
		List<Case> cases= this.cases(userId, actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	/**
	 * Star a case of a Process Id
	 * @param processId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Case startCase(long processId){		
		String url= "API/bpm/case/";
		String metodo= "POST";
		JSONObject json= new JSONObject();
		json.put("processDefinitionId", Long.toString(processId));
		String resultado= this.proxy.enviarPeticion(url, metodo, json.toJSONString(), "application/json");
		return this.mapearCase(resultado);
	}
	
	/**
	 * Delete a Case (Instance of a Process)
	 * @param caseId
	 * @return
	 */
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
	
	/**
	 * Paged Archived Cases list of a User
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> archivedCases(long userId, int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/archivedCase?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=user_id=" + Long.toString(userId) + "&o=endDate%20" + order + "&s=" + search + "&d=processDefinitionId";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearCases(resultado);
	}
	
	/**
	 * Paged Archived Cases list of a User
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Case> archivedCases(long userId, int page, int amountPerPage){
		return this.archivedCases(userId, page, amountPerPage, false, "");
	}
	
	/**
	 * Return if Archived Cases list has a previous page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageArchivedCases(long userId, int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Archived Cases list has a next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageArchivedCases(long userId, int actualPage, int amountPerPage){
		List<Case> cases= this.archivedCases(userId, actualPage + 1, amountPerPage);
		return (cases.size() > 0);
	}
	
	/**
	 * Return if Archived Cases list has a next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageArchivedCases(long userId, int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<Case> cases= this.archivedCases(userId, actualPage + 1, amountPerPage, orderAsc, search);
		return (cases.size() > 0);
	}
	
	/*
	 * SECCION TASKS
	 */
	
	/**
	 * Paged Task list ordered by priority
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> tasks(int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/humanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&o=priority%20" + order + "&s=" + search + "&d=rootContainerId&d=assigned_id";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	/**
	 * Paged Task list ordered by priority
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> tasks(int page, int amountPerPage){
		return this.tasks(page, amountPerPage, false, "");
	}
	
	/**
	 * Return if Task list has previous page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageTasks(int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Task list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageTasks(int actualPage, int amountPerPage){
		List<Task> tasks= this.tasks(actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
	
	/**
	 * Return if Task list has next page
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageTasks(int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<Task> tasks= this.tasks(actualPage + 1, amountPerPage, orderAsc, search);
		return (tasks.size() > 0);
	}
	
	/**
	 * Paged Task list of a User ordered by priority
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> tasks(long userId, int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/humanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=state=ready&f=user_id=" + Long.toString(userId) + "&o=priority%20" + order + "&s=" + search + "&d=rootContainerId&d=assigned_id";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	/**
	 * Paged Task list of a User ordered by priority
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> tasks(long userId, int page, int amountPerPage){
		return this.tasks(userId, page, amountPerPage, false, "");
	}
	
	/**
	 * Return if Task list of user has previous page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageTasks(long userId, int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Task list of user has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageTasks(long userId, int actualPage, int amountPerPage){
		List<Task> tasks= this.tasks(userId, actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
	
	/**
	 * Return if Task list of user has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageTasks(long userId, int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<Task> tasks= this.tasks(userId, actualPage + 1, amountPerPage, orderAsc, search);
		return (tasks.size() > 0);
	}
	
	/**
	 * Return Task with Id
	 * @param taskId
	 * @return
	 */
	public Task task(long taskId){
		String url= "API/bpm/humanTask/" + Long.toString(taskId)+"?d=assigned_id";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTask(resultado);
	}
	
	@Deprecated
	public Task task(long userId, long taskId){
		String url= "API/bpm/humanTask?p=0&c=10000&f=user_id=" + Long.toString(userId)+"&d=assigned_id";
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
	
	/**
	 * Assign a Task with Id to a User with Id
	 * @param taskId
	 * @param userId
	 * @return
	 */
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
	
	/**
	 * Release a Task with Id
	 * @param taskId
	 * @return
	 */
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
	
	/**
	 * Paged Archived Human Task list for a User
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> archivedHumanTask(long userId, int page, int amountPerPage, Boolean orderAsc, String search){
		String order= "ASC";
		if(!orderAsc)order= "DESC";
		String url= "API/bpm/archivedHumanTask?p=" + Integer.toString(page) + "&c=" + Integer.toString(amountPerPage) + "&f=assigned_id=" + Long.toString(userId) + "&reached_state_date=%20" + order + "&s=" + search + "&d=rootContainerId&d=assigned_id";
		String metodo= "GET";
		String resultado= this.proxy.enviarPeticion(url, metodo, null, null);
		
		return this.mapearTasks(resultado);
	}
	
	/**
	 * Paged Archived Human Task list for a User
	 * @param userId
	 * @param page
	 * @param amountPerPage
	 * @return
	 */
	public List<Task> archivedHumanTask(long userId, int page, int amountPerPage){
		return this.archivedHumanTask(userId, page, amountPerPage, false, "");
	}
	
	/**
	 * Return if Archived Human Task has previous page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasPreviousPageArchivedHumanTask(long userId, int actualPage, int amountPerPage){
		return this.PreviousPage(actualPage, amountPerPage);
	}
	
	/**
	 * Return if Archived Human Task has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageArchivedHumanTask(long userId, int actualPage, int amountPerPage){
		List<Task> tasks= this.archivedHumanTask(userId, actualPage + 1, amountPerPage);
		return (tasks.size() > 0);
	}
		
	/**
	 * Return if Archived Human Task has next page
	 * @param userId
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	public Boolean hasNextPageArchivedHumanTask(long userId, int actualPage, int amountPerPage, Boolean orderAsc, String search){
		List<Task> tasks= this.archivedHumanTask(userId, actualPage + 1, amountPerPage, orderAsc, search);
		return (tasks.size() > 0);
	}
	
	
	/*
	 * METODOS PRIVADOS
	 */
	
	/**
	 * Return if a list has previous page - generic method for all list
	 * @param actualPage
	 * @param amountPerPage
	 * @return
	 */
	private Boolean PreviousPage(int actualPage, int amountPerPage){
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
	
	private Process mapearProcess(String jsonString){
		try {
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			Process process= new Process();
			process.setId(Long.parseLong((String)json.get("id")));
			process.setName((String) json.get("name"));
			process.setDescription((String) json.get("description"));
			process.setDisplayName((String) json.get("displayName"));
			process.setDisplayDescription((String) json.get("displayDescription"));
			process.setState((String) json.get("activationState"));
			process.setVersion((String) json.get("version"));
			return process;
		}catch(Exception e){
			return null;
		}
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
	
	private Role mapearRole(String jsonString){
		try {
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			Role role= new Role();
			role.setId(Long.parseLong((String)json.get("id")));
			role.setName((String) json.get("name"));
			role.setDisplayName((String) json.get("displayName"));
			role.setDescription((String) json.get("description"));
			return role;
		}catch(Exception e){
			return null;
		}
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
	
	private Group mapearGroup(String jsonString){
		try {
			JSONParser par= new JSONParser();  		
			JSONObject json= (JSONObject) par.parse(jsonString);
			Group group= new Group();
			group.setId(Long.parseLong((String)json.get("id")));
			group.setName((String) json.get("name"));
			group.setDisplayName((String) json.get("displayName"));
			group.setDescription((String) json.get("description"));
			return group;
		}catch(Exception e){
			return null;
		}
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
				
				membership.setRole(this.mapearRole(userJson.get("role_id").toString()));
				membership.setGroup(this.mapearGroup(userJson.get("group_id").toString()));
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
				
				caso.setProcess(this.mapearProcess(caseJson.get("processDefinitionId").toString()));
				//caso.setProcess(this.deployedProcess(Long.parseLong((String)caseJson.get("processDefinitionId"))));
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
				User assignedId= null;
				String user= userJson.get("assigned_id").toString();
				if(!user.isEmpty()){
					assignedId= this.mapearUser(user);
				}
				task.setAssignedId(assignedId);
				
				task.setProcess(this.mapearProcess(userJson.get("rootContainerId").toString()));
				//task.setProcess(this.deployedProcess(Long.parseLong((String)userJson.get("processId"))));
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
			User assignedId= null;
			String user= json.get("assigned_id").toString();
			if(!user.isEmpty()){
				assignedId= this.mapearUser(user);
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
