<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row colset-2-its">
      <g:if test="${flash.error}">
        <div id="alert" class="alert alert-danger" style="display: block">${flash.error}</div>
      </g:if>
      <g:if test="${flash.message}">
        <div id="alert" class="alert alert-success" style="display: block">${flash.message}</div>
      </g:if>

    </section>
  </div>
</div>

</body>
</html>
