<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Received Interests</title>
  <asset:stylesheet src="interests.css"/>
</head>

<body>
<div class="container">
  <div class="row">
    <div class="col-md-12">
      <h2>Received Interests</h2>

      <!-- Interest Statistics -->
      <div class="interest-stats mb-4">
        <div class="row">
          <div class="col-md-3">
            <div class="stat-card">
              <h4>${stats?.get('PENDING') ?: 0}</h4>

              <p>Pending</p>
            </div>
          </div>

          <div class="col-md-3">
            <div class="stat-card">
              <h4>${stats?.get('ACCEPTED') ?: 0}</h4>

              <p>Accepted</p>
            </div>
          </div>

          <div class="col-md-3">
            <div class="stat-card">
              <h4>${stats?.get('DECLINED') ?: 0}</h4>

              <p>Declined</p>
            </div>
          </div>

          <div class="col-md-3">
            <div class="stat-card">
              <h4>${interests.size()}</h4>

              <p>Total</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Filter Tabs -->
      <ul class="nav nav-tabs mb-3">
        <li class="nav-item">
          <g:link class="nav-link ${!params.status ? 'active' : ''}"
                  action="received">All</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'PENDING' ? 'active' : ''}"
                  action="received" params="[status: 'PENDING']">Pending</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'ACCEPTED' ? 'active' : ''}"
                  action="received" params="[status: 'ACCEPTED']">Accepted</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'DECLINED' ? 'active' : ''}"
                  action="received" params="[status: 'DECLINED']">Declined</g:link>
        </li>
      </ul>

    <!-- Interests List -->
      <g:if test="${interests}">
        <div class="interests-list">
          <g:each in="${interests}" var="interest">
            <div class="interest-card mb-3">
              <div class="card">
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-2">
                      <img src="${createLink(controller: 'user', action: 'avatar', id: interest.sender.id)}"
                           class="profile-avatar" alt="Profile Picture">
                    </div>

                    <div class="col-md-6">
                      <h5>
                        <g:link controller="user" action="show" id="${interest.sender.id}">
                          ${interest.sender}
                        </g:link>
                      </h5>

                      <p class="text-muted">
                        <i class="fa fa-calendar"></i>
                        <g:formatDate date="${interest.dateCreated}" format="dd MMM yyyy, hh:mm a"/>
                      </p>
                      <g:if test="${interest.message}">
                        <p class="interest-message">
                          <i class="fa fa-quote-left"></i>
                          ${interest.message}
                        </p>
                      </g:if>
                    </div>

                    <div class="col-md-4 text-right">
                      <div class="interest-status">
                        <span class="badge badge-${interest.status.value.toLowerCase()}">
                          ${interest.status.value.capitalize()}
                        </span>
                      </div>

                      <g:if test="${interest.status.value == 'pending'}">
                        <div class="interest-actions mt-2">
                          <button class="btn btn-success btn-sm"
                                  onclick="respondToInterest(${interest.id}, 'ACCEPTED')">
                            <i class="fa fa-check"></i> Accept
                          </button>
                          <button class="btn btn-danger btn-sm ml-2"
                                  onclick="respondToInterest(${interest.id}, 'DECLINED')">
                            <i class="fa fa-times"></i> Decline
                          </button>
                        </div>
                      </g:if>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </g:each>
        </div>
      </g:if>
      <g:else>
        <div class="empty-state text-center py-5">
          <i class="fa fa-heart-o fa-3x text-muted mb-3"></i>
          <h4>No interests received yet</h4>

          <p class="text-muted">When someone sends you an interest, it will appear here.</p>
        </div>
      </g:else>
    </div>
  </div>
</div>

<script>
  function respondToInterest(interestId, response) {
    if (confirm('Are you sure you want to ' + response.toLowerCase() + ' this interest?')) {
      $.ajax({
        url: '${createLink(action: "respondAjax")}',
        type: 'POST',
        data: {
          id: interestId,
          response: response
        },
        success: function (data) {
          if (data.success) {
            location.reload();
          } else {
            alert(data.message);
          }
        },
        error: function () {
          alert('An error occurred. Please try again.');
        }
      });
    }
  }
</script>
</body>
</html>
