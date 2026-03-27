<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="layout/header.jsp" %>

<div class="role-toggle">
  <button type="button" id="clientBtn" class="active" onclick="switchRole('CLIENT')">Client</button>
  <button type="button" id="trainerBtn" onclick="switchRole('TRAINER')">Trainer</button>
</div>

<form action="/api/auth/register" method="post" onsubmit="return validateRegister()">
  <input type="hidden" id="role" name="role" value="CLIENT"/>

  <div class="form-group">
    <label>First Name</label>
    <input type="text" id="firstName" name="firstName" placeholder="John"/>
    <span class="error-msg" id="firstNameError">First name is required</span>
  </div>

  <div class="form-group">
    <label>Last Name</label>
    <input type="text" id="lastName" name="lastName" placeholder="Doe"/>
    <span class="error-msg" id="lastNameError">Last name is required</span>
  </div>

  <div class="form-group">
    <label>Email</label>
    <input type="email" id="email" name="email" placeholder="demo@fitnation.app"/>
    <span class="error-msg" id="emailError">Please enter a valid email</span>
  </div>

  <div class="form-group">
    <label>Password</label>
    <input type="password" id="password" name="password" placeholder="••••••••••"/>
    <span class="error-msg" id="passwordError">
            Password must be at least 8 characters, contain one uppercase letter, one number, and one special character
        </span>
  </div>

  <div class="form-group">
    <label>Phone</label>
    <input type="text" id="phone" name="phone" placeholder="+1234567890"/>
    <span class="error-msg" id="phoneError">Phone is required</span>
  </div>

  <div class="trainer-fields" id="trainerFields">
    <div class="form-group">
      <label>Specialization</label>
      <input type="text" id="specialization" name="specialization" placeholder="e.g. Yoga, CrossFit"/>
      <span class="error-msg" id="specializationError">Specialization is required for trainers</span>
    </div>

    <div class="form-group">
      <label>Bio</label>
      <textarea id="bio" name="bio" placeholder="Tell us about yourself..."></textarea>
    </div>
  </div>

  <button type="submit" class="btn btn-primary">Register</button>

  <div class="link-row">
    Already have an account? <a href="login.jsp">Login</a>
  </div>
</form>

<script>
  function switchRole(role) {
    document.getElementById('role').value = role;
    const trainerFields = document.getElementById('trainerFields');
    const clientBtn = document.getElementById('clientBtn');
    const trainerBtn = document.getElementById('trainerBtn');

    if (role === 'TRAINER') {
      trainerFields.style.display = 'block';
      trainerBtn.classList.add('active');
      clientBtn.classList.remove('active');
    } else {
      trainerFields.style.display = 'none';
      clientBtn.classList.add('active');
      trainerBtn.classList.remove('active');
    }
  }

  function validateRegister() {
    let valid = true;

    const firstName = document.getElementById('firstName').value.trim();
    const firstNameError = document.getElementById('firstNameError');
    if (firstName.length === 0) {
      firstNameError.style.display = 'block';
      valid = false;
    } else {
      firstNameError.style.display = 'none';
    }

    const lastName = document.getElementById('lastName').value.trim();
    const lastNameError = document.getElementById('lastNameError');
    if (lastName.length === 0) {
      lastNameError.style.display = 'block';
      valid = false;
    } else {
      lastNameError.style.display = 'none';
    }

    const email = document.getElementById('email').value.trim();
    const emailError = document.getElementById('emailError');
    const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRegex.test(email)) {
      emailError.style.display = 'block';
      valid = false;
    } else {
      emailError.style.display = 'none';
    }

    const password = document.getElementById('password').value;
    const passwordError = document.getElementById('passwordError');
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(password)) {
      passwordError.style.display = 'block';
      valid = false;
    } else {
      passwordError.style.display = 'none';
    }

    const phone = document.getElementById('phone').value.trim();
    const phoneError = document.getElementById('phoneError');
    if (phone.length === 0) {
      phoneError.style.display = 'block';
      valid = false;
    } else {
      phoneError.style.display = 'none';
    }

    const role = document.getElementById('role').value;
    if (role === 'TRAINER') {
      const specialization = document.getElementById('specialization').value.trim();
      const specializationError = document.getElementById('specializationError');
      if (specialization.length === 0) {
        specializationError.style.display = 'block';
        valid = false;
      } else {
        specializationError.style.display = 'none';
      }
    }

    return valid;
  }
</script>

<%@ include file="layout/footer.jsp" %>