<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
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
    <g:if test="${!params?.pTId}">
      <a
        href="${createLink(controller: 'editDBoard', action: 'index', params: [pId: params?.pId, e: params?.e, con: params?.con, ac: params?.ac, oPTId: params?.oPTId])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
    </g:if>
    <g:else>
      <a
        href="${createLink(controller: 'editDBoard', action: 'index', params: [pId: params?.pId, e: params?.e, con: params?.con, ac: params?.ac, oPTId: params?.oPTId, pTId: params?.pTId])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
    </g:else>
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
    <form
      action="${createLink(controller: 'adminDBoard', action: 'SavePolicies', params: [pId: params?.pId, ac: params?.ac])}"
      method="post">

      <section class="row">
        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.person?.policies?.isActive == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isActive" name="isActive" checked>
          </g:if>
          <g:else test="${session?.person?.policies?.isActive == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isActive" name="isActive">
          </g:else>
          <label class="form-check-label" for="isActive">Active</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.person?.policies?.isBlocked == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isBlocked" name="isBlocked" checked>
          </g:if>
          <g:else test="${session?.person?.policies?.isBlocked == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isBlocked" name="isBlocked">
          </g:else>
          <label class="form-check-label" for="isBlocked">Blocked</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.person?.policies?.isOnHold == true}">
            <input class="form-check-input" type="checkbox" value="on"
                   id="isOnHold" name="isOnHold" checked>
          </g:if>
          <g:else test="${session?.person?.policies?.isOnHold == false}">
            <input class="form-check-input" type="checkbox" value=""
                   id="isOnHold" name="isOnHold">
          </g:else>
          <label class="form-check-label" for="isOnHold">On Hold</label>
        </div></div>
      </section>
      <section class="row">
        <div class="d-flex justify-content-end">
          <button class="btn btn-primary toolbar-button" type="submit"><i class="fas fa-save"
                                                                          aria-hidden="true">&nbsp;save</i>
          </button>
        </div>
      </section>
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
