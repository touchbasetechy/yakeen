package com.murad.app.matrimony.background

/**
 * GlassBackgroundForestTagLib
 * namespace: gbforest
 *
 * Forest variant — near-black green base with deep emerald and pine orbs.
 * Requires GlassBackgroundBaseTagLib (<gbb:styles/>) in the same <head>.
 *
 * Tags
 * ────
 *   <gbforest:styles/>
 *       Emits .gb-forest palette + 5 orb rules. Call after <gbb:styles/>.
 *
 *   <gbforest:background id="gbRoot" mesh="false"/>
 *       Delegates to <gbb:background variantClass="gb-forest"/> so the
 *       root div / orb / grain / mesh markup lives in exactly one place
 *       (GlassBackgroundBaseTagLib). This taglib only supplies the
 *       "gb-forest" palette class.
 *
 * Attributes
 * ──────────
 *   id    OPTIONAL  HTML id   (default: gbRoot)
 *   mesh  OPTIONAL  "true" shows dot mesh on load   (default: false)
 */
class GlassBackgroundForestTagLib {

  static namespace = 'gbforest'

  // ── styles ───────────────────────────────────────────────────────────────

  def styles = { attrs ->
    out << '''<style>
/* ═══════════════════════════════════════════════════════════════
   GLASS BACKGROUND — FOREST VARIANT — GlassBackgroundForestTagLib
   Base colours: #040e08 · pine green · emerald · hunter · forest
═══════════════════════════════════════════════════════════════ */

.gb-forest {
  background:
    radial-gradient(ellipse at 18% 22%, #166534 0%, transparent 55%),
    radial-gradient(ellipse at 80% 15%, #15803d 0%, transparent 50%),
    radial-gradient(ellipse at 65% 78%, #065f46 0%, transparent 55%),
    radial-gradient(ellipse at  8% 80%, #14532d 0%, transparent 45%),
    #040e08;
}

.gb-forest .gb-orb:nth-child(1) {
  width: 580px; height: 580px;
  background: radial-gradient(circle, rgba(22,101,52,.55), transparent 65%);
  top: -130px; left: -90px;
  filter: blur(95px);
  animation-duration: 23s;
}
.gb-forest .gb-orb:nth-child(2) {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(21,128,61,.48), transparent 65%);
  top: -80px; right: -90px;
  filter: blur(90px);
  animation-duration: 29s; animation-delay: -9s;
}
.gb-forest .gb-orb:nth-child(3) {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(6,95,70,.50), transparent 65%);
  bottom: -80px; right: -50px;
  filter: blur(85px);
  animation-duration: 20s; animation-delay: -14s;
}
.gb-forest .gb-orb:nth-child(4) {
  width: 340px; height: 340px;
  background: radial-gradient(circle, rgba(20,83,45,.60), transparent 65%);
  bottom: -50px; left: -60px;
  filter: blur(80px);
  animation-duration: 25s; animation-delay: -6s;
}
.gb-forest .gb-orb:nth-child(5) {
  width: 270px; height: 270px;
  background: radial-gradient(circle, rgba(21,128,61,.28), transparent 65%);
  top: 46%; left: 46%;
  filter: blur(100px);
  animation-duration: 18s; animation-delay: -11s;
}
</style>'''
  }

  // ── background ───────────────────────────────────────────────────────────
  // Delegates entirely to GlassBackgroundBaseTagLib's <gbb:background/> so the
  // root/orbs/grain/mesh markup is defined in exactly one place. This taglib's
  // only job is to pick the "gb-forest" variant class.

  def background = { attrs ->
    String id = attrs.id ?: 'gbRoot'
    boolean meshOn = Boolean.parseBoolean(attrs.mesh?.toString() ?: 'false')

    out << gbb.background(id: id, variantClass: 'gb-forest', mesh: meshOn.toString())
  }
}
