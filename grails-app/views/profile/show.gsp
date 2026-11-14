<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Profile - ${user.firstName} ${user.lastName}</title>
  <asset:stylesheet src="shortlist.css"/>
  <asset:javascript src="shortlist.js"/>
</head>

<body>
<div class="profile-container">
  <div class="profile-header">
    <div class="profile-image-large">
      <g:if test="${user.profilePhoto}">
        <img src="${createLink(controller: 'user', action: 'profilePhoto', id: user.id)}"
             alt="Profile Photo" class="img-fluid"/>
      </g:if>
      <g:else>
        <div class="no-photo-large">No Photo Available</div>
      </g:else>
    </div>

    <div class="profile-basic-info">
      <h1>${user.firstName} ${user.lastName}</h1>

      <p class="profile-subtitle">${user.age} years, ${user.city}, ${user.state}</p>

    <!-- Shortlist Actions -->
      <sec:ifLoggedIn>
        <g:if test="${springSecurityService.currentUser.id != user.id}">
          <div class="profile-actions">
            <button class="btn btn-success shortlist-status-check"
                    data-user-id="${user.id}"
                    style="display: none;">
              Add to Shortlist
            </button>

            <g:link controller="messages" action="compose"
                    params="[recipientId: user.id]"
                    class="btn btn-primary">
              <i class="fas fa-envelope"></i> Send Message
            </g:link>

            <button class="btn btn-info" onclick="shareProfile(${user.id})">
              <i class="fas fa-share"></i> Share Profile
            </button>
          </div>

          <!-- Shortlist Notes Section -->
          <div class="shortlist-notes-section" style="display: none;">
            <label>Add a note (optional):</label>
            <textarea class="form-control shortlist-notes-input"
                      placeholder="Why are you interested in this profile?"></textarea>
          </div>
        </g:if>
      </sec:ifLoggedIn>
    </div>
  </div>

  <!-- Profile Details -->
  <div class="profile-details-section">
    <div class="row">
      <div class="col-md-6">
        <div class="profile-section">
          <h3>Personal Information</h3>

          <div class="info-grid">
            <div class="info-item">
              <label>Age:</label>
              <span>${user.age} years</span>
            </div>

            <div class="info-item">
              <label>Height:</label>
              <span>${user.height}</span>
            </div>

            <div class="info-item">
              <label>Religion:</label>
              <span>${user.religion}</span>
            </div>

            <div class="info-item">
              <label>Caste:</label>
              <span>${user.caste}</span>
            </div>

            <div class="info-item">
              <label>Mother Tongue:</label>
              <span>${user.motherTongue}</span>
            </div>

            <div class="info-item">
              <label>Marital Status:</label>
              <span>${user.maritalStatus}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="col-md-6">
        <div class="profile-section">
          <h3>Professional Information</h3>

          <div class="info-grid">
            <div class="info-item">
              <label>Education:</label>
              <span>${user.education}</span>
            </div>

            <div class="info-item">
              <label>Profession:</label>
              <span>${user.profession}</span>
            </div>

            <div class="info-item">
              <label>Income:</label>
              <span>${user.income}</span>
            </div>

            <div class="info-item">
              <label>Location:</label>
              <span>${user.city}, ${user.state}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="profile-section">
          <h3>About Me</h3>

          <div class="about-text">
            ${user.aboutMe ?: 'No description provided.'}
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-12">
        <div class="profile-section">
          <h3>Family Information</h3>

          <div class="info-grid">
            <div class="info-item">
              <label>Family Type:</label>
              <span>${user.familyType}</span>
            </div>

            <div class="info-item">
              <label>Family Status:</label>
              <span>${user.familyStatus}</span>
            </div>

            <div class="info-item">
              <label>Family Values:</label>
              <span>${user.familyValues}</span>
            </div>
          </div>

          <div class="family-details">
            ${user.familyDetails ?: 'No family details provided.'}
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  // Profile specific JavaScript
  function shareProfile(userId) {
    if (navigator.share) {
      navigator.share({
        title: 'Profile - ${user.firstName} ${user.lastName}',
        url: window.location.href
      });
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(window.location.href);
      alert('Profile link copied to clipboard!');
    }
  }

  // Show/hide notes section based on button state
  $(document).on('click', '.add-to-shortlist', function () {
    $('.shortlist-notes-section').show();
  });

  $(document).on('click', '.remove-shortlist', function () {
    $('.shortlist-notes-section').hide();
  });
</script>
</body>
</html>
