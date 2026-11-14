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
    <div class="form-container">
      <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
         class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      <a href="${createLink(controller: 'SuperAdmin', action: 'updateSpouseDetails')}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h4 class="same-line">Edit Address Details</h4>
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
    <section class="row colset-2-its form-container">
      <form action="${createLink(controller: 'SuperAdmin', action: 'saveAddressDetails')}"
            method="post">
        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="street1" name="street1" placeholder=""
                 value="${session?.personToEdit?.address?.street1}" required>
          <label for="street1" class="form-label">Street 1</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="street2" name="street2" placeholder=""
                 value="${session?.personToEdit?.address?.street2}" required>
          <label for="street2" class="form-label">Street 2</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="city" name="city" placeholder=""
                 value="${session?.personToEdit?.address?.city}" required>
          <label for="city" class="form-label">City</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="district" name="district" placeholder=""
                 value="${session?.personToEdit?.address?.district}" required>
          <label for="district" class="form-label">District</label>
        </div></div>

        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="state" name="state" placeholder=""
                 value="${session?.personToEdit?.address?.state}" required>
          <label for="state" class="form-label">State</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="country" name="country" placeholder=""
                 value="${session?.personToEdit?.address?.country}" required>
          <label for="country" class="form-label">Country</label>
        </div></div>

        <div class="mb-3 same-line"><div class="input-container">
          <input type="text" class="form-control" id="postalCode" name="postalCode" placeholder=""
                 value="${session?.personToEdit?.address?.postalCode}" required>
          <label for="postalCode" class="form-label">Postal Code</label>
        </div></div>

        <div class="d-flex justify-content-end">
          <button class="btn btn-primary toolbar-button" type="submit"><span class="fas fa-angle-right"
                                                                             aria-hidden="true">&nbsp;next</span>
          </button>
        </div>
      </form>
    </section>
  </div>
</div>

</body>
</html>
