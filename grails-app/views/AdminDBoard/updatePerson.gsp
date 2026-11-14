<!doctype html>
<html>
<head>
  <meta name="layout" content="signinsignup"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-edit"><span class="label-text">Super Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="form-container">
      <a href="${createLink(controller: 'adminDBoard', action: 'listPerson')}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Edit User</h1>
    </div>    <g:if test="${flash.error}">
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
      <form action="${createLink(controller: 'AdminDBoard', action: 'updateAndSavePerson')}" method="post">
        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="firstName" name="firstName" placeholder=""
                 value="${session?.person?.firstName}" autofocus
                 required>
          <label for="firstName" class="form-label">First Name</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="lastName" name="lastName" placeholder=""
                 value="${session?.person?.lastName}" required>
          <label for="lastName" class="form-label">Last Name</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="email" class="form-control" id="eMail" name="eMail" placeholder=""
                 value="${session?.person?.eMail}" required>
          <label for="eMail" class="form-label">Email</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="password" class="form-control" id="password" name="password" placeholder=""
                 value="${session?.person?.password}" required>
          <label for="password" class="form-label">Password</label>
        </div></div>
        <button type="submit" class="btn btn-primary">Save</button>
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
  });
</script>
