<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-hat-wizard"><span
      class="label-text">Admin</span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="form-container">
      <a href="${createLink(controller: 'person', action: 'index')}" class="same-line"><i
        class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      <a
        href="${createLink(controller: 'person', action: 'updateProfessionDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      <h4 class="same-line">Edit Address Details</h4>
      <g:if test="${flash.error}">
        <div id="alert" class="alert alert-danger">
          ${flash.error}
        </div>
        <g:if test="${flash?.address}">
          <input type="hidden" id="address" name="address" value="${flash?.address}">
        </g:if>
      </g:if>
      <g:if test="${flash.message}">
        <div id="alert" class="alert alert-success">
          ${flash.message}
        </div>
      </g:if>
    </div>

    <section class="row colset-2-its form-container">

      <div class="col d-flex justify-content-start">
        <g:if test="${addresses}">
          <ul class="nav nav-tabs" id="addressTabs" role="tablist">
            <g:each in="${addresses}" var="address" status="i">
              <li class="nav-item" role="presentation">
                <button class="nav-link ${i == 0 ? 'active' : ''}" id="address${i}-tab" data-toggle="tab"
                        data-target="#address${i}" type="button" role="tab" aria-controls="address${i}"
                        aria-selected="${i == 0 ? 'true' : 'false'}">
                  Address ${i + 1}
                </button>
              </li>
            </g:each>
          </ul>
        </g:if>
        <g:else>
          <div class="d-flex align-items-center h-auto">
            <strong class="same-line text-center">No Address Details found. Click the</strong>
            <img src="${resource(dir: 'images', file: 'add_new.png')}" class="same-line text-nowrap custom-4x4em"
                 alt="Add Button Image">
            <strong class="same-line text-center">button to add a new address !!</strong>
          </div>
        </g:else>
      </div>


      <div class="col d-flex justify-content-end text-nowrap">
        <a
          href="${createLink(controller: 'person', action: 'addAddress', params: [id: params?.id, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
      </div>


      <div class="tab-content" id="addressTabsContent">
        <g:each in="${addresses}" var="address" status="i">
          <div class="tab-pane fade ${i == 0 ? 'show active' : ''}" id="address${i}" role="tabpanel"
               aria-labelledby="address${i}-tab">
            <div class="d-flex justify-content-center">
              <form
                action="${createLink(controller: 'person', action: 'saveAddressDetails')}"
                method="post">
                <input type="hidden" name="id" value="${address?.id}"/>
                <input type="hidden" name="op" value="${params?.op}"/>
                <input type="hidden" name="con" value="${params?.con}"/>

                <div class="row g-3">
                  <div class="col-12 col-md-6 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="street1" name="street1" placeholder=""
                           value="${address?.street1}" required autofocus>
                    <label for="street1" class="form-label">Street 1</label>
                  </div></div>

                  <div class="col-12 col-md-6 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="street2" name="street2" placeholder=""
                           value="${address?.street2}" required>
                    <label for="street2" class="form-label">Street 2</label>
                  </div></div>
                </div>

                <div class="row g-3">
                  <div class="col-12 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="city" name="city" placeholder=""
                           value="${address?.city}"
                           required>
                    <label for="city" class="form-label">City</label>
                  </div></div>
                </div>

                <div class="row g-3">
                  <div class="col-12 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="district" name="district" placeholder=""
                           value="${address?.district}" required>
                    <label for="district" class="form-label">District</label>
                  </div></div>
                </div>

                <div class="row g-3">
                  <div class="col-12 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="state" name="state" placeholder=""
                           value="${address?.state}"
                           required>
                    <label for="state" class="form-label">State</label>
                  </div></div>
                </div>

                <div class="row g-3">
                  <div class="col-12 col-md-6 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="country" name="country" placeholder=""
                           value="${address?.country}"
                           required>
                    <label for="country" class="form-label">Country</label>
                  </div></div>

                  <div class="col-12 col-md-6 mb-3"><div class="input-container">
                    <input type="text" class="form-control" id="postalCode" name="postalCode" placeholder=""
                           value="${address?.postalCode}" maxlength="15" pattern="\d*"
                           oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric"
                           required>
                    <label for="postalCode" class="form-label">Postal Code</label>
                  </div></div>
                </div>
                <section class="row row-padding d-flex justify-content-center">
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

                <div class="d-flex justify-content-end">
                  <button class="btn btn-primary"><i class="fas fa-save"
                                                     aria-hidden="true">&nbsp;&nbsp;Save</i></button>
                </div>
              </form>
            </div>
          </div>
        </g:each>
      </div>
    </section>
  </div>
</div>
<g:render template="/DBoard/addressModal"/>
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
        otherTypeRow.className = "row row-padding slide-up-fade-out";
      }
    });

    present.addEventListener('click', () => {
      permanent.checked = false;
      temporary.checked = false;
      other.checked = false;
      if (slideOutFlag) {
        slideOutFlag = false;
        otherTypeRow.className = "row row-padding slide-up-fade-out";
      }
    });

    temporary.addEventListener('click', () => {
      permanent.checked = false;
      present.checked = false;
      other.checked = false;
      if (slideOutFlag) {
        slideOutFlag = false;
        otherTypeRow.className = "row row-padding slide-up-fade-out";
      }
    });

    other.addEventListener('click', () => {
      permanent.checked = false;
      present.checked = false;
      temporary.checked = false;
      if (!slideOutFlag) {
        slideOutFlag = true;
        otherTypeRow.className = "row row-padding animate__animated animate__fadeInDown";
        document.getElementById('otherType').focus();
      }
    });
  });
</script>
</body>
</html>
