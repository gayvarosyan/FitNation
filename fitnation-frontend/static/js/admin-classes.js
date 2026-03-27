(function () {
  'use strict';

  var API_BASE = '/api';
  var schedules = [];
  var trainers = [];

  var scheduleTableBody = document.getElementById('scheduleTableBody');
  var scheduleError = document.getElementById('scheduleError');
  var statsError = document.getElementById('statsError');
  var logoutBtn = document.getElementById('logoutBtn');
  var toastContainer = document.getElementById('toastContainer');

  var scheduleClassBtn = document.getElementById('scheduleClassBtn');
  var scheduleClassModalEl = document.getElementById('scheduleClassModal');
  var scheduleClassSubmitBtn = document.getElementById('scheduleClassSubmitBtn');
  var scheduleClassAlert = document.getElementById('scheduleClassAlert');

  var editClassModalEl = document.getElementById('editClassModal');
  var editClassSubmitBtn = document.getElementById('editClassSubmitBtn');
  var editClassAlert = document.getElementById('editClassAlert');

  var deleteScheduleModalEl = document.getElementById('deleteScheduleModal');
  var deleteScheduleConfirmBtn = document.getElementById('deleteScheduleConfirmBtn');
  var deleteScheduleAlert = document.getElementById('deleteScheduleAlert');

  function getToken() {
    return localStorage.getItem('token');
  }

  function authHeaders() {
    var headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' };
    var token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    return headers;
  }

  function request(path, method, body) {
    return fetch(API_BASE + path, {
      method: method,
      headers: authHeaders(),
      body: body ? JSON.stringify(body) : undefined
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () { return {}; }).then(function (err) {
          throw new Error((err && (err.message || err.error)) || 'Request failed');
        });
      }
      if (res.status === 204) return null;
      return res.text().then(function (t) { return t ? JSON.parse(t) : null; });
    });
  }

  function showError(el, message) {
    if (!el) return;
    el.textContent = message;
    el.style.display = 'block';
  }

  function hideError(el) {
    if (!el) return;
    el.style.display = 'none';
  }

  function showToast(message) {
    if (!toastContainer) return;
    var toast = document.createElement('div');
    toast.className = 'fn-toast';
    toast.textContent = message;
    toast.setAttribute('role', 'status');
    toastContainer.appendChild(toast);
    setTimeout(function () {
      if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 3500);
  }

  function formatDate(dateString) {
    if (!dateString) return '—';
    var d = new Date(dateString);
    if (isNaN(d.getTime())) return dateString;
    return d.toLocaleDateString();
  }

  function formatTime(timeString) {
    if (!timeString) return '—';
    return String(timeString).slice(0, 5);
  }

  function toDateInputValue(dateString) {
    if (!dateString) return '';
    return String(dateString).slice(0, 10);
  }

  function buildStats(list) {
    var now = new Date();
    var weekStart = new Date(now);
    weekStart.setDate(now.getDate() - now.getDay() + 1);
    weekStart.setHours(0, 0, 0, 0);
    var weekEnd = new Date(weekStart);
    weekEnd.setDate(weekStart.getDate() + 7);

    var classesThisWeek = 0;
    var uniqueClassSet = {};

    list.forEach(function (item) {
      if (item.classId != null) uniqueClassSet[item.classId] = true;
      var d = new Date(item.date);
      if (!isNaN(d.getTime()) && d >= weekStart && d < weekEnd) classesThisWeek += 1;
    });

    document.getElementById('classesThisWeek').textContent = String(classesThisWeek);
    document.getElementById('totalSchedules').textContent = String(list.length);
    document.getElementById('uniqueClasses').textContent = String(Object.keys(uniqueClassSet).length);
  }

  function renderSchedules(list) {
    if (!scheduleTableBody) return;
    scheduleTableBody.innerHTML = '';

    if (!Array.isArray(list) || list.length === 0) {
      var tr = document.createElement('tr');
      var td = document.createElement('td');
      td.colSpan = 5;
      td.textContent = 'No class schedules yet.';
      td.style.textAlign = 'center';
      td.style.padding = '1rem';
      td.style.color = 'var(--fn-text-muted)';
      tr.appendChild(td);
      scheduleTableBody.appendChild(tr);
      return;
    }

    list.forEach(function (item) {
      var row = document.createElement('tr');

      var classCell = document.createElement('td');
      classCell.innerHTML =
        '<div class="fn-class-name">' + (item.className || '—') + '</div>' +
        '<div class="fn-class-id">CLS-' + (item.classId != null ? item.classId : '—') + '</div>';

      var scheduleCell = document.createElement('td');
      scheduleCell.innerHTML =
        '<div class="fn-class-name">' + formatDate(item.date) + '</div>' +
        '<div class="fn-class-id">' + formatTime(item.startTime) + ' - ' + formatTime(item.endTime) + '</div>';

      var trainerCell = document.createElement('td');
      trainerCell.textContent = item.trainerName || '—';

      var capCell = document.createElement('td');
      capCell.textContent = item.capacity != null ? String(item.capacity) : '—';

      var actionsCell = document.createElement('td');
      actionsCell.className = 'fn-admin-actions-cell';

      var editBtn = document.createElement('button');
      editBtn.type = 'button';
      editBtn.className = 'fn-admin-action-btn fn-admin-action-edit';
      editBtn.textContent = 'Edit';
      editBtn.addEventListener('click', function () { openEditModal(item); });

      var delBtn = document.createElement('button');
      delBtn.type = 'button';
      delBtn.className = 'fn-admin-action-btn fn-admin-action-delete';
      delBtn.textContent = 'Delete';
      delBtn.addEventListener('click', function () { openDeleteModal(item); });

      actionsCell.appendChild(editBtn);
      actionsCell.appendChild(delBtn);

      row.appendChild(classCell);
      row.appendChild(scheduleCell);
      row.appendChild(trainerCell);
      row.appendChild(capCell);
      row.appendChild(actionsCell);
      scheduleTableBody.appendChild(row);
    });
  }

  function fetchSchedules() {
    hideError(scheduleError);
    hideError(statsError);
    return request('/classes/schedule', 'GET').then(function (list) {
      schedules = Array.isArray(list) ? list : [];
      renderSchedules(schedules);
      buildStats(schedules);
      populateClassSelect();
    }).catch(function (err) {
      showError(scheduleError, err.message || 'Failed to load schedules.');
      showError(statsError, err.message || 'Failed to load class stats.');
    });
  }

  function fetchTrainers() {
    return request('/trainers', 'GET').then(function (list) {
      trainers = Array.isArray(list) ? list : [];
      populateTrainerSelects();
    }).catch(function () {
      trainers = [];
      populateTrainerSelects();
    });
  }

  function populateClassSelect() {
    // No-op: schedule modal now creates a class inline.
  }

  function populateTrainerSelects() {
    ['ecTrainer', 'scTrainer'].forEach(function (id) {
      var select = document.getElementById(id);
      if (!select) return;
      select.innerHTML = '';
      if (!trainers.length) {
        var empty = document.createElement('option');
        empty.value = '';
        empty.textContent = 'No trainers available';
        select.appendChild(empty);
        return;
      }
      trainers.forEach(function (t) {
        var option = document.createElement('option');
        var trainerId = t.trainerId != null ? t.trainerId : t.id;
        option.value = trainerId;
        option.textContent = (t.firstName || '') + ' ' + (t.lastName || '');
        select.appendChild(option);
      });
    });
  }

  function openEditModal(item) {
    hideError(editClassAlert);
    document.getElementById('ecClassId').value = item.classId || '';
    document.getElementById('ecScheduleId').value = item.scheduleId || '';
    document.getElementById('ecName').value = item.className || '';
    document.getElementById('ecDescription').value = item.classDescription || '';
    document.getElementById('ecCapacity').value = item.capacity != null ? item.capacity : '';
    document.getElementById('ecDate').value = toDateInputValue(item.date);
    document.getElementById('ecStart').value = formatTime(item.startTime);
    document.getElementById('ecEnd').value = formatTime(item.endTime);
    populateTrainerSelects();
    var trainerEl = document.getElementById('ecTrainer');
    if (trainerEl && item.trainerId != null) trainerEl.value = String(item.trainerId);
    new bootstrap.Modal(editClassModalEl).show();
  }

  function openDeleteModal(item) {
    hideError(deleteScheduleAlert);
    document.getElementById('deleteScheduleId').value = item.scheduleId || '';
    document.getElementById('deleteScheduleClassName').textContent = item.className || 'this class';
    new bootstrap.Modal(deleteScheduleModalEl).show();
  }

  function submitScheduleClass() {
    hideError(scheduleClassAlert);
    var name = document.getElementById('scName').value.trim();
    var description = document.getElementById('scDescription').value.trim();
    var capacity = parseInt(document.getElementById('scCapacity').value, 10);
    var trainerId = document.getElementById('scTrainer').value;
    var date = document.getElementById('scDate').value;
    var start = document.getElementById('scStart').value;
    var end = document.getElementById('scEnd').value;
    if (!name || !capacity || !trainerId || !date || !start || !end) {
      showError(scheduleClassAlert, 'All fields are required.');
      return;
    }

    scheduleClassSubmitBtn.disabled = true;
    request('/admin/classes', 'POST', {
      name: name,
      description: description || null,
      capacity: capacity,
      trainerId: Number(trainerId)
    }).then(function (createdClass) {
      return request('/admin/classes/' + createdClass.id + '/schedule', 'POST', {
        date: date,
        startTime: start,
        endTime: end
      });
    }).then(function () {
      var modal = bootstrap.Modal.getInstance(scheduleClassModalEl);
      if (modal) modal.hide();
      showToast('Class created and scheduled successfully.');
      fetchTrainers().then(fetchSchedules);
    }).catch(function (err) {
      showError(scheduleClassAlert, err.message || 'Failed to schedule class.');
    }).finally(function () {
      scheduleClassSubmitBtn.disabled = false;
    });
  }

  function submitEditClass() {
    hideError(editClassAlert);
    var classId = document.getElementById('ecClassId').value;
    var scheduleId = document.getElementById('ecScheduleId').value;
    var name = document.getElementById('ecName').value.trim();
    var description = document.getElementById('ecDescription').value.trim();
    var capacity = parseInt(document.getElementById('ecCapacity').value, 10);
    var trainerId = document.getElementById('ecTrainer').value;
    var date = document.getElementById('ecDate').value;
    var start = document.getElementById('ecStart').value;
    var end = document.getElementById('ecEnd').value;

    if (!classId || !scheduleId || !name || !capacity || !trainerId || !date || !start || !end) {
      showError(editClassAlert, 'Please fill all required fields.');
      return;
    }

    editClassSubmitBtn.disabled = true;
    request('/admin/classes/' + classId, 'PUT', {
      name: name,
      description: description || null,
      capacity: capacity,
      trainerId: Number(trainerId)
    }).then(function () {
      return request('/admin/classes/schedule/' + scheduleId, 'PUT', {
        date: date,
        startTime: start,
        endTime: end
      });
    }).then(function () {
      var modal = bootstrap.Modal.getInstance(editClassModalEl);
      if (modal) modal.hide();
      showToast('Class updated successfully.');
      fetchSchedules();
    }).catch(function (err) {
      showError(editClassAlert, err.message || 'Failed to update class.');
    }).finally(function () {
      editClassSubmitBtn.disabled = false;
    });
  }

  function submitDeleteSchedule() {
    hideError(deleteScheduleAlert);
    var scheduleId = document.getElementById('deleteScheduleId').value;
    if (!scheduleId) return;
    deleteScheduleConfirmBtn.disabled = true;
    request('/admin/classes/schedule/' + scheduleId, 'DELETE').then(function () {
      var modal = bootstrap.Modal.getInstance(deleteScheduleModalEl);
      if (modal) modal.hide();
      showToast('Schedule deleted.');
      fetchSchedules();
    }).catch(function (err) {
      showError(deleteScheduleAlert, err.message || 'Failed to delete schedule.');
    }).finally(function () {
      deleteScheduleConfirmBtn.disabled = false;
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      localStorage.removeItem('token');
      window.location.href = '/login';
    });
  }

  if (scheduleClassBtn && scheduleClassModalEl) {
    scheduleClassBtn.addEventListener('click', function () {
      hideError(scheduleClassAlert);
      document.getElementById('scheduleClassForm').reset();
      populateTrainerSelects();
      new bootstrap.Modal(scheduleClassModalEl).show();
    });
  }
  if (scheduleClassSubmitBtn) scheduleClassSubmitBtn.addEventListener('click', submitScheduleClass);
  if (editClassSubmitBtn) editClassSubmitBtn.addEventListener('click', submitEditClass);
  if (deleteScheduleConfirmBtn) deleteScheduleConfirmBtn.addEventListener('click', submitDeleteSchedule);

  fetchTrainers().then(fetchSchedules);
})();
