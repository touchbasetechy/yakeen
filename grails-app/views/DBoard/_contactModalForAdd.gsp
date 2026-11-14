<%-- ── contactModal.gsp ────────────────────────────────────────────────────
     Shown when a new primary contact conflicts with an existing one.
     Populated via JS from data.contact on a 409 response.
     Triggered by:  GlassModal.open('contactStatusModal')
──────────────────────────────────────────────────────────────────────── --%>

<g:set var="contactAddFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    <i class="fa-solid fa-circle-info"></i>
    Can't overwrite a contact detail here. Do it in "Edit your information" section of your Dashboard.
  </p>
</g:set>
<glass:modal
  id="addContactModal"
  title="Contacts Summary"
  faIcon="fas fa-address-book"
  size="lg"
  autoClose="8000"
  variant="light"
  footerHtml="${contactAddFooter}">

  <glass:mtable
    id="addContactTable"
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
