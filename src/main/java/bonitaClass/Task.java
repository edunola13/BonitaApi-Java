package bonitaClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Task implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String name;
	private String description;
	private String displayName;
	private String displayDescription;
	private String type;
	private String priority;
	private String state;
	private Date dueDate;
	private Date executedDate;
	private long executedBy;
	private long caseId;
	private long actorId;
	private long assignedId;
	private Process process;
	
	public Task(){
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	
	public String getDueDateString(){
		String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.getDueDate());
		return dateString;
	}

	public void setDueDate(String dueDate) {
		if(! dueDate.equals("")){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				this.dueDate = sdf.parse(dueDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public Date getExecutedDate() {
		return executedDate;
	}
	
	public String getExecutedDateString(){
		String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.getExecutedDate());
		return dateString;
	}

	public void setExecutedDate(String executedDate) {
		if(! executedDate.equals("")){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				this.executedDate = sdf.parse(executedDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public long getExecutedBy() {
		return executedBy;
	}

	public void setExecutedBy(long executedBy) {
		this.executedBy = executedBy;
	}

	public long getCaseId() {
		return caseId;
	}

	public void setCaseId(long caseId) {
		this.caseId = caseId;
	}

	public long getActorId() {
		return actorId;
	}

	public void setActorId(long actorId) {
		this.actorId = actorId;
	}

	public long getAssignedId() {
		return assignedId;
	}

	public void setAssignedId(long assignedId) {
		this.assignedId = assignedId;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}
	
	/*
	 * Funcionalidad
	 */
	
	public Boolean exceededDeadline(){
		Calendar cal= Calendar.getInstance();
		if(this.getDueDate().getTime() < cal.getTime().getTime()){
			return true;
		}else{
			return false;
		}
	}
	
	public String timeToDeadline(){
		Calendar cal= Calendar.getInstance();
		if(cal.getTime().getTime() < this.getDueDate().getTime()){
			System.out.println(this.getDueDate() + " " + cal.getTime());
			long diferenciaEn_ms = this.getDueDate().getTime() - cal.getTime().getTime();
			
			String tiempo= "";
			if(diferenciaEn_ms / (1000 * 60) <= 60L){
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60)) + "min";
			}else if(diferenciaEn_ms / (1000 * 60 * 60) <= 24){
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60 * 60)) + "hour";
			}else{
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60 * 60 * 24)) + "day";
			}
			
			return tiempo;
		}else{
			//Retorna 0 ya que se excedio
			return "0";
		}		
	}
	
	public String timeExceededDeadline(){
		Calendar cal= Calendar.getInstance();
		if(this.getDueDate().getTime() < cal.getTime().getTime()){
			long diferenciaEn_ms = cal.getTime().getTime() - this.getDueDate().getTime();
			
			String tiempo= "";
			if(diferenciaEn_ms / (1000 * 60) <= 60L){
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60)) + "min";
			}else if(diferenciaEn_ms / (1000 * 60 * 60) <= 24){
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60 * 60)) + "hour";
			}else{
				tiempo= Long.toString(diferenciaEn_ms / (1000 * 60 * 60 * 24)) + "day";
			}
			
			return tiempo;
		}else{
			//Retorna 0 ya que se excedio
			return "0";
		}		
	}
}
