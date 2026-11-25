<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>
  <glass:styles/>
  <glass:textInputStyles/>
</head>

<body>

<g:set var="spouseFamilyCardToggle">
  <glassToggle:toggle name="sameAsFamilyDetails" id="sameAsFamilyDetails" label="Same As Family Details"
                      disabled="${!familyDetails}"/>
</g:set>

<glass:modernGlassCard
  title="Edit Spouse Family Preferences"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your spouse family preferences"
  headerHtml="${spouseFamilyCardToggle}">

  <form id="editSpouseFamilyDetailsForm">
    <%-- ── Marital Status ─────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="maritalStatus" value="UnMarried" id="UnMarried"
                   title="UnMarried"
                   checked="${spouseFamilyDetails?.maritalStatus == 'UnMarried'}"/>
      <glass:radio name="maritalStatus" value="Divorced" id="Divorced"
                   title="Divorced"
                   checked="${spouseFamilyDetails?.maritalStatus == 'Divorced'}"/>
      <glass:radio name="maritalStatus" value="Awaiting Divorce" id="AwaitingDivorce"
                   title="Awaiting Divorce"
                   checked="${spouseFamilyDetails?.maritalStatus == 'Awaiting Divorce'}"/>
      <glass:radio name="maritalStatus" value="Widowed" id="Widowed"
                   title="Widowed"
                   checked="${spouseFamilyDetails?.maritalStatus == 'Widowed'}"/>
    </section>

    <%-- ── Children — revealed when not UnMarried ───────────────── --%>
    <section class="row row-padding d-none" id="children">
      <glass:radio name="children" value="No Children" id="NoChildren"
                   title="No Children"
                   checked="${spouseFamilyDetails?.children == 'No Children'}"/>
      <glass:radio name="children" value="1" id="Children1"
                   title="1"
                   checked="${spouseFamilyDetails?.children == '1'}"/>
      <glass:radio name="children" value="2" id="Children2"
                   title="2"
                   checked="${spouseFamilyDetails?.children == '2'}"/>
      <glass:radio name="children" value="3" id="Children3"
                   title="3"
                   checked="${spouseFamilyDetails?.children == '3'}"/>
      <glass:radio name="children" value="4 and more" id="Children4"
                   title="4 and more"
                   checked="${spouseFamilyDetails?.children == '4 and more'}"/>
    </section>

    <%-- ── Child Status — revealed when children > 0 ────────────── --%>
    <section class="row row-padding d-none" id="child_status">
      <glass:radio name="childrenStatus" value="Living with me" id="LivingWithMe"
                   title="Living with me"
                   checked="${spouseFamilyDetails?.childrenStatus == 'Living with me'}"/>
      <glass:radio name="childrenStatus" value="Not living with me" id="NotLivingWithMe"
                   title="Not living with me"
                   checked="${spouseFamilyDetails?.childrenStatus == 'Not living with me'}"/>
    </section>

    <%-- ── Height & Weight (dual-thumb ranges) ───────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:heightRangeSlider name="partnerHeight"
                                 min="140" max="210" step="1" dual="true"
                                 valueMin="${spouseFamilyDetails?.minHeight ?: 155}"
                                 valueMax="${spouseFamilyDetails?.maxHeight ?: 185}"
                                 label="Height" unit="${spouseFamilyDetails?.heightUnit ?: 'ft'}"/>
      </div>

      <div class="col-sm-6">
        <glass:weightRangeSlider name="partnerWeight"
                                 min="35" max="150" step="0.5" dual="true"
                                 valueMin="${spouseFamilyDetails?.minWeight ?: 50}"
                                 valueMax="${spouseFamilyDetails?.maxWeight ?: 80}"
                                 label="Weight" unit="${spouseFamilyDetails?.weightUnit ?: 'lb'}"/>
      </div>
    </section>

    <%-- ── Family Status ─────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="familyStatus" value="Middle Class" id="MiddleClass"
                   title="Middle Class"
                   checked="${spouseFamilyDetails?.familyStatus == 'Middle Class'}"/>
      <glass:radio name="familyStatus" value="Upper Middle Class" id="UpperMiddleClass"
                   title="Upper Middle Class"
                   checked="${spouseFamilyDetails?.familyStatus == 'Upper Middle Class'}"/>
      <glass:radio name="familyStatus" value="High Class" id="HighClass"
                   title="High Class"
                   checked="${spouseFamilyDetails?.familyStatus == 'High Class'}"/>
      <glass:radio name="familyStatus" value="Rich / Affluent" id="RichAffluent"
                   title="Rich / Affluent"
                   checked="${spouseFamilyDetails?.familyStatus == 'Rich / Affluent'}"/>
    </section>

    <%-- ── Family Type ────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="familyType" value="Joint Family" id="JointFamily"
                   title="Joint Family"
                   checked="${spouseFamilyDetails?.familyType == 'Joint Family'}"/>
      <glass:radio name="familyType" value="Nuclear Family" id="NuclearFamily"
                   title="Nuclear Family"
                   checked="${spouseFamilyDetails?.familyType == 'Nuclear Family'}"/>
    </section>

    <%-- ── Family Values ──────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="familyValues" value="Orthodox" id="Orthodox"
                   title="Orthodox"
                   checked="${spouseFamilyDetails?.familyValues == 'Orthodox'}"/>
      <glass:radio name="familyValues" value="Traditional" id="Traditional"
                   title="Traditional"
                   checked="${spouseFamilyDetails?.familyValues == 'Traditional'}"/>
      <glass:radio name="familyValues" value="Moderate" id="Moderate"
                   title="Moderate"
                   checked="${spouseFamilyDetails?.familyValues == 'Moderate'}"/>
      <glass:radio name="familyValues" value="Liberal" id="Liberal"
                   title="Liberal"
                   checked="${spouseFamilyDetails?.familyValues == 'Liberal'}"/>
    </section>

    <%-- ── Disabilities ───────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="disabilities" value="Normal" id="Normal"
                   title="Normal"
                   checked="${spouseFamilyDetails?.disabilities == 'Normal'}"/>
      <glass:radio name="disabilities" value="Differently Abled" id="DifferentlyAbled"
                   title="Differently Abled"
                   checked="${spouseFamilyDetails?.disabilities == 'Differently Abled'}"/>
    </section>

    <%-- ── Save ──────────────────────────────────────────────────── --%>
    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>

  </form>

</glass:modernGlassCard>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {

    // ── Server values, JS-escaped (safe for quotes / apostrophes) ───
    var SPOUSE = {
      maritalStatus: "${raw(spouseFamilyDetails?.maritalStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      children: "${raw(spouseFamilyDetails?.children?.toString()?.encodeAsJavaScript() ?: '')}",
      childrenStatus: "${raw(spouseFamilyDetails?.childrenStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      familyStatus: "${raw(spouseFamilyDetails?.familyStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      familyType: "${raw(spouseFamilyDetails?.familyType?.toString()?.encodeAsJavaScript() ?: '')}",
      familyValues: "${raw(spouseFamilyDetails?.familyValues?.toString()?.encodeAsJavaScript() ?: '')}",
      disabilities: "${raw(spouseFamilyDetails?.disabilities?.toString()?.encodeAsJavaScript() ?: '')}"
    };
    var FAMILY = {
      exists:         ${familyDetails ? 'true' : 'false'},
      maritalStatus: "${raw(familyDetails?.maritalStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      children: "${raw(familyDetails?.children?.toString()?.encodeAsJavaScript() ?: '')}",
      childrenStatus: "${raw(familyDetails?.childrenStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      familyStatus: "${raw(familyDetails?.familyStatus?.toString()?.encodeAsJavaScript() ?: '')}",
      familyType: "${raw(familyDetails?.familyType?.toString()?.encodeAsJavaScript() ?: '')}",
      familyValues: "${raw(familyDetails?.familyValues?.toString()?.encodeAsJavaScript() ?: '')}",
      disabilities: "${raw(familyDetails?.disabilities?.toString()?.encodeAsJavaScript() ?: '')}"
    };
    var RADIO_FIELDS = ['maritalStatus', 'children', 'childrenStatus',
      'familyStatus', 'familyType', 'familyValues', 'disabilities'];

    // ── classList helpers ──────────────────────────────────────────
    function show(el) {
      el.classList.remove('d-none');
      el.classList.add('animate__animated', 'animate__fadeInDown');
    }

    function hide(el) {
      el.classList.add('d-none');
      el.classList.remove('animate__animated', 'animate__fadeInDown');
    }

    function clearRadios(name) {
      document.querySelectorAll('input[name="' + name + '"]')
        .forEach(function (r) {
          r.checked = false;
        });
    }

    function setRadio(name, value) {
      document.querySelectorAll('input[name="' + name + '"]')
        .forEach(function (r) {
          r.checked = (r.value === value);
        });
    }

    function syncVisibility(ms, ch) {
      if (!ms || ms === 'UnMarried') {
        hide(childrenSection);
        hide(childStatusSection);
      } else {
        show(childrenSection);
        if (ch && ch !== 'No Children') {
          show(childStatusSection);
        } else {
          hide(childStatusSection);
        }
      }
    }

    var childrenSection = document.getElementById('children');
    var childStatusSection = document.getElementById('child_status');
    var toggle = document.getElementById('sameAsFamilyDetails');

    // ── Marital Status → show/hide Children row ────────────────────
    document.querySelectorAll('input[name="maritalStatus"]')
      .forEach(function (radio) {
        radio.addEventListener('change', function () {
          if (this.value === 'UnMarried') {
            hide(childrenSection);
            hide(childStatusSection);
            clearRadios('children');
            clearRadios('childrenStatus');
          } else {
            show(childrenSection);
            hide(childStatusSection);
            clearRadios('childrenStatus');
          }
        });
      });

    // ── Children → show/hide Child Status row ─────────────────────
    document.querySelectorAll('input[name="children"]')
      .forEach(function (radio) {
        radio.addEventListener('change', function () {
          if (this.value === 'No Children') {
            hide(childStatusSection);
            clearRadios('childrenStatus');
          } else {
            show(childStatusSection);
          }
        });
      });

    // ── Restore initial visibility from server-side values ─────────
    if (SPOUSE.maritalStatus && SPOUSE.maritalStatus !== 'UnMarried') {
      show(childrenSection);
      if (SPOUSE.children && SPOUSE.children !== 'No Children') {
        show(childStatusSection);
      }
    }

    // ── Same as Family Details toggle ─────────────────────────────
    // Height / weight are preference RANGES (min–max sliders) here but single
    // values on Family Details, so they are not copied or compared.
    if (toggle && FAMILY.exists) {
      toggle.checked = RADIO_FIELDS.every(function (f) {
        return SPOUSE[f] === FAMILY[f];
      });

      toggle.addEventListener('change', function (ev) {
        var src = ev.target.checked ? FAMILY : SPOUSE;
        RADIO_FIELDS.forEach(function (f) {
          setRadio(f, src[f]);
        });
        syncVisibility(src.maritalStatus, src.children);
      });

      // Editing any radio by hand means it's no longer "same as family"
      RADIO_FIELDS.forEach(function (f) {
        document.querySelectorAll('input[name="' + f + '"]').forEach(function (r) {
          r.addEventListener('change', function (ev) {
            if (ev.isTrusted) toggle.checked = false;
          });
        });
      });
    }

    // ── Dual-slider reader (min / max) ────────────────────────────
    // Reads the slider's submitted inputs by name prefix, so it works whether
    // the slider names them <name>Min/<name>Max or just emits two inputs.
    function readRange(form, prefix) {
      var min = null, max = null, rest = [];
      form.querySelectorAll('input[name^="' + prefix + '"]').forEach(function (i) {
        var n = i.name.toLowerCase();
        if (/min$/.test(n)) min = i.value;
        else if (/max$/.test(n)) max = i.value;
        else rest.push(i.value);
      });
      if (min === null && rest.length) min = rest.shift();
      if (max === null && rest.length) max = rest.shift();
      return {min: min, max: max};
    }

    function checkedValue(form, name) {
      var el = form.querySelector('[name="' + name + '"]:checked');
      return el ? el.value : '';
    }

    // ── Async form submission ──────────────────────────────────────
    var form = document.getElementById("editSpouseFamilyDetailsForm");
    var saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      var height = readRange(form, 'partnerHeight');
      var weight = readRange(form, 'partnerWeight');

      var payload = {
        maritalStatus: checkedValue(form, 'maritalStatus'),
        children: checkedValue(form, 'children'),
        childrenStatus: checkedValue(form, 'childrenStatus'),
        minHeight: height.min,
        maxHeight: height.max,
        minWeight: weight.min,
        maxWeight: weight.max,
        familyStatus: checkedValue(form, 'familyStatus'),
        familyType: checkedValue(form, 'familyType'),
        familyValues: checkedValue(form, 'familyValues'),
        disabilities: checkedValue(form, 'disabilities')
      };

      saveBtn.disabled = true;
      var originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        var response = await fetch(
          "${raw(createLink(controller: 'DBoard', action: 'updateSpouseFamilyDetails', params: (request.getAttribute('currentSlot') ? [slot: request.getAttribute('currentSlot')] : [:])))}",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "X-Requested-With": "XMLHttpRequest"
            },
            body: JSON.stringify(payload)
          }
        );
        var data = await response.json();

        if (response.ok && data.success) {
          window.showSuccess(data.message || "Spouse family preferences updated successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }
        } else if (response.status === 404) {
          window.showWarning(data.message || "Spouse family details not found.");
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
