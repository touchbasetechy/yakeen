<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>My Shortlist</title>
  <asset:javascript src="shortlist.js"/>
</head>

<body>
<div class="nav" role="navigation">
  <ul>
    <li><g:link class="home" action="index" controller="profile">Home</g:link></li>
    <li><g:link action="shortlistedBy">Who Shortlisted Me</g:link></li>
  </ul>
</div>

<div id="list-shortlist" class="content scaffold-list" role="main">
  <h1>My Shortlist (${shortlistCount} profiles)</h1>

  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>

  <g:if test="${flash.error}">
    <div class="errors" role="alert">${flash.error}</div>
  </g:if>

  <div class="shortlist-actions">
    <g:if test="${shortlistCount > 0}">
      <g:link action="clear" class="btn btn-danger"
              onclick="return confirm('Are you sure you want to clear your entire shortlist?')">
        Clear Shortlist
      </g:link>
    </g:if>
  </div>

  <g:if test="${shortlist}">
    <div class="shortlist-grid">
      <g:each in="${shortlist}" var="entry">
        <div class="profile-card shortlist-entry" data-user-id="${entry.shortlistedUser.id}">
          <div class="profile-image">
            <g:if test="${entry.shortlistedUser.profilePhoto}">
              <img src="${createLink(controller: 'user', action: 'profilePhoto', id: entry.shortlistedUser.id)}"
                   alt="Profile Photo" class="img-thumbnail"/>
            </g:if>
            <g:else>
              <div class="no-photo">No Photo</div>
            </g:else>
          </div>

          <div class="profile-details">
            <h4>
              <g:link controller="profile" action="show" id="${entry.shortlistedUser.id}">
                ${entry.shortlistedUser.firstName} ${entry.shortlistedUser.lastName}
              </g:link>
            </h4>

            <div class="profile-info">
              <p><strong>Age:</strong> ${entry.shortlistedUser.age} years</p>

              <p><strong>Location:</strong> ${entry.shortlistedUser.city}, ${entry.shortlistedUser.state}</p>

              <p><strong>Profession:</strong> ${entry.shortlistedUser.profession}</p>

              <p><strong>Shortlisted on:</strong>
                <g:formatDate date="${entry.dateCreated}" format="dd-MM-yyyy"/>
              </p>
            </div>

            <div class="shortlist-notes">
              <label>My Notes:</label>
              <textarea class="notes-field form-control"
                        data-user-id="${entry.shortlistedUser.id}"
                        placeholder="Add your notes...">${entry.notes}</textarea>
              <button class="btn btn-sm btn-primary save-notes"
                      data-user-id="${entry.shortlistedUser.id}">
                Save Notes
              </button>
            </div>

            <div class="shortlist-actions">
              <g:link controller="profile" action="show" id="${entry.shortlistedUser.id}"
                      class="btn btn-primary">View Profile</g:link>
              <button class="btn btn-danger remove-shortlist"
                      data-user-id="${entry.shortlistedUser.id}">
                Remove from Shortlist
              </button>
            </div>
          </div>
        </div>
      </g:each>
    </div>

    <!-- Pagination -->
    <div class="pagination">
      <g:paginate total="${shortlist.totalCount}" params="${params}"/>
    </div>
  </g:if>
  <g:else>
    <div class="empty-shortlist">
      <h3>Your shortlist is empty</h3>

      <p>Start browsing profiles and add interesting candidates to your shortlist.</p>
      <g:link controller="profile" action="search" class="btn btn-primary">
        Browse Profiles
      </g:link>
    </div>
  </g:else>
</div>
</body>
</html>
