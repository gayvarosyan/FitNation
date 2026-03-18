(function () {
  'use strict';

  const API_BASE = '/api';
  const PAGE_SIZE = 10;

  const nutritionStats = document.getElementById('nutritionStats');
  const statsError = document.getElementById('statsError');
  const statsLoading = document.getElementById('statsLoading');
  const plansError = document.getElementById('plansError');
  const plansLoading = document.getElementById('plansLoading');
  const tableWrapper = document.getElementById('tableWrapper');
  const plansTableBody = document.getElementById('plansTableBody');
  const planSearch = document.getElementById('planSearch');
  const plansSummary = document.getElementById('plansSummary');
  const prevPageBtn = document.getElementById('prevPageBtn');
  const nextPageBtn = document.getElementById('nextPageBtn');
  const createPlanBtn = document.getElementById('createPlanBtn');
  const createPlanModalEl = document.getElementById('createPlanModal');
  const createPlanForm = document.getElementById('createPlanForm');
  const createPlanSubmitBtn = document.getElementById('createPlanSubmitBtn');
  const createPlanAlert = document.getElementById('createPlanAlert');
  const logoutBtn = document.getElementById('logoutBtn');
  const toastContainer = document.getElementById('toastContainer');

  let allPlans = [];
  let currentPage = 0;
  let filteredPlans = [];

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
          var msg = (body && (body.message || body.error)) || 'Request failed';
          throw new Error(msg);
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
          var msg = (data && (data.message || data.error)) || 'Request failed';
          throw new Error(msg);
        });
      }
      return res.json();
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
    if (s === 'DRAFT') return { label: 'DRAFT', className: 'fn-nutrition-status-draft' };
    return { label: s || 'DRAFT', className: 'fn-nutrition-status-draft' };
  }

  function fetchStats() {
    if (statsLoading) statsLoading.style.display = 'flex';
    hideError(statsError);
    if (nutritionStats) {
      var placeholder = document.getElementById('statsLoading');
      if (placeholder && placeholder.parentNode === nutritionStats) {
        nutritionStats.innerHTML = '';
        nutritionStats.appendChild(placeholder);
      }
    }

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
    var totalPlans = data && (data.totalPlans != null) ? Number(data.totalPlans) : 0;
    var activeUsers = data && (data.activeUsers != null) ? Number(data.activeUsers) : 0;
    var avgSuccessRate = (data && data.avgSuccessRate != null) ? data.avgSuccessRate : '92%';

    if (statsLoading && statsLoading.parentNode === nutritionStats) {
      nutritionStats.removeChild(statsLoading);
    }
    nutritionStats.innerHTML = '';

    var card1 = document.createElement('div');
    card1.className = 'fn-admin-stat-card';
    card1.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">🍎</div>' +
      '<div><div class="fn-admin-stat-meta">Total Plans</div><div class="fn-admin-stat-value">' + totalPlans + '</div></div>';
    nutritionStats.appendChild(card1);

    var card2 = document.createElement('div');
    card2.className = 'fn-admin-stat-card';
    card2.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">👥</div>' +
      '<div><div class="fn-admin-stat-meta">Active Users</div><div class="fn-admin-stat-value">' + formatNumber(activeUsers) + '</div></div>';
    nutritionStats.appendChild(card2);

    var card3 = document.createElement('div');
    card3.className = 'fn-admin-stat-card';
    card3.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">📈</div>' +
      '<div><div class="fn-admin-stat-meta">Avg. Success Rate</div><div class="fn-admin-stat-value">' + avgSuccessRate + '</div></div>';
    nutritionStats.appendChild(card3);
  }

  function formatNumber(n) {
    if (typeof n !== 'number' || isNaN(n)) return '0';
    if (n >= 1000) return n.toLocaleString();
    return String(n);
  }

  function fetchPlans() {
    hideError(plansError);
    if (plansLoading) plansLoading.style.display = 'flex';
    if (tableWrapper) tableWrapper.style.visibility = 'hidden';

    fetchJson('/nutrition/plans')
      .then(function (list) {
        allPlans = Array.isArray(list) ? list : [];
        applyFilterAndPage();
        if (plansLoading) plansLoading.style.display = 'none';
        if (tableWrapper) tableWrapper.style.visibility = '';
      })
      .catch(function (err) {
        allPlans = [];
        applyFilterAndPage();
        if (plansLoading) plansLoading.style.display = 'none';
        if (tableWrapper) tableWrapper.style.visibility = '';
        showError(plansError, err.message || 'Failed to load plans.');
      });
  }

  function getSearchQuery() {
    return planSearch && planSearch.value ? planSearch.value.trim().toLowerCase() : '';
  }

  function applyFilterAndPage() {
    var q = getSearchQuery();
    if (!q) {
      filteredPlans = allPlans.slice();
    } else {
      filteredPlans = allPlans.filter(function (p) {
        var name = (p.planName || '').toLowerCase();
        return name.indexOf(q) !== -1;
      });
    }
    currentPage = 0;
    renderPlans();
  }

  function renderPlans() {
    if (!plansTableBody) return;

    var total = filteredPlans.length;
    var start = currentPage * PAGE_SIZE;
    var pagePlans = filteredPlans.slice(start, start + PAGE_SIZE);

    plansTableBody.innerHTML = '';

    if (pagePlans.length === 0) {
      var emptyRow = document.createElement('tr');
      var emptyCell = document.createElement('td');
      emptyCell.colSpan = 6;
      emptyCell.className = 'fn-nutrition-empty-cell';
      emptyCell.innerHTML =
        '<div class="fn-nutrition-empty">' +
        '<div class="fn-nutrition-empty-icon" aria-hidden="true">🍎</div>' +
        '<p class="fn-nutrition-empty-title">No plans found</p>' +
        '<p class="fn-nutrition-empty-text">' + (total === 0 && allPlans.length === 0
          ? 'Create your first nutrition plan to get started.'
          : 'No plans match your search. Try a different term.') + '</p>' +
        '</div>';
      emptyRow.appendChild(emptyCell);
      plansTableBody.appendChild(emptyRow);
    } else {
      pagePlans.forEach(function (plan) {
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
        ratingCell.innerHTML = '<span class="fn-nutrition-rating"><span class="fn-nutrition-rating-star">★</span> ' + (rating > 0 ? rating.toFixed(1) : '—') + '</span>';
        row.appendChild(ratingCell);

        var statusMap = mapStatus(plan.status);
        var statusCell = document.createElement('td');
        statusCell.innerHTML = '<span class="fn-nutrition-status ' + statusMap.className + '">' + statusMap.label + '</span>';
        row.appendChild(statusCell);

        var actionsCell = document.createElement('td');
        actionsCell.className = 'fn-admin-actions-cell';
        actionsCell.innerHTML =
          '<button type="button" class="fn-admin-action-btn fn-admin-action-edit">Edit</button>' +
          '<button type="button" class="fn-admin-action-btn fn-admin-action-delete">Delete</button>';
        row.appendChild(actionsCell);

        plansTableBody.appendChild(row);
      });
    }

    if (plansSummary) {
      plansSummary.textContent = 'Showing ' + (pagePlans.length ? start + 1 : 0) + '–' + (start + pagePlans.length) + ' of ' + total + ' plans';
    }
    if (prevPageBtn) {
      prevPageBtn.disabled = currentPage <= 0;
    }
    if (nextPageBtn) {
      nextPageBtn.disabled = start + pagePlans.length >= total;
    }
  }

  function escapeHtml(text) {
    var div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  function createPlan(payload) {
    return postJson('/nutrition/plans', payload);
  }

  function validateForm() {
    var nameEl = document.getElementById('planName');
    var categoryEl = document.getElementById('category');
    var priceEl = document.getElementById('price');
    var nameErr = document.getElementById('planNameError');
    var categoryErr = document.getElementById('categoryError');
    var priceErr = document.getElementById('priceError');

    function setErr(el, msg) {
      if (el) { el.textContent = msg || ''; }
    }
    setErr(nameErr, '');
    setErr(categoryErr, '');
    setErr(priceErr, '');

    var valid = true;
    if (!nameEl || !nameEl.value.trim()) {
      setErr(nameErr, 'Plan name is required.');
      valid = false;
    }
    if (!categoryEl || !categoryEl.value.trim()) {
      setErr(categoryErr, 'Category is required.');
      valid = false;
    }
    var priceVal = priceEl ? parseFloat(priceEl.value, 10) : NaN;
    if (!priceEl || (priceEl.value.trim() === '' || isNaN(priceVal) || priceVal < 0)) {
      setErr(priceErr, 'Price is required and must be 0 or greater.');
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
    var modal = new bootstrap.Modal(createPlanModalEl);
    modal.show();
  }

  function submitCreatePlan() {
    if (!createPlanForm) return;
    hideError(createPlanAlert);
    if (!validateForm()) return;

    var planName = document.getElementById('planName').value.trim();
    var category = document.getElementById('category').value.trim();
    var price = parseFloat(document.getElementById('price').value, 10);
    var descriptionEl = document.getElementById('description');
    var statusEl = document.getElementById('status');
    var description = descriptionEl ? descriptionEl.value.trim() : '';
    var status = statusEl ? statusEl.value : 'DRAFT';

    var payload = {
      planName: planName,
      category: category,
      price: price,
      status: status
    };
    if (description) payload.description = description;

    createPlanSubmitBtn.disabled = true;
    createPlan(payload)
      .then(function () {
        var modal = bootstrap.Modal.getInstance(createPlanModalEl);
        if (modal) modal.hide();
        if (createPlanForm) createPlanForm.reset();
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

  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      localStorage.removeItem('token');
      window.location.href = '/login';
    });
  }

  if (planSearch) {
    planSearch.addEventListener('input', function () {
      applyFilterAndPage();
    });
    planSearch.addEventListener('keydown', function (e) {
      if (e.key === 'Enter') e.preventDefault();
    });
  }

  if (prevPageBtn) {
    prevPageBtn.addEventListener('click', function () {
      if (currentPage > 0) {
        currentPage--;
        renderPlans();
      }
    });
  }
  if (nextPageBtn) {
    nextPageBtn.addEventListener('click', function () {
      var start = (currentPage + 1) * PAGE_SIZE;
      if (start < filteredPlans.length) {
        currentPage++;
        renderPlans();
      }
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

  fetchStats();
  fetchPlans();
})();
