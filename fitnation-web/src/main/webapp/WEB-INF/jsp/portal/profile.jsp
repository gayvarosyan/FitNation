<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Profile - FitNation</title>
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/portal.css" rel="stylesheet">
</head>
<body class="fn-portal-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-portal-shell">
  <%@ include file="portal-sidebar.jspf" %>
  <main class="fn-portal-main">
    <%@ include file="portal-header.jspf" %>
    <div class="fn-portal-content">
      <header class="fn-portal-page-head"><h1 class="fn-portal-page-title">My Profile</h1></header>
      <div class="fn-portal-table-wrap">
        <table class="fn-portal-table">
          <tbody>
            <tr><th>ID</th><td>${profile.id}</td></tr>
            <tr><th>Email</th><td>${profile.email}</td></tr>
            <tr><th>First Name</th><td>${profile.firstName}</td></tr>
            <tr><th>Last Name</th><td>${profile.lastName}</td></tr>
            <tr><th>Role</th><td>${profile.role}</td></tr>
            <tr><th>Status</th><td>${profile.status}</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
</div>
</body>
</html>
