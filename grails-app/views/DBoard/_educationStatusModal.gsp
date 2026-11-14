<%-- ── educationStatusModal.gsp ──────────────────────────────────────────────
     Shown when a new education record is added/conflicts.
     Populated via JS from data.education on response.
     Triggered by:  GlassModal.open('educationStatusModal')
──────────────────────────────────────────────────────────────────────────── --%>
<glass:modal
  id="educationSummaryModal"
  title="Educations Status"
  faIcon="fas fa-graduation-cap"
  size="lg"
  autoClose="8000"
  autoRedirectUrl="${createLink(controller: 'DBoard', action: 'editEducationDetails')}"
  variant="dark">

  <glass:mtable
    id="educationSummaryTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Education Details" sortable="false"/>
      <glass:mth label="Old" sortable="false"/>
      <glass:mth label="Current" sortable="false"/>
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
