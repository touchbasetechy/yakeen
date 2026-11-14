<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Reset Password</title>
</head>

<body>
<div id="content" role="main">
  <div class="container">
    <div class="row d-flex justify-content-center">
      <div class="col-md-6">
        <h3 class="text-center">Reset Password</h3>

        <g:if test="${flash.message}">
          <div class="alert alert-danger">${flash.message}</div>
        </g:if>

        <form action="${createLink(controller: 'passwordReset', action: 'updatePassword')}" method="POST">
          <input type="hidden" name="token" value="${token}"/>

          <div class="input-container"><div class="form-group">
            <input type="password" class="form-control" name="password" id="password" placeholder="" required
                   autofocus/>
            <label for="password" class="form-label">New Password</label>
          </div></div>

          <div class="input-container"><div class="form-group">
            <input type="password" class="form-control" name="passwordConfirm" id="passwordConfirm" placeholder=""
                   required/>
            <label for="passwordConfirm" class="form-label">Confirm New Password</label>
          </div></div>

          <button type="submit" class="btn btn-primary btn-block">Reset Password</button>
        </form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
