<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Subscriptions – FitNation</title>
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
      <header class="fn-portal-page-head">
        <h1 class="fn-portal-page-title">Choose Your Plan</h1>
        <p class="fn-portal-page-subtitle">Select the perfect membership to help you reach your fitness goals. Subscriptions become active after an administrator approves your request.</p>
      </header>

      <c:if test="${not empty error}">
        <div class="fn-alert fn-alert-error mb-3" role="alert"><c:out value="${error}" /></div>
      </c:if>
      <c:if test="${not empty message}">
        <div class="fn-alert fn-alert-success mb-3" role="alert"><c:out value="${message}" /></div>
      </c:if>

      <c:choose>
        <c:when test="${activeMembership != null}">
          <section class="fn-portal-membership-banner" aria-label="Current membership">
            <div class="fn-portal-banner-icon" aria-hidden="true">🛡️</div>
            <div class="fn-portal-banner-body">
              <div class="fn-portal-banner-label">Current membership</div>
              <div class="fn-portal-banner-title"><c:out value="${activeMembership.membershipType}" /></div>
              <div class="fn-portal-banner-meta">Through <c:out value="${activeMembership.endDate}" /> · <span class="fn-portal-badge fn-portal-badge-active">Active</span></div>
            </div>
          </section>
        </c:when>
        <c:otherwise>
          <section class="fn-portal-membership-banner fn-portal-membership-banner-muted" aria-label="No active membership">
            <div class="fn-portal-banner-icon" aria-hidden="true">🛡️</div>
            <div class="fn-portal-banner-body">
              <div class="fn-portal-banner-label">Current membership</div>
              <div class="fn-portal-banner-title">No active subscription</div>
              <div class="fn-portal-banner-meta">Request a plan below. Once approved, your access starts right away.</div>
            </div>
          </section>
        </c:otherwise>
      </c:choose>

      <section class="fn-portal-section" aria-labelledby="plans-heading">
        <h2 id="plans-heading" class="fn-portal-section-title">Plans</h2>
        <div class="fn-portal-plan-grid">
          <c:forEach var="p" items="${plans}">
            <article class="fn-portal-plan-card">
              <h3 class="fn-portal-plan-name"><c:out value="${p.name}" /></h3>
              <p class="fn-portal-plan-desc"><c:out value="${p.description}" /></p>
              <div class="fn-portal-plan-price">
                <fmt:formatNumber value="${p.price}" type="currency" currencyCode="USD" />
                <span class="fn-portal-plan-duration"> · ${p.durationDays} days</span>
              </div>
              <ul class="fn-portal-plan-features">
                <li><strong>Includes:</strong></li>
                <c:choose>
                  <c:when test="${p.nutritionPlanId != null}">
                    <li>Nutrition:
                      <c:choose>
                        <c:when test="${not empty nutritionNames[p.nutritionPlanId]}"><c:out value="${nutritionNames[p.nutritionPlanId]}" /></c:when>
                        <c:otherwise>Plan #${p.nutritionPlanId}</c:otherwise>
                      </c:choose>
                    </li>
                  </c:when>
                  <c:otherwise><li>Nutrition: —</li></c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${p.trainerId != null}">
                    <li>Trainer:
                      <c:choose>
                        <c:when test="${not empty trainerNames[p.trainerId]}"><c:out value="${trainerNames[p.trainerId]}" /></c:when>
                        <c:otherwise>Trainer #${p.trainerId}</c:otherwise>
                      </c:choose>
                    </li>
                  </c:when>
                  <c:otherwise><li>Trainer: —</li></c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${p.groupClassId != null}">
                    <li>Class:
                      <c:choose>
                        <c:when test="${not empty groupClassNames[p.groupClassId]}"><c:out value="${groupClassNames[p.groupClassId]}" /></c:when>
                        <c:otherwise>Class #${p.groupClassId}</c:otherwise>
                      </c:choose>
                    </li>
                  </c:when>
                  <c:otherwise><li>Group class: —</li></c:otherwise>
                </c:choose>
              </ul>
              <form method="post" action="${ctx}/portal/subscriptions/request" class="fn-portal-plan-form">
                <input type="hidden" name="membershipTypeId" value="${p.id}" />
                <button type="submit" class="fn-portal-btn-primary">Request subscription</button>
              </form>
            </article>
          </c:forEach>
        </div>
        <c:if test="${empty plans}">
          <p class="fn-portal-empty">No membership plans are available yet.</p>
        </c:if>
      </section>

      <section class="fn-portal-section" aria-labelledby="req-heading">
        <h2 id="req-heading" class="fn-portal-section-title">My requests</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <thead>
              <tr>
                <th>Plan</th>
                <th>Status</th>
                <th>Requested</th>
                <th>Notes</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="r" items="${myRequests}">
                <tr>
                  <td><c:out value="${r.membershipTypeName}" /> <span class="fn-portal-muted">(${r.durationDays} days)</span></td>
                  <td><span class="fn-portal-status fn-portal-status-${r.status.name()}"><c:out value="${r.status}" /></span></td>
                  <td><c:out value="${r.createdAt}" /></td>
                  <td>
                    <c:choose>
                      <c:when test="${r.status.name() == 'REJECTED' && not empty r.rejectionReason}">
                        <c:out value="${r.rejectionReason}" />
                      </c:when>
                      <c:otherwise>—</c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty myRequests}">
                <tr><td colspan="4" class="fn-portal-empty-cell">You have not submitted any requests yet.</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </section>

      <section class="fn-portal-section" aria-labelledby="mem-heading">
        <h2 id="mem-heading" class="fn-portal-section-title">My memberships</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <thead>
              <tr>
                <th>Plan</th>
                <th>Start</th>
                <th>End</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="m" items="${myMemberships}">
                <tr>
                  <td><c:out value="${m.membershipType}" /></td>
                  <td><c:out value="${m.startDate}" /></td>
                  <td><c:out value="${m.endDate}" /></td>
                  <td><c:out value="${m.status}" /></td>
                  <td><a class="fn-portal-btn-secondary fn-portal-btn-inline" href="${ctx}/portal/memberships/${m.id}/freeze">Freeze</a></td>
                </tr>
              </c:forEach>
              <c:if test="${empty myMemberships}">
                <tr><td colspan="5" class="fn-portal-empty-cell">No memberships yet.</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </main>
</div>
</body>
</html>
