<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Membership Requests – FitNation Admin</title>
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
        <h1 class="fn-admin-title">Membership Requests</h1>
        <p class="fn-admin-subtitle">Review and approve membership requests.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Requests</h2>
          <p class="fn-admin-section-subtitle">All membership requests in the system.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Membership requests">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">User</th>
              <th scope="col">Email</th>
              <th scope="col">Status</th>
              <th scope="col">Submitted</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="req" items="${requests.content}">
              <tr>
                <td>${req.id}</td>
                <td>${req.userName}</td>
                <td>${req.userEmail}</td>
                <td><span class="fn-admin-status fn-admin-status-${fn:toLowerCase(req.status)}"><span class="fn-admin-status-dot"></span>${req.status}</span></td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">📅</span> ${req.submittedAt}</span></td>
                <td class="fn-admin-actions-cell">
                  <c:if test="${req.status == 'PENDING'}">
                    <form method="post" action="${ctx}/admin/membership-requests/${req.id}/approve" style="display:inline">
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">Approve</button>
                    </form>
                    <form method="post" action="${ctx}/admin/membership-requests/${req.id}/reject" style="display:inline">
                      <input type="text" name="reason" placeholder="Reason (optional)" style="width:150px;padding:0.3rem 0.5rem;margin-right:0.35rem;border-radius:4px;border:1px solid var(--fn-border);background:var(--fn-input-bg);color:var(--fn-text);">
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Reject</button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty requests.content}">
              <tr><td colspan="6" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No membership requests found.</td></tr>
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
