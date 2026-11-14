package com.murad.app.matrimony.background

/**
 * GlassBackgroundBaseTagLib
 * namespace: gbb
 *
 * Shared infrastructure for the animated glassmorphic background system.
 * MUST be included on every page that uses any gb-variant taglib.
 *
 * Tags
 * ────
 *   <gbb:styles/>
 *       Emits the base <style> block: .gb-root, .gb-orbs, .gb-orb,
 * @keyframes gbOrbFloat, .gb-grain, .gb-mesh.
 *       Call once inside <head>.
 *
 *   <gbb:background id="gbRoot" variantClass="gb-cosmic" mesh="false"/>
 *       Emits the fixed .gb-root div with 5 orb children, grain, and mesh.
 *       Normally called by a variant taglib rather than directly — but
 *       can be used standalone if you supply variantClass yourself.
 *
 * Attributes of <gbb:background>
 * ───────────────────────────────
 *   id           OPTIONAL  HTML id for the root div              (default: gbRoot)
 *   variantClass OPTIONAL  CSS class that supplies the palette   (default: gb-cosmic)
 *   mesh         OPTIONAL  "true" to show dot mesh on load       (default: false)
 */
class GlassBackgroundBaseTagLib {

  static namespace = 'gbb'

  // ── styles ───────────────────────────────────────────────────────────────

  def styles = { attrs ->
    out << '''<style>
/* ═══════════════════════════════════════════════════════════════
   GLASS BACKGROUND BASE — GlassBackgroundBaseTagLib
═══════════════════════════════════════════════════════════════ */

/* Fixed layer sits behind all content */
.gb-root {
  position: fixed; inset: 0; z-index: 0;
  overflow: hidden; pointer-events: none;
  transition: background 1.2s cubic-bezier(.4,0,.2,1);
}

/* Orb container fills the root */
.gb-orbs { position: absolute; inset: 0; }

/* Individual orb — sized and coloured by each variant sheet */
.gb-orb {
  position: absolute; border-radius: 50%;
  will-change: transform;
  animation: gbOrbFloat ease-in-out infinite;
}

@keyframes gbOrbFloat {
  0%,100% { transform: translate(0,0)         scale(1);    }
  20%     { transform: translate(32px,-48px)  scale(1.07); }
  40%     { transform: translate(-22px,36px)  scale(0.94); }
  60%     { transform: translate(48px,22px)   scale(1.05); }
  80%     { transform: translate(-28px,-18px) scale(1.02); }
}

/* Film grain overlay */
.gb-grain {
  position: absolute; inset: 0; opacity: .030;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.88' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
  background-size: 160px;
}

/* Dot mesh — hidden by default, shown via .gb-show-mesh */
.gb-mesh {
  position: absolute; inset: 0;
  background-image: radial-gradient(rgba(255,255,255,.09) 1px, transparent 1px);
  background-size: 36px 36px;
  opacity: 0;
  transition: opacity .8s ease;
}
.gb-root.gb-show-mesh .gb-mesh { opacity: 1; }
</style>'''
  }

  // ── background ───────────────────────────────────────────────────────────

  def background = { attrs ->
    String id = attrs.id ?: 'gbRoot'
    String variantClass = attrs.variantClass ?: 'gb-cosmic'
    boolean meshOn = Boolean.parseBoolean(attrs.mesh?.toString() ?: 'false')
    String meshClass = meshOn ? ' gb-show-mesh' : ''

    out << """<div class="gb-root ${variantClass}${meshClass}" id="${id}">
  <div class="gb-orbs">
    <div class="gb-orb"></div>
    <div class="gb-orb"></div>
    <div class="gb-orb"></div>
    <div class="gb-orb"></div>
    <div class="gb-orb"></div>
  </div>
  <div class="gb-grain"></div>
  <div class="gb-mesh"></div>
</div>"""
  }
}
