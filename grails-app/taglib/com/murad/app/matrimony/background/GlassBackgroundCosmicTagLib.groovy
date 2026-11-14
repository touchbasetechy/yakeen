package com.murad.app.matrimony.background

/**
 * GlassBackgroundCosmicTagLib
 * namespace: gbcosmic
 *
 * Cosmic variant — deep navy base with blue, purple, cyan, pink orbs.
 * Requires GlassBackgroundBaseTagLib (<gbb:styles/>) in the same <head>.
 *
 * Tags
 * ────
 *   <gbcosmic:styles/>
 *       Emits the variant <style> block: .gb-cosmic palette + 5 orb rules.
 *       Call once inside <head>, after <gbb:styles/>.
 *
 *   <gbcosmic:background id="gbRoot" mesh="false"/>
 *       Convenience wrapper — delegates to <gbb:background variantClass="gb-cosmic"/>.
 *       Use this instead of <gbb:background> so callers never have to know
 *       the internal class name.
 *
 * Attributes of <gbcosmic:background>
 * ─────────────────────────────────────
 *   id    OPTIONAL  HTML id for the root div   (default: gbRoot)
 *   mesh  OPTIONAL  "true" to show dot mesh    (default: false)
 */
class GlassBackgroundCosmicTagLib {

  static namespace = 'gbcosmic'

  // ── styles ───────────────────────────────────────────────────────────────

  def styles = { attrs ->
    out << '''<style>
/* ═══════════════════════════════════════════════════════════════
   GLASS BACKGROUND — COSMIC VARIANT — GlassBackgroundCosmicTagLib
   Base colours: navy #0d1230 · blue · purple · cyan · pink
═══════════════════════════════════════════════════════════════ */

.gb-cosmic {
  background:
    radial-gradient(ellipse at 15% 25%, #3a6fff 0%, transparent 55%),
    radial-gradient(ellipse at 85% 10%, #a855f7 0%, transparent 50%),
    radial-gradient(ellipse at 70% 80%, #06b6d4 0%, transparent 55%),
    radial-gradient(ellipse at 10% 85%, #ec4899 0%, transparent 45%),
    #0d1230;
}

.gb-cosmic .gb-orb:nth-child(1) {
  width: 600px; height: 600px;
  background: radial-gradient(circle, rgba(58,111,255,.50), transparent 65%);
  top: -150px; left: -100px;
  filter: blur(95px);
  animation-duration: 22s;
}
.gb-cosmic .gb-orb:nth-child(2) {
  width: 500px; height: 500px;
  background: radial-gradient(circle, rgba(168,85,247,.44), transparent 65%);
  top: -80px; right: -100px;
  filter: blur(90px);
  animation-duration: 28s; animation-delay: -8s;
}
.gb-cosmic .gb-orb:nth-child(3) {
  width: 420px; height: 420px;
  background: radial-gradient(circle, rgba(6,182,212,.42), transparent 65%);
  bottom: -90px; right: -60px;
  filter: blur(85px);
  animation-duration: 20s; animation-delay: -14s;
}
.gb-cosmic .gb-orb:nth-child(4) {
  width: 360px; height: 360px;
  background: radial-gradient(circle, rgba(236,72,153,.40), transparent 65%);
  bottom: -60px; left: -50px;
  filter: blur(80px);
  animation-duration: 25s; animation-delay: -5s;
}
.gb-cosmic .gb-orb:nth-child(5) {
  width: 300px; height: 300px;
  background: radial-gradient(circle, rgba(58,111,255,.28), transparent 65%);
  top: 44%; left: 44%;
  filter: blur(100px);
  animation-duration: 18s; animation-delay: -11s;
}
</style>'''
  }

  // ── background ───────────────────────────────────────────────────────────
  // Delegates entirely to GlassBackgroundBaseTagLib's <gbb:background/> so the
  // root/orbs/grain/mesh markup is defined in exactly one place. This taglib's
  // only job is to pick the "gb-cosmic" variant class.

  def background = { attrs ->
    String id = attrs.id ?: 'gbRoot'
    boolean meshOn = Boolean.parseBoolean(attrs.mesh?.toString() ?: 'false')

    out << gbb.background(id: id, variantClass: 'gb-cosmic', mesh: meshOn.toString())
  }
}
