<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>My Assignment Requests – FitNation Client</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="../portal/portal-sidebar.jspf" %>

  <main class="fn-admin-main">
    <header class="fn-admin-header">
      <div>
        <a href="${ctx}/trainer-assignments/trainers" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Browse Trainers</a>
        <h1 class="fn-admin-title" style="display:inline;">My Assignment Requests</h1>
        <p class="fn-admin-subtitle">Track your trainer assignment requests.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Requests</h2>
          <p class="fn-admin-section-subtitle">Your trainer assignment requests.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Assignment requests">
          <thead>
            <tr>
              <th scope="col">Trainer</th>
              <th scope="col">Status</th>
              <th scope="col">Requested</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="req" items="${clientRequests}">
              <tr>
                <td>${req.trainerName}</td>
                <td><span class="fn-admin-status fn-admin-status-${fn:toLowerCase(req.status)}"><span class="fn-admin-status-dot"></span>${req.status}</span></td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">📅</span> ${req.requestedAt}</span></td>
              </tr>
            </c:forEach>
            <c:if test="${empty clientRequests}">
              <tr><td colspan="3" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No assignment requests found.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
