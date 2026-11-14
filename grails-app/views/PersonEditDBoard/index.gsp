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
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-edit"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row">
      <div class="col d-flex justify-content-end">
        <a
          href="${createLink(controller: 'person', action: 'index', params: [p: params?.p, con: params?.con, op: params?.op])}"
          class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

        <h1 class="same-line">DashBoard</h1>

        <g:if test="${personType?.id != oPT.id}">
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
             <g:elseif test="${params?.con == 'sup'}">
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

            <div class='col'>${personType?.personTypeName}</div>
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
        </g:else>
      </div>


      <div class="col d-flex justify-content-end">
        <!-- Toggle Switch -->
        <div class="same-line col d-flex justify-content-end">
          <div class="form-group">
            <label class="sleek-toggle">
              <input class="toggle-input" type="checkbox" id="showIcons">
              <span class="toggle-track">
                <span class="toggle-thumb"></span>
                <span class="toggle-icons">
                  <span class="icon-on">✓</span>
                  <span class="icon-off">✕</span>
                </span>
              </span>
              <span class="toggle-label">Show Icons</span>
            </label>
          </div>
        </div>
      </div>
    </section>
    <section class="row">
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

    <section class="row d-none h-0" id="listItems">
      <div class="col d-flex justify-content-center">
        <ul>
          <li class="header badge badge-warn">Edit</li>
          <li>
            <ul>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editGeneralDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">General Details</g:link>
                </div>
              </li>

              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editPersonalDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Personal Details</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editSpouseDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Spouse Preferences</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editFamilyDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Family Details</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editSpouseFamilyPreferences"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Spouse Family Preferences</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editContactDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Contact Details</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editEducationDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Education Details</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editProfessionDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Profession Details</g:link>
                </div>
              </li>
              <li>
                <div class="badge badge-prime">
                  <g:link controller="PersonEditDBoard"
                          action="editAddressDetails"
                          params="${[p: params?.p, con: params?.con, op: params?.op]}">Address</g:link>
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </section>
    <section class="row d-none h-0" id="listIcons">
      <div class="row">
        <div class="flex-container">
          <g:if test="${!person?.personDetails}">
            <div class="card flex-item flex-icon" id="add-icon-1">
              <div class="card-body">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="icon Image">
                <span class="d-flex justify-content-center text-center">Personal Details</span>

                <a
                  href="${createLink(controller: 'PersonEditDBoard', action: 'editPersonalDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                  class="stretched-link">
                </a>
              </div>
            </div>
          </g:if>
          <g:if test="${!person?.spouseDetails}">
            <div class="card flex-item flex-icon" id="add-icon-2">
              <div class="card-body">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="icon Image">
                <span class="d-flex justify-content-center text-center">Spouse Preferences</span>
                <a
                  href="${createLink(controller: 'PersonEditDBoard', action: 'editSpouseDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                  class="stretched-link">
                </a>
              </div>
            </div>
          </g:if>
          <g:if test="${!person?.familyDetails}">
            <div class="card flex-item flex-icon" id="add-icon-3">
              <div class="card-body">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="icon Image">
                <span class="d-flex justify-content-center text-center">Family Details</span>

                <a
                  href="${createLink(controller: 'PersonEditDBoard', action: 'editFamilyDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                  class="stretched-link">
                </a>
              </div>
            </div>
          </g:if>

          <g:if test="${!person?.spouseFamilyDetails}">
            <div class="card flex-item flex-icon" id="add-icon-4">
              <div class="card-body">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="icon Image">
                <span class="d-flex justify-content-center text-center">Spouse Family Preferences</span>

                <a
                  href="${createLink(controller: 'PersonEditDBoard', action: 'editSpouseFamilyPreferences', params: [p: params?.p, op: params?.op, con: params?.con])}"
                  class="stretched-link">
                </a>
              </div>
            </div>
          </g:if>

          <div class="card flex-item flex-icon" id="add-icon-5">
            <div class="card-body">
              <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                   alt="icon Image">
              <span class="d-flex justify-content-center text-center">Contact Details</span>
              <a
                href="${createLink(controller: 'PersonEditDBoard', action: 'editContactDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                class="stretched-link">
              </a>
            </div>
          </div>

          <div class="card flex-item flex-icon" id="add-icon-6">
            <div class="card-body">
              <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                   alt="icon Image">
              <span class="d-flex justify-content-center text-center">Education Details</span>
              <a
                href="${createLink(controller: 'PersonEditDBoard', action: 'editEducationDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                class="stretched-link">
              </a>
            </div>
          </div>

          <div class="card flex-item flex-icon" id="add-icon-7">
            <div class="card-body">
              <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                   alt="icon Image">
              <span class="d-flex justify-content-center text-center">Profession Details</span>
              <a
                href="${createLink(controller: 'PersonEditDBoard', action: 'editProfessionDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                class="stretched-link">
              </a>
            </div>
          </div>

          <div class="card flex-item flex-icon" id="add-icon-8">
            <div class="card-body">
              <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                   alt="icon Image">
              <span class="d-flex justify-content-center text-center">Address</span>
              <a
                href="${createLink(controller: 'PersonEditDBoard', action: 'editAddressDetails', params: [p: params?.p, op: params?.op, con: params?.con])}"
                class="stretched-link">
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);


    const listIcons = document.getElementById('listIcons');
    const listItems = document.getElementById('listItems');
    if (localStorage.getItem("showIcons") === "true") {
      listIcons.classList.remove('d-none');
      listIcons.classList.add('h-100');
      listIcons.classList.add('d-block');

      listItems.className = "row h-0 d-none";
      document.getElementById('showIcons').checked = true;
    } else {
      listIcons.className = "row h-0 d-none";

      listItems.classList.remove('d-none');
      listItems.classList.add('h-100');
      listItems.classList.add('d-block');
      document.getElementById('showIcons').checked = false;
    }

    document.getElementById('showIcons').addEventListener('change', function () {
      const listIcons = document.getElementById('listIcons');
      const listItems = document.getElementById('listItems');

      if (this.checked) {
        listIcons.classList.remove('d-none');
        listIcons.classList.add('h-100');
        listIcons.classList.add('ani');
        listIcons.classList.add('d-block');
        localStorage.setItem("showIcons", "true");

        listItems.classList.remove('d-block');
        listItems.classList.remove('h-100');
        listItems.classList.add('d-none');
      } else {
        listIcons.classList.remove('d-block');
        listIcons.classList.add('h-0');
        listIcons.classList.add('d-none');
        localStorage.setItem("showIcons", "false");

        listItems.classList.remove('d-none');
        listItems.classList.add('h-100');
        listItems.classList.add('d-block');
      }
    });

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
