<!doctype html>
<html xmlns="http://www.w3.org/1999/html">
<head>
  <meta name="layout" content="main"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-hat-wizard"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="d-flex justify-content-center">
      <a href="${createLink(controller: 'person', action: 'listUsers')}" class="same-line"><i
        class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      <h4 class="same-line">General User Details</h4>

      <div class="same-line custom-icon d-flex justify-content-end">
      </div>
      <section class="row colset-2-its">
        <div class="d-flex justify-content-center">
          <i class="fas fa-info-circle" data-container="body"
             data-toggle="popover"
             data-html="true"
             title="${personToEdit?.firstName}&nbsp;${personToEdit?.lastName}"
             data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>User Name</div>
                <div class='col'>${personToEdit?.eMail}</div>
              </div>
          <div class='row even-row'>
            <div class='col'>User Type</div>

            <div class='col'>${personToEdit?.personType?.personTypeName}</div>
          </div>

          <div class='row odd-row'>
            <div class='col'>Edit Mode</div>
            <g:if test="${params?.con == 'edb'}">
               <div class='col'>Dash Board</div>
             </g:if>
             <g:elseif test="${params?.con == 'sup'}">
               <div class='col'>Wizard</div>
             </g:elseif>
             <g:else>
               <div class='col'>N/A</div>
             </g:else>
          </div>"></i>
          %{--          </g:else>--}%

        </div>

        <g:if test="${flash.error}">
          <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger">
            ${flash.error}
          </div>
        </g:if>
        <g:if test="${flash.message}">
          <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success">
            ${flash.message}
          </div>
        </g:if>
    </div>
  </section>
    <section class="row colset-2-its">
      <div class="d-flex justify-content-center">
        <form
          action="${createLink(controller: 'person', action: 'saveGeneralDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
          method="post">
          <div class="mb-3"><div class="input-container">
            <input type="text" class="form-control" id="firstName" name="firstName" placeholder=""
                   value="${personToEdit?.firstName}"
                   autofocus
                   required>
            <label for="firstName" class="form-label">First Name</label>
          </div></div>

          <div class="mb-3"><div class="input-container">
            <input type="text" class="form-control" id="lastName" name="lastName" placeholder=""
                   value="${personToEdit?.lastName}"
                   required>
            <label for="lastName" class="form-label">Last Name</label>
          </div></div>

          <div class="d-flex justify-content-end">
            <button class="btn btn-primary" type="submit"><i class="fas fa-save"
                                                             aria-hidden="true">&nbsp;&nbsp;Save</i>
            </button>
          </div>
        </form>
      </div>
    </section>
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
  });
</script>
</body>
</html>
