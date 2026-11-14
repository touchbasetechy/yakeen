<section class="row">
  <div class="col-md-12">
    <div
      class="col-md-12"><i
      class="fa-solid fa-triangle-exclamation lg-icon same-line"></i>

      <div
        class="error-msg same-line">
        <strong>This ticket is not Approved. Please click the</strong>
        <img src="${resource(dir: 'images', file: 'approve-btn.png')}" class="minimized-image"
             alt="Approve Button Image">
        <strong>button to approve the ticket and proceed further !!</strong>
      </div>
    </div>
    <label for="unApprovedMsg"><strong>Message</strong></label>
    <textarea id="unApprovedMsg" class="form-control custom-textarea border-0 no-decoration"
              readonly>${ticket?.message}</textarea>
  </div>
</section>
