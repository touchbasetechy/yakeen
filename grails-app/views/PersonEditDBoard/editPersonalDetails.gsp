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
    <a href="${createLink(controller: 'person', action: 'index')}"
       class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <a
      href="${createLink(controller: 'PersonEditDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
    <h4 class="same-line">Personal Details</h4>
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
      action="${createLink(controller: 'PersonEditDBoard', action: 'editAndSavePersonalDetails', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
      method="post">
      <section class="row row-padding">
        <div id="personalMessage"></div>

        <div class="col-sm-1"><div class="input-container">
          <input type="text" class="form-control" id="day" name="day" placeholder=""
                 value="${personDetails?.dob?.dayOfMonth}" required autofocus maxlength="2" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric">
          <label for="day" class="form-label">Day</label>
        </div></div>

        <div class="col-sm-1"><div class="form-group">
          <g:set var="monthOptions" value="${[
            1 : 'JANUARY',
            2 : 'FEBRUARY',
            3 : 'MARCH',
            4 : 'APRIL',
            5 : 'MAY',
            6 : 'JUNE',
            7 : 'JULY',
            8 : 'AUGUST',
            9 : 'SEPTEMBER',
            10: 'OCTOBER',
            11: 'NOVEMBER',
            12: 'DECEMBER'
          ]}"/>
          <g:set var="selectedMonth" value="${personDetails?.dob?.monthValue ?: 1}"/>

          <select class="form-select" id="month" name="month">
            <g:each in="${monthOptions}" var="month">
              <option value="${month.key}" ${month.key == selectedMonth ? 'selected' : ''}>
                ${month.value}
              </option>
            </g:each>
          </select>
        </div></div>

        <div class="col-sm-1"><div class="input-container">
          <input type="text" class="form-control" id="year" name="year" placeholder=""
                 value="${personDetails?.dob?.year}" required maxlength="4" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric">
          <label for="year" class="form-label">Year</label>
        </div></div>
      </section>

      <section class="row row-padding">
        <g:if test="${!personDetails}">
          <div class="col-2">
            <div class="checkbox-wrapper-42">
              <g:if test="${personDetails?.gender == 'Male'}">
                <input class="form-check-input" type="checkbox" value="Male" id="Male"
                       name="gender">
              </g:if>
              <g:elseif test="${!personDetails?.gender}">
                <input class="form-check-input" type="checkbox" value="Male" id="Male"
                       name="gender">
              </g:elseif>
              <label class="cbx" for="Male"></label>
              <label class="lbl" for="Male">Male</label>
            </div>
          </div>

          <div class="col-2">
            <div class="checkbox-wrapper-42">

              <g:if test="${personDetails?.gender == 'Female'}">
                <input class="form-check-input" type="checkbox" value="Female" id="Female"
                       name="gender">
              </g:if>
              <g:elseif test="${!personDetails?.gender}">
                <input class="form-check-input" type="checkbox" value="Female" id="Female"
                       name="gender">
              </g:elseif>
              <label class="cbx" for="Female"></label>
              <label class="lbl" for="Female">Female</label>
            </div>
          </div>
        </g:if>
        <g:else>
          <div class="col-12 greyed-out">
            <div class="row">
              <div class="col-2">
                <div class="checkbox-wrapper-42">
                  <g:if test="${personDetails?.gender == 'Male'}">
                    <input class="form-check-input" type="checkbox" value="Male" id="Male"
                           name="gender" checked disabled>
                  </g:if>
                  <g:elseif test="${!personDetails?.gender}">
                    <input class="form-check-input" type="checkbox" value="Male" id="Male"
                           name="gender" disabled>
                  </g:elseif>
                  <label class="cbx" for="Male"></label>
                  <label class="lbl" for="Male">Male</label>
                </div>
              </div>

              <div class="col-2">
                <div class="checkbox-wrapper-42">

                  <g:if test="${personDetails?.gender == 'Female'}">
                    <input class="form-check-input" type="checkbox" value="Female" id="Female"
                           name="gender" checked disabled>
                  </g:if>
                  <g:elseif test="${!personDetails?.gender}">
                    <input class="form-check-input" type="checkbox" value="Female" id="Female"
                           name="gender" disabled>
                  </g:elseif>
                  <label class="cbx" for="Female"></label>
                  <label class="lbl" for="Female">Female</label>
                </div>
              </div>

              <div class="col d-flex justify-content-end">
                <i class="fa-solid fa-circle-info blue-icon"></i>

                <div
                  class="d-flex align-items-center err-msg">Fields inside the grayed out area are not editable !
                </div>
              </div>
            </div>
          </div>
        </g:else>
      </section>

      <section class="row row-padding">
        <div class="col-sm-2"><div class="form-select">
          <g:set var="selectedReligion" value="${personDetails?.religion}"/>
          <select class="form-select" id="religion" name="religion" aria-label="Select a Religion">
            <g:each in="${religions}" var="religion">
              <option value="${religion?.name}">
                ${religion?.name}
              </option>
            </g:each>
          </select>
        </div></div>

        <div class="col-sm-2"><div class="form-select">
          <select class="form-select" id="caste" name="caste" aria-label="Select a Caste">
          </select>
        </div></div>
      </section>

      <section class="row row-padding">
        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.smokingHabit == 'Non Smoker'}">
            <input type="checkbox" value="Non Smoker" id="NonSmoker" name="smokingHabit"
                   checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Non Smoker" id="NonSmoker"
                   name="smokingHabit"/>
          </g:else>
          <label class="cbx" for="NonSmoker"></label>
          <label class="lbl" for="NonSmoker">Non Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.smokingHabit == 'Social Smoker'}">
            <input type="checkbox" value="Social Smoker" id="SocialSmoker" name="smokingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Social Smoker" id="SocialSmoker" name="smokingHabit"/>
          </g:else>
          <label class="cbx" for="SocialSmoker"></label>
          <label class="lbl" for="SocialSmoker">Social Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.smokingHabit == 'Normal Smoker'}">
            <input type="checkbox" value="Normal Smoker" id="NormalSmoker" name="smokingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Normal Smoker" id="NormalSmoker" name="smokingHabit"/>
          </g:else>
          <label class="cbx" for="NormalSmoker"></label>
          <label class="lbl" for="NormalSmoker">Normal Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.smokingHabit == 'Chain Smoker'}">
            <input type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit"/>
          </g:else>
          <label class="cbx" for="ChainSmoker"></label>
          <label class="lbl" for="ChainSmoker">Chain Smoker</label>
        </div></div>
      </section>

      <section class="row row-padding">
        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Non Alcoholic'}">
            <input type="checkbox" value="Non Alcoholic" id="NonDrinker" name="drinkingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Non Alcoholic" id="NonDrinker" name="drinkingHabit"/>
          </g:else>
          <label class="cbx" for="NonDrinker"></label>
          <label class="lbl" for="NonDrinker">Non Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Social Drinker'}">
            <input type="checkbox" value="Social Drinker" id="SocialDrinker"
                   name="drinkingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Social Drinker" id="SocialDrinker"
                   name="drinkingHabit"/>
          </g:else>
          <label class="cbx" for="SocialDrinker"></label>
          <label class="lbl" for="SocialDrinker">Social Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Normal Drinker'}">
            <input type="checkbox" value="Normal Drinker" id="NormalDrinker"
                   name="drinkingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Normal Drinker" id="NormalDrinker"
                   name="drinkingHabit"/>
          </g:else>
          <label class="cbx" for="NormalDrinker"></label>
          <label class="lbl" for="NormalDrinker">Normal Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Alcoholic'}">
            <input type="checkbox" value="Alcoholic" id="HeavyDrinker" name="drinkingHabit" checked/>
          </g:if>
          <g:else>
            <input type="checkbox" value="Alcoholic" id="HeavyDrinker" name="drinkingHabit"/>
          </g:else>
          <label class="cbx" for="HeavyDrinker"></label>
          <label class="lbl" for="HeavyDrinker">Alcoholic</label>
        </div></div>
      </section>

      <div class="d-flex justify-content-end">
        <button class="btn btn-primary toolbar-button" type="submit"><i class="fas fa-save"
                                                                        aria-hidden="true">&nbsp;save</i>
        </button>
      </div>

    </form>

  </div>
</div>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      const alert = document.getElementById('alert');
      if (alert) {
        alert.style.display = 'none';
      }
    }, 6000);

    const religionSelect = document.getElementById("religion");
    const casteSelect = document.getElementById("caste");

    const religionsData = [
      <g:each in="${religions}" var="religion" status="i">
      {
        name: "${raw(religion.name)}",
        caste: [
          <g:each in="${religion.caste}" var="caste" status="j">
          "${raw(caste)}"${j < religion.caste.size() - 1 ? ',' : ''}
          </g:each>
        ]
      }${i < religions.size() - 1 ? ',' : ''}
      </g:each>
    ];


    // Initial population when page loads
    if (religionSelect.value) {
      populateCaste(religionSelect.value);
    }

    // Event listener for religion change
    religionSelect.addEventListener('change', function (ev) {
      populateCaste(ev.target.value);
    });

    // Function to populate caste dropdown
    function populateCaste(selectedReligionName) {
      // Clear existing options
      casteSelect.innerHTML = '';

      // Find the selected religion
      const selectedReligion =
        religionsData.find(religion => religion.name === selectedReligionName);

      if (selectedReligion && selectedReligion.caste) {
        // Add caste options
        selectedReligion.caste.forEach(caste => {
          const option = document.createElement('option');
          option.value = caste;
          option.textContent = caste;
          casteSelect.appendChild(option);
        });

        // Set previously selected caste if available
        const selectedCaste = "${personDetails?.caste}";
        if (selectedCaste && selectedReligion.name === "${personDetails?.religion}") {
          casteSelect.value = selectedCaste;
        }
      }
    }

    // Gender checkboxes
    const male = document.getElementById('Male');
    const female = document.getElementById('Female');

    if (male && female) {
      male.addEventListener('click', () => {
        if (male.checked) female.checked = false;
      });

      female.addEventListener('click', () => {
        if (female.checked) male.checked = false;
      });
    }

    // Smoking habit checkboxes
    const smokingCheckboxes = ['NonSmoker', 'SocialSmoker', 'NormalSmoker', 'ChainSmoker']
      .map(id => document.getElementById(id))
      .filter(checkbox => checkbox !== null);

    smokingCheckboxes.forEach(checkbox => {
      checkbox.addEventListener('click', function () {
        if (this.checked) {
          smokingCheckboxes.forEach(cb => {
            if (cb !== this) cb.checked = false;
          });
        }
      });
    });

    // Drinking habit checkboxes
    const drinkingCheckboxes = ['NonDrinker', 'SocialDrinker', 'NormalDrinker', 'HeavyDrinker']
      .map(id => document.getElementById(id))
      .filter(checkbox => checkbox !== null);

    drinkingCheckboxes.forEach(checkbox => {
      checkbox.addEventListener('click', function () {
        if (this.checked) {
          drinkingCheckboxes.forEach(cb => {
            if (cb !== this) cb.checked = false;
          });
        }
      });
    });
  });
</script>

</body>
</html>
