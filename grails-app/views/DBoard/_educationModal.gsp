<%-- ── educationStatusModal.gsp ──────────────────────────────────────────────
     Shown when a new education record is added/conflicts.
     Populated via JS from data.education on response.
     Triggered by:  GlassModal.open('educationStatusModal')
──────────────────────────────────────────────────────────────────────────── --%>
<g:set var="educationStatusFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    Do you wish to replace the existing <strong id="educationNameLabel">—</strong> education detail with the new one?
  </p>
  <button class="gm-btn gm-btn-ghost" data-gm-dismiss>Cancel</button>
  <button class="gm-btn gm-btn-primary" id="confirmReplacePrimaryBtn">Replace</button>
</g:set>

<glass:modal
  id="educationStatusModal"
  title="Educations Summary"
  faIcon="fas fa-graduation-cap"
  size="lg"
  variant="dark"
  footerHtml="${educationStatusFooter}">

  <glass:mtable
    id="educationStatusTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Education Details" sortable="false"/>
      <glass:mth label="New" sortable="false"/>
      <glass:mth label="Exisiting" sortable="false"/>
    </glass:mthead>

    <glass:mtbody>

      <glass:mtr>
        <glass:mtd><strong>Education</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Institution</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Institution Location</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>University</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>University Location</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Highest Education</strong></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
      </glass:mtr>

    </glass:mtbody>

  </glass:mtable>

</glass:modal>
