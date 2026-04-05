<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login – FitNation</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
  <div class="fn-container">
    <div class="fn-logo-wrap" aria-hidden="true">
      <svg class="fn-logo" viewBox="0 0 24 24" fill="currentColor">
        <path d="M20.57 14.86L22 13.43 20.57 12 17 15.57 8.43 7 12 3.43 10.57 2 9.14 3.43 7.71 2 5.57 4.14 4.14 2.71 2 4.86l1.43 1.43L2 7.71l1.43 1.43L2 10.57 3.43 12 7 8.43 15.57 17 12 20.57 13.43 22l1.43-1.43L16.29 22l2.14-2.14 1.43 1.43L22 19.14l-1.43-1.43L22 16.29z"/>
      </svg>
    </div>
    <h1 class="fn-title">FitNation</h1>
    <p class="fn-tagline">Modern fitness management</p>

    <c:if test="${not empty error}">
      <div class="fn-alert fn-alert-error" role="alert">${error}</div>
    </c:if>
    <c:if test="${not empty message}">
      <div class="fn-alert fn-alert-success" role="alert">${message}</div>
    </c:if>

    <form class="fn-form" method="post" action="${pageContext.request.contextPath}/login">
      <div class="fn-form-group">
        <label class="fn-label" for="email">Email</label>
        <input type="email" id="email" name="email" class="fn-input" required autocomplete="email" placeholder="you@example.com" value="${loginRequest.email}">
      </div>
      <div class="fn-form-group">
        <div class="fn-label-row">
          <label class="fn-label" for="password">Password</label>
          <a href="#" class="fn-forgot">Forgot?</a>
        </div>
        <input type="password" id="password" name="password" class="fn-input" required autocomplete="current-password" placeholder="••••••••">
      </div>

      <button type="submit" class="fn-btn-primary" id="loginBtn">Login</button>
    </form>

    <div class="fn-nav">
      <a href="${pageContext.request.contextPath}/register" class="fn-nav-btn">Register</a>
    </div>
  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
