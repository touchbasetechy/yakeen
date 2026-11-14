<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-square"><span
      class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">

    <h1 class="d-flex justify-content-center">Dash Board</h1>
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


    <section class="row">
      <div class="col">
        <div class="badge badge-primary badge-pill flex-title">Add</div>
      </div>
      <!-- Popover Trigger -->
      <div class="col d-flex justify-content-end">
        <i
          id="autoPopover"
          class="fas fa-info-circle d-none"
          data-toggle="popover"
          data-placement="bottom"
          title="Hi ! ${person?.firstName}&nbsp; ${person?.lastName}"
          data-content="${popperMsg}">
        </i></div>
    </section>
    <section class="row">
      <div class="flex-container">
        <div class="card flex-item flex-icon" id="add-icon-1">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">User</span>

            <a href="${createLink(controller: 'person', action: 'addPerson')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-2">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">Ticket</span>
            <a href="${createLink(controller: 'person', action: 'listTicketTypeForAdd')}" class="stretched-link">
            </a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="add-icon-3">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'hobbies.svg')}" class="card-img-top"
                 alt="icon Image">
            <span class="d-flex justify-content-center">Hobby</span>

            <a href="${createLink(controller: 'Hobby', action: 'addHobby', params: [ac: 'ad'])}" class="stretched-link">
            </a>
          </div>
        </div>
      </div>
    </section>
    <section class="row">
      <div class="badge badge-primary badge-pill flex-title">Edit</div>
    </section>
    <section class="row">
      <div class="flex-container">
        <div class="card flex-item flex-icon" id="edit-icon-1">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">

            <span class="d-flex justify-content-center">User</span>
            <a href="${createLink(controller: 'person', action: 'listUsers')}" class="stretched-link"></a>
          </div>
        </div>
      </div>
    </section>
    <section class="row">
      <div class="badge badge-primary badge-pill flex-title">Manage</div>
    </section>

    <section class="row">
      <div class="flex-container">
        <div class="card flex-item flex-icon" id="manage1">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">

            <span class="d-flex justify-content-center">User Policies</span>
            <a href="${createLink(controller: 'person', action: 'listUsersForPolicies')}" class="stretched-link"></a>
          </div>
        </div>

        <div class="card flex-item flex-icon" id="manage2">
          <div class="card-body">
            <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                 alt="icon Image">

            <span class="d-flex justify-content-center">Tickets</span>
            <a href="${createLink(controller: 'person', action: 'listUsers')}" class="stretched-link"></a>
          </div>
        </div>
      </div>
    </section>
  </div>
</div>
<!-- jQuery, Popper.js, Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    $('#autoPopover').popover();

    if (localStorage.getItem('adminPopOverShown') === "false") {
      $('#autoPopover').removeClass("d-none");
      $('#autoPopover').addClass("d-block");
      $('#autoPopover').popover('show');
      localStorage.setItem('adminPopOverShown', "true");
    }

    setTimeout(function () {
      $("#alert").hide();
      $('#autoPopover').popover('hide');
      $('#autoPopover').hide();
      $('#autoPopover').removeClass("d-block");
      $('#autoPopover').addClass("d-none");
    }, 6000);
  });
</script>
</body>
</html>
