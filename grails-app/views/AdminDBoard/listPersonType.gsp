<!doctype html>
<html>
<head>
  <meta name="layout" content="bottomslidein"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Select User Type</h1>
    </div>
    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger"
           style="display: block">${flash.error}</div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success"
           style="display: block">${flash.message}</div>
    </g:if>
    <section class="row">
      <div id="table-container" tabindex="-1" class="overflow-auto">
        <g:if test="${personTypes}">
          <table id="table">
            <thead>
            <tr>
              <th>User Type</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody id="table-body">
            <g:each in="${personTypes}" var="personType">
              <g:set var="paramName" value="id"/>
              <g:set var="paramValue" value="${personType.id}"/>

              <tr>
                <td>${personType?.personTypeName}</td>
                <td><div id="toolbar">
                  <a
                    href="${createLink(controller: 'adminDBoard', action: 'editPersonType', params: [(paramName): paramValue])}"
                    class="btn btn-primary toolbar-button" role="button"><i class="fas fa-edit"></i></a>
                  <a
                    href="#" data-id=${personType?.id} data-href="${personType?.personTypeName}"
                    data-toggle="modal"
                    class="btn btn-danger toolbar-button" role="button"><i class="fas fa-trash-alt"></i></a>
                </div></td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </g:if>
        <g:else>
          <table id="table">
            <thead>
            <tr>
              <th>User Type</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>No user types found.</td>
              <td>Unable to edit user type !!</td>
              <td>
                <a
                  href="${createLink(controller: 'adminDBoard', action: 'addPersonType')}"
                  class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
              </td>
            </tr>
            </tbody>
          </table>
        </g:else>
      </div>

      <div id="loading-indicator" style="display: none;">
        <img src="${resource(dir: 'images', file: "3D fading lines.gif")}" class="img_25x25"
             alt="Loading Image">

        &nbsp;Loading...
      </div>

    </section>
  </div>
</div>

<div class="modal fade animate__animated animate__backInDown" id="confirmationModal" tabindex="-1"
     aria-labelledby="confirmationModalLabel"
     aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header border-0">
        <h5 class="modal-title" id="confirmationModalLabel">Confirm Delete</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"><span
          aria-hidden="true">&times;</span></button>
      </div>

      <div class="modal-body border-0">
        <input type="hidden" id="id" name="id">

        <div class="same-line">Are you sure you want to delete <strong>"</strong></div>

        <div class="same-line"><strong id="fullName1"></strong></div>

        <div class="same-line"><strong>"</strong> ?</div>
      </div>

      <div class="modal-footer border-0">
        <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fas fa-times">&nbsp;No</i>
        </button>
        <button type="button" class="btn btn-primary" id="confirmYes" name="confirmYes"
                onclick="submitHiddenValue()"><i class="fas fa-check">&nbsp;Yes</i></button>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
  const tableContainerDiv = document.getElementById('table-container');
  tableContainerDiv.focus();

  function submitHiddenValue() {
    let hiddenValue = $('#id').val();
    let url = "${createLink(controller: 'adminDBoard', action: 'deletePersonType', params: [id: 'REPLACE_ME'])}";
    url = url.replace('REPLACE_ME', hiddenValue);
    window.location.href = url;
  }

  function isScrolledToBottom(el) {
    return el.scrollHeight - el.scrollTop - el.clientHeight <= 1;
  }

  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);

    function preventScrolling() {
      window.scrollTo(0, 0); // Scroll to the top left corner
    }

// Add an event listener to the window to prevent scrolling
    window.addEventListener('scroll', preventScrolling);

    tableContainerDiv.addEventListener('scroll', function () {
      if (isScrolledToBottom(tableContainerDiv)) {
        loadMoreData();
      }
    });


    $(document).on("click", ".btn-danger", function () {
      const personId = $(this).data('id');
      const fullName = $(this).data('href');
      $(".modal-body #id").val(personId);
      let fullNameParagraph = document.getElementById('fullName1')
      fullNameParagraph.textContent = fullName;

      $('#confirmationModal').modal('show');
    });

  });

  let offset = 10;
  let max = 10;
  let loading = false;
  let previousDataSetEmpty = false;

  function loadMoreData() {
    if (loading) return;

    loading = true;

    if (!previousDataSetEmpty) {
      $('#loading-indicator').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'adminDBoard', action: 'listMorePersonTypes')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, personType) {
              addRow(personType.id, personType.personTypeName)
            });
            offset = offset + max;
            $('#loading-indicator').hide();
            loading = false;
          } else {
            previousDataSetEmpty = true;
            $('#loading-indicator').hide();
          }
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator').hide();
        }
      });
    }
  }

  function addRow(personTypeId, personTypeName) {
    const newRow = document.createElement('tr');

    const personTypeCell = document.createElement('td');
    personTypeCell.textContent = personTypeName;

    newRow.appendChild(personTypeCell);

    const actionsCell = document.createElement('td');
    const toolbarDiv = document.createElement('div');
    toolbarDiv.id = "toolbar";
    toolbarDiv.className = "toolbar";

    const editIcon = document.createElement('i');
    editIcon.className = "fas fa-edit";

    const editAnchor = document.createElement('a');
    editAnchor.href = "/adminDBoard/editPersonType/" + personTypeId.toString();
    editAnchor.className = "btn btn-primary toolbar-button";
    editAnchor.role = "button";

    editAnchor.appendChild(editIcon);
    toolbarDiv.appendChild(editAnchor);

    const deleteIcon = document.createElement('i');
    deleteIcon.className = "fas fa-trash-alt";

    const deleteAnchor = document.createElement('a');
    deleteAnchor.href = "#";
    deleteAnchor.dataset.id = personTypeId;
    deleteAnchor.dataset.href = personTypeName;
    deleteAnchor.dataset.toggle = "modal";
    deleteAnchor.className = "btn btn-danger toolbar-button";
    deleteAnchor.role = "button";

    deleteAnchor.appendChild(deleteIcon);
    toolbarDiv.appendChild(deleteAnchor);

    actionsCell.appendChild(toolbarDiv);
    newRow.appendChild(actionsCell);

    // Add the new row to the table
    var table = document.getElementById('table');
    table.appendChild(newRow);
  }
</script>
</body>
</html>
