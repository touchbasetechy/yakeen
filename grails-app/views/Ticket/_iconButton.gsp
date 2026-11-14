<i class="fas fa-info-circle custom-icon-small-margin" data-container="body"
   data-toggle="popover"
   data-html="true"
   title="${ticket?.ticketId}"
   data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>Ticket Details</strong></div>
                </div>
               <div class='row odd-row'>
                <div class='col'>Subject</div>
                <div class='col'>${ticket?.subject}</div>
              </div>
        <div class='row even-row'>
        <div class='col'>Message</div>
        <div class='col'>${ticket?.message}</div>
             </div>
<div class='row odd-row'>
        <div class='col'>Status</div>
        <div class='col'>${ticketStatus}</div>
             </div>"></i>
