<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Progress Entry – FitNation User</title>
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
        <a href="${ctx}/users/progress" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Back to Progress</a>
        <h1 class="fn-admin-title" style="display:inline;">Progress Entry</h1>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Record Progress</h2>
        </div>
      </div>
      <form method="post" action="${ctx}/users/progress">
        <div class="row g-2">
          <div class="col-md-6">
            <label class="fn-label" for="recordedAt">Date</label>
            <input type="datetime-local" id="recordedAt" name="recordedAt" class="fn-input" required>
          </div>
          <div class="col-md-6">
            <label class="fn-label" for="weight">Weight (kg)</label>
            <input type="number" step="0.1" id="weight" name="weight" class="fn-input" required>
          </div>
        </div>
        <div class="row g-2">
          <div class="col-md-6">
            <label class="fn-label" for="bodyFatPercent">Body Fat (%)</label>
            <input type="number" step="0.1" id="bodyFatPercent" name="bodyFatPercent" class="fn-input">
          </div>
          <div class="col-md-6">
            <label class="fn-label" for="muscleMass">Muscle Mass (kg)</label>
            <input type="number" step="0.1" id="muscleMass" name="muscleMass" class="fn-input">
          </div>
        </div>
        <div class="row g-2">
          <div class="col-md-6">
            <label class="fn-label" for="waistCm">Waist (cm)</label>
            <input type="number" step="0.1" id="waistCm" name="waistCm" class="fn-input">
          </div>
          <div class="col-md-6">
            <label class="fn-label" for="chestCm">Chest (cm)</label>
            <input type="number" step="0.1" id="chestCm" name="chestCm" class="fn-input">
          </div>
        </div>
        <div class="row g-2">
          <div class="col-md-6">
            <label class="fn-label" for="hipCm">Hip (cm)</label>
            <input type="number" step="0.1" id="hipCm" name="hipCm" class="fn-input">
          </div>
        </div>
        <div class="fn-form-group">
          <label class="fn-label" for="notes">Notes</label>
          <textarea id="notes" name="notes" class="fn-input" rows="3" placeholder="Any additional notes..."></textarea>
        </div>
        <button type="submit" class="fn-admin-add-btn">Save Entry</button>
      </form>
    </section>
  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
