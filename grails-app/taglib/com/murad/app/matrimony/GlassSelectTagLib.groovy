package com.murad.app.matrimony

/**
 * GlassSelectTagLib.groovy
 *
 * Glassmorphic <select> GSP tag library.
 *
 * Namespace : glass
 * Tag       : select
 *
 * Usage:
 *   <glass:select
 *       name="religion"
 *       id="religionSelect"
 *       label="Religion"
 *       placeholder="Select a religion…"
 *       value="${selectedReligion}"
 *       from="${religionList}"
 *       optionKey="id"
 *       optionValue="name"
 *       noSelection="${['': '-- Select --']}"
 *       multiple="false"
 *       required="false"
 *       disabled="false"
 *       cssClass="my-class"
 *       style="max-width: 340px"
 *   />
 *
 * DISABLED (same behaviour as glass:radio):
 *   disabled accepts: true / false / "true" / "false" / 1 / 0 / "yes" / "on"
 *   (case-insensitive; same coercion as glass:radio's isTruthy).
 *   • Disabled, no value chosen  → muted card (opacity 0.42, greyscale,
 *     no hover / no active transform).
 *   • Disabled AND a value chosen → the selected value stays clearly
 *     visible: white text, brighter glass, border kept, dimmed less
 *     (opacity 0.65), and a 🔒 lock badge in the corner. Wrapper carries
 *     .gsl-disabled + .gsl-disabled-selected (set server-side, and kept in
 *     sync by JS when the value changes programmatically).
 *   • The <select> also gets aria-disabled="true".
 *
 * FROM / VALUE FALLBACK:
 *   from is optional. If it is omitted or empty, the option(s) given in
 *   value are rendered instead (value is used as both the option's key
 *   and its label) and shown as selected. A single select shows just
 *   one value; a multiple select shows every value. This lets a page
 *   display a saved value before any option list has been loaded (for
 *   example a field that a script populates later).
 *
 * WIDTH:
 *   .gsl-wrapper always fills 100% of its containing block (e.g. a
 *   Bootstrap column). It no longer shrink-wraps to the native
 *   <select>'s own intrinsic/preferred width (the old behaviour sized
 *   the control to its longest option's text). If you need a narrower
 *   control, constrain the parent element or pass an explicit
 *   style="max-width: ...".
 *
 * OPEN / CLOSE BEHAVIOUR:
 *   Clicking the control already opens the native option list on its own
 *   (that's default browser behaviour for <select> and isn't something
 *   JS needs to drive). What browsers do NOT do by default is open that
 *   list when focus arrives via keyboard (Tab) rather than a click — you
 *   normally need a follow-up Enter/Space/Arrow-Down for that. The script
 *   below closes that gap: on focus that did NOT originate from a mouse
 *   press, it calls the standard HTMLSelectElement.showPicker() to open
 *   the list immediately, in browsers that support it.
 *
 *   CLOSING:
 *   • Selecting an option / blurring the field — browsers already dismiss
 *     an open <select> list the moment it loses focus, so removing our own
 *     .gsl-active state on blur is all that's required there.
 *   • Clicking outside the control — the native option list dismisses on
 *     its own, but several browsers leave the <select> ELEMENT itself
 *     focused afterwards, which used to leave the chevron visually stuck
 *     rotated (its rotation is tied to :focus-within). A document-level
 *     mousedown listener now explicitly blurs any focused glass-select
 *     whose wrapper doesn't contain the click target, so the chevron
 *     reliably rotates back on outside click.
 *   • Tab / Shift+Tab while open — native <select> behaviour already
 *     closes the list and moves focus to the next/previous focusable
 *     element on its own; this isn't something page JS can or should
 *     override. A keydown listener on Tab clears .gsl-active immediately
 *     (rather than waiting on 'blur') purely so the chevron's rotation
 *     resets in the same frame, without calling preventDefault() or
 *     otherwise interfering with native focus movement.
 *
 * PROGRAMMATIC VALUE CHANGES:
 *   Setting select.value / select.selectedIndex from JS fires no 'change'
 *   event, so the white-text state (.gsl-has-value) used to stay stale.
 *   Each select now wraps its own value / selectedIndex setters (instance
 *   level only — the native prototype is untouched) to re-sync, and a
 *   MutationObserver re-syncs when options are added/removed/re-selected
 *   (e.g. a script repopulating a dependent select via innerHTML). A form
 *   'reset' also re-syncs. Setting option.selected = true directly on an
 *   <option> does not go through either path; call select.dispatchEvent(
 *   new Event('change')) or window.gslSync(select) after doing that.
 *
 * REQUIRED / VALIDATION TEXT COLOUR:
 *   Displayed text is always white when the select is required (or
 *   :invalid / .is-invalid / .was-validated :invalid), overriding any
 *   page-level or framework rule that tints invalid selects red.
 *   Validation feedback is shown by the wrapper ring/label only.
 *
 * noSelection TEXT COLOUR:
 *   When noSelection (or a single-select placeholder) is supplied the <select>
 *   gets the class .gsl-no-selection and its displayed text (e.g.
 *   "-- Select --") is white, not muted. (A disabled select still uses the muted colour.)
 *
 * NO RED FLASH ON LOAD:
 *   Fonts are loaded with <link> tags, not a CSS @import. An @import
 *   inside the injected <style> delays applying the whole sheet until the
 *   font CSS downloads, which let the page's own required/invalid styling
 *   (red text) show for a fraction of a second on load/refresh.
 *
 * Validation states (set on the WRAPPER, not the <select>):
 *   wrapperEl.classList.add('gsl-invalid')   — red ring
 *   wrapperEl.classList.add('gsl-valid')     — green ring
 *   wrapperEl.classList.remove('gsl-invalid', 'gsl-valid')  — reset
 *
 * HEIGHT MATCHING (--gform-height):
 *   .gsl-wrapper and .gsl-select are pinned to the shared --gform-height
 *   token, now 44px (reduced from the previous 50px). That token was
 *   previously declared identically in TextInputStylesTagLib's,
 *   glass:radio's, and dp:picker's stylesheets so all glass form
 *   controls rendered at the same height — since only this taglib's
 *   value has been reduced here, update those other stylesheets to
 *   44px too if you still want a glass:select to line up with a text
 *   input, radio, or date picker on the same form.
 *
 * TYPOGRAPHY MATCHING:
 *   .gsl-select's font-family/font-size are shared with
 *   TextInputStylesTagLib's .input-glass input (both 'DM Sans',
 *   1rem) so a <glass:select> and an alphanumeric/text field render
 *   with identical type on the same form.
 *
 * CHEVRON:
 *   Rendered as a Font Awesome 6 fa-solid fa-chevron-down icon (was
 *   previously an inline SVG) — matches the fa-solid fa-xmark clear icon
 *   already used by AlphaNumericTextFieldTagLib, so this taglib now shares
 *   that same Font Awesome 6 dependency. Colour is fixed solid white
 *   (--gsl-chevron / --gsl-chevron-open, both rgba(255,255,255,1)) in every
 *   state, including light mode — it no longer dims when idle or tints
 *   green/red on valid/invalid.
 */
class GlassSelectTagLib {

  static namespace = "glass"

  def select = { attrs, body ->

    /* ── Require name ────────────────────────────────────────────── */
    if (!attrs.name) {
      throwTagError("Tag [glass:select] is missing required attribute [name]")
    }

    /* ── Read attributes ─────────────────────────────────────────── */
    String name = attrs.remove("name")?.toString() ?: ""
    String id = attrs.remove("id")?.toString() ?: name
    String label = attrs.remove("label")?.toString() ?: ""
    String placeholder = attrs.remove("placeholder")?.toString() ?: ""
    def value = attrs.remove("value")
    def from = attrs.remove("from") ?: []
    String optionKey = attrs.remove("optionKey")?.toString() ?: null
    String optionValue = attrs.remove("optionValue")?.toString() ?: null
    def noSelection = attrs.remove("noSelection") ?: null
    String cssClass = attrs.remove("cssClass")?.toString() ?: ""
    String wrapStyle = attrs.remove("style")?.toString() ?: ""

    def multipleRaw = attrs.remove("multiple")
    def requiredRaw = attrs.remove("required")
    def disabledRaw = attrs.remove("disabled")

    boolean multiple = (multipleRaw instanceof Boolean) ? multipleRaw : multipleRaw?.toString()?.equalsIgnoreCase("true") ?: false
    boolean required = (requiredRaw instanceof Boolean) ? requiredRaw : requiredRaw?.toString()?.equalsIgnoreCase("true") ?: false

    // ── Robust boolean coercion (same as glass:radio) ────────────────────────
    boolean disabled = isTruthy(disabledRaw)
    // ────────────────────────────────────────────────────────────────────────

    /* ── Normalise selected values ───────────────────────────────── */
    Set<String> selected = []
    if (value instanceof Collection) {
      selected = value.collect { it?.toString() } as Set
    } else if (value != null) {
      selected = [value.toString()] as Set
    }

    /* A "real" (non-empty) value is chosen — drives the disabled+selected look */
    boolean hasSelection = selected.any { it }

    /* ── HTML encoder ────────────────────────────────────────────── */
    def h = { String s ->
      s?.replace("&", "&amp;")
        ?.replace("<", "&lt;")
        ?.replace(">", "&gt;")
        ?.replace("\"", "&quot;") ?: ""
    }

    /* ── Inject CSS + JS once per page ──────────────────────────── */
    if (!pageScope["__gslCssWritten__"]) {
      pageScope["__gslCssWritten__"] = true
      out << '''<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600&family=DM+Sans:opsz,wght@9..40,300;9..40,400;9..40,500&display=swap">
<style id="glass-select-taglib-styles">
/* NOTE: fonts are loaded by the <link> above, NOT via @import here. A
   stylesheet containing @import is not applied until the imported sheet
   has downloaded, so every rule below (including the white text colour)
   arrived a beat after first paint and the select flashed whatever colour
   the page's own CSS gave it (red for required/invalid) before this sheet
   kicked in. Without @import these rules apply on the very first paint. */

/* ── Design tokens ──────────────────────────────────────────────── */
:root {
  --gsl-border           : rgba(255, 255, 255, 0.28);
  --gsl-border-hover     : rgba(255, 255, 255, 0.50);
  --gsl-border-focus     : rgba(255, 255, 255, 0.95);
  --gsl-shadow           : 0 8px 32px rgba(15, 20, 80, 0.22);
  --gsl-shadow-hover     : 0 10px 36px rgba(15, 20, 80, 0.30);
  --gsl-shadow-focus     : 0 0 0 3px rgba(255, 255, 255, 0.28), 0 8px 32px rgba(15, 20, 80, 0.28);
  --gsl-shadow-active    : 0 0 0 4px rgba(255, 255, 255, 0.38), 0 6px 24px rgba(15, 20, 80, 0.30);
  --gsl-radius           : 14px;
  --gsl-text             : rgba(255, 255, 255, 0.92);
  --gsl-text-muted       : rgba(255, 255, 255, 0.45);
  --gsl-chevron          : rgba(255, 255, 255, 1);
  --gsl-chevron-open     : rgba(255, 255, 255, 1);
  --gsl-opt-background           : rgba(18, 22, 48, 0.97);
  --gsl-opt-text         : #dde8ff;
  --gsl-transition       : 200ms cubic-bezier(0.4, 0, 0.2, 1);
  --gsl-font-label       : 'Outfit', sans-serif;
  --gsl-font-ctrl        : 'DM Sans', sans-serif;
  --gsl-valid-border     : rgba(70, 220, 130, 0.80);
  --gsl-valid-shadow     : 0 0 0 3px rgba(70, 220, 130, 0.25), 0 8px 32px rgba(15, 20, 80, 0.22);
  --gsl-invalid-border   : rgba(255, 90, 90, 0.80);
  --gsl-invalid-shadow   : 0 0 0 3px rgba(255, 90, 90, 0.25), 0 8px 32px rgba(15, 20, 80, 0.22);

  /* ── Shared cross-taglib height token ────────────────────────────
     Same value as glass:textInputStyles' .input-glass, glass:radio's
     .radio-glass, and dp:picker's .dp-trigger — keeps every glass form
     control the same height. Also declared independently inside
     TextInputStylesTagLib's stylesheet (identical value) so a
     page that loads only one of the two taglibs still resolves the
     correct height. */
  --gform-height          : 44px;
}

/* ══════════════════════════════════════════════════════════════════
   WRAPPER  —  owns all visual states.
   The native <select> is transparent and used only for interaction.
   ══════════════════════════════════════════════════════════════════ */
.gsl-wrapper {
  position                : relative;
  display                 : block;
  /* Always fills 100% of its containing block (e.g. a Bootstrap
     column) rather than shrink-wrapping to the native <select>'s own
     intrinsic/preferred width. */
  width                   : 100%;
  max-width               : 100%;
  height                  : var(--gform-height);
  /* Matches .radio-glass's spacing (see GlassmorphicRadioTagLib) on right
     and bottom so glass controls — radios, selects, pickers — keep
     consistent gaps between them whether stacked vertically or sitting
     side by side in a row. Left is intentionally 0 so the control stays
     flush with whatever it's aligned against (a column edge, a label
     above it, etc.) instead of double-gapping alongside that edge's own
     spacing.
     Top is intentionally 0 (was 15px) — glass:textInputStyles' .input-glass
     carries NO margin of its own (its only spacing comes from the
     .glass-form-group wrapper's margin-bottom); a top margin here alone
     pushed .gsl-wrapper's box 15px lower than .input-glass's box, so a
     glass:select and an alphaNumericTextField placed side by side in the
     same row visibly drifted out of vertical alignment even though both
     are pinned to the same --gform-height. Removing it makes both boxes
     start flush at the top of their row/column. */
  margin                  : 0 15px 15px 0;
  /* Small explicit interior padding, added per request to slightly tighten
     the pill's overall roominess. box-sizing:border-box below means this is
     subtracted from the fixed 44px height (not added on top of it), so the
     wrapper's outer, visible height still matches --gform-height exactly —
     only the interior space available to .gsl-select shrinks. */
  padding                 : 2px 0;
  box-sizing              : border-box;
  border-radius           : var(--gsl-radius);
  background              : linear-gradient(135deg, rgba(255,255,255,0.18), rgba(255,255,255,0.08));
  backdrop-filter         : blur(16px) saturate(1.5);
  -webkit-backdrop-filter : blur(16px) saturate(1.5);
  border                  : 1px solid var(--gsl-border);
  box-shadow              : var(--gsl-shadow),
                            inset 0  1px 0 rgba(255,255,255,0.22),
                            inset 0 -1px 0 rgba(0,0,0,0.08);
  transition              : border-color var(--gsl-transition),
                            box-shadow   var(--gsl-transition),
                            background   var(--gsl-transition),
                            transform    var(--gsl-transition);
}

/* Shimmer sheen */
.gsl-wrapper::before {
  content        : '';
  position       : absolute;
  inset          : 0;
  border-radius  : var(--gsl-radius);
  background     : linear-gradient(180deg, rgba(255,255,255,0.10) 0%, transparent 60%);
  pointer-events : none;
  z-index        : 0;
}

/* ── HOVER ─────────────────────────────────────────────────────── */
.gsl-wrapper:hover:not(.gsl-disabled) {
  border-color : var(--gsl-border-hover);
  box-shadow   : var(--gsl-shadow-hover),
                 inset 0  1px 0 rgba(255,255,255,0.30),
                 inset 0 -1px 0 rgba(0,0,0,0.10);
  background   : linear-gradient(135deg, rgba(255,255,255,0.22), rgba(255,255,255,0.11));
}

/* ── FOCUS ─────────────────────────────────────────────────────── */
.gsl-wrapper:focus-within:not(.gsl-disabled) {
  border-color : var(--gsl-border-focus);
  box-shadow   : var(--gsl-shadow-focus),
                 inset 0 1px 0 rgba(255,255,255,0.30);
  background   : linear-gradient(135deg, rgba(255,255,255,0.24), rgba(255,255,255,0.12));
}

/* ── ACTIVE (via JS .gsl-active class on mousedown / focus) ──────── */
.gsl-wrapper.gsl-active:not(.gsl-disabled) {
  border-color : var(--gsl-border-focus);
  box-shadow   : var(--gsl-shadow-active),
                 inset 0 1px 0 rgba(255,255,255,0.28);
  transform    : scale(0.995);
}

/* ── DISABLED (no value chosen) ───────────────────────────────────
   Mutes the control visually. Does NOT apply when .gsl-disabled-selected
   is also present — that combination is handled separately below.
─────────────────────────────────────────────────────────────────── */
.gsl-wrapper.gsl-disabled:not(.gsl-disabled-selected) {
  opacity        : 0.42;
  cursor         : not-allowed;
  filter         : grayscale(35%);
  transform      : none !important;
  box-shadow     : none !important;
  border-color   : rgba(255, 255, 255, 0.12) !important;
  background     : rgba(255, 255, 255, 0.05) !important;
}

/* ── DISABLED + SELECTED ──────────────────────────────────────────
   The control is locked but its chosen value must remain visible.
   Keeps the glass look (background, border, glow) while still
   communicating non-interactivity (reduced opacity, no hover lift,
   not-allowed cursor, lock badge).
─────────────────────────────────────────────────────────────────── */
.gsl-wrapper.gsl-disabled.gsl-disabled-selected {
  background     : linear-gradient(135deg, rgba(255,255,255,0.24), rgba(255,255,255,0.12)) !important;
  border-color   : rgba(255, 255, 255, 0.5) !important;
  box-shadow     : 0 8px 28px rgba(255, 255, 255, 0.07),
                   inset 0 0 0 1px rgba(255, 255, 255, 0.2) !important;
  opacity        : 0.65;
  cursor         : not-allowed;
  filter         : grayscale(20%);
  transform      : none !important;
}

/* Cursor: pointer-events must stay ON for the wrapper, otherwise the
   pointer "falls through" to the parent and the not-allowed cursor is
   never shown. Every child shows not-allowed too (label, chevron etc. are
   pointer-events:none so they defer to the wrapper's cursor). */
.gsl-wrapper.gsl-disabled,
.gsl-wrapper.gsl-disabled .gsl-select {
  cursor : not-allowed !important;
}

/* Lock badge — communicates "selected but locked" clearly */
.gsl-wrapper.gsl-disabled.gsl-disabled-selected::after {
  content        : '\\1F512';   /* 🔒 */
  position       : absolute;
  top            : 4px;
  right          : 8px;
  font-size      : 11px;
  opacity        : 0.7;
  line-height    : 1;
  pointer-events : none;
  z-index        : 3;
}

/* Selected value stays white while locked */
.gsl-wrapper.gsl-disabled-selected .gsl-select:disabled {
  color  : #ffffff !important;
  cursor : not-allowed;
}

/* ── VALID ─────────────────────────────────────────────────────── */
.gsl-wrapper.gsl-valid,
.gsl-wrapper.is-valid {
  border-color : var(--gsl-valid-border);
  box-shadow   : var(--gsl-valid-shadow);
}
.gsl-wrapper.gsl-valid:hover,
.gsl-wrapper.is-valid:hover {
  border-color : rgba(70, 220, 130, 0.95);
  box-shadow   : 0 0 0 3px rgba(70, 220, 130, 0.30), var(--gsl-shadow-hover);
}
.gsl-wrapper.gsl-valid:focus-within,
.gsl-wrapper.is-valid:focus-within {
  border-color : rgba(70, 220, 130, 0.95);
  box-shadow   : 0 0 0 4px rgba(70, 220, 130, 0.35), var(--gsl-shadow-focus);
}

/* ── INVALID ────────────────────────────────────────────────────── */
.gsl-wrapper.gsl-invalid,
.gsl-wrapper.is-invalid {
  border-color : var(--gsl-invalid-border);
  box-shadow   : var(--gsl-invalid-shadow);
}
.gsl-wrapper.gsl-invalid:hover,
.gsl-wrapper.is-invalid:hover {
  border-color : rgba(255, 90, 90, 0.95);
  box-shadow   : 0 0 0 3px rgba(255, 90, 90, 0.30), var(--gsl-shadow-hover);
}
.gsl-wrapper.gsl-invalid:focus-within,
.gsl-wrapper.is-invalid:focus-within {
  border-color : rgba(255, 90, 90, 0.95);
  box-shadow   : 0 0 0 4px rgba(255, 90, 90, 0.35), var(--gsl-shadow-focus);
}

/* ══════════════════════════════════════════════════════════════════
   LABEL
   ══════════════════════════════════════════════════════════════════ */
.gsl-label {
  position       : absolute;
  top            : 0;
  left           : 17px;
  transform      : translateY(-50%);
  font-family    : var(--gsl-font-label);
  font-size      : 0.68rem;
  font-weight    : 700;
  letter-spacing : 0.11em;
  text-transform : uppercase;
  color          : rgba(255, 255, 255, 0.90);
  padding        : 0 5px;
  pointer-events : none;
  white-space    : nowrap;
  transition     : color var(--gsl-transition);
  z-index        : 2;
  background     : linear-gradient(135deg, rgba(255,255,255,0.18), rgba(255,255,255,0.06));
  backdrop-filter: blur(8px);
  border-radius  : 4px;
}
.gsl-wrapper:focus-within .gsl-label            { color: rgba(200, 235, 255, 0.98); }
.gsl-wrapper.gsl-valid    .gsl-label,
.gsl-wrapper.is-valid      .gsl-label            { color: rgba(100, 240, 160, 0.95); }
.gsl-wrapper.gsl-invalid   .gsl-label,
.gsl-wrapper.is-invalid    .gsl-label            { color: rgba(255, 130, 130, 0.95); }

/* ══════════════════════════════════════════════════════════════════
   <SELECT>  —  transparent shell; browser styles fully suppressed.
   All visual feedback is delegated to .gsl-wrapper above.
   ══════════════════════════════════════════════════════════════════ */
.gsl-select {
  -webkit-appearance : none;
  -moz-appearance    : none;
  appearance         : none;
  display            : block;
  width              : 100%;
  height             : 100%;
  margin             : 0;
  box-sizing         : border-box;
  /* Vertical padding removed — height now controls vertical space
     (was 13px top/bottom); line-height centers the text within it. */
  /* Real vertical padding (not line-height — line-height has no visible
     effect on a <select>'s value position in most browsers once
     appearance:none is set, which is why the previous line-height-only
     attempt at reducing this space produced no visible change). 7px was
     chosen to fit the shorter 44px --gform-height (was 9px at the
     previous 50px, and 13px before that). box-sizing:border-box below
     means these 7px are carved out of the select's own height:100%, so
     the wrapper's overall 44px stays exactly matched regardless of
     this value. */
  padding            : 7px 40px 7px 14px;
  line-height        : normal;
  border-radius      : var(--gsl-radius);
  /* Lock every visual property so no browser/framework pseudo-state
     can bleed colour, shadow or outline onto this element           */
  background         : transparent !important;
  background-image   : none        !important;
  border             : none        !important;
  box-shadow         : none        !important;
  -webkit-box-shadow : none        !important;
  outline            : none        !important;
  /* Typography — kept identical to TextInputStylesTagLib's
     .input-glass input ('DM Sans', 1rem) so a glass:select renders
     with the same font and size as the alphanumeric/text fields on
     the same form. */
  font-family        : var(--gsl-font-ctrl);
  font-size          : 1rem;
  font-weight        : 400;
  color              : var(--gsl-text-muted);   /* muted until a real value is chosen */
  cursor             : pointer;
  position           : relative;
  z-index            : 1;
  transition         : color var(--gsl-transition);
}

/*
 * ── COMPLETE BROWSER/FRAMEWORK VALIDATION SUPPRESSION ──────────────────────
 *
 * Every pseudo-class that could paint a ring or change background on the raw
 * <select> is neutralised here.  This includes:
 *   • Native :valid / :invalid  (fire on page load for required + empty value)
 *   • Bootstrap .was-validated  (fires after first submit attempt)
 *   • Bootstrap utility classes .is-valid / .is-invalid
 *   • Firefox's :-moz-ui-invalid
 *
 * !important is intentional and necessary — without it, Bootstrap's specificity
 * wins and the red/green flash reappears.
 * ─────────────────────────────────────────────────────────────────────────── */
.gsl-select:required,
.gsl-select:optional,
.gsl-select:valid,
.gsl-select:invalid,
.gsl-select:-moz-ui-invalid,
.gsl-select:-moz-focusring,
.gsl-select:focus,
.gsl-select:focus-visible,
.gsl-select:hover,
.gsl-select:active,
.gsl-select:focus:valid,
.gsl-select:focus:invalid,
.was-validated .gsl-select:valid,
.was-validated .gsl-select:invalid,
.gsl-select.is-valid,
.gsl-select.is-invalid {
  background         : transparent !important;
  background-image   : none        !important;
  border             : none        !important;
  box-shadow         : none        !important;
  -webkit-box-shadow : none        !important;
  outline            : none        !important;
  padding-right      : 40px        !important;
}

/*
 * :-moz-focusring specifically — Firefox paints its own dotted focus
 * rectangle a couple of pixels INSIDE a <select>'s padding box on
 * keyboard focus, independent of the `outline: none` above (Firefox
 * doesn't route it through the standard outline mechanism for this
 * element the way other browsers do). Left unhandled, this is exactly
 * what makes the white focus border look like it's sitting 1-2px
 * inside the control instead of flush with its true edge. Setting
 * outline explicitly to none/0 on this pseudo-class removes it so the
 * only visible border is .gsl-wrapper's own, right on the first pixel.
 */
.gsl-select:-moz-focusring {
  outline        : none !important;
  outline-offset : 0     !important;
  color          : inherit;
}

/*
 * ── TEXT COLOUR: muted for placeholder, white once a real value is chosen ───
 *
 * JS adds .gsl-has-value to the <select> whenever its value is non-empty.
 * Without it the base colour above (--gsl-text-muted) stays in effect.
 * !important is needed to beat the broad pseudo-class block above which does
 * not set colour but whose combined specificity could otherwise interfere.
 * Hardcoded to #ffffff rather than var(--gsl-text) on purpose — --gsl-text
 * flips to a dark navy under the light-mode media query below, which would
 * make a selected value unreadable against these controls' still-dark
 * glass background in light mode. Adjust here if the light-mode background
 * itself is ever changed to something a dark text colour would suit.
 * ─────────────────────────────────────────────────────────────────────────── */
.gsl-select.gsl-has-value {
  color : #ffffff !important;
}

/*
 * ── noSelection / placeholder: displayed text is white too ─────────────────
 * The tag adds .gsl-no-selection to the <select> whenever the noSelection
 * or placeholder attribute renders a placeholder option. Disabled selects keep the muted colour.
 * ─────────────────────────────────────────────────────────────────────────── */
.gsl-select.gsl-no-selection:not(:disabled) {
  color : #ffffff !important;
}

/*
 * ── REQUIRED / INVALID: displayed text stays white, never red ──────────────
 *
 * Page-level or framework rules (Bootstrap form validation, app CSS such as
 * select:required:invalid { color: red }) can tint the displayed text of a
 * required/invalid select. Validation feedback belongs to the wrapper's ring
 * and label (.gsl-invalid / .is-invalid), so the text itself is pinned white.
 * ─────────────────────────────────────────────────────────────────────────── */
.gsl-select:required,
.gsl-select:invalid,
.gsl-select:-moz-ui-invalid,
.was-validated .gsl-select:invalid,
.gsl-select.is-invalid,
.gsl-select.is-valid {
  color : #ffffff !important;
}

/* Disabled */
.gsl-select:disabled {
  cursor : not-allowed;
  color  : var(--gsl-text-muted);
}

/* Placeholder / noSelection option */
.gsl-select option[disabled] {
  color      : #ffffff;
  font-style : italic;
}

/* Option list */
.gsl-select option {
  background-color : var(--gsl-opt-background);
  color            : var(--gsl-opt-text);
  font-family      : var(--gsl-font-ctrl);
  font-size        : 1rem;
  padding          : 6px 14px;
}
.gsl-select option:checked {
  background-color : rgba(80, 160, 255, 0.35);
}

/* Bootstrap utility reset */
.gsl-select.form-control,
.gsl-select.form-select {
  background-image : none !important;
}

/* Multi-select scrollbar */
.gsl-select[multiple] {
  padding-right   : 16px;
  cursor          : default;
  scrollbar-width : thin;
  scrollbar-color : rgba(255,255,255,0.22) transparent;
}
.gsl-select[multiple]::-webkit-scrollbar       { width: 5px; }
.gsl-select[multiple]::-webkit-scrollbar-track { background: transparent; }
.gsl-select[multiple]::-webkit-scrollbar-thumb {
  background-color : rgba(255,255,255,0.22);
  border-radius    : 10px;
}

/* ══════════════════════════════════════════════════════════════════
   CHEVRON
   ══════════════════════════════════════════════════════════════════ */
.gsl-chevron {
  position       : absolute;
  right          : 14px;
  top            : 0;
  bottom         : 0;
  margin         : auto 0;
  height         : 18px;
  pointer-events : none;
  display        : flex;
  align-items    : center;
  color          : var(--gsl-chevron);
  transition     : color     var(--gsl-transition),
                   transform var(--gsl-transition);
  z-index        : 2;
}
.gsl-chevron i             { font-size: 14px; display: block; }

/* Driven by :focus-within (persists for as long as the <select> is
   focused, which in virtually every browser is also however long its
   native option list stays open — there's no CSS-visible "list is open"
   state for a native select, so focus is the closest reliable proxy) and
   by .gsl-active (a short-lived JS-managed "being pressed" state, added
   on mousedown/keyboard-focus and cleared on mouseup/mouseleave/blur —
   see the JS below) for a slightly stronger rotation while the control
   is actively being pressed. To make an outside click reliably rotate
   the chevron back, the JS below explicitly blurs the <select> on
   outside click rather than trying to drive this purely off .gsl-active,
   since .gsl-active alone is cleared almost immediately after any click
   (on mouseup) and would rotate the chevron back before the dropdown
   visually closes. */
.gsl-wrapper:focus-within .gsl-chevron,
.gsl-wrapper.gsl-active   .gsl-chevron {
  color     : var(--gsl-chevron-open);
  transform : rotate(180deg);
}
.gsl-wrapper.gsl-active   .gsl-chevron { transform: rotate(180deg) scale(1.1); }

/* ══════════════════════════════════════════════════════════════════
   LIGHT-MODE OVERRIDES
   ══════════════════════════════════════════════════════════════════ */
@media (prefers-color-scheme: light) {
  :root {
    --gsl-border         : rgba(160, 190, 230, 0.55);
    --gsl-border-hover   : rgba(100, 150, 220, 0.65);
    --gsl-border-focus   : rgba(255, 255, 255, 0.95);
    --gsl-shadow         : 0 8px 32px rgba(80, 110, 200, 0.14);
    --gsl-shadow-hover   : 0 10px 36px rgba(80, 110, 200, 0.22);
    --gsl-shadow-focus   : 0 0 0 3px rgba(255, 255, 255, 0.35), 0 8px 32px rgba(80, 110, 200, 0.18);
    --gsl-shadow-active  : 0 0 0 4px rgba(255, 255, 255, 0.45), 0 6px 24px rgba(80, 110, 200, 0.22);
    --gsl-text           : rgba(20,  40,  90, 0.90);
    --gsl-text-muted     : rgba(80,  110, 160, 0.65);
    --gsl-chevron        : rgba(255, 255, 255, 1);
    --gsl-chevron-open   : rgba(255, 255, 255, 1);
    --gsl-opt-background         : #eef2ff;
    --gsl-opt-text       : #1a2460;
    --gsl-valid-shadow   : 0 0 0 3px rgba(30, 180, 90, 0.22), 0 8px 32px rgba(80, 110, 200, 0.14);
    --gsl-invalid-shadow : 0 0 0 3px rgba(210, 50, 50, 0.22), 0 8px 32px rgba(80, 110, 200, 0.14);
  }
}
</style>

<script id="glass-select-taglib-script">
/* ── GlassSelect interactivity ─────────────────────────────────────────────
   Exported as window.gslInit for dynamic/ajax pages.
   ─────────────────────────────────────────────────────────────────────── */
(function () {
  function initGslInteractivity() {
    document.querySelectorAll('.gsl-wrapper:not([data-gsl-init])').forEach(function (wrapper) {
      wrapper.setAttribute('data-gsl-init', '1');

      var sel = wrapper.querySelector('.gsl-select');
      if (!sel) return;

      /* Mark disabled wrapper so pointer-events and opacity apply */
      if (sel.disabled) {
        wrapper.classList.add('gsl-disabled');
      }

      /* ── TEXT COLOUR: toggle .gsl-has-value based on whether a real
            (non-empty) option is selected.  Runs on load so server-side
            pre-filled values (edit forms) also start white.
            Also keeps the wrapper's .gsl-disabled-selected (disabled +
            value chosen → locked look with lock badge) in step.        */
      function syncHasValue() {
        var has = sel.value !== '';
        sel.classList.toggle('gsl-has-value', has);
        wrapper.classList.toggle('gsl-disabled-selected', sel.disabled && has);
      }
      syncHasValue();
      /* Exposed so scripts can force a re-sync, e.g. after setting
         option.selected = true directly (which bypasses the setters
         wrapped below): window.gslSync(selectEl)                      */
      sel.gslSync = syncHasValue;

      /* NOTE: listener is on 'change', NOT inside onchange="…" attribute.
         The inline onchange="this.blur()" that was previously on the <select>
         fired synchronously and blurred the element before this listener ran,
         preventing syncHasValue() from ever seeing the new value.
         blur() is now called here, after the colour is already updated.     */
      sel.addEventListener('change', function () {
        syncHasValue();
        sel.blur();
      });

      /* ── PROGRAMMATIC CHANGES ─────────────────────────────────────────────
         select.value = 'x' / select.selectedIndex = n fire NO 'change'
         event, so the listener above never ran and the text stayed muted.
         Wrap the two setters on THIS element only (the native prototype
         is left alone) so every programmatic assignment re-syncs.        */
      ['value', 'selectedIndex'].forEach(function (prop) {
        var desc = Object.getOwnPropertyDescriptor(HTMLSelectElement.prototype, prop);
        if (!desc || !desc.set) return;
        Object.defineProperty(sel, prop, {
          configurable : true,
          enumerable   : desc.enumerable,
          get          : function () { return desc.get.call(this); },
          set          : function (v) { desc.set.call(this, v); syncHasValue(); }
        });
      });

      /* Options added/removed/re-marked selected (e.g. a script rebuilding
         a dependent select via innerHTML + appendChild).                  */
      if (typeof MutationObserver === 'function') {
        new MutationObserver(syncHasValue).observe(sel, {
          childList       : true,
          subtree         : true,
          attributes      : true,
          attributeFilter : ['selected', 'value']
        });
      }

      /* form.reset() restores defaults without firing 'change'; the reset
         is applied after the event, hence the 0ms defer.                  */
      if (sel.form) {
        sel.form.addEventListener('reset', function () {
          setTimeout(syncHasValue, 0);
        });
      }

      /* ── OPEN ON FOCUS / ACTIVE, CLOSE ON BLUR ───────────────────────────
         A mouse click on the select already opens its native option list
         on its own — that's ordinary <select> behaviour and needs no help
         from us. What browsers don't do by default is open that list when
         focus arrives via keyboard (Tab) — normally a follow-up
         Enter/Space/Arrow-Down is required. `viaMouse` lets the focus
         handler tell the two apart so it only calls showPicker() for the
         keyboard case: calling it for a mouse-originated focus as well
         would fight the click's own native open/close toggle and could
         make the list flash open then instantly shut on the same click.
         Closing needs no explicit handling — the browser already dismisses
         an open <select> list the instant it loses focus; blur here just
         also drops our own .gsl-active styling hook in step with that.   */
      var viaMouse = false;

      wrapper.addEventListener('mousedown', function () {
        viaMouse = true;
        if (!sel.disabled) wrapper.classList.add('gsl-active');
        // Clear the flag once this click's synchronous event sequence
        // (mousedown → focus → mouseup → click) has finished, so a later
        // focus caused by Tab/keyboard navigation is correctly treated
        // as non-mouse again.
        setTimeout(function () { viaMouse = false; }, 0);
      });
      wrapper.addEventListener('mouseup', function () {
        wrapper.classList.remove('gsl-active');
      });
      wrapper.addEventListener('mouseleave', function () {
        wrapper.classList.remove('gsl-active');
      });

      sel.addEventListener('focus', function () {
        wrapper.classList.add('gsl-active');
        if (!viaMouse && !sel.disabled && !sel.multiple && typeof sel.showPicker === 'function') {
          try {
            sel.showPicker();
          } catch (err) {
            // No transient user activation, or unsupported in this
            // browser/context — fall back silently to native behaviour
            // (user presses Enter/Space/Arrow-Down to open as usual).
          }
        }
      });
      sel.addEventListener('blur', function () {
        wrapper.classList.remove('gsl-active');
      });

      /* ── TAB / SHIFT+TAB WHILE OPEN ───────────────────────────────────────
         Native <select> behaviour already closes an open option list and
         moves focus to the next (Tab) or previous (Shift+Tab) focusable
         element — that's standard, un-hijackable browser focus management
         and this handler does NOT call preventDefault() or otherwise
         interfere with it. It exists purely as a same-frame safety net so
         .gsl-active (and therefore the chevron's rotated state) clears the
         instant Tab is pressed, rather than waiting on 'blur' to fire,
         which in a couple of browser/OS combinations can lag a frame
         behind the dropdown's own close animation.                        */
      sel.addEventListener('keydown', function (e) {
        if (e.key === 'Tab') {
          wrapper.classList.remove('gsl-active');
        }
      });
    });

    /* ── CLICK OUTSIDE CLOSES THE DROPDOWN ────────────────────────────────
       This sits inside initGslInteractivity (which can re-run for
       AJAX/dynamic content via window.gslInit()), but the
       window.__gslOutsideClickBound flag below ensures the actual
       document-level listener is only ever attached once per page,
       regardless of how many times initGslInteractivity runs.

       Native <select> dismisses its own open option list on an outside
       click, but in several browsers the <select> ELEMENT ITSELF stays
       focused afterwards — the popup closes while :focus-within remains
       true, leaving the chevron visually stuck in its rotated "open"
       position. Explicitly blurring any focused glass-select whose
       wrapper doesn't contain the click target fixes this: blur clears
       :focus-within (and, via the 'blur' listener above, .gsl-active too),
       so the chevron reliably rotates back the moment the user clicks
       anywhere outside the control.                                       */
    if (!window.__gslOutsideClickBound) {
      window.__gslOutsideClickBound = true;
      document.addEventListener('mousedown', function (e) {
        document.querySelectorAll('.gsl-wrapper').forEach(function (w) {
          if (w.contains(e.target)) return;
          var s = w.querySelector('.gsl-select');
          if (s && document.activeElement === s) {
            s.blur();
          }
        });
      });
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initGslInteractivity);
  } else {
    initGslInteractivity();
  }

  window.gslInit = initGslInteractivity;
  window.gslSync = function (el) { if (el && el.gslSync) el.gslSync(); };
})();
</script>'''
    }

    /* ── Wrapper ─────────────────────────────────────────────────── */
    String styleAttr = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ""
    // is-disabled only            → .gsl-disabled
    // disabled + value chosen     → .gsl-disabled .gsl-disabled-selected
    String disabledClass = disabled ? " gsl-disabled" : ""
    if (disabled && hasSelection) disabledClass += " gsl-disabled-selected"
    out << "<div class=\"gsl-wrapper${disabledClass}\"${styleAttr}>"

    /* ── Label ───────────────────────────────────────────────────── */
    if (label) {
      out << "<label for=\"${h(id)}\" class=\"gsl-label\">${h(label)}</label>"
    }

    /* ── <select> open tag ───────────────────────────────────────── */
    String multiAttr = multiple ? " multiple" : ""
    String requiredAttr = required ? " required" : ""
    String disabledAttr = disabled ? " disabled" : ""
    String ariaDisabled = disabled ? " aria-disabled=\"true\"" : ""
    String extraClass = cssClass ? " ${h(cssClass)}" : ""
    if (noSelection || (placeholder && !multiple)) {
      extraClass += " gsl-no-selection"   // placeholder text is white too
    }

    /* NOTE: no onchange="this.blur()" here — blur is handled in the JS
       change listener AFTER syncHasValue() updates the text colour.    */
    out << "<select id=\"${h(id)}\" name=\"${h(name)}\" class=\"gsl-select${extraClass}\"${multiAttr}${requiredAttr}${disabledAttr}${ariaDisabled}>"

    /* ── noSelection / placeholder option ────────────────────────── */
    if (noSelection) {
      noSelection.each { k, v ->
        String sel = (!multiple && selected.isEmpty()) ? " selected" : ""
        out << "<option value=\"${h(k.toString())}\" disabled${sel}>${h(v.toString())}</option>"
      }
    } else if (placeholder && !multiple) {
      String sel = selected.isEmpty() ? " selected" : ""
      out << "<option value=\"\" disabled${sel}>${h(placeholder)}</option>"
    }

    /* ── Options from collection ─────────────────────────────────── */
    from.each { item ->
      String optVal = optionKey ? item."${optionKey}"?.toString() ?: "" : item?.toString() ?: ""
      String optText = optionValue ? item."${optionValue}"?.toString() ?: "" : item?.toString() ?: ""
      String selAttr = selected.contains(optVal) ? " selected" : ""
      out << "<option value=\"${h(optVal)}\"${selAttr}>${h(optText)}</option>"
    }

    /* ── No "from" supplied: show the value itself ───────────────── */
    if (!from) {
      def shown = selected.findAll { it }          // skip null/empty strings
      if (!multiple && shown) {
        shown = [shown.first()]                    // single select shows one value
      }
      shown.each { String v ->
        out << "<option value=\"${h(v)}\" selected>${h(v)}</option>"
      }
    }

    out << "</select>"

    /* ── Chevron icon (single-select only) ───────────────────────── */
    if (!multiple) {
      out << '<span class="gsl-chevron" aria-hidden="true"><i class="fa-solid fa-chevron-down"></i></span>'
    }

    out << "</div>"
  }

  /**
   * Treats the following as TRUE (case-insensitive where applicable):
   *   Boolean true, Integer/Long 1, String "true" / "yes" / "on" / "1"
   * Everything else (null, false, 0, "false", "no", "off", "0") → false.
   * Same coercion as GlassmorphicRadioTagLib.isTruthy.
   */
  private static boolean isTruthy(Object val) {
    if (val == null) return false
    if (val instanceof Boolean) return val
    if (val instanceof Number) return val.intValue() == 1
    switch (val.toString().trim().toLowerCase()) {
      case 'true': case 'yes': case 'on': case '1': return true
      default: return false
    }
  }
}
