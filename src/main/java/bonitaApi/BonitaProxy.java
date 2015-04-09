package bonitaApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BonitaProxy {
	private String serverUrl= "http://localhost:8080/bonita/";
	
	private URL url;
	private HttpURLConnection conn;
	private String cookieHeader;
	//Minutos
	private long sessionTime= 0;
	private String userName;
	private String password;
	
	private Date sessionDate;
	
	public BonitaProxy(){
		
	}
	
	public BonitaProxy(String serverUrl){
		this.serverUrl= serverUrl;
	}
	
	/**
	 * Realiza la autentificacion y luego realiza el metodo rest apropiado
	 * @param urls
	 * @param metodo
	 * @param datos
	 * @param contentType
	 * @return
	 */
	public String enviarPeticion(String urls, String metodo, String datos, String contentType){
		this.autentificarse();
		
		try{
			url = new URL(this.serverUrl + urls);
			
			conn= (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(metodo);
			conn.setConnectTimeout(10000);
			//mando las cookies
			conn.setRequestProperty("Cookie", cookieHeader);
			
			//Seteo el tipo del contenido si se ha mandado
			if(contentType != null){
				conn.setRequestProperty("Content-Type", contentType);
			}
			
			//Analizo si hay datos y los agrego
			if(datos != null){
				//especificamos que vamos a escribir
				conn.setDoOutput(true);
				//Obtenemos el flujo de escritura
				OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
				//escribimos los datos
				wr.write(datos);
				//Cerramos la conexion
				wr.close();
			}
						
			//Hacer la llamado y recibir el InputStream resultante, transformalo a string y devolverlo
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			//Analizo la respuesta
			if(conn.getResponseCode() == 200){
				return response.toString();
			}
			else{
				this.logout();
				return response.toString();
			}
		}
		catch(Exception e){
			System.out.print(e.getMessage());
			this.logout();
			return "error";
		}
	}
	
	/**
	 * Realiza el metodo de autentificacion
	 * @return boolean= true si pudo autentificarse correctamente
	 */
	public boolean autentificarse(String user, String pass){
		if(cookieHeader == null || this.timeExceed()){
			if(cookieHeader != null){
				this.logout();
			}
			try{			
				url = new URL(this.serverUrl + "loginservice");
				
				conn= (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				
				String urlParameters = "username="+user+"&password="+pass+"&redirect=false&redirectUrl=";
					
				// envio los datos por post
				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
				
				conn.setConnectTimeout(10000);
				
				conn.connect();
				//Analizo el codigo de respuesta
				if(conn.getResponseCode() == 200){
					guardarCookie();
					this.sessionDate= Calendar.getInstance().getTime();
					return true;
				}
				else{
					this.setCookieHeader(null);
					return false;
				}
				
			}
			catch(Exception e){	
				System.out.println(e.getMessage());
				this.setCookieHeader(null);
				return false;
			}
		}else{
			return true;
		}
	}
	
	public boolean logout(){
		//Si ya hay una cookie no realizo la autentificacion ya que se encuentra autentificado
		if(cookieHeader != null){
			return true;
		}
		else{
			try{			
				url = new URL(this.serverUrl + "logoutservice?redirect=false");
				
				conn= (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(10000);
				//mando las cookies
				conn.setRequestProperty("Cookie", cookieHeader);
				
				//Elimino la cache
				this.setCookieHeader(null);
				this.sessionDate= null;
				
				//Analizo la respuesta
				if(conn.getResponseCode() == 200){
					return true;
				}
				else{
					return false;
				}				
			}catch(Exception e){	
				System.out.print(e.getMessage());
				this.setCookieHeader(null);
				this.sessionDate= null;
				return false;
			}
		}
	}
	
	protected boolean autentificarse(){
		return this.autentificarse(this.getUserName(), this.getPassword());
	}
	
	/**
	 * Almacena las cookies de la autentificacion para luego utilizarlo en otra respuesta 
	 */
	protected void guardarCookie(){
		StringBuilder sb = new StringBuilder();

		//Encuentro las cookies en el header de la respuesta de la peticion de logueo
		List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
		if (cookies != null) {
		    for (String cookie : cookies) {
		        //Si es la primer cookie no le agrego ";"
		    	if (sb.length() > 0) {
		            sb.append("; ");
		        }

		        // Solo queremos la primer parte de la cookie header que tiene el valor
		        String value = cookie.split(";")[0];
		        sb.append(value);
		    }
		}

		// Escribo la cookie resultante del header para enviar a todos los requerimientos siguientes
		cookieHeader = sb.toString();
	}
	
	protected boolean timeExceed(){
		if(this.sessionTime == 0){
			return false;
		}else{
			long diferenciaEn_ms = Calendar.getInstance().getTime().getTime() - this.getSessionDate().getTime();			
			long tiempo= diferenciaEn_ms / (1000 * 60);
			if(this.sessionTime > tiempo){
				return true;
			}else{
				return false;
			}
		}
	}

	public String getCookieHeader() {
		return cookieHeader;
	}

	public void setCookieHeader(String cookieHeader) {
		this.cookieHeader = cookieHeader;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(long sessionTime) {
		this.sessionTime = sessionTime;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}
	
	
}
