<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:dotMeshStyles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
</head>

<body>

<glass:modernGlassCard
  title="Contact Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="editContactDetails"
  titleHtml="true"
  subtitle="How can people reach you?">

  <form id="contactDetailsForm">

    <%-- ── Country Code + Mobile ──────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-4">
        <div class="form-group">
          <sheet:countryPicker
            id="phoneCountry"
            countries="${countries}"
            isdCodeName="isdCode"
            countryName="country"
            flagName="flag"
            selectedIsdCode="+91"
            selectedCountry="India"
            selectedFlag="in"
            buttonText="Select Country Code"
            panelTitle="Select Country Code"
            style="max-width:360px"/>
        </div>
      </div>

      <div class="col-sm-8">
        <glass:positiveNumericField
          name="mobileNumber" id="mobileNumber" label="Mobile Number"
          maxlength="15" required="true"/>
      </div>
    </section>

    <%-- ── Email ──────────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:emailField
          name="eMail" id="eMail" label="Email Address"
          type="email" required="true"/>
      </div>
    </section>

    <%-- ── Is Primary ───────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:dotMesh name="isPrimary" label="Is Primary"/>
      </div>
    </section>

    <div class="row row-padding">
      <div class="col-sm-12 d-flex justify-content-end">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>
  </form>

</glass:modernGlassCard>

<g:render template="contactModalForAdd"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("contactDetailsForm");
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
      const isPrimaryEl = form.querySelector('[name="isPrimary"]');
      const payload = {
        isdCode: form.querySelector('input[name="isdCode"]').value.trim(),
        country: form.querySelector('input[name="country"]').value.trim(),
        flag: form.querySelector('input[name="flag"]').value.trim(),
        mobileNumber: form.querySelector('[name="mobileNumber"]').value.trim(),
        eMail: form.querySelector('[name="eMail"]').value.trim(),
        // dotMesh renders a checkbox — read .checked for a true boolean
        isPrimary: isPrimaryEl ? isPrimaryEl.checked : false
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveContactDetailsForEditAdd', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Contact details saved successfully!");
          if (data.redirectUrl) {
            setTimeout(() => {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 409 && data.existingContact) {
          const c = data.existingContact;
          const n = data.newContact;

          window.showWarning(data.message || "A primary contact already exists.");
          GlassModal.open('addContactModal');

          window.populateContactStatusTable('#addContactTable', n, c);

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

    // ── Populate contact status / summary tables ───────────────────
    window.populateContactStatusTable = function (tableName, c, n) {
      const table = tableName + ' tbody';

      function row(i) {
        return table + ' tr:nth-child(' + i + ')';
      }

      function badge(isPrimary) {
        return isPrimary
          ? '<span class="gt-badge gt-badge-success"><span class="gt-status-dot"></span>Yes</span>'
          : '<span class="gt-badge gt-badge-danger"><span class="gt-status-dot"></span>No</span>';
      }

      function mailLink(email) {
        return email
          ? '<a href="mailto:' + email + '">' + email + '</a>'
          : 'Not Available';
      }

      document.querySelector(row(1) + ' td:nth-child(2)').textContent = c.country || 'Not Available';
      document.querySelector(row(1) + ' td:last-child').textContent = n.country || 'Not Available';

      document.querySelector(row(2) + ' td:nth-child(2)').textContent =
        ((c.isdCode || '') + ' ' + (c.mobileNumber || '')).trim() || 'Not Available';
      document.querySelector(row(2) + ' td:last-child').textContent =
        ((n.isdCode || '') + ' ' + (n.mobileNumber || '')).trim() || 'Not Available';

      document.querySelector(row(3) + ' td:nth-child(2)').innerHTML = mailLink(c.eMail);
      document.querySelector(row(3) + ' td:last-child').innerHTML = mailLink(n.eMail);

      document.querySelector(row(4) + ' td:nth-child(2)').innerHTML =
        badge(c.isPrimary === true || c.isPrimary === 'true');
      document.querySelector(row(4) + ' td:last-child').innerHTML =
        badge(n.isPrimary === true || n.isPrimary === 'true');
    };

  });
</script>
</body>
</html>
