<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Welcome - ${grailsApplication.metadata.getApplicationName()}
  </title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <style>
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  body {
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
    overflow-x: hidden;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    position: relative;
  }

  /* Animated background */
  .bg-animation {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    overflow: hidden;
  }

  .floating-shape {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.5;
    animation: float 20s infinite ease-in-out;
  }

  .shape-1 {
    width: 300px;
    height: 300px;
    background: rgba(255, 107, 107, 0.4);
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  .shape-2 {
    width: 400px;
    height: 400px;
    background: rgba(72, 219, 251, 0.4);
    top: 50%;
    right: 10%;
    animation-delay: 5s;
  }

  .shape-3 {
    width: 250px;
    height: 250px;
    background: rgba(253, 203, 110, 0.4);
    bottom: 10%;
    left: 30%;
    animation-delay: 10s;
  }

  @keyframes float {
    0%, 100% {
      transform: translate(0, 0) scale(1);
    }
    33% {
      transform: translate(50px, -50px) scale(1.1);
    }
    66% {
      transform: translate(-50px, 50px) scale(0.9);
    }
  }

  .container {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
  }

  /* Glassmorphic hero section */
  .hero {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    border-radius: 30px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    padding: 60px 50px;
    margin-bottom: 50px;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.2);
    text-align: center;
    animation: fadeInUp 0.8s ease-out;
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .hero h1 {
    font-size: 3.5rem;
    font-weight: 700;
    color: white;
    margin-bottom: 20px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    line-height: 1.2;
  }

  .hero .lead {
    font-size: 1.3rem;
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: 30px;
    font-weight: 400;
  }

  .divider {
    width: 100px;
    height: 3px;
    background: linear-gradient(90deg, transparent, white, transparent);
    margin: 30px auto;
  }

  .hero-description {
    font-size: 1.1rem;
    color: rgba(255, 255, 255, 0.85);
    margin-bottom: 40px;
    max-width: 700px;
    margin-left: auto;
    margin-right: auto;
  }

  /* Button section */
  .button-group {
    display: flex;
    gap: 20px;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 30px;
  }

  .btn {
    padding: 14px 32px;
    border-radius: 50px;
    font-size: 1rem;
    font-weight: 600;
    text-decoration: none;
    cursor: pointer;
    transition: all 0.3s ease;
    border: none;
    display: inline-flex;
    align-items: center;
    gap: 10px;
    position: relative;
    overflow: hidden;
  }

  .btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
  }

  .btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);
  }

  .btn-secondary {
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border: 2px solid rgba(255, 255, 255, 0.3);
    color: white;
  }

  .btn-secondary:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
  }

  .separator {
    color: rgba(255, 255, 255, 0.6);
    font-weight: 600;
    font-size: 0.9rem;
  }

  /* OAuth buttons */
  .oauth-buttons {
    display: flex;
    gap: 15px;
    justify-content: center;
    flex-wrap: wrap;
  }

  .oauth-btn {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    padding: 8px 24px;
    border-radius: 50px;
    border: 1px solid rgba(255, 255, 255, 0.3);
    text-decoration: none;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }

  .oauth-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }

  .google-btn {
    color: #3c4043;
  }

  .github-btn {
    color: #24292e;
    background: rgba(36, 41, 46, 0.95);
    color: white;
  }

  .github-btn:hover {
    background: #2c3e50;
  }

  .oauth-icon {
    width: 20px;
    height: 20px;
  }

  /* Feature cards */
  .features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 25px;
    animation: fadeInUp 0.8s ease-out 0.2s backwards;
  }

  .feature-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    border-radius: 25px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    padding: 40px 30px;
    text-align: center;
    transition: all 0.3s ease;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    position: relative;
    overflow: hidden;
  }

  .feature-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .feature-card:hover::before {
    opacity: 1;
  }

  .feature-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 45px 0 rgba(31, 38, 135, 0.25);
    border-color: rgba(255, 255, 255, 0.4);
  }

  .feature-icon {
    font-size: 3rem;
    margin-bottom: 20px;
    display: inline-block;
    position: relative;
    z-index: 1;
  }

  .feature-card h3 {
    color: white;
    font-size: 1.5rem;
    margin-bottom: 15px;
    font-weight: 600;
    position: relative;
    z-index: 1;
  }

  .feature-card p {
    color: rgba(255, 255, 255, 0.85);
    font-size: 1rem;
    line-height: 1.6;
    position: relative;
    z-index: 1;
  }

  /* Logged in state */
  .welcome-message {
    font-size: 1.3rem;
    color: white;
    margin-bottom: 30px;
    font-weight: 500;
  }

  .username {
    color: #ffd700;
    font-weight: 700;
  }

  /* Responsive */
  @media (max-width: 768px) {
    .hero {
      padding: 40px 30px;
    }

    .hero h1 {
      font-size: 2.5rem;
    }

    .hero .lead {
      font-size: 1.1rem;
    }

    .button-group {
      flex-direction: column;
      gap: 15px;
    }

    .oauth-buttons {
      flex-direction: column;
      width: 100%;
    }

    .oauth-btn {
      width: 100%;
      justify-content: center;
    }

    .separator {
      margin: 10px 0;
    }

    .features {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 480px) {
    .container {
      padding: 20px 15px;
    }

    .hero h1 {
      font-size: 2rem;
    }

    .btn {
      width: 100%;
      justify-content: center;
    }
  }
  </style>
</head>

<body>
<!-- Animated background -->
<div class="bg-animation">
  <div class="floating-shape shape-1"></div>

  <div class="floating-shape shape-2"></div>

  <div class="floating-shape shape-3"></div>
</div>

<div class="container">
  <!-- Hero Section -->
  <div class="hero">
    <h1>Welcome to ${grailsApplication.metadata.getApplicationName()}</h1>

    <p class="lead">Find your perfect match with our comprehensive matrimonial platform.</p>

    <div class="divider"></div>

    <!-- Not Logged In State -->
    <div class="not-logged-in">
      <p class="hero-description">Join thousands of happy couples who found their soulmate through our platform.</p>

      <div class="button-group">
        <a href="/auth/register" class="btn btn-primary">
          <span>✨</span>
          <span>Get Started</span>
        </a>
        <g:if test="${!session.loggedInIdentities}">
          <a href="${createLink(controller: 'auth', action: 'login')}" class="btn btn-secondary">
            <span>🔐</span>
            <span>Sign In</span>
          </a>
        </g:if>
        <g:else>
          <a href="${createLink(controller: 'login', action: 'addAccount')}" class="btn btn-secondary">
            <span>🔐</span>
            <span>Sign In</span>
          </a>
        </g:else>
      </div>

      <div class="separator">OR</div>

      <div class="oauth-buttons">
        <a href="/oauth2/google" class="oauth-btn google-btn">
          <svg class="oauth-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
              fill="#4285F4"/>
            <path
              d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
              fill="#34A853"/>
            <path
              d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
              fill="#FBBC05"/>
            <path
              d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
              fill="#EA4335"/>
          </svg>
          <span>Sign in with Google</span>
        </a>
        <a href="/oauth2/github" class="oauth-btn github-btn">
          <svg class="oauth-icon" viewBox="0 0 24 24" fill="currentColor">
            <path
              d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
          </svg>
          <span>Sign in with GitHub</span>
        </a>
      </div>
    </div>

    <!-- Logged In State (uncomment to use) -->
    <!-- <div class="logged-in">
        <p class="welcome-message">Welcome back, <span class="username">[Username]</span>!</p>
        <a href="/dashboard" class="btn btn-primary">
          <span>🏠</span>
          <span>Go to Dashboard</span>
        </a>
      </div> -->
  </div>

  <!-- Features Section -->
  <div class="features">
    <div class="feature-card">
      <div class="feature-icon">💝</div>

      <h3>Find Your Match</h3>

      <p>Browse through thousands of verified profiles to find your perfect partner.</p>
    </div>

    <div class="feature-card">
      <div class="feature-icon">🔒</div>

      <h3>Secure & Private</h3>

      <p>Your privacy is our priority. All profiles are verified and secure.</p>
    </div>

    <div class="feature-card">
      <div class="feature-icon">🎉</div>

      <h3>Success Stories</h3>

      <p>Join thousands of couples who found love through our platform.</p>
    </div>
  </div>
</div>

<script>
  // Add smooth scrolling and animations
  document.addEventListener('DOMContentLoaded', function () {
    localStorage.setItem("isPopOverShown", "false");
    localStorage.setItem("isAdminPopOverShown", "false");

    // Add entrance animations to elements
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = '1';
          entry.target.style.transform = 'translateY(0)';
        }
      });
    }, {
      threshold: 0.1
    });

    document.querySelectorAll('.feature-card').forEach(card => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(20px)';
      card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
      observer.observe(card);
    });
  });
</script>
<glass:simplefooter companyName="Murad Matrimony"/>

</body>
</html>
