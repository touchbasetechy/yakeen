<%-- ── addressStatusModal.gsp ────────────────────────────────────────────────────
     Shown when an existing primary address is replaced or updated.
     Populated via JS from data.address on a success response.
     Triggered by:  GlassModal.open('addressSummaryModal')
──────────────────────────────────────────────────────────────────────── --%>
<glass:modal
  id="addressSummaryModal"
  title="Addresses Status"
  faIcon="fas fa-map-marker-alt"
  size="lg"
  autoClose="8000"
  autoRedirectUrl="${createLink(controller: 'DBoard', action: 'editAddressDetails')}"
  variant="dark">

  <glass:mtable
    id="addressSummaryTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Address Details" sortable="false"/>
      <glass:mth label="Old" sortable="false"/>
      <glass:mth label="Current" sortable="false"/>
    </glass:mthead>

    <glass:mtbody>

      <glass:mtr>
        <glass:mtd><strong>Address Line 1</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Address Line 2</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>City</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>State / Province</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Postal Code</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Country</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Address Type</strong></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
      </glass:mtr>
    </glass:mtbody>

  </glass:mtable>

</glass:modal>
