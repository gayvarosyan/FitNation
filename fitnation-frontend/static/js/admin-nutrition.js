(function () {
  'use strict';

  const API_BASE = '/api';

  const nutritionStats   = document.getElementById('nutritionStats');
  const statsError       = document.getElementById('statsError');
  const statsLoading     = document.getElementById('statsLoading');
  const plansError       = document.getElementById('plansError');
  const plansLoading     = document.getElementById('plansLoading');
  const tableWrapper     = document.getElementById('tableWrapper');
  const plansTableBody   = document.getElementById('plansTableBody');
  const createPlanBtn    = document.getElementById('createPlanBtn');
  const createPlanModalEl = document.getElementById('createPlanModal');
  const createPlanForm   = document.getElementById('createPlanForm');
  const createPlanSubmitBtn = document.getElementById('createPlanSubmitBtn');
  const createPlanAlert  = document.getElementById('createPlanAlert');
  const editPlanModalEl  = document.getElementById('editPlanModal');
  const editPlanForm     = document.getElementById('editPlanForm');
  const editPlanSubmitBtn = document.getElementById('editPlanSubmitBtn');
  const editPlanAlert    = document.getElementById('editPlanAlert');
  const deletePlanModalEl = document.getElementById('deletePlanModal');
  const deletePlanConfirmBtn = document.getElementById('deletePlanConfirmBtn');
  const deletePlanAlert  = document.getElementById('deletePlanAlert');
  const logoutBtn        = document.getElementById('logoutBtn');
  const toastContainer   = document.getElementById('toastContainer');

  function getToken() {
    return localStorage.getItem('token');
  }

  function authHeaders() {
    var headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' };
    var token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    return headers;
  }

  function fetchJson(path) {
    return fetch(API_BASE + path, {
      method: 'GET',
      headers: authHeaders()
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () { return {}; }).then(function (body) {
          throw new Error((body && (body.message || body.error)) || 'Request failed');
        });
      }
      return res.json();
    });
  }

  function postJson(path, body) {
    return fetch(API_BASE + path, {
      method: 'POST',
      headers: authHeaders(),
      body: JSON.stringify(body)
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () { return {}; }).then(function (data) {
          throw new Error((data && (data.message || data.error)) || 'Request failed');
        });
      }
      return res.json();
    });
  }

  function putJson(path, body) {
    return fetch(API_BASE + path, {
      method: 'PUT',
      headers: authHeaders(),
      body: JSON.stringify(body)
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () { return {}; }).then(function (data) {
          throw new Error((data && (data.message || data.error)) || 'Request failed');
        });
      }
      return res.json();
    });
  }

  function deleteRequest(path) {
    return fetch(API_BASE + path, {
      method: 'DELETE',
      headers: authHeaders()
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () { return {}; }).then(function (data) {
          throw new Error((data && (data.message || data.error)) || 'Request failed');
        });
      }
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
    toast.setAttribute('role', 'status');
    toast.textContent = message;
    toastContainer.appendChild(toast);
    setTimeout(function () {
      if (toast.parentNode) toast.parentNode.removeChild(toast);
    }, 4000);
  }

  function mapStatus(status) {
    var s = status ? String(status).toUpperCase() : '';
    if (s === 'ACTIVE') return { label: 'ACTIVE', className: 'fn-nutrition-status-active' };
    if (s === 'DRAFT')  return { label: 'DRAFT',  className: 'fn-nutrition-status-draft' };
    return { label: s || 'DRAFT', className: 'fn-nutrition-status-draft' };
  }

  function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  function formatNumber(n) {
    if (typeof n !== 'number' || isNaN(n)) return '0';
    return n >= 1000 ? n.toLocaleString() : String(n);
  }

  // ── Stats ──────────────────────────────────────────────────────────────────

  function fetchStats() {
    if (statsLoading) statsLoading.style.display = 'flex';
    hideError(statsError);

    fetchJson('/nutrition/stats')
      .then(function (data) {
        if (statsLoading) statsLoading.style.display = 'none';
        renderStats(data);
      })
      .catch(function (err) {
        if (statsLoading) statsLoading.style.display = 'none';
        showError(statsError, err.message || 'Failed to load statistics.');
        if (nutritionStats) nutritionStats.innerHTML = '';
      });
  }

  function renderStats(data) {
    if (!nutritionStats) return;
    var totalPlans  = data && data.totalPlans  != null ? Number(data.totalPlans)  : 0;
    var activeUsers = data && data.activeUsers != null ? Number(data.activeUsers) : 0;

    if (statsLoading && statsLoading.parentNode === nutritionStats) {
      nutritionStats.removeChild(statsLoading);
    }
    nutritionStats.innerHTML = '';

    var card1 = document.createElement('div');
    card1.className = 'fn-admin-stat-card';
    card1.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">🍎</div>' +
      '<div><div class="fn-admin-stat-meta">Total Plans</div>' +
      '<div class="fn-admin-stat-value">' + totalPlans + '</div></div>';
    nutritionStats.appendChild(card1);

    var card2 = document.createElement('div');
    card2.className = 'fn-admin-stat-card';
    card2.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">👥</div>' +
      '<div><div class="fn-admin-stat-meta">Active Users</div>' +
      '<div class="fn-admin-stat-value">' + formatNumber(activeUsers) + '</div></div>';
    nutritionStats.appendChild(card2);
  }

  // ── Plans table ────────────────────────────────────────────────────────────

  function fetchPlans() {
    hideError(plansError);
    if (plansLoading) plansLoading.style.display = 'flex';
    if (tableWrapper) tableWrapper.style.visibility = 'hidden';

    fetchJson('/nutrition/plans')
      .then(function (list) {
        if (plansLoading) plansLoading.style.display = 'none';
        if (tableWrapper) tableWrapper.style.visibility = '';
        renderPlans(Array.isArray(list) ? list : []);
      })
      .catch(function (err) {
        if (plansLoading) plansLoading.style.display = 'none';
        if (tableWrapper) tableWrapper.style.visibility = '';
        renderPlans([]);
        showError(plansError, err.message || 'Failed to load plans.');
      });
  }

  function renderPlans(plans) {
    if (!plansTableBody) return;
    plansTableBody.innerHTML = '';

    if (plans.length === 0) {
      var emptyRow  = document.createElement('tr');
      var emptyCell = document.createElement('td');
      emptyCell.colSpan = 6;
      emptyCell.className = 'fn-nutrition-empty-cell';
      emptyCell.innerHTML =
        '<div class="fn-nutrition-empty">' +
        '<div class="fn-nutrition-empty-icon" aria-hidden="true">🍎</div>' +
        '<p class="fn-nutrition-empty-title">No plans found</p>' +
        '<p class="fn-nutrition-empty-text">Create your first nutrition plan to get started.</p>' +
        '</div>';
      emptyRow.appendChild(emptyCell);
      plansTableBody.appendChild(emptyRow);
      return;
    }

    plans.forEach(function (plan) {
      var row = document.createElement('tr');

      var nameCell = document.createElement('td');
      nameCell.className = 'fn-nutrition-plan-cell';
      nameCell.innerHTML =
        '<span class="fn-nutrition-plan-icon" aria-hidden="true">🍎</span>' +
        '<span class="fn-nutrition-plan-name">' + escapeHtml(plan.planName || '—') + '</span>';
      row.appendChild(nameCell);

      var categoryCell = document.createElement('td');
      categoryCell.innerHTML = '<span class="fn-nutrition-category">' + escapeHtml(plan.category || '—') + '</span>';
      row.appendChild(categoryCell);

      var clientsCell = document.createElement('td');
      clientsCell.textContent = plan.activeClients != null ? formatNumber(Number(plan.activeClients)) : '0';
      row.appendChild(clientsCell);

      var ratingCell = document.createElement('td');
      var rating = plan.avgRating != null ? Number(plan.avgRating) : 0;
      ratingCell.innerHTML =
        '<span class="fn-nutrition-rating">' +
        '<span class="fn-nutrition-rating-star">★</span> ' +
        (rating > 0 ? rating.toFixed(1) : '—') +
        '</span>';
      row.appendChild(ratingCell);

      var statusMap  = mapStatus(plan.status);
      var statusCell = document.createElement('td');
      statusCell.innerHTML =
        '<span class="fn-nutrition-status ' + statusMap.className + '">' + statusMap.label + '</span>';
      row.appendChild(statusCell);

      var actionsCell = document.createElement('td');
      actionsCell.className = 'fn-admin-actions-cell';

      var editBtn = document.createElement('button');
      editBtn.type = 'button';
      editBtn.className = 'fn-admin-action-btn fn-admin-action-edit';
      editBtn.textContent = 'Edit';
      editBtn.addEventListener('click', function () { openEditModal(plan); });

      var deleteBtn = document.createElement('button');
      deleteBtn.type = 'button';
      deleteBtn.className = 'fn-admin-action-btn fn-admin-action-delete';
      deleteBtn.textContent = 'Delete';
      deleteBtn.addEventListener('click', function () { openDeleteModal(plan); });

      actionsCell.appendChild(editBtn);
      actionsCell.appendChild(deleteBtn);
      row.appendChild(actionsCell);

      plansTableBody.appendChild(row);
    });
  }

  // ── Create ─────────────────────────────────────────────────────────────────

  function validateCreateForm() {
    var nameEl     = document.getElementById('planName');
    var categoryEl = document.getElementById('category');
    var priceEl    = document.getElementById('price');
    var nameErr    = document.getElementById('planNameError');
    var categoryErr = document.getElementById('categoryError');
    var priceErr   = document.getElementById('priceError');

    nameErr.textContent = '';
    categoryErr.textContent = '';
    priceErr.textContent = '';

    var valid = true;
    if (!nameEl || !nameEl.value.trim()) {
      nameErr.textContent = 'Plan name is required.';
      valid = false;
    }
    if (!categoryEl || !categoryEl.value.trim()) {
      categoryErr.textContent = 'Category is required.';
      valid = false;
    }
    var priceVal = priceEl ? parseFloat(priceEl.value) : NaN;
    if (!priceEl || priceEl.value.trim() === '' || isNaN(priceVal) || priceVal < 0) {
      priceErr.textContent = 'Price is required and must be 0 or greater.';
      valid = false;
    }
    return valid;
  }

  function openCreateModal() {
    if (createPlanForm) createPlanForm.reset();
    hideError(createPlanAlert);
    document.getElementById('planNameError').textContent = '';
    document.getElementById('categoryError').textContent = '';
    document.getElementById('priceError').textContent = '';
    new bootstrap.Modal(createPlanModalEl).show();
  }

  function submitCreatePlan() {
    if (!createPlanForm) return;
    hideError(createPlanAlert);
    if (!validateCreateForm()) return;

    var payload = {
      planName:    document.getElementById('planName').value.trim(),
      category:    document.getElementById('category').value.trim(),
      price:       parseFloat(document.getElementById('price').value),
      description: (document.getElementById('description').value || '').trim() || null,
      status:      document.getElementById('status').value || 'DRAFT'
    };
    if (!payload.description) delete payload.description;

    createPlanSubmitBtn.disabled = true;
    postJson('/nutrition/plans', payload)
      .then(function () {
        var modal = bootstrap.Modal.getInstance(createPlanModalEl);
        if (modal) modal.hide();
        createPlanForm.reset();
        showToast('Plan created successfully.');
        fetchPlans();
        fetchStats();
      })
      .catch(function (err) {
        showError(createPlanAlert, err.message || 'Failed to create plan.');
      })
      .finally(function () {
        createPlanSubmitBtn.disabled = false;
      });
  }

  // ── Edit ───────────────────────────────────────────────────────────────────

  function validateEditForm() {
    var nameEl     = document.getElementById('editPlanName');
    var categoryEl = document.getElementById('editCategory');
    var priceEl    = document.getElementById('editPrice');
    var nameErr    = document.getElementById('editPlanNameError');
    var categoryErr = document.getElementById('editCategoryError');
    var priceErr   = document.getElementById('editPriceError');

    nameErr.textContent = '';
    categoryErr.textContent = '';
    priceErr.textContent = '';

    var valid = true;
    if (!nameEl || !nameEl.value.trim()) {
      nameErr.textContent = 'Plan name is required.';
      valid = false;
    }
    if (!categoryEl || !categoryEl.value.trim()) {
      categoryErr.textContent = 'Category is required.';
      valid = false;
    }
    var priceVal = priceEl ? parseFloat(priceEl.value) : NaN;
    if (!priceEl || priceEl.value.trim() === '' || isNaN(priceVal) || priceVal < 0) {
      priceErr.textContent = 'Price is required and must be 0 or greater.';
      valid = false;
    }
    return valid;
  }

  function openEditModal(plan) {
    hideError(editPlanAlert);
    document.getElementById('editPlanId').value           = plan.id;
    document.getElementById('editPlanName').value         = plan.planName  || '';
    document.getElementById('editCategory').value         = plan.category  || '';
    document.getElementById('editPrice').value            = plan.price     != null ? plan.price : '';
    document.getElementById('editDescription').value      = plan.description || '';
    document.getElementById('editStatus').value           = plan.status    || 'DRAFT';
    document.getElementById('editPlanNameError').textContent  = '';
    document.getElementById('editCategoryError').textContent  = '';
    document.getElementById('editPriceError').textContent     = '';
    new bootstrap.Modal(editPlanModalEl).show();
  }

  function submitEditPlan() {
    hideError(editPlanAlert);
    if (!validateEditForm()) return;

    var id = document.getElementById('editPlanId').value;
    var payload = {
      planName:    document.getElementById('editPlanName').value.trim(),
      category:    document.getElementById('editCategory').value.trim(),
      price:       parseFloat(document.getElementById('editPrice').value),
      description: (document.getElementById('editDescription').value || '').trim() || null,
      status:      document.getElementById('editStatus').value || 'DRAFT'
    };
    if (!payload.description) delete payload.description;

    editPlanSubmitBtn.disabled = true;
    putJson('/nutrition/plans/' + id, payload)
      .then(function () {
        var modal = bootstrap.Modal.getInstance(editPlanModalEl);
        if (modal) modal.hide();
        showToast('Plan updated successfully.');
        fetchPlans();
        fetchStats();
      })
      .catch(function (err) {
        showError(editPlanAlert, err.message || 'Failed to update plan.');
      })
      .finally(function () {
        editPlanSubmitBtn.disabled = false;
      });
  }

  // ── Delete ─────────────────────────────────────────────────────────────────

  function openDeleteModal(plan) {
    hideError(deletePlanAlert);
    document.getElementById('deletePlanId').value   = plan.id;
    document.getElementById('deletePlanName').textContent = plan.planName || 'this plan';
    new bootstrap.Modal(deletePlanModalEl).show();
  }

  function confirmDelete() {
    var id = document.getElementById('deletePlanId').value;
    hideError(deletePlanAlert);
    deletePlanConfirmBtn.disabled = true;
    deleteRequest('/nutrition/plans/' + id)
      .then(function () {
        var modal = bootstrap.Modal.getInstance(deletePlanModalEl);
        if (modal) modal.hide();
        showToast('Plan deleted successfully.');
        fetchPlans();
        fetchStats();
      })
      .catch(function (err) {
        showError(deletePlanAlert, err.message || 'Failed to delete plan.');
      })
      .finally(function () {
        deletePlanConfirmBtn.disabled = false;
      });
  }

  // ── Event wiring ───────────────────────────────────────────────────────────

  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      localStorage.removeItem('token');
      window.location.href = '/login';
    });
  }

  if (createPlanBtn && createPlanModalEl) {
    createPlanBtn.addEventListener('click', openCreateModal);
  }

  if (createPlanSubmitBtn) {
    createPlanSubmitBtn.addEventListener('click', submitCreatePlan);
  }

  if (createPlanForm) {
    createPlanForm.addEventListener('submit', function (e) {
      e.preventDefault();
      submitCreatePlan();
    });
  }

  if (editPlanSubmitBtn) {
    editPlanSubmitBtn.addEventListener('click', submitEditPlan);
  }

  if (editPlanForm) {
    editPlanForm.addEventListener('submit', function (e) {
      e.preventDefault();
      submitEditPlan();
    });
  }

  if (deletePlanConfirmBtn) {
    deletePlanConfirmBtn.addEventListener('click', confirmDelete);
  }

  // ── Init ───────────────────────────────────────────────────────────────────

  fetchStats();
  fetchPlans();
})();
