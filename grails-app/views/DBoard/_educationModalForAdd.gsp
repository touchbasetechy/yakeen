<%-- ── educationStatusModal.gsp ──────────────────────────────────────────────
     Shown when a new education record is added/conflicts.
     Populated via JS from data.education on response.
     Triggered by:  GlassModal.open('educationStatusModal')
──────────────────────────────────────────────────────────────────────────── --%>
<g:set var="educationAddFooter">
  <p class="mb-0 me-auto" style="opacity:0.65;font-size:0.85rem;">
    <i class="fa-solid fa-circle-info"></i>
    Can't overwrite an education detail here. Do it in "Edit your information" section of your Dashboard.
  </p>
</g:set>

<glass:modal
  id="addEducationModal"
  title="Educations Summary"
  faIcon="fas fa-graduation-cap"
  size="lg"
  autoClose="8000"
  variant="light"
  footerHtml="${educationAddFooter}">

  <glass:mtable
    id="addEducationTable"
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
