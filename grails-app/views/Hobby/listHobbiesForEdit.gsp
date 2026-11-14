<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>
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
      <a href="${createLink(controller: 'adminDBoard', action: 'index')}"
         class="same-line"><i class="fas fa-chevron-left" aria-hidden="true"></i></a>

      <h1 class="form-container">Select Hobbies</h1>

      <g:if test="${flash.error}">
        <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger"
             style="display: block">${flash.error}</div>
      </g:if>
      <g:if test="${flash.message}">
        <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success"
             style="display: block">${flash.message}</div>
      </g:if>

      <ul class="nav nav-tabs border-0" id="hobbiesTabs" role="tablist">
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
            <g:if test='${hobbies}'>
              <table id="clickable-table">
                <thead>
                <tr>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>

                <g:each in="${hobbies}" var="hobby">
                  <g:set var="paramName" value="id"/>
                  <g:set var="paramValue" value="${hobby.id}"/>

                  <tr>
                    <td>${hobby?.name}</td>
                    <td>${hobby?.description}</td>

                    <td><div id="toolbar">
                      <a
                        href="${createLink(controller: 'Hobby', action: 'editHobby', params: [(paramName): paramValue])}"
                        class="btn btn-primary" role="button"><i class="fas fa-edit"></i></a>
                      <a
                        href="#" data-id=${paramValue} data-href="${hobby?.name}"
                        data-toggle="modal"
                        class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>
                    </div></td>
                  </tr>
                </g:each>

                </tbody>
              </table>
            </g:if>
            <g:else>
              <table id="clickable-table">
                <thead>
                <tr>
                  <th>Hobby</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>

                <tr>
                  <td>No hobbies found.</td>
                  <td>Unable to edit hobbies !!</td>
                  <td><a
                    href="${createLink(controller: 'Hobby', action: 'addHobby', params: [ac: 'l'])}"
                    class="btn btn-primary" role="button"><i class="fas fa-add"></i></a>
                  </td>
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
            <g:if test='${hobbies}'>
              <g:each var="hobby" in="${hobbies}">
              %{--                  <g:set var="cardParamName" value="id"/>--}%
                <g:set var="cardParamValue" value="${hobby?.id}"/>

                <div class="card flex-item card-width">

                  <img src="${resource(dir: 'images', file: 'hobbies.svg')}" class="card-img-top"
                       alt="Profile Image">


                  <div id="card-toolbar">
                    <a
                      href="${createLink(controller: 'Hobby', action: 'editHobby', params: [id: cardParamValue])}"
                      class="btn btn-primary"><i
                      class="fas fa-edit" aria-hidden="true"></i></a>
                    <a
                      href="#" data-id=${cardParamValue} data-href="${hobby?.name}"
                      data-toggle="modal"
                      class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>

                  </div>

                  <div class="card-body">
                    <h5 class="card-title">${hobby?.name}</h5>

                    <div>Description: ${hobby?.description}</div>
                  </div>
                </div>
              </g:each>
            </g:if>
            <g:else>
              <div class="card flex-item card-width">
                <img src="${resource(dir: 'images', file: 'hobbies.svg')}" class="card-img-top"
                     alt="Hobby Image">

                <div class="d-flex justify-content-end">
                  <a
                    href="${createLink(controller: 'Hobby', action: 'addHobby', params: [ac: 'l'])}"
                    class="btn btn-primary" role="button"><i class="fas fa-add"></i></a>
                </div>

                <div class="card-body">
                  <h5 class="d-flex justify-content-center flex-padding ">No hobbies found.</h5>

                  <div class="card-text">Unable to edit hobbies !!</div>
                </div>
              </div>
              </div>
            </g:else>
          </div>
        </div>
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
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
          aria-hidden="true">&times;</span>
        </button>
      </div>

      <div class="modal-body border-0">
        <input type="hidden" id="id" name="id">

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
    let url = "${createLink(controller: 'Hobby', action: 'deleteHobby', params: [id: 'REPLACE_ME'])}";
    url = url.replace('REPLACE_ME', hiddenValue);
    window.location.href = url;
  }

  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);

    $('#hobbiesTabs a').on('shown.bs.tab', function (e) {
      const target = $(e.target).attr('href');
      localStorage.setItem('selectedHobbiesTab', target.substring(1));
    });

    let selectedTab = localStorage.getItem('selectedHobbiesTab');

    if (selectedTab) {
      $('#hobbiesTabs a[href="#' + selectedTab + '"]').tab('show');

      if (selectedTab === "tab2") {
        cardContainerDiv.focus();
      } else if (selectedTab === "tab1") {
        tableContainerDiv.focus();
      }
    } else {
      $('#hobbiesTabs a[href="#tab1"]').tab('show');
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
      const hobbyId = $(this).data('id');
      const fullName = $(this).data('href');
      $(".modal-body #id").val(hobbyId);
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
        url: '${g.createLink(controller: 'Hobby', action: 'listMoreHobbies')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, hobby) {
              addRow(hobby);
              addCard(hobby);
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


  function addRow(hobby) {
    const newRow = document.createElement('tr');

    const nameCell = document.createElement('td');
    nameCell.textContent = hobby?.name;
    newRow.appendChild(nameCell);

    const descriptionCell = document.createElement('td');
    descriptionCell.textContent = hobby?.description;
    newRow.appendChild(descriptionCell);

    const actionsCell = document.createElement('td');

    createToolbar(hobby.id, 'toolbar', hobby, actionsCell);

    newRow.appendChild(actionsCell);

    // Add the new row to the table
    const table = document.getElementById('clickable-table');
    table.appendChild(newRow);
  }

  function addCard(hobby) {
    const card = document.createElement('div');

    card.className = "card flex-item border-0 card-width"

    const hobbyImageSrc = "${resource(dir: 'images', file: 'hobbies.svg')}";

    const hobbyImageElement = document.createElement('img');

    hobbyImageElement.src = hobbyImageSrc;
    hobbyImageElement.className = 'card-img-top';
    hobbyImageElement.alt = 'Hobby Image';
    card.appendChild(hobbyImageElement);

    createToolbar(hobby.id, 'card-toolbar', hobby, card);

    const cardBodyDiv = document.createElement('div');
    cardBodyDiv.className = 'card-body';

// Create the card-title h5 element
    const cardTitleH5 = document.createElement('h5');
    cardTitleH5.className = 'card-title';
    cardTitleH5.textContent = hobby?.name;

// Append the h5 element to the div
    cardBodyDiv.appendChild(cardTitleH5);

    const descriptionDiv = document.createElement('div');
    descriptionDiv.className = 'card-text';
    descriptionDiv.textContent = 'Description: ' + hobby?.description;
    cardBodyDiv.appendChild(descriptionDiv);

    card.appendChild(cardBodyDiv);

    const cardFlexContainer = document.getElementById("tab2-flex-container");
    cardFlexContainer.appendChild(card);
  }

  function createToolbar(paramValue, toolbarId, hobby, cardElement) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    // Create the edit button
    const editButton = document.createElement('a');
    const urlString = window.location.origin + "${createLink(controller: 'Hobby', action: 'editHobby')}";

    let editUrl = new URL(urlString);
    editUrl.searchParams.append(id, paramValue);
    editUrl.searchParams.append('con', 'sup');

    editButton.href = editUrl;
    editButton.className = 'btn btn-primary';
    editButton.setAttribute('role', 'button');

    const editIcon = document.createElement('i');
    editIcon.className = "fas fa-edit";
    editButton.append(editIcon);


    // Create the delete button
    const deleteButton = document.createElement('a');
    deleteButton.href = '#';
    deleteButton.dataset.id = hobby?.id;
    deleteButton.dataset.href = `${hobby?.name}`;
    deleteButton.setAttribute('data-toggle', 'modal');
    deleteButton.className = 'btn btn-danger toolbar-button';
    deleteButton.setAttribute('role', 'button');
    deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';

    // Append btn-group and delete button to the cardToolbar
    cardToolbar.appendChild(editButton);
    cardToolbar.appendChild(deleteButton);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);

    // let dob = "N/A";
    // if (person?.personDetails?.dob) {
    //   dob = DateTimeExtensions.format(person?.personDetails?.dob, DateTimeExtensions.FormatStyle.FULL)
    // }
  }
</script>

<!-- Bootstrap JS -->
%{--<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--}%
%{--<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js"></script>--}%
%{--<script src="https://stackpath.bootstrapcdn.com/bootstrap/5.1.3/js/bootstrap.min.js"></script>--}%

</body>
</html>

