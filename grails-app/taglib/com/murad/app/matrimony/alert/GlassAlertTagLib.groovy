package com.murad.app.matrimony.alert

import grails.gsp.TagLib
import groovy.json.JsonOutput

/**
 * GlassAlertTagLib  -  glassmorphic toast alerts.
 *
 * CHANGES in this version
 *   - NEW <glass:alertRuntime/>: emits the CSS + the client function
 *       window.glassAlert(message, type, duration, id)
 *     once per request. GlassTableTagLib calls it automatically; you can also put it
 *     in your layout. Any script can now raise an alert at runtime.
 *   - <glass:alert> is now a thin wrapper that calls window.glassAlert on page load.
 *   - Messages are inserted with textContent (no HTML injection).
 *
 * Tags
 *   <glass:alertRuntime/>
 *   <glass:alert message="…" [type="success|error|warning|info"] [duration="5000"] [id="…"]/>
 */
@TagLib
class GlassAlertTagLib {

  static namespace = 'glass'

  private static String js(Object value) {
    return JsonOutput.toJson(value).replace('</', '<\\/')
  }

  def alertRuntime = { attrs ->
    if (request.getAttribute('_glassAlertRuntimeEmitted')) {
      return
    }
    request.setAttribute('_glassAlertRuntimeEmitted', true)

    out << '''<style>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap');

.glass-alert-wrapper {
    font-family: 'DM Sans', sans-serif;
    position: fixed; top: 1.5rem; right: 1.5rem; z-index: 9999;
    display: flex; flex-direction: column; gap: 0.75rem;
    pointer-events: none;
}
.glass-alert {
    pointer-events: all;
    display: flex; align-items: flex-start; gap: 0.85rem;
    min-width: 280px; max-width: 420px;
    padding: 1rem 1.15rem; border-radius: 16px;
    border: 1px solid rgba(255,255,255,0.18);
    backdrop-filter: blur(18px) saturate(180%);
    -webkit-backdrop-filter: blur(18px) saturate(180%);
    box-shadow: 0 8px 32px rgba(0,0,0,0.18), 0 1.5px 0 rgba(255,255,255,0.12) inset;
    animation: glassSlideIn 0.38s cubic-bezier(0.22,1,0.36,1) forwards;
    transition: opacity 0.4s ease, transform 0.4s ease;
    position: relative; overflow: hidden;
}
.glass-alert::before {
    content: ''; position: absolute; inset: 0; border-radius: inherit;
    background: linear-gradient(135deg, rgba(255,255,255,0.15) 0%, rgba(255,255,255,0.03) 60%, transparent 100%);
    pointer-events: none;
}
.glass-alert.glass-info    { background: rgba(14,165,233,0.18); border-color: rgba(56,189,248,0.35); }
.glass-alert.glass-success { background: rgba(16,185,129,0.18); border-color: rgba(52,211,153,0.35); }
.glass-alert.glass-warning { background: rgba(245,158,11,0.18); border-color: rgba(251,191,36,0.35); }
.glass-alert.glass-error   { background: rgba(239,68,68,0.18);  border-color: rgba(248,113,113,0.35); }

.glass-alert-icon { flex-shrink: 0; width: 22px; height: 22px; margin-top: 1px; }
.glass-info    .glass-alert-icon { color: #38bdf8; }
.glass-success .glass-alert-icon { color: #34d399; }
.glass-warning .glass-alert-icon { color: #fbbf24; }
.glass-error   .glass-alert-icon { color: #f87171; }

.glass-alert-body { flex: 1; }
.glass-alert-title {
    font-size: 0.78rem; font-weight: 600; letter-spacing: 0.06em; text-transform: uppercase;
    opacity: 0.65; margin-bottom: 0.2rem; color: #fff;
}
.glass-alert-message { font-size: 0.93rem; font-weight: 400; color: rgba(255,255,255,0.92); line-height: 1.45; }

.glass-alert-close {
    flex-shrink: 0; cursor: pointer; background: none; border: none; padding: 0;
    color: rgba(255,255,255,0.5); line-height: 1; transition: color 0.2s; margin-top: 1px;
}
.glass-alert-close:hover { color: rgba(255,255,255,0.95); }

.glass-alert-progress {
    position: absolute; bottom: 0; left: 0; height: 3px; width: 100%;
    border-radius: 0 0 16px 16px; transform-origin: left;
}
.glass-info    .glass-alert-progress { background: rgba(56,189,248,0.75); }
.glass-success .glass-alert-progress { background: rgba(52,211,153,0.75); }
.glass-warning .glass-alert-progress { background: rgba(251,191,36,0.75); }
.glass-error   .glass-alert-progress { background: rgba(248,113,113,0.75); }

@keyframes glassSlideIn {
    from { opacity: 0; transform: translateX(60px) scale(0.95); }
    to   { opacity: 1; transform: translateX(0) scale(1); }
}
@keyframes glassSlideOut {
    from { opacity: 1; transform: translateX(0) scale(1); max-height: 120px; margin-bottom: 0; }
    to   { opacity: 0; transform: translateX(60px) scale(0.95); max-height: 0; margin-bottom: -0.75rem; }
}
.glass-alert.dismissing { animation: glassSlideOut 0.4s cubic-bezier(0.55,0,1,0.45) forwards; }
</style>
<script>
(function () {
    if (window.glassAlert) return;

    var ICONS = {
        info:    '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>',
        success: '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
        warning: '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        error:   '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>'
    };
    var LABELS = { info: 'Info', success: 'Success', warning: 'Warning', error: 'Error' };
    var seq = 0;

    function getWrapper() {
        var w = document.getElementById('glass-alert-wrapper');
        if (!w) {
            w = document.createElement('div');
            w.id = 'glass-alert-wrapper';
            w.className = 'glass-alert-wrapper';
            document.body.appendChild(w);
        }
        return w;
    }

    function dismiss(el) {
        if (el.dataset.dismissed) return;
        el.dataset.dismissed = '1';
        el.classList.add('dismissing');
        setTimeout(function () { el.remove(); }, 420);
    }

    // window.glassAlert('Saved', 'success', 5000)   (duration 0 = stays until closed)
    window.glassAlert = function (message, type, duration, id) {
        if (!message) return null;
        if (!ICONS[type]) type = 'info';
        duration = (duration === undefined || duration === null || isNaN(Number(duration))) ? 5000 : Number(duration);
        id = id || ('glass-alert-' + Date.now() + '-' + (seq++));

        var el = document.createElement('div');
        el.id = id;
        el.className = 'glass-alert glass-' + type;
        el.setAttribute('role', type === 'error' ? 'alert' : 'status');
        el.innerHTML =
            ICONS[type] +
            '<div class="glass-alert-body">' +
                '<div class="glass-alert-title"></div>' +
                '<div class="glass-alert-message"></div>' +
            '</div>' +
            '<button type="button" class="glass-alert-close" aria-label="Close">' +
                '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>' +
            '</button>';
        el.querySelector('.glass-alert-title').textContent = LABELS[type];
        el.querySelector('.glass-alert-message').textContent = String(message);
        el.querySelector('.glass-alert-close').addEventListener('click', function () { dismiss(el); });
        getWrapper().appendChild(el);

        if (duration > 0) {
            var prog = document.createElement('div');
            prog.className = 'glass-alert-progress';
            prog.style.transition = 'transform ' + duration + 'ms linear';
            el.appendChild(prog);
            requestAnimationFrame(function () {
                requestAnimationFrame(function () { prog.style.transform = 'scaleX(0)'; });
            });
            setTimeout(function () { dismiss(el); }, duration);
        }
        return el;
    };
})();
</script>'''
  }

  def alert = { attrs, body ->
    def message = attrs.message ?: body()?.toString()?.trim() ?: ''
    def type = (attrs.type ?: 'info').toString()
    def duration = attrs.duration != null ? attrs.duration as int : 5000
    def id = (attrs.id ?: "glass-alert-${System.nanoTime()}").toString()

    if (!message) {
      return
    }
    if (!(type in ['success', 'error', 'warning', 'info'])) {
      type = 'info'
    }

    alertRuntime([:])

    out << '<script>(function () {' +
      'var run = function () { window.glassAlert(' + js(message.toString()) + ',' + js(type) + ',' + duration + ',' + js(id) + '); };' +
      'if (document.readyState === "loading") { document.addEventListener("DOMContentLoaded", run); } else { run(); }' +
      '})();</script>'
  }
}
