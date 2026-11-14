<%-- ── contactStatusModal.gsp ────────────────────────────────────────────────────
     Shown when an existing primary contact is replaced with another.
     Populated via JS from data.contact on a success response.
     Triggered by:  GlassModal.open('contactSummaryModal')
──────────────────────────────────────────────────────────────────────── --%>
<glass:modal
  id="contactSummaryModal"
  title="Contacts Status"
  faIcon="fas fa-address-book"
  size="lg"
  autoClose="8000"
  autoRedirectUrl="${createLink(controller: 'DBoard', action: 'editContactDetails')}"
  variant="dark">

  <glass:mtable
    id="contactSummaryTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Contact Details" sortable="false"/>
      <glass:mth label="Old" sortable="false"/>
      <glass:mth label="Current" sortable="false"/>
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
