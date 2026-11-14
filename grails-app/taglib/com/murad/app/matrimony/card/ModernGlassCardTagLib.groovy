package com.murad.app.matrimony.card
/**
 * Glassmorphic Modern Card — GSP TagLib
 *
 * Usage in GSP:
 *   <glass:modernGlassCard title="Hello World" subtitle="A sleek glassmorphic card">
 *       <!-- optional inner content slot -->
 *       <p>Your body content here</p>
 *   </glass:modernGlassCard>
 *
 * Attributes:
 *   title          (String, required) — Card heading; supports plain text or HTML markup
 *   subtitle       (String, optional) — Card sub-heading / descriptor; supports plain text or HTML markup
 *   titleHtml      (Boolean, optional, default: false) — Set to true to render title as raw HTML
 *   subtitleHtml   (Boolean, optional, default: false) — Set to true to render subtitle as raw HTML
 *   headerHtml     (String, optional) — Raw HTML rendered at the far right of the title row
 *                                       (same line as the title). Use <g:set> to compose rich
 *                                       markup and pass the variable here, e.g.
 *                                         <g:set var="myActions">…</g:set>
 *                                         <glass:modernGlassCard headerHtml="${myActions}">
 *   iconClass      (String, optional) — Font Awesome icon class(es) rendered before the title
 *                                       e.g. "fa-solid fa-arrow-left" or "fas fa-home"
 *   iconController (String, optional) — Grails controller name for the icon link target
 *   iconAction     (String, optional) — Grails action name for the icon link target
 *   iconUrl        (String, optional) — Explicit URL for the icon link; used when controller/action
 *                                       are not supplied (falls back to "#" when neither is set)
 *   iconTitle      (String, optional) — Tooltip / aria-label for the icon link (default: "Go back")
 *   slot           (String/Number, optional) — Gmail-style account "slot" number used to build a
 *                                       slot-scoped icon URL, /u/$slot/$controller/$action, instead
 *                                       of a normal Grails link. See "Icon URL / slot resolution"
 *                                       below for the full precedence rules.
 *   id             (String, optional) — Custom HTML id for the card element
 *   class          (String, optional) — Extra CSS classes appended to .demo-card
 *
 * Icon URL / slot resolution
 * ───────────────────────────────────────────────────────────────────────────
 * The icon link's href is resolved in this order, evaluated only when
 * iconController and/or iconAction is supplied:
 *
 *   1. request.getAttribute("currentSlot") — if present (e.g. set by a
 *      filter/interceptor upstream from the currently active URL), it wins
 *      over everything else, including an explicit `slot` attribute. The
 *      href is built as:
 *          /u/${currentSlot}/${iconController}/${iconAction}
 *
 *   2. the `slot` tag attribute — used when the "currentSlot" request
 *      attribute is not set. Same URL shape:
 *          /u/${slot}/${iconController}/${iconAction}
 *
 *   3. neither is available — falls back to a normal Grails-generated link
 *      via g.createLink(controller: iconController, action: iconAction),
 *      which resolves to the plain, non-slotted form:
 *          /${iconController}/${iconAction}
 *
 * In all three cases the URL always conforms to either
 * "/u/$slot/$controller/$action" (slot present) or "/$controller/$action"
 * (no slot) — never a mix of the two shapes. iconParams (when supplied) are
 * appended as a query string in the slot-based cases (1 and 2), and passed
 * through to g.createLink's params in case 3.
 *
 * If neither iconController nor iconAction is supplied, this whole
 * resolution is skipped entirely and the tag falls back to iconUrl (if
 * given) or "#" — exactly as before slot support was added.
 */

class ModernGlassCardTagLib {

  static namespace = "glass"

  /**
   * Renders a glassmorphic card with slide-in animation and glow-on-hover.
   *
   * @attr title          REQUIRED  Main heading displayed at the top of the card.
   *                                Accepts plain text (auto-escaped) or HTML markup
   *                                when titleHtml="true".
   * @attr subtitle                 Sub-heading rendered beneath the title.
   *                                Accepts plain text (auto-escaped) or HTML markup
   *                                when subtitleHtml="true".
   * @attr titleHtml                When "true", renders title as raw HTML (default: false).
   * @attr subtitleHtml             When "true", renders subtitle as raw HTML (default: false).
   * @attr headerHtml               Raw HTML string injected at the far-right of the title row,
   *                                vertically centred and aligned opposite the title/icon group.
   *                                Compose with <g:set> in the calling GSP, then pass the
   *                                variable: headerHtml="${myVar}".
   * @attr iconClass                Font Awesome icon class(es) placed before the title text,
   *                                e.g. "fa-solid fa-arrow-left". Omit to show no icon.
   * @attr iconController           Grails controller for the icon navigation link.
   * @attr iconAction               Grails action for the icon navigation link.
   * @attr iconUrl                  Explicit href for the icon link (used when controller/action
   *                                are absent; defaults to "#" when nothing is provided).
   * @attr iconTitle                Tooltip / aria-label on the icon anchor (default: "Go back").
   * @attr iconParams               Map of extra params passed to g.createLink() for the icon link,
   *                                or appended as a query string when a slot-based URL is built.
   * @attr slot                     Account "slot" number/id. When resolved (either from the
   *                                "currentSlot" request attribute or this attribute — see class
   *                                doc above), the icon link is built as
   *                                /u/$slot/$iconController/$iconAction instead of a normal
   *                                Grails link.
   * @attr id                       HTML id attribute for the card wrapper.
   * @attr class                    Additional CSS classes for the card wrapper.
   */
  def modernGlassCard = { attrs, body ->

    def title = attrs.remove('title') ?: ''
    def subtitle = attrs.remove('subtitle') ?: ''
    def titleHtml = attrs.remove('titleHtml') in [true, 'true', '1', 'yes']
    def subtitleHtml = attrs.remove('subtitleHtml') in [true, 'true', '1', 'yes']
    def headerHtml = attrs.remove('headerHtml') ?: ''
    def iconClass = attrs.remove('iconClass') ?: ''
    def iconController = attrs.remove('iconController') ?: ''
    def iconAction = attrs.remove('iconAction') ?: ''
    def iconUrl = attrs.remove('iconUrl') ?: ''
    def iconTitle = attrs.remove('iconTitle') ?: 'Go back'
    def iconParams = attrs.remove('iconParams') ?: [:]
    def slotAttr = attrs.remove('slot')
    def cardId = attrs.remove('id') ?: ''
    def extra = attrs.remove('class') ?: ''

    def renderedTitle = titleHtml ? title : title.encodeAsHTML()
    def renderedSubtitle = subtitleHtml ? subtitle : subtitle.encodeAsHTML()

    // ── Icon link resolution ──────────────────────────────────────
    // Slot precedence: request attribute "currentSlot" first, then the
    // `slot` tag attribute, then no slot at all. Blank/whitespace-only
    // values are treated as "not provided" so an accidentally empty
    // request attribute or attr="" doesn't silently produce "/u//..." .
    def requestCurrentSlot = request?.getAttribute('currentSlot')
    def hasRequestSlot = requestCurrentSlot != null && requestCurrentSlot.toString().trim()
    def hasSlotAttr = slotAttr != null && slotAttr.toString().trim()
    def effectiveSlot = hasRequestSlot ? requestCurrentSlot : (hasSlotAttr ? slotAttr : null)

    log.debug("modernGlassCard: currentSlot(request)=${requestCurrentSlot}, slot(attr)=${slotAttr}, effectiveSlot=${effectiveSlot}, iconParams=${iconParams}")

    def iconHref
    if (iconController || iconAction) {
      if (effectiveSlot != null) {
        // Slot-scoped URL: /u/$slot/$controller/$action
        def slotPart = effectiveSlot.toString().encodeAsURL()
        def controllerPart = iconController.toString().encodeAsURL()
        def actionPart = iconAction.toString().encodeAsURL()
        def path = "/u/${slotPart}/${controllerPart}/${actionPart}"
        if (iconParams) {
          def qs = iconParams.collect { k, v -> "${k.toString().encodeAsURL()}=${v.toString().encodeAsURL()}" }.join('&')
          if (qs) path += "?${qs}"
        }
        iconHref = path.encodeAsHTML()
        log.debug("modernGlassCard: built slot-scoped link=${iconHref}")
      } else {
        // No slot resolved anywhere — normal Grails link: /$controller/$action
        def linkParams = [controller: iconController ?: null, action: iconAction ?: null]
        if (iconParams) linkParams.params = iconParams
        iconHref = g.createLink(linkParams)
        log.debug("modernGlassCard: built createLink fallback link=${iconHref}")
      }
    } else if (iconUrl) {
      iconHref = iconUrl.encodeAsHTML()
      log.debug("modernGlassCard: using explicit iconUrl link=${iconHref}")
    } else {
      iconHref = '#'
      log.debug("modernGlassCard: no iconController/iconAction/iconUrl supplied, link=#")
    }

    def renderedIcon = iconClass ? """\
<a href="${iconHref}" class="demo-card-icon-link" title="${iconTitle.encodeAsHTML()}" aria-label="${iconTitle.encodeAsHTML()}">
                <i class="${iconClass.encodeAsHTML()}" aria-hidden="true"></i>
            </a>""" : ''

    def idAttr = cardId ? " id=\"${cardId.encodeAsHTML()}\"" : ''
    def classAttr = "demo-card${extra ? ' ' + extra : ''}"
    def headerStyle = iconClass ? ' style="--demo-icon-offset: calc(2rem + 10px)"' : ''

    // ── Header-actions slot (right-aligned, title-row only) ───────
    def renderedHeaderActions = headerHtml ? """\
<div class="demo-card-header-actions">${headerHtml}</div>""" : ''

    out << """
<style>
/* ─── Keyframe: slide in from right ─────────────────────────────────────────
   Prefixed for older WebKit (Safari < 9, old Android Browser).             */
@-webkit-keyframes glassSlideIn {
    from { opacity: 0; -webkit-transform: translateX(60px); transform: translateX(60px); }
    to   { opacity: 1; -webkit-transform: translateX(0);    transform: translateX(0);    }
}
@keyframes glassSlideIn {
    from { opacity: 0; -webkit-transform: translateX(60px); transform: translateX(60px); }
    to   { opacity: 1; -webkit-transform: translateX(0);    transform: translateX(0);    }
}

/* ─── Base card ──────────────────────────────────────────────────────────── */
.demo-card {
    width                   : 100%;
    max-width               : 800px;
    -webkit-box-sizing      : border-box;
            box-sizing      : border-box;
    background              : rgba(255, 255, 255, 0.10);
    /* backdrop-filter: prefixed for Safari, standard for Chrome/Edge/Firefox */
    -webkit-backdrop-filter : blur(28px) saturate(1.5);
            backdrop-filter : blur(28px) saturate(1.5);
    border                  : 1px solid rgba(255, 255, 255, 0.22);
    -webkit-border-radius   : 24px;
            border-radius   : 24px;
    padding                 : 44px 40px;
    -webkit-box-shadow      :
        0 16px 60px rgba(0, 0, 0, 0.35),
        inset 0 1px 0 rgba(255, 255, 255, 0.20);
            box-shadow      :
        0 16px 60px rgba(0, 0, 0, 0.35),
        inset 0 1px 0 rgba(255, 255, 255, 0.20);

    /* animation — prefixed for Safari < 9 */
    -webkit-animation : glassSlideIn 0.55s cubic-bezier(0.22, 0.61, 0.36, 1) both;
            animation : glassSlideIn 0.55s cubic-bezier(0.22, 0.61, 0.36, 1) both;
    -webkit-transition:
        -webkit-box-shadow 0.35s ease,
                box-shadow 0.35s ease,
        border-color       0.35s ease,
        -webkit-transform  0.35s ease,
                transform  0.35s ease;
            transition:
        box-shadow   0.35s ease,
        border-color 0.35s ease,
        transform    0.35s ease;
}

/* ─── Responsive sizing ──────────────────────────────────────────────────────
   Padding, corner radius, and header typography scale down at smaller
   viewports so the card never sits edge-to-edge with desktop-sized padding
   on a phone, and the title/subtitle stay proportionate to the available
   width. Colours, blur, shadows, and animation are untouched at every size —
   only spacing and font-size change — so the glass look stays identical.
   ═══════════════════════════════════════════════════════════════════════ */
@media (max-width: 768px) {
    .demo-card {
        max-width     : 100%;
        padding       : 32px 24px;
        border-radius : 20px;
    }
    .demo-card-header {
        margin-bottom : 28px;
    }
    .demo-card-title {
        font-size : 1.35rem;
    }
    .demo-card-sub {
        font-size : 0.8rem;
    }
}

@media (max-width: 480px) {
    .demo-card {
        padding       : 20px 16px;
        border-radius : 16px;
    }
    .demo-card-header {
        margin-bottom : 20px;
    }
    .demo-card-title-row {
        gap : 6px;
    }
    .demo-card-title {
        font-size     : 1.1rem;
        white-space   : normal;
        overflow-wrap : break-word;
    }
    .demo-card-sub {
        font-size   : 0.72rem;
        margin-top  : 4px;
        margin-left : 0;
    }
    .demo-card-icon-link {
        width     : 1.75rem;
        height    : 1.75rem;
        font-size : 0.85rem;
    }
    .demo-card-header-actions {
        gap : 6px;
    }
}

/* ─── Card glow on hover ─────────────────────────────────────────────────── */
.demo-card:hover {
    -webkit-box-shadow:
        0 0 0 1px rgba(255, 255, 255, 0.35),
        0 0 32px 6px  rgba(180, 140, 255, 0.45),
        0 0 80px 20px rgba(120,  80, 255, 0.25),
        0 24px 80px   rgba(0,     0,   0, 0.45),
        inset 0 1px 0 rgba(255, 255, 255, 0.30);
            box-shadow:
        0 0 0 1px rgba(255, 255, 255, 0.35),
        0 0 32px 6px  rgba(180, 140, 255, 0.45),
        0 0 80px 20px rgba(120,  80, 255, 0.25),
        0 24px 80px   rgba(0,     0,   0, 0.45),
        inset 0 1px 0 rgba(255, 255, 255, 0.30);
    border-color      : rgba(200, 170, 255, 0.55);
    -webkit-transform : translateY(-3px) scale(1.012);
            transform : translateY(-3px) scale(1.012);
}

/* ─── Header block ───────────────────────────────────────────────────────── */
.demo-card-header {
    display        : -webkit-box;
    display        : -webkit-flex;
    display        : -ms-flexbox;
    display        : flex;
    -webkit-flex-direction: column;
        -ms-flex-direction: column;
            flex-direction: column;
    margin-bottom  : 36px;
}

/* ─── Title row ──────────────────────────────────────────────────────────── */
.demo-card-title-row {
    display            : -webkit-box;
    display            : -webkit-flex;
    display            : -ms-flexbox;
    display            : flex;
    -webkit-box-align  : center;
    -webkit-align-items: center;
        -ms-flex-align : center;
            align-items: center;
    -webkit-box-pack         : justify;
    -webkit-justify-content  : space-between;
        -ms-flex-pack         : justify;
            justify-content  : space-between;
    gap                      : 10px;
    width                    : 100%;
}

/* ─── Left group: icon + title ───────────────────────────────────────────── */
.demo-card-title-left {
    display            : -webkit-box;
    display            : -webkit-flex;
    display            : -ms-flexbox;
    display            : flex;
    -webkit-box-align  : center;
    -webkit-align-items: center;
        -ms-flex-align : center;
            align-items: center;
    gap                : 10px;
    -webkit-box-flex   : 1;
    -webkit-flex       : 1 1 auto;
        -ms-flex       : 1 1 auto;
            flex       : 1 1 auto;
    min-width          : 0;
}

/* ─── Title text ─────────────────────────────────────────────────────────── */
.demo-card-title {
    font-family   : 'Outfit', sans-serif;
    font-size     : 1.65rem;
    font-weight   : 700;
    color         : #ffffff;
    margin-bottom : 0;
    letter-spacing: -0.02em;
    white-space   : nowrap;
    overflow      : hidden;
    text-overflow : ellipsis;
}

/* ─── Right-side header actions ──────────────────────────────────────────── */
.demo-card-header-actions {
    display            : -webkit-box;
    display            : -webkit-flex;
    display            : -ms-flexbox;
    display            : flex;
    -webkit-box-align  : center;
    -webkit-align-items: center;
        -ms-flex-align : center;
            align-items: center;
    gap                : 8px;
    -webkit-flex-shrink: 0;
        -ms-flex-negative: 0;
            flex-shrink: 0;
}

/* ─── Subtitle ───────────────────────────────────────────────────────────── */
.demo-card-sub {
    font-size     : 0.85rem;
    color         : rgba(255, 255, 255, 0.45);
    margin-top    : 6px;
    margin-bottom : 0;
    font-weight   : 300;
    letter-spacing: 0.04em;
    margin-left   : var(--demo-icon-offset, 0px);
}

/* ─── Icon anchor (circular button) ─────────────────────────────────────────
   CROSS-BROWSER STRATEGY
   ════════════════════════════════════════════════════════════════════════════
   We do NOT rely on CSS custom-property reassignment inside pseudo-classes to
   drive color/border-color, because older Safari and Firefox ESR do not
   recompute `color: var(--x)` when --x changes in a :hover rule.

   Instead every interactive state declares color AND border-color explicitly.
   The <i> carries `color: inherit` so it always matches the anchor's color.

   Normal state  → muted frost (rgba 255/255/255 @ 18 % opacity)
   :hover        → white  (#fff)
   :focus        → white  (#fff)  + accessible outline
   :focus-visible→ white  (#fff)  + modern focus ring (browsers that support it)
   :active       → white  (#fff)  + slight scale-down press feedback
   ════════════════════════════════════════════════════════════════════════════ */
.demo-card-icon-link {
    display                : -webkit-inline-box;
    display                : -webkit-inline-flex;
    display                : -ms-inline-flexbox;
    display                : inline-flex;
    -webkit-box-align      : center;
    -webkit-align-items    : center;
        -ms-flex-align     : center;
            align-items    : center;
    -webkit-box-pack       : center;
    -webkit-justify-content: center;
        -ms-flex-pack      : center;
            justify-content: center;
    -webkit-flex-shrink    : 0;
        -ms-flex-negative  : 0;
            flex-shrink    : 0;

    width          : 2rem;
    height         : 2rem;
    -webkit-border-radius: 50%;
            border-radius: 50%;

    /* ── Resting colours — explicit, no custom-property indirection ── */
    color          : rgba(255, 255, 255, 0.45);   /* icon colour (normal) */
    border         : 1px solid rgba(255, 255, 255, 0.18); /* border (normal) */
    background     : rgba(255, 255, 255, 0.08);

    text-decoration: none;
    font-size      : 0.95rem;
    cursor         : pointer;

    /* Remove default focus outline — we supply our own below */
    outline        : none;

    -webkit-box-shadow: none;
            box-shadow: none;

    -webkit-transition:
        color             0.25s ease,
        background        0.25s ease,
        border-color      0.25s ease,
        -webkit-transform 0.25s ease,
                transform 0.25s ease,
        -webkit-box-shadow 0.25s ease,
                box-shadow 0.25s ease;
            transition:
        color        0.25s ease,
        background   0.25s ease,
        border-color 0.25s ease,
        transform    0.25s ease,
        box-shadow   0.25s ease;
}

/* Icon <i> always inherits the anchor's color — works in all browsers */
.demo-card-icon-link i {
    color          : inherit;
    display        : block;    /* avoids inline baseline quirks in older IE/Edge */
    line-height    : 1;
    pointer-events : none;     /* prevent stray mouseenter on the <i> itself */
}

/* ── :hover ──────────────────────────────────────────────────────────────── */
.demo-card-icon-link:hover {
    color        : #ffffff;                        /* icon  → white           */
    border-color : #ffffff;                        /* border → white          */
    background   : rgba(180, 140, 255, 0.30);
    -webkit-box-shadow: 0 0 12px 3px rgba(180, 140, 255, 0.40);
            box-shadow: 0 0 12px 3px rgba(180, 140, 255, 0.40);
    -webkit-transform: scale(1.12);
            transform: scale(1.12);
}

/* ── :focus (all browsers, including those that don't support :focus-visible) */
.demo-card-icon-link:focus {
    color        : #ffffff;                        /* icon  → white           */
    border-color : #ffffff;                        /* border → white          */
    background   : rgba(180, 140, 255, 0.30);
    /* box-shadow supplies the visible focus ring (outline suppressed above) */
    -webkit-box-shadow:
        0 0 12px 3px rgba(180, 140, 255, 0.40),
        0 0 0 3px rgba(255, 255, 255, 0.55);      /* white focus ring         */
            box-shadow:
        0 0 12px 3px rgba(180, 140, 255, 0.40),
        0 0 0 3px rgba(255, 255, 255, 0.55);
}

/* ── :focus-visible (modern browsers) — keyboard-only focus ring ─────────── */
.demo-card-icon-link:focus-visible {
    color         : #ffffff;                       /* icon  → white           */
    border-color  : #ffffff;                       /* border → white          */
    background    : rgba(180, 140, 255, 0.30);
    /* White outline for keyboards; box-shadow provides the glow underneath  */
    outline       : 2px solid rgba(255, 255, 255, 0.80);
    outline-offset: 2px;
    -webkit-box-shadow: 0 0 12px 3px rgba(180, 140, 255, 0.40);
            box-shadow: 0 0 12px 3px rgba(180, 140, 255, 0.40);
}

/* ── :active (mouse press / tap) ─────────────────────────────────────────── */
.demo-card-icon-link:active {
    color        : #ffffff;                        /* icon  → white           */
    border-color : #ffffff;                        /* border → white          */
    background   : rgba(180, 140, 255, 0.45);
    -webkit-box-shadow: 0 0 8px 2px rgba(180, 140, 255, 0.30);
            box-shadow: 0 0 8px 2px rgba(180, 140, 255, 0.30);
    -webkit-transform: scale(0.96);
            transform: scale(0.96);
}
</style>

<div${idAttr} class="${classAttr}">
    <div class="demo-card-header"${headerStyle}>

      <!--  <%-- ── Title row: [icon + title] ·············· [headerHtml] ── --%> -->
        <div class="demo-card-title-row">
            <div class="demo-card-title-left">
                ${renderedIcon}
                <span class="demo-card-title">${renderedTitle}</span>
            </div>
            ${renderedHeaderActions}
        </div>

        <!-- <%-- ── Subtitle (indented to align with title text) ───────── --%> -->
        ${renderedSubtitle ? "<div class=\"demo-card-sub\">${renderedSubtitle}</div>" : ''}

    </div>
    ${body()}
</div>
"""
  }
}
