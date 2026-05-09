<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Available Classes – FitNation User</title>
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
        <h1 class="fn-admin-title">Available Classes</h1>
        <p class="fn-admin-subtitle">Browse and book group fitness classes.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Class Schedule</h2>
          <p class="fn-admin-section-subtitle">Available group fitness classes.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Available classes">
          <thead>
            <tr>
              <th scope="col">Class</th>
              <th scope="col">Trainer</th>
              <th scope="col">Date</th>
              <th scope="col">Time</th>
              <th scope="col">Capacity</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="cls" items="${classes}">
              <tr>
                <td>${cls.className}</td>
                <td>${cls.trainerName}</td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">📅</span> ${cls.date}</span></td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">⏰</span> ${cls.time}</span></td>
                <td>${cls.bookedCount}/${cls.capacity}</td>
                <td class="fn-admin-actions-cell">
                  <c:if test="${cls.bookedCount < cls.capacity}">
                    <form method="post" action="${ctx}/users/classes/${cls.id}/book" style="display:inline">
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">Book</button>
                    </form>
                  </c:if>
                  <c:if test="${cls.bookedCount >= cls.capacity}">
                    <button type="button" class="fn-admin-action-btn" disabled style="opacity:0.5;">Full</button>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty classes}">
              <tr><td colspan="6" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No classes available.</td></tr>
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
