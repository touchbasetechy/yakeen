<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:dotMeshStyles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
  <glass:glassCardStyles/>
  <glass:textInputStyles/>
  <glass:styles/>
</head>

<body>

<%-- ================================================================= --%>
<%-- ADD BUTTON placed in header via headerHtml (like contact version) --%>
<%-- ================================================================= --%>
<g:set var="addEducationBtn">
  <ui:pulseRing
    label="Add New Education"
    message="Opening..."
    controller="DBoard"
    action="addEducationDetailsForEdit">
    <i class="fas fa-add"></i>
  </ui:pulseRing>
</g:set>

<glass:modernGlassCard
  title="Edit Education Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your education details"
  headerHtml="${addEducationBtn}">

<%-- ── If education records exist, show tabs similar to contact ── --%>
  <g:if test="${educationDetails}">
    <glass:tabs id="educationDetailTabs">
      <g:each in="${educationDetails}" var="educationDetail" status="i">
        <glass:tab
          title="${'Education ' + (i + 1)}"
          active="${i == 0}">

        <%-- Each tab holds its own form with hidden fields and fields --%>
          <form id="educationDetailForm${i}" class="educationDetailForm">

            <input type="hidden" name="id" value="${educationDetail?.id}"/>
            <input type="hidden" name="op" value="${params?.op}"/>
            <input type="hidden" name="con" value="${params?.con}"/>

            <%-- Education & Institution row (similar to original but aligned like contact form) --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="name" id="name${i}" label="Education / Degree"
                  value="${educationDetail?.name}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="institution" id="institution${i}" label="Institution"
                  value="${educationDetail?.institution}" required="true"/>
              </div>
            </section>

            <%-- Institution Location (full width, consistent with contact style offset) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:alphaNumericTextField
                  name="institutionLocation" id="institutionLocation${i}"
                  label="Institution Location"
                  value="${educationDetail?.institutionLocation}" required="true"/>
              </div>
            </section>

            <%-- University & University Location --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="university" id="university${i}" label="University / Affiliated Body"
                  value="${educationDetail?.university}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="universityLocation" id="universityLocation${i}"
                  label="University Location"
                  value="${educationDetail?.universityLocation}" required="true"/>
              </div>
            </section>

            <%-- Is Highest Education (dotMesh toggle) --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:dotMesh name="isHighest" id="isHighest${i}"
                               label="Is Highest Education"
                               initialOn="${educationDetail?.isHighest}"/>
              </div>
            </section>

            <%-- Save button (right aligned like contact) --%>
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
    <glass:text>No Education Details found. Click <strong>+</strong> to add one.</glass:text>
  </g:else>

</glass:modernGlassCard>

<%-- Modals reused from original educationStatusModal and educationModal (templates) --%>
<g:render template="educationStatusModal"/>
<g:render template="educationModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Async submit for every education form (fully modeled on contact logic) ──
    document.querySelectorAll('.educationDetailForm').forEach(function (form) {

      var saveBtn = form.querySelector('button[type="submit"]');

      form.addEventListener('submit', async function (e) {
        e.preventDefault(); // stops native submission for both click AND Enter-key triggers

        // your AJAX call here
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        // ── Build JSON payload ──────────────────────────────────────
        var isHighestEl = form.querySelector('[name="isHighest"]');
        var payload = {
          id: form.querySelector('[name="id"]').value,
          op: form.querySelector('[name="op"]').value,
          con: form.querySelector('[name="con"]').value,
          name: form.querySelector('[name="name"]').value.trim(),
          institution: form.querySelector('[name="institution"]').value.trim(),
          institutionLocation: form.querySelector('[name="institutionLocation"]').value.trim(),
          university: form.querySelector('[name="university"]').value.trim(),
          universityLocation: form.querySelector('[name="universityLocation"]').value.trim(),
          isHighest: isHighestEl ? isHighestEl.checked : false
        };

        saveBtn.disabled = true;
        var originalLabel = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

        try {
          var response = await fetch(
            "${createLink(controller: 'DBoard', action: 'updateEducationDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
            window.showSuccess(data.message || "Education details updated successfully!");

          } else if (response.status === 409 && data.existingEducation) {
            // ── Conflict: a highest education already exists ─────────
            var existingEdu = data.existingEducation;   // existing highest education record
            var newEdu = data.newEducation;             // incoming (user submitted)

            // Populate the status table (educationStatusModal) with comparison
            window.populateEducationStatusTable('#educationStatusTable', newEdu, existingEdu);

            // Optional: set label for replace confirmation modal (can be used if needed)
            var eduNameSpan = document.getElementById('educationNameLabel');
            if (eduNameSpan) eduNameSpan.textContent = existingEdu.name || '\u2014';

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
                    "${createLink(controller: 'DBoard', action: 'replaceEducation', params: [slot: "${request.getAttribute('currentSlot')}"])}",
                    {
                      method: "POST",
                      headers: {
                        "Content-Type": "application/json",
                        "X-Requested-With": "XMLHttpRequest"
                      },
                      body: JSON.stringify({
                        existingId: existingEdu.id,
                        newEducation: newEdu
                      })
                    }
                  );
                  var replaceData = await replaceResponse.json();

                  if (replaceResponse.ok && replaceData.success) {
                    // Populate summary table (educationSummaryModal) showing old vs new
                    if (typeof window.populateEducationSummaryTable === 'function') {
                      window.populateEducationSummaryTable('#educationSummaryTable', replaceData.oldEducation, replaceData.newEducation);
                    } else {
                      // fallback using same table function if present
                      if (window.populateEducationStatusTable) {
                        window.populateEducationStatusTable('#educationSummaryTable', replaceData.oldEducation, replaceData.newEducation);
                      }
                    }
                    GlassModal.close('educationStatusModal');
                    window.showSuccess(replaceData.message || "Highest education replaced successfully!");
                    GlassModal.open('educationSummaryModal');
                  } else {
                    window.showError(replaceData.message || "Failed to replace highest education. Please try again.");
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

            window.showWarning(data.message || "A highest education record already exists.");
            GlassModal.open('educationStatusModal');

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

    // ── Populate education status table (comparison view inside educationStatusModal) ──
    // This mirrors contact version but with education fields.
    window.populateEducationStatusTable = function (tableSelector, existingEdu, newEdu) {
      var tableBody = document.querySelector(tableSelector + ' tbody');
      if (!tableBody) {
        console.warn("Table body not found for selector:", tableSelector);
        return;
      }

      // Helper functions
      function badgeIsHighest(value) {
        var isOn = (value === true || value === 'true');
        return isOn
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
      }

      function formatText(val) {
        return (val && val.trim()) ? val.trim() : 'Not Available';
      }

      // Define rows mapping: index 0-based row order inside tbody
      // Assuming 6 rows: Education, Institution, InstitutionLocation, University, UniversityLocation, IsHighest
      var rows = tableBody.querySelectorAll('tr');
      if (rows.length >= 6) {
        // Row 1: Education name
        var eduNameCellExisting = rows[0].querySelector('td:nth-child(2)');
        var eduNameCellNew = rows[0].querySelector('td:last-child');
        if (eduNameCellExisting) eduNameCellExisting.textContent = formatText(existingEdu.name);
        if (eduNameCellNew) eduNameCellNew.textContent = formatText(newEdu.name);

        // Row 2: Institution
        var instCellExisting = rows[1].querySelector('td:nth-child(2)');
        var instCellNew = rows[1].querySelector('td:last-child');
        if (instCellExisting) instCellExisting.textContent = formatText(existingEdu.institution);
        if (instCellNew) instCellNew.textContent = formatText(newEdu.institution);

        // Row 3: Institution Location
        var instLocCellExisting = rows[2].querySelector('td:nth-child(2)');
        var instLocCellNew = rows[2].querySelector('td:last-child');
        if (instLocCellExisting) instLocCellExisting.textContent = formatText(existingEdu.institutionLocation);
        if (instLocCellNew) instLocCellNew.textContent = formatText(newEdu.institutionLocation);

        // Row 4: University
        var uniCellExisting = rows[3].querySelector('td:nth-child(2)');
        var uniCellNew = rows[3].querySelector('td:last-child');
        if (uniCellExisting) uniCellExisting.textContent = formatText(existingEdu.university);
        if (uniCellNew) uniCellNew.textContent = formatText(newEdu.university);

        // Row 5: University Location
        var uniLocCellExisting = rows[4].querySelector('td:nth-child(2)');
        var uniLocCellNew = rows[4].querySelector('td:last-child');
        if (uniLocCellExisting) uniLocCellExisting.textContent = formatText(existingEdu.universityLocation);
        if (uniLocCellNew) uniLocCellNew.textContent = formatText(newEdu.universityLocation);

        // Row 6: Is Highest (badges)
        var isHighCellExisting = rows[5].querySelector('td:nth-child(2)');
        var isHighCellNew = rows[5].querySelector('td:last-child');
        if (isHighCellExisting) isHighCellExisting.innerHTML = badgeIsHighest(existingEdu.isHighest);
        if (isHighCellNew) isHighCellNew.innerHTML = badgeIsHighest(newEdu.isHighest);
      } else {
        // Fallback: more dynamic approach if the table structure differs
        // but we assume template is stable — still error handling
        console.warn("Table does not have expected row count. Update row mapping.");
      }
    };

    // Optional: summary table population for educationSummaryModal (similar to contact summary)
    window.populateEducationSummaryTable = function (tableSelector, oldEdu, newEdu) {
      // same logic as status table but used after replace success
      var tableBody = document.querySelector(tableSelector + ' tbody');
      if (!tableBody) return;

      var rows = tableBody.querySelectorAll('tr');
      if (rows.length >= 6) {
        rows[0].querySelector('td:nth-child(2)').textContent = oldEdu?.name || 'Not Available';
        rows[0].querySelector('td:last-child').textContent = newEdu?.name || 'Not Available';

        rows[1].querySelector('td:nth-child(2)').textContent = oldEdu?.institution || 'Not Available';
        rows[1].querySelector('td:last-child').textContent = newEdu?.institution || 'Not Available';

        rows[2].querySelector('td:nth-child(2)').textContent = oldEdu?.institutionLocation || 'Not Available';
        rows[2].querySelector('td:last-child').textContent = newEdu?.institutionLocation || 'Not Available';

        rows[3].querySelector('td:nth-child(2)').textContent = oldEdu?.university || 'Not Available';
        rows[3].querySelector('td:last-child').textContent = newEdu?.university || 'Not Available';

        rows[4].querySelector('td:nth-child(2)').textContent = oldEdu?.universityLocation || 'Not Available';
        rows[4].querySelector('td:last-child').textContent = newEdu?.universityLocation || 'Not Available';

        var badgeOld = (oldEdu?.isHighest === true || oldEdu?.isHighest === 'true')
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
        var badgeNew = (newEdu?.isHighest === true || newEdu?.isHighest === 'true')
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
        rows[5].querySelector('td:nth-child(2)').innerHTML = badgeOld;
        rows[5].querySelector('td:last-child').innerHTML = badgeNew;
      }
    };

  });
</script>

</body>
</html>
