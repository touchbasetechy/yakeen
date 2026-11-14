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
    <a href="${createLink(controller: 'person', action: 'index')}"
       class="same-line"><i class="fas fa-home embossed-link"></i></a>
    <a href="${createLink(controller: 'person', action: 'updateContactDetails')}"
       class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>

    <h1 class="same-line">Add Contact Details</h1>

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
    <section class="row colset-2-its justify-content-center">
      <form
        action="${createLink(controller: 'person', action: 'saveNewContactDetails', params: [id: params?.id, con: params?.con, op: params?.op])}"
        method="post">
        <div class="container mt-5">
          <div class="row">
            <div class="col d-flex justify-content-center">
              <div class="dropdown">
                <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton"
                        data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  Select Country
                </button>

                <div class="dropdown-menu country-code" aria-labelledby="dropdownMenuButton">
                  <div class="px-3 py-2">
                    <input type="text" class="form-control"
                           placeholder="Search..." id="dropdownSearch">
                  </div>

                  <div class="dropdown-items" style="max-height: 200px; overflow-y: auto;">
                    <g:each in="${countries}" var="country">
                      <div class="dropdown-item d-flex align-items-center gap-2"
                           data-value="${country?.dial_code}"
                           data-country="${country?.name}"
                           data-flag="${country?.emoji}">
                        <!-- Emoji -->
                        <span style="font-size: 1.75em;">${country.emoji}</span>

                        <!-- Text -->
                        <span class="text-nowrap">
                          &nbsp;&nbsp; ${country?.name} ${country?.dial_code}
                        </span>
                      </div>
                    </g:each>
                  </div>
                </div>
                <input type="hidden" id="isdCode" name="isdCode">
                <input type="hidden" id="country" name="country">
                <input type="hidden" id="flag" name="flag">
              </div>
            </div>

            <div class="col">
              <div class="mb-3">
                <div class="input-container">
                  <input type="text" class="form-control" id="mobileNumber" name="mobileNumber"
                         placeholder="" required maxlength="15"
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
                         required>
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
                  <input name="isPrimary" id="isPrimary" type="checkbox"/>
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
    </section>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById('dropdownSearch');
    const dropdownItems = document.querySelectorAll('.dropdown-item');
    const dropdownButton = document.querySelector('.dropdown-toggle');
    const isdCodeInput = document.getElementById('isdCode');
    const countryInput = document.getElementById('country');
    const flagInput = document.getElementById('flag');
    const dropdownElement = document.querySelector('.dropdown');

    // Search functionality
    searchInput.addEventListener('keyup', function (e) {
      const searchTerm = e.target.value.toLowerCase();

      dropdownItems.forEach(item => {
        const text = item.textContent.toLowerCase();
        item.classList.toggle('hidden', !text.includes(searchTerm));
      });
    });

    // Update button text when item is selected
    dropdownItems.forEach(item => {
      item.addEventListener('click', function (e) {
        e.preventDefault();
        const isdCode = this.dataset.value;
        const country = this.dataset.country;
        const flag = this.dataset.flag;

        // Update hidden inputs
        isdCodeInput.value = isdCode;
        countryInput.value = country;
        flagInput.value = flag;

        // Update button text (using template literal for clarity)
        dropdownButton.textContent = item.textContent;//`${flag} ${country} ${isdCode}`;
      });
    });

    // Bootstrap dropdown shown event
    dropdownElement.addEventListener('shown.bs.dropdown', function () {
      searchInput.focus();
    });
  });
</script>
</body>
</html>
