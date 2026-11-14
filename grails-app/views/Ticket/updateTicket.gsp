<!doctype html>
<html>
<head>
  <meta name="layout" content="rightslidein"/>

  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-edit"><span class="label-text">Super Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="form-container">
      <a href="${createLink(controller: 'adminDBoard', action: 'index')}"
         class="same-line"><i class="fas fa-home embossed-link" aria-hidden="true"></i></a>
      <a
        href="${createLink(controller: 'Ticket', action: 'listTicketTypeForEdit', params: [t: "${params.t}", at: true])}"
        class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Edit Ticket</h1>


      <div class="same-line custom-icon d-flex justify-content-end">

        <g:if test="${presentTt?.id != oldTt?.id}">
          <i class="fas fa-info-circle" data-container="body"
             data-toggle="popover"
             data-html="true"
             title="${session?.ticketToEdit?.ticketId}"
             data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>Subject</div>
                <div class='col'>${session?.ticketToEdit?.subject}</div>
              </div>
        <div class='row even-row'>
        <div class='col'>Message</div>
        <div class='col'>${session?.ticketToEdit?.message}</div>
             </div>
             <div class='row odd-row'>
             <div class='col'>&nbsp;</div>
                </div>
               <div class='row even-row'>
                <div class='col'><strong>Ticket Type Details</strong></div>
       </div>
       <div class='row odd-row'>
       <div class='col'>Old Ticket Type</div>
       <div class='col'>${oldTt?.name}</div>
                </div>
                <div class='row even-row'>
       <div class='col'>New Ticket Type</div>
       <div class='col'>${presentTt?.name}</div>
        </div>"></i>
        </g:if>
        <g:else>
          <i class="fas fa-info-circle" data-container="body"
             data-toggle="popover"
             data-html="true"
             title="${session?.ticketToEdit?.ticketId}"
             data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>Subject</div>
                <div class='col'>${session?.ticketToEdit?.subject}</div>
              </div>
          <div class='row even-row'>
            <div class='col'>Message</div>
            <div class='col'>${session?.ticketToEdit?.message}</div>
          </div>
<div class='row odd-row'>
                </div>
               <div class='row even-row'>
                <div class='col'><strong>Ticket Type Details</strong></div>
       </div>
       <div class='row odd-row'>
       <div class='col'>Ticket Type</div>
       <div class='col'>${oldTt?.name}</div>
                </div>"></i>
        </g:else>
      </div>
    </div>

    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>

    <div class="form-container">
      <form
        action="${createLink(controller: 'Ticket', action: 'updateAndSaveTicket', params: [ott: params?.ott, ptt: params?.ptt])}"
        method="post">

        <section class="row colset-2-its  form-container">

          <div class="mb-3"><div class="input-container">
            <input type="text" class="form-control" id="subject" name="subject"
                   value="${session?.ticketToEdit?.subject}" autofocus required>
            <label for="subject" class="form-label">Subject</label>
          </div></div>
        </section>
        <section class="row colset-2-its  form-container">
          <div class="mb-3"><div class="input-container">
            <input type="text" class="form-control" id="message" name="message"
                   value="${session?.ticketToEdit?.message}" required>
            <label for="message" class="form-label">Message</label>
          </div></div>
        </section>
        <section class="row colset-2-its  form-container">
          <div class="d-flex justify-content-end">

            <button class=" btn btn-primary toolbar-button" type="submit"><span class="fas fa-angle-right"
                                                                                aria-hidden="true">&nbsp;save</span>
            </button>
          </div>
        </section>
      </form>
    </div>
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);

    // Initialize the popover
    $('[data-toggle="popover"]').popover({
      trigger: 'manual', // Set trigger to 'manual'
      html: true, // Allow HTML content
      placement: 'left',
    }).on('mouseover', function () {
      $(this).popover('show');
    }).on('mouseout', function () {
      $(this).popover('hide');
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
