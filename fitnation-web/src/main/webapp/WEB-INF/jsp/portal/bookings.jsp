<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Bookings - FitNation</title>
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
        <h1 class="fn-portal-page-title">Class Bookings</h1>
        <p class="fn-portal-page-subtitle">Browse class schedules and manage your bookings.</p>
      </header>

      <c:if test="${not empty error}">
        <div class="fn-alert fn-alert-error mb-3" role="alert"><c:out value="${error}" /></div>
      </c:if>
      <c:if test="${not empty message}">
        <div class="fn-alert fn-alert-success mb-3" role="alert"><c:out value="${message}" /></div>
      </c:if>

      <section class="fn-portal-section" aria-labelledby="schedule-filter-heading">
        <h2 id="schedule-filter-heading" class="fn-portal-section-title">Schedule filters</h2>
        <form method="get" action="${ctx}/portal/bookings" class="fn-sub-filter-form">
          <div class="fn-form-grid">
            <div>
              <label class="form-label" for="trainerId">Trainer</label>
              <select class="form-control fn-input" id="trainerId" name="trainerId">
                <option value="">All trainers</option>
                <c:forEach var="t" items="${trainers}">
                  <option value="${t.trainerId}" ${selectedTrainerId != null && selectedTrainerId.toString() == t.trainerId ? 'selected' : ''}>
                    <c:out value="${t.firstName}" /> <c:out value="${t.lastName}" />
                  </option>
                </c:forEach>
              </select>
            </div>
            <div>
              <label class="form-label" for="fromDate">From</label>
              <input class="form-control fn-input" type="date" id="fromDate" name="fromDate" value="${selectedFromDate}" />
            </div>
            <div>
              <label class="form-label" for="toDate">To</label>
              <input class="form-control fn-input" type="date" id="toDate" name="toDate" value="${selectedToDate}" />
            </div>
            <div>
              <label class="form-label" for="scheduleStatus">Schedule status</label>
              <select class="form-control fn-input" id="scheduleStatus" name="scheduleStatus">
                <option value="">All</option>
                <c:forEach var="s" items="${scheduleStatuses}">
                  <option value="${s}" ${selectedScheduleStatus != null && selectedScheduleStatus.name() == s.name() ? 'selected' : ''}>${s}</option>
                </c:forEach>
              </select>
            </div>
            <div>
              <label class="form-label" for="bookingStatus">My booking status</label>
              <select class="form-control fn-input" id="bookingStatus" name="bookingStatus">
                <option value="">All</option>
                <option value="BOOKED" ${selectedBookingStatus == 'BOOKED' ? 'selected' : ''}>BOOKED</option>
                <option value="CANCELLED" ${selectedBookingStatus == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
              </select>
            </div>
          </div>
          <div class="mt-2">
            <button type="submit" class="fn-portal-btn-primary fn-portal-btn-inline">Apply filters</button>
          </div>
        </form>
      </section>

      <section class="fn-portal-section" aria-labelledby="available-heading">
        <h2 id="available-heading" class="fn-portal-section-title">Available classes</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <thead>
            <tr>
              <th>Class</th>
              <th>Trainer</th>
              <th>Date</th>
              <th>Time</th>
              <th>Spots</th>
              <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="s" items="${availableSchedules}">
              <tr>
                <td><c:out value="${s.className}" /></td>
                <td><c:out value="${s.trainerName}" /></td>
                <td><c:out value="${s.date}" /></td>
                <td><c:out value="${s.startTime}" /> - <c:out value="${s.endTime}" /></td>
                <td>${s.bookedCount} / ${s.capacity}</td>
                <td>
                  <c:choose>
                    <c:when test="${s.full}">
                      <span class="fn-portal-muted">Full</span>
                    </c:when>
                    <c:otherwise>
                      <form method="post" action="${ctx}/portal/bookings/book">
                        <input type="hidden" name="scheduleId" value="${s.scheduleId}" />
                        <button class="fn-portal-btn-primary fn-portal-btn-inline" type="submit">Book</button>
                      </form>
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty availableSchedules}">
              <tr><td colspan="6" class="fn-portal-empty-cell">No schedules match your filters.</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </section>

      <section class="fn-portal-section" aria-labelledby="my-bookings-heading">
        <h2 id="my-bookings-heading" class="fn-portal-section-title">My bookings</h2>
        <div class="fn-portal-table-wrap">
          <table class="fn-portal-table">
            <thead>
            <tr>
              <th>Class</th>
              <th>Trainer</th>
              <th>Date</th>
              <th>Time</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="b" items="${myBookings}">
              <tr>
                <td><c:out value="${b.className}" /></td>
                <td><c:out value="${b.trainerName}" /></td>
                <td><c:out value="${b.date}" /></td>
                <td><c:out value="${b.startTime}" /> - <c:out value="${b.endTime}" /></td>
                <td><span class="fn-portal-status"><c:out value="${b.displayStatus}" /></span></td>
                <td>
                  <c:choose>
                    <c:when test="${b.status.name() == 'BOOKED'}">
                      <form method="post" action="${ctx}/portal/bookings/cancel">
                        <input type="hidden" name="bookingId" value="${b.bookingId}" />
                        <button class="fn-portal-btn-secondary fn-portal-btn-inline" type="submit">Cancel</button>
                      </form>
                    </c:when>
                    <c:otherwise>
                      <span class="fn-portal-muted">-</span>
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty myBookings}">
              <tr><td colspan="6" class="fn-portal-empty-cell">No bookings yet.</td></tr>
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
