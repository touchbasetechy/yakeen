<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <asset:stylesheet src="application.css"/>
</head>

<body>
<g:if test="${flash.error}">
  <div class="alert alert-danger">${flash.error}</div>
</g:if>

<form action="${request.contextPath}/login/authenticate" method="POST">
  <input type="hidden" name="${_csrf?.parameterName}" value="${_csrf?.token}"/>

  <div class="form-group">
    <label for="username">Email:</label>
    <input type="text" name="username" id="username" class="form-control"/>
  </div>

  <div class="form-group">
    <label for="password">Password:</label>
    <input type="password" name="password" id="password" class="form-control"/>
    <small><a href="${request.contextPath}/PasswordReset/forgotPassword">Forgot password?</a></small>
  </div>

  <button type="submit" class="btn btn-primary">Login</button>
</form>
</body>
</html>
