(function () {
  const API_BASE = '/api/auth';

  const form = document.getElementById('registerForm');
  const registerBtn = document.getElementById('registerBtn');
  const alertEl = document.getElementById('registerAlert');
  const roleButtons = document.querySelectorAll('.fn-role-group[aria-label="Registration role"] .fn-role-btn');
  const trainerFields = document.getElementById('trainerFields');
  const specializationInput = document.getElementById('specialization');
  const bioInput = document.getElementById('bio');

  function showAlert(message, isError) {
    alertEl.textContent = message;
    alertEl.className = 'fn-alert ' + (isError ? 'fn-alert-error' : 'fn-alert-success');
    alertEl.style.display = 'block';
  }

  function hideAlert() {
    alertEl.style.display = 'none';
  }

  function setRole(role) {
    if (role === 'TRAINER') {
      trainerFields.style.display = 'block';
      if (specializationInput) specializationInput.removeAttribute('data-optional');
      if (bioInput) bioInput.removeAttribute('data-optional');
    } else {
      trainerFields.style.display = 'none';
      if (specializationInput) specializationInput.value = '';
      if (bioInput) bioInput.value = '';
    }
  }

  roleButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {
      roleButtons.forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
      setRole(btn.getAttribute('data-role'));
    });
  });

  setRole('CLIENT');

  form.addEventListener('submit', function (e) {
    e.preventDefault();
    hideAlert();

    var activeRoleBtn = document.querySelector('.fn-role-group[aria-label="Registration role"] .fn-role-btn.active');
    var role = activeRoleBtn ? activeRoleBtn.getAttribute('data-role') : 'CLIENT';

    var payload = {
      firstName: document.getElementById('firstName').value.trim(),
      lastName: document.getElementById('lastName').value.trim(),
      email: document.getElementById('regEmail').value.trim(),
      password: document.getElementById('regPassword').value,
      phone: document.getElementById('phone').value.trim(),
      role: role
    };

    if (role === 'TRAINER') {
      payload.specialization = (specializationInput && specializationInput.value.trim()) || null;
      payload.bio = (bioInput && bioInput.value.trim()) || null;
    }

    registerBtn.disabled = true;

    fetch(API_BASE + '/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })
      .then(function (res) {
        if (!res.ok) {
          return res.json().then(function (body) {
            throw new Error(body.message || body.error || 'Registration failed');
          }).catch(function (err) {
            if (err instanceof Error) throw err;
            throw new Error('Registration failed');
          });
        }
        return res.text().then(function (body) {
          if (!body || body.trim() === '') return {};
          try {
            return JSON.parse(body);
          } catch (_) {
            return {};
          }
        });
      })
      .then(function () {
        showAlert('Registration successful. You can log in now.', false);
        form.reset();
        setRole('CLIENT');
        roleButtons.forEach(function (b) {
          b.classList.toggle('active', b.getAttribute('data-role') === 'CLIENT');
        });
      })
      .catch(function (err) {
        showAlert(err.message || 'Registration failed.', true);
      })
      .finally(function () {
        registerBtn.disabled = false;
      });
  });
})();
