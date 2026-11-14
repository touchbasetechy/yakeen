<%@ page import="grails.util.GrailsNameUtils" %>
<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <glass:tableStyles/>
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
  <div class="container py-4">

    <%-- ── PAGE HEADER ─────────────────────────────────────────────────── --%>
    <header class="d-flex align-items-center gap-3 mb-4">
      <a href="${createLink(controller: 'search', action: 'index')}"
         class="gt-nav-btn" title="Back to Search">
        <i class="fa-solid fa-chevron-left"></i>
      </a>

      <h1 style="
      font-family:'DM Sans',sans-serif;
      font-size:1.45rem;
      font-weight:600;
      color:rgba(232,238,255,.92);
      letter-spacing:.02em;
      margin:0;">
        Profile
      </h1>
    </header>

    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <%-- 1 · PERSONAL DETAILS                                              --%>
    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <div class="mb-4">
      <glass:table
        cols="Field, Value"
        caption="Personal Details"
        searchable="false"
        rowCount="false"
        homeController="search" homeAction="index"
        homeIcon="fa-solid fa-house"
        homeTooltip="Back to Search">

        <tr>
          <td>Name</td>
          <td>${person?.firstName?.encodeAsHTML()}&nbsp;${person?.lastName?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Date of Birth</td>
          <td>
            <g:if test="${person?.personDetails?.dob}">
              <g:formatDate date="${person?.personDetails?.dob}"
                            format="EEE, MMMM d'th', yyyy"
                            timeZone="America/New_York"/>
            </g:if>
          </td>
        </tr>

        <tr>
          <td>Gender</td>
          <td>${person?.personDetails?.gender?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Email</td>
          <td>
            <glass:tableBadge text="${person?.eMail}" variant="blue" dot="false"/>
            <g:each in="${person?.contactDetails}" var="cd">
              <glass:tableBadge text="${cd?.eMail}" variant="default" dot="false"/>
            </g:each>
          </td>
        </tr>

        <tr>
          <td>Mobile</td>
          <td>
            <glass:tableBadge text="${person?.mobileNumber}" variant="violet" dot="false"/>
            <g:each in="${person?.contactDetails}" var="cd">
              <glass:tableBadge text="${cd?.mobileNumber}" variant="default" dot="false"/>
            </g:each>
          </td>
        </tr>

        <tr>
          <td>Religion</td>
          <td>${person?.personDetails?.religion?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Caste</td>
          <td>${person?.personDetails?.caste?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Smoking Habit</td>
          <td>
            <g:if test="${person?.personDetails?.smokingHabit}">
              <glass:tableBadge text="${person?.personDetails?.smokingHabit}"
                                variant="${person?.personDetails?.smokingHabit?.toString()?.equalsIgnoreCase('Never') ? 'success' : 'warning'}"
                                dot="true"/>
            </g:if>
          </td>
        </tr>

        <tr>
          <td>Drinking Habit</td>
          <td>
            <g:if test="${person?.personDetails?.drinkingHabit}">
              <glass:tableBadge text="${person?.personDetails?.drinkingHabit}"
                                variant="${person?.personDetails?.drinkingHabit?.toString()?.equalsIgnoreCase('Never') ? 'success' : 'warning'}"
                                dot="true"/>
            </g:if>
          </td>
        </tr>

      </glass:table>
    </div>

    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <%-- 2 · SPOUSE PREFERENCES                                            --%>
    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <div class="mb-4">
      <glass:table
        cols="Field, Value"
        caption="Spouse Preferences"
        searchable="false"
        rowCount="false">

        <tr>
          <td>Age Range</td>
          <td>
            <glass:tableBadge text="${person?.spouseDetails?.minAge} – ${person?.spouseDetails?.maxAge}" variant="info"
                              dot="false"/>
          </td>
        </tr>

        <tr>
          <td>Gender</td>
          <td>${person?.spouseDetails?.gender?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Religion</td>
          <td>${person?.spouseDetails?.religion?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Caste</td>
          <td>${person?.spouseDetails?.caste?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Smoking Habit</td>
          <td>
            <g:if test="${person?.spouseDetails?.smokingHabit}">
              <glass:tableBadge text="${person?.spouseDetails?.smokingHabit}"
                                variant="${person?.spouseDetails?.smokingHabit?.toString()?.equalsIgnoreCase('Never') ? 'success' : 'warning'}"
                                dot="true"/>
            </g:if>
          </td>
        </tr>

        <tr>
          <td>Drinking Habit</td>
          <td>
            <g:if test="${person?.spouseDetails?.drinkingHabit}">
              <glass:tableBadge text="${person?.spouseDetails?.drinkingHabit}"
                                variant="${person?.spouseDetails?.drinkingHabit?.toString()?.equalsIgnoreCase('Never') ? 'success' : 'warning'}"
                                dot="true"/>
            </g:if>
          </td>
        </tr>

      </glass:table>
    </div>

    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <%-- 3 · FAMILY DETAILS                                                --%>
    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <div class="mb-4">
      <glass:table
        cols="Field, Value"
        caption="Family Details"
        searchable="false"
        rowCount="false">

        <tr>
          <td>Marital Status</td>
          <td>
            <g:if test="${person?.familyDetails?.maritalStatus}">
              <glass:tableBadge text="${person?.familyDetails?.maritalStatus}" variant="violet" dot="false"/>
            </g:if>
            <g:else>
              <glass:tableBadge text="N/A" variant="default" dot="false"/>
            </g:else>
          </td>
        </tr>

        <tr>
          <td>Children</td>
          <td>${person?.familyDetails?.children?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Children Status</td>
          <td>${person?.familyDetails?.childrenStatus?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Height</td>
          <td>${person?.familyDetails?.height?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Weight</td>
          <td>${person?.familyDetails?.weight?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Family Status</td>
          <td>${person?.familyDetails?.familyStatus?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Family Type</td>
          <td>${person?.familyDetails?.familyType?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Family Values</td>
          <td>${person?.familyDetails?.familyValues?.encodeAsHTML()}</td>
        </tr>

        <tr>
          <td>Disabilities</td>
          <td>${person?.familyDetails?.disabilities?.encodeAsHTML()}</td>
        </tr>

      </glass:table>
    </div>

    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <%-- 4 · HOBBIES                                                       --%>
    <%-- ══════════════════════════════════════════════════════════════════ --%>
    <div class="mb-4">
      <glass:table
        cols="Hobbies"
        caption="Hobbies"
        searchable="false"
        rowCount="false">

        <tr>
          <td>
            <g:if test="${person?.hobbies}">
              <g:each in="${person?.hobbies}" var="hobby">
                <glass:tableBadge text="${hobby?.name}" variant="cyan" dot="false"/>
              </g:each>
            </g:if>
            <g:else>
              <glass:tableBadge text="None listed" variant="default" dot="false"/>
            </g:else>
          </td>
        </tr>

      </glass:table>
    </div>

  </div><%-- /container --%>
</div><%-- #content --%>
</body>
</html>
