<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Client Progress - FitNation</title>
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
      <header class="fn-portal-page-head"><h1 class="fn-portal-page-title">Client Progress</h1></header>
      <p class="fn-portal-page-subtitle">Client #${clientUserId}</p>
      <div class="fn-portal-table-wrap"><table class="fn-portal-table"><thead><tr><th>Recorded</th><th>Weight</th><th>Body Fat</th><th>Muscle</th></tr></thead><tbody>
      <c:forEach var="e" items="${entries}">
        <tr><td>${e.recordedAt}</td><td>${e.weight}</td><td>${e.bodyFatPercent}</td><td>${e.muscleMass}</td></tr>
      </c:forEach>
      <c:if test="${empty entries}"><tr><td colspan="4" class="fn-portal-empty-cell">No entries found.</td></tr></c:if>
      </tbody></table></div>
    </div>
  </main>
</div>
</body>
</html>
