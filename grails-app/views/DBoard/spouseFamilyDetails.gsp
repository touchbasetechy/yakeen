<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:slideAnimationStyles/>
  <glass:sliderAssets/>
  <glass:weightRangeSliderAssets/>
  <glass:heightRangeSliderAssets/>

  <%--
    Responsive tweaks live here rather than scattered inline styles, so the
    breakpoints are easy to find/adjust in one place.
  --%>
  <style>
  @media (max-width: 575.98px) {
    .row-padding {
      padding-left: 8px;
      padding-right: 8px;
    }
  }
  </style>
</head>

<body>
<g:set var="spouseFamilyCardToggle">
  <glassToggle:toggle name="sameAsFamilyDetails" id="sameAsFamilyDetails" label="Same As Family Details"
                      onChange="handleToggle(checked)" disabled="${!familyDetails}"/>
</g:set>

<glass:modernGlassCard
  title="Spouse Family Preferences"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  subtitle="Tell us about your ideal spouse's family"
  headerHtml="${spouseFamilyCardToggle}">

  <form id="spouseFamilyPreferencesForm">

    <%-- ── Marital Status ──────────────────────────────────────────────── --%>
    <%-- 4 options: 2-up on phones, 4-up from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="UnMarried" id="un_married" title="UnMarried"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Divorced" id="maritalStatus1" title="Divorced"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Awaiting Divorce" id="maritalStatus2" title="Awaiting Divorce"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Widowed" id="maritalStatus3" title="Widowed"/>
      </div>
    </section>

    <%-- ── Children (shown only when not UnMarried) ───────────────────── --%>
    <%-- 5 options: 2-up on phones, 3-up on small tablets, 5-up from lg up. --%>
    <section class="row row-padding g-3 d-none" id="children">
      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="children" value="No Children" id="no_children" title="No Children"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="1" id="children1" title="1"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="2" id="children2" title="2"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="3" id="children3" title="3"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="children" value="4 and more" id="children4" title="4 and more"/>
      </div>
    </section>

    <%-- ── Children Status (shown only when children > 0) ────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3 d-none" id="child_status">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="childrenStatus" value="Living with me" id="children_status1" title="Living with me"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="childrenStatus" value="Not living with me" id="children_status2" title="Not living with me"/>
      </div>
    </section>

    <%-- ── Height & Weight ────────────────────────────────────────────── --%>
    <%-- Stacked full-width on phones/small tablets, side-by-side from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">
        <g:set var="selectedMinHeight" value="${spousePreferences?.minHeight}"/>
        <g:set var="selectedMaxHeight" value="${spousePreferences?.maxHeight}"/>

        <glass:heightRangeSlider name="partnerHeight"
                                 min="140" max="210" step="1" dual="true"
                                 valueMin="${spousePreferences?.minHeight ?: 155}"
                                 valueMax="${spousePreferences?.maxHeight ?: 185}"
                                 label="Height" unit="${spousePreferences?.heightUnit ?: 'ft'}"/>

      </div>

      <div class="col-12 col-md-6">
        <g:set var="selectedMinWeight" value="${spousePreferences?.minWeight}"/>
        <g:set var="selectedMaxWeight" value="${spousePreferences?.maxWeight}"/>

        <glass:weightRangeSlider name="weight"
                                 min="35" max="150" step="0.5" dual="true"
                                 valueMin="${spousePreferences?.minWeight ?: 50}"
                                 valueMax="${spousePreferences?.maxWeight ?: 80}"
                                 label="Weight" unit="${spousePreferences?.weightUnit ?: 'lb'}"/>

      </div>
    </section>

    <%-- ── Family Status ───────────────────────────────────────────────── --%>
    <%-- 4 options: same responsive pattern as Children above. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Middle Class" id="family_status2" title="Middle Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Upper Middle Class" id="family_status3" title="Upper Middle Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="High Class" id="family_status4" title="High Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Rich / Affluent" id="family_status5" title="Rich / Affluent"/>
      </div>
    </section>

    <%-- ── Family Type ─────────────────────────────────────────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="familyType" value="Joint Family" id="family_type1" title="Joint Family"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="familyType" value="Nuclear Family" id="family_type2" title="Nuclear Family"/>
      </div>
    </section>

    <%-- ── Family Values ───────────────────────────────────────────────── --%>
    <%-- 4 options: 2-up on phones, 4-up from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Orthodox" id="family_value1" title="Orthodox"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Traditional" id="family_value2" title="Traditional"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Moderate" id="family_value3" title="Moderate"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Liberal" id="family_value4" title="Liberal"/>
      </div>
    </section>

    <%-- ── Disabilities ────────────────────────────────────────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="disabilities" value="Normal" id="disabilities1" title="Normal"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="disabilities" value="Differently Abled" id="disabilities2" title="Differently Abled"/>
      </div>
    </section>

    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>

  </form>

</glass:modernGlassCard>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── classList helpers ─────────────────────────────────────────────
    // Using classList.add/remove rather than replacing the entire className
    // string so Bootstrap's responsive classes are never accidentally wiped.
    function show(el) {
      el.classList.remove('slide-out-fade-out', 'd-none');
      el.classList.add('slide-in-fade-in');
    }

    function hide(el) {
      el.classList.remove('slide-in-fade-in');
      el.classList.add('slide-out-fade-out');
    }

    function clearRadios(name) {
      document.querySelectorAll('input[name="' + name + '"]')
        .forEach(function (r) {
          r.checked = false;
        });
    }

    function selectRadio(name, value) {
      document.querySelectorAll('input[name="' + name + '"]').forEach(function (r) {
        r.checked = (r.value === value);
      });
    }

    // ── Section refs ──────────────────────────────────────────────────
    const childrenSection = document.getElementById('children');
    const childStatusSection = document.getElementById('child_status');
    const heightSelect = document.getElementById('height');
    const weightSelect = document.getElementById('weight');

    // ── Marital Status → show/hide Children row ───────────────────────
    document.querySelectorAll('input[name="maritalStatus"]').forEach(function (radio) {
      radio.addEventListener('change', function () {
        if (this.value === 'UnMarried') {
          hide(childrenSection);
          hide(childStatusSection);
          clearRadios('children');
          clearRadios('childrenStatus');
        } else {
          // Show the children question.
          // Always re-hide child_status and clear its selection: switching
          // between non-UnMarried values (e.g. Divorced → Widowed) must not
          // leave a stale child_status row visible with an old selection.
          show(childrenSection);
          hide(childStatusSection);
          clearRadios('childrenStatus');
        }
      });
    });

    // ── Children → show/hide Child Status row ─────────────────────────
    document.querySelectorAll('input[name="children"]').forEach(function (radio) {
      radio.addEventListener('change', function () {
        if (this.value === 'No Children') {
          hide(childStatusSection);
          clearRadios('childrenStatus');
        } else {
          show(childStatusSection);
        }
      });
    });

    // ── Family details from server (constant reference) ───────────────
    const fd = {
      maritalStatus: "${familyDetails?.maritalStatus  ?: ''}",
      children: "${familyDetails?.children       ?: ''}",
      childrenStatus: "${familyDetails?.childrenStatus ?: ''}",
      height: "${familyDetails?.height         ?: ''}",
      weight: "${familyDetails?.weight         ?: ''}",
      familyStatus: "${familyDetails?.familyStatus   ?: ''}",
      familyType: "${familyDetails?.familyType     ?: ''}",
      familyValues: "${familyDetails?.familyValues   ?: ''}",
      disabilities: "${familyDetails?.disabilities   ?: ''}"
    };

    // ── Snapshot of user's own values before "Same as" override ──────
    let snapshot = null;

    function captureSnapshot() {
      function getRadioValue(name) {
        const checked = document.querySelector('input[name="' + name + '"]:checked');
        return checked ? checked.value : '';
      }

      return {
        maritalStatus: getRadioValue('maritalStatus'),
        children: getRadioValue('children'),
        childrenStatus: getRadioValue('childrenStatus'),
        familyStatus: getRadioValue('familyStatus'),
        familyType: getRadioValue('familyType'),
        familyValues: getRadioValue('familyValues'),
        disabilities: getRadioValue('disabilities'),
        height: heightSelect.value,
        weight: weightSelect.value
      };
    }

    function applyValues(source) {
      selectRadio('maritalStatus', source.maritalStatus);
      selectRadio('children', source.children);
      selectRadio('childrenStatus', source.childrenStatus);
      selectRadio('familyStatus', source.familyStatus);
      selectRadio('familyType', source.familyType);
      selectRadio('familyValues', source.familyValues);
      selectRadio('disabilities', source.disabilities);

      heightSelect.value = source.height;
      weightSelect.value = source.weight;

      // Sync section visibility
      if (source.maritalStatus && source.maritalStatus !== 'UnMarried') {
        show(childrenSection);
        if (source.children && source.children !== 'No Children') {
          show(childStatusSection);
        } else {
          hide(childStatusSection);
        }
      } else {
        hide(childrenSection);
        hide(childStatusSection);
      }
    }

    window.handleToggle = function (checked) {
      if (checked) {
        snapshot = captureSnapshot();   // 1. save current UI state
        applyValues(fd);                // 2. apply family details
      } else {
        if (snapshot) {
          applyValues(snapshot);        // restore saved state
          snapshot = null;
        } else {
          // fallback: clear everything if no snapshot exists
          ['maritalStatus', 'children', 'childrenStatus', 'familyStatus',
            'familyType', 'familyValues', 'disabilities'].forEach(clearRadios);
          heightSelect.value = '';
          weightSelect.value = '';
          hide(childrenSection);
          hide(childStatusSection);
        }
      }
    };

    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("spouseFamilyPreferencesForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      const maritalStatusEl = form.querySelector('[name="maritalStatus"]:checked');
      const childrenEl = form.querySelector('[name="children"]:checked');
      const childrenStatusEl = form.querySelector('[name="childrenStatus"]:checked');
      const familyStatusEl = form.querySelector('[name="familyStatus"]:checked');
      const familyTypeEl = form.querySelector('[name="familyType"]:checked');
      const familyValuesEl = form.querySelector('[name="familyValues"]:checked');
      const disabilitiesEl = form.querySelector('[name="disabilities"]:checked');
      const minHeightField = form.querySelector('[name="partnerHeightMin"], [name="minPartnerHeight"], [name="partnerHeight_min"]');
      const maxHeightField = form.querySelector('[name="partnerHeightMax"], [name="maxPartnerHeight"], [name="partnerHeight_max"]');
      const minWeightField = form.querySelector('[name="weightMin"], [name="minWeight"], [name="weight_min"]');
      const maxWeightField = form.querySelector('[name="weightMax"], [name="maxWeight"], [name="weight_max"]');

      const payload = {
        maritalStatus: maritalStatusEl ? maritalStatusEl.value : '',
        children: childrenEl ? childrenEl.value : '',
        childrenStatus: childrenStatusEl ? childrenStatusEl.value : '',
        minHeight: minHeightField ? minHeightField.value : '',
        maxHeight: maxHeightField ? maxHeightField.value : '',
        minWeight: minWeightField ? minWeightField.value : '',
        maxWeight: maxWeightField ? maxWeightField.value : '',
        familyStatus: familyStatusEl ? familyStatusEl.value : '',
        familyType: familyTypeEl ? familyTypeEl.value : '',
        familyValues: familyValuesEl ? familyValuesEl.value : '',
        disabilities: disabilitiesEl ? disabilitiesEl.value : ''
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving…';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveSpouseFamilyDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "X-Requested-With": "XMLHttpRequest"
            },
            body: JSON.stringify(payload)
          }
        );

        const data = await response.json();

        if (response.ok && data.success) {
          window.showSuccess(data.message || "Spouse family preferences saved successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 700);
          }
        } else if (response.status === 400) {
          window.showWarning(data.message || "Please check your input and try again.");
        } else {
          window.showError(data.message || "Something went wrong. Please try again.");
        }

      } catch (err) {
        console.error("Save failed:", err);
        window.showError("Network error — please check your connection.");

      } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalLabel;
      }
    });

  });
</script>

</body>
</html>
