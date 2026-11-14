<!doctype html>
<html>
<head>
  <meta name="layout" content="topslidein"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>

  %{--  <link rel="stylesheet" href="${resource(dir: 'assets/css', file: 'normalize.css')}"/>--}%
  <link rel="stylesheet" href="${resource(dir: 'assets/css', file: 'demo.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'assets/css', file: 'tabs.css')}"/>
  <link rel="stylesheet" href="${resource(dir: 'assets/css', file: 'tabstyles.css')}"/>

  <asset:javascript src="modernizr.custom.js"/>
  <asset:javascript src="cbpFWTabs.js"/>
</head>

<body>

<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-success fixed-size-badge"><i class="fas fa-square"><span
      class="label-text">Super Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div class="container">
  <header>
    <a href="${createLink(controller: 'adminDBoard', action: 'index')}"
       class="same-line"><i class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

    <h1 class="same-line">Manage Tickets</h1>
  </header>
  <g:if test="${flash.error}">
    <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-danger"
         style="display: block">${flash.error}</div>
  </g:if>
  <g:if test="${flash.message}">
    <div id="alert" class="animate__animated  animate__fadeIn animate__delay-0s alert alert-success"
         style="display: block">${flash.message}</div>
  </g:if>
  <svg class="hidden">
    <defs>
      <path id="tabshape" d="M80,60C34,53.5,64.417,0,0,0v60H80z"/>
    </defs>
  </svg>
  <section>
    <div class="tabs tabs-style-shape">
      <nav>
        <ul class="" id="manageTicketConsoleTabs" role="tablist">
          <li class="" role="presentation">
            <a href="#approvalsPendingTab" class="" id="manageTab-tab1"
               data-toggle="tab"
               role="tab" aria-controls="approvalsPendingTab" aria-selected="true">
              <svg viewBox="0 0 80 60" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>UnApproved</span>
            </a>
          </li>
          <li class="" role="presentation">
            <a href="#approvedTab" class="" id="manageTab-tab2" data-toggle="tab"
               role="tab" aria-controls="approvedTab" aria-selected="false">
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>Approved</span>
            </a>
          </li>
          <li class="" role="presentation">
            <a href="#onHoldTab" class="" id="manageTab-tab3" data-toggle="tab"
               role="tab" aria-controls="onHoldTab" aria-selected="false">
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>On Hold</span>
            </a>
          </li>
          <li class="" role="presentation">
            <a href="#revokedTab" class="" id="manageTab-tab4" data-toggle="tab"
               role="tab" aria-controls="revokedTab" aria-selected="false">
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>Restored</span>
            </a>
          </li>
          <li class="" role="presentation">
            <a href="#duplicatedTab" class="" id="manageTab-tab5" data-toggle="tab"
               role="tab" aria-controls="duplicatedTab" aria-selected="false">
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>Duplicated</span>
            </a>
          </li>
          <li class="" role="presentation">
            <a href="#closedTab" class="" id="manageTab-tab6" data-toggle="tab"
               role="tab" aria-controls="closedTab" aria-selected="false">
              <svg viewBox="0 0 80 59" preserveAspectRatio="none"><use xlink:href="#tabshape"></use></svg>
              <span>Closed</span>
            </a>
          </li>
        </ul>
      </nav>

      <div class="content-wrap" id="manageTabContent">
        <section class="" id="approvalsPendingTab"
                 role="tabpanel"
                 aria-labelledby="manageTab-tab1">
          <g:render template="approvalsPendingTab" model="[approvalPendingTickets: approvalPendingTickets]"/>
        </section>

        <section class="" role="tabpanel"
                 aria-labelledby="manageTab-tab2">
          <g:render template="approvedTab" model="[approvedTickets: approvedTickets]"/>
        </section>

        <section class="" role="tabpanel"
                 aria-labelledby="manageTab-tab3">
          <g:render template="onHoldTab" model="[onHoldTickets: onHoldTickets]"/>
        </section>

        <section class="" id="revokedTab" role="tabpanel"
                 aria-labelledby="manageTab-tab4">
          <g:render template="revokedTab" model="[revokedTickets: revokedTickets]"/>
        </section>

        <section class="" id="duplicatedTab" role="tabpanel"
                 aria-labelledby="manageTab-tab5">
          <g:render template="duplicatedTab" model="[duplicatedTickets: duplicatedTickets]"/>
        </section>

        <section class="" id="closedTab" role="tabpanel"
                 aria-labelledby="manageTab-tab6">
          <g:render template="closedTab" model="[closedTickets: closedTickets]"/>
        </section>
      </div>
    </div>
  </section>
</div>



<script type="text/javascript">
  (function () {

    [].slice.call(document.querySelectorAll('.tabs')).forEach(function (el) {
      new CBPFWTabs(el);
    });

  })();

  function isScrolledToBottom(el) {
    return el.scrollHeight - el.scrollTop - el.clientHeight < 2;
  }

  document.addEventListener("DOMContentLoaded", function () {
      setTimeout(function () {
        $("#alert").hide();
      }, 6000);

      const baseUrl = window.location.origin;


// Store selected parent tab
      $('#manageTicketConsoleTabs a').on('shown.bs.tab', function (e) {
        const target = $(e.target).attr('href');
        localStorage.setItem('selectedManageTicketTab', target.substring(1));
      });

      const selectedTab = localStorage.getItem('selectedManageTicketTab');
      if (selectedTab) {
        $('#manageTicketConsoleTabs a[href="#' + selectedTab + '"]').tab('show');
      } else {
        $('#manageTicketConsoleTabs a[href="#approvalsPendingTab"]').tab('show');
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

      const tableContainerDiv = document.getElementById('table-container11');
      const cardContainerDiv = document.getElementById('card-container12');

      const tableContainerDiv21 = document.getElementById('table-container21');
      const cardContainerDiv22 = document.getElementById('card-container22');

      const tableContainerDiv31 = document.getElementById('table-container31');
      const cardContainerDiv32 = document.getElementById('card-container32');

      const tableContainerDiv41 = document.getElementById('table-container41');
      const cardContainerDiv42 = document.getElementById('card-container42');

      const tableContainerDiv51 = document.getElementById('table-container51');
      const cardContainerDiv52 = document.getElementById('card-container52');

      const tableContainerDiv61 = document.getElementById('table-container61');
      const cardContainerDiv62 = document.getElementById('card-container62');

      handleTabSelection('#approvalsPendingTabChild', 'selectedApprovalsPendingTab', cardContainerDiv, tableContainerDiv);
      handleTabSelection('#approvedTabChild', 'selectedApprovedTab', cardContainerDiv22, tableContainerDiv21);
      handleTabSelection('#onHoldTabChild', 'selectedOnHoldTab', cardContainerDiv32, tableContainerDiv31);
      handleTabSelection('#revokedTabChild', 'selectedRevokedTab', cardContainerDiv42, tableContainerDiv41);
      handleTabSelection('#duplicatedTabChild', 'selectedDuplicatedTab', cardContainerDiv52, tableContainerDiv51);
      handleTabSelection('#closedTabChild', 'selectedClosedTab', cardContainerDiv62, tableContainerDiv61);
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

      tableContainerDiv21.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv21)) {
          loadMoreData11();
        }
      });

      cardContainerDiv22.addEventListener('scroll', function () {
        if (isScrolledToBottom(cardContainerDiv22)) {
          loadMoreData11();
        }
      });

      tableContainerDiv31.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv31)) {
          loadMoreData21();
        }
      });

      cardContainerDiv32.addEventListener('scroll', function () {
        if (isScrolledToBottom(cardContainerDiv32)) {
          loadMoreData21();
        }
      });

      tableContainerDiv41.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv41)) {
          loadMoreData31();
        }
      });

      cardContainerDiv42.addEventListener('scroll', function () {
        if (isScrolledToBottom(cardContainerDiv42)) {
          loadMoreData31();
        }
      });

      tableContainerDiv51.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv51)) {
          loadMoreData41();
        }
      });

      cardContainerDiv52.addEventListener('scroll', function () {
        if (isScrolledToBottom(cardContainerDiv52)) {
          loadMoreData41();
        }
      });

      tableContainerDiv61.addEventListener('scroll', function () {
        if (isScrolledToBottom(tableContainerDiv61)) {
          loadMoreData51();
        }
      });

      cardContainerDiv62.addEventListener('scroll', function () {
        if (isScrolledToBottom(cardContainerDiv62)) {
          loadMoreData51();
        }
      });

      var pullStartY = null;
      var pullDistance = 25; // Minimum distance to trigger refresh
      document.addEventListener('touchstart', function (e) {
        pullStartY = e.touches[0].pageY;
        console.log("hello refresh!!");
      });
      document.addEventListener('touchmove', function (e) {
        if (pullStartY === null) return;
        var currentY = e.touches[0].pageY;
        var distance = currentY - pullStartY;
        if (distance > 0 && distance >= pullDistance) {
          // Trigger refresh
          pullStartY = null;
          refreshContent();
        }
      });

      function refreshContent() {
        // Send AJAX request to refresh content
        $.ajax({
          url: '${g.createLink(controller: 'Ticket', action: 'refreshClosedTicketsPage')}',
          method: 'GET',
          success: function (data) {
            // Update the content on the page
            //$('#content').html(data);
            console.log("Pulled to refresh!!" + data);
          }, error: function () {
            console.error('Failed to refresh content');
          }
        });
      }
    }
  );

  let offset = 10;
  let max = 10;
  let loading = false;
  let previousDataSetEmpty = false;

  function loadMoreData() {
    if (loading) return;

    loading = true;

    if (!previousDataSetEmpty) {
      $('#loading-indicator1').show();
      let firstUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewTicket')}";
      let secondUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'listTicketTypeForAddToManageTickets')}";
      let thirdUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";


      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreApprovalPendingTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell = document.createElement('td');
              const card = getCardFirstHalf();

              addRow(ticket, actionsCell, () => createToolbar('toolbar11', ticket, actionsCell, 'Pending Approval', firstUrlString, 'btn btn-primary toolbar-button', 'fa-solid fa-eye', secondUrlString, 'btn btn-primary toolbar-button', 'fas fa-add', thirdUrlString, 'btn btn-danger toolbar-button', 'fas fa-trash-alt'), "clickable-table", 'id', ticket?.id, null, null, 't', ticket?.id, 'ac', "listTicketToManage");
              addCard(ticket, card, () => createToolbar('card-toolbar12', ticket, card, 'Pending Approval', firstUrlString, 'btn btn-primary toolbar-button', 'fa-solid fa-eye', secondUrlString, 'btn btn-primary toolbar-button', 'fas fa-add', thirdUrlString, 'btn btn-danger toolbar-button', 'fas fa-trash-alt'), "tab2-flex-container", 'id', ticket?.id, null, null, 't', ticket?.id, 'ac', "listTicketToManage");

            });
            offset = offset + max;
            loading = false;
          } else {
            previousDataSetEmpty = true;
          }
          $('#loading-indicator1').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator1').hide();
        }
      });
    }
  }

  function addRow(ticket, actionsCell, createToolBar, tableName) {
    const newRow = document.createElement('tr');

    const ticketIdCell = document.createElement('td');
    ticketIdCell.textContent = ticket?.ticketId;
    newRow.appendChild(ticketIdCell);

    const ticketSubjectCell = document.createElement('td');
    ticketSubjectCell.textContent = ticket?.subject;
    newRow.appendChild(ticketSubjectCell);

    const ticketMessageCell = document.createElement('td');
    ticketMessageCell.textContent = ticket?.message;
    newRow.appendChild(ticketMessageCell);

    // const actionsCell = document.createElement('td');

    createToolBar();

    newRow.appendChild(actionsCell);

    // Add the new row to the table
    const table = document.getElementById(tableName);
    table.appendChild(newRow);
  }

  function addCard(ticket, card, createToolBar, gridName) {


    createToolBar();

    const cardBodyDiv = document.createElement('div');
    cardBodyDiv.className = 'card-body';

// Create the card-title h5 element
    const cardTitleH5 = document.createElement('h5');
    cardTitleH5.className = 'card-title';
    cardTitleH5.textContent = ticket?.ticketId;

// Append the h5 element to the div
    cardBodyDiv.appendChild(cardTitleH5);

    const subjectDiv = document.createElement('div');
    subjectDiv.textContent = 'Subject: ' + ticket?.subject;
    cardBodyDiv.appendChild(subjectDiv);

    const messageDiv = document.createElement('div');
    messageDiv.textContent = 'Message: ' + ticket?.message;
    cardBodyDiv.appendChild(messageDiv);

    card.appendChild(cardBodyDiv);

    const cardFlexContainer = document.getElementById(gridName);
    cardFlexContainer.appendChild(card);
  }

  function getCardFirstHalf() {
    const card = document.createElement('div');

    card.className = "card flex-item border-0 card-width"

    const ticketImageSrc = "${resource(dir: 'images', file: 'ticket.svg')}";

    const ticketImageElement = document.createElement('img');

    ticketImageElement.src = ticketImageSrc;
    ticketImageElement.className = 'card-img-top';
    ticketImageElement.alt = 'ticket Image';
    card.appendChild(ticketImageElement);

    return card;
  }

  function createToolbar(toolbarId, ticket, cardElement, status, firstUrlString, buttonClass1, buttonIcon1, secondUrlString, buttonClass2, buttonIcon2, thirdUrlString, buttonClass3, buttonIcon3, firstSearchParam, firstSearchParamValue, secondSearchParam, secondSearchParamValue, thirdSearchParam, thirdSearchParamValue, additionalSearchParam, additionalSearchParamValue) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    const firstUrl = new URL(firstUrlString);
    const secondUrl = new URL(secondUrlString);
    const thirdUrl = new URL(thirdUrlString);

    firstUrl.searchParams.append(firstSearchParam, firstSearchParamValue);
    secondUrl.searchParams.append(secondSearchParam, secondSearchParamValue);
    thirdUrl.searchParams.append(thirdSearchParam, thirdSearchParamValue);

    firstUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);
    secondUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);
    thirdUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);

    const firstButton = createButton(firstUrl, buttonClass1, buttonIcon1);
    cardToolbar.appendChild(firstButton);

    const secondButton = createButton(secondUrl, buttonClass2, buttonIcon2);
    cardToolbar.appendChild(secondButton);

    const thirdButton = createButton(thirdUrl, buttonClass3, buttonIcon3);
    cardToolbar.appendChild(thirdButton);

    const popOverIcon = getPopOverIcon(ticket);

    cardToolbar.appendChild(popOverIcon);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);


    toolbarLastPart(popOverIcon, ticket, status);

  }

  function getPopOverIcon(ticket) {
    const popOverIcon = document.createElement('i');
    popOverIcon.className = 'fas fa-info-circle custom-icon-small-margin';
    popOverIcon.setAttribute('data-container', 'body');
    popOverIcon.setAttribute('data-toggle', "popover");
    popOverIcon.setAttribute('data-bs-html', "true");
    popOverIcon.setAttribute('title', ticket?.ticketId);

    return popOverIcon;
  }

  function toolbarLastPart(popOverIcon, ticket, status) {

    $(popOverIcon).popover({
      content: "<div class='row even-row'><div class='col'><strong>Ticket Details</strong></div></div> <div class='row odd-row'><div class='col'>Subject</div><div class='col'>"
        + ticket?.subject + "</div></div><div class='row even-row'><div class='col'>Message</div><div class='col'>"
        + ticket?.message + "</div></div><div class='row odd-row'><div class='col'>Status</div><div class='col'>" + status + "</div></div>",
      placement: 'left'
    });

    $(popOverIcon).on('mouseover', function () {
      $(this).popover('show');

    });

    $(popOverIcon).on('mouseout', function () {
      $(this).popover('hide');
    });

  }

  let offset11 = 10;
  let loading11 = false;
  let previousDataSetEmpty11 = false;

  function loadMoreData11() {
    if (loading11) return;

    loading11 = true;

    if (!previousDataSetEmpty11) {
      $('#loading-indicator2').show();
      let fourthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndHoldTicket')}";
      let fifthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket')}";
      let sixthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndCloseTicket')}";
      let seventhUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreApprovedTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset11,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell_11 = document.createElement('td');
              const card_11 = getCardFirstHalf();
              addRow(ticket, actionsCell_11, () => createToolbar11('toolbar21', ticket, actionsCell_11, 'Approved', fourthUrlString, 'btn btn-warning toolbar-button', 'fa-solid fa-pause', fifthUrlString, 'btn btn-secondary toolbar-button', 'fas fa-clone', sixthUrlString, "btn btn-success toolbar-button", 'fas fa-close', seventhUrlString, "btn btn-danger toolbar-button", "fa fa-trash"), "clickable-table2");
              addCard(ticket, card_11, () => createToolbar11('card-toolbar22', ticket, card_11, 'Approved', fourthUrlString, 'btn btn-warning toolbar-button', 'fa-solid fa-pause', fifthUrlString, 'btn btn-secondary toolbar-button', 'fas fa-clone', sixthUrlString, "btn btn-success toolbar-button", 'fas fa-close', seventhUrlString, "btn btn-danger toolbar-button", "fa fa-trash"), "tab22-flex-container");
            });
            offset11 = offset11 + max;
            loading11 = false;
          } else {
            previousDataSetEmpty11 = true;
          }
          $('#loading-indicator2').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator2').hide();
        }
      });
    }
  }

  function createToolbar11(toolbarId, ticket, cardElement, status, fourthUrlString, buttonClass4, buttonIcon4, fifthUrlString, buttonClass5, buttonIcon5, sixthUrlString, buttonClass6, buttonIcon6, seventhUrlString, buttonClass7, buttonIcon7) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    let ticketId = ticket?.id;

    const fourthUrl = new URL(fourthUrlString);
    fourthUrl.searchParams.append('t', ticketId);
    fourthUrl.searchParams.append('ac', 'listTicketToManage');

    const fifthUrl = new URL(fifthUrlString);
    fifthUrl.searchParams.append('t', ticketId);
    fifthUrl.searchParams.append('ac', 'listTicketToManage');

    const sixthUrl = new URL(sixthUrlString);
    sixthUrl.searchParams.append('t', ticketId);
    sixthUrl.searchParams.append('ac', 'listTicketToManage');

    const seventhUrl = new URL(seventhUrlString);
    seventhUrl.searchParams.append('t', ticketId);
    seventhUrl.searchParams.append('ac', 'listTicketToManage');

    const fourthButton = createButton(fourthUrl, buttonClass4, buttonIcon4);
    cardToolbar.appendChild(fourthButton);

    const fifthButton = createButton(fifthUrl, buttonClass5, buttonIcon5);
    cardToolbar.appendChild(fifthButton);

    const sixthButton = createButton(sixthUrl, buttonClass6, buttonIcon6);
    cardToolbar.appendChild(sixthButton);

    const seventhButton = createButton(seventhUrl, buttonClass7, buttonIcon7);
    cardToolbar.appendChild(seventhButton);

    const popOverIcon = getPopOverIcon(ticket);

    cardToolbar.appendChild(popOverIcon);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);

    toolbarLastPart(popOverIcon, ticket, status);
  }

  let offset22 = 10;
  let loading22 = false;
  let previousDataSetEmpty22 = false;

  function loadMoreData21() {
    if (loading22) return;

    loading22 = true;

    if (!previousDataSetEmpty22) {
      let eighthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndRevokeTicket')}";
      let ninthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket')}";
      let tenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndCloseTicket')}";
      let eleventhUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";

      $('#loading-indicator3').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreOnHoldTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset22,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell_22 = document.createElement('td');
              const card_22 = getCardFirstHalf();
              addRow(ticket, actionsCell_22, () => createToolbar11('toolbar31', ticket, actionsCell_22, 'On Hold', eighthUrlString, "btn btn-warning toolbar-button", "fa-solid fa-play", ninthUrlString, "btn btn-secondary toolbar-button", "fa-solid fa-clone", tenthUrlString, "btn btn-success toolbar-button", "fa fa-close", eleventhUrlString, "btn btn-danger toolbar-button", "fa fa-trash"), "clickable-table3");
              addCard(ticket, card_22, () => createToolbar11('card-toolbar32', ticket, card_22, 'On Hold', eighthUrlString, "btn btn-warning toolbar-button", "fa-solid fa-play", ninthUrlString, "btn btn-secondary toolbar-button", "fa-solid fa-clone", tenthUrlString, "btn btn-success toolbar-button", "fa fa-close", eleventhUrlString, "btn btn-danger toolbar-button", "fa fa-trash"), "tab32-flex-container");
            });
            offset22 = offset22 + max;
            loading22 = false;
          } else {
            previousDataSetEmpty22 = true;
          }
          $('#loading-indicator3').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator3').hide();
        }
      });
    }
  }

  let offset32 = 10;
  let loading32 = false;
  let previousDataSetEmpty32 = false;

  function loadMoreData31() {
    if (loading32) return;

    loading32 = true;

    if (!previousDataSetEmpty32) {
      let twelthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket')}";
      let thirteenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndCloseTicket')}";
      let fourteenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";

      $('#loading-indicator4').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreRevokedTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset32,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell_42 = document.createElement('td');
              const card_42 = getCardFirstHalf();
              addRow(ticket, actionsCell_42, () => createToolbar('toolbar41', ticket, actionsCell_42, 'Restored', twelthUrlString, "btn btn-secondary toolbar-button", "fa-solid fa-clone", thirteenthUrlString, "btn btn-success toolbar-button", "fa-solid fa-close", fourteenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 't', ticket?.id, 't', ticket?.id, 'ac', "listTicketToManage"), "clickable-table4");
              addCard(ticket, card_42, () => createToolbar('card-toolbar42', ticket, card_42, 'Restored', twelthUrlString, "btn btn-secondary toolbar-button", "fa-solid fa-clone", thirteenthUrlString, "btn btn-success toolbar-button", "fa-solid fa-close", fourteenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 't', ticket?.id, 't', ticket?.id, 'ac', "listTicketToManage"), "tab42-flex-container");
            });
            offset32 = offset32 + max;
            loading32 = false;
          } else {
            previousDataSetEmpty32 = true;
          }
          $('#loading-indicator4').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator4').hide();
        }
      });
    }
  }

  let offset42 = 10;
  let loading42 = false;
  let previousDataSetEmpty42 = false;

  function loadMoreData41() {
    if (loading42) return;

    loading42 = true;

    if (!previousDataSetEmpty42) {
      let fifteenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndCloseTicket')}";
      let sixteenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";

      $('#loading-indicator5').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreDuplicatedTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset42,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell_52 = document.createElement('td');
              const card_52 = getCardFirstHalf();
              addRow(ticket, actionsCell_52, () => createToolbar22('toolbar51', ticket, actionsCell_52, 'Duplicated', fifteenthUrlString, "btn btn-success toolbar-button", "fa-solid fa-close", sixteenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 't', ticket?.id, 'ac', "listTicketToManage"), "clickable-table5");
              addCard(ticket, card_52, () => createToolbar22('card-toolbar52', ticket, card_52, 'Duplicated', fifteenthUrlString, "btn btn-success toolbar-button", "fa-solid fa-close", sixteenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 't', ticket?.id, 'ac', "listTicketToManage"), "tab52-flex-container");
            });
            offset42 = offset42 + max;
            loading42 = false;
          } else {
            previousDataSetEmpty42 = true;
          }
          $('#loading-indicator5').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator5').hide();
        }
      });
    }
  }

  function createToolbar22(toolbarId, ticket, cardElement, status, fifteenthUrlString, buttonClass15, buttonIcon15, sixteenthUrlString, buttonClass16, buttonIcon16, fifteenthSearchParam, fifteenthSearchParamValue, sixteenthSearchParam, sixteenthSearchParamValue, additionalSearchParam, additionalSearchParamValue) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    const fifteenthUrl = new URL(fifteenthUrlString);
    const sixteenthUrl = new URL(sixteenthUrlString);

    fifteenthUrl.searchParams.append(fifteenthSearchParam, fifteenthSearchParamValue);

    sixteenthUrl.searchParams.append(sixteenthSearchParam, sixteenthSearchParamValue);

    fifteenthUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);
    sixteenthUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);

    const fifteenthButton = createButton(fifteenthUrl, buttonClass15, buttonIcon15);
    cardToolbar.appendChild(fifteenthButton);

    const sixteenthButton = createButton(sixteenthUrl, buttonClass16, buttonIcon16);
    cardToolbar.appendChild(sixteenthButton);

    const popOverIcon = getPopOverIcon(ticket);

    cardToolbar.appendChild(popOverIcon);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);

    toolbarLastPart(popOverIcon, ticket, status);

  }


  let offset52 = 10;
  let loading52 = false;
  let previousDataSetEmpty52 = false;

  function loadMoreData51() {
    if (loading52) return;

    loading52 = true;

    if (!previousDataSetEmpty52) {
      let seventeenthUrlString = window.location.origin + "${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket')}";

      $('#loading-indicator6').show();

      $.ajax({
        type: 'GET',
        url: '${g.createLink(controller: 'ticket', action: 'listMoreClosedTickets')}',
        contentType: 'application/json',
        dataType: 'json',
        data: {
          offset: offset52,
          max: max
        },
        success: function (data) {
          if (data.length > 0) {
            $.each(data, function (index, ticket) {
              const actionsCell_62 = document.createElement('td');
              const card_62 = getCardFirstHalf();
              addRow(ticket, actionsCell_62, () => createToolbar33('toolbar61', ticket, actionsCell_62, 'Closed', seventeenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 'ac', "listTicketToManage"), "clickable-table6");
              addCard(ticket, card_62, () => createToolbar33('card-toolbar62', ticket, card_62, 'Closed', seventeenthUrlString, "btn btn-danger toolbar-button", "fa fa-trash", 't', ticket?.id, 'ac', "listTicketToManage"), "tab62-flex-container");
            });
            offset52 = offset52 + max;
            loading52 = false;
          } else {
            previousDataSetEmpty52 = true;
          }
          $('#loading-indicator6').hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
          console.error('Error:', textStatus, errorThrown);
          console.error('Response Text:', jqXHR.responseText);
          console.error('Response Status:', jqXHR.status);

          $('#loading-indicator6').hide();
        }
      });
    }
  }

  function createToolbar33(toolbarId, ticket, cardElement, status, seventeenthUrlString, buttonClass17, buttonIcon17, seventeenthSearchParam, seventeenthSearchParamValue, additionalSearchParam, additionalSearchParamValue) {
    // Create the main div element
    const cardToolbar = document.createElement('div');
    cardToolbar.id = toolbarId;
    cardToolbar.className = toolbarId;

    const seventeenthUrl = new URL(seventeenthUrlString);

    seventeenthUrl.searchParams.append(seventeenthSearchParam, seventeenthSearchParamValue);

    seventeenthUrl.searchParams.append(additionalSearchParam, additionalSearchParamValue);

    const seventeenthButton = createButton(seventeenthUrl, buttonClass17, buttonIcon17);
    cardToolbar.appendChild(seventeenthButton);

    const popOverIcon = getPopOverIcon(ticket);

    cardToolbar.appendChild(popOverIcon);

    // Append the Toolbar to the parent element
    cardElement.appendChild(cardToolbar);

    toolbarLastPart(popOverIcon, ticket, status);

  }


  // Helper function to create a button
  function createButton(url, className, iconClass) {
    const button = document.createElement('a');
    button.href = url;
    button.className = className;
    button.setAttribute('role', 'button');

    const icon = document.createElement('i');
    icon.className = iconClass;
    button.append(icon);

    return button;
  }

</script>

</body>
</html>
