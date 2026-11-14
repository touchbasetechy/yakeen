package com.murad.app.matrimony

/**
 * GlassDisclosureTagLib
 *
 * Glassmorphic, futuristic, mobile-first disclosure / accordion taglib for Grails GSP.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * INSTALLATION
 * ─────────────────────────────────────────────────────────────────────────────
 * 1. Drop this file into:  grails-app/taglib/com/glass/ui/GlassDisclosureTagLib.groovy
 * 2. In your GSP layout <head>, add:   <glass:disclosureStyles/>
 * 3. Before </body>, add:              <glass:disclosureScripts/>
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * QUICK START
 * ─────────────────────────────────────────────────────────────────────────────
 *
 *   <glass:disclosureStyles/>
 *
 *   <glass:disclosureStack>
 *
 *     <glass:disclosure title="How does it work?" subtitle="Design basics"
 *                       badge="FAQ" glow="purple">
 *       <p>Your body content here.</p>
 *     </glass:disclosure>
 *
 *     <glass:disclosure title="Technical specs" subtitle="Browser support"
 *                       badge="Specs" glow="teal" open="true">
 *       <glass:specTable>
 *         <glass:specRow key="Chrome / Edge" value="76+" status="ok"/>
 *         <glass:specRow key="Safari / iOS"  value="9+"   status="ok"/>
 *         <glass:specRow key="Firefox"       value="103+" status="ok"/>
 *         <glass:specRow key="Blur radius"   value="20px"/>
 *       </glass:specTable>
 *       <glass:notice type="warn">
 *         Avoid stacking more than 3–4 blurred layers on older mobile devices.
 *       </glass:notice>
 *     </glass:disclosure>
 *
 *     <glass:disclosure title="Implementation steps" subtitle="Start to finish"
 *                       badge="Guide" glow="pink" open="true">
 *       <glass:stepList>
 *         <glass:step>Set a vivid, high-contrast background behind your glass panel.</glass:step>
 *         <glass:step>Apply backdrop-filter: blur(16–24px) with the -webkit- prefix.</glass:step>
 *         <glass:step>Use a semi-transparent fill between 5–15% white opacity.</glass:step>
 *         <glass:step>Add a 1px border at ~20% white and a top-edge inset highlight.</glass:step>
 *       </glass:stepList>
 *     </glass:disclosure>
 *
 *     <glass:disclosure title="Project progress" subtitle="Q2 2026 milestones"
 *                       badge="Live" glow="amber">
 *       <glass:progressList>
 *         <glass:progressItem label="Design system" value="94" color="purple"/>
 *         <glass:progressItem label="Frontend"      value="78" color="teal"/>
 *         <glass:progressItem label="API integration" value="61" color="pink"/>
 *         <glass:progressItem label="QA and testing"  value="42" color="amber"/>
 *       </glass:progressList>
 *     </glass:disclosure>
 *
 *   </glass:disclosureStack>
 *
 *   <glass:disclosureScripts/>
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * TAG REFERENCE
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * <glass:disclosureStyles/>
 *   Emits all required CSS. Place once in <head>. Safe to call multiple times
 *   (idempotent via a request attribute guard).
 *
 * <glass:disclosureScripts/>
 *   Emits the toggle JS. Place once before </body>. Idempotent.
 *
 * <glass:disclosureStack [id] [gap]>
 *   Wrapper that provides the scene background, orbs, and vertical stack layout.
 *   id    — optional HTML id on the outer div
 *   gap   — CSS gap between panels, default "10px"
 *
 * <glass:disclosure title [subtitle] [badge] [glow] [icon] [open] [id] [animate]>
 *   A single collapsible panel.
 *   title    — (required) heading text
 *   subtitle — muted sub-label rendered below the title
 *   badge    — pill badge text (e.g. "FAQ", "Specs", "Live")
 *   glow     — accent color key: purple | teal | pink | amber | blue | none  (default: none)
 *   icon     — raw inline SVG <path>/<circle>/… content drawn inside an 18×18 viewBox;
 *              omit to use the default question-mark icon
 *   open     — "true" to render the panel already expanded (default: false)
 *   id       — HTML id; auto-generated as "gd-N" when omitted
 *   animate  — "true" (default) triggers progress-bar animation on first open
 *
 * <glass:specTable>
 *   Wrapper for <glass:specRow> entries. Renders a borderless key/value table.
 *
 * <glass:specRow key value [status]>
 *   key    — left-column label
 *   value  — right-column value
 *   status — ok | warn | none  (default: none)
 *            ok   → value rendered in green
 *            warn → value rendered in yellow
 *
 * <glass:notice [type]>
 *   Callout box wrapping body content.
 *   type — info (default) | warn | danger
 *
 * <glass:stepList>
 *   Ordered wrapper for <glass:step> items.
 *
 * <glass:step>
 *   A single numbered step. Body content is the step description.
 *
 * <glass:progressList>
 *   Wrapper for <glass:progressItem> bars.
 *
 * <glass:progressItem label value [color]>
 *   label — bar label text
 *   value — integer 0–100
 *   color — purple | teal | pink | amber | blue  (default: purple)
 *
 * <glass:tagRow>
 *   Flex-wrap row for <glass:tag> chips.
 *
 * <glass:tag>
 *   A single pill chip. Body is the chip text.
 */
class GlassDisclosureTagLib {

  static namespace = 'glass'

  // ── idempotency counters ──────────────────────────────────────────────────
  private static final String STYLES_RENDERED = 'glass.styles.rendered'
  private static final String SCRIPTS_RENDERED = 'glass.scripts.rendered'

  // ── glow palette ─────────────────────────────────────────────────────────
  private static final Map GLOWS = [
    purple: [
      track : 'rgba(168,85,247,0.28)',
      border: 'rgba(168,85,247,0.4)',
      active: 'rgba(168,85,247,0.7)',
      shadow: 'rgba(168,85,247,0.45)',
      stroke: 'rgba(168,85,247,0.9)',
      badge : 'rgba(168,85,247,0.12)',
      bBdr  : 'rgba(168,85,247,0.35)',
      bText : 'rgba(192,132,252,0.9)'
    ],
    teal  : [
      track : 'rgba(6,182,212,0.22)',
      border: 'rgba(6,182,212,0.4)',
      active: 'rgba(6,182,212,0.7)',
      shadow: 'rgba(6,182,212,0.45)',
      stroke: 'rgba(6,182,212,0.9)',
      badge : 'rgba(6,182,212,0.1)',
      bBdr  : 'rgba(6,182,212,0.35)',
      bText : 'rgba(103,232,249,0.9)'
    ],
    pink  : [
      track : 'rgba(236,72,153,0.2)',
      border: 'rgba(236,72,153,0.35)',
      active: 'rgba(236,72,153,0.65)',
      shadow: 'rgba(236,72,153,0.4)',
      stroke: 'rgba(236,72,153,0.9)',
      badge : 'rgba(236,72,153,0.1)',
      bBdr  : 'rgba(236,72,153,0.35)',
      bText : 'rgba(249,168,212,0.9)'
    ],
    amber : [
      track : 'rgba(251,191,36,0.18)',
      border: 'rgba(251,191,36,0.35)',
      active: 'rgba(251,191,36,0.6)',
      shadow: 'rgba(251,191,36,0.4)',
      stroke: 'rgba(251,191,36,0.9)',
      badge : 'rgba(251,191,36,0.1)',
      bBdr  : 'rgba(251,191,36,0.35)',
      bText : 'rgba(253,224,71,0.9)'
    ],
    blue  : [
      track : 'rgba(59,130,246,0.2)',
      border: 'rgba(59,130,246,0.35)',
      active: 'rgba(59,130,246,0.65)',
      shadow: 'rgba(59,130,246,0.4)',
      stroke: 'rgba(59,130,246,0.9)',
      badge : 'rgba(59,130,246,0.1)',
      bBdr  : 'rgba(59,130,246,0.35)',
      bText : 'rgba(147,197,253,0.9)'
    ]
  ]

  // ── progress-bar gradient stops ───────────────────────────────────────────
  private static final Map PROG_GRADIENTS = [
    purple: 'rgba(168,85,247,0.9),rgba(139,92,246,0.6)',
    teal  : 'rgba(6,182,212,0.9),rgba(14,165,233,0.6)',
    pink  : 'rgba(236,72,153,0.9),rgba(217,70,239,0.6)',
    amber : 'rgba(251,191,36,0.9),rgba(245,158,11,0.6)',
    blue  : 'rgba(59,130,246,0.9),rgba(99,102,241,0.6)'
  ]

  // ── notice palette ────────────────────────────────────────────────────────
  private static final Map NOTICES = [
    info  : [stroke: 'rgba(6,182,212,0.7)', icon: 'circle'],
    warn  : [stroke: 'rgba(250,204,21,0.7)', icon: 'triangle'],
    danger: [stroke: 'rgba(248,113,113,0.7)', icon: 'circle']
  ]

  // ── auto-increment panel counter (per-request) ────────────────────────────
  private int nextId() {
    def count = request.getAttribute('glass.disc.count') ?: 0
    count++
    request.setAttribute('glass.disc.count', count)
    return count
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:disclosureStyles/>
  // ─────────────────────────────────────────────────────────────────────────
  def disclosureStyles = { attrs ->
    if (request.getAttribute(STYLES_RENDERED)) return
    request.setAttribute(STYLES_RENDERED, true)

    out << """<style>
*{box-sizing:border-box;margin:0;padding:0}
.gd-scene{min-height:520px;padding:2.5rem 1.5rem;display:flex;flex-direction:column;gap:1rem;position:relative;overflow:hidden;background:linear-gradient(135deg,#0d0221 0%,#1a0533 30%,#0a1628 65%,#001a1a 100%);border-radius:20px}
.gd-scene::before{content:'';position:absolute;width:400px;height:400px;border-radius:50%;background:radial-gradient(circle,rgba(168,85,247,0.28) 0%,transparent 68%);top:-120px;left:-80px;pointer-events:none}
.gd-scene::after{content:'';position:absolute;width:300px;height:300px;border-radius:50%;background:radial-gradient(circle,rgba(6,182,212,0.22) 0%,transparent 68%);bottom:-60px;right:-40px;pointer-events:none}
.gd-orb{position:absolute;width:200px;height:200px;border-radius:50%;background:radial-gradient(circle,rgba(236,72,153,0.15) 0%,transparent 68%);bottom:80px;left:30%;pointer-events:none}
.gd-stack{display:flex;flex-direction:column;position:relative;z-index:1}
.gd-panel{border-radius:16px;border:1px solid rgba(255,255,255,0.13);background:rgba(255,255,255,0.06);backdrop-filter:blur(20px);-webkit-backdrop-filter:blur(20px);overflow:hidden;transition:border-color .25s,box-shadow .25s;box-shadow:0 8px 32px rgba(0,0,0,0.22),inset 0 1px 0 rgba(255,255,255,0.1)}
.gd-panel:hover{border-color:rgba(255,255,255,0.22);box-shadow:0 12px 40px rgba(0,0,0,0.3),inset 0 1px 0 rgba(255,255,255,0.15)}
.gd-panel.gd-open{border-color:rgba(255,255,255,0.25);box-shadow:0 12px 40px rgba(0,0,0,0.3),inset 0 1px 0 rgba(255,255,255,0.18)}
.gd-trigger{width:100%;display:flex;align-items:center;gap:14px;padding:18px 20px;background:none;border:none;cursor:pointer;color:#fff;font-family:inherit;-webkit-tap-highlight-color:transparent;position:relative;text-align:left}
.gd-trigger::before{content:'';position:absolute;inset:0;background:linear-gradient(180deg,rgba(255,255,255,0.05) 0%,transparent 100%);pointer-events:none;border-radius:16px 16px 0 0}
.gd-icon{width:38px;height:38px;border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0;border:1px solid rgba(255,255,255,0.15);background:rgba(255,255,255,0.08);transition:background .2s,border-color .2s}
.gd-icon svg{width:18px;height:18px}
.gd-panel.gd-open .gd-icon{background:rgba(255,255,255,0.14);border-color:rgba(255,255,255,0.28)}
.gd-title-wrap{flex:1;min-width:0}
.gd-title{font-size:15px;font-weight:500;color:rgba(255,255,255,0.95);line-height:1.3;display:block}
.gd-sub{font-size:12px;color:rgba(255,255,255,0.42);margin-top:2px;display:block}
.gd-badge{font-size:11px;font-weight:500;padding:3px 9px;border-radius:999px;flex-shrink:0;border:1px solid;letter-spacing:.03em}
.gd-chevron{width:22px;height:22px;border-radius:50%;background:rgba(255,255,255,0.07);border:1px solid rgba(255,255,255,0.13);display:flex;align-items:center;justify-content:center;flex-shrink:0;transition:transform .32s cubic-bezier(.34,1.2,.64,1),background .2s}
.gd-chevron svg{width:12px;height:12px;stroke:rgba(255,255,255,0.6);stroke-width:2;fill:none}
.gd-panel.gd-open .gd-chevron{transform:rotate(180deg);background:rgba(255,255,255,0.12)}
.gd-body{max-height:0;overflow:hidden;transition:max-height .38s cubic-bezier(.4,0,.2,1)}
.gd-panel.gd-open .gd-body{max-height:800px}
.gd-content{padding:0 20px 20px;border-top:1px solid rgba(255,255,255,0.08);padding-top:16px}
.gd-content p{font-size:14px;color:rgba(255,255,255,0.62);line-height:1.75}
.gd-divider{height:1px;background:rgba(255,255,255,0.07);margin:14px 0}
.gd-spec-row{display:flex;align-items:center;justify-content:space-between;padding:8px 0;border-bottom:1px solid rgba(255,255,255,0.06)}
.gd-spec-row:last-child{border-bottom:none}
.gd-spec-key{font-size:13px;color:rgba(255,255,255,0.42)}
.gd-spec-val{font-size:13px;font-weight:500;color:rgba(255,255,255,0.88)}
.gd-spec-val.gd-ok{color:rgba(74,222,128,0.9)}
.gd-spec-val.gd-warn{color:rgba(253,224,71,0.9)}
.gd-notice{display:flex;gap:10px;align-items:flex-start;padding:12px 14px;border-radius:10px;background:rgba(255,255,255,0.05);border:1px solid rgba(255,255,255,0.1);margin-top:12px}
.gd-notice svg{width:16px;height:16px;flex-shrink:0;margin-top:1px}
.gd-notice p{font-size:13px;color:rgba(255,255,255,0.55);line-height:1.6}
.gd-step-list{display:flex;flex-direction:column;gap:12px;margin-top:4px}
.gd-step{display:flex;gap:12px;align-items:flex-start}
.gd-step-num{width:24px;height:24px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:500;background:rgba(255,255,255,0.1);border:1px solid rgba(255,255,255,0.18);color:rgba(255,255,255,0.8);margin-top:1px}
.gd-step p{font-size:13px;color:rgba(255,255,255,0.62);line-height:1.65}
.gd-prog-list{display:flex;flex-direction:column;gap:10px;margin-top:4px}
.gd-prog-item{display:flex;flex-direction:column;gap:5px}
.gd-prog-label{display:flex;justify-content:space-between}
.gd-prog-label span{font-size:12px;color:rgba(255,255,255,0.5)}
.gd-prog-label strong{font-size:12px;font-weight:500;color:rgba(255,255,255,0.85)}
.gd-prog-track{height:5px;border-radius:999px;background:rgba(255,255,255,0.08);overflow:hidden}
.gd-prog-fill{height:100%;border-radius:999px;width:0;transition:width .7s cubic-bezier(.4,0,.2,1) .1s}
.gd-tag-row{display:flex;flex-wrap:wrap;gap:8px;margin-top:12px}
.gd-tag{font-size:12px;padding:5px 12px;border-radius:8px;background:rgba(255,255,255,0.07);border:1px solid rgba(255,255,255,0.13);color:rgba(255,255,255,0.7);transition:background .18s,border-color .18s;cursor:default}
.gd-tag:hover{background:rgba(255,255,255,0.12);border-color:rgba(255,255,255,0.22)}
@media(max-width:480px){
  .gd-scene{padding:1.75rem 1rem}
  .gd-trigger{padding:15px 16px;gap:11px}
  .gd-content{padding:0 16px 16px;padding-top:14px}
  .gd-title{font-size:14px}
  .gd-icon{width:34px;height:34px}
}
</style>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:disclosureScripts/>
  // ─────────────────────────────────────────────────────────────────────────
  def disclosureScripts = { attrs ->
    if (request.getAttribute(SCRIPTS_RENDERED)) return
    request.setAttribute(SCRIPTS_RENDERED, true)

    out << """<script>
(function(){
  function gdToggle(id){
    var p=document.getElementById(id);
    if(!p)return;
    var open=p.classList.contains('gd-open');
    p.classList.toggle('gd-open',!open);
    var btn=p.querySelector('.gd-trigger');
    if(btn)btn.setAttribute('aria-expanded',String(!open));
    if(!open && p.dataset.animate!=='false'){
      setTimeout(function(){
        p.querySelectorAll('.gd-prog-fill').forEach(function(el){
          el.style.width=(el.dataset.value||'0')+'%';
        });
      },80);
    }
  }
  window.gdToggle=gdToggle;
  document.addEventListener('DOMContentLoaded',function(){
    document.querySelectorAll('.gd-panel.gd-open').forEach(function(p){
      p.querySelectorAll('.gd-prog-fill').forEach(function(el){
        el.style.width=(el.dataset.value||'0')+'%';
      });
    });
  });
})();
</script>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:disclosureStack [id] [gap]>…</glass:disclosureStack>
  // ─────────────────────────────────────────────────────────────────────────
  def disclosureStack = { attrs, body ->
    def id = attrs.id ? "id=\"${attrs.id}\"" : ''
    def gap = attrs.gap ?: '10px'

    out << """<div class="gd-scene" ${id}>
<div class="gd-orb"></div>
<div class="gd-stack" style="gap:${gap}">
${body()}
</div>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:disclosure title [subtitle] [badge] [glow] [icon] [open] [id] [animate]>
  // ─────────────────────────────────────────────────────────────────────────
  def disclosure = { attrs, body ->
    def panelId = attrs.id ?: "gd-${nextId()}"
    def title = attrs.title?.encodeAsHTML() ?: ''
    def subtitle = attrs.subtitle?.encodeAsHTML() ?: ''
    def badge = attrs.badge?.encodeAsHTML() ?: ''
    def glowKey = attrs.glow ?: 'none'
    def isOpen = (attrs.open == 'true')
    def animate = (attrs.animate != 'false')

    def glow = GLOWS[glowKey] ?: null
    def openCls = isOpen ? ' gd-open' : ''

    // ── panel-level glow overrides ──────────────────────────────────────
    def panelStyle = ''
    if (glow) {
      panelStyle = """ style="box-shadow:0 8px 32px rgba(0,0,0,0.22),inset 0 1px 0 rgba(255,255,255,0.1),0 0 24px ${glow.shadow};border-color:${glow.border};" """
    }

    // ── icon ─────────────────────────────────────────────────────────────
    def iconColor = glow ? glow.stroke : 'rgba(255,255,255,0.7)'
    def iconInner = attrs.icon ?: """<circle cx="9" cy="9" r="7"/><path d="M9 12v-1m0-4a2 2 0 1 1 0 4"/>"""
    def iconSvg = """<svg viewBox="0 0 18 18" fill="none" stroke="${iconColor}" stroke-width="1.7">${iconInner}</svg>"""

    // ── badge ─────────────────────────────────────────────────────────────
    def badgeHtml = ''
    if (badge) {
      def bg = glow ? glow.badge : 'rgba(255,255,255,0.08)'
      def bBdr = glow ? glow.bBdr : 'rgba(255,255,255,0.18)'
      def bTxt = glow ? glow.bText : 'rgba(255,255,255,0.7)'
      badgeHtml = """<span class="gd-badge" style="background:${bg};border-color:${bBdr};color:${bTxt};">${badge}</span>"""
    }

    // ── subtitle ──────────────────────────────────────────────────────────
    def subHtml = subtitle ? """<span class="gd-sub">${subtitle}</span>""" : ''

    out << """<div class="gd-panel${openCls}" id="${panelId}"${panelStyle} data-animate="${animate}">
  <button class="gd-trigger" onclick="gdToggle('${panelId}')" aria-expanded="${isOpen}" aria-controls="${panelId}-body">
    <span class="gd-icon">${iconSvg}</span>
    <span class="gd-title-wrap">
      <span class="gd-title">${title}</span>
      ${subHtml}
    </span>
    ${badgeHtml}
    <span class="gd-chevron">
      <svg viewBox="0 0 12 12"><polyline points="2,4 6,8 10,4"/></svg>
    </span>
  </button>
  <div class="gd-body" id="${panelId}-body" role="region" aria-labelledby="${panelId}">
    <div class="gd-content">
      ${body()}
    </div>
  </div>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:specTable>…</glass:specTable>
  // ─────────────────────────────────────────────────────────────────────────
  def specTable = { attrs, body ->
    out << """<div class="gd-spec-table">${body()}</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:specRow key value [status]/>
  // ─────────────────────────────────────────────────────────────────────────
  def specRow = { attrs ->
    def key = attrs.key?.encodeAsHTML() ?: ''
    def value = attrs.value?.encodeAsHTML() ?: ''
    def status = attrs.status ?: 'none'
    def valCls = status == 'ok' ? ' gd-ok' : (status == 'warn' ? ' gd-warn' : '')

    out << """<div class="gd-spec-row">
  <span class="gd-spec-key">${key}</span>
  <span class="gd-spec-val${valCls}">${value}</span>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:notice [type]>…</glass:notice>
  // ─────────────────────────────────────────────────────────────────────────
  def notice = { attrs, body ->
    def type = attrs.type ?: 'info'
    def config = NOTICES[type] ?: NOTICES.info
    def stroke = config.stroke
    def iconPath = config.icon == 'triangle'
      ? """<path d="M8 2L15 14H1Z"/><line x1="8" y1="7" x2="8" y2="10"/><circle cx="8" cy="12" r="0.5" fill="${stroke}"/>"""
      : """<circle cx="8" cy="8" r="6"/><line x1="8" y1="5" x2="8" y2="8"/><circle cx="8" cy="11" r="0.5" fill="${stroke}"/>"""

    out << """<div class="gd-notice">
  <svg viewBox="0 0 16 16" fill="none" stroke="${stroke}" stroke-width="1.6">${iconPath}</svg>
  <p>${body()}</p>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:stepList>…</glass:stepList>
  // ─────────────────────────────────────────────────────────────────────────
  def stepList = { attrs, body ->
    request.setAttribute('glass.step.count', 0)
    out << """<div class="gd-step-list">${body()}</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:step>…</glass:step>
  // ─────────────────────────────────────────────────────────────────────────
  def step = { attrs, body ->
    def count = (request.getAttribute('glass.step.count') ?: 0) + 1
    request.setAttribute('glass.step.count', count)

    out << """<div class="gd-step">
  <span class="gd-step-num">${count}</span>
  <p>${body()}</p>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:progressList>…</glass:progressList>
  // ─────────────────────────────────────────────────────────────────────────
  def progressList = { attrs, body ->
    out << """<div class="gd-prog-list">${body()}</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:progressItem label value [color]/>
  // ─────────────────────────────────────────────────────────────────────────
  def progressItem = { attrs ->
    def label = attrs.label?.encodeAsHTML() ?: ''
    def value = attrs.value?.toString()?.replaceAll('[^0-9]', '') ?: '0'
    def colorKey = attrs.color ?: 'purple'
    def gradient = PROG_GRADIENTS[colorKey] ?: PROG_GRADIENTS.purple

    out << """<div class="gd-prog-item">
  <div class="gd-prog-label">
    <span>${label}</span>
    <strong>${value}%</strong>
  </div>
  <div class="gd-prog-track">
    <div class="gd-prog-fill" data-value="${value}"
         style="background:linear-gradient(90deg,${gradient})"></div>
  </div>
</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:tagRow>…</glass:tagRow>
  // ─────────────────────────────────────────────────────────────────────────
  def tagRow = { attrs, body ->
    out << """<div class="gd-tag-row">${body()}</div>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:tag>label text</glass:tag>
  // ─────────────────────────────────────────────────────────────────────────
  def tag = { attrs, body ->
    out << """<span class="gd-tag">${body()}</span>"""
  }

  // ─────────────────────────────────────────────────────────────────────────
  // <glass:divider/>
  // ─────────────────────────────────────────────────────────────────────────
  def divider = { attrs ->
    out << """<div class="gd-divider"></div>"""
  }
}
