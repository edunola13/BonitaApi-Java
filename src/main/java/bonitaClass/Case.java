package bonitaClass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Case implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private long rootCaseId;
	private String state;
	private Date beginDate;
	private Date endDate;
	private long startedBy;
	private Process process;
	
	public Case(){
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRootCaseId() {
		return rootCaseId;
	}

	public void setRootCaseId(long rootCaseId) {
		this.rootCaseId = rootCaseId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getBeginDate() {
		return beginDate;
	}
	
	public String getBeginDateString(){
		String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.getBeginDate());
		return dateString;
	}

	public void setBeginDate(String beginDate) {
		if(! beginDate.equals("")){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				this.beginDate = sdf.parse(beginDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public Date getEndDate() {
		return endDate;
	}
	
	public String getEndDateString(){
		if(this.getEndDate() != null){
			String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.getEndDate());
			return dateString;
		}else{
			return "";
		}
	}

	public void setEndDate(String endDate) {
		if(! endDate.equals("")){
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				this.endDate = sdf.parse(endDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public long getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(long startedBy) {
		this.startedBy = startedBy;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}
}
