<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <style>
  /* Override the default modal backdrop to make it fully transparent */

  .modal-backdrop {
    background-color: transparent !important;
  }

  .modal-header, .modal-content {
    border-radius: 15px 0 0 15px; /* Top left and bottom left corners */
    border: 1px solid transparent;
  }

  .modal-content {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  }
  </style>

  <style>
  /* Custom modal positioning and animations */
  .modal.right-slide {
    position: fixed;
    right: 0;
    top: 0;
    bottom: 0;
    margin: 0;
    width: 400px;
    max-width: 100%;
    transform: translateX(100%);
    transition: transform 0.3s ease-out;
  }

  .modal.right-slide.show {
    transform: translateX(0);
  }

  .modal.right-slide .modal-dialog {
    margin: 0;
    height: 100%;
    max-width: 100%;
  }

  .modal.right-slide .modal-content {
    height: 100%;
    border-radius: 0;
    border: none;
  }

  .modal.right-slide .modal-header,
  .modal.right-slide .modal-footer {
    border-width: 0;
  }

  /* Animation for backdrop */
  .modal-backdrop {
    opacity: 0;
    transition: opacity 0.15s linear;
  }

  .modal-backdrop.show {
    opacity: 0.5;
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
    <a
      href="${createLink(controller: 'personEditDBoard', action: 'index', params: [id: params?.id, op: params?.op, con: params?.con])}"><i
      class="fas fa-chevron-left embossed-link"></i></a>

    <h1 class="same-line">Edit Profession Details</h1>

    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
      <g:if test="${flash?.profession}">
        <input type="hidden" id="profession" name="profession" value="${flash?.profession}">
      </g:if>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>
    <section class="row colset-2-its form-container">
      <div class="col d-flex justify-content-start">
        <g:if test="${professions}">
          <ul class="nav nav-tabs" id="professionionDetailTabs" role="tablist">
            <g:each in="${professions}" var="professionDetail" status="i">
              <li class="nav-item" role="presentation">
                <button class="nav-link ${i == 0 ? 'active' : ''}" id="professionDetail${i}-tab" data-toggle="tab"
                        data-target="#professionDetail${i}" type="button" role="tab"
                        aria-controls="professionDetail${i}"
                        aria-selected="${i == 0 ? 'true' : 'false'}">
                  Profession Detail ${i + 1}
                </button>
              </li>
            </g:each>
          </ul>
        </g:if>
        <g:else>
          <div class="d-flex align-items-center h-auto">
            <strong class="same-line text-center">No Profession Details found. Click the</strong>
            <img src="${resource(dir: 'images', file: 'add_new.png')}" class="same-line text-nowrap custom-4x4em"
                 alt="Add Button Image">
            <strong class="same-line text-center">button to add a new Profession Detail !!</strong>
          </div>
        </g:else>
      </div>

      <div class="col d-flex justify-content-end text-nowrap">
        <a
          href="${createLink(controller: 'personEditDBoard', action: 'addProfessionDetail', params: [p: params?.p, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
        <a
          href="${createLink(controller: 'personEditDBoard', action: 'editAddressDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-chevron-right"></i></a>
      </div>

      <div class="tab-content" id="professionDetailTabsContent">
        <g:each in="${professions}" var="professionDetail" status="i">
          <div class="tab-pane fade ${i == 0 ? 'show active' : ''}" id="professionDetail${i}" role="tabpanel"
               aria-labelledby="professionDetail${i}-tab">
            <form
              action="${createLink(controller: 'personEditDBoard', action: 'editAndSaveProfessionDetails')}"
              method="post">
              <input type="hidden" name="p" value="${params?.p}"/>
              <input type="hidden" name="id" value="${professionDetail?.id}"/>
              <input type="hidden" name="op" value="${params?.op}"/>
              <input type="hidden" name="con" value="${params?.con}"/>

              <section class="row colset-2-its d-flex justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="designation" name="designation" placeholder=""
                         value="${professionDetail?.designation}" required>
                  <label for="designation" class="form-label">Designation</label>
                </div></div>
              </section>
              <section class="row colset-2-its d-flex justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="organisation" name="organisation" placeholder=""
                         value="${professionDetail?.organisation}" required>
                  <label for="organisation" class="form-label">Organisation</label>
                </div></div>
              </section>
              <section class="row colset-2-its d-flex justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="organisationLocation" name="organisationLocation"
                         placeholder="" value="${professionDetail?.organisationLocation}"
                         required>
                  <label for="organisationLocation" class="form-label">Location</label>
                </div></div>
              </section>
              <section class="row colset-2-its d-flex justify-content-center">
                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Government / PSU'}">
                    <input class="job-category" name="govt_psu" id="govt_psu_${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="govt_psu" id="govt_psu_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="govt_psu_${i}"></label>
                  <label class="lbl" for="govt_psu_${i}">Government / PSU</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Private Sector'}">
                    <input class="job-category" name="private_sector" id="private_sector_${i}" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="private_sector" id="private_sector_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="private_sector_${i}"></label>
                  <label class="lbl" for="private_sector_${i}">Private Sector</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Business'}">
                    <input class="job-category" name="business" id="business_${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="business" id="business_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="business_${i}"></label>
                  <label class="lbl" for="business_${i}">Business</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Defence'}">
                    <input class="job-category" name="defence" id="defence_${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="defence" id="defence_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="defence_${i}"></label>
                  <label class="lbl" for="defence_${i}">Defence</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Self Employed'}">
                    <input class="job-category" name="self_employed" id="self_employed_${i}" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="self_employed" id="self_employed_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="self_employed_${i}"></label>
                  <label class="lbl" for="self_employed_${i}">Self Employed</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.jobCategory == 'Un Employed'}">
                    <input class="job-category" name="un_employed" id="un_employed_${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input class="job-category" name="un_employed" id="un_employed_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="un_employed_${i}"></label>
                  <label class="lbl" for="un_employed_${i}">Un Employed</label>
                </div>
              </section>
              <section class="row colset-2-its d-flex justify-content-center">
                <div class="container mt-5">
                  <div class="row">
                    <div class="col">
                      <div class="dropdown">
                        <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                          <g:if
                            test="${professionDetail?.currencyCode && professionDetail?.denomination && professionDetail?.country}">
                            ${professionDetail?.currencyCode}&nbsp;[&nbsp;${professionDetail?.denomination}&nbsp;]&nbsp;${professionDetail?.country}
                          </g:if>
                          <g:else>
                            Select Currency
                          </g:else>
                        </button>

                        <div class="dropdown-menu country-code" aria-labelledby="dropdownMenuButton">
                          <div class="px-3 py-2">
                            <input type="text" class="form-control"
                                   placeholder="Search..." id="dropdownSearch">
                          </div>

                          <div class="dropdown-items" style="max-height: 200px; overflow-y: auto;">
                            <g:each in="${currencies}" var="currency">
                              <div class="dropdown-item" data-currency-code=${currency?.cc}
                              data-denomination="${currency?.symbol}"
                                   data-country="${currency?.name}">
                                ${currency?.cc}&nbsp;
                                [&nbsp;${currency?.symbol}&nbsp;]&nbsp;&nbsp;
                                ${currency?.name}
                              </div>
                            </g:each>
                          </div>
                        </div>
                        <input type="hidden" id="currencyCode" name="currencyCode">
                        <input type="hidden" id="denomination" name="denomination">
                        <input type="hidden" id="country" name="country">
                      </div>
                    </div>

                    <div class="col">
                      <div class="mb-3">
                        <div class="input-container">
                          <input type="text" class="form-control" id="salary" name="salary"
                                 value="${professionDetail?.salary}" required maxlength="15"
                                 pattern="\d*"
                                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric">
                          <label for="salary" class="form-label">Salary</label>
                        </div>
                      </div>
                    </div>
                  </div></div>
              </section>
              <section class="row row-padding colset-2-its d-flex justify-content-center">
                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.salaryFor == 'Per Anum'}">
                    <input class="salaryFor" name="salaryFor" id="perAnum_${i}" value="Per Anum" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="salaryFor" name="salaryFor" id="perAnum_${i}" value="Per Anum" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="perAnum_${i}"></label>
                  <label class="lbl" for="perAnum_${i}">Per Anum</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.salaryFor == 'Per Month'}">
                    <input class="salaryFor" name="salaryFor" id="perMonth_${i}" value="Per Month" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="salaryFor" name="salaryFor" id="perMonth_${i}" value="Per Month" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="perMonth_${i}"></label>
                  <label class="lbl" for="perMonth_${i}">Per Month</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.salaryFor == 'Per Week'}">
                    <input class="salaryFor" name="salaryFor" id="perWeek_${i}" value="Per Week" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="salaryFor" name="salaryFor" id="perWeek_${i}" value="Per Week" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="perWeek_${i}"></label>
                  <label class="lbl" for="perWeek_${i}">Per Week</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.salaryFor == 'Per Day'}">
                    <input class="salaryFor" name="salaryFor" id="perDay_${i}" value="Per Day" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input class="salaryFor" name="salaryFor" id="perDay_${i}" value="Per Day" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="perDay_${i}"></label>
                  <label class="lbl" for="perDay_${i}">Per Day</label>
                </div>

                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.salaryFor == 'Per Hour'}">
                    <input class="salaryFor" name="salaryFor" id="perHour_${i}" value="Per Hour" type="checkbox"
                           checked/>
                  </g:if>
                  <g:else>
                    <input class="salaryFor" name="salaryFor" id="perHour_${i}" value="Per Hour" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="perHour_${i}"></label>
                  <label class="lbl" for="perHour_${i}">Per Hour</label>
                </div>
              </section>
              <section class="row colset-2-its d-flex justify-content-center">
                <div class="checkbox-wrapper-42">
                  <g:if test="${professionDetail?.isCurrent}">
                    <input name="isCurrent" id="isCurrent_${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input name="isCurrent" id="isCurrent_${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="isCurrent_${i}"></label>
                  <label class="lbl" for="isCurrent_${i}">Is Current Job</label>
                </div>
              </section>
              <section class="row row-padding colset-2-its d-flex justify-content-end">
                <div class="d-flex justify-content-end">
                  <button class="btn btn-primary" type="submit"><i class="fas fa-save"
                                                                   aria-hidden="true">&nbsp;&nbsp;Save</i>
                  </button>
                </div>
              </section>
            </form>
          </div>
        </g:each>
      </div>
    </section>
  </div>
</div>

<g:render template="professionalDetailModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // Initialize modal if profession exists
    const profession = document.getElementById("profession");
    if (profession?.value) {
      const professionModal = new bootstrap.Modal(document.getElementById('statusModal'));
      professionModal.show();
    }

    // Handle checkbox groups within each tab
    document.getElementById('professionDetailTabsContent').addEventListener('change', function (e) {
      const checkbox = e.target;
      const tabPane = checkbox.closest('.tab-pane');

      if (!tabPane) return;

      if (checkbox.classList.contains('job-category') || checkbox.classList.contains('salaryFor')) {
        const groupClass = checkbox.classList.contains('job-category') ? 'job-category' : 'salaryFor';
        const checkboxes = tabPane.querySelectorAll('.' + groupClass);

        if (checkbox.checked) {
          checkboxes.forEach((otherCheckbox) => {
            if (otherCheckbox !== checkbox) {
              otherCheckbox.checked = false;
            }
          });
        }
      }
    });

    // Initialize dropdown functionality for each tab
    document.querySelectorAll('.tab-pane').forEach(tabPane => {
      const searchInput = tabPane.querySelector('#dropdownSearch');
      const dropdownItems = tabPane.querySelectorAll('.dropdown-item');
      const dropdownButton = tabPane.querySelector('.dropdown-toggle');
      const currencyCodeInput = tabPane.querySelector('#currencyCode');
      const denominationInput = tabPane.querySelector('#denomination');
      const countryInput = tabPane.querySelector('#country');

      if (searchInput) {
        searchInput.addEventListener('keyup', function (e) {
          const searchTerm = e.target.value.toLowerCase();
          dropdownItems.forEach(item => {
            const text = item.textContent.toLowerCase();
            item.style.display = text.includes(searchTerm) ? 'block' : 'none';
          });
        });
      }

      if (dropdownItems) {
        dropdownItems.forEach(item => {
          item.addEventListener('click', function (e) {
            e.preventDefault();
            if (currencyCodeInput) currencyCodeInput.value = this.dataset.currencyCode;
            if (denominationInput) denominationInput.value = this.dataset.denomination;
            if (countryInput) countryInput.value = this.dataset.country;
            if (dropdownButton) dropdownButton.textContent = this.textContent.trim();
          });
        });
      }


      $('.dropdown').on('shown.bs.dropdown', function () {
        searchInput.focus();
      });


    });
  });
</script>
</body>
</html>
