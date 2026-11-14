<!doctype html>
<html>
<head>
  <meta name="layout" content="signinsignup"/>
</head>

<body>
<div id="content" role="main">
  <div class="container">
    <h1 class="form-container">Sign Up</h1>
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
    <section class="row colset-2-its form-container">
      <form action="${createLink(controller: 'SignUp', action: 'save')}" method="post">
        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="firstName" name="firstName" placeholder="" autofocus required>
          <label for="firstName" class="form-label">First Name</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="lastName" name="lastName" placeholder="" required>
          <label for="lastName" class="form-label">Last Name</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="email" class="form-control" id="eMail" name="eMail" placeholder="" required>
          <label for="email" class="form-label">Email Id</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="password" class="form-control" id="password" name="password" placeholder="" required>
          <label for="password" class="form-label">Password</label>
        </div></div>

        <div class="mb-3">
          <p class="tiny-text same-line">Already have an account? <g:link controller="SignIn"
                                                                          action="index">Sign In</g:link></p>

          <div class="same-line"><div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-primary">Sign Up</button>
          </div></div>

        </div>

      </form>
    </section>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);

    const floatingInputs = document.querySelectorAll('.form-floating');

    floatingInputs.forEach(input => {
      const inputField = input.querySelector('.form-control');
      inputField.addEventListener('input', (event) => {
        if (event.target.value.trim() !== '') {
          input.classList.add('filled');
        } else {
          input.classList.remove('filled');
        }
      });
    });
  });
</script>

</body>
</html>
