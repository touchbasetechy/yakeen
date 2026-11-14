<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Welcome to ${appName}</title>
  <style>
  body {
    margin: 0;
    padding: 0;
    font-family: 'Arial', 'Helvetica', sans-serif;
    background-color: #f4f4f4;
  }

  .container {
    max-width: 600px;
    margin: 0 auto;
    background-color: #ffffff;
  }

  .header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 40px 20px;
    text-align: center;
  }

  .header h1 {
    color: #ffffff;
    margin: 0;
    font-size: 28px;
  }

  .content {
    padding: 40px 30px;
  }

  .greeting {
    font-size: 20px;
    color: #333333;
    margin-bottom: 20px;
  }

  .message {
    color: #666666;
    line-height: 1.6;
    margin-bottom: 30px;
  }

  .cta-button {
    display: inline-block;
    padding: 15px 35px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    text-decoration: none;
    border-radius: 5px;
    font-weight: bold;
    margin: 10px 0;
  }

  .links-section {
    background-color: #f9f9f9;
    padding: 20px;
    border-radius: 5px;
    margin: 30px 0;
  }

  .links-section h3 {
    color: #333333;
    margin-top: 0;
  }

  .link-item {
    margin: 15px 0;
  }

  .link-item a {
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
  }

  .footer {
    background-color: #f4f4f4;
    padding: 30px;
    text-align: center;
    color: #999999;
    font-size: 14px;
  }

  .footer a {
    color: #667eea;
    text-decoration: none;
  }
  </style>
</head>

<body>
<div class="container">
  <div class="header">
    <h1>🎉 Welcome to ${appName}!</h1>
  </div>

  <div class="content">
    <div class="greeting">
      Hello ${firstName},
    </div>

    <div class="message">
      <p>Welcome aboard! We're thrilled to have you join the ${appName} community.</p>

      <p>Your account has been successfully activated, and you're now ready to start your journey to finding your perfect match. Our platform is designed to help you connect with like-minded individuals who share your values and aspirations.</p>

      <p style="text-align: center; margin: 30px 0;">
        <a href="${dashboardUrl}" class="cta-button">Get Started →</a>
      </p>
    </div>

    <div class="links-section">
      <h3>Quick Links to Get You Started:</h3>

      <div class="link-item">
        <strong>📝 <a href="${profileUrl}">Complete Your Profile</a></strong><br>
        <span style="color: #666; font-size: 14px;">Add photos and details to help others get to know you better</span>
      </div>

      <div class="link-item">
        <strong>💝 <a href="${dashboardUrl}/matches">Browse Matches</a></strong><br>
        <span style="color: #666; font-size: 14px;">Discover profiles that match your preferences</span>
      </div>

      <div class="link-item">
        <strong>⚙️ <a href="${dashboardUrl}/settings">Customize Preferences</a></strong><br>
        <span style="color: #666; font-size: 14px;">Set your search criteria and notification preferences</span>
      </div>

      <div class="link-item">
        <strong>💬 <a href="${supportUrl}">Get Support</a></strong><br>
        <span style="color: #666; font-size: 14px;">Have questions? Our team is here to help!</span>
      </div>
    </div>

    <div class="message">
      <p><strong>Tips for Success:</strong></p>
      <ul style="color: #666;">
        <li>Complete your profile with genuine information</li>
        <li>Upload clear, recent photos</li>
        <li>Be respectful and authentic in your interactions</li>
        <li>Take your time getting to know potential matches</li>
      </ul>
    </div>
  </div>

  <div class="footer">
    <p>Need help? <a href="${supportUrl}">Contact our support team</a></p>

    <p style="margin-top: 20px;">
      © ${String.format('%tY', new Date())} ${appName}. All rights reserved.

    </p>

    <p style="font-size: 12px; margin-top: 15px;">
      You're receiving this email because you created an account at ${appName}.
    </p>
  </div>
</div>
</body>
</html>
