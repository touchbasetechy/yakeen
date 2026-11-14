<!doctype html>
<html>
<head>
  <meta name="layout" content="search"/>
  <glass:glassCardStyles/>
  <glass:styles/>
  <glass:sliderAssets/>
</head>

<body>
<glass:modernGlassCard
  title="Search Profiles"
  iconClass="fa-solid fa-chevron-left"
  iconController="DBoard"
  iconAction="index"
  titleHtml="true"
  subtitle="Search for matching profiles">

  <form action="${createLink(controller: 'search', action: 'search')}" method="post">
    <section>
      <glass:tabs id="searchProfileTabs" variant="pills" useShadow="false">
        <glass:tab title="Spouse Details" icon="fas fa-heart-pulse" active="true">
          <g:render template="spouseDetails" model="[spouseDetails: spouseDetails, religions: religions]"/>
        </glass:tab>
        <glass:tab title="Spouse Family Preferences" icon="fas fa-home">
          <g:render template="spouseFamilyPreferences"
                    model="[spouseFamilyPreferences: spouseFamilyPreferences, heights: heights, weights: weights]"/>
        </glass:tab>
        <glass:tab title="Additional Spouse Details" icon="fas fa-user-plus">
          <g:render template="additionalSpouseDetails"
                    model="[additionalSpouseDetails: additionalSpouseDetails, educations: educations, countries: countries]"/>
        </glass:tab>
      </glass:tabs>
    </section>

    <div class="d-flex justify-content-end">
      <glass:cardBtn label="Search" variant="cosmic" faIcon="fas fa-search"/>
    </div>
  </form>
</glass:modernGlassCard>
<script type="text/javascript">
  // Utility function to get checked radio value
  function getCheckedValue(name) {
    const checked = document.querySelector('input[name="' + name + '"]:checked');
    return checked ? checked.value : '';
  }

  // Auto-hide alerts
  setTimeout(() => {
    const alert = document.getElementById('alert');
    if (alert) alert.style.display = 'none';
  }, 6000);

  document.addEventListener("DOMContentLoaded", function () {
    console.log("${params.slot}");
    const religionSelect = document.getElementById("religion");
    const casteSelect = document.getElementById("caste");

    const religionsData = [
      <g:each in="${religions}" var="religion" status="i">
      {
        name: "${raw(religion.name)}",
        caste: [
          <g:each in="${religion.caste}" var="caste" status="j">
          "${raw(caste)}"${j < religion.caste.size() - 1 ? ',' : ''}
          </g:each>
        ]
      }${i < religions.size() - 1 ? ',' : ''}
      </g:each>
    ];

    // Populate caste dropdown
    function populateCaste(selectedReligionName) {
      if (!casteSelect) return;
      casteSelect.innerHTML = "<option value=\"Doesn't Matter\">Doesn't Matter</option>";

      const selectedReligion = religionsData.find(religion => religion.name === selectedReligionName);
      if (selectedReligion && selectedReligion.caste) {
        selectedReligion.caste.forEach(caste => {
          const option = document.createElement('option');
          option.value = caste;
          option.textContent = caste;
          casteSelect.appendChild(option);
        });

        const savedCaste = "${spouseDetails?.caste}";
        if (savedCaste && selectedReligion.name === "${spouseDetails?.religion}") {
          casteSelect.value = savedCaste;
        }
      }
    }

    // Initialize caste on page load
    if (religionSelect && religionSelect.value) {
      populateCaste(religionSelect.value);
      casteSelect.value = "${spouseDetails?.caste}" ? "${spouseDetails?.caste?.encodeAsJavaScript()}" : "Doesn't Matter";
    }

    // Religion change handler
    if (religionSelect) {
      religionSelect.addEventListener('change', (e) => populateCaste(e.target.value));
    }


    // Use Saved Spouse Details Toggle
    const useSavedSpouseDetails = document.getElementById("useSavedSpouseDetails");
    let savedState = {
      religion: "${spouseDetails?.religion}",
      caste: "${spouseDetails?.caste}",
      smokingHabit: "${spouseDetails?.smokingHabit}",
      drinkingHabit: "${spouseDetails?.drinkingHabit}"
    };

    if (useSavedSpouseDetails) {
      useSavedSpouseDetails.addEventListener('change', function (ev) {
        if (ev.target.checked) {
          religionSelect.value = savedState.religion;
          populateCaste(savedState.religion);
          casteSelect.value = savedState.caste;

          const smokingRadio = document.querySelector(`input[name="smokingHabit"][value="${savedState?.smokingHabit}"]`);
          if (smokingRadio) smokingRadio.checked = true;

          const drinkingRadio = document.querySelector(`input[name="drinkingHabit"][value="${savedState?.drinkingHabit}"]`);
          if (drinkingRadio) drinkingRadio.checked = true;
        } else {
          religionSelect.value = "Doesn't Matter";
          populateCaste("Doesn't Matter");
          casteSelect.value = "Doesn't Matter";
          document.querySelectorAll('input[name="smokingHabit"]').forEach(r => r.checked = false);
          document.querySelectorAll('input[name="drinkingHabit"]').forEach(r => r.checked = false);
        }
      });
    }

    // Save Spouse Details
    const saveSpouseDetailsBtn = document.getElementById('saveSpouseDetails');
    if (saveSpouseDetailsBtn) {
      saveSpouseDetailsBtn.addEventListener('click', function (e) {
        e.preventDefault();

        const formData = {
          minAge: document.getElementById('StartingAge')?.value || '',
          maxAge: document.getElementById('EndingAge')?.value || '',
          gender: getCheckedValue('gender'),
          religion: document.getElementById('religion')?.value || "Doesn't Matter",
          caste: document.getElementById('caste')?.value || "Doesn't Matter",
          smokingHabit: getCheckedValue("smokingHabit"),
          drinkingHabit: getCheckedValue("drinkingHabit")
        };

        fetch('${createLink(controller: "search", action: "saveSpouseDetails")}', {
          method: 'POST',
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          body: new URLSearchParams(formData)
        })
          .then(response => {
            if (response.redirected) {
              window.location.href = response.url;
            }
            return response.text();
          })
          .catch(error => {
            console.error('Error:', error);
            alert('Failed to save spouse details');
          });
      });
    }

    // Clear Spouse Details
    const clearSpouseDetailsBtn = document.getElementById('clearSpouseDetails');
    if (clearSpouseDetailsBtn) {
      clearSpouseDetailsBtn.addEventListener('click', function () {
        document.getElementById('StartingAge').value = '';
        document.getElementById('EndingAge').value = '';
        document.querySelectorAll('input[name="gender"]').forEach(r => r.checked = false);
        religionSelect.value = "Doesn't Matter";
        casteSelect.value = "Doesn't Matter";
        document.querySelectorAll('input[name="smokingHabit"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="drinkingHabit"]').forEach(r => r.checked = false);
        if (useSavedSpouseDetails) useSavedSpouseDetails.checked = false;
      });
    }

    // Marital Status and Children Logic
    const maritalStatusInputs = document.querySelectorAll('input[name="maritalStatus"]');
    const childrenDiv = document.getElementById('children');
    const childStatusDiv = document.getElementById('child_status');
    const childrenInputs = document.querySelectorAll('input[name="children"]');

    maritalStatusInputs.forEach(input => {
      input.addEventListener('change', function () {
        if (this.value === 'UnMarried') {
          childrenDiv?.classList.add('d-none');
          childStatusDiv?.classList.add('d-none');
        } else {
          childrenDiv?.classList.remove('d-none');
          childrenDiv?.classList.add('animate__animated', 'animate__fadeInDown');
        }
      });
    });

    childrenInputs.forEach(input => {
      input.addEventListener('change', function () {
        if (this.value === 'No Children') {
          childStatusDiv?.classList.add('d-none');
        } else {
          childStatusDiv?.classList.remove('d-none');
          childStatusDiv?.classList.add('animate__animated', 'animate__fadeInDown');
        }
      });
    });

    // Save Spouse Family Details
    const saveSpouseFamilyDetailsBtn = document.getElementById('saveSpouseFamilyDetails');
    if (saveSpouseFamilyDetailsBtn) {
      saveSpouseFamilyDetailsBtn.addEventListener('click', function (e) {
        e.preventDefault();

        const formData = {
          maritalStatus: getCheckedValue('maritalStatus'),
          children: getCheckedValue('children'),
          childrenStatus: getCheckedValue('childrenStatus'),
          height: document.getElementById('height')?.value || "Doesn't Matter",
          weight: document.getElementById('weight')?.value || "Doesn't Matter",
          familyStatus: getCheckedValue('familyStatus'),
          familyType: getCheckedValue('familyType'),
          familyValues: getCheckedValue('familyValues'),
          disabilities: getCheckedValue('disabilities')
        };

        fetch('${createLink(controller: "search", action: "saveSpouseFamilyDetails")}', {
          method: 'POST',
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          body: new URLSearchParams(formData)
        })
          .then(response => {
            if (response.redirected) window.location.href = response.url;
            return response.text();
          })
          .catch(error => {
            console.error('Error:', error);
            alert('Failed to save spouse family details');
          });
      });
    }

    // Clear Spouse Family Details
    const clearSpouseFamilyDetailsBtn = document.getElementById('clearSpouseFamilyDetails');
    if (clearSpouseFamilyDetailsBtn) {
      clearSpouseFamilyDetailsBtn.addEventListener('click', function () {
        document.querySelectorAll('input[name="maritalStatus"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="children"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="childrenStatus"]').forEach(r => r.checked = false);
        document.getElementById('height').value = "Doesn't Matter";
        document.getElementById('weight').value = "Doesn't Matter";
        document.querySelectorAll('input[name="familyStatus"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="familyType"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="familyValues"]').forEach(r => r.checked = false);
        document.querySelectorAll('input[name="disabilities"]').forEach(r => r.checked = false);
        document.getElementById('useSavedSpouseFamilyDetails').checked = false;
        childrenDiv?.classList.add('d-none');
        childStatusDiv?.classList.add('d-none');
      });
    }

    // Save Additional Spouse Details
    const saveAdditionalSpouseDetailsBtn = document.getElementById('saveAdditionalSpouseDetails');
    if (saveAdditionalSpouseDetailsBtn) {
      saveAdditionalSpouseDetailsBtn.addEventListener('click', function (e) {
        e.preventDefault();

        const formData = {
          education: document.getElementById('education').value,
          profession: document.getElementById('profession').value,
          salary: document.getElementById('salary').value,
          salaryFor: getCheckedValue('salaryFor'),
          workLocation: document.getElementById('workLocation').value,
          workingCountry: document.getElementById('workingCountry').value
        };

        fetch('${createLink(controller: "search", action: "saveAdditionalSpouseDetails")}', {
          method: 'POST',
          headers: {'Content-Type': 'application/x-www-form-urlencoded'},
          body: new URLSearchParams(formData)
        })
          .then(response => {
            if (response.redirected) window.location.href = response.url;
            return response.text();
          })
          .catch(error => {
            console.error('Error:', error);
            alert('Failed to save additional spouse details');
          });
      });
    }

    // Clear Additional Spouse Details
    const clearAdditionalSpouseDetailsBtn = document.getElementById('clearAdditionalSpouseDetails');
    if (clearAdditionalSpouseDetailsBtn) {
      clearAdditionalSpouseDetailsBtn.addEventListener('click', function () {
        document.getElementById('education').value = "Doesn't Matter";
        document.getElementById('profession').value = '';
        document.getElementById('salary').value = '';
        document.getElementById('workLocation').value = '';
        document.getElementById('workingCountry').value = "Doesn't Matter";
        document.querySelectorAll('input[name="salaryFor"]').forEach(r => r.checked = false);
        document.getElementById('useSavedAdditionalSpouseDetails').checked = false;
      });
    }
  });
</script>

</body>
</html>
