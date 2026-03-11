(function () {
  const API_BASE = '/api/auth';

  const form = document.getElementById('loginForm');
  const loginBtn = document.getElementById('loginBtn');
  const alertEl = document.getElementById('loginAlert');

  function showAlert(message, isError) {
    alertEl.textContent = message;
    alertEl.className = 'fn-alert ' + (isError ? 'fn-alert-error' : 'fn-alert-success');
    alertEl.style.display = 'block';
  }

  function hideAlert() {
    alertEl.style.display = 'none';
  }

  form.addEventListener('submit', function (e) {
    e.preventDefault();
    hideAlert();

    var payload = {
      email: document.getElementById('email').value.trim(),
      password: document.getElementById('password').value
    };

    loginBtn.disabled = true;

    fetch(API_BASE + '/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })
      .then(function (res) {
        if (!res.ok) {
          return res.json().then(function (body) {
            throw new Error(body.message || body.error || 'Login failed');
          }).catch(function (err) {
            if (err instanceof Error) throw err;
            throw new Error('Login failed');
          });
        }
        return res.json();
      })
      .then(function (data) {
        showAlert('Login successful.', false);
        if (data.token) {
          localStorage.setItem('token', data.token);
        }
        if (data.redirectUrl) {
          window.location.href = data.redirectUrl;
        } else if (data.role === 'ADMIN' || data.role === 'SUPER_ADMIN') {
          window.location.href = '/admin-trainers.html';
        } else {
          window.location.href = '/';
        }
      })
      .catch(function (err) {
        showAlert(err.message || 'Login failed.', true);
      })
      .finally(function () {
        loginBtn.disabled = false;
      });
  });
})();
