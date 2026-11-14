<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Page Not Found - ${grailsApplication.config.getProperty('info.app.name', 'Matrimony App')}</title>
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
    color: #ffc107;
    margin-bottom: 20px;
  }

  .error-message {
    font-size: 24px;
    margin-bottom: 30px;
    color: #6c757d;
  }

  .error-details {
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    padding: 20px;
    margin: 20px 0;
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
    text-align: left;
    margin: 20px 0;
  }

  .suggestions ul {
    list-style-type: disc;
    margin-left: 20px;
  }
  </style>
</head>

<body>
<div class="error-container">
  <div class="error-code">404</div>

  <div class="error-message">Page Not Found</div>

  <p>The page you're looking for doesn't exist or may have been moved.</p>

  <g:if test="${path}">
    <div class="error-details">
      <strong>Requested Path:</strong> ${path}
    </div>
  </g:if>

  <div class="suggestions">
    <h4>What you can do:</h4>
    <ul>
      <li>Check the URL for typing errors</li>
      <li>Go back to the previous page</li>
      <li>Visit our homepage</li>
      <li>Use the navigation menu to find what you're looking for</li>
    </ul>
  </div>

  <div>
    <g:link uri="/" class="btn-home">Go to Homepage</g:link>
    <a href="javascript:history.back()" class="btn-home" style="background-color: #6c757d;">Go Back</a>
  </div>
</div>
</body>
</html>
