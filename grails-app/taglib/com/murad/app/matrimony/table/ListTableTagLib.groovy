package com.murad.app.matrimony.table

import grails.gsp.TagLib
import groovy.json.JsonOutput

/**
 * ListTableTagLib  —  Clickable, infinite-scrolling glass table for Grails 6
 * Namespace : glass
 *
 * Extends GlassTableTagLib, so it inherits:
 *   - buildLink(Map)   slot-aware link helper
 *                      (currentSlot request attr -> /u/{slot}/{controller}/{action}?params,
 *                       otherwise standard createLink)
 *   - table / tableSearch / tableBadge / tableActions / tableActionBtn / tableStyles
 * and adds <glass:listTable>, which calls the inherited `table` tag and then
 * layers on bright styles + infinite scroll.
 *
 * Extra attributes (everything else is forwarded to glass:table):
 *   moreController / moreAction / moreParams   endpoint returning a JSON array (also slot-aware)
 *   fields       = "name,age,city"             JSON properties rendered as <td>s, in order
 *   idField      = "id"                        JSON property used for the row id attribute
 *   max          = 10                          page size
 *   offset       = max                         first offset to request
 *   badgeText / badgeVariant / badgeDot        trailing badge cell on appended rows
 *   loadingText  = "Loading more…"
 *   scrollThreshold = 200                      px from page bottom that triggers a load
 *   bright       = "true|false"                bright tick + badge palette (default true)
 *
 * Row count footer and search behave exactly like glass:table:
 * "0 rows" when there is no data / no match, otherwise "N of M rows".
 */
@TagLib
@SuppressWarnings(['unused'])
class ListTableTagLib extends GlassTableTagLib {

  static namespace = 'glass'

  /** Attributes consumed here; not forwarded to the inherited table tag. */
  private static final List<String> LIST_ONLY_ATTRS = [
    'moreController', 'moreAction', 'moreParams',
    'fields', 'idField', 'max', 'offset',
    'badgeText', 'badgeVariant', 'badgeDot',
    'loadingText', 'scrollThreshold', 'bright'
  ]

  /** [variant, background, border, text, dot] — bright badge palette. */
  private static final List<List<String>> BRIGHT_BADGES = [
    ['default', 'rgba(147,197,253,.20)', 'rgba(191,219,254,.75)', '#e0eeff', '#bfdbfe'],
    ['success', 'rgba(74,222,128,.22)', 'rgba(134,239,172,.85)', '#a7ffc8', '#4ade80'],
    ['danger', 'rgba(255,105,180,.22)', 'rgba(255,160,205,.85)', '#ffc2de', '#ff6eb4'],
    ['warning', 'rgba(253,224,71,.22)', 'rgba(254,240,138,.85)', '#fff3a3', '#fde047'],
    ['info', 'rgba(34,230,255,.22)', 'rgba(103,232,249,.85)', '#b8f5ff', '#22e6ff'],
    ['violet', 'rgba(167,139,250,.28)', 'rgba(196,181,253,.85)', '#ece4ff', '#c4b5fd'],
    ['blue', 'rgba(96,165,250,.26)', 'rgba(147,197,253,.85)', '#d4e6ff', '#7dbbff']
  ]

  /** Brighter tick + badges, scoped to one table (also covers rows appended later). */
  private static String brightStyles(String tableId) {
    String t = '#' + tableId
    StringBuilder sb = new StringBuilder('<style>\n')
    sb << t << ' .gt-tick-svg { filter: drop-shadow(0 0 4px rgba(74,255,160,.95)) drop-shadow(0 0 10px rgba(74,255,160,.55)); }\n'
    sb << t << ' .gt-tick-svg .gt-tick-circle, ' << t << ' .gt-tick-svg .gt-tick-check { stroke: #4dffa6; stroke-width: 4; }\n'
    sb << t << ' tbody tr.gt-row-selected td { background-color: rgba(74,255,160,.14) !important; }\n'
    BRIGHT_BADGES.each { v ->
      sb << t << ' .gt-badge-' << v[0] << ' { background:' << v[1] << '; border-color:' << v[2] << '; color:' << v[3] << '; font-weight:600; }\n'
      sb << t << ' .gt-badge-' << v[0] << ' .gt-badge-dot { background:' << v[4] << '; box-shadow: 0 0 8px ' << v[4] << ', 0 0 14px ' << v[4] << '; }\n'
    }
    sb << '</style>'
    return sb.toString()
  }

  // buildLink(Map) is inherited from GlassTableTagLib — no duplicate here.

  /** Drop null / blank values so we never emit "slot=null". */
  private static Map cleanParams(Object p) {
    Map result = [:]
    if (p instanceof Map) {
      p.each { k, v ->
        if (v != null && v.toString().trim()) {
          result[k] = v
        }
      }
    }
    return result
  }

  /** JSON-encode a value for safe inlining inside <script>. */
  private static String js(Object value) {
    return JsonOutput.toJson(value).replace('</', '<\\/')
  }

  private static int toInt(Object v, int fallback) {
    try {
      return v != null ? v.toString().trim().toInteger() : fallback
    } catch (ignored) {
      return fallback
    }
  }

  def listTable = { attrs, body ->
    String tableId = (attrs.id ?: "lt-${UUID.randomUUID().toString().take(8)}").toString()

    // ── Build attrs for the inherited table tag ────────────────────────────
    Map tableAttrs = new LinkedHashMap(attrs)
    LIST_ONLY_ATTRS.each { tableAttrs.remove(it) }
    tableAttrs.id = tableId
    if (tableAttrs.rowParams != null) {
      tableAttrs.rowParams = cleanParams(tableAttrs.rowParams)
    }

    // ── Styles for the loading indicator (once per request) ────────────────
    if (!request.getAttribute('_listTableStylesEmitted')) {
      request.setAttribute('_listTableStylesEmitted', true)
      out << '''<style>
  .lt-loading {
    display: none; align-items: center; gap: 10px;
    padding: 14px 20px; margin-top: 12px;
    border-radius: 12px;
    background: rgba(58,111,255,.07);
    border: 1px solid rgba(167,139,250,.18);
    color: rgba(160,180,255,.55);
    font-size: 12px; font-family: 'DM Mono', monospace; letter-spacing: .08em;
  }
  .lt-loading i { animation: ltSpin 1s linear infinite; padding: 0; margin: 0; }
  @keyframes ltSpin { to { transform: rotate(360deg); } }
  </style>'''
    }

    // Inherited tag closure: writes straight to `out`, so don't `out <<` its result.
    table(tableAttrs, body)

    if (attrs.bright?.toString() != 'false') {
      out << brightStyles(tableId)
    }

    // ── Infinite scroll (optional) ─────────────────────────────────────────
    if (!(attrs.moreController && attrs.moreAction && attrs.fields)) {
      return
    }

    Map moreLink = [controller: attrs.moreController, action: attrs.moreAction]
    Map moreParams = cleanParams(attrs.moreParams)
    if (moreParams) {
      moreLink.params = moreParams
    }
    // Slot-aware: /u/{slot}/{controller}/{action}?params or standard createLink.
    String built = buildLink(moreLink)
    if (built == '#') {
      return
    }
    String moreUrl = built + (built.contains('?') ? '&' : '?')

    List<String> fields = attrs.fields.toString().split(',').collect { it.trim() }.findAll { it }
    String idField = (attrs.idField ?: 'id').toString()
    String rowIdAttr = (attrs.rowIdAttr ?: 'data-row-id').toString()
    int max = toInt(attrs.max, 10)
    int offset = toInt(attrs.offset, max)
    int threshold = toInt(attrs.scrollThreshold, 200)
    String loadingText = (attrs.loadingText ?: 'Loading more\u2026').toString()

    String badgeHtml = ''
    if (attrs.badgeText) {
      String variant = (attrs.badgeVariant ?: 'violet').toString().encodeAsHTML()
      String dot = attrs.badgeDot?.toString() != 'false' ? '<span class="gt-badge-dot"></span>' : ''
      badgeHtml = '<span class="gt-badge gt-badge-' + variant + '">' + dot + attrs.badgeText.toString().encodeAsHTML() + '</span>'
    }

    out << '<div id="' + tableId.encodeAsHTML() + '-loading" class="lt-loading">'
    out << '<i class="fa-solid fa-spinner"></i><span>' + loadingText.encodeAsHTML() + '</span></div>'

    out << '<script>'
    out << '(function () {'
    out << "  'use strict';"
    out << '  var TABLE_ID  = ' + js(tableId) + ';'
    out << '  var MORE_URL  = ' + js(moreUrl) + ';'
    out << '  var FIELDS    = ' + js(fields) + ';'
    out << '  var ID_FIELD  = ' + js(idField) + ';'
    out << '  var ID_ATTR   = ' + js(rowIdAttr) + ';'
    out << '  var BADGE     = ' + js(badgeHtml) + ';'
    out << '  var MAX       = ' + max + ';'
    out << '  var offset    = ' + offset + ';'
    out << '  var THRESHOLD = ' + threshold + ';'
    out << '''
    var tbl = document.getElementById(TABLE_ID);
    if (!tbl) return;
    var tbody     = tbl.querySelector('tbody');
    var loadingEl = document.getElementById(TABLE_ID + '-loading');
    var counter   = tbody.querySelectorAll('tr').length;
    var loading   = false;
    var exhausted = false;

    function isNearBottom() {
      return (window.innerHeight + window.scrollY) >= document.body.offsetHeight - THRESHOLD;
    }

    // Re-applies the search filter (if any) to the new rows and refreshes the
    // footer: "0 rows" when nothing is visible, otherwise "N of M rows".
    function refreshState() {
      if (tbl.gtApplyFilter) tbl.gtApplyFilter();
      else if (tbl.gtUpdateState) tbl.gtUpdateState();
    }

    function buildRow(item) {
      var tr = document.createElement('tr');
      tr.setAttribute(ID_ATTR, item[ID_FIELD]);

      FIELDS.forEach(function (f) {
        var td = document.createElement('td');
        td.textContent = item[f] == null ? '' : item[f];
        tr.appendChild(td);
      });

      if (BADGE) {
        var tdBadge = document.createElement('td');
        tdBadge.innerHTML = BADGE;
        tr.appendChild(tdBadge);
      }

      tr.style.animationDelay = (counter * 45) + 'ms';
      counter++;
      return tr;
    }

    function loadMore() {
      if (loading || exhausted) return;
      loading = true;
      if (loadingEl) loadingEl.style.display = 'flex';

      fetch(MORE_URL + 'offset=' + offset + '&max=' + MAX, { credentials: 'same-origin' })
        .then(function (r) { return r.json(); })
        .then(function (data) {
          if (!data || data.length === 0) {
            exhausted = true;
            return;
          }
          data.forEach(function (item) {
            var tr = buildRow(item);
            tbody.appendChild(tr);
            if (tbl.gtBindRow) tbl.gtBindRow(tr);
          });
          offset += MAX;
          if (data.length < MAX) exhausted = true;
          refreshState();
        })
        .catch(function (err) { console.error('Load error:', err); })
        .finally(function () {
          loading = false;
          if (loadingEl) loadingEl.style.display = 'none';
          if (!exhausted && isNearBottom()) loadMore();
        });
    }

    window.addEventListener('scroll', function () {
      if (isNearBottom()) loadMore();
    });
  '''
    out << '})();'
    out << '</script>'
  }
}
