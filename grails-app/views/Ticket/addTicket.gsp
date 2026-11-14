<!doctype html>
<html>
<head>
  <meta name="layout" content="leftslidein"/>
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
      <g:if test="${params?.ac == 'an'}">
        <a href="${createLink(controller: 'adminDBoard', action: 'index')}"
           class="same-line"><i class="fas fa-home embossed-link" aria-hidden="true"></i></a>
        <a
          href="${createLink(controller: 'Ticket', action: 'listTicketTypeForAdd', params: [t: "${params.id}", at: true])}"
          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      </g:if>
      <g:else>
        <a href="${createLink(controller: 'Ticket', action: 'listTicketToManage')}"
           class="same-line"><i class="fas fa-home embossed-link" aria-hidden="true"></i></a>
        <a
          href="${createLink(controller: 'Ticket', action: 'listTicketTypeForAddToManageTickets', params: [t: "${params.id}", at: true])}"
          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      </g:else>
      <h1 class="same-line form-container">Add Ticket</h1>
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

    <form
      action="${createLink(controller: 'Ticket', action: 'saveTicket', params: [id: "${params.id}", ac: params?.ac])}"
      method="post">
      <section class="row colset-2-its  form-container">

        <div class="mb-3"><div class="input-container">
          <input type="text" class="form-control" id="subject" name="subject"
                 value="" autofocus required>
          <label for="subject" class="form-label">Subject</label>
        </div></div>
      </section>
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
  </div>
</div>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {

      $("#alert").hide();
    }, 6000);

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
