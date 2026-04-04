<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Class Management – FitNation Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/admin-classes.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>

  <main class="fn-admin-main">
    <header class="fn-admin-header">
      <div>
        <h1 class="fn-admin-title">Class Management</h1>
        <p class="fn-admin-subtitle">Create classes, schedule sessions, and manage capacity.</p>
      </div>
      <button class="fn-admin-add-btn" type="button" data-bs-toggle="modal" data-bs-target="#scheduleClassModal">
        <span class="fn-admin-add-icon">+</span>
        <span>Schedule Class</span>
      </button>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="fn-alert fn-alert-success mb-3" role="alert">${message}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-stats-grid">
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">📅</div>
          <div>
            <div class="fn-admin-stat-meta">Classes This Week</div>
            <div class="fn-admin-stat-value">${classesThisWeek}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">👥</div>
          <div>
            <div class="fn-admin-stat-meta">Total Schedules</div>
            <div class="fn-admin-stat-value">${totalSchedules}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">🏋️</div>
          <div>
            <div class="fn-admin-stat-meta">Unique Classes</div>
            <div class="fn-admin-stat-value">${uniqueClassIds}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Master Schedule</h2>
          <p class="fn-admin-section-subtitle">Scheduled group sessions.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Class schedules">
          <thead>
            <tr>
              <th scope="col">Class</th>
              <th scope="col">Schedule</th>
              <th scope="col">Trainer</th>
              <th scope="col">Capacity</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="row" items="${scheduleRows}">
              <c:set var="s" value="${row.item}" />
              <tr>
                <td>
                  <div class="fn-class-name">${s.className}</div>
                  <div class="fn-class-id">CLS-${s.classId}</div>
                </td>
                <td>
                  <div class="fn-class-name">${s.date}</div>
                  <div class="fn-class-id">${row.startHm} - ${row.endHm}</div>
                </td>
                <td>${s.trainerName}</td>
                <td>${s.capacity}</td>
                <td class="fn-admin-actions-cell">
                  <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-schedule"
                    data-class-id="${s.classId}"
                    data-schedule-id="${s.scheduleId}"
                    data-name="${fn:escapeXml(s.className)}"
                    data-description="${fn:escapeXml(s.classDescription)}"
                    data-capacity="${s.capacity}"
                    data-trainer-id="${s.trainerId}"
                    data-date="${s.date}"
                    data-start="${row.startHm}"
                    data-end="${row.endHm}">Edit</button>
                  <form method="post" action="${ctx}/admin/classes/schedule/${s.scheduleId}/delete" style="display:inline" onsubmit="return confirm('Delete this schedule?');">
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty scheduleRows}">
              <tr><td colspan="5" style="text-align:center;padding:1rem;">No class schedules yet.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>

<div class="modal fade" id="scheduleClassModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/classes/schedule-new">
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Schedule Class</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="fn-form-group">
            <label class="fn-label" for="scName">Class name</label>
            <input id="scName" name="name" type="text" class="fn-input" required maxlength="255">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="scDescription">Description</label>
            <textarea id="scDescription" name="description" rows="3" class="fn-input" maxlength="2000"></textarea>
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="scCapacity">Capacity</label>
              <input id="scCapacity" name="capacity" type="number" min="1" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="scTrainer">Trainer</label>
              <select id="scTrainer" name="trainerId" class="fn-input" required>
                <c:forEach var="tr" items="${trainerOptions}">
                  <option value="${tr.trainerId()}">${tr.firstName()} ${tr.lastName()}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="scDate">Date</label>
            <input id="scDate" name="date" type="date" class="fn-input" required>
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="scStart">Start time</label>
              <input id="scStart" name="startTime" type="time" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="scEnd">End time</label>
              <input id="scEnd" name="endTime" type="time" class="fn-input" required>
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

<div class="modal fade" id="editClassModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <form method="post" action="${ctx}/admin/classes/update" id="editClassForm">
        <input type="hidden" name="classId" id="ecClassId" />
        <input type="hidden" name="scheduleId" id="ecScheduleId" />
        <div class="modal-header fn-admin-modal-header">
          <h2 class="modal-title fs-5">Edit Class</h2>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body fn-admin-modal-body">
          <div class="fn-form-group">
            <label class="fn-label" for="ecName">Class name</label>
            <input id="ecName" name="name" type="text" class="fn-input" required>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="ecDescription">Description</label>
            <textarea id="ecDescription" name="description" rows="3" class="fn-input"></textarea>
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="ecCapacity">Capacity</label>
              <input id="ecCapacity" name="capacity" type="number" min="1" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="ecTrainer">Trainer</label>
              <select id="ecTrainer" name="trainerId" class="fn-input" required>
                <c:forEach var="tr" items="${trainerOptions}">
                  <option value="${tr.trainerId()}">${tr.firstName()} ${tr.lastName()}</option>
                </c:forEach>
              </select>
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="ecDate">Date</label>
            <input id="ecDate" name="date" type="date" class="fn-input" required>
          </div>
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="ecStart">Start time</label>
              <input id="ecStart" name="startTime" type="time" class="fn-input" required>
            </div>
            <div class="col-6">
              <label class="fn-label" for="ecEnd">End time</label>
              <input id="ecEnd" name="endTime" type="time" class="fn-input" required>
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
<script src="${ctx}/js/admin-classes.js"></script>
</body>
</html>
