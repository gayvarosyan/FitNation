(function () {
  'use strict';

  var API_BASE = '/api';
  var TOKEN_KEY = 'token';

  var createPlanModalEl = document.getElementById('createPlanModal');
  var createPlanModal = createPlanModalEl ? new bootstrap.Modal(createPlanModalEl) : null;
  var editMembershipModalEl = document.getElementById('editMembershipModal');
  var editMembershipModal = editMembershipModalEl ? new bootstrap.Modal(editMembershipModalEl) : null;
  var cancelMembershipModalEl = document.getElementById('cancelMembershipModal');
  var cancelMembershipModal = cancelMembershipModalEl ? new bootstrap.Modal(cancelMembershipModalEl) : null;
  var deletePlanModalEl = document.getElementById('deletePlanModal');
  var deletePlanModal = deletePlanModalEl ? new bootstrap.Modal(deletePlanModalEl) : null;

  var createPlanBtn = document.getElementById('createPlanBtn');
  var createPlanSubmitBtn = document.getElementById('createPlanSubmitBtn');
  var editMembershipSubmitBtn = document.getElementById('editMembershipSubmitBtn');
  var cancelMembershipConfirmBtn = document.getElementById('cancelMembershipConfirmBtn');
  var deletePlanConfirmBtn = document.getElementById('deletePlanConfirmBtn');
  var logoutBtn = document.getElementById('logoutBtn');
  var tableBody = document.getElementById('subsTableBody');
  var tableLoading = document.getElementById('tableLoading');
  var tableError = document.getElementById('tableError');
  var pageError = document.getElementById('pageError');
  var dataScopeNotice = document.getElementById('dataScopeNotice');
  var kpiGrid = document.getElementById('kpiGrid');
  var toastContainer = document.getElementById('toastContainer');
  var createPlanAlert = document.getElementById('createPlanAlert');
  var editMembershipAlert = document.getElementById('editMembershipAlert');
  var cancelMembershipAlert = document.getElementById('cancelMembershipAlert');
  var deletePlanAlert = document.getElementById('deletePlanAlert');

  var state = {
    membershipTypes: [],
    records: [],
    stats: null,
    nutritionPlans: [],
    trainers: [],
    groupClasses: []
  };

  function getToken() {
    return localStorage.getItem(TOKEN_KEY);
  }

  function redirectToLogin() {
    localStorage.removeItem(TOKEN_KEY);
    window.location.href = '/login';
  }

  function headers(contentType) {
    var token = getToken();
    var result = { Accept: 'application/json' };
    if (contentType) result['Content-Type'] = contentType;
    if (token) result.Authorization = 'Bearer ' + token;
    return result;
  }

  function parseErrorResponse(res) {
    if (res.status === 401) {
      redirectToLogin();
      return Promise.reject(new Error('Unauthorized'));
    }
    return res.json().catch(function () {
      return {};
    }).then(function (body) {
      var msg = body && (body.message || body.error);
      throw new Error(msg || 'Request failed');
    });
  }

  function getJson(path) {
    return fetch(API_BASE + path, { method: 'GET', headers: headers() })
      .then(function (res) {
        if (!res.ok) return parseErrorResponse(res);
        return res.json();
      });
  }

  function postJson(path, body) {
    return fetch(API_BASE + path, {
      method: 'POST',
      headers: headers('application/json'),
      body: JSON.stringify(body)
    }).then(function (res) {
      if (!res.ok) return parseErrorResponse(res);
      return res.json();
    });
  }

  function putJson(path, body) {
    var h = body != null ? headers('application/json') : headers();
    return fetch(API_BASE + path, {
      method: 'PUT',
      headers: h,
      body: body != null ? JSON.stringify(body) : undefined
    }).then(function (res) {
      if (!res.ok) return parseErrorResponse(res);
      return res.json().catch(function () { return {}; });
    });
  }

  function deleteJson(path) {
    return fetch(API_BASE + path, { method: 'DELETE', headers: headers() }).then(function (res) {
      if (!res.ok) return parseErrorResponse(res);
    });
  }

  function showToast(message, isError) {
    if (!toastContainer) return;
    var toast = document.createElement('div');
    toast.className = 'fn-toast';
    if (isError) {
      toast.style.borderColor = '#ef4444';
      toast.style.color = '#fecaca';
    }
    toast.textContent = message;
    toastContainer.appendChild(toast);
    setTimeout(function () {
      if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 4000);
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

  function setFieldError(id, message) {
    var el = document.getElementById(id);
    if (el) el.textContent = message;
  }

  function clearCreateForm() {
    var cpPlanId = document.getElementById('cpPlanId');
    if (cpPlanId) cpPlanId.value = '';
    ['cpName', 'cpDuration', 'cpPrice', 'cpDescription'].forEach(function (id) {
      var el = document.getElementById(id);
      if (el) el.value = '';
    });
    ['cpNutritionSelect', 'cpTrainerSelect', 'cpGroupClassSelect'].forEach(function (id) {
      var el = document.getElementById(id);
      if (el) el.value = '';
    });
    setFieldError('cpNameError', '');
    setFieldError('cpDurationError', '');
    setFieldError('cpPriceError', '');
    hideError(createPlanAlert);
  }

  function resetPlanModalHeader() {
    var lbl = document.getElementById('createPlanModalLabel');
    if (lbl) lbl.textContent = 'Create Membership Plan';
    if (createPlanSubmitBtn) createPlanSubmitBtn.textContent = 'Create Plan';
  }

  function openCreatePlanModal() {
    clearCreateForm();
    resetPlanModalHeader();
    if (createPlanModal) createPlanModal.show();
  }

  function openEditPlanModal(t) {
    hideError(createPlanAlert);
    setFieldError('cpNameError', '');
    setFieldError('cpDurationError', '');
    setFieldError('cpPriceError', '');
    var cpPlanId = document.getElementById('cpPlanId');
    if (cpPlanId) cpPlanId.value = String(t.id);
    document.getElementById('cpName').value = t.name || '';
    var dur = planDurationDays(t);
    document.getElementById('cpDuration').value =
      dur != null && !isNaN(dur) ? String(dur) : '';
    document.getElementById('cpPrice').value = t.price != null ? String(t.price) : '';
    document.getElementById('cpDescription').value = t.description || '';
    fillAllBundleSelects();
    var nu = document.getElementById('cpNutritionSelect');
    var tr = document.getElementById('cpTrainerSelect');
    var gc = document.getElementById('cpGroupClassSelect');
    if (nu) nu.value = t.nutritionPlanId != null ? String(t.nutritionPlanId) : '';
    if (tr) tr.value = t.trainerId != null ? String(t.trainerId) : '';
    if (gc) gc.value = t.groupClassId != null ? String(t.groupClassId) : '';
    var lbl = document.getElementById('createPlanModalLabel');
    if (lbl) lbl.textContent = 'Edit membership plan';
    if (createPlanSubmitBtn) createPlanSubmitBtn.textContent = 'Save changes';
    if (createPlanModal) createPlanModal.show();
  }

  function openDeletePlanModal(t) {
    hideError(deletePlanAlert);
    document.getElementById('deletePlanId').value = String(t.id);
    var name = (t.name || 'this plan').trim();
    document.getElementById('deletePlanText').textContent =
      'Delete "' + name + '" from the catalog? Plans still linked to subscriptions cannot be removed.';
    if (deletePlanModal) deletePlanModal.show();
  }

  function confirmDeletePlan() {
    var id = document.getElementById('deletePlanId').value;
    var btn = deletePlanConfirmBtn;
    hideError(deletePlanAlert);
    if (!id) return;
    if (btn) btn.disabled = true;
    deleteJson('/admin/membership-types/' + encodeURIComponent(id))
      .then(function () {
        if (deletePlanModal) deletePlanModal.hide();
        showToast('Plan deleted.');
        return refreshData();
      })
      .catch(function (err) {
        showError(deletePlanAlert, err.message || 'Failed to delete plan.');
      })
      .finally(function () {
        if (btn) btn.disabled = false;
      });
  }

  function formatCurrency(value) {
    if (value == null || isNaN(value)) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(value);
  }

  function formatDate(dateValue) {
    if (!dateValue) return '-';
    if (typeof dateValue === 'string' && /^\d{4}-\d{2}-\d{2}/.test(dateValue)) {
      return dateValue.slice(0, 10);
    }
    var dt = new Date(dateValue);
    if (isNaN(dt.getTime())) return '-';
    return dt.toISOString().slice(0, 10);
  }

  function deriveCycle(durationDays) {
    if (!durationDays || isNaN(durationDays)) return 'Unknown cycle';
    if (durationDays >= 360) return 'Yearly';
    if (durationDays >= 28) return 'Monthly';
    if (durationDays >= 7) return 'Weekly';
    return durationDays + ' days';
  }

  function planDurationDays(t) {
    if (!t) return null;
    if (t.duration != null) return Number(t.duration);
    if (t.durationDays != null) return Number(t.durationDays);
    return null;
  }

  function renderPlansCatalog() {
    var tbody = document.getElementById('plansCatalogBody');
    if (!tbody) return;
    tbody.innerHTML = '';
    if (!state.membershipTypes.length) {
      var emptyRow = document.createElement('tr');
      emptyRow.innerHTML =
        '<td colspan="7"><div class="fn-sub-empty" style="padding:1rem;"><p class="fn-sub-empty-text" style="margin:0;">No plans yet. Use <strong>Create Plan</strong>.</p></div></td>';
      tbody.appendChild(emptyRow);
      return;
    }
    state.membershipTypes.forEach(function (t) {
      var tr = document.createElement('tr');
      var dur = planDurationDays(t);
      tr.innerHTML =
        '<td><span class="fn-sub-plan-name">' + escapeHtml(t.name || '') + '</span> <span class="fn-sub-id">#' + escapeHtml(String(t.id)) + '</span></td>' +
        '<td>' + escapeHtml(deriveCycle(dur)) + '</td>' +
        '<td><span class="fn-sub-amount">' + escapeHtml(formatCurrency(t.price != null ? Number(t.price) : NaN)) + '</span></td>' +
        '<td>' + bundleCell('nutrition', t.nutritionPlanId) + '</td>' +
        '<td>' + bundleCell('trainer', t.trainerId) + '</td>' +
        '<td>' + bundleCell('class', t.groupClassId) + '</td>';

      var actionsCell = document.createElement('td');
      actionsCell.className = 'fn-sub-actions-cell fn-admin-actions-cell';
      var editBtn = document.createElement('button');
      editBtn.type = 'button';
      editBtn.className = 'fn-admin-action-btn fn-admin-action-edit';
      editBtn.textContent = 'Edit';
      editBtn.addEventListener('click', function () {
        openEditPlanModal(t);
      });
      var delBtn = document.createElement('button');
      delBtn.type = 'button';
      delBtn.className = 'fn-admin-action-btn fn-admin-action-delete';
      delBtn.textContent = 'Delete';
      delBtn.addEventListener('click', function () {
        openDeletePlanModal(t);
      });
      actionsCell.appendChild(editBtn);
      actionsCell.appendChild(delBtn);
      tr.appendChild(actionsCell);

      tbody.appendChild(tr);
    });
  }

  function normalizeStatus(status) {
    var value = String(status || '').toUpperCase();
    if (value === 'ACTIVE') return { label: 'Active', cls: 'fn-sub-status-active' };
    if (value === 'EXPIRED') return { label: 'Expired', cls: 'fn-sub-status-expired' };
    if (value === 'CANCELLED') return { label: 'Cancelled', cls: 'fn-sub-status-cancelled' };
    if (value === 'PAST_DUE') return { label: 'Past due', cls: 'fn-sub-status-pastdue' };
    if (value === 'FROZEN' || value === 'BLOCKED') return { label: value.charAt(0) + value.slice(1).toLowerCase(), cls: 'fn-sub-status-expired' };
    return { label: value || 'Unknown', cls: 'fn-sub-status-expired' };
  }

  function nutritionLabel(id) {
    if (id == null) return '—';
    var p = state.nutritionPlans.find(function (x) { return Number(x.id) === Number(id); });
    return p && p.planName ? p.planName : '#' + id;
  }

  function trainerLabel(id) {
    if (id == null) return '—';
    var sid = String(id);
    var t = state.trainers.find(function (x) { return String(x.trainerId) === sid; });
    if (!t) return '#' + id;
    var name = [t.firstName || '', t.lastName || ''].join(' ').trim();
    return name || t.email || '#' + id;
  }

  function groupClassLabel(id) {
    if (id == null) return '—';
    var g = state.groupClasses.find(function (x) { return Number(x.id) === Number(id); });
    return g && g.name ? g.name : '#' + id;
  }

  function bundleCell(kind, id) {
    var label = '—';
    var tip = '';
    if (kind === 'nutrition') {
      label = nutritionLabel(id);
      tip = id != null ? 'Nutrition plan ID: ' + id : '';
    } else if (kind === 'trainer') {
      label = trainerLabel(id);
      tip = id != null ? 'Trainer user ID: ' + id : '';
    } else {
      label = groupClassLabel(id);
      tip = id != null ? 'Group class ID: ' + id : '';
    }
    if (label === '—') return '—';
    return '<span class="fn-sub-bundle-name" title="' + escapeHtml(tip) + '">' + escapeHtml(label) + '</span>';
  }

  function fillBundleSelect(selectId, items, getValue, getLabel) {
    var sel = document.getElementById(selectId);
    if (!sel) return;
    var prev = sel.value;
    sel.innerHTML = '';
    var o0 = document.createElement('option');
    o0.value = '';
    o0.textContent = 'None';
    sel.appendChild(o0);
    (items || []).forEach(function (it) {
      var o = document.createElement('option');
      o.value = String(getValue(it));
      o.textContent = getLabel(it);
      sel.appendChild(o);
    });
    if (prev !== '' && Array.from(sel.options).some(function (opt) { return opt.value === prev; })) {
      sel.value = prev;
    }
  }

  function fillAllBundleSelects() {
    fillBundleSelect('cpNutritionSelect', state.nutritionPlans, function (p) { return p.id; }, function (p) {
      return (p.planName || 'Plan') + ' (#' + p.id + ')';
    });
    fillBundleSelect('umNutritionSelect', state.nutritionPlans, function (p) { return p.id; }, function (p) {
      return (p.planName || 'Plan') + ' (#' + p.id + ')';
    });
    fillBundleSelect('cpTrainerSelect', state.trainers, function (t) { return t.trainerId; }, function (t) {
      var name = [t.firstName || '', t.lastName || ''].join(' ').trim() || t.email || 'Trainer';
      return name + ' (#' + t.trainerId + ')';
    });
    fillBundleSelect('umTrainerSelect', state.trainers, function (t) { return t.trainerId; }, function (t) {
      var name = [t.firstName || '', t.lastName || ''].join(' ').trim() || t.email || 'Trainer';
      return name + ' (#' + t.trainerId + ')';
    });
    fillBundleSelect('cpGroupClassSelect', state.groupClasses, function (g) { return g.id; }, function (g) {
      return (g.name || 'Class') + ' (#' + g.id + ')';
    });
    fillBundleSelect('umGroupClassSelect', state.groupClasses, function (g) { return g.id; }, function (g) {
      return (g.name || 'Class') + ' (#' + g.id + ')';
    });
  }

  function mapAdminRecord(raw) {
    var userName = [raw.userFirstName || '', raw.userLastName || ''].join(' ').trim() || raw.userEmail || '—';
    var price = raw.membershipTypePrice != null ? Number(raw.membershipTypePrice) : null;
    return {
      id: raw.id,
      raw: raw,
      subId: 'SUB-' + raw.id,
      userName: userName,
      planName: raw.membershipTypeName || '—',
      planCycle: deriveCycle(raw.membershipTypeDurationDays),
      amountValue: price,
      amountLabel: formatCurrency(price),
      nextBillingLabel: formatDate(raw.endDate),
      status: raw.status,
      nutritionPlanId: raw.nutritionPlanId,
      trainerId: raw.trainerId,
      groupClassId: raw.groupClassId
    };
  }

  function recordToRow(record) {
    var tr = document.createElement('tr');
    var statusMap = normalizeStatus(record.status);
    var canCancel = String(record.status || '').toUpperCase() === 'ACTIVE';

    tr.innerHTML =
      '<td><span class="fn-sub-id">' + escapeHtml(record.subId) + '</span></td>' +
      '<td>' + escapeHtml(record.userName) + '</td>' +
      '<td><div class="fn-sub-plan-name">' + escapeHtml(record.planName) + '</div><div class="fn-sub-plan-cycle">' + escapeHtml(record.planCycle) + '</div></td>' +
      '<td><span class="fn-sub-amount">' + escapeHtml(record.amountLabel) + '</span></td>' +
      '<td>' + escapeHtml(record.nextBillingLabel) + '</td>' +
      '<td><span class="fn-sub-status ' + statusMap.cls + '"><span class="fn-sub-status-dot"></span>' + statusMap.label + '</span></td>' +
      '<td>' + bundleCell('nutrition', record.nutritionPlanId) + '</td>' +
      '<td>' + bundleCell('trainer', record.trainerId) + '</td>' +
      '<td>' + bundleCell('class', record.groupClassId) + '</td>' +
      '<td class="fn-sub-actions-cell"><div class="fn-sub-action-wrap"><button class="fn-sub-menu-btn" type="button" aria-label="Membership actions">⋮</button></div></td>';

    var menuBtn = tr.querySelector('.fn-sub-menu-btn');
    var wrap = tr.querySelector('.fn-sub-action-wrap');
    if (menuBtn && wrap) {
      menuBtn.addEventListener('click', function (event) {
        event.stopPropagation();
        closeAllMenus();
        wrap.appendChild(buildMenu(record, canCancel));
      });
    }
    return tr;
  }

  function buildMenu(record, canCancel) {
    var menu = document.createElement('div');
    menu.className = 'fn-sub-dropdown';

    var editBtn = document.createElement('button');
    editBtn.type = 'button';
    editBtn.className = 'fn-sub-dropdown-item';
    editBtn.textContent = 'Edit';
    editBtn.addEventListener('click', function () {
      closeAllMenus();
      openEditModal(record);
    });

    var cancelBtn = document.createElement('button');
    cancelBtn.type = 'button';
    cancelBtn.className = 'fn-sub-dropdown-item fn-sub-dropdown-danger';
    cancelBtn.textContent = 'Cancel subscription';
    cancelBtn.disabled = !canCancel;
    if (!canCancel) {
      cancelBtn.classList.add('fn-sub-dropdown-disabled');
      cancelBtn.textContent = 'Cancel unavailable';
    }
    cancelBtn.addEventListener('click', function () {
      closeAllMenus();
      if (!canCancel) return;
      openCancelModal(record);
    });

    menu.appendChild(editBtn);
    menu.appendChild(cancelBtn);
    return menu;
  }

  function openCancelModal(record) {
    hideError(cancelMembershipAlert);
    document.getElementById('cancelMembershipId').value = String(record.id);
    document.getElementById('cancelMembershipText').textContent =
      'End subscription ' + record.subId + ' for ' + record.userName + '? It will be marked expired.';
    if (cancelMembershipModal) cancelMembershipModal.show();
  }

  function populateMembershipTypeSelect(selectEl, selectedId) {
    if (!selectEl) return;
    selectEl.innerHTML = '';
    state.membershipTypes.forEach(function (t) {
      var opt = document.createElement('option');
      opt.value = String(t.id);
      var label = (t.name || '') + ' (' + formatCurrency(t.price != null ? Number(t.price) : 0) + ')';
      opt.textContent = label;
      selectEl.appendChild(opt);
    });
    if (selectedId != null) {
      selectEl.value = String(selectedId);
    }
  }

  function openEditModal(record) {
    hideError(editMembershipAlert);
    var raw = record.raw;
    document.getElementById('umMembershipId').value = String(record.id);
    populateMembershipTypeSelect(document.getElementById('umMembershipTypeId'), raw.membershipTypeId);
    document.getElementById('umStartDate').value = formatDate(raw.startDate);
    document.getElementById('umEndDate').value = formatDate(raw.endDate);
    document.getElementById('umStatus').value = String(raw.status || 'ACTIVE');
    var nu = document.getElementById('umNutritionSelect');
    var tr = document.getElementById('umTrainerSelect');
    var gc = document.getElementById('umGroupClassSelect');
    if (nu) nu.value = raw.nutritionPlanId != null ? String(raw.nutritionPlanId) : '';
    if (tr) tr.value = raw.trainerId != null ? String(raw.trainerId) : '';
    if (gc) gc.value = raw.groupClassId != null ? String(raw.groupClassId) : '';
    if (editMembershipModal) editMembershipModal.show();
  }

  function parseOptionalSelect(selectId) {
    var el = document.getElementById(selectId);
    if (!el || el.value === '') return null;
    var n = parseInt(String(el.value).trim(), 10);
    return isNaN(n) ? null : n;
  }

  function submitEditMembership() {
    hideError(editMembershipAlert);
    var id = document.getElementById('umMembershipId').value;
    var membershipTypeId = parseInt(document.getElementById('umMembershipTypeId').value, 10);
    var startDate = document.getElementById('umStartDate').value;
    var endDate = document.getElementById('umEndDate').value;
    var status = document.getElementById('umStatus').value;
    if (!startDate || !endDate) {
      showError(editMembershipAlert, 'Start and end dates are required.');
      return;
    }
    if (editMembershipSubmitBtn) editMembershipSubmitBtn.disabled = true;
    var payload = {
      membershipTypeId: membershipTypeId,
      startDate: startDate,
      endDate: endDate,
      status: status,
      nutritionPlanId: parseOptionalSelect('umNutritionSelect'),
      trainerId: parseOptionalSelect('umTrainerSelect'),
      groupClassId: parseOptionalSelect('umGroupClassSelect')
    };
    putJson('/memberships/' + id, payload)
      .then(function () {
        if (editMembershipModal) editMembershipModal.hide();
        showToast('Membership updated.');
        return refreshData();
      })
      .catch(function (err) {
        showError(editMembershipAlert, err.message || 'Failed to update membership.');
      })
      .finally(function () {
        if (editMembershipSubmitBtn) editMembershipSubmitBtn.disabled = false;
      });
  }

  function closeAllMenus() {
    document.querySelectorAll('.fn-sub-dropdown').forEach(function (el) {
      if (el && el.parentNode) el.parentNode.removeChild(el);
    });
  }

  function renderEmptyState(message, subtext) {
    if (!tableBody) return;
    tableBody.innerHTML = '';
    var row = document.createElement('tr');
    var subHtml = subtext
      ? '<p class="fn-sub-empty-text fn-sub-empty-sub">' + escapeHtml(subtext) + '</p>'
      : '';
    row.innerHTML =
      '<td colspan="10"><div class="fn-sub-empty"><div class="fn-sub-empty-icon">📋</div><p class="fn-sub-empty-title">No subscriptions found</p><p class="fn-sub-empty-text">' +
      escapeHtml(message || 'No subscription records are available.') + '</p>' + subHtml + '</div></td>';
    tableBody.appendChild(row);
  }

  function renderTable() {
    if (!tableBody) return;
    tableBody.innerHTML = '';

    var total = state.records.length;
    if (!total) {
      var sub = state.membershipTypes.length
        ? 'Your catalog (Create Plan) can include nutrition, trainer, and class defaults — that is not the same as rows here. This table only shows purchased memberships. You have ' + state.membershipTypes.length + ' plan(s); rows appear after a member has a subscription record.'
        : 'Create a membership plan first. Subscription rows appear when members have an active membership in the system.';
      renderEmptyState('No subscription records yet.', sub);
      return;
    }

    state.records.forEach(function (record) {
      tableBody.appendChild(recordToRow(record));
    });
  }

  function renderKpisFromStats(stats) {
    if (!kpiGrid || !stats) return;
    kpiGrid.innerHTML = '';
    var mrr = stats.monthlyRecurringRevenue != null ? Number(stats.monthlyRecurringRevenue) : 0;
    var kpiChurn = stats.churnRate != null ? Number(stats.churnRate) : 0;
    kpiGrid.appendChild(kpiCard('💳', formatCurrency(mrr), 'Monthly Recurring Revenue', '—', true));
    kpiGrid.appendChild(kpiCard('📈', String(stats.activeSubscriptions != null ? stats.activeSubscriptions : 0), 'Active Subscriptions', '—', true));
    kpiGrid.appendChild(kpiCard('📉', kpiChurn.toFixed(1) + '%', 'Churn Rate', '—', false));
    var pastDue = stats.pastDueAccounts != null ? stats.pastDueAccounts : 0;
    kpiGrid.appendChild(kpiCard('⏰', String(pastDue), 'Past Due Accounts', '—', false, true));
  }

  function renderKpisFromRecords() {
    if (!kpiGrid) return;
    var activeCount = state.records.filter(function (r) { return String(r.status).toUpperCase() === 'ACTIVE'; }).length;
    var cancelledCount = state.records.filter(function (r) {
      var s = String(r.status).toUpperCase();
      return s === 'CANCELLED' || s === 'EXPIRED';
    }).length;
    var churn = state.records.length ? ((cancelledCount / state.records.length) * 100) : 0;
    var mrr = state.records.reduce(function (acc, rec) {
      return acc + (rec.amountValue || 0);
    }, 0);

    kpiGrid.innerHTML = '';
    kpiGrid.appendChild(kpiCard('💳', formatCurrency(mrr), 'Monthly Recurring Revenue', '—', true));
    kpiGrid.appendChild(kpiCard('📈', String(activeCount), 'Active Subscriptions', '—', true));
    kpiGrid.appendChild(kpiCard('📉', churn.toFixed(1) + '%', 'Churn Rate', '—', false));
    kpiGrid.appendChild(kpiCard('⏰', '—', 'Past Due Accounts', '—', false, true));
  }

  function kpiCard(icon, value, label, trendText, trendPositive, dangerIcon) {
    var card = document.createElement('div');
    card.className = 'fn-admin-stat-card';
    card.innerHTML =
      '<div class="fn-admin-stat-icon ' + (dangerIcon ? 'fn-sub-stat-icon-danger' : '') + '">' + icon + '</div>' +
      '<div class="fn-sub-stat-body">' +
      '<div class="fn-sub-stat-top"><span class="fn-sub-kpi-badge ' + (trendPositive ? 'fn-sub-kpi-up' : 'fn-sub-kpi-down') + '">' + escapeHtml(trendText) + '</span></div>' +
      '<div class="fn-admin-stat-value">' + escapeHtml(value) + '</div>' +
      '<div class="fn-admin-stat-meta">' + label + '</div>' +
      '</div>';
    return card;
  }

  function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text == null ? '' : String(text);
    return div.innerHTML;
  }

  function validateCreateForm() {
    var name = (document.getElementById('cpName').value || '').trim();
    var duration = parseInt((document.getElementById('cpDuration').value || '').trim(), 10);
    var price = parseFloat((document.getElementById('cpPrice').value || '').trim());
    var isValid = true;

    if (!name) {
      setFieldError('cpNameError', 'Name is required.');
      isValid = false;
    } else {
      setFieldError('cpNameError', '');
    }
    if (!duration || duration < 1) {
      setFieldError('cpDurationError', 'Duration must be at least 1 day.');
      isValid = false;
    } else {
      setFieldError('cpDurationError', '');
    }
    if (isNaN(price) || price <= 0) {
      setFieldError('cpPriceError', 'Price must be greater than 0.');
      isValid = false;
    } else {
      setFieldError('cpPriceError', '');
    }
    return isValid;
  }

  function createPlan() {
    hideError(createPlanAlert);
    if (!validateCreateForm()) return;

    var payload = {
      name: document.getElementById('cpName').value.trim(),
      durationDays: parseInt(document.getElementById('cpDuration').value, 10),
      price: parseFloat(document.getElementById('cpPrice').value),
      description: (document.getElementById('cpDescription').value || '').trim() || null,
      nutritionPlanId: parseOptionalSelect('cpNutritionSelect'),
      trainerId: parseOptionalSelect('cpTrainerSelect'),
      groupClassId: parseOptionalSelect('cpGroupClassSelect')
    };

    var planId = (document.getElementById('cpPlanId').value || '').trim();
    var isEdit = planId !== '';

    createPlanSubmitBtn.disabled = true;
    var req = isEdit
      ? putJson('/admin/membership-types/' + encodeURIComponent(planId), payload)
      : postJson('/admin/membership-types', payload);

    req
      .then(function () {
        if (createPlanModal) createPlanModal.hide();
        showToast(isEdit ? 'Plan updated.' : 'Membership plan created.');
        clearCreateForm();
        resetPlanModalHeader();
        return refreshData();
      })
      .catch(function (err) {
        showError(
          createPlanAlert,
          err.message || (isEdit ? 'Failed to update plan.' : 'Failed to create membership plan.')
        );
      })
      .finally(function () {
        createPlanSubmitBtn.disabled = false;
      });
  }

  function confirmCancelMembership() {
    var id = document.getElementById('cancelMembershipId').value;
    var btn = cancelMembershipConfirmBtn;
    hideError(cancelMembershipAlert);
    if (!id) return;
    if (btn) btn.disabled = true;
    putJson('/memberships/' + id + '/cancel', null)
      .then(function () {
        if (cancelMembershipModal) cancelMembershipModal.hide();
        showToast('Subscription cancelled.');
        return refreshData();
      })
      .catch(function (err) {
        showError(cancelMembershipAlert, err.message || 'Failed to cancel.');
      })
      .finally(function () {
        if (btn) btn.disabled = false;
      });
  }

  function refreshData() {
    hideError(pageError);
    hideError(tableError);
    if (tableLoading) tableLoading.style.display = 'flex';

    return Promise.allSettled([
      getJson('/membership-types'),
      getJson('/admin/memberships'),
      getJson('/admin/memberships/stats'),
      getJson('/nutrition/plans'),
      getJson('/trainers'),
      getJson('/admin/group-classes')
    ]).then(function (results) {
      if (tableLoading) tableLoading.style.display = 'none';

      var typesResult = results[0];
      var adminListResult = results[1];
      var statsResult = results[2];
      var nutritionResult = results[3];
      var trainersResult = results[4];
      var groupClassesResult = results[5];

      if (typesResult.status === 'fulfilled') {
        state.membershipTypes = Array.isArray(typesResult.value) ? typesResult.value : [];
      } else {
        state.membershipTypes = [];
      }

      state.nutritionPlans = nutritionResult.status === 'fulfilled' && Array.isArray(nutritionResult.value)
        ? nutritionResult.value
        : [];
      state.trainers = trainersResult.status === 'fulfilled' && Array.isArray(trainersResult.value)
        ? trainersResult.value
        : [];
      state.groupClasses = groupClassesResult.status === 'fulfilled' && Array.isArray(groupClassesResult.value)
        ? groupClassesResult.value
        : [];

      fillAllBundleSelects();
      renderPlansCatalog();

      if (adminListResult.status === 'fulfilled') {
        var rawList = Array.isArray(adminListResult.value) ? adminListResult.value : [];
        state.records = rawList.map(mapAdminRecord);
        renderTable();
      } else {
        state.records = [];
        var msg = adminListResult.reason && adminListResult.reason.message
          ? adminListResult.reason.message
          : 'Failed to load memberships.';
        showError(
          tableError,
          msg + ' Log in with an Admin or Super Admin account. If you already use Super Admin, redeploy the API: /api/admin/** must allow SUPER_ADMIN.'
        );
        renderEmptyState('Subscription list unavailable (access denied).');
      }

      if (statsResult.status === 'fulfilled' && statsResult.value) {
        state.stats = statsResult.value;
        renderKpisFromStats(state.stats);
      } else {
        state.stats = null;
        renderKpisFromRecords();
      }

      if (dataScopeNotice) {
        dataScopeNotice.textContent =
          'Plans (catalog section) come from GET /api/membership-types. Subscription rows come from GET /api/admin/memberships (actual purchases). Both need Admin or Super Admin for full access; bundle names need nutrition/trainers/classes loaded.';
      }
    }).catch(function (err) {
      if (tableLoading) tableLoading.style.display = 'none';
      showError(pageError, err.message || 'Failed to load subscriptions screen.');
      renderEmptyState('Unable to load subscription records.');
    });
  }

  function bindEvents() {
    if (logoutBtn) {
      logoutBtn.addEventListener('click', function () {
        localStorage.removeItem(TOKEN_KEY);
        window.location.href = '/login';
      });
    }
    if (createPlanBtn) {
      createPlanBtn.addEventListener('click', function () {
        openCreatePlanModal();
      });
    }
    if (createPlanSubmitBtn) {
      createPlanSubmitBtn.addEventListener('click', createPlan);
    }
    if (editMembershipSubmitBtn) {
      editMembershipSubmitBtn.addEventListener('click', submitEditMembership);
    }
    if (cancelMembershipConfirmBtn) {
      cancelMembershipConfirmBtn.addEventListener('click', confirmCancelMembership);
    }
    if (deletePlanConfirmBtn) {
      deletePlanConfirmBtn.addEventListener('click', confirmDeletePlan);
    }
    document.addEventListener('click', function () {
      closeAllMenus();
    });
  }

  function init() {
    var token = getToken();
    if (!token) {
      redirectToLogin();
      return;
    }
    bindEvents();
    refreshData();
  }

  init();
})();
