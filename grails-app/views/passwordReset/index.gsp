<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <h1>Reset Password</h1>
    <section class="row colset-2-its">
      <g:if test="${flash.error}">
        <div class="alert alert-danger" style="display: block">${flash.error}</div>
      </g:if>
      <g:if test="${flash.message}">
        <div class="alert alert-success" style="display: block">${flash.message}</div>
      </g:if>
    </section>
    <section class="row colset-2-its">

      <form action="${createLink(controller: 'PasswordReset', action: 'sendEmail')}" method="post">
        <div class="mb-3">
          <label for="eMail" class="form-label">Username</label>
          <input type="email" class="form-control" id="eMail" name="eMail" placeholder="EMail Id" required>
        </div>

        <button type="submit" class="btn btn-primary mb-3">Send Mail</button>
      </form>

    </section>
  </div>
</div>

</body>
</html>
