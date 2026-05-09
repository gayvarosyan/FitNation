<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Member Details – FitNation Admin</title>
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
        <a href="${ctx}/admin/members" class="fn-admin-btn-secondary" style="margin-right: 0.5rem;">← Back to Members</a>
        <h1 class="fn-admin-title" style="display:inline;">Member Details</h1>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Profile Information</h2>
        </div>
      </div>
      
      <div class="row g-3">
        <div class="col-md-3">
          <div class="fn-admin-avatar" style="width:80px;height:80px;font-size:1.5rem;margin:0 auto 1rem;">
            <c:set var="fn0" value="${member.firstName}" />
            <c:set var="ln0" value="${member.lastName}" />
            <c:choose>
              <c:when test="${fn:length(fn0) > 0}">${fn:substring(fn0,0,1)}</c:when>
              <c:otherwise>?</c:otherwise>
            </c:choose>
            <c:if test="${fn:length(ln0) > 0}">${fn:substring(ln0,0,1)}</c:if>
          </div>
        </div>
        <div class="col-md-9">
          <div class="row g-2">
            <div class="col-md-6">
              <label class="fn-label">First Name</label>
              <div class="fn-input" style="background:transparent;border:none;padding:0.5rem 0;">${member.firstName}</div>
            </div>
            <div class="col-md-6">
              <label class="fn-label">Last Name</label>
              <div class="fn-input" style="background:transparent;border:none;padding:0.5rem 0;">${member.lastName}</div>
            </div>
            <div class="col-md-6">
              <label class="fn-label">Email</label>
              <div class="fn-input" style="background:transparent;border:none;padding:0.5rem 0;">${member.email}</div>
            </div>
            <div class="col-md-6">
              <label class="fn-label">Phone</label>
              <div class="fn-input" style="background:transparent;border:none;padding:0.5rem 0;">${empty member.phone ? '—' : member.phone}</div>
            </div>
            <div class="col-md-6">
              <label class="fn-label">Status</label>
              <span class="fn-admin-status fn-admin-status-${fn:toLowerCase(member.userStatus)}">
                <span class="fn-admin-status-dot"></span>${member.userStatus}
              </span>
            </div>
            <div class="col-md-6">
              <label class="fn-label">Join Date</label>
              <div class="fn-input" style="background:transparent;border:none;padding:0.5rem 0;">${member.joinDate}</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Actions</h2>
        </div>
      </div>
      <div class="d-flex gap-2">
        <form method="post" action="${ctx}/admin/members/${member.id}/update" style="display:inline">
          <button type="button" class="fn-admin-action-btn fn-admin-action-edit fn-edit-member"
            data-id="${member.id}"
            data-first="${fn:escapeXml(member.firstName)}"
            data-last="${fn:escapeXml(member.lastName)}"
            data-email="${fn:escapeXml(member.email)}"
            data-phone="${fn:escapeXml(member.phone)}"
            data-status="${member.userStatus}">Edit Member</button>
        </form>
        <a href="${ctx}/admin/clients/${member.id}/progress" class="fn-admin-action-btn fn-admin-action-edit">View Progress</a>
        <form method="post" action="${ctx}/admin/members/${member.id}/delete" style="display:inline" onsubmit="return confirm('Delete this member?');">
          <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>
        </form>
      </div>
    </section>
  </main>
</div>

<div class="modal fade" id="editMemberModal" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content fn-admin-modal">
      <div class="modal-header fn-admin-modal-header">
        <h2 class="modal-title fs-5">Edit Member</h2>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
      </div>
      <form method="post" action="${ctx}/admin/members/${member.id}/update" id="editMemberForm">
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
            <label class="fn-label" for="editPhone">Phone</label>
            <input type="tel" id="editPhone" name="phone" class="fn-input" required maxlength="50">
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
    const editButton = document.querySelector('.fn-edit-member');
    const editModal = document.getElementById('editMemberModal');
    
    if (editButton) {
        editButton.addEventListener('click', function() {
            document.getElementById('editFirstName').value = this.dataset.first;
            document.getElementById('editLastName').value = this.dataset.last;
            document.getElementById('editEmail').value = this.dataset.email;
            document.getElementById('editPhone').value = this.dataset.phone;
            
            new bootstrap.Modal(editModal).show();
        });
    }
});
</script>
</body>
</html>
