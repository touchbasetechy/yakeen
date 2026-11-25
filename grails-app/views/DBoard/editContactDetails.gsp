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
<g:set var="addContactBtn">
  <ui:pulseRing
    label="Add New Contact"
    message="Opening..."
    controller="DBoard"
    action="addContactDetailsForEdit">
    <i class="fas fa-add"></i>
  </ui:pulseRing>
</g:set>

<glass:modernGlassCard
  title="Edit Contact Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your contact details"
  headerHtml="${addContactBtn}">

  <g:if test="${contactDetails}">
    <glass:tabs id="contactTabs">
      <g:each in="${contactDetails}" var="contactDetail" status="i">
        <glass:tab
          title="${'Contact ' + (i + 1)}"
          active="${i == 0}">

          <g:set var="i" value="0"/>
          <form id="contactDetailForm${i}" class="contactDetailForm">
            <input type="hidden" name="id" value="${contactDetail?.id}"/>
            <input type="hidden" name="op" value="${params?.op}"/>
            <input type="hidden" name="con" value="${params?.con}"/>

            <%-- Country picker + Mobile --%>
            <section class="row row-padding align-items-start">
              <div class="col-sm-4 d-flex justify-content-center align-items-start pt-2">
                <sheet:countryPicker
                  id="phone${i}"
                  countries="${countries}"
                  isdCodeName="isdCode"
                  countryName="country"
                  flagName="flag"
                  selectedIsdCode="${contactDetail?.isdCode}"
                  selectedCountry="${contactDetail?.country}"
                  selectedFlag="${contactDetail?.flag}"
                  buttonText="Select Country Code"
                  panelTitle="Select Country Code"
                  style="max-width:360px"/>
              </div>

              <div class="col-sm-8">
                <glass:mobileInput name="mobileNumber"
                                   id="mobileNumber_${i}"
                                   countries="${countries}"
                                   isdCodeName="isdCode"
                                   countryName="country"
                                   flagName="flag"
                                   selectedIsdCode="${contactDetail?.isdCode}"
                                   selectedCountry="${contactDetail?.country}"
                                   selectedFlag="${contactDetail?.flag}"
                                   value="${contactDetail?.mobileNumber}"
                                   label="Mobile Number"
                                   required="true"
                                   fullName="mobileE164"/>

              </div>

            </section>

            <%-- Email --%>
            <section class="row row-padding">
              <div class="col-sm-8 offset-sm-4">
                <glass:emailField
                  name="eMail" id="eMail_${i}" label="Email Id"
                  value="${contactDetail?.eMail}"
                  required="true"/>
              </div>
            </section>

            <%-- Is Primary --%>
            <section class="row row-padding">
              <div class="col-sm-12">
                <glass:dotMesh name="isPrimary" id="isPrimary_${i}"
                               label="Is Primary"
                               initialOn="${contactDetail?.isPrimary}"/>
              </div>
            </section>

            <%-- Save --%>
            <%-- Full-width Save button on phones, right-aligned from sm up. --%>
            <div class="d-flex justify-content-center justify-content-sm-end mt-2">
              <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
            </div>

          </form>
        </glass:tab>
      </g:each>
    </glass:tabs>
  </g:if>
  <g:else>
    <glass:text>No Contact Details found. Click <strong>&nbsp;+&nbsp;</strong> to add one</glass:text>
  </g:else>
</glass:modernGlassCard>

<g:render template="contactStatusModal"/>
<g:render template="contactModal"/>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Per-tab form wiring ────────────────────────────────────────
    document.querySelectorAll('.contactDetailForm').forEach(function (form) {

      var saveBtn = form.querySelector('button[type="submit"]');

      form.addEventListener('submit', async function (e) {
        e.preventDefault(); // stops native submission for both click AND Enter-key triggers

        // your AJAX call here
        if (!form.checkValidity()) {
          form.reportValidity();
          return;
        }

        var payload = {
          id: form.querySelector('[name="id"]').value,
          op: form.querySelector('[name="op"]').value,
          con: form.querySelector('[name="con"]').value,
          isdCode: form.querySelector('[name="isdCode"]').value.trim(),
          country: form.querySelector('[name="country"]').value.trim(),
          flag: form.querySelector('[name="flag"]').value.trim(),
          mobileNumber: form.querySelector('[name="mobileNumber"]').value.trim(),
          eMail: form.querySelector('[name="eMail"]').value.trim(),
          isPrimary: form.querySelector('[name="isPrimary"]').checked
        };

        saveBtn.disabled = true;
        var originalLabel = saveBtn.innerHTML;
        saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

        try {
          var response = await fetch(
            "${createLink(controller: 'DBoard', action: 'updateContactDetails', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
            window.showSuccess(data.message || "Contact details updated successfully!");

          } else if (response.status === 409 && data.contact) {
            var c = data.contact;
            var n = data.newContact;

            window.populateContactStatusTable('#contactStatusTable', n, c);

            var replaceBtn = document.getElementById('confirmReplacePrimaryBtn');
            var freshReplaceBtn = replaceBtn.cloneNode(true);
            replaceBtn.parentNode.replaceChild(freshReplaceBtn, replaceBtn);

            freshReplaceBtn.addEventListener('click', async function () {
              freshReplaceBtn.disabled = true;
              var origLabel = freshReplaceBtn.innerHTML;
              freshReplaceBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Replacing\u2026';

              try {
                var replaceResponse = await fetch(
                  "${createLink(controller: 'DBoard', action: 'replaceContact', params: [slot: "${request.getAttribute('currentSlot')}"])}",
                  {
                    method: "POST",
                    headers: {
                      "Content-Type": "application/json",
                      "X-Requested-With": "XMLHttpRequest"
                    },
                    body: JSON.stringify({existingId: c.id, newContact: n})
                  }
                );
                var replaceData = await replaceResponse.json();

                if (replaceResponse.ok && replaceData.success) {
                  window.populateContactStatusTable('#contactSummaryTable', replaceData.oldContact, replaceData.newContact);
                  GlassModal.close('contactStatusModal');
                  window.showSuccess(replaceData.message || "Primary contact replaced successfully!");
                  GlassModal.open('contactSummaryModal');
                } else {
                  window.showError(replaceData.message || "Failed to replace contact. Please try again.");
                }
              } catch (err) {
                console.error("Replace failed:", err);
                window.showError("Network error \u2014 please check your connection.");
              } finally {
                freshReplaceBtn.disabled = false;
                freshReplaceBtn.innerHTML = origLabel;
              }
            });

            window.showWarning(data.message || "A primary contact already exists.");
            GlassModal.open('contactStatusModal');

          } else if (response.status === 404) {
            window.showWarning(data.message || "Contact details not found.");
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

    // ── Populate contact status / summary tables ───────────────────
    window.populateContactStatusTable = function (tableName, c, n) {
      var table = tableName + ' tbody';

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
