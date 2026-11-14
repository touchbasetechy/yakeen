<!DOCTYPE html>
<html>
<head>
  <style>
  body {
    font-family: Arial, sans-serif;
    line-height: 1.6;
    color: #333;
  }

  .container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
  }

  .header {
    background-color: #4CAF50;
    color: white;
    padding: 20px;
    text-align: center;
  }

  .content {
    padding: 20px;
    background-color: #f9f9f9;
  }

  .button {
    display: inline-block;
    padding: 12px 24px;
    margin: 20px 0;
    background-color: #4CAF50;
    color: white;
    text-decoration: none;
    border-radius: 4px;
  }

  .footer {
    padding: 20px;
    text-align: center;
    font-size: 12px;
    color: #666;
  }
  </style>
</head>

<body>
<div class="container">
  <div class="header">
    <h1>Welcome to ${appName}!</h1>
  </div>

  <div class="content">
    <p>Hello ${username},</p>

    <p>Thank you for registering! To complete your registration, please activate your account by clicking the button below:</p>

    <div style="text-align: center;">
      <a href="${activationLink}" class="button">Activate Account</a>
    </div>

    <p>Or copy and paste this link into your browser:</p>

    <p style="word-break: break-all;">${activationLink}</p>

    <p><strong>This link will expire in 24 hours.</strong></p>

    <p>If you did not create this account, please ignore this email.</p>
  </div>

  <div class="footer">
    <p>&copy; <script>document.write(new Date().getFullYear());</script> ${appName}. All rights reserved.</p>
  </div>
</div>
</body>
</html>
