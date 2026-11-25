package com.murad.app.matrimony.textinput

import com.murad.app.matrimony.slideinsheet.GlassBottomSheetBaseTagLib

/**
 * glass:mobileInput — glassmorphic mobile-number text input with an
 * optional glass bottom-sheet country picker (merged from
 * GlassCountryBottomSheetTagLib / <sheet:countryPicker>).
 *
 * Built on top of TextInputStylesTagLib (shared shell / floating label /
 * clear button / validation icon / helper / error CSS + JS) and, in picker
 * mode, on GlassBottomSheetBaseTagLib (sheet CSS/JS, flag rendering,
 * tolerant country accessors) — the same base the country picker uses.
 *
 * Three prefix modes (chosen automatically)
 * ─────────────────────────────────────────
 *   1. PICKER   `countries` supplied  → flag + dial code button inside the
 *               field; tapping it slides up a searchable bottom sheet
 *               (flag + ISD code + country name rows, selected dot,
 *               tooltip, keyboard nav, focus trap — all from the base).
 *   2. SELECT   `countryCodes` has 2+ codes → invisible native <select>
 *               over the visible code (unchanged from the old version).
 *   3. FIXED    otherwise → static "+91" prefix (unchanged).
 *
 * Usage
 * ─────
 *   <%-- plain, India only --%>
 *   <glass:mobileInput name="mobile" label="Mobile Number" required="true"/>
 *
 *   <%-- with the bottom-sheet country picker --%>
 *   <glass:mobileInput name="mobile" label="Mobile Number" required="true"
 *       countries="${countries}"
 *       value="${contactDetail?.mobile}"
 *       selectedIsdCode="${contactDetail?.isdCode}"
 *       selectedCountry="${contactDetail?.country}"
 *       selectedFlag="${contactDetail?.flag}"
 *       isdCodeName="isdCode" countryName="country" flagName="flag"
 *       fullName="mobileE164"
 *       panelTitle="Select Country Code"
 *       searchPlaceholder="Search country or code…"
 *       showCountryName="false"/>
 *
 * Attributes — original
 * ─────────────────────
 *   name             (required) submitted field — national digits only
 *   id               element id; defaults to name
 *   label            floating label; default "Mobile Number"
 *   value            national digits, or full "+91 98765 43210" (code is split off)
 *   countryCode      default / selected calling code; default "+91"
 *   countryCodes     comma list or List (SELECT mode). Ignored in PICKER mode
 *   countryCodeName  name of a submitted field holding the selected code
 *   fullName         hidden E.164 field (+919876543210), empty until valid
 *   required, disabled, readonly, autofocus   boolean flags
 *   helper           hint under the field
 *   error            server-side error; shown immediately, cleared on next edit
 *   class            extra class(es) for the wrapping .glass-form-group
 *
 * Attributes — merged from the country picker (PICKER mode)
 * ─────────────────────────────────────────────────────────
 *   countries        List of Maps / domain objects. Tolerant accessors:
 *                      dial code : .dial_code | .dialCode | .isdCode
 *                      name      : .countryName | .name | .country
 *                      flag      : .countryFlag | .flag | .emoji
 *                    Presence of a non-empty list switches PICKER mode on.
 *   isdCodeName      hidden field for the ISD code (alias of countryCodeName;
 *                    default "isdCode" in picker mode)
 *   countryName      hidden field for the country name (default "country")
 *   flagName         hidden field for the flag value (default "flag")
 *   selectedIsdCode  pre-selected ISD code (alias of countryCode)
 *   selectedCountry  pre-selected country name (disambiguates shared codes
 *                    such as +1 US / Canada)
 *   selectedFlag     fallback flag if the selection is not found in `countries`
 *   buttonText       label shown when nothing is selected; default "Code"
 *   panelTitle       sheet title; default "Select Country Code"
 *   searchPlaceholder placeholder of the sheet search box
 *   showCountryName  true → button reads "+91 India"; default false (compact
 *                    "+91", flag always shown)
 *   cssClass         alias of `class`
 *   style            inline style for the outer .glass-form-group
 *
 * Matching of the initial selection (PICKER mode)
 *   code + country name → country name only (when `value` carries no "+code")
 *   → code only → raw selected* attributes.
 *
 * Behaviour (unchanged parts)
 * ───────────────────────────
 *  • type="tel" + inputmode="numeric", digits only, paste normalisation
 *  • Per-country length rules (India 10 digits from 6–9; generic 6–15 fallback)
 *  • Validity through setCustomValidity(); glass error line instead of bubble
 *  • Prefix fades in when the label floats (focus / has value)
 *  • window.glassMobileValidateAll(formOrRoot) → boolean
 *
 * New in the merge
 *  • Changing the country re-normalises/re-validates the number against that
 *    country's rule and refreshes the hidden E.164 field.
 *  • Bubbling CustomEvent "glassmobile:countrychange" on the number input,
 *    detail: {dialCode, country, flag, input}
 *  • Prefix width is measured in JS (picker mode) so long codes / names never
 *    overlap the digits.
 *
 * Guard keys are versioned. Bump the suffix whenever CSS or JS changes.
 */
class MobileInputTagLib extends GlassBottomSheetBaseTagLib {

  static namespace = "glass"

  static final String MOBILE_INPUT_STYLES_EMITTED = 'glass_mobileInputStylesEmitted_v3'
  static final String PICKER_ASSETS_EMITTED = 'glass_mobileInputPickerAssets_v1'
  static final String PICKER_PREFIX = 'gmcs'

  private static final String CHEVRON_SVG =
    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.3" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"/></svg>'

  protected AlphaNumericPlusTextFieldTagLib getAlphaNumericPlusTextFieldTagLib() {
    grailsApplication.mainContext.getBean(AlphaNumericPlusTextFieldTagLib)
  }

  def mobileInput = { attrs ->
    String name = attrs.name?.toString()
    if (!name) {
      throwTagError("Tag [glass:mobileInput] is missing required attribute [name]")
    }

    List countryList = toList(attrs.countries)
    boolean pickerMode = !countryList.isEmpty()

    // Shared glass text-input CSS + JS (own once-per-page guard)
    out << glass.textInputStyles()
    if (pickerMode) emitPickerAssetsOnce()
    emitMobileInputStylesOnce()

    String id = attrs.id?.toString() ?: name
    String label = attrs.label?.toString() ?: 'Mobile Number'
    String helper = attrs.helper?.toString()
    String error = attrs.error?.toString()
    String extraClass = (attrs['class'] ?: attrs.cssClass)?.toString()
    String wrapStyle = attrs.style?.toString()
    String fullName = attrs.fullName?.toString()
    String ccName = (attrs.isdCodeName ?: attrs.countryCodeName)?.toString()

    boolean required = truthy(attrs.required)
    boolean disabled = truthy(attrs.disabled)
    boolean readonly = truthy(attrs.readonly)
    boolean autofocus = truthy(attrs.autofocus)

    // ── Country codes ────────────────────────────────────────────────────
    List<String> ccList = []
    if (pickerMode) {
      ccList = countryList.collect { normalizeCc(countryDialCode(it)) }
    } else {
      def rawCodes = attrs.countryCodes
      if (rawCodes instanceof Collection) {
        ccList = rawCodes.collect { normalizeCc(it) }
      } else if (rawCodes) {
        ccList = rawCodes.toString().split(',').collect { normalizeCc(it) }
      }
    }
    ccList = ccList.findAll { it != '+' }.unique()

    String selIsdRaw = (attrs.selectedIsdCode ?: attrs.countryCode)?.toString()
    String selectedCc = normalizeCc(selIsdRaw?.trim() ? selIsdRaw : '+91')
    if (!pickerMode && ccList && !ccList.contains(selectedCc)) {
      ccList.add(0, selectedCc)
    }

    // ── Initial value (national digits; split off a leading +code) ───────
    String rawValue = attrs.value?.toString()?.trim() ?: ''
    boolean valueHadCode = false
    if (rawValue.startsWith('+')) {
      String intl = '+' + rawValue.replaceAll('[^0-9]', '')
      String match = (ccList + [selectedCc]).unique().sort { -it.length() }.find { intl.startsWith(it) }
      if (match) {
        selectedCc = match
        rawValue = intl.substring(match.length())
        valueHadCode = true
      } else {
        rawValue = intl.substring(1)
      }
    }
    String value = rawValue.replaceAll('[^0-9]', '')

    // ── PICKER: resolve the initially selected country ───────────────────
    String selectedCountry = attrs.selectedCountry?.toString() ?: ''
    String initIsd = selectedCc
    String initName = selectedCountry
    String initFlag = attrs.selectedFlag?.toString() ?: ''
    boolean showCountryName = truthy(attrs.showCountryName)
    String buttonText = attrs.buttonText?.toString() ?: 'Code'
    String panelTitle = attrs.panelTitle?.toString() ?: 'Select Country Code'
    String searchPlaceholder = attrs.searchPlaceholder?.toString() ?: 'Search country or code\u2026'

    def pickerMatch = null   // local on purpose: taglib instances may be shared across requests
    if (pickerMode) {
      def pick = null
      if (selectedCountry && !valueHadCode) {
        pick = countryList.find {
          countryDisplayName(it) == selectedCountry && normalizeCc(countryDialCode(it)) == selectedCc
        } ?: countryList.find { countryDisplayName(it) == selectedCountry }
      }
      if (!pick) {
        pick = countryList.find {
          normalizeCc(countryDialCode(it)) == selectedCc &&
            (!selectedCountry || countryDisplayName(it) == selectedCountry)
        } ?: countryList.find { normalizeCc(countryDialCode(it)) == selectedCc }
      }
      if (pick) {
        initIsd = countryDialCode(pick)
        initName = countryDisplayName(pick)
        initFlag = countryFlagValue(pick)
        selectedCc = normalizeCc(initIsd)
      } else {
        initIsd = selIsdRaw?.trim() ? selIsdRaw.trim() : selectedCc
      }
      pickerMatch = pick
    }

    // ── Markup pieces ────────────────────────────────────────────────────
    boolean hasSelect = !pickerMode && ccList.size() > 1
    String padLeft
    if (pickerMode) {
      int chars = showCountryName ? "${initIsd} ${initName}".length() : initIsd.length()
      padLeft = "calc(${chars * 0.62}rem + 88px)"   // JS refines this by measuring
    } else {
      int ccChars = hasSelect ? ccList*.length().max() : selectedCc.length()
      padLeft = hasSelect ? "calc(${ccChars * 0.62}rem + 56px)" : "calc(${ccChars * 0.62}rem + 40px)"
    }

    String eid = id.encodeAsHTML()
    String sheetId = "${id}-sheet"
    String listId = "${id}-list"
    String tooltipId = "${id}-tooltip"
    String titleId = "${id}-title"
    String searchId = "${id}-search"

    String ccBlock
    if (pickerMode) {
      String initLabel = buttonLabel(initIsd, initName, showCountryName, buttonText)
      ccBlock = '<span class="glass-mobile-cc glass-mobile-cc-picker">' +
        "<button type=\"button\" class=\"gmcs-btn\" aria-haspopup=\"dialog\" aria-expanded=\"false\" aria-controls=\"${h(sheetId)}\" aria-label=\"${h(panelTitle)}\"${disabled ? ' disabled' : ''}>" +
        "<span class=\"gmcs-btn-flag\" aria-hidden=\"true\">${flagMarkup(initFlag)}</span>" +
        "<span class=\"gmcs-btn-label\">${h(initLabel)}</span>" +
        "<span class=\"gmcs-btn-chevron\" aria-hidden=\"true\">${CHEVRON_SVG}</span>" +
        '</button></span>'
    } else if (hasSelect) {
      StringBuilder opts = new StringBuilder()
      ccList.each { String c ->
        opts << """<option value="${c.encodeAsHTML()}"${c == selectedCc ? ' selected' : ''}>${c.encodeAsHTML()}</option>"""
      }
      String selName = ccName ? """ name="${ccName.encodeAsHTML()}\"""" : ''
      ccBlock = """<span class="glass-mobile-cc"><span class="glass-mobile-cc-text" aria-hidden="true">${selectedCc.encodeAsHTML()}</span><span class="glass-mobile-caret" aria-hidden="true">&#9662;</span><select id="${eid}-cc" aria-label="Country calling code"${selName}${disabled ? ' disabled' : ''}>${opts}</select></span>"""
    } else {
      String hiddenCc = ccName ? """<input type="hidden" name="${ccName.encodeAsHTML()}" value="${selectedCc.encodeAsHTML()}"${disabled ? ' disabled' : ''}>""" : ''
      ccBlock = """<span class="glass-mobile-cc"><span class="glass-mobile-cc-text">${selectedCc.encodeAsHTML()}</span></span>${hiddenCc}"""
    }

    String shellClass = 'input-glass glass-mobile' +
      (hasSelect ? ' has-cc-select' : '') +
      (pickerMode ? ' has-picker' : '') +
      (value ? ' has-value' : '') +
      (disabled ? ' is-disabled' : '') +
      (error ? ' is-invalid' : '')

    String groupClass = 'glass-form-group has-floating-label glass-mobile-group' +
      (error ? ' has-error' : '') +
      (extraClass ? ' ' + extraClass.encodeAsHTML() : '')

    String describedBy = (helper ? "${eid}-help " : '') + "${eid}-error"
    String groupStyle = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ''

    StringBuilder html = new StringBuilder()
    html << """<div class="${groupClass}" data-glass-mobile-group${groupStyle}>"""

    if (pickerMode) {
      html << "<div class=\"gmcs-wrap glass-mobile-picker\" data-show-name=\"${showCountryName ? '1' : '0'}\" data-button-text=\"${h(buttonText)}\">"
    }

    html << """<div class="${shellClass}" data-cc="${selectedCc.encodeAsHTML()}">"""
    html << ccBlock
    html << """<input type="tel" id="${eid}" name="${name.encodeAsHTML()}" value="${value.encodeAsHTML()}" """
    html << """inputmode="numeric" enterkeyhint="next" autocomplete="tel-national" autocapitalize="off" autocorrect="off" spellcheck="false" """
    html << """placeholder=" " style="padding-left:${padLeft}" data-glass-mobile-input """
    html << """aria-describedby="${describedBy}" aria-invalid="${error ? 'true' : 'false'}"${required ? ' required' : ''}${disabled ? ' disabled' : ''}${readonly ? ' readonly' : ''}${autofocus ? ' autofocus' : ''}>"""
    html << """<label class="floating-label" for="${eid}">${label.encodeAsHTML()}</label>"""
    html << """<button type="button" class="clear-btn" onclick="clearInput(this)" aria-label="Clear mobile number" tabindex="-1">&times;</button>"""
    html << """<span class="validation-icon" aria-hidden="true"></span>"""
    html << """</div>"""   // shell

    if (pickerMode) {
      // Tooltip (flag + "+91 — India"); 8s auto-hide handled by the base JS
      String tipHtml = "No country selected yet"
      if (initIsd || initName) {
        String tipText = "${initIsd ?: ''}${initName ? ' \u2014 ' + initName : ''}".trim()
        String mk = flagMarkup(initFlag)
        tipHtml = (mk ? "${mk} " : "") + h(tipText)
      }
      html << "<div id=\"${h(tooltipId)}\" class=\"gmcs-tooltip\" role=\"tooltip\">${tipHtml}</div>"
      html << "<div class=\"gmcs-overlay\" style=\"opacity:0;visibility:hidden;\"></div>"

      html << "<div id=\"${h(sheetId)}\" class=\"gmcs-panel\" role=\"dialog\" aria-modal=\"true\" aria-labelledby=\"${h(titleId)}\" aria-hidden=\"true\" style=\"-webkit-transform:translateY(100%);transform:translateY(100%);visibility:hidden;\">"
      html << "<div class=\"gmcs-panel-handle\" aria-hidden=\"true\"></div>"
      html << "<div class=\"gmcs-panel-head\"><span id=\"${h(titleId)}\" class=\"gmcs-panel-title\">${h(panelTitle)}</span></div>"
      html << "<div class=\"gmcs-search-wrap\">"
      html << (alphaNumericPlusTextFieldTagLib.alphaNumericPlusTextField(
        name: searchId,
        id: searchId,
        value: '',
        placeholder: searchPlaceholder,
        wrapper: false,
        floatingLabel: false,
        autocomplete: 'off',
        autocapitalize: 'off',
        class: 'gmcs-search',
        tooltip: 'Clear search',
        tooltipPlacement: 'bottom'
      )?.toString() ?: '')
      html << "</div>"

      html << "<div id=\"${h(listId)}\" class=\"gmcs-list\" role=\"listbox\" aria-label=\"${h(panelTitle)}\">"
      countryList.each { country ->
        String dialCode = countryDialCode(country)
        String cName = countryDisplayName(country)
        String flagValue = countryFlagValue(country)
        boolean isSel = pickerMatch != null ? (country.is(pickerMatch) || country == pickerMatch) : false
        html << "<div class=\"gmcs-row${isSel ? ' gmcs-row-selected' : ''}\" role=\"option\" aria-selected=\"${isSel}\" tabindex=\"-1\" data-dial-code=\"${h(dialCode)}\" data-country=\"${h(cName)}\" data-flag=\"${h(flagValue)}\">"
        html << "<span class=\"gmcs-row-flag\" aria-hidden=\"true\">${flagMarkup(flagValue)}</span>"
        html << "<span class=\"gmcs-row-code\">${h(dialCode)}</span>"
        html << "<span class=\"gmcs-row-name\">${h(cName)}</span>"
        html << "<span class=\"gmcs-row-dot\" aria-hidden=\"true\"></span>"
        html << "</div>"
      }
      html << "<div class=\"gmcs-empty\" style=\"display:none\">No countries found</div>"
      html << "</div>"   // list
      html << "</div>"   // panel

      String isdField = ccName ?: 'isdCode'
      String cnField = attrs.countryName?.toString() ?: 'country'
      String flField = attrs.flagName?.toString() ?: 'flag'
      String dis = disabled ? ' disabled' : ''
      html << "<input type=\"hidden\" class=\"gmcs-hidden-isd\" name=\"${h(isdField)}\" value=\"${h(initIsd)}\"${dis}>"
      html << "<input type=\"hidden\" class=\"gmcs-hidden-country\" name=\"${h(cnField)}\" value=\"${h(initName)}\"${dis}>"
      html << "<input type=\"hidden\" class=\"gmcs-hidden-flag\" name=\"${h(flField)}\" value=\"${h(initFlag)}\"${dis}>"

      html << "</div>"   // gmcs-wrap
    }

    if (helper) {
      html << """<div class="glass-helper-text" id="${eid}-help">${helper.encodeAsHTML()}</div>"""
    }
    html << """<div class="glass-error${error ? ' show' : ''}" id="${eid}-error" role="alert">${error ? error.encodeAsHTML() : ''}</div>"""
    if (fullName) {
      String full = value ? selectedCc + value : ''
      html << """<input type="hidden" name="${fullName.encodeAsHTML()}" value="${full.encodeAsHTML()}" data-glass-mobile-full${disabled ? ' disabled' : ''}>"""
    }
    html << """</div>"""

    out << html.toString()
  }

  // ── Helpers ──────────────────────────────────────────────────────────────

  private static boolean truthy(def v) {
    if (v == null) return false
    if (v instanceof Boolean) return v
    return v.toString().toLowerCase() in ['true', 'yes', 'on', '1', 'required', 'disabled', 'readonly', 'autofocus']
  }

  private static String normalizeCc(def raw) {
    return '+' + (raw ?: '').toString().replaceAll('[^0-9]', '')
  }

  private static List toList(def v) {
    if (v == null) return []
    if (v instanceof Collection) return v as List
    if (v.getClass().isArray()) return v as List
    return []
  }

  /** Button label: "+91 India", or just "+91" when the name is hidden. */
  protected String buttonLabel(String dialCode, String cName, boolean showName, String fallback) {
    String lbl = showName ? "${dialCode ?: ''} ${cName ?: ''}".trim() : (dialCode ?: '').trim()
    lbl ?: fallback
  }

  /** Sheet CSS/JS (from the base) + the hooks that tie it to the number field. */
  protected void emitPickerAssetsOnce() {
    out << flagIconsCdnLink(pageScope)
    if (pageScope[PICKER_ASSETS_EMITTED]) return
    pageScope[PICKER_ASSETS_EMITTED] = true

    out << "<style id=\"gmcs-styles\">"
    out << commonCss(PICKER_PREFIX)
    out << "</style>"
    out << "<script id=\"gmcs-script\">"
    out << commonJs(PICKER_PREFIX, pickerJsHooks())
    out << "</script>"
  }

  /**
   * selectRow() hooks: write the three hidden fields, refresh button +
   * tooltip, then hand over to the mobile script, which re-validates the
   * number against the newly chosen country.
   */
  protected Map pickerJsHooks() {
    [
      hiddenFields: '''
        var elIsd=wrap.querySelector('.gmcs-hidden-isd'), elCn=wrap.querySelector('.gmcs-hidden-country'), elFl=wrap.querySelector('.gmcs-hidden-flag');
        if(elIsd) elIsd.value=dc; if(elCn) elCn.value=cn; if(elFl) elFl.value=fl;
''',
      uiUpdate    : '''
        setFlagContent(btnFlag, fl);
        var showName = wrap.getAttribute('data-show-name')==='1';
        if(btnLabel){
          var lbl = showName ? ((dc?dc+' ':'')+(cn||'')).trim() : (dc||'').trim();
          btnLabel.textContent = lbl || (wrap.getAttribute('data-button-text')||'Code');
        }
        setTooltipContent(tooltip, fl, (dc||'') + (cn ? ' \\u2014 ' + cn : ''));
        if(window.glassMobileOnCountryChange) window.glassMobileOnCountryChange(wrap, dc, cn, fl);
''',
      eventName   : 'gmcs:change',
      eventDetail : '{dialCode:dc,country:cn,flag:fl}'
    ]
  }

  void emitMobileInputStylesOnce() {
    if (pageScope[MOBILE_INPUT_STYLES_EMITTED]) return
    pageScope[MOBILE_INPUT_STYLES_EMITTED] = true

    out << '''
<style>
/* ═══════════════════════════════════════════════════════════════════════════
   Mobile input — country prefix  (mobile-first: this IS the touch layout)
   Hidden while the floating label sits in the field (blur + empty);
   fades in when the label rises (focus / has-value).
   The container spans the full shell height; the vertical padding lines the
   visible code up with the input text (20px top / 8px bottom, same as input).
   ═══════════════════════════════════════════════════════════════════════════ */
.input-glass.glass-mobile input {
    /* Fallback — the taglib sets an exact inline padding-left per prefix width */
    padding-left        : 4.5rem;
    letter-spacing      : 0.04em;
    font-variant-numeric: tabular-nums;
}

.input-glass .glass-mobile-cc {
    position                    : absolute;
    left                        : 0;
    top                         : 0;
    bottom                      : 0;
    display                     : -webkit-box;
    display                     : -ms-flexbox;
    display                     : flex;
    -webkit-box-align           : center;
        -ms-flex-align          : center;
            align-items         : center;
    -webkit-box-sizing          : border-box;
            box-sizing          : border-box;
    padding                     : 20px 10px 8px 16px;
    color                       : #ffffff;
    font                        : inherit;
    font-size                   : 1rem;
    white-space                 : nowrap;
    opacity                     : 0;
    pointer-events              : none;
    z-index                     : 2;
    -webkit-transition          : opacity 0.18s ease;
            transition          : opacity 0.18s ease;
    touch-action                : manipulation;
    -webkit-tap-highlight-color : transparent;
}

/* Divider between code and number */
.input-glass .glass-mobile-cc::after {
    content    : "";
    position   : absolute;
    right      : 0;
    top        : 14px;
    bottom     : 10px;
    width      : 1px;
    background : rgba(255, 255, 255, 0.30);
}

.input-glass.glass-mobile:focus-within .glass-mobile-cc,
.input-glass.glass-mobile.has-value .glass-mobile-cc {
    opacity        : 1;
    pointer-events : auto;
}

.glass-mobile-caret {
    margin-left    : 6px;
    font-size      : 0.65rem;
    color          : rgba(255, 255, 255, 0.80);
    pointer-events : none;
}

/* Native <select> stretched invisibly over the whole prefix: one tap opens
   the OS picker (iOS wheel / Android dialog / desktop list). 1rem font keeps
   iOS from zooming. Keyboard focus is shown on the visible code instead.    */
.glass-mobile-cc select {
    position                    : absolute;
    left                        : 0;
    top                         : 0;
    width                       : 100%;
    height                      : 100%;
    margin                      : 0;
    padding                     : 0;
    border                      : 0 !important;
    background                  : transparent !important;
    -webkit-box-shadow          : none !important;
            box-shadow          : none !important;
    outline                     : 0 !important;
    opacity                     : 0;
    font-size                   : 1rem;
    cursor                      : pointer;
    z-index                     : 1;
    -webkit-appearance          : none;
       -moz-appearance          : none;
            appearance          : none;
    touch-action                : manipulation;
    -webkit-tap-highlight-color : transparent;
}

.glass-mobile-cc:focus-within .glass-mobile-cc-text {
    text-decoration     : underline;
    text-underline-offset: 3px;
}

/* Dropdown list is drawn by the OS on a light surface — keep text readable */
.glass-mobile-cc select option {
    color      : #1a202c;
    background : #ffffff;
}

/* ═══════════════════════════════════════════════════════════════════════════
   Bottom-sheet country picker as the prefix (gmcs-*)
   The sheet CSS itself comes from GlassBottomSheetBaseTagLib.commonCss();
   these rules only turn its glass button into a borderless in-field prefix
   and keep the whole prefix a full-height tap target.
   ═══════════════════════════════════════════════════════════════════════════ */
.glass-mobile-picker {
    display   : block;
    position  : relative;
    width     : 100%;
    max-width : none;
    margin    : 0;
}

/* Keep the prefix visible while the sheet (inside the wrap) holds focus */
.glass-mobile-picker:focus-within .input-glass.glass-mobile .glass-mobile-cc {
    opacity        : 1;
    pointer-events : auto;
}

.input-glass.has-picker .glass-mobile-cc { padding: 0; }

.input-glass .glass-mobile-cc .gmcs-btn {
    display                     : -webkit-inline-box;
    display                     : -ms-inline-flexbox;
    display                     : inline-flex;
    -webkit-box-align           : center;
        -ms-flex-align          : center;
            align-items         : center;
    gap                         : 6px;
    height                      : 100%;
    min-height                  : 0 !important;
    width                       : auto !important;
    max-width                   : none !important;
    margin                      : 0 !important;
    padding                     : 20px 10px 8px 16px !important;
    -webkit-box-sizing          : border-box;
            box-sizing          : border-box;
    border                      : 0 !important;
    border-radius               : 0 !important;
    background                  : transparent !important;
    -webkit-box-shadow          : none !important;
            box-shadow          : none !important;
    -webkit-backdrop-filter     : none !important;
            backdrop-filter     : none !important;
    color                       : #ffffff;
    font                        : inherit;
    font-size                   : 1rem;
    cursor                      : pointer;
    touch-action                : manipulation;
    -webkit-tap-highlight-color : transparent;
}
.input-glass .glass-mobile-cc .gmcs-btn-label {
    font-size     : 1rem;
    font-weight   : 600;
    color         : #ffffff;
    white-space   : nowrap;
    max-width     : 12rem;
    overflow      : hidden;
    text-overflow : ellipsis;
}
.input-glass .glass-mobile-cc .gmcs-btn-chevron {
    display     : -webkit-inline-box;
    display     : -ms-inline-flexbox;
    display     : inline-flex;
    margin-left : 2px;
}
.input-glass .glass-mobile-cc .gmcs-btn-chevron svg { width: 14px; height: 14px; }
.input-glass.is-disabled .gmcs-btn { cursor: not-allowed; }

/* Narrow phones (≤360px): tighten tracking so a full number never wraps */
@media (max-width: 360px) {
    .input-glass.glass-mobile input { letter-spacing: 0.02em; }
}

@media (prefers-reduced-motion: reduce) {
    .input-glass .glass-mobile-cc { -webkit-transition: none; transition: none; }
}

@media (forced-colors: active) {
    .input-glass .glass-mobile-cc { forced-color-adjust: none; }
}

/* ═══════════════════════════════════════════════════════════════════════════
   Validation state
   ═══════════════════════════════════════════════════════════════════════════ */
/* No icon until there is a verdict */
.input-glass .validation-icon:not(.valid):not(.invalid) { visibility: hidden; }

/* Red underline when invalid and not being edited (focus keeps the white one) */
.input-glass.glass-mobile.is-invalid:not(:focus-within) {
    border-bottom-color: rgba(245, 101, 101, 0.95) !important;
}

/* Error replaces helper text rather than stacking under it */
.glass-mobile-group.has-error .glass-helper-text { display: none; }

.input-glass.glass-mobile.is-disabled .glass-mobile-cc { opacity: 0.60; }
</style>
<script>
(function () {
    if (window.glassMobileInitialized) return;
    window.glassMobileInitialized = true;

    var CHECK = String.fromCharCode(10003);
    var CROSS = String.fromCharCode(10005);

    /* Per-country national-number rules. Unlisted codes use DEFAULT_RULE.   */
    var RULES = {
        "+91":  { min: 10, max: 10, re: /^[6-9][0-9]{9}$/, reMsg: "Mobile number must start with 6, 7, 8 or 9" },
        "+1":   { min: 10, max: 10 },
        "+44":  { min: 10, max: 10 },
        "+61":  { min: 9,  max: 9  },
        "+65":  { min: 8,  max: 8  },
        "+60":  { min: 9,  max: 10 },
        "+92":  { min: 10, max: 10 },
        "+94":  { min: 9,  max: 9  },
        "+880": { min: 10, max: 10 },
        "+977": { min: 10, max: 10 },
        "+965": { min: 8,  max: 8  },
        "+966": { min: 9,  max: 9  },
        "+968": { min: 8,  max: 8  },
        "+971": { min: 9,  max: 9  },
        "+973": { min: 8,  max: 8  },
        "+974": { min: 8,  max: 8  }
    };
    var DEFAULT_RULE = { min: 6, max: 15 };

    function closest(el, sel) {
        var fn = el.matches || el.webkitMatchesSelector || el.msMatchesSelector;
        var n = el;
        while (n && n.nodeType === 1) {
            if (fn.call(n, sel)) return n;
            n = n.parentElement;
        }
        return null;
    }

    function setClass(el, cls, on) {
        if (!el) return;
        if (on) el.classList.add(cls); else el.classList.remove(cls);
    }

    function isMobile(el) {
        return !!el && el.tagName === "INPUT" && el.hasAttribute("data-glass-mobile-input");
    }

    function digitsOnly(s) { return String(s).replace(/[^0-9]/g, ""); }

    function normCc(s) { return "+" + digitsOnly(s || ""); }

    /* Current calling code: native select → picker hidden field → data-cc */
    function getCc(shell) {
        var sel = shell.querySelector("select");
        if (sel) return normCc(sel.value);
        var wrap = closest(shell, ".gmcs-wrap");
        if (wrap) {
            var hid = wrap.querySelector(".gmcs-hidden-isd");
            if (hid && digitsOnly(hid.value)) return normCc(hid.value);
        }
        return normCc(shell.getAttribute("data-cc"));
    }

    function ruleFor(cc) { return RULES[cc] || DEFAULT_RULE; }

    /* Visible code text mirrors the (invisible) overlay select */
    function syncCc(shell) {
        var sel = shell.querySelector("select");
        var txt = shell.querySelector(".glass-mobile-cc-text");
        if (sel && txt) txt.textContent = sel.value;
    }

    /* Picker mode: size the input's left padding to the measured prefix */
    function fitPrefix(shell) {
        if (!shell || !shell.classList.contains("has-picker")) return;
        var cc = shell.querySelector(".glass-mobile-cc");
        var input = shell.querySelector("input[data-glass-mobile-input]");
        if (!cc || !input) return;
        var w = cc.offsetWidth;
        if (w > 0) input.style.paddingLeft = (w + 12) + "px";
    }

    function fitAll() {
        var list = document.querySelectorAll(".input-glass.has-picker");
        for (var i = 0; i < list.length; i++) fitPrefix(list[i]);
    }

    /* "+91 98765 43210" / "0091…" / "09876…" → national digits, capped at max */
    function normalize(raw, cc, rule) {
        var d = digitsOnly(raw);
        var ccDigits = cc.replace("+", "");
        if (d.length > rule.max && d.charAt(0) === "0") d = d.replace(/^0+/, "");
        if (d.length > rule.max
            && d.length >= rule.min + ccDigits.length
            && d.indexOf(ccDigits) === 0) {
            d = d.slice(ccDigits.length);
        }
        return d.slice(0, rule.max);
    }

    /* mode: "silent" (no error UI), "live" (typing), "final" (blur / submit) */
    function validate(input, mode) {
        var shell = closest(input, ".input-glass");
        var group = closest(input, ".glass-form-group");
        var cc    = getCc(shell);
        var rule  = ruleFor(cc);
        var v     = input.value;
        var msg   = "";

        if (!v) {
            if (input.required) msg = "Enter your mobile number";
        } else if (v.length < rule.min) {
            msg = rule.min === rule.max
                ? "Enter a valid " + rule.min + "-digit mobile number"
                : "Enter " + rule.min + " to " + rule.max + " digits";
        } else if (rule.re && !rule.re.test(v)) {
            msg = rule.reMsg || "Enter a valid mobile number";
        }

        input.setCustomValidity(msg);

        var touched = shell.classList.contains("is-touched");

        /* Icon: green tick when valid; red cross after blur / submit, or as
           soon as the number is full-length but wrong. Nothing while typing. */
        var icon = shell.querySelector(".validation-icon");
        if (icon) {
            var state = "";
            if (v && !msg) {
                /* A server-side error is still showing — do not contradict it with a tick */
                state = (mode === "silent" && shell.classList.contains("is-invalid")) ? "" : "valid";
            }
            else if (v && msg && (mode === "final" || touched || v.length >= rule.max)) state = "invalid";
            icon.className = "validation-icon" + (state ? " " + state : "");
            icon.textContent = state === "valid" ? CHECK : (state === "invalid" ? CROSS : "");
        }

        if (mode !== "silent") {
            var showErr = !!msg && (mode === "final" || (touched && !!v));
            var errEl = group ? group.querySelector(".glass-error") : null;
            if (errEl) {
                if (showErr) errEl.textContent = msg;
                setClass(errEl, "show", showErr);
            }
            setClass(shell, "is-invalid", showErr);
            setClass(group, "has-error", showErr);
            input.setAttribute("aria-invalid", showErr ? "true" : "false");
        }

        /* Hidden E.164 field — only populated for a valid number */
        var full = group ? group.querySelector("input[data-glass-mobile-full]") : null;
        if (full) full.value = (v && !msg) ? cc + v : "";

        return !msg;
    }

    function onInput(input) {
        var shell = closest(input, ".input-glass");
        var cc    = getCc(shell);
        var raw   = input.value;
        var clean = normalize(raw, cc, ruleFor(cc));

        if (clean !== raw) {
            var pos = null;
            try { pos = input.selectionStart; } catch (e) { pos = null; }
            var before = (pos === null) ? clean.length : digitsOnly(raw.slice(0, pos)).length;
            input.value = clean;
            try {
                var p = Math.min(before, clean.length);
                input.setSelectionRange(p, p);
            } catch (e2) { /* selection not supported — ignore */ }
        }

        if (window.toggleClearButton) window.toggleClearButton(input);
        validate(input, "live");
    }

    function fire(el, name, detail) {
        var ev;
        try {
            ev = new CustomEvent(name, { bubbles: true, detail: detail });
        } catch (e) {
            ev = document.createEvent("CustomEvent");
            ev.initCustomEvent(name, true, false, detail);
        }
        el.dispatchEvent(ev);
    }

    /* Called by the bottom-sheet's selectRow() hook (picker mode) */
    window.glassMobileOnCountryChange = function (wrap, dc, cn, fl) {
        var shell = wrap.querySelector(".input-glass.glass-mobile");
        var input = shell ? shell.querySelector("input[data-glass-mobile-input]") : null;
        if (!input) return;
        shell.setAttribute("data-cc", normCc(dc));
        fitPrefix(shell);
        onInput(input);
        fire(input, "glassmobile:countrychange", { dialCode: dc, country: cn, flag: fl, input: input });
        /* After the sheet has closed, hand the cursor back to the number */
        setTimeout(function () { try { input.focus(); } catch (e) { /* ignore */ } }, 0);
    };

    document.addEventListener("input", function (e) {
        if (isMobile(e.target)) onInput(e.target);
    }, true);

    /* focusout bubbles; blur does not */
    document.addEventListener("focusout", function (e) {
        var t = e.target;
        if (!isMobile(t)) return;
        var shell = closest(t, ".input-glass");
        /* Moving to this field's own country select / picker button is not "leaving" */
        if (e.relatedTarget && closest(e.relatedTarget, ".input-glass") === shell) return;
        /* ...nor is moving into this field's bottom sheet (search box, rows) */
        var wrap = closest(t, ".gmcs-wrap");
        if (wrap && e.relatedTarget && closest(e.relatedTarget, ".gmcs-wrap") === wrap) return;
        shell.classList.add("is-touched");
        validate(t, "final");
    }, true);

    document.addEventListener("change", function (e) {
        var t = e.target;
        if (!t || t.tagName !== "SELECT") return;
        var shell = closest(t, ".glass-mobile");
        if (!shell) return;
        var input = shell.querySelector("input[data-glass-mobile-input]");
        if (!input) return;
        syncCc(shell);
        onInput(input);
        input.focus();
    }, true);

    /* Native submit / reportValidity() — swap the browser bubble for ours */
    document.addEventListener("invalid", function (e) {
        var t = e.target;
        if (!isMobile(t)) return;
        var shell = closest(t, ".input-glass");
        shell.classList.add("is-touched");
        validate(t, "final");
        e.preventDefault();
        var form = t.form;
        if (form && !form.glassMobileFocusPending && form.querySelector(":invalid") === t) {
            form.glassMobileFocusPending = true;
            t.focus();
            setTimeout(function () { form.glassMobileFocusPending = false; }, 0);
        }
    }, true);

    /* Keep the hidden E.164 field fresh right before any submit (autofill etc.) */
    document.addEventListener("submit", function (e) {
        var form = e.target;
        if (!form || !form.querySelectorAll) return;
        var list = form.querySelectorAll("input[data-glass-mobile-input]");
        for (var i = 0; i < list.length; i++) validate(list[i], "silent");
    }, true);

    /* Chrome autofill: no input event — reuse the base taglib animationstart hook */
    document.addEventListener("animationstart", function (e) {
        if (e.animationName === "glassAutofillStart" && isMobile(e.target)) onInput(e.target);
    }, true);

    /* For fetch()/ajax submit flows: validates every mobile field under root */
    window.glassMobileValidateAll = function (root) {
        var scope = root || document;
        var list = scope.querySelectorAll("input[data-glass-mobile-input]");
        var ok = true, firstBad = null;
        for (var i = 0; i < list.length; i++) {
            if (list[i].disabled) continue;
            closest(list[i], ".input-glass").classList.add("is-touched");
            if (!validate(list[i], "final")) { ok = false; if (!firstBad) firstBad = list[i]; }
        }
        if (firstBad) firstBad.focus();
        return ok;
    };

    function init() {
        var list = document.querySelectorAll("input[data-glass-mobile-input]");
        for (var i = 0; i < list.length; i++) {
            var input = list[i];
            var shell = closest(input, ".input-glass");
            syncCc(shell);
            fitPrefix(shell);
            var cc    = getCc(shell);
            var clean = normalize(input.value, cc, ruleFor(cc));
            if (clean !== input.value) input.value = clean;
            if (window.toggleClearButton) window.toggleClearButton(input);
            validate(input, "silent");
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
    /* Flag-icons CSS / web fonts arrive later and change the prefix width */
    window.addEventListener("load", fitAll);
    if (document.fonts && document.fonts.ready) { document.fonts.ready.then(fitAll); }
    /* Back/forward cache restores form values without re-running scripts */
    window.addEventListener("pageshow", init);
})();
</script>
'''
  }
}
