<!DOCTYPE html>
<html>
<head>
  <style>
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
    line-height: 1.6;
    color: #1a1a1a;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    padding: 40px 20px;
  }

  .container {
    max-width: 600px;
    margin: 0 auto;
    background: #ffffff;
    border-radius: 16px;
    padding: 48px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  }

  h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1a1a1a;
    margin-bottom: 24px;
    letter-spacing: -0.5px;
  }

  p {
    margin-bottom: 20px;
    color: #4a5568;
    font-size: 16px;
  }

  .button-container {
    text-align: center;
    margin: 36px 0;
  }

  .button {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 16px 40px;
    text-decoration: none;
    border-radius: 12px;
    display: inline-block;
    font-weight: 600;
    font-size: 16px;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
  }

  .button:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  }

  .link-box {
    background: #f7fafc;
    border: 2px dashed #e2e8f0;
    border-radius: 8px;
    padding: 16px;
    word-break: break-all;
    font-size: 14px;
    color: #667eea;
    margin: 20px 0;
  }

  .warning {
    background: #fff5f5;
    border-left: 4px solid #fc8181;
    padding: 16px;
    border-radius: 8px;
    margin: 24px 0;
    font-size: 14px;
  }

  .footer {
    margin-top: 36px;
    padding-top: 24px;
    border-top: 1px solid #e2e8f0;
    color: #718096;
    font-size: 14px;
  }

  .greeting {
    color: #2d3748;
    font-weight: 500;
  }
  </style>
</head>

<body>
<div class="container">
  <h2>🔐 Password Reset Request</h2>

  <p class="greeting">Hello ${userEMail},</p>

  <p>We received a request to reset your password. Click the button below to create a new password:</p>

  <div class="button-container">
    <a href="${resetUrl}" class="button">Reset Password</a>
  </div>

  <p>Or copy and paste this link into your browser:</p>

  <div class="link-box">${resetUrl}</div>

  <div class="warning">
    <strong>⏰ Important:</strong> This link will expire in 24 hours.
  </div>

  <p>If you didn't request this password reset, please ignore this email. Your password will remain unchanged.</p>

  <div class="footer">
    <p>Thanks,<br><strong>Your ${appName} Team</strong></p>
  </div>
</div>
</body>
</html>
