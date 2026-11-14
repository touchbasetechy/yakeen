<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Unauthorized Access - ${grailsApplication.config.getProperty('info.app.name', 'Matrimony App')}</title>
  <style>
  .error-container {
    max-width: 800px;
    margin: 50px auto;
    padding: 20px;
    text-align: center;
  }

  .error-code {
    font-size: 72px;
    font-weight: bold;
    color: #fd7e14;
    margin-bottom: 20px;
  }

  .error-message {
    font-size: 24px;
    margin-bottom: 30px;
    color: #6c757d;
  }

  .btn-home {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    text-decoration: none;
    border-radius: 5px;
    display: inline-block;
    margin: 10px;
  }

  .btn-home:hover {
    background-color: #0056b3;
    text-decoration: none;
    color: white;
  }
  </style>
</head>

<body>
<div class="error-container">
  <div class="error-code">401</div>

  <div class="error-message">Unauthorized Access</div>

  <p>You need to authenticate to access this resource.</p>

  <p>Please log in with your credentials to continue.</p>

  <div>
    <g:link controller="login" class="btn-home">Login</g:link>
    <g:link controller="auth" action="register" class="btn-home" style="background-color: #28a745;">Register</g:link>
    <g:link uri="/" class="btn-home" style="background-color: #6c757d;">Homepage</g:link>
  </div>
</div>
</body>
</html>
