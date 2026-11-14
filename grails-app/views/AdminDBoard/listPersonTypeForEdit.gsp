<!doctype html>
<html>
<head>
  <meta name="layout" content="bottomslidein"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"></script>
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
    <div class="form-container">
      <a href="${createLink(controller: 'AdminDBoard', action: 'index')}"
         class="same-line"><i class="fa fa-home embossed-link" aria-hidden="true"></i></a>
      <a href="${createLink(controller: 'AdminDBoard', action: 'listPerson')}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Select User Type</h1>

      <div class="same-line custom-icon d-flex justify-content-end">
        <i class="fas fa-info-circle" data-container="body"
           data-toggle="popover"
           data-html="true"
           title="${person?.firstName}&nbsp;${person?.lastName}"
           data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Selection Details</strong></div>
                </div>
                       <div class='row odd-row'>
                <div class='col'>User Name</div>
                <div class='col'>${person?.eMail}</div>
              </div>
              <div class='row even-row'>
                <div class='col'>User Type</div>
                <div class='col'>${pType?.personTypeName}</div>
              </div>
              <div class='row odd-row'>
                <div class='col'>Edit Mode</div>
           <g:if test="${params?.con == 'EditDBoard'}">
             <div class='col'>Dash Board</div>
           </g:if>
           <g:elseif test="${params?.con == 'person'}">
             <div class='col'>Wizard</div>
           </g:elseif>
           <g:else>
             <div class='col'>N/A</div>
           </g:else>
              </div>"></i>

      </div>
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
      <div id="table-container" tabindex="-1" class="overflow-auto">
        <table id="table">
          <thead>
          <tr>
            <th>User Type</th>
            <th>Status</th>
          </tr>
          </thead>
          <tbody id="table-body">
          <g:each in="${personTypes}" var="personType">
            <tr>
              <td>${personType?.personTypeName}</td>
              <td class="hidden-td">
                <input type="hidden" id="pTId" name="pTId" value= ${personType?.id}>
              </td>
              <td></td>
            </tr>
          </g:each>
          </tbody>
        </table>

      </div>

      <div id="loading-indicator" style="display: none;">
        <img src="${resource(dir: 'images', file: "3D fading lines.gif")}" class="img_25x25"
             alt="Loading Image">

        &nbsp;Loading...
      </div>

    </section>
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
    return el.scrollHeight - el.scrollTop - el.clientHeight < 2;
  }

  let counter = 0;
  let previousRow = null;
  let toggleFlag = false;
  let rowSelected = false;

  let urlParams = null;

  let offset = 10;
  let max = 10;
  let loading = false;
  let previousDataSetEmpty = false;
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

    const tableRows = document.querySelectorAll('tbody tr');

    urlParams = new URLSearchParams(window.location.search);

    const pType = ${pType?.id};

    tableRows.forEach(function (row) {
      let personTypeId = row.querySelector('input[type="hidden"]').value;
      if (Number(personTypeId) === pType) {
        drawTick(row);
        editRow(row);

        rowSelected = true;

        row.scrollIntoView({behavior: 'smooth', block: 'center'});
        return row;
      } else {
        editRow(row);
      }
    });

    if (!rowSelected) {
      loadMoreData();
    }
  });


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
              addRow(personType);
            });

            offset = offset + max;
            loading = false;

            if (!rowSelected) {
              loadMoreData();
            }
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

  function addRow(personType) {
    // Create a table row element
    const trElement = document.createElement('tr');

// Create the first table cell element
    const tdElement1 = document.createElement('td');
    tdElement1.textContent = personType?.personTypeName; // Replace with actual value or variable

// Create the second table cell element with a hidden input
    const tdElement2 = document.createElement('td');
    tdElement2.className = 'hidden-td';

    const inputElement = document.createElement('input');
    inputElement.type = "hidden";
    inputElement.id = "pTId";
    inputElement.name = "pTId";
    inputElement.value = personType?.id;

    tdElement2.appendChild(inputElement);

// Create the third table cell element (empty)
    const tdElement3 = document.createElement('td');

// Append all table cell elements to the table row
    trElement.appendChild(tdElement1);
    trElement.appendChild(tdElement2);
    trElement.appendChild(tdElement3);

// Now you can append the trElement to a table body or another element
// For example, if you have a table body element:
    const tableBodyElement = document.getElementById("table-body");
    tableBodyElement.appendChild(trElement);

    let personTypeId = trElement.querySelector('input[type="hidden"]').value;

    if (Number(personTypeId) === ${pType?.id}) {
      drawTick(trElement);
      rowSelected = true;

      trElement.scrollIntoView({behavior: 'smooth', block: 'center'});
    }
    editRow(trElement);
  }

  function editRow(row) {
    row.id = counter.toString();
    counter++;
    $(row).on('click', function () {
      toggleFlag = !toggleFlag;

      if (previousRow) {
        previousRow.removeChild(previousRow.cells[2]);

        const td = document.createElement('td');
        previousRow.appendChild(td);

        previousRow.cells[0].className = "";

        if (previousRow.id !== this.id) {
          toggleFlag = true;
        }
      }

      if (toggleFlag) {

        drawTick(row);

        let control = urlParams.get("con");

        let urlString = window.location.origin;
        if (control === 'sup') {
          urlString = urlString + "${createLink(controller: 'SuperAdmin', action: 'edit')}";
        } else {
          urlString = urlString + "${createLink(controller: 'EditDBoard', action: 'edit')}";
        }

        let editUrl = new URL(urlString);
        editUrl.searchParams.append('p', ${person?.id});
        editUrl.searchParams.append('con', control);
        editUrl.searchParams.append('op', ${pType?.id});
        editUrl.searchParams.append('pt', row.querySelector('input[type="hidden"]').value);

        window.location.href = editUrl;
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

    row.cells[2].appendChild(svg);
    row.cells[0].className = "selected-row";
    row.cells[2].className = "selected-row";

    previousRow = row;
  }
</script>
</body>
</html>
