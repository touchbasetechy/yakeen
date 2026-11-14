<%-- ── contactModal.gsp ────────────────────────────────────────────────────
     Shown when a new primary contact conflicts with an existing one.
     Populated via JS from data.contact on a 409 response.
     Triggered by:  GlassModal.open('contactStatusModal')
──────────────────────────────────────────────────────────────────────── --%>
<g:set var="contactStatusFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    Do you wish to replace the existing primary contact with the new one?
  </p>
  <button class="gm-btn gm-btn-ghost" data-gm-dismiss>Cancel</button>
  <button class="gm-btn gm-btn-primary" id="confirmReplacePrimaryBtn">Replace</button>
</g:set>

<glass:modal
  id="contactStatusModal"
  title="Contacts Summary"
  faIcon="fas fa-address-book"
  size="lg"
  variant="dark"
  footerHtml="${contactStatusFooter}">

  <glass:mtable
    id="contactStatusTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Contact Details" sortable="false"/>
      <glass:mth label="New" sortable="false"/>
      <glass:mth label="Existing" sortable="false"/>
    </glass:mthead>

    <glass:mtbody>

      <glass:mtr>
        <glass:mtd><strong>Country</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Mobile Number</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Email</strong></glass:mtd>
        <glass:mtd type="email"></glass:mtd>
        <glass:mtd type="email"></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Primary Contact</strong></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
      </glass:mtr>

    </glass:mtbody>

  </glass:mtable>

</glass:modal>
