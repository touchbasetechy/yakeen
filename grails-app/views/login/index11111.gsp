<!DOCTYPE html>
<html>
<head>
  <meta name="layout" name="main"/>
</head>

<body>
<div class="container">
  <g:if test='${flash.message}'>
    <div class="alert alert-danger">${flash.message}</div>
  </g:if>

  <form action="${postUrl ?: '/login/authenticate'}" method="POST" id="loginForm" autocomplete="off">
    <div class="form-group">
      <label for="username">Email</label>
      <input type="text" class="form-control" name="${usernameParameter ?: 'username'}" id="username"
             autocapitalize="none"/>
    </div>

    <div class="form-group">
      <label for="password">Password</label>
      <input type="password" class="form-control" name="${passwordParameter ?: 'password'}" id="password"/>
    </div>

    <div class="form-group form-check">
      <input type="checkbox" class="form-check-input" name="${rememberMeParameter ?: 'remember-me'}" id="remember_me"
             <g:if test='${hasCookie}'>checked="checked"</g:if>/>
      <label class="form-check-label" for="remember_me">Remember me</label>
    </div>

    <button type="submit" class="btn btn-primary">Login</button>
  </form>
</div>
</body>
</html>
