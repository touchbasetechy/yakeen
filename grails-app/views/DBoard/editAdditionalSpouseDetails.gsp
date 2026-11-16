<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:dotMeshStyles/>
  <glass:styles/>
</head>

<body>

<glass:modernGlassCard
  title="Edit Additional Spouse Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your additional spouse details">

  <form id="additionalSpouseDetailsForm">

    <%-- ── Education ───────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:select
          name="education" id="education" label="Education"
          placeholder="Select an education…"
          from="${educations}"
          optionKey="name" optionValue="name"
          noSelection="${['': 'Doesnt Matter']}"
          value="${person?.additionalSpouseDetails?.education}"
          multiple="false" required="false" disabled="false"/>
      </div>
    </section>

    <%-- ── Profession ──────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:alphaNumericTextField
          name="profession" id="profession" label="Profession"
          value="${person?.additionalSpouseDetails?.profession}"
          maxlength="50" required="true"/>
      </div>
    </section>

    <section class="row row-padding">
      <%-- ── Salary ──────────────────────────────────────────────────── --%>
      <div class="col-sm-12">
        <glass:formattedNumericField
          name="salary" id="salary" label="Salary"
          value="${person?.additionalSpouseDetails?.salary}"
          maxlength="10" required="true"/>
      </div>
    </section>

    <%-- ── Salary Period ────────────────────────────────────────────── --%>
    <section class="row">
      <glass:radio name="salaryFor" id="perAnum" value="Per Anum" title="Per Anum"
                   checked="${person?.additionalSpouseDetails?.salaryFor == 'Per Anum'}"/>
      <glass:radio name="salaryFor" id="perMonth" value="Per Month" title="Per Month"
                   checked="${person?.additionalSpouseDetails?.salaryFor == 'Per Month'}"/>
      <glass:radio name="salaryFor" id="perWeek" value="Per Week" title="Per Week"
                   checked="${person?.additionalSpouseDetails?.salaryFor == 'Per Week'}"/>
      <glass:radio name="salaryFor" id="perDay" value="Per Day" title="Per Day"
                   checked="${person?.additionalSpouseDetails?.salaryFor == 'Per Day'}"/>
      <glass:radio name="salaryFor" id="perHour" value="Per Hour" title="Per Hour"
                   checked="${person?.additionalSpouseDetails?.salaryFor == 'Per Hour'}"/>
    </section>

    <%-- ── Work Location ───────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">

        <glass:alphaNumericTextField
          name="workLocation" id="workLocation" label="Work Location"
          value="${person?.additionalSpouseDetails?.workLocation}"
          required="true"/>
      </div>
    </section>

    <%-- ── Working Country ────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:select
          name="workingCountry" id="workingCountry" label="Working Country"
          placeholder="Select a country…"
          from="${countries}"
          optionKey="name" optionValue="name"
          noSelection="${['': 'Doesnt Matter']}"
          value="${person?.additionalSpouseDetails?.workingCountry}"
          multiple="false" required="false" disabled="false"/>
      </div>
    </section>

    <%-- ── Save ───────────────────────────────────────────────────── --%>
    <div class="row row-padding">
      <div class="col-sm-12 d-flex justify-content-end">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>

  </form>

</glass:modernGlassCard>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    var form = document.getElementById('additionalSpouseDetailsForm');
    var saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      var salaryForEl = form.querySelector('[name="salaryFor"]:checked');
      var payload = {
        education: form.querySelector('[name="education"]').value.trim(),
        profession: form.querySelector('[name="profession"]').value.trim(),
        salary: form.querySelector('[name="salary"]').value.trim(),
        salaryFor: salaryForEl ? salaryForEl.value : '',
        workLocation: form.querySelector('[name="workLocation"]').value.trim(),
        workingCountry: form.querySelector('[name="workingCountry"]').value.trim()
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
