(function () {
  var roleButtons = document.querySelectorAll('.fn-role-group[aria-label="Registration role"] .fn-role-btn');
  var trainerFields = document.getElementById('trainerFields');
  var roleInput = document.getElementById('roleInput');

  function setRole(role) {
    if (role === 'TRAINER') {
      trainerFields.style.display = 'block';
    } else {
      trainerFields.style.display = 'none';
      var specializationInput = document.getElementById('specialization');
      var bioInput = document.getElementById('bio');
      if (specializationInput) specializationInput.value = '';
      if (bioInput) bioInput.value = '';
    }
    if (roleInput) roleInput.value = role;
  }

  roleButtons.forEach(function (btn) {
    btn.addEventListener('click', function () {
      roleButtons.forEach(function (b) { b.classList.remove('active'); });
      btn.classList.add('active');
      setRole(btn.getAttribute('data-role'));
    });
  });

  setRole('CLIENT');
})();
