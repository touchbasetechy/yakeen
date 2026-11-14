<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Sent Interests</title>
  <asset:stylesheet src="interests.css"/>
</head>

<body>
<div class="container">
  <div class="row">
    <div class="col-md-12">
      <h2>Sent Interests</h2>

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
              <h4>${stats?.get('WITHDRAWN') ?: 0}</h4>

              <p>Withdrawn</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Filter Tabs -->
      <ul class="nav nav-tabs mb-3">
        <li class="nav-item">
          <g:link class="nav-link ${!params.status ? 'active' : ''}"
                  action="sent">All</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'PENDING' ? 'active' : ''}"
                  action="sent" params="[status: 'PENDING']">Pending</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'ACCEPTED' ? 'active' : ''}"
                  action="sent" params="[status: 'ACCEPTED']">Accepted</g:link>
        </li>
        <li class="nav-item">
          <g:link class="nav-link ${params.status == 'DECLINED' ? 'active' : ''}"
                  action="sent" params="[status: 'DECLINED']">Declined</g:link>
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
                      <img src="${createLink(controller: 'user', action: 'avatar', id: interest.receiver.id)}"
                           class="profile-avatar" alt="Profile Picture">
                    </div>

                    <div class="col-md-6">
                      <h5>
                        <g:link controller="user" action="show" id="${interest.receiver.id}">
                          ${interest.receiver}
                        </g:link>
                      </h5>

                      <p class="text-muted">
                        <i class="fa fa-calendar"></i>
                        Sent on <g:formatDate date="${interest.dateCreated}" format="dd MMM yyyy, hh:mm a"/>
                      </p>
                      <g:if test="${interest.respondedDate}">
                        <p class="text-muted">
                          <i class="fa fa-reply"></i>
                          Responded on <g:formatDate date="${interest.respondedDate}" format="dd MMM yyyy, hh:mm a"/>
                        </p>
                      </g:if>
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
                          <button class="btn btn-outline-danger btn-sm"
                                  onclick="withdrawInterest(${interest.id})">
                            <i class="fa fa-undo"></i> Withdraw
                          </button>
                        </div>
                      </g:if>

                      <g:if test="${interest.status.value == 'accepted'}">
                        <div class="interest-actions mt-2">
                          <g:link controller="message" action="chat"
                                  id="${interest.receiver.id}"
                                  class="btn btn-primary btn-sm">
                            <i class="fa fa-comment"></i> Chat
                          </g:link>
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
          <i class="fa fa-paper-plane-o fa-3x text-muted mb-3"></i>
          <h4>No interests sent yet</h4>

          <p class="text-muted">Start browsing profiles and send interests to connect with others.</p>
          <g:link controller="user" action="search" class="btn btn-primary">
            <i class="fa fa-search"></i> Browse Profiles
          </g:link>
        </div>
      </g:else>
    </div>
  </div>
</div>

<script>
  function withdrawInterest(interestId) {
    if (confirm('Are you sure you want to withdraw this interest?')) {
      window.location.href = '${createLink(action: "withdraw")}/' + interestId;
    }
  }
</script>
</body>
</html>
