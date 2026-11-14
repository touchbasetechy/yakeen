package com.murad.app.matrimony.slideinsheet

/**
 * GlassBottomSheetBaseTagLib.groovy
 *
 * Shared base for the "bottom sheet country picker" taglibs:
 *   - GlassCountryBottomSheetTagLib      (sheet:countryPicker)     -> flag + ISD code + name
 *   - GlassCountryNameBottomSheetTagLib  (sheet:countryNamePicker) -> flag + name only
 *
 * Both pickers share the same trigger-button / bottom-sheet chrome,
 * behaviour (open/close, focus trap, keyboard nav, touch handling,
 * search-field delegation, tooltip auto-hide) and almost all of their
 * CSS. This base centralizes that shared code so each subclass only
 * has to carry what's genuinely different about it (the ISD code
 * column, which hidden fields it writes, its button label format).
 *
 * Extracted here:
 *   - h()                : shared HTML-escaping helper
 *   - parseDisabled()    : shared `disabled` attribute parsing
 *   - prop()             : tolerant property read off a Map *or* a POJO/
 *                          domain object, so both pickers accept the same
 *                          country objects regardless of whether they came
 *                          from parsed JSON or from GORM
 *   - countryDialCode()  : reads dial_code / dialCode / isdCode
 *   - countryDisplayName(): reads countryName / name / country
 *   - countryFlagValue() : reads countryFlag / flag / emoji
 *   - flagCode()         : normalizes a raw flag value to a bare lowercase
 *                          ISO 3166-1 alpha-2 code
 *   - flagMarkup()       : renders a flag value as `<span class="fi fi-xx">`,
 *                          falling back to the raw value (emoji) when it
 *                          isn't a two-letter code
 *   - flagIconsCdnLink() : writes the flag-icons stylesheet <link> at most
 *                          once per page, shared across BOTH pickers
 *   - commonCss()        : the shared CSS ruleset (variables, wrap, button
 *                          core incl. border/box-shadow, flag sizing,
 *                          tooltip, overlay, panel, search-wrap shell,
 *                          list, row, empty, reduced-motion), parameterized
 *                          by a CSS class prefix ("gcbs" / "gcnp")
 *   - commonJs()         : the shared JS behaviour (helpers incl. the flag
 *                          renderers, focus trap, open/close, filter,
 *                          keyboard nav, touch handling), parameterized by
 *                          prefix plus a small set of hooks for the bits
 *                          that differ per picker
 *
 * NOT extracted: the actual `out <<` tag-markup closures in each
 * subclass, since the button/row content differs meaningfully between
 * a flag + ISD-code + name render and a flag + name render, and forcing
 * that through shared markup helpers would add more indirection than
 * it would save.
 *
 * This class is `abstract` and declares no `namespace`/tag closures
 * of its own, so Grails' taglib artefact scanner skips it — it is
 * purely a plain Groovy superclass that the real taglibs extend for
 * these shared helpers.
 */
abstract class GlassBottomSheetBaseTagLib {

  /** flag-icons (https://github.com/lipis/flag-icons), loaded from cdnjs. */
  protected static final String FLAG_ICONS_CDN =
    '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/7.2.3/css/flag-icons.min.css">'

  /** Shared HTML-attribute/text escaping helper. */
  protected String h(String s) {
    s?.replace("&", "&amp;")
      ?.replace("<", "&lt;")
      ?.replace(">", "&gt;")
      ?.replace("\"", "&quot;") ?: ""
  }

  /** Shared `disabled` attribute parsing (Boolean or "true"/"false" string). */
  protected boolean parseDisabled(disabledRaw) {
    (disabledRaw instanceof Boolean) ? disabledRaw
      : disabledRaw?.toString()?.equalsIgnoreCase("true") ?: false
  }

  /**
   * Tolerant property read. `countries` may be a list of Maps (parsed
   * JSON, where a missing key is simply null) or of domain/POJO objects
   * (where a missing property throws). Returns null when absent so the
   * callers below can chain `?:` fallbacks across naming variants.
   */
  protected String prop(obj, String name) {
    if (obj == null) return null
    try {
      def v = (obj instanceof Map) ? obj[name] : obj."$name"
      return v?.toString()
    } catch (Exception ignored) {
      return null
    }
  }

  /** dial_code / dialCode / isdCode, whichever the country object carries. */
  protected String countryDialCode(c) {
    prop(c, 'dial_code') ?: prop(c, 'dialCode') ?: prop(c, 'isdCode') ?: ""
  }

  /** countryName / name / country, whichever the country object carries. */
  protected String countryDisplayName(c) {
    prop(c, 'countryName') ?: prop(c, 'name') ?: prop(c, 'country') ?: ""
  }

  /** countryFlag / flag / emoji, whichever the country object carries. */
  protected String countryFlagValue(c) {
    prop(c, 'countryFlag') ?: prop(c, 'flag') ?: prop(c, 'emoji') ?: ""
  }

  /**
   * Normalizes a raw flag value into a bare, lowercase ISO 3166-1
   * alpha-2 code suitable for a flag-icons class (`fi-<code>`).
   *
   * Accepts a plain code ("IN"), a legacy FontAwesome-style value
   * ("fas fa-flag-af"), or a class string whose last token is the code.
   * Returns "" for anything that isn't a two-letter code — notably
   * emoji flags, which flagMarkup() then renders as-is.
   */
  protected String flagCode(String raw) {
    if (!raw) return ""
    String trimmed = raw.trim()
    def faMatch = (trimmed =~ /(?i)fa-flag-([a-z]{2})\b/)
    if (faMatch.find()) {
      return faMatch.group(1).toLowerCase()
    }
    String token = trimmed.tokenize(" ").last().toLowerCase()
    (token ==~ /[a-z]{2}/) ? token : ""
  }

  /**
   * Renders a flag value as `<span class="fi fi-xx">`, per flag-icons'
   * documented markup, so the sprite paints instead of printing the raw
   * code to the page. Values that aren't ISO codes (emoji flags) are
   * emitted escaped, so legacy emoji-based country lists still show a flag.
   */
  protected String flagMarkup(String v) {
    if (!v) return ""
    String code = flagCode(v)
    code ? "<span class=\"fi fi-${h(code)}\" aria-hidden=\"true\"></span>" : h(v.trim())
  }

  /**
   * Returns the flag-icons stylesheet <link> the first time it is called
   * for a page, and "" thereafter. The page-scope key is deliberately
   * NOT prefixed, so a page using both pickers loads the sheet once.
   */
  protected String flagIconsCdnLink(pageScope) {
    if (pageScope["__flagIconsCdnWritten__"]) return ""
    pageScope["__flagIconsCdnWritten__"] = true
    FLAG_ICONS_CDN
  }

  /**
   * Shared CSS ruleset for a bottom-sheet country picker, with the
   * given class prefix ("gcbs" / "gcnp") substituted in for every
   * `.PFX-*` selector and `--PFX-*` custom property below.
   *
   * Includes the flag-icon sizing/centering rules for both the trigger
   * button and the rows, so the two pickers render flags identically.
   * Deliberately excludes anything that differs per picker: button
   * font-size/min-height and the ISD-code column. Subclasses add those
   * as a small extra CSS block appended after this one.
   */
  protected String commonCss(String prefix) {
    String css = '''
:root {
  --PFX-accent        : #7ea8ff;
  --PFX-accent-soft   : rgba(126,168,255,0.35);
  --PFX-background-btn        : rgba(255,255,255,0.10);
  --PFX-background-btn-hover  : rgba(255,255,255,0.16);
  --PFX-border        : rgba(255,255,255,0.28);
  --PFX-border-hover  : rgba(255,255,255,0.48);
  --PFX-text          : #ffffff;
  --PFX-text-muted    : rgba(255,255,255,0.72);
  --PFX-radius        : 14px;
  --PFX-panel-background      : rgba(22,28,58,0.35);
  --PFX-panel-border  : rgba(255,255,255,0.24);
  --PFX-overlay       : rgba(4,6,18,0.55);
  --PFX-item-hover    : rgba(255,255,255,0.12);
  --PFX-item-selected : rgba(126,168,255,0.24);
  --PFX-font          : 'Segoe UI', system-ui, -apple-system, BlinkMacSystemFont, Roboto, sans-serif;
  --PFX-transition    : 220ms cubic-bezier(.4,0,.2,1);
}

.PFX-wrap,.PFX-wrap *,.PFX-wrap *::before,.PFX-wrap *::after{-webkit-box-sizing:border-box;box-sizing:border-box;}
.PFX-wrap{position:relative;display:inline-block;font-family:var(--PFX-font);width:100%;}

.PFX-btn{
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  gap:10px;width:100%;padding:8px 18px;
  border-radius:var(--PFX-radius);border:1px solid var(--PFX-border);
  background:var(--PFX-background-btn);color:var(--PFX-text);cursor:pointer;
  font-weight:500;letter-spacing:0.01em;
  -webkit-transition:background var(--PFX-transition),border-color var(--PFX-transition),box-shadow var(--PFX-transition),-webkit-transform var(--PFX-transition);
  transition:background var(--PFX-transition),border-color var(--PFX-transition),box-shadow var(--PFX-transition),transform var(--PFX-transition);
  touch-action:manipulation;-webkit-tap-highlight-color:transparent;
  box-shadow:0 4px 18px rgba(0,0,0,0.28),inset 0 1px 0 rgba(255,255,255,0.08);
  position:relative;z-index:1;
}
@supports((-webkit-backdrop-filter:blur(1px)) or (backdrop-filter:blur(1px))){
  .PFX-btn{-webkit-backdrop-filter:blur(24px) saturate(180%);backdrop-filter:blur(24px) saturate(180%);}
}
@supports not ((-webkit-backdrop-filter:blur(1px)) or (backdrop-filter:blur(1px))){
  .PFX-btn{background:rgba(28,34,66,0.88);}
}
.PFX-btn:hover{border-color:var(--PFX-border-hover);background:var(--PFX-background-btn-hover);}
.PFX-btn:focus-visible{border-color:#ffffff;background:var(--PFX-background-btn-hover);outline:2px solid #ffffff;outline-offset:2px;}
.PFX-btn:active{-webkit-transform:scale(0.98);transform:scale(0.98);}
.PFX-btn[disabled]{opacity:0.45;cursor:not-allowed;pointer-events:none;}
.PFX-btn-label{-webkit-box-flex:1;-webkit-flex:1;-ms-flex:1 1 auto;flex:1;text-align:left;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.PFX-btn-chevron{opacity:0.65;-webkit-flex-shrink:0;flex-shrink:0;display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;}
.PFX-btn-chevron svg{width:16px;height:16px;}
.PFX-btn::after{
  content:'';position:absolute;inset:0;border-radius:inherit;pointer-events:none;
  background:linear-gradient(135deg, rgba(255,255,255,0.35) 0%, rgba(255,255,255,0.06) 40%, rgba(255,255,255,0) 70%);
  -webkit-mask:linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite:destination-out; mask-composite:exclude;
  border:1px solid transparent;
}

/* ── flag rendering (shared by both pickers) ───────────────────── */
.PFX-btn-flag{
  font-size:1.35em;line-height:1;min-width:1.5em;text-align:center;
  -webkit-flex-shrink:0;flex-shrink:0;
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  -webkit-box-pack:center;-webkit-justify-content:center;-ms-flex-pack:center;justify-content:center;
}
.PFX-row-flag{
  font-size:1.4em;line-height:1;min-width:1.6em;text-align:center;
  -webkit-flex-shrink:0;flex-shrink:0;
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  -webkit-box-pack:center;-webkit-justify-content:center;-ms-flex-pack:center;justify-content:center;
}
.PFX-btn-flag .fi,.PFX-row-flag .fi{font-size:1em;line-height:1;border-radius:3px;box-shadow:0 0 0 1px rgba(255,255,255,0.18);}

.PFX-tooltip{
  position:absolute;bottom:calc(100% + 10px);left:50%;
  -webkit-transform:translateX(-50%) translateY(4px);transform:translateX(-50%) translateY(4px);
  background:rgba(10,14,34,0.92);color:#ffffff;padding:8px 12px;border-radius:10px;
  font-size:0.8rem;white-space:nowrap;pointer-events:none;opacity:0;
  border:1px solid rgba(255,255,255,0.15);box-shadow:0 8px 22px rgba(0,0,0,0.35);
  z-index:5;visibility:hidden;
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  gap:6px;
  -webkit-transition:opacity 160ms ease,-webkit-transform 160ms ease;
  transition:opacity 160ms ease,transform 160ms ease;
}
.PFX-tooltip .fi{border-radius:2px;box-shadow:0 0 0 1px rgba(255,255,255,0.18);-webkit-flex-shrink:0;flex-shrink:0;}
.PFX-tooltip::after{content:'';position:absolute;top:100%;left:50%;-webkit-transform:translateX(-50%);transform:translateX(-50%);
  border:6px solid transparent;border-top-color:rgba(10,14,34,0.92);}
.PFX-tooltip.PFX-tooltip-show{opacity:1;visibility:visible;-webkit-transform:translateX(-50%) translateY(0);transform:translateX(-50%) translateY(0);}

.PFX-overlay{
  position:fixed;inset:0;background:var(--PFX-overlay);opacity:0;visibility:hidden;
  z-index:2147483000;-webkit-transition:opacity var(--PFX-transition);transition:opacity var(--PFX-transition);
}
.PFX-overlay.PFX-overlay-open{opacity:1;visibility:visible;}

.PFX-panel{
  position:fixed;inset:0;width:100%;height:100%;
  -webkit-transform:translateY(100%);transform:translateY(100%);
  visibility:hidden;
  background:rgba(22,28,58,0.35);background:var(--PFX-panel-background) !important;border:1px solid var(--PFX-panel-border);
  border-radius:0;box-shadow:0 -14px 50px rgba(0,0,0,0.5),inset 0 1px 0 rgba(255,255,255,0.12);
  z-index:2147483001;display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-orient:vertical;-webkit-box-direction:normal;-webkit-flex-direction:column;-ms-flex-direction:column;flex-direction:column;
  -webkit-transition:-webkit-transform 320ms cubic-bezier(.32,.72,0,1);transition:transform 320ms cubic-bezier(.32,.72,0,1),visibility 0s linear 320ms;
  overscroll-behavior:contain;color:var(--PFX-text);
  padding-top:env(safe-area-inset-top,0px);padding-bottom:env(safe-area-inset-bottom,0px);
}
@supports((-webkit-backdrop-filter:blur(1px)) or (backdrop-filter:blur(1px))){
  .PFX-panel{-webkit-backdrop-filter:blur(18px) saturate(140%);backdrop-filter:blur(18px) saturate(140%);}
}
@supports not ((-webkit-backdrop-filter:blur(1px)) or (backdrop-filter:blur(1px))){
  .PFX-panel{background:rgba(16,20,46,0.94);}
}
.PFX-panel.PFX-panel-open{-webkit-transform:translateY(0);transform:translateY(0);visibility:visible;transition:transform 320ms cubic-bezier(.32,.72,0,1),visibility 0s linear 0s;}

.PFX-panel-handle{width:42px;height:4px;border-radius:4px;background:rgba(255,255,255,0.25);margin:10px auto 2px;-webkit-flex-shrink:0;flex-shrink:0;cursor:pointer;touch-action:manipulation;-webkit-tap-highlight-color:transparent;-webkit-transition:background var(--PFX-transition);transition:background var(--PFX-transition);}
.PFX-panel-handle:hover{background:rgba(255,255,255,0.42);}
.PFX-panel-head{
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  -webkit-box-pack:justify;-webkit-justify-content:space-between;-ms-flex-pack:justify;justify-content:space-between;
  padding:8px 18px 12px;-webkit-flex-shrink:0;flex-shrink:0;
  max-width:700px;margin:0 auto;width:100%;
}
.PFX-panel-title{font-size:1.02rem;font-weight:700;color:var(--PFX-text);}

.PFX-search-wrap{padding:0 18px 12px;-webkit-flex-shrink:0;flex-shrink:0;max-width:700px;margin:0 auto;width:100%;box-sizing:border-box;}
.PFX-search-wrap .input-glass{background:rgba(255,255,255,0.06);border-color:var(--PFX-border);}
.PFX-search-wrap .input-glass input.PFX-search{
  width:100%;padding:11px 14px 11px 38px !important;border:0;background:transparent;color:var(--PFX-text);
  font-size:1rem;outline:none;
  -webkit-appearance:none;-moz-appearance:none;appearance:none;
  background-image:url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%2399aadd' stroke-width='2.2' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='11' cy='11' r='8'/%3E%3Cline x1='21' y1='21' x2='16.65' y2='16.65'/%3E%3C/svg%3E") !important;
  background-repeat:no-repeat !important;background-position:12px center !important;background-size:16px 16px !important;
}
.PFX-search-wrap .input-glass input.PFX-search::placeholder{color:var(--PFX-text-muted);}
.PFX-search-wrap .input-glass:focus-within{border-color:var(--PFX-accent);box-shadow:0 0 0 3px var(--PFX-accent-soft);}

.PFX-list{
  -webkit-box-flex:1;-webkit-flex:1 1 auto;-ms-flex:1 1 auto;flex:1 1 auto;min-height:0;
  overflow-y:auto;-webkit-overflow-scrolling:touch;overscroll-behavior:contain;padding:0 8px 14px;scrollbar-width:thin;
}
@media (min-width:700px){ .PFX-list{ max-width:640px;margin:0 auto;padding-left:0;padding-right:0; } }
.PFX-list::-webkit-scrollbar{width:5px;}
.PFX-list::-webkit-scrollbar-thumb{background:rgba(255,255,255,0.18);border-radius:10px;}

.PFX-row{
  display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;
  -webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;
  gap:12px;padding:12px 12px;margin:2px 4px;border-radius:14px;cursor:pointer;color:var(--PFX-text);
  min-height:48px;touch-action:manipulation;-webkit-tap-highlight-color:transparent;
  -webkit-transition:background var(--PFX-transition);transition:background var(--PFX-transition);
}
.PFX-row:hover,.PFX-row.PFX-row-hi{background:var(--PFX-item-hover);}
.PFX-row.PFX-row-selected{background:var(--PFX-item-selected);}
.PFX-row-name{-webkit-box-flex:1;-webkit-flex:1;-ms-flex:1 1 auto;flex:1;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.PFX-row-dot{width:8px;height:8px;border-radius:50%;background:var(--PFX-accent);-webkit-flex-shrink:0;flex-shrink:0;opacity:0;-webkit-transform:scale(0.4);transform:scale(0.4);-webkit-transition:opacity 140ms,-webkit-transform 140ms;transition:opacity 140ms,transform 140ms;}
.PFX-row.PFX-row-selected .PFX-row-dot{opacity:1;-webkit-transform:scale(1);transform:scale(1);}
.PFX-empty{padding:24px 14px;text-align:center;color:var(--PFX-text-muted);font-size:0.88rem;font-style:italic;}

@media (prefers-reduced-motion:reduce){
  .PFX-panel,.PFX-overlay,.PFX-tooltip,.PFX-btn{transition:none !important;}
}
'''
    css.replace('PFX', prefix)
  }

  /**
   * Shared JS behaviour for a bottom-sheet country picker.
   *
   * `prefix` is substituted into every `.PFX-*` selector/id below.
   *
   * Always available to the hooks (declared at the top of the IIFE):
   *   escHtml(v)               -> HTML-escaped string
   *   flagCode(raw)            -> '' or a two-letter ISO code
   *   flagMarkup(raw)          -> '<span class="fi fi-xx">' or escaped emoji
   *   setFlagContent(el,fl)    -> renders a flag into an element
   *   setTooltipContent(el,fl,text) -> flag + text into the tooltip
   *
   * `hooks` supplies the bits that differ per picker:
   *   - extraHelpers : extra top-level helper function declarations,
   *                    inserted just above `function init(){...}`
   *   - hiddenFields : statements inside selectRow() that write the
   *                    picker's own hidden `<input>` fields
   *   - uiUpdate     : statements inside selectRow() that update the
   *                    trigger button's flag/label and the tooltip
   *   - eventName    : the CustomEvent name dispatched on selection
   *   - eventDetail  : the CustomEvent detail object literal
   *
   * `dc`/`cn`/`fl` (dial code / country name / flag) are always
   * extracted from the row's dataset before the hooks run, even for
   * pickers with no dial code — `dc` is simply `''` in that case.
   */
  protected String commonJs(String prefix, Map hooks) {
    String js = '''
(function(){
  'use strict';
  if (typeof Element!=='undefined' && !Element.prototype.closest){
    Element.prototype.closest=function(sel){
      var el=this;
      while(el && el.nodeType===1){
        var m=el.matches||el.msMatchesSelector||el.webkitMatchesSelector;
        if(m && m.call(el,sel)) return el;
        el=el.parentElement||el.parentNode;
      }
      return null;
    };
  }
  var passiveOK=false;
  try{
    var o=Object.defineProperty({},'passive',{get:function(){passiveOK=true;return true;}});
    window.addEventListener('__PFXprobe',null,o);window.removeEventListener('__PFXprobe',null,o);
  }catch(e){}
  function on(el,type,fn,passive){el.addEventListener(type,fn,(passive&&passiveOK)?{passive:true}:false);}
  function each(list,fn){Array.prototype.forEach.call(list,fn);}
  function data(el,key){return el.dataset?el.dataset[key]:el.getAttribute('data-'+key.replace(/([A-Z])/g,'-$1').toLowerCase());}
  function escHtml(v){ return String(v==null?'':v).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;'); }
  function flagCode(raw){
    if(!raw) return '';
    var trimmed=String(raw).trim();
    var m=trimmed.match(/fa-flag-([a-z]{2})\\b/i);
    if(m) return m[1].toLowerCase();
    var parts=trimmed.split(/\\s+/);
    var token=parts[parts.length-1].toLowerCase();
    return /^[a-z]{2}$/.test(token)?token:'';
  }
  function flagMarkup(raw){
    if(!raw) return '';
    var code=flagCode(raw);
    return code ? '<span class="fi fi-'+escHtml(code)+'" aria-hidden="true"></span>' : escHtml(String(raw).trim());
  }
  function setFlagContent(el,fl){ if(el) el.innerHTML=flagMarkup(fl); }
  function setTooltipContent(el,fl,text){
    if(!el) return;
    var mk=flagMarkup(fl);
    el.innerHTML=(mk?mk+' ':'')+escHtml(text);
  }
__EXTRA_HELPERS__
  function init(){
    each(document.querySelectorAll('.PFX-wrap:not([data-PFX-init])'),function(wrap){
      wrap.setAttribute('data-PFX-init','1');
      var btn      = wrap.querySelector('.PFX-btn');
      var tooltip  = wrap.querySelector('.PFX-tooltip');
      var overlay  = wrap.querySelector('.PFX-overlay');
      var panel    = wrap.querySelector('.PFX-panel');
      var panelHandle = wrap.querySelector('.PFX-panel-handle');
      var searchWrapEl = wrap.querySelector('.PFX-search-wrap');
      var search   = wrap.querySelector('.PFX-search');
      var listEl   = wrap.querySelector('.PFX-list');
      var btnFlag  = wrap.querySelector('.PFX-btn-flag');
      var btnLabel = wrap.querySelector('.PFX-btn-label');
      if(!btn||!overlay||!panel||!listEl) return;

      if(search && listEl.id) search.setAttribute('aria-controls', listEl.id);

      overlay.removeAttribute('style');
      panel.removeAttribute('style');

      document.body.appendChild(overlay);
      document.body.appendChild(panel);

      var isOpen=false, hiIdx=-1, lastTouchEnd=0;

      function visible(){
        var out=[];
        each(listEl.querySelectorAll('.PFX-row'),function(r){ if(r.style.display!=='none') out.push(r); });
        return out;
      }
      function clearHi(){ each(listEl.querySelectorAll('.PFX-row-hi'),function(r){r.classList.remove('PFX-row-hi');}); hiIdx=-1; }
      function setHi(idx){
        var vis=visible(); if(!vis.length){clearHi();return;}
        if(idx<0) idx=0; if(idx>=vis.length) idx=vis.length-1;
        each(vis,function(r,i){ r.classList.toggle('PFX-row-hi', i===idx); });
        hiIdx=idx;
        vis[idx].scrollIntoView({block:'nearest'});
      }
      function filter(q){
        var norm=(q||'').toLowerCase().trim();
        var normCompact=norm.replace(/\\s+/g,'');
        var normDigits=norm.replace(/[^0-9]/g,'');
        var rows=listEl.querySelectorAll('.PFX-row'); var any=false;
        each(rows,function(r){
          var cn=(data(r,'country')||'').toLowerCase();
          var cnCompact=cn.replace(/\\s+/g,'');
          var dc=(data(r,'dialCode')||'').toLowerCase();
          var dcDigits=dc.replace(/[^0-9]/g,'');
          var match=!norm || cn.indexOf(norm)!==-1 || cnCompact.indexOf(normCompact)!==-1
                    || (dc && dc.indexOf(norm)!==-1)
                    || (normDigits && dcDigits && dcDigits.indexOf(normDigits)===0);
          r.style.display= match?'':'none';
          if(match) any=true;
        });
        var empty=listEl.querySelector('.PFX-empty');
        if(empty) empty.style.display= any?'none':'';
        clearHi();
      }
      var tipAutoHideTimer=null;
      function showTip(){ clearTimeout(tipAutoHideTimer); if(tooltip) tooltip.classList.add('PFX-tooltip-show'); }
      function hideTip(){ clearTimeout(tipAutoHideTimer); if(tooltip) tooltip.classList.remove('PFX-tooltip-show'); }

      function handleNavKey(e){
        var vis=visible(); var len=vis.length;
        if(e.key==='ArrowDown'){ e.preventDefault(); setHi(hiIdx<len-1?hiIdx+1:0); }
        else if(e.key==='ArrowUp'){ e.preventDefault(); setHi(hiIdx>0?hiIdx-1:len-1); }
        else if(e.key==='Enter'){ e.preventDefault(); if(hiIdx>=0&&vis[hiIdx]) selectRow(vis[hiIdx]); }
        else if(e.key==='Escape'){ e.preventDefault(); closePanel(true); }
      }

      function openPanel(){
        if(btn.disabled) return;
        hideTip();
        isOpen=true;
        overlay.classList.add('PFX-overlay-open');
        panel.classList.add('PFX-panel-open');
        panel.setAttribute('aria-hidden','false');
        btn.setAttribute('aria-expanded','true');
        if(search){ search.value=''; filter(''); setTimeout(function(){search.focus();},260); }
        var sel=listEl.querySelector('.PFX-row-selected');
        clearHi();
        if(sel){ var vis=visible(); each(vis,function(r,i){ if(r===sel) setHi(i); }); }
      }
      function closePanel(retFocus){
        isOpen=false;
        overlay.classList.remove('PFX-overlay-open');
        panel.classList.remove('PFX-panel-open');
        panel.setAttribute('aria-hidden','true');
        btn.setAttribute('aria-expanded','false');
        clearHi();
        if(retFocus) btn.focus();
      }
      function selectRow(row){
        if(!row) return;
        var dc=data(row,'dialCode')||'', cn=data(row,'country')||'', fl=data(row,'flag')||'';
__SELECT_ROW_HIDDEN_FIELDS__
__SELECT_ROW_UI_UPDATE__
        each(listEl.querySelectorAll('.PFX-row'),function(r){
          r.classList.toggle('PFX-row-selected', r===row);
          r.setAttribute('aria-selected', r===row ? 'true':'false');
        });
        closePanel(true);
        clearTimeout(tipAutoHideTimer);
        tipAutoHideTimer=setTimeout(function(){ hideTip(); },8000);
        wrap.dispatchEvent(new CustomEvent('__EVENT_NAME__',{bubbles:true,detail:__EVENT_DETAIL__}));
      }

      on(btn,'mouseenter',showTip,true);
      on(btn,'mouseleave',hideTip,true);
      btn.addEventListener('focus',showTip);
      btn.addEventListener('blur',hideTip);
      on(btn,'touchstart',showTip,true);
      btn.addEventListener('click',function(e){ e.stopPropagation(); isOpen?closePanel(false):openPanel(); });
      btn.addEventListener('keydown',function(e){
        if(isOpen) return;
        var k=e.key;
        if(k==='Enter'||k===' '||k==='ArrowDown'){ e.preventDefault(); openPanel(); }
      });

      if(panelHandle) panelHandle.addEventListener('click',function(e){ e.stopPropagation(); closePanel(true); });
      overlay.addEventListener('click',function(){ closePanel(false); });

      panel.addEventListener('click',function(e){
        var t=e.target;
        if(searchWrapEl && searchWrapEl.contains(t)) return;
        if(listEl && listEl.contains(t)) return;
        if(search) search.focus(); else if(searchWrapEl) searchWrapEl.focus();
      });

      document.addEventListener('keydown',function(e){
        if(!isOpen) return;
        if(e.key==='Escape'){ e.preventDefault(); closePanel(true); return; }
        if(e.key==='Tab'){
          if(!searchWrapEl) return;
          e.preventDefault();
          (search||searchWrapEl).focus();
        }
      });

      if(search){
        search.addEventListener('input',function(){ filter(this.value); });
        search.addEventListener('keydown', handleNavKey);
      }

      listEl.addEventListener('click',function(e){
        var row=e.target.closest?e.target.closest('.PFX-row'):null;
        selectRow(row);
      });
      listEl.addEventListener('mouseover',function(e){
        var row=e.target.closest?e.target.closest('.PFX-row'):null;
        if(!row) return;
        var vis=visible();
        for(var i=0;i<vis.length;i++){ if(vis[i]===row){ setHi(i); break; } }
      });

      var tStartY=0,tMoved=false;
      on(listEl,'touchstart',function(e){ var t=e.changedTouches&&e.changedTouches[0]; if(t){tStartY=t.clientY;tMoved=false;} },true);
      on(listEl,'touchmove',function(e){ var t=e.changedTouches&&e.changedTouches[0]; if(t&&Math.abs(t.clientY-tStartY)>8) tMoved=true; },true);
      on(listEl,'touchend',function(e){
        if(tMoved){tMoved=false;return;}
        var now=Date.now(); if(now-lastTouchEnd<350){e.preventDefault();return;} lastTouchEnd=now;
        var t=e.changedTouches&&e.changedTouches[0];
        var target=(t&&document.elementFromPoint)?document.elementFromPoint(t.clientX,t.clientY):e.target;
        var row=target&&target.closest?target.closest('.PFX-row'):null;
        if(row){ e.preventDefault(); selectRow(row); }
      },false);
    });
  }
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',init); else init();
  window.PFXInit=init;
})();
'''
    js = js.replace('PFX', prefix)
    js = js.replace('__EXTRA_HELPERS__', hooks?.extraHelpers ?: '')
    js = js.replace('__SELECT_ROW_HIDDEN_FIELDS__', hooks?.hiddenFields ?: '')
    js = js.replace('__SELECT_ROW_UI_UPDATE__', hooks?.uiUpdate ?: '')
    js = js.replace('__EVENT_NAME__', hooks?.eventName ?: "${prefix}:change")
    js = js.replace('__EVENT_DETAIL__', hooks?.eventDetail ?: '{}')
    js
  }
}
