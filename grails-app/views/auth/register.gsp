<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="welcome"/>
  <title>Register - ${grailsApplication.metadata.getApplicationName()}</title>
  <glass:gradientHeadingStyles/>
  <glass:greenAnchorStyles/>
  <glass:whiteAnchorStyles/>
  <glass:buttonStyles/>
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

  /* Sign in link styling */
  .signin-link {
    text-align: center;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  .signin-link p {
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

      const registerForm = document.getElementById('registerForm');

      registerForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const firstName = document.querySelector('input[name="firstName"]').value.trim();
        const lastName = document.querySelector('input[name="lastName"]').value.trim();
        const email = document.querySelector('input[name="email"]').value.trim();
        const mobileNumber = document.querySelector('input[name="mobileNumber"]').value.trim();
        const password = document.querySelector('input[name="password"]').value.trim();

        // Client-side validation
        if (!firstName) {
          window.showError('First name is required');
          return;
        }

        if (!lastName) {
          window.showError('Last name is required');
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

        // Show loading state
        const submitBtn = registerForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Creating account...';

        // Submit via AJAX
        fetch('${createLink(controller: 'login', action: 'register')}', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            email: email,
            mobileNumber: mobileNumber,
            password: password,
            ajax: 'true'
          })
        })
          .then(response => response.json())
          .then(data => {
            if (data.success) {
              window.showSuccess(data.message || 'Registration successful!');

              // Redirect after short delay
              setTimeout(() => {
                if (data.redirectUrl) {
                  window.location.href = data.redirectUrl;
                } else {
                  window.location.href = '${createLink(controller: 'auth', action: 'login')}';
                }
              }, 1000);
            } else {
              window.showError(data.message || 'Registration failed');
              submitBtn.disabled = false;
              submitBtn.textContent = originalText;
            }
          })
          .catch(error => {
            console.error('Registration error:', error);
            window.showError('An error occurred. Please try again.');
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
          });
      });
    });
  </script>
</head>

<body>
<glass:glassCard style="width: 450px;">

  <glass:gradientHeading text="Sign Up"/>

  <!-- Registration Form -->
  <form id="registerForm" autocomplete="off">
    <glass:alphaNumericTextField name="firstName" value="${firstName}" label="First Name" required="true"
                                 autofocus="true"/>

    <glass:alphaNumericTextField name="lastName" value="${lastName}" label="Last Name" required="true"/>

    <glass:emailField name="email" value="${email}" label="Email Address" required="true"/>

    <glass:positiveNumericField name="mobileNumber" value="${mobileNumber}" label="Mobile Number (Optional)"/>

    <glass:passwordField name="password" label="Password" required="true"/>

    <glass:primaryButton text="Sign Up" type="submit"/>
  </form>

  <div class="signin-link">
    <p>Already have an account?</p> <glass:whiteAnchor text="Sign in here" controller="auth" action="login"
                                                       wrapper="false"/>
  </div>
</glass:glassCard>
<glass:flashMessages/>
</body>
</html>
