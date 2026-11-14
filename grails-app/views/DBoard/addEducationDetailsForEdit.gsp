<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:dotMeshStyles/>
  <glass:modalAssets/>
</head>

<body>

<glass:modernGlassCard
  title="Education Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="editEducationDetails"
  titleHtml="true"
  subtitle="Tell us about your education">

  <form id="addEducationDetailsForm">

    <%-- ── Education & Institution ─────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="name" id="name" label="Education" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="institution" id="institution" label="Institution" required="true"/>
      </div>
    </section>

    <%-- ── Institution Location ───────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:alphaNumericTextField
          name="institutionLocation" id="institutionLocation"
          label="Institution Location" required="true"/>
      </div>
    </section>

    <%-- ── University ─────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="university" id="university" label="University" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="universityLocation" id="universityLocation"
          label="University Location" required="true"/>
      </div>
    </section>

    <%-- ── Is Highest Education ───────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:dotMesh name="isHighest" label="Is Highest Education"/>
      </div>
    </section>

    <div class="row row-padding">
      <div class="col-sm-12 d-flex justify-content-end">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>

  </form>

</glass:modernGlassCard>

<g:render template="educationStatusModal"/>
<g:render template="educationModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("addEducationDetailsForm");
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
      const isHighestEl = form.querySelector('[name="isHighest"]');
      const payload = {
        name: form.querySelector('[name="name"]').value.trim(),
        institution: form.querySelector('[name="institution"]').value.trim(),
        institutionLocation: form.querySelector('[name="institutionLocation"]').value.trim(),
        university: form.querySelector('[name="university"]').value.trim(),
        universityLocation: form.querySelector('[name="universityLocation"]').value.trim(),
        // dotMesh renders a checkbox — read .checked for a true boolean
        isHighest: isHighestEl ? isHighestEl.checked : false
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveEducationDetailsForEditAdd', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Education details saved successfully!");
          if (data.redirectUrl) {
            setTimeout(() => {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 409 && data.existingEducation) {
          const c = data.existingEducation;
          const n = data.newEducation;

          window.populateEducationStatusTable('#educationStatusTable', n, c);

          const replaceBtn = document.getElementById('confirmReplacePrimaryBtn');
          const freshReplaceBtn = replaceBtn.cloneNode(true);
          replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

          freshReplaceBtn.addEventListener('click', async function () {
            freshReplaceBtn.disabled = true;
            const origLabel = freshReplaceBtn.innerHTML;
            freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

            try {
              const replaceResponse = await fetch(
                "${createLink(controller: 'DBoard', action: 'replaceEducationDetailsForEditAdd')}",
                {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                  },
                  body: JSON.stringify({existingId: c.id, newEducation: n})
                }
              );
              const replaceData = await replaceResponse.json();

              if (replaceResponse.ok && replaceData.success) {
                window.populateEducationStatusTable('#educationSummaryTable', replaceData.oldEducation, replaceData.newEducation);
                GlassModal.close('educationStatusModal');
                window.showSuccess(replaceData.message || "Highest education replaced successfully!");
                GlassModal.open('educationSummaryModal');
              } else {
                window.showError(replaceData.message || "Failed to replace education. Please try again.");
              }
            } catch (err) {
              console.error("Replace failed:", err);
              window.showError("Network error \u2014 please check your connection.");
            } finally {
              freshReplaceBtn.disabled = false;
              freshReplaceBtn.innerHTML = origLabel;
            }
          });

          window.showWarning(data.message || "Highest education already exists.");
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

    // ── Populate education status / summary tables ─────────────────
    window.populateEducationStatusTable = function (tableName, a, n) {
      const table = tableName + ' tbody';

      function row(i) {
        return table + ' tr:nth-child(' + i + ')';
      }

      function badge(isHighest) {
        return isHighest
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
      }

      document.querySelector(row(1) + ' td:nth-child(2)').textContent = a.name || 'Not Available';
      document.querySelector(row(1) + ' td:last-child').textContent = n.name || 'Not Available';
      document.querySelector(row(2) + ' td:nth-child(2)').textContent = a.institution || 'Not Available';
      document.querySelector(row(2) + ' td:last-child').textContent = n.institution || 'Not Available';
      document.querySelector(row(3) + ' td:nth-child(2)').textContent = a.institutionLocation || 'Not Available';
      document.querySelector(row(3) + ' td:last-child').textContent = n.institutionLocation || 'Not Available';
      document.querySelector(row(4) + ' td:nth-child(2)').textContent = a.university || 'Not Available';
      document.querySelector(row(4) + ' td:last-child').textContent = n.university || 'Not Available';
      document.querySelector(row(5) + ' td:nth-child(2)').textContent = a.universityLocation || 'Not Available';
      document.querySelector(row(5) + ' td:last-child').textContent = n.universityLocation || 'Not Available';
      document.querySelector(row(6) + ' td:nth-child(2)').innerHTML = badge(a.isHighest === true || a.isHighest === 'true');
      document.querySelector(row(6) + ' td:last-child').innerHTML = badge(n.isHighest === true || n.isHighest === 'true');
    };

  });
</script>
</body>
</html>
