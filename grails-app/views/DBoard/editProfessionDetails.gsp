<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>
  <glass:dotMeshStyles/>
  <glass:textInputStyles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
  <glass:styles/>
</head>

<body>

<%-- ================================================================= --%>
<%-- ADD BUTTON placed in header via headerHtml (like contact version) --%>
<%-- ================================================================= --%>
<g:set var="addProfessionBtn">
  <ui:pulseRing
    label="Add New Profession"
    message="Opening..."
    controller="DBoard"
    action="addProfessionDetailsForEdit">
    <i class="fas fa-add"></i>
  </ui:pulseRing>

</g:set>

<glass:modernGlassCard
  title="Edit Profession Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your profession details"
  headerHtml="${addProfessionBtn}">

<%-- ── If profession records exist, show tabs (like contact version) ── --%>
  <g:if test="${professions}">
    <glass:tabs id="professionDetailTabs" variant="flat">
      <g:each in="${professions}" var="professionDetail" status="i">
        <glass:tab
          title="${'Profession ' + (i + 1)}"
          active="${i == 0}">

        <%-- Each tab holds its own form --%>
          <form id="professionDetailForm${i}" class="professionDetailForm">

            <input type="hidden" name="id" value="${professionDetail?.id}"/>
            <input type="hidden" name="op" value="${params?.op}"/>
            <input type="hidden" name="con" value="${params?.con}"/>

            <%-- Designation & Organisation row --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="designation" id="designation${i}" label="Designation"
                  value="${professionDetail?.designation}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="organisation" id="organisation${i}" label="Organisation"
                  value="${professionDetail?.organisation}" required="true"/>
              </div>
            </section>

            <%-- Organisation Location (full width) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:alphaNumericTextField
                  name="organisationLocation" id="organisationLocation${i}"
                  label="Location"
                  value="${professionDetail?.organisationLocation}" required="true"/>
              </div>
            </section>

            <%-- Job Category (radio group) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <label class="form-label d-block">Job Category</label>

                <div class="d-flex flex-wrap gap-3">
                  <glass:radio name="jobCategory_${i}" id="govt_psu_${i}" value="Government / PSU"
                               title="Government / PSU"
                               checked="${professionDetail?.jobCategory == 'Government / PSU'}"/>
                  <glass:radio name="jobCategory_${i}" id="private_sector_${i}" value="Private Sector"
                               title="Private Sector"
                               checked="${professionDetail?.jobCategory == 'Private Sector'}"/>
                  <glass:radio name="jobCategory_${i}" id="business_${i}" value="Business"
                               title="Business"
                               checked="${professionDetail?.jobCategory == 'Business'}"/>
                  <glass:radio name="jobCategory_${i}" id="defence_${i}" value="Defence"
                               title="Defence"
                               checked="${professionDetail?.jobCategory == 'Defence'}"/>
                  <glass:radio name="jobCategory_${i}" id="self_employed_${i}" value="Self Employed"
                               title="Self Employed"
                               checked="${professionDetail?.jobCategory == 'Self Employed'}"/>
                  <glass:radio name="jobCategory_${i}" id="un_employed_${i}" value="Un Employed"
                               title="Un Employed"
                               checked="${professionDetail?.jobCategory == 'Un Employed'}"/>
                </div>
              </div>
            </section>

            <%-- Currency & Salary row --%>
            <section class="row row-padding">
              <div class="col-sm-4">
                <glass:select
                  name="currencyCode" id="currencyCode${i}" label="Currency"
                  placeholder="Select a currency…"
                  from="${currencies}"
                  optionKey="cc" optionValue="name"
                  noSelection="${['': '-- Select --']}"
                  value="${professionDetail?.currencyCode}"
                  multiple="false" required="true" disabled="false"/>
                <input type="hidden" id="denomination${i}" name="denomination"
                       value="${professionDetail?.denomination}"/>
              </div>

              <div class="col-sm-8">
                <glass:formattedNumericField
                  name="salary" id="salary${i}" label="Salary"
                  value="${professionDetail?.salary}"
                  maxlength="15" required="true"/>
              </div>
            </section>

            <%-- Salary Period (radio group) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <label class="form-label d-block">Salary Period</label>

                <div class="d-flex flex-wrap gap-3">
                  <glass:radio name="salaryFor_${i}" id="perAnum_${i}" value="Per Anum"
                               title="Per Anum"
                               checked="${professionDetail?.salaryFor == 'Per Anum'}"/>
                  <glass:radio name="salaryFor_${i}" id="perMonth_${i}" value="Per Month"
                               title="Per Month"
                               checked="${professionDetail?.salaryFor == 'Per Month'}"/>
                  <glass:radio name="salaryFor_${i}" id="perWeek_${i}" value="Per Week"
                               title="Per Week"
                               checked="${professionDetail?.salaryFor == 'Per Week'}"/>
                  <glass:radio name="salaryFor_${i}" id="perDay_${i}" value="Per Day" title="Per Day"
                               checked="${professionDetail?.salaryFor == 'Per Day'}"/>
                  <glass:radio name="salaryFor_${i}" id="perHour_${i}" value="Per Hour"
                               title="Per Hour"
                               checked="${professionDetail?.salaryFor == 'Per Hour'}"/>
                </div>
              </div>
            </section>

            <%-- Is Current Job (dotMesh toggle) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:dotMesh name="isCurrent" id="isCurrent${i}"
                               label="Is Current Job"
                               initialOn="${professionDetail?.isCurrent}"/>
              </div>
            </section>

            <%-- Save button (right aligned) --%>
            <div class="row row-padding">
              <div class="col-sm-12 d-flex justify-content-end">
                <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
              </div>
            </div>

          </form>
        </glass:tab>
      </g:each>
    </glass:tabs>
  </g:if>
  <g:else>
    <glass:text>No Profession Details found. Click <strong>&nbsp;+&nbsp;</strong> to add one</glass:text>
  </g:else>

</glass:modernGlassCard>

<%-- Modals for conflict handling --%>
<g:render template="professionStatusModal"/>
<g:render template="professionModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Currency data for denomination sync ──────────────────────────
    const currenciesData = [
      <g:each in="${currencies}" var="currency" status="i">
      {cc: "${raw(currency.cc)}", symbol: "${raw(currency.symbol)}"}${i < currencies.size() - 1 ? ',' : ''}
      </g:each>
    ];

    // ── Process each profession form (similar to contact wiring) ─────
    document.querySelectorAll('.professionDetailForm').forEach(function (form) {

      // ── Sync denomination hidden field when currency changes ───────
      const currencySelect = form.querySelector('[name="currencyCode"]');
      const denominationField = form.querySelector('[name="denomination"]');

      function syncDenomination(cc) {
        const match = currenciesData.find(c => c.cc === cc);
        if (denominationField) denominationField.value = match ? match.symbol : '';
      }

      if (currencySelect) {
        if (currencySelect.value) syncDenomination(currencySelect.value);
        currencySelect.addEventListener('change', function () {
          syncDenomination(currencySelect.value);
        });
      }

      // ── Async form submission (modeled after contact version) ──────
      var saveBtn = form.querySelector('button[type="submit"]');

      form.addEventListener('submit', async function (e) {
        e.preventDefault(); // stops native submission for both click AND Enter-key triggers

        // your AJAX call here
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        // ── Build JSON payload ──────────────────────────────────────
        var payload = {
          id: form.querySelector('[name="id"]').value,
          op: form.querySelector('[name="op"]').value,
          con: form.querySelector('[name="con"]').value,
          designation: form.querySelector('[name="designation"]').value.trim(),
          organisation: form.querySelector('[name="organisation"]').value.trim(),
          organisationLocation: form.querySelector('[name="organisationLocation"]').value.trim(),
          jobCategory: (form.querySelector('[name^="jobCategory_"]:checked') || {}).value || '',
          currencyCode: form.querySelector('[name="currencyCode"]').value,
          denomination: form.querySelector('[name="denomination"]').value,
          salary: form.querySelector('[name="salary"]').value.trim(),
          salaryFor: (form.querySelector('[name^="salaryFor_"]:checked') || {}).value || '',
          isCurrent: form.querySelector('[name="isCurrent"]').checked
        };

        saveBtn.disabled = true;
        var originalLabel = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

        try {
          var response = await fetch(
            "${createLink(controller: 'DBoard', action: 'updateProfessionDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
            window.showSuccess(data.message || "Profession details updated successfully!");

          } else if (response.status === 409 && data.existingProfession) {
            // ── Conflict: a current job already exists ───────────────
            var existingProf = data.existingProfession;
            var newProf = data.newProfession;

            // Populate the status table for comparison
            window.populateProfessionStatusTable('#jobStatusTable', existingProf, newProf);

            // ── Wire up Replace button (clone to avoid duplicate listeners) ─
            var replaceBtn = document.getElementById('confirmReplacePrimaryBtn');
            if (replaceBtn) {
              var freshReplaceBtn = replaceBtn.cloneNode(true);
              replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

              freshReplaceBtn.addEventListener('click', async function () {
                freshReplaceBtn.disabled = true;
                var origReplaceLabel = freshReplaceBtn.innerHTML;
                freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

                try {
                  var replaceResponse = await fetch(
                    "${createLink(controller: 'DBoard', action: 'replaceProfession', params: [slot: "${request.getAttribute('currentSlot')}"])}",
                    {
                      method: "POST",
                      headers: {
                        "Content-Type": "application/json",
                        "X-Requested-With": "XMLHttpRequest"
                      },
                      body: JSON.stringify({
                        existingId: existingProf.id,
                        newCurrentJob: newProf
                      })
                    }
                  );
                  var replaceData = await replaceResponse.json();

                  if (replaceResponse.ok && replaceData.success) {
                    // Populate summary table after successful replacement
                    window.populateProfessionStatusTable('#jobSummaryTable', replaceData.newProfession, replaceData.oldProfession);
                    GlassModal.close('jobStatusModal');
                    window.showSuccess(replaceData.message || "Current job replaced successfully!");
                    GlassModal.open('jobSummaryModal');
                  } else {
                    window.showError(replaceData.message || "Failed to replace job. Please try again.");
                  }
                } catch (err) {
                  console.error("Replace failed:", err);
                  window.showError("Network error \u2014 please check your connection.");
                } finally {
                  freshReplaceBtn.disabled = false;
                  freshReplaceBtn.innerHTML = origReplaceLabel;
                }
              });
            }

            window.showWarning(data.message || "A current job already exists.");
            GlassModal.open('jobStatusModal');

          } else if (response.status === 404) {
            window.showWarning(data.message || "Profession details not found.");
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

    // ── Populate profession status / summary tables ──────────────────
    // This function populates a comparison table (existing vs new)
    window.populateProfessionStatusTable = function (tableSelector, existingProf, newProf) {
      var tableBody = document.querySelector(tableSelector + ' tbody');
      if (!tableBody) {
        console.warn("Table body not found for selector:", tableSelector);
        return;
      }

      function badgeIsCurrent(value) {
        var isOn = (value === true || value === 'true');
        return isOn
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
      }

      function formatText(val) {
        return (val && val.trim()) ? val.trim() : 'Not Available';
      }

      function formatSalary(prof) {
        if (!prof) return 'Not Available';
        var currencyCode = prof.currencyCode || '';
        var denomination = prof.denomination || '';
        var salary = prof.salary || '';
        var salaryFor = prof.salaryFor || '';
        var parts = [];
        if (currencyCode) parts.push(currencyCode);
        if (denomination) parts.push('[' + denomination + ']');
        if (salary) parts.push(salary);
        if (salaryFor) parts.push('/- ' + salaryFor);
        return parts.length ? parts.join(' ') : 'Not Available';
      }

      var rows = tableBody.querySelectorAll('tr');
      if (rows.length >= 6) {
        // Row 1: Designation
        var desigCellExisting = rows[0].querySelector('td:nth-child(2)');
        var desigCellNew = rows[0].querySelector('td:last-child');
        if (desigCellExisting) desigCellExisting.textContent = formatText(existingProf.designation);
        if (desigCellNew) desigCellNew.textContent = formatText(newProf.designation);

        // Row 2: Organisation
        var orgCellExisting = rows[1].querySelector('td:nth-child(2)');
        var orgCellNew = rows[1].querySelector('td:last-child');
        if (orgCellExisting) orgCellExisting.textContent = formatText(existingProf.organisation);
        if (orgCellNew) orgCellNew.textContent = formatText(newProf.organisation);

        // Row 3: Organisation Location
        var locCellExisting = rows[2].querySelector('td:nth-child(2)');
        var locCellNew = rows[2].querySelector('td:last-child');
        if (locCellExisting) locCellExisting.textContent = formatText(existingProf.organisationLocation);
        if (locCellNew) locCellNew.textContent = formatText(newProf.organisationLocation);

        // Row 4: Job Category
        var catCellExisting = rows[3].querySelector('td:nth-child(2)');
        var catCellNew = rows[3].querySelector('td:last-child');
        if (catCellExisting) catCellExisting.textContent = formatText(existingProf.jobCategory);
        if (catCellNew) catCellNew.textContent = formatText(newProf.jobCategory);

        // Row 5: Salary
        var salaryCellExisting = rows[4].querySelector('td:nth-child(2)');
        var salaryCellNew = rows[4].querySelector('td:last-child');
        if (salaryCellExisting) salaryCellExisting.textContent = formatSalary(existingProf);
        if (salaryCellNew) salaryCellNew.textContent = formatSalary(newProf);

        // Row 6: Is Current Job
        var currentCellExisting = rows[5].querySelector('td:nth-child(2)');
        var currentCellNew = rows[5].querySelector('td:last-child');
        if (currentCellExisting) currentCellExisting.innerHTML = badgeIsCurrent(existingProf.isCurrent);
        if (currentCellNew) currentCellNew.innerHTML = badgeIsCurrent(newProf.isCurrent);
      } else {
        console.warn("Table does not have expected row count. Update row mapping.");
      }
    };

  });
</script>

</body>
</html>
