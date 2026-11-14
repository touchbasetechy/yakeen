<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
</head>

<body>

<glass:modernGlassCard
  title="Additional Spouse Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Tell us more about your spouse">

  <form id="additionalSpouseDetailsForm">

    <%-- ── Education ───────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:select
          name="education" id="education" label="Education">
          <option value="Doesn't Matter">Doesn't Matter</option>
          <g:each in="${educations}" var="education">
            <option value="${education?.name}">${education?.name}</option>
          </g:each>
        </glass:select>
      </div>
    </section>

    <%-- ── Profession ──────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="profession" id="profession" label="Profession"
          maxlength="50" required="true"/>
      </div>
    </section>

    <%-- ── Salary ──────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:positiveNumericField
          name="salary" id="salary" label="Salary"
          maxlength="10" required="true"/>
      </div>
    </section>

    <%-- ── Salary Period ───────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="salaryFor" id="perAnum" value="Per Anum" title="Per Anum"/>
      <glass:radio name="salaryFor" id="perMonth" value="Per Month" title="Per Month"/>
      <glass:radio name="salaryFor" id="perWeek" value="Per Week" title="Per Week"/>
      <glass:radio name="salaryFor" id="perDay" value="Per Day" title="Per Day"/>
      <glass:radio name="salaryFor" id="perHour" value="Per Hour" title="Per Hour"/>
    </section>

    <%-- ── Work Location ──────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="workLocation" id="workLocation" label="Work Location" required="true"/>
      </div>
    </section>

    <%-- ── Working Country ────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:select
          name="workingCountry" id="workingCountry" label="Working Country">
          <option value="Doesn't Matter">Doesn't Matter</option>
          <g:each in="${countries}" var="country">
            <option value="${country?.name}">${country?.name}</option>
          </g:each>
        </glass:select>
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
    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("additionalSpouseDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      const salaryForEl = form.querySelector('[name="salaryFor"]:checked');
      const payload = {
        education: form.querySelector('[name="education"]').value.trim(),
        profession: form.querySelector('[name="profession"]').value.trim(),
        salary: form.querySelector('[name="salary"]').value.trim(),
        salaryFor: salaryForEl ? salaryForEl.value : '',
        workLocation: form.querySelector('[name="workLocation"]').value.trim(),
        workingCountry: form.querySelector('[name="workingCountry"]').value.trim()
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving…';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveAdditionalSpouseDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Spouse details saved successfully!");
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
