<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>

</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-edit"><span class="label-text">Super Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
       class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
    <g:if test="${!params?.pt}">
      <a
        href="${createLink(controller: 'editDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
    </g:if>
    <g:else>
      <a
        href="${createLink(controller: 'editDBoard', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
    </g:else>
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
      action="${createLink(controller: 'editDBoard', action: 'editAndSavePersonalDetails', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
      method="post">
      <section class="row colset-2-its">
        <div id="personalMessage"></div>

        <div class="col-sm-2">Date of Birth</div>

        <div class="col-sm-1"><div class="form-group">
          <label class="form-text-label" for="day">Day</label>
          <input type="text" class="form-control" id="day" name="day" placeholder="DD" required
                 value= ${session?.personToEdit?.personDetails?.dob?.dayOfMonth}>
        </div></div>

        <div class="col-sm-1"><div class="form-group">
          <input type="hidden" value=${session?.personToEdit?.personDetails?.dob?.month} id="monthOfDOB"
                 name="monthOfDOB"/>
          <select class="form-select" id="month" name="month" aria-label="Month">
            <option value="JANUARY">JANUARY</option>
            <option value="FEBRUARY">FEBRUARY</option>
            <option value="MARCH">MARCH</option>
            <option value="APRIL">APRIL</option>
            <option value="MAY">MAY</option>
            <option value="JUNE">JUNE</option>
            <option value="JULY">JULY</option>
            <option value="AUGUST">AUGUST</option>
            <option value="SEPTEMBER">SEPTEMBER</option>
            <option value="OCTOBER">OCTOBER</option>
            <option value="NOVEMBER">NOVEMBER</option>
            <option value="DECEMBER">DECEMBER</option>
          </select>
          <g:hiddenField name="month" value="${params?.month}"/>
        </div></div>

        <div class="col-sm-1"><div class="form-group">
          <label class="form-text-label" for="year">Year</label>
          <input type="text" class="form-control" id="year" name="year" placeholder="YYYY" required
                 value= ${session?.personToEdit?.personDetails?.dob?.year}>
        </div></div>
      </section>

      <section class="row colset-2-its">
        <div class="col-sm-2">Gender</div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.gender == 0}">
            <input class="form-check-input" type="checkbox" value="on" id="Male" name="Male" checked disabled>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.gender == 1}">
            <input class="form-check-input" type="checkbox" value="" id="Male" name="Male" disabled>
          </g:if>
          <label class="form-check-label" for="Male">Male</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.gender == 1}">
            <input class="form-check-input" type="checkbox" value="on" id="Female" name="Female" checked disabled>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.gender == 0}">
            <input class="form-check-input" type="checkbox" value="" id="Female" name="Female" disabled>
          </g:if>
          <label class="form-check-label" for="Female">Female</label>
        </div></div>
      </section>

      <section class="row colset-2-its">
        <div class="col-sm-2">Religion</div>

        <div class="col-sm-2"><div class="form-select">
          <select class="form-select" id="Religion" name="Religion" aria-label="Select a Religion">
            <g:if test="${session?.personToEdit?.personDetails?.religion == "[Buddhist, ]"}">
              <option value="Buddhist" selected>Buddhist</option>
            </g:if>
            <g:if test="${session.personToEdit?.personDetails?.religion != "[Buddhist, ]"}">
              <option value="Buddhist">Buddhist</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.religion == "[Christian, ]"}">
              <option value="Christian" selected>Christian</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.religion != "[Christian, ]"}">
              <option value="Christian">Christian</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.religion == "[Hindu, ]"}">
              <option value="Hindu" selected>Hindu</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.religion != "[Hindu, ]"}">
              <option value="Hindu">Hindu</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.religion == "[Jain, ]"}">
              <option value="Jain" selected>Jain</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.religion != "[Jain, ]"}">
              <option value="Jain">Jain</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.religion == "[Muslim, ]"}">
              <option value="Muslim" selected>Muslim</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.religion != "[Muslim, ]"}">
              <option value="Muslim">Muslim</option>
            </g:if>
          </select>
          <g:hiddenField name="Religion" value="${params.Religion}"/>
        </div></div>

        <div class="col-sm-2">Caste</div>

        <div class="col-sm-2"><div class="form-select">
          <select class="form-select" id="Caste" name="Caste" aria-label="Select a Caste">
            <g:if test="${session?.personToEdit?.personDetails?.caste == "[Caste 1, ]"}">
              <option value="Caste 1" selected>Caste 1</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.caste != "[Caste 1, ]"}">
              <option value="Caste 1">Caste 1</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.caste == "[Caste 2, ]"}">
              <option value="Caste 2" selected>Caste 2</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.caste != "[Caste 2, ]"}">
              <option value="Caste 2">Caste 2</option>
            </g:if>

            <g:if test="${session?.personToEdit?.personDetails?.caste == "[Caste 3, ]"}">
              <option value="Caste 3" selected>Caste 3</option>
            </g:if>
            <g:if test="${session?.personToEdit?.personDetails?.caste != "[Caste 3, ]"}">
              <option value="Caste 3">Caste 3</option>
            </g:if>
          </select>
          <g:hiddenField name="Caste" value="${params.Caste}"/>
        </div></div>
      </section>

      <section class="row colset-2-its">
        <div class="col-sm-2">Smoking Habits</div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit == 0}">
            <input class="form-check-input" type="checkbox" value="on" id="NonSmoker" name="NonSmoker" checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit != 0}">
            <input class="form-check-input" type="checkbox" value="" id="NonSmoker" name="NonSmoker">
          </g:if>

          <label class="form-check-label" for="NonSmoker">Non Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit == 1}">
            <input class="form-check-input" type="checkbox" value="on" id="SocialSmoker" name="SocialSmoker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit != 1}">
            <input class="form-check-input" type="checkbox" value="" id="SocialSmoker" name="SocialSmoker">
          </g:if>

          <label class="form-check-label" for="SocialSmoker">Social Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit == 2}">
            <input class="form-check-input" type="checkbox" value="on" id="NormalSmoker" name="NormalSmoker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit != 2}">
            <input class="form-check-input" type="checkbox" value="" id="NormalSmoker" name="NormalSmoker">
          </g:if>

          <label class="form-check-label" for="NormalSmoker">Normal Smoker</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit == 3}">
            <input class="form-check-input" type="checkbox" value="on" id="ChainSmoker" name="ChainSmoker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.smokingHabit != 3}">
            <input class="form-check-input" type="checkbox" value="" id="ChainSmoker" name="ChainSmoker">
          </g:if>

          <label class="form-check-label" for="ChainSmoker">Chain Smoker</label>
        </div></div>
      </section>

      <section class="row colset-2-its">
        <div class="col-sm-2">Drinking Habits</div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit == 0}">
            <input class="form-check-input" type="checkbox" value="on" id="NonDrinker" name="NonDrinker" checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit != 0}">
            <input class="form-check-input" type="checkbox" value="" id="NonDrinker" name="NonDrinker">
          </g:if>

          <label class="form-check-label" for="NonDrinker">Non Alcoholic</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit == 1}">
            <input class="form-check-input" type="checkbox" value="on" id="SocialDrinker" name="SocialDrinker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit != 1}">
            <input class="form-check-input" type="checkbox" value="" id="SocialDrinker" name="SocialDrinker">
          </g:if>

          <label class="form-check-label" for="SocialDrinker">Social Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit == 2}">
            <input class="form-check-input" type="checkbox" value="on" id="NormalDrinker" name="NormalDrinker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit != 2}">
            <input class="form-check-input" type="checkbox" value="" id="NormalDrinker" name="NormalDrinker">
          </g:if>

          <label class="form-check-label" for="NormalDrinker">Normal Drinker</label>
        </div></div>

        <div class="col-sm-2"><div class="form-check">
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit == 3}">
            <input class="form-check-input" type="checkbox" value="on" id="HeavyDrinker" name="HeavyDrinker"
                   checked>
          </g:if>
          <g:if test="${session?.personToEdit?.personDetails?.drinkingHabit != 3}">
            <input class="form-check-input" type="checkbox" value="" id="HeavyDrinker" name="HeavyDrinker">

          </g:if>

          <label class="form-check-label" for="HeavyDrinker">Alcoholic</label>
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

      $("#alert").hide();
    }, 6000);

    var hiddenValue = document.getElementById('monthOfDOB').value;
    var bsSelect = document.getElementById('month');
    console.log(">>>>hidden: " + hiddenValue);

    bsSelect.value = hiddenValue;
    //$('#month').val(hiddenValue).change();
  });

  //<!--row 1-->
  const male = document.getElementById('Male');
  const female = document.getElementById('Female');

  male.addEventListener('click', () => {
    male.value = "on"
    female.checked = false;
  });

  female.addEventListener('click', () => {
    female.value = "on"
    male.checked = false;
  });

  <!--row 2-->
  const nonsmoker = document.getElementById('NonSmoker');
  const socialsmoker = document.getElementById('SocialSmoker');
  const normalsmoker = document.getElementById('NormalSmoker');
  const chainsmoker = document.getElementById('ChainSmoker');

  nonsmoker.addEventListener('click', () => {
    nonsmoker.value = "on"
    socialsmoker.checked = false;
    normalsmoker.checked = false;
    chainsmoker.checked = false;
  });

  socialsmoker.addEventListener('click', () => {
    socialsmoker.value = "on"
    nonsmoker.checked = false;
    normalsmoker.checked = false;
    chainsmoker.checked = false;
  });

  normalsmoker.addEventListener('click', () => {
    normalsmoker.value = "on"
    nonsmoker.checked = false;
    socialsmoker.checked = false;
    chainsmoker.checked = false;
  });

  chainsmoker.addEventListener('click', () => {
    chainsmoker.value = "on"
    nonsmoker.checked = false;
    socialsmoker.checked = false;
    normalsmoker.checked = false;
  });

  <!--row 3-->
  const nondrinker = document.getElementById('NonDrinker');
  const socialdrinker = document.getElementById('SocialDrinker');
  const normaldrinker = document.getElementById('NormalDrinker');
  const heavydrinker = document.getElementById('HeavyDrinker');

  nondrinker.addEventListener('click', () => {
    nondrinker.value = "on"
    socialdrinker.checked = false;
    normaldrinker.checked = false;
    heavydrinker.checked = false;
  });

  socialdrinker.addEventListener('click', () => {
    socialdrinker.value = "on"
    nondrinker.checked = false;
    normaldrinker.checked = false;
    heavydrinker.checked = false;
  });

  normaldrinker.addEventListener('click', () => {
    normaldrinker.value = "on"
    nondrinker.checked = false;
    socialdrinker.checked = false;
    heavydrinker.checked = false;
  });

  heavydrinker.addEventListener('click', () => {
    heavydrinker.value = "on"
    nondrinker.checked = false;
    socialdrinker.checked = false;
    normaldrinker.checked = false;
  });

</script>

</body>
</html>
