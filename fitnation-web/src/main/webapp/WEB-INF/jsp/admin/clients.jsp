<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Client Management – FitNation Admin</title>
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
        <h1 class="fn-admin-title">Client Management</h1>
        <p class="fn-admin-subtitle">Onboard and manage your gym members.</p>
      </div>
      <button class="fn-admin-add-btn" type="button" data-bs-toggle="modal" data-bs-target="#addClientModal">
        <span class="fn-admin-add-icon">+</span>
        <span>Invite New Client</span>
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
          <div class="fn-admin-stat-icon" aria-hidden="true">👥</div>
          <div>
            <div class="fn-admin-stat-meta">Total Clients</div>
            <div class="fn-admin-stat-value">${stats.totalMembers}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">✅</div>
          <div>
            <div class="fn-admin-stat-meta">Currently Active</div>
            <div class="fn-admin-stat-value">${stats.totalActiveUsers}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">💳</div>
          <div>
            <div class="fn-admin-stat-meta">Active Subscriptions</div>
            <div class="fn-admin-stat-value">${stats.usersWithActiveSubscription}</div>
          </div>
        </div>
        <div class="fn-admin-stat-card">
          <div class="fn-admin-stat-icon" aria-hidden="true">🚫</div>
          <div>
            <div class="fn-admin-stat-meta">Blocked</div>
            <div class="fn-admin-stat-value">${stats.blockedMembers}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Client Directory</h2>
          <p class="fn-admin-section-subtitle">All clients in the system.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Client directory">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Client</th>
              <th scope="col">Contact</th>
              <th scope="col">Join Date</th>
              <th scope="col">Status</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="c" items="${clients}">
              <tr>
                <td>${c.formattedId}</td>
                <td>
                  <div class="fn-admin-trainer-cell">
                    <div class="fn-admin-avatar">
                      <c:set var="fn0" value="${c.firstName}" />
                      <c:set var="ln0" value="${c.lastName}" />
                      <c:choose>
                        <c:when test="${fn:length(fn0) > 0}">${fn:substring(fn0,0,1)}</c:when>
                        <c:otherwise>?</c:otherwise>
                      </c:choose>
                      <c:if test="${fn:length(ln0) > 0}">${fn:substring(ln0,0,1)}</c:if>
                    </div>
                    <div class="fn-admin-trainer-main">
                      <div class="fn-admin-trainer-name">${c.firstName} ${c.lastName}</div>
                    </div>
                  </div>
                </td>
                <td class="fn-admin-contact">
                  <span><span>✉️</span><span>${c.email}</span></span><br/>
                  <span><span>📞</span><span>${empty c.phone ? '—' : c.phone}</span></span>
                </td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">📅</span> ${c.joinDate}</span></td>
                <td><span class="fn-admin-status fn-admin-status-${fn:toLowerCase(c.userStatus)}"><span class="fn-admin-status-dot"></span>${c.userStatus}</span></td>
                <td class="fn-admin-actions-cell">
                  <c:if test="${c.userStatus == 'PENDING'}">
                    <form method="post" action="${ctx}/admin/clients/invite" style="display:inline">
                      <input type="hidden" name="firstName" value="${c.firstName}" />
                      <input type="hidden" name="lastName" value="${c.lastName}" />
                      <input type="hidden" name="email" value="${c.email}" />
                      <input type="hidden" name="phone" value="${c.phone}" />
                      <input type="hidden" name="password" value="TempPassword123!" />
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">
                        Resend Invite
                      </button>
                    </form>
                  </c:if>
                  <c:choose>
                    <c:when test="${c.userStatus == 'PENDING'}">
                      <button type="button" class="fn-admin-action-btn fn-admin-action-edit" disabled>Edit</button>
                    </c:when>
                    <c:otherwise>
                      <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-client"
                        data-id="${c.id}"
                        data-first="${fn:escapeXml(c.firstName)}"
                        data-last="${fn:escapeXml(c.lastName)}"
                        data-email="${fn:escapeXml(c.email)}"
                        data-phone="${fn:escapeXml(c.phone)}"
                        data-status="${c.userStatus}">Edit</button>
                    </c:otherwise>
                  </c:choose>
                  <form method="post" action="${ctx}/admin/clients/delete" style="display:inline" onsubmit="return confirm('Delete this client?');">
                    <input type="hidden" name="clientId" value="${c.id}" />
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty clients}">
              <tr><td colspan="6" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No clients found.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</div>

<div class="modal fade" id="addClientModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <div class="modal-header fn-admin-modal-header">
        <h2 class="modal-title fs-5">Invite New Client</h2>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <form method="post" action="${ctx}/admin/clients/create">
        <div class="modal-body fn-admin-modal-body">
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="addFirstName">First name</label>
              <input type="text" id="addFirstName" name="firstName" class="fn-input" required maxlength="50">
            </div>
            <div class="col-6">
              <label class="fn-label" for="addLastName">Last name</label>
              <input type="text" id="addLastName" name="lastName" class="fn-input" required maxlength="50">
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="addEmail">Email</label>
            <input type="email" id="addEmail" name="email" class="fn-input" required>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="addPassword">Password</label>
            <input type="password" id="addPassword" name="password" class="fn-input" required minlength="8"
                   autocomplete="new-password"
                   title="At least 8 characters, one uppercase letter, one number, and one special character">
            <p class="fn-admin-section-subtitle" style="margin-top:0.35rem;margin-bottom:0;">Use at least 8 characters with one uppercase letter, one number, and one special character (e.g. @$!%*?&amp;._-#+). This is the client's first-login password; it is also sent in the invitation email.</p>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="addPhone">Phone</label>
            <input type="tel" id="addPhone" name="phone" class="fn-input" required maxlength="50">
          </div>
          <p class="fn-admin-section-subtitle">Clients stay pending until first login.</p>
        </div>
        <div class="modal-footer fn-admin-modal-footer">
          <button type="button" class="fn-admin-btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="fn-admin-add-btn">Invite Client</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="modal fade" id="editClientModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <div class="modal-header fn-admin-modal-header">
        <h2 class="modal-title fs-5">Edit Client</h2>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <form method="post" action="${ctx}/admin/clients/edit" id="editClientForm">
        <input type="hidden" name="clientId" id="editClientId" />
        <div class="modal-body fn-admin-modal-body">
          <div class="row g-2">
            <div class="col-6">
              <label class="fn-label" for="editFirstName">First name</label>
              <input type="text" id="editFirstName" name="firstName" class="fn-input" required maxlength="50">
            </div>
            <div class="col-6">
              <label class="fn-label" for="editLastName">Last name</label>
              <input type="text" id="editLastName" name="lastName" class="fn-input" required maxlength="50">
            </div>
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editEmail">Email</label>
            <input type="email" id="editEmail" name="email" class="fn-input" maxlength="100">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editPassword">New password</label>
            <input type="password" id="editPassword" name="password" class="fn-input" minlength="8" placeholder="Leave blank to keep current">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editPhone">Phone</label>
            <input type="tel" id="editPhone" name="phone" class="fn-input" required maxlength="50">
          </div>
          <div class="fn-form-group">
            <label class="fn-label" for="editStatus">Status</label>
            <select id="editStatus" name="status" class="fn-input">
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="BLOCKED">Blocked</option>
            </select>
          </div>
        </div>
        <div class="modal-footer fn-admin-modal-footer">
          <button type="button" class="fn-admin-btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="fn-admin-add-btn">Save changes</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const editButtons = document.querySelectorAll('.fn-edit-client');
    const editModal = document.getElementById('editClientModal');
    
    editButtons.forEach(button => {
        button.addEventListener('click', function() {
            document.getElementById('editClientId').value = this.dataset.id;
            document.getElementById('editFirstName').value = this.dataset.first;
            document.getElementById('editLastName').value = this.dataset.last;
            document.getElementById('editEmail').value = this.dataset.email;
            document.getElementById('editPhone').value = this.dataset.phone;
            document.getElementById('editStatus').value = this.dataset.status;
            
            new bootstrap.Modal(editModal).show();
        });
    });
});
</script>
</body>
</html>
