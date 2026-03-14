(function () {
  const API_BASE = '/api';

  const statsContainer = document.getElementById('trainerStats');
  const statsError = document.getElementById('statsError');
  const trainersError = document.getElementById('trainersError');
  const tableBody = document.getElementById('trainerTableBody');
  const logoutBtn = document.getElementById('logoutBtn');
  const addTrainerBtn = document.getElementById('addTrainerBtn');
  const addTrainerModalEl = document.getElementById('addTrainerModal');
  const addTrainerForm = document.getElementById('addTrainerForm');
  const addTrainerSubmitBtn = document.getElementById('addTrainerSubmitBtn');
  const addTrainerAlert = document.getElementById('addTrainerAlert');
  const editTrainerModalEl = document.getElementById('editTrainerModal');
  const editTrainerForm = document.getElementById('editTrainerForm');
  const editTrainerSubmitBtn = document.getElementById('editTrainerSubmitBtn');
  const editTrainerAlert = document.getElementById('editTrainerAlert');

  function showError(el, message) {
    if (!el) return;
    el.textContent = message;
    el.style.display = 'block';
  }

  function hideError(el) {
    if (!el) return;
    el.style.display = 'none';
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', function () {
      localStorage.removeItem('token');
      window.location.href = '/login';
    });
  }

  function mapStatus(status) {
    if (!status) return { label: 'Unknown', className: 'fn-admin-status-inactive' };
    var normalized = String(status).toUpperCase();
    if (normalized === 'ACTIVE') {
      return { label: 'Active', className: 'fn-admin-status-active' };
    }
    if (normalized === 'BLOCKED') {
      return { label: 'Blocked', className: 'fn-admin-status-blocked' };
    }
    return { label: 'Inactive', className: 'fn-admin-status-inactive' };
  }

  function renderStats(stats) {
    if (!statsContainer) return;
    statsContainer.innerHTML = '';

    var totalCard = document.createElement('div');
    totalCard.className = 'fn-admin-stat-card';
    totalCard.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">👥</div>' +
      '<div>' +
      '<div class="fn-admin-stat-meta">Total Trainers</div>' +
      '<div class="fn-admin-stat-value">' + (stats.totalTrainers != null ? stats.totalTrainers : '–') + '</div>' +
      '</div>';

    var activeCard = document.createElement('div');
    activeCard.className = 'fn-admin-stat-card';
    activeCard.innerHTML =
      '<div class="fn-admin-stat-icon" aria-hidden="true">✅</div>' +
      '<div>' +
      '<div class="fn-admin-stat-meta">Currently Active</div>' +
      '<div class="fn-admin-stat-value">' + (stats.currentlyActive != null ? stats.currentlyActive : '–') + '</div>' +
      '</div>';

    statsContainer.appendChild(totalCard);
    statsContainer.appendChild(activeCard);
  }

  function renderTrainers(list) {
    if (!tableBody) return;
    tableBody.innerHTML = '';

    if (!Array.isArray(list) || list.length === 0) {
      var emptyRow = document.createElement('tr');
      var emptyCell = document.createElement('td');
      emptyCell.colSpan = 6;
      emptyCell.textContent = 'No trainers found.';
      emptyCell.style.color = 'var(--fn-text-muted)';
      emptyCell.style.textAlign = 'center';
      emptyCell.style.padding = '1.25rem';
      emptyRow.appendChild(emptyCell);
      tableBody.appendChild(emptyRow);
      return;
    }

    list.forEach(function (trainer) {
      var row = document.createElement('tr');

      var idCell = document.createElement('td');
      idCell.textContent = trainer.trainerId != null ? trainer.trainerId : '';

      var trainerCell = document.createElement('td');
      trainerCell.className = 'fn-admin-trainer-cell';

      var initials = '';
      if (trainer.firstName) initials += trainer.firstName.charAt(0);
      if (trainer.lastName) initials += trainer.lastName.charAt(0);
      initials = initials || 'FN';

      var avatar = document.createElement('div');
      avatar.className = 'fn-admin-avatar';
      avatar.textContent = initials.toUpperCase();

      var infoWrapper = document.createElement('div');
      infoWrapper.className = 'fn-admin-trainer-main';

      var nameEl = document.createElement('div');
      nameEl.className = 'fn-admin-trainer-name';
      nameEl.textContent = (trainer.firstName || '') + (trainer.lastName ? ' ' + trainer.lastName : '');

      infoWrapper.appendChild(nameEl);

      if (trainer.bio) {
        var bioEl = document.createElement('div');
        bioEl.className = 'fn-admin-trainer-bio';
        bioEl.textContent = trainer.bio;
        infoWrapper.appendChild(bioEl);
      }

      trainerCell.appendChild(avatar);
      trainerCell.appendChild(infoWrapper);

      var specCell = document.createElement('td');
      var pill = document.createElement('span');
      pill.className = 'fn-admin-pill';
      pill.innerHTML =
        '<span class="fn-admin-pill-icon">🏷️</span>' +
        '<span>' + (trainer.specialization || '—') + '</span>';
      specCell.appendChild(pill);

      var contactCell = document.createElement('td');
      contactCell.className = 'fn-admin-contact';
      var emailLine = document.createElement('span');
      emailLine.innerHTML = '<span>✉️</span><span>' + (trainer.email || '—') + '</span>';
      var phoneLine = document.createElement('span');
      phoneLine.innerHTML = '<span>📞</span><span>' + (trainer.phone || '—') + '</span>';
      contactCell.appendChild(emailLine);
      contactCell.appendChild(phoneLine);

      var statusCell = document.createElement('td');
      var statusMap = mapStatus(trainer.status);
      var statusWrapper = document.createElement('span');
      statusWrapper.className = 'fn-admin-status ' + statusMap.className;
      statusWrapper.innerHTML =
        '<span class="fn-admin-status-dot" aria-hidden="true"></span>' +
        '<span>' + statusMap.label + '</span>';
      statusCell.appendChild(statusWrapper);

      var actionsCell = document.createElement('td');
      actionsCell.className = 'fn-admin-actions-cell';
      var editBtn = document.createElement('button');
      editBtn.type = 'button';
      editBtn.className = 'fn-admin-action-btn fn-admin-action-edit';
      editBtn.textContent = 'Edit';
      editBtn.setAttribute('aria-label', 'Edit ' + (trainer.firstName || '') + ' ' + (trainer.lastName || ''));
      var deleteBtn = document.createElement('button');
      deleteBtn.type = 'button';
      deleteBtn.className = 'fn-admin-action-btn fn-admin-action-delete';
      deleteBtn.textContent = 'Delete';
      deleteBtn.setAttribute('aria-label', 'Delete ' + (trainer.firstName || '') + ' ' + (trainer.lastName || ''));
      actionsCell.appendChild(editBtn);
      actionsCell.appendChild(deleteBtn);

      row.appendChild(idCell);
      row.appendChild(trainerCell);
      row.appendChild(specCell);
      row.appendChild(contactCell);
      row.appendChild(statusCell);
      row.appendChild(actionsCell);

      editBtn.addEventListener('click', function () {
        openEditModal(trainer);
      });
      deleteBtn.addEventListener('click', function () {
        confirmDeleteTrainer(trainer);
      });

      tableBody.appendChild(row);
    });
  }

  function openEditModal(trainer) {
    var idEl = document.getElementById('editTrainerId');
    var firstNameEl = document.getElementById('editFirstName');
    var lastNameEl = document.getElementById('editLastName');
    var passwordEl = document.getElementById('editPassword');
    var phoneEl = document.getElementById('editPhone');
    var statusEl = document.getElementById('editStatus');
    var specializationEl = document.getElementById('editSpecialization');
    var bioEl = document.getElementById('editBio');
    if (!idEl || !firstNameEl || !lastNameEl || !phoneEl) return;
    idEl.value = trainer.trainerId || '';
    firstNameEl.value = trainer.firstName || '';
    lastNameEl.value = trainer.lastName || '';
    passwordEl.value = '';
    phoneEl.value = trainer.phone || '';
    if (statusEl) {
      var statusVal = (trainer.status || '').toUpperCase();
      if (['ACTIVE', 'INACTIVE', 'BLOCKED'].indexOf(statusVal) >= 0) {
        statusEl.value = statusVal;
      } else {
        statusEl.value = 'ACTIVE';
      }
    }
    specializationEl.value = trainer.specialization || '';
    bioEl.value = trainer.bio || '';
    if (editTrainerAlert) {
      editTrainerAlert.style.display = 'none';
      editTrainerAlert.textContent = '';
    }
    var modal = new bootstrap.Modal(editTrainerModalEl);
    modal.show();
  }

  function confirmDeleteTrainer(trainer) {
    var name = (trainer.firstName || '') + (trainer.lastName ? ' ' + trainer.lastName : '');
    if (!window.confirm('Delete trainer ' + (name || trainer.email) + '? This cannot be undone.')) {
      return;
    }
    var id = trainer.trainerId;
    if (!id) return;
    deleteJson('/trainers/' + id)
      .then(function () {
        refreshData();
      })
      .catch(function (err) {
        showError(trainersError, err.message || 'Failed to delete trainer.');
      });
  }

  function getToken() {
    return localStorage.getItem('token');
  }

  function authHeaders() {
    var headers = { 'Accept': 'application/json', 'Content-Type': 'application/json' };
    var token = getToken();
    if (token) {
      headers['Authorization'] = 'Bearer ' + token;
    }
    return headers;
  }

  function fetchJson(path) {
    return fetch(API_BASE + path, {
      method: 'GET',
      headers: authHeaders()
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () {
          return {};
        }).then(function (body) {
          var msg = body && (body.message || body.error);
          throw new Error(msg || 'Request failed');
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
    }).then(handleResponse);
  }

  function putJson(path, body) {
    return fetch(API_BASE + path, {
      method: 'PUT',
      headers: authHeaders(),
      body: JSON.stringify(body)
    }).then(handleResponse);
  }

  function deleteJson(path) {
    return fetch(API_BASE + path, {
      method: 'DELETE',
      headers: authHeaders()
    }).then(function (res) {
      if (!res.ok) {
        return res.json().catch(function () {
          return {};
        }).then(function (data) {
          var msg = data && (data.message || data.error);
          throw new Error(msg || 'Request failed');
        });
      }
      return res.text().then(function (t) { return t ? JSON.parse(t) : {}; }).catch(function () { return {}; });
    });
  }

  function handleResponse(res) {
    if (!res.ok) {
      return res.json().catch(function () {
        return {};
      }).then(function (data) {
        var msg = data && (data.message || data.error);
        throw new Error(msg || 'Request failed');
      });
    }
    return res.json();
  }

  function refreshData() {
    fetchJson('/trainers/stats')
      .then(renderStats)
      .catch(function (err) {
        showError(statsError, err.message || 'Failed to load trainer statistics.');
      });
    fetchJson('/trainers')
      .then(renderTrainers)
      .catch(function (err) {
        showError(trainersError, err.message || 'Failed to load trainers.');
      });
  }

  if (addTrainerBtn && addTrainerModalEl) {
    var addTrainerModal = new bootstrap.Modal(addTrainerModalEl);
    addTrainerBtn.addEventListener('click', function () {
      if (addTrainerForm) addTrainerForm.reset();
      if (addTrainerAlert) {
        addTrainerAlert.style.display = 'none';
        addTrainerAlert.textContent = '';
      }
      addTrainerModal.show();
    });
  }

  if (addTrainerSubmitBtn && addTrainerForm) {
    addTrainerSubmitBtn.addEventListener('click', function () {
      var firstName = document.getElementById('addFirstName');
      var lastName = document.getElementById('addLastName');
      var email = document.getElementById('addEmail');
      var password = document.getElementById('addPassword');
      var phone = document.getElementById('addPhone');
      var specialization = document.getElementById('addSpecialization');
      var bio = document.getElementById('addBio');
      if (!firstName || !lastName || !email || !password || !phone) return;
      if (addTrainerAlert) {
        addTrainerAlert.style.display = 'none';
        addTrainerAlert.textContent = '';
      }
      var payload = {
        firstName: firstName.value.trim(),
        lastName: lastName.value.trim(),
        email: email.value.trim(),
        password: password.value,
        phone: phone.value.trim(),
        specialization: specialization ? specialization.value.trim() || null : null,
        bio: bio ? bio.value.trim() || null : null
      };
      addTrainerSubmitBtn.disabled = true;
      postJson('/trainers', payload)
        .then(function () {
          if (addTrainerModalEl) {
            var modal = bootstrap.Modal.getInstance(addTrainerModalEl);
            if (modal) modal.hide();
          }
          refreshData();
        })
        .catch(function (err) {
          showError(addTrainerAlert, err.message || 'Failed to add trainer.');
        })
        .finally(function () {
          addTrainerSubmitBtn.disabled = false;
        });
    });
  }

  if (editTrainerSubmitBtn && editTrainerForm && editTrainerModalEl) {
    editTrainerSubmitBtn.addEventListener('click', function () {
      var idEl = document.getElementById('editTrainerId');
      var firstNameEl = document.getElementById('editFirstName');
      var lastNameEl = document.getElementById('editLastName');
      var passwordEl = document.getElementById('editPassword');
      var phoneEl = document.getElementById('editPhone');
      var statusEl = document.getElementById('editStatus');
      var specializationEl = document.getElementById('editSpecialization');
      var bioEl = document.getElementById('editBio');
      if (!idEl || !firstNameEl || !lastNameEl || !phoneEl) return;
      var id = idEl.value.trim();
      if (!id) return;
      if (editTrainerAlert) {
        editTrainerAlert.style.display = 'none';
        editTrainerAlert.textContent = '';
      }
      var payload = {
        firstName: firstNameEl.value.trim(),
        lastName: lastNameEl.value.trim(),
        phone: phoneEl.value.trim(),
        specialization: specializationEl ? specializationEl.value.trim() || null : null,
        bio: bioEl ? bioEl.value.trim() || null : null,
        status: statusEl ? statusEl.value : 'ACTIVE'
      };
      var pwd = passwordEl ? passwordEl.value.trim() : '';
      if (pwd) payload.password = pwd;

      editTrainerSubmitBtn.disabled = true;
      putJson('/trainers/' + id, payload)
        .then(function () {
          var modal = bootstrap.Modal.getInstance(editTrainerModalEl);
          if (modal) modal.hide();
          refreshData();
        })
        .catch(function (err) {
          showError(editTrainerAlert, err.message || 'Failed to update trainer.');
        })
        .finally(function () {
          editTrainerSubmitBtn.disabled = false;
        });
    });
  }

  hideError(statsError);
  hideError(trainersError);
  refreshData();
})();

