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
    <section class="row">
      <g:if test='${persons}'>
        <table id="clickable-table">
          <thead>
          <tr>
            <th>First Name</th>
            <th>Last Name</th>
            <th>User Name</th>
            <th>Password</th>
            <th>Gender</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody>

          <g:each in="${persons}" var="person">
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
              <td class="d-none">
                <input type="hidden" id="personId" value="${person?.id}"/>
              </td>
              <td></td>
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

      <div id="loading-indicator" style="display: none;">
        <img src="${resource(dir: 'images', file: '3D fading lines.gif')}" class="img_25x25"
             alt="Spinner Image">
        &nbsp;Loading...
      </div>
  </div>
</section>
</div>
</div>

<script type="text/javascript">
  const tableContainerDiv = document.getElementById('table-container');

  var offset = 10;
  var max = 10;
  var loading = false;
  var previousDataSetEmpty = false;

  let counter = 0;
  let previousRow = null;
  let toggleFlag = false;
  let rowSelected = false;

  let queryString = null;
  let urlParams = null;

  document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
      $("#alert").hide();
    }, 6000);

    const tableRows = document.querySelectorAll('tbody tr');
    tableRows.forEach(function (row) {
      editRow(row);
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

    newRow.appendChild(actionsCell);

    // Add the new row to the table
    const table = document.getElementById('clickable-table');
    table.appendChild(newRow);
  }

  function editRow(row) {
    row.id = counter.toString();
    counter++;

    $(row).on('click', function () {
      toggleFlag = !toggleFlag;

      if (previousRow) {
        if (previousRow.id !== this.id) {
          toggleFlag = true;
        }
      }

      if (toggleFlag) {

        drawTick(row);

        let url = "${createLink(controller: 'person', action: 'updatePolicies', params: [id: 'REPLACE_ME'])}";
        url = url.replace('REPLACE_ME', row.querySelector('input[type="hidden"]').value);

        window.location.href = url;
      }
    });
  }

  function drawTick(row) {
    // Create the SVG element
    const svgNS = "http://www.w3.org/2000/svg";
    const svg = document.createElementNS(svgNS, "svg");
    svg.setAttribute("viewBox", "0 0 75.2 75.2");
    svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");

// Create the circle element
    const circle = document.createElementNS(svgNS, "circle");
    circle.setAttribute("class", "path circle");
    circle.setAttribute("fill", "none");
    circle.setAttribute("stroke", "#73AF55");
    circle.setAttribute("stroke-width", "3");
    circle.setAttribute("stroke-miterlimit", "10");
    circle.setAttribute("cx", "35.1");
    circle.setAttribute("cy", "35.1");
    circle.setAttribute("r", "30.0");

// Create the polyline element
    const polyline = document.createElementNS(svgNS, "polyline");
    polyline.setAttribute("class", "path check");
    polyline.setAttribute("fill", "none");
    polyline.setAttribute("stroke", "#73AF55");
    polyline.setAttribute("stroke-width", "3");
    polyline.setAttribute("stroke-linecap", "round");
    polyline.setAttribute("stroke-miterlimit", "10");
    //  polyline.setAttribute("points", "100.2,40.2 51.5,88.8 29.8,67.5");
    polyline.setAttribute("points", "55,25 50,30 30,50 20,40");

// Append circle and polyline to the SVG
    svg.appendChild(circle);
    svg.appendChild(polyline);


    row.cells[6].appendChild(svg);
    row.cells[6].className = "selected-row";
    row.cells[0].className = "selected-row";
    row.cells[1].className = "selected-row";
    row.cells[2].className = "selected-row";
    row.cells[3].className = "selected-row";
    row.cells[4].className = "selected-row";
    previousRow = row;
  }
</script>
</body>
</html>

