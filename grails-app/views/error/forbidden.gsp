<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Access Forbidden - ${grailsApplication.config.getProperty('info.app.name', 'Matrimony App')}</title>
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
    color: #dc3545;
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

  .suggestions {
    margin: 20px 0;
  }
  </style>
</head>

<body>
<div class="error-container">
  <div class="error-code">403</div>

  <div class="error-message">Access Forbidden</div>

  <p>You don't have permission to access this resource.</p>

  <div class="suggestions">
    <p>This might happen if:</p>
    <ul style="text-align: left; display: inline-block;">
      <li>You need to log in to access this page</li>
      <li>Your account doesn't have the required permissions</li>
      <li>The resource is restricted to certain user roles</li>
    </ul>
  </div>

  <div>
    <sec:ifNotLoggedIn>
      <g:link controller="login" class="btn-home">Login</g:link>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
      <g:link controller="dashboard" class="btn-home">Go to Dashboard</g:link>
    </sec:ifLoggedIn>
    <g:link uri="/" class="btn-home" style="background-color: #6c757d;">Homepage</g:link>
  </div>
</div>
</body>
</html>
