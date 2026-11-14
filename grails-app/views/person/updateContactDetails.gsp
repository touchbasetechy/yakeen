<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <!-- CSS -->
  <style>
  .dropdown-menu .dropdown-items {
    padding: 0.25rem 0;
  }

  .dropdown-item {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
    transition: background-color 0.2s;
  }

  .dropdown-item:hover {
    background-color: #f8f9fa;
  }

  .dropdown-item.hidden {
    display: none !important;
  }
  </style>
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
    <a
      href="${createLink(controller: 'person', action: 'index')}"
      class="same-line"><i
      class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a
      href="${createLink(controller: 'person', action: 'updateSpouseFamilyDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>

    <h1 class="same-line">Edit Contact Details</h1>

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
    <section class="row justify-content-center align-items-start">
      <div class="col d-flex justify-content-start">
        <g:if test="${contacts}">
          <ul class="nav nav-tabs" id="contactDetailTabs" role="tablist">
            <g:each in="${contacts}" var="contactDetail" status="i">
              <li class="nav-item" role="presentation">
                <button class="nav-link ${i == 0 ? 'active' : ''}" id="contactDetail${i}-tab" data-toggle="tab"
                        data-target="#contactDetail${i}" type="button" role="tab" aria-controls="contactDetail${i}"
                        aria-selected="${i == 0 ? 'true' : 'false'}">
                  Contact Detail ${i + 1}
                </button>
              </li>
            </g:each>
          </ul>
        </g:if>
        <g:else>
          <div class="d-flex align-items-center h-auto">
            <strong class="same-line text-center">No Contact Details found. Click the</strong>
            <img src="${resource(dir: 'images', file: 'add_new.png')}" class="same-line text-nowrap custom-4x4em"
                 alt="Add Button Image">
            <strong class="same-line text-center">button to add a new Contact Detail !!</strong>
          </div>
        </g:else>
      </div>

      <div class="col d-flex justify-content-end text-nowrap">
        <a
          href="${createLink(controller: 'person', action: 'addContactDetail', params: [id: params?.id, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
        <a
          href="${createLink(controller: 'person', action: 'updateEducationDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-chevron-right"></i></a>
      </div>

      <div class="tab-content" id="contactDetailTabsContent">
        <g:each in="${contacts}" var="contactDetail" status="i">
          <div class="tab-pane fade ${i == 0 ? 'show active' : ''}" id="contactDetail${i}" role="tabpanel"
               aria-labelledby="contactDetail${i}-tab">
            <form action="${createLink(controller: 'person', action: 'saveContactDetails')}" method="post">
              <input type="hidden" name="id" value="${contactDetail?.id}"/>
              <input type="hidden" name="op" value="${params?.op}"/>
              <input type="hidden" name="con" value="${params?.con}"/>

              <div class="container mt-5">
                <div class="row">
                  <div class="col d-flex justify-content-end">
                    <div class="dropdown">
                      <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton${i}"
                              data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Select Country
                      </button>

                      <div class="dropdown-menu country-code" aria-labelledby="dropdownMenuButton${i}">
                        <div class="px-3 py-2">
                          <input type="text" class="form-control"
                                 placeholder="Search..."
                                 id="dropdownSearch${i}">
                        </div>

                        <div class="dropdown-items" id="dropdown-items${i}"
                             style="max-height: 200px; overflow-y: auto;">
                          <g:each in="${countries}" var="country">
                            <div class="dropdown-item d-flex align-items-center gap-2"
                                 data-value="${country?.dial_code}"
                                 data-country="${country?.name}"
                                 data-flag="${country?.emoji}">
                              <!-- Emoji -->
                              <span style="font-size: 1.75em;">${country?.emoji}</span>

                              <!-- Text -->
                              <span class="text-nowrap">
                                &nbsp;&nbsp; ${country?.name} ${country?.dial_code}
                              </span>
                            </div>
                          </g:each>
                        </div>
                      </div>

                      <input type="hidden" name="isdCode" value="${contactDetail?.isdCode}">
                      <input type="hidden" name="country" value="${contactDetail?.country}">
                      <input type="hidden" name="flag" value="${contactDetail?.flag}">
                    </div>
                  </div>

                  <div class="col">
                    <div class="mb-3">
                      <div class="input-container">
                        <input type="text" class="form-control" id="mobileNumber" name="mobileNumber"
                               placeholder="" value="${contactDetail?.mobileNumber}" required maxlength="15"
                               pattern="\d*"
                               oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric">
                        <label for="mobileNumber" class="form-label">Mobile Number</label>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col"></div>

                  <div class="col">
                    <div class="mb-3">
                      <div class="input-container">
                        <input type="text" class="form-control" id="eMail" name="eMail" placeholder=""
                               value="${contactDetail?.eMail}" required>
                        <label for="eMail" class="form-label">EMail Id</label>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col"></div>

                  <div class="col">
                    <div class="mb-3">
                      <div class="checkbox-wrapper-42">
                        <g:if test="${contactDetail?.isPrimary == 'on'}">
                          <input name="isPrimary" id="isPrimary" type="checkbox" checked/>
                        </g:if>
                        <g:else>
                          <input name="isPrimary" id="isPrimary" type="checkbox"/>
                        </g:else>

                        <label class="cbx" for="isPrimary"></label>
                        <label class="lbl" for="isPrimary">Is Primary</label>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-end">
                  <button class="btn btn-primary" type="submit"><i class="fas fa-save"
                                                                   aria-hidden="true">&nbsp;&nbsp;Save</i>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </g:each>
      </div>
    </section>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll('.tab-pane').forEach((tabPane, index) => {
      const dropdownButton = tabPane.querySelector(`#dropdownMenuButton` + index);
      if (!dropdownButton) {
        console.warn(`Dropdown button #dropdownMenuButton'+index+' not found`);
        return;
      }

      const searchInput = tabPane.querySelector(`#dropdownSearch` + index);
      const dropdownItems = tabPane.querySelectorAll('.dropdown-item');
      const isdCodeInput = tabPane.querySelector('input[name="isdCode"]');
      const countryInput = tabPane.querySelector('input[name="country"]');
      const flagInput = tabPane.querySelector('input[name="flag"]');
      const dropdownElement = tabPane.querySelector('.dropdown');
      const isPrimarys = tabPane.querySelectorAll('input[name="isPrimary"]');

      // Initialize dropdown only if all required elements exist
      //if (dropdownButton && searchInput && isdCodeInput && countryInput && flagInput) {
      const bsDropdown = new bootstrap.Dropdown(dropdownButton);

      searchInput.addEventListener('keyup', function (e) {
        const searchTerm = e.target.value.toLowerCase();
        dropdownItems.forEach(item => {
          const text = item.textContent.toLowerCase();
          item.classList.toggle('hidden', !text.includes(searchTerm));
        });
      });

      dropdownItems.forEach(item => {
        item.addEventListener('click', function (e) {
          isdCodeInput.value = this.dataset.value;
          countryInput.value = this.dataset.country;
          flagInput.value = this.dataset.flag;

          dropdownButton.innerHTML = this.dataset.flag + ' ' +
            this.dataset.country + ' ' +
            this.dataset.value;
          //  dropdownButton.textContent = item.textContent;
        });
      });

      if (flagInput.value && countryInput.value && isdCodeInput.value) {
        dropdownButton.innerHTML = flagInput.value + ' ' + countryInput.value + ' ' + isdCodeInput.value;
      }

      // Bootstrap dropdown shown event
      dropdownElement.addEventListener('shown.bs.dropdown', function () {
        searchInput.focus();
      });
      // dropdownButton.closest('.dropdown').addEventListener('shown.bs.dropdown', () => {
      //   searchInput.focus();
      // });
      //}
    });
  });
</script>
</body>
</html>
