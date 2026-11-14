<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:snackbarStyles/>
  <glass:slideAnimationStyles/>
  <glass:sliderAssets/>
  <asset:stylesheet src="gs-glass-border-hover.css"/>

  <%--
    Responsive tweaks live here rather than scattered inline styles, so the
    breakpoints are easy to find/adjust in one place. Bootstrap already
    handles the column stacking (col-12 → col-sm-* → col-md-*), this just
    smooths out spacing/wrapping at the smallest sizes.
  --%>
  <style>
  @media (max-width: 575.98px) {
    .row-padding {
      padding-left: 8px;
      padding-right: 8px;
    }

    .gs-range {
      margin: 8px 0 !important;
    }
  }
  </style>
</head>

<body>

<g:set var="spouseCardToggle">
  <glassToggle:toggle name="sameAsPersonalDetails" id="sameAsPersonalDetails" label="Same As Personal Details"
                      onChange="handleToggle(checked)" disabled="${!personalDetails}"/>
</g:set>

<glass:modernGlassCard
  title="Spouse Preferences"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  subtitle="Tell us about your ideal partner"
  headerHtml="${spouseCardToggle}">

  <form id="spouseDetailsForm">

    <%-- ── Age Range ───────────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="col-12">
        <glass:rangeSlider name="age"
                           min="18" max="100" step="1" dual="true"
                           valueMin="24" valueMax="50"
                           label=" "
                           unit=" Years"/>
      </div>
    </section>

    <%-- ── Gender (auto-derived from personalDetails) ────────────────
         No manual "greyed-out" wrapper needed — glass:radio already
         renders its own locked/disabled look (dimmed card + 🔒 icon
         when disabled+checked) via GlassmorphicRadioTagLib's CSS.

         Columns: col-6 keeps the two radio cards side-by-side even on
         phones (rather than each taking the full width and stacking),
         widening at col-sm-3/col-md-2 as the viewport grows. The helper
         text drops to its own full-width line at every size. ──── --%>
    <div class="gs-slider">
      <section class="row row-padding g-3 gs-range align-items-center">
        <div class="col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Male" id="Male" title="Male"
                       checked="${personalDetails?.gender == 'Female'}"
                       disabled="true"/>
        </div>

        <div class="col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Female" id="Female" title="Female"
                       checked="${personalDetails?.gender == 'Male'}"
                       disabled="true"/>
        </div>

        <div class="col-12">
          <glass:text>The gender of spouse gets auto assigned based on gender of personal details.</glass:text>
        </div>

        <%--
          Disabled form controls are never included in FormData / a native
          submit, so the two disabled "gender" radios above render the right
          selection visually but silently drop "gender" from the POST body.
          Carry the derived value in a plain (enabled) hidden input instead,
          using the same "opposite of personalDetails.gender" rule as the
          radios' checked= expressions above.
        --%>
      </section>
    </div>

    <%-- ── Religion & Caste ────────────────────────────────────────── --%>
    <%-- Stacked full-width on phones/small tablets, side-by-side from md
         up so neither select feels cramped when the page narrows. --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">

        <glass:select
          name="religion" id="religion" label="Religion"
          placeholder="Select a religion…"
          from="${religions}"
          optionKey="name" optionValue="name"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="false" disabled="false"/>
      </div>

      <div class="col-12 col-md-6">

        <glass:select
          name="caste" id="caste" label="Caste"
          placeholder="Select a caste…"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="false" disabled="false"/>
      </div>
    </section>

    <%-- ── Smoking Habit ───────────────────────────────────────────── --%>
    <%-- 2-up grid on phones (col-6), 4-up from md up (col-md-3) so each
         card stays readable instead of squeezing four into one row on a
         narrow screen. --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">

        <glass:radio name="smokingHabit" value="Non Smoker" id="NonSmoker" title="Non Smoker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="smokingHabit" value="Social Smoker" id="SocialSmoker" title="Social Smoker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="smokingHabit" value="Normal Smoker" id="NormalSmoker" title="Normal Smoker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="smokingHabit" value="Chain Smoker" id="ChainSmoker" title="Chain Smoker"/>
      </div>
    </section>

    <%-- ── Drinking Habit ──────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="col-6 col-md-3">

        <glass:radio name="drinkingHabit" value="Non Drinker" id="NonDrinker" title="Non Drinker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="drinkingHabit" value="Social Drinker" id="SocialDrinker" title="Social Drinker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="drinkingHabit" value="Normal Drinker" id="NormalDrinker" title="Normal Drinker"/>
      </div>

      <div class="col-6 col-md-3">

        <glass:radio name="drinkingHabit" value="Alcoholic" id="HeavyDrinker" title="Alcoholic"/>
      </div>
    </section>

    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>
  </form>

</glass:modernGlassCard>

<script type="text/javascript">

  // Saved state for "Same as Personal Details" toggle restore
  var savedReligion = null;
  var savedCaste = null;
  var savedSmokingHabit = null;
  var savedDrinkingHabit = null;

  // ── DOM ready ──────────────────────────────────────────────────────────────
  document.addEventListener('DOMContentLoaded', function () {
    // ── Religion → Caste cascade ───────────────────────────────────────────
    var religionSelect = document.getElementById('religion');
    var casteSelect = document.getElementById('caste');

    // NOTE: every dynamic value interpolated below is run through
    // encodeAsJavaScript() (and wrapped in raw() so GSP's default output
    // codec doesn't re-escape the already-JS-safe string on top of it).
    // Without it, a religion/caste name containing a quote or backslash
    // (e.g. a name with an apostrophe) would either break this script's
    // syntax outright or, left to GSP's default HTML-escaping, get mangled
    // into HTML entities that no longer match the real DB value — and
    // unescaped user-influenced strings dropped straight into a <script>
    // block are also an XSS vector.
    var religionsData = [
      <g:each in="${religions}" var="religion" status="i">
      {
        name: "${raw(religion.name.encodeAsJavaScript())}",
        caste: [
          <g:each in="${religion.caste}" var="caste" status="j">
          "${raw(caste.encodeAsJavaScript())}"${j < religion.caste.size() - 1 ? ',' : ''}
          </g:each>
        ]
      }${i < religions.size() - 1 ? ',' : ''}
      </g:each>
    ];

    if (religionSelect.value) {
      populateCaste(religionSelect.value);
    }

    religionSelect.addEventListener('change', function (ev) {
      populateCaste(ev.target.value);
      ev.target.blur();
    });

    function populateCaste(selectedReligionName) {
      casteSelect.innerHTML = '<option value="Doesn\'t Matter">Doesn\'t Matter</option>';
      var selectedReligion = religionsData.find(function (r) {
        return r.name === selectedReligionName;
      });
      if (selectedReligion && selectedReligion.caste) {
        selectedReligion.caste.forEach(function (caste) {
          var option = document.createElement('option');
          option.value = caste;
          option.textContent = caste;
          casteSelect.appendChild(option);
        });
      }
    }

    // ── Async form submission ──────────────────────────────────────────────
    var form = document.getElementById('spouseDetailsForm');
    var saveBtn = form.querySelector('button[type="submit"]');
    var saveUrl = "${createLink(controller: 'DBoard', action: 'saveSpouseDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}";

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      // Gender radios are disabled (auto-derived), so read .checked
      // directly rather than relying on FormData, which drops disabled
      // fields entirely.
      var smokingHabitEl = form.querySelector('[name="smokingHabit"]:checked');
      var drinkingHabitEl = form.querySelector('[name="drinkingHabit"]:checked');
      var minAgeField = form.querySelector('[name="ageMin"], [name="minAge"], [name="age_min"]');
      var maxAgeField = form.querySelector('[name="ageMax"], [name="maxAge"], [name="age_max"]');

      var payload = {
        minAge: minAgeField ? minAgeField.value : '',
        maxAge: maxAgeField ? maxAgeField.value : '',
        religion: religionSelect.value,
        caste: casteSelect.value,
        smokingHabit: smokingHabitEl ? smokingHabitEl.value : '',
        drinkingHabit: drinkingHabitEl ? drinkingHabitEl.value : ''
      };

      saveBtn.disabled = true;
      var originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving…';

      try {
        var response = await fetch(saveUrl, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          },
          body: JSON.stringify(payload)
        });

        var data = await response.json();

        if (response.ok && data.success) {
          showSuccess(data.message || 'Spouse preferences saved successfully!');
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }
        } else if (response.status === 400) {
          showWarning(data.message || 'Please check your input and try again.');
        } else {
          showError(data.message || 'Something went wrong. Please try again.');
        }

      } catch (err) {
        console.error('Save failed:', err);
        showError('Network error — please check your connection.');

      } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalLabel;
      }
    });

    // ── Same As Personal Details toggle ───────────────────────────────────
    window.handleToggle = function (checked) {
      if (checked) {
        savedReligion = religionSelect.value;
        savedCaste = casteSelect.value;
        savedSmokingHabit = (document.querySelector('input[name="smokingHabit"]:checked') || {}).value || null;
        savedDrinkingHabit = (document.querySelector('input[name="drinkingHabit"]:checked') || {}).value || null;

        religionSelect.value = "${raw((personalDetails?.religion ?: '').encodeAsJavaScript())}";
        populateCaste(religionSelect.value);
        casteSelect.value = "${raw((personalDetails?.caste ?: '').encodeAsJavaScript())}";

        var smokingInput = document.querySelector('input[name="smokingHabit"][value="${raw((personalDetails?.smokingHabit ?: '').encodeAsJavaScript())}"]');
        if (smokingInput) smokingInput.checked = true;

        var drinkingInput = document.querySelector('input[name="drinkingHabit"][value="${raw((personalDetails?.drinkingHabit ?: '').encodeAsJavaScript())}"]');
        if (drinkingInput) drinkingInput.checked = true;

      } else {
        religionSelect.value = savedReligion;
        populateCaste(savedReligion);
        casteSelect.value = savedCaste;

        document.querySelectorAll('input[name="smokingHabit"]').forEach(function (r) {
          r.checked = false;
        });
        document.querySelectorAll('input[name="drinkingHabit"]').forEach(function (r) {
          r.checked = false;
        });

        if (savedSmokingHabit) {
          var r = document.querySelector('input[name="smokingHabit"][value="' + savedSmokingHabit + '"]');
          if (r) r.checked = true;
        }
        if (savedDrinkingHabit) {
          var r = document.querySelector('input[name="drinkingHabit"][value="' + savedDrinkingHabit + '"]');
          if (r) r.checked = true;
        }
      }
    };
  });
</script>
</body>
</html>
