<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <!-- Bootstrap JS and dependencies -->
  <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
  %{--    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">--}%

  %{--  <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.2/dist/umd/popper.min.js"></script>--}%
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
  <style>
  /* Override the default modal backdrop to make it fully transparent */

  .modal-backdrop {
    background-color: transparent !important;
  }

  .modal-header, .modal-content {
    border-radius: 15px 0 0 15px; /* Top left and bottom left corners */
    border: 1px solid transparent;
  }

  .modal-content {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  }


  </style>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row">
      <a href="${createLink(controller: 'ticket', action: params?.ac, params: [t: params?.t])}"
         class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

      <h1 class="same-line form-container">Duplicate Ticket</h1>

      <div class="col-md-12"><strong>Ticket ID:</strong> ${ticket?.ticketId}</div>

      <div class="col-md-12"><strong>Ticket Type:</strong> ${ticket?.ticketType?.name}</div>

      <div class="col-md-12">( ${ticket?.ticketType?.description} )</div>

      <div class="col-md-12 margin-top "><strong>Subject:</strong> ${ticket?.subject}</div>

    </section>

    <g:if test="${(ticket?.approvedBy && ticket?.approvedAt)}">

      <form method="post"
            action="${createLink(controller: 'Ticket', action: 'markDuplicateTicket', params: [t: params?.t])}">
        <section class="row">
          <div class="col-md-12">
            <label for="message"><strong>Message</strong></label>
            <textarea id="message" class="form-control custom-textarea border-0 no-decoration"
                      readonly>${ticket?.message}</textarea>
          </div>
        </section>
        <section class="row">
          <div class="col-md-12">
            <label for="Reason"><strong>Reason for Duplication</strong></label>
            <textarea id="Reason" name="Reason" class="form-control custom-textarea border-0 no-decoration"
                      autofocus></textarea>
          </div>
        </section>
        <section class="row">
          <button type="button"
                  class="btn btn-primary btn-rounded d-flex justify-content-end"
                  id="openModalBtn">Compare</button>
        </section>

        <div class="row justify-content-center">
          <div class="col-md-6 text-center">
            <button type="submit" class="btn btn-secondary mb-2"><i class="fa-solid fa-clone"></i>Duplicate</button>
          </div>
        </div>
      </form>
    </g:if>
    <g:else>
      <g:render template="partialView"/>
      <div class="row justify-content-center">
      <div class="col-md-6 text-center">
        <a href="${createLink(controller: 'Ticket', action: 'approveTicketAndReturnToDuplicate', params: [t: params?.t])}"
       class="btn btn-primary mb-2 btn-text"><i class="fa fa-check" aria-hidden="true"></i>Approve</a>
    </g:else>

  </div>
  </div>

    <!-- Modal -->
    <div class="modal fade animate__animated animate__fadeInRight" id="myModal" tabindex="-1" role="dialog"
         aria-labelledby="myModalLabel">
      <div class="modal-dialog mr-0" role="document">
        <div class="modal-content">
          <div class="modal-header border-0">
            <h4 class="modal-title" id="myModalLabel">Compare Tickets</h4>

            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>

          <div class="modal-body border-0">
            <table class="table">
              <thead>
              <tr>
                <th>Ticket ID</th>
                <th>Ticket Type</th>
                <th>Subject</th>
                <th>Message</th>
              </tr>
              </thead>

              <div class="overflow-auto" id="comparableContainer">
                <tbody id="duplicateTableBody">
                <!-- Data will be inserted here -->
                </tbody>
              </div>
            </table>

            <div id="loading-indicator" style="display: none;">
              <img src="${resource(dir: 'images', file: '3D fading lines.gif')}" class="img_25x25"
                   alt="Spinner Image">
              &nbsp;Loading...
            </div>
          </div>

          <div class="modal-footer border-0">
          </div>
        </div>
      </div>
    </div>

    <script type="text/javascript">

      document.addEventListener('DOMContentLoaded', function () {

        // Button to open the modal
        $("#openModalBtn").click(function () {
          loadMoreComparisonData();
        });
      });

      let offset = 0;
      let max = 10;
      let loading = false;
      let previousDataSetEmpty = false;


      const tableContainerDiv = document.getElementById("comparableContainer");
      tableContainerDiv.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv)) {
          loadMoreComparisonData();
        }
      });

      function loadMoreComparisonData() {
        if (loading) return;

        loading = true;
        if (!previousDataSetEmpty) {
          $('#loading-indicator').show();
          $.ajax({
            url: "${createLink(controller: 'ticket', action: 'loadTicketsToCompare')}",
            method: "GET",
            contentType: 'application/json',
            dataType: 'json',
            data: {
              offset: offset,
              max: max
            },
            success: function (data) {

              if (data.length > 0) {
                let tableBody = document.getElementById('duplicateTableBody');
                tableBody.innerHTML = ''; // Clear existing rows

                // Iterate over the data and create table rows
                $.each(data, function (index, item) {
                  let row = document.createElement('tr');

                  // Create and append cells to the row
                  let ticketIdCell = document.createElement('td');
                  ticketIdCell.textContent = item?.ticketId;
                  row.appendChild(ticketIdCell);

                  let ticketTypeNameCell = document.createElement('td');
                  ticketTypeNameCell.textContent = item?.ticketTypeName;
                  row.appendChild(ticketTypeNameCell);

                  let subjectCell = document.createElement('td');
                  subjectCell.textContent = item?.subject;
                  row.appendChild(subjectCell);

                  let messageCell = document.createElement('td');
                  messageCell.textContent = item?.message;

                  row.appendChild(messageCell);

                  tableBody.appendChild(row);

                  row.addEventListener('click', function (event) {
                    var firstTd = row.children[0];
                    if (firstTd) {
                      let reason = document.getElementById("Reason");
                      reason.value = "This ticket is a duplicate of " + firstTd.textContent;

                      $('#myModal').modal('hide');
                    }
                  });
                });

                offset = offset + max;
                loading = false;

                // Show the modal
                $("#myModal").modal("show");
                tableContainerDiv.focus();
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
    </script>
    %{--    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">--}%

</body>
</html>
