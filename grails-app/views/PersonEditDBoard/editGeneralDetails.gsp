<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>

</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-edit"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="form-container">
      <a href="${createLink(controller: 'person', action: 'index')}"
         class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      %{--      <g:if test="${!params?.pt}">--}%
      <a
        href="${createLink(controller: 'PersonEditDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      %{--      </g:if>--}%
      %{--      <g:else>--}%
      %{--        <a--}%
      %{--          href="${createLink(controller: 'editDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"--}%
      %{--          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>--}%
      %{--      </g:else>--}%
      <h4 class="same-line">General Details</h4>
    </div>
    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>
    <section class="row colset-2-its form-container">
      <form
        action="${createLink(controller: 'PersonEditDBoard', action: 'editAndSaveGeneralDetails', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
        method="post">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="firstName" name="firstName" placeholder=""
                 value="${personToEdit?.firstName}" autofocus>
          <label for="firstName" class="form-label">First Name</label>
        </div></div>

        <div class="input-container"><div class="mb-3">
          <input type="text" class="form-control" id="lastName" name="lastName" placeholder=""
                 value="${personToEdit?.lastName}">
          <label for="lastName" class="form-label">Last Name</label>
        </div></div>

        <div class="d-flex justify-content-end">
          <button class="btn btn-primary toolbar-button" type="submit"><i class="fas fa-save"
                                                                          aria-hidden="true">&nbsp;save</i>
          </button>
        </div>
      </form>
    </section>
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
