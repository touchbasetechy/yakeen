package com.murad.app.matrimony

/**
 * Emits the .slide-in-fade-in and .slide-out-fade-out utility classes once
 * per page, after delegating to both keyframe taglibs to guarantee the
 * @keyframes are always present before the classes that reference them.
 *
 * CSS class contract
 * ──────────────────
 *  .slide-in-fade-in
 *      Applies slideDownFadeIn (0.32 s, ease-in curve).
 *      Sets display:flex !important to override a .d-none that hides the
 *      element at rest. Use this class to show a hidden element with a
 *      slide-down + fade-in entrance.
 *
 *  .slide-out-fade-out
 *      Applies slideUpFadeOut (0.25 s, ease-out curve).
 *      Keeps display:flex !important so the element remains visible during
 *      the exit animation. After the animationend event fires, JS should
 *      remove this class and restore .d-none so the element takes no space.
 *
 * Usage (call once, typically in <head>):
 *   <glass:slideAnimationStyles/>
 *
 * This taglib also calls <glass:slideInFadeInKeyframes/> and
 * <glass:slideOutFadeOutKeyframes/> internally — you do not need to call
 * the keyframe taglibs separately unless you need just the keyframes.
 */
class SlideAnimationStylesTagLib {

  static namespace = "glass"

  static final String STYLES_EMITTED = 'glass_slideAnimationStylesEmitted'

  def slideAnimationStyles = {
    // Delegate keyframes first (each guards itself with its own pageScope key)
    glass.slideInFadeInKeyframes()
    glass.slideOutFadeOutKeyframes()

    if (pageScope[STYLES_EMITTED]) return
    pageScope[STYLES_EMITTED] = true

    out << '''
<style>
/* ── .slide-in-fade-in ───────────────────────────────────────────────────────
   Attach to an element (replacing .d-none) to trigger the slide-down + fade-in
   entrance animation.
   display:flex !important overrides Bootstrap's .d-none (display:none).
   The animation curve (0.22, 0.61, 0.36, 1) gives a snappy settle-in feel. */
.slide-in-fade-in {
  display           : flex !important;
  opacity           : 1;
  -webkit-animation : slideDownFadeIn 0.32s cubic-bezier(0.22, 0.61, 0.36, 1) both;
          animation : slideDownFadeIn 0.32s cubic-bezier(0.22, 0.61, 0.36, 1) both;
}

/* ── .slide-out-fade-out ─────────────────────────────────────────────────────
   Attach to a visible element to trigger the slide-up + fade-out exit animation.
   display:flex !important keeps the element in the layout during the animation;
   JS restores .d-none in the animationend callback so it collapses afterwards.
   The animation curve (0.55, 0, 1, 0.45) gives a quick, decisive exit.     */
.slide-out-fade-out {
  display           : flex !important;
  opacity           : 0;
  -webkit-animation : slideUpFadeOut 0.25s cubic-bezier(0.55, 0, 1, 0.45) both;
          animation : slideUpFadeOut 0.25s cubic-bezier(0.55, 0, 1, 0.45) both;
}
</style>
'''
  }
}
