package com.murad.app.matrimony.background

/**
 * GlassBackgroundOceanTagLib
 * namespace: gbocean
 *
 * Ocean variant — deep black-blue base with sky-blue and slate orbs.
 * Requires GlassBackgroundBaseTagLib (<gbb:styles/>) in the same <head>.
 *
 * Tags
 * ────
 *   <gbocean:styles/>
 *       Emits .gb-ocean palette + 5 orb rules. Call after <gbb:styles/>.
 *
 *   <gbocean:background id="gbRoot" mesh="false"/>
 *       Delegates to <gbb:background variantClass="gb-ocean"/> so the
 *       root div / orb / grain / mesh markup lives in exactly one place
 *       (GlassBackgroundBaseTagLib). This taglib only supplies the
 *       "gb-ocean" palette class.
 *
 * Attributes
 * ──────────
 *   id    OPTIONAL  HTML id   (default: gbRoot)
 *   mesh  OPTIONAL  "true" shows dot mesh on load   (default: false)
 */
class GlassBackgroundOceanTagLib {

  static namespace = 'gbocean'

  // ── styles ───────────────────────────────────────────────────────────────

  def styles = { attrs ->
    out << '''<style>
/* ═══════════════════════════════════════════════════════════════
   GLASS BACKGROUND — OCEAN VARIANT — GlassBackgroundOceanTagLib
   Base colours: #020d1a · sky-blue · cyan · slate
═══════════════════════════════════════════════════════════════ */

.gb-ocean {
  background:
    radial-gradient(ellipse at 10% 20%, #0369a1 0%, transparent 55%),
    radial-gradient(ellipse at 82% 12%, #0891b2 0%, transparent 50%),
    radial-gradient(ellipse at 65% 82%, #0284c7 0%, transparent 55%),
    radial-gradient(ellipse at  5% 82%, #164e63 0%, transparent 45%),
    #020d1a;
}

.gb-ocean .gb-orb:nth-child(1) {
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(3,105,161,.52), transparent 65%);
  top: -140px; left: -100px;
  filter: blur(95px);
  animation-duration: 23s;
}
.gb-ocean .gb-orb:nth-child(2) {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(8,145,178,.45), transparent 65%);
  top: -80px; right: -90px;
  filter: blur(90px);
  animation-duration: 29s; animation-delay: -7s;
}
.gb-ocean .gb-orb:nth-child(3) {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(2,132,199,.42), transparent 65%);
  bottom: -80px; right: -55px;
  filter: blur(85px);
  animation-duration: 22s; animation-delay: -13s;
}
.gb-ocean .gb-orb:nth-child(4) {
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(22,78,99,.60), transparent 65%);
  bottom: -55px; left: -40px;
  filter: blur(80px);
  animation-duration: 27s; animation-delay: -4s;
}
.gb-ocean .gb-orb:nth-child(5) {
  width: 280px; height: 280px;
  background: radial-gradient(circle, rgba(8,145,178,.30), transparent 65%);
  top: 40%; left: 43%;
  filter: blur(100px);
  animation-duration: 17s; animation-delay: -10s;
}
</style>'''
  }

  // ── background ───────────────────────────────────────────────────────────
  // Delegates entirely to GlassBackgroundBaseTagLib's <gbb:background/> so the
  // root/orbs/grain/mesh markup is defined in exactly one place. This taglib's
  // only job is to pick the "gb-ocean" variant class.

  def background = { attrs ->
    String id = attrs.id ?: 'gbRoot'
    boolean meshOn = Boolean.parseBoolean(attrs.mesh?.toString() ?: 'false')

    out << gbb.background(id: id, variantClass: 'gb-ocean', mesh: meshOn.toString())
  }
}
