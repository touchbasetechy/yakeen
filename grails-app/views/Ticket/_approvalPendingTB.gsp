<a
  href="${createLink(controller: 'Ticket', action: 'viewTicket', params: [t: "${ticket?.id}"])}"
  class="btn btn-primary toolbar-button" role="button"><i class="fa-solid fa-eye"></i></a>
<a
  href="${createLink(controller: 'ticket', action: 'listTicketTypeForAddToManageTickets')}"
  class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
<a
  href="${createLink(controller: 'Ticket', action: 'viewAndDeleteTicket', params: [t: "${ticket?.id}", ac: "listTicketToManage"])}"
  class="btn btn-danger toolbar-button"><i class="fa fa-trash"></i></a>
