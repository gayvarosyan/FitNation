<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nutrition Plans – FitNation Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/admin-nutrition.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>

  <main class="fn-admin-main">
    <header class="fn-admin-header">
      <div>
        <h1 class="fn-admin-title">Nutrition Plans</h1>
        <p class="fn-admin-subtitle">Manage dietary programs across the system.</p>
      </div>
      <button class="fn-admin-add-btn" type="button" data-bs-toggle="modal" data-bs-target="#createPlanModal">
        <span class="fn-admin-add-icon">+</span>
        <span>Create New Plan</span>
      </button>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="fn-alert fn-alert-success mb-3" role="alert">${message}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-stats-grid fn-nutrition-stats">
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">🍎</div>
          <div>
            <div class="fn-admin-stat-meta">Total Plans</div>
            <div class="fn-admin-stat-value">${stats.totalPlans}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">👥</div>
          <div>
            <div class="fn-admin-stat-meta">Active Users</div>
            <div class="fn-admin-stat-value">${stats.activeUsers}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Master Catalog</h2>
          <p class="fn-admin-section-subtitle">Nutrition plans.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table fn-nutrition-table" aria-label="Nutrition plans catalog">
          <thead>
            <tr>
              <th scope="col">Plan Name</th>
              <th scope="col">Category</th>
              <th scope="col">Active Clients</th>
              <th scope="col">Avg Rating</th>
              <th scope="col">Status</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="p" items="${plans}">
              <tr>
                <td>${p.planName}</td>
                <td>${p.category}</td>
                <td>${p.activeClients}</td>
                <td><c:choose><c:when test="${p.avgRating != null && p.avgRating > 0}"><fmt:formatNumber value="${p.avgRating}" maxFractionDigits="1" /></c:when><c:otherwise>—</c:otherwise></c:choose></td>
                <td>${p.status}</td>
                <td class="fn-admin-actions-cell">
                  <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-nutrition"
                    data-id="${p.id}"
                    data-name="${fn:escapeXml(p.planName)}"
                    data-category="${fn:escapeXml(p.category)}"
                    data-price="${p.price}"
                    data-description="${fn:escapeXml(p.description)}"
                    data-status="${p.status}">Edit</button>
                  <form method="post" action="${ctx}/admin/nutrition/plans/${p.id}/delete" style="display:inline" onsubmit="return confirm('Delete this plan?');">
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty plans}">
              <tr><td colspan="6" class="fn-nutrition-empty-cell"><div class="fn-nutrition-empty"><p>No plans found.</p></div></td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>

<div class="modal fade" id="createPlanModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/nutrition/plans/save">
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Create New Plan</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="fn-form-group">
            <label class="fn-label" for="planName">Plan Name</label>
            <input type="text" id="planName" name="planName" class="fn-input" required maxlength="200">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="category">Category</label>
            <input type="text" id="category" name="category" class="fn-input" required maxlength="100">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="price">Price</label>
            <input type="number" id="price" name="price" class="fn-input" required min="0" step="0.01">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="description">Description</label>
            <textarea id="description" name="description" class="fn-input" rows="3" maxlength="1000"></textarea>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="status">Status</label>
            <select id="status" name="status" class="fn-input">
              <option value="ACTIVE">Active</option>
              <option value="DRAFT" selected>Draft</option>
            </select>
          </div>
        </div>
        <div class="modal-footer fn-admin-modal-footer">
          <button type="button" class="fn-admin-btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="fn-admin-add-btn">Create Plan</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="editPlanModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/nutrition/plans/save" id="editNutritionForm">
        <input type="hidden" name="planId" id="editPlanId" />
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Edit Plan</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="fn-form-group">
            <label class="fn-label" for="editPlanName">Plan Name</label>
            <input type="text" id="editPlanName" name="planName" class="fn-input" required maxlength="200">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editCategory">Category</label>
            <input type="text" id="editCategory" name="category" class="fn-input" required maxlength="100">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editPrice">Price</label>
            <input type="number" id="editPrice" name="price" class="fn-input" required min="0" step="0.01">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editDescription">Description</label>
            <textarea id="editDescription" name="description" class="fn-input" rows="3" maxlength="1000"></textarea>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editStatus">Status</label>
            <select id="editStatus" name="status" class="fn-input">
              <option value="ACTIVE">Active</option>
              <option value="DRAFT">Draft</option>
            </select>
          </div>
        </div>
        <div class="modal-footer fn-admin-modal-footer">
          <button type="button" class="fn-admin-btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="fn-admin-add-btn">Save</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/js/admin-nutrition.js"></script>
</body>
</html>
