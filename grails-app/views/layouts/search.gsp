<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>

<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
  <gbb:styles/>
  <gbrose:styles/>

  <glass:badgeStyles/>
  <glass:textInputStyles/>
  <glass:tooltipStyles/>
  <glass:styles/>

  <title>
  <g:layoutTitle default="Murad"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <asset:stylesheet src="application.css"/>

  <g:layoutHead/>
  <glass:snackbarStyles/>
  <style>
  *, *::before, *::after {
    box-sizing: border-box;
  }

  /*body {*/
  /*  min-height: 100vh;*/
  /*  margin: 0;*/
  /*  font-family: 'DM Sans', sans-serif;*/
  /*  background: ;*/
  /*background: radial-gradient(ellipse at 15% 25%, #3a6fff 0%, transparent 55%),*/
  /*radial-gradient(ellipse at 85% 10%, #a855f7 0%, transparent 50%),*/
  /*radial-gradient(ellipse at 70% 80%, #06b6d4 0%, transparent 55%),*/
  /*radial-gradient(ellipse at 10% 85%, #ec4899 0%, transparent 45%),*/
  /*#0d1230;*/
  /*  display: flex;*/
  /*  flex-direction: column;*/
  /*  align-items: center;*/
  /*  justify-content: center;*/
  /*  padding: 48px 20px;*/
  /*  gap: 28px;*/
  /*  padding-top: calc(200px + 24px) !important;*/
  /*}*/
  </style>

</head>
<noscript><h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2></noscript>

<body>
<gbrose:background/>

<glass:navbar
  brand="MURAD"
  brandUrl="/"
  brandImage="${asset.assetPath(src: 'matrimony-logo.png')}">
  <li>
    <glass:badgeLink text="${SecurityContextHolder.context?.authentication?.name}" variant="warning"
                     controller="profile"
                     action="index" faIcon="fas fa-user"
                     size="md"/>
  </li>
  <li>
    <g:form controller="login" action="logoutSlot" method="POST">
      <g:hiddenField name="slot" value="${params.slot}"/>
      <glass:cardBtn label="Sign Out" variant="cosmic" faIcon="fa-solid fa-right-from-bracket" type="submit"/>
    </g:form>
  </li>
</glass:navbar>

<header>
  <div class="page_header">
  </div>
</header>

<g:layoutBody/>
<glass:simplefooter companyName="Murad Matrimony"/>
<asset:javascript src="application.js"/>
</body>
</html>
