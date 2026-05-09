<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Client Progress - FitNation Admin</title>
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
        <h1 class="fn-admin-title">Client Progress</h1>
        <p class="fn-admin-subtitle">Client #${clientUserId} progress history and summary.</p>
      </div>
      <a class="fn-admin-add-btn" href="${ctx}/admin/clients">Back to Clients</a>
    </header>

    <section class="fn-admin-section">
      <div class="fn-admin-stats-grid">
        <div class="fn-admin-stat-card">
          <div><div class="fn-admin-stat-meta">Total Entries</div><div class="fn-admin-stat-value">${summary.totalEntries}</div></div>
        </div>
        <div class="fn-admin-stat-card">
          <div><div class="fn-admin-stat-meta">Latest Date</div><div class="fn-admin-stat-value">${summary.latestEntry != null ? summary.latestEntry.recordedAt : '-'}</div></div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table">
          <thead>
          <tr>
            <th>Recorded</th>
            <th>Weight</th>
            <th>Body Fat</th>
            <th>Muscle</th>
            <th>Waist</th>
            <th>Chest</th>
            <th>Hip</th>
            <th>Notes</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="e" items="${entries}">
            <tr>
              <td>${e.recordedAt}</td>
              <td>${e.weight}</td>
              <td>${e.bodyFatPercent}</td>
              <td>${e.muscleMass}</td>
              <td>${e.waistCm}</td>
              <td>${e.chestCm}</td>
              <td>${e.hipCm}</td>
              <td><c:out value="${e.notes}" /></td>
            </tr>
          </c:forEach>
          <c:if test="${empty entries}">
            <tr><td colspan="8" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No progress entries found.</td></tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>
</body>
</html>
