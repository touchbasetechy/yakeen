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
  iconAction="index"

  titleHtml="true"
  subtitle="Tell us where you live">
  <div class="form-container">
    <a
      href="${createLink(controller: 'DBoard', action: 'listTicketTypeForAddTicket')}"
      class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
  </div>

  <form id="addTicketForm" method="post">
    <section class="row colset-2-its  form-container">

      <div class="mb-3"><div class="input-container">
        <input type="text" class="form-control" id="subject" name="subject"
               value="" autofocus required>
        <label for="subject" class="form-label">Subject</label>
      </div></div></section>
    <section class="row colset-2-its  form-container">
      <div class="mb-3"><div class="input-container">
        <input type="text" class="form-control" id="message" name="message"
               value="" required>
        <label for="message" class="form-label">Message</label>
      </div></div>
    </section>
    <section class="row colset-2-its  form-container">
      <div class="d-flex justify-content-end">

        <button class=" btn btn-primary toolbar-button" type="submit"><i class="fas fa-angle-right"
                                                                         aria-hidden="true">&nbsp;save</i>
        </button>
      </div>
    </section>
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
          "${createLink(controller: 'DBoard', action: 'saveTicketForAdd', params: [id: params?.id, ac: params?.ac, slot: "${request.getAttribute('currentSlot')}"])}",
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

    const inputs = document.querySelectorAll("input[type='text']");
    inputs.forEach((input) => {
      input.addEventListener("focus", function () {
        this.nextElementSibling.classList.add("active");
      });
      input.addEventListener("blur", function () {
        this.nextElementSibling.classList.remove("active");
      });
    });
  });
</script>
</body>
</html>
