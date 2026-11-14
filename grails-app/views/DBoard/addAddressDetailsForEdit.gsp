<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
  <glass:glassCardStyles/>
  <glass:dotMeshStyles/>
  <glass:textInputStyles/>
  <glass:styles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
</head>

<body>

<glass:modernGlassCard
  title="Address Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="editAddressDetails"
  titleHtml="true"
  subtitle="Tell us where you live">

  <form id="addAddressDetailsForm">

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

<g:render template="addressStatusModal"/>
<g:render template="addressModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Other address-type reveal / hide ──────────────────────────────
    const radioButtons = document.querySelectorAll('[name="addressType"]');
    const otherTypeRow = document.getElementById('otherTypeRow');
    const otherTypeField = document.getElementById('otherType');

    radioButtons.forEach((radio) => {
      radio.addEventListener('change', function () {
        if (this.value === 'Other' && this.checked) {
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
    const form = document.getElementById('addAddressDetailsForm');
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

      const addressTypeEl = form.querySelector('[name="addressType"]:checked');
      const payload = {
        street1: form.querySelector('[name="street1"]').value.trim(),
        street2: form.querySelector('[name="street2"]').value.trim(),
        city: form.querySelector('[name="city"]').value.trim(),
        district: form.querySelector('[name="district"]').value.trim(),
        state: form.querySelector('[name="state"]').value.trim(),
        country: form.querySelector('[name="country"]').value.trim(),
        postalCode: form.querySelector('[name="postalCode"]').value.trim(),
        addressType: addressTypeEl ? addressTypeEl.value.trim() : '',
        otherType: otherTypeField ? otherTypeField.value.trim() : ''
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveAddressDetailsForEditAdd', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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

          const a = data.existingAddress; // existing address in DB
          const n = data.newAddress;      // what the user just tried to save

          window.populateAddressStatusTable('#addressStatusTable', n, a);
          document.getElementById('addressTypeLabel').textContent =
            a.addressType === 'Other' ? (a.otherType || 'Other') : (a.addressType || '');

          // ── Re-wire replace button (clone to drop old listeners) ────
          const replaceBtn = document.getElementById('confirmReplacePrimaryAddressBtn');
          const freshReplaceBtn = replaceBtn.cloneNode(true);
          replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

          freshReplaceBtn.addEventListener('click', async function () {
            freshReplaceBtn.disabled = true;
            const origLabel = freshReplaceBtn.innerHTML;
            freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

            try {
              const replaceResponse = await fetch(
                "${createLink(controller: 'DBoard', action: 'replaceAddressForEditAdd')}",
                {
                  method: "POST",
                  headers: {
                    "Content-Type": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                  },
                  body: JSON.stringify({existingId: a.id, newAddress: n})
                }
              );
              const replaceData = await replaceResponse.json();

              if (replaceResponse.ok && replaceData.success) {
                window.populateAddressStatusTable('#addressSummaryTable', replaceData.oldAddress, replaceData.newAddress);
                GlassModal.close('addressStatusModal');
                if (replaceData.newAddress.addressType) {
                  window.showSuccess(replaceData.message || replaceData.newAddress.addressType + " address replaced successfully!");
                } else if (replaceData.newAddress.otherType) {
                  window.showSuccess(replaceData.message || replaceData.newAddress.otherType + " address replaced successfully!");
                }
                GlassModal.open('addressSummaryModal');
              } else {
                window.showError(replaceData.message || "Failed to replace address. Please try again.");
              }
            } catch (err) {
              console.error("Replace failed:", err);
              window.showError("Network error \u2014 please check your connection.");
            } finally {
              freshReplaceBtn.disabled = false;
              freshReplaceBtn.innerHTML = origLabel;
            }
          });

          // ── Toast first, then open modal — once ────────────────────
          if (a.addressType === 'Other') {
            window.showWarning(data.message || "An address of type '" + (a.otherType || 'Other') + "' already exists.");
          } else {
            window.showWarning(data.message || "An address of type '" + a.addressType + "' already exists.");
          }
          GlassModal.open('addressStatusModal');

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

    // ── Populate address status/summary tables ────────────────────────
    window.populateAddressStatusTable = function (tableName, a, n) {
      const table = tableName + ' tbody';

      function row(i) {
        return table + ' tr:nth-child(' + i + ')';
      }

      function addressTypeBadge(addressType, otherType) {
        const label = (addressType === 'Other' ? otherType : addressType) || 'Not Available';
        const variantMap = {
          'Permanent': 'gt-badge-success',
          'Present': 'gt-badge-default',
          'Temporary': 'gt-badge-warning',
          'Other': 'gt-badge-info'
        };
        const variant = variantMap[addressType] || 'gt-badge-default';
        return '<span class="gt-badge ' + variant + '">'
          + '<span class="gt-status-dot"></span>'
          + label
          + '</span>';
      }

      document.querySelector(row(1) + ' td:nth-child(2)').textContent = a.street1 || 'Not Available';
      document.querySelector(row(1) + ' td:last-child').textContent = n.street1 || 'Not Available';
      document.querySelector(row(2) + ' td:nth-child(2)').textContent = a.street2 || 'Not Available';
      document.querySelector(row(2) + ' td:last-child').textContent = n.street2 || 'Not Available';
      document.querySelector(row(3) + ' td:nth-child(2)').textContent = a.city || 'Not Available';
      document.querySelector(row(3) + ' td:last-child').textContent = n.city || 'Not Available';
      document.querySelector(row(4) + ' td:nth-child(2)').textContent =
        ([a.district, a.state].filter(Boolean).join(', ')) || 'Not Available';
      document.querySelector(row(4) + ' td:last-child').textContent =
        ([n.district, n.state].filter(Boolean).join(', ')) || 'Not Available';
      document.querySelector(row(5) + ' td:nth-child(2)').textContent = a.postalCode || 'Not Available';
      document.querySelector(row(5) + ' td:last-child').textContent = n.postalCode || 'Not Available';
      document.querySelector(row(6) + ' td:nth-child(2)').textContent = a.country || 'Not Available';
      document.querySelector(row(6) + ' td:last-child').textContent = n.country || 'Not Available';
      document.querySelector(row(7) + ' td:nth-child(2)').innerHTML = addressTypeBadge(a.addressType, a.otherType);
      document.querySelector(row(7) + ' td:last-child').innerHTML = addressTypeBadge(n.addressType, n.otherType);
    };

  });
</script>
</body>
</html>
