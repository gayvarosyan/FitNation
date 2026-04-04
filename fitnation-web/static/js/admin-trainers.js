(function () {
  'use strict';

  document.querySelectorAll('.fn-edit-trainer').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      document.getElementById('editTrainerId').value = d.id || '';
      document.getElementById('editFirstName').value = d.first || '';
      document.getElementById('editLastName').value = d.last || '';
      document.getElementById('editPassword').value = '';
      document.getElementById('editPhone').value = d.phone || '';
      document.getElementById('editStatus').value = d.status || 'ACTIVE';
      document.getElementById('editSpecialization').value = d.spec || '';
      document.getElementById('editBio').value = d.bio || '';
      var modal = new bootstrap.Modal(document.getElementById('editTrainerModal'));
      modal.show();
    });
  });
})();
