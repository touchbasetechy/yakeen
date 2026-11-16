<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="welcome"/>
  <title>Sign In - ${grailsApplication.metadata.getApplicationName()}</title>
  <glass:gradientHeadingStyles/>
  <glass:greenAnchorStyles/>
  <glass:whiteAnchorStyles/>
  <glass:buttonStyles/>
  <glass:checkboxStyles/>
  <glass:snackbarStyles/>
  <style>
  @import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&family=Space+Mono:wght@400;700&display=swap');

  /* Form styling */

  .input-container {
    margin-bottom: 28px;
    position: relative;
  }

  .form-group {
    position: relative;
  }

  .position-absolute {
    position: absolute;
  }

  /* Forgot password link - smaller size */
  .position-absolute .green-anchor-link {
    font-size: 12px;
    font-weight: 500;
  }

  /* Sign up link styling */
  .signup-link {
    text-align: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  .signup-link p {
    color: var(--text-secondary);
    font-size: 14px;
    margin: 0;
    display: inline;
  }
  </style>

  <script>
    document.addEventListener('DOMContentLoaded', function () {
      localStorage.setItem("isPopOverShown", "false");
      localStorage.setItem("isAdminPopOverShown", "false");

      const loginForm = document.getElementById('loginForm');

      loginForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const email = document.querySelector('input[name="email"]').value.trim();
        const password = document.querySelector('input[name="password"]').value.trim();

        // Client-side validation
        if (!email && !password) {
          window.showError('Email and password are required');
          return;
        }

        if (!email) {
          window.showError('Email is required');
          return;
        }

        if (!password) {
          window.showError('Password is required');
          return;
        }

        // Show loading state (optional)
        const submitBtn = loginForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Signing in...';

        // Submit via AJAX
        fetch('${createLink(controller: 'login', action: 'authenticate')}', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify({
            username: email,
            password: password,
            ajax: 'true'
          })
        })
          .then(response => response.json())
          .then(data => {
            if (data.success) {
              // window.showSuccess(data.message || 'Login successful!');

              // Redirect after short delay
              // setTimeout(() => {
              //   if (data.redirectUrl) {

              window.location.href = data.redirectUrl;
              //   } else {
              //     window.location.reload();
              //   }
              // }, 1000);
            } else {
              window.showError(data.message || 'Login failed');
              submitBtn.disabled = false;
              submitBtn.textContent = originalText;
            }
          })
          .catch(error => {
            console.error('Login error:', error);
            window.showError('An error occurred. Please try again.');
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
          });
      });
    });
  </script>
</head>

<body>
<glass:glassCard style="width: 400px;">
  <glass:gradientHeading text="Sign In"/>

  <!-- Login Form -->
  <form id="loginForm" autocomplete="off">
    <glass:emailField name="email" value="${email}" label="Email Address" autofocus="true"/>

    <div class="input-container">
      <glass:simplePasswordField name="password" label="Password"/>
      <div class="position-absolute" style="right: 0; bottom: -25px;">
        <glass:greenAnchor text="Forgot Password?" controller="passwordReset" action="forgotPassword"/>
      </div>
    </div>

    <div class="form-group mt-4">
      <glass:checkbox name="remember-me" label="Remember me" checked="true"/>
    </div>

    <glass:primaryButton text="Sign In" type="submit"/>
  </form>

  <div class="signup-link">
    <p>Don't have an account?</p> <glass:whiteAnchor text="Sign up here" controller="auth" action="register"
                                                     wrapper="false"/>
  </div>
</glass:glassCard>
<glass:flashMessages/>
</body>
</html>
