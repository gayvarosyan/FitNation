<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Membership Freeze - FitNation</title>
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/portal.css" rel="stylesheet">
</head>
<body class="fn-portal-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-portal-shell">
  <%@ include file="portal-sidebar.jspf" %>
  <main class="fn-portal-main">
    <%@ include file="portal-header.jspf" %>
    <div class="fn-portal-content">
      <header class="fn-portal-page-head"><h1 class="fn-portal-page-title">Membership Freeze</h1></header>
      <c:if test="${not empty error}"><div class="fn-alert fn-alert-error mb-3">${error}</div></c:if>
      <c:if test="${not empty message}"><div class="fn-alert fn-alert-success mb-3">${message}</div></c:if>
      <section class="fn-portal-section">
        <form method="post" action="${ctx}/portal/memberships/${membershipId}/freeze/submit" class="fn-sub-filter-form">
          <div class="fn-form-grid">
            <div><label class="form-label">Freeze Start</label><input class="form-control fn-input" type="date" name="freezeStart" required /></div>
            <div><label class="form-label">Freeze End</label><input class="form-control fn-input" type="date" name="freezeEnd" required /></div>
          </div>
          <div class="mt-2"><button class="fn-portal-btn-primary fn-portal-btn-inline" type="submit">Submit Freeze Request</button></div>
        </form>
      </section>
      <section class="fn-portal-section">
        <h2 class="fn-portal-section-title">My Freeze Requests</h2>
        <div class="fn-portal-table-wrap"><table class="fn-portal-table"><thead><tr><th>ID</th><th>Start</th><th>End</th><th>Status</th><th>Reason</th></tr></thead><tbody>
        <c:forEach var="r" items="${requests}">
          <tr><td>${r.id}</td><td>${r.freezeStart}</td><td>${r.freezeEnd}</td><td>${r.status}</td><td><c:out value="${r.rejectionReason}" /></td></tr>
        </c:forEach>
        <c:if test="${empty requests}"><tr><td colspan="5" class="fn-portal-empty-cell">No freeze requests.</td></tr></c:if>
        </tbody></table></div>
      </section>
    </div>
  </main>
</div>
</body>
</html>
