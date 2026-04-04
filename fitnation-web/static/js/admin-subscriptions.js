(function () {
  'use strict';

  function setSelect(id, value) {
    var el = document.getElementById(id);
    if (!el) return;
    if (value === undefined || value === null || value === 'null' || value === '') {
      el.value = '';
    } else {
      el.value = String(value);
    }
  }

  document.querySelectorAll('.fn-edit-catalog').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      document.getElementById('ecPlanId').value = d.id || '';
      document.getElementById('ecName').value = d.name || '';
      document.getElementById('ecDuration').value = d.duration || '';
      document.getElementById('ecPrice').value = d.price || '';
      document.getElementById('ecDesc').value = d.description || '';
      setSelect('ecNut', d.nutrition);
      setSelect('ecTr', d.trainer);
      setSelect('ecGc', d.groupclass);
      new bootstrap.Modal(document.getElementById('editPlanModal')).show();
    });
  });

  document.querySelectorAll('.fn-edit-membership').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      document.getElementById('umMembershipId').value = d.id || '';
      document.getElementById('umMembershipTypeId').value = d.typeId || '';
      document.getElementById('umStart').value = d.start || '';
      document.getElementById('umEnd').value = d.end || '';
      document.getElementById('umStatus').value = d.status || 'ACTIVE';
      setSelect('umNut', d.nutrition);
      setSelect('umTr', d.trainer);
      setSelect('umGc2', d.groupclass);
      new bootstrap.Modal(document.getElementById('editMembershipModal')).show();
    });
  });
})();
