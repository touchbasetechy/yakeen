<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Admin Panel - Matrimony App</title>
</head>

<body>
<sec:ifAllGranted roles="ROLE_ADMIN">
  <div class="container mt-4">
    <h2>Admin Panel</h2>

    <div class="card mt-4">
      <div class="card-header">
        <h4>All Users</h4>
      </div>

      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-striped">
            <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Status</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${users}" var="user">
              <tr>
                <td>${user.id}</td>
                <td>${user.firstName} ${user.lastName}</td>
                <td>${user.eMail}</td>
                <td>
                  <span class="badge ${user.enabled ? 'badge-success' : 'badge-danger'}">
                    ${user.enabled ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td><g:formatDate date="${user.dateCreated}" format="dd/MM/yyyy"/></td>
                <td>
                  <g:link controller="admin" action="userDetails" id="${user.id}" class="btn btn-sm btn-info">
                    View Details
                  </g:link>
                </td>
              </tr>
            </g:each>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div class="mt-3">
      <g:link controller="dashboard" class="btn btn-primary">Back to Dashboard</g:link>
    </div>
  </div>
</sec:ifAllGranted>
</body>
</html>
