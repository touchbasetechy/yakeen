<a
  href="${createLink(controller: 'Ticket', action: 'viewAndRevokeTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-warning toolbar-button" role="button"><i class="fa-solid fa-play"></i></a>
<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-secondary toolbar-button" role="button"><i class="fa-solid fa-clone"></i></a>

<a
  href="${createLink(controller: 'Ticket', action: 'viewAndCloseTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-success toolbar-button" role="button"><i class="fa fa-close"></i></a>
<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-danger toolbar-button"><i class="fa fa-trash"></i></a>
