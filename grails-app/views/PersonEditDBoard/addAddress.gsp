<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><span class="badge badge-primary fixed-size-badge">Admin<i class="fa fa-user-plus"
                                                                   aria-hidden="true"></i>
    </span></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a
      href="${createLink(controller: 'person', action: 'index')}"
      class="same-line"><i
      class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a
      href="${createLink(controller: 'personEditDBoard', action: 'editAddressDetails', params: [p: params?.p, con: params?.con, op: params?.op])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>


    <h1 class="same-line">Add Address Details</h1>
    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
      <g:if test="${flash?.address}">
        <input type="hidden" id="address" name="address" value="${flash?.address}">
      </g:if>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>

    <div class="d-flex justify-content-center">

      <form
        action="${createLink(controller: 'personEditDBoard', action: 'saveAddress', params: [p: params?.p, con: params?.con, op: params?.op])}"
        method="post">
        <div class="row g-3"><!-- Adds gutter spacing between columns -->
          <div class="col-12 col-md-6 mb-3"><!-- Full width on mobile, half on medium+ -->
            <div class="input-container">
              <input type="text" class="form-control" id="street1" name="street1" placeholder="" required>
              <label for="street1" class="form-label">Street 1</label>
            </div>
          </div>

          <div class="col-12 col-md-6 mb-3">
            <div class="input-container">
              <input type="text" class="form-control" id="street2" name="street2" placeholder="" required>
              <label for="street2" class="form-label">Street 2</label>
            </div>
          </div>
        </div>
        <section class="row">
          <div class="col-12 mb-3"><div class="input-container">
            <input type="text" class="form-control" id="city" name="city" placeholder="" required>
            <label for="city" class="form-label">City</label>
          </div></div>
        </section>
        <section class="row">
          <div class="col-12 mb-3"><div class="input-container">
            <input type="text" class="form-control" id="district" name="district" placeholder="" required>
            <label for="district" class="form-label">District</label>
          </div></div>
        </section>
        <section class="row">
          <div class="col-12 mb-3"><div class="input-container">
            <input type="text" class="form-control" id="state" name="state" placeholder="" required>
            <label for="state" class="form-label">State</label>
          </div></div>
        </section>

        <div class="row g-3"><!-- Adds gutter spacing between columns -->
          <div class="col-12 col-md-6 mb-3"><!-- Full width on mobile, half on medium+ -->
            <div class="input-container">
              <input type="text" class="form-control" id="country" name="country" placeholder="" required>
              <label for="country" class="form-label">Country</label>
            </div>
          </div>

          <div class="col-12 col-md-6 mb-3">
            <div class="input-container">
              <input type="text" class="form-control" id="postalCode" name="postalCode" placeholder="" required
                     maxlength="15"
                     pattern="\d*"
                     oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric">
              <label for="postalCode" class="form-label">Postal Code</label>
            </div>
          </div>
        </div>
        <section class="row row-padding colset-2-its d-flex justify-content-center">
          <div class="checkbox-wrapper-42">
            <input name="addressType" id="permanent" type="checkbox" value="Permanent"/>
            <label class="cbx" for="permanent"></label>
            <label class="lbl" for="permanent">Permanent Address</label>
          </div>

          <div class="checkbox-wrapper-42">
            <input name="addressType" id="present" type="checkbox" value="Present"/>
            <label class="cbx" for="present"></label>
            <label class="lbl" for="present">Present Address</label>
          </div>

          <div class="checkbox-wrapper-42">
            <input name="addressType" id="temporary" type="checkbox" value="Temporary"/>
            <label class="cbx" for="temporary"></label>
            <label class="lbl" for="temporary">Temporary Address</label>
          </div>

          <div class="checkbox-wrapper-42">
            <input name="addressType" id="other" type="checkbox" value="Other"/>
            <label class="cbx" for="other"></label>
            <label class="lbl" for="other">Other</label>
          </div>
        </section>
        <section class="row row-padding d-none" id="otherTypeRow">
          <div class="col-12 mb-3"><div class="input-container">
            <input type="text" class="form-control" id="otherType" name="otherType" placeholder="">
            <label for="otherType" class="form-label">Address Type</label>
          </div></div>
        </section>

        <section class="row row-padding colset-2-its">
          <div class="col d-flex justify-content-end">
            <button class="btn btn-primary"><i class="fas fa-save" aria-hidden="true">&nbsp;&nbsp;Save</i></button>
          </div>
        </section>
      </form>

    </div>
  </div>
</div>
<g:render template="addressModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);

    const address = document.getElementById('address');

    if (address && address.value) {
      const addressModal = new bootstrap.Modal(document.getElementById('statusModal'));
      addressModal.show();
    }

    const permanent = document.getElementById('permanent');
    const present = document.getElementById('present');
    const temporary = document.getElementById('temporary');
    const other = document.getElementById('other');
    const otherTypeRow = document.getElementById('otherTypeRow');
    let slideOutFlag = false;

    permanent.addEventListener('click', () => {
      present.checked = false;
      temporary.checked = false;
      other.checked = false;
      if (slideOutFlag) {
        slideOutFlag = false;
        otherTypeRow.className = "row row-padding colset-2-its slide-up-fade-out";
      }
    });

    present.addEventListener('click', () => {
      permanent.checked = false;
      temporary.checked = false;
      other.checked = false;
      if (slideOutFlag) {
        slideOutFlag = false;
        otherTypeRow.className = "row row-padding colset-2-its slide-up-fade-out";
      }
    });

    temporary.addEventListener('click', () => {
      permanent.checked = false;
      present.checked = false;
      other.checked = false;
      if (slideOutFlag) {
        slideOutFlag = false;
        otherTypeRow.className = "row row-padding colset-2-its slide-up-fade-out";
      }
    });

    other.addEventListener('click', () => {
      permanent.checked = false;
      present.checked = false;
      temporary.checked = false;
      if (!slideOutFlag) {
        slideOutFlag = true;
        otherTypeRow.className = "row row-padding colset-2-its  animate__animated animate__fadeInDown";
        document.getElementById('otherType').focus();
      }
    });
  });
</script>
</body>
</html>
