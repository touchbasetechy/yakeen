<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <%-- Ensure mobile browsers use the real device width (safe to keep even if the layout already has it) --%>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
</head>

<body>

<%-- Saved record: prefer the controller's model key, fall back to the signed-in person.
     .properties is a Map, so a property missing on the domain reads as null instead of throwing. --%>
<g:set var="saved" value="${(additionalSpouseDetails ?: person?.additionalSpouseDetails)?.properties ?: [:]}"/>

<glass:modernGlassCard
  title="Edit Additional Spouse Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your additional spouse details">

  <form id="additionalSpouseDetailsForm" novalidate>

    <%-- ── Education + Profession ─────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">
        <glass:select name="education" label="Education"
                      from="${educations}" optionKey="name" optionValue="name"
                      value="${saved.education}"/>
      </div>

      <div class="col-12 col-md-6">
        <glass:alphaNumericTextField
          name="profession" id="profession" label="Profession"
          value="${saved.profession}"
          maxlength="50" required="true"/>
      </div>
    </section>

    <%-- ── Salary ──────────────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">
        <sheet:currencyPicker
          id="currency1"
          currencies="${currencies}"
          currencyCodeName="currencyCode"
          currencyName="currencyName"
          symbolName="currencySymbol"
          flagName="currencyFlag"
          selectedCode="${saved.currencyCode ?: 'INR'}"
          selectedCurrency="${saved.currencyName ?: 'Indian rupee'}"
          selectedSymbol="${saved.currencySymbol ?: '\u20B9'}"
          selectedFlag="${saved.currencyFlag ?: 'in'}"
          buttonText="Select Currency"
          panelTitle="Select Currency"
          showCurrencyName="true"/>
      </div>

      <div class="col-12 col-md-6">
        <glass:formattedNumericField
          name="salary" id="salary" label="Salary"
          value="${saved.salary}"
          maxlength="10" required="true"/>
      </div>
    </section>

    <%-- ── Salary Period (wraps onto multiple lines on small screens) ── --%>
    <section class="row row-padding g-3">
      <div class="col-12 d-flex flex-wrap gap-3">
        <glass:radio name="salaryFor" id="perAnum" value="Per Anum" title="Per Anum"
                     checked="${saved.salaryFor == 'Per Anum'}"/>
        <glass:radio name="salaryFor" id="perMonth" value="Per Month" title="Per Month"
                     checked="${saved.salaryFor == 'Per Month'}"/>
        <glass:radio name="salaryFor" id="perWeek" value="Per Week" title="Per Week"
                     checked="${saved.salaryFor == 'Per Week'}"/>
        <glass:radio name="salaryFor" id="perDay" value="Per Day" title="Per Day"
                     checked="${saved.salaryFor == 'Per Day'}"/>
        <glass:radio name="salaryFor" id="perHour" value="Per Hour" title="Per Hour"
                     checked="${saved.salaryFor == 'Per Hour'}"/>
      </div>
    </section>

    <%-- ── Work Location + Working Country ────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="col-12 col-md-6">
        <glass:alphaNumericTextField
          name="workLocation" id="workLocation" label="Work Location"
          value="${saved.workLocation}"
          required="true"/>
      </div>

      <div class="col-12 col-md-6">
        <sheet:countryNamePicker
          id="workingCountry"
          name="workingCountry"
          label="Working Country"
          countries="${countries}"
          countryName="workingCountry"
          flagName="workingCountryFlag"
          selectedCountry="${saved.workingCountry ?: 'India'}"
          buttonText="Select Country"
          panelTitle="Select Country"
          style="max-width:320px"/>
      </div>
    </section>

    <%-- ── Save (full width on all screen sizes) ───────────────────── --%>
    <div class="row row-padding">
      <div class="col-12 d-grid save-row">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>

  </form>

</glass:modernGlassCard>

<style>
/* Make the Save button fill its row, regardless of how glass:cardBtn renders it */
.save-row > button,
.save-row > a,
.save-row .btn {
  width: 100%;
}
</style>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    var form = document.getElementById("additionalSpouseDetailsForm");
    var saveBtn = form.querySelector('button[type="submit"]');

    // Safe field reader: never throws if a control is missing, always returns a trimmed string.
    function val(name) {
      var el = form.querySelector('[name="' + name + '"]');
      return el && el.value != null ? String(el.value).trim() : '';
    }

    // Working country: the picker writes its hidden input under countryName="workingCountry".
    // Fallback to the first hidden input inside the picker wrapper in case the tag names it differently.
    function workingCountry() {
      var v = val('workingCountry');
      if (v) return v;
      var h = form.querySelector('#workingCountry input[type="hidden"]');
      return h && h.value ? String(h.value).trim() : '';
    }

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload (keys match the AdditionalSpouseDetails properties) ──
      var salaryForEl = form.querySelector('[name="salaryFor"]:checked');
      var payload = {
        education: val('education'),
        profession: val('profession'),
        currencyCode: val('currencyCode'),
        currencyName: val('currencyName'),
        currencySymbol: val('currencySymbol'),
        currencyFlag: val('currencyFlag'),
        salary: val('salary').replace(/[,\s]/g, ''),   // formatted "1,00,000" -> "100000"
        salaryFor: salaryForEl ? salaryForEl.value : '',
        workLocation: val('workLocation'),
        workingCountry: workingCountry()
      };

      saveBtn.disabled = true;
      var originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        var response = await fetch(
          "${createLink(controller: 'DBoard', action: 'updateAdditionalSpouseDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Additional spouse details updated successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 404) {
          window.showWarning(data.message || "Additional spouse details not found.");

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
