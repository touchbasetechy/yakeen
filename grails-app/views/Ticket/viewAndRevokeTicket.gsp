<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row">
      <a href="${createLink(controller: 'ticket', action: params?.ac, params: [t: params?.t])}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Restore Ticket</h1>

      <div class="col-md-12"><strong>Ticket ID:</strong> ${ticket?.ticketId}</div>

      <div class="col-md-12"><strong>Ticket Type:</strong> ${ticket?.ticketType?.name}</div>

      <div class="col-md-12">( ${ticket?.ticketType?.description} )</div>

      <div class="col-md-12 margin-top "><strong>Subject:</strong> ${ticket?.subject}</div>

    </section>

    <g:if test="${ticket?.approvedBy && ticket?.approvedAt}">
      <form method="post"
            action="${createLink(controller: 'Ticket', action: 'revokeHeldTicket', params: [t: params?.t])}">
        <section class="row">
          <div class="col-md-12">
            <label for="message"><strong>Message</strong></label>
            <textarea id="message" class="form-control custom-textarea border-0 no-decoration"
                      readonly>${ticket?.message}</textarea>
          </div>
        </section>
        <section class="row">
          <div class="col-md-12">
            <label for="Reason"><strong>Reason for Restore</strong></label>
            <textarea id="Reason" name="Reason" class="form-control custom-textarea border-0 no-decoration"
                      autofocus></textarea>
          </div>
        </section>

        <div class="row justify-content-center">
          <div class="col-md-6 text-center">
            <button type="submit" class="btn btn-warning mb-2"><i
              class="fa-solid fa-play"></i>Restore</button>
          </div>
        </div>
      </form>
    </g:if>
    <g:else>
      <g:render template="partialView"/>
      <div class="row justify-content-center">
      <div class="col-md-6 text-center">
        <a href="${createLink(controller: 'Ticket', action: 'approveTicketAndReturnToRevoke', params: [t: params?.t])}"
       class="btn btn-primary mb-2 btn-text"><i class="fa fa-check" aria-hidden="true"></i>Approve</a>
    </g:else>

  </div>
  </div>
    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
