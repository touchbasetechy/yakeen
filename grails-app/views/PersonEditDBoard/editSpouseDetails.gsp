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
    <h4 class="same-line">Spouse Preferences</h4>

    <div class="same-line col d-flex justify-content-end">
      <div class="form-group">
        <label class="sleek-toggle">
          <input class="toggle-input" type="checkbox" id="sameAsPersonalDetails"
                 <g:if test="${!personDetails}">disabled</g:if>>
          <span class="toggle-track">
            <span class="toggle-thumb"></span>
            <span class="toggle-icons">
              <span class="icon-on">✓</span>
              <span class="icon-off">✕</span>
            </span>
          </span>
          <span class="toggle-label">Same as Personal Details</span>
        </label>
      </div>
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
    <form
      action="${createLink(controller: 'PersonEditDBoard', action: 'editAndSaveSpouseDetails', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
      method="post">
      <section class="row row-padding">

        <div class="col-sm-1 mb-3"><div class="input-container">
          <input type="text" class="form-control" id="minAge" name="minAge" placeholder="" required autofocus
                 value="${spouseDetails?.minAge}" maxlength="2" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric" min="18" max="99">
          <label for="minAge" class="form-label">From</label>
        </div></div>

        <div class="col-sm-1 mb-3"><div class="input-container">
          <input type="text" class="form-control" id="maxAge" name="maxAge" placeholder="" required
                 value="${spouseDetails?.maxAge}" maxlength="2" pattern="\d*"
                 oninput="this.value = this.value.replace(/[^\d]/g, '')" inputmode="numeric" min="19" max="99">
          <label for="maxAge" class="form-label">To</label>
        </div></div>
      </section>
      <section class="row row-padding d-none" id="errorMessage">
        <div class="col-sm-12 mb-3 d-flex justify-content-start">
          <div style="color:red; font-size: 0.9em;">"From" age must be less than "To" age! <a
            href="#" onclick="swapValues();
            return false;"><strong>Swap</strong></a></div>
        </div>
      </section>

      <section class="row row-padding">
        <g:if test="${!personDetails?.gender}">
          <div class="col-2">
            <div class="checkbox-wrapper-42">
              <input class="form-check-input" type="checkbox" value="Male" id="newMale"
                     name="gender">
              <label class="cbx" for="newMale"></label>
              <label class="lbl" for="newMale">Male</label>
            </div>
          </div>

          <div class="col-2">
            <div class="checkbox-wrapper-42">
              <input class="form-check-input" type="checkbox" value="Female" id="newFemale"
                     name="gender">
              <label class="cbx" for="newFemale"></label>
              <label class="lbl" for="newFemale">Female</label>
            </div>
          </div>
        </g:if>
        <g:else>
          <div class="col-12 greyed-out">
            <div class="row">
              <div class="col-2">
                <div class="checkbox-wrapper-42">
                  <g:if test="${!personDetails?.gender}">
                    <input class="form-check-input" type="checkbox" value="Male" id="Male"
                           name="gender" disabled>
                  </g:if>
                  <g:elseif test="${personDetails?.gender == 'Female'}">
                    <input class="form-check-input" type="checkbox" value="Male" id="Male"
                           name="gender" checked disabled>
                  </g:elseif>

                  <label class="cbx" for="Male"></label>
                  <label class="lbl" for="Male">Male</label>
                </div>
              </div>

              <div class="col-2">
                <div class="checkbox-wrapper-42">
                  <g:if test="${!personDetails?.gender}">
                    <input class="form-check-input" type="checkbox" value="Female" id="Female"
                           name="gender" disabled>
                  </g:if>
                  <g:elseif test="${personDetails?.gender == 'Male'}">
                    <input class="form-check-input" type="checkbox" value="Female" id="Female"
                           name="gender" checked disabled>
                  </g:elseif>

                  <label class="cbx" for="Female"></label>
                  <label class="lbl" for="Female">Female</label>
                </div>
              </div>

              <div class="col d-flex justify-content-end">
                <i class="fa-solid fa-circle-info blue-icon"></i>

                <div
                  class="d-flex align-items-center err-msg">Fields inside the grayed out area are not editable !</div>
              </div>
            </div>
          </div>
        </g:else>
      </section>

      <section class="row row-padding">
        <div class="col-sm-2"><div class="form-select">
          <g:set var="selectedReligion" value="${spouseDetails?.religion}"/>
          <select class="form-select" id="religion" name="religion" aria-label="Select a Religion">
            <option value="Doesn't Matter">Doesn't Matter</option>
            <g:each in="${religions}" var="religion">
              <option value="${religion?.name}" ${selectedReligion == religion?.name ? 'selected' : ''}>
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
          <g:if test="${spouseDetails?.smokingHabit == 'Non Smoker'}">
            <input class="form-check-input" type="checkbox" value="Non Smoker" id="NonSmoker" name="smokingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Non Smoker" id="NonSmoker" name="smokingHabit">
          </g:else>

          <label class="cbx" for="NonSmoker"></label>
          <label class="lbl" for="NonSmoker">Non Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${spouseDetails?.smokingHabit == 'Social Smoker'}">
            <input class="form-check-input" type="checkbox" value="Social Smoker" id="SocialSmoker" name="smokingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Social Smoker" id="SocialSmoker" name="smokingHabit">
          </g:else>

          <label class="cbx" for="SocialSmoker"></label>
          <label class="lbl" for="SocialSmoker">Social Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${spouseDetails?.smokingHabit == 'Normal Smoker'}">
            <input class="form-check-input" type="checkbox" value="Normal Smoker" id="NormalSmoker" name="smokingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Normal Smoker" id="NormalSmoker" name="smokingHabit">
          </g:else>

          <label class="cbx" for="NormalSmoker"></label>
          <label class="lbl" for="NormalSmoker">Normal Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${spouseDetails?.smokingHabit == 'Chain Smoker'}">
            <input class="form-check-input" type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Chain Smoker" id="ChainSmoker" name="smokingHabit">
          </g:else>

          <label class="cbx" for="ChainSmoker"></label>
          <label class="lbl" for="ChainSmoker">Chain Smoker</label>
        </div></div>
      </section>

      <section class="row row-padding">
        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${spouseDetails?.drinkingHabit == 'Non Alcoholic'}">
            <input class="form-check-input" type="checkbox" value="Non Drinker" id="NonDrinker" name="drinkingHabit"
                   checked>
          </g:if>
          <g:else>
            <input class="form-check-input" type="checkbox" value="Non Drinker" id="NonDrinker" name="drinkingHabit">
          </g:else>

          <label class="cbx" for="NonDrinker"></label>
          <label class="lbl" for="NonDrinker">Non Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="checkbox-wrapper-42">
          <g:if test="${spouseDetails?.drinkingHabit == 'Social Drinker'}">
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
          <g:if test="${spouseDetails?.drinkingHabit == 'Normal Drinker'}">
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
          <g:if test="${spouseDetails?.drinkingHabit == 'Alcoholic'}">
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
        <button class="btn btn-primary toolbar-button" type="submit"><i class="fas fa-save"
                                                                        aria-hidden="true" data-ripple>&nbsp;save</i>
        </button>
      </div>
    </form>

  </div>
</div>

<script type="text/javascript">
  var errorFlag = false;

  var religion = "${spouseDetails?.religion}";
  var caste = "${spouseDetails?.caste}";
  var smokingHabit = "${spouseDetails?.smokingHabit}";
  var drinkingHabit = "${spouseDetails?.drinkingHabit}";


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

    const sameAsCheckBox = document.getElementById('sameAsPersonalDetails');

    // Initial population when page loads
    if (religionSelect.value) {
      populateCaste(religionSelect.value);
    }

    religionSelect.addEventListener('change', function (e) {
      religion = e.target.value;
      populateCaste(religion);
    });

    casteSelect.addEventListener('change', function (e) {
      caste = e.target.value;
    });

    sameAsCheckBox.checked = (religion === "${personDetails?.religion}") && (caste === "${personDetails?.caste}") && (smokingHabit === "${personDetails?.smokingHabit}") && (drinkingHabit === "${personDetails?.drinkingHabit}")

    sameAsCheckBox.addEventListener('change', function (ev) {
      if (ev.target.checked) {
        religion = religionSelect.value;
        caste = casteSelect.value;

        religionSelect.value = "${personDetails?.religion}";
        populateCaste("${personDetails?.religion}");

        casteSelect.value = "${personDetails?.caste}";

        nonsmoker.checked =
          "${personDetails?.smokingHabit}" ===
          "Non Smoker"
        socialsmoker.checked =
          "${personDetails?.smokingHabit}" ===
          "Social Smoker"
        normalsmoker.checked =
          "${personDetails?.smokingHabit}" ===
          "Normal Smoker"
        chainsmoker.checked =
          "${personDetails?.smokingHabit}" ===
          "Chain Smoker"

        nondrinker.checked =
          "${personDetails?.drinkingHabit}" ===
          "Non Drinker"
        socialdrinker.checked =
          "${personDetails?.drinkingHabit}" ===
          "Social Drinker"
        normaldrinker.checked =
          "${personDetails?.drinkingHabit}" ===
          "Normal Drinker"
        heavydrinker.checked =
          "${personDetails?.drinkingHabit}" ===
          "Alcoholic"
      } else {
        religionSelect.value = religion;
        populateCaste(religion);
        casteSelect.value = caste;

        nonsmoker.checked =
          smokingHabit ===
          "Non Smoker"
        socialsmoker.checked =
          smokingHabit ===
          "Social Smoker"
        normalsmoker.checked =
          smokingHabit ===
          "Normal Smoker"
        chainsmoker.checked =
          smokingHabit ===
          "Chain Smoker"

        nondrinker.checked =
          drinkingHabit ===
          "Non Drinker"
        socialdrinker.checked =
          drinkingHabit ===
          "Social Drinker"
        normaldrinker.checked =
          drinkingHabit ===
          "Normal Drinker"
        heavydrinker.checked =
          drinkingHabit ===
          "Alcoholic"
      }
    });

    // Function to populate caste dropdown
    function populateCaste(selectedReligionName) {
      // Clear existing options
      casteSelect.innerHTML = '<option value="Doesn\'t Matter">Doesn\'t Matter</option>';

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
        const selectedCaste = "${spouseDetails?.caste}";
        if (selectedCaste && selectedReligion.name === "${spouseDetails?.religion}") {
          casteSelect.value = selectedCaste;
        }
      }
    }
  });

  // <!--row 1-->
  // Get all text inputs
  const textInputs = document.querySelectorAll('input[type="text"]');

  // Add focus event listener to each
  textInputs.forEach(input => {
    input.addEventListener('focus', function (event) {
      const focusedField = event.target; // Reference to the focused field
      // You can store this reference for later use
      window.currentFocusedField = focusedField;
    });
  });

  const firstInput = document.getElementById('minAge');
  const secondInput = document.getElementById('maxAge');
  const errorElement = document.getElementById('errorMessage');

  function validateNumbers() {
    const firstValue = parseFloat(firstInput.value);
    const secondValue = parseFloat(secondInput.value);

    if (isNaN(firstValue) || isNaN(secondValue)) {
      errorFlag = false;
      return;
    }

    if ((firstValue >= secondValue) && (secondValue > 18)) {
      errorFlag = true;
      errorElement.className = "row row-padding animate__animated animate__fadeInDown";//.style.display = 'block';
      secondInput.setCustomValidity("Second number must be greater");
    } else if (errorFlag) {
      errorElement.className = "row row-padding slide-up-fade-out";//.style.display = 'none';
      errorFlag = false;
      secondInput.setCustomValidity("");
    }
  }

  function swapValues() {
    const secondValue = secondInput.value;
    secondInput.value = firstInput.value;
    firstInput.value = secondValue;
    validateNumbers();
    setTimeout(() => {
      window.currentFocusedField.focus();
    }, 0);
  }

  firstInput.addEventListener('input', validateNumbers);
  secondInput.addEventListener('input', validateNumbers);

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

  <!--row 2-->
  const nonsmoker = document.getElementById('NonSmoker');
  const socialsmoker = document.getElementById('SocialSmoker');
  const normalsmoker = document.getElementById('NormalSmoker');
  const chainsmoker = document.getElementById('ChainSmoker');

  nonsmoker.addEventListener('click', (ev) => {
    smokingHabit = ev.target.value;

    socialsmoker.checked = false;
    normalsmoker.checked = false;
    chainsmoker.checked = false;
  });

  socialsmoker.addEventListener('click', (ev) => {
    smokingHabit = ev.target.value;

    nonsmoker.checked = false;
    normalsmoker.checked = false;
    chainsmoker.checked = false;
  });

  normalsmoker.addEventListener('click', (ev) => {
    smokingHabit = ev.target.value;

    nonsmoker.checked = false;
    socialsmoker.checked = false;
    chainsmoker.checked = false;
  });

  chainsmoker.addEventListener('click', (ev) => {
    smokingHabit = ev.target.value;

    nonsmoker.checked = false;
    socialsmoker.checked = false;
    normalsmoker.checked = false;
  });

  <!--row 3-->
  const nondrinker = document.getElementById('NonDrinker');
  const socialdrinker = document.getElementById('SocialDrinker');
  const normaldrinker = document.getElementById('NormalDrinker');
  const heavydrinker = document.getElementById('HeavyDrinker');

  nondrinker.addEventListener('click', (ev) => {
    drinkingHabit = ev.target.value;

    socialdrinker.checked = false;
    normaldrinker.checked = false;
    heavydrinker.checked = false;
  });

  socialdrinker.addEventListener('click', (ev) => {
    drinkingHabit = ev.target.value;

    nondrinker.checked = false;
    normaldrinker.checked = false;
    heavydrinker.checked = false;
  });

  normaldrinker.addEventListener('click', (ev) => {
    drinkingHabit = ev.target.value;

    nondrinker.checked = false;
    socialdrinker.checked = false;
    heavydrinker.checked = false;
  });

  heavydrinker.addEventListener('click', (ev) => {
    drinkingHabit = ev.target.value;

    nondrinker.checked = false;
    socialdrinker.checked = false;
    normaldrinker.checked = false;
  });
</script>
</body>
</html>
