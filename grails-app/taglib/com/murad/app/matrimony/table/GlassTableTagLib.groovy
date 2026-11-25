package com.murad.app.matrimony.table

import com.murad.app.matrimony.GlassTooltipStylesTagLib
import grails.gsp.TagLib
import groovy.json.JsonOutput
import org.springframework.http.HttpStatus

/**
 * GlassTableTagLib  —  Cosmic glassmorphic table for Grails 6
 * Namespace : glass
 *
 * CHANGES in this version
 *   - NEW: when the table runs out of rows (after a successful row click removed the last one
 *     and no further rows can be loaded) and the headerHtml contains a glass:select, the
 *     currently selected option is removed from that select, the next option is selected and a
 *     'change' event is fired on the select (so the page reloads the table for the next option).
 *       * ListTableTagLib fires gt:noMoreRows once the refill fetch returned nothing.
 *       * A plain glass:table (no loader) prunes directly from gt:empty.
 *       * Hooks / events:  table.gtPruneHeaderSelect()   gt:noMoreRows   gt:headerOptionRemoved
 *                          (detail: {value, label})
 *   - NEW attrs headerHtml / headerAlign: free-form HTML rendered in its own row directly
 *     BENEATH the caption/search toolbar (same idea as glass:modernGlassCard's headerHtml).
 *   - Row click is now an AJAX call (fetch, JSON request body, JSON response) instead of a
 *     page navigation. Set rowAjax="false" to get the old navigation behaviour.
 *   - The HTTP status of the response drives the feedback in the clicked row AND a glass alert:
 *         HttpStatus.OK                    -> animated tick       + success alert, then the row is
 *                                             removed from the table (rowRemove="false" keeps it)
 *         HttpStatus.NOT_FOUND             -> animated error (X)  + error alert
 *         HttpStatus.CONFLICT              -> animated error (X)  + error alert
 *         HttpStatus.INTERNAL_SERVER_ERROR -> animated error (X)  + error alert
 *         HttpStatus.UNPROCESSABLE_ENTITY  -> animated warning    + warning alert
 *         anything else / network failure  -> animated error (X)  + error alert
 *     Error / warning icons are removed again after ~2.6s so the row can be retried.
 *   - After a row is removed the table fires two DOM events on the <table>:
 *         gt:rowRemoved   detail: {id, response}
 *         gt:empty        when no <tr> is left in the body (ListTableTagLib listens to it
 *                         and fetches the next page of rows)
 *   - The server answers with JSON; the alert text is taken from  message | error | errors[].
 *   - Alerts come from GlassAlertTagLib (window.glassAlert). This tag emits the alert runtime
 *     once per request via <glass:alertRuntime/>.
 *   - NEW attrs: rowAjax, rowMethod, rowBody, rowRemove, removeDelay.
 *
 * ── Tags ─────────────────────────────────────────────────────────────────────
 *
 *  <glass:tableStyles/>
 *    Emit global CSS. Call once in <head>.
 *
 *  <glass:table
 *      cols             = "Col1, Col2, …"      REQUIRED — comma-separated headers
 *      [id] [caption] [captionStyle] [searchable] [rowCount] [emptyText]
 *
 *      ── Header row (beneath the caption / search toolbar) ─────────────────
 *      [headerHtml]                             Raw HTML (toggles, filter buttons, chips, ...).
 *                                               Compose it with <g:set> and pass the variable,
 *                                               exactly like glass:modernGlassCard's headerHtml.
 *                                               Emitted UNESCAPED - never put raw user input in it.
 *      [headerAlign     = "start"]              start | center | end | between
 *
 *      [headerData]                             Map of data shown IN the header HTML elements. Mark an
 *                                               element with data-gt-bind="key" (dotted paths ok, e.g.
 *                                               "user.name") and it is filled client-side:
 *                                                 <select>            list -> <option>s (items: "text" or
 *                                                                     {value,label[,selected]}); scalar -> value
 *                                                 checkbox / radio    checked state
 *                                                 <input>/<textarea>  value
 *                                                 <ul>/<ol>/other     list -> <li> / chip <span>s
 *                                                 anything else       textContent (always escaped)
 *                                               data-gt-bind-attr="title:tipKey,placeholder:phKey" binds
 *                                               attributes. Options marked data-gt-static are kept when a
 *                                               <select> is rebuilt. Use plain Maps / Lists / scalars.
 *                                               A header reload response that is a JSON OBJECT with a
 *                                               "headerData" key updates the bound elements too (only the
 *                                               keys sent; inputs the user is typing in stay untouched).
 *                                               Hooks: table.gtSetHeaderData(map)  table.gtHeaderData
 *
 *      ── Header-driven data loading (AJAX, JSON in / JSON out) ─────────────
 *      [headerController] [headerAction]        slot-aware endpoint, built with createLink:
 *                                               /u/$slot/$controller/$action?queryparams  (currentSlot set)
 *                                               /$controller/$action?queryparams          (no currentSlot)
 *      [headerParams]                           Map of static params -> query string of that URL
 *      [headerMethod    = "POST"]               POST | PUT | PATCH | GET. POST/PUT/PATCH send the control
 *                                               values as a JSON body; GET appends them to the query string.
 *      [headerLoad      = "false"]              true -> also load once when the page is ready
 *      The values of every named control (input / select / textarea, or any element with
 *      data-gt-param="name") inside the header row are collected and sent. They are sent
 *      automatically when a control changes (text inputs: debounced). Skip a control with
 *      data-gt-no-reload. A plain button / link / chip can also trigger a reload by carrying
 *      data-gt-param="name" data-gt-value="x" (it gets class gt-active, siblings with the same
 *      name lose it) or just data-gt-reload.
 *      Every header-triggered reload ALWAYS sends offset=0 (paging restarts from the first page)
 *      and fires gt:headerReload on the table before the request. The response must be a JSON array of items (or an object containing
 *      one); the body rows are REPLACED using `fields`, `idField` and the badge attributes.
 *      Client hooks:  table.gtReload(extraParams)  table.gtHeaderValues()  table.gtReplaceData(items)
 *      DOM event:     gt:dataLoaded  detail: {items, params}
 *
 *      ── First-load data ───────────────────────────────────────────────────
 *      [data] [fields] [idField="id"] [badgeText] [badgeVariant] [badgeDot]
 *
 *      ── Row-click (AJAX) ──────────────────────────────────────────────────
 *      [rowController] [rowAction]              slot-aware endpoint
 *      [rowParams]                              static params -> query string
 *      [rowMethod       = "POST"]               POST | PUT | PATCH | DELETE | GET
 *                                               (GET sends ?id=…, others send a JSON body)
 *      [rowBody]                                Map of extra static JSON body fields
 *      [rowIdAttr       = "data-row-id"]        attribute on <tr>; its value is sent as "id"
 *      [rowTickCol]                             0-based column replaced by the status icon
 *                                               (default: last column = -1)
 *      [rowAjax         = "true|false"]         false -> legacy navigation to ...?id=…
 *      [rowRemove       = "true|false"]         remove the row after HTTP 200 (default true)
 *      [removeDelay     = "950"]                ms the tick stays visible before removal
 *
 *      ── Toolbar nav buttons ───────────────────────────────────────────────
 *      [homeController] [homeAction] [homeParams] [homeIcon] [homeTooltip]
 *      [backController] [backAction] [backParams] [backIcon] [backTooltip]
 *  >
 *    <tr data-row-id="${item.id}">…</tr>
 *  </glass:table>
 *
 *  <glass:tableSearch tableId="…" [placeholder]/>
 *  <glass:tableBadge text="…" [variant] [dot]/>
 *  <glass:tableActions>…</glass:tableActions>
 *  <glass:tableActionBtn icon controller action params href onclick variant title/>
 *
 * ── Slot-aware URLs ──────────────────────────────────────────────────────────
 *   currentSlot request attribute present ->  /u/$slot/$controller/$action?queryparams
 *   currentSlot absent                    ->  /$controller/$action?queryparams
 *   (context path preserved). Used for home, back, action buttons, the row AJAX endpoint
 *   and (ListTableTagLib) the load-more endpoint.
 *
 * ── Background expected on the layout body ───────────────────────────────────
 *   radial gradients #3a6fff / #a855f7 / #06b6d4 / #ec4899 over #0d1230
 */
@TagLib
@SuppressWarnings(['unused'])
class GlassTableTagLib {

  static namespace = 'glass'

  /** status code -> feedback kind (success = tick, error = X icon, warning = triangle icon). */
  private static final Map<Integer, String> STATUS_KIND = [
    (HttpStatus.OK.value())                   : 'success',
    (HttpStatus.NOT_FOUND.value())            : 'error',
    (HttpStatus.CONFLICT.value())             : 'error',
    (HttpStatus.INTERNAL_SERVER_ERROR.value()): 'error',
    (HttpStatus.UNPROCESSABLE_ENTITY.value()) : 'warning'
  ]

  /** Fallback alert text when the JSON response carries none. */
  private static final Map<Integer, String> STATUS_MESSAGE = [
    (HttpStatus.OK.value())                   : 'Done.',
    (HttpStatus.NOT_FOUND.value())            : 'The record could not be found.',
    (HttpStatus.CONFLICT.value())             : 'The record was changed or already exists.',
    (HttpStatus.INTERNAL_SERVER_ERROR.value()): 'Something went wrong on the server.',
    (HttpStatus.UNPROCESSABLE_ENTITY.value()) : 'Please check the submitted data.'
  ]

  private static final List<String> ROW_METHODS = ['POST', 'PUT', 'PATCH', 'DELETE', 'GET']

  private static final List<String> HEADER_METHODS = ['POST', 'PUT', 'PATCH', 'GET']

  private static final List<String> HEADER_ALIGNS = ['start', 'center', 'end', 'between']

  // ── Tooltip-styles delegate ───────────────────────────────────────────────
  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  // ── Slot-aware link building ─────────────────────────────────────────────

  /** Request attribute "currentSlot" as a trimmed String, or null when absent/blank. */
  private String resolveCurrentSlot() {
    String slot = request?.getAttribute('currentSlot')?.toString()?.trim()
    return slot ?: null
  }

  private static String encodeSegment(String value) {
    return URLEncoder.encode(value, 'UTF-8').replace('+', '%20')
  }

  /** JSON-encode a value for safe inlining inside <script>. */
  protected static String jsLiteral(Object value) {
    return JsonOutput.toJson(value).replace('</', '<\\/')
  }

  /**
   * Inserts /u/{slot} right after the context path of an app-relative URL.
   * Untouched: no slot, '#', absolute/external, protocol-relative, javascript:/mailto:,
   * and URLs already scoped to a slot.
   */
  String applySlot(String url) {
    String slot = resolveCurrentSlot()
    if (!slot || !url || !url.startsWith('/') || url.startsWith('//')) {
      return url
    }
    String ctx = request?.contextPath ?: ''
    String path = (ctx && url.startsWith(ctx)) ? url.substring(ctx.length()) : url
    String prefix = '/u/' + encodeSegment(slot)
    if (path == prefix || path.startsWith(prefix + '/') || path.startsWith(prefix + '?')) {
      return url   // already slot-scoped
    }
    return ctx + prefix + path
  }

  /**
   * createLink-based link builder: /{controller}/{action}?params, then /u/{slot} is added
   * when currentSlot is available.
   */
  String buildLink(Map linkAttrs) {
    try {
      return applySlot(g.createLink(linkAttrs).toString())
    } catch (ignored) {
      return '#'
    }
  }

  // ── First-load data rows ─────────────────────────────────────────────────

  Object readProp(Object item, String name) {
    if (item == null) {
      return null
    }
    if (item instanceof Map) {
      return ((Map) item).get(name)
    }
    if (item instanceof CharSequence || item instanceof Number || item instanceof Boolean) {
      return item
    }
    try {
      return item."${name}"
    } catch (MissingPropertyException ignored) {
      return null
    }
  }

  String buildBadgeHtml(Map attrs) {
    if (!attrs.badgeText) {
      return ''
    }
    String variant = (attrs.badgeVariant ?: 'violet').toString().encodeAsHTML()
    String dot = attrs.badgeDot?.toString() != 'false' ? '<span class="gt-badge-dot"></span>' : ''
    return '<span class="gt-badge gt-badge-' + variant + '">' + dot +
      attrs.badgeText.toString().encodeAsHTML() + '</span>'
  }

  String renderDataRows(Map attrs) {
    def data = attrs.data
    if (data == null) {
      return ''
    }
    Collection items = null
    if (data instanceof Collection) {
      items = (Collection) data
    } else if (data instanceof Object[]) {
      items = Arrays.asList((Object[]) data)
    }
    if (items == null) {
      return '<!-- glass:table: "data" must be a List -->'
    }
    List<String> fields = (attrs.fields ?: '').toString().split(',').collect { it.trim() }.findAll { it }
    if (!fields) {
      return '<!-- glass:table: "fields" is required when "data" is used -->'
    }
    String idField = (attrs.idField ?: 'id').toString()
    String rowIdAttr = (attrs.rowIdAttr ?: 'data-row-id').toString().encodeAsHTML()
    String badge = buildBadgeHtml(attrs)

    StringBuilder sb = new StringBuilder()
    items.each { item ->
      Object id = readProp(item, idField)
      sb << '<tr ' << rowIdAttr << '="' << (id == null ? '' : id.toString().encodeAsHTML()) << '">'
      fields.each { f ->
        Object v = readProp(item, f)
        sb << '<td>' << (v == null ? '' : v.toString().encodeAsHTML()) << '</td>'
      }
      if (badge) {
        sb << '<td>' << badge << '</td>'
      }
      sb << '</tr>'
    }
    return sb.toString()
  }

  // ── Styles ───────────────────────────────────────────────────────────────

  def tableStyles = { attrs ->
    out << '''<style>
  /* ── Glass Table — design tokens ─────────────────────────── */
  :root {
    --gt-background:        rgba(58,111,255,0.07);
    --gt-border:    rgba(167,139,250,0.18);
    --gt-shine:     rgba(103,232,249,0.28);
    --gt-head-background:   rgba(168,85,247,0.10);
    --gt-row-hover: rgba(96,165,250,0.11);
    --gt-stripe:    rgba(167,139,250,0.04);
    --gt-blur:      24px;
    --gt-radius:    20px;
    --gt-text:      rgba(232,238,255,0.92);
    --gt-muted:     rgba(160,180,255,0.45);
    --gt-head-text: rgba(160,180,255,0.65);
    --gt-violet:    #a78bfa;
    --gt-blue:      #60a5fa;
    --gt-pink:      #f472b6;
    --gt-cyan:      #22d3ee;
    --gt-emerald:   #34d399;
    --gt-amber:     #fbbf24;
  }
  .gt-wrapper {
    position: relative;
    border-radius: var(--gt-radius);
    background: var(--gt-background);
    backdrop-filter: blur(var(--gt-blur)) saturate(180%);
    -webkit-backdrop-filter: blur(var(--gt-blur)) saturate(180%);
    border: 1px solid var(--gt-border) !important;
    box-shadow:
      0 0 0 .5px rgba(167,139,250,.08) inset,
      0 1px 0 var(--gt-shine) inset,
      0 28px 80px rgba(0,0,0,.45),
      0 4px 20px  rgba(58,111,255,.14),
      0 0  60px   rgba(168,85,247,.10);
    overflow: hidden;
    --bs-table-border-color: transparent;
  }
  .gt-wrapper::before {
    content: '';
    position: absolute; top: 0; left: 8%; right: 8%; height: 1px;
    background: linear-gradient(90deg,
      transparent,
      rgba(165,180,252,.9) 30%,
      rgba(103,232,249,1)  50%,
      rgba(244,114,182,.8) 70%,
      transparent);
    pointer-events: none; z-index: 1;
  }
  .gt-wrapper::after {
    content: '';
    position: absolute; inset: 0; border-radius: var(--gt-radius);
    background: radial-gradient(ellipse 90% 50% at 50% 0%,
      rgba(168,85,247,.12), transparent 65%);
    pointer-events: none;
  }
  .gt-wrapper .gt-toolbar {
    position: relative; z-index: 2;
    padding: 20px 24px 0;
    display: flex; align-items: center;
    justify-content: space-between;
    flex-wrap: wrap; gap: 12px;
  }
  .gt-toolbar-left {
    display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
  }

  /* ── Header row (headerHtml) — sits directly under the toolbar ─────── */
  .gt-wrapper .gt-header-row {
    position: relative; z-index: 2;
    padding: 14px 24px 0;
    display: flex; align-items: center; flex-wrap: wrap; gap: 10px;
    justify-content: flex-start;
  }
  .gt-wrapper .gt-header-row.gt-header-center  { justify-content: center; }
  .gt-wrapper .gt-header-row.gt-header-end     { justify-content: flex-end; }
  .gt-wrapper .gt-header-row.gt-header-between { justify-content: space-between; }
  .gt-wrapper .gt-header-chip {
    display: inline-flex; align-items: center; padding: 3px 11px; border-radius: 999px;
    font-size: 11px; color: var(--gt-text);
    background: rgba(167,139,250,.10); border: 1px solid rgba(167,139,250,.28);
  }
  .gt-wrapper .gt-header-row [data-gt-param].gt-active {
    border-color: var(--gt-violet) !important; color: var(--gt-violet) !important;
  }
  .gt-wrapper .gt-scroll.gt-loading { opacity: .55; pointer-events: none; transition: opacity .2s; }
  @media (max-width: 575.98px) {
    .gt-wrapper .gt-toolbar,
    .gt-wrapper .gt-header-row { padding-left: 14px; padding-right: 14px; }
  }

  /* ── Buttons (toolbar nav + row actions) ─────────────────────
     --gt-accent is an "r,g,b" triplet. --gt-edge is the single colour
     used for BOTH the border and the icon in hover/focus/active, so
     they can never differ. */
  .gt-nav-btn,
  .gt-wrapper .gt-action-btn {
    --gt-accent: 196,181,253;
    --gt-edge: rgb(var(--gt-accent));
    display: inline-flex; align-items: center; justify-content: center;
    width: 30px; height: 30px; border-radius: 8px; flex-shrink: 0;
    background: rgba(58,111,255,.09);
    border: 1px solid rgba(167,139,250,.22) !important;
    color: rgba(160,180,255,.70) !important;
    font-size: 11px; cursor: pointer;
    text-decoration: none !important;
    outline: none;
    transition: background .2s, border-color .2s, color .2s,
                transform .12s, box-shadow .2s;
    box-shadow: 0 1px 4px rgba(0,0,0,.25);
  }
  .gt-wrapper .gt-action-btn.gt-action-danger  { --gt-accent: 255,122,192; }
  .gt-wrapper .gt-action-btn.gt-action-success { --gt-accent: 77,255,166; }
  .gt-wrapper .gt-action-btn.gt-action-info    { --gt-accent: 56,232,255; }

  .gt-nav-btn i,
  .gt-wrapper .gt-action-btn i {
    padding: 0; margin: 0; color: inherit;
    transition: color .2s, filter .2s;
  }

  .gt-nav-btn:hover,
  .gt-nav-btn:focus,
  .gt-nav-btn:focus-visible,
  .gt-wrapper .gt-action-btn:hover,
  .gt-wrapper .gt-action-btn:focus,
  .gt-wrapper .gt-action-btn:focus-visible {
    background: rgba(var(--gt-accent), .18);
    border-color: var(--gt-edge) !important;
    color: var(--gt-edge) !important;
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(var(--gt-accent), .38),
                0 0 0 1px rgba(var(--gt-accent), .20) inset;
  }
  .gt-nav-btn:hover i,
  .gt-nav-btn:focus i,
  .gt-nav-btn:focus-visible i,
  .gt-wrapper .gt-action-btn:hover i,
  .gt-wrapper .gt-action-btn:focus i,
  .gt-wrapper .gt-action-btn:focus-visible i {
    color: var(--gt-edge) !important;
    filter: drop-shadow(0 0 4px rgba(var(--gt-accent), .95))
            drop-shadow(0 0 9px rgba(var(--gt-accent), .55));
  }

  .gt-nav-btn:focus-visible,
  .gt-wrapper .gt-action-btn:focus-visible {
    outline: 2px solid var(--gt-edge);
    outline-offset: 2px;
  }

  .gt-nav-btn:active,
  .gt-wrapper .gt-action-btn:active {
    background: rgba(var(--gt-accent), .34);
    border-color: var(--gt-edge) !important;
    color: var(--gt-edge) !important;
    transform: translateY(0) scale(.92);
    box-shadow: 0 0 18px rgba(var(--gt-accent), .65),
                0 0 0 1px rgba(var(--gt-accent), .45) inset;
  }
  .gt-nav-btn:active i,
  .gt-wrapper .gt-action-btn:active i {
    color: var(--gt-edge) !important;
    filter: drop-shadow(0 0 5px rgba(var(--gt-accent), 1))
            drop-shadow(0 0 12px rgba(var(--gt-accent), .85));
  }

  .gt-wrapper .gt-table tbody td a.gt-action-btn,
  .gt-wrapper .gt-table tbody td a.gt-action-btn:visited {
    color: rgba(160,180,255,.70) !important;
  }
  .gt-wrapper .gt-table tbody td a.gt-action-btn:hover,
  .gt-wrapper .gt-table tbody td a.gt-action-btn:focus,
  .gt-wrapper .gt-table tbody td a.gt-action-btn:focus-visible,
  .gt-wrapper .gt-table tbody td a.gt-action-btn:active {
    color: var(--gt-edge) !important;
  }

  @media (prefers-reduced-motion: reduce) {
    .gt-nav-btn, .gt-wrapper .gt-action-btn { transition: none; }
    .gt-nav-btn:hover, .gt-nav-btn:focus, .gt-nav-btn:active,
    .gt-wrapper .gt-action-btn:hover, .gt-wrapper .gt-action-btn:focus,
    .gt-wrapper .gt-action-btn:active { transform: none; }
  }

  .gt-wrapper .gt-caption {
    font-family: 'DM Mono', monospace;
    font-size: 10px; letter-spacing: .22em; text-transform: uppercase;
    color: var(--gt-muted);
    display: flex; align-items: center;
  }
  .gt-wrapper .gt-search-wrap { position: relative; min-width: 230px; }
  .gt-wrapper .gt-search-wrap i {
    position: absolute; left: 12px; top: 50%; transform: translateY(-50%);
    color: var(--gt-muted); font-size: 11px; pointer-events: none;
    padding: 0; margin: 0;
  }
  .gt-wrapper .gt-search-input {
    width: 100%;
    padding: 9px 14px 9px 34px !important;
    background: rgba(58,111,255,.08) !important;
    border: 1px solid rgba(167,139,250,.18) !important;
    border-bottom: 1px solid rgba(167,139,250,.18) !important;
    border-radius: 10px !important;
    color: var(--gt-text) !important;
    font-family: 'DM Sans', sans-serif; font-size: 12.5px;
    outline: none !important;
    box-shadow: none !important;
    transition: border-color .25s, box-shadow .25s, background .25s;
  }
  .gt-wrapper .gt-search-input::placeholder { color: var(--gt-muted); }
  .gt-wrapper .gt-search-input:focus {
    background: rgba(96,165,250,.12) !important;
    border-color: rgba(167,139,250,.60) !important;
    border-bottom-color: rgba(167,139,250,.60) !important;
    box-shadow: 0 0 0 3px rgba(167,139,250,.14) !important;
  }
  .gt-wrapper .gt-scroll {
    overflow-x: auto; padding: 14px 2px 2px;
    scrollbar-width: thin;
    scrollbar-color: rgba(167,139,250,.18) transparent;
  }
  .gt-wrapper .gt-scroll::-webkit-scrollbar { height: 5px; }
  .gt-wrapper .gt-scroll::-webkit-scrollbar-track { background: transparent; }
  .gt-wrapper .gt-scroll::-webkit-scrollbar-thumb {
    background: rgba(167,139,250,.18); border-radius: 99px;
  }
  .gt-wrapper .gt-table {
    width: 100%; border-collapse: separate; border-spacing: 0;
    font-size: 13px; color: var(--gt-text);
    --bs-table-background: transparent;
    --bs-table-striped-background: transparent;
    --bs-table-hover-background: transparent;
    --bs-table-accent-background: transparent;
    --bs-table-border-color: transparent;
    --bs-table-color: var(--gt-text);
    margin-bottom: 0;
    border: none !important;
    border-color: transparent !important;
  }
  .gt-wrapper .gt-table th,
  .gt-wrapper .gt-table td { border-color: transparent !important; }
  .gt-wrapper .gt-table.table-bordered th,
  .gt-wrapper .gt-table.table-bordered td { border-color: transparent !important; }
  .gt-wrapper .gt-table thead,
  .gt-wrapper .gt-table thead tr,
  .gt-wrapper .gt-table thead tr th {
    background: rgba(168,85,247,0.10) !important;
    background-color: rgba(168,85,247,0.10) !important;
    color: rgba(160,180,255,0.65) !important;
    --bs-table-background: rgba(168,85,247,0.10);
    --bs-table-accent-background: rgba(168,85,247,0.10);
    --bs-table-hover-background: rgba(168,85,247,0.10);
    --bs-table-color: rgba(160,180,255,0.65);
    --bs-table-border-color: transparent;
  }
  .gt-wrapper .gt-table thead tr th {
    padding: 12px 18px;
    font-size: 10px; font-weight: 500;
    letter-spacing: .14em; text-transform: uppercase;
    white-space: nowrap;
    border-bottom: 1px solid rgba(167,139,250,.14) !important;
    border-top: none !important; border-left: none !important; border-right: none !important;
    cursor: pointer; user-select: none;
    transition: background-color .2s, color .2s;
    position: relative; z-index: 2;
  }
  .gt-wrapper .gt-table thead tr th:first-child { padding-left: 28px; }
  .gt-wrapper .gt-table thead tr th:last-child  { padding-right: 28px; }
  .gt-wrapper .gt-th-inner { display: inline-flex; align-items: center; gap: 6px; }
  .gt-wrapper .gt-sort-icon {
    display: inline-flex; flex-direction: column; gap: 2.5px;
    opacity: .3; transition: opacity .2s;
  }
  .gt-wrapper .gt-sort-icon span {
    display: block; width: 5px; height: 1.5px;
    background: currentColor; border-radius: 1px; transition: background .2s;
  }
  .gt-wrapper .gt-sort-icon span:first-child { width: 7px; }
  .gt-wrapper .gt-table th.gt-sorted .gt-sort-icon { opacity: 1; }
  .gt-wrapper .gt-table th.gt-sorted-asc  .gt-sort-icon span:last-child  { background: var(--gt-violet); }
  .gt-wrapper .gt-table th.gt-sorted-desc .gt-sort-icon span:first-child { background: var(--gt-violet); }
  .gt-wrapper .gt-table tbody tr {
    transition: background .15s;
    animation: gtRowIn .35s ease both;
    background: transparent;
    --bs-table-background: transparent;
    --bs-table-color: var(--gt-text);
  }
  .gt-wrapper .gt-table tbody tr:nth-child(even) td {
    background-color: var(--gt-stripe);
  }
  .gt-wrapper .gt-table tbody tr:hover td {
    background-color: var(--gt-row-hover) !important;
    color: var(--gt-text) !important;
  }
  .gt-wrapper .gt-table tbody tr.gt-hidden { display: none; }
  .gt-wrapper .gt-table tbody tr.gt-row-clickable { cursor: pointer; }
  .gt-wrapper .gt-table tbody tr.gt-row-clickable:hover td {
    background-color: rgba(96,165,250,0.13) !important;
  }
  .gt-wrapper .gt-table tbody tr.gt-row-selected td {
    background-color: rgba(167,139,250,0.15) !important;
  }
  .gt-wrapper .gt-table tbody tr.gt-row-selected:hover td {
    background-color: rgba(167,139,250,0.20) !important;
  }

  /* ── Row feedback states (AJAX row click) ────────────────── */
  .gt-wrapper .gt-table tbody tr.gt-row-error td,
  .gt-wrapper .gt-table tbody tr.gt-row-error:hover td {
    background-color: rgba(255,122,192,0.14) !important;
  }
  .gt-wrapper .gt-table tbody tr.gt-row-warning td,
  .gt-wrapper .gt-table tbody tr.gt-row-warning:hover td {
    background-color: rgba(251,191,36,0.14) !important;
  }
  .gt-wrapper .gt-table tbody tr.gt-row-busy { cursor: progress; opacity: .7; }
  .gt-wrapper .gt-table tbody tr.gt-row-removing {
    animation: gtRowOut .4s ease forwards !important;
    pointer-events: none;
  }
  @keyframes gtRowOut {
    from { opacity: 1; transform: translateX(0); }
    to   { opacity: 0; transform: translateX(28px); }
  }

  /* ── Animated status icons (tick / error / warning) ──────── */
  .gt-tick-svg {
    width: 22px; height: 22px;
    display: inline-block; vertical-align: middle;
    overflow: visible;
  }
  .gt-tick-svg .gt-tick-circle {
    stroke-dasharray: 188.5;
    stroke-dashoffset: 188.5;
    animation: gtTickCircle .4s ease forwards;
  }
  .gt-tick-svg .gt-tick-check {
    stroke-dasharray: 50;
    stroke-dashoffset: 50;
    animation: gtTickCheck .3s ease .35s forwards;
  }
  @keyframes gtTickCircle { to { stroke-dashoffset: 0; } }
  @keyframes gtTickCheck  { to { stroke-dashoffset: 0; } }

  .gt-tick-svg.gt-icon-error   { filter: drop-shadow(0 0 4px rgba(255,122,192,.85)); }
  .gt-tick-svg.gt-icon-warning { filter: drop-shadow(0 0 4px rgba(251,191,36,.85)); }

  .gt-tick-svg .gt-err-circle {
    stroke-dasharray: 188.5;
    stroke-dashoffset: 188.5;
    animation: gtTickCircle .4s ease forwards;
  }
  .gt-tick-svg .gt-err-line {
    stroke-dasharray: 30;
    stroke-dashoffset: 30;
    animation: gtTickCheck .25s ease forwards;
  }
  .gt-tick-svg .gt-err-line-1 { animation-delay: .35s; }
  .gt-tick-svg .gt-err-line-2 { animation-delay: .55s; }

  .gt-tick-svg .gt-warn-tri {
    stroke-dasharray: 180;
    stroke-dashoffset: 180;
    animation: gtTickCircle .5s ease forwards;
  }
  .gt-tick-svg .gt-warn-bar {
    stroke-dasharray: 20;
    stroke-dashoffset: 20;
    animation: gtTickCheck .2s ease .45s forwards;
  }
  .gt-tick-svg .gt-warn-dot {
    opacity: 0;
    transform-box: fill-box; transform-origin: center;
    animation: gtIconPop .2s ease .65s forwards;
  }
  @keyframes gtIconPop {
    from { opacity: 0; transform: scale(.2); }
    to   { opacity: 1; transform: scale(1); }
  }

  .gt-wrapper .gt-table tbody td {
    padding: 13px 18px;
    border-bottom: 1px solid rgba(167,139,250,.08);
    border-top: none !important; border-left: none !important; border-right: none !important;
    vertical-align: middle; white-space: nowrap;
    color: var(--gt-text); background: transparent;
  }
  .gt-wrapper .gt-table tbody td:first-child { padding-left: 28px; }
  .gt-wrapper .gt-table tbody td:last-child  { padding-right: 28px; }
  .gt-wrapper .gt-table tbody tr:last-child td { border-bottom: none; }
  .gt-wrapper .gt-table tbody td a,
  .gt-wrapper .gt-table tbody td a:hover,
  .gt-wrapper .gt-table tbody td a:visited { color: inherit; text-decoration: none; }
  .gt-wrapper .gt-empty {
    padding: 56px 24px; text-align: center;
    color: var(--gt-muted); font-size: 12.5px; letter-spacing: .05em; display: none;
  }
  .gt-wrapper .gt-empty i {
    font-size: 26px; display: block;
    opacity: .3; color: var(--gt-violet);
    padding: 0; margin: 0 0 12px 0;
  }
  .gt-wrapper .gt-footer {
    position: relative; z-index: 2;
    padding: 12px 28px;
    border-top: 1px solid rgba(167,139,250,.12);
    display: flex; align-items: center; justify-content: flex-end;
    font-size: 11px; font-family: 'DM Mono', monospace;
    color: var(--gt-muted); letter-spacing: .05em;
    background: rgba(58,111,255,.04);
  }
  .gt-badge {
    display: inline-flex; align-items: center; gap: 5px;
    padding: 3px 11px; border-radius: 999px;
    font-size: 10.5px; font-weight: 500;
    letter-spacing: .07em; text-transform: uppercase;
    border: 1px solid transparent; margin: 0 !important;
  }
  .gt-badge-dot { width: 5px; height: 5px; border-radius: 50%; flex-shrink: 0; }
  .gt-badge-default { background:rgba(96,165,250,.08);  border-color:rgba(96,165,250,.22);  color:rgba(180,200,255,.75); }
  .gt-badge-default .gt-badge-dot { background:rgba(160,180,255,.5); }
  .gt-badge-success { background:rgba(52,211,153,.10);  border-color:rgba(52,211,153,.28);  color:#34d399; }
  .gt-badge-success .gt-badge-dot { background:#34d399; box-shadow:0 0 7px rgba(52,211,153,.7); animation:gtDotPulse 2s ease-in-out infinite; }
  .gt-badge-danger  { background:rgba(244,114,182,.10); border-color:rgba(244,114,182,.28); color:#f472b6; }
  .gt-badge-danger  .gt-badge-dot { background:#f472b6; box-shadow:0 0 6px rgba(244,114,182,.6); }
  .gt-badge-warning { background:rgba(251,191,36,.10);  border-color:rgba(251,191,36,.28);  color:#fbbf24; }
  .gt-badge-warning .gt-badge-dot { background:#fbbf24; box-shadow:0 0 6px rgba(251,191,36,.6); }
  .gt-badge-info    { background:rgba(34,211,238,.10);  border-color:rgba(34,211,238,.28);  color:#22d3ee; }
  .gt-badge-info    .gt-badge-dot { background:#22d3ee; box-shadow:0 0 6px rgba(34,211,238,.6); }
  .gt-badge-violet  { background:rgba(167,139,250,.10); border-color:rgba(167,139,250,.28); color:#a78bfa; }
  .gt-badge-violet  .gt-badge-dot { background:#a78bfa; box-shadow:0 0 6px rgba(167,139,250,.6); }
  .gt-badge-blue    { background:rgba(96,165,250,.10);  border-color:rgba(96,165,250,.28);  color:#60a5fa; }
  .gt-badge-blue    .gt-badge-dot { background:#60a5fa; box-shadow:0 0 6px rgba(96,165,250,.6); }
  @keyframes gtDotPulse { 0%,100%{opacity:.5;transform:scale(1)} 50%{opacity:1;transform:scale(1.5)} }
  .gt-wrapper .gt-actions { display: inline-flex; align-items: center; gap: 6px; }
  @keyframes gtRowIn { from{opacity:0;transform:translateY(6px)} to{opacity:1;transform:translateY(0)} }
  </style>'''
  }

  /** The <glass:table> tag. Delegates to renderTable so subclasses can reuse it. */
  def table = { attrs, body -> renderTable(attrs, body) }

  /**
   * Renders the complete glass table (toolbar, search, header row, sortable header, body,
   * footer, styles and behaviour script). Protected so ListTableTagLib can call it.
   */
  protected void renderTable(Map attrs, Closure body) {
    def tableId = attrs.id ?: "gt-${UUID.randomUUID().toString().take(8)}"
    def cols = (attrs.cols ?: '').toString().split(',').collect { it.trim() }
    def caption = attrs.caption
    def captionStyle = attrs.captionStyle ?: ''
    def searchable = attrs.searchable?.toString() != 'false'
    def showCount = attrs.rowCount?.toString() != 'false'
    def emptyText = attrs.emptyText ?: 'No records found'

    // Free-form HTML row beneath the caption/search toolbar (same idea as modernGlassCard's headerHtml)
    String headerHtml = attrs.headerHtml?.toString()?.trim() ?: ''
    String headerAlign = (attrs.headerAlign ?: 'start').toString().trim().toLowerCase()
    if (!(headerAlign in HEADER_ALIGNS)) {
      headerAlign = 'start'
    }

    // Header-driven data loading: slot-aware endpoint built from headerController / headerAction / headerParams
    def headerController = attrs.headerController
    def headerAction = attrs.headerAction
    Map headerParams = [:]
    if (attrs.headerParams instanceof Map) {
      ((Map) attrs.headerParams).each { k, v ->
        if (v != null && v.toString().trim()) {
          headerParams[k] = v
        }
      }
    }
    boolean hasHeaderLoad = headerController && headerAction
    String headerMethod = (attrs.headerMethod ?: 'POST').toString().trim().toUpperCase()
    if (!(headerMethod in HEADER_METHODS)) {
      headerMethod = 'POST'
    }
    boolean headerLoad = attrs.headerLoad?.toString() == 'true'
    String headerUrl = ''
    if (hasHeaderLoad) {
      def hm = [controller: headerController, action: headerAction]
      if (headerParams) {
        hm.params = headerParams
      }
      headerUrl = buildLink(hm)
    }
    List<String> dataFields = (attrs.fields ?: '').toString().split(',').collect { it.trim() }.findAll { it }
    String dataIdField = (attrs.idField ?: 'id').toString()
    String dataBadgeHtml = buildBadgeHtml(attrs)
    // headerData: values shown inside the header HTML (elements with data-gt-bind="key")
    String headerDataJson = '{}'
    if (attrs.headerData instanceof Map) {
      try {
        headerDataJson = jsLiteral(attrs.headerData)
      } catch (Exception ex) {
        headerDataJson = '{}'
        log.error('glass:table headerData could not be serialised to JSON - use plain Maps/Lists/scalars', ex)
      }
    }

    def rowController = attrs.rowController
    def rowAction = attrs.rowAction
    def rowParams = attrs.rowParams instanceof Map ? attrs.rowParams : [:]
    def rowBody = attrs.rowBody instanceof Map ? attrs.rowBody : [:]
    def rowIdAttr = (attrs.rowIdAttr ?: 'data-row-id').toString()
    def rowTickCol = attrs.rowTickCol != null ? attrs.rowTickCol.toString() : '-1'
    boolean hasRowNav = rowController && rowAction
    boolean rowAjax = attrs.rowAjax?.toString() != 'false'
    boolean rowRemove = attrs.rowRemove?.toString() != 'false'
    String rowMethod = (attrs.rowMethod ?: 'POST').toString().toUpperCase()
    if (!(rowMethod in ROW_METHODS)) {
      rowMethod = 'POST'
    }
    String removeDelayStr = (attrs.removeDelay ?: '950').toString().trim()
    int removeDelay = removeDelayStr.isInteger() ? removeDelayStr.toInteger() : 950

    // Slot-aware endpoint: /u/$slot/$controller/$action?queryparams  or  /$controller/$action?queryparams
    String rowUrl = ''
    if (hasRowNav) {
      def linkMap = [controller: rowController, action: rowAction]
      if (rowParams) {
        linkMap.params = rowParams
      }
      rowUrl = buildLink(linkMap)
    }
    // legacy navigation target (rowAjax="false")
    String rowNavBase = rowUrl ? (rowUrl + (rowUrl.contains('?') ? '&' : '?') + 'id=') : ''

    def homeController = attrs.homeController
    def homeAction = attrs.homeAction
    def homeParams = attrs.homeParams instanceof Map ? attrs.homeParams : [:]
    def homeIcon = (attrs.homeIcon ?: 'fa-solid fa-house').toString()
    def homeTooltip = (attrs.homeTooltip ?: 'Go back to main page').toString()
    String homeUrl = ''
    if (homeController && homeAction) {
      def lm = [controller: homeController, action: homeAction]
      if (homeParams) {
        lm.params = homeParams
      }
      homeUrl = buildLink(lm)
    }

    def backController = attrs.backController
    def backAction = attrs.backAction
    def backParams = attrs.backParams instanceof Map ? attrs.backParams : [:]
    def backIcon = (attrs.backIcon ?: 'fa-solid fa-chevron-left').toString()
    def backTooltip = (attrs.backTooltip ?: 'Go to previous page').toString()
    String backUrl = ''
    if (backController && backAction) {
      def lm = [controller: backController, action: backAction]
      if (backParams) {
        lm.params = backParams
      }
      backUrl = buildLink(lm)
    }

    // Alert runtime (window.glassAlert). Emitted once per request. If the tag cannot be
    // resolved here, put <glass:alertRuntime/> in your layout instead.
    if (hasRowNav && rowAjax) {
      try {
        out << glass.alertRuntime()
      } catch (ignored) {
        // JS side checks for window.glassAlert before using it
      }
    }

    out << '<div class="gt-wrapper">'

    if (caption || searchable || homeUrl || backUrl) {
      out << '<div class="gt-toolbar">'
      out << '<div class="gt-toolbar-left">'

      if (backUrl) {
        out << '<a href="' + backUrl.encodeAsHTML() + '" class="gt-nav-btn" title="' + backTooltip.encodeAsHTML() + '"><i class="' + backIcon.encodeAsHTML() + '"></i></a>'
      }
      if (homeUrl) {
        out << '<a href="' + homeUrl.encodeAsHTML() + '" class="gt-nav-btn" title="' + homeTooltip.encodeAsHTML() + '"><i class="' + homeIcon.encodeAsHTML() + '"></i></a>'
      }
      if (caption) {
        String styleAttr = captionStyle ? ' style="' + captionStyle.encodeAsHTML() + '"' : ''
        out << '<span class="gt-caption"' + styleAttr + '>' + caption + '</span>'
      }

      out << '</div>'

      if (searchable) {
        out << '<div class="gt-search-wrap">'
        out << '<i class="fa-solid fa-magnifying-glass"></i>'
        out << '<input class="gt-search-input" type="search" placeholder="Search\u2026" data-gt-search="' + tableId + '" autocomplete="off"/>'
        out << '</div>'
      }

      out << '</div>'
    }

    // ── Header row: free-form HTML, directly beneath the caption/search row ──
    if (headerHtml) {
      out << '<div class="gt-header-row gt-header-' + headerAlign + '">'
      out << headerHtml
      out << '</div>'
    }

    out << '<div class="gt-scroll"><table class="gt-table" id="' + tableId + '"><thead><tr>'
    cols.each { col ->
      out << '<th><span class="gt-th-inner">' + col.encodeAsHTML() + '<span class="gt-sort-icon"><span></span><span></span></span></span></th>'
    }
    out << '</tr></thead><tbody>' + renderDataRows(attrs) + (body ? body() : '') + '</tbody></table>'
    out << '<div class="gt-empty" id="' + tableId + '-empty"><i class="fa-solid fa-magnifying-glass"></i>' + emptyText.encodeAsHTML() + '</div>'
    out << '</div>'

    if (showCount) {
      out << '<div class="gt-footer"><span id="' + tableId + '-count"></span></div>'
    }

    out << '</div>'

    out << '<style>'
    out << '#' + tableId + ' thead,'
    out << '#' + tableId + ' thead tr,'
    out << '#' + tableId + ' thead th {'
    out << '  background: rgba(168,85,247,0.10) !important;'
    out << '  background-color: rgba(168,85,247,0.10) !important;'
    out << '  color: rgba(160,180,255,0.65) !important;'
    out << '  --bs-table-background: rgba(168,85,247,0.10) !important;'
    out << '  --bs-table-color: rgba(160,180,255,0.65) !important;'
    out << '  --bs-table-accent-background: rgba(168,85,247,0.10) !important;'
    out << '  --bs-table-hover-background: rgba(168,85,247,0.10) !important;'
    out << '  --bs-table-striped-background: rgba(168,85,247,0.10) !important;'
    out << '}'
    out << '#' + tableId + ' thead th:hover,'
    out << '#' + tableId + ' thead tr:hover th {'
    out << '  background: rgba(96,165,250,0.18) !important;'
    out << '  background-color: rgba(96,165,250,0.18) !important;'
    out << '  color: rgba(220,228,255,0.95) !important;'
    out << '  --bs-table-background: rgba(96,165,250,0.18) !important;'
    out << '  --bs-table-color: rgba(220,228,255,0.95) !important;'
    out << '}'
    out << '</style>'

    out << '<script>'
    out << '(function () {'
    out << "  'use strict';"
    out << "  var tbl   = document.getElementById('" + tableId + "');"
    out << "  var empty = document.getElementById('" + tableId + "-empty');"
    out << "  var cnt   = document.getElementById('" + tableId + "-count');"
    out << "  var srch  = document.querySelector('[data-gt-search=\"" + tableId + "\"]');"
    out << '  if (!tbl) return;'

    out << '''
    // tbl.gtLoading is set by ListTableTagLib while it fetches a page, so the
    // "no records" state does not flash between "last row removed" and "new rows arrived".
    function updateState() {
      var visible = tbl.querySelectorAll('tbody tr:not(.gt-hidden)').length;
      var total   = tbl.querySelectorAll('tbody tr').length;
      if (cnt)   cnt.textContent = visible === 0 ? '0 rows' : visible + ' of ' + total + ' rows';
      if (empty) empty.style.display = (visible === 0 && !tbl.gtLoading) ? 'block' : 'none';
    }

    var sortState = { col: -1, asc: true };

    function applySort() {
      if (sortState.col < 0) return;
      var ci  = sortState.col;
      var asc = sortState.asc;
      var tbody = tbl.querySelector('tbody');
      var rows  = Array.from(tbody.querySelectorAll('tr'));
      rows.sort(function (a, b) {
        var av = (a.cells[ci] ? a.cells[ci].innerText : '').trim().toLowerCase();
        var bv = (b.cells[ci] ? b.cells[ci].innerText : '').trim().toLowerCase();
        var an = parseFloat(av.replace(/,/g, '')), bn = parseFloat(bv.replace(/,/g, ''));
        if (!isNaN(an) && !isNaN(bn)) return asc ? an - bn : bn - an;
        return asc ? av.localeCompare(bv) : bv.localeCompare(av);
      });
      rows.forEach(function (r) { tbody.appendChild(r); });
    }

    tbl.querySelectorAll('thead th').forEach(function (th, ci) {
      th.addEventListener('click', function () {
        var asc = sortState.col === ci ? !sortState.asc : true;
        sortState = { col: ci, asc: asc };
        tbl.querySelectorAll('thead th').forEach(function (h) {
          h.classList.remove('gt-sorted', 'gt-sorted-asc', 'gt-sorted-desc');
        });
        th.classList.add('gt-sorted', asc ? 'gt-sorted-asc' : 'gt-sorted-desc');
        applySort();
        updateState();
      });
    });

    function applyFilter() {
      var q = srch ? srch.value.toLowerCase().trim() : '';
      tbl.querySelectorAll('tbody tr').forEach(function (r) {
        r.classList.toggle('gt-hidden', q !== '' && !r.innerText.toLowerCase().includes(q));
      });
      updateState();
    }

    if (srch) {
      srch.addEventListener('input', applyFilter);
    }

    // ── Animated status icons ────────────────────────────────────────────
    var SVG_NS = 'http://www.w3.org/2000/svg';
    var ICON_COLOR = { success: '#34d399', error: '#ff7ac0', warning: '#fbbf24' };
    var ICON_LABEL = { success: 'Success', error: 'Error', warning: 'Warning' };
    var ROW_STATE_CLASS = { success: 'gt-row-selected', error: 'gt-row-error', warning: 'gt-row-warning' };

    function svgEl(name, attrs) {
      var e = document.createElementNS(SVG_NS, name);
      Object.keys(attrs).forEach(function (k) { e.setAttribute(k, attrs[k]); });
      return e;
    }

    function buildIcon(kind) {
      var color = ICON_COLOR[kind];
      var svg = svgEl('svg', {
        'viewBox': '0 0 75.2 75.2',
        'class': 'gt-tick-svg gt-icon-' + kind,
        'aria-label': ICON_LABEL[kind]
      });
      var stroke = { 'fill': 'none', 'stroke': color, 'stroke-width': '3', 'stroke-miterlimit': '10' };
      function withStroke(extra) {
        var o = {};
        Object.keys(stroke).forEach(function (k) { o[k] = stroke[k]; });
        Object.keys(extra).forEach(function (k) { o[k] = extra[k]; });
        return o;
      }
      if (kind === 'success') {
        svg.appendChild(svgEl('circle', withStroke({ 'class': 'gt-tick-circle', 'cx': '35.1', 'cy': '35.1', 'r': '30.0' })));
        svg.appendChild(svgEl('polyline', withStroke({ 'class': 'gt-tick-check', 'stroke-linecap': 'round', 'points': '20,37 30,47 55,22' })));
      } else if (kind === 'warning') {
        svg.appendChild(svgEl('polygon', withStroke({ 'class': 'gt-warn-tri', 'stroke-linejoin': 'round', 'points': '35.1,8 64,58 6.2,58' })));
        svg.appendChild(svgEl('line', withStroke({ 'class': 'gt-warn-bar', 'stroke-linecap': 'round', 'x1': '35.1', 'y1': '25', 'x2': '35.1', 'y2': '42' })));
        svg.appendChild(svgEl('circle', { 'class': 'gt-warn-dot', 'cx': '35.1', 'cy': '50', 'r': '2.6', 'fill': color }));
      } else {
        svg.appendChild(svgEl('circle', withStroke({ 'class': 'gt-err-circle', 'cx': '35.1', 'cy': '35.1', 'r': '30.0' })));
        svg.appendChild(svgEl('line', withStroke({ 'class': 'gt-err-line gt-err-line-1', 'stroke-linecap': 'round', 'x1': '25', 'y1': '25', 'x2': '45.2', 'y2': '45.2' })));
        svg.appendChild(svgEl('line', withStroke({ 'class': 'gt-err-line gt-err-line-2', 'stroke-linecap': 'round', 'x1': '45.2', 'y1': '25', 'x2': '25', 'y2': '45.2' })));
      }
      return svg;
    }

    function clearRowState(row) {
      row.classList.remove('gt-row-selected', 'gt-row-error', 'gt-row-warning');
    }

    // kind: 'success' (tick) | 'error' (X) | 'warning' (triangle)
    function drawIcon(row, tickColIdx, kind) {
      clearRowState(row);
      row.classList.add(ROW_STATE_CLASS[kind] || ROW_STATE_CLASS.error);
      var colIndex = (tickColIdx >= 0) ? tickColIdx : (row.cells.length - 1);
      var cell = row.cells[colIndex];
      if (!cell) return;
      if (!row._gtSaved) row._gtSaved = { cell: cell, html: cell.innerHTML };
      cell.innerHTML = '';
      cell.appendChild(buildIcon(ICON_COLOR[kind] ? kind : 'error'));
    }

    function drawTick(row, tickColIdx) { drawIcon(row, tickColIdx, 'success'); }

    function restoreRow(row) {
      clearRowState(row);
      if (row._gtSaved) {
        row._gtSaved.cell.innerHTML = row._gtSaved.html;
        row._gtSaved = null;
      }
    }
  '''

    out << "  var ROW_URL      = " + jsLiteral(rowUrl) + ";"
    out << "  var ROW_NAV_BASE = " + jsLiteral(rowNavBase) + ";"
    out << "  var ROW_METHOD   = " + jsLiteral(rowMethod) + ";"
    out << "  var ROW_BODY     = " + jsLiteral(rowBody) + ";"
    out << "  var ROW_ID_ATTR  = " + jsLiteral(rowIdAttr) + ";"
    out << '  var ROW_TICK_COL = ' + rowTickCol + ';'
    out << '  var HAS_ROW_NAV  = ' + (hasRowNav ? 'true' : 'false') + ';'
    out << '  var ROW_AJAX     = ' + (rowAjax ? 'true' : 'false') + ';'
    out << '  var ROW_REMOVE   = ' + (rowRemove ? 'true' : 'false') + ';'
    out << '  var REMOVE_DELAY = ' + removeDelay + ';'
    out << '  var STATUS_KIND  = ' + jsLiteral(STATUS_KIND) + ';'
    out << '  var STATUS_MSG   = ' + jsLiteral(STATUS_MESSAGE) + ';'
    out << '  var HEADER_URL    = ' + jsLiteral(headerUrl) + ';'
    out << '  var HEADER_METHOD = ' + jsLiteral(headerMethod) + ';'
    out << '  var HEADER_LOAD   = ' + (headerLoad ? 'true' : 'false') + ';'
    out << '  var DATA_FIELDS   = ' + jsLiteral(dataFields) + ';'
    out << '  var DATA_ID_FIELD = ' + jsLiteral(dataIdField) + ';'
    out << '  var DATA_BADGE    = ' + jsLiteral(dataBadgeHtml) + ';'
    out << '  var HEADER_DATA   = ' + headerDataJson + ';'

    out << '''
    function notify(kind, message) {
      if (typeof window.glassAlert === 'function') {
        window.glassAlert(message, kind, kind === 'success' ? 3500 : 6000);
      }
    }

    function pickMessage(json, status) {
      if (json && typeof json === 'object') {
        if (typeof json.message === 'string' && json.message) return json.message;
        if (typeof json.error === 'string' && json.error) return json.error;
        if (Array.isArray(json.errors) && json.errors.length) {
          var parts = json.errors.map(function (e) {
            return typeof e === 'string' ? e : ((e && e.message) || '');
          }).filter(Boolean);
          if (parts.length) return parts.join(', ');
        }
      }
      if (STATUS_MSG[status]) return STATUS_MSG[status];
      return status === 0 ? 'Network error. Please try again.' : 'Unexpected response (HTTP ' + status + ').';
    }

    // Removes a row (animated), refreshes the footer and fires the table events.
    function removeRow(row, rowId, json) {
      var done = false;
      function finish() {
        if (done) return;
        done = true;
        row.remove();
        updateState();
        tbl.dispatchEvent(new CustomEvent('gt:rowRemoved', { detail: { id: rowId, response: json } }));
        if (tbl.querySelectorAll('tbody tr').length === 0) {
          tbl.dispatchEvent(new CustomEvent('gt:empty', { detail: {} }));
        }
      }
      row.addEventListener('animationend', function (e) { if (e.target === row) finish(); });
      row.classList.add('gt-row-removing');
      setTimeout(finish, 600);   // safety net (reduced-motion, hidden tab, ...)
    }

    function handleResult(row, rowId, status, json) {
      var kind = STATUS_KIND[status] || 'error';
      var message = pickMessage(json, status);
      row.classList.remove('gt-row-busy');
      drawIcon(row, ROW_TICK_COL, kind);
      notify(kind, message);

      if (kind === 'success') {
        // row stays locked (_gtBusy) until it is gone
        if (ROW_REMOVE) {
          setTimeout(function () { removeRow(row, rowId, json); }, REMOVE_DELAY);
        } else {
          row._gtBusy = false;
        }
      } else {
        setTimeout(function () { restoreRow(row); row._gtBusy = false; }, 2600);
      }
    }

    // AJAX row click: JSON request ({id, ...rowBody}), JSON response.
    function submitRow(row, rowId) {
      if (row._gtBusy) return;
      row._gtBusy = true;
      row.classList.add('gt-row-busy');

      var url  = ROW_URL;
      var opts = { method: ROW_METHOD, credentials: 'same-origin', headers: { 'Accept': 'application/json' } };
      if (ROW_METHOD === 'GET') {
        url += (url.indexOf('?') < 0 ? '?' : '&') + 'id=' + encodeURIComponent(rowId);
      } else {
        var payload = {};
        Object.keys(ROW_BODY).forEach(function (k) { payload[k] = ROW_BODY[k]; });
        payload.id = rowId;
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(payload);
      }

      fetch(url, opts)
        .then(function (r) {
          return r.text().then(function (t) {
            var j = {};
            try { j = t ? JSON.parse(t) : {}; } catch (ignore) { j = {}; }
            return { status: r.status, json: j };
          });
        })
        .catch(function () { return { status: 0, json: {} }; })
        .then(function (res) { handleResult(row, rowId, res.status, res.json); });
    }

    function bindRowClick(row) {
      if (!HAS_ROW_NAV || row._gtBound) return;
      row._gtBound = true;
      row.classList.add('gt-row-clickable');
      row.addEventListener('click', function (e) {
        if (e.target.closest('.gt-action-btn, .gt-actions, a, button')) return;
        var rowId = row.getAttribute(ROW_ID_ATTR);
        if (!rowId) return;
        if (ROW_AJAX) {
          submitRow(row, rowId);
        } else {
          drawTick(row, ROW_TICK_COL);
          setTimeout(function () {
            window.location.href = ROW_NAV_BASE + encodeURIComponent(rowId);
          }, 300);
        }
      });
    }

    // Binds every row already in the body: `data` rows and hand-written rows alike.
    tbl.querySelectorAll('tbody tr').forEach(function (r, i) {
      r.style.animationDelay = (i * 45) + 'ms';
      bindRowClick(r);
    });

    // ── Header-driven loading (headerController / headerAction / headerParams) ──
    var HEADER_ROW = tbl.closest('.gt-wrapper') ? tbl.closest('.gt-wrapper').querySelector('.gt-header-row') : null;
    var SCROLL_EL  = tbl.closest('.gt-scroll');
    var headerSeq  = 0;
    var headerTimer = null;
    var clickValues = {};     // values set by clicked data-gt-param/data-gt-value elements

    // Named controls inside the header row -> { name: value }
    function collectHeaderValues() {
      var vals = {};
      if (!HEADER_ROW) return vals;
      HEADER_ROW.querySelectorAll('[name], [data-gt-param]').forEach(function (el) {
        var name = el.getAttribute('data-gt-param') || el.name;
        var type = (el.type || '').toLowerCase();
        if (!name || el.disabled || type === 'button' || type === 'submit' || type === 'reset') return;
        if (type === 'checkbox') {
          vals[name] = el.checked;
        } else if (type === 'radio') {
          if (el.checked) vals[name] = el.value;
        } else if (el.tagName === 'SELECT' && el.multiple) {
          vals[name] = Array.from(el.selectedOptions).map(function (o) { return o.value; });
        } else if ('value' in el && !el.hasAttribute('data-gt-value')) {
          vals[name] = el.value;
        }
      });
      Object.keys(clickValues).forEach(function (k) { vals[k] = clickValues[k]; });
      return vals;
    }

    // ── headerData binding: data-gt-bind="key" / data-gt-bind-attr="attr:key" ──
    function lookupPath(obj, path) {
      return path.split('.').reduce(function (o, k) { return (o === null || o === undefined) ? undefined : o[k]; }, obj);
    }
    function optValue(it) {
      if (it !== null && typeof it === 'object') return it.value != null ? it.value : (it.id != null ? it.id : '');
      return it;
    }
    function optLabel(it) {
      if (it !== null && typeof it === 'object') return it.label != null ? it.label : (it.name != null ? it.name : optValue(it));
      return it;
    }
    function bindElement(el, v) {
      if (v === undefined) return;
      var tag = el.tagName, type = (el.type || '').toLowerCase();
      if (tag === 'SELECT') {
        if (Array.isArray(v)) {
          var prev = el.value, chosen = null;
          Array.from(el.options).forEach(function (o) { if (!o.hasAttribute('data-gt-static')) o.remove(); });
          v.forEach(function (it) {
            var o = document.createElement('option');
            o.value = String(optValue(it));
            o.textContent = String(optLabel(it));
            if (it !== null && typeof it === 'object' && it.selected) chosen = o.value;
            el.appendChild(o);
          });
          var want = chosen !== null ? chosen : prev;
          if (Array.from(el.options).some(function (o) { return o.value === want; })) el.value = want;
        } else {
          el.value = v === null ? '' : String(v);
        }
      } else if (type === 'checkbox') {
        el.checked = (v === true || v === 'true' || v === 1);
      } else if (type === 'radio') {
        el.checked = String(el.value) === String(v);
      } else if (tag === 'INPUT' || tag === 'TEXTAREA') {
        el.value = v === null ? '' : String(v);
      } else if (Array.isArray(v)) {
        var asList = (tag === 'UL' || tag === 'OL');
        el.textContent = '';
        v.forEach(function (it) {
          var c = document.createElement(asList ? 'li' : 'span');
          if (!asList) c.className = 'gt-header-chip';
          c.textContent = String(optLabel(it));
          el.appendChild(c);
        });
      } else if (v !== null && typeof v === 'object') {
        el.textContent = JSON.stringify(v);
      } else {
        el.textContent = v === null ? '' : String(v);
      }
    }
    // Merges `data` into the header data and refreshes every bound element whose key was sent.
    function applyHeaderData(data) {
      if (!data || typeof data !== 'object') return;
      HEADER_DATA = Object.assign({}, HEADER_DATA, data);
      tbl.gtHeaderData = HEADER_DATA;
      if (!HEADER_ROW) return;
      function sent(key) { return Object.prototype.hasOwnProperty.call(data, key.split('.')[0]); }
      HEADER_ROW.querySelectorAll('[data-gt-bind]').forEach(function (el) {
        var key = el.getAttribute('data-gt-bind');
        if (key && sent(key)) bindElement(el, lookupPath(HEADER_DATA, key));
      });
      HEADER_ROW.querySelectorAll('[data-gt-bind-attr]').forEach(function (el) {
        el.getAttribute('data-gt-bind-attr').split(',').forEach(function (pair) {
          var i = pair.indexOf(':');
          if (i < 1) return;
          var attr = pair.slice(0, i).trim(), key = pair.slice(i + 1).trim();
          if (!attr || !key || !sent(key)) return;
          var v = lookupPath(HEADER_DATA, key);
          if (v !== undefined && v !== null) el.setAttribute(attr, String(v));
        });
      });
    }

    function buildDataRow(item) {
      var tr = document.createElement('tr');
      tr.setAttribute(ROW_ID_ATTR, item[DATA_ID_FIELD] == null ? '' : item[DATA_ID_FIELD]);
      DATA_FIELDS.forEach(function (f) {
        var td = document.createElement('td');
        td.textContent = item[f] == null ? '' : item[f];
        tr.appendChild(td);
      });
      if (DATA_BADGE) {
        var tdBadge = document.createElement('td');
        tdBadge.innerHTML = DATA_BADGE;
        tr.appendChild(tdBadge);
      }
      return tr;
    }

    // Replaces every body row with the given items.
    function replaceData(items, params) {
      var tbody = tbl.querySelector('tbody');
      tbody.innerHTML = '';
      items.forEach(function (item, i) {
        var tr = buildDataRow(item);
        tr.style.animationDelay = (Math.min(i, 20) * 45) + 'ms';
        tbody.appendChild(tr);
        bindRowClick(tr);
      });
      applySort();
      applyFilter();
      tbl.dispatchEvent(new CustomEvent('gt:dataLoaded', { detail: { items: items, params: params } }));
    }

    function extractArray(json) {
      if (Array.isArray(json)) return json;
      var found = null;
      if (json && typeof json === 'object') {
        Object.keys(json).some(function (k) {
          if (Array.isArray(json[k])) { found = json[k]; return true; }
        });
      }
      return found;
    }

    function reloadFromHeader(extra) {
      if (!HEADER_URL) return;
      if (!DATA_FIELDS.length) {
        console.error('[glass:table] header loading needs the "fields" attribute to build rows');
        return;
      }
      var params = collectHeaderValues();
      params.offset = 0;                          // any header interaction restarts paging at 0
      tbl.dispatchEvent(new CustomEvent('gt:headerReload', { detail: { params: params } }));
      if (typeof tbl.gtHeaderExtra === 'function') {
        var more = tbl.gtHeaderExtra() || {};
        Object.keys(more).forEach(function (k) { params[k] = more[k]; });
      }
      if (extra && typeof extra === 'object') {
        Object.keys(extra).forEach(function (k) { params[k] = extra[k]; });
      }
      tbl.gtHeaderState = params;

      var seq = ++headerSeq;
      var url = HEADER_URL;
      var opts = { method: HEADER_METHOD, credentials: 'same-origin', headers: { 'Accept': 'application/json' } };
      if (HEADER_METHOD === 'GET') {
        var qs = [];
        Object.keys(params).forEach(function (k) {
          var v = params[k];
          if (v == null) return;
          (Array.isArray(v) ? v : [v]).forEach(function (one) {
            qs.push(encodeURIComponent(k) + '=' + encodeURIComponent(one));
          });
        });
        if (qs.length) url += (url.indexOf('?') < 0 ? '?' : '&') + qs.join('&');
      } else {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(params);
      }

      tbl.gtLoading = true;
      if (SCROLL_EL) SCROLL_EL.classList.add('gt-loading');
      updateState();

      fetch(url, opts)
        .then(function (r) {
          return r.text().then(function (t) {
            var j = null;
            try { j = t ? JSON.parse(t) : null; } catch (ignore) { j = null; }
            return { status: r.status, json: j };
          });
        })
        .catch(function () { return { status: 0, json: null }; })
        .then(function (res) {
          if (seq !== headerSeq) return;          // a newer request superseded this one
          var items = res.status >= 200 && res.status < 300 ? extractArray(res.json) : null;
          if (items) {
            if (res.json && !Array.isArray(res.json) && res.json.headerData && typeof res.json.headerData === 'object') {
              applyHeaderData(res.json.headerData);
            }
            replaceData(items, params);
          } else {
            var kind = STATUS_KIND[res.status] || 'error';
            notify(kind === 'success' ? 'error' : kind,
              res.status >= 200 && res.status < 300 ? 'Response is not a JSON array' : pickMessage(res.json, res.status));
          }
        })
        .finally(function () {
          if (seq !== headerSeq) return;
          tbl.gtLoading = false;
          if (SCROLL_EL) SCROLL_EL.classList.remove('gt-loading');
          updateState();
        });
    }

    if (HEADER_URL && HEADER_ROW) {
      HEADER_ROW.addEventListener('change', function (e) {
        if (e.target.closest('[data-gt-no-reload]')) return;
        clearTimeout(headerTimer);
        reloadFromHeader();
      });
      HEADER_ROW.addEventListener('click', function (e) {
        var el = e.target.closest('[data-gt-param][data-gt-value], [data-gt-reload]');
        if (!el || !HEADER_ROW.contains(el) || el.closest('[data-gt-no-reload]')) return;
        var tag = el.tagName;
        var type = (el.type || '').toLowerCase();
        // real form controls are handled by the change / input listeners
        if ((tag === 'INPUT' && type !== 'button' && type !== 'submit') || tag === 'SELECT' || tag === 'TEXTAREA') return;
        var name = el.getAttribute('data-gt-param');
        if (name && el.hasAttribute('data-gt-value')) {
          clickValues[name] = el.getAttribute('data-gt-value');
          HEADER_ROW.querySelectorAll('[data-gt-param]').forEach(function (o) {
            if (o.getAttribute('data-gt-param') === name) o.classList.toggle('gt-active', o === el);
          });
        }
        clearTimeout(headerTimer);
        reloadFromHeader();
      });
      HEADER_ROW.addEventListener('input', function (e) {
        var t = e.target;
        var type = (t.type || '').toLowerCase();
        if (t.tagName === 'SELECT' || type === 'checkbox' || type === 'radio' || type === 'range') return;
        if (t.closest('[data-gt-no-reload]')) return;
        clearTimeout(headerTimer);
        headerTimer = setTimeout(function () { reloadFromHeader(); }, 350);
      });
    }

    // ── Empty table -> drop the exhausted option from a glass:select in the header ──
    // Runs when the table has no rows left AND nothing more can be loaded:
    //   - ListTableTagLib fires gt:noMoreRows after its refill fetch came back empty;
    //   - a plain glass:table (no loader / gtAppendData) prunes straight from gt:empty.
    // The selected option is removed, the next real option is selected and a 'change' event is
    // fired on the select so the page loads the next category. No glass:select in the header
    // (or a multi-select) -> nothing happens.
    function pruneEmptyHeaderSelect() {
      if (!HEADER_ROW || tbl.querySelectorAll('tbody tr').length > 0) return;
      var sel = HEADER_ROW.querySelector('.gsl-wrapper select.gsl-select:not([multiple])');
      if (!sel || sel.selectedIndex < 0) return;

      var current = sel.options[sel.selectedIndex];
      if (!current || current.disabled || current.value === '') return;   // placeholder / noSelection
      var removed = { value: current.value, label: current.textContent };
      current.remove();

      var next = Array.prototype.find.call(sel.options, function (o) {
        return !o.disabled && o.value !== '';
      });
      if (next) {
        sel.value = next.value;                                    // glass:select re-syncs its own state
        sel.dispatchEvent(new Event('change', { bubbles: true })); // loads the next option's rows
      } else {
        sel.disabled = true;                                       // nothing left to pick
        var w = sel.closest('.gsl-wrapper');
        if (w) w.classList.add('gsl-disabled');
      }
      tbl.dispatchEvent(new CustomEvent('gt:headerOptionRemoved', { detail: removed }));
    }
    tbl.gtPruneHeaderSelect = pruneEmptyHeaderSelect;
    tbl.addEventListener('gt:noMoreRows', pruneEmptyHeaderSelect);
    tbl.addEventListener('gt:empty', function () {
      if (!tbl.gtAppendData) pruneEmptyHeaderSelect();   // plain glass:table has no loader
    });

    // Hooks used by subclasses (ListTableTagLib) and page scripts.
    tbl.gtReload       = reloadFromHeader;
    tbl.gtHeaderValues = collectHeaderValues;
    tbl.gtReplaceData  = function (items) { replaceData(Array.isArray(items) ? items : [], tbl.gtHeaderState || {}); };
    tbl.gtSetHeaderData = applyHeaderData;
    applyHeaderData(HEADER_DATA);
    tbl.gtBindRow     = bindRowClick;
    tbl.gtDrawTick    = drawTick;
    tbl.gtDrawIcon    = drawIcon;
    tbl.gtRemoveRow   = function (row) { removeRow(row, row.getAttribute(ROW_ID_ATTR), null); };
    tbl.gtNotify      = notify;
    tbl.gtUpdateState = updateState;
    tbl.gtApplyFilter = applyFilter;
    tbl.gtApplySort   = applySort;

    updateState();
    // deferred so subclass scripts (ListTableTagLib) can register their hooks first
    if (HEADER_LOAD) setTimeout(function () { reloadFromHeader(); }, 0);
  })();
  '''
    out << '</script>'
  }

  def tableSearch = { attrs ->
    def tableId = (attrs.tableId ?: '').toString()
    def placeholder = (attrs.placeholder ?: 'Search\u2026').toString()
    out << '<div class="gt-search-wrap" style="max-width:260px;">'
    out << '<i class="fa-solid fa-magnifying-glass"></i>'
    out << '<input class="gt-search-input" type="search"'
    out << ' placeholder="' + placeholder.encodeAsHTML() + '"'
    out << ' data-gt-search="' + tableId.encodeAsHTML() + '"'
    out << ' autocomplete="off"/>'
    out << '</div>'
  }

  def tableBadge = { attrs ->
    def text = (attrs.text ?: '').toString()
    def variant = (attrs.variant ?: 'default').toString()
    def dot = attrs.dot?.toString() != 'false'
      ? '<span class="gt-badge-dot"></span>' : ''
    out << '<span class="gt-badge gt-badge-' + variant.encodeAsHTML() + '">' + dot + text.encodeAsHTML() + '</span>'
  }

  def tableActions = { attrs, body ->
    out << '<span class="gt-actions">' + body() + '</span>'
  }

  def tableActionBtn = { attrs ->
    def icon = (attrs.icon ?: 'fa-solid fa-ellipsis').toString()
    def variant = attrs.variant ? ' gt-action-' + attrs.variant.toString().encodeAsHTML() : ''
    def title = (attrs.title ?: '').toString()

    String href = attrs.href ? applySlot(attrs.href.toString()) : ''
    if (!href && attrs.controller && attrs.action) {
      def lm = [controller: attrs.controller.toString(), action: attrs.action.toString()]
      if (attrs.params instanceof Map) {
        lm.params = attrs.params
      }
      href = buildLink(lm)
    }

    if (href) {
      out << '<a href="' + href.encodeAsHTML() + '" class="gt-action-btn' + variant + '" title="' + title.encodeAsHTML() + '"><i class="' + icon.encodeAsHTML() + '"></i></a>'
    } else {
      def oc = attrs.onclick ? ' onclick="' + attrs.onclick.toString().encodeAsHTML() + '"' : ''
      out << '<button type="button" class="gt-action-btn' + variant + '" title="' + title.encodeAsHTML() + '"' + oc + '><i class="' + icon.encodeAsHTML() + '"></i></button>'
    }
  }
}
