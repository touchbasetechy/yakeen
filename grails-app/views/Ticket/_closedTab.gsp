<ul class="nav nav-tabs border-0 tab justify-content-center" id="closedTabChild" role="tablist">
  <li class="nav-item" role="presentation">
    <a href="#tab61" class="nav-link active border-0" id="tab61-tab" data-toggle="tab"
       role="tab" aria-controls="tab61" aria-selected="true">
      <i class="fas fa-list"></i>
    </a>
  </li>
  <li class="nav-item" role="presentation">
    <a href="#tab62" class="nav-link border-0" id="tab62-tab" data-toggle="tab"
       role="tab" aria-controls="tab62" aria-selected="false">
      <i class="fas fa-table-cells-large"></i>
    </a>
  </li>
</ul>

<div class="tab-content" id="myTabContent6">
  <div class="tab-pane fade show active" id="tab61" role="tabpanel" aria-labelledby="tab61-tab">
    <div id="table-container61" tabindex="-61" class="overflow-auto">
      <g:if test="${closedTickets}">
        <table id="clickable-table6">
          <thead>
          <tr>
            <th>Ticket Id</th>
            <th>Subject</th>
            <th>Message</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody id="table-body6">
          <g:each in="${closedTickets}" var="ticket">
          %{--            <g:set var="paramName" value="ticketId"/>--}%
          %{--            <g:set var="paramValue" value="${ticket.id}"/>--}%
            <tr>
              <td>${ticket.ticketId}</td>
              <td>${ticket.subject}</td>
              <td>${ticket.message}</td>
              <td><div id="toolbar61">
                <a
                  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${ticket.id}", ac: "listTicketToManage"])}"
                  class="btn btn-danger toolbar-button" role="button"><i class="fa fa-trash" aria-hidden="true"></i></a>
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
            <th>Ticket</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>No closed tickets found.</td>
            <td>Unable to manage tickets !!</td>
            <td><a
              href="${createLink(controller: 'ticket', action: 'listTicketTypeForAdd')}"
              class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a></td>
          </tr>
          </tbody>
        </table>
      </g:else>
    </div>

    <div id="loading-indicator6" style="display: none;">
      <img src="${resource(dir: 'images', file: '3D fading lines.gif')}" class="img_25x25"
           alt="Spinner Image">
      &nbsp;Loading...
    </div>
  </div>


  <div class="tab-pane fade" id="tab62" role="tabpanel" aria-labelledby="tab62-tab">
    <div id="card-container62" tabindex="-62" class="overflow-auto">
      <div class="flex-container" id="tab62-flex-container">
        <g:if test="${closedTickets}">
          <g:each in="${closedTickets}" var="ticket">
            <g:set var="cardParamName" value="ticketId"/>
            <g:set var="cardParamValue" value="${ticket.id}"/>

            <div class="card flex-item border-0 card-width">

              <img src="${resource(dir: 'images', file: 'ticket.svg')}" class="card-img-top"
                   alt="Ticket Image">

              <div id="card-toolbar62">
                <a
                  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${ticket.id}", ac: "listTicketToManage"])}"
                  class="btn btn-danger toolbar-button" role="button"><i class="fa fa-trash" aria-hidden="true"></i>
                </a>

                <g:render template="iconButton" model="[ticket: ticket, ticketStatus: 'Closed']"/>
              </div>


              <div class="card-body6">
                <h5 class="card-title">${ticket?.ticketId}</h5>

                <div>Subject: ${ticket?.subject}</div>

                <div class="card-text">Message: ${ticket?.message}</div>
              </div>
            </div>
          </g:each>
        </g:if>
        <g:else>
          <div class="card flex-item card-width">
            <img src="${resource(dir: 'images', file: 'ticket.svg')}" class="card-img-top"
                 alt="Profile Image">

            <div class="d-flex justify-content-end">
              <a
                href="${createLink(controller: 'ticket', action: 'listTicketTypeForAdd')}"
                class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>

              <g:render template="iconButton" model="[ticket: ticket, ticketStatus: 'Closed']"/>
            </div>

            <div class="card-body">
              <h5 class="d-flex justify-content-center flex-padding">No closed tickets found.</h5>

              <div class="card-text d-flex justify-content-center">Unable to manage tickets !!</div>
            </div>
          </div>
        </g:else>
      </div>
    </div>
  </div>
</div>
