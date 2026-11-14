<div class="row row-padding">
  <div class="col-12 d-flex justify-content-end align-items-center gap-3">
    <glass:diagonalButton leftLabel="Save" leftId="saveSpouseFamilyDetails" leftName="saveSpouseFamilyDetails"
                          rightLabel="Clear" rightId="clearSpouseFamilyDetails" rightName="clearSpouseFamilyDetails"
                          gap="10"/>
    <glassToggle:toggle id="useSavedSpouseFamilyDetails" name="useSavedSpouseFamilyDetails"
                        label="Use saved spouse family preferences"
                        checked="${spouseFamilyPreferences?.useSaved}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="maritalStatus" value="UnMarried" id="un_married" title="UnMarried"
                 checked="${spouseFamilyPreferences?.maritalStatus == 'UnMarried'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="maritalStatus" value="Divorced" id="maritalStatus1" title="Divorced"
                 checked="${spouseFamilyPreferences?.maritalStatus == 'Divorced'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="maritalStatus" value="Awaiting Divorce" id="maritalStatus2" title="Awaiting Divorce"
                 checked="${spouseFamilyPreferences?.maritalStatus == 'Awaiting Divorce'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="maritalStatus" value="Widowed" id="maritalStatus3" title="Widowed"
                 checked="${spouseFamilyPreferences?.maritalStatus == 'Widowed'}"/>
  </div>
</div>

<div class="row row-padding d-none g-3" id="children">
  <div class="col-6 col-sm-3">
    <glass:radio name="children" value="No Children" id="no_children" title="No Children"
                 checked="${spouseFamilyPreferences?.children == 'No Children'}"/>
  </div>

  <div class="col-6 col-sm-2">
    <glass:radio name="children" value="1" id="children1" title="1"
                 checked="${spouseFamilyPreferences?.children == '1'}"/>
  </div>

  <div class="col-6 col-sm-2">
    <glass:radio name="children" value="2" id="children2" title="2"
                 checked="${spouseFamilyPreferences?.children == '2'}"/>
  </div>

  <div class="col-6 col-sm-2">
    <glass:radio name="children" value="3" id="children3" title="3"
                 checked="${spouseFamilyPreferences?.children == '3'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="children" value="4 and more" id="children4" title="4 and more"
                 checked="${spouseFamilyPreferences?.children == '4 and more'}"/>
  </div>
</div>

<div class="row row-padding d-none g-3" id="child_status">
  <div class="col-6 col-sm-3">
    <glass:radio name="childrenStatus" value="Living with me" id="children_status1" title="Living with me"
                 checked="${spouseFamilyPreferences?.childrenStatus == 'Living with me'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="childrenStatus" value="Not living with me" id="children_status2" title="Not living with me"
                 checked="${spouseFamilyPreferences?.childrenStatus == 'Not living with me'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-12 col-md-6">
    <g:set var="selectedMinHeight" value="${spousePreferences?.minHeight}"/>
    <g:set var="selectedMaxHeight" value="${spousePreferences?.maxHeight}"/>

    <glass:heightRangeSlider name="partnerHeight"
                             min="140" max="210" step="1" dual="true"
                             valueMin="${spousePreferences?.minHeight ?: 155}"
                             valueMax="${spousePreferences?.maxHeight ?: 185}"
                             label="Height" unit="${spousePreferences?.heightUnit ?: 'ft'}"/>

  </div>

  <div class="col-12 col-md-6">
    <g:set var="selectedMinWeight" value="${spousePreferences?.minWeight}"/>
    <g:set var="selectedMaxWeight" value="${spousePreferences?.maxWeight}"/>

    <glass:weightRangeSlider name="weight"
                             min="35" max="150" step="0.5" dual="true"
                             valueMin="${spousePreferences?.minWeight ?: 50}"
                             valueMax="${spousePreferences?.maxWeight ?: 80}"
                             label="Weight" unit="${spousePreferences?.weightUnit ?: 'lb'}"/>

  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="familyStatus" value="Middle Class" id="family_status2" title="Middle Class"
                 checked="${spouseFamilyPreferences?.familyStatus == 'Middle Class'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyStatus" value="Upper Middle Class" id="family_status3" title="Upper Middle Class"
                 checked="${spouseFamilyPreferences?.familyStatus == 'Upper Middle Class'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyStatus" value="High Class" id="family_status4" title="High Class"
                 checked="${spouseFamilyPreferences?.familyStatus == 'High Class'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyStatus" value="Rich / Affluent" id="family_status5" title="Rich / Affluent"
                 checked="${spouseFamilyPreferences?.familyStatus == 'Rich / Affluent'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="familyType" value="Joint Family" id="family_type1" title="Joint Family"
                 checked="${spouseFamilyPreferences?.familyType == 'Joint Family'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyType" value="Nuclear Family" id="family_type2" title="Nuclear Family"
                 checked="${spouseFamilyPreferences?.familyType == 'Nuclear Family'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="familyValues" value="Orthodox" id="family_value1" title="Orthodox"
                 checked="${spouseFamilyPreferences?.familyValues == 'Orthodox'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyValues" value="Traditional" id="family_value2" title="Traditional"
                 checked="${spouseFamilyPreferences?.familyValues == 'Traditional'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyValues" value="Moderate" id="family_value3" title="Moderate"
                 checked="${spouseFamilyPreferences?.familyValues == 'Moderate'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="familyValues" value="Liberal" id="family_value4" title="Liberal"
                 checked="${spouseFamilyPreferences?.familyValues == 'Liberal'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="disabilities" value="Normal" id="disabilities1" title="Normal"
                 checked="${spouseFamilyPreferences?.disabilities == 'Normal'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="disabilities" value="Differently Abled" id="disabilities2" title="Differently Abled"
                 checked="${spouseFamilyPreferences?.disabilities == 'Differently Abled'}"/>
  </div>
</div>
