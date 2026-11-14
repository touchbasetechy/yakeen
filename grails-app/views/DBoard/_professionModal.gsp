<%-- ── statusModal.gsp ────────────────────────────────────────────────────
     Displays profession/job details.
     Triggered by:  GlassModal.open('jobStatusModal')
──────────────────────────────────────────────────────────────────────── --%>
<g:set var="professionStatusFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    Do you wish to replace the existing current job with the new one?
  </p>
  <button class="gm-btn gm-btn-ghost" data-gm-dismiss>Cancel</button>
  <button class="gm-btn gm-btn-primary" id="confirmReplacePrimaryBtn">Replace</button>
</g:set>

<glass:modal
  id="jobStatusModal"
  title="Jobs Summary"
  faIcon="fas fa-briefcase"
  size="lg"
  variant="dark"
  footerHtml="${professionStatusFooter}">

  <glass:mtable
    id="jobStatusTable"
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
