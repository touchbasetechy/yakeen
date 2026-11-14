<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>

<!doctype html>
<html lang="en" class="no-js">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <script src="https://kit.fontawesome.com/bb4625bf55.js" crossorigin="anonymous"></script>
  <glass:badgeStyles/>

  <title>
  <g:layoutTitle default="Murad"/>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

  <asset:stylesheet src="application.css"/>

  <g:layoutHead/>
  <glass:snackbarStyles/>

  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Sans:ital,wght@0,300;0,400;0,500;1,300&family=DM+Mono:wght@300;400&display=swap"
    rel="stylesheet"/>

  <style>

  /* ═══════════════════════════════════════════════
     RESET
  ═══════════════════════════════════════════════ */
  *, *::before, *::after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  html, body {
    width: 100%;
    min-height: 100vh;
  }

  body {
    font-family: 'DM Sans', sans-serif;
    overflow-x: hidden;
  }

  /* ═══════════════════════════════════════════════
     BACKGROUND — ROSE VARIANT
     Fixed layer sits behind everything.
  ═══════════════════════════════════════════════ */
  .bg-root {
    position: fixed;
    inset: 0;
    z-index: 0;
    overflow: hidden;
    pointer-events: none;
    background: radial-gradient(ellipse at 16% 26%, #9f1239 0%, transparent 55%),
    radial-gradient(ellipse at 82% 12%, #db2777 0%, transparent 50%),
    radial-gradient(ellipse at 64% 80%, #7c3aed 0%, transparent 55%),
    radial-gradient(ellipse at 8% 82%, #be185d 0%, transparent 45%),
    #120814;
  }

  .bg-orbs {
    position: absolute;
    inset: 0;
  }

  .bg-orb {
    position: absolute;
    border-radius: 50%;
    will-change: transform;
    animation: orbFloat ease-in-out infinite;
  }

  @keyframes orbFloat {
    0%, 100% {
      transform: translate(0, 0) scale(1);
    }
    20% {
      transform: translate(32px, -48px) scale(1.07);
    }
    40% {
      transform: translate(-22px, 36px) scale(0.94);
    }
    60% {
      transform: translate(48px, 22px) scale(1.05);
    }
    80% {
      transform: translate(-28px, -18px) scale(1.02);
    }
  }

  .bg-orb:nth-child(1) {
    width: 580px;
    height: 580px;
    background: radial-gradient(circle, rgba(159, 18, 57, .52), transparent 65%);
    top: -140px;
    left: -90px;
    filter: blur(95px);
    animation-duration: 22s;
  }

  .bg-orb:nth-child(2) {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(219, 39, 119, .46), transparent 65%);
    top: -75px;
    right: -95px;
    filter: blur(90px);
    animation-duration: 28s;
    animation-delay: -8s;
  }

  .bg-orb:nth-child(3) {
    width: 420px;
    height: 420px;
    background: radial-gradient(circle, rgba(124, 58, 237, .42), transparent 65%);
    bottom: -80px;
    right: -55px;
    filter: blur(85px);
    animation-duration: 21s;
    animation-delay: -13s;
  }

  .bg-orb:nth-child(4) {
    width: 350px;
    height: 350px;
    background: radial-gradient(circle, rgba(190, 24, 93, .48), transparent 65%);
    bottom: -55px;
    left: -45px;
    filter: blur(80px);
    animation-duration: 25s;
    animation-delay: -5s;
  }

  .bg-orb:nth-child(5) {
    width: 280px;
    height: 280px;
    background: radial-gradient(circle, rgba(219, 39, 119, .26), transparent 65%);
    top: 45%;
    left: 44%;
    filter: blur(100px);
    animation-duration: 17s;
    animation-delay: -10s;
  }

  /* Film grain overlay */
  .bg-grain {
    position: absolute;
    inset: 0;
    opacity: .030;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.88' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
    background-size: 160px;
  }

  /* ═══════════════════════════════════════════════
     PAGE CHROME — sits above background
  ═══════════════════════════════════════════════ */

  /* Navbar and all page content must be above the fixed background */
  glass-navbar, nav, header, .page_header, main,
  g\:layoutBody, .container, .container-fluid,
  footer, glass-simplefooter {
    position: relative;
    z-index: 1;
  }

  body > *:not(.bg-root) {
    position: relative;
    z-index: 1;
  }

  /* ── Flash message ── */
  .flash-msg {
    margin: 16px auto;
    max-width: 920px;
    padding: 14px 20px;
    border-radius: 14px;
    background: rgba(159, 18, 57, .18);
    backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 100, 150, .25);
    display: flex;
    align-items: center;
    gap: 12px;
    color: rgba(255, 210, 220, .80);
    font-size: 13px;
    font-weight: 300;
  }

  .flash-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    flex-shrink: 0;
    background: rgba(219, 39, 119, .22);
    border: 1px solid rgba(255, 100, 150, .25);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
  }

  /* ── Glass cards ── */
  .glass-card {
    padding: 28px 24px;
    border-radius: 20px;
    background: rgba(255, 255, 255, .07);
    backdrop-filter: blur(24px) saturate(160%);
    -webkit-backdrop-filter: blur(24px) saturate(160%);
    border: 1px solid rgba(255, 180, 200, .15);
    box-shadow: 0 1px 0 rgba(255, 180, 200, .20) inset,
    0 0 0 .5px rgba(255, 255, 255, .06) inset,
    0 20px 60px rgba(0, 0, 0, .40);
    position: relative;
    overflow: hidden;
    transition: transform .25s, box-shadow .25s;
  }

  .glass-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 1px 0 rgba(255, 180, 200, .30) inset,
    0 0 0 .5px rgba(255, 255, 255, .10) inset,
    0 28px 80px rgba(0, 0, 0, .50);
  }

  .glass-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 12%;
    right: 12%;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 150, 180, .55), transparent);
  }

  .glass-card::after {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: 20px;
    background: radial-gradient(ellipse 80% 45% at 50% 0%, rgba(255, 100, 150, .07), transparent 65%);
    pointer-events: none;
  }

  /* ── Rose button ── */
  .btn-rose {
    padding: 10px 22px;
    border-radius: 12px;
    border: none;
    cursor: pointer;
    background: linear-gradient(135deg, #9f1239 0%, #db2777 50%, #7c3aed 100%);
    color: rgba(255, 240, 245, .92);
    font-family: 'DM Sans', sans-serif;
    font-size: 13px;
    font-weight: 500;
    letter-spacing: .04em;
    transition: opacity .2s, transform .15s, box-shadow .2s;
    box-shadow: 0 4px 20px rgba(219, 39, 119, .35);
  }

  .btn-rose:hover {
    opacity: .9;
    transform: translateY(-1px);
    box-shadow: 0 8px 28px rgba(219, 39, 119, .45);
  }

  .btn-rose:active {
    transform: translateY(0);
  }

  /* ── Form fields ── */
  .field {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 16px;
  }

  .field label {
    font-family: 'DM Mono', monospace;
    font-size: 9.5px;
    letter-spacing: .2em;
    text-transform: uppercase;
    color: rgba(255, 180, 200, .45);
  }

  .field input, .field select, .field textarea {
    padding: 11px 14px;
    border-radius: 10px;
    background: rgba(255, 255, 255, .06);
    border: 1px solid rgba(255, 150, 180, .18);
    color: rgba(255, 220, 230, .85);
    font-family: 'DM Sans', sans-serif;
    font-size: 13px;
    font-weight: 300;
    outline: none;
    transition: border-color .2s, box-shadow .2s;
  }

  .field input::placeholder,
  .field textarea::placeholder {
    color: rgba(255, 180, 200, .25);
  }

  .field input:focus,
  .field select:focus,
  .field textarea:focus {
    border-color: rgba(255, 100, 150, .45);
    box-shadow: 0 0 0 3px rgba(219, 39, 119, .12);
  }

  .field select option {
    background: #1e0814;
    color: rgba(255, 220, 230, .85);
  }

  /* ── Stat pills ── */
  .stat-pill {
    padding: 14px 28px;
    border-radius: 999px;
    background: rgba(255, 255, 255, .06);
    backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 150, 180, .15);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
  }

  .stat-num {
    font-family: 'DM Mono', monospace;
    font-size: 22px;
    font-weight: 400;
    background: linear-gradient(135deg, #fda4af, #c084fc);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .stat-label {
    font-family: 'DM Mono', monospace;
    font-size: 9px;
    letter-spacing: .22em;
    text-transform: uppercase;
    color: rgba(255, 180, 200, .35);
  }

  </style>
</head>

<noscript>
  <h2 style="color: #FF0000">Your browser either can't or won't execute JavaScript!</h2>
</noscript>

<body>

<!-- ═══════════════════════════════════════════════
         ROSE BACKGROUND LAYER — fixed, behind everything
    ═══════════════════════════════════════════════ -->
<div class="bg-root">
  <div class="bg-orbs">
    <div class="bg-orb"></div>

    <div class="bg-orb"></div>

    <div class="bg-orb"></div>

    <div class="bg-orb"></div>

    <div class="bg-orb"></div>
  </div>

  <div class="bg-grain"></div>
</div>

<!-- ═══════════════════════════════════════════════
         NAVBAR
    ═══════════════════════════════════════════════ -->
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
  <div class="page_header"></div>
</header>

<!-- ═══════════════════════════════════════════════
         FLASH MESSAGE
    ═══════════════════════════════════════════════ -->
<g:if test="${flash.message}">
  <div class="flash-msg">
    <div class="flash-icon">✦</div>
    <span>${flash.message}</span>
  </div>
</g:if>

<!-- ═══════════════════════════════════════════════
         PAGE BODY (layout body injects view content)
    ═══════════════════════════════════════════════ -->
<g:layoutBody/>

<!-- ═══════════════════════════════════════════════
         FOOTER
    ═══════════════════════════════════════════════ -->
<glass:simplefooter companyName="Murad Matrimony"/>

<asset:javascript src="application.js"/>

</body>
</html>
