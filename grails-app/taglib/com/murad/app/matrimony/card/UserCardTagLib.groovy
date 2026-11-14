package com.murad.app.matrimony.card

import groovy.util.logging.Slf4j

/**
 * UserCardTagLib
 *
 * Renders a Bootstrap-based profile card (modeled on the existing person-card
 * markup) with an icon in place of a photo, fully curved corners, and a
 * semitransparent glassmorphic slide-in panel confined to the lower
 * (card-body) section — it never covers the icon area.
 *
 * Namespace : glass2
 * Tag       : glass2:userCard
 *
 * ── PANEL BEHAVIOR ────────────────────────────────────────────────────
 *  Open   : hover (real pointer devices only, via mouseenter), tapping/
 *           clicking the icon area, or keyboard focus entering the card.
 *  Close  : clicking/tapping anywhere inside the panel, clicking/tapping
 *           anywhere outside the card, keyboard focus leaving the card,
 *           mouse leaving the card, or pressing Escape.
 *  The panel is deliberately NOT opened by clicking the lower/body area —
 *  that area is the card's stretched-link (navigates to the profile) when
 *  the panel is closed, and becomes the panel's own click-to-close surface
 *  once it's open.
 *
 * ── USAGE ──────────────────────────────────────────────────────────────
 *
 *  <!-- Basic — icon auto-picked from gender -->
 *  <glass2:userCard id="${person[0]}" firstName="${person[1]}" lastName="${person[2]}"
 *                    email="${person[3]}" gender="${person[4]}"/>
 *
 *  <!-- Explicit FontAwesome icon override -->
 *  <glass2:userCard id="${person[0]}" firstName="${person[1]}" lastName="${person[2]}"
 *                    email="${person[3]}" gender="${person[4]}" faIcon="user-graduate"/>
 *
 *  <!-- Custom link target -->
 *  <glass2:userCard id="${person[0]}" firstName="${person[1]}" lastName="${person[2]}"
 *                    email="${person[3]}" gender="${person[4]}"
 *                    controller="profile" action="view"/>
 *
 * ── ATTRIBUTES ────────────────────────────────────────────────────────
 *  id          REQUIRED - person id, passed as the 'personId' link param
 *  firstName   REQUIRED
 *  lastName    REQUIRED
 *  email       REQUIRED
 *  gender      Used for the default icon (Male/Female) and shown as text
 *  icon        Full icon class string (e.g. 'fa-solid fa-user') - overrides the gender default
 *  faIcon      FontAwesome icon name without prefix (e.g. 'user') - alternative to icon
 *  faStyle     FontAwesome style prefix: fas, far, fab, fal, fad (default: 'fas') - used with faIcon
 *  controller  Link controller (default: 'spouseprofile')
 *  action      Link action (default: 'readSpouseProfile')
 *  paramName   Name of the id param passed to the link (default: 'personId')
 *  width       Card width, any CSS width value (default: '18rem')
 *  panelText   Text shown on the slide-in panel (default: 'View Profile')
 *  panelIcon   Full icon class string shown on the panel (default: 'fa-solid fa-eye')
 */
@Slf4j
class UserCardTagLib {

  static namespace = 'glass2'

  private static final String STYLE_SENTINEL = 'glass2UserCardStyleInjected'
  private static final String SCRIPT_SENTINEL = 'glass2UserCardScriptInjected'

  def userCard = { attrs, body ->

    def personId = attrs.id
    def firstName = attrs.firstName ?: ''
    def lastName = attrs.lastName ?: ''
    def email = attrs.email ?: ''
    def gender = attrs.gender ?: ''
    def controller = attrs.controller ?: 'spouseprofile'
    def action = attrs.action ?: 'readSpouseProfile'
    def paramName = attrs.paramName ?: 'personId'
    def width = attrs.width ?: '18rem'
    def panelText = attrs.panelText ?: 'View Profile'
    def panelIcon = attrs.panelIcon ?: 'fa-solid fa-eye'

    def icon = owner.resolveIcon(attrs, gender)

    emitAssets()

    def href = g.createLink(controller: controller, action: action, params: [(paramName): personId])

    out << """
<div class="glass2-user-card" tabindex="0" style="width: ${width};">
  <div class="glass2-user-card-icon-wrap" data-gt-trigger>
    <i class="${icon}"></i>
  </div>
  <div class="glass2-user-card-lower">
    <div class="card-body">
      <h5 class="card-title">${firstName}&nbsp;${lastName}</h5>
      <div class="glass2-user-card-meta">
        <span class="glass2-user-card-meta-row">
          <i class="fa-solid fa-venus-mars"></i>
          <span class="glass2-user-card-meta-text">${gender}</span>
        </span>
        <span class="glass2-user-card-meta-row">
          <i class="fa-solid fa-envelope"></i>
          <span class="glass2-user-card-meta-text">${email}</span>
        </span>
      </div>
      <a href="${href}" class="stretched-link"></a>
    </div>
    <div class="glass2-user-card-panel" data-gt-panel>
      <div class="glass2-user-card-panel-inner">
        <i class="${panelIcon}"></i>
        <span>${panelText}</span>
      </div>
    </div>
  </div>
</div>
"""
  }

  /**
   * Resolves the icon class string, honoring explicit icon/faIcon overrides
   * first, then falling back to a gender-based default. Must NOT be private
   * — called via owner. from the tag closure.
   */
  def resolveIcon(attrs, gender) {
    if (attrs.icon) return attrs.icon
    if (attrs.faIcon) {
      def faStyle = attrs.faStyle ?: 'fas'
      return "${faStyle} fa-${attrs.faIcon}"
    }
    switch (gender?.toString()?.toLowerCase()) {
      case 'female': return 'fa-solid fa-person-dress'
      case 'male': return 'fa-solid fa-person'
      default: return 'fa-solid fa-user'
    }
  }

  private void emitAssets() {
    emitStyle()
    emitScript()
  }

  private void emitStyle() {
    if (request.getAttribute(STYLE_SENTINEL)) return
    request.setAttribute(STYLE_SENTINEL, true)
    out << '''
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet"/>
<style id="glass2-user-card-styles">
.glass2-user-card {
  position: relative;
  display: flex;
  flex-direction: column;
  border: none;
  border-radius: 20px;
  overflow: hidden;
  background: #ffffff;
  box-shadow: 0 4px 18px rgba(0,0,0,0.10);
  outline: none;
  font-family: 'Inter', -apple-system, 'Segoe UI', Roboto, sans-serif;
  transition: transform 0.25s cubic-bezier(0.4,0,0.2,1), box-shadow 0.25s cubic-bezier(0.4,0,0.2,1);
}
.glass2-user-card:focus-visible {
  outline: 2px solid #6a5cff;
  outline-offset: 3px;
}
.glass2-user-card.gt-panel-open {
  box-shadow: 0 10px 28px rgba(0,0,0,0.16);
}

/* ── Icon area — the panel never covers this ─────────────────────────── */
.glass2-user-card-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  aspect-ratio: 16 / 10;
  background: linear-gradient(135deg, #6a5cff 0%, #b06ab3 100%);
  cursor: pointer;
}
.glass2-user-card-icon-wrap i {
  font-size: 4rem;
  color: rgba(255,255,255,0.95);
}

/* ── Lower section — positioning context for the panel ────────────────
   The panel is `inset:0` *relative to this element*, not the whole card,
   so it can only ever slide up as far as this element's top edge — i.e.
   exactly the bottom of the icon-wrap above it.                        */
.glass2-user-card-lower {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.glass2-user-card .card-body {
  padding: 1.1rem 1.2rem 1.25rem;
}
.glass2-user-card .card-title {
  margin: 0 0 0.65rem;
  font-size: 1.08rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  line-height: 1.3;
  color: #1a1a2e;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.glass2-user-card-meta {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  padding-top: 0.6rem;
  border-top: 1px solid rgba(0,0,0,0.07);
}
.glass2-user-card-meta-row {
  display: flex;
  align-items: center;
  gap: 0.55rem;
  min-width: 0;
}
.glass2-user-card-meta-row i {
  flex-shrink: 0;
  width: 1.1rem;
  text-align: center;
  font-size: 0.8rem;
  color: #6a5cff;
}
.glass2-user-card-meta-text {
  font-size: 0.86rem;
  font-weight: 500;
  color: #5b5f73;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 0;
}

/* ── Slide-in semitransparent glass panel ─────────────────────────────
   Closed by default. Opened only by the JS-managed .gt-panel-open class
   (see glass2-user-card-script below) — hover, focus, click/tap and
   Escape are all normalized through that single class so open/close is
   deterministic and identical across mouse, keyboard, and touch.        */
.glass2-user-card-panel {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 1.2rem;
  background: rgba(20, 16, 40, 0.42);
  -webkit-backdrop-filter: blur(10px) saturate(150%);
          backdrop-filter: blur(10px) saturate(150%);
  opacity: 0;
  transform: translateY(100%);
  transition: transform 0.32s cubic-bezier(0.4,0,0.2,1), opacity 0.32s ease;
  pointer-events: none;
  cursor: pointer;
  z-index: 2;
}
.glass2-user-card.gt-panel-open .glass2-user-card-panel {
  opacity: 1;
  transform: translateY(0);
  pointer-events: auto;
}
.glass2-user-card-panel-inner {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.55rem 1.1rem;
  border-radius: 999px;
  background: rgba(255,255,255,0.16);
  border: 1px solid rgba(255,255,255,0.35);
  color: #ffffff;
  font-weight: 600;
  font-size: 0.9rem;
  box-shadow: 0 4px 16px rgba(0,0,0,0.25);
  pointer-events: none;
}
.glass2-user-card-panel-inner i {
  font-size: 0.95rem;
}

/* ── Backdrop-filter fallback for browsers without support ───────────── */
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .glass2-user-card-panel {
    background: rgba(20, 16, 40, 0.72);
  }
}

/* ── Reduced motion ────────────────────────────────────────────────────*/
@media (prefers-reduced-motion: reduce) {
  .glass2-user-card,
  .glass2-user-card-panel {
    transition: none !important;
  }
}
</style>
'''
  }

  private void emitScript() {
    if (request.getAttribute(SCRIPT_SENTINEL)) return
    request.setAttribute(SCRIPT_SENTINEL, true)
    out << '''
<script id="glass2-user-card-script">
(function () {
  'use strict';

  var OPEN_CLASS = 'gt-panel-open';
  var canHover = window.matchMedia && window.matchMedia('(hover: hover) and (pointer: fine)').matches;

  function closeCard(card) {
    card.classList.remove(OPEN_CLASS);
  }

  function openCard(card) {
    card.classList.add(OPEN_CLASS);
  }

  function closeAllExcept(exceptCard) {
    document.querySelectorAll('.glass2-user-card.' + OPEN_CLASS).forEach(function (card) {
      if (card !== exceptCard) closeCard(card);
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.glass2-user-card').forEach(function (card) {
      var trigger = card.querySelector('[data-gt-trigger]');
      var panel = card.querySelector('[data-gt-panel]');

      // Real mouse hover (fine pointer, hover-capable) opens/closes naturally.
      if (canHover) {
        card.addEventListener('mouseenter', function () {
          closeAllExcept(card);
          openCard(card);
        });
        card.addEventListener('mouseleave', function () {
          closeCard(card);
        });
      }

      // Keyboard focus entering/leaving the card (Tab navigation).
      card.addEventListener('focusin', function () {
        closeAllExcept(card);
        openCard(card);
      });
      card.addEventListener('focusout', function (e) {
        if (!card.contains(e.relatedTarget)) closeCard(card);
      });

      // Tapping/clicking the icon toggles the panel — primary trigger for
      // touch devices, also usable on desktop.
      if (trigger) {
        trigger.addEventListener('click', function (e) {
          e.preventDefault();
          e.stopPropagation();
          var isOpen = card.classList.contains(OPEN_CLASS);
          closeAllExcept(card);
          if (isOpen) { closeCard(card); } else { openCard(card); }
        });
      }

      // Clicking/tapping anywhere inside the open panel closes it, and
      // does NOT trigger the underlying stretched-link navigation.
      if (panel) {
        panel.addEventListener('click', function (e) {
          e.preventDefault();
          e.stopPropagation();
          closeCard(card);
        });
      }
    });

    // Clicking/tapping anywhere outside a currently-open card closes it.
    document.addEventListener('click', function (e) {
      document.querySelectorAll('.glass2-user-card.' + OPEN_CLASS).forEach(function (card) {
        if (!card.contains(e.target)) closeCard(card);
      });
    });

    // Escape closes any open panel.
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') {
        document.querySelectorAll('.glass2-user-card.' + OPEN_CLASS).forEach(closeCard);
      }
    });
  });
})();
</script>
'''
  }
}
