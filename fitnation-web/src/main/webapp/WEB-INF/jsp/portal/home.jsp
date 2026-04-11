<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard – FitNation</title>
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
      <section class="fn-portal-hero">
        <h1 class="fn-portal-page-title">Welcome back</h1>
        <p class="fn-portal-page-subtitle">Your training hub. Open <a class="fn-portal-inline-link" href="${ctx}/portal/subscriptions">Subscription</a> to browse plans and request membership.</p>
      </section>
    </div>
  </main>
</div>
</body>
</html>
