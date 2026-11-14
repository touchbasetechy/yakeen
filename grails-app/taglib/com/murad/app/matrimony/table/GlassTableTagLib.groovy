package com.murad.app.matrimony.table

import com.murad.app.matrimony.GlassTooltipStylesTagLib
import grails.gsp.TagLib

/**
 * GlassTableTagLib  —  Cosmic glassmorphic table for Grails 6
 * Namespace : glass
 *
 * ── Tags ─────────────────────────────────────────────────────────────────────
 *
 *  <glass:tableStyles/>
 *    Emit global CSS. Call once in <head>.
 *
 *  <glass:table
 *      cols             = "Col1, Col2, …"      REQUIRED — comma-separated headers
 *      [id]                                     auto-generated UUID if omitted
 *      [caption]                                plain text OR raw HTML string
 *      [captionStyle]                           inline CSS applied to the caption element
 *      [searchable      = "true|false"]         default true
 *      [rowCount        = "true|false"]         default true
 *      [emptyText]                              default "No records found"
 *
 *      ── Row-click navigation ──────────────────────────────────────────────
 *      [rowController]                          Grails controller name
 *      [rowAction]                              Grails action name
 *      [rowParams]                              Extra static params map, e.g. [ac:'an']
 *      [rowIdAttr       = "data-row-id"]        data-* attribute on <tr> whose value
 *                                               is forwarded as the "id" URL param
 *      [rowTickCol]                             0-based column index to replace with the
 *                                               tick SVG on selection (default: last col = -1)
 *
 *      ── Toolbar nav buttons (rendered left of caption) ────────────────────
 *      [homeController] [homeAction]
 *      [homeParams]                             Extra params map for home link
 *      [homeIcon]                               FA icon class  (default fa-solid fa-house)
 *      [homeTooltip]                            Tooltip on home button (default: "Go back to main page")
 *
 *      [backController] [backAction]
 *      [backParams]                             Extra params map for back link
 *      [backIcon]                               FA icon class  (default fa-solid fa-chevron-left)
 *      [backTooltip]                            Tooltip on back button (default: "Go to previous page")
 *  >
 *    <tr data-row-id="${item.id}">…</tr>   <- rows go here
 *  </glass:table>
 *
 *  <glass:tableSearch tableId="…" [placeholder]/>
 *
 *  <glass:tableBadge text="…" [variant] [dot]/>
 *    variant: success | danger | warning | info | violet | blue | default
 *
 *  <glass:tableActions>…</glass:tableActions>
 *
 *  <glass:tableActionBtn
 *      icon        = "fa-solid fa-pencil"
 *      [controller] [action] [params]           generates href via buildLink (slot-aware)
 *      [href]                                   explicit href (overrides controller/action; app-relative
 *                                               paths starting with '/' are slot-prefixed too)
 *      [onclick]
 *      [variant]                                danger | success | info  (default: violet accent)
 *      [title]/>
 *
 * ── Slot-aware URLs ──────────────────────────────────────────────────────────
 *   Every app-relative URL (home, back, action buttons incl. explicit href, and
 *   row-click navigation) honours the request attribute "currentSlot":
 *     currentSlot present  ->  /u/$slot/$controller/$action?urlparams
 *     currentSlot absent   ->  /$controller/$action?urlparams
 *   (context path is preserved in front of either form)
 *
 * ── Button states ────────────────────────────────────────────────────────────
 *   Nav and action buttons share one accent colour per variant (--gt-accent).
 *   On hover / focus / active the border AND the icon take exactly the same
 *   colour (--gt-edge), so they always match:
 *     hover   — mouse over
 *     focus   — keyboard focus or after a click (plus a focus ring on keyboard focus)
 *     active  — mouse button held down: button presses in, stronger glow
 *
 * ── Background expected on the layout body ───────────────────────────────────
 *   background:
 *     radial-gradient(ellipse at 15% 25%, #3a6fff 0%, transparent 55%),
 *     radial-gradient(ellipse at 85% 10%, #a855f7 0%, transparent 50%),
 *     radial-gradient(ellipse at 70% 80%, #06b6d4 0%, transparent 55%),
 *     radial-gradient(ellipse at 10% 85%, #ec4899 0%, transparent 45%),
 *     #0d1230;
 */
@TagLib
@SuppressWarnings(['unused'])
class GlassTableTagLib {

  static namespace = 'glass'

  // ── Tooltip-styles delegate ───────────────────────────────────────────────
  // Property injection of cross-namespace taglibs is unreliable in Grails —
  // the bean may not be wired by the time the taglib is first used.
  // grailsApplication is inherited from WebAttributes and is always available;
  // we look up GlassTooltipStylesTagLib lazily via the Spring context.
  // Current buttons use plain HTML title attributes; call
  // tooltipStylesTagLib.emitTooltipStylesOnce() before emitting any
  // glass-tooltip markup if that changes.
  private GlassTooltipStylesTagLib getTooltipStylesTagLib() {
    grailsApplication.mainContext.getBean(GlassTooltipStylesTagLib)
  }

  // ── Slot-aware link building ─────────────────────────────────────────────
  // Every link in this taglib (home, back, action buttons incl. explicit href,
  // row-click navigation) goes through buildLink / applySlot, so they all behave
  // identically:
  //   request attribute "currentSlot" present -> {ctx}/u/{slot}/{controller}/{action}?params
  //   absent / blank                          -> {ctx}/{controller}/{action}?params

  /** Request attribute "currentSlot" as a trimmed String, or null when absent/blank. */
  private String resolveCurrentSlot() {
    String slot = request?.getAttribute('currentSlot')?.toString()?.trim()
    return slot ?: null
  }

  private static String encodeSegment(String value) {
    return URLEncoder.encode(value, 'UTF-8').replace('+', '%20')
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
   * createLink-based link builder. g.createLink produces the standard
   * /{controller}/{action}?params URL (hyphenation, encoding, context path);
   * applySlot then adds /u/{slot} when currentSlot is available.
   */
  String buildLink(Map linkAttrs) {
    try {
      return applySlot(g.createLink(linkAttrs).toString())
    } catch (ignored) {
      return '#'
    }
  }

  // ── rest of the class ────────────────────────────────────────────────────

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

  /* hover + focus + keyboard focus: border and icon share --gt-edge */
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

  /* keyboard focus ring */
  .gt-nav-btn:focus-visible,
  .gt-wrapper .gt-action-btn:focus-visible {
    outline: 2px solid var(--gt-edge);
    outline-offset: 2px;
  }

  /* active: mouse button held down — button presses in, icon keeps the border colour */
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

  /* links inside cells (incl. :visited) must not override the button colours */
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

  def table = { attrs, body ->
    def tableId = attrs.id ?: "gt-${UUID.randomUUID().toString().take(8)}"
    def cols = (attrs.cols ?: '').toString().split(',').collect { it.trim() }
    def caption = attrs.caption
    def captionStyle = attrs.captionStyle ?: ''
    def searchable = attrs.searchable?.toString() != 'false'
    def showCount = attrs.rowCount?.toString() != 'false'
    def emptyText = attrs.emptyText ?: 'No records found'

    def rowController = attrs.rowController
    def rowAction = attrs.rowAction
    def rowParams = attrs.rowParams instanceof Map ? attrs.rowParams : [:]
    def rowIdAttr = (attrs.rowIdAttr ?: 'data-row-id').toString()
    def rowTickCol = attrs.rowTickCol != null ? attrs.rowTickCol.toString() : '-1'
    boolean hasRowNav = rowController && rowAction

    String rowBaseUrl = ''
    if (hasRowNav) {
      def linkMap = [controller: rowController, action: rowAction]
      if (rowParams) {
        linkMap.params = rowParams
      }
      String built = buildLink(linkMap)
      String sep = built.contains('?') ? '&' : '?'
      rowBaseUrl = built + sep + 'id='
    }
    String rowBaseUrlJs = rowBaseUrl.replace('\\', '\\\\').replace("'", "\\'")

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

    out << '<div class="gt-scroll"><table class="gt-table" id="' + tableId + '"><thead><tr>'
    cols.each { col ->
      out << '<th><span class="gt-th-inner">' + col.encodeAsHTML() + '<span class="gt-sort-icon"><span></span><span></span></span></span></th>'
    }
    out << '</tr></thead><tbody>' + body() + '</tbody></table>'
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
    function updateState() {
      var visible = tbl.querySelectorAll('tbody tr:not(.gt-hidden)').length;
      var total   = tbl.querySelectorAll('tbody tr').length;
      if (cnt)   cnt.textContent = visible === 0 ? '0 rows' : visible + ' of ' + total + ' rows';
      if (empty) empty.style.display = visible === 0 ? 'block' : 'none';
    }

    var sortState = { col: -1, asc: true };
    tbl.querySelectorAll('thead th').forEach(function (th, ci) {
      th.addEventListener('click', function () {
        var asc = sortState.col === ci ? !sortState.asc : true;
        sortState = { col: ci, asc: asc };
        tbl.querySelectorAll('thead th').forEach(function (h) {
          h.classList.remove('gt-sorted', 'gt-sorted-asc', 'gt-sorted-desc');
        });
        th.classList.add('gt-sorted', asc ? 'gt-sorted-asc' : 'gt-sorted-desc');
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
        updateState();
      });
    });

    // Re-applies the current search text to every row, then refreshes the footer.
    // Also called by ListTableTagLib after infinite-scroll rows are appended.
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

    function drawTick(row, tickColIdx) {
      row.classList.add('gt-row-selected');
      var colIndex = (tickColIdx >= 0) ? tickColIdx : (row.cells.length - 1);
      var cell = row.cells[colIndex];
      if (!cell) return;
      cell.innerHTML = '';
      var NS      = 'http://www.w3.org/2000/svg';
      var svg     = document.createElementNS(NS, 'svg');
      svg.setAttribute('viewBox', '0 0 75.2 75.2');
      svg.setAttribute('class', 'gt-tick-svg');
      svg.setAttribute('aria-label', 'Selected');
      var circle  = document.createElementNS(NS, 'circle');
      circle.setAttribute('class', 'gt-tick-circle');
      circle.setAttribute('fill', 'none');
      circle.setAttribute('stroke', '#34d399');
      circle.setAttribute('stroke-width', '3');
      circle.setAttribute('stroke-miterlimit', '10');
      circle.setAttribute('cx', '35.1');
      circle.setAttribute('cy', '35.1');
      circle.setAttribute('r', '30.0');
      var polyline = document.createElementNS(NS, 'polyline');
      polyline.setAttribute('class', 'gt-tick-check');
      polyline.setAttribute('fill', 'none');
      polyline.setAttribute('stroke', '#34d399');
      polyline.setAttribute('stroke-width', '3');
      polyline.setAttribute('stroke-linecap', 'round');
      polyline.setAttribute('stroke-miterlimit', '10');
      polyline.setAttribute('points', '20,37 30,47 55,22');
      svg.appendChild(circle);
      svg.appendChild(polyline);
      cell.appendChild(svg);
    }
  '''

    out << "  var ROW_BASE_URL = '" + rowBaseUrlJs + "';"
    out << "  var ROW_ID_ATTR  = '" + rowIdAttr.encodeAsHTML() + "';"
    out << '  var ROW_TICK_COL = ' + rowTickCol + ';'
    out << '  var HAS_ROW_NAV  = ' + (hasRowNav ? 'true' : 'false') + ';'

    out << '''
    function bindRowClick(row) {
      if (!HAS_ROW_NAV) return;
      row.classList.add('gt-row-clickable');
      row.addEventListener('click', function (e) {
        if (e.target.closest('.gt-action-btn, .gt-actions, a, button')) return;
        var rowId = row.getAttribute(ROW_ID_ATTR);
        if (!rowId) return;
        drawTick(row, ROW_TICK_COL);
        setTimeout(function () {
          window.location.href = ROW_BASE_URL + encodeURIComponent(rowId);
        }, 300);
      });
    }

    tbl.querySelectorAll('tbody tr').forEach(function (r, i) {
      r.style.animationDelay = (i * 45) + 'ms';
      bindRowClick(r);
    });

    // Hooks used by subclasses (ListTableTagLib).
    tbl.gtBindRow     = bindRowClick;
    tbl.gtDrawTick    = drawTick;
    tbl.gtUpdateState = updateState;
    tbl.gtApplyFilter = applyFilter;

    updateState();
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
