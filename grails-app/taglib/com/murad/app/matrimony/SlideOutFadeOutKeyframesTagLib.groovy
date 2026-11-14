package com.murad.app.matrimony

/**
 * Emits the slideUpFadeOut @keyframes block once per page.
 *
 * Owns only the @-webkit-keyframes + @keyframes declarations for the
 * slide-up-fade-out motion. No utility classes are emitted here —
 * those live in SlideAnimationStylesTagLib (<glass:slideAnimationStyles/>).
 *
 * Usage:
 *   <glass:slideOutFadeOutKeyframes/>
 *
 * Automatically called by <glass:slideAnimationStyles/> — you only need
 * to call this directly if you want the keyframe without the utility class.
 */
class SlideOutFadeOutKeyframesTagLib {

  static namespace = "glass"

  static final String KEYFRAME_EMITTED = 'glass_slideOutFadeOutKeyframeEmitted'

  def slideOutFadeOutKeyframes = {
    if (pageScope[KEYFRAME_EMITTED]) return
    pageScope[KEYFRAME_EMITTED] = true

    out << '''
<style>
/* ── slideUpFadeOut ──────────────────────────────────────────────────────────
   Element exits by sliding back up 20 px while fading out to opacity 0.
   -webkit- prefix covers Safari < 9 and old Android Browser.             */
@-webkit-keyframes slideUpFadeOut {
  from {
    opacity           : 1;
    -webkit-transform : translateY(0);
            transform : translateY(0);
  }
  to {
    opacity           : 0;
    -webkit-transform : translateY(-20px);
            transform : translateY(-20px);
  }
}
@keyframes slideUpFadeOut {
  from {
    opacity           : 1;
    -webkit-transform : translateY(0);
            transform : translateY(0);
  }
  to {
    opacity           : 0;
    -webkit-transform : translateY(-20px);
            transform : translateY(-20px);
  }
}
</style>
'''
  }
}
