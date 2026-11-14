package com.murad.app.matrimony.background

/**
 * GlassBackgroundRoseTagLib
 * namespace: gbrose
 *
 * Rose variant — near-black burgundy base with deep rose, pink, violet orbs.
 * Requires GlassBackgroundBaseTagLib (<gbb:styles/>) in the same <head>.
 *
 * Tags
 * ────
 *   <gbrose:styles/>
 *       Emits .gb-rose palette + 5 orb rules. Call after <gbb:styles/>.
 *
 *   <gbrose:background id="gbRoot" mesh="false"/>
 *       Delegates to <gbb:background variantClass="gb-rose"/> so the
 *       root div / orb / grain / mesh markup lives in exactly one place
 *       (GlassBackgroundBaseTagLib). This taglib only supplies the
 *       "gb-rose" palette class.
 *
 * Attributes
 * ──────────
 *   id    OPTIONAL  HTML id   (default: gbRoot)
 *   mesh  OPTIONAL  "true" shows dot mesh on load   (default: false)
 */
class GlassBackgroundRoseTagLib {

  static namespace = 'gbrose'

  // ── styles ───────────────────────────────────────────────────────────────

  def styles = { attrs ->
    out << '''<style>
/* ═══════════════════════════════════════════════════════════════
   GLASS BACKGROUND — ROSE VARIANT — GlassBackgroundRoseTagLib
   Base colours: #120814 · deep rose · pink · violet · crimson
═══════════════════════════════════════════════════════════════ */

.gb-rose {
  background:
    radial-gradient(ellipse at 16% 26%, #9f1239 0%, transparent 55%),
    radial-gradient(ellipse at 82% 12%, #db2777 0%, transparent 50%),
    radial-gradient(ellipse at 64% 80%, #7c3aed 0%, transparent 55%),
    radial-gradient(ellipse at  8% 82%, #be185d 0%, transparent 45%),
    #120814;
}

.gb-rose .gb-orb:nth-child(1) {
  width: 580px; height: 580px;
  background: radial-gradient(circle, rgba(159,18,57,.52), transparent 65%);
  top: -140px; left: -90px;
  filter: blur(95px);
  animation-duration: 22s;
}
.gb-rose .gb-orb:nth-child(2) {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(219,39,119,.46), transparent 65%);
  top: -75px; right: -95px;
  filter: blur(90px);
  animation-duration: 28s; animation-delay: -8s;
}
.gb-rose .gb-orb:nth-child(3) {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(124,58,237,.42), transparent 65%);
  bottom: -80px; right: -55px;
  filter: blur(85px);
  animation-duration: 21s; animation-delay: -13s;
}
.gb-rose .gb-orb:nth-child(4) {
  width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(190,24,93,.48), transparent 65%);
  bottom: -55px; left: -45px;
  filter: blur(80px);
  animation-duration: 25s; animation-delay: -5s;
}
.gb-rose .gb-orb:nth-child(5) {
  width: 280px; height: 280px;
  background: radial-gradient(circle, rgba(219,39,119,.26), transparent 65%);
  top: 45%; left: 44%;
  filter: blur(100px);
  animation-duration: 17s; animation-delay: -10s;
}
</style>'''
  }

  // ── background ───────────────────────────────────────────────────────────
  // Delegates entirely to GlassBackgroundBaseTagLib's <gbb:background/> so the
  // root/orbs/grain/mesh markup is defined in exactly one place. This taglib's
  // only job is to pick the "gb-rose" variant class.

  def background = { attrs ->
    String id = attrs.id ?: 'gbRoot'
    boolean meshOn = Boolean.parseBoolean(attrs.mesh?.toString() ?: 'false')

    out << gbb.background(id: id, variantClass: 'gb-rose', mesh: meshOn.toString())
  }
}
