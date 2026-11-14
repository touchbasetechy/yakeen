<ul class="nav nav-tabs border-0 tab justify-content-center" id="duplicatedTabChild" role="tablist">
  <li class="nav-item" role="presentation">
    <a href="#tab51" class="nav-link active border-0" id="tab51-tab" data-toggle="tab"
       role="tab" aria-controls="tab51" aria-selected="true">
      <i class="fas fa-list"></i>
    </a>
  </li>
  <li class="nav-item" role="presentation">
    <a href="#tab52" class="nav-link border-0" id="tab52-tab" data-toggle="tab"
       role="tab" aria-controls="tab52" aria-selected="false">
      <i class="fas fa-table-cells-large"></i>
    </a>
  </li>
</ul>

<div class="tab-content" id="myTabContent5">
  <div class="tab-pane fade show active" id="tab51" role="tabpanel" aria-labelledby="tab51-tab">
    <div id="table-container51" tabindex="-51" class="overflow-auto">
      <g:if test="${duplicatedTickets}">
        <table id="clickable-table5">
          <thead>
          <tr>
            <th>Ticket Id</th>
            <th>Subject</th>
            <th>Message</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody id="table-body5">
          <g:each in="${duplicatedTickets}" var="ticket">
          %{--            <g:set var="paramName" value="ticketId"/>--}%
          %{--            <g:set var="paramValue" value="${ticket.id}"/>--}%
            <tr>
              <td>${ticket.ticketId}</td>
              <td>${ticket.subject}</td>
              <td>${ticket.message}</td>

              <td><div id="toolbar51">
                <g:render template="duplicatedTB" model="[cardParamValue: ticket?.id]"/>

                <g:render template="iconButton" model="[ticket: ticket, ticketStatus: 'Duplicated']"/>
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
            <td>No duplicated tickets found.</td>
            <td>Unable to manage tickets !!</td>
            <td><a
              href="${createLink(controller: 'ticket', action: 'listTicketTypeForAdd')}"
              class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a></td>
          </tr>
          </tbody>
        </table>
      </g:else>
    </div>

    <div id="loading-indicator5" style="display: none;">
      <img src="${resource(dir: 'images', file: '3D fading lines.gif')}" class="img_25x25"
           alt="Spinner Image">
      &nbsp;Loading...
    </div>
  </div>


  <div class="tab-pane fade" id="tab52" role="tabpanel" aria-labelledby="tab52-tab">
    <div id="card-container52" tabindex="-52" class="overflow-auto">
      <div class="flex-container" id="tab52-flex-container">
        <g:if test="${duplicatedTickets}">
          <g:each in="${duplicatedTickets}" var="ticket">

            <div class="card flex-item border-0 card-width">

              <img src="${resource(dir: 'images', file: 'ticket.svg')}" class="card-img-top"
                   alt="Ticket Image">

              <div id="card-toolbar52">
                <g:render template="duplicatedTB" model="[cardParamValue: ticket?.id]"/>

                <g:render template="iconButton" model="[ticket: ticket, ticketStatus: 'Duplicated']"/>
              </div>


              <div class="card-body5">
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
            </div>

            <div class="card-body">
              <h5 class="d-flex justify-content-center flex-padding">No duplicated tickets found.</h5>

              <div class="card-text d-flex justify-content-center">Unable to manage tickets !!</div>
            </div>
          </div>
        </g:else>
      </div>
    </div>
  </div>
</div>
