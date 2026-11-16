<%-- grails-app/views/login/addAccount.gsp --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="welcome"/>
  <title>Signed in list - ${grailsApplication.metadata.getApplicationName()}</title>
  <glass:whiteAnchorStyles/>

  <style>
  :root {
    --glass-bg: rgba(255, 255, 255, 0.12);
    --glass-border: rgba(255, 255, 255, 0.25);
    --glass-shadow: rgba(0, 0, 0, 0.25);
    --text-primary: #f5f6fa;
    --text-secondary: rgba(245, 246, 250, 0.7);
  }

  .glass-card {
    width: 100%;
    max-width: 480px;
    background: var(--glass-bg);
    border: 1px solid var(--glass-border);
    border-radius: 24px;
    padding: 36px 32px;
    backdrop-filter: blur(20px) saturate(160%);
    -webkit-backdrop-filter: blur(20px) saturate(160%);
    box-shadow: 0 20px 40px var(--glass-shadow), inset 0 1px 0 rgba(255, 255, 255, 0.15);
    color: var(--text-primary);
    font-family: 'Segoe UI', 'Inter', system-ui, -apple-system, sans-serif;
    animation: fadeSlideIn 0.5s ease-out;
  }

  @keyframes fadeSlideIn {
    from {
      opacity: 0;
      transform: translateY(16px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .glass-card h4 {
    margin: 0 0 18px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 0.03em;
    text-transform: uppercase;
    color: var(--text-secondary);
  }

  .identity-list {
    list-style: none;
    margin: 0 0 8px;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .identity-list li {
    border-radius: 14px;
    overflow: hidden;
  }

  .identity-link {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 14px;
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 14px;
    text-decoration: none;
    color: var(--text-primary);
    transition: transform 0.18s ease, background 0.18s ease, border-color 0.18s ease;
  }

  .identity-link:hover {
    transform: translateY(-2px);
    background: rgba(255, 255, 255, 0.14);
    border-color: rgba(255, 255, 255, 0.3);
  }

  .avatar {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 14px;
    color: #fff;
    /* Background gradient is set per-item inline by glass:listItem so each
       row gets a distinct avatar color. */
  }

  .identity-meta {
    display: flex;
    flex-direction: column;
    min-width: 0;
  }

  .identity-name {
    font-weight: 600;
    font-size: 15px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .identity-email {
    font-size: 12.5px;
    color: var(--text-secondary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }


  .glass-divider {
    border: none;
    height: 1px;
    margin: 24px 0 18px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.25), transparent);
  }

  .add-account-hint {
    margin: 0;
    font-size: 13px;
    color: var(--text-secondary);
  }
  </style>
</head>

<body>
<glass:glassCard>
  <g:if test="${session.loggedInIdentities}">

    <h4>Already signed in as</h4>
    <ul class="identity-list">
      <g:each in="${session.loggedInIdentities}" var="idn">
        <glass:listItem uri="/u/${idn.slot}/${idn.controller}/${idn.action}"
                        name="${idn.displayName}"
                        email="${idn.email}"/>
      </g:each>
    </ul>
    <hr class="glass-divider"/>

    <glass:whiteAnchor text="Add another account" controller="auth" action="login"
                       wrapper="false"/>
  </g:if>

</glass:glassCard>
</body>
</html>
