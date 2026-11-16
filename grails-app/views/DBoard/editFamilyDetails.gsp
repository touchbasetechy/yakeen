<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardEdit"/>
  <glass:glassCardStyles/>
  <glass:styles/>
  <glass:textInputStyles/>
  <glass:slideAnimationStyles/>
  <asset:stylesheet src="gs-glass-border-hover.css"/>
  <%--
    Responsive tweaks live here rather than scattered inline styles, so the
    breakpoints are easy to find/adjust in one place.
    (.gs-readonly-bleed and the .glass-readonly-block styling live in the CSS file.)
  --%>
  <style>
  @media (max-width: 575.98px) {
    .row-padding {
      padding-left: 8px;
      padding-right: 8px;
    }
  }
  </style>
</head>

<body>

<glass:modernGlassCard
  title="Edit Family Details"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Update your family details">

  <form id="editFamilyDetailsForm">

    <%-- ── Marital Status — read-only (bleeds past card padding) ──── --%>
    <div class="glass-readonly-block gs-readonly-bleed">
      <section class="row row-padding g-3">
        <div class="form-group col-6 col-md-3">
          <glass:radio name="maritalStatus" value="UnMarried" id="UnMarried" title="UnMarried"
                       checked="${familyDetails?.maritalStatus == 'UnMarried'}" disabled="true"/>
        </div>

        <div class="form-group col-6 col-md-3">
          <glass:radio name="maritalStatus" value="Divorced" id="Divorced" title="Divorced"
                       checked="${familyDetails?.maritalStatus == 'Divorced'}" disabled="true"/>
        </div>

        <div class="form-group col-6 col-md-3">
          <glass:radio name="maritalStatus" value="Awaiting Divorce" id="AwaitingDivorce" title="Awaiting Divorce"
                       checked="${familyDetails?.maritalStatus == 'Awaiting Divorce'}" disabled="true"/>
        </div>

        <div class="form-group col-6 col-md-3">
          <glass:radio name="maritalStatus" value="Widowed" id="Widowed" title="Widowed"
                       checked="${familyDetails?.maritalStatus == 'Widowed'}" disabled="true"/>
        </div>
      </section>

      <glass:text>Fields in this section are not editable</glass:text>
    </div><%-- /.gs-readonly-bleed --%>

  <%-- ── Height & Weight ───────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-12 col-md-6" style="min-width: 0;">
        <glass:heightRangeSlider name="height"
                                 min="140" max="210" step="1"
                                 value="${familyDetails?.height ?: 170}"
                                 label="Height" unit="cm"/>
      </div>

      <div class="form-group col-12 col-md-6" style="min-width: 0;">
        <glass:weightRangeSlider name="weight"
                                 min="30" max="200" step="0.1"
                                 value="${familyDetails?.weight ?: 68}"
                                 label="Weight" unit="kg"/>
      </div>
    </section>

    <%-- ── Family Status ─────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyStatus" value="Middle Class" id="MiddleClass" title="Middle Class"
                     checked="${familyDetails?.familyStatus == 'Middle Class'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyStatus" value="Upper Middle Class" id="UpperMiddleClass" title="Upper Middle Class"
                     checked="${familyDetails?.familyStatus == 'Upper Middle Class'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyStatus" value="High Class" id="HighClass" title="High Class"
                     checked="${familyDetails?.familyStatus == 'High Class'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyStatus" value="Rich / Affluent" id="RichAffluent" title="Rich / Affluent"
                     checked="${familyDetails?.familyStatus == 'Rich / Affluent'}"/>
      </div>
    </section>

    <%-- ── Family Type ───────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyType" value="Joint Family" id="JointFamily" title="Joint Family"
                     checked="${familyDetails?.familyType == 'Joint Family'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyType" value="Nuclear Family" id="NuclearFamily" title="Nuclear Family"
                     checked="${familyDetails?.familyType == 'Nuclear Family'}"/>
      </div>
    </section>

    <%-- ── Family Values ─────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyValues" value="Orthodox" id="Orthodox" title="Orthodox"
                     checked="${familyDetails?.familyValues == 'Orthodox'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyValues" value="Traditional" id="Traditional" title="Traditional"
                     checked="${familyDetails?.familyValues == 'Traditional'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyValues" value="Moderate" id="Moderate" title="Moderate"
                     checked="${familyDetails?.familyValues == 'Moderate'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="familyValues" value="Liberal" id="Liberal" title="Liberal"
                     checked="${familyDetails?.familyValues == 'Liberal'}"/>
      </div>
    </section>

    <%-- ── Disabilities ──────────────────────────────────────────── --%>
    <section class="row row-padding g-3">
      <div class="form-group col-6 col-md-3">
        <glass:radio name="disabilities" value="Normal" id="Normal" title="Normal"
                     checked="${familyDetails?.disabilities == 'Normal'}"/>
      </div>

      <div class="form-group col-6 col-md-3">
        <glass:radio name="disabilities" value="Differently Abled" id="DifferentlyAbled" title="Differently Abled"
                     checked="${familyDetails?.disabilities == 'Differently Abled'}"/>
      </div>
    </section>

    <%-- Full-width Save button on phones, right-aligned from sm up. --%>
    <div class="d-flex justify-content-center justify-content-sm-end mt-2">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit" class="w-100 w-sm-auto"/>
    </div>

  </form>

</glass:modernGlassCard>

<%-- Build the save-URL params without nested GStrings; only add values that exist. --%>
<%
  Map saveUrlParams = [:]

  if (params?.ac) {
    saveUrlParams.put('ac', params.ac)
  }

  def currentSlot = request.getAttribute('currentSlot')
  if (currentSlot != null) {
    saveUrlParams.put('slot', currentSlot)
  }
%>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("editFamilyDetailsForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    // ── Children rows (only present if the markup includes them) ─────
    const childrenSection = document.getElementById("children");
    const childStatusSection = document.getElementById("child_status");

    // ── classList helpers — null-safe: a missing section is skipped
    //    instead of throwing and aborting the rest of this script ─────
    function show(el) {
      if (!el) return;
      el.classList.remove("slide-out-fade-out", "d-none");
      el.classList.add("slide-in-fade-in");
    }

    function hide(el) {
      if (!el) return;
      el.classList.remove("slide-in-fade-in");
      el.classList.add("slide-out-fade-out");
    }

    function clearRadios(name) {
      document.querySelectorAll('input[name="' + name + '"]').forEach(function (r) {
        r.checked = false;
      });
    }

    // ── Marital Status → show/hide Children row ──────────────────────
    // Radios are disabled (read-only) but the listener handles any
    // programmatic value changes correctly.
    document.querySelectorAll('input[name="maritalStatus"]').forEach(function (radio) {
      radio.addEventListener("change", function () {
        if (this.value === "UnMarried") {
          hide(childrenSection);
          hide(childStatusSection);
          clearRadios("children");
          clearRadios("childrenStatus");
        } else {
          show(childrenSection);
          hide(childStatusSection);
          clearRadios("childrenStatus");
        }
      });
    });

    // ── Children → show/hide Child Status row ────────────────────────
    document.querySelectorAll('input[name="children"]').forEach(function (radio) {
      radio.addEventListener("change", function () {
        if (this.value === "No Children") {
          hide(childStatusSection);
          clearRadios("childrenStatus");
        } else {
          show(childStatusSection);
        }
      });
    });

    // ── Restore initial visibility from server-side values ───────────
    const maritalStatus = "${familyDetails?.maritalStatus ?: ''}";
    if (maritalStatus && maritalStatus !== "UnMarried") {
      show(childrenSection);
      const children = "${familyDetails?.children ?: ''}";
      if (children && children !== "No Children") {
        show(childStatusSection);
      }
    }

    // ── Read the form into a plain object ────────────────────────────
    function checkedValue(name) {
      const el = form.querySelector('[name="' + name + '"]:checked');
      return el ? el.value : '';
    }

    function fieldValue(name) {
      const el = form.querySelector('[name="' + name + '"]');
      return el ? el.value.trim() : '';
    }

    function buildPayload() {
      return {
        height: fieldValue("height"),
        weight: fieldValue("weight"),
        familyStatus: checkedValue("familyStatus"),
        familyType: checkedValue("familyType"),
        familyValues: checkedValue("familyValues"),
        disabilities: checkedValue("disabilities")
      };
    }

    // ── Save URL (raw() stops &amp; encoding between params) ─────────
    const saveUrl = '${raw(createLink(controller: 'DBoard', action: 'updateFamilyDetails', params: saveUrlParams))}';

    // ── Async form submission ────────────────────────────────────────
    form.addEventListener("submit", async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      const payload = buildPayload();

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(saveUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "X-Requested-With": "XMLHttpRequest"
          },
          body: JSON.stringify(payload)
        });

        // ── Is it a JSON response? ───────────────────────────────────
        const contentType = (response.headers.get("Content-Type") || "").toLowerCase();
        const isJson = contentType.includes("application/json");
        const raw = await response.text();

        let data = null;
        if (isJson && raw) {
          try {
            data = JSON.parse(raw);
          } catch (parseErr) {
            console.error("Response claimed to be JSON but failed to parse:", parseErr, raw.slice(0, 300));
          }
        }

        if (!data) {
          let reason;
          if (response.redirected) {
            reason = "redirected to " + response.url + " (session expired or login required?)";
          } else if (!raw) {
            reason = "empty response body";
          } else if (!isJson) {
            reason = "not JSON (Content-Type: " + (contentType || "none") + ")";
          } else {
            reason = "invalid JSON";
          }
          console.error("Expected JSON from updateFamilyDetails but got " + reason, {
            status: response.status,
            contentType: contentType,
            body: raw.slice(0, 300)
          });
          window.showError("Unexpected server response (" + reason + "). Status " + response.status + ".");
          return;
        }

        console.log("JSON response received:", response.status, data);

        // ── Handle the JSON payload ──────────────────────────────────
        if (response.ok && data.success) {
          window.showSuccess(data.message || "Family details updated successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }
        } else if (response.status === 404) {
          window.showWarning(data.message || "Family details not found.");

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
</script>

</body>
</html>
