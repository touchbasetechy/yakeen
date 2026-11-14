<div class="row row-padding">
  <div class="col-12 d-flex justify-content-end align-items-center gap-3">
    <glass:diagonalButton leftLabel="Save" leftId="saveSpouseDetails" leftName="saveSpouseDetails"
                          rightLabel="Clear" rightId="clearSpouseDetails" rightName="clearSpouseDetails"
                          gap="10"/>
    <glassToggle:toggle id="useSavedSpouseDetails" name="useSavedSpouseDetails"
                        label="Use saved spouse details"
                        checked="${spouseDetails?.useSaved}"/>
  </div>
</div>

<div class="row row-padding">
  %{--  <div class="col-sm-3">--}%
  %{--    <glass:positiveNumericField name="minAge" id="StartingAge" label="From"--}%
  %{--                                value="${spouseDetails?.minAge}"--}%
  %{--                                autofocus="true" wrapper="false"/>--}%
  %{--  </div>--}%

  %{--  <div class="col-sm-3">--}%
  %{--    <glass:positiveNumericField name="maxAge" id="EndingAge" label="To"--}%
  %{--                                value="${spouseDetails?.maxAge}"--}%
  %{--                                wrapper="false"/>--}%
  %{--  </div>--}%

  <div class="col-12">
    <glass:rangeSlider name="age"
                       min="18" max="100" step="1" dual="true"
                       valueMin="24" valueMax="50"
                       label=" "
                       unit=" Years"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-12 col-sm-6">
    <g:set var="selectedReligion" value="${spouseDetails?.religion}"/>
    <glass:select name="religion" id="religion" label="Religion"
                  from="${religions}" optionKey="name" optionValue="name"
                  noSelection="${["Doesn't Matter": "Doesn't Matter"]}"
                  value="${selectedReligion}"/>
  </div>

  <div class="col-12 col-sm-6">
    <glass:select name="caste" id="caste" label="Caste" placeholder="Select a Caste"
                  from="${[]}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="smokingHabit" value="Non Smoker" id="NonSmoker" title="Non Smoker"
                 checked="${spouseDetails?.smokingHabit == 'Non Smoker'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="smokingHabit" value="Social Smoker" id="SocialSmoker" title="Social Smoker"
                 checked="${spouseDetails?.smokingHabit == 'Social Smoker'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="smokingHabit" value="Normal Smoker" id="NormalSmoker" title="Normal Smoker"
                 checked="${spouseDetails?.smokingHabit == 'Normal Smoker'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="smokingHabit" value="Chain Smoker" id="ChainSmoker" title="Chain Smoker"
                 checked="${spouseDetails?.smokingHabit == 'Chain Smoker'}"/>
  </div>
</div>

<div class="row row-padding g-3">
  <div class="col-6 col-sm-3">
    <glass:radio name="drinkingHabit" value="Non Alcoholic" id="NonDrinker" title="Non Alcoholic"
                 checked="${spouseDetails?.drinkingHabit == 'Non Alcoholic'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="drinkingHabit" value="Social Drinker" id="SocialDrinker" title="Social Drinker"
                 checked="${spouseDetails?.drinkingHabit == 'Social Drinker'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="drinkingHabit" value="Normal Drinker" id="NormalDrinker" title="Normal Drinker"
                 checked="${spouseDetails?.drinkingHabit == 'Normal Drinker'}"/>
  </div>

  <div class="col-6 col-sm-3">
    <glass:radio name="drinkingHabit" value="Alcoholic" id="HeavyDrinker" title="Alcoholic"
                 checked="${spouseDetails?.drinkingHabit == 'Alcoholic'}"/>
  </div>
</div>
