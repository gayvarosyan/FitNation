<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Trainer Profile – FitNation Client</title>
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
        <a href="${ctx}/trainer-assignments/trainers" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Back to Directory</a>
        <h1 class="fn-admin-title" style="display:inline;">Trainer Profile</h1>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="row g-3">
        <div class="col-md-4">
          <div class="fn-admin-avatar" style="width:120px;height:120px;font-size:2rem;margin:0 auto 1rem;">
            <c:set var="fn0" value="${trainerProfile.firstName}" />
            <c:set var="ln0" value="${trainerProfile.lastName}" />
            <c:choose>
              <c:when test="${fn:length(fn0) > 0}">${fn:substring(fn0,0,1)}</c:when>
              <c:otherwise>?</c:otherwise>
            </c:choose>
            <c:if test="${fn:length(ln0) > 0}">${fn:substring(ln0,0,1)}</c:if>
          </div>
        </div>
        <div class="col-md-8">
          <h2 class="fn-admin-section-title">${trainerProfile.firstName} ${trainerProfile.lastName}</h2>
          <p class="fn-admin-section-subtitle">${trainerProfile.specialization}</p>
          <div class="fn-admin-stats-grid" style="margin-top:1rem;">
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon" aria-hidden="true">⭐</div>
              <div>
                <div class="fn-admin-stat-meta">Rating</div>
                <div class="fn-admin-stat-value">${trainerProfile.rating}/5</div>
              </div>
            </div>
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon" aria-hidden="true">👥</div>
              <div>
                <div class="fn-admin-stat-meta">Clients</div>
                <div class="fn-admin-stat-value">${trainerProfile.activeClients}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">About</h2>
        </div>
      </div>
      <p style="color:var(--fn-text-muted);">${trainerProfile.bio}</p>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Request Assignment</h2>
        </div>
      </div>
      <form method="post" action="${ctx}/trainer-assignments/request">
        <input type="hidden" name="trainerId" value="${trainerId}" />
        <div class="fn-form-group">
          <label class="fn-label">Message to trainer</label>
          <textarea name="message" class="fn-input" rows="3" placeholder="Introduce yourself and explain your fitness goals..."></textarea>
        </div>
        <button type="submit" class="fn-admin-add-btn">Request Assignment</button>
      </form>
    </section>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
