<!doctype html>
<html>
<head>
  <meta name="layout" content="dboard"/>
  <!-- Font Awesome CDN (add this if not already in your layout) -->
  %{--  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>--}%

  <!-- Modern Glassmorphic Styles -->
  <glass:modernBtnStyles/>
  <glass:modernCardLinkStyles/>

  <!-- Floating Toolbar Styles -->
  <glass:floatingToolbarStyles/>
  <glass:adCardButtonStyles/>
  <style>
  .chat-textarea:focus,
  .chat-textarea:active {
    background: transparent !important;
    background-color: transparent !important;
    color: rgba(255, 255, 255, 0.9) !important;
    outline: none !important;
    box-shadow: none !important;
    -webkit-box-shadow: none !important;
    border: none !important;
  }

  /* Override browser autofill background */
  .chat-textarea:-webkit-autofill,
  .chat-textarea:-webkit-autofill:focus,
  .chat-textarea:-webkit-autofill:active {
    -webkit-box-shadow: 0 0 0 1000px transparent inset !important;
    -webkit-text-fill-color: rgba(255, 255, 255, 0.9) !important;
    transition: background-color 9999s ease-in-out 0s;
  }

  /* Custom Dashboard Styles */
  .dashboard-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 2rem;
    position: relative;
    z-index: 1;
  }

  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 3rem;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .dashboard-title-section {
    text-align: left;
  }

  .dashboard-title {
    font-family: 'Syne', sans-serif;
    font-size: clamp(2rem, 4vw, 3rem);
    font-weight: 800;
    color: #ffffff;
    margin-bottom: 0.5rem;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .dashboard-title i {
    font-size: 0.85em;
    opacity: 0.85;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  /* View Sections */
  .view-section {
    transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .view-section.hidden {
    display: none !important;
  }

  .view-section.visible {
    display: block !important;
    animation: fadeIn 0.5s ease;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2rem;
  }

  .section-label {
    font-size: 1.5rem;
    font-weight: 700;
    color: #ffffff;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    display: flex;
    align-items: center;
    gap: 0.6rem;
  }

  .section-label i {
    font-size: 1.1rem;
    opacity: 0.75;
  }

  .empty-state {
    text-align: center;
    padding: 3rem;
    background: rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(16px);
    border: 1.5px solid rgba(255, 255, 255, 0.18);
    border-radius: 24px;
    color: rgba(255, 255, 255, 0.7);
    font-size: 1.125rem;
  }

  .empty-state i.main-icon {
    font-size: 3.5rem;
    margin-bottom: 1rem;
    display: block;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .empty-state p {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .empty-state p i {
    font-size: 0.95em;
    opacity: 0.6;
  }

  /* ── Glassmorphic Chat Panel ─────────────────────── */
  .glass-chat-panel {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: min(70vh, 680px);
    background: rgba(10, 10, 25, 0.72);
    backdrop-filter: blur(24px) saturate(180%);
    -webkit-backdrop-filter: blur(24px) saturate(180%);
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 24px 24px 0 0;
    display: flex;
    flex-direction: column;
    transform: translateY(100%);
    transition: transform 0.42s cubic-bezier(0.32, 0.72, 0, 1);
    z-index: 999;
    overflow: hidden;
  }

  .glass-chat-panel.open {
    transform: translateY(0);
  }

  .panel-handle {
    display: flex;
    justify-content: center;
    padding: 10px 0 4px;
    cursor: pointer;
    flex-shrink: 0;
  }

  .handle-pill {
    width: 40px;
    height: 4px;
    background: rgba(255, 255, 255, 0.22);
    border-radius: 2px;
    transition: background 0.2s;
  }

  .panel-handle:hover .handle-pill {
    background: rgba(255, 255, 255, 0.5);
  }

  .chat-panel-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0 1rem 0.75rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    flex-shrink: 0;
  }

  .chat-header-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea, #764ba2);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    color: white;
    font-weight: 600;
    flex-shrink: 0;
  }

  .chat-header-info {
    flex: 1;
    min-width: 0;
  }

  .chat-header-name {
    font-size: 15px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.95);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .chat-header-name i {
    font-size: 0.85em;
    opacity: 0.7;
  }

  .chat-header-status {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.5);
  }

  .chat-status-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #22c55e;
    flex-shrink: 0;
    box-shadow: 0 0 6px rgba(34, 197, 94, 0.7);
  }

  .chat-icon-btn {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background 0.2s;
    color: rgba(255, 255, 255, 0.7);
    font-size: 14px;
  }

  .chat-icon-btn:hover {
    background: rgba(255, 255, 255, 0.15);
    color: rgba(255, 255, 255, 1);
  }

  /* Keep old SVG btn style too, in case used elsewhere */
  .chat-icon-btn svg {
    width: 15px;
    height: 15px;
    stroke: rgba(255, 255, 255, 0.7);
    fill: none;
    stroke-width: 1.8;
    stroke-linecap: round;
    stroke-linejoin: round;
  }

  .online-users-bar {
    padding: 0.5rem 1rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    display: flex;
    gap: 8px;
    overflow-x: auto;
    flex-shrink: 0;
    scrollbar-width: none;
  }

  .online-users-bar::-webkit-scrollbar {
    display: none;
  }

  .user-chip {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
    padding: 4px 10px 4px 5px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.07);
    border: 1px solid rgba(255, 255, 255, 0.1);
    font-size: 12px;
    color: rgba(255, 255, 255, 0.75);
  }

  .user-chip-avatar {
    width: 22px;
    height: 22px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 9px;
    font-weight: 700;
    color: white;
    position: relative;
  }

  .user-chip-avatar::after {
    content: '';
    position: absolute;
    bottom: 0;
    right: 0;
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #22c55e;
    border: 1.5px solid #0d0d1a;
  }

  .chat-messages-area {
    flex: 1;
    overflow-y: auto;
    padding: 1rem;
    display: flex;
    flex-direction: column;
    gap: 12px;
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.1) transparent;
  }

  .chat-messages-area::-webkit-scrollbar {
    width: 3px;
  }

  .chat-messages-area::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 2px;
  }

  .msg-row {
    display: flex;
    gap: 8px;
    align-items: flex-end;
  }

  .msg-row.me {
    flex-direction: row-reverse;
  }

  .msg-avatar {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    font-weight: 700;
    color: white;
    flex-shrink: 0;
  }

  .msg-bubble {
    max-width: min(72%, 320px);
    padding: 10px 14px;
    border-radius: 18px 18px 18px 4px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.12);
    font-size: 14px;
    line-height: 1.5;
    color: rgba(255, 255, 255, 0.9);
    word-break: break-word;
  }

  .msg-row.me .msg-bubble {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.55), rgba(118, 75, 162, 0.55));
    border-color: rgba(102, 126, 234, 0.4);
    border-radius: 18px 18px 4px 18px;
  }

  .msg-meta {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.3);
    margin-top: 4px;
    text-align: left;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .msg-row.me .msg-meta {
    text-align: right;
    flex-direction: row-reverse;
  }

  .msg-meta i {
    font-size: 9px;
    opacity: 0.7;
  }

  .date-divider {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 4px 0;
  }

  .date-divider::before, .date-divider::after {
    content: '';
    flex: 1;
    height: 1px;
    background: rgba(255, 255, 255, 0.08);
  }

  .date-divider span {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.3);
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .date-divider span i {
    font-size: 9px;
  }

  .typing-indicator {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 0 1rem 0.5rem;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
  }

  .typing-indicator i {
    font-size: 11px;
  }

  .typing-dots {
    display: flex;
    gap: 4px;
    padding: 8px 12px;
    background: rgba(255, 255, 255, 0.08);
    border-radius: 18px;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }

  .typing-dots span {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.5);
    animation: typingBounce 1.2s infinite;
  }

  .typing-dots span:nth-child(2) {
    animation-delay: 0.2s;
  }

  .typing-dots span:nth-child(3) {
    animation-delay: 0.4s;
  }

  @keyframes typingBounce {
    0%, 60%, 100% {
      transform: translateY(0);
      opacity: 0.5;
    }
    30% {
      transform: translateY(-5px);
      opacity: 1;
    }
  }

  .chat-input-bar {
    padding: 0.75rem 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    display: flex;
    align-items: flex-end;
    gap: 8px;
    flex-shrink: 0;
  }

  .input-extras {
    display: flex;
    gap: 4px;
  }

  .chat-input-wrap {
    flex: 1;
    background: rgba(255, 255, 255, 0.07);
    border: 1px solid rgba(255, 255, 255, 0.14);
    border-radius: 24px;
    display: flex;
    align-items: flex-end;
    padding: 8px 12px;
    transition: border-color 0.2s, background 0.2s;
  }

  .chat-input-wrap:focus-within {
    border-color: rgba(102, 126, 234, 0.6);
    background: rgba(255, 255, 255, 0.1);
  }

  .chat-textarea {
    flex: 1;
    background: none;
    border: none;
    outline: none;
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    line-height: 1.5;
    resize: none;
    max-height: 100px;
    overflow-y: auto;
    font-family: inherit;
  }

  .chat-textarea::placeholder {
    color: rgba(255, 255, 255, 0.3);
  }

  /* ── Clear button inside the input wrap ── */
  .chat-clear-btn {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    border-radius: 50%;
    border: none;
    background: rgba(255, 255, 255, 0.12);
    color: rgba(255, 255, 255, 0.45);
    font-size: 10px;
    display: none; /* hidden until there is text */
    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-bottom: 2px;
    transition: background 0.18s, color 0.18s, transform 0.15s;
    line-height: 1;
  }

  .chat-clear-btn:hover {
    background: rgba(239, 68, 68, 0.35);
    color: rgba(255, 255, 255, 0.9);
    transform: scale(1.15);
  }

  .chat-clear-btn:active {
    transform: scale(0.9);
  }

  .chat-clear-btn.visible {
    display: flex;
  }

  .chat-send-btn {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    border: none;
    cursor: pointer;
    flex-shrink: 0;
    background: linear-gradient(135deg, #667eea, #764ba2);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    transition: transform 0.15s, box-shadow 0.15s;
    color: white;
    font-size: 15px;
  }

  .chat-send-btn:hover {
    transform: scale(1.08);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
  }

  .chat-send-btn:active {
    transform: scale(0.95);
  }

  /* ── Chat empty / no-messages state ─── */
  .chat-empty-state {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
    color: rgba(255, 255, 255, 0.25);
  }

  .chat-empty-state i {
    font-size: 2.5rem;
    opacity: 0.4;
  }

  .chat-empty-state p {
    font-size: 13px;
  }

  /* ── Emoji picker trigger — sized to match .chat-icon-btn ── */
  .chat-icon-btn.ep-trigger-btn {
    font-size: 18px;
    width: 34px;
    height: 34px;
    padding: 0;
    /* Inherit the glassmorphic button look */
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.7);
  }

  .chat-icon-btn.ep-trigger-btn:hover {
    background: rgba(255, 255, 255, 0.15);
    transform: scale(1.08);
  }
  </style>
</head>

<body>
<div class="dashboard-container">

  <!-- Dashboard Header -->
  <div class="dashboard-header">
    <div class="dashboard-title-section">
      <h1 class="dashboard-title">
        Dashboard
      </h1>
    </div>
    <glassToggle:toggle name="showIcons" id="showIcons" label="Show Icons"
                        onChange="handleToggle(checked)"/>
  </div>

  <!-- Alerts -->
  <g:if test="${flash.error}">
    <glass2:alert message="${flash.error}" type="error"/>
  </g:if>
  <g:if test="${flash.message}">
    <glass2:alert message="${flash.message}" type="success"/>
  </g:if>

  <!-- ═══════════════════════════════════════════════════════ -->
  <!-- List Items View (Text-based)                           -->
  <!-- ═══════════════════════════════════════════════════════ -->
  <div id="listItems" class="view-section hidden">

    <!-- Add Section -->
    <section style="margin-bottom: 4rem;">
      <div class="section-header">
        <title:twotone muted="Add" strong="Your Information"/>
      </div>

      <glass:cardGrid columns="4" gap="lg">
        <g:if test="${!person?.personDetails}">
          <glass:badgeLink text="Personal Details" variant="success" controller="DBoard"
                           action="personDetails"
                           size="lg" faIcon="fas fa-user"/>
        </g:if>
        <g:if test="${!person?.spouseDetails}">
          <glass:badgeLink text="Spouse Preferences" variant="success" controller="DBoard"
                           action="spouseDetails"
                           size="lg" faIcon="fas fa-heart-pulse"/>
        </g:if>
        <g:if test="${!person?.familyDetails}">
          <glass:badgeLink text="Family Details" variant="success" controller="DBoard"
                           action="familyDetails"
                           size="lg" faIcon="fas fa-people-group"/>
        </g:if>
        <g:if test="${!person?.spouseFamilyDetails}">
          <glass:badgeLink text="Spouse Family Preferences" variant="success" controller="DBoard"
                           action="spouseFamilyDetails" size="lg" faIcon="fas fa-home"/>
        </g:if>
        <glass:badgeLink text="Contact Details" variant="success" controller="DBoard"
                         action="contactDetails"
                         size="lg" faIcon="fas fa-address-book"/>
        <glass:badgeLink text="Education Details" variant="success" controller="DBoard"
                         action="educationDetails"
                         size="lg" faIcon="fas fa-graduation-cap"/>
        <glass:badgeLink text="Profession Details" variant="success" controller="DBoard"
                         action="professionDetails"
                         size="lg" faIcon="fas fa-briefcase"/>
        <glass:badgeLink text="Address" variant="success" controller="DBoard" action="address"
                         size="lg" faIcon="fas fa-map-marker-alt"/>
        <glass:badgeLink text="Hobbies" variant="success" controller="DBoard"
                         action="listHobbiesToAdd"
                         size="lg" faIcon="fas fa-palette"/>
        <g:if test="${!person?.additionalSpouseDetails}">
          <glass:badgeLink text="Additional Spouse Details" variant="success" controller="DBoard"
                           action="addAdditionalSpouseDetails" size="lg" faIcon="fas fa-user-plus"/>
        </g:if>
      </glass:cardGrid>
    </section>

    <!-- Edit Section -->
    <section style="margin-bottom: 4rem;">
      <div class="section-header">
        <title:twotone muted="Edit" strong="Your Information"/>
      </div>
      <g:if
        test="${(!person?.personDetails) && (!person?.spouseDetails) && (!person?.familyDetails) && (!person?.spouseFamilyDetails) && (!person?.contactDetails) && (!person?.educationDetails) && (!person?.professionDetails) && (!person?.address) && (!person?.additionalSpouseDetails)}">
        <div class="empty-state">
          <i class="fas fa-folder-open main-icon"></i>

          <p>
            <i class="fas fa-circle-info"></i>
            Nothing to edit yet. Please add your information first.
          </p>
        </div>
      </g:if>
      <g:else>
        <glass:cardGrid columns="4" gap="lg">
          <g:if test="${person?.personDetails}">
            <glass:badgeLink text="Personal Details" variant="info" controller="DBoard"
                             action="editPersonDetails"
                             size="lg" faIcon="fas fa-user-pen"/>
          </g:if>
          <g:if test="${person?.spouseDetails}">
            <glass:badgeLink text="Spouse Preferences" variant="info" controller="DBoard"
                             action="editSpouseDetails"
                             size="lg" faIcon="fas fa-heart-pulse"/>
          </g:if>
          <g:if test="${person?.familyDetails}">
            <glass:badgeLink text="Family Details" variant="info" controller="DBoard"
                             action="editFamilyDetails"
                             size="lg" faIcon="fas fa-people-group"/>
          </g:if>
          <g:if test="${person?.spouseFamilyDetails}">
            <glass:badgeLink text="Spouse Family Preferences" variant="info" controller="DBoard"
                             action="editSpouseFamilyDetails" size="lg" faIcon="fas fa-home"/>
          </g:if>
          <g:if test="${person?.contactDetails}">
            <glass:badgeLink text="Contact Details" variant="info" controller="DBoard"
                             action="editContactDetails"
                             size="lg" faIcon="fas fa-address-card"/>
          </g:if>
          <g:if test="${person?.educationDetails}">
            <glass:badgeLink text="Education Details" variant="info" controller="DBoard"
                             action="editEducationDetails" size="lg" faIcon="fas fa-book-open"/>
          </g:if>
          <g:if test="${person?.professionDetails}">
            <glass:badgeLink text="Profession Details" variant="info" controller="DBoard"
                             action="editProfessionDetails" size="lg" faIcon="fas fa-building"/>
          </g:if>
          <g:if test="${person?.address}">
            <glass:badgeLink text="Address" variant="info" controller="DBoard"
                             action="editAddressDetails"
                             size="lg" faIcon="fas fa-location-dot"/>
          </g:if>
          <g:if test="${person?.hobbies}">
            <glass:badgeLink text="Hobbies" variant="info" controller="DBoard"
                             action="listHobbiesToEdit"
                             size="lg" faIcon="fas fa-wand-sparkles"/>
          </g:if>
          <g:if test="${person?.additionalSpouseDetails}">
            <glass:badgeLink text="Additional Spouse Details" variant="info" controller="DBoard"
                             action="editAdditionalSpouseDetails" size="lg" faIcon="fas fa-user-gear"/>
          </g:if>
        </glass:cardGrid>
      </g:else>
    </section>
  </div>

  <!-- ═══════════════════════════════════════════════════════ -->
  <!-- List Icons View (Card-based)                           -->
  <!-- ═══════════════════════════════════════════════════════ -->
  <div id="listIcons" class="view-section hidden">

    <!-- Add Section -->
    <section style="margin-bottom: 4rem;">
      <div class="section-header">
        <title:twotone muted="Add" strong="Your Information"/>
      </div>
      <glass:cardGrid columns="4" gap="lg">
        <g:if test="${!person?.personDetails}">
          <glass:cardLink title="Personal Details" faIcon="fas fa-user"
                          controller="DBoard" action="personDetails" variant="primary"
                          id="add-icon-1"/>
        </g:if>
        <g:if test="${!person?.spouseDetails}">
          <glass:cardLink title="Spouse Preferences" faIcon="fas fa-heart-pulse"
                          controller="DBoard" action="spouseDetails" variant="primary"
                          id="add-icon-2"/>
        </g:if>
        <g:if test="${!person?.familyDetails}">
          <glass:cardLink title="Family Details" faIcon="fas fa-people-group"
                          controller="DBoard" action="familyDetails" variant="primary"
                          id="add-icon-3"/>
        </g:if>
        <g:if test="${!person?.spouseFamilyDetails}">
          <glass:cardLink title="Spouse Family Preferences"
                          faIcon="fas fa-home"
                          controller="DBoard" action="spouseFamilyDetails" variant="primary"
                          id="add-icon-4"/>
        </g:if>
        <glass:iconCard title="Contact Details" icon="fas fa-address-book" variant="primary"
                        controller="DBoard" action="contactDetails" id="add-icon-5"/>
        <glass:iconCard title="Education Details" icon="fas fa-graduation-cap" variant="primary"
                        controller="DBoard" action="educationDetails" id="add-icon-6"/>
        <glass:iconCard title="Profession Details" icon="fas fa-briefcase" variant="primary"
                        controller="DBoard" action="professionDetails" id="add-icon-7"/>
        <glass:iconCard title="Address" icon="fas fa-map-marker-alt" variant="primary"
                        controller="DBoard" action="address" id="add-icon-8"/>
        <glass:iconCard title="Hobbies" icon="fas fa-palette" variant="primary"
                        controller="DBoard" action="listHobbiesToAdd" id="add-icon-9"/>
        <g:if test="${!person?.additionalSpouseDetails}">
          <glass:iconCard title="Additional Spouse Details" icon="fas fa-user-plus" variant="primary"
                          controller="DBoard" action="addAdditionalSpouseDetails"
                          id="add-icon-10"/>
        </g:if>
      </glass:cardGrid>
    </section>

    <!-- Edit Section -->
    <section style="margin-bottom: 4rem;">
      <div class="section-header">
        <title:twotone muted="Edit" strong="Your Information"/>
      </div>
      <g:if
        test="${(!person?.personDetails) && (!person?.spouseDetails) && (!person?.familyDetails) && (!person?.spouseFamilyDetails) && (!person?.contactDetails) && (!person?.educationDetails) && (!person?.professionDetails) && (!person?.address) && (!person?.additionalSpouseDetails)}">
        <div class="empty-state">
          <i class="fas fa-folder-open main-icon"></i>

          <p>
            <i class="fas fa-circle-info"></i>
            Nothing to edit yet. Please add your information first.
          </p>
        </div>
      </g:if>
      <g:else>
        <glass:cardGrid columns="4" gap="lg">
          <g:if test="${person?.personDetails}">
            <glass:cardLink title="Personal Details" faIcon="fas fa-user"
                            controller="DBoard" action="editPersonDetails" variant="primary"
                            id="edit-icon-1"/>
          </g:if>
          <g:if test="${person?.spouseDetails}">
            <glass:cardLink title="Spouse Preferences" faIcon="fas fa-heart-pulse"
                            controller="DBoard" action="editSpouseDetails" variant="primary"
                            id="edit-icon-2"/>
          </g:if>
          <g:if test="${person?.familyDetails}">
            <glass:cardLink title="Family Details" faIcon="fas fa-people-group"
                            controller="DBoard" action="editFamilyDetails" variant="primary"
                            id="edit-icon-3"/>
          </g:if>
          <g:if test="${person?.spouseFamilyDetails}">
            <glass:cardLink title="Spouse Family Preferences"
                            faIcon="fas fa-home"
                            controller="DBoard" action="editSpouseFamilyDetails" variant="primary"
                            id="edit-icon-4"/>
          </g:if>
          <g:if test="${person?.contactDetails}">
            <glass:iconCard title="Contact Details" icon="fas fa-address-card" variant="primary"
                            controller="DBoard" action="editContactDetails" id="edit-icon-5"/>
          </g:if>
          <g:if test="${person?.educationDetails}">
            <glass:iconCard title="Education Details" icon="fas fa-book-open" variant="primary"
                            controller="DBoard" action="editEducationDetails" id="edit-icon-6"/>
          </g:if>
          <g:if test="${person?.professionDetails}">
            <glass:iconCard title="Profession Details" icon="fas fa-building" variant="primary"
                            controller="DBoard" action="editProfessionDetails" id="edit-icon-7"/>
          </g:if>
          <g:if test="${person?.address}">
            <glass:iconCard title="Address" icon="fas fa-location-dot" variant="primary"
                            controller="DBoard" action="editAddressDetails" id="edit-icon-8"/>
          </g:if>
          <g:if test="${person?.hobbies}">
            <glass:iconCard title="Hobbies" icon="fas fa-wand-sparkles" variant="primary"
                            controller="DBoard" action="listHobbiesToEdit" id="edit-icon-9"/>
          </g:if>
          <g:if test="${person?.additionalSpouseDetails}">
            <glass:iconCard title="Additional Spouse Details" icon="fas fa-user-gear" variant="primary"
                            controller="DBoard" action="editAdditionalSpouseDetails"
                            id="edit-icon-10"/>
          </g:if>
        </glass:cardGrid>
      </g:else>
    </section>
  </div>

  <!-- Chat FAB -->
  <div style="position: fixed; bottom: 2rem; right: 2rem; z-index: 998;">
    <glass:modernBtn text="Chat" icon="fas fa-comments" variant="gradient" size="lg" id="openButton" pulse="true"/>
  </div>

</div>

<!-- Floating Toolbar -->
<glass:floatingToolbar slot="${params.slot}">
  <glass:toolbarButton icon="fas fa-magnifying-glass" controller="search" action="index" title="Search Users"/>
  <glass:toolbarButton icon="fas fa-ticket" controller="DBoard"
                       action="listTicketTypeForAddTicket" title="Tickets"/>
  <glass:toolbarButton icon="fas fa-images" controller="photo" action="index" title="Photo Gallery"/>
</glass:floatingToolbar>

<!-- ════════════════════════════════════════════════════════════ -->
<!-- Chat Panel                                                   -->
<!-- ════════════════════════════════════════════════════════════ -->
<section>
  <input type="hidden" id="name" name="name" value="${person?.firstName} ${person?.lastName}">

  <div id="glassChatPanel" class="glass-chat-panel">

    <!-- Drag handle -->
    <div class="panel-handle" onclick="glassChatToggle()">
      <div class="handle-pill"></div>
    </div>

    <!-- Header -->
    <div class="chat-panel-header">
      <div class="chat-header-avatar">
        <i class="fas fa-user" style="font-size:14px;"></i>
      </div>

      <div class="chat-header-info">
        <div class="chat-header-name">
          <i class="fas fa-comments"></i>
          Chat — ${person?.firstName} ${person?.lastName}
        </div>

        <div class="chat-header-status">
          <span class="chat-status-dot"></span>
          <glass:onlineStatus id="onlineCount" status="online" label="${userCount} Online now"/>
        </div>
      </div>

      <div style="display:flex;gap:6px;">
        <!-- Online users toggle -->
        <div class="chat-icon-btn" title="Online users" onclick="toggleUserList()">
          %{--          <i class="fas fa-users"></i>--}%
        </div>
      </div>
    </div>

    <!-- Online users chips -->
    <div class="online-users-bar" id="onlineUsersBar">
      <g:each var="item" in="${signedInUsers}">
        <div class="user-chip">
          <div class="user-chip-avatar" style="background:linear-gradient(135deg,#667eea,#764ba2)">
            ${item?.user?.firstName?.take(1)}${item?.user?.lastName?.take(1)}
          </div>
          ${item?.user?.firstName} ${item?.user?.lastName?.take(1)}.
        </div>
      </g:each>
    </div>

    <!-- Messages area -->
    <div class="chat-messages-area" id="messageList">
      <%-- Shown when no messages are loaded yet; JS can remove this once msgs arrive --%>
      <div class="chat-empty-state" id="chatEmptyState">
        <i class="fas fa-message"></i>

        <p>No messages yet — say hello!</p>
      </div>
    </div>

    <!-- Input bar -->
    <div class="chat-input-bar">
      <div class="input-extras">
        <%-- Emoji Picker — inserts into #chatMessage at cursor position --%>
        <emoji:picker
          targetInput="#chatMessage"
          placement="top"
          accentColor="#667eea"
          buttonLabel="Open emoji picker"
          buttonClass="chat-icon-btn"/>
        <!-- Attachment -->
        <div class="chat-icon-btn" title="Attach file">
          <i class="fas fa-paperclip"></i>
        </div>
      </div>

      <div class="chat-input-wrap">
        <textarea id="chatMessage" class="chat-textarea" rows="1"
                  placeholder="Message…"
                  onkeydown="if (event.key === 'Enter' && !event.shiftKey) {
                    event.preventDefault();
                    sendMessage();
                  }"
                  oninput="this.style.height = 'auto';
                  this.style.height = Math.min(this.scrollHeight, 100) + 'px';
                  toggleClearBtn(this.value);">
        </textarea>

        <!-- Clear button — visible only when textarea has content -->
        <button type="button"
                id="chatClearBtn"
                class="chat-clear-btn"
                title="Clear message"
                onclick="clearChatInput()">
          <i class="fas fa-xmark"></i>
        </button>
      </div>

      <!-- Send button — FA icon replaces inline SVG -->
      <button class="chat-send-btn" type="button" onclick="sendMessage()" title="Send message">
        <i class="fas fa-paper-plane"></i>
      </button>
    </div>

  </div>
</section>

<script type="text/javascript">
  // ── Show Icons toggle handler ──────────────────────────────────
  window.handleToggle = function (checked) {
    const listIcons = document.getElementById('listIcons');
    const listItems = document.getElementById('listItems');
    if (checked) {
      listIcons.classList.replace('hidden', 'visible');
      listItems.classList.replace('visible', 'hidden');
    } else {
      listItems.classList.replace('hidden', 'visible');
      listIcons.classList.replace('visible', 'hidden');
    }
    localStorage.setItem('showIcons', checked ? 'true' : 'false');
  };

  // ── Chat panel toggle ──────────────────────────────────────────
  window.glassChatToggle = function () {
    document.getElementById('glassChatPanel').classList.toggle('open');
  };

  // ── Hide empty state once messages are rendered ────────────────
  window.hideChatEmptyState = function () {
    const el = document.getElementById('chatEmptyState');
    if (el) el.style.display = 'none';
  };

  // ── Clear button visibility ────────────────────────────────────
  window.toggleClearBtn = function (value) {
    const btn = document.getElementById('chatClearBtn');
    if (!btn) return;
    if (value && value.trim().length > 0) {
      btn.classList.add('visible');
    } else {
      btn.classList.remove('visible');
    }
  };

  // ── Clear the chat textarea ────────────────────────────────────
  window.clearChatInput = function () {
    const ta = document.getElementById('chatMessage');
    if (!ta) return;
    ta.value = '';
    ta.style.height = 'auto';
    ta.focus();
    toggleClearBtn('');
  };

  document.addEventListener('DOMContentLoaded', function () {
    const listIcons = document.getElementById('listIcons');
    const listItems = document.getElementById('listItems');

    // Restore saved toggle state
    if (localStorage.getItem('showIcons') === 'true') {
      listIcons.classList.replace('hidden', 'visible');
      __GT__.instances['showIcons'].toggle(true);
    } else {
      listItems.classList.replace('hidden', 'visible');
      __GT__.instances['showIcons'].toggle(false);
    }

    // Wire up Chat FAB
    document.getElementById('openButton')
      .addEventListener('click', glassChatToggle);

    // Swipe-down to close chat panel
    const panel = document.getElementById('glassChatPanel');
    let startY = 0;
    panel.addEventListener('touchstart', e => {
      startY = e.touches[0].clientY;
    }, {passive: true});
    panel.addEventListener('touchend', e => {
      if (e.changedTouches[0].clientY - startY > 60) panel.classList.remove('open');
    }, {passive: true});
  });
</script>

<!-- Floating Toolbar Scripts -->
<glass:floatingToolbarScripts/>
<glass:modernBtnScripts/>
<glass:orbs/>

</body>
</html>
