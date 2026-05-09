<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Member Statistics – FitNation Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>

  <main class="fn-admin-main">
    <header class="fn-admin-header">
      <div>
        <a href="${ctx}/admin/members" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Back to Members</a>
        <h1 class="fn-admin-title" style="display:inline;">Member Statistics</h1>
        <p class="fn-admin-subtitle">Overview of member metrics and trends.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Overview</h2>
        </div>
      </div>
      <div class="fn-admin-stats-grid">
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">👥</div>
          <div>
            <div class="fn-admin-stat-meta">Total Members</div>
            <div class="fn-admin-stat-value">${memberStats.totalMembers}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">✅</div>
          <div>
            <div class="fn-admin-stat-meta">Active Members</div>
            <div class="fn-admin-stat-value">${memberStats.totalActiveUsers}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">💳</div>
          <div>
            <div class="fn-admin-stat-meta">Active Subscriptions</div>
            <div class="fn-admin-stat-value">${memberStats.usersWithActiveSubscription}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">🚫</div>
          <div>
            <div class="fn-admin-stat-meta">Blocked Members</div>
            <div class="fn-admin-stat-value">${memberStats.blockedMembers}</div>
          </div>
        </div>
      </div>
    </section>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
