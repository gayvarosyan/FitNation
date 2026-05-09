<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Users - FitNation</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="fn-admin-body">
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<div class="fn-admin-shell">
  <%@ include file="admin-sidebar.jspf" %>
  <main class="fn-admin-main">
    <header class="fn-admin-header"><h1 class="fn-admin-title">Admin Users</h1></header>
    <c:if test="${not empty error}"><div class="fn-alert fn-alert-error mb-3">${error}</div></c:if>
    <c:if test="${not empty message}"><div class="fn-alert fn-alert-success mb-3">${message}</div></c:if>
    <section class="fn-admin-section">
      <form method="post" action="${ctx}/admin/users/delete" class="fn-sub-filter-form">
        <label class="form-label">Soft delete user by ID</label>
        <input class="form-control fn-input" type="number" name="userId" required />
        <div class="mt-2"><button class="fn-admin-action-btn fn-admin-action-delete" type="submit">Soft Delete</button></div>
      </form>
    </section>
    <section class="fn-admin-section">
      <form method="post" action="${ctx}/admin/users/restore" class="fn-sub-filter-form">
        <label class="form-label">Restore deleted user by ID</label>
        <input class="form-control fn-input" type="number" name="userId" required />
        <div class="mt-2"><button class="fn-admin-action-btn fn-admin-action-edit" type="submit">Restore</button></div>
      </form>
    </section>
  </main>
</div>
</body>
</html>
