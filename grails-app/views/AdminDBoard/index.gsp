<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/pace/1.2.4/themes/blue/pace-theme-flash.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/pace/1.2.4/pace.min.js"></script>

</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-square"><span class="label-text">Super Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row colset-2-its">
      <h1>Dash Board</h1>
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
    </section>

    <section class="row">
      <div class="badge badge-primary badge-pill flex-title">Add</div>
    </section>

    <section class="row">
      <div class="flex-container">
        <div class="card flex-item flex-icon" id="add-icon-1">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">User Type</span>

            <a href="${createLink(controller: 'AdminDBoard', action: 'addPersonType')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-2">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'user-add.png')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">User</span>

            <a href="${createLink(controller: 'AdminDBoard', action: 'listPersonTypeForAdd')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-3">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'tickettypes.svg')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">Ticket Type</span>

            <a href="${createLink(controller: 'AdminDBoard', action: 'addTicketType')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-4">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'ticket_add.svg')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">Ticket</span>

            <a href="${createLink(controller: 'Ticket', action: 'listTicketTypeForAdd')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-5">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'hobbies.svg')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">Hobby</span>

            <a href="${createLink(controller: 'Hobby', action: 'addHobby', params: [ac: 'an'])}" class="stretched-link">
            </a>
          </div>
        </div>
      </div>
    </section>

    <section class="row">
      <div class="badge badge-primary badge-pill flex-title">Edit</div>
    </section>

    <section class="row">
      <div class="card flex-item flex-icon" id="edit-icon-1">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">User Type</span>

          <a href="${createLink(controller: 'AdminDBoard', action: 'listPersonType')}" class="stretched-link">
          </a>
        </div>
      </div>

      <div class="card flex-item flex-icon" id="edit-icon-2">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'user-edit.png')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">User</span>

          <a href="${createLink(controller: 'AdminDBoard', action: 'listPerson')}" class="stretched-link">
          </a>
        </div>
      </div>

      <div class="card flex-item flex-icon" id="edit-icon-3">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'tickettypes_edit.svg')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">Ticket Type</span>

          <a href="${createLink(controller: 'AdminDBoard', action: 'listTicketType')}" class="stretched-link">
          </a>
        </div>
      </div>

      <div class="card flex-item flex-icon" id="edit-icon-4">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'ticket.svg')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">Ticket</span>

          <a href="${createLink(controller: 'Ticket', action: 'listTicket')}" class="stretched-link">
          </a>
        </div>
      </div>

      <div class="card flex-item flex-icon" id="edit-icon-5">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'hobbies.svg')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">Hobby</span>

          <a href="${createLink(controller: 'Hobby', action: 'listHobbiesForEdit')}" class="stretched-link">
          </a>
        </div>
      </div>
    </section>

    <section class="row">
      <div class="badge badge-primary badge-pill flex-title">Manage</div>
    </section>
    <section class="row colset-2-its">
      <div class="card flex-item flex-icon" id="manage-icon-1">
        <div class="card-body">
          <img src="${resource(dir: 'images', file: 'ticket.svg')}" class="card-img-top"
               alt="icon Image">
          <span class="d-flex justify-content-center">Ticket</span>

          <a href="${createLink(controller: 'Ticket', action: 'listTicketToManage')}" class="stretched-link">
          </a>
        </div>
      </div>
    </section>
  </div>
</div>


<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);
  });
  Pace.options = {
    ajax: true, // Enable AJAX loading
    document: true, // Enable document loading
    eventLag: false // Disable event lag
  };
</script>
</body>
</html>
