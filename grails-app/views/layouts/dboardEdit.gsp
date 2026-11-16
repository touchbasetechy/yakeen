<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>

<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
  <gbb:styles/>
  <gbcosmic:styles/>

  <glass:badgeStyles/>
  <glass:textInputStyles/>
  <glass:tooltipStyles/>

  <title>
  <g:layoutTitle default="${grailsApplication.metadata.getApplicationName()}"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <asset:stylesheet src="application.css"/>

  <g:layoutHead/>
  <glass:snackbarStyles/>
</head>
<noscript><h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2></noscript>

<body>
<gbcosmic:background/>
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
