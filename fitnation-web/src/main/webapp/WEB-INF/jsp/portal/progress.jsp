<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Progress - FitNation</title>
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
      <header class="fn-portal-page-head">
        <h1 class="fn-portal-page-title">Progress Tracking</h1>
      </header>

      <c:if test="${not empty error}">
        <div class="fn-alert fn-alert-error mb-3" role="alert"><c:out value="${error}" /></div>
      </c:if>
      <c:if test="${not empty message}">
        <div class="fn-alert fn-alert-success mb-3" role="alert"><c:out value="${message}" /></div>
      </c:if>

      <section class="fn-portal-section">
        <h2 class="fn-portal-section-title">Summary</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <tbody>
            <tr><th>Total Entries</th><td>${summary.totalEntries}</td></tr>
            <tr><th>Latest Date</th><td>${summary.latestEntry != null ? summary.latestEntry.recordedAt : '-'}</td></tr>
            <tr><th>Latest Weight</th><td>${summary.latestEntry != null ? summary.latestEntry.weight : '-'}</td></tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="fn-portal-section">
        <h2 class="fn-portal-section-title">Add Entry</h2>
        <form method="post" action="${ctx}/portal/progress/create" class="fn-sub-filter-form">
          <div class="fn-form-grid">
            <div><label class="form-label">Recorded At (ISO)</label><input class="form-control fn-input" name="recordedAt" placeholder="2026-05-06T20:00:00" required /></div>
            <div><label class="form-label">Weight</label><input class="form-control fn-input" name="weight" /></div>
            <div><label class="form-label">Body Fat %</label><input class="form-control fn-input" name="bodyFatPercent" /></div>
            <div><label class="form-label">Muscle Mass</label><input class="form-control fn-input" name="muscleMass" /></div>
            <div><label class="form-label">Waist Cm</label><input class="form-control fn-input" name="waistCm" /></div>
            <div><label class="form-label">Chest Cm</label><input class="form-control fn-input" name="chestCm" /></div>
            <div><label class="form-label">Hip Cm</label><input class="form-control fn-input" name="hipCm" /></div>
            <div><label class="form-label">Notes</label><input class="form-control fn-input" name="notes" /></div>
          </div>
          <div class="mt-2">
            <button type="submit" class="fn-portal-btn-primary fn-portal-btn-inline">Save Entry</button>
          </div>
        </form>
      </section>

      <section class="fn-portal-section">
        <h2 class="fn-portal-section-title">History</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <thead>
            <tr>
              <th>Recorded</th>
              <th>Weight</th>
              <th>Body Fat</th>
              <th>Muscle</th>
              <th>Notes</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="e" items="${entries}">
              <tr>
                <td>${e.recordedAt}</td>
                <td>${e.weight}</td>
                <td>${e.bodyFatPercent}</td>
                <td>${e.muscleMass}</td>
                <td><c:out value="${e.notes}" /></td>
                <td>
                  <form method="post" action="${ctx}/portal/progress/${e.id}/delete" style="display:inline" onsubmit="return confirm('Delete this entry?');">
                    <button type="submit" class="fn-portal-btn-secondary fn-portal-btn-inline">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty entries}">
              <tr><td colspan="6" class="fn-portal-empty-cell">No progress entries yet.</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </main>
</div>
</body>
</html>
