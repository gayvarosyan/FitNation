(function () {
  'use strict';

  document.querySelectorAll('.fn-edit-schedule').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      document.getElementById('ecClassId').value = d.classId || '';
      document.getElementById('ecScheduleId').value = d.scheduleId || '';
      document.getElementById('ecName').value = d.name || '';
      document.getElementById('ecDescription').value = d.description || '';
      document.getElementById('ecCapacity').value = d.capacity || '';
      document.getElementById('ecDate').value = d.date || '';
      document.getElementById('ecStart').value = d.start || '';
      document.getElementById('ecEnd').value = d.end || '';
      var sel = document.getElementById('ecTrainer');
      if (sel && d.trainerId) sel.value = String(d.trainerId);
      new bootstrap.Modal(document.getElementById('editClassModal')).show();
    });
  });
})();
