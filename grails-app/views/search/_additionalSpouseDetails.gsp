<div class="row row-padding">
  <div class="col-12 d-flex justify-content-end align-items-center gap-3">
    <glass:diagonalButton leftLabel="Save" leftId="saveAdditionalSpouseDetails"
                          leftName="saveAdditionalSpouseDetails"
                          rightLabel="Clear" rightId="clearAdditionalSpouseDetails"
                          rightName="clearAdditionalSpouseDetails"
                          gap="10"/>
    <glassToggle:toggle id="useSavedAdditionalSpouseDetails" name="useSavedAdditionalSpouseDetails"
                        label="Use saved additional spouse details"
                        checked="${additionalSpouseDetails?.useSaved}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-12 col-md-6">
    <glass:select name="education" id="education" label="Education"
                  from="${educations}" optionKey="name" optionValue="name"
                  noSelection="${["Doesn't Matter": "Doesn't Matter"]}"
                  value="${additionalSpouseDetails?.education}"/>
  </div>
  %{--</div>--}%

  %{--<div class="row row-padding g-3">--}%
  <div class="col-12 col-md-6">
    <glass:alphaNumericTextField name="profession" id="profession" label="Profession"
                                 value="${additionalSpouseDetails?.profession}"
                                 maxlength="50"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-12 col-md-6">
    Add currency selector here
  </div>

  <div class="col-12 col-md-6">
    <glass:formattedNumericField name="salary" id="salary" label="Salary"
                                 value="${additionalSpouseDetails?.salary}"
                                 max="9999999999"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="salaryFor" value="Per Anum" id="perAnum" title="Per Anum"
                 checked="${additionalSpouseDetails?.salaryFor == 'Per Anum'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="salaryFor" value="Per Month" id="perMonth" title="Per Month"
                 checked="${additionalSpouseDetails?.salaryFor == 'Per Month'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="salaryFor" value="Per Week" id="perWeek" title="Per Week"
                 checked="${additionalSpouseDetails?.salaryFor == 'Per Week'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="salaryFor" value="Per Day" id="perDay" title="Per Day"
                 checked="${additionalSpouseDetails?.salaryFor == 'Per Day'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="salaryFor" value="Per Hour" id="perHour" title="Per Hour"
                 checked="${additionalSpouseDetails?.salaryFor == 'Per Hour'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-12 col-md-6">
    <glass:alphaNumericTextField name="workLocation" id="workLocation" label="Work Location"
                                 value="${additionalSpouseDetails?.workLocation}"/>
  </div>
  %{--</div>--}%

  %{--<div class="row row-padding g-3">--}%
  <div class="col-12 col-md-6">
    <sheet:countryNamePicker
      id="countryPicker"
      countries="${countries}"
      countryName="country"
      flagName="flag"
      selectedCountry="India"
      selectedFlag="in"
      buttonText="Select Country"
      panelTitle="Select Country"
      disabled="false"
      style="max-width:320px"/>

  </div>
</div>
