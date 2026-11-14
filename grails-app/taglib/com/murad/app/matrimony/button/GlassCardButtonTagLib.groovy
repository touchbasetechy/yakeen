package com.murad.app.matrimony.button

/**
 * GlassCardButtonTagLib.groovy
 *
 * Next-generation glassmorphic buttons for ModernGlassCardTagLib.
 *
 * Tags:
 *   <glass:cardBtn />       — single button
 *   <glass:cardBtnGroup />  — flex row wrapper
 *
 * Variants : aurora | cosmic | ember | frost | obsidian | neon | golden
 * Sizes    : sm | md | lg | xl
 */
class GlassCardButtonTagLib {

  static namespace = "glass"

  private void writeStyles(out, pageScope) {
    if (pageScope["__gcbCssWritten__"]) return
    pageScope["__gcbCssWritten__"] = true

    out << '''<style id="glass-card-btn-styles">
@import url(\'https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;700&family=DM+Sans:opsz,wght@9..40,400;9..40,500;9..40,600&display=swap\');

:root {
  --gcb-font        : \'DM Sans\', sans-serif;
  --gcb-radius      : 14px;
  --gcb-transition  : 240ms cubic-bezier(0.22, 0.61, 0.36, 1);
  --gcb-ease-spring : cubic-bezier(0.34, 1.56, 0.64, 1);

  --gcb-aurora-1    : #a78bfa;
  --gcb-aurora-2    : #6366f1;
  --gcb-aurora-3    : #06b6d4;
  --gcb-aurora-glow : rgba(99, 102, 241, 0.55);

  --gcb-cosmic-1    : #e879f9;
  --gcb-cosmic-2    : #a855f7;
  --gcb-cosmic-3    : #6d28d9;
  --gcb-cosmic-glow : rgba(168, 85, 247, 0.55);

  --gcb-ember-1     : #fb923c;
  --gcb-ember-2     : #f43f5e;
  --gcb-ember-3     : #be123c;
  --gcb-ember-glow  : rgba(244, 63, 94, 0.50);

  --gcb-frost-background    : rgba(255, 255, 255, 0.10);
  --gcb-frost-hover : rgba(255, 255, 255, 0.18);
  --gcb-frost-border: rgba(255, 255, 255, 0.32);
  --gcb-frost-glow  : rgba(200, 220, 255, 0.20);
  --gcb-frost-text  : rgba(255, 255, 255, 0.90);

  --gcb-obsidian-background    : rgba(10, 10, 20, 0.55);
  --gcb-obsidian-hover : rgba(20, 20, 40, 0.70);
  --gcb-obsidian-border: rgba(255, 255, 255, 0.12);
  --gcb-obsidian-glow  : rgba(0, 0, 0, 0.50);
  --gcb-obsidian-text  : rgba(255, 255, 255, 0.80);

  --gcb-neon-1     : #a3e635;
  --gcb-neon-2     : #16a34a;
  --gcb-neon-3     : #065f46;
  --gcb-neon-glow  : rgba(163, 230, 53, 0.50);
  --gcb-neon-text  : #0a0a0a;

  --gcb-golden-glow: rgba(245, 158, 11, 0.45);
  --gcb-golden-text: #1a0a00;
}

/* ════════════════════════════════════════════════════════════════════
   BASE BUTTON
════════════════════════════════════════════════════════════════════ */
.gcb {
  display          : inline-flex;
  align-items      : center;
  justify-content  : center;
  gap              : 9px;
  font-family      : var(--gcb-font);
  font-weight      : 600;
  letter-spacing   : 0.025em;
  text-decoration  : none;
  cursor           : pointer;
  border           : 1px solid transparent;
  border-radius    : var(--gcb-radius);
  position         : relative;
  overflow         : hidden;
  outline          : none;
  white-space      : nowrap;
  isolation        : isolate;
  -webkit-font-smoothing : antialiased;
  transition:
    transform    var(--gcb-transition),
    box-shadow   var(--gcb-transition),
    border-color var(--gcb-transition),
    opacity      var(--gcb-transition);
}

/* ── Layer 1: static shimmer ── */
.gcb::before {
  content        : \'\';
  position       : absolute;
  inset          : 0;
  border-radius  : inherit;
  background     : linear-gradient(
                     160deg,
                     rgba(255,255,255,0.18) 0%,
                     rgba(255,255,255,0.04) 40%,
                     transparent 70%
                   );
  pointer-events : none;
  z-index        : 1;
}

/* ── Layer 2: mouse-tracked specular ── */
.gcb::after {
  content        : \'\';
  position       : absolute;
  inset          : 0;
  border-radius  : inherit;
  background     : radial-gradient(
                     circle at var(--mx, 50%) var(--my, 50%),
                     rgba(255,255,255,0.22) 0%,
                     transparent 60%
                   );
  opacity        : 0;
  pointer-events : none;
  z-index        : 2;
  transition     : opacity 300ms ease;
}
.gcb:hover::after { opacity: 1; }

.gcb > * { position: relative; z-index: 3; }

.gcb:active {
  transform : scale(0.96) translateY(1px) !important;
  transition-duration: 80ms;
}

.gcb:focus-visible {
  outline    : none;
  box-shadow : 0 0 0 2px rgba(0,0,0,0.6),
               0 0 0 4px rgba(160, 130, 255, 0.70) !important;
}

.gcb:disabled,
.gcb[aria-disabled="true"] {
  opacity        : 0.35;
  cursor         : not-allowed;
  pointer-events : none;
  filter         : grayscale(30%);
}

/* ════════════════════════════════════════════════════════════════════
   RIPPLE
════════════════════════════════════════════════════════════════════ */

/* The ripple span is injected by JS and sits above all pseudo-elements */
.gcb-ripple {
  position       : absolute;
  border-radius  : 50%;
  transform      : scale(0);
  pointer-events : none;
  z-index        : 10;  /* above shimmer & specular layers */
  animation      : gcb-ripple-expand 600ms cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

/* Ripple colour adapts per variant via --gcb-ripple-color */
.gcb-aurora   { --gcb-ripple-color: rgba(255, 255, 255, 0.35); }
.gcb-cosmic   { --gcb-ripple-color: rgba(255, 220, 255, 0.30); }
.gcb-ember    { --gcb-ripple-color: rgba(255, 220, 180, 0.35); }
.gcb-frost    { --gcb-ripple-color: rgba(255, 255, 255, 0.45); }
.gcb-obsidian { --gcb-ripple-color: rgba(255, 255, 255, 0.18); }
.gcb-neon     { --gcb-ripple-color: rgba(0,   40,   0,  0.25); }
.gcb-golden   { --gcb-ripple-color: rgba(120,  60,   0,  0.20); }

@keyframes gcb-ripple-expand {
  0%   { transform: scale(0);  opacity: 1; }
  70%  { transform: scale(4);  opacity: 0.6; }
  100% { transform: scale(6);  opacity: 0; }
}

/* ════════════════════════════════════════════════════════════════════
   SIZES
════════════════════════════════════════════════════════════════════ */
.gcb-sm  { padding: 8px 18px;  font-size: 0.8125rem; }
.gcb-md  { padding: 12px 26px; font-size: 0.9375rem; }
.gcb-lg  { padding: 15px 34px; font-size: 1.0625rem; }
.gcb-xl  { padding: 19px 44px; font-size: 1.1875rem; letter-spacing: 0.035em; }

.gcb-full { width: 100%; }

.gcb .gcb-fa {
  font-size   : 0.9em;
  line-height : 1;
  width       : 1em;
  text-align  : center;
  flex-shrink : 0;
}

.gcb-icon-only { gap: 0; }
.gcb-icon-only.gcb-sm { padding: 8px;  width: 33px; height: 33px; }
.gcb-icon-only.gcb-md { padding: 11px; width: 41px; height: 41px; }
.gcb-icon-only.gcb-lg { padding: 14px; width: 50px; height: 50px; }
.gcb-icon-only.gcb-xl { padding: 17px; width: 59px; height: 59px; }

/* ════════════════════════════════════════════════════════════════════
   VARIANTS
════════════════════════════════════════════════════════════════════ */

/* ── AURORA ── */
@keyframes gcb-aurora-shift {
  0%   { background-position: 0%   50%; }
  50%  { background-position: 100% 50%; }
  100% { background-position: 0%   50%; }
}
.gcb-aurora {
  background      : linear-gradient(135deg, var(--gcb-aurora-1), var(--gcb-aurora-2), var(--gcb-aurora-3), var(--gcb-aurora-2));
  background-size : 300% 300%;
  animation       : gcb-aurora-shift 6s ease infinite;
  color           : #fff;
  box-shadow      : 0 6px 28px var(--gcb-aurora-glow),
                    0 2px  8px rgba(0,0,0,0.30),
                    inset 0 1px 0 rgba(255,255,255,0.20);
}
.gcb-aurora:hover:not(:disabled) {
  box-shadow : 0 12px 40px var(--gcb-aurora-glow),
               0  4px 16px rgba(0,0,0,0.35),
               inset 0 1px 0 rgba(255,255,255,0.25);
  transform  : translateY(-3px) scale(1.02);
}

/* ── COSMIC ── */
.gcb-cosmic {
  background   : linear-gradient(135deg, var(--gcb-cosmic-1), var(--gcb-cosmic-2), var(--gcb-cosmic-3));
  color        : #fff;
  box-shadow   : 0 6px 28px var(--gcb-cosmic-glow),
                 0 2px  8px rgba(0,0,0,0.30),
                 inset 0 1px 0 rgba(255,255,255,0.20);
  border-color : rgba(232, 121, 249, 0.35);
}
.gcb-cosmic:hover:not(:disabled) {
  box-shadow : 0 0 0 1px rgba(232,121,249,0.50),
               0 12px 40px var(--gcb-cosmic-glow),
               0  0   60px rgba(168,85,247,0.35),
               inset 0 1px 0 rgba(255,255,255,0.25);
  transform  : translateY(-3px) scale(1.02);
}

/* ── EMBER ── */
.gcb-ember {
  background : linear-gradient(135deg, var(--gcb-ember-1) 0%, var(--gcb-ember-2) 55%, var(--gcb-ember-3) 100%);
  color      : #fff;
  box-shadow : 0 6px 28px var(--gcb-ember-glow),
               0 2px  8px rgba(0,0,0,0.30),
               inset 0 1px 0 rgba(255,200,150,0.25);
}
.gcb-ember:hover:not(:disabled) {
  box-shadow : 0 12px 44px var(--gcb-ember-glow),
               0  4px 20px rgba(251,146,60,0.45),
               0  0   60px rgba(244,63,94,0.25),
               inset 0 1px 0 rgba(255,200,150,0.30);
  transform  : translateY(-3px) scale(1.02);
}

/* ── FROST ── */
.gcb-frost {
  background          : var(--gcb-frost-background);
  backdrop-filter     : blur(20px) saturate(1.8);
  -webkit-backdrop-filter: blur(20px) saturate(1.8);
  color               : var(--gcb-frost-text);
  border-color        : var(--gcb-frost-border);
  box-shadow          : 0 4px 20px var(--gcb-frost-glow),
                        inset 0 1px 0 rgba(255,255,255,0.25),
                        inset 0 -1px 0 rgba(0,0,0,0.08);
}
.gcb-frost:hover:not(:disabled) {
  background   : var(--gcb-frost-hover);
  border-color : rgba(255,255,255,0.55);
  box-shadow   : 0 8px 32px var(--gcb-frost-glow),
                 0 0  24px  rgba(200,220,255,0.18),
                 inset 0 1px 0 rgba(255,255,255,0.35);
  transform    : translateY(-2px);
}

/* ── OBSIDIAN ── */
.gcb-obsidian {
  background          : var(--gcb-obsidian-background);
  backdrop-filter     : blur(24px) saturate(1.4);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  color               : var(--gcb-obsidian-text);
  border-color        : var(--gcb-obsidian-border);
  box-shadow          : 0 4px 20px var(--gcb-obsidian-glow),
                        inset 0 1px 0 rgba(255,255,255,0.08),
                        inset 0 -1px 0 rgba(0,0,0,0.25);
}
.gcb-obsidian:hover:not(:disabled) {
  background   : var(--gcb-obsidian-hover);
  border-color : rgba(255,255,255,0.22);
  color        : rgba(255,255,255,0.95);
  box-shadow   : 0 8px 32px rgba(0,0,0,0.55),
                 inset 0 1px 0 rgba(255,255,255,0.12);
  transform    : translateY(-2px);
}

/* ── NEON ── */
@keyframes gcb-neon-flicker {
  0%, 95%, 100% { opacity: 1;    }
  96%           { opacity: 0.85; }
  97%           { opacity: 1;    }
  98%           { opacity: 0.90; }
}
.gcb-neon {
  background      : linear-gradient(135deg, var(--gcb-neon-1) 0%, var(--gcb-neon-2) 60%, var(--gcb-neon-3) 100%);
  background-size : 300% 300%;
  animation       : gcb-aurora-shift 6s ease infinite, gcb-neon-flicker 8s ease-in-out infinite;
  color           : var(--gcb-neon-text);
  font-weight     : 700;
  box-shadow      : 0 6px 24px var(--gcb-neon-glow),
                    0 0  12px  rgba(163,230,53,0.40),
                    inset 0 1px 0 rgba(255,255,255,0.30);
}
.gcb-neon:hover:not(:disabled) {
  box-shadow   : 0 8px 32px var(--gcb-neon-glow),
                 0 0  40px  rgba(163,230,53,0.55),
                 0 0  80px  rgba(163,230,53,0.25),
                 inset 0 1px 0 rgba(255,255,255,0.35);
  transform    : translateY(-3px) scale(1.02);
}

/* ── GOLDEN ── */
@keyframes gcb-golden-sheen {
  0%   { background-position: -200% center; }
  100% { background-position:  200% center; }
}
.gcb-golden {
  background      : linear-gradient(105deg, #92600a 0%, #f59e0b 25%, #fde68a 50%, #f59e0b 75%, #92600a 100%);
  background-size : 200% auto;
  animation       : gcb-golden-sheen 4s linear infinite;
  color           : var(--gcb-golden-text);
  font-weight     : 700;
  letter-spacing  : 0.04em;
  box-shadow      : 0 6px 28px var(--gcb-golden-glow),
                    0 2px  6px rgba(0,0,0,0.25),
                    inset 0 1px 0 rgba(255,240,180,0.50),
                    inset 0 -1px 0 rgba(100,50,0,0.25);
}
.gcb-golden:hover:not(:disabled) {
  box-shadow   : 0 12px 44px var(--gcb-golden-glow),
                 0  4px 16px rgba(245,158,11,0.50),
                 0  0   60px rgba(253,230,138,0.20),
                 inset 0 1px 0 rgba(255,240,180,0.60);
  transform    : translateY(-3px) scale(1.02);
}

/* ── PULSE ── */
@keyframes gcb-pulse {
  0%, 100% { box-shadow: 0 0 0 0   var(--gcb-pulse-color, rgba(160,130,255,0.55)); }
  60%      { box-shadow: 0 0 0 12px rgba(0,0,0,0); }
}
.gcb-pulse { animation: gcb-pulse 2.2s ease-in-out infinite; }

/* ── Group ── */
.gcb-group        { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; }
.gcb-group-start  { justify-content: flex-start; }
.gcb-group-center { justify-content: center;     }
.gcb-group-end    { justify-content: flex-end;   }

/* ── Entry animation ── */
@keyframes gcb-appear {
  from { opacity: 0; transform: translateY(8px) scale(0.96); }
  to   { opacity: 1; transform: translateY(0)   scale(1);    }
}
.gcb { animation: gcb-appear 0.40s var(--gcb-ease-spring) both; }

</style>

<script>
(function () {
  if (window.__gcbInitialised) return;
  window.__gcbInitialised = true;

  /* ── Specular highlight tracking ───────────────────────────────── */
  document.addEventListener('mousemove', function (e) {
    var btn = e.target.closest(\'.gcb\');
    if (!btn) return;
    var r = btn.getBoundingClientRect();
    btn.style.setProperty(\'--mx\', ((e.clientX - r.left) / r.width  * 100).toFixed(1) + \'%\');
    btn.style.setProperty(\'--my\', ((e.clientY - r.top)  / r.height * 100).toFixed(1) + \'%\');
  });

  /* ── Ripple on click ────────────────────────────────────────────
     Technique:
       1. Capture pointer position relative to the button.
       2. Size the circle to fully cover the button from that origin
          (diameter = 2 × longest distance to any corner).
       3. Inject a <span class="gcb-ripple">, let the CSS keyframe
          run, then remove the element once the animation ends.
          This keeps the DOM clean and allows multiple rapid clicks.
  ───────────────────────────────────────────────────────────────── */
  document.addEventListener(\'click\', function (e) {
    var btn = e.target.closest(\'.gcb\');
    if (!btn || btn.disabled || btn.getAttribute(\'aria-disabled\') === \'true\') return;

    var rect     = btn.getBoundingClientRect();
    var originX  = e.clientX - rect.left;
    var originY  = e.clientY - rect.top;

    /* Diameter must reach the farthest corner from click origin */
    var dx       = Math.max(originX, rect.width  - originX);
    var dy       = Math.max(originY, rect.height - originY);
    var diameter = Math.ceil(Math.sqrt(dx * dx + dy * dy) * 2);

    var ripple   = document.createElement(\'span\');
    ripple.className = \'gcb-ripple\';

    var color = getComputedStyle(btn).getPropertyValue(\'--gcb-ripple-color\').trim()
                || \'rgba(255, 255, 255, 0.30)\';

    ripple.style.cssText = [
      \'width:\'       + diameter + \'px\',
      \'height:\'      + diameter + \'px\',
      \'left:\'        + (originX - diameter / 2) + \'px\',
      \'top:\'         + (originY - diameter / 2) + \'px\',
      \'background:\'  + color,
    ].join(\';\'  );

    btn.appendChild(ripple);

    /* Clean up after animation finishes */
    ripple.addEventListener(\'animationend\', function () {
      ripple.remove();
    }, { once: true });
  });

})();
</script>
'''
  }

  private static String h(String s) {
    s?.replace('&', '&amp;')?.replace('<', '&lt;')?.replace('>', '&gt;')?.replace('"', '&quot;') ?: ''
  }

  /* ─────────────────────────────────────────────────────────────────
     <glass:cardBtn />
  ───────────────────────────────────────────────────────────────── */
  def cardBtn = { attrs, body ->

    writeStyles(out, pageScope)

    String label = attrs.remove('label')?.toString() ?: body()?.toString() ?: ''
    String faIcon = attrs.remove('faIcon')?.toString() ?: ''
    String faIconPos = attrs.remove('faIconPosition')?.toString() ?: 'left'
    String variant = attrs.remove('variant')?.toString() ?: 'aurora'
    String size = attrs.remove('size')?.toString() ?: 'md'
    String type = attrs.remove('type')?.toString() ?: 'button'
    String href = attrs.remove('href')?.toString() ?: ''
    String onclick = attrs.remove('onclick')?.toString() ?: ''
    String customClass = attrs.remove('class')?.toString() ?: ''
    String customId = attrs.remove('id')?.toString() ?: ''
    String customStyle = attrs.remove('style')?.toString() ?: ''

    boolean disabled = attrs.remove('disabled')?.toString()?.equalsIgnoreCase('true') ?: false
    boolean fullWidth = attrs.remove('fullWidth')?.toString()?.equalsIgnoreCase('true') ?: false
    boolean pulse = attrs.remove('pulse')?.toString()?.equalsIgnoreCase('true') ?: false
    boolean iconOnly = attrs.remove('iconOnly')?.toString()?.equalsIgnoreCase('true') ?: false

    Map<String, String> pulseColors = [
      aurora  : 'rgba(99,102,241,0.60)',
      cosmic  : 'rgba(168,85,247,0.60)',
      ember   : 'rgba(244,63,94,0.55)',
      frost   : 'rgba(200,220,255,0.35)',
      obsidian: 'rgba(255,255,255,0.15)',
      neon    : 'rgba(163,230,53,0.55)',
      golden  : 'rgba(245,158,11,0.50)',
    ]

    List<String> classes = ['gcb', "gcb-${h(size)}", "gcb-${h(variant)}"]
    if (fullWidth) classes << 'gcb-full'
    if (pulse) classes << 'gcb-pulse'
    if (iconOnly) classes << 'gcb-icon-only'
    if (customClass) classes << h(customClass)

    String iconHtml = faIcon ? "<i class=\"${h(faIcon)} gcb-fa\" aria-hidden=\"true\"></i>" : ''
    String labelHtml = (!iconOnly && label) ? "<span>${h(label)}</span>" : ''
    String inner = (faIconPos == 'right') ? "${labelHtml}${iconHtml}" : "${iconHtml}${labelHtml}"

    String idAttr = customId ? " id=\"${h(customId)}\"" : ''
    String onclickAttr = onclick ? " onclick=\"${h(onclick)}\"" : ''
    String pulseStyle = pulse ? "--gcb-pulse-color:${pulseColors[variant] ?: 'rgba(160,130,255,0.55)'};" : ''
    String styleAttr = " style=\"${pulseStyle}${customStyle}\""

    if (href) {
      String ariaDisabled = disabled ? ' aria-disabled="true" tabindex="-1"' : ''
      out << "<a href=\"${h(href)}\" class=\"${classes.join(' ')}\"${idAttr}${styleAttr}${onclickAttr}${ariaDisabled}>${inner}</a>"
    } else {
      String disabledAttr = disabled ? ' disabled' : ''
      out << "<button type=\"${h(type)}\" class=\"${classes.join(' ')}\"${idAttr}${styleAttr}${onclickAttr}${disabledAttr}>${inner}</button>"
    }
  }

  /* ─────────────────────────────────────────────────────────────────
     <glass:cardBtnGroup />
  ───────────────────────────────────────────────────────────────── */
  def cardBtnGroup = { attrs, body ->
    String align = attrs.remove('align')?.toString() ?: 'end'
    String customClass = attrs.remove('class')?.toString() ?: ''
    String customStyle = attrs.remove('style')?.toString() ?: ''

    List<String> classes = ['gcb-group', "gcb-group-${h(align)}"]
    if (customClass) classes << h(customClass)

    String styleAttr = customStyle ? " style=\"${h(customStyle)}\"" : ''
    out << "<div class=\"${classes.join(' ')}\"${styleAttr}>${body()}</div>"
  }
}
