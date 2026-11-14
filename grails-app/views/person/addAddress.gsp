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
      <a
        href="${createLink(controller: 'person', action: 'updateAddressDetails', params: [id: params?.id, con: params?.con, op: params?.op])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      <h4 class="same-line">Add Address</h4>
    </div>

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
        action="${createLink(controller: 'person', action: 'saveNewAddress', params: [id: params?.id, con: params?.con, op: params?.op])}"
        method="post">
        <div class="row g-3">
          <div class="col-12 col-md-6 mb-3">
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

        <div class="mb-3">
          <div class="input-container">
            <input type="text" class="form-control" id="city" name="city" placeholder="" required>
            <label for="city" class="form-label">City</label>
          </div>
        </div>

        <div class="mb-3">
          <div class="input-container">
            <input type="text" class="form-control" id="district" name="district" placeholder="" required>
            <label for="district" class="form-label">District</label>
          </div>
        </div>

        <div class="mb-3">
          <div class="input-container">
            <input type="text" class="form-control" id="state" name="state" placeholder="" required>
            <label for="state" class="form-label">State</label>
          </div>
        </div>

        <div class="row g-3">
          <div class="col-12 col-md-6 mb-3">
            <div class="input-container">
              <input type="text" class="form-control" id="country" name="country" placeholder="" required>
              <label for="country" class="form-label">Country</label>
            </div>
          </div>

          <div class="col-12 col-md-6 mb-3">
            <div class="input-container">
              <input type="text" class="form-control" id="postalCode" name="postalCode" placeholder=""
                     required maxlength="15" pattern="\d*" oninput="this.value = this.value.replace(/[^\d]/g, '')"
                     inputmode="numeric">
              <label for="postalCode" class="form-label">Postal Code</label>
            </div>
          </div>
        </div>

        <div class="row row-padding d-flex justify-content-center">
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
        </div>

        <div class="row row-padding d-none" id="otherTypeRow">
          <div class="col-12 mb-3">
            <div class="input-container">
              <input type="text" class="form-control" id="otherType" name="otherType" placeholder="">
              <label for="otherType" class="form-label">Address Type</label>
            </div>
          </div>
        </div>

        <div class="row row-padding">
          <div class="col d-flex justify-content-end">
            <button class="btn btn-primary"><i class="fas fa-save" aria-hidden="true">&nbsp;&nbsp;Save</i></button>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // Hide alert after 6 seconds
    setTimeout(function () {
      const alert = document.getElementById('alert');
      if (alert) {
        alert.style.display = 'none';
      }
    }, 6000);

    // Handle address type checkboxes
    const permanent = document.getElementById('permanent');
    const present = document.getElementById('present');
    const temporary = document.getElementById('temporary');
    const other = document.getElementById('other');
    const otherTypeRow = document.getElementById('otherTypeRow');
    const otherTypeInput = document.getElementById('otherType');

    function uncheckOthers(current) {
      [permanent, present, temporary, other].forEach(checkbox => {
        if (checkbox !== current) {
          checkbox.checked = false;
        }
      });
    }

    function toggleOtherTypeRow(show) {
      if (show) {
        otherTypeRow.classList.remove('d-none');
        otherTypeInput.focus();
      } else {
        otherTypeRow.classList.add('d-none');
        otherTypeInput.value = '';
      }
    }

    if (permanent) permanent.addEventListener('change', function () {
      if (this.checked) {
        uncheckOthers(this);
        toggleOtherTypeRow(false);
      }
    });

    if (present) present.addEventListener('change', function () {
      if (this.checked) {
        uncheckOthers(this);
        toggleOtherTypeRow(false);
      }
    });

    if (temporary) temporary.addEventListener('change', function () {
      if (this.checked) {
        uncheckOthers(this);
        toggleOtherTypeRow(false);
      }
    });

    if (other) other.addEventListener('change', function () {
      if (this.checked) {
        uncheckOthers(this);
        toggleOtherTypeRow(true);
      } else {
        toggleOtherTypeRow(false);
      }
    });
  });
</script>
</body>
</html>
