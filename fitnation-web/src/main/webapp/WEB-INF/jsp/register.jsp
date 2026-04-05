<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register – FitNation</title>
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

    <form class="fn-form" method="post" action="${pageContext.request.contextPath}/register">
      <div class="row g-2">
        <div class="col-6">
          <div class="fn-form-group">
            <label class="fn-label" for="firstName">First name</label>
            <input type="text" id="firstName" name="firstName" class="fn-input" required maxlength="50">
          </div>
        </div>
        <div class="col-6">
          <div class="fn-form-group">
            <label class="fn-label" for="lastName">Last name</label>
            <input type="text" id="lastName" name="lastName" class="fn-input" required maxlength="50">
          </div>
        </div>
      </div>
      <div class="fn-form-group">
        <label class="fn-label" for="regEmail">Email</label>
        <input type="email" id="regEmail" name="email" class="fn-input" required autocomplete="email" placeholder="you@example.com">
      </div>
      <div class="fn-form-group">
        <label class="fn-label" for="regPassword">Password</label>
        <input type="password" id="regPassword" name="password" class="fn-input" required autocomplete="new-password" minlength="8" placeholder="••••••••">
      </div>
      <div class="fn-form-group">
        <label class="fn-label" for="phone">Phone</label>
        <input type="tel" id="phone" name="phone" class="fn-input" required maxlength="50" placeholder="+1 555 000 0000">
      </div>

      <input type="hidden" name="role" id="roleInput" value="CLIENT">
      <span class="fn-role-label">Role</span>
      <div class="fn-role-group" role="group" aria-label="Registration role">
        <button type="button" class="fn-role-btn active" data-role="CLIENT">User</button>
        <button type="button" class="fn-role-btn" data-role="TRAINER">Trainer</button>
      </div>

      <div id="trainerFields" class="fn-trainer-fields" style="display: none;">
        <div class="fn-form-group">
          <label class="fn-label" for="specialization">Specialization</label>
          <input type="text" id="specialization" name="specialization" class="fn-input" maxlength="50" placeholder="e.g. Yoga, Strength">
        </div>
        <div class="fn-form-group">
          <label class="fn-label" for="bio">Bio</label>
          <textarea id="bio" name="bio" class="fn-input" rows="3" maxlength="250" placeholder="Short bio"></textarea>
        </div>
      </div>

      <button type="submit" class="fn-btn-primary" id="registerBtn">Register</button>
    </form>

    <div class="fn-nav">
      <a href="${pageContext.request.contextPath}/login" class="fn-nav-btn">Login</a>
    </div>
  </div>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/register.js"></script>
</body>
</html>
