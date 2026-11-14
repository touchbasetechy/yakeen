<!doctype html>
<html>
<head>
  <meta name="layout" content="signinsignup.gsp"/>
</head>

<body>

<div id="content" role="main">
  <div class="container">
    <section class="row colset-2-its form-container">
      <h1>Sign In</h1>
      <g:if test="${flash.error}">
        <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
          ${flash.error}
        </div>
      </g:if>
      <g:if test="${flash.message}">
        <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
          ${flash.message}
        </div>
      </g:if>
      <form action="${createLink(controller: 'signIn', action: 'login')}" method="post">
        <div class="mb-3"><div class="form-group"><div class="input-container">
          <input type="email" class="form-control" id="eMail" name="eMail" placeholder=""
                 required autofocus>
          <label for="eMail" class="form-label">Email Id</label>
        </div></div></div>

        <div class="mb-3"><div class="form-group"><div class="input-container">
          <input type="password" class="form-control" id="password" name="password" placeholder="" required>
          <label for="password" class="form-label">Password</label>

          <div class="d-flex justify-content-end"><div class="forgot-password">
            <a
              href="${createLink(controller: 'passwordReset', action: 'forgotPassword', params: ['emailId': 'value1'])}"
              id="forgotPassword"
              class="btn btn-link">Forgot password?</a>
          </div></div>
        </div></div></div>

        <div class="mb-3">
          <p class="tiny-text same-line">Don't have an account? <g:link controller="SignUp"
                                                                        action="index">Sign Up</g:link></p>

          <div class="same-line"><div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-primary">Sign In</button>
          </div></div>

        </div>
      </form>
    </section>
    %{--    <g:link controller="PasswordReset" action="sendEmail">forgot password</g:link>--}%

  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    localStorage.setItem('userPopOverShown', "false");
    localStorage.setItem('adminPopOverShown', "false");
    localStorage.setItem('superPopOverShown', "false");
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);
  });
  %{--const emailId = document.getElementById('eMail').value;--}%
  %{--const linkElement = document.getElementById('a[name="forgotPassword"]');--}%
  %{--linkElement.href += `?emailId=${emailId.encodeAsURL()}`;--}%
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
