<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-hat-wizard"><span class="label-text">Admin
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
      href="${createLink(controller: 'person', action: 'updateGeneralDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
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
      action="${createLink(controller: 'person', action: 'savePersonalDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
      method="post">
      <section class="row row-padding colset-2-its">
        <div id="personalMessage"></div>

        <div class="col-sm-1"><div class="input-container">
          <input type="text" class="form-control" id="day" name="day" placeholder="" required autofocus
                 value="${personDetails?.dob?.dayOfMonth}" maxlength="2" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric" min="18" max="99">
          <label class="form-label" for="day">Day</label>
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
          <input type="text" class="form-control" id="year" name="year" placeholder="" required
                 value="${personDetails?.dob?.year}" maxlength="4" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric" min="19" max="99">
          <label class="form-label" for="year">Year</label>
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

      <section class="row row-padding colset-2-its">
        <div class="col-sm-2"><div class="form-select">
          <g:set var="selectedReligion" value="${personalDetails?.religion}"/>
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
        <div class="col-sm-2">
          <div class="checkbox-wrapper-42">
            <g:if test="${personDetails?.smokingHabit == 'Non Smoker'}">
              <input class="form-check-input" type="checkbox" value="Non Smoker" id="NonSmoker" name="smokingHabit"
                     checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="Non Smoker" id="NonSmoker" name="smokingHabit">
            </g:else>

            <label class="cbx" for="NonSmoker"></label>
            <label class="lbl" for="NonSmoker">Non Smoker</label>
          </div>
        </div>

        <div class="col-sm-2">
          <div class="checkbox-wrapper-42">
            <g:if test="${personDetails?.smokingHabit == 'Social Smoker'}">
              <input class="form-check-input" type="checkbox" value="Social Smoker" id="SocialSmoker"
                     name="smokingHabit"
                     checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="Social Smoker" id="SocialSmoker"
                     name="smokingHabit">
            </g:else>

            <label class="cbx" for="SocialSmoker"></label>
            <label class="lbl" for="SocialSmoker">Social Smoker</label>
          </div>
        </div>

        <div class="col-sm-2">
          <div class="checkbox-wrapper-42">
            <g:if test="${personDetails?.smokingHabit == 'Normal Smoker'}">
              <input class="form-check-input" type="checkbox" value="Normal Smoker" id="NormalSmoker"
                     name="smokingHabit"
                     checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="Normal Smoker" id="NormalSmoker"
                     name="smokingHabit">
            </g:else>

            <label class="cbx" for="NormalSmoker"></label>
            <label class="lbl" for="NormalSmoker">Normal Smoker</label>
          </div>
        </div>

        <div class="col-sm-2">
          <div class="checkbox-wrapper-42">
            <g:if test="${personDetails?.smokingHabit == 'Chain Smoker'}">
              <input class="form-check-input" type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit"
                     checked>
            </g:if>
            <g:else>
              <input class="form-check-input" type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit">
            </g:else>

            <label class="cbx" for="ChainSmoker"></label>
            <label class="lbl" for="ChainSmoker">Chain Smoker</label>
          </div>
        </div>
      </section>

      <section class="row row-padding colset-2-its">
        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Non Drinker'}">
            <input class="form-check-input" type="checkbox" value="Non Drinker" id="NonDrinker" name="drinkingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Non Drinker" id="NonDrinker" name="drinkingHabit">
          </g:else>

          <label class="cbx" for="NonDrinker"></label>
          <label class="lbl" for="NonDrinker">Non Alcoholic</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Social Drinker'}">
            <input class="form-check-input" type="checkbox" value="Social Drinker" id="SocialDrinker"
                   name="drinkingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Social Drinker" id="SocialDrinker"
                   name="drinkingHabit">
          </g:else>

          <label class="cbx" for="SocialDrinker"></label>
          <label class="lbl" for="SocialDrinker">Social Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Normal Drinker'}">
            <input class="form-check-input" type="checkbox" value="Normal Drinker" id="NormalDrinker"
                   name="drinkingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Normal Drinker" id="NormalDrinker"
                   name="drinkingHabit">
          </g:else>

          <label class="cbx" for="NormalDrinker"></label>
          <label class="lbl" for="NormalDrinker">Normal Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${personDetails?.drinkingHabit == 'Alcoholic'}">
            <input class="form-check-input" type="checkbox" value="Alcoholic" id="HeavyDrinker" name="drinkingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Alcoholic" id="HeavyDrinker" name="drinkingHabit">
          </g:else>

          <label class="cbx" for="HeavyDrinker"></label>
          <label class="lbl" for="HeavyDrinker">Alcoholic</label>

        </div></div>
      </section>

      <div class="d-flex justify-content-end">
        <button class="btn btn-primary toolbar-button" type="submit"><span class="fas fa-save"
                                                                           aria-hidden="true">&nbsp;Save</span>
        </button>
      </div>

    </form>

  </div>
</div>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
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

    // Handle gender checkboxes - works for both enabled and disabled cases
    const male = document.getElementById('Male');
    const female = document.getElementById('Female');

    if (male && female) {
      male.addEventListener('click', () => {
        if (!male.disabled) {
          female.checked = false;
        }
      });

      female.addEventListener('click', () => {
        if (!female.disabled) {
          male.checked = false;
        }
      });
    }

    // Handle smoking habits
    const nonsmoker = document.getElementById('NonSmoker');
    const socialsmoker = document.getElementById('SocialSmoker');
    const normalsmoker = document.getElementById('NormalSmoker');
    const chainsmoker = document.getElementById('ChainSmoker');

    if (nonsmoker) {
      nonsmoker.addEventListener('click', () => {
        socialsmoker.checked = false;
        normalsmoker.checked = false;
        chainsmoker.checked = false;
      });
    }

    if (socialsmoker) {
      socialsmoker.addEventListener('click', () => {
        nonsmoker.checked = false;
        normalsmoker.checked = false;
        chainsmoker.checked = false;
      });
    }

    if (normalsmoker) {
      normalsmoker.addEventListener('click', () => {
        nonsmoker.checked = false;
        socialsmoker.checked = false;
        chainsmoker.checked = false;
      });
    }

    if (chainsmoker) {
      chainsmoker.addEventListener('click', () => {
        nonsmoker.checked = false;
        socialsmoker.checked = false;
        normalsmoker.checked = false;
      });
    }

    // Handle drinking habits
    const nondrinker = document.getElementById('NonDrinker');
    const socialdrinker = document.getElementById('SocialDrinker');
    const normaldrinker = document.getElementById('NormalDrinker');
    const heavydrinker = document.getElementById('HeavyDrinker');

    if (nondrinker) {
      nondrinker.addEventListener('click', () => {
        socialdrinker.checked = false;
        normaldrinker.checked = false;
        heavydrinker.checked = false;
      });
    }

    if (socialdrinker) {
      socialdrinker.addEventListener('click', () => {
        nondrinker.checked = false;
        normaldrinker.checked = false;
        heavydrinker.checked = false;
      });
    }

    if (normaldrinker) {
      normaldrinker.addEventListener('click', () => {
        nondrinker.checked = false;
        socialdrinker.checked = false;
        heavydrinker.checked = false;
      });
    }

    if (heavydrinker) {
      heavydrinker.addEventListener('click', () => {
        nondrinker.checked = false;
        socialdrinker.checked = false;
        normaldrinker.checked = false;
      });
    }
  });
</script>

</body>
</html>
