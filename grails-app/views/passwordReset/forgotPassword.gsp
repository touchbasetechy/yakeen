<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="welcome"/>
  <title>Forgot Password</title>
  <glass:glassCardStyles/>
  <glass:gradientHeadingStyles/>
  <glass:greenAnchorStyles/>
  <glass:whiteAnchorStyles/>
  <glass:textInputStyles/>
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

  /* Info text styling */
  .info-text {
    color: var(--text-secondary);
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 24px;
    text-align: center;
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
      const forgotPasswordForm = document.getElementById('forgotPasswordForm');

      forgotPasswordForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const email = document.querySelector('input[name="email"]').value.trim();

        // Client-side validation
        if (!email) {
          window.showError('Email is required');
          return;
        }

        // Basic email format validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
          window.showError('Please enter a valid email address');
          return;
        }

        // Show loading state
        const submitBtn = forgotPasswordForm.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Sending...';

        // Submit via AJAX
        fetch('${createLink(controller: 'passwordReset', action: 'requestReset')}', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify({
            email: email,
            ajax: 'true'
          })
        })
          .then(response => response.json())
          .then(data => {
            if (data.success) {
              window.showSuccess(data.message || 'Password reset link sent to your email');

              // Clear the form
              forgotPasswordForm.reset();

              // Redirect after short delay if needed
              if (data.redirectUrl) {
                setTimeout(() => {
                  window.location.href = data.redirectUrl;
                }, 2000);
              }

              submitBtn.disabled = false;
              submitBtn.textContent = originalText;
            } else {
              window.showError(data.message || 'Failed to send reset link');
              submitBtn.disabled = false;
              submitBtn.textContent = originalText;
            }
          })
          .catch(error => {
            console.error('Password reset error:', error);
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

  <glass:gradientHeading text="Forgot Password"/>

  <p class="info-text">Enter your email address, and we'll send you a link to reset your password.</p>

  <!-- Forgot Password Form -->
  <form id="forgotPasswordForm" autocomplete="off">
    <glass:emailField name="email" value="${email}" label="Email Address" required="true" autofocus="true"/>

    <glass:primaryButton text="Send Reset Link" type="submit"/>
  </form>

  <div class="signin-link">
    <p>Remember your password?</p> <glass:whiteAnchor text="Sign in here" controller="auth" action="login"
                                                      wrapper="false"/>
  </div>
</glass:glassCard>
<glass:flashMessages/>
</body>
</html>
