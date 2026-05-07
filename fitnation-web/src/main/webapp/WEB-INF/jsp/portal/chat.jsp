<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Chat - FitNation</title>
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
      <header class="fn-portal-page-head"><h1 class="fn-portal-page-title">Chat</h1></header>
      <c:if test="${not empty error}"><div class="fn-alert fn-alert-error mb-3">${error}</div></c:if>
      <c:if test="${not empty message}"><div class="fn-alert fn-alert-success mb-3">${message}</div></c:if>
      <section class="fn-portal-section">
        <form method="post" action="${ctx}/portal/chat/open" class="fn-sub-filter-form">
          <label class="form-label">Open conversation with user ID</label>
          <input class="form-control fn-input" name="otherUserId" type="number" required />
          <div class="mt-2"><button class="fn-portal-btn-primary fn-portal-btn-inline" type="submit">Open</button></div>
        </form>
      </section>
      <section class="fn-portal-section">
        <h2 class="fn-portal-section-title">Conversations</h2>
        <div class="fn-portal-table-wrap"><table class="fn-portal-table"><thead><tr><th>ID</th><th>Client</th><th>Trainer</th><th>Last Message At</th><th></th></tr></thead><tbody>
        <c:forEach var="c" items="${conversations}">
          <tr><td>${c.id}</td><td>${c.clientId}</td><td>${c.trainerId}</td><td>${c.lastMessageAt}</td>
            <td><a href="${ctx}/portal/chat?conversationId=${c.id}" class="fn-portal-btn-secondary fn-portal-btn-inline">View</a></td></tr>
        </c:forEach>
        <c:if test="${empty conversations}"><tr><td colspan="5" class="fn-portal-empty-cell">No conversations.</td></tr></c:if>
        </tbody></table></div>
      </section>
      <c:if test="${selectedConversationId != null}">
        <section class="fn-portal-section">
          <h2 class="fn-portal-section-title">Messages (Conversation #${selectedConversationId})</h2>
          <div class="fn-portal-table-wrap"><table class="fn-portal-table"><thead><tr><th>Sender</th><th>Body</th><th>Created</th></tr></thead><tbody>
          <c:forEach var="m" items="${messages}">
            <tr><td>${m.senderId}</td><td><c:out value="${m.body}" /></td><td>${m.createdAt}</td></tr>
          </c:forEach>
          <c:if test="${empty messages}"><tr><td colspan="3" class="fn-portal-empty-cell">No messages.</td></tr></c:if>
          </tbody></table></div>
        </section>
      </c:if>
    </div>
  </main>
</div>
</body>
</html>
