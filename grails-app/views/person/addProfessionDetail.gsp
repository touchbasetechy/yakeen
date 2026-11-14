<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
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
       class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a
      href="${createLink(controller: 'person', action: 'updateProfessionDetails', params: [id: params?.id, con: params?.con, op: params?.op])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

    <h1 class="same-line">Add Profession Details</h1>

    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
      <g:if test="${flash.profession}">
        <input type="hidden" id="profession" name="profession" value="Add Profession Details">
      </g:if>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>
    <form
      action="${createLink(controller: 'person', action: 'saveNewProfessionDetails')}"
      method="post">
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="designation" name="designation" placeholder="" required>
          <label for="designation" class="form-label">Designation</label>
        </div></div>
      </section>
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="organisation" name="organisation" placeholder="" required>
          <label for="organisation" class="form-label">Organisation</label>
        </div></div>
      </section>
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="organisationLocation" name="organisationLocation" placeholder=""
                 required>
          <label for="organisationLocation" class="form-label">Location</label>
        </div></div>
      </section>
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="govt_psu" value="Government / PSU" type="checkbox"/>
          <label class="cbx" for="govt_psu"></label>
          <label class="lbl" for="govt_psu">Government / PSU</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="private_sector" value="Private Sector" type="checkbox"/>
          <label class="cbx" for="private_sector"></label>
          <label class="lbl" for="private_sector">Private Sector</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="business" value="Business" type="checkbox"/>
          <label class="cbx" for="business"></label>
          <label class="lbl" for="business">Business</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="defence" value="Defence" type="checkbox"/>
          <label class="cbx" for="defence"></label>
          <label class="lbl" for="defence">Defence</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="self_employed" value="Self Employed" type="checkbox"/>
          <label class="cbx" for="self_employed"></label>
          <label class="lbl" for="self_employed">Self Employed</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input name="jobCategory" id="un_employed" value="Un Employed" type="checkbox"/>
          <label class="cbx" for="un_employed"></label>
          <label class="lbl" for="un_employed">Un Employed</label>
        </div>
      </section>
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="container mt-5">
          <div class="row">
            <div class="col">
              <div class="dropdown">
                <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton"
                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                  Select Currency
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
                  <input type="text" class="form-control" id="salary" name="salary" placeholder=""
                         required maxlength="15"
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
          <input class="salaryFor" name="salaryFor" id="perAnum" value="Per Anum" type="checkbox"/>
          <label class="cbx" for="perAnum"></label>
          <label class="lbl" for="perAnum">Per Anum</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input class="salaryFor" name="salaryFor" id="perMonth" value="Per Month" type="checkbox"/>
          <label class="cbx" for="perMonth"></label>
          <label class="lbl" for="perMonth">Per Month</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input class="salaryFor" name="salaryFor" id="perWeek" value="Per Week" type="checkbox"/>
          <label class="cbx" for="perWeek"></label>
          <label class="lbl" for="perWeek">Per Week</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input class="salaryFor" name="salaryFor" id="perDay" value="Per Day" type="checkbox"/>
          <label class="cbx" for="perDay"></label>
          <label class="lbl" for="perDay">Per Day</label>
        </div>

        <div class="checkbox-wrapper-42">
          <input class="salaryFor" name="salaryFor" id="perHour" value="Per Hour" type="checkbox"/>
          <label class="cbx" for="perHour"></label>
          <label class="lbl" for="perHour">Per Hour</label>
        </div>
      </section>
      <section class="row colset-2-its d-flex justify-content-center">
        <div class="checkbox-wrapper-42">
          <input name="isCurrent" id="isCurrent" type="checkbox"/>
          <label class="cbx" for="isCurrent"></label>
          <label class="lbl" for="isCurrent">Is Current Job</label>
        </div>
      </section>
      <section class="row row-padding colset-2-its d-flex justify-content-end">
        <div class="d-flex justify-content-end">
          <button class="btn btn-primary"><i class="fas fa-save" aria-hidden="true">&nbsp;&nbsp;Save</i></button>
        </div>
      </section>
    </form>
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    const govt_psu = document.getElementById('govt_psu');
    const private_sector = document.getElementById('private_sector');
    const business = document.getElementById('business');
    const defence = document.getElementById('defence');
    const self_employed = document.getElementById('self_employed');
    const un_employed = document.getElementById('un_employed');

    govt_psu.addEventListener('click', () => {
      private_sector.checked = false;
      business.checked = false;
      defence.checked = false;
      self_employed.checked = false;
      un_employed.checked = false;
    });

    private_sector.addEventListener('click', () => {
      govt_psu.checked = false;
      business.checked = false;
      defence.checked = false;
      self_employed.checked = false;
      un_employed.checked = false;
    });

    business.addEventListener('click', () => {
      govt_psu.checked = false;
      private_sector.checked = false;
      defence.checked = false;
      self_employed.checked = false;
      un_employed.checked = false;
    });

    defence.addEventListener('click', () => {
      govt_psu.checked = false;
      private_sector.checked = false;
      business.checked = false;
      self_employed.checked = false;
      un_employed.checked = false;
    });

    self_employed.addEventListener('click', () => {
      govt_psu.checked = false;
      private_sector.checked = false;
      business.checked = false;
      defence.checked = false;
      un_employed.checked = false;
    });

    un_employed.addEventListener('click', () => {
      govt_psu.checked = false;
      private_sector.checked = false;
      business.checked = false;
      defence.checked = false;
      self_employed.checked = false;
    });

    const searchInput = document.getElementById('dropdownSearch');
    const dropdownItems = document.querySelectorAll('.dropdown-item');
    const dropdownButton = document.querySelector('.dropdown-toggle');

    // Search functionality
    searchInput.addEventListener('keyup', function (e) {
      const searchTerm = e.target.value.toLowerCase();

      dropdownItems.forEach(item => {
        const text = item.textContent.toLowerCase();
        item.style.display = text.includes(searchTerm) ? 'block' : 'none';
      });
    });

    // Update button text when item is selected
    dropdownItems.forEach(item => {
      item.addEventListener('click', function (e) {
        e.preventDefault();
        $("#currencyCode").val($(this).data("currency-code"));
        $("#denomination").val($(this).data("denomination"));
        $("#country").val($(this).data("country"));
        dropdownButton.textContent = this.textContent;
      });
    });

    $('.dropdown').on('shown.bs.dropdown', function () {
      searchInput.focus();
    });

    const perAnum = document.getElementById("perAnum");
    const perMonth = document.getElementById("perMonth");
    const perWeek = document.getElementById("perWeek");
    const perDay = document.getElementById("perDay");
    const perHour = document.getElementById("perHour");

    perAnum.addEventListener('click', () => {
      perMonth.checked = false;
      perWeek.checked = false;
      perDay.checked = false;
      perHour.checked = false;
    });
    perMonth.addEventListener('click', () => {
      perAnum.checked = false;
      perWeek.checked = false;
      perDay.checked = false;
      perHour.checked = false;
    });
    perWeek.addEventListener('click', () => {
      perAnum.checked = false;
      perMonth.checked = false;
      perDay.checked = false;
      perHour.checked = false;
    });
    perDay.addEventListener('click', () => {
      perAnum.checked = false;
      perMonth.checked = false;
      perWeek.checked = false;
      perHour.checked = false;
    });
    perHour.addEventListener('click', () => {
      perAnum.checked = false;
      perMonth.checked = false;
      perWeek.checked = false;
      perDay.checked = false;
    });
  });
</script>
</body>
</html>
