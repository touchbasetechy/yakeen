<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
  %{--  <glass:snackbarStyles/>--}%

  <title>
  <g:layoutTitle default="Murad"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <asset:stylesheet src="application.css"/>

  <g:layoutHead/>
</head>
<noscript><h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2></noscript>

<body>

%{--<glass:navbar brand="MURAD" brandUrl="/">--}%
%{--  <glass:navItem href="/" active="${true}">Home</glass:navItem>--}%
%{--  <glass:navItem href="/about">About</glass:navItem>--}%
%{--  <glass:navItem href="/services">Services</glass:navItem>--}%
%{--  <glass:navItem href="/contact">Contact</glass:navItem>--}%
%{--</glass:navbar>--}%
<glass:simplenavbar
  brand="MURAD"
  brandUrl="/"
  brandImage="${asset.assetPath(src: 'matrimony-logo.png')}"/>
<header>
  <div class="page_header">
  </div>
</header>

<g:layoutBody/>
<asset:javascript src="application.js"/>

</body>
<glass:simplefooter companyName="Murad Matrimony"/>

</html>
