<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-hat-wizard"><span
      class="label-text">Super Admin</span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
       class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a href="${createLink(controller: 'SuperAdmin', action: 'updateAddressDetails')}"
       class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

    <h1 class="same-line">Add Policies</h1>
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
    <form action="${createLink(controller: 'SuperAdmin', action: 'savePolicies')}"
          method="post">

      <section class="row colset-2-its">
        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.policies?.isActive == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isActive" name="isActive" checked>
          </g:if>
          <g:if test="${session?.personToEdit?.policies?.isActive == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isActive" name="isActive">
          </g:if>
          <label class="form-check-label" for="isActive">Active</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.policies?.isBlocked == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isBlocked" name="isBlocked" checked>
          </g:if>
          <g:if test="${session?.personToEdit?.policies?.isBlocked == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isBlocked" name="isBlocked">
          </g:if>
          <label class="form-check-label" for="isBlocked">Blocked</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.policies?.isOnHold == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isOnHold" name="isOnHold" checked>
          </g:if>
          <g:if test="${session?.personToEdit?.policies?.isOnHold == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isOnHold" name="isOnHold">
          </g:if>
          <label class="form-check-label" for="isOnHold">On Hold</label>
        </div></div>
      </section>
      %{--      <section class="row colset-2-its">--}%
      <div class="d-flex justify-content-end">
        <button class="btn btn-primary toolbar-button" type="submit"><span class="fas fa-angles-right"
                                                                           aria-hidden="true">&nbsp;Save</span>
        </button>
      </div>
      %{--      </section>--}%
    </form>
  </div>
</div>
<script type="text/javascript">
  <!--row 1-->
  const isActive = document.getElementById('isActive');
  const isBlocked = document.getElementById('isBlocked');
  const isOnHold = document.getElementById('isOnHold');

  isActive.addEventListener('click', () => {
    isActive.value = "on"
    isBlocked.checked = false;
    isOnHold.checked = false;
  });

  isBlocked.addEventListener('click', () => {
    isBlocked.value = "on"
    isActive.checked = false;
    isOnHold.checked = false;
  });

  isOnHold.addEventListener('click', () => {
    isOnHold.value = "on"
    isActive.checked = false;
    isBlocked.checked = false;
  });
</script>
</body>
</html>
