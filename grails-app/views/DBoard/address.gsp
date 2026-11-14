<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:dotMeshStyles/>
  <glass:styles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
</head>

<body>

<glass:modernGlassCard
  title="Address Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Tell us where you live">

  <form id="addressDetailsForm">

    <%-- ── Street ───────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="street1" id="street1" label="Street 1" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="street2" id="street2" label="Street 2" required="true"/>
      </div>
    </section>

    <%-- ── City & District ────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="city" id="city" label="City" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="district" id="district" label="District" required="true"/>
      </div>
    </section>

    <%-- ── State ──────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-12">
        <glass:alphaNumericTextField
          name="state" id="state" label="State" required="true"/>
      </div>
    </section>

    <%-- ── Country & Postal Code ───────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="col-sm-6">
        <glass:alphaNumericTextField
          name="country" id="country" label="Country" required="true"/>
      </div>

      <div class="col-sm-6">
        <glass:positiveNumericField
          name="postalCode" id="postalCode" label="Postal Code"
          maxlength="15" required="true"/>
      </div>
    </section>

    <%-- ── Address Type ────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <glass:radio name="addressType" id="permanent" value="Permanent" title="Permanent"/>
      <glass:radio name="addressType" id="present" value="Present" title="Present"/>
      <glass:radio name="addressType" id="temporary" value="Temporary" title="Temporary"/>
      <glass:radio name="addressType" id="other" value="Other" title="Other"/>
    </section>

    <%-- ── Other Address Type (revealed when Other is selected) ──── --%>
    <section class="row row-padding d-none" id="otherTypeRow">
      <div class="col-sm-12">
        <glass:alphaNumericTextField
          name="otherType" id="otherType" label="Address Type"/>
      </div>
    </section>

    <div class="row row-padding">
      <div class="col-sm-12 d-flex justify-content-end">
        <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
      </div>
    </div>

  </form>

</glass:modernGlassCard>

<g:render template="addressModalForAdd"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Other address type field reveal ───────────────────────────────
    const otherRadio = document.getElementById('other');
    const otherTypeRow = document.getElementById('otherTypeRow');
    const otherTypeField = document.getElementById('otherType');

    document.querySelectorAll('input[name="addressType"]').forEach(function (radio) {
      radio.addEventListener('change', function () {
        if (otherRadio.checked) {
          otherTypeRow.classList.remove('d-none');
          otherTypeRow.classList.add('animate__animated', 'animate__fadeInDown');
          otherTypeField.focus();
        } else {
          otherTypeRow.classList.add('d-none');
          otherTypeRow.classList.remove('animate__animated', 'animate__fadeInDown');
          otherTypeField.value = '';
        }
      });
    });

    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("addressDetailsForm");
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
      const addressTypeEl = form.querySelector('[name="addressType"]:checked');
      const payload = {
        street1: form.querySelector('[name="street1"]').value.trim(),
        street2: form.querySelector('[name="street2"]').value.trim(),
        city: form.querySelector('[name="city"]').value.trim(),
        district: form.querySelector('[name="district"]').value.trim(),
        state: form.querySelector('[name="state"]').value.trim(),
        country: form.querySelector('[name="country"]').value.trim(),
        postalCode: form.querySelector('[name="postalCode"]').value.trim(),
        addressType: addressTypeEl ? addressTypeEl.value : '',
        otherType: form.querySelector('[name="otherType"]').value.trim()
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveAddress', params: [id: params?.id, ac: params?.ac, slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Address details saved successfully!");
          if (data.redirectUrl) {
            setTimeout(() => {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

        } else if (response.status === 409 && data.existingAddress) {
          const e = data.existingAddress;
          const n = data.newAddress;

          window.showWarning(data.message || "An address record already exists.");
          GlassModal.open('addAddressModal');

          window.populateAddressStatusTable('#addAddressTable', e, n);

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

    // ── Populate address status / summary table ───────────────────
    window.populateAddressStatusTable = function (tableName, e, n) {
      const table = tableName + ' tbody';

      function set(rowIndex, existingVal, newVal) {
        const existing = document.querySelector(table + ' tr:nth-child(' + rowIndex + ') td:nth-child(2)');
        const updated = document.querySelector(table + ' tr:nth-child(' + rowIndex + ') td:last-child');
        if (existing) existing.textContent = existingVal || 'Not Available';
        if (updated) updated.textContent = newVal || 'Not Available';
      }

      set(1, e.street1, n.street1);
      set(2, e.street2, n.street2);
      set(3, e.city, n.city);
      set(4, e.district, n.district);
      set(5, e.state, n.state);
      set(6, e.country, n.country);
      set(7, e.postalCode, n.postalCode);
      set(8, e.addressType === 'Other' ? e.otherType : e.addressType,
        n.addressType === 'Other' ? n.otherType : n.addressType);
    };

  });
</script>
</body>
</html>
