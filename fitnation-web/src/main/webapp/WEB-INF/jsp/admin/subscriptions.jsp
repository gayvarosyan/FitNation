<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Subscriptions – FitNation Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/admin-subscriptions.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>

  <main class="fn-admin-main">
    <header class="fn-admin-header">
      <div>
        <h1 class="fn-admin-title">Subscriptions</h1>
        <p class="fn-admin-subtitle">Membership catalog and purchased subscriptions.</p>
      </div>
      <button class="fn-admin-add-btn" type="button" data-bs-toggle="modal" data-bs-target="#createPlanModal">
        <span class="fn-admin-add-icon">+</span>
        <span>Create Plan</span>
      </button>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="fn-alert fn-alert-success mb-3" role="alert">${message}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-stats-grid" id="kpiGrid">
        <c:choose>
          <c:when test="${stats != null}">
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon">💳</div>
              <div class="fn-sub-stat-body">
                <div class="fn-admin-stat-value"><fmt:formatNumber value="${stats.monthlyRecurringRevenue}" type="currency" currencyCode="USD" /></div>
                <div class="fn-admin-stat-meta">Monthly Recurring Revenue</div>
              </div>
            </div>
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon">📈</div>
              <div class="fn-sub-stat-body">
                <div class="fn-admin-stat-value">${stats.activeSubscriptions}</div>
                <div class="fn-admin-stat-meta">Active Subscriptions</div>
              </div>
            </div>
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon">📉</div>
              <div class="fn-sub-stat-body">
                <div class="fn-admin-stat-value"><fmt:formatNumber value="${stats.churnRate}" maxFractionDigits="1" />%</div>
                <div class="fn-admin-stat-meta">Churn Rate</div>
              </div>
            </div>
            <div class="fn-admin-stat-card">
              <div class="fn-admin-stat-icon">⏰</div>
              <div class="fn-sub-stat-body">
                <div class="fn-admin-stat-value">${stats.pastDueAccounts}</div>
                <div class="fn-admin-stat-meta">Past Due Accounts</div>
              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="fn-admin-stat-card"><div class="fn-admin-stat-meta">Stats unavailable</div></div>
          </c:otherwise>
        </c:choose>
      </div>
    </section>

    <section class="fn-admin-section">
      <h2 class="fn-admin-section-title">Plans catalog</h2>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table">
          <thead>
            <tr>
              <th>Name</th><th>Duration</th><th>Price</th><th>Bundle</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="t" items="${membershipTypes}">
              <tr>
                <td>${t.name} <span class="fn-sub-id">#${t.id}</span></td>
                <td>${t.durationDays} days</td>
                <td><fmt:formatNumber value="${t.price}" type="currency" currencyCode="USD" /></td>
                <td>
                  N:<c:choose><c:when test="${t.nutritionPlanId != null}">${t.nutritionPlanId}</c:when><c:otherwise>—</c:otherwise></c:choose>
                  T:<c:choose><c:when test="${t.trainerId != null}">${t.trainerId}</c:when><c:otherwise>—</c:otherwise></c:choose>
                  G:<c:choose><c:when test="${t.groupClassId != null}">${t.groupClassId}</c:when><c:otherwise>—</c:otherwise></c:choose>
                </td>
                <td class="fn-admin-actions-cell">
                  <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-catalog"
                    data-id="${t.id}"
                    data-name="${fn:escapeXml(t.name)}"
                    data-duration="${t.durationDays}"
                    data-price="${t.price}"
                    data-description="${fn:escapeXml(t.description)}"
                    data-nutrition="${t.nutritionPlanId}"
                    data-trainer="${t.trainerId}"
                    data-groupclass="${t.groupClassId}">Edit</button>
                  <form method="post" action="${ctx}/admin/subscriptions/plans/${t.id}/delete" style="display:inline" onsubmit="return confirm('Delete this plan?');">
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty membershipTypes}">
              <tr><td colspan="5">No plans yet.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>

    <section class="fn-admin-section">
      <h2 class="fn-admin-section-title">Subscription records</h2>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table">
          <thead>
            <tr>
              <th>ID</th><th>Member</th><th>Plan</th><th>Amount</th><th>End</th><th>Status</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${records}">
              <tr>
                <td>SUB-${r.id}</td>
                <td>${r.userFirstName} ${r.userLastName}<br/><small>${r.userEmail}</small></td>
                <td>${r.membershipTypeName}<br/><small>${r.membershipTypeDurationDays} days</small></td>
                <td><fmt:formatNumber value="${r.membershipTypePrice}" type="currency" currencyCode="USD" /></td>
                <td>${r.endDate}</td>
                <td>${r.status}</td>
                <td class="fn-admin-actions-cell">
                  <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-membership"
                    data-id="${r.id}"
                    data-type-id="${r.membershipTypeId}"
                    data-start="${r.startDate}"
                    data-end="${r.endDate}"
                    data-status="${r.status}"
                    data-nutrition="${r.nutritionPlanId}"
                    data-trainer="${r.trainerId}"
                    data-groupclass="${r.groupClassId}">Edit</button>
                  <c:if test="${r.status.name() == 'ACTIVE'}">
                    <form method="post" action="${ctx}/admin/subscriptions/memberships/cancel" style="display:inline" onsubmit="return confirm('Cancel this subscription?');">
                      <input type="hidden" name="membershipId" value="${r.id}" />
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Cancel</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty records}">
              <tr><td colspan="7">No subscription records.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>

<div class="modal fade" id="createPlanModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/subscriptions/plans/save">
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Create Membership Plan</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="cpName">Name</label>
              <input type="text" id="cpName" name="name" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="cpDuration">Duration (days)</label>
              <input type="number" id="cpDuration" name="durationDays" class="fn-input" required min="1">
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="cpPrice">Price</label>
            <input type="number" id="cpPrice" name="price" class="fn-input" required min="0" step="0.01">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="cpDescription">Description</label>
            <textarea id="cpDescription" name="description" class="fn-input" rows="2"></textarea>
          </div>
          <div class="row g-2">
            <div class="col-4">
              <label class="fn-label" for="cpNutrition">Nutrition plan</label>
              <select id="cpNutrition" name="nutritionPlanId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="np" items="${nutritionPlans}">
                  <option value="${np.id}">${np.planName} (#${np.id})</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="cpTrainer">Trainer</label>
              <select id="cpTrainer" name="trainerId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="tr" items="${trainerOptions}">
                  <option value="${tr.trainerId}">${tr.firstName} ${tr.lastName} (#${tr.trainerId})</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="cpGc">Group class</label>
              <select id="cpGc" name="groupClassId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="gc" items="${groupClasses}">
                  <option value="${gc.id}">${gc.name} (#${gc.id})</option>
                </c:forEach>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer fn-admin-modal-footer">
          <button type="button" class="fn-admin-btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="fn-admin-add-btn">Create</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="editPlanModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/subscriptions/plans/save" id="editCatalogForm">
        <input type="hidden" name="planId" id="ecPlanId" />
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Edit membership plan</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="ecName">Name</label>
              <input type="text" id="ecName" name="name" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="ecDuration">Duration (days)</label>
              <input type="number" id="ecDuration" name="durationDays" class="fn-input" required min="1">
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="ecPrice">Price</label>
            <input type="number" id="ecPrice" name="price" class="fn-input" required min="0" step="0.01">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="ecDesc">Description</label>
            <textarea id="ecDesc" name="description" class="fn-input" rows="2"></textarea>
          </div>
          <div class="row g-2">
            <div class="col-4">
              <label class="fn-label" for="ecNut">Nutrition</label>
              <select id="ecNut" name="nutritionPlanId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="np" items="${nutritionPlans}">
                  <option value="${np.id}">${np.planName}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="ecTr">Trainer</label>
              <select id="ecTr" name="trainerId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="tr" items="${trainerOptions}">
                  <option value="${tr.trainerId}">${tr.firstName} ${tr.lastName}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="ecGc">Group class</label>
              <select id="ecGc" name="groupClassId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="gc" items="${groupClasses}">
                  <option value="${gc.id}">${gc.name}</option>
                </c:forEach>
              </select>
            </div>
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

<div class="modal fade" id="editMembershipModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/subscriptions/memberships/save" id="editMembershipForm">
        <input type="hidden" name="membershipId" id="umMembershipId" />
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Edit membership</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="fn-form-group">
            <label class="fn-label" for="umMembershipTypeId">Plan</label>
            <select id="umMembershipTypeId" name="membershipTypeId" class="fn-input" required>
              <c:forEach var="mt" items="${membershipTypes}">
                <option value="${mt.id}">${mt.name}</option>
              </c:forEach>
            </select>
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="umStart">Start</label>
              <input type="date" id="umStart" name="startDate" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="umEnd">End</label>
              <input type="date" id="umEnd" name="endDate" class="fn-input" required>
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="umStatus">Status</label>
            <select id="umStatus" name="status" class="fn-input">
              <option value="ACTIVE">ACTIVE</option>
              <option value="EXPIRED">EXPIRED</option>
              <option value="CANCELLED">CANCELLED</option>
              <option value="FROZEN">FROZEN</option>
              <option value="BLOCKED">BLOCKED</option>
              <option value="PAST_DUE">PAST_DUE</option>
            </select>
          </div>
          <div class="row g-2">
            <div class="col-4">
              <label class="fn-label" for="umNut">Nutrition</label>
              <select id="umNut" name="nutritionPlanId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="np" items="${nutritionPlans}">
                  <option value="${np.id}">${np.planName}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="umTr">Trainer</label>
              <select id="umTr" name="trainerId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="tr" items="${trainerOptions}">
                  <option value="${tr.trainerId}">${tr.firstName} ${tr.lastName}</option>
                </c:forEach>
              </select>
            </div>
            <div class="col-4">
              <label class="fn-label" for="umGc">Group class</label>
              <select id="umGc2" name="groupClassId" class="fn-input">
                <option value="">None</option>
                <c:forEach var="gc" items="${groupClasses}">
                  <option value="${gc.id}">${gc.name}</option>
                </c:forEach>
              </select>
            </div>
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
<script src="${ctx}/js/admin-subscriptions.js"></script>
</body>
</html>
