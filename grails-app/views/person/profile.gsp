<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>My Profile - Matrimony App</title>
</head>

<body>
<div class="container mt-4">
  <div class="row">
    <div class="col-md-8">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h4>My Profile</h4>
          <g:link action="edit" class="btn btn-primary btn-sm">Edit Profile</g:link>
        </div>

        <div class="card-body">
          <g:if test='${flash.message}'>
            <div class="alert alert-success">${flash.message}</div>
          </g:if>
          <g:if test='${flash.error}'>
            <div class="alert alert-danger">${flash.error}</div>
          </g:if>

          <div class="row">
            <div class="col-md-6">
              <p><strong>Name:</strong> ${user.firstName} ${user.lastName}</p>

              <p><strong>Email:</strong> ${user.eMail}</p>

              <p><strong>Mobile:</strong> ${user.mobileNumber ?: 'Not provided'}</p>
            </div>

            <div class="col-md-6">
              <p><strong>Account Created:</strong> <g:formatDate date="${user.dateCreated}" format="dd/MM/yyyy"/></p>

              <p><strong>Last Updated:</strong> <g:formatDate date="${user.lastUpdated}" format="dd/MM/yyyy"/></p>

              <p><strong>Status:</strong>
                <span class="badge ${user.enabled ? 'background-success' : 'background-danger'}">
                  ${user.enabled ? 'Active' : 'Inactive'}
                </span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="col-md-4">
      <div class="card">
        <div class="card-header">
          <h5>Account Actions</h5>
        </div>

        <div class="card-body">
          <div class="d-grid gap-2">
            <g:link action="changePassword" class="btn btn-outline-primary">Change Password</g:link>
            <g:link controller="dashboard" class="btn btn-outline-secondary">Back to Dashboard</g:link>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
