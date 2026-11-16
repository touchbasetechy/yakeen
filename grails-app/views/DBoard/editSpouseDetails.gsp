<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>,
  <glass:styles/>
  <glass:textInputStyles/>
  <glass:slideAnimationStyles/>
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

<g:set var="spouseCardToggle">
  <glassToggle:toggle name="sameAsPersonalDetails" id="sameAsPersonalDetails" label="Same As Personal Details"
                      disabled="${!personalDetails}"/>
</g:set>

<glass:modernGlassCard
  title="Edit Spouse Preferences"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your spouse preferences"
  headerHtml="${spouseCardToggle}">

  <form id="editSpouseDetailsForm">

    <%-- ── Age Range ─────────────────────────────────────────────── --%>
    <section class="row row-padding g-3" style="padding-bottom: 0;">
      <glass:rangeSlider name="age"
                         min="18" max="100" step="1" dual="true"
                         valueMin="${spouseDetails?.minAge ?: 24}" valueMax="${spouseDetails?.maxAge ?: 50}"
                         label="Age Range"
                         unit=" Years"/>
    </section>


    <%-- ── Greyed-out block — not editable (bleeds past card padding) ── --%>
    <div class="glass-readonly-block gs-readonly-bleed">
      <%-- ── Gender — opposite of personal details ─────────────────── --%>
      <section class="row row-padding g-3">
        <div class="form-group col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Male" id="Male" title="Male"
                       checked="${personalDetails?.gender == 'Female'}" disabled="true"/>
        </div>

        <div class="form-group col-6 col-sm-3 col-md-2">
          <glass:radio name="gender" value="Female" id="Female" title="Female"
                       checked="${personalDetails?.gender == 'Male'}" disabled="true"/>
        </div>

        <glass:text>Fields in this section are not editable.</glass:text>
      </section>
    </div>


    <%-- ── Religion & Caste ──────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-12 col-md-6">
        <glass:select
          name="religion" id="religion" label="Religion"
          placeholder="Select a religion…"
          value="${spouseDetails?.religion}"
          from="${religions}"
          optionKey="name" optionValue="name"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="true"/>
      </div>

      <div class="form-group col-12 col-md-6">
        <glass:select
          name="caste" id="caste" label="Caste"
          placeholder="Select a caste…"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="true"/>
      </div>
    </section>

    <%-- ── Smoking Habit ─────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Non Smoker" id="NonSmoker" title="Non Smoker"
                     checked="${spouseDetails?.smokingHabit == 'Non Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Social Smoker" id="SocialSmoker" title="Social Smoker"
                     checked="${spouseDetails?.smokingHabit == 'Social Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Normal Smoker" id="NormalSmoker" title="Normal Smoker"
                     checked="${spouseDetails?.smokingHabit == 'Normal Smoker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="smokingHabit" value="Chain Smoker" id="ChainSmoker" title="Chain Smoker"
                     checked="${spouseDetails?.smokingHabit == 'Chain Smoker'}"/>
      </div>
    </section>

    <%-- ── Drinking Habit ────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Non Drinker" id="NonDrinker" title="Non Drinker"
                     checked="${spouseDetails?.drinkingHabit == 'Non Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Social Drinker" id="SocialDrinker" title="Social Drinker"
                     checked="${spouseDetails?.drinkingHabit == 'Social Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Normal Drinker" id="NormalDrinker" title="Normal Drinker"
                     checked="${spouseDetails?.drinkingHabit == 'Normal Drinker'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="drinkingHabit" value="Alcoholic" id="HeavyDrinker" title="Alcoholic"
                     checked="${spouseDetails?.drinkingHabit == 'Alcoholic'}"/>
      </div>
    </section>

    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>
  </form>

</glass:modernGlassCard>

<%-- Build the save-URL params without nested GStrings; only add values that exist. --%>
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
    // ── Server-side values, declared once ─────────────────────────────
    const saved = {
      religion: "${spouseDetails?.religion ?: ''}",
      caste: "${spouseDetails?.caste ?: ''}"
    };
    const personal = {
      religion: "${personalDetails?.religion ?: ''}",
      caste: "${personalDetails?.caste ?: ''}",
      smokingHabit: "${personalDetails?.smokingHabit ?: ''}",
      drinkingHabit: "${personalDetails?.drinkingHabit ?: ''}"
    };

    // ── Tracked values (restored when "Same As" toggle is switched off) ──
    let religion = saved.religion;
    let caste = saved.caste;
    let smokingHabit = "${spouseDetails?.smokingHabit ?: ''}";
    let drinkingHabit = "${spouseDetails?.drinkingHabit ?: ''}";

    const form = document.getElementById("editSpouseDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');
    const religionSelect = document.getElementById("religion");
    const casteSelect = document.getElementById("caste");

    // ── Religion → Caste cascade ──────────────────────────────────────
    const religionsData = [
      <g:each in="${religions}" var="rel" status="i">
      {
        name: "${raw(rel.name)}",
        caste: [<g:each in="${rel.caste}" var="c" status="j">"${raw(c)}"${j < rel.caste.size() - 1 ? ',' : ''}</g:each>]
      }${i < religions.size() - 1 ? ',' : ''}
      </g:each>
    ];

    function populateCaste(selectedReligionName) {
      casteSelect.innerHTML = '<option value="Doesn\'t Matter">Doesn\'t Matter</option>';
      const rel = religionsData.find(function (r) {
        return r.name === selectedReligionName;
      });
      if (rel && rel.caste) {
        rel.caste.forEach(function (c) {
          const opt = document.createElement("option");
          opt.value = c;
          opt.textContent = c;
          casteSelect.appendChild(opt);
        });
        if (saved.caste && rel.name === saved.religion) {
          casteSelect.value = saved.caste;
        }
      }
    }

    if (religionSelect.value) {
      populateCaste(religionSelect.value);
    }

    religionSelect.addEventListener("change", function (e) {
      religion = e.target.value;
      populateCaste(religion);
    });

    casteSelect.addEventListener("change", function (e) {
      caste = e.target.value;
    });

    // ── Radio helpers ─────────────────────────────────────────────────
    function setRadio(name, value) {
      document.querySelectorAll('input[name="' + name + '"]').forEach(function (r) {
        r.checked = (r.value === value);
      });
    }

    // Same-name radios are already mutually exclusive natively; just track the value.
    document.querySelectorAll('input[name="smokingHabit"]').forEach(function (radio) {
      radio.addEventListener("change", function () {
        if (radio.checked) smokingHabit = radio.value;
      });
    });
    document.querySelectorAll('input[name="drinkingHabit"]').forEach(function (radio) {
      radio.addEventListener("change", function () {
        if (radio.checked) drinkingHabit = radio.value;
      });
    });

    // ── Same As Personal Details toggle ───────────────────────────────
    const toggle = document.getElementById("sameAsPersonalDetails");
    if (toggle) {
      toggle.checked =
        religion === personal.religion &&
        caste === personal.caste &&
        smokingHabit === personal.smokingHabit &&
        drinkingHabit === personal.drinkingHabit;

      toggle.addEventListener("change", function (ev) {
        if (ev.target.checked) {
          religionSelect.value = personal.religion;
          populateCaste(personal.religion);
          casteSelect.value = personal.caste;
          setRadio("smokingHabit", personal.smokingHabit);
          setRadio("drinkingHabit", personal.drinkingHabit);
        } else {
          religionSelect.value = religion;
          populateCaste(religion);
          casteSelect.value = caste;
          setRadio("smokingHabit", smokingHabit);
          setRadio("drinkingHabit", drinkingHabit);
        }
      });
    }

    // ── Read the form into a plain object (read-only gender is not sent) ──
    function buildPayload() {
      const smokingHabitEl = form.querySelector('[name="smokingHabit"]:checked');
      const drinkingHabitEl = form.querySelector('[name="drinkingHabit"]:checked');
      const minAgeField = form.querySelector('[name="ageMin"], [name="minAge"], [name="age_min"]');
      const maxAgeField = form.querySelector('[name="ageMax"], [name="maxAge"], [name="age_max"]');
      return {
        minAge: minAgeField ? minAgeField.value : '',
        maxAge: maxAgeField ? maxAgeField.value : '',
        religion: religionSelect.value,
        caste: casteSelect.value,
        smokingHabit: smokingHabitEl ? smokingHabitEl.value : '',
        drinkingHabit: drinkingHabitEl ? drinkingHabitEl.value : ''
      };
    }

    // ── Save URL (raw() stops &amp; encoding between params) ──────────
    const saveUrl = '${raw(createLink(controller: 'DBoard', action: 'updateSpouseDetails', params: saveUrlParams))}';

    // ── Async form submission ─────────────────────────────────────────
    form.addEventListener("submit", async function (e) {
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
        const contentType = (response.headers.get("Content-Type") || "").toLowerCase();
        const isJson = contentType.includes("application/json");
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
          console.error("Expected JSON from updateSpouseDetails but got " + reason, {
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
          window.showSuccess(data.message || "Spouse preferences updated successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 404) {
          // Controller (updateSpouseDetails): "Spouse details not found."
          window.showWarning(data.message || "Spouse details not found.");

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
