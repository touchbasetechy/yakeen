package com.murad.app.matrimony.slideinsheet


import com.murad.app.matrimony.textinput.AlphaTextFieldTagLib

/**
 * GlassCountryNameBottomSheetTagLib.groovy
 *
 * Glassmorphic "Select Country" BUTTON that opens a bottom-up
 * slide-in sheet listing flag + country name only (no ISD/dial code).
 *
 * Namespace : sheet
 * Tag       : countryNamePicker
 *
 * Usage:
 *   <sheet:countryNamePicker
 *       id="birthCountry"
 *       countries="${countries}"
 *       countryName="country"
 *       flagName="flag"
 *       selectedCountry="${contactDetail?.country}"
 *       selectedFlag="${contactDetail?.flag}"
 *       buttonText="Select Country"
 *       panelTitle="Select Country"
 *       disabled="false"
 *       cssClass=""
 *       style="max-width:320px"
 *   />
 *
 * Country objects are read through the base class's tolerant
 * accessors, so .countryName/.name/.country and
 * .countryFlag/.flag/.emoji all work, on Maps or on domain objects.
 *
 * NOTE on the `countryName` ATTRIBUTE vs the `countryName` PROPERTY:
 * the tag *attribute* (countryName="country") is the form field name
 * the picked country is submitted under; the *property* read off each
 * item in `countries` is a different, unrelated thing of the same name.
 *
 * Flag rendering (flagCode/flagMarkup on the server,
 * flagCode/flagMarkup/setFlagContent/setTooltipContent on the client),
 * the flag-icons CDN <link>, the shared chrome (border/box-shadow, CSS
 * variables, flag sizing) and all JS interaction behaviour now live in
 * GlassBottomSheetBaseTagLib and are shared with
 * GlassCountryBottomSheetTagLib. What's still specific here: the
 * button font-size, the hidden fields written on selection, and the
 * absence of an ISD-code column.
 *
 * Search field
 * ────────────
 *  The search box is delegated to <glass:alphaTextField/>
 *  (AlphaTextFieldTagLib), which restricts typed input to letters and
 *  single spaces — a closer fit for country-name search than an
 *  alphanumeric field, since country names carry no digits. This is a
 *  constraint on the *query* only: rendered country names (including
 *  any punctuation) are unaffected and remain selectable.
 */
class GlassCountryNameBottomSheetTagLib extends GlassBottomSheetBaseTagLib {

  static namespace = "sheet"

  private static final String PREFIX = "gcnp"

  private AlphaTextFieldTagLib getAlphaTextFieldTagLib() {
    grailsApplication.mainContext.getBean(AlphaTextFieldTagLib)
  }

  /** CSS that's specific to this picker (just the button font-size). */
  private String extraCss() {
    '''
.gcnp-btn{font-size:1rem;}
'''
  }

  /** selectRow() hooks specific to this picker: no ISD field. */
  private Map jsHooks() {
    [
      hiddenFields: '''
        var elCn=wrap.querySelector('.gcnp-hidden-country'), elFl=wrap.querySelector('.gcnp-hidden-flag');
        if(elCn) elCn.value=cn; if(elFl) elFl.value=fl;
''',
      uiUpdate    : '''
        setFlagContent(btnFlag, fl);
        if(btnLabel) btnLabel.textContent = cn || (wrap.getAttribute('data-button-text')||'Select Country');
        setTooltipContent(tooltip, fl, cn);
''',
      eventName   : 'gcnp:change',
      eventDetail : '{country:cn,flag:fl}'
    ]
  }

  def countryNamePicker = { attrs, body ->

    if (attrs.countries == null) {
      throwTagError("Tag [sheet:countryNamePicker] is missing required attribute [countries]")
    }

    String id = attrs.id?.toString() ?: "gcnp_${System.nanoTime()}"
    def countries = attrs.countries ?: []
    String countryFieldName = attrs.countryName?.toString() ?: "country"
    String flagFieldName = attrs.flagName?.toString() ?: "flag"
    String selectedCountry = attrs.selectedCountry?.toString() ?: ""
    String selectedFlag = attrs.selectedFlag?.toString() ?: ""
    String buttonText = attrs.buttonText?.toString() ?: "Select Country"
    String panelTitle = attrs.panelTitle?.toString() ?: "Select Country"
    String cssClass = attrs.cssClass?.toString() ?: ""
    String wrapStyle = attrs.style?.toString() ?: ""

    boolean disabled = parseDisabled(attrs.disabled)

    def match = selectedCountry ? countries.find { countryDisplayName(it) == selectedCountry } : null
    String initName = match ? countryDisplayName(match) : selectedCountry
    String initFlag = match ? countryFlagValue(match) : selectedFlag

    String initFullTipHtml = "No country selected yet"
    if (initName) {
      String mk = flagMarkup(initFlag)
      initFullTipHtml = (mk ? "${mk} " : "") + h(initName)
    }

    String sheetId = "${id}-sheet"
    String listId = "${id}-list"
    String tooltipId = "${id}-tooltip"
    String titleId = "${id}-title"
    String searchId = "${id}-search"

    // flag-icons stylesheet: written once per page, shared with the
    // sibling ISD-code picker.
    out << flagIconsCdnLink(pageScope)

    if (!pageScope["__gcnpCssWritten__"]) {
      pageScope["__gcnpCssWritten__"] = true
      out << "<style id=\"gcnp-styles\">"
      out << commonCss(PREFIX)
      out << extraCss()
      out << "</style>"
      out << "<script id=\"gcnp-script\">"
      out << commonJs(PREFIX, jsHooks())
      out << "</script>"
    }

    String styleAttr = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ""
    String extraClass = cssClass ? " ${h(cssClass)}" : ""

    out << "<div id=\"${h(id)}\" class=\"gcnp-wrap${extraClass}\" data-button-text=\"${h(buttonText)}\"${styleAttr}>"

    out << "<button type=\"button\" class=\"gcnp-btn\" aria-haspopup=\"dialog\" aria-expanded=\"false\" aria-controls=\"${h(sheetId)}\""
    if (disabled) out << " disabled"
    out << ">"
    out << "<span class=\"gcnp-btn-flag\" aria-hidden=\"true\">${flagMarkup(initFlag)}</span>"
    out << "<span class=\"gcnp-btn-label\">${h(initName ?: buttonText)}</span>"
    out << "<span class=\"gcnp-btn-chevron\" aria-hidden=\"true\"><svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.3\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"6 9 12 15 18 9\"/></svg></span>"
    out << "</button>"

    out << "<div id=\"${h(tooltipId)}\" class=\"gcnp-tooltip\" role=\"tooltip\">${initFullTipHtml}</div>"

    out << "<div class=\"gcnp-overlay\" style=\"opacity:0;visibility:hidden;\"></div>"

    out << "<div id=\"${h(sheetId)}\" class=\"gcnp-panel\" role=\"dialog\" aria-modal=\"true\" aria-labelledby=\"${h(titleId)}\" aria-hidden=\"true\" style=\"-webkit-transform:translateY(100%);transform:translateY(100%);visibility:hidden;\">"
    out << "<div class=\"gcnp-panel-handle\" aria-hidden=\"true\"></div>"
    out << "<div class=\"gcnp-panel-head\">"
    out << "<span id=\"${h(titleId)}\" class=\"gcnp-panel-title\">${h(panelTitle)}</span>"
    out << "</div>"

    out << "<div class=\"gcnp-search-wrap\">"
    out << alphaTextFieldTagLib.alphaTextField(
      name: searchId,
      id: searchId,
      value: '',
      placeholder: 'Search country\u2026',
      wrapper: false,
      floatingLabel: false,
      autocomplete: 'off',
      autocapitalize: 'off',
      class: 'gcnp-search',
      tooltip: 'Clear search',
      tooltipPlacement: 'bottom'
    )
    out << "</div>"

    out << "<div id=\"${h(listId)}\" class=\"gcnp-list\" role=\"listbox\" aria-label=\"${h(panelTitle)}\">"
    countries.each { country ->
      String cName = countryDisplayName(country)
      String flagValue = countryFlagValue(country)
      boolean isSel = (match != null) ? (country == match) : (selectedCountry && cName == selectedCountry)
      out << "<div class=\"gcnp-row${isSel ? ' gcnp-row-selected' : ''}\" role=\"option\" aria-selected=\"${isSel}\" tabindex=\"-1\" data-country=\"${h(cName)}\" data-flag=\"${h(flagValue)}\">"
      out << "<span class=\"gcnp-row-flag\" aria-hidden=\"true\">${flagMarkup(flagValue)}</span>"
      out << "<span class=\"gcnp-row-name\">${h(cName)}</span>"
      out << "<span class=\"gcnp-row-dot\" aria-hidden=\"true\"></span>"
      out << "</div>"
    }
    out << "<div class=\"gcnp-empty\" style=\"display:none\">No countries found</div>"
    out << "</div>" // list
    out << "</div>" // panel

    out << "<input type=\"hidden\" class=\"gcnp-hidden-country\" name=\"${h(countryFieldName)}\" value=\"${h(initName)}\">"
    out << "<input type=\"hidden\" class=\"gcnp-hidden-flag\" name=\"${h(flagFieldName)}\" value=\"${h(initFlag)}\">"

    out << "</div>" // wrap
  }
}
