<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Change Password - Matrimony App</title>
</head>

<body>
<div class="container mt-4">
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">
          <h4>Change Password</h4>
        </div>

        <div class="card-body">
          <g:if test='${flash.message}'>
            <div class="alert alert-success">${flash.message}</div>
          </g:if>
          <g:if test='${flash.error}'>
            <div class="alert alert-danger">${flash.error}</div>
          </g:if>

          <g:form action="changePassword" method="POST">
            <div class="mb-3">
              <label for="oldPassword" class="form-label">Current Password</label>
              <g:passwordField name="oldPassword" class="form-control" required="true"/>
            </div>

            <div class="mb-3">
              <label for="newPassword" class="form-label">New Password</label>
              <g:passwordField name="newPassword" class="form-control" required="true"/>
            </div>

            <div class="mb-3">
              <label for="confirmPassword" class="form-label">Confirm New Password</label>
              <g:passwordField name="confirmPassword" class="form-control" required="true"/>
            </div>

            <div class="d-grid gap-2">
              <g:submitButton name="submit" class="btn btn-primary" value="Change Password"/>
              <g:link action="profile" class="btn btn-outline-secondary">Cancel</g:link>
            </div>
          </g:form>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
