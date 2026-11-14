<a
  href="${createLink(controller: 'Ticket', action: 'viewAndHoldTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-warning toolbar-button" role="button">
  <i class="fas fa-pause"></i>
</a>

<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-secondary toolbar-button" role="button">
  <i class="fas fa-clone"></i>
</a>

<a
  href="${createLink(controller: 'Ticket', action: 'viewAndCloseTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-success toolbar-button" role="button">
  <i class="fas fa-times"></i>
</a>

<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-danger toolbar-button" role="button">
  <i class="fas fa-trash"></i>
</a>
