<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>
  <glass:styles/>
  <glass:textInputStyles/>
  <dp:resources/>
  <asset:stylesheet src="gs-glass-border-hover.css"/>
  <%--
    Responsive tweaks live here rather than scattered inline styles, so the
    breakpoints are easy to find/adjust in one place.
    (.gs-readonly-bleed and the .gs-slider transparency live in the CSS file.)
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
  title="Edit Personal Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="${params?.ac == 'an' ? 'AdminDBoard' : 'DBoard'}"
  iconAction="${params?.ac == 'an' ? 'addPerson' : 'index'}"
  iconParams="${params?.ac == 'an' ? [id: params?.id, ac: params?.ac] : [:]}"
  titleHtml="true"
  subtitle="Update your personal details">

  <form id="editPersonDetailsForm">

    <%-- ── Greyed-out block — not editable (bleeds past card padding) ── --%>
    <div class="glass-readonly-block gs-readonly-bleed">

      <%-- ── Date of Birth ─────────────────────────────────────────── --%>
      <section class="row row-padding g-3">
        <div class="form-group col-12 col-sm-4">
          <dp:picker name="birthday" value="${personalDetails?.dob?.toString()}" mode="date"
                     disabled="true" class="mr-2"/>
        </div>
      </section>

      <%-- ── Gender ──────────────────────────────────────────────────── --%>
      <section class="row row-padding g-3">
        <div class="form-group col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Male" id="Male" title="Male"
                       checked="${personalDetails?.gender == 'Male'}" disabled="true"/>
        </div>

        <div class="form-group col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Female" id="Female" title="Female"
                       checked="${personalDetails?.gender == 'Female'}" disabled="true"/>
        </div>
      </section>

      <%-- ── Religion & Caste ────────────────────────────────────────── --%>
      <section class="row row-padding g-3">
        <div class="form-group col-12 col-md-6">
          <glass:select
            name="religion" id="religion" label="Religion"
            placeholder="Select a religion…"
            value="${personalDetails?.religion}"
            optionKey="name" optionValue="name"
            noSelection="${['': '-- Select --']}"
            multiple="false" disabled="true"/>
        </div>

        <div class="form-group col-12 col-md-6">
          <glass:select
            name="caste" id="caste" label="Caste"
            placeholder="Select a caste…"
            value="${personalDetails?.caste}"
            noSelection="${['': '-- Select --']}"
            multiple="false" required="true" disabled="true"/>
        </div>
      </section>

      <glass:text>Fields in this section are not editable</glass:text>
    </div><%-- /.gs-readonly-bleed --%>

  <%-- ── Smoking Habit ─────────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Non Smoker" id="NonSmoker" title="Non Smoker"
                     checked="${personalDetails?.smokingHabit == 'Non Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Social Smoker" id="SocialSmoker" title="Social Smoker"
                     checked="${personalDetails?.smokingHabit == 'Social Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Normal Smoker" id="NormalSmoker" title="Normal Smoker"
                     checked="${personalDetails?.smokingHabit == 'Normal Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Chain Smoker" id="ChainSmoker" title="Chain Smoker"
                     checked="${personalDetails?.smokingHabit == 'Chain Smoker'}"/>
      </div>
    </section>

    <%-- ── Drinking Habit ───────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Non Drinker" id="NonDrinker" title="Non Drinker"
                     checked="${personalDetails?.drinkingHabit == 'Non Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Social Drinker" id="SocialDrinker" title="Social Drinker"
                     checked="${personalDetails?.drinkingHabit == 'Social Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Normal Drinker" id="NormalDrinker" title="Normal Drinker"
                     checked="${personalDetails?.drinkingHabit == 'Normal Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Alcoholic" id="HeavyDrinker" title="Alcoholic"
                     checked="${personalDetails?.drinkingHabit == 'Alcoholic'}"/>
      </div>
    </section>

    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>`
  </form>

</glass:modernGlassCard>

<%-- Build the save-URL params without GStrings; only add values that exist. --%>
<%
  Map saveUrlParams = [:]

  if (params?.ac) {
    saveUrlParams.put('ac', params.ac)
  }

  def currentSlot = request.getAttribute('currentSlot')
  if (currentSlot != null) {
    saveUrlParams.put('slot', currentSlot)
  }
%>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Shared: read the form into a plain object ─────────────────────
    const form = document.getElementById("editPersonDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    function buildPayload() {
      const smokingHabitEl = form.querySelector('[name="smokingHabit"]:checked');
      const drinkingHabitEl = form.querySelector('[name="drinkingHabit"]:checked');
      return {
        smokingHabit: smokingHabitEl ? smokingHabitEl.value : '',
        drinkingHabit: drinkingHabitEl ? drinkingHabitEl.value : ''
      };
    }

    // ── Save URL (raw() stops &amp; encoding between params) ──────────
    const saveUrl = '${raw(createLink(controller: 'DBoard', action: 'updatePersonDetails', params: saveUrlParams))}';

    // ── Async form submission ─────────────────────────────────────────
    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      const payload = buildPayload();

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(saveUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest"
          },
          body: JSON.stringify(payload)
        });

        // ── Is it a JSON response? ───────────────────────────────────
        const contentType = (response.headers.get('Content-Type') || '').toLowerCase();
        const isJson = contentType.includes('application/json');
        const raw = await response.text();

        let data = null;
        if (isJson && raw) {
          try {
            data = JSON.parse(raw);
          } catch (parseErr) {
            console.error("Response claimed to be JSON but failed to parse:", parseErr, raw.slice(0, 300));
          }
        }

        if (!data) {
          let reason;
          if (response.redirected) {
            reason = "redirected to " + response.url + " (session expired or login required?)";
          } else if (!raw) {
            reason = "empty response body";
          } else if (!isJson) {
            reason = "not JSON (Content-Type: " + (contentType || "none") + ")";
          } else {
            reason = "invalid JSON";
          }
          console.error("Expected JSON from updatePersonDetails but got " + reason, {
            status: response.status,
            contentType: contentType,
            body: raw.slice(0, 300)
          });
          window.showError("Unexpected server response (" + reason + "). Status " + response.status + ".");
          return;
        }

        console.log("JSON response received:", response.status, data);

        // ── Handle the JSON payload ──────────────────────────────────
        if (response.ok && data.success) {
          window.showSuccess(data.message || "Personal details updated successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 404) {
          // Controller (updatePersonDetails): "Personal details not found."
          window.showWarning(data.message || "Personal details not found.");

        } else if (response.status === 400) {
          window.showWarning(data.message || "Please check your input and try again.");

        } else {
          window.showError(data.message || "Something went wrong. Please try again.");
        }

      } catch (err) {
        console.error("Save failed:", err);
        window.showError("Network error \u2014 please check your connection.");

      } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalLabel;
      }
    });

  });
</script>
</body>
</html>
