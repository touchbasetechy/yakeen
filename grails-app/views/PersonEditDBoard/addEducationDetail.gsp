<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-square"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'person', action: 'index')}"
       class="same-line"><i class="fas fa-home embossed-link"></i></a>
    <a
      href="${createLink(controller: 'personEditDBoard', action: 'editEducationDetails', params: [p: params?.p, con: params?.con, op: params?.op])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>

    <h1 class="same-line">Add Education Detail</h1>

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
    <form
      action="${createLink(controller: 'personEditDBoard', action: 'saveNewEducationDetails', params: [p: params?.p, con: params?.con, op: params?.op])}"
      method="post">
      <section class="row colset-2-its justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="name" name="name" placeholder="" required>
          <label for="name" class="form-label">Education</label>
        </div></div>
      </section>
      <section class="row colset-2-its justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="institution" name="institution" placeholder="" required>
          <label for="institution" class="form-label">Institution</label>
        </div></div>
      </section>
      <section class="row colset-2-its justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="institutionLocation" name="institutionLocation" placeholder=""
                 required>
          <label for="institutionLocation" class="form-label">Institution Location</label>
        </div></div>
      </section>
      <section class="row colset-2-its justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="university" name="university" placeholder="" required>
          <label for="university" class="form-label">University</label>
        </div></div>
      </section>

      <section class="row colset-2-its justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="universityLocation" name="universityLocation" placeholder=""
                 required>
          <label for="universityLocation" class="form-label">University Location</label>
        </div></div>
      </section>
      <section class="row colset-2-its justify-content-center">
        <div class="checkbox-wrapper-42">
          <input name="isHighest" id="isHighest" type="checkbox"/>
          <label class="cbx" for="isHighest"></label>
          <label class="lbl" for="isHighest">Is Highest Education</label>
        </div>
      </section>

      <div class="d-flex justify-content-end">
        <button class="btn btn-primary" type="submit"><i class="fas fa-save" aria-hidden="true">&nbsp;&nbsp;Save</i>
        </button>
      </div>
    </form>
  </div>
</div>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {

  });
</script>
</body>
</html>
