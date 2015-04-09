<%@page import="bonitaApi.BonitaProxy"%>
<%@page import="bonitaApi.BonitaApi" %>
<%@page import="bonitaClass.*" %>
<%@page import="java.util.*" %>
<html>
<body>
<%-- <%BonitaProxy pro= new BonitaProxy();%> --%>
<%-- <%=pro.autentificarse("juan", "juan")%> --%>

<%BonitaApi con= new BonitaApi("http://server200364b:8080/bonita/","compras", "bpm");
 User user= con.user("compras");
 List<bonitaClass.Process> precesos= con.deployedProccessForUser(user.getId()); %>
 <%= con.getCorrectLogin() %>
<%--  <%for ( bonitaClass.Process preceso : precesos ) { %> --%>
<%-- 	<%=preceso.getVersion() %> --%>
<%-- 	<%=preceso.getName() %> --%>
<%-- <%} %> --%>
 
<%-- <%List<Case> cases= con.cases(user.getId(),0,100);%> --%>
<%-- <%for ( Case caso : cases ) { %> --%>
<%-- 	<%=caso.getId() %> --%>
<%-- 	<%=caso.getProcess().getId() %> --%>
<%-- 	<%=caso.getBeginDate() %> --%>
<%-- 	<%=caso.getBeginDateString() %> --%>
<!-- 	<br/> -->
<%-- <%} %> --%>

<%-- <%=con.actualUser().getFirstName() %> --%>

<%-- <%= con.updateUser(503L, "ahoraSi", "ahora", "ahora", "Ehhha adad", "asdasd", true) %> --%>

<%-- <%= con.deleteUser(601L) %> --%>

<%-- <%= con.desactivateUser(503L) %> --%>

<%-- <%= con.activeUser(503L) %> --%>

<%-- <%= user.getId() %> --%>

<%-- <%List<Task> tasks= con.tasks(user.getId(),0,100);%> --%>
<%-- <%= user.getId() %> --%>
<%-- <%for ( Task task : tasks ) { %> --%>
<%-- 	<%=task.getId() %> --%>
<%-- 	<%=task.getProcess().getName() %> --%>
<%-- 	<%=task.timeToDeadline() %> --%>
<%-- 	<%=task.exceededDeadline() %> --%>
<%-- 	<%=task.timeExceededDeadline() %> --%>
<!-- 	<br/> -->
<%-- <%} %> --%>

<%-- <%=con.role("cliente").getName() %> --%>

<%-- <%List<Role> roles= con.roles(0,100);%> --%>
<%-- <%for ( Role role : roles ) { %> --%>
<%-- 	<%=role.getId() %> --%>
<%-- 	<%=role.getName() %> --%>
<%-- <%} %> --%>

<%-- <%List<Group> groups= con.groups(0, 100);%> --%>
<%-- <%for ( Group group : groups ) { %> --%>
<%-- 	<%=group.getId() %> --%>
<%-- 	<%=group.getName() %> --%>
<%-- <%} %> --%>

<%-- <%List<Membership> memberships= con.memberships(103L);%> --%>
<%-- <%for ( Membership membership : memberships ) { %> --%>
<%-- 	<%=membership.getUserId() %> --%>
<%-- 	<%=membership.getRole().getName() %> --%>
<%-- 	<%=membership.getGroup().getName() %> --%>
<%-- <%} %> --%>

<%-- <%= con.createUser("Fede22", "Fede22", "Fede22", "Fede22", "Fede22") %> --%>

<%-- <%= con.addMembership(12L, 12L, 12L) %> --%>

<%-- <%long processId= 7253659303785341982L; %> --%>
<%-- <%= con.startCase(processId).getStartedBy() %> --%>
<h2>Hello World!</h2>
</body>
</html>
