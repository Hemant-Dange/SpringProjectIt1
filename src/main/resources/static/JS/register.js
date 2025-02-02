$(document).ready(function () {

  // Get the current date in YYYY-MM-DD format
  let today = new Date().toISOString().split("T")[0];

  // Set the max attribute for the date input field
  $("#tenantDOB").attr("max", today);

    $('#tenantRegisterForm').on('submit', function (e) {
      e.preventDefault();

      // Email validation
      var email = $('#tenantEmail').val();
      var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
      if (!emailPattern.test(email)) {
        alert('Please enter a valid email address.');
        return;
      }

      // Password match validation
      var password = $('#tenantPassword').val();
      var confirmPassword = $('#tenantConfirmPassword').val();
      if (password !== confirmPassword) {
        alert('Passwords do not match.');
        return;
      }


    // Dynamic password match message
    $('#tenantConfirmPassword').on('input', function () {
      var password = $('#tenantPassword').val();
      var confirmPassword = $(this).val();
      if (password !== confirmPassword) {
        $(this).get(0).setCustomValidity('Passwords do not match.');
      } else {
        $(this).get(0).setCustomValidity('');
      }
    });

    
  });