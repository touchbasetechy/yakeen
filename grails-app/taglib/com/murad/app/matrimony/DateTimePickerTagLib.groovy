package com.murad.app.matrimony

import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.atomic.AtomicInteger

/**
 * DatePickerTagLib
 * ------------------
 * A GSP custom taglib that renders an iOS-style wheel/spinner date-time
 * picker: a glassmorphic bottom sheet (modal on larger screens) holding
 * scroll-snapped, touch-scrollable columns with a carousel curvature
 * effect, matching the native iPhone picker feel — no dependencies.
 *
 * Tags:
 *   <dp:resources/>   emit CSS + JS once
 *   <dp:picker name="birthday" value="1990-05-12" mode="date"/>
 *
 * SUBMITTED VALUE FORMAT:
 *   mode="date"      -> yyyy-MM-ddT00:00   (parseable by LocalDateTime.parse)
 *   mode="time"      -> HH:mm
 *   mode="datetime"  -> yyyy-MM-ddTHH:mm
 *   In date mode the "value" attribute may be either yyyy-MM-dd or
 *   yyyy-MM-ddT... (e.g. a LocalDateTime rendered with toString()).
 *
 * DISABLED (disabled="true"):
 *   The trigger is a real disabled <button>, so it can't be clicked or
 *   focused and the wheel sheet never opens; the hidden input is disabled
 *   too (native form semantics: not submitted, ignored by checkValidity,
 *   but its .value can still be read from script). The trigger is dimmed
 *   with a not-allowed cursor. Removing the "disabled" property from the
 *   trigger at runtime re-enables the picker.
 *
 * HEIGHT MATCHING (--gform-height):
 *   .dp-trigger is pinned to the shared --gform-height token (also
 *   declared in glass:radio's and glass:select's stylesheets, and sourced
 *   from glass:input's shell height) so this control renders at the same
 *   height as a text input field.
 */
class DateTimePickerTagLib {

  static namespace = 'dp'

  static encodeAsForTags = [
    resources: 'raw',
    picker   : 'raw'
  ]

  private static final AtomicInteger COUNTER = new AtomicInteger(0)

  // ---------------------------------------------------------------------
  // <dp:resources/> — idempotent, emit once per response
  // ---------------------------------------------------------------------
  def resources = { attrs ->
    if (request.getAttribute('dp_resources_rendered')) {
      return
    }
    request.setAttribute('dp_resources_rendered', true)
    out << CSS
    out << JS
  }

  // ---------------------------------------------------------------------
  // <dp:picker
  //     name="birthday"                     required — hidden input field name
  //     value="1990-05-12"                   ISO date / time / date-time string
  //     mode="date|time|datetime"            default "date"
  //     min="1900-01-01" max="2026-12-31"    optional bounds (year-clamped)
  //     startYear="1950" endYear="2030"      optional explicit year range
  //     hour12="true"                        12h clock with AM/PM column
  //     minuteStep="5"                        minute increment
  //     locale="en-US"                        BCP-47 locale tag
  //     dateStyle="medium" timeStyle="short"  Intl/java.time style names
  //     label="Date of birth"                 accessible field label
  //     placeholder="Select date"
  //     required="true"
  //     disabled="true"                       non-interactive, dimmed, not submitted
  //     glass="true" tint="light|dark"
  //     id="..." class="...">
  // </dp:picker>
  // ---------------------------------------------------------------------
  def picker = { attrs ->
    def name = attrs.name
    if (!name) {
      out << '<!-- dp:picker requires a "name" attribute -->'
      return
    }

    def mode = (attrs.mode ?: 'date').toString().toLowerCase() // date | time | datetime
    def localeTag = (attrs.locale ?: 'en-US').toString()
    def locale = Locale.forLanguageTag(localeTag)
    def hour12 = attrs.hour12 == null ? true : Boolean.valueOf(attrs.hour12.toString())
    def minuteStep = (attrs.minuteStep ?: 1) as int
    def glass = attrs.glass == null ? true : Boolean.valueOf(attrs.glass.toString())
    def tint = (attrs.tint ?: 'light').toString().toLowerCase()
    def dateStyleName = (attrs.dateStyle ?: 'medium').toString().toLowerCase()
    def timeStyleName = (attrs.timeStyle ?: 'short').toString().toLowerCase()
    def placeholder = (attrs.placeholder ?: 'Select date').toString()
    def label = attrs.label
    def required = attrs.required == null ? false : Boolean.valueOf(attrs.required.toString())
    def disabled = attrs.disabled == null ? false : Boolean.valueOf(attrs.disabled.toString())
    def id = attrs.id ?: "dp-${COUNTER.incrementAndGet()}"
    def cssClass = attrs.class ? " ${attrs.class}" : ''

    int currentYear = Year.now().value
    int startYear = (attrs.startYear ?: (currentYear - 100)) as int
    int endYear = (attrs.endYear ?: (currentYear + 10)) as int

    // ---- resolve initial value ----
    LocalDate d = LocalDate.now()
    LocalTime t = LocalTime.now().withSecond(0).withNano(0)
    boolean hasValue = false
    if (attrs.value) {
      try {
        switch (mode) {
          case 'time':
            t = LocalTime.parse(attrs.value.toString())
            hasValue = true
            break
          case 'datetime':
            def ldt = LocalDateTime.parse(attrs.value.toString())
            d = ldt.toLocalDate(); t = ldt.toLocalTime()
            hasValue = true
            break
          default:
            // accepts "yyyy-MM-dd" as well as "yyyy-MM-ddTHH:mm[:ss]"
            d = LocalDate.parse(attrs.value.toString().split('T')[0])
            hasValue = true
        }
      } catch (ignored) { /* fall back to now */
      }
    }
    int minuteVal = t.minute - (t.minute % minuteStep)

    def isoValue
    switch (mode) {
      case 'time': isoValue = String.format('%02d:%02d', t.hour, minuteVal); break
      case 'datetime': isoValue = "${d}T" + String.format('%02d:%02d', t.hour, minuteVal); break
      default: isoValue = "${d}T00:00" // LocalDateTime-parseable
    }

    // ---- server-rendered trigger label (progressive enhancement) ----
    String triggerLabel = placeholder
    if (hasValue) {
      def styles = [full: FormatStyle.FULL, long: FormatStyle.LONG, medium: FormatStyle.MEDIUM, short: FormatStyle.SHORT]
      def dStyle = styles[dateStyleName] ?: FormatStyle.MEDIUM
      def tStyle = styles[timeStyleName] ?: FormatStyle.SHORT
      switch (mode) {
        case 'time':
          triggerLabel = t.format(DateTimeFormatter.ofLocalizedTime(tStyle).withLocale(locale))
          break
        case 'datetime':
          triggerLabel = d.format(DateTimeFormatter.ofLocalizedDate(dStyle).withLocale(locale)) +
            ' \u00B7 ' + t.format(DateTimeFormatter.ofLocalizedTime(tStyle).withLocale(locale))
          break
        default:
          triggerLabel = d.format(DateTimeFormatter.ofLocalizedDate(dStyle).withLocale(locale))
      }
    }

    // ---- markup ----
    def sb = new StringBuilder()
    sb << "<div class=\"dp-field${cssClass}${disabled ? ' dp-disabled' : ''}\" id=\"${id}\">\n"
    if (label) {
      sb << "<span class=\"dp-label\" id=\"${id}-label\">${label.toString().encodeAsHTML()}${required ? ' *' : ''}</span>\n"
    }
    sb << "<button type=\"button\" class=\"dp-trigger${glass ? ' dp-glass' : ''}\" aria-haspopup=\"dialog\" " +
      "${disabled ? 'disabled aria-disabled=\"true\" ' : ''}" +
      "${label ? "aria-labelledby=\"${id}-label ${id}-value\"" : ''}>\n"
    sb << "<span class=\"dp-trigger-icon\" aria-hidden=\"true\">${ICON}</span>\n"
    sb << "<span class=\"dp-trigger-value${hasValue ? '' : ' dp-placeholder'}\" id=\"${id}-value\">${triggerLabel.encodeAsHTML()}</span>\n"
    sb << "</button>\n"
    sb << "<input type=\"hidden\" name=\"${name}\" class=\"dp-input\" value=\"${isoValue}\" ${required ? 'required' : ''} ${disabled ? 'disabled' : ''}/>\n"

    sb << "<div class=\"dp-sheet\" data-mode=\"${mode}\" data-hour12=\"${hour12}\" data-locale=\"${localeTag}\" " +
      "data-date-style=\"${dateStyleName}\" data-time-style=\"${timeStyleName}\" data-minute-step=\"${minuteStep}\" " +
      "data-placeholder=\"${placeholder.encodeAsHTML()}\" hidden>\n"
    sb << "<div class=\"dp-backdrop\"></div>\n"
    sb << "<div class=\"dp-panel${glass ? ' dp-glass' : ''} dp-tint-${tint}\" role=\"dialog\" aria-modal=\"true\">\n"
    sb << "<div class=\"dp-grabber\"></div>\n"
    sb << "<div class=\"dp-header\">\n"
    sb << "<button type=\"button\" class=\"dp-cancel\">Cancel</button>\n"
    sb << "<button type=\"button\" class=\"dp-today\">${mode == 'time' ? 'Now' : 'Today'}</button>\n"
    sb << "<button type=\"button\" class=\"dp-done\">Done</button>\n"
    sb << "</div>\n"
    sb << "<div class=\"dp-wheel-wrap\">\n"
    sb << "<div class=\"dp-wheel-highlight\"></div>\n"
    sb << "<div class=\"dp-columns\">\n"

    if (mode in ['date', 'datetime']) {
      sb << buildMonthColumn(locale, d.monthValue - 1)
      sb << buildNumberColumn('day', 1, 31, d.dayOfMonth, 2, false, 1)
      sb << buildNumberColumn('year', startYear, endYear, d.year, 4, false, 1)
    }
    if (mode in ['time', 'datetime']) {
      if (hour12) {
        int h12 = t.hour % 12
        if (h12 == 0) h12 = 12
        sb << buildNumberColumn('hour', 1, 12, h12, 2, false, 1)
      } else {
        sb << buildNumberColumn('hour', 0, 23, t.hour, 2, true, 1)
      }
      sb << buildNumberColumn('minute', 0, 59, minuteVal, 2, true, minuteStep)
      if (hour12) {
        sb << buildAmPmColumn(t.hour < 12 ? 'AM' : 'PM')
      }
    }

    sb << "</div>\n</div>\n</div>\n</div>\n</div>\n"
    out << sb.toString()
  }

  private String buildMonthColumn(Locale locale, int selectedIdx) {
    def months = new DateFormatSymbols(locale).months
    def sb = new StringBuilder()
    sb << "<div class=\"dp-col\" data-role=\"month\" data-min=\"0\" data-max=\"11\" data-step=\"1\" tabindex=\"0\">\n"
    (0..11).each { i ->
      sb << "<div class=\"dp-col-item${i == selectedIdx ? ' dp-selected' : ''}\" data-value=\"${i}\">${months[i]}</div>\n"
    }
    sb << "</div>\n"
    sb.toString()
  }

  private String buildNumberColumn(String role, int min, int max, int selected, int pad, boolean zeroPad, int step) {
    def sb = new StringBuilder()
    sb << "<div class=\"dp-col\" data-role=\"${role}\" data-min=\"${min}\" data-max=\"${max}\" data-step=\"${step}\" tabindex=\"0\">\n"
    for (int v = min; v <= max; v += step) {
      def display = zeroPad ? String.format("%0${pad}d", v) : String.valueOf(v)
      sb << "<div class=\"dp-col-item${v == selected ? ' dp-selected' : ''}\" data-value=\"${v}\">${display}</div>\n"
    }
    sb << "</div>\n"
    sb.toString()
  }

  private String buildAmPmColumn(String selected) {
    def sb = new StringBuilder()
    sb << "<div class=\"dp-col dp-col-ampm\" data-role=\"ampm\" tabindex=\"0\">\n"
    ['AM', 'PM'].each { v ->
      sb << "<div class=\"dp-col-item${v == selected ? ' dp-selected' : ''}\" data-value=\"${v}\">${v}</div>\n"
    }
    sb << "</div>\n"
    sb.toString()
  }

  /* Font Awesome calendar icon (regular style). Requires the Font Awesome
     stylesheet, which is loaded once via FA_LINK below (emitted by
     <dp:resources/>). Swap the class list here to use a different FA icon
     (e.g. "fa-solid fa-calendar-days") if preferred. */
  private static final String ICON =
    '<i class="fa-regular fa-calendar"></i>'

  // Font Awesome CDN stylesheet — loaded once alongside the picker's own
  // CSS/JS via <dp:resources/>. Uses cdnjs (already an allowed/trusted CDN).
  // NOTE: no `integrity` hash is pinned here since the exact release hash
  // wasn't verified against cdnjs at generation time — add one from
  // https://cdnjs.com/libraries/font-awesome if you want subresource
  // integrity checking, or self-host Font Awesome instead.
  private static final String FA_LINK =
    '<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" referrerpolicy="no-referrer" />\n'

  // =========================================================================
  // CSS
  // =========================================================================
  private static final String CSS = FA_LINK + '''
<style data-dp-taglib="css">
:root{
  --dp-row: 44px;
  --dp-visible: 5;
  --dp-radius: 24px;
  --glass-background: rgba(255,255,255,0.14);
  --glass-background-strong: rgba(255,255,255,0.22);
  --glass-border: rgba(255,255,255,0.32);
  --glass-border-idle: transparent;
  --glass-border-focus: #ffffff;
  --glass-shadow: 0 20px 60px rgba(0,0,0,0.35);
  --glass-blur: 22px;
  --dp-text: #1c1c1e;

  /* ── Shared cross-taglib height token ────────────────────────────
     Same value as glass:textInputStyles' .input-glass, glass:radio's
     .radio-glass, and glass:select's .gsl-wrapper — keeps every glass
     form control the same height. */
  --gform-height: 50px;
}
.dp-tint-dark{
  --glass-background: rgba(18,18,24,0.55);
  --glass-background-strong: rgba(18,18,24,0.72);
  --glass-border: rgba(255,255,255,0.14);
  --glass-border-idle: transparent;
  --glass-border-focus: #ffffff;
  --glass-shadow: 0 20px 60px rgba(0,0,0,0.55);
  --dp-text: #f2f2f7;
}

.dp-field{
  display:inline-flex; flex-direction:column; gap:.35rem;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI", Roboto, Inter, sans-serif;
}
.dp-label{ font-size:.8rem; font-weight:500; opacity:.75; color:#ffffff; }

.dp-trigger{
  display:inline-flex; align-items:center; gap:.5rem;
  /* Vertical padding removed (was .65rem top/bottom) — height is now
     fixed via --gform-height and align-items:center handles vertical
     centering of the icon + label. */
  padding:0 1rem;
  height: var(--gform-height);
  box-sizing: border-box;
  /* min-width removed (was 220px) — it forced the trigger to stay 220px
     wide even when its content (icon + text) was shorter, which left
     unwanted empty space on the right since .dp-trigger-value has
     flex:1 and stretches to fill any leftover width. The trigger now
     sizes to its content, or to whatever a surrounding flex/grid row
     gives it via width/flex-basis. */
  border-radius:14px; border:1px solid var(--glass-border-idle);
  background: var(--glass-background);
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(160%);
  backdrop-filter: blur(var(--glass-blur)) saturate(160%);
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
  color: var(--dp-text);
  font-size:.95rem; cursor:pointer;
  outline: none;
  transition: transform .15s ease, box-shadow .15s ease, border-color .15s ease;
  touch-action: manipulation; -webkit-tap-highlight-color: transparent;
}
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .dp-trigger{ background: rgba(230,230,235,0.92); }
  .dp-tint-dark .dp-trigger{ background: rgba(30,30,36,0.92); }
}
.dp-trigger:hover{ box-shadow: 0 10px 26px rgba(0,0,0,0.18); }
.dp-trigger:active{ transform: scale(.98); border-color: var(--glass-border-focus); }
.dp-trigger:focus-visible{ border-color: var(--glass-border-focus); }
.dp-trigger.dp-trigger-active{ border-color: var(--glass-border-focus); }

/* ---------- disabled: non-interactive, dimmed, no hover/active/focus feedback ---------- */
.dp-trigger:disabled,
.dp-trigger[disabled]{
  cursor: not-allowed;
  opacity: .55;
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}
.dp-trigger:disabled:hover,
.dp-trigger[disabled]:hover{ box-shadow: 0 6px 20px rgba(0,0,0,0.12); }
.dp-trigger:disabled:active,
.dp-trigger[disabled]:active{ transform: none; border-color: var(--glass-border-idle); }

.dp-trigger-icon{ opacity:.7; flex-shrink:0; color:#ffffff; font-size:16px; line-height:1; display:inline-flex; align-items:center; }
.dp-trigger-value{ flex:1; text-align:left; color:#ffffff; }
.dp-trigger-value.dp-placeholder{ opacity:.55; }

/* ---------- sheet / modal ---------- */
.dp-sheet{ position:fixed; inset:0; z-index:9999; display:flex; align-items:flex-end; justify-content:center; }
.dp-sheet[hidden]{ display:none; }
.dp-backdrop{
  position:absolute; inset:0; background: rgba(6,7,12,0.45);
  -webkit-backdrop-filter: blur(6px); backdrop-filter: blur(6px);
  opacity:0; transition: opacity .28s ease;
}
.dp-sheet.is-open .dp-backdrop{ opacity:1; }

.dp-panel{
  position:relative; width:100%; max-width:420px;
  border-radius: var(--dp-radius) var(--dp-radius) 0 0;
  padding:.5rem 1rem 1.25rem;
  background: var(--glass-background-strong);
  border:1px solid var(--glass-border-idle); border-bottom:none;
  -webkit-backdrop-filter: blur(var(--glass-blur)) saturate(180%);
  backdrop-filter: blur(var(--glass-blur)) saturate(180%);
  box-shadow: var(--glass-shadow);
  color: var(--dp-text);
  outline: none;
  transform: translateY(100%);
  transition: transform .32s cubic-bezier(.16,1,.3,1), border-color .15s ease;
  touch-action: pan-y;
}
@supports not ((backdrop-filter: blur(1px)) or (-webkit-backdrop-filter: blur(1px))) {
  .dp-panel{ background: rgba(245,245,250,0.98); }
  .dp-tint-dark .dp-panel{ background: rgba(24,24,30,0.98); }
}
.dp-sheet.is-open .dp-panel{ transform: translateY(0); }
.dp-sheet.is-open .dp-panel, .dp-panel:focus-within, .dp-panel:focus-visible{ border-color: var(--glass-border-focus); }
.dp-grabber{ width:36px; height:4px; border-radius:2px; background: rgba(120,120,128,0.4); margin:.5rem auto .25rem; }

.dp-header{ display:flex; align-items:center; justify-content:space-between; padding:.4rem .1rem .75rem; }
.dp-header button{
  background:none; border:1px solid var(--glass-border-idle); border-radius:8px;
  font-size:.95rem; padding:.4rem .5rem; cursor:pointer;
  color: var(--dp-text); outline:none;
  touch-action: manipulation; font-family:inherit;
  transition: border-color .15s ease;
}
.dp-header button:focus-visible, .dp-header button:active{ border-color: var(--glass-border-focus); }
.dp-cancel{ opacity:.7; }
.dp-done{ font-weight:600; color:#0a84ff; }
.dp-tint-dark .dp-done{ color:#409cff; }
.dp-today{ font-size:.82rem; opacity:.65; }

/* ---------- wheel ---------- */
.dp-wheel-wrap{ position:relative; }
.dp-wheel-highlight{
  position:absolute; left:0; right:0; top:50%; transform: translateY(-50%);
  height: var(--dp-row); pointer-events:none;
  border-top:1px solid var(--glass-border-idle); border-bottom:1px solid var(--glass-border-idle);
  background: rgba(255,255,255,0.06); border-radius:10px;
  transition: border-color .15s ease;
}
.dp-wheel-wrap:focus-within .dp-wheel-highlight{
  border-top-color: var(--glass-border-focus); border-bottom-color: var(--glass-border-focus);
}
.dp-columns{
  display:flex; overflow-x:auto; -webkit-overflow-scrolling:touch;
  perspective: 900px; scrollbar-width:none; -ms-overflow-style:none;
}
.dp-columns::-webkit-scrollbar{ display:none; }

.dp-col{
  flex:1 1 0; min-width:64px;
  height: calc(var(--dp-row) * var(--dp-visible));
  box-sizing:border-box;
  padding: calc(var(--dp-row) * 2) 0;
  overflow-y:auto; overflow-x:hidden;
  scroll-snap-type: y mandatory;
  scrollbar-width:none; -ms-overflow-style:none;
  touch-action: pan-y; -webkit-overflow-scrolling:touch;
  border:1px solid var(--glass-border-idle); border-radius:10px;
  outline:none;
  transition: border-color .15s ease;
}
.dp-col::-webkit-scrollbar{ display:none; }
.dp-col:active{ border-color: var(--glass-border-focus); }
.dp-col.dp-col-focused{ border-color: var(--glass-border-focus); transition: border-color .15s ease; }
.dp-col.dp-col-faded{ border-color: var(--glass-border-idle); transition: border-color .8s ease; }
/* Chromium/WebKit can "capture" a programmatic smooth scrollTo() on a
   scroll-snap element and stop it at the next snap point instead of
   letting it travel all the way to the requested offset. This is barely
   noticeable on short columns (month/day/hour/minute/ampm) but is very
   visible on the year column, which can be 100+ rows tall — clicking a
   year far from the current scroll position would appear to do nothing.
   smoothScrollColumn() (JS) toggles this class on for the duration of a
   programmatic scroll so scroll-snap can't intercept it, then removes it
   once the scroll settles. */
.dp-col.dp-col-no-snap{ scroll-snap-type: none; }
.dp-col[data-role="month"]{ min-width:128px; flex-grow:1.6; }
.dp-col[data-role="year"]{ min-width:84px; }
.dp-col-ampm{ min-width:60px; flex-grow:.6; }

.dp-col-item{
  height: var(--dp-row); line-height: var(--dp-row);
  text-align:center; font-size:1.05rem; color: var(--dp-text);
  scroll-snap-align:center; user-select:none; cursor:pointer;
  white-space:nowrap; overflow:hidden; text-overflow:ellipsis;
  transform-origin:center center;
  border:1px solid var(--glass-border-idle); border-radius:8px;
  box-sizing:border-box;
  transition: border-color .15s ease;
}
.dp-col-item.dp-selected{ font-weight:600; }
.dp-col-item:active{ border-color: var(--glass-border-focus); }

@media (prefers-reduced-motion: reduce){
  .dp-panel, .dp-backdrop{ transition: opacity .15s linear !important; }
}

/* ---------- responsive: centered modal on larger / non-touch screens ---------- */
@media (min-width:640px){
  .dp-sheet{ align-items:center; }
  .dp-panel{
    border-radius: var(--dp-radius); border-bottom:1px solid var(--glass-border-idle);
    transform: translateY(16px) scale(.96); opacity:0;
    transition: transform .28s cubic-bezier(.16,1,.3,1), opacity .28s ease, border-color .15s ease;
  }
  .dp-sheet.is-open .dp-panel{ transform: translateY(0) scale(1); opacity:1; }
  .dp-grabber{ display:none; }
}
@media (max-width:380px){
  :root{ --dp-row: 40px; }
  .dp-trigger{ width:100%; }
}
@media (pointer: coarse){
  .dp-col-item{ font-size:1.1rem; }
}
</style>
'''

  // =========================================================================
  // JS
  // =========================================================================
  private static final String JS = '''
<script data-dp-taglib="js">
(function(){
  "use strict";
  var ROW = 44, VISIBLE = 5;

  function ready(fn){
    if(document.readyState !== "loading"){ fn(); }
    else { document.addEventListener("DOMContentLoaded", fn); }
  }

  function daysInMonth(year, monthIndex){ return new Date(year, monthIndex + 1, 0).getDate(); }
  function pad(n, len){ var s = String(n); while(s.length < len) s = "0" + s; return s; }

  // Chromium/WebKit have a long-standing bug where scrollTo({behavior:"smooth"})
  // on an element with `scroll-snap-type` set can silently stop at the next
  // snap point instead of travelling all the way to the requested target.
  // Barely noticeable on the short month/day/hour/minute/ampm columns, but
  // the year column can be 100+ rows tall, so clicking a year far from the
  // current scroll position visibly failed to scroll there. Toggling snap
  // off for the duration of the programmatic scroll (via the
  // .dp-col-no-snap CSS class) works around it; restored on scrollend, with
  // a timeout fallback for browsers that don't support that event.
  function smoothScrollColumn(col, top){
    col.classList.add("dp-col-no-snap");
    function cleanup(){
      col.classList.remove("dp-col-no-snap");
      col.removeEventListener("scrollend", cleanup);
    }
    col.addEventListener("scrollend", cleanup);
    setTimeout(cleanup, 450); // fallback for browsers without scrollend
    col.scrollTo({ top: top, behavior: "smooth" });
  }

  function bindItemClicks(col){
    Array.prototype.slice.call(col.querySelectorAll(".dp-col-item")).forEach(function(item, i){
      item.onclick = function(){ smoothScrollColumn(col, i * ROW); };
    });
  }

  function buildItems(col, values, formatFn, selectedValue){
    col.innerHTML = "";
    values.forEach(function(v){
      var div = document.createElement("div");
      div.className = "dp-col-item" + (v === selectedValue ? " dp-selected" : "");
      div.setAttribute("data-value", String(v));
      div.textContent = formatFn(v);
      col.appendChild(div);
    });
    bindItemClicks(col);
  }

  function initColumn(col, onSettle){
    var raf = null, settleTimer = null, lastIndex = -1;

    function items(){ return Array.prototype.slice.call(col.querySelectorAll(".dp-col-item")); }

    function applyCurve(){
      var list = items();
      var centerY = col.scrollTop + col.clientHeight / 2;
      list.forEach(function(item){
        var itemCenter = item.offsetTop + ROW / 2;
        var offset = (itemCenter - centerY) / ROW;
        var clamped = Math.max(Math.min(offset, 3), -3);
        item.style.transform = "rotateX(" + (clamped * -22) + "deg) translateZ(" + (-Math.abs(clamped) * 14) + "px)";
        item.style.opacity = String(Math.max(1 - Math.abs(clamped) * 0.32, 0.08));
      });
    }

    function currentIndex(){
      var list = items();
      return Math.max(0, Math.min(Math.round(col.scrollTop / ROW), list.length - 1));
    }

    function settle(){
      var list = items();
      if(!list.length) return;
      var idx = currentIndex();
      var target = idx * ROW;
      if(Math.abs(col.scrollTop - target) > 1){
        smoothScrollColumn(col, target);
      }
      if(idx !== lastIndex){
        lastIndex = idx;
        if(navigator.vibrate){ try{ navigator.vibrate(4); }catch(e){} }
        onSettle(list[idx]);
      }
    }

    col.addEventListener("scroll", function(){
      if(raf) return;
      raf = requestAnimationFrame(function(){ applyCurve(); raf = null; });
      clearTimeout(settleTimer);
      settleTimer = setTimeout(settle, 130);
    }, { passive: true });
    col.addEventListener("scrollend", function(){ clearTimeout(settleTimer); settle(); });

    bindItemClicks(col);
    applyCurve();

    var fadeTimer = null;
    var FOCUS_FADE_DELAY = 4000;
    col.addEventListener("focus", function(){
      clearTimeout(fadeTimer);
      col.classList.remove("dp-col-faded");
      col.classList.add("dp-col-focused");
      fadeTimer = setTimeout(function(){
        col.classList.add("dp-col-faded");
      }, FOCUS_FADE_DELAY);
    });
    col.addEventListener("blur", function(){
      clearTimeout(fadeTimer);
      col.classList.remove("dp-col-focused", "dp-col-faded");
    });

    function scrollToValue(value, animate){
      var list = items();
      var idx = list.findIndex(function(it){ return it.getAttribute("data-value") === String(value); });
      if(idx < 0) idx = 0;
      if(animate){
        smoothScrollColumn(col, idx * ROW);
      } else {
        col.scrollTo({ top: idx * ROW, behavior: "auto" });
      }
      lastIndex = idx;
    }

    return { settle: settle, scrollToValue: scrollToValue, applyCurve: applyCurve, items: items };
  }

  function initPicker(field){
    if(field.dataset.dpInit) return;
    field.dataset.dpInit = "1";

    var trigger = field.querySelector(".dp-trigger");
    var hiddenInput = field.querySelector(".dp-input");
    var valueLabel = field.querySelector(".dp-trigger-value");
    var sheet = field.querySelector(".dp-sheet");
    document.body.appendChild(sheet); // avoid ancestor transform/overflow issues with position:fixed

    var mode = sheet.dataset.mode;
    var hour12 = sheet.dataset.hour12 === "true";
    var locale = sheet.dataset.locale || "en-US";
    var dateStyle = sheet.dataset.dateStyle || "medium";
    var timeStyle = sheet.dataset.timeStyle || "short";
    var minuteStep = parseInt(sheet.dataset.minuteStep, 10) || 1;

    var panel = sheet.querySelector(".dp-panel");
    var backdrop = sheet.querySelector(".dp-backdrop");
    var grabber = sheet.querySelector(".dp-grabber");

    var monthCol = sheet.querySelector('[data-role="month"]');
    var dayCol = sheet.querySelector('[data-role="day"]');
    var yearCol = sheet.querySelector('[data-role="year"]');
    var hourCol = sheet.querySelector('[data-role="hour"]');
    var minuteCol = sheet.querySelector('[data-role="minute"]');
    var ampmCol = sheet.querySelector('[data-role="ampm"]');

    var now = new Date();
    var state = {
      year: now.getFullYear(), month: now.getMonth(), day: now.getDate(),
      hour: now.getHours(), minute: 0, ampm: "AM", hourRaw: 0
    };

    (function parseIso(){
      var v = hiddenInput.value;
      if(!v) return;
      if(mode === "time"){
        var tp = v.split(":");
        state.hour = parseInt(tp[0], 10); state.minute = parseInt(tp[1], 10);
      } else if(mode === "datetime"){
        var parts = v.split("T");
        var dp = parts[0].split("-");
        var tp2 = (parts[1] || "00:00").split(":");
        state.year = parseInt(dp[0], 10); state.month = parseInt(dp[1], 10) - 1; state.day = parseInt(dp[2], 10);
        state.hour = parseInt(tp2[0], 10); state.minute = parseInt(tp2[1], 10);
      } else {
        // date mode: value may be "yyyy-MM-dd" or "yyyy-MM-ddT00:00"
        var dp2 = v.split("T")[0].split("-");
        state.year = parseInt(dp2[0], 10); state.month = parseInt(dp2[1], 10) - 1; state.day = parseInt(dp2[2], 10);
      }
      state.ampm = state.hour < 12 ? "AM" : "PM";
    })();
    state.hourRaw = hour12 ? (state.hour % 12 === 0 ? 12 : state.hour % 12) : state.hour;

    var committed = JSON.parse(JSON.stringify(state));
    var wheels = {};

    function rebuildDays(){
      if(!dayCol) return;
      var max = daysInMonth(state.year, state.month);
      var selected = Math.min(state.day, max);
      var days = []; for(var i = 1; i <= max; i++) days.push(i);
      buildItems(dayCol, days, function(v){ return String(v); }, selected);
      state.day = selected;
      if(wheels.day){
        wheels.day.scrollToValue(selected, false);
        // buildItems() just replaced every .dp-col-item in the day column
        // with fresh nodes that carry none of the rotateX/opacity curve
        // (only applyCurve() ever writes those inline styles), and
        // scrollToValue() won't reliably fire a native "scroll" event of
        // its own (e.g. when the selected day lands on the same scrollTop
        // offset as before), so the scroll listener's applyCurve() call
        // may never rerun on its own. Reapply it explicitly here so the
        // day column doesn't flatten out into a plain list whenever the
        // month or year wheel settles.
        wheels.day.applyCurve();
      }
    }

    function syncHour(){
      if(!hour12){ state.hour = state.hourRaw; return; }
      var h = state.hourRaw % 12;
      if(state.ampm === "PM") h += 12;
      state.hour = h;
    }

    if(monthCol) wheels.month = initColumn(monthCol, function(item){ state.month = parseInt(item.dataset.value, 10); rebuildDays(); });
    if(yearCol) wheels.year = initColumn(yearCol, function(item){ state.year = parseInt(item.dataset.value, 10); rebuildDays(); });
    if(dayCol) wheels.day = initColumn(dayCol, function(item){ state.day = parseInt(item.dataset.value, 10); });
    if(hourCol) wheels.hour = initColumn(hourCol, function(item){ state.hourRaw = parseInt(item.dataset.value, 10); syncHour(); });
    if(minuteCol) wheels.minute = initColumn(minuteCol, function(item){ state.minute = parseInt(item.dataset.value, 10); });
    if(ampmCol) wheels.ampm = initColumn(ampmCol, function(item){ state.ampm = item.dataset.value; syncHour(); });

    if(dayCol) rebuildDays(); // correct day count for the actual initial month/year

    [monthCol, dayCol, yearCol, hourCol, minuteCol, ampmCol].forEach(function(col){
      if(!col) return;
      col.addEventListener("keydown", function(e){
        if(e.key === "Enter"){
          e.preventDefault();
          doneAndClose();
        }
      });
    });

    function applyStateToWheels(animate){
      if(wheels.month) wheels.month.scrollToValue(state.month, animate);
      if(wheels.year) wheels.year.scrollToValue(state.year, animate);
      rebuildDays();
      if(wheels.hour) wheels.hour.scrollToValue(state.hourRaw, animate);
      if(wheels.minute) wheels.minute.scrollToValue(state.minute - (state.minute % minuteStep), animate);
      if(wheels.ampm) wheels.ampm.scrollToValue(state.ampm, animate);
    }

    function isoFromState(){
      var mm = pad(state.month + 1, 2), dd = pad(state.day, 2);
      var HH = pad(state.hour, 2), MM = pad(state.minute, 2);
      if(mode === "time") return HH + ":" + MM;
      if(mode === "datetime") return state.year + "-" + mm + "-" + dd + "T" + HH + ":" + MM;
      // date mode: append midnight so the value is LocalDateTime-parseable
      return state.year + "-" + mm + "-" + dd + "T00:00";
    }

    function formattedLabel(){
      var dateObj = new Date(state.year, state.month, state.day, state.hour, state.minute);
      var opts;
      if(mode === "time"){ opts = { timeStyle: timeStyle }; }
      else if(mode === "datetime"){ opts = { dateStyle: dateStyle, timeStyle: timeStyle }; }
      else { opts = { dateStyle: dateStyle }; }
      try{ return new Intl.DateTimeFormat(locale, opts).format(dateObj); }
      catch(e){ return dateObj.toLocaleString(); }
    }

    function commit(){
      committed = JSON.parse(JSON.stringify(state));
      hiddenInput.value = isoFromState();
      valueLabel.textContent = formattedLabel();
      valueLabel.classList.remove("dp-placeholder");
      hiddenInput.dispatchEvent(new Event("change", { bubbles: true }));
    }

    function onKey(e){ if(e.key === "Escape") cancelAndClose(); }

    function focusDefaultColumn(){
      var target = (mode === "time") ? hourCol : monthCol;
      if(!target) return;
      try{ target.focus({ preventScroll: true }); }
      catch(e){ target.focus(); }
    }

    function open(){
      // A disabled picker must never open (also covers a trigger that was
      // disabled/re-enabled at runtime after init).
      if(trigger.disabled) return;
      sheet.removeAttribute("hidden");
      // FIX: position the wheels only AFTER the sheet is un-hidden. While
      // [hidden] (display:none) the columns have no layout box, so
      // scrollTo()/scrollTop assignments are silently ignored — which is
      // why the first open always showed the wheels at the top (January /
      // 1 / first year / 1 / 00 / AM) instead of the initial value. Reading
      // layout here forces a synchronous reflow, so the scrollTo() calls
      // inside applyStateToWheels() take effect before the first paint.
      applyStateToWheels(false);
      Object.keys(wheels).forEach(function(k){ wheels[k].applyCurve(); });
      requestAnimationFrame(function(){
        requestAnimationFrame(function(){
          sheet.classList.add("is-open");
          Object.keys(wheels).forEach(function(k){ wheels[k].applyCurve(); });
          focusDefaultColumn();
        });
      });
      document.addEventListener("keydown", onKey);
      trigger.classList.add("dp-trigger-active");
    }
    function close(){
      sheet.classList.remove("is-open");
      document.removeEventListener("keydown", onKey);
      trigger.classList.remove("dp-trigger-active");
      setTimeout(function(){ sheet.setAttribute("hidden", ""); }, 320);
    }
    function cancelAndClose(){
      state = JSON.parse(JSON.stringify(committed));
      applyStateToWheels(false);
      close();
    }
    function doneAndClose(){ commit(); close(); }

    trigger.addEventListener("click", function(){
      if(trigger.disabled) return;
      // Reset to the committed value; the wheels themselves are positioned
      // inside open(), once the sheet is actually laid out.
      state = JSON.parse(JSON.stringify(committed));
      open();
    });

    // Header buttons (Cancel/Today/Done) sit outside .dp-wheel-wrap, and a
    // <button> normally grabs focus on mousedown — before any "click"
    // handler runs. That means by the time Today's click handler reads
    // document.activeElement, it's already the button itself, not the
    // wheel column that had focus a moment ago; "restoring" that captured
    // element then leaves focus on the button instead of a column, so
    // .dp-wheel-wrap never regains :focus-within and the highlight
    // border's border-top/border-bottom stay transparent for good.
    // Blocking the mousedown default keeps focus on whichever column was
    // active, so Today's focus save/restore logic below works correctly.
    sheet.querySelectorAll(".dp-header button").forEach(function(btn){
      btn.addEventListener("mousedown", function(e){ e.preventDefault(); });
    });

    sheet.querySelector(".dp-cancel").addEventListener("click", cancelAndClose);
    sheet.querySelector(".dp-done").addEventListener("click", doneAndClose);
    sheet.querySelector(".dp-today").addEventListener("click", function(){
      // remember what currently has focus so we can restore it after the
      // wheels re-render (Today/Now shouldn't steal focus away from the
      // control the user was just interacting with)
      var focused = document.activeElement;
      var wasInDayCol = !!(focused && dayCol && dayCol !== focused && dayCol.contains(focused));

      var n = new Date();
      state.year = n.getFullYear(); state.month = n.getMonth(); state.day = n.getDate();
      state.hour = n.getHours(); state.minute = n.getMinutes() - (n.getMinutes() % minuteStep);
      state.ampm = state.hour < 12 ? "AM" : "PM";
      state.hourRaw = hour12 ? (state.hour % 12 === 0 ? 12 : state.hour % 12) : state.hour;
      applyStateToWheels(true);

      // restore focus: prefer the exact element if it survived the update;
      // otherwise fall back to its containing column, since rebuildDays()
      // replaces the day column's item elements via innerHTML
      var restoreTarget = (focused && document.body.contains(focused))
        ? focused
        : (wasInDayCol ? dayCol : null);
      if(restoreTarget){
        try{ restoreTarget.focus({ preventScroll: true }); }
        catch(e){ restoreTarget.focus(); }
      }
    });
    backdrop.addEventListener("click", cancelAndClose);

    // swipe-down on the grab handle to dismiss (touch)
    var startY = null;
    grabber.addEventListener("touchstart", function(e){ startY = e.touches[0].clientY; }, { passive: true });
    grabber.addEventListener("touchmove", function(e){
      if(startY === null) return;
      var dy = e.touches[0].clientY - startY;
      if(dy > 0) panel.style.transform = "translateY(" + dy + "px)";
    }, { passive: true });
    grabber.addEventListener("touchend", function(e){
      var dy = startY !== null ? (e.changedTouches[0].clientY - startY) : 0;
      panel.style.transform = "";
      if(dy > 70){ cancelAndClose(); }
      startY = null;
    });
  }

  function scan(root){ (root || document).querySelectorAll(".dp-field").forEach(initPicker); }

  ready(function(){ scan(document); });

  if("MutationObserver" in window){
    var mo = new MutationObserver(function(mutations){
      mutations.forEach(function(m){
        m.addedNodes && m.addedNodes.forEach(function(node){
          if(node.nodeType === 1){
            if(node.classList && node.classList.contains("dp-field")) initPicker(node);
            if(node.querySelectorAll) scan(node);
          }
        });
      });
    });
    mo.observe(document.documentElement, { childList: true, subtree: true });
  }

  window.DatePickerTaglib = { scan: scan, init: initPicker };
})();
</script>
'''
}
