package com.murad.app.matrimony.toggle

/**
 * Tag library for rendering a glassmorphism-styled custom checkbox.
 *
 * Architecture
 * ─────────────
 * ::after pseudo-elements are not rendered on replaced elements (<input>)
 * in any browser. The visual checkbox is therefore a <span> sibling driven
 * by the adjacent-sibling CSS selector:
 *   input:checked + .gcb-indicator
 * The real <input> is visually hidden but stays in the DOM so form
 * submission, labels, and accessibility work normally.
 *
 * All CSS class names use the "gcb-" prefix (glass checkbox) to minimise
 * collision risk in host applications.
 *
 * Mobile-first & cross-browser decisions
 * ────────────────────────────────────────
 *  1. Touch target — ::before pseudo on the indicator inflates the tappable
 *     area to 44 × 44 px (Apple HIG / WCAG 2.5.5) with no visual change.
 *  2. -webkit-tap-highlight-color:transparent — suppresses the default blue
 *     flash on iOS Safari and Android Chrome.
 *  3. No :has() — replaced by an explicit .gcb-label--disabled class applied
 *     server-side; :has() is unsupported in Firefox < 121 and older Samsung
 *     Internet.
 *  4. clip-path:inset(50%) — replaces the deprecated clip:rect(0,0,0,0).
 *  5. All vendor prefixes included for -webkit-, -moz-, -ms- where needed:
 *     appearance, user-select, transform, flex, box-sizing, transition.
 *  6. :focus kept alongside :focus-visible for IE 11, Edge 18, Safari < 15.4.
 *  7. box-sizing:content-box on ::after prevents borders from collapsing the
 *     tick mark on Android WebView / UC Browser.
 *  8. Wrapper margins scale via a mobile-first media query (< 480 px compact).
 *  9. will-change:background,border-color promotes the indicator to its own
 *     compositor layer, reducing paint cost during the check animation.
 * 10. CSS logical properties (margin-inline) are NOT used — kept as physical
 *     margin-left/margin-right for IE 11 / older Android compatibility.
 *
 * Usage
 * ──────
 *   <%-- Add once in your layout or <head> --%>
 *   <glass:checkboxStyles />
 *
 *   <glass:checkbox name="remember" label="Remember me" />
 *   <glass:checkbox name="agree"    label="I agree"     checked="true" />
 *   <glass:checkbox name="terms"    label="Terms"       disabled="true" />
 *
 * Attributes
 * ───────────
 * @attr name         REQUIRED — name attribute for the <input>
 * @attr label        REQUIRED — visible label text (HTML-escaped)
 * @attr id           id attribute (defaults to name)
 * @attr value        value attribute (defaults to "on")
 * @attr checked      initial checked state, true/false (default false)
 * @attr disabled     true/false (default false)
 * @attr required     true/false (default false)
 * @attr wrapper      wrap in a .gcb-group <div> (default true)
 * @attr class        extra CSS classes on the <input>
 * @attr labelClass   extra CSS classes on the <label>
 * @attr wrapperClass extra CSS classes on the wrapper <div>
 * @attr onchange     JS onchange handler expression
 */
class CheckBoxTagLib {

  static namespace = "glass"

  // ── CSS ────────────────────────────────────────────────────────────────────
  def checkboxStyles = { attrs ->
    out << '''<style>
/* ── Design tokens ───────────────────────────────────────────────────────── */
:root {
  --gcb-text         : rgba(255,255,255,0.7);
  --gcb-accent       : #00d4ff;
  --gcb-size         : 20px;
  --gcb-radius       : 6px;
  --gcb-touch        : 44px;
  --gcb-background           : rgba(255,255,255,0.05);
  --gcb-border       : rgba(255,255,255,0.2);
  --gcb-border-hover : rgba(255,255,255,0.4);
  --gcb-focus-ring   : rgba(0,212,255,0.25);
  --gcb-disabled-op  : 0.45;
  --gcb-label-dis-op : 0.6;
  --gcb-gap          : 10px;
  --gcb-font-size    : 14px;
  --gcb-transition   : 0.2s ease;
}

/* ── Wrapper ─────────────────────────────────────────────────────────────── */
/* Mobile-first: compact margins, expand on wider viewports */
.gcb-group {
  margin-top   : 16px;
  margin-bottom: 12px;
}
@media (min-width: 480px) {
  .gcb-group {
    margin-top   : 32px;
    margin-bottom: 24px;
  }
}

/* ── Label ───────────────────────────────────────────────────────────────── */
.gcb-label {
  display                    : -webkit-inline-box;
  display                    : -ms-inline-flexbox;
  display                    : inline-flex;
  -webkit-box-align          : center;
      -ms-flex-align         : center;
          align-items        : center;
  gap                        : var(--gcb-gap);
  color                      : var(--gcb-text);
  font-size                  : var(--gcb-font-size);
  font-weight                : 400;
  line-height                : 1.4;
  cursor                     : pointer;
  -webkit-tap-highlight-color: transparent;
  -webkit-user-select        : none;
     -moz-user-select        : none;
      -ms-user-select        : none;
          user-select        : none;
}

/* ── Hidden native input ─────────────────────────────────────────────────── */
/* Visually hidden but accessible to AT and form submission.                  */
.gcb-input {
  position          : absolute;
  width             : 1px;
  height            : 1px;
  margin            : -1px;
  padding           : 0;
  overflow          : hidden;
  clip-path         : inset(50%);
  white-space       : nowrap;
  border            : 0;
  -webkit-appearance: none;
     -moz-appearance: none;
      -ms-appearance: none;
          appearance: none;
}

/* ── Visual indicator ────────────────────────────────────────────────────── */
/* position:relative + explicit min-width/height lock the box to exactly      */
/* --gcb-size in both states regardless of flexbox shrinkage.                 */
.gcb-indicator {
  position                  : relative;
  width                     : var(--gcb-size);
  height                    : var(--gcb-size);
  min-width                 : var(--gcb-size);
  min-height                : var(--gcb-size);
  -webkit-box-sizing        : border-box;
          box-sizing        : border-box;
  -webkit-flex-shrink       : 0;
      -ms-flex-negative     : 0;
          flex-shrink       : 0;
  border-radius             : var(--gcb-radius);
  background                : var(--gcb-background);
  border                    : 1.5px solid var(--gcb-border);
  pointer-events            : none;
  will-change               : background, border-color;

  /* Split shorthand for IE 11 / old Edge */
  -webkit-transition-property       : background, border-color, box-shadow;
          transition-property       : background, border-color, box-shadow;
  -webkit-transition-duration       : var(--gcb-transition);
          transition-duration       : var(--gcb-transition);
  -webkit-transition-timing-function: ease;
          transition-timing-function: ease;
}

/* ── Touch-target expansion ──────────────────────────────────────────────── */
/* Inflates tappable area to 44 × 44 px; invisible; no layout impact.        */
.gcb-indicator::before {
  content          : "";
  position         : absolute;
  top              : 50%;
  left             : 50%;
  width            : var(--gcb-touch);
  height           : var(--gcb-touch);
  -webkit-transform: translate(-50%,-50%);
      -ms-transform: translate(-50%,-50%);
          transform: translate(-50%,-50%);
}

/* ── Checked — indicator fill ────────────────────────────────────────────── */
.gcb-input:checked + .gcb-indicator {
  background  : var(--gcb-accent);
  border-color: var(--gcb-accent);
}

/* ── Checked — tick mark ─────────────────────────────────────────────────── */
/* box-sizing:content-box prevents border from collapsing the tick on         */
/* Android WebView / UC Browser.                                              */
.gcb-input:checked + .gcb-indicator::after {
  content                   : "";
  -webkit-box-sizing        : content-box;
          box-sizing        : content-box;
  position                  : absolute;
  top                       : 50%;
  left                      : 50%;
  width                     : 5px;
  height                    : 9px;
  border                    : 2px solid #000;
  border-top                : none;
  border-left               : none;
  -webkit-transform         : translate(-50%,-65%) rotate(45deg);
      -ms-transform         : translate(-50%,-65%) rotate(45deg);
          transform         : translate(-50%,-65%) rotate(45deg);
}

/* ── Hover ───────────────────────────────────────────────────────────────── */
.gcb-label:hover .gcb-indicator {
  border-color: var(--gcb-border-hover);
}

/* ── Focus ring ──────────────────────────────────────────────────────────── */
/* :focus base rule covers IE 11, Edge 18, Safari < 15.4.                    */
/* :focus:not(:focus-visible) removes the ring for pointer users in modern    */
/* browsers that support :focus-visible.                                      */
.gcb-input:focus + .gcb-indicator {
  outline   : none;
  box-shadow: 0 0 0 3px var(--gcb-focus-ring);
}
.gcb-input:focus:not(:focus-visible) + .gcb-indicator {
  box-shadow: none;
}

/* ── Disabled ────────────────────────────────────────────────────────────── */
/* :has() intentionally omitted — unsupported in Firefox < 121 and older     */
/* Samsung Internet. Opacity on the indicator + .gcb-label--disabled on the  */
/* label element achieves the same result without :has().                     */
.gcb-input:disabled + .gcb-indicator {
  opacity: var(--gcb-disabled-op);
}
.gcb-label--disabled {
  opacity       : var(--gcb-label-dis-op);
  cursor        : not-allowed;
  pointer-events: none;
}
</style>'''
  }

  // ── Markup ─────────────────────────────────────────────────────────────────
  def checkbox = { attrs ->
    if (!attrs.name) throwTagError("Tag [checkbox] is missing required attribute [name]")
    if (!attrs.label) throwTagError("Tag [checkbox] is missing required attribute [label]")

    // ── Attribute resolution ───────────────────────────────────────────────
    String name = attrs.name
    String label = attrs.label
    String id = attrs.id ?: name
    String value = attrs.value ?: 'on'
    String extraInput = attrs.class ?: ''
    String extraLabel = attrs.labelClass ?: ''
    String extraWrapper = attrs.wrapperClass ?: ''
    boolean checked = attrs.checked?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''

    // ── Class composition ──────────────────────────────────────────────────
    String labelClasses = "gcb-label${disabled ? ' gcb-label--disabled' : ''}${extraLabel ? ' ' + extraLabel : ''}"
    String inputClasses = "gcb-input${extraInput ? ' ' + extraInput : ''}"
    String wrapperClasses = "gcb-group${extraWrapper ? ' ' + extraWrapper : ''}"

    // ── Render ─────────────────────────────────────────────────────────────
    if (wrapper) {
      out << "<div class=\"${wrapperClasses.encodeAsHTML()}\">"
    }

    out << "<label class=\"${labelClasses.encodeAsHTML()}\">"

    // Hidden native input — owns the :checked state for the sibling selector
    out << "<input type=\"checkbox\""
    out << " name=\"${name.encodeAsHTML()}\""
    out << " id=\"${id.encodeAsHTML()}\""
    out << " value=\"${value.encodeAsHTML()}\""
    out << " class=\"${inputClasses.encodeAsHTML()}\""
    if (checked) out << ' checked="checked"'
    if (disabled) out << ' disabled="disabled"'
    if (required) out << ' required="required"'
    if (onchange) out << " onchange=\"${onchange.encodeAsHTML()}\""
    out << '/>'

    // Visual indicator — ::before = touch target, ::after = tick mark
    out << '<span class="gcb-indicator" aria-hidden="true"></span>'

    out << label.encodeAsHTML()
    out << '</label>'

    if (wrapper) {
      out << '</div>'
    }
  }
}
