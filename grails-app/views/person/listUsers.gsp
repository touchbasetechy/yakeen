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
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-square"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <div class="d-flex justify-content-center">
      <a href="${createLink(controller: 'person', action: 'index')}"
         class="same-line"><i class="fas fa-chevron-left" aria-hidden="true"></i></a>

      <h1 class="same-line">Select User</h1>
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
      <ul class="nav nav-tabs border-0" id="adminConsoleTabs" role="tablist">
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
            <g:if test='${persons}'>
              <table id="clickable-table">
                <thead>
                <tr>
                  <th>First Name</th>
                  <th>Last Name</th>
                  <th>User Name</th>
                  <th>Password</th>
                  <th>Gender</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>

                <g:each in="${persons}" var="person">
                  <g:set var="paramName" value="id"/>
                  <g:set var="paramValue" value="${person.id}"/>

                  <tr>
                    <td>${person?.firstName}</td>
                    <td>${person?.lastName}</td>
                    <td>${person?.eMail}</td>
                    <td>${person?.password}</td>
                    <td>
                      <g:if test="${person?.personDetails?.gender}">
                        ${person?.personDetails?.gender}
                      </g:if>
                      <g:else>N/A</g:else>
                    </td>
                    <td><div id="toolbar">

                      <div class="btn-group toolbar-button" role="group">
                        <a
                          href="${createLink(controller: 'person', action: 'edit', params: [(paramName): paramValue, con: 'sup'])}"
                          class="btn btn-primary" role="button"><i class="fas fa-hat-wizard"></i></a>
                        <button type="button" class="btn btn-primary dropdown-toggle dropdown-toggle-split"
                                data-bs-toggle="dropdown" aria-expanded="false">
                          <span class="visually-hidden">Toggle Dropdown</span>
                        </button>
                        <ul class="dropdown-menu">
                          <li><a class="dropdown-item"
                                 href="${createLink(controller: 'PersonEditDBoard', action: 'edit', params: [(paramName): paramValue, con: 'edb'])}"><i
                              class="fas fa-edit" aria-hidden="true"></i>DashBoard</a>
                          </li>
                          <li><a class="dropdown-item"
                                 href="${createLink(controller: 'person', action: 'edit', params: [(paramName): paramValue, con: 'sup'])}"><i
                              class="fas fa-hat-wizard"></i>Wizard</a>
                          </li>
                        </ul>
                      </div>
                      <a
                        href="#" data-id=${person?.id} data-href="${person?.firstName}&nbsp;${person?.lastName}"
                        data-toggle="modal"
                        class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>

                      <!-- Icon Button -->
                      <g:render template="popover" model="[person: person]"/>

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
                  <th>User</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
                </thead>
                <tbody>

                <tr>
                  <td>No users found.</td>
                  <td>Unable to edit users !!</td>
                  <td><a
                    href="${createLink(controller: 'person', action: 'addPerson')}"
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
            <g:if test='${persons}'>
              <g:each var="person" in="${persons}">
              %{--                  <g:set var="cardParamName" value="id"/>--}%
                <g:set var="cardParamValue" value="${person.id}"/>

                <div class="card border-0 flex-item card-width">

                  <g:if test="${person?.personDetails?.gender == 'Male' || !person?.personDetails}">
                    <img src="${resource(dir: 'images', file: 'Male-no-photo.png')}" class="card-img-top"
                         alt="Profile Image">
                  </g:if>
                  <g:elseif test="${person?.personDetails?.gender == 'Female'}">
                    <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                         alt="Profile Image">
                  </g:elseif>

                  <div id="card-toolbar">
                    <div class="btn-group" role="group">
                      <a
                        href="${createLink(controller: 'person', action: 'edit', params: [id: cardParamValue, con: 'sup'])}"
                        class="btn btn-primary" role="button"><i class="fas fa-hat-wizard"></i></a>
                      <button type="button" class="btn btn-primary dropdown-toggle dropdown-toggle-split"
                              data-bs-toggle="dropdown" aria-expanded="false">
                        <span class="visually-hidden">Toggle Dropdown</span>
                      </button>
                      <ul class="dropdown-menu">
                        <li><a class="dropdown-item"
                               href="${createLink(controller: 'PersonEditDBoard', action: 'edit', params: [id: cardParamValue, con: 'edb'])}"><i
                            class="fas fa-edit" aria-hidden="true"></i>DashBoard</a>
                        </li>
                        <li><a class="dropdown-item"
                               href="${createLink(controller: 'person', action: 'edit', params: [id: cardParamValue, con: 'sup'])}"><i
                            class="fas fa-hat-wizard"></i>Wizard</a>
                        </li>
                      </ul>
                    </div>
                    <a
                      href="#" data-id=${person?.id} data-href="${person?.firstName}&nbsp;${person?.lastName}"
                      data-toggle="modal"
                      class="btn btn-danger" role="button"><i class="fas fa-trash-alt"></i></a>

                    <!-- Icon Button -->
                    <g:render template="popover" model="[person: person]"/>
                  </div>

                  <div class="card-body">
                    <h5 class="card-title">${person?.firstName}&nbsp;${person?.lastName}</h5>

                    <div>User Name: ${person?.eMail}</div>
                    <g:if test="${person?.personDetails?.gender}">
                      <div class="card-text">Gender: ${person?.personDetails?.gender}</div>
                    </g:if>
                    <g:else class="card-text"><div>Gender: N/A</div></g:else>
                  </div>
                </div>
              </g:each>
            </g:if>
            <g:else>
              <div class="card flex-item card-width">
                <img src="${resource(dir: 'images', file: 'Female-no-photo.png')}" class="card-img-top"
                     alt="Profile Image">

                <div class="d-flex justify-content-end">
                  <a
                    href="${createLink(controller: 'person', action: 'addPerson')}"
                    class="btn btn-primary" role="button"><i class="fas fa-add"></i></a>
                </div>

                <div class="card-body">
                  <h5 class="d-flex justify-content-center flex-padding ">No users found.</h5>

                  <div class="card-text">Unable to edit users !!</div>
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
    let url = "${createLink(controller: 'person', action: 'delete', params: [id: 'REPLACE_ME'])}";
    url = url.replace('REPLACE_ME', hiddenValue);
    window.location.href = url;
  }


  var offset = 10;
  var max = 10;
  var loading = false;
  var previousDataSetEmpty = false;

  // function isScrolledToBottom(el) {
  //   return el.scrollHeight - el.scrollTop - el.clientHeight < 2;
  // }

  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);

    $('#adminConsoleTabs a').on('shown.bs.tab', function (e) {
      const target = $(e.target).attr('href');
      localStorage.setItem('selectedAdminConsoleTab', target.substring(1));
    });

    let selectedTab = localStorage.getItem('selectedAdminConsoleTab');

    if (selectedTab) {
      $('#adminConsoleTabs a[href="#' + selectedTab + '"]').tab('show');

      if (selectedTab === "tab2") {
        cardContainerDiv.focus();
      } else if (selectedTab === "tab1") {
        tableContainerDiv.focus();
      }
    } else {
      $('#adminConsoleTabs a[href="#tab1"]').tab('show');
      tableContainerDiv.focus();
    }

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
      const personId = $(this).data('id');
      const fullName = $(this).data('href');
      $(".modal-body #id").val(personId);
      const fullNameParagraph = document.getElementById('fullName1')
      fullNameParagraph.textContent = fullName;

      $('#confirmationModal').modal('show');
    });
  });


  function loadMoreData() {
    if (loading) return;

    loading = true;

    if (!previousDataSetEmpty) {
      $('#loading-indicator').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'person', action: 'listMorePersons')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, person) {
              addRow(person);
              addCard(person);
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


  function addRow(person) {
    const newRow = document.createElement('tr');

    const personFirstNameCell = document.createElement('td');
    personFirstNameCell.textContent = person?.firstName;
    newRow.appendChild(personFirstNameCell);

    const personLastNameCell = document.createElement('td');
    personLastNameCell.textContent = person?.lastName;
    newRow.appendChild(personLastNameCell);

    const personEmailCell = document.createElement('td');
    personEmailCell.textContent = person?.eMail;
    newRow.appendChild(personEmailCell);

    const personPasswordCell = document.createElement('td');
    personPasswordCell.textContent = person?.password;
    newRow.appendChild(personPasswordCell);

    const personGenderCell = document.createElement('td');
    if (person?.personDetails?.gender === 0) {
      personGenderCell.textContent = 'Male';
    } else if (person?.personDetails?.gender === 1) {
      personGenderCell.textContent = 'Female';
    } else {
      personGenderCell.textContent = 'N/A';
    }

    newRow.appendChild(personGenderCell);

    const actionsCell = document.createElement('td');

    createToolbar(person.id, 'toolbar', person, actionsCell);

    newRow.appendChild(actionsCell);

    // Add the new row to the table
    const table = document.getElementById('clickable-table');
    table.appendChild(newRow);
  }

  function addCard(person) {
    const card = document.createElement('div');

    card.className = "card flex-item border-0 card-width"

    if (person?.personDetails?.gender === 0 || !person?.personDetails) {
      const maleImageSrc = "${resource(dir: 'images', file: 'Male-no-photo.png')}";

      const maleImageElement = document.createElement('img');

      maleImageElement.src = maleImageSrc;
      maleImageElement.className = 'card-img-top';
      maleImageElement.alt = 'Profile Image';
      card.appendChild(maleImageElement);
    } else if (person?.personDetails?.gender === 1) {
      const femaleImageSrc = "${resource(dir: 'images', file: 'Female-no-photo.png')}";

      const femaleImageElement = document.createElement('img');

      femaleImageElement.src = femaleImageSrc;
      femaleImageElement.className = 'card-img-top';
      femaleImageElement.alt = 'Profile Image';
      card.appendChild(femaleImageElement);
    }

    createToolbar(person.id, 'card-toolbar', person, card);

    const cardBodyDiv = document.createElement('div');
    cardBodyDiv.className = 'card-body';

// Create the card-title h5 element
    const cardTitleH5 = document.createElement('h5');
    cardTitleH5.className = 'card-title';
    cardTitleH5.textContent = person?.firstName + ' ' + person?.lastName;

// Append the h5 element to the div
    cardBodyDiv.appendChild(cardTitleH5);

    const emailDiv = document.createElement('div');
    emailDiv.className = 'card-text';
    emailDiv.textContent = 'User Name: ' + person?.eMail;
    cardBodyDiv.appendChild(emailDiv);

// Create and append the gender div based on the condition
    if (person?.personDetails?.gender === 0) {
      const genderMaleDiv = document.createElement('div');
      genderMaleDiv.className = 'card-text';
      genderMaleDiv.textContent = 'Gender: Male';
      cardBodyDiv.appendChild(genderMaleDiv);
    } else if (person?.personDetails?.gender === 1) {
      const genderFemaleDiv = document.createElement('div');
      genderFemaleDiv.className = 'card-text';
      genderFemaleDiv.textContent = 'Gender: Female';
      cardBodyDiv.appendChild(genderFemaleDiv);
    } else {
      const genderDiv = document.createElement('div');
      genderDiv.className = 'card-text';
      genderDiv.textContent = 'Gender: N/A';
      cardBodyDiv.appendChild(genderDiv);
    }

    card.appendChild(cardBodyDiv);

    const cardFlexContainer = document.getElementById("tab2-flex-container");
    cardFlexContainer.appendChild(card);
  }

  function createToolbar(paramValue, toolbarId, person, cardElement) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    // Create the btn-group div
    const btnGroup = document.createElement('div');
    btnGroup.className = 'btn-group toolbar-button';
    btnGroup.setAttribute('role', 'group');

    // Create the edit button
    const editButton = document.createElement('a');

    const urlString = window.location.origin + "${createLink(controller: 'person', action: 'edit')}";

    let editUrl = new URL(urlString);

    editUrl.searchParams.append(id, paramValue);
    editUrl.searchParams.append('con', 'sup');

    editButton.href = editUrl;
    editButton.className = 'btn btn-primary';
    editButton.setAttribute('role', 'button');

    const wizardIcon = document.createElement('i');
    wizardIcon.className = "fas fa-hat-wizard";
    editButton.append(wizardIcon);

    // Create the dropdown toggle button
    const dropdownToggle = document.createElement('button');
    dropdownToggle.type = 'button';
    dropdownToggle.className = 'btn btn-primary dropdown-toggle dropdown-toggle-split';
    dropdownToggle.setAttribute('data-bs-toggle', 'dropdown');
    dropdownToggle.setAttribute('aria-expanded', 'false');
    dropdownToggle.innerHTML = '<span class="visually-hidden">Toggle Dropdown</span>';

    // Create the dropdown menu
    const dropdownMenu = document.createElement('ul');
    dropdownMenu.className = 'dropdown-menu';

    // Create the DashBoard menu item
    const dashboardItem = document.createElement('li');
    const dashboardLink = document.createElement('a');
    dashboardLink.className = 'dropdown-item';

    let dashBoardUrl = new URL(urlString);
    dashBoardUrl.searchParams.append(id, paramValue);
    dashBoardUrl.searchParams.append('con', 'edb');

    dashboardLink.href = dashBoardUrl;

    const dashBoardIcon = document.createElement('i');
    dashBoardIcon.className = "fas fa-edit";

    dashboardLink.textContent = 'DashBoard';
    dashboardItem.appendChild(dashboardLink);
    dashboardLink.insertBefore(dashBoardIcon, dashboardLink.firstChild);

    // Create the Wizard menu item
    const wizardItem = document.createElement('li');
    const wizardLink = document.createElement('a');
    wizardLink.className = 'dropdown-item';

    let wizardUrl = new URL(urlString);
    wizardUrl.searchParams.append(id, paramValue);
    wizardUrl.searchParams.append('con', 'sup');

    wizardLink.href = wizardUrl;

    const wizardIcon1 = document.createElement('i');
    wizardIcon1.className = "fas fa-hat-wizard";

    wizardLink.textContent = 'Wizard';
    wizardItem.appendChild(wizardLink);
    wizardLink.insertBefore(wizardIcon1, wizardLink.firstChild);

    // Append menu items to the dropdown menu
    dropdownMenu.appendChild(dashboardItem);
    dropdownMenu.appendChild(wizardItem);

    // Append buttons and dropdown menu to the btn-group
    btnGroup.appendChild(editButton);
    btnGroup.appendChild(dropdownToggle);
    btnGroup.appendChild(dropdownMenu);

    // Create the delete button
    const deleteButton = document.createElement('a');
    deleteButton.href = '#';
    deleteButton.dataset.id = person?.id;
    deleteButton.dataset.href = `${person?.firstName} ${person?.lastName}`;
    deleteButton.setAttribute('data-toggle', 'modal');
    deleteButton.className = 'btn btn-danger toolbar-button';
    deleteButton.setAttribute('role', 'button');
    deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i>';

    // Append btn-group and delete button to the cardToolbar
    cardToolbar.appendChild(btnGroup);
    cardToolbar.appendChild(deleteButton);

    const popOverIcon = document.createElement('i');
    popOverIcon.className = 'fas fa-info-circle custom-icon-small-margin';
    popOverIcon.setAttribute('data-container', 'body');
    popOverIcon.setAttribute('data-toggle', "popover");
    // popOverIcon.setAttribute('data-placement', "left");
    popOverIcon.setAttribute('data-bs-html', "true");


    //  popOverIcon.setAttribute('data-html', 'true');
    popOverIcon.setAttribute('title', person?.firstName + ' ' + person?.lastName);

    cardToolbar.appendChild(popOverIcon);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);

    let dob = "N/A";
    if (person?.personDetails?.dob) {
      dob = DateTimeExtensions.format(person?.personDetails?.dob, DateTimeExtensions.FormatStyle.FULL)
    }

    let gender = "N/A";
    if (person?.personDetails?.gender === 0) {
      gender = "Male";
    } else if (person?.personDetails?.gender === 1) {
      gender = "Female";
    }

    let religion = "N/A";
    if (person?.personDetails?.religion) {
      religion = person?.personDetails?.religion;
    }

    let caste = "N/A";
    if (person?.personDetails?.caste) {
      caste = person?.personDetails?.caste;
    }

    let smokingHabit = "N/A";
    if (person?.personDetails?.smokingHabit === 0) {
      smokingHabit = "Non Smoker";
    } else if (person?.personDetails?.smokingHabit === 1) {
      smokingHabit = "Normal Smoker";
    } else if (person?.personDetails?.smokingHabit === 2) {
      smokingHabit = "Heavy Smoker";
    } else if (person?.personDetails?.smokingHabit === 3) {
      smokingHabit = "Chain Smoker";
    }

    let drinkingHabit = "N/A";
    if (person?.personDetails?.drinkingHabit === 0) {
      drinkingHabit = "Non Drinker";
    } else if (person?.personDetails?.drinkingHabit === 1) {
      drinkingHabit = "Normal Drinker";
    } else if (person?.personDetails?.drinkingHabit === 2) {
      drinkingHabit = "Heavy Drinker";
    } else if (person?.personDetails?.drinkingHabit === 3) {
      drinkingHabit = "Alcoholic";
    }

    let spouseAge = "N/A";
    if (person?.spouseDetails?.minAge && person?.spouseDetails?.maxAge) {
      spouseAge = person?.spouseDetails?.minAge + " - " + person?.spouseDetails?.maxAge
    } else if (person?.spouseDetails?.minAge && !person?.spouseDetails?.maxAge) {
      spouseAge = person?.spouseDetails?.minAge;
    } else if (!person?.spouseDetails?.minAge && person?.spouseDetails?.maxAge) {
      spouseAge = person?.spouseDetails?.maxAge;
    }

    let spouseGender = "N/A";
    if (person?.spouseDetails?.gender === 0) {
      spouseGender = "Male";
    } else if (person?.spouseDetails?.gender === 1) {
      spouseGender = "Female";
    }

    let spouseReligion = "N/A";
    if (person?.spouseDetails?.religion) {
      spouseReligion = person?.spouseDetails?.religion;
    }

    let spouseCaste = "N/A";
    if (person?.spouseDetails?.caste) {
      spouseCaste = person?.spouseDetails?.caste;
    }

    let spouseSmokingHabit = "N/A";
    if (person?.spouseDetails?.smokingHabit === 0) {
      spouseSmokingHabit = "Non Smoker";
    } else if (person?.spouseDetails?.smokingHabit === 1) {
      spouseSmokingHabit = "Normal Smoker";
    } else if (person?.spouseDetails?.smokingHabit === 2) {
      spouseSmokingHabit = "Heavy Smoker";
    } else if (person?.spouseDetails?.smokingHabit === 3) {
      spouseSmokingHabit = "Chain Smoker";
    }

    let spouseDrinkingHabit = "N/A";
    if (person?.spouseDetails?.drinkingHabit === 0) {
      spouseDrinkingHabit = "Non Drinker";
    } else if (person?.spouseDetails?.drinkingHabit === 1) {
      spouseDrinkingHabit = "Normal Drinker";
    } else if (person?.spouseDetails?.drinkingHabit === 2) {
      spouseDrinkingHabit = "Heavy Drinker";
    } else if (person?.spouseDetails?.drinkingHabit === 3) {
      spouseDrinkingHabit = "Alcoholic";
    }

    $(popOverIcon).popover({
      content: "<div class='row even-row'><div class='col'><strong>General Details</strong></div></div> <div class='row odd-row'><div class='col'>User Name</div><div class='col'>"
        + person.eMail + "</div></div><div class='row even-row'><div class='col'>Password</div><div class='col'>"
        + person?.password + "</div></div><div class='row odd-row'><div class='col'>User Type</div><div class='col'>"
        + person?.personType?.personTypeName + "</div></div> <div class='row even-row'><div class='col'>&nbsp;</div></div><div class='row even-row'><div class='col'><strong>Personal Details</strong></div></div><div class='row odd-row'><div class='col'>Date of Birth</div><div class='col'>"
        + dob + "</div></div> <div class='row even-row'><div class='col'>Gender</div><div class='col'>"
        + gender + "</div></div><div class='row odd-row'><div class='col'>Religion</div><div class='col'>"
        + religion + "</div></div>  <div class='row even-row'><div class='col'>Caste</div><div class='col'>"
        + caste + "</div></div><div class='row odd-row'><div class='col'>Smoking Habit</div><div class='col'>"
        + smokingHabit + "</div></div><div class='row even-row'><div class='col'>Drinking Habit</div><div class='col'>"
        + drinkingHabit + "</div></div><div class='row even-row'><div class='col'>&nbsp;</div></div><div class='row even-row'><div class='col'><strong>Spouse Preferences</strong></div></div><div class='row odd-row'><div class='col'>Age</div><div class='col'>"
        + spouseAge + "</div></div><div class='row even-row'><div class='col'>Gender</div><div class='col'>"
        + spouseGender + "</div></div><div class='row odd-row'><div class='col'>Religion</div><div class='col'>"
        + spouseReligion + "</div></div>  <div class='row even-row'><div class='col'>Caste</div><div class='col'>"
        + spouseCaste + "</div></div><div class='row odd-row'><div class='col'>Smoking Habit</div><div class='col'>"
        + spouseSmokingHabit + "</div></div><div class='row even-row'><div class='col'>Drinking Habit</div><div class='col'>"
        + spouseDrinkingHabit + "</div></div>",
      placement: 'left'
    });
// $(popOverIcon).attr('data-html', 'true');
// $(popOverIcon).attr('data-bs-content',
//    );

    $(popOverIcon).on('mouseover', function () {
      $(this).popover('show');

    });

    $(popOverIcon).on('mouseout', function () {
      $(this).popover('hide');
    });

  }
</script>

<!-- Bootstrap JS -->
%{--<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--}%
%{--<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.10.2/dist/umd/popper.min.js"></script>--}%
%{--<script src="https://stackpath.bootstrapcdn.com/bootstrap/5.1.3/js/bootstrap.min.js"></script>--}%

</body>
</html>

