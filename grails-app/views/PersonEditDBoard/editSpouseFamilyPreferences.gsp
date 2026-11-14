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
      href="${createLink(controller: 'personEditDBoard', action: 'index', params: [id: params?.id, op: params?.op, con: params?.con])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>


    <h1 class="same-line">Edit Spouse Family Preferences</h1>

    <div class="same-line col d-flex justify-content-end">
      <div class="form-group">
        <label class="sleek-toggle">
          <input class="toggle-input" type="checkbox" id="sameAsFamilyDetails"
                 <g:if test="${!familyDetails}">disabled</g:if>>
          <span class="toggle-track">
            <span class="toggle-thumb"></span>
            <span class="toggle-icons">
              <span class="icon-on">✓</span>
              <span class="icon-off">✕</span>
            </span>
          </span>
          <span class="toggle-label">Same as Family Details</span>
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
      action="${createLink(controller: 'personEditDBoard', action: 'editAndSaveSpouseFamilyPreferences', params: [id: params?.id, op: params?.op, con: params?.con])}"
      method="post">
    <section class="row row-padding colset-2-its">

      <g:if test="${!spouseFamilyDetails}">
        <div class="checkbox-wrapper-42">
          <g:if test="${familyDetails?.maritalStatus == 'UnMarried'}">
            <input name="maritalStatus" id="un_married" value="UnMarried" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="maritalStatus" id="un_married" value="UnMarried" type="checkbox"/>
          </g:else>
          <label class="cbx" for="un_married"></label>
          <label class="lbl" for="un_married">UnMarried</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${familyDetails?.maritalStatus == 'Divorced'}">
            <input name="maritalStatus" id="maritalStatus1" value="Divorced" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="maritalStatus" id="maritalStatus1" value="Divorced" type="checkbox"/>
          </g:else>
          <label class="cbx" for="maritalStatus1"></label>
          <label class="lbl" for="maritalStatus1">Divorced</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${familyDetails?.maritalStatus == 'Awaiting Divorce'}">
            <input name="maritalStatus" id="maritalStatus2" value="Awaiting Divorce" type="checkbox"
                   checked/>
          </g:if>
          <g:else>
            <input name="maritalStatus" id="maritalStatus2" value="Awaiting Divorce" type="checkbox"/>
          </g:else>
          <label class="cbx" for="maritalStatus2"></label>
          <label class="lbl" for="maritalStatus2">Awaiting Divorce</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${familyDetails?.maritalStatus == 'Widowed'}">
            <input name="maritalStatus" id="maritalStatus3" value="Widowed" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="maritalStatus" id="maritalStatus3" value="Widowed" type="checkbox"/>
          </g:else>
          <label class="cbx" for="maritalStatus3"></label>
          <label class="lbl" for="maritalStatus3">Widowed</label>
        </div>
      </g:if>
      <g:else>
        <div class="col-12 greyed-out">
          <div class="row">

            <div class="checkbox-wrapper-42">
              <g:if test="${spouseFamilyDetails?.maritalStatus == 'UnMarried'}">
                <input name="maritalStatus" id="un_married" value="UnMarried" type="checkbox" checked disabled/>
              </g:if>
              <g:else>
                <input name="maritalStatus" id="un_married" value="UnMarried" type="checkbox" disabled/>
              </g:else>
              <label class="cbx" for="un_married"></label>
              <label class="lbl" for="un_married">UnMarried</label>
            </div>

            <div class="checkbox-wrapper-42">
              <g:if test="${spouseFamilyDetails?.maritalStatus == 'Divorced'}">
                <input name="maritalStatus" id="maritalStatus1" value="Divorced" type="checkbox" checked disabled/>
              </g:if>
              <g:else>
                <input name="maritalStatus" id="maritalStatus1" value="Divorced" type="checkbox" disabled/>
              </g:else>
              <label class="cbx" for="maritalStatus1"></label>
              <label class="lbl" for="maritalStatus1">Divorced</label>
            </div>

            <div class="checkbox-wrapper-42">
              <g:if test="${spouseFamilyDetails?.maritalStatus == 'Awaiting Divorce'}">
                <input name="maritalStatus" id="maritalStatus2" value="Awaiting Divorce" type="checkbox" checked
                       disabled/>
              </g:if>
              <g:else>
                <input name="maritalStatus" id="maritalStatus2" value="Awaiting Divorce" type="checkbox" disabled/>
              </g:else>
              <label class="cbx" for="maritalStatus2"></label>
              <label class="lbl" for="maritalStatus2">Awaiting Divorce</label>
            </div>

            <div class="checkbox-wrapper-42">
              <g:if test="${spouseFamilyDetails?.maritalStatus == 'Widowed'}">
                <input name="maritalStatus" id="maritalStatus3" value="Widowed" type="checkbox" checked disabled/>
              </g:if>
              <g:else>
                <input name="maritalStatus" id="maritalStatus3" value="Widowed" type="checkbox" disabled/>
              </g:else>
              <label class="cbx" for="maritalStatus3"></label>
              <label class="lbl" for="maritalStatus3">Widowed</label>
            </div>

            <div class="col d-flex justify-content-end">
              <i class="fa-solid fa-circle-info blue-icon"></i>

              <div
                class="d-flex align-items-center err-msg">Fields inside the grayed out area are not editable !
              </div>
            </div>

          </div>
        </div>
        </section>
        <section class="row row-padding colset-2-its d-none" id="children">
          <div class="checkbox-wrapper-42">
        <g:if test="${spouseFamilyDetails?.children == 'No Children'}">
          <input name="children" id="no_children" value="No Children" type="checkbox" checked/>
        </g:if>
        <g:else>
          <input name="children" id="no_children" value="No Children" type="checkbox"/>
        </g:else>
        <label class="cbx" for="no_children"></label>
        <label class="lbl" for="no_children">No Children</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.children == '1'}">
            <input name="children" id="children1" value="1" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="children" id="children1" value="1" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children1"></label>
          <label class="lbl" for="children1">1</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.children == '2'}">
            <input name="children" id="children2" value="2" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="children" id="children2" value="2" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children2"></label>
          <label class="lbl" for="children2">2</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.children == '3'}">
            <input name="children" id="children3" value="3" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="children" id="children3" value="3" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children3"></label>
          <label class="lbl" for="children3">3</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.children == '4 and more'}">
            <input name="children" id="children4" value="4 and more" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="children" id="children4" value="4 and more" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children4"></label>
          <label class="lbl" for="children4">4 and more</label>
        </div>
      </g:else>
    </section>
      <section class="row row-padding colset-2-its d-none" id="child_status">
        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.childrenStatus == 'Living with me'}">
            <input name="childrenStatus" id="children_status1" value="Living with me" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="childrenStatus" id="children_status1" value="Living with me" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children_status1"></label>
          <label class="lbl" for="children_status1">Living with me</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.childrenStatus == 'Not living with me'}">
            <input name="childrenStatus" id="children_status2" value="Not living with me" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="childrenStatus" id="children_status2" value="Not living with me" type="checkbox"/>
          </g:else>
          <label class="cbx" for="children_status2"></label>
          <label class="lbl" for="children_status2">Not living with me</label>
        </div>
      </section>
      <section class="row row-padding">
        <div class="col-sm-2"><div class="form-select">
          <g:set var="selectedHeight" value="${spouseFamilyDetails?.height}"/>

          <select class="form-select" id="height" name="height" aria-label="Select a Height">
            <option value="Doesn't Matter">Doesn't Matter</option>
            <g:each in="${heights}" var="height">
              <option value="${height?.height}" ${height?.height == selectedHeight ? 'selected' : ''}>
                ${height?.height}
              </option>
            </g:each>
          </select>
        </div></div>

        <div class="col-sm-2"><div class="form-select">
          <g:set var="selectedWeight" value="${familyDetails?.weight}"/>
          <select class="form-select" id="weight" name="weight" aria-label="Select a Weight">
            <option value="Doesn't Matter">Doesn't Matter</option>
          </select>
        </div></div>
      </section>
      <section class="row row-padding">
        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyStatus == 'Lower Middle Class'}">
            <input name="familyStatus" id="family_status1" value="Lower Middle Class" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyStatus" id="family_status1" value="Lower Middle Class" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_status1"></label>
          <label class="lbl" for="family_status1">Lower Middle Class</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyStatus == 'Middle Class'}">
            <input name="familyStatus" id="family_status2" value="Middle Class" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyStatus" id="family_status2" value="Middle Class" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_status2"></label>
          <label class="lbl" for="family_status2">Middle Class</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyStatus == 'Upper Middle Class'}">
            <input name="familyStatus" id="family_status3" value="Upper Middle Class" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyStatus" id="family_status3" value="Upper Middle Class" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_status3"></label>
          <label class="lbl" for="family_status3">Upper Middle Class</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyStatus == 'High Class'}">
            <input name="familyStatus" id="family_status4" value="High Class" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyStatus" id="family_status4" value="High Class" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_status4"></label>
          <label class="lbl" for="family_status4">High Class</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyStatus == 'Rich / Affluent'}">
            <input name="familyStatus" id="family_status5" value="Rich / Affluent" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyStatus" id="family_status5" value="Rich / Affluent" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_status5"></label>
          <label class="lbl" for="family_status5">Rich / Affluent</label>
        </div>
      </section>
      <section class="row row-padding colset-2-its">
        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyType == 'Joint Family'}">
            <input name="familyType" id="family_type1" value="Joint Family" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyType" id="family_type1" value="Joint Family" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_type1"></label>
          <label class="lbl" for="family_type1">Joint Family</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyType == 'Nuclear Family'}">
            <input name="familyType" id="family_type2" value="Nuclear Family" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyType" id="family_type2" value="Nuclear Family" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_type2"></label>
          <label class="lbl" for="family_type2">Nuclear Family</label>
        </div>
      </section>
      <section class="row row-padding colset-2-its">
        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyValues == 'Orthodox'}">
            <input name="familyValues" id="family_value1" value="Orthodox" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyValues" id="family_value1" value="Orthodox" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_value1"></label>
          <label class="lbl" for="family_value1">Orthodox</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyValues == 'Traditional'}">
            <input name="familyValues" id="family_value2" value="Traditional" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyValues" id="family_value2" value="Traditional" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_value2"></label>
          <label class="lbl" for="family_value2">Traditional</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyValues == 'Moderate'}">
            <input name="familyValues" id="family_value3" value="Moderate" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyValues" id="family_value3" value="Moderate" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_value3"></label>
          <label class="lbl" for="family_value3">Moderate</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.familyValues == 'Liberal'}">
            <input name="familyValues" id="family_value4" value="Liberal" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="familyValues" id="family_value4" value="Liberal" type="checkbox"/>
          </g:else>
          <label class="cbx" for="family_value4"></label>
          <label class="lbl" for="family_value4">Liberal</label>
        </div>
      </section>
      <section class="row row-padding colset-2-its">
        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.disabilities == 'Normal'}">
            <input name="disabilities" id="disabilities1" value="Normal" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="disabilities" id="disabilities1" value="Normal" type="checkbox"/>
          </g:else>
          <label class="cbx" for="disabilities1"></label>
          <label class="lbl" for="disabilities1">Normal</label>
        </div>

        <div class="checkbox-wrapper-42">
          <g:if test="${spouseFamilyDetails?.disabilities == 'Differently Abled'}">
            <input name="disabilities" id="disabilities2" value="Differently Abled" type="checkbox" checked/>
          </g:if>
          <g:else>
            <input name="disabilities" id="disabilities2" value="Differently Abled" type="checkbox"/>
          </g:else>
          <label class="cbx" for="disabilities2"></label>
          <label class="lbl" for="disabilities2">Differently Abled</label>
        </div>
      </section>

      <div class="d-flex justify-content-end">
        <button class="btn btn-primary"><i class="fas fa-save" aria-hidden="true">&nbsp;&nbsp;Save</i></button>
      </div>
    </form>
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    var maritalStatusFlag = false;
    var childStatusFlag = false;
    const un_married = document.getElementById('un_married');
    const maritalStatus1 = document.getElementById('maritalStatus1');
    const maritalStatus2 = document.getElementById('maritalStatus2');
    const maritalStatus3 = document.getElementById('maritalStatus3');

    un_married.addEventListener('click', (ev) => {
      maritalStatus = ev.target.value;

      maritalStatus1.checked = false;
      maritalStatus2.checked = false;
      maritalStatus3.checked = false;

      slideOutRows(maritalStatusFlag, childStatusFlag);
      maritalStatusFlag = false;
    });
    maritalStatus1.addEventListener('click', (ev) => {
      maritalStatus = ev.target.value;

      un_married.checked = false;
      maritalStatus2.checked = false;
      maritalStatus3.checked = false;
      maritalStatusFlag = true;
      if (maritalStatus1.checked === true) {
        const box = document.getElementById('children');
        box.className = "row row-padding colset-2-its animate__animated animate__fadeInDown";
      } else if (maritalStatus1.checked === false) {
        slideOutRows(maritalStatusFlag, childStatusFlag);
      }
    });
    maritalStatus2.addEventListener('click', (ev) => {
      maritalStatus = ev.target.value;

      un_married.checked = false;
      maritalStatus1.checked = false;
      maritalStatus3.checked = false;
      maritalStatusFlag = true;
      if (maritalStatus2.checked === true) {
        const box = document.getElementById('children');
        box.className = "row row-padding colset-2-its animate__animated animate__fadeInDown";
      } else if (maritalStatus2.checked === false) {
        slideOutRows(maritalStatusFlag, childStatusFlag);
      }
    });
    maritalStatus3.addEventListener('click', (ev) => {
      maritalStatus = ev.target.value;

      un_married.checked = false;
      maritalStatus1.checked = false;
      maritalStatus2.checked = false;
      maritalStatusFlag = true;
      if (maritalStatus3.checked === true) {
        const box = document.getElementById('children');
        box.className = "row row-padding colset-2-its animate__animated animate__fadeInDown";
      } else if (maritalStatus3.checked === false) {
        slideOutRows(maritalStatusFlag, childStatusFlag);
      }
    });

    const no_children = document.getElementById('no_children');
    const children1 = document.getElementById('children1');
    const children2 = document.getElementById('children2');
    const children3 = document.getElementById('children3');
    const children4 = document.getElementById('children4');
    no_children.addEventListener('click', (ev) => {
      children = ev.target.value;

      children1.checked = false;
      children2.checked = false;
      children3.checked = false;
      children4.checked = false;
      if (childStatusFlag) {
        const box = document.getElementById('child_status');
        box.className = "row row-padding colset-2-its slide-up-fade-out";
      }
      childStatusFlag = false;
    });
    children1.addEventListener('click', (ev) => {
      children = ev.target.value;

      no_children.checked = false;
      children2.checked = false;
      children3.checked = false;
      children4.checked = false;
      childStatusFlag = true;
      slideInSlideOutChildStatus(children1);
    });
    children2.addEventListener('click', (ev) => {
      children = ev.target.value;

      no_children.checked = false;
      children1.checked = false;
      children3.checked = false;
      children4.checked = false;
      childStatusFlag = true;
      slideInSlideOutChildStatus(children2);
    });
    children3.addEventListener('click', (ev) => {
      children = ev.target.value;

      no_children.checked = false;
      children1.checked = false;
      children2.checked = false;
      children4.checked = false;
      childStatusFlag = true;
      slideInSlideOutChildStatus(children3);
    });
    children4.addEventListener('click', (ev) => {
      children = ev.target.value;

      no_children.checked = false;
      children1.checked = false;
      children2.checked = false;
      children3.checked = false;
      childStatusFlag = true;
      slideInSlideOutChildStatus(children4);
    });

    const children_status1 = document.getElementById('children_status1');
    const children_status2 = document.getElementById('children_status2');

    const heightSelect = document.getElementById('height');
    const weightSelect = document.getElementById('weight');

    var maritalStatus = "${spouseFamilyDetails?.maritalStatus}";
    var children = "${spouseFamilyDetails?.children}";
    var childrenStatus = "${spouseFamilyDetails?.childrenStatus}";
    var height = "${spouseFamilyDetails?.height}";
    var weight = "${spouseFamilyDetails?.weight}";
    var familyStatus = "${spouseFamilyDetails?.familyStatus}";
    var familyType = "${spouseFamilyDetails?.familyType}";
    var familyValues = "${spouseFamilyDetails?.familyValues}";
    var disabilities = "${spouseFamilyDetails?.disabilities}";


    const maritalStatusAvailable = "${familyDetails?.maritalStatus}";
    const childrenAvailable = "${familyDetails?.children}";
    const childrenStatusAvailable = "${familyDetails?.childrenStatus}";
    const heightAvailable = "${familyDetails?.height}";
    const weightAvailable = "${familyDetails?.weight}";
    const familyStatusAvailable = "${familyDetails?.familyStatus}";
    const familyTypeAvailable = "${familyDetails?.familyType}";
    const familyValueAvailable = "${familyDetails?.familyValues}";
    const disabilitiesAvailable = "${familyDetails?.disabilities}";

    const sameAsCheckBox = document.getElementById("sameAsFamilyDetails");

    sameAsCheckBox.checked = (maritalStatus === "${familyDetails?.maritalStatus}") && (children === "${familyDetails?.children}") && (childrenStatus === "${familyDetails?.childrenStatus}") && (height === "${familyDetails?.height}") && (weight === "${familyDetails?.weight}") && (familyStatus === "${familyDetails?.familyStatus}") && (familyType === "${familyDetails?.familyType}") && (familyValues === "${familyDetails?.familyValues}") && (disabilities === "${familyDetails?.disabilities}");

    sameAsCheckBox.addEventListener('change', function (ev) {
      if (ev.target.checked) {
        height = heightSelect.value;
        weight = weightSelect.value;

        un_married.checked = maritalStatusAvailable == "UnMarried";
        maritalStatus1.checked = maritalStatusAvailable == "Divorced";
        maritalStatus2.checked = maritalStatusAvailable == "Awaiting Divorce";
        maritalStatus3.checked = maritalStatusAvailable == "Widowed";

        no_children.checked = childrenAvailable == "No Children";
        children1.checked = childrenAvailable == "1";
        children2.checked = childrenAvailable == "2";
        children3.checked = childrenAvailable == "3";
        children4.checked = childrenAvailable == "4 and more";

        children_status1.checked = childrenStatusAvailable == "Living with me";
        children_status2.checked = childrenStatusAvailable == "Not living with me";

        heightSelect.value = heightAvailable;
        weightSelect.value = weightAvailable;

        family_status1.checked = familyStatusAvailable == "Lower Middle Class";
        family_status2.checked = familyStatusAvailable == "Middle Class";
        family_status3.checked = familyStatusAvailable == "Upper Middle Class";
        family_status4.checked = familyStatusAvailable == "High Class";
        family_status5.checked = familyStatusAvailable == "Rich / Affluent";

        family_type1.checked = familyTypeAvailable == "Joint Family";
        family_type2.checked = familyTypeAvailable == "Nuclear Family";

        family_value1.checked = familyValueAvailable == "Orthodox";
        family_value2.checked = familyValueAvailable == "Traditional";
        family_value3.checked = familyValueAvailable == "Moderate";
        family_value4.checked = familyValueAvailable == "Liberal";

        disabilities1.checked = disabilitiesAvailable == "Normal";
        disabilities2.checked = disabilitiesAvailable == "Differently Abled";
      } else {
        un_married.checked = maritalStatus == "UnMarried";
        maritalStatus1.checked = maritalStatus == "Divorced";
        maritalStatus2.checked = maritalStatus == "Awaiting Divorce";
        maritalStatus3.checked = maritalStatus == "Widowed";

        no_children.checked = children == "No Children";
        children1.checked = children == "1";
        children2.checked = children == "2";
        children3.checked = children == "3";
        children4.checked = children == "4 and more";

        children_status1.checked = childrenStatus == "Living with me";
        children_status2.checked = childrenStatus == "Not living with me";

        heightSelect.value = height;
        weightSelect.value = weight;

        family_status1.checked = familyStatus == "Lower Middle Class";
        family_status2.checked = familyStatus == "Middle Class";
        family_status3.checked = familyStatus == "Upper Middle Class";
        family_status4.checked = familyStatus == "High Class";
        family_status5.checked = familyStatus == "Rich / Affluent";

        family_type1.checked = familyType == "Joint Family";
        family_type2.checked = familyType == "Nuclear Family";

        family_value1.checked = familyValues == "Orthodox";
        family_value2.checked = familyValues == "Traditional";
        family_value3.checked = familyValues == "Moderate";
        family_value4.checked = familyValues == "Liberal";

        disabilities1.checked = disabilities == "Normal";
        disabilities2.checked = disabilities == "Differently Abled";
      }
    });

    children_status1.addEventListener('click', (ev) => {
      childrenStatus = ev.target.value;

      children_status2.checked = false;
      const box = document.getElementById('children');
      box.className = "row row-padding colset-2-its  animate__animated animate__fadeInDown";
    });
    children_status2.addEventListener('click', (ev) => {
      childrenStatus = ev.target.value;

      children_status1.checked = false;
      const box = document.getElementById('children');
      box.className = "row row-padding colset-2-its  animate__animated animate__fadeInDown";
    });

    const family_status1 = document.getElementById('family_status1');
    const family_status2 = document.getElementById('family_status2');
    const family_status3 = document.getElementById('family_status3');
    const family_status4 = document.getElementById('family_status4');
    const family_status5 = document.getElementById('family_status5');

    family_status1.addEventListener('click', (ev) => {
      familyStatus = ev.target.value;

      family_status2.checked = false;
      family_status3.checked = false;
      family_status4.checked = false;
      family_status5.checked = false;
    });
    family_status2.addEventListener('click', (ev) => {
      familyStatus = ev.target.value;

      family_status1.checked = false;
      family_status3.checked = false;
      family_status4.checked = false;
      family_status5.checked = false;
    });
    family_status3.addEventListener('click', (ev) => {
      familyStatus = ev.target.value;

      family_status1.checked = false;
      family_status2.checked = false;
      family_status4.checked = false;
      family_status5.checked = false;
    });
    family_status4.addEventListener('click', (ev) => {
      familyStatus = ev.target.value;

      family_status1.checked = false;
      family_status2.checked = false;
      family_status3.checked = false;
      family_status5.checked = false;
    });
    family_status5.addEventListener('click', (ev) => {
      familyStatus = ev.target.value;

      family_status1.checked = false;
      family_status2.checked = false;
      family_status3.checked = false;
      family_status4.checked = false;
    });

    const family_type1 = document.getElementById('family_type1');
    const family_type2 = document.getElementById('family_type2');
    family_type1.addEventListener('click', (ev) => {
      familyType = ev.target.value;

      family_type2.checked = false;
    });
    family_type2.addEventListener('click', (ev) => {
      familyType = ev.target.value;

      family_type1.checked = false;
    });

    const family_value1 = document.getElementById('family_value1');
    const family_value2 = document.getElementById('family_value2');
    const family_value3 = document.getElementById('family_value3');
    const family_value4 = document.getElementById('family_value4');

    family_value1.addEventListener('click', (ev) => {
      familyValues = ev.target.value;

      family_value2.checked = false;
      family_value3.checked = false;
      family_value4.checked = false;
    });
    family_value2.addEventListener('click', (ev) => {
      familyValues = ev.target.value;

      family_value1.checked = false;
      family_value3.checked = false;
      family_value4.checked = false;
    });
    family_value3.addEventListener('click', (ev) => {
      familyValues = ev.target.value;

      family_value1.checked = false;
      family_value2.checked = false;
      family_value4.checked = false;
    });
    family_value4.addEventListener('click', (ev) => {
      familyValues = ev.target.value;

      family_value1.checked = false;
      family_value2.checked = false;
      family_value3.checked = false;
    });

    const disabilities1 = document.getElementById('disabilities1');
    const disabilities2 = document.getElementById('disabilities2');
    disabilities1.addEventListener('click', (ev) => {
      disabilities = ev.target.value;

      disabilities2.checked = false;
    });
    disabilities2.addEventListener('click', (ev) => {
      disabilities = ev.target.value;

      disabilities1.checked = false;
    });

    if ("${familyDetails?.maritalStatus}" !== 'UnMarried') {
      maritalStatusFlag = true;
      document.getElementById('children').className = "row row-padding animate__animated animate__fadeInDown";
      if ("${familyDetails?.children}" !== 'No Children') {
        childStatusFlag = true;
        document.getElementById('child_status').className = "row row-padding animate__animated animate__fadeInDown";
      }
    }
  });

  function slideOutRows(maritalStatusFlag, childStatusFlag) {
    if (maritalStatusFlag) {
      const box = document.getElementById('children');
      box.className = "row row-padding slide-up-fade-out";

      if (childStatusFlag) {
        const box = document.getElementById('child_status');
        box.className = "row row-padding slide-up-fade-out";
      }
    }
  }

  function slideInSlideOutChildStatus(children) {
    if (children.checked === true) {
      const box = document.getElementById('child_status');
      box.className = "row row-padding animate__animated animate__fadeInDown";
    } else if (children.checked === false) {
      const box = document.getElementById('child_status');
      box.className = "row row-padding slide-up-fade-out";
    }
  }
</script>
</body>
</html>
