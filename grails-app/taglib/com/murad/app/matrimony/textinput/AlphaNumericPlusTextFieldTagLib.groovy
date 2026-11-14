package com.murad.app.matrimony.textinput


import com.murad.app.matrimony.GlassTooltipStylesTagLib

/**
 * Renders a glassmorphism-styled text input that accepts ONLY:
 *   - letters       [a-zA-Z]
 *   - digits        [0-9]
 *   - the '+' character  — AT MOST ONE occurrence in the whole value, and
 *                          it must be immediately followed by a digit
 *                          (never a letter, never a space)
 *   - the ' ' (single space) character
 *
 * No other characters (no hyphens, underscores, dots, or any other symbol)
 * are permitted anywhere in the value. Space is allowed but is NOT a free
 * separator: any run of 2+ consecutive spaces the user types is collapsed
 * down to exactly one space as they type (see oninput below), so normal
 * "United Kingdom" / "+91 AB 1234"-style single-space-between-tokens input
 * works, but "double  spacing" cannot happen. Unlike
 * AlphaNumericTextFieldTagLib, there is still no trimming and no
 * collapsing of interior '+' runs — leading/trailing spaces are left
 * as-is (only *interior* multi-space runs get collapsed).
 *
 * Because letters, digits, '+' and spaces can all appear together (in that
 * order), an ISD code and a country name can be searched/typed as a single
 * combined value — e.g. "+91 India" — without anything being stripped:
 * the '+' + digit rule only constrains the character *immediately* after
 * the '+', so the space-separated country name that follows is left
 * completely untouched.
 *
 * '+' RULES
 * ---------
 *  - Only the FIRST '+' the user types anywhere in the field is kept; any
 *    subsequent '+' keystrokes are silently dropped as they're typed.
 *  - Whatever immediately follows that single '+' must be a digit — never
 *    a space and never a letter. Both are stripped live as they're typed:
 *      * typing "+ab91" collapses to "+91" live, because letters can
 *        never sit directly against the '+'.
 *      * typing "+ 91" (a space right after '+') collapses to "+91" live,
 *        because a space can never sit directly against the '+' either.
 *    Digits, spaces, and letters are all still fine anywhere else in the
 *    value once at least one digit separates them from the '+' (e.g.
 *    "+91 AB 1234", "United Kingdom", "+91 India").
 *  - The instant a space is stripped from directly after the '+', a
 *    message reading "No spaces allowed after the +" appears directly
 *    below the field. The instant a letter is stripped from directly
 *    after the '+', a message reading "No alphabets allowed after the +"
 *    appears instead. Both are rendered via the same hidden-by-default
 *    <div id="{id}-plus-error"> (oninput flips it to display:block and
 *    sets its text to whichever message applies — the two conditions are
 *    mutually exclusive since the single character right after '+' can
 *    only be one thing at a time). It's cleared again as soon as the
 *    offending character is no longer present directly after the '+',
 *    after 8 seconds, or as soon as the user types again (whichever comes
 *    first). role="alert" + aria-live="polite" on that div, and
 *    aria-describedby="{id}-plus-error {id}-space-error" on the <input>,
 *    so screen readers announce it without requiring focus to move.
 *
 * SPACE RULES
 * -----------
 *  - Typing a second consecutive space (anywhere in the value, not just
 *    right after '+') collapses live down to a single space (see class
 *    doc above), and the instant that happens, a message reading "Only
 *    one space is allowed at a time" appears directly below the field (a
 *    hidden-by-default <div id="{id}-space-error"> that oninput flips to
 *    display:block). Same auto-dismiss behaviour as the '+' messages: it
 *    hides itself after 8 seconds, or immediately as soon as the user
 *    types again and the condition is no longer met. This is distinct
 *    from the "no space directly after +" rule above, which is reported
 *    through the plus-error div instead.
 *
 * NUMBER RULES — nothing may sit *between* two digits
 * ----------------------------------------------------
 *  - Digits belonging to a number must stay contiguous: a space or a
 *    letter (or a run of either) sandwiched directly between two digits
 *    is stripped live as it's typed, closing the gap back up. Which
 *    message appears depends on what the offending run is made of:
 *      * typing "91ab234" (letters only, wedged between the '1' and the
 *        '2') collapses to "91234" live, and a message reading "No
 *        alphabets allowed between the numbers" appears directly below
 *        the field (a hidden-by-default <div id="{id}-num-alpha-error">
 *        that oninput flips to display:block).
 *      * typing "91 234" (spaces only, wedged between the '1' and the
 *        '2') collapses to "91234" live, and a message reading "No
 *        spaces allowed between the numbers" appears instead (a
 *        hidden-by-default <div id="{id}-num-space-error">).
 *      * typing "91 a 234" or "91a b234" (a run that mixes at least one
 *        letter AND at least one space, wedged between the '1' and the
 *        '2') collapses to "91234" live too, but shows a third message
 *        instead — "Combination of alphabets and spaces is not allowed
 *        between numbers" (a hidden-by-default
 *        <div id="{id}-num-mixed-error">) — since it's neither a pure
 *        letter run nor a pure space run.
 *    All three auto-dismiss after 8 seconds, or immediately as soon as
 *    the user types again and the condition is no longer met — same
 *    pattern as every other inline message on this field.
 *  - These three rules only fire when a digit sits on *both* sides of
 *    the gap. A space that separates a completed number from a following
 *    word (e.g. "+91 India", where a letter — not a digit — follows the
 *    space) is untouched, since letters are perfectly fine once they are
 *    no longer wedged between two digits. Likewise the single space
 *    right after "+91" in "+91 9876543210" IS between two digits ('1'
 *    and '9') and so is stripped, keeping the phone-number digits
 *    contiguous; only free-text tokens like a country name are exempt.
 *  - Independent of, and checked before, the interior-multi-space
 *    collapse described under SPACE RULES above: a single space wedged
 *    between two digits is removed outright (not collapsed to one),
 *    because no space at all is permitted there.
 *  - Separately, ANY letter that ends up sitting directly against a
 *    digit with no space between them — even when the *other* side of
 *    that letter is not a digit (so none of the three sandwiched cases
 *    above apply), and it isn't the character immediately after '+'
 *    either (which has its own messages under '+' RULES) — is also
 *    stripped live. Typing "India91" or "91abc", for instance, strips
 *    the letters back out and shows "You can't mix alphabets and
 *    numbers" (a hidden-by-default <div id="{id}-mixed-alphanum-error">).
 *    In short: anywhere a letter and a digit would otherwise end up
 *    touching directly, at least one space is required between them.
 *
 * This shape is convenient for fields like international dial-code-
 * prefixed identifiers or free-text country names (e.g. "+91 AB 1234",
 * "United Kingdom", "+91 India") where '+' only ever appears once, at the
 * very start of a numeric code, and spaces are single word-separators.
 * This tag does not itself enforce '+' position (start-of-string) or
 * forbid leading/trailing space — combine with a server-side validator if
 * a stricter rule is required.
 *
 * Usage:
 *   <glass:tooltipStyles />            <- emit once in layout <head>
 *   <glass:textInputStyles />          <- emit once in layout <head>
 *   <glass:alphaNumericPlusTextField name="isdCode" value="${isdCode}"
 *       label="ISD Code"
 *       helperText="Letters, numbers, spaces, and a single + (followed by digits) only" />
 *
 * Features
 * --------
 *  - Accepts [a-zA-Z0-9+ ] (space included) and nothing else
 *  - At most one '+' in the whole value; extra '+' keystrokes are dropped
 *  - '+' must be immediately followed by a digit; a space typed right
 *    after '+' is stripped live and shows "No spaces allowed after the +";
 *    a letter typed right after '+' is stripped live and shows "No
 *    alphabets allowed after the +" — both messages render in the same
 *    slot right below the field while their respective condition holds
 *  - Interior runs of 2+ spaces are collapsed to a single space on input,
 *    and a "Only one space is allowed at a time" message is shown right
 *    below the field the instant that happens
 *  - A space or letter wedged directly between two digits is stripped
 *    live, closing the number back up, and shows "No spaces allowed
 *    between the numbers", "No alphabets allowed between the numbers",
 *    or (if the wedged run mixes both) "Combination of alphabets and
 *    spaces is not allowed between numbers", right below the field while
 *    that's happening
 *  - Separately, a letter directly touching a digit anywhere else (with
 *    no space between them, and not immediately after '+') is stripped
 *    live too, showing "You can't mix alphabets and numbers" — letters
 *    and digits must always be separated by at least one space
 *  - All seven inline messages auto-dismiss after 8 seconds, or
 *    immediately once the user types again and the condition no longer
 *    holds
 *  - Combined ISD-code + country-name values (e.g. "+91 India") work as a
 *    single field value, since the '+' rule only constrains the character
 *    directly touching the '+'
 *  - Floating label: centred/resting at blur+empty; raised at focus/value
 *  - Clear button (omitted when disabled=true)
 *  - Disabled state via .input-glass.is-disabled shell class
 *  - Tooltip on clear button (portal pattern — GlassTooltipStylesTagLib)
 *
 * CSS responsibility
 * ------------------
 *  This taglib emits NO CSS of its own — it reuses TextInputStylesTagLib
 *  in full for the input shell, floating label, clear button, validation
 *  icon, and helper text (.input-glass, .floating-label, .clear-btn, etc.),
 *  and GlassTooltipStylesTagLib for the clear-button tooltip. Calling
 *  emitTextInputStylesOnce() / emitTooltipStylesOnce() here guarantees the
 *  shared stylesheet is present even if the caller forgot to add
 *  <glass:textInputStyles /> / <glass:tooltipStyles /> in the layout, while
 *  the pageScope guard on each means it is still only ever written once per
 *  page no matter how many glass input taglibs are used together.
 *  All helper text — the plain informational helperText div and the six
 *  inline validation message slots (plus-error, space-error,
 *  num-alpha-error, num-space-error, num-mixed-error,
 *  mixed-alphanum-error) — is rendered in white (inline
 *  style="color:#fff;") to read clearly against the
 *  glassmorphism background.
 *
 * Bootstrap compatibility
 * -----------------------
 *  <input> carries class="form-control" for Grails binding and hasErrors().
 *  TextInputStylesTagLib uses .input-glass input selectors (higher
 *  specificity) so glass styles always win over Bootstrap's .form-control.
 *
 * Portal JS
 * ---------
 *  Single unified IIFE: (function(PLACEMENT, IS_AUTO){...})('auto', true)
 *  Auto vs fixed placement handled via the two arguments. Identical portal
 *  tooltip implementation to AlphaNumericTextFieldTagLib — see that class
 *  for the full per-line rationale on scroll/resize repositioning, IE11
 *  fallbacks, and the non-passive touchstart listener.
 *
 * Mobile-first & cross-browser notes
 * ------------------------------------
 *  HTML INPUT ATTRIBUTES
 *  - inputmode="text"     : the field accepts letters as well as digits, so
 *                            "text" is used rather than "tel"/"numeric" —
 *                            those keyboards hide the letter keys entirely
 *                            on iOS/Android.
 *  - autocomplete         : attr-controlled (default "off"); pass "name" etc. to enable
 *  - autocorrect="off"    : disables iOS Safari word correction
 *  - autocapitalize="off" : disables iOS/Android first-char capitalisation
 *  - spellcheck="false"   : suppresses red-squiggle underlining in Chrome/Firefox/Safari
 *
 *  PATTERN ATTRIBUTE — no v-flag hazards
 *  AlphaNumericTextFieldTagLib's separator-based pattern had to work around
 *  several Chromium 112+ / Safari 17+ v-flag (Unicode sets) restrictions on
 *  hyphens and space escapes inside character classes. Only one of those
 *  restrictions is even relevant here: a literal space inside a [...]
 *  character class needs no escaping in any regex flavour (v-flag
 *  included) — it is only *leading/trailing whitespace around the pattern
 *  string itself* that historically caused confusion, not whitespace
 *  written inside the brackets.
 *
 *  The pattern is now structured (rather than a flat [A-Za-z0-9+ ]+) to
 *  express the "at most one +, and it must be followed by a digit" rule
 *  natively, since native HTML pattern validation is still worth doing
 *  even though the oninput handler already prevents the browser from ever
 *  holding an invalid value:
 *    [A-Za-z0-9 ]*(\+[0-9][A-Za-z0-9 ]*)?
 *  Read as: any run of letters/digits/spaces (no '+' allowed here), then
 *  OPTIONALLY a single literal '+' immediately followed by one digit, then
 *  any further run of letters/digits/spaces (still no '+' — a second '+'
 *  cannot appear anywhere, since the only '+' slot in the whole pattern is
 *  that one optional group). Because the digit is mandatory right after
 *  the '+' in that group, this pattern already rejects a '+' immediately
 *  followed by a space or a letter — the oninput handler below just
 *  additionally strips those live and surfaces which one occurred. HTML
 *  pattern matching is implicitly anchored (^...$), so no explicit anchors
 *  are needed. This is exactly what makes combined values like "+91 India"
 *  valid: the optional group consumes "+9", and the trailing
 *  [A-Za-z0-9 ]* consumes "1 India".
 *
 *  The pattern is intentionally NOT tightened to also forbid a space or
 *  letter between two digits (e.g. it still matches "91 234" or
 *  "91ab234" at the native HTML-validation level) — the oninput handler
 *  below is what strips those live before they can ever be submitted, so
 *  duplicating that stricter contiguous-digits rule into the regex would
 *  only add complexity without changing what a user can actually type or
 *  submit.
 *
 *  oninput applies these transforms in sequence (see inline comment for detail):
 *    1. Strip disallowed characters      : /[^a-zA-Z0-9+ ]/g
 *    2. Drop every '+' after the first one that was typed
 *    3. Check whether a space, or a letter, currently sits directly after
 *       that single '+' (checked before either is stripped, since a
 *       stripped character can no longer be detected)
 *    4. Strip any space(s) sitting directly after the '+', and strip any
 *       letter(s) sitting directly after the '+'
 *    5. Collapse interior multi-space runs to one : / {2,}/g -> ' '
 *    6. Toggle the plus-error message (space-after-plus vs
 *       alphabet-after-plus text, from step 3) and the space-error
 *       message (interior double-space, from step 5), each auto-dismissing
 *       after 8 seconds or on the next keystroke
 *  No leading/trailing trim — a leading/trailing space the user is
 *  actively typing is left alone; only *runs of 2 or more* spaces are
 *  collapsed.
 *
 *  TOUCHSTART PASSIVE LISTENER
 *  - touchstart uses false (non-passive) so e.preventDefault() is honoured.
 *    All other listeners use _passiveOpt ({passive:true} when supported).
 *
 *  CLEAR BUTTON JS GUARD
 *  - onclick guards: typeof clearInput==="function"&&clearInput(this)
 *    so the button fails silently if the style taglib JS has not yet run.
 *
 * Dependencies
 * ------------
 *  - GlassTooltipStylesTagLib    (same package)
 *  - TextInputStylesTagLib  (same package)
 *  - Font Awesome 6 (fa-solid fa-xmark)
 */
class AlphaNumericPlusTextFieldTagLib {

  static namespace = "glass"

  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  private TextInputStylesTagLib getTextInputStylesTagLib() {
    grailsApplication.mainContext.getBean(TextInputStylesTagLib)
  }

  /**
   * @attr name             REQUIRED — name attribute for the input
   * @attr value            — initial value
   * @attr id                — id attribute (defaults to name)
   * @attr label              — floating-label text
   * @attr placeholder        — placeholder (ignored when floatingLabel=true)
   * @attr helperText         — small hint shown below the field (rendered in white)
   * @attr required           — true/false (default false)
   * @attr minlength          — minimum character length
   * @attr maxlength          — maximum character length
   * @attr class               — extra CSS classes on <input>
   * @attr disabled            — true/false (default false)
   * @attr readonly            — true/false (default false)
   * @attr autofocus           — true/false (default false)
   * @attr onchange            — JS onChange handler
   * @attr oninput             — JS onInput handler (appended after built-ins)
   * @attr wrapper             — wrap in .glass-form-group (default true)
   * @attr floatingLabel       — use floating label (default true)
   * @attr tooltipPlacement    — top | right | bottom | left | auto (default auto)
   * @attr tooltip             — tooltip text (default "Clear")
   * @attr autocomplete        — HTML autocomplete value (default "off")
   */
  def alphaNumericPlusTextField = { attrs ->
    if (!attrs.name) {
      throwTagError("Tag [alphaNumericPlusTextField] is missing required attribute [name]")
    }

    String name = attrs.name
    String id = attrs.id ?: name
    String value = attrs.value ?: ''
    String label = attrs.label ?: ''
    String placeholder = attrs.placeholder ?: ''
    String helperText = attrs.helperText ?: ''
    String minlength = attrs.minlength ?: ''
    String maxlength = attrs.maxlength ?: ''
    String additionalClasses = attrs.class ?: ''
    boolean required = attrs.required?.toString()?.toBoolean() ?: false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean readonly = attrs.readonly?.toString()?.toBoolean() ?: false
    boolean autofocus = attrs.autofocus?.toString()?.toBoolean() ?: false
    boolean wrapper = attrs.wrapper?.toString()?.toBoolean() != false
    boolean floatingLabel = attrs.floatingLabel?.toString()?.toBoolean() != false
    String onchange = attrs.onchange ?: ''
    String oninput = attrs.oninput ?: ''
    String autocomplete = attrs.autocomplete ?: 'off'

    List validPlacements = ['top', 'right', 'bottom', 'left']
    String rawPlacement = attrs.tooltipPlacement?.toString()?.toLowerCase() ?: 'auto'
    boolean autoPlacement = !validPlacements.contains(rawPlacement)
    String tooltipPlacement = autoPlacement ? 'auto' : rawPlacement
    String tooltipText = attrs.tooltip ?: 'Clear'

    // This taglib emits NO CSS of its own — .input-glass, .floating-label,
    // .clear-btn, .validation-icon, and .glass-helper-text all come from
    // TextInputStylesTagLib; the clear-button tooltip portal styling
    // comes from GlassTooltipStylesTagLib. Both guard themselves with a
    // pageScope flag, so calling them here is always safe and only ever
    // writes the shared <style>/<script> block once per page.
    tooltipStylesTagLib.emitTooltipStylesOnce()
    textInputStylesTagLib.emitTextInputStylesOnce()

    if (wrapper) {
      String wrapperClass = 'glass-form-group'
      if (floatingLabel && label) wrapperClass += ' has-floating-label'
      out << "<div class=\"${wrapperClass}\">"
      if (label && !floatingLabel) {
        out << "<label for=\"${id.encodeAsHTML()}\">${label.encodeAsHTML()}</label>"
      }
    }

    String shellClass = disabled ? 'input-glass is-disabled' : 'input-glass'
    if (value) shellClass += ' has-value'
    out << "<div class=\"${shellClass}\">"

    // Hoisted so both the oninput handler (below) and the two inline
    // validation-message elements (emitted after .input-glass closes) can
    // share the same escaped ids without recomputing them.
    String escapedId = id.encodeAsHTML()
    String plusErrorId = "${escapedId}-plus-error"
    String spaceErrorId = "${escapedId}-space-error"
    String numSpaceErrorId = "${escapedId}-num-space-error"
    String numAlphaErrorId = "${escapedId}-num-alpha-error"
    String numMixedErrorId = "${escapedId}-num-mixed-error"
    String mixedAlphaNumErrorId = "${escapedId}-mixed-alphanum-error"

    out << '<input '
    out << 'type="text" '
    out << "name=\"${name.encodeAsHTML()}\" "
    out << "id=\"${id.encodeAsHTML()}\" "
    if (value) out << "value=\"${value.encodeAsHTML()}\" "

    if (floatingLabel && label) {
      // Single space triggers :placeholder-shown reliably across all browsers,
      // driving the CSS blur-down / raise animation for the floating label.
      out << 'placeholder=" " '
    } else if (placeholder) {
      out << "placeholder=\"${placeholder.encodeAsHTML()}\" "
    }

    if (minlength) out << "minlength=\"${minlength}\" "
    if (maxlength) out << "maxlength=\"${maxlength}\" "

    String inputClasses = "form-control ${additionalClasses}".trim()
    out << "class=\"${inputClasses.encodeAsHTML()}\" "

    // Structured pattern (see class-level doc comment for the full
    // rationale): letters/digits/spaces, then an OPTIONAL single '+'
    // immediately followed by exactly one digit, then more letters/
    // digits/spaces. Because '+' only appears in that one optional slot,
    // a second '+' anywhere in the value can never match, and because the
    // digit right after '+' is mandatory, a '+' followed by a space or a
    // letter can never match either. HTML pattern matching is implicitly
    // anchored (^...$). Combined ISD-code + country-name values (e.g.
    // "+91 India") satisfy this pattern.
    out << 'pattern="[A-Za-z0-9 ]*(\\+[0-9][A-Za-z0-9 ]*)?" '
    out << 'title="Only letters, numbers, single spaces, and a single + (immediately followed by a digit, never a space or letter) are allowed" '

    out << 'inputmode="text" '                              // includes letters, so not "tel"/"numeric"
    out << "autocomplete=\"${autocomplete.encodeAsHTML()}\" " // default "off"; caller passes "name" etc. to enable
    out << 'autocorrect="off" '                             // iOS Safari: disable word correction
    out << 'autocapitalize="off" '                          // iOS/Android: disable first-char capitalisation
    out << 'spellcheck="false" '                            // Chrome/Firefox/Safari: suppress red-squiggle underlining

    if (required) out << 'required '
    if (disabled) out << 'disabled '
    if (readonly) out << 'readonly '
    if (autofocus) out << 'autofocus '

    // oninput applies these transforms in sequence:
    //   1. Strip any character that is not a letter, digit, '+', or space.
    //   2. Keep only the FIRST '+' typed anywhere in the value; every
    //      subsequent '+' keystroke is dropped as it's typed.
    //   3. Check whether a space, or a letter, is currently sitting
    //      directly after that single '+' — these are the "typed a space
    //      right after +" / "typed a letter right after +" events the
    //      plus-error message reacts to. Checked *before* either gets
    //      stripped in the next step, since after stripping neither can
    //      be found. The two checks are mutually exclusive: the single
    //      character immediately after '+' is either a digit, a space, a
    //      letter, or nothing at all.
    //   4. Strip any space(s) sitting directly after that single '+', and
    //      strip any letter(s) sitting directly after that single '+' —
    //      e.g. "+ 91" becomes "+91" live, and "+ab91" becomes "+91"
    //      live, because '+' may only ever be followed by a digit.
    //   5. Check whether the run of characters directly wedged between
    //      two digits (letters and/or spaces, run-length agnostic) is a
    //      pure letter run, a pure space run, or a mix of both, then
    //      strip that whole run and close the digits back up — repeating
    //      until no such run remains. E.g. "91ab234" -> "91234" (pure
    //      letters), "91 234" -> "91234" (pure spaces), "91 a 234" ->
    //      "91234" (mixed). This drives the num-alpha-error,
    //      num-space-error, and num-mixed-error messages respectively.
    //   6. Check whether a letter is now sitting directly against a digit
    //      anywhere else in the value (either order, letter-then-digit or
    //      digit-then-letter) — this can only be the "one-sided" case
    //      left over after step 5, since any digit-both-sides sandwich
    //      was already resolved. Strip the letter run touching that digit
    //      (e.g. "India91" -> "91", "91abc" -> "91"), repeating until
    //      none remain. This is what the mixed-alphanum-error message
    //      reacts to.
    //   7. Check whether the value currently contains a run of 2+ spaces
    //      anywhere — this is the "typed a second consecutive space"
    //      event the space-error message reacts to. Checked *before* it
    //      gets collapsed in the next step, for the same reason as step 3.
    //   8. Collapse any *interior* run of 2+ spaces down to a single
    //      space, preserving cursor-friendly typing.
    //   9. Show/hide the plus-error message based on step 3 — its text is
    //      set to "No spaces allowed after the +" or "No alphabets
    //      allowed after the +" depending on which condition fired —
    //      show/hide the num-mixed-error / num-alpha-error /
    //      num-space-error messages based on step 5, show/hide the
    //      mixed-alphanum-error message based on step 6, and show/hide
    //      the "Only one space is allowed at a time" message based on
    //      step 7. Any pending auto-hide timer from a previous keystroke
    //      is cleared first for each message — this is what makes each
    //      message disappear the instant the user types again once its
    //      triggering condition is no longer true, and also what gives it
    //      a fresh 8-second countdown while the condition keeps
    //      recurring. When a message is shown, a setTimeout hides it
    //      again after 8000ms if no further keystroke happens first.
    // No leading/trailing trim is applied, so a space the user is actively
    // typing at either end of the field is left alone; only runs of 2+
    // spaces anywhere in the value get collapsed.
    String onInputHandler =
      'this.value=this.value.replace(/[^a-zA-Z0-9+ ]/g,"");' +
        '(function(el){' +
        'var seen=false;' +
        'el.value=el.value.replace(/\\+/g,function(m){if(seen){return "";}seen=true;return m;});' +
        'var hadSpaceAfterPlus=/\\+ /.test(el.value);' +
        'var hadAlphaAfterPlus=/\\+[a-zA-Z]/.test(el.value);' +
        'el.value=el.value.replace(/\\+ +/g,"+");' +
        'el.value=el.value.replace(/\\+[a-zA-Z]+/g,"+");' +
        'var hadMixedBetweenNumbers=false;' +
        'var hadAlphaBetweenNumbers=false;' +
        'var hadSpaceBetweenNumbers=false;' +
        'while(/\\d[a-zA-Z ]+\\d/.test(el.value)){' +
        'el.value=el.value.replace(/(\\d)([a-zA-Z ]+)(\\d)/,function(m,a,mid,b){' +
        'var hasAlpha=/[a-zA-Z]/.test(mid);' +
        'var hasSpace=/ /.test(mid);' +
        'if(hasAlpha&&hasSpace){hadMixedBetweenNumbers=true;}' +
        'else if(hasAlpha){hadAlphaBetweenNumbers=true;}' +
        'else{hadSpaceBetweenNumbers=true;}' +
        'return a+b;' +
        '});' +
        '}' +
        'var hadMixedAlphaNum=/[a-zA-Z]\\d/.test(el.value)||/\\d[a-zA-Z]/.test(el.value);' +
        'while(/[a-zA-Z]+\\d/.test(el.value)){el.value=el.value.replace(/[a-zA-Z]+(\\d)/g,"$1");}' +
        'while(/\\d[a-zA-Z]+/.test(el.value)){el.value=el.value.replace(/(\\d)[a-zA-Z]+/g,"$1");}' +
        'var hadMultiSpace=/ {2,}/.test(el.value);' +
        'el.value=el.value.replace(/ {2,}/g," ");' +
        'var plusErrEl=document.getElementById("' + plusErrorId + '");' +
        'var spaceErrEl=document.getElementById("' + spaceErrorId + '");' +
        'var numAlphaErrEl=document.getElementById("' + numAlphaErrorId + '");' +
        'var numSpaceErrEl=document.getElementById("' + numSpaceErrorId + '");' +
        'var numMixedErrEl=document.getElementById("' + numMixedErrorId + '");' +
        'var mixedAlphaNumErrEl=document.getElementById("' + mixedAlphaNumErrorId + '");' +
        'if(el._glassPlusErrTimer){clearTimeout(el._glassPlusErrTimer);el._glassPlusErrTimer=null;}' +
        'if(el._glassSpaceErrTimer){clearTimeout(el._glassSpaceErrTimer);el._glassSpaceErrTimer=null;}' +
        'if(el._glassNumAlphaErrTimer){clearTimeout(el._glassNumAlphaErrTimer);el._glassNumAlphaErrTimer=null;}' +
        'if(el._glassNumSpaceErrTimer){clearTimeout(el._glassNumSpaceErrTimer);el._glassNumSpaceErrTimer=null;}' +
        'if(el._glassNumMixedErrTimer){clearTimeout(el._glassNumMixedErrTimer);el._glassNumMixedErrTimer=null;}' +
        'if(el._glassMixedAlphaNumErrTimer){clearTimeout(el._glassMixedAlphaNumErrTimer);el._glassMixedAlphaNumErrTimer=null;}' +
        'if(plusErrEl){' +
        'if(hadSpaceAfterPlus){' +
        'plusErrEl.textContent="No spaces allowed after the +";' +
        'plusErrEl.style.display="block";' +
        'el._glassPlusErrTimer=setTimeout(function(){plusErrEl.style.display="none";},8000);' +
        '}else if(hadAlphaAfterPlus){' +
        'plusErrEl.textContent="No alphabets allowed after the +";' +
        'plusErrEl.style.display="block";' +
        'el._glassPlusErrTimer=setTimeout(function(){plusErrEl.style.display="none";},8000);' +
        '}else{' +
        'plusErrEl.style.display="none";' +
        '}' +
        '}' +
        'if(numMixedErrEl){' +
        'if(hadMixedBetweenNumbers){' +
        'numMixedErrEl.style.display="block";' +
        'el._glassNumMixedErrTimer=setTimeout(function(){numMixedErrEl.style.display="none";},8000);' +
        '}else{' +
        'numMixedErrEl.style.display="none";' +
        '}' +
        '}' +
        'if(numAlphaErrEl){' +
        'if(hadAlphaBetweenNumbers){' +
        'numAlphaErrEl.style.display="block";' +
        'el._glassNumAlphaErrTimer=setTimeout(function(){numAlphaErrEl.style.display="none";},8000);' +
        '}else{' +
        'numAlphaErrEl.style.display="none";' +
        '}' +
        '}' +
        'if(numSpaceErrEl){' +
        'if(hadSpaceBetweenNumbers){' +
        'numSpaceErrEl.style.display="block";' +
        'el._glassNumSpaceErrTimer=setTimeout(function(){numSpaceErrEl.style.display="none";},8000);' +
        '}else{' +
        'numSpaceErrEl.style.display="none";' +
        '}' +
        '}' +
        'if(mixedAlphaNumErrEl){' +
        'if(hadMixedAlphaNum){' +
        'mixedAlphaNumErrEl.style.display="block";' +
        'el._glassMixedAlphaNumErrTimer=setTimeout(function(){mixedAlphaNumErrEl.style.display="none";},8000);' +
        '}else{' +
        'mixedAlphaNumErrEl.style.display="none";' +
        '}' +
        '}' +
        'if(spaceErrEl){' +
        'if(hadMultiSpace){' +
        'spaceErrEl.style.display="block";' +
        'el._glassSpaceErrTimer=setTimeout(function(){spaceErrEl.style.display="none";},8000);' +
        '}else{' +
        'spaceErrEl.style.display="none";' +
        '}' +
        '}' +
        '})(this);' +
        'if(typeof toggleClearButton==="function")toggleClearButton(this);'
    if (oninput) onInputHandler += " ${oninput}"
    out << "oninput=\"${onInputHandler.encodeAsHTML()}\" "
    if (onchange) out << "onchange=\"${onchange.encodeAsHTML()}\" "
    out << "aria-describedby=\"${plusErrorId} ${spaceErrorId} ${numAlphaErrorId} ${numSpaceErrorId} ${numMixedErrorId} ${mixedAlphaNumErrorId}\" "

    out << '/>'

    if (!disabled) {
      String placementAttr = autoPlacement ? '' : " data-tooltip-placement=\"${tooltipPlacement}\""

      out << "<button type=\"button\" class=\"clear-btn\"${placementAttr} "
      out << 'aria-label="Clear" '
      out << "data-tooltip-text=\"${tooltipText.encodeAsHTML()}\" "
      // Guard clearInput so the button fails silently if the style taglib JS has not yet run.
      out << 'onclick="typeof clearInput===\'function\'&&clearInput(this)">'
      out << '<i class="fa-solid fa-xmark" aria-hidden="true"></i>'
      out << '<span class="glass-tooltip" aria-hidden="true">' + tooltipText.encodeAsHTML() + '</span>'
      out << '</button>'

      out << """<script>
(function(PLACEMENT, IS_AUTO){

  /* Passive listener support detection (IE11 / old Android fallback) */
  var _passiveOpt = false;
  try {
    var _o = Object.defineProperty({}, 'passive', { get: function(){ _passiveOpt = { passive: true }; } });
    window.addEventListener('_glassTest', null, _o);
    window.removeEventListener('_glassTest', null, _o);
  } catch(e){}

  var OPPOSITE = { top:'bottom', bottom:'top', left:'right', right:'left' };

  /* -webkit-transform helper — Safari < 9, older Android WebKit */
  function setTransform(el, val){
    el.style.webkitTransform = val;
    el.style.transform       = val;
  }

  function getBtn(){
    var inp = document.getElementById('${escapedId}');
    return inp ? inp.parentElement.querySelector('.clear-btn') : null;
  }

  /* Coarse placement from button's viewport quadrant (auto mode only) */
  function coarsePlacement(btn){
    var r  = btn.getBoundingClientRect();
    var vw = window.innerWidth  || document.documentElement.clientWidth;
    var vh = window.innerHeight || document.documentElement.clientHeight;
    var cx = r.left + r.width  / 2;
    var cy = r.top  + r.height / 2;
    if      (cy < vh * 0.25) return 'bottom';
    else if (cy > vh * 0.75) return 'top';
    else if (cx < vw * 0.5)  return 'right';
    else                     return 'left';
  }

  function portalCoords(btn, placement, portal){
    var br      = btn.getBoundingClientRect();
    var pr      = portal.getBoundingClientRect();
    /* --tt-gap: IE11 returns "" from getPropertyValue so parseInt falls back to 8 */
    var gap     = parseInt(getComputedStyle(document.documentElement)
                    .getPropertyValue('--tt-gap')) || 8;
    /* scrollX/scrollY: undefined in IE11 — pageXOffset/pageYOffset are the fallbacks */
    var scrollX = window.scrollX !== undefined ? window.scrollX : (window.pageXOffset || 0);
    var scrollY = window.scrollY !== undefined ? window.scrollY : (window.pageYOffset || 0);
    var top, left;
    if (placement === 'top') {
      top  = br.top    + scrollY - pr.height - gap;
      left = br.left   + scrollX + (br.width - pr.width) / 2;
    } else if (placement === 'bottom') {
      top  = br.bottom + scrollY + gap;
      left = br.left   + scrollX + (br.width - pr.width) / 2;
    } else if (placement === 'right') {
      top  = br.top    + scrollY + (br.height - pr.height) / 2;
      left = br.right  + scrollX + gap;
    } else {
      top  = br.top    + scrollY + (br.height - pr.height) / 2;
      left = br.left   + scrollX - pr.width - gap;
    }
    return { top: top, left: left };
  }

  /* Overflow check + flip (auto mode only) */
  function resolvedPlacement(btn, portal){
    var candidate = coarsePlacement(btn);
    portal.setAttribute('data-tooltip-placement', candidate);
    portal.style.visibility = 'hidden';
    portal.style.opacity    = '0';
    portal.style.display    = 'block';
    var coords = portalCoords(btn, candidate, portal);
    portal.style.top  = coords.top  + 'px';
    portal.style.left = coords.left + 'px';
    var br   = portal.getBoundingClientRect();
    var vw   = window.innerWidth  || document.documentElement.clientWidth;
    var vh   = window.innerHeight || document.documentElement.clientHeight;
    var clips = (candidate === 'top'    && br.top    < 0)
             || (candidate === 'bottom' && br.bottom > vh)
             || (candidate === 'left'   && br.left   < 0)
             || (candidate === 'right'  && br.right  > vw);
    portal.style.visibility = '';
    portal.style.display    = '';
    return clips ? OPPOSITE[candidate] : candidate;
  }

  var _portal    = null;
  var _hideTimer = null;

  function getOrCreatePortal(btn){
    if (!_portal) {
      _portal = document.createElement('span');
      _portal.className = 'glass-tooltip glass-tooltip-portal';
      _portal.setAttribute('aria-hidden', 'true');
      if (!IS_AUTO) _portal.setAttribute('data-tooltip-placement', PLACEMENT);
      _portal.style.position = 'absolute';
      _portal.style.margin   = '0';
      document.body.appendChild(_portal);
    }
    /* btn.dataset: IE10 fallback to getAttribute */
    _portal.textContent = (btn.dataset
      ? btn.dataset.tooltipText
      : btn.getAttribute('data-tooltip-text')) || 'Clear';
    return _portal;
  }

  function showPortal(btn){
    if (_hideTimer) { clearTimeout(_hideTimer); _hideTimer = null; }
    var portal    = getOrCreatePortal(btn);
    var placement = IS_AUTO ? resolvedPlacement(btn, portal) : PLACEMENT;
    portal.setAttribute('data-tooltip-placement', placement);
    var coords    = portalCoords(btn, placement, portal);
    portal.style.top     = coords.top  + 'px';
    portal.style.left    = coords.left + 'px';
    portal.style.opacity = '1';
    setTransform(portal, 'scale(1)');
    btn._glassPortal = portal;
  }

  function hidePortal(btn, delay){
    var doHide = function(){
      if (btn._glassPortal) {
        btn._glassPortal.style.opacity = '0';
        setTransform(btn._glassPortal, 'scale(0.88)');
      }
    };
    if (delay) { _hideTimer = setTimeout(doHide, delay); } else { doHide(); }
  }

  function updatePortalPosition(){
    var btn = getBtn();
    if (!btn || !btn._glassPortal) return;
    var placement = IS_AUTO
      ? (btn._glassPortal.getAttribute('data-tooltip-placement') || coarsePlacement(btn))
      : PLACEMENT;
    var coords = portalCoords(btn, placement, btn._glassPortal);
    btn._glassPortal.style.top  = coords.top  + 'px';
    btn._glassPortal.style.left = coords.left + 'px';
  }

  function bindBtn(){
    var btn = getBtn();
    if (!btn) return;

    btn.addEventListener('mouseenter',  function(){ showPortal(btn); },      _passiveOpt);
    btn.addEventListener('focusin',     function(){ showPortal(btn); },      _passiveOpt);
    btn.addEventListener('mouseleave',  function(){ hidePortal(btn, 0); },   _passiveOpt);
    btn.addEventListener('focusout',    function(){ hidePortal(btn, 0); },   _passiveOpt);

    /* touchstart uses false (non-passive) so e.preventDefault() is honoured.
     * Chrome 56+ silently drops preventDefault() in passive listeners.
     * preventDefault() only fires when the button is the direct touch target —
     * this stops iOS Safari from hiding the tooltip when a parent container
     * intercepts a scroll, while not blocking scroll on non-button swipes.    */
    btn.addEventListener('touchstart', function(e){
      if (e.target === btn || btn.contains(e.target)) {
        e.preventDefault();
      }
      showPortal(btn);
    }, false);

    btn.addEventListener('touchend',    function(){ hidePortal(btn, 320); }, _passiveOpt);
    btn.addEventListener('touchcancel', function(){ hidePortal(btn, 0); },   _passiveOpt);
  }

  window.addEventListener('resize', updatePortalPosition, _passiveOpt);
  window.addEventListener('scroll', updatePortalPosition, _passiveOpt);

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', bindBtn);
  } else {
    bindBtn();
  }

})('${tooltipPlacement}', ${autoPlacement});
</script>"""
    } // end !disabled

    if (floatingLabel && label) {
      out << "<label for=\"${id.encodeAsHTML()}\" class=\"floating-label\">${label.encodeAsHTML()}</label>"
    }

    out << '</div>' // .input-glass

    // Inline validation message #1 — hidden by default, toggled to
    // display:block by the oninput handler above the instant a space or a
    // letter is typed directly after the single '+'. Its text is set
    // dynamically by the oninput handler to whichever of "No spaces
    // allowed after the +" / "No alphabets allowed after the +" applies
    // (the two conditions are mutually exclusive, since only one
    // character can sit directly after '+' at a time). Auto-dismisses
    // after 8 seconds or immediately on the next keystroke once the
    // condition clears (see oninput handler). role="alert" + aria-live so
    // assistive tech announces it the moment it appears, without needing
    // focus to move. The static text below is just an inert placeholder —
    // it is always hidden (display:none) until JS overwrites it.
    out << "<div id=\"${plusErrorId}\" class=\"glass-helper-text glass-plus-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << 'No spaces allowed after the +'
    out << '</div>'

    // Inline validation message #2 — same behaviour as above, triggered
    // instead by typing a second consecutive space anywhere in the value
    // (which is live-collapsed back down to a single space). Distinct
    // from the "space directly after +" case above, which is reported
    // through the plus-error div instead.
    out << "<div id=\"${spaceErrorId}\" class=\"glass-helper-text glass-space-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << 'Only one space is allowed at a time'
    out << '</div>'

    // Inline validation message #3 — hidden by default, toggled to
    // display:block by the oninput handler the instant a letter-only run
    // (no space mixed in) is typed directly between two digits (and
    // stripped back out to close the number back up). If the run instead
    // contains both letters AND spaces, the num-mixed-error message fires
    // instead (see below) rather than this one. Same auto-dismiss
    // behaviour as the other messages.
    out << "<div id=\"${numAlphaErrorId}\" class=\"glass-helper-text glass-num-alpha-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << 'No alphabets allowed between the numbers'
    out << '</div>'

    // Inline validation message #4 — same behaviour as above, triggered
    // instead by a space-only run (no letters mixed in) typed directly
    // between two digits (removed outright, not collapsed, since no
    // space at all is permitted there). Does not fire for a space that
    // instead precedes a following word, e.g. "+91 India".
    out << "<div id=\"${numSpaceErrorId}\" class=\"glass-helper-text glass-num-space-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << 'No spaces allowed between the numbers'
    out << '</div>'

    // Inline validation message #5 — triggered when the run directly
    // between two digits contains BOTH at least one letter and at least
    // one space (e.g. "91 a 234" or "91a b234"), which is neither a pure
    // alphabet run nor a pure space run, so it gets its own message
    // instead of #3 or #4. The whole run is stripped and the number
    // closed back up, same as the other between-digits cases.
    out << "<div id=\"${numMixedErrorId}\" class=\"glass-helper-text glass-num-mixed-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << 'Combination of alphabets and spaces is not allowed between numbers'
    out << '</div>'

    // Inline validation message #6 — triggered whenever a letter sits
    // directly against a digit with no space between them anywhere else
    // in the value (i.e. the one-sided case not already covered by
    // messages #3/#4/#5 above, which only fire when a digit sits on
    // *both* sides of the gap, or by the plus-error messages, which only
    // fire directly after '+'). For example typing "India91" or "91abc"
    // strips the letters back out live and shows this message — letters
    // and digits must always be separated by at least one space anywhere
    // they'd otherwise touch directly.
    out << "<div id=\"${mixedAlphaNumErrorId}\" class=\"glass-helper-text glass-mixed-alphanum-error\" "
    out << 'style="display:none;color:#fff;" role="alert" aria-live="polite">'
    out << "You can't mix alphabets and numbers"
    out << '</div>'

    if (helperText) {
      // Plain informational helper text — rendered in white so it reads
      // clearly against the glassmorphism background, distinct from the
      // two warning-coloured inline messages above.
      out << "<div class=\"glass-helper-text\" style=\"color:#fff;\">${helperText.encodeAsHTML()}</div>"
    }

    if (wrapper) {
      out << '</div>' // .glass-form-group
    }
  }
}
