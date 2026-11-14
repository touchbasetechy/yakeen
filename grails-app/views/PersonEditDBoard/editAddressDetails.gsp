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
        href="${createLink(controller: 'PersonEditDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      <h4 class="same-line">Address</h4>
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
    <section class="row d-flex justify-content-center">
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
          href="${createLink(controller: 'personEditDBoard', action: 'addAddress', params: [p: params?.p, con: params?.con, op: params?.op])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
      </div>

      <div class="tab-content" id="addressTabsContent">
        <g:each in="${addresses}" var="address" status="i">
          <div class="tab-pane fade ${i == 0 ? 'show active' : ''}" id="address${i}" role="tabpanel"
               aria-labelledby="address${i}-tab">
            <div class="d-flex justify-content-center">
              <form
                action="${createLink(controller: 'PersonEditDBoard', action: 'editAndSaveAddressDetails')}"
                method="post">
                <input type="hidden" name="p" value="${params?.p}"/>
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

                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="city" name="city" placeholder=""
                         value="${address?.city}" required>
                  <label for="city" class="form-label">City</label>
                </div></div>

                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="district" name="district" placeholder=""
                         value="${address?.district}" required>
                  <label for="district" class="form-label">District</label>
                </div></div>

                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="state" name="state" placeholder=""
                         value="${address?.state}" required>
                  <label for="state" class="form-label">State</label>
                </div></div>

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
                    <g:if test="${address?.addressType == 'Permanent'}">
                      <input class="addressType" name="addressType" id="permanent${i}" type="checkbox" value="Permanent"
                             checked/>
                    </g:if>
                    <g:else>
                      <input class="addressType" name="addressType" id="permanent${i}" type="checkbox"
                             value="Permanent"/>
                    </g:else>
                    <label class="cbx" for="permanent${i}"></label>
                    <label class="lbl" for="permanent${i}">Permanent Address</label>
                  </div>

                  <div class="checkbox-wrapper-42">
                    <g:if test="${address?.addressType == 'Present'}">
                      <input class="addressType" name="addressType" id="present${i}" type="checkbox" value="Present"
                             checked/>
                    </g:if>
                    <g:else>
                      <input class="addressType" name="addressType" id="present${i}" type="checkbox" value="Present"/>
                    </g:else>
                    <label class="cbx" for="present${i}"></label>
                    <label class="lbl" for="present${i}">Present Address</label>
                  </div>

                  <div class="checkbox-wrapper-42">
                    <g:if test="${address?.addressType == 'Temporary'}">
                      <input class="addressType" name="addressType" id="temporary${i}" type="checkbox" value="Temporary"
                             checked/>
                    </g:if>
                    <g:else>
                      <input class="addressType" name="addressType" id="temporary${i}" type="checkbox"
                             value="Temporary"/>
                    </g:else>
                    <label class="cbx" for="temporary${i}"></label>
                    <label class="lbl" for="temporary${i}">Temporary Address</label>
                  </div>

                  <div class="checkbox-wrapper-42">
                    <g:if test="${address?.addressType == 'Other'}">
                      <input class="addressType other" name="addressType" id="other${i}" type="checkbox" value="Other"
                             checked/>
                    </g:if>
                    <g:else>
                      <input class="addressType other" name="addressType" id="other${i}" type="checkbox" value="Other"/>
                    </g:else>
                    <label class="cbx" for="other${i}"></label>
                    <label class="lbl" for="other${i}">Other</label>
                  </div>
                </section>
                <g:if test="${address?.addressType == 'Other'}">
                  <section class="row row-padding otherTypeRow animate__animated animate__fadeInDown" id="otherTypeRow">
                    <div class="col-12 mb-3"><div class="input-container">
                      <input type="text" class="form-control otherType" id="otherType${i}" name="otherType"
                             placeholder=" "
                             value="${address?.otherType}">
                      <label for="otherType${i}" class="form-label">Address Type</label>
                    </div></div>
                  </section>
                </g:if>
                <g:else>
                  <section class="row row-padding d-none otherTypeRow" id="otherTypeRow">
                    <div class="col-12 mb-3"><div class="input-container">
                      <input type="text" class="form-control otherType" id="otherType${i}" name="otherType"
                             placeholder=" ">
                      <label for="otherType${i}" class="form-label">Address Type</label>
                    </div></div>
                  </section>
                </g:else>


                <div class="d-flex justify-content-end">
                  <button class="btn btn-primary toolbar-button" type="submit"><i class="fas fa-save"
                                                                                  aria-hidden="true">&nbsp;save</i>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </g:each>
      </div>
    </section>
  </div>
</div>
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

    // Handle checkbox groups within each tab
    document.getElementById('addressTabsContent').addEventListener('change', function (e) {
      const checkbox = e.target;
      const tabPane = checkbox.closest('.tab-pane');

      if (!tabPane) return;

      if (checkbox.classList.contains('addressType')) {
        const checkboxes = tabPane.querySelectorAll('.addressType');

        if (checkbox.checked) {
          checkboxes.forEach((otherCheckbox) => {
            if (otherCheckbox !== checkbox) {
              otherCheckbox.checked = false;
            }
          });
        }

        let otherTypeRow = tabPane.querySelector('.otherTypeRow');

        if (checkbox.classList.contains('other')) {
          otherTypeRow.className = "row row-padding otherTypeRow animate__animated animate__fadeInDown";
          tabPane.querySelector('.otherType').focus();
        } else if (otherTypeRow.classList.contains('animate__fadeInDown')) {
          otherTypeRow.className = "row row-padding otherTypeRow slide-up-fade-out";
        }
      }
    });
  });
</script>
</body>
</html>
