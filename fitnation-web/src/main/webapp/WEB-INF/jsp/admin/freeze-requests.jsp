<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Freeze Requests - FitNation Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>
  <main class="fn-admin-main">
    <header class="fn-admin-header"><h1 class="fn-admin-title">Membership Freeze Requests</h1></header>
    <c:if test="${not empty error}"><div class="fn-alert fn-alert-error mb-3">${error}</div></c:if>
    <c:if test="${not empty message}"><div class="fn-alert fn-alert-success mb-3">${message}</div></c:if>
    <section class="fn-admin-section">
      <div class="fn-admin-table-wrapper"><table class="fn-admin-table"><thead><tr><th>ID</th><th>User</th><th>Membership</th><th>Range</th><th>Status</th><th>Actions</th></tr></thead><tbody>
      <c:forEach var="r" items="${requests.content}">
        <tr>
          <td>${r.id}</td>
          <td>${r.userFirstName} ${r.userLastName} (${r.userEmail})</td>
          <td>${r.membershipTypeName}</td>
          <td>${r.freezeStart} - ${r.freezeEnd}</td>
          <td>${r.status}</td>
          <td>
            <form method="post" action="${ctx}/admin/membership-freeze-requests/${r.id}/approve" style="display:inline">
              <button type="submit" class="fn-admin-action-btn fn-admin-action-edit">Approve</button>
            </form>
            <form method="post" action="${ctx}/admin/membership-freeze-requests/${r.id}/reject" style="display:inline">
              <input type="text" name="reason" placeholder="Reason" />
              <button type="submit" class="fn-admin-action-btn fn-admin-action-delete">Reject</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty requests.content}"><tr><td colspan="6" style="text-align:center;padding:1.25rem;color:var(--fn-text-muted);">No freeze requests.</td></tr></c:if>
      </tbody></table></div>
    </section>
  </main>
</div>
</body>
</html>
