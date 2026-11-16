<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>
  <glass:dotMeshStyles/>
  <glass:textInputStyles/>
  <glass:styles/>
  <glass:modalAssets/>
  <glass:mtableAssets/>
</head>

<body>
<g:set var="addAddressBtn">
  <ui:pulseRing
    label="Add New Address"
    message="Opening..."
    controller="DBoard"
    action="addAddressDetailsForEdit">
    <i class="fas fa-add"></i>
  </ui:pulseRing>
</g:set>

<glass:modernGlassCard
  title="Edit Address Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your address details"
  headerHtml="${addAddressBtn}">

  <g:if test="${addresses}">
    <glass:tabs id="addressTabs">
      <g:each in="${addresses}" var="address" status="i">
        <glass:tab
          title="${'Address ' + (i + 1)}"
          active="${i == 0}">

          <form id="addressDetailForm${i}" class="addressDetailForm">

            <input type="hidden" name="id" value="${address?.id}"/>
            <input type="hidden" name="op" value="${params?.op}"/>
            <input type="hidden" name="con" value="${params?.con}"/>

            <%-- Street 1 & Street 2 --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="street1" id="street1_${i}" label="Street 1"
                  value="${address?.street1}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="street2" id="street2_${i}" label="Street 2"
                  value="${address?.street2}" required="true"/>
              </div>
            </section>

            <%-- City & District --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="city" id="city_${i}" label="City"
                  value="${address?.city}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="district" id="district_${i}" label="District"
                  value="${address?.district}" required="true"/>
              </div>
            </section>

            <%-- State --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:alphaNumericTextField
                  name="state" id="state_${i}" label="State"
                  value="${address?.state}" required="true"/>
              </div>
            </section>

            <%-- Country & Postal Code --%>
            <section class="row row-padding">
              <div class="col-sm-6">
                <glass:alphaNumericTextField
                  name="country" id="country_${i}" label="Country"
                  value="${address?.country}" required="true"/>
              </div>

              <div class="col-sm-6">
                <glass:formattedNumericField
                  name="postalCode" id="postalCode_${i}" label="Postal Code"
                  value="${address?.postalCode}"
                  maxlength="15" required="true"/>
              </div>
            </section>

            <%-- Address Type (radio) --%>
            <section class="row row-padding">
              <glass:radio name="addressType" id="permanent_${i}" value="Permanent" title="Permanent"
                           checked="${address?.addressType == 'Permanent'}"/>
              <glass:radio name="addressType" id="present_${i}" value="Present" title="Present"
                           checked="${address?.addressType == 'Present'}"/>
              <glass:radio name="addressType" id="temporary_${i}" value="Temporary" title="Temporary"
                           checked="${address?.addressType == 'Temporary'}"/>
              <glass:radio name="addressType" id="other_${i}" value="Other" title="Other"
                           checked="${address?.addressType == 'Other'}"/>
            </section>

            <%-- Other Address Type (conditional) --%>
            <section class="row row-padding otherTypeRow_${i} ${address?.addressType == 'Other' ? '' : 'd-none'}">
              <div class="col-sm-12">
                <glass:alphaNumericTextField
                  name="otherType" id="otherType_${i}" label="Address Type"
                  value="${address?.otherType}"/>
              </div>
            </section>

            <%-- Save --%>
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
    <glass:text>No Address Details found. Click <strong>&nbsp;+&nbsp;</strong> to add one</glass:text>
  </g:else>
</glass:modernGlassCard>

<g:render template="addressStatusModal"/>
<g:render template="addressModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Per-tab form wiring ────────────────────────────────────────
    document.querySelectorAll('.addressDetailForm').forEach(function (form, idx) {

      // ── "Other" address-type reveal / hide ────────────────────
      var radioButtons = form.querySelectorAll('[name="addressType"]');
      var otherRow = document.querySelector('.otherTypeRow_' + idx);
      var otherInput = otherRow ? otherRow.querySelector('[name="otherType"]') : null;

      radioButtons.forEach(function (radio) {
        radio.addEventListener('change', function () {
          if (!otherRow) return;
          if (this.value === 'Other' && this.checked) {
            otherRow.classList.remove('d-none');
            otherRow.classList.add('animate__animated', 'animate__fadeInDown');
            if (otherInput) otherInput.focus();
          } else {
            otherRow.classList.add('d-none');
            otherRow.classList.remove('animate__animated', 'animate__fadeInDown');
            if (otherInput) otherInput.value = '';
          }
        });
      });

      // ── Async form submission ─────────────────────────────────
      var saveBtn = form.querySelector('button[type="submit"]');

      form.addEventListener('submit', async function (e) {
        e.preventDefault(); // stops native submission for both click AND Enter-key triggers

        // your AJAX call here
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        var addressTypeEl = form.querySelector('[name="addressType"]:checked');
        var payload = {
          id: form.querySelector('[name="id"]').value,
          op: form.querySelector('[name="op"]').value,
          con: form.querySelector('[name="con"]').value,
          street1: form.querySelector('[name="street1"]').value.trim(),
          street2: form.querySelector('[name="street2"]').value.trim(),
          city: form.querySelector('[name="city"]').value.trim(),
          district: form.querySelector('[name="district"]').value.trim(),
          state: form.querySelector('[name="state"]').value.trim(),
          country: form.querySelector('[name="country"]').value.trim(),
          postalCode: form.querySelector('[name="postalCode"]').value.trim(),
          addressType: addressTypeEl ? addressTypeEl.value : '',
          otherType: form.querySelector('[name="otherType"]')
            ? form.querySelector('[name="otherType"]').value.trim()
            : ''
        };

        saveBtn.disabled = true;
        var originalLabel = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

        try {
          var response = await fetch(
            "${createLink(controller: 'DBoard', action: 'updateAddressDetails')}",
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
            window.showSuccess(data.message || "Address details updated successfully!");

          } else if (response.status === 409 && data.address) {
            var a = data.address;
            var n = data.newAddress;

            window.populateAddressStatusTable('#addressStatusTable', n, a);
            document.getElementById('addressTypeLabel').textContent = a.addressType;

            var replaceBtn = document.getElementById('confirmReplacePrimaryAddressBtn');
            var freshReplaceBtn = replaceBtn.cloneNode(true);
            replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

            freshReplaceBtn.addEventListener('click', async function () {
              freshReplaceBtn.disabled = true;
              var origLabel = freshReplaceBtn.innerHTML;
              freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

              try {
                var replaceResponse = await fetch(
                  "${createLink(controller: 'DBoard', action: 'replaceAddress', params: [slot: "${request.getAttribute('currentSlot')}"])}",
                  {
                    method: "POST",
                    headers: {
                      "Content-Type": "application/json",
                      "X-Requested-With": "XMLHttpRequest"
                    },
                    body: JSON.stringify({existingId: a.id, newAddress: n})
                  }
                );
                var replaceData = await replaceResponse.json();

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

            if (a.addressType) {
              window.showWarning(data.message || "A " + a.addressType + " address already exists.");
            } else if (a.otherType) {
              window.showWarning(data.message || "A " + a.otherType + " address already exists.");
            }
            GlassModal.open('addressStatusModal');
          } else if (response.status === 404) {
            window.showWarning(data.message || "Address details not found.");
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

    // ── Populate address status/summary tables ─────────────────────
    window.populateAddressStatusTable = function (tableName, a, n) {
      var table = tableName + ' tbody';

      function row(i) {
        return table + ' tr:nth-child(' + i + ')';
      }

      function addressTypeBadge(addressType, otherType) {
        var label = (addressType === 'Other' ? otherType : addressType) || 'Not Available';
        var variantMap = {
          'Permanent': 'gt-badge-success',
          'Present': 'gt-badge-default',
          'Temporary': 'gt-badge-warning',
          'Other': 'gt-badge-info'
        };
        var variant = variantMap[addressType] || 'gt-badge-default';
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
