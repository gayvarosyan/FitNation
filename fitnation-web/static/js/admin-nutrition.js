(function () {
  'use strict';

  document.querySelectorAll('.fn-edit-nutrition').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      document.getElementById('editPlanId').value = d.id || '';
      document.getElementById('editPlanName').value = d.name || '';
      document.getElementById('editCategory').value = d.category || '';
      document.getElementById('editPrice').value = d.price != null ? d.price : '';
      document.getElementById('editDescription').value = d.description || '';
      document.getElementById('editStatus').value = d.status || 'DRAFT';
      new bootstrap.Modal(document.getElementById('editPlanModal')).show();
    });
  });
})();
