<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Select Ticket Type</h1>
    </div>
    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger"
           style="display: block">${flash.error}</div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success"
           style="display: block">${flash.message}</div>
    </g:if>
    <section class="row colset-2-its">
      <ul class="nav nav-tabs border-0" id="superAdminConsoleTabs" role="tablist">
        <li class="nav-item" role="presentation">
          <a href="#tab1" class="nav-link active fas fa-list border-0" id="tab1-tab" data-toggle="tab"
             role="tab" aria-controls="tab1" aria-selected="true">
          </a>
        </li>
        <li class="nav-item" role="presentation">
          <a href="#tab2" class="nav-link fas fa-table-cells-large border-0" id="tab2-tab" data-toggle="tab"
             role="tab" aria-controls="tab2" aria-selected="false">
          </a>
        </li>
      </ul>

      <div class="tab-content" id="myTabContent">
        <div class="tab-pane fade show active" id="tab1" role="tabpanel" aria-labelledby="tab1-tab">
          <div id="table-container" tabindex="-1" class="overflow-auto">
            <g:if test="${ticketTypes}">
              <table id="clickable-table">
                <thead>
                <tr>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody id="table-body">
                <g:each in="${ticketTypes}" var="ticketType">
                  <g:set var="paramValue" value="${ticketType.id}"/>
                  <tr>
                    <td>${ticketType.name}</td>
                    <td>${ticketType.description}</td>

                    <td><div id="toolbar">
                      <a
                        href="${createLink(controller: 'AdminDBoard', action: 'editTicketType', params: [tt: paramValue])}"
                        class="btn btn-primary" role="button"><i class="fas fa-edit"></i></a>
                      <a
                        href="#" data-id=${ticketType?.id} data-href="${ticketType?.name}"
                        data-toggle="modal"
                        class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>

                    </div></td>
                  </tr>
                </g:each>
                </tbody>
              </table>
            </g:if>
            <g:else>
              <table>
                <thead>
                <tr>
                  <th>Ticket Type</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td>No ticket types found.</td>
                  <td>Unable to edit ticket type !!</td>
                  <td><a
                    href="${createLink(controller: 'adminDBoard', action: 'addTicketType')}"
                    class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a></td>
                </tr>
                </tbody>
              </table>
            </g:else>
          </div>

          <div id="loading-indicator" style="display: none;">
            <img src="${resource(dir: 'images', file: '3D fading lines.gif')}" class="img_25x25"
                 alt="Spinner Image">
            &nbsp;Loading...
          </div>
        </div>


        <div class="tab-pane fade" id="tab2" role="tabpanel" aria-labelledby="tab2-tab">
          <div id="card-container" tabindex="-2" class="overflow-auto">
          <div class="flex-container" id="tab2-flex-container">
            <g:if test="${ticketTypes}">
              <g:each in="${ticketTypes}" var="ticketType">
                <g:set var="cardParamValue" value="${ticketType.id}"/>

                <div class="card flex-item border-0 card-width">

                  <img src="${resource(dir: 'images', file: 'ticketType.png')}" class="card-img-top"
                       alt="Ticket Type Image">

                  <div id="card-toolbar">
                    <a
                      href="${createLink(controller: 'AdminDBoard', action: 'editTicketType', params: [tt: paramValue])}"
                      class="btn btn-primary" role="button"><i class="fas fa-edit"></i></a>
                    <a
                      href="#" data-id=${ticketType?.id} data-href="${ticketType?.name}"
                      data-toggle="modal"
                      class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>
                  </div>

                  <div class="card-body">
                    <h5 class="card-title">Name: ${ticketType?.name}</h5>

                    <div>Description: ${ticketType?.description}</div>

                  </div>
                </div>
              </g:each>
              </div>
            </g:if>
            <g:else>
              <div class="card flex-item card-width">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="Profile Image">

                <div class="d-flex justify-content-end">
                  <a
                    href="${createLink(controller: 'AdminDBoard', action: 'addTicketType')}"
                    class="btn btn-primary" role="button"><i class="fas fa-add"></i></a>
                </div>

                <div class="card-body">
                  <h5 class="d-flex justify-content-center flex-padding">No ticket types found.</h5>

                  <div class="card-text d-flex justify-content-center">Unable to edit ticket type !!</div>
                </div>
              </div>
            </g:else>
          </div>
        </div>
      </div>

    </section>

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
            <input type="hidden" id="id" name="id" value="">

            <div class="same-line">Are you sure you want to delete <strong>"</strong></div>

            <div class="same-line"><strong id="fullName1"></strong></div>

            <div class="same-line"><strong>"</strong> ?</div>
          </div>

          <div class="modal-footer border-0">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><i class="fas fa-times">&nbsp;No</i>
            </button>
            <button type="button" class="btn btn-primary" id="confirmYes" name="confirmYes"
                    onclick="submitHiddenValue()"><i class="fas fa-check">&nbsp;Yes</i></button>
          </div>
        </div>
      </div>
    </div>

    <script type="text/javascript">
      const tableContainerDiv = document.getElementById('table-container');
      const cardContainerDiv = document.getElementById('card-container');

      function submitHiddenValue() {
        const hiddenValue = $('#id').val();

        let url = "${createLink(controller: 'adminDBoard', action: 'deleteTicketType', params: [id: 'REPLACE_ME'])}";
        url = url.replace('REPLACE_ME', hiddenValue);
        window.location.href = url;
      }

      function isScrolledToBottom(el) {
        return el.scrollHeight - el.scrollTop - el.clientHeight < 2;
      }

      document.addEventListener("DOMContentLoaded", function () {
        setTimeout(function () {
          $("#alert").hide();
        }, 6000);


        $('#superAdminConsoleTabs a').on('shown.bs.tab', function (e) {
          const target = $(e.target).attr('href');
          localStorage.setItem('selectedSACTicketTypeTab', target.substring(1));
        });

        const selectedTab = localStorage.getItem('selectedSACTicketTypeTab');

        if (selectedTab) {
          $('#superAdminConsoleTabs a[href="#' + selectedTab + '"]').tab('show');

          if (selectedTab === "tab2") {
            cardContainerDiv.focus();
          } else if (selectedTab === "tab1") {
            tableContainerDiv.focus();
          }
        } else {
          $('#selectedSACTicketTypeTab a[href="#tab1"]').tab('show');
          tableContainerDiv.focus();
        }

        function preventScrolling() {
          window.scrollTo(0, 0);
        }

        window.addEventListener('scroll', preventScrolling);

        tableContainerDiv.addEventListener('scroll', function () {
          if (isScrolledToBottom(tableContainerDiv)) {
            loadMoreData();
          }
        });

        cardContainerDiv.addEventListener('scroll', function () {
          if (isScrolledToBottom(cardContainerDiv)) {
            loadMoreData();
          }
        });


        $(document).on("click", ".btn-danger", function () {
          const ticketTypeId = $(this).data('id');
          const fullName = $(this).data('href');
          $(".modal-body #id").val(ticketTypeId);
          const fullNameParagraph = document.getElementById('fullName1')
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
            url: '${g.createLink(controller: 'adminDBoard', action: 'listMoreTicketTypes')}',
            contentType: 'application/json',
            dataType: 'json',
            data: {
              offset: offset,
              max: max
            },
            success: function (data) {
              if (data.length > 0) {
                $.each(data, function (index, ticketType) {
                  addRow(ticketType);
                  addCard(ticketType);
                });
                offset = offset + max;
                loading = false;
              } else {
                previousDataSetEmpty = true;
              }
              $('#loading-indicator').hide();
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

      function addRow(ticketType) {
        const newRow = document.createElement('tr');

        const ticketTypeFirstNameCell = document.createElement('td');
        ticketTypeFirstNameCell.textContent = ticketType?.name;
        newRow.appendChild(ticketTypeFirstNameCell);

        const ticketTypeDescriptionCell = document.createElement('td');
        ticketTypeDescriptionCell.textContent = ticketType?.description;
        newRow.appendChild(ticketTypeDescriptionCell);

        const actionsCell = document.createElement('td');

        createToolbar(ticketType.id, 'toolbar', ticketType, actionsCell);

        newRow.appendChild(actionsCell);

        // Add the new row to the table
        const table = document.getElementById('clickable-table');
        table.appendChild(newRow);
      }

      function addCard(ticketType) {
        const card = document.createElement('div');

        card.className = "card flex-item border-0 card-width";

        const ticketTypeImageSrc = "${resource(dir: 'images', file: 'ticketType.png')}";

        const ticketTypeImageElement = document.createElement('img');

        ticketTypeImageElement.src = ticketTypeImageSrc;
        ticketTypeImageElement.className = 'card-img-top';
        ticketTypeImageElement.alt = 'ticketType Image';
        card.appendChild(ticketTypeImageElement);

        createToolbar(ticketType.id, 'card-toolbar', ticketType, card);

        const cardBodyDiv = document.createElement('div');
        cardBodyDiv.className = 'card-body';

// Create the card-title h5 element
        const cardTitleH5 = document.createElement('h5');
        cardTitleH5.className = 'card-title';
        cardTitleH5.textContent = ticketType?.name;

// Append the h5 element to the div
        cardBodyDiv.appendChild(cardTitleH5);

        const ttNameDiv = document.createElement('div');
        ttNameDiv.textContent = 'Name: ' + ticketType?.name;
        cardBodyDiv.appendChild(ttNameDiv);

// Create and append the ticket type description
        const ttDescriptionDiv = document.createElement('div');
        ttDescriptionDiv.className = 'card-text';
        ttDescriptionDiv.textContent = 'Description: ' + ticketType?.description;
        cardBodyDiv.appendChild(ttDescriptionDiv);

        card.appendChild(cardBodyDiv);

        const cardFlexContainer = document.getElementById("tab2-flex-container");
        cardFlexContainer.appendChild(card);
      }

      function createToolbar(paramValue, toolbarId, ticketType, cardElement) {
        // Create the main div element
        const cardToolbar = document.createElement('div');
        cardToolbar.id = toolbarId;
        cardToolbar.className = toolbarId;

        // Create the edit button
        const editButton = document.createElement('a');

        const urlString = window.location.origin + "${createLink(controller: 'AdminDBoard', action: 'editTicketType')}";

        let editUrl = new URL(urlString);
        editUrl.searchParams.append('tt', paramValue);

        editButton.href = editUrl;
        editButton.className = 'btn btn-primary';
        editButton.setAttribute('role', 'button');

        const editIcon = document.createElement('i');
        editIcon.className = "fas fa-edit";
        editButton.append(editIcon);


        // Create the delete button
        const deleteButton = document.createElement('a');
        deleteButton.href = '#';
        deleteButton.dataset.id = ticketType?.id;
        deleteButton.dataset.href = `${ticketType?.name}`;
        deleteButton.setAttribute('data-toggle', 'modal');
        deleteButton.className = 'btn btn-danger toolbar-button';
        deleteButton.setAttribute('role', 'button');
        deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';

        // Append btn-group and delete button to the cardToolbar
        cardToolbar.appendChild(editButton);
        cardToolbar.appendChild(deleteButton);

        // Append the Toolbar to the parent element
        cardElement.appendChild(cardToolbar);
      }

    </script>
</body>
</html>
