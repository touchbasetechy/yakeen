<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
  <glass:badgeStyles/>

  <title>
  <g:layoutTitle default="${grailsApplication.metadata.getApplicationName()}"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <%-- Bootstrap CSS --%>
  %{--  <asset:stylesheet href="bootstrap.min.css"/>--}%

  <%-- Your custom CSS (loads after Bootstrap so you can override) --%>
  <asset:stylesheet href="application.css"/>

  <%-- JavaScript bundle includes jQuery, Popper, Bootstrap in correct order --%>
  <asset:javascript src="application.js"/>
  <style>
  *, *::before, *::after {
    box-sizing: border-box;
  }

  body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    padding: 40px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 30px;
  }
  </style>
  <g:layoutHead/>

</head>
<noscript><h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2></noscript>

<body>
<glass:navbar
  brand="MURAD"
  brandUrl="/"
  brandImage="${asset.assetPath(src: 'matrimony-logo.png')}">
  <li>
    <glass:popOver
      id="autoPopover"
      title="✨ Hi! ${person?.firstName}${person?.lastName}"
      content="${popperMsg}"
      placement="bottom"
      variant="aurora"
      animation="slide"
      glowEffect="true"
      autoHideDelay="7000"
      localStorageName="isPopOverShown"
      localStorageValue="true"
      showWhenValue="false">
      <i class="fas fa-info-circle"></i>
    </glass:popOver>
  </li>

  <li>
    <glass:badgeLink text="${person?.firstName + " " + person?.lastName}" variant="warning"
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
<!-- SockJS -->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
<!-- STOMP.js -->
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js"></script>

</body>
</html>
