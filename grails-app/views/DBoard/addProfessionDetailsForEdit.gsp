<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:dotMeshStyles/>
  <glass:styles/>
  <glass:modalAssets/>
</head>

<body>

<glass:modernGlassCard
  title="Profession Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="editProfessionDetails"
  titleHtml="true"
  subtitle="Tell us about your profession">

  <form id="addProfessionDetailsForm">

    <%-- ── Designation & Organisation ─────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="designation" id="designation" label="Designation" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="organisation" id="organisation" label="Organisation" required="true"/>
      </div>
    </section>

    <%-- ── Organisation Location ──────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:alphaNumericTextField
          name="organisationLocation" id="organisationLocation"
          label="Location" required="true"/>
      </div>
    </section>

    <%-- ── Job Category ────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="jobCategory" id="govt_psu" value="Government / PSU" title="Government / PSU"/>
      <glass:radio name="jobCategory" id="private_sector" value="Private Sector" title="Private Sector"/>
      <glass:radio name="jobCategory" id="business" value="Business" title="Business"/>
      <glass:radio name="jobCategory" id="defence" value="Defence" title="Defence"/>
      <glass:radio name="jobCategory" id="self_employed" value="Self Employed" title="Self Employed"/>
      <glass:radio name="jobCategory" id="un_employed" value="Un Employed" title="Un Employed"/>
    </section>

    <%-- ── Currency & Salary ──────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-4">
        <glass:select
          name="currencyCode" id="currencyCode" label="Currency"
          placeholder="Select a currency…"
          from="${currencies}"
          optionKey="cc" optionValue="name"
          noSelection="${['': '-- Select --']}"
          multiple="false" required="true" disabled="false"/>
        <input type="hidden" id="denomination" name="denomination"/>
      </div>

      <div class="col-sm-8">
        <glass:formattedNumericField
          name="salary" id="salary" label="Salary"
          maxlength="15" required="true"/>
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

    <%-- ── Is Current Job ─────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:dotMesh name="isCurrent" label="Is Current Job"/>
      </div>
    </section>

    <div class="row row-padding">
      <div class="col-sm-12 d-flex justify-content-end">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>

  </form>

</glass:modernGlassCard>

<g:render template="professionStatusModal"/>
<g:render template="professionModal"/>


<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Currency select: sync denomination hidden field ───────────────
    const currencySelect = document.getElementById('currencyCode');
    const denominationField = document.getElementById('denomination');
    const currenciesData = [
      <g:each in="${currencies}" var="currency" status="i">
      {cc: "${raw(currency.cc)}", symbol: "${raw(currency.symbol)}"}${i < currencies.size() - 1 ? ',' : ''}
      </g:each>
    ];

    function syncDenomination(cc) {
      const match = currenciesData.find(c => c.cc === cc);
      denominationField.value = match ? match.symbol : '';
    }

    if (currencySelect.value) syncDenomination(currencySelect.value);
    currencySelect.addEventListener('change', () => syncDenomination(currencySelect.value));

    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("addProfessionDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    if (!saveBtn) {
      console.error("Save button not found!");
      return;
    }

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      const isCurrentEl = form.querySelector('[name="isCurrent"]');
      const jobCategoryEl = form.querySelector('[name="jobCategory"]:checked');
      const salaryForEl = form.querySelector('[name="salaryFor"]:checked');
      const payload = {
        designation: form.querySelector('[name="designation"]').value.trim(),
        organisation: form.querySelector('[name="organisation"]').value.trim(),
        organisationLocation: form.querySelector('[name="organisationLocation"]').value.trim(),
        jobCategory: jobCategoryEl ? jobCategoryEl.value : '',
        currencyCode: form.querySelector('[name="currencyCode"]').value.trim(),
        denomination: denominationField.value.trim(),
        salary: form.querySelector('[name="salary"]').value.trim(),
        salaryFor: salaryForEl ? salaryForEl.value : '',
        // dotMesh renders a checkbox — read .checked for a true boolean
        isCurrent: isCurrentEl ? isCurrentEl.checked : false
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveProfessionalDetailsForEditAdd', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Profession details saved successfully!");
          if (data.redirectUrl) {
            setTimeout(() => {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 409 && data.existingProfessionalDetail) {
          const c = data.existingProfessionalDetail;
          const n = data.newProfessionalDetail;

          window.populateProfessionalStatusTable('#jobStatusTable', n, c);

          const replaceBtn = document.getElementById('confirmReplacePrimaryBtn');
          const freshReplaceBtn = replaceBtn.cloneNode(true);
          replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

          freshReplaceBtn.addEventListener('click', async function () {
            freshReplaceBtn.disabled = true;
            const origLabel = freshReplaceBtn.innerHTML;
            freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

            try {
              const replaceResponse = await fetch(
                "${createLink(controller: 'DBoard', action: 'replaceProfessionalDetailsForEditAdd')}",
                {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                  },
                  body: JSON.stringify({existingId: c.id, newProfessionalDetail: n})
                }
              );
              const replaceData = await replaceResponse.json();

              if (replaceResponse.ok && replaceData.success) {
                window.populateProfessionalStatusTable('#jobSummaryTable', replaceData.oldProfessionalDetail, replaceData.newProfessionalDetail);
                GlassModal.close('jobStatusModal');
                window.showSuccess(replaceData.message || "Current profession replaced successfully!");
                GlassModal.open('jobSummaryModal');
              } else {
                window.showError(replaceData.message || "Failed to replace profession. Please try again.");
              }
            } catch (err) {
              console.error("Replace failed:", err);
              window.showError("Network error \u2014 please check your connection.");
            } finally {
              freshReplaceBtn.disabled = false;
              freshReplaceBtn.innerHTML = origLabel;
            }
          });

          window.showWarning(data.message || "Current profession already exists.");
          GlassModal.open('jobStatusModal');

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

    // ── Populate profession status / summary tables ───────────────────
    window.populateProfessionalStatusTable = function (tableName, a, n) {
      const table = tableName + ' tbody';

      function row(i) {
        return table + ' tr:nth-child(' + i + ')';
      }

      function badge(isCurrent) {
        return isCurrent
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
      }

      document.querySelector(row(1) + ' td:nth-child(2)').textContent = a.designation || 'Not Available';
      document.querySelector(row(1) + ' td:last-child').textContent = n.designation || 'Not Available';
      document.querySelector(row(2) + ' td:nth-child(2)').textContent = a.organisation || 'Not Available';
      document.querySelector(row(2) + ' td:last-child').textContent = n.organisation || 'Not Available';
      document.querySelector(row(3) + ' td:nth-child(2)').textContent = a.organisationLocation || 'Not Available';
      document.querySelector(row(3) + ' td:last-child').textContent = n.organisationLocation || 'Not Available';
      document.querySelector(row(4) + ' td:nth-child(2)').textContent = a.jobCategory || 'Not Available';
      document.querySelector(row(4) + ' td:last-child').textContent = n.jobCategory || 'Not Available';
      document.querySelector(row(5) + ' td:nth-child(2)').textContent = a.salary || 'Not Available';
      document.querySelector(row(5) + ' td:last-child').textContent = n.salary || 'Not Available';
      document.querySelector(row(6) + ' td:nth-child(2)').textContent = a.salaryFor || 'Not Available';
      document.querySelector(row(6) + ' td:last-child').textContent = n.salaryFor || 'Not Available';
      document.querySelector(row(7) + ' td:nth-child(2)').innerHTML = badge(a.isCurrent === true || a.isCurrent === 'true');
      document.querySelector(row(7) + ' td:last-child').innerHTML = badge(n.isCurrent === true || n.isCurrent === 'true');
    };

  });
</script>
</body>
</html>
