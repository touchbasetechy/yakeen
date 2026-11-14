<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Server Error - ${grailsApplication.config.getProperty('info.app.name', 'Matrimony App')}</title>
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

  .error-details {
    background-color: #f8f9fa;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    padding: 20px;
    margin: 20px 0;
    text-align: left;
  }

  .btn-home {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    text-decoration: none;
    border-radius: 5px;
    display: inline-block;
    margin-top: 20px;
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
  <div class="error-code">500</div>

  <div class="error-message">Internal Server Error</div>

  <p>We're sorry, but something went wrong on our end. Our team has been notified and is working to fix the issue.</p>

  <g:if test="${grails.util.Environment.current == grails.util.Environment.DEVELOPMENT}">
    <div class="error-details">
      <h4>Error Details (Development Mode Only):</h4>

      <p><strong>Path:</strong> ${path}</p>

      <p><strong>Status:</strong> ${status}</p>
      <g:if test="${exception}">
        <p><strong>Exception:</strong> ${exception.class.name}</p>

        <p><strong>Message:</strong> ${exception.message}</p>
        <g:if test="${exception.cause}">
          <p><strong>Cause:</strong> ${exception.cause.message}</p>
        </g:if>
      </g:if>
    </div>
  </g:if>

  <g:link uri="/" class="btn-home">Return to Home</g:link>
</div>
</body>
</html>
