<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Trainer Directory – FitNation Client</title>
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
        <h1 class="fn-admin-title">Trainer Directory</h1>
        <p class="fn-admin-subtitle">Browse and request trainer assignments.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Available Trainers</h2>
          <p class="fn-admin-section-subtitle">All active trainers available for assignment.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Trainer directory">
          <thead>
            <tr>
              <th scope="col">Trainer</th>
              <th scope="col">Specialization</th>
              <th scope="col">Rating</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="trainer" items="${trainers}">
              <tr>
                <td>
                  <div class="fn-admin-trainer-cell">
                    <div class="fn-admin-avatar">
                      <c:set var="fn0" value="${trainer.firstName}" />
                      <c:set var="ln0" value="${trainer.lastName}" />
                      <c:choose>
                        <c:when test="${fn:length(fn0) > 0}">${fn:substring(fn0,0,1)}</c:when>
                        <c:otherwise>?</c:otherwise>
                      </c:choose>
                      <c:if test="${fn:length(ln0) > 0}">${fn:substring(ln0,0,1)}</c:if>
                    </div>
                    <div class="fn-admin-trainer-main">
                      <div class="fn-admin-trainer-name">${trainer.firstName} ${trainer.lastName}</div>
                    </div>
                  </div>
                </td>
                <td>${trainer.specialization}</td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">⭐</span> ${trainer.rating}/5</span></td>
                <td class="fn-admin-actions-cell">
                  <a href="${ctx}/trainer-assignments/trainers/${trainer.id}" class="fn-admin-action-btn fn-admin-action-edit">View Profile</a>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty trainers}">
              <tr><td colspan="4" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No trainers available.</td></tr>
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
