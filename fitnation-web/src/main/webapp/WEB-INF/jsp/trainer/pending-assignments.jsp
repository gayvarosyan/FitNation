<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Pending Assignments – FitNation Trainer</title>
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
        <h1 class="fn-admin-title">Pending Assignments</h1>
        <p class="fn-admin-subtitle">Review and respond to client assignment requests.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert">${error}</div>
    </c:if>

    <section class="fn-admin-section">
      <div class="fn-admin-section-header">
        <div>
          <h2 class="fn-admin-section-title">Assignment Requests</h2>
          <p class="fn-admin-section-subtitle">Clients requesting your training services.</p>
        </div>
      </div>
      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table" aria-label="Pending assignments">
          <thead>
            <tr>
              <th scope="col">Client</th>
              <th scope="col">Email</th>
              <th scope="col">Message</th>
              <th scope="col">Requested</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="req" items="${trainerPendingRequests}">
              <tr>
                <td>${req.clientName}</td>
                <td>${req.clientEmail}</td>
                <td style="max-width:250px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${req.message}</td>
                <td><span class="fn-admin-pill"><span class="fn-admin-pill-icon">📅</span> ${req.requestedAt}</span></td>
                <td class="fn-admin-actions-cell">
                  <form method="post" action="${ctx}/trainer-assignments/approve" style="display:inline">
                    <input type="hidden" name="assignmentRequestId" value="${req.id}" />
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">Approve</button>
                  </form>
                  <form method="post" action="${ctx}/trainer-assignments/reject" style="display:inline" onsubmit="return confirm('Reject this request?');">
                    <input type="hidden" name="assignmentRequestId" value="${req.id}" />
                    <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Reject</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty trainerPendingRequests}">
              <tr><td colspan="5" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No pending assignment requests.</td></tr>
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
