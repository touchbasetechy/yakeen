<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  %{--  <style>--}%
  %{--  .btn {--}%
  %{--    border: 2px solid #ffffff; /* Dark border */--}%
  %{--    border-radius: 5px; /* Slightly rounded corners */--}%
  %{--    padding: 10px; /* Padding for better visual appearance */--}%
  %{--    transition: background-color 0.3s ease, border-color 0.3s ease; /* Smooth transition on hover */--}%
  %{--  }--}%

  %{--  .btn:hover {--}%
  %{--    border-color: #888; /* Softened border color on hover */--}%
  %{--  }--}%

  %{--  .btn i {--}%
  %{--    margin-right: 5px; /* Space between icon and text */--}%
  %{--  }--}%
  %{--  </style>--}%
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
      <a href="${createLink(controller: 'ticket', action: 'listTicketToManage')}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line">View Ticket</h1>

      <div class="col-md-12"><strong>Ticket ID:</strong> ${ticket?.ticketId}</div>

      <div class="col-md-12"><strong>Ticket Type:</strong> ${ticket?.ticketType?.name}</div>

      <div class="col-md-12">( ${ticket?.ticketType?.description} )</div>

      <div class="col-md-12 margin-top "><strong>Subject:</strong> ${ticket?.subject}</div>

    </section>
    <section class="row">
      <div class="col-md-12">
        <label for="message"><strong>Message</strong></label>
        <textarea id="message" class="form-control custom-textarea border-0 no-decoration"
                  readonly>${ticket?.message}</textarea>
      </div>
    </section>
  </div>
</div>

<div class="row justify-content-center">
  <div class="col-md-6 text-center">
    <a href="${createLink(controller: 'Ticket', action: 'approveTicket', params: [t: params?.t, ac: 'viewTicket'])}"
       class="btn btn-primary mb-2 btn-text" role="button">
      <i class="fas fa-check" aria-hidden="true"></i>Approve
    </a>
    <a href="${createLink(controller: 'Ticket', action: 'viewAndHoldTicket', params: [t: params?.t, ac: 'viewTicket'])}"
       class="btn btn-warning mb-2 btn-text"><i class="fa-solid fa-pause"></i>Hold</a>

    <a
      href="${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket', params: [t: params?.t, ac: 'viewTicket'])}"
      class="btn btn-secondary mb-2 btn-text"><i class="fa-solid fa-pause"></i>Duplicate</a>
    <a
      href="${createLink(controller: 'Ticket', action: 'viewAndCloseTicket', params: [t: params?.t, ac: 'viewTicket'])}"
      class="btn btn-success mb-2 btn-text"><i class="fa-solid fa-close"></i>Close</a>
    <a
      href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: params?.t, ac: 'viewTicket'])}"
      class="btn btn-danger mb-2 btn-text"><i class="fa fa-trash"></i>Delete</a>
  </div>
</div>


<!-- Bootstrap JS and dependencies -->
%{--<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>--}%
%{--<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>--}%
%{--<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>--}%
</body>
</html>
