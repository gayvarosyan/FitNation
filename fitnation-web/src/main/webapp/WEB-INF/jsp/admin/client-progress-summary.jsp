<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Client Progress Summary – FitNation Admin</title>
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
        <a href="${ctx}/admin/clients/${userId}/progress" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Back to Progress</a>
        <h1 class="fn-admin-title" style="display:inline;">Progress Summary</h1>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Progress Overview</h2>
        </div>
      </div>
      <div class="fn-admin-stats-grid">
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">📊</div>
          <div>
            <div class="fn-admin-stat-meta">Total Entries</div>
            <div class="fn-admin-stat-value">${progressSummary.totalEntries}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">⚖️</div>
          <div>
            <div class="fn-admin-stat-meta">Current Weight</div>
            <div class="fn-admin-stat-value">${progressSummary.currentWeight} kg</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">📈</div>
          <div>
            <div class="fn-admin-stat-meta">Weight Change</div>
            <div class="fn-admin-stat-value">${progressSummary.weightChange} kg</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">💪</div>
          <div>
            <div class="fn-admin-stat-meta">Body Fat</div>
            <div class="fn-admin-stat-value">${progressSummary.currentBodyFat}%</div>
          </div>
        </div>
      </div>
    </section>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
