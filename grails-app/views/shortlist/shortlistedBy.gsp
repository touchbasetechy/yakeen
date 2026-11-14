<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Who Shortlisted Me</title>
</head>

<body>
<div class="nav" role="navigation">
  <ul>
    <li><g:link class="home" action="index" controller="profile">Home</g:link></li>
    <li><g:link action="index">My Shortlist</g:link></li>
  </ul>
</div>

<div id="shortlisted-by" class="content scaffold-list" role="main">
  <h1>Users Who Shortlisted Me</h1>

  <g:if test="${shortlistedBy}">
    <div class="shortlist-grid">
      <g:each in="${shortlistedBy}" var="entry">
        <div class="profile-card">
          <div class="profile-image">
            <g:if test="${entry.user.profilePhoto}">
              <img src="${createLink(controller: 'user', action: 'profilePhoto', id: entry.user.id)}"
                   alt="Profile Photo" class="img-thumbnail"/>
            </g:if>
            <g:else>
              <div class="no-photo">No Photo</div>
            </g:else>
          </div>

          <div class="profile-details">
            <h4>
              <g:link controller="profile" action="show" id="${entry.user.id}">
                ${entry.user.firstName} ${entry.user.lastName}
              </g:link>
            </h4>

            <div class="profile-info">
              <p><strong>Age:</strong> ${entry.user.age} years</p>

              <p><strong>Location:</strong> ${entry.user.city}, ${entry.user.state}</p>

              <p><strong>Profession:</strong> ${entry.user.profession}</p>

              <p><strong>Shortlisted me on:</strong>
                <g:formatDate date="${entry.dateCreated}" format="dd-MM-yyyy"/>
              </p>
            </div>

            <div class="actions">
              <g:link controller="profile" action="show" id="${entry.user.id}"
                      class="btn btn-primary">View Profile</g:link>
              <button class="btn btn-success add-to-shortlist"
                      data-user-id="${entry.user.id}">
                Add to My Shortlist
              </button>
            </div>
          </div>
        </div>
      </g:each>
    </div>

    <div class="pagination">
      <g:paginate total="${shortlistedBy.totalCount}" params="${params}"/>
    </div>
  </g:if>
  <g:else>
    <div class="empty-list">
      <h3>No one has shortlisted you yet</h3>

      <p>Make your profile more attractive to get more attention!</p>
    </div>
  </g:else>
</div>
</body>
</html>
