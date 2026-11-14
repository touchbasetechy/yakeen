<!doctype html>
<html>
<head>
  <meta name="layout" content="rightslidein"/>
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
      <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
         class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      <a href="${createLink(controller: 'adminDBoard', action: 'listPersonType')}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Edit User Type</h1>
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

    <form action="${createLink(controller: 'AdminDBoard', action: 'updateAndSavePersonType')}" method="post">
      <section class="row colset-2-its  form-container">

        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="personTypeName" name="personTypeName" placeholder=""
                 value="${session?.personType?.personTypeName}" autofocus required>
          <label for="personTypeName" class="form-label">User Type</label>
        </div></div>
      </section>
      <section class="row colset-2-its  form-container">
        <div class="d-flex justify-content-end">

          <button class=" btn btn-primary toolbar-button" type="submit"><span class="fas fa-angle-right"
                                                                              aria-hidden="true">&nbsp;save</span>
          </button>
        </div>
      </section>
    </form>
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);
  });
</script>
</body>
</html>
