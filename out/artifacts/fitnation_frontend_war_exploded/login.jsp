<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="layout/header.jsp" %>

<div id="globalError" class="error-msg" style="display:none"></div>

<form onsubmit="doLogin(event)">
    <div class="form-group">
        <label>Email</label>
        <input type="email" id="email" name="email" placeholder="demo@fitnation.app"/>
        <span class="error-msg" id="emailError">Please enter a valid email</span>
    </div>

    <div class="forgot">
        <a href="#">Forgot?</a>
    </div>

    <div class="form-group">
        <label>Password</label>
        <input type="password" id="password" name="password" placeholder="••••••••••"/>
        <span class="error-msg" id="passwordError">Password is required</span>
    </div>

    <button type="submit" class="btn btn-primary">Login</button>

    <div class="link-row">
        Don't have an account? <a href="register.jsp">Register</a>
    </div>
</form>

<script>
    function validate() {
        let valid = true;
        const email = document.getElementById('email').value.trim();
        const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        document.getElementById('emailError').style.display = emailRegex.test(email) ? 'none' : 'block';
        if (!emailRegex.test(email)) valid = false;

        const password = document.getElementById('password').value;
        document.getElementById('passwordError').style.display = password.length > 0 ? 'none' : 'block';
        if (password.length === 0) valid = false;

        return valid;
    }

    async function doLogin(e) {
        e.preventDefault();
        if (!validate()) return;

        try {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: document.getElementById('email').value.trim(),
                    password: document.getElementById('password').value
                })
            });

            const data = await res.json();

            if (!res.ok) {
                document.getElementById('globalError').textContent = data.message || 'Login failed';
                document.getElementById('globalError').style.display = 'block';
                return;
            }

            localStorage.setItem('fn_token', data.accessToken);  // ✅ accessToken
            localStorage.setItem('fn_role', data.role);

            const map = {
                ADMIN:   '/admin/dashboard.jsp',
                TRAINER: '/trainer/dashboard.jsp',
                USER:    '/user/dashboard.jsp'
            };
            window.location.href = map[data.role] || '/login.jsp';

        } catch (err) {
            document.getElementById('globalError').textContent = 'Something went wrong';
            document.getElementById('globalError').style.display = 'block';
        }
    }
</script>

<%@ include file="layout/footer.jsp" %>
