<!doctype html>
<html>
<head>
  <meta name="layout" content="leftslidein"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-user-plus"><span
      class="label-text">Super Admin
    </span></i></div></li>

    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="d-flex justify-content-center">
      <a href="${createLink(controller: 'adminDBoard', action: 'index')}"
         class="same-line"><i class="fas fa-home embossed-link" aria-hidden="true"></i></a>

      <a
        href="${createLink(controller: 'adminDBoard', action: 'listPersonTypeForAdd', params: [p: "${params?.id}", ap: true])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Add User</h1>
    </div>
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
    <section class="row d-flex justify-content-center">
      <form
        action="${createLink(controller: 'AdminDBoard', action: 'savePerson', params: [id: "${params?.id}", ac: params?.ac])}"
        method="post">
        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control " id="firstName" name="firstName" placeholder="" autofocus
                 required>
          <label for="firstName" class="form-label">First Name</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="lastName" name="lastName" placeholder="" required>
          <label for="lastName" class="form-label">Last Name</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="email" class="form-control" id="eMail" name="eMail" placeholder="" required>
          <label for="eMail" class="form-label">Email</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="password" class="form-control" id="password" name="password" placeholder="" required>
          <label for="password" class="form-label">Password</label>
        </div></div>

        <div class="d-flex justify-content-end">
          <button type="submit" class="btn btn-primary">Save</button>
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
  });
</script>
