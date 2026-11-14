<%-- ── statusModal.gsp ────────────────────────────────────────────────────
     Displays profession/job details.
     Triggered by:  GlassModal.open('jobStatusModal')
──────────────────────────────────────────────────────────────────────── --%>
<g:set var="professionAddFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    <i class="fa-solid fa-circle-info"></i>
    Can't overwrite a profession detail here. Do it in "Edit your information" section of your Dashboard.
  </p>
</g:set>

<glass:modal
  id="addJobModal"
  title="Jobs Summary"
  faIcon="fas fa-briefcase"
  size="lg"
  autoClose="8000"
  variant="light"
  footerHtml="${professionAddFooter}">

  <glass:mtable
    id="addJobTable"
    variant="modal"
    compact="true"
    hover="true"
    sortable="false"
    searchable="false"
    paginate="false"
    responsive="true">

    <glass:mthead>
      <glass:mth label="Job Details" sortable="false"/>
      <glass:mth label="New" sortable="false"/>
      <glass:mth label="Existing" sortable="false"/>
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
