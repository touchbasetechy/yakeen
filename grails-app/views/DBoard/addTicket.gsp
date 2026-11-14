<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardAdd"/>
</head>

<body>

<glass:modernGlassCard
  title="Add Ticket"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="listTicketTypeForAdd"
  titleHtml="true"
  subtitle="Tell us about your issue">

  <form id="addTicketForm">

    <%-- ── Subject ───────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="form-group col-sm-12">
        <glass:alphaNumericTextField
          name="subject" id="subject" label="Subject"
          required="true" autofocus="true"/>
      </div>
    </section>

    <%-- ── Message ──────────────────────────────────────────────────── --%>
    <section class="row row-padding">
      <div class="form-group col-sm-12">
        <glass:alphaNumericTextField
          name="message" id="message" label="Message"
          required="true"/>
      </div>
    </section>

    <div class="d-flex justify-content-end">
      <glass:cardBtn label="Save" variant="cosmic" faIcon="fas fa-save" type="submit"/>
    </div>

  </form>

</glass:modernGlassCard>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    // ── Async form submission ─────────────────────────────────────────
    const form = document.getElementById("addTicketForm");
    const saveBtn = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', async function (e) {
      e.preventDefault(); // stops native submission for both click AND Enter-key triggers

      // your AJAX call here
      if (!form.checkValidity()) {
        form.reportValidity();
        return;
      }

      // ── Build JSON payload ────────────────────────────────────────
      const payload = {
        subject: form.querySelector('[name="subject"]').value.trim(),
        message: form.querySelector('[name="message"]').value.trim()
      };

      saveBtn.disabled = true;
      const originalLabel = saveBtn.innerHTML;
      saveBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Saving\u2026';

      try {
        const response = await fetch(
          "${createLink(controller: 'DBoard', action: 'saveTicket', params: [slot: "${request.getAttribute('currentSlot')}"])}",
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
          window.showSuccess(data.message || "Ticket saved successfully!");
          if (data.redirectUrl) {
            setTimeout(function () {
              window.location.href = data.redirectUrl;
            }, 1500);
          }

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
