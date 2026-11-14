<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-hat-wizard"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'person', action: 'index')}"
       class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a
      href="${createLink(controller: 'person', action: 'listUsersForPolicies')}"
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
    <form
      action="${createLink(controller: 'person', action: 'savePolicies', params: [id: params?.id])}"
      method="post">
      <g:if test="${policies}">
        <section class="row">
          <div class="col-sm-2"><div class="form-check">
            <g:if test="${policies?.isActive}">
              <input class="form-check-input" type="checkbox" value="${policies?.isActive}"
                     id="isActive" name="isActive" checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="${policies?.isActive}"
                     id="isActive" name="isActive">
            </g:else>
            <label class="form-check-label" for="isActive">Active</label>
          </div></div>

          <div class="col-sm-2"><div class="form-check">
            <g:if test="${policies?.isBlocked}">
              <input class="form-check-input" type="checkbox" value="${policies?.isBlocked}"
                     id="isBlocked" name="isBlocked" checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="${policies?.isBlocked}"
                     id="isBlocked" name="isBlocked">
            </g:else>
            <label class="form-check-label" for="isBlocked">Blocked</label>
          </div></div>

          <div class="col-sm-2"><div class="form-check">
            <g:if test="${policies?.isOnHold}">
              <input class="form-check-input" type="checkbox" value="${policies?.isOnHold}"
                     id="isOnHold" name="isOnHold" checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="${policies?.isOnHold}"
                     id="isOnHold" name="isOnHold">
            </g:else>
            <label class="form-check-label" for="isOnHold">On Hold</label>
          </div></div>
        </section>
      </g:if>
      <g:else>
        <section class="row">
          <div class="col-sm-2"><div class="form-check">
            <input class="form-check-input" type="checkbox"
                   id="isActive1" name="isActive">
            <label class="form-check-label" for="isActive1">Active</label>
          </div></div>

          <div class="col-sm-2"><div class="form-check">
            <input class="form-check-input" type="checkbox"
                   id="isBlocked1" name="isBlocked">
            <label class="form-check-label" for="isBlocked1">Blocked</label>
          </div></div>

          <div class="col-sm-2"><div class="form-check">
            <input class="form-check-input" type="checkbox"
                   id="isOnHold1" name="isOnHold">
            <label class="form-check-label" for="isOnHold1">On Hold</label>
          </div></div>
        </section>
      </g:else>


      <div class="d-flex justify-content-end">
        <button class="btn btn-primary toolbar-button" type="submit"><span class="fas fa-save"
                                                                           aria-hidden="true">&nbsp;Save</span>
        </button>
      </div>
    </form>
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    <!--row 1-->
    const isActive = document.getElementById('isActive');
    const isBlocked = document.getElementById('isBlocked');
    const isOnHold = document.getElementById('isOnHold');

    if (isActive) {
      isActive.addEventListener('click', () => {
        isActive.value = "on";
        isBlocked.checked = false;
        isOnHold.checked = false;
      });
    }
    if (isBlocked) {
      isBlocked.addEventListener('click', () => {
        isBlocked.value = "on";
        isActive.checked = false;
        isOnHold.checked = false;
      });
    }
    if (isOnHold) {
      isOnHold.addEventListener('click', () => {
        isOnHold.value = "on";
        isActive.checked = false;
        isBlocked.checked = false;
      });
    }

    const isActive1 = document.getElementById('isActive1');
    const isBlocked1 = document.getElementById('isBlocked1');
    const isOnHold1 = document.getElementById('isOnHold1');
    if (isActive1) {
      isActive1.addEventListener('click', () => {
        isActive1.value = "on";
        isBlocked1.checked = false;
        isOnHold1.checked = false;
      });
    }
    if (isBlocked1) {
      isBlocked1.addEventListener('click', () => {
        isBlocked1.value = "on";
        isActive1.checked = false;
        isOnHold1.checked = false;
      });
    }
    if (isOnHold1) {
      isOnHold1.addEventListener('click', () => {
        isOnHold1.value = "on";
        isActive1.checked = false;
        isBlocked1.checked = false;
      });
    }
  });
</script>
</body>
</html>
