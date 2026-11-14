<%-- ── statusModal.gsp ────────────────────────────────────────────────────
     Displays profession/job details.
     Triggered by:  GlassModal.open('jobSummaryModal')
──────────────────────────────────────────────────────────────────────── --%>

<glass:modal
  id="jobSummaryModal"
  title="Jobs Status"
  faIcon="fas fa-briefcase"
  size="lg"
  autoClose="8000"
  autoRedirectUrl="${createLink(controller: 'DBoard', action: 'editProfessionDetails')}"
  variant="dark">

  <glass:mtable
    id="jobSummaryTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Job Details" sortable="false"/>
      <glass:mth label="Old" sortable="false"/>
      <glass:mth label="Current" sortable="false"/>
    </glass:mthead>

    <glass:mtbody>

      <glass:mtr>
        <glass:mtd><strong>Designation</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Organisation</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Organisation Location</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Job Category</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Salary</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Salary For</strong></glass:mtd>
        <glass:mtd></glass:mtd>
        <glass:mtd></glass:mtd>
      </glass:mtr>

      <glass:mtr>
        <glass:mtd><strong>Is Current Job</strong></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
        <glass:mtd type="status" badgeVariant="default"></glass:mtd>
      </glass:mtr>

    </glass:mtbody>

  </glass:mtable>

</glass:modal>
