package com.murad.app.matrimony.slideinsheet

import com.murad.app.matrimony.textinput.AlphaNumericPlusTextFieldTagLib

/**
 * GlassCurrencyBottomSheetTagLib.groovy
 *
 * Glassmorphic "Select Currency" BUTTON that opens a bottom-up
 * slide-in sheet listing flag (or symbol badge) + currency code + name + symbol.
 *
 * Namespace : sheet
 * Tag       : currencyPicker
 *
 * Usage:
 *   <sheet:currencyPicker
 *       id="currency1"
 *       currencies="${currencies}"
 *       currencyCodeName="currencyCode"
 *       currencyName="currencyName"
 *       symbolName="currencySymbol"
 *       flagName="currencyFlag"
 *       selectedCode="${profile?.currencyCode}"
 *       selectedCurrency="${profile?.currencyName}"
 *       selectedSymbol="${profile?.currencySymbol}"
 *       selectedFlag="${profile?.currencyFlag}"
 *       buttonText="Select Currency"
 *       panelTitle="Select Currency"
 *       showCurrencyName="true"
 *       disabled="false"
 *       cssClass=""
 *       style="max-width:320px"
 *   />
 *
 * Currency objects (Maps from currencies.json, or domain/POJOs)
 * ─────────────────────────────────────────────────────────────
 *    code   : .code | .currencyCode | .cc | .iso | .isoCode
 *    name   : .name | .currencyName | .currency
 *    symbol : .symbol_native | .symbol | .currencySymbol
 *    flag   : .countryFlag | .flag | .emoji   (OPTIONAL)
 *  Flags use the base class flagMarkup() (flag-icons). When a currency
 *  has no flag, a symbol badge ("$", "₹", "€") is shown in its place.
 *
 * Reuses from GlassBottomSheetBaseTagLib (unchanged): commonCss(),
 * commonJs(), h(), parseDisabled(), prop(), flagMarkup(),
 * flagIconsCdnLink(). The shared JS reads data-dial-code / data-country,
 * so here those attributes carry the CURRENCY CODE and CURRENCY NAME.
 *
 * Search: delegated to <glass:alphaNumericPlusTextField/> like the
 * country pickers. Code and name filtering comes from the shared
 * filter; symbol matching is added by a small once-per-page script.
 *
 * Submits four hidden fields: code, name, symbol, flag.
 * Fires `gccp:change` on the wrap: detail {code, name, symbol, flag}.
 */
class GlassCurrencyBottomSheetTagLib extends GlassBottomSheetBaseTagLib {

  static namespace = "sheet"

  private static final String PREFIX = "gccp"

  private AlphaNumericPlusTextFieldTagLib getAlphaNumericPlusTextFieldTagLib() {
    grailsApplication.mainContext.getBean(AlphaNumericPlusTextFieldTagLib)
  }

  // ── tolerant accessors for currency objects ───────────────────────

  private String currencyCodeOf(c) {
    prop(c, 'code') ?: prop(c, 'currencyCode') ?: prop(c, 'cc') ?: prop(c, 'iso') ?: prop(c, 'isoCode') ?: ""
  }

  private String currencyNameOf(c) {
    prop(c, 'name') ?: prop(c, 'currencyName') ?: prop(c, 'currency') ?: ""
  }

  private String currencySymbolOf(c) {
    prop(c, 'symbol_native') ?: prop(c, 'symbol') ?: prop(c, 'currencySymbol') ?: ""
  }

  /** Flag markup, or a symbol badge when the currency has no flag. */
  private String flagOrSymbol(String flag, String symbol) {
    if (flag) return flagMarkup(flag)
    symbol ? "<span class=\"gccp-sym\">${h(symbol)}</span>" : ""
  }

  /** "USD US Dollar", or just "USD" when showCurrencyName="false". */
  private String buttonLabel(String code, String name, boolean showName, String fallback) {
    String label = showName ? "${code ?: ''} ${name ?: ''}".trim() : (code ?: '').trim()
    label ?: fallback
  }

  /** CSS specific to this picker. */
  private String extraCss() {
    '''
.gccp-btn{font-size:0.95rem;min-height:44px;}
.gccp-sym{display:-webkit-inline-box;display:-webkit-inline-flex;display:inline-flex;-webkit-box-align:center;-webkit-align-items:center;align-items:center;-webkit-box-pack:center;-webkit-justify-content:center;justify-content:center;min-width:1.7em;height:1.25em;padding:0 0.35em;border-radius:6px;background:rgba(255,255,255,0.14);box-shadow:0 0 0 1px rgba(255,255,255,0.18);font-size:0.72em;font-weight:700;line-height:1;color:#ffffff;}
.gccp-row-code{font-size:0.85rem;color:#ffffff;font-weight:700;opacity:0.9;min-width:52px;-webkit-flex-shrink:0;flex-shrink:0;letter-spacing:0.02em;}
.gccp-row-symbol{font-size:0.85rem;color:var(--gccp-text-muted);min-width:2.2em;text-align:right;-webkit-flex-shrink:0;flex-shrink:0;font-weight:600;}
'''
  }

  /** Once-per-page script: also match the typed text against the symbol. */
  private String symbolSearchJs() {
    '''
(function(){
  document.addEventListener('input',function(e){
    var t=e.target;
    if(!t||!t.classList||!t.classList.contains('gccp-search')) return;
    var q=(t.value||'').trim().toLowerCase();
    var panel=t.closest('.gccp-panel'); if(!panel||!q) return;
    var list=panel.querySelector('.gccp-list'); if(!list) return;
    var any=false, rows=list.querySelectorAll('.gccp-row');
    Array.prototype.forEach.call(rows,function(r){
      if(r.style.display==='none'){
        var s=(r.getAttribute('data-symbol')||'').toLowerCase();
        if(s && s.indexOf(q)!==-1) r.style.display='';
      }
      if(r.style.display!=='none') any=true;
    });
    var empty=list.querySelector('.gccp-empty');
    if(empty) empty.style.display=any?'none':'';
  });
})();
'''
  }

  private Map jsHooks() {
    [
      extraHelpers: '''
  function symBadge(s){ return s ? '<span class="gccp-sym">'+escHtml(s)+'</span>' : ''; }
''',
      hiddenFields: '''
        var sy=data(row,'symbol')||'';
        var elCd=wrap.querySelector('.gccp-hidden-code'), elNm=wrap.querySelector('.gccp-hidden-name'), elSy=wrap.querySelector('.gccp-hidden-symbol'), elFl=wrap.querySelector('.gccp-hidden-flag');
        if(elCd) elCd.value=dc; if(elNm) elNm.value=cn; if(elSy) elSy.value=sy; if(elFl) elFl.value=fl;
''',
      uiUpdate    : '''
        var leadHtml = fl ? flagMarkup(fl) : symBadge(sy);
        if(btnFlag) btnFlag.innerHTML = leadHtml;
        var showName = wrap.getAttribute('data-show-name')!=='0';
        if(btnLabel){
          var lbl = showName ? ((dc?dc+' ':'')+(cn||'')).trim() : (dc||'').trim();
          btnLabel.textContent = lbl || (wrap.getAttribute('data-button-text')||'Select Currency');
        }
        if(tooltip) tooltip.innerHTML = (leadHtml?leadHtml+' ':'') + escHtml((dc||'') + (cn ? ' \\u2014 ' + cn : ''));
''',
      eventName   : 'gccp:change',
      eventDetail : '{code:dc,name:cn,symbol:sy,flag:fl}'
    ]
  }

  def currencyPicker = { attrs, body ->

    if (attrs.currencies == null) {
      throwTagError("Tag [sheet:currencyPicker] is missing required attribute [currencies]")
    }

    String id = attrs.id?.toString() ?: "gccp_${System.nanoTime()}"
    def currencies = attrs.currencies ?: []
    String codeFieldName = attrs.currencyCodeName?.toString() ?: "currencyCode"
    String nameFieldName = attrs.currencyName?.toString() ?: "currencyName"
    String symbolFieldName = attrs.symbolName?.toString() ?: "currencySymbol"
    String flagFieldName = attrs.flagName?.toString() ?: "currencyFlag"
    String selectedCode = attrs.selectedCode?.toString() ?: ""
    String selectedCurrency = attrs.selectedCurrency?.toString() ?: ""
    String selectedSymbol = attrs.selectedSymbol?.toString() ?: ""
    String selectedFlag = attrs.selectedFlag?.toString() ?: ""
    String buttonText = attrs.buttonText?.toString() ?: "Select Currency"
    String panelTitle = attrs.panelTitle?.toString() ?: "Select Currency"
    String cssClass = attrs.cssClass?.toString() ?: ""
    String wrapStyle = attrs.style?.toString() ?: ""

    boolean disabled = parseDisabled(attrs.disabled)
    boolean showName = (attrs.showCurrencyName == null) ? true : parseDisabled(attrs.showCurrencyName)

    // Resolve initial selection: code first, then name; else raw attrs.
    def match = null
    if (selectedCode) {
      match = currencies.find { currencyCodeOf(it) == selectedCode }
    }
    if (!match && selectedCurrency) {
      match = currencies.find { currencyNameOf(it) == selectedCurrency }
    }

    String initCode = match ? currencyCodeOf(match) : selectedCode
    String initName = match ? currencyNameOf(match) : selectedCurrency
    String initSymbol = match ? currencySymbolOf(match) : selectedSymbol
    String initFlag = match ? countryFlagValue(match) : selectedFlag

    String initTipHtml = "No currency selected yet"
    if (initCode || initName) {
      String tipText = "${initCode ?: ''}${initName ? ' \u2014 ' + initName : ''}".trim()
      String lead = flagOrSymbol(initFlag, initSymbol)
      initTipHtml = (lead ? "${lead} " : "") + h(tipText)
    }

    String initLabel = buttonLabel(initCode, initName, showName, buttonText)

    String sheetId = "${id}-sheet"
    String listId = "${id}-list"
    String tooltipId = "${id}-tooltip"
    String titleId = "${id}-title"
    String searchId = "${id}-search"

    out << flagIconsCdnLink(pageScope)

    if (!pageScope["__gccpCssWritten__"]) {
      pageScope["__gccpCssWritten__"] = true
      out << "<style id=\"gccp-styles\">"
      out << commonCss(PREFIX)
      out << extraCss()
      out << "</style>"
      out << "<script id=\"gccp-script\">"
      out << commonJs(PREFIX, jsHooks())
      out << "</script>"
      out << "<script id=\"gccp-symbol-search\">"
      out << symbolSearchJs()
      out << "</script>"
    }

    String styleAttr = wrapStyle ? " style=\"${h(wrapStyle)}\"" : ""
    String extraClass = cssClass ? " ${h(cssClass)}" : ""

    out << "<div id=\"${h(id)}\" class=\"gccp-wrap${extraClass}\" data-show-name=\"${showName ? '1' : '0'}\" data-button-text=\"${h(buttonText)}\"${styleAttr}>"

    out << "<button type=\"button\" class=\"gccp-btn\" aria-haspopup=\"dialog\" aria-expanded=\"false\" aria-controls=\"${h(sheetId)}\""
    if (disabled) out << " disabled"
    out << ">"
    out << "<span class=\"gccp-btn-flag\" aria-hidden=\"true\">${flagOrSymbol(initFlag, initSymbol)}</span>"
    out << "<span class=\"gccp-btn-label\">${h(initLabel)}</span>"
    out << "<span class=\"gccp-btn-chevron\" aria-hidden=\"true\"><svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2.3\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><polyline points=\"6 9 12 15 18 9\"/></svg></span>"
    out << "</button>"

    out << "<div id=\"${h(tooltipId)}\" class=\"gccp-tooltip\" role=\"tooltip\">${initTipHtml}</div>"

    out << "<div class=\"gccp-overlay\" style=\"opacity:0;visibility:hidden;\"></div>"

    out << "<div id=\"${h(sheetId)}\" class=\"gccp-panel\" role=\"dialog\" aria-modal=\"true\" aria-labelledby=\"${h(titleId)}\" aria-hidden=\"true\" style=\"-webkit-transform:translateY(100%);transform:translateY(100%);visibility:hidden;\">"
    out << "<div class=\"gccp-panel-handle\" aria-hidden=\"true\"></div>"
    out << "<div class=\"gccp-panel-head\">"
    out << "<span id=\"${h(titleId)}\" class=\"gccp-panel-title\">${h(panelTitle)}</span>"
    out << "</div>"

    out << "<div class=\"gccp-search-wrap\">"
    out << alphaNumericPlusTextFieldTagLib.alphaNumericPlusTextField(
      name: searchId,
      id: searchId,
      value: '',
      placeholder: 'Search currency, code or symbol\u2026',
      wrapper: false,
      floatingLabel: false,
      autocomplete: 'off',
      autocapitalize: 'off',
      class: 'gccp-search',
      tooltip: 'Clear search',
      tooltipPlacement: 'bottom'
    )
    out << "</div>"

    out << "<div id=\"${h(listId)}\" class=\"gccp-list\" role=\"listbox\" aria-label=\"${h(panelTitle)}\">"
    currencies.each { cur ->
      String code = currencyCodeOf(cur)
      String cName = currencyNameOf(cur)
      String symbol = currencySymbolOf(cur)
      String flagValue = countryFlagValue(cur)
      boolean isSel = (match != null) ? (cur == match)
        : (selectedCode && code == selectedCode)
      out << "<div class=\"gccp-row${isSel ? ' gccp-row-selected' : ''}\" role=\"option\" aria-selected=\"${isSel}\" tabindex=\"-1\" data-dial-code=\"${h(code)}\" data-country=\"${h(cName)}\" data-symbol=\"${h(symbol)}\" data-flag=\"${h(flagValue)}\">"
      out << "<span class=\"gccp-row-flag\" aria-hidden=\"true\">${flagOrSymbol(flagValue, symbol)}</span>"
      out << "<span class=\"gccp-row-code\">${h(code)}</span>"
      out << "<span class=\"gccp-row-name\">${h(cName)}</span>"
      out << "<span class=\"gccp-row-symbol\" aria-hidden=\"true\">${h(symbol)}</span>"
      out << "<span class=\"gccp-row-dot\" aria-hidden=\"true\"></span>"
      out << "</div>"
    }
    out << "<div class=\"gccp-empty\" style=\"display:none\">No currencies found</div>"
    out << "</div>" // list
    out << "</div>" // panel

    out << "<input type=\"hidden\" class=\"gccp-hidden-code\" name=\"${h(codeFieldName)}\" value=\"${h(initCode)}\">"
    out << "<input type=\"hidden\" class=\"gccp-hidden-name\" name=\"${h(nameFieldName)}\" value=\"${h(initName)}\">"
    out << "<input type=\"hidden\" class=\"gccp-hidden-symbol\" name=\"${h(symbolFieldName)}\" value=\"${h(initSymbol)}\">"
    out << "<input type=\"hidden\" class=\"gccp-hidden-flag\" name=\"${h(flagFieldName)}\" value=\"${h(initFlag)}\">"

    out << "</div>" // wrap
  }
}
