<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <dp:resources/>

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

%{--<ul class="d-flex justify-content-end">--}%
%{--  <g:if test="${params?.ac == 'an'}">--}%
%{--    <li><span class="badge badge-success fixed-size-badge">Super Admin<i class="fa fa-user-plus"--}%
%{--                                                                         aria-hidden="true"></i>--}%
%{--    </span></li>--}%
%{--  </g:if>--}%
%{--  <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>--}%
%{--  <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>--}%
%{--  <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>--}%
%{--</ul>--}%

%{--<header>--}%
%{--  <div class="d-flex justify-content-center">--}%
%{--    <div class="header-section">--}%
%{--      <g:if test="${params?.ac == 'an'}">--}%
%{--        <a--}%
%{--          href="${createLink(controller: 'AdminDBoard', action: 'addPerson', params: [id: params?.id, ac: params?.ac])}"--}%
%{--          class="embossed-link"><i class="fas fa-chevron-left" aria-hidden="true"></i></a>--}%
%{--      </g:if>--}%
%{--      <g:else>--}%
%{--        <a href="${createLink(controller: 'DBoard', action: 'index')}"--}%
%{--           class="embossed-link"><i class="fas fa-chevron-left"></i></a>--}%
%{--      </g:else>--}%
%{--      <h1>Add Personal Details</h1>--}%
%{--    </div>--}%
%{--  </div>--}%
%{--</header>--}%

<glass:modernGlassCard
  title="Personal Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Tell us about yourself">

  <form id="personalDetailsForm">

    <%-- ── Date of Birth ─────────────────────────────────────────────── --%>
    <%-- Full width on phones, a third of the row from sm up. --%>
    <section class="row row-padding g-3">
      <div class="form-group col-12 col-sm-4">
        <dp:picker name="birthday" value="1990-05-12" mode="date" class="mr-2" required="true"/>
      </div>
    </section>

    <%-- ── Gender (required) ───────────────────────────────────────────── --%>
    <%--
      Previously these two glass:radio tags were left inside the DOB
      <section> because that section's closing tag had been commented
      out along with the day/month/year fields above — so the DOB
      picker and both gender radios were rendering as one row instead
      of two. Gender now gets its own <section class="row row-padding">
      here (matching Smoking/Drinking below), fixing the row grouping.

      Columns: col-6 keeps the two radio cards side-by-side even on
      phones instead of each stacking full-width, widening slightly
      at col-md-2 as the viewport grows.

      required="true" is set on BOTH radios: the browser treats the whole
      `gender` group as mandatory, and the existing submit handler's
      form.checkValidity() / reportValidity() blocks the save until one
      is chosen.
    --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-sm-3 col-md-2">
        <glass:radio name="gender" value="Male" id="Male" title="Male" required="true"/>
      </div>

      <div class="form-group col-6 col-sm-3 col-md-2">
        <glass:radio name="gender" value="Female" id="Female" title="Female" required="true"/>
      </div>
    </section>

    <%-- ── Religion & Caste ────────────────────────────────────────────── --%>
    <%-- Stacked full-width on phones/small tablets, side-by-side from md
         up so neither select feels cramped when the page narrows. --%>
    <section class="row row-padding g-3">
      <div class="form-group col-12 col-md-6">
        <g:set var="selectedReligion" value="${personalDetails?.religion}"/>
        <glass:select
          name="religion" id="religion" label="Religion"
          placeholder="Select a religion…"
          value="${selectedReligion}"
          from="${religions}"
          optionKey="name" optionValue="name"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="true" disabled="false"/>
      </div>

      <div class="form-group col-12 col-md-6">
        <glass:select
          name="caste" id="caste" label="Caste"
          placeholder="Select a caste…"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="true" disabled="false"/>
      </div>
    </section>

    <%-- ── Smoking Habit ─────────────────────────────────────────────── --%>
    <%-- 2-up grid on phones (col-6), 4-up from md up (col-md-3) so each
         card stays readable instead of squeezing four into one row on a
         narrow screen. --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Non Smoker" id="NonSmoker" title="Non Smoker" required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Social Smoker" id="SocialSmoker" title="Social Smoker" required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Normal Smoker" id="NormalSmoker" title="Normal Smoker" required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Chain Smoker" id="ChainSmoker" title="Chain Smoker" required="true"/>
      </div>
    </section>

    <%-- ── Drinking Habit ───────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Non Drinker" id="NonDrinker" title="Non Drinker" required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Social Drinker" id="SocialDrinker" title="Social Drinker"
                     required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Normal Drinker" id="NormalDrinker" title="Normal Drinker"
                     required="true"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Alcoholic" id="HeavyDrinker" title="Alcoholic" required="true"/>
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
    // ── Religion → Caste cascade ──────────────────────────────────────
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

    if (religionSelect.value) {
      populateCaste(religionSelect.value);
    }

    religionSelect.addEventListener('change', function (ev) {
      const val = ev.target.value;
      populateCaste(val);
      ev.target.blur();
    });

    function populateCaste(selectedReligionName) {
      casteSelect.innerHTML = '';
      const selectedReligion = religionsData.find(r => r.name === selectedReligionName);
      if (selectedReligion && selectedReligion.caste) {
        selectedReligion.caste.forEach(caste => {
          const option = document.createElement('option');
          option.value = caste;
          option.textContent = caste;
          casteSelect.appendChild(option);
        });
        const selectedCaste = "${personalDetails?.caste}";
        if (selectedCaste && selectedReligion.name === "${personalDetails?.religion}") {
          casteSelect.value = selectedCaste;
        }
      }
    }

    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("personalDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // Native validation: blocks the save (and shows the browser's
      // "Please select one of these options" bubble) if gender is unselected.
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      const genderEl = form.querySelector('[name="gender"]:checked');
      const smokingHabitEl = form.querySelector('[name="smokingHabit"]:checked');
      const drinkingHabitEl = form.querySelector('[name="drinkingHabit"]:checked');
      const payload = {
        birthday: form.querySelector('[name="birthday"]').value.trim(),
        gender: genderEl ? genderEl.value : '',
        religion: form.querySelector('[name="religion"]').value.trim(),
        caste: form.querySelector('[name="caste"]').value.trim(),
        smokingHabit: smokingHabitEl ? smokingHabitEl.value : '',
        drinkingHabit: drinkingHabitEl ? drinkingHabitEl.value : ''
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving…';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'savePersonDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Personal details saved successfully!");
          if (data.redirectUrl) {
            setTimeout(() => {
              window.location.href = data.redirectUrl;
            }, 1500);
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
