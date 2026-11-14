package com.murad.app.matrimony

import grails.gsp.TagLib

@TagLib
class GlassAlertTagLib {

  static namespace = 'glass'

  /**
   * Renders a glassmorphic alert component.
   *
   * @attr message REQUIRED The alert message to display.
   * @attr type    The alert type: 'success' | 'error' | 'warning' | 'info' (default: 'info')
   * @attr duration Auto-dismiss duration in milliseconds (default: 5000). Set to 0 to disable auto-dismiss.
   * @attr id      Optional DOM id for the alert element.
   */
  def alert = { attrs, body ->
    def message = attrs.message ?: body()?.toString()?.trim() ?: ''
    def type = attrs.type ?: 'info'
    def duration = attrs.duration != null ? attrs.duration as int : 5000
    def id = attrs.id ?: "glass-alert-${System.nanoTime()}"

    if (!message) return

    // Validate type
    def allowedTypes = ['success', 'error', 'warning', 'info']
    if (!(type in allowedTypes)) type = 'info'

    out << """
<style>
/* ── Glass Alert: base ───────────────────────────────────────── */
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&display=swap');

.glass-alert-wrapper {
    font-family: 'DM Sans', sans-serif;
    position: fixed;
    top: 1.5rem;
    right: 1.5rem;
    z-index: 9999;
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    pointer-events: none;
}

.glass-alert {
    pointer-events: all;
    display: flex;
    align-items: flex-start;
    gap: 0.85rem;
    min-width: 280px;
    max-width: 420px;
    padding: 1rem 1.15rem;
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    backdrop-filter: blur(18px) saturate(180%);
    -webkit-backdrop-filter: blur(18px) saturate(180%);
    box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.18),
        0 1.5px 0 rgba(255, 255, 255, 0.12) inset;
    animation: glassSlideIn 0.38s cubic-bezier(0.22, 1, 0.36, 1) forwards;
    transition: opacity 0.4s ease, transform 0.4s ease;
    position: relative;
    overflow: hidden;
}

/* Shimmer sheen overlay */
.glass-alert::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    background: linear-gradient(
        135deg,
        rgba(255, 255, 255, 0.15) 0%,
        rgba(255, 255, 255, 0.03) 60%,
        transparent 100%
    );
    pointer-events: none;
}

/* ── Type colour schemes ──────────────────────────────────────── */
.glass-alert.glass-info    { background: rgba(14, 165, 233, 0.18); border-color: rgba(56, 189, 248, 0.35); }
.glass-alert.glass-success { background: rgba(16, 185, 129, 0.18); border-color: rgba(52, 211, 153, 0.35); }
.glass-alert.glass-warning { background: rgba(245, 158, 11, 0.18); border-color: rgba(251, 191, 36, 0.35); }
.glass-alert.glass-error   { background: rgba(239, 68,  68, 0.18); border-color: rgba(248, 113, 113, 0.35); }

/* ── Icon ─────────────────────────────────────────────────────── */
.glass-alert-icon {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    margin-top: 1px;
}
.glass-info    .glass-alert-icon { color: #38bdf8; }
.glass-success .glass-alert-icon { color: #34d399; }
.glass-warning .glass-alert-icon { color: #fbbf24; }
.glass-error   .glass-alert-icon { color: #f87171; }

/* ── Text ─────────────────────────────────────────────────────── */
.glass-alert-body {
    flex: 1;
}
.glass-alert-title {
    font-size: 0.78rem;
    font-weight: 600;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    opacity: 0.65;
    margin-bottom: 0.2rem;
    color: #fff;
}
.glass-alert-message {
    font-size: 0.93rem;
    font-weight: 400;
    color: rgba(255, 255, 255, 0.92);
    line-height: 1.45;
}

/* ── Close button ─────────────────────────────────────────────── */
.glass-alert-close {
    flex-shrink: 0;
    cursor: pointer;
    background: none;
    border: none;
    padding: 0;
    color: rgba(255, 255, 255, 0.5);
    line-height: 1;
    transition: color 0.2s;
    margin-top: 1px;
}
.glass-alert-close:hover { color: rgba(255, 255, 255, 0.95); }

/* ── Progress bar ─────────────────────────────────────────────── */
.glass-alert-progress {
    position: absolute;
    bottom: 0;
    left: 0;
    height: 3px;
    border-radius: 0 0 16px 16px;
    width: 100%;
    transform-origin: left;
}
.glass-info    .glass-alert-progress { background: rgba(56,  189, 248, 0.75); }
.glass-success .glass-alert-progress { background: rgba(52,  211, 153, 0.75); }
.glass-warning .glass-alert-progress { background: rgba(251, 191, 36,  0.75); }
.glass-error   .glass-alert-progress { background: rgba(248, 113, 113, 0.75); }

/* ── Animations ───────────────────────────────────────────────── */
@keyframes glassSlideIn {
    from { opacity: 0; transform: translateX(60px) scale(0.95); }
    to   { opacity: 1; transform: translateX(0)    scale(1);    }
}
@keyframes glassSlideOut {
    from { opacity: 1; transform: translateX(0)    scale(1);    max-height: 120px; margin-bottom: 0; }
    to   { opacity: 0; transform: translateX(60px) scale(0.95); max-height: 0;    margin-bottom: -0.75rem; }
}
.glass-alert.dismissing {
    animation: glassSlideOut 0.4s cubic-bezier(0.55, 0, 1, 0.45) forwards;
}
</style>

<script>
(function () {
    /* Ensure a single wrapper div exists */
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

    var ICONS = {
        info:    '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>',
        success: '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
        warning: '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>',
        error:   '<svg class="glass-alert-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>'
    };

    var LABELS = { info: 'Info', success: 'Success', warning: 'Warning', error: 'Error' };

    function dismiss(el) {
        if (el.dataset.dismissed) return;
        el.dataset.dismissed = '1';
        el.classList.add('dismissing');
        setTimeout(function () { el.remove(); }, 420);
    }

    function createAlert(id, message, type, duration) {
        var wrapper = getWrapper();
        var el = document.createElement('div');
        el.id = id;
        el.className = 'glass-alert glass-' + type;

        el.innerHTML =
            (ICONS[type] || ICONS.info) +
            '<div class="glass-alert-body">' +
                '<div class="glass-alert-title">' + LABELS[type] + '</div>' +
                '<div class="glass-alert-message">' + message + '</div>' +
            '</div>' +
            '<button class="glass-alert-close" aria-label="Close">' +
                '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>' +
            '</button>' +
            (duration > 0 ? '<div class="glass-alert-progress" id="' + id + '-prog"></div>' : '');

        wrapper.appendChild(el);

        el.querySelector('.glass-alert-close').addEventListener('click', function () {
            dismiss(el);
        });

        if (duration > 0) {
            var prog = document.getElementById(id + '-prog');
            if (prog) {
                prog.style.transition = 'transform ' + duration + 'ms linear';
                /* Kick off in next frame */
                requestAnimationFrame(function () {
                    requestAnimationFrame(function () {
                        prog.style.transform = 'scaleX(0)';
                    });
                });
            }
            setTimeout(function () { dismiss(el); }, duration);
        }
    }

    /* Invoke immediately for this alert */
    document.addEventListener('DOMContentLoaded', function () {
        createAlert('${id}', ${message.encodeAsHTML().inspect()}, '${type}', ${duration});
    });
})();
</script>
"""
  }
}
