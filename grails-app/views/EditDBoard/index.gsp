<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>

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

      <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
         class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      <g:if test="${!params?.pt}">
        <a
          href="${createLink(controller: 'AdminDBoard', action: 'listPersonTypeForEdit', params: [p: params?.p, con: params?.con, op: params?.op])}"
          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      </g:if>
      <g:else>
        <a
          href="${createLink(controller: 'AdminDBoard', action: 'listPersonTypeForEdit', params: [p: params?.p, con: params?.con, op: params?.op, pt: params?.pt])}"
          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>
      </g:else>


      <h1 class="same-line">DashBoard</h1>

      <div class="same-line custom-icon d-flex justify-content-end">

        <g:if test="${personType?.id != oPT.id}">
          <i class="fas fa-info-circle" data-container="body"
             data-toggle="popover"
             data-html="true"
             title="${session?.personToEdit?.firstName}&nbsp;${session?.personToEdit?.lastName}"
             data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>User Name</div>
                <div class='col'>${session?.personToEdit?.eMail}</div>
              </div>
        <div class='row even-row'>
        <div class='col'>Old User Type</div>
        <div class='col'>${oPT?.personTypeName}</div>
             </div>
             <div class='row odd-row'>
             <div class='col'>New User Type</div>
             <div class='col'>${personType?.personTypeName}</div>
             </div>
               <div class='row even-row'>
                <div class='col'>Edit Mode</div>
        <g:if test="${params?.con == 'edb'}">
               <div class='col'>Dash Board</div>
             </g:if>
             <g:elseif test="${params?.con == 'person'}">
               <div class='col'>Wizard</div>
             </g:elseif>
             <g:else>
               <div class='col'>N/A</div>
             </g:else>
        </div>"></i>
        </g:if>
        <g:else>
          <i class="fas fa-info-circle" data-container="body"
             data-toggle="popover"
             data-html="true"
             title="${session?.personToEdit?.firstName}&nbsp;${session?.personToEdit?.lastName}"
             data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>User Name</div>
                <div class='col'>${session?.personToEdit?.eMail}</div>
              </div>
          <div class='row even-row'>
            <div class='col'>User Type</div>

            <div class='col'>${personType?.personTypeName}</div>
          </div>

          <div class='row odd-row'>
            <div class='col'>Edit Mode</div>
            <g:if test="${params?.con == 'edb'}">
               <div class='col'>Dash Board</div>
             </g:if>
             <g:elseif test="${params?.con == 'person'}">
               <div class='col'>Wizard</div>
             </g:elseif>
             <g:else>
               <div class='col'>N/A</div>
             </g:else>
          </div>"></i>
        </g:else>
      </div>
    </div>

    <div class="form-container">
      <section class="row colset-2-its">
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
        <ul>
          <li class="header">Edit</li>
          <li>
            <ul>
              <li>
                <g:if test="${!params?.pt}">
                  <g:link controller="EditDBoard"
                          action="editGeneralDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">General Details</g:link>
                </g:if>
                <g:else>
                  <g:link controller="EditDBoard"
                          action="editGeneralDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op, pt: params?.pt]}">General Details</g:link>
                </g:else>
              </li>

              <li>
                <g:if test="${!params?.pt}">
                  <g:link controller="EditDBoard"
                          action="editPersonalDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Personal Details</g:link>
                </g:if>
                <g:else>
                  <g:link controller="EditDBoard"
                          action="editPersonalDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op, pt: params?.pt]}">Personal Details</g:link>
                </g:else>
              </li>
              <li>
                <g:if test="${!params?.pt}">
                  <g:link controller="EditDBoard"
                          action="editSpouseDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Spouse Preferences</g:link>
                </g:if>
                <g:else>
                  <g:link controller="EditDBoard"
                          action="editSpouseDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op, pt: params?.pt]}">Spouse Preferences</g:link>
                </g:else>
              </li>
              <li>
                <g:if test="${!params?.pt}">
                  <g:link controller="EditDBoard"
                          action="editAddressDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Address</g:link>
                </g:if>
                <g:else>
                  <g:link controller="EditDBoard"
                          action="editAddressDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op, pt: params?.pt]}">Address</g:link>
                </g:else>
              </li>
              <li>
                <g:if test="${!params?.pt}">
                  <g:link controller="EditDBoard"
                          action="editPolicies"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Policies</g:link>
                </g:if>
                <g:else>
                  <g:link controller="EditDBoard"
                          action="editPolicies"
                          params="${[p: params?.p, con: params?.con, op: params?.op, pt: params?.pt]}">Policies</g:link>
                </g:else>
              </li>
            </ul>
          </li>
        </ul>
      </section>
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
  });
</script>
</body>
</html>
