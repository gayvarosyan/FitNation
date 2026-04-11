<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Subscription requests – FitNation Admin</title>
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
        <h1 class="fn-admin-title">Subscription requests</h1>
        <p class="fn-admin-subtitle">Approve or reject membership requests. Approval creates an active membership for the plan duration.</p>
      </div>
    </header>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error mb-3" role="alert"><c:out value="${error}" /></div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="fn-alert fn-alert-success mb-3" role="alert"><c:out value="${message}" /></div>
    </c:if>

    <section class="fn-admin-section">
      <form method="get" action="${ctx}/admin/subscription-requests" class="row g-2 align-items-end fn-sub-filter-form mb-3">
        <div class="col-auto">
          <label class="fn-label" for="statusSel6921">Status</label>
          <select name="status" id="statusSel6921" class="fn-input">
            <option value="" ${empty statusFilter ? 'selected' : ''}>All</option>
            <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="APPROVED" ${statusFilter == 'APPROVED' ? 'selected' : ''}>Approved</option>
            <option value="REJECTED" ${statusFilter == 'REJECTED' ? 'selected' : ''}>Rejected</option>
          </select>
        </div>
        <div class="col-auto">
          <button type="submit" class="fn-admin-add-btn">Apply</button>
        </div>
      </form>

      <div class="fn-admin-table-wrapper">
        <table class="fn-admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Member</th>
              <th>Plan</th>
              <th>Status</th>
              <th>Requested</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${requests.content}">
              <tr>
                <td>#${r.id}</td>
                <td><c:out value="${r.userFirstName}" /> <c:out value="${r.userLastName}" /><br/>
                  <small><c:out value="${r.userEmail}" /></small></td>
                <td><c:out value="${r.membershipTypeName}" /><br/>
                  <small>${r.durationDays} days</small></td>
                <td><c:out value="${r.status}" /></td>
                <td><c:out value="${r.createdAt}" /></td>
                <td class="fn-admin-actions-cell">
                  <c:if test="${r.status.name() == 'PENDING'}">
                    <form method="post" action="${ctx}/admin/subscription-requests/${r.id}/approve" style="display:inline" onsubmit="return confirm('Approve this request?');">
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">Accept</button>
                    </form>
                    <form method="post" action="${ctx}/admin/subscription-requests/${r.id}/reject" class="fn-reject-inline-form">
                      <input type="text" name="reason" class="fn-input fn-reject-reason" placeholder="Rejection reason (optional)" />
                      <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Reject</button>
                    </form>
                  </c:if>
                  <c:if test="${r.status.name() == 'REJECTED' && not empty r.rejectionReason}">
                    <small class="text-secondary"><c:out value="${r.rejectionReason}" /></small>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty requests.content}">
              <tr><td colspan="6">No requests for this filter.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>

      <c:if test="${requests.totalPages > 1}">
        <nav class="fn-pagination mt-3" aria-label="Pagination">
          <c:url var="urlPrev" value="/admin/subscription-requests">
            <c:param name="page" value="${requests.number - 1}" />
            <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}" /></c:if>
          </c:url>
          <c:url var="urlNext" value="/admin/subscription-requests">
            <c:param name="page" value="${requests.number + 1}" />
            <c:if test="${not empty statusFilter}"><c:param name="status" value="${statusFilter}" /></c:if>
          </c:url>
          <c:if test="${requests.hasPrevious()}">
            <a class="fn-link" href="${urlPrev}">Previous</a>
          </c:if>
          <span class="fn-portal-muted mx-2">Page ${requests.number + 1} / ${requests.totalPages}</span>
          <c:if test="${requests.hasNext()}">
            <a class="fn-link" href="${urlNext}">Next</a>
          </c:if>
        </nav>
      </c:if>
    </section>
  </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
