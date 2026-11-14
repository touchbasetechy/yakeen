<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDuplicateTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-secondary" role="button"><i class="fa-solid fa-clone" aria-hidden="true"></i></a>
<a
  href="${createLink(controller: 'Ticket', action: 'viewAndCloseTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-success" role="button"><i class="fa fa-close" aria-hidden="true"></i></a>
<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${cardParamValue}", ac: "listTicketToManage"])}"
  class="btn btn-danger toolbar-button"><i class="fa fa-trash"></i></a>
