<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'search', action: 'search')}" class="same-line"><i
      class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

    <h1 class="same-line">Spouse Profile</h1>
    <section class="row colset-2-its">
      <table>
        <thead>
        <tr>
          Personal Details
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>Name</td>
          <td>${spouse?.firstName}&nbsp;${spouse?.lastName}</td>
        </tr>
        <tr>
          <td>Date of Birth</td>
          <td>${spouse?.personDetails?.dob}</td>
        </tr>
        <tr>
          <td>Gender</td>
          <g:if test="${spouse?.personDetails?.gender == 0}">
            <td>Male</td>

          </g:if>
          <g:if test="${spouse?.personDetails?.gender == 1}">
            <td>Female</td>
          </g:if>

        </tr>
        <tr>
          <td>EMail</td>
          <td>${spouse.eMail}</td>
        </tr>
        <tr>
          <td>Mobile</td>
          <td>xxxxx</td>
        </tr>
        <tr>
          <td>Religion</td>
          <td>${spouse?.personDetails?.religion}</td>
        </tr>
        <tr>
          <td>Caste</td>
          <td>${spouse?.personDetails?.caste}</td>
        </tr>
        <tr>
          <td>Smoking Habit</td>
          <g:if test="${spouse?.personDetails?.smokingHabit == 0}">
            <td>Non Smoker</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.smokingHabit == 1}">
            <td>Social Smoker</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.smokingHabit == 2}">
            <td>Normal Smoker</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.smokingHabit == 3}">
            <td>Chain Smoker</td>
          </g:if>

        </tr>
        <tr>
          <td>Drinking Habit</td>
          <g:if test="${spouse?.personDetails?.drinkingHabit == 0}">
            <td>Non Alcoholic</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.drinkingHabit == 1}">
            <td>Social Drinker</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.drinkingHabit == 2}">
            <td>Normal Drinker</td>
          </g:if>
          <g:if test="${spouse?.personDetails?.drinkingHabit == 3}">
            <td>Alcoholic</td>
          </g:if>
        </tr>
        </tbody>
      </table>
    </section>
    <section class="row colset-2-its">
      <table>
        <thead>
        <tr>
          Spouse Preferences
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>Age (Range)</td>
          <td>${spouse?.spouseDetails?.minAge} - ${session?.person?.spouseDetails?.maxAge}</td>
        </tr>
        <tr>
          <td>Gender</td>
          <g:if test="${spouse?.spouseDetails?.gender == 0}">
            <td>Male</td>

          </g:if>
          <g:if test="${spouse?.spouseDetails?.gender == 1}">
            <td>Female</td>
          </g:if>
        </tr>
        <tr>
          <td>Religion</td>
          <td>${spouse?.personDetails?.religion}</td>
        </tr>
        <tr>
          <td>Caste</td>
          <td>${spouse?.personDetails?.caste}</td>
        </tr>
        <tr>
          <td>Smoking Habit</td>
          <g:if test="${spouse?.spouseDetails?.smokingHabit == 0}">
            <td>Non Smoker</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.smokingHabit == 1}">
            <td>Social Smoker</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.smokingHabit == 2}">
            <td>Normal Smoker</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.smokingHabit == 3}">
            <td>Chain Smoker</td>
          </g:if>

        </tr>
        <tr>
          <td>Drinking Habit</td>
          <g:if test="${spouse?.spouseDetails?.drinkingHabit == 0}">
            <td>Non Alcoholic</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.drinkingHabit == 1}">
            <td>Social Drinker</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.drinkingHabit == 2}">
            <td>Normal Drinker</td>
          </g:if>
          <g:if test="${spouse?.spouseDetails?.drinkingHabit == 3}">
            <td>Alcoholic</td>
          </g:if>
        </tr>
        </tbody>
      </table>
    </section>
    <section class="row colset-2-its">
      <h4>Hobbies</h4>
    </section>
  </div>
</div>

</body>
</html>
