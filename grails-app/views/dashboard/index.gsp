<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Dashboard - Matrimony App</title>
</head>

<body>
<div class="container mt-4">
  <div class="row">
    <div class="col-md-12">
      <h2>Welcome, ${user.firstName} ${user.lastName}!</h2>

      <div class="card mt-4">
        <div class="card-header">
          <h4>Your Profile</h4>
        </div>

        <div class="card-body">
          <p><strong>Name:</strong> ${user.firstName} ${user.lastName}</p>

          <p><strong>Email:</strong> ${user.eMail}</p>

          <p><strong>Mobile:</strong> ${user.mobileNumber ?: 'Not provided'}</p>

          <p><strong>Account Status:</strong>
            <span class="badge ${user.enabled ? 'badge-success' : 'badge-danger'}">
              ${user.enabled ? 'Active' : 'Inactive'}
            </span>
          </p>
        </div>
      </div>

      <div class="mt-3">
        <sec:ifAllGranted roles="ROLE_ADMIN">
          <g:link controller="admin" action="index" class="btn btn-secondary">Admin Panel</g:link>
        </sec:ifAllGranted>
        <g:link controller="logout" class="btn btn-danger">Logout</g:link>
      </div>
    </div>
  </div>
</div>
</body>
</html>
