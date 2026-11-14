<%-- ── addressModal.gsp ───────────────────────────────────────────────────
     Shown when a new primary address conflicts with an existing one.
     Populated via JS from data.address on a 409 response.
     Triggered by:  GlassModal.open('addressStatusModal')
──────────────────────────────────────────────────────────────────────── --%>
<g:set var="addAddressFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    <i class="fa-solid fa-circle-info"></i>
    Can't overwrite an address detail here. Do it in "Edit your information" section of your Dashboard.
  </p>
</g:set>

<glass:modal
  id="addAddressModal"
  title="Addresses Summary"
  faIcon="fas fa-map-marker-alt"
  size="lg"
  variant="light"
  footerHtml="${addAddressFooter}">

  <glass:mtable
    id="addAddressTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Address Details" sortable="false"/>
      <glass:mth label="New" sortable="false"/>
      <glass:mth label="Existing" sortable="false"/>
    </glass:mthead>

    <glass:mtbody>

      <glass:mtr>
        <glass:mtd><strong>Street 1</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Street 2</strong></glass:mtd>
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
