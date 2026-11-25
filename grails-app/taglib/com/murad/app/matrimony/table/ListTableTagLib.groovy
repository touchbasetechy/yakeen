package com.murad.app.matrimony.table

import grails.gsp.TagLib

/**
 * ListTableTagLib  -  glass:listTable = glass:table + infinite scroll.
 *
 * Inherits everything from GlassTableTagLib by calling its renderTable(...) method
 * (toolbar, caption, search, header row, sort, AJAX row-click with status icons + alerts,
 * row removal, row count, empty state, slot-aware links and the `data` first-load rows).
 * Every attribute of glass:table works unchanged, including headerHtml / headerAlign
 * (free-form HTML row rendered beneath the caption/search toolbar), and headerData
 * (Map bound into header elements carrying data-gt-bind="key").
 *
 * Consumed here (not forwarded):
 *   moreController / moreAction / moreParams   endpoint returning a JSON array (slot-aware)
 *   moreMethod       GET | POST                GET (default): ?offset=&max=  ->  JSON response
 *                                              POST: JSON body {offset, max, ...moreParams}
 *   max              10                        page size
 *   offset           (default: rows already rendered in the body)
 *   offsetOnRemove   shift | keep              shift (default): every row removed by a successful
 *                                              row click lowers the offset by one, because the server
 *                                              no longer returns it. When all rows are gone the next
 *                                              set is therefore requested at offset 0.
 *                                              keep: the offset is left alone (the removed row still
 *                                              exists server-side in the list being paged).
 *   loadingText, scrollThreshold, bright
 *
 * Refill: when the last row is removed (event gt:empty from glass:table) the next set of rows
 * (e.g. hobbies) is fetched with the same AJAX call used for infinite scroll and appended.
 *
 * Exhausted refill: if that refill fetch returns no rows (and the table is still empty) this tag
 * fires  gt:noMoreRows  on the <table>. GlassTableTagLib listens to it and, when the headerHtml
 * contains a glass:select, removes the currently selected option (e.g. the hobby category that
 * has no hobbies left), selects the next option and fires 'change' on the select so the page
 * loads the next one.
 *
 * Header-driven reload (headerController / headerAction / headerParams on glass:table):
 *   - the first page is requested with offset=0&max=<max> in addition to the header control values
 *   - after the rows are replaced (event gt:dataLoaded) infinite scroll restarts from there
 *   - the current header control values are also sent with every load-more request, so the
 *     appended pages match the active filter (the endpoint just has to read them)
 *
 * Client hook: table.gtAppendData(arrayOfItems) appends rows at runtime.
 */
@TagLib
@SuppressWarnings(['unused'])
class ListTableTagLib extends GlassTableTagLib {

  static namespace = 'glass'

  private static final List<String> LIST_ONLY_ATTRS = [
    'moreController', 'moreAction', 'moreParams', 'moreMethod',
    'max', 'offset', 'offsetOnRemove',
    'loadingText', 'scrollThreshold', 'bright'
  ]

  /** [variant, background, border, text, dot] */
  private static final List<List<String>> BRIGHT_BADGES = [
    ['default', 'rgba(147,197,253,.20)', 'rgba(191,219,254,.75)', '#e0eeff', '#bfdbfe'],
    ['success', 'rgba(74,222,128,.22)', 'rgba(134,239,172,.85)', '#a7ffc8', '#4ade80'],
    ['danger', 'rgba(255,105,180,.22)', 'rgba(255,160,205,.85)', '#ffc2de', '#ff6eb4'],
    ['warning', 'rgba(253,224,71,.22)', 'rgba(254,240,138,.85)', '#fff3a3', '#fde047'],
    ['info', 'rgba(34,230,255,.22)', 'rgba(103,232,249,.85)', '#b8f5ff', '#22e6ff'],
    ['violet', 'rgba(167,139,250,.28)', 'rgba(196,181,253,.85)', '#ece4ff', '#c4b5fd'],
    ['blue', 'rgba(96,165,250,.26)', 'rgba(147,197,253,.85)', '#d4e6ff', '#7dbbff']
  ]

  // ── helpers ───────────────────────────────────────────────────────────────

  private static String brightStyles(String tableId) {
    String t = '#' + tableId
    StringBuilder sb = new StringBuilder('<style>\n')
    sb << t << ' .gt-tick-svg .gt-tick-circle, ' << t << ' .gt-tick-svg .gt-tick-check { stroke: #4dffa6; stroke-width: 4; }\n'
    sb << t << ' .gt-tick-svg.gt-icon-success { filter: drop-shadow(0 0 4px rgba(74,255,160,.95)) drop-shadow(0 0 10px rgba(74,255,160,.55)); }\n'
    sb << t << ' tbody tr.gt-row-selected td { background-color: rgba(74,255,160,.14) !important; }\n'
    BRIGHT_BADGES.each { v ->
      sb << t << ' .gt-badge-' << v[0] << ' { background:' << v[1] << '; border-color:' << v[2] << '; color:' << v[3] << '; font-weight:600; }\n'
      sb << t << ' .gt-badge-' << v[0] << ' .gt-badge-dot { background:' << v[4] << '; box-shadow: 0 0 8px ' << v[4] << ', 0 0 14px ' << v[4] << '; }\n'
    }
    sb << '</style>'
    return sb.toString()
  }

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

  private static int toInt(Object v, int fallback) {
    try {
      return v != null ? v.toString().trim().toInteger() : fallback
    } catch (ignored) {
      return fallback
    }
  }

  // ── tag ───────────────────────────────────────────────────────────────────

  def listTable = { attrs, body ->
    String tableId = (attrs.id ?: "lt-${UUID.randomUUID().toString().take(8)}").toString()

    // headerHtml / headerAlign are NOT in LIST_ONLY_ATTRS, so they are forwarded to renderTable.
    Map tableAttrs = new LinkedHashMap(attrs)
    LIST_ONLY_ATTRS.each { tableAttrs.remove(it) }
    tableAttrs.id = tableId
    if (tableAttrs.rowParams != null) {
      tableAttrs.rowParams = cleanParams(tableAttrs.rowParams)
    }

    emitLoadingStyles()
    renderTable(tableAttrs, body)          // <- the whole glass:table, inherited

    if (attrs.bright?.toString() != 'false') {
      out << brightStyles(tableId)
    }
    renderInfiniteScroll(attrs, tableId)
  }

  // ── pieces ────────────────────────────────────────────────────────────────

  protected void emitLoadingStyles() {
    if (request.getAttribute('_listTableStylesEmitted')) {
      return
    }
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

  protected void renderInfiniteScroll(Map attrs, String tableId) {
    if (!(attrs.moreController && attrs.moreAction && attrs.fields)) {
      return
    }

    // Slot-aware: /u/$slot/$controller/$action?queryparams  or  /$controller/$action?queryparams
    Map moreLink = [controller: attrs.moreController, action: attrs.moreAction]
    Map moreParams = cleanParams(attrs.moreParams)
    String method = (attrs.moreMethod ?: 'GET').toString().toUpperCase() == 'POST' ? 'POST' : 'GET'
    if (moreParams && method == 'GET') {
      moreLink.params = moreParams
    }
    String built = buildLink(moreLink)
    if (built == '#') {
      out << '<script>console.error("[listTable] could not build the moreController/moreAction link");</script>'
      return
    }

    List<String> fields = attrs.fields.toString().split(',').collect { it.trim() }.findAll { it }
    String idField = (attrs.idField ?: 'id').toString()
    String rowIdAttr = (attrs.rowIdAttr ?: 'data-row-id').toString()
    int max = toInt(attrs.max, 10)
    int threshold = toInt(attrs.scrollThreshold, 200)
    String offsetJs = (attrs.offset != null && attrs.offset.toString().trim()) ? String.valueOf(toInt(attrs.offset, 0)) : 'null'
    String loadingText = (attrs.loadingText ?: 'Loading more\u2026').toString()
    boolean shiftOnRemove = (attrs.offsetOnRemove ?: 'shift').toString() != 'keep'

    String badgeHtml = buildBadgeHtml(attrs)

    out << '<div id="' + tableId.encodeAsHTML() + '-loading" class="lt-loading">'
    out << '<i class="fa-solid fa-spinner"></i><span>' + loadingText.encodeAsHTML() + '</span></div>'

    out << '<script>'
    out << '(function () {'
    out << "  'use strict';"
    out << '  var TABLE_ID    = ' + jsLiteral(tableId) + ';'
    out << '  var MORE_URL    = ' + jsLiteral(built) + ';'
    out << '  var MORE_METHOD = ' + jsLiteral(method) + ';'
    out << '  var MORE_BODY   = ' + jsLiteral(method == 'POST' ? moreParams : [:]) + ';'
    out << '  var FIELDS      = ' + jsLiteral(fields) + ';'
    out << '  var ID_FIELD    = ' + jsLiteral(idField) + ';'
    out << '  var ID_ATTR     = ' + jsLiteral(rowIdAttr) + ';'
    out << '  var BADGE       = ' + jsLiteral(badgeHtml) + ';'
    out << '  var MAX         = ' + max + ';'
    out << '  var OFFSET_ATTR = ' + offsetJs + ';'
    out << '  var THRESHOLD   = ' + threshold + ';'
    out << '  var SHIFT_ON_REMOVE = ' + (shiftOnRemove ? 'true' : 'false') + ';'
    out << '''
    var tbl = document.getElementById(TABLE_ID);
    if (!tbl) return;
    var tbody     = tbl.querySelector('tbody');
    var loadingEl = document.getElementById(TABLE_ID + '-loading');
    var counter   = tbody.querySelectorAll('tr').length;
    var offset    = OFFSET_ATTR !== null ? OFFSET_ATTR : counter;
    var loading   = false;
    var exhausted = false;
    var failed    = false;
    var refillAfterEmpty = false;   // true while refilling after the last row was removed (gt:empty)
    var loadGen   = 0;     // bumped on every header reload so stale page requests are ignored
    var LOADING_TEXT = loadingEl ? loadingEl.querySelector('span').textContent : '';

    function isNearBottom() {
      return (window.innerHeight + window.scrollY) >= document.body.offsetHeight - THRESHOLD;
    }

    function refreshState() {
      if (tbl.gtApplySort)  tbl.gtApplySort();
      if (tbl.gtApplyFilter) tbl.gtApplyFilter();
      else if (tbl.gtUpdateState) tbl.gtUpdateState();
    }

    function buildRow(item) {
      var tr = document.createElement('tr');
      tr.setAttribute(ID_ATTR, item[ID_FIELD] == null ? '' : item[ID_FIELD]);
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
      tr.style.animationDelay = (Math.min(counter, 20) * 45) + 'ms';
      counter++;
      return tr;
    }

    // Single entry point for every row that arrives after the first load.
    function appendItems(items) {
      items.forEach(function (item) {
        var tr = buildRow(item);
        tbody.appendChild(tr);
        if (tbl.gtBindRow) tbl.gtBindRow(tr);
      });
      refreshState();
    }

    tbl.gtAppendData = function (items) {
      if (!Array.isArray(items) || items.length === 0) return;
      appendItems(items);
      offset += items.length;
    };

    function notifyError(msg) {
      if (typeof window.glassAlert === 'function') window.glassAlert(msg, 'error', 6000);
    }

    function fail(msg) {
      exhausted = true;
      failed = true;
      console.error('[listTable]', msg);
      notifyError(msg);
      if (loadingEl) {
        loadingEl.querySelector('span').textContent = msg;
        loadingEl.style.display = 'flex';
      }
    }

    // Header control values (set by glass:table's header loading) travel with every page request.
    tbl.gtHeaderExtra = function () { return { offset: 0, max: MAX }; };

    function buildRequest() {
      var opts = { credentials: 'same-origin', headers: { 'Accept': 'application/json' } };
      var hv = tbl.gtHeaderState || {};
      if (MORE_METHOD === 'POST') {
        var payload = {};
        Object.keys(hv).forEach(function (k) { payload[k] = hv[k]; });
        Object.keys(MORE_BODY).forEach(function (k) { payload[k] = MORE_BODY[k]; });
        payload.offset = offset;
        payload.max = MAX;
        opts.method = 'POST';
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(payload);
        return { url: MORE_URL, opts: opts };
      }
      var sep = MORE_URL.indexOf('?') < 0 ? '?' : '&';
      opts.method = 'GET';
      var extra = '';
      Object.keys(hv).forEach(function (k) {
        if (k === 'offset' || k === 'max' || hv[k] == null) return;
        (Array.isArray(hv[k]) ? hv[k] : [hv[k]]).forEach(function (one) {
          extra += '&' + encodeURIComponent(k) + '=' + encodeURIComponent(one);
        });
      });
      return { url: MORE_URL + sep + 'offset=' + offset + '&max=' + MAX + extra, opts: opts };
    }

    function loadMore() {
      if (loading || exhausted) return;
      loading = true;
      failed = false;
      tbl.gtLoading = true;                       // suppress the "no records" flash
      if (tbl.gtUpdateState) tbl.gtUpdateState();
      if (loadingEl) {
        loadingEl.querySelector('span').textContent = LOADING_TEXT;
        loadingEl.style.display = 'flex';
      }

      var gen = loadGen;
      var req = buildRequest();
      fetch(req.url, req.opts)
        .then(function (r) {
          if (!r.ok) throw new Error('HTTP ' + r.status);
          return r.json();
        })
        .then(function (json) {
          if (gen !== loadGen) return;
          var data = Array.isArray(json) ? json : null;
          if (!data && json && typeof json === 'object') {
            Object.keys(json).some(function (k) {
              if (Array.isArray(json[k])) { data = json[k]; return true; }
            });
          }
          if (!data) throw new Error('Response is not a JSON array');
          if (data.length === 0) { exhausted = true; return; }
          appendItems(data);
          offset += data.length;
          if (data.length < MAX) exhausted = true;
        })
        .catch(function (err) { if (gen === loadGen) fail('Could not load more rows: ' + err.message); })
        .finally(function () {
          if (gen !== loadGen) return;
          loading = false;
          tbl.gtLoading = false;
          if (tbl.gtUpdateState) tbl.gtUpdateState();
          if (loadingEl && !failed) loadingEl.style.display = 'none';

          // Refill after the last row was removed came back empty: nothing left to show.
          // GlassTableTagLib reacts by removing the exhausted option from a header glass:select.
          if (refillAfterEmpty && !failed) {
            refillAfterEmpty = false;
            if (exhausted && tbody.querySelectorAll('tr').length === 0) {
              tbl.dispatchEvent(new CustomEvent('gt:noMoreRows', { detail: {} }));
            }
          }

          if (!exhausted && isNearBottom()) loadMore();
        });
    }

    // A row was removed after HTTP 200: the server list shrank by one.
    tbl.addEventListener('gt:rowRemoved', function () {
      if (SHIFT_ON_REMOVE) offset = Math.max(0, offset - 1);
      if (!exhausted && isNearBottom()) loadMore();
    });

    // Any header click / selection: paging restarts at offset 0 immediately.
    tbl.addEventListener('gt:headerReload', function () {
      loadGen++;
      offset = 0;
      counter = 0;
      exhausted = false;
      failed = false;
      loading = false;
      refillAfterEmpty = false;
      if (loadingEl) loadingEl.style.display = 'none';
    });

    // Rows were replaced from the header controls: restart paging after them.
    tbl.addEventListener('gt:dataLoaded', function (e) {
      var n = (e.detail && e.detail.items) ? e.detail.items.length : 0;
      counter = n;
      offset = n;
      exhausted = n < MAX;
      if (!exhausted && isNearBottom()) loadMore();
    });

    // Every row is gone: fetch the next set (e.g. hobbies) into the table.
    tbl.addEventListener('gt:empty', function () {
      counter = 0;
      exhausted = false;
      refillAfterEmpty = true;
      loadMore();
    });

    window.addEventListener('scroll', function () {
      if (isNearBottom()) loadMore();
    });

    setTimeout(function () { if (isNearBottom()) loadMore(); }, 0);
  '''
    out << '})();'
    out << '</script>'
  }
}
