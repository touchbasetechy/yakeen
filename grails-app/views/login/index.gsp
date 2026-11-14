<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Welcome - Matrimony App</title>
</head>

<body>
<div class="container">
  <div class="jumbotron mt-5">
    <h1 class="display-4">Welcome to Matrimony App</h1>

    <p class="lead">Find your perfect match with our comprehensive matrimonial platform.</p>
    <hr class="my-4">

    <sec:ifNotLoggedIn>
      <p>Join thousands of happy couples who found their soulmate through our platform.</p>
      <g:link controller="auth" action="register" class="btn btn-primary btn-lg">Get Started</g:link>
      <g:link controller="login" action="auth" class="btn btn-outline-secondary btn-lg ml-2">Login</g:link>
    </sec:ifNotLoggedIn>

    <sec:ifLoggedIn>
      <p>Welcome back, <sec:username/>!</p>
      <g:link controller="dashboard" class="btn btn-primary btn-lg">Go to Dashboard</g:link>
    </sec:ifLoggedIn>
  </div>

  <div class="row">
    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Find Your Match</h5>

          <p class="card-text">Browse through thousands of verified profiles to find your perfect partner.</p>
        </div>
      </div>
    </div>

    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Secure & Private</h5>

          <p class="card-text">Your privacy is our priority. All profiles are verified and secure.</p>
        </div>
      </div>
    </div>

    <div class="col-md-4">
      <div class="card">
        <div class="card-body">
          <h5 class="card-title">Success Stories</h5>

          <p class="card-text">Join thousands of couples who found love through our platform.</p>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
