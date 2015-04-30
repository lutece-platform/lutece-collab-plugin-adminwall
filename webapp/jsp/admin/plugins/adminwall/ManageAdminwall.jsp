<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="AdminWall" scope="session" class="fr.paris.lutece.plugins.adminwall.web.AdminWallJspBean" />

<% AdminWall.init( request, AdminWall.RIGHT_AdminWall ); %>
<%= AdminWall.getAdminWallHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
