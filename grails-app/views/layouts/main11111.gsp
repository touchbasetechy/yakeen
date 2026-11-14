<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <title>
  <g:layoutTitle default="Matrimony App"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

  <asset:stylesheet src="application.css"/>
  <g:layoutHead/>
</head>

<body>
<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
  <div class="container">
    <g:link controller="home" class="navbar-brand">Matrimony App</g:link>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <sec:ifNotLoggedIn>
          <li class="nav-item">
            <g:link controller="auth" action="login" class="nav-link">Login</g:link>
          </li>
          <li class="nav-item">
            <g:link controller="auth" action="register" class="nav-link">Register</g:link>
          </li>
        </sec:ifNotLoggedIn>

        <sec:ifLoggedIn>
          <li class="nav-item">
            <g:link controller="dashboard" class="nav-link">Dashboard</g:link>
          </li>
          <sec:hasRole role="ROLE_ADMIN">
            <li class="nav-item">
              <g:link controller="admin" class="nav-link">Admin</g:link>
            </li>
          </sec:hasRole>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
              <sec:username/>
            </a>
            <ul class="dropdown-menu">
              <li><g:link controller="person" action="profile" class="dropdown-item">Profile</g:link></li>
              <li><hr class="dropdown-divider"></li>
              <li><g:link controller="logout" class="dropdown-item">Logout</g:link></li>
            </ul>
          </li>
        </sec:ifLoggedIn>
      </ul>
    </div>
  </div>
</nav>

<!-- Main Content -->
<main>
  <g:layoutBody/>
</main>

<!-- Footer -->
<footer class="bg-dark text-light py-4 mt-5">
  <div class="container">
    <div class="row">
      <div class="col-md-6">
        <p>&copy; 2025 Matrimony App. All rights reserved.</p>
      </div>

      <div class="col-md-6 text-end">
        <p>Built with Grails & Spring Security</p>
      </div>
    </div>
  </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<asset:javascript src="application.js"/>
</body>
</html>
