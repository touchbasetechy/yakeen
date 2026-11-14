package com.murad.app.matrimony.slideinsheet


import com.murad.app.matrimony.textinput.AlphaNumericPlusTextFieldTagLib

/**
 * GlassCountryBottomSheetTagLib.groovy
 *
 * Glassmorphic "Select Country Code" BUTTON that opens a bottom-up
 * slide-in sheet listing flag + ISD code + country name.
 *
 * Namespace : sheet
 * Tag       : countryPicker
 *
 * Usage:
 *   <sheet:countryPicker
 *       id="phone1"
 *       countries="${countries}"
 *       isdCodeName="isdCode"
 *       countryName="country"
 *       flagName="flag"
 *       selectedIsdCode="${contactDetail?.isdCode}"
 *       selectedCountry="${contactDetail?.country}"
 *       selectedFlag="${contactDetail?.flag}"
 *       buttonText="Select Country Code"
 *       panelTitle="Select Country Code"
 *       showCountryName="true"
 *       disabled="false"
 *       cssClass=""
 *       style="max-width:320px"
 *   />
 *
 * Country objects
 * ───────────────
 *  Each item in `countries` is read through the base class's tolerant
 *  accessors, so any of these naming variants work (and may be mixed):
 *    dial code : .dial_code | .dialCode | .isdCode
 *    name      : .countryName | .name | .country
 *    flag      : .countryFlag | .flag | .emoji
 *  Both Maps (parsed JSON) and domain/POJO objects are accepted.
 *
 *  Flags are rendered by the shared flagMarkup(): an ISO 3166-1
 *  alpha-2 code ("in") or a legacy FontAwesome-style value
 *  ("fas fa-flag-in") becomes `<span class="fi fi-in">` via the
 *  flag-icons stylesheet; an emoji flag is printed as-is. The
 *  stylesheet <link> is emitted once per page by the base class and
 *  is shared with GlassCountryNameBottomSheetTagLib.
 *
 * What this picker renders
 * ────────────────────────
 *  Button : flag + ISD code + country name (name suppressed with
 *           showCountryName="false", leaving flag + code only).
 *  Row    : flag + ISD code column + country name + selected dot.
 *  Tooltip: flag + "+91 — India".
 *  Submits three hidden fields: ISD code, country name, flag.
 *
 * Behaviour (open/close, focus trap, keyboard nav, touch, search
 * filtering, 8s auto-hiding tooltip), the CSS variables, the button
 * border/box-shadow and the flag sizing all come from
 * GlassBottomSheetBaseTagLib.commonCss()/commonJs(), shared with
 * GlassCountryNameBottomSheetTagLib. What remains here: the ISD-code
 * column CSS, the hidden-field writes, and the button label format.
 *
 * Search field
 * ────────────
 *  The search box inside the sheet is delegated to
 *  <glass:alphaNumericPlusTextField/> (AlphaNumericPlusTextFieldTagLib),
 *  so a user can type either a country name or a dial code. This
 *  taglib still fully owns list-filtering, row highlighting and
 *  keyboard navigation — it just attaches its own `input`/`keydown`
 *  listeners to whatever <input class="gcbs-search"> ends up in the DOM.
 *  The shared filter matches on name, space-insensitive name, and dial
 *  code (with or without the leading "+").
 */
class GlassCountryBottomSheetTagLib extends GlassBottomSheetBaseTagLib {

  static namespace = "sheet"

  private static final String PREFIX = "gcbs"

  private AlphaNumericPlusTextFieldTagLib getAlphaNumericPlusTextFieldTagLib() {
    grailsApplication.mainContext.getBean(AlphaNumericPlusTextFieldTagLib)
  }

  /** Button label: "+91 India", or just "+91" when showCountryName="false". */
  private String buttonLabel(String dialCode, String cName, boolean showName, String fallback) {
    String label = showName ? "${dialCode ?: ''} ${cName ?: ''}".trim() : (dialCode ?: '').trim()
    label ?: fallback
  }

  /** CSS specific to this picker: the ISD-code column and button metrics. */
  private String extraCss() {
    '''
.gcbs-btn{font-size:0.95rem;min-height:44px;}
.gcbs-btn-code{font-size:0.95rem;font-weight:700;color:#ffffff;-webkit-flex-shrink:0;flex-shrink:0;}
.gcbs-row-code{font-size:0.85rem;color:#ffffff;font-weight:600;opacity:0.85;min-width:56px;-webkit-flex-shrink:0;flex-shrink:0;}
.gcbs-tooltip-code{font-weight:700;}
'''
  }

  /**
   * selectRow() hooks specific to this picker: writes the ISD hidden
   * field and renders flag-icon markup into the button and tooltip.
   * The "show country name in the label" choice is read off the wrap
   * element at runtime (data-show-name), not baked into the shared
   * script, since the script is emitted only once per page while the
   * attribute can differ per tag instance.
   */
  private Map jsHooks() {
    [
      hiddenFields: '''
        var elIsd=wrap.querySelector('.gcbs-hidden-isd'), elCn=wrap.querySelector('.gcbs-hidden-country'), elFl=wrap.querySelector('.gcbs-hidden-flag');
        if(elIsd) elIsd.value=dc; if(elCn) elCn.value=cn; if(elFl) elFl.value=fl;
''',
      uiUpdate    : '''
        setFlagContent(btnFlag, fl);
        var showName = wrap.getAttribute('data-show-name')!=='0';
        if(btnLabel){
          var lbl = showName ? ((dc?dc+' ':'')+(cn||'')).trim() : (dc||'').trim();
          btnLabel.textContent = lbl || (wrap.getAttribute('data-button-text')||'Select Country Code');
        }
        setTooltipContent(tooltip, fl, (dc||'') + (cn ? ' \\u2014 ' + cn : ''));
''',
      eventName   : 'gcbs:change',
      eventDetail : '{dialCode:dc,country:cn,flag:fl}'
    ]
  }

  def countryPicker = { attrs, body ->

    if (attrs.countries == null) {
      throwTagError("Tag [sheet:countryPicker] is missing required attribute [countries]")
    }

    String id = attrs.id?.toString() ?: "gcbs_${System.nanoTime()}"
    def countries = attrs.countries ?: []
    String isdCodeName = attrs.isdCodeName?.toString() ?: "isdCode"
    String countryFieldName = attrs.countryName?.toString() ?: "country"
    String flagFieldName = attrs.flagName?.toString() ?: "flag"
    String selectedIsd = attrs.selectedIsdCode?.toString() ?: ""
    String selectedCountry = attrs.selectedCountry?.toString() ?: ""
    String selectedFlag = attrs.selectedFlag?.toString() ?: ""
    String buttonText = attrs.buttonText?.toString() ?: "Select Country Code"
    String panelTitle = attrs.panelTitle?.toString() ?: "Select Country Code"
    String cssClass = attrs.cssClass?.toString() ?: ""
    String wrapStyle = attrs.style?.toString() ?: ""

    boolean disabled = parseDisabled(attrs.disabled)
    boolean showCountryName = (attrs.showCountryName == null) ? true : parseDisabled(attrs.showCountryName)

    /*
     * Resolve the initially selected row. Match on ISD code first
     * (that's what this picker's value is), then fall back to the
     * country name so a record that stored only the name still
     * pre-selects correctly. If neither matches, fall back to the
     * raw selected* attributes so nothing silently disappears.
     */
    def match = null
    if (selectedIsd) {
      match = countries.find { countryDialCode(it) == selectedIsd }
    }
    if (!match && selectedCountry) {
      match = countries.find { countryDisplayName(it) == selectedCountry }
    }

    String initIsd = match ? countryDialCode(match) : selectedIsd
    String initName = match ? countryDisplayName(match) : selectedCountry
    String initFlag = match ? countryFlagValue(match) : selectedFlag

    String initFullTipHtml = "No country selected yet"
    if (initIsd || initName) {
      String tipText = "${initIsd ?: ''}${initName ? ' \u2014 ' + initName : ''}".trim()
      String mk = flagMarkup(initFlag)
      initFullTipHtml = (mk ? "${mk} " : "") + h(tipText)
    }

    String initLabel = buttonLabel(initIsd, initName, showCountryName, buttonText)

    String sheetId = "${id}-sheet"
    String listId = "${id}-list"
    String tooltipId = "${id}-tooltip"
    String titleId = "${id}-title"
    String searchId = "${id}-search"

    // flag-icons stylesheet: written once per page, shared with the
    // sibling name-only picker.
    out << flagIconsCdnLink(pageScope)

    if (!pageScope["__gcbsCssWritten__"]) {
      pageScope["__gcbsCssWritten__"] = true
      out << "<style id=\"gcbs-styles\">"
      out << commonCss(PREFIX)
      out << extraCss()
      out << "</style>"
      out << "<script id=\"gcbs-script\">"
      out << commonJs(PREFIX, jsHooks())
      out << "</script>"
    }

    String styleAttr = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ""
    String extraClass = cssClass ? " ${h(cssClass)}" : ""

    out << "<div id=\"${h(id)}\" class=\"gcbs-wrap${extraClass}\" data-show-name=\"${showCountryName ? '1' : '0'}\" data-button-text=\"${h(buttonText)}\"${styleAttr}>"

    out << "<button type=\"button\" class=\"gcbs-btn\" aria-haspopup=\"dialog\" aria-expanded=\"false\" aria-controls=\"${h(sheetId)}\""
    if (disabled) out << " disabled"
    out << ">"
    out << "<span class=\"gcbs-btn-flag\" aria-hidden=\"true\">${flagMarkup(initFlag)}</span>"
    out << "<span class=\"gcbs-btn-label\">${h(initLabel)}</span>"
    out << "<span class=\"gcbs-btn-chevron\" aria-hidden=\"true\"><svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.3\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"6 9 12 15 18 9\"/></svg></span>"
    out << "</button>"

    out << "<div id=\"${h(tooltipId)}\" class=\"gcbs-tooltip\" role=\"tooltip\">${initFullTipHtml}</div>"

    out << "<div class=\"gcbs-overlay\" style=\"opacity:0;visibility:hidden;\"></div>"

    out << "<div id=\"${h(sheetId)}\" class=\"gcbs-panel\" role=\"dialog\" aria-modal=\"true\" aria-labelledby=\"${h(titleId)}\" aria-hidden=\"true\" style=\"-webkit-transform:translateY(100%);transform:translateY(100%);visibility:hidden;\">"
    out << "<div class=\"gcbs-panel-handle\" aria-hidden=\"true\"></div>"
    out << "<div class=\"gcbs-panel-head\">"
    out << "<span id=\"${h(titleId)}\" class=\"gcbs-panel-title\">${h(panelTitle)}</span>"
    out << "</div>"

    out << "<div class=\"gcbs-search-wrap\">"
    out << alphaNumericPlusTextFieldTagLib.alphaNumericPlusTextField(
      name: searchId,
      id: searchId,
      value: '',
      placeholder: 'Search country or code\u2026',
      wrapper: false,
      floatingLabel: false,
      autocomplete: 'off',
      autocapitalize: 'off',
      class: 'gcbs-search',
      tooltip: 'Clear search',
      tooltipPlacement: 'bottom'
    )
    out << "</div>"

    out << "<div id=\"${h(listId)}\" class=\"gcbs-list\" role=\"listbox\" aria-label=\"${h(panelTitle)}\">"
    countries.each { country ->
      String dialCode = countryDialCode(country)
      String cName = countryDisplayName(country)
      String flagValue = countryFlagValue(country)
      boolean isSel = (match != null) ? (country == match)
        : (selectedIsd && dialCode == selectedIsd)
      out << "<div class=\"gcbs-row${isSel ? ' gcbs-row-selected' : ''}\" role=\"option\" aria-selected=\"${isSel}\" tabindex=\"-1\" data-dial-code=\"${h(dialCode)}\" data-country=\"${h(cName)}\" data-flag=\"${h(flagValue)}\">"
      out << "<span class=\"gcbs-row-flag\" aria-hidden=\"true\">${flagMarkup(flagValue)}</span>"
      out << "<span class=\"gcbs-row-code\">${h(dialCode)}</span>"
      out << "<span class=\"gcbs-row-name\">${h(cName)}</span>"
      out << "<span class=\"gcbs-row-dot\" aria-hidden=\"true\"></span>"
      out << "</div>"
    }
    out << "<div class=\"gcbs-empty\" style=\"display:none\">No countries found</div>"
    out << "</div>" // list
    out << "</div>" // panel

    out << "<input type=\"hidden\" class=\"gcbs-hidden-isd\" name=\"${h(isdCodeName)}\" value=\"${h(initIsd)}\">"
    out << "<input type=\"hidden\" class=\"gcbs-hidden-country\" name=\"${h(countryFieldName)}\" value=\"${h(initName)}\">"
    out << "<input type=\"hidden\" class=\"gcbs-hidden-flag\" name=\"${h(flagFieldName)}\" value=\"${h(initFlag)}\">"

    out << "</div>" // wrap
  }
}
