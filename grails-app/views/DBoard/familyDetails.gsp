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

<glass:modernGlassCard
  title="Family Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  subtitle="Tell us about your family">

  <form id="familyDetailsForm">

    <%-- ── Marital Status ──────────────────────────────────────────────── --%>
    <%-- 4 options: 2-up on phones, 4-up from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="UnMarried" id="UnMarried" title="UnMarried"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Divorced" id="Divorced" title="Divorced"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Awaiting Divorce" id="AwaitingDivorce" title="Awaiting Divorce"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="maritalStatus" value="Widowed" id="Widowed" title="Widowed"/>
      </div>
    </section>

    <%-- ── Children (shown only when not UnMarried) ───────────────────── --%>
    <%-- 5 options: 2-up on phones, 3-up on small tablets, 5-up from lg up. --%>
    <section class="row row-padding g-3 d-none" id="children">
      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="children" value="No Children" id="NoChildren" title="No Children"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="1" id="Children1" title="1"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="2" id="Children2" title="2"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-2">
        <glass:radio name="children" value="3" id="Children3" title="3"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="children" value="4 and more" id="Children4" title="4 and more"/>
      </div>
    </section>

    <%-- ── Children Status (shown only when children > 0) ────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3 d-none" id="child_status">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="childrenStatus" value="Living with me" id="LivingWithMe" title="Living with me"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="childrenStatus" value="Not living with me" id="NotLivingWithMe" title="Not living with me"/>
      </div>
    </section>

    <%-- ── Height & Weight ────────────────────────────────────────────── --%>
    <%-- Stacked full-width on phones/small tablets, side-by-side from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">
        <g:set var="selectedHeight" value="${familyDetails?.height}"/>

        <glass:heightRangeSlider name="height"
                                 min="140" max="210" step="1"
                                 value="${familyDetails?.height ?: 170}"
                                 label="Height" unit="cm"/>
      </div>

      <div class="col-12 col-md-6">
        <g:set var="selectedWeight" value="${familyDetails?.weight}"/>
        <glass:weightRangeSlider name="weight"
                                 min="30" max="200" step="0.1"
                                 value="${familyDetails?.weight ?: 68}"
                                 label="Weight" unit="kg"/>
      </div>
    </section>

    <%-- ── Family Status ───────────────────────────────────────────────── --%>
    <%-- 5 options: same responsive pattern as Children above. --%>
    <section class="row row-padding g-3">
      %{--      <div class="col-6 col-sm-4 col-lg-3">--}%
      %{--        <glass:radio name="familyStatus" value="Lower Middle Class" id="LowerMiddleClass" title="Lower Middle Class"/>--}%
      %{--      </div>--}%

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Middle Class" id="MiddleClass" title="Middle Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Upper Middle Class" id="UpperMiddleClass" title="Upper Middle Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="High Class" id="HighClass" title="High Class"/>
      </div>

      <div class="col-6 col-sm-4 col-lg-3">
        <glass:radio name="familyStatus" value="Rich / Affluent" id="RichAffluent" title="Rich / Affluent"/>
      </div>
    </section>

    <%-- ── Family Type ─────────────────────────────────────────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="familyType" value="Joint Family" id="JointFamily" title="Joint Family"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="familyType" value="Nuclear Family" id="NuclearFamily" title="Nuclear Family"/>
      </div>
    </section>

    <%-- ── Family Values ───────────────────────────────────────────────── --%>
    <%-- 4 options: 2-up on phones, 4-up from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Orthodox" id="Orthodox" title="Orthodox"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Traditional" id="Traditional" title="Traditional"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Moderate" id="Moderate" title="Moderate"/>
      </div>

      <div class="col-6 col-md-3">
        <glass:radio name="familyValues" value="Liberal" id="Liberal" title="Liberal"/>
      </div>
    </section>

    <%-- ── Disabilities ────────────────────────────────────────────────── --%>
    <%-- 2 options: side-by-side even on phones, narrower from md up. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="disabilities" value="Normal" id="Normal" title="Normal"/>
      </div>

      <div class="col-6 col-md-4 col-lg-3">
        <glass:radio name="disabilities" value="Differently Abled" id="DifferentlyAbled" title="Differently Abled"/>
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

    // ── Marital Status → show/hide Children row ───────────────────────
    const maritalRadios = document.querySelectorAll('input[name="maritalStatus"]');
    const childrenSection = document.getElementById('children');
    const childStatusSection = document.getElementById('child_status');

    maritalRadios.forEach(function (radio) {
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
    const childrenRadios = document.querySelectorAll('input[name="children"]');

    childrenRadios.forEach(function (radio) {
      radio.addEventListener('change', function () {
        if (this.value === 'No Children') {
          hide(childStatusSection);
          clearRadios('childrenStatus');
        } else {
          show(childStatusSection);
        }
      });
    });

    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("familyDetailsForm");
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

      const payload = {
        maritalStatus: maritalStatusEl ? maritalStatusEl.value : '',
        children: childrenEl ? childrenEl.value : '',
        childrenStatus: childrenStatusEl ? childrenStatusEl.value : '',
        height: form.querySelector('[name="height"]').value,
        weight: form.querySelector('[name="weight"]').value,
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
          "${createLink(controller: 'DBoard', action: 'saveFamilyDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Family details saved successfully!");
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
