package com.murad.app.matrimony

/**
 * Emits the slideDownFadeIn @keyframes block once per page.
 *
 * Owns only the @-webkit-keyframes + @keyframes declarations for the
 * slide-down-fade-in motion. No utility classes are emitted here —
 * those live in SlideAnimationStylesTagLib (<glass:slideAnimationStyles/>).
 *
 * Usage:
 *   <glass:slideInFadeInKeyframes/>
 *
 * Automatically called by <glass:slideAnimationStyles/> — you only need
 * to call this directly if you want the keyframe without the utility class.
 */
class SlideInFadeInKeyframesTagLib {

  static namespace = "glass"

  static final String KEYFRAME_EMITTED = 'glass_slideInFadeInKeyframeEmitted'

  def slideInFadeInKeyframes = {
    if (pageScope[KEYFRAME_EMITTED]) return
    pageScope[KEYFRAME_EMITTED] = true

    out << '''
<style>
/* ── slideDownFadeIn ─────────────────────────────────────────────────────────
   Element enters by dropping down 20 px while fading in from opacity 0.
   -webkit- prefix covers Safari < 9 and old Android Browser.             */
@-webkit-keyframes slideDownFadeIn {
  from {
    opacity           : 0;
    -webkit-transform : translateY(-20px);
            transform : translateY(-20px);
  }
  to {
    opacity           : 1;
    -webkit-transform : translateY(0);
            transform : translateY(0);
  }
}
@keyframes slideDownFadeIn {
  from {
    opacity           : 0;
    -webkit-transform : translateY(-20px);
            transform : translateY(-20px);
  }
  to {
    opacity           : 1;
    -webkit-transform : translateY(0);
            transform : translateY(0);
  }
}
</style>
'''
  }
}
