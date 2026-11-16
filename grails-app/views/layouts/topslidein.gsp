<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <title>
  <g:layoutTitle default="${grailsApplication.metadata.getApplicationName()}"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <asset:stylesheet src="application.css"/>
  <%-- <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script> --%>
  <g:layoutHead/>
</head>
<noscript><h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2></noscript>

<body class="animate__animated animate__delay-0s animate__bounceInDown">

<nav class="navbar navbar-expand-lg navbar-dark navbar-static-top shadow-sm custom-navbar" role="navigation">
  <div class="container-fluid">
    <a class="navbar-brand" href="/#"><asset:image src="matrimony-logo.png" alt="Matrimony Logo" width="50px"
                                                   height="50px"/>&nbsp;&nbsp;MURAD</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent"
            aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" aria-expanded="false" style="height: 0.8px;" id="navbarContent">
      <ul class="nav navbar-nav ml-auto">
        <g:pageProperty name="page.nav"/>
      </ul>
    </div>
  </div>
</nav>
<header>
  <div class="page_header">
  </div>
</header>
<g:layoutBody/>
<glass:simplefooter companyName="Murad Matrimony"/>

<script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
%{--<script src="https://unpkg.com/@barba/core"></script>--}%

<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
<asset:javascript src="application.js"/>
</body>
</html>
