<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Device Login - FitNation</title>
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
      <header class="fn-portal-page-head"><h1 class="fn-portal-page-title">Device Login (QR)</h1></header>
      <c:if test="${not empty error}"><div class="fn-alert fn-alert-error mb-3">${error}</div></c:if>
      <c:if test="${not empty message}"><div class="fn-alert fn-alert-success mb-3">${message}</div></c:if>
      <section class="fn-portal-section">
        <form method="post" action="${ctx}/portal/device-login/create">
          <button class="fn-portal-btn-primary fn-portal-btn-inline" type="submit">Create QR Session</button>
        </form>
      </section>
      <c:if test="${not empty createdSession}">
        <section class="fn-portal-section">
          <div class="fn-portal-table-wrap"><table class="fn-portal-table"><tbody>
          <tr><th>Session ID</th><td>${createdSession.sessionId}</td></tr>
          <tr><th>QR Payload</th><td>${createdSession.qrPayload}</td></tr>
          <tr><th>Expires At</th><td>${createdSession.expiresAt}</td></tr>
          </tbody></table></div>
        </section>
      </c:if>
      <c:if test="${not empty sessionStatus}">
        <section class="fn-portal-section"><strong>Session Status:</strong> ${sessionStatus}</section>
      </c:if>
    </div>
  </main>
</div>
</body>
</html>
