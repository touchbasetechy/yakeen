package com.murad.app.matrimony.toggle

/**
 * GlassDotMeshTagLib
 *
 * Namespace : glass
 * Tags      : dotMeshStyles  — inject CSS (once, in <head>)
 *             dotMeshOverlay — render the .background-mesh <div> inside .background-root
 *             dotMesh        — render the toggle + wiring JS
 *
 * Z-index layering
 * ─────────────────
 *  .background-mesh      z-index: 0   — background texture, behind everything
 *  .dm-toggle    z-index: 1   — visible pill, but below dropdowns/modals
 *  dropdowns     z-index: 1000 (gcd-menu) — always on top
 *
 * Visual state pattern (identical to CheckboxTagLib)
 * ───────────────────────────────────────────────────
 * The hidden native <input type="checkbox" class="dm-toggle-input"> drives
 * all visual state through CSS adjacent-sibling selectors:
 *
 *   .dm-toggle-input:checked + .dm-toggle-dot          → glowing dot
 *   .dm-toggle:has(.dm-toggle-input:checked)            → active pill
 *   .dm-toggle-input:focus  + .dm-toggle-dot            → focus ring
 *   .dm-toggle-input:disabled + .dm-toggle-dot          → dimmed
 *
 * No JS class manipulation is used for the visual state.
 * JS only handles the side effect: toggling .show-mesh on the background-root.
 *
 * ─── Typical usage ────────────────────────────────────────────────────────
 *
 *  In <head>:
 *      <glass:dotMeshStyles/>
 *
 *  Inside your .background-root div (after any grain/blur layers):
 *      <glass:dotMeshOverlay/>
 *
 *  Anywhere visible on the page:
 *      <glass:dotMesh
 *          targetId="background"
 *          id="showMesh"
 *          name="showMesh"
 *          label="Dot mesh"
 *          dotSize="1px"
 *          spacing="36px"
 *          dotColor="rgba(255,255,255,.09)"
 *          initialOn="false"/>
 *
 * ─── Attributes ───────────────────────────────────────────────────────────
 *
 *  dotMeshStyles  — no attributes
 *
 *  dotMeshOverlay
 *    dotSize    (optional, default "1px")
 *    spacing    (optional, default "36px 36px")
 *    dotColor   (optional, default "rgba(255,255,255,.09)")
 *
 *  dotMesh
 *    targetId   (required)  — id of the .background-root element to toggle
 *    id         (optional)  — checkbox id
 *    name       (optional)  — checkbox name attribute
 *    label      (optional, default "Dot mesh")
 *    dotSize    (optional, default "1px")
 *    spacing    (optional, default "36px 36px")
 *    dotColor   (optional, default "rgba(255,255,255,.09)")
 *    initialOn  (optional, default "false") — "true" to start checked
 *    disabled   (optional, default "false")
 *
 * ─────────────────────────────────────────────────────────────────────────
 */
class GlassDotMeshTagLib {

  static namespace = 'glass'

  // ── 1. <glass:dotMeshStyles/> ──────────────────────────────────────────
  def dotMeshStyles = { attrs ->
    out << '''
<style>
/* ── GlassDotMesh : shared styles ────────────────────────────────── */

/* Overlay div — carries the dot pattern inside .background-root.
   z-index: 0 pins it to the background layer so every interactive
   UI element (dropdowns at z-index 1000, modals, etc.) renders above it. */
.background-mesh {
    position        : absolute;
    inset           : 0;
    z-index         : -1;
    background-image: radial-gradient(
                        var(--dm-dot-color, rgba(255,255,255,.09))
                        var(--dm-dot-size,  1px),
                        transparent
                        var(--dm-dot-size,  1px));
    background-size : var(--dm-spacing, 36px 36px);
    opacity         : 0;
    pointer-events  : none;
    transition      : opacity .8s ease;
}

.background-root.show-mesh .background-mesh {
    opacity: 1;
}

/* ── Toggle pill ──────────────────────────────────────────────────── */
/* z-index: 1 keeps the pill above the mesh but well below dropdowns
   (gcd-menu uses z-index: 1000) so open menus always overlap the pill. */
.dm-toggle {
    display            : inline-flex;
    align-items        : center;
    gap                : 8px;
    padding            : 8px 16px;
    border-radius      : 999px;
    cursor             : pointer;
    font-family        : \'DM Mono\', monospace;
    font-size          : 10px;
    letter-spacing     : .14em;
    text-transform     : uppercase;
    color              : rgba(200,210,255,.40);
    border             : 1px solid rgba(255,255,255,.10);
    background         : rgba(255,255,255,.05);
    backdrop-filter    : blur(8px);
    -webkit-backdrop-filter: blur(8px);
    transition         : color .25s, border-color .25s, background .25s;
    user-select        : none;
    position           : relative;
    z-index            : 1;
}

.dm-toggle:hover {
    color        : rgba(200,210,255,.70);
    border-color : rgba(255,255,255,.22);
    background   : rgba(255,255,255,.08);
}

/* Hidden native checkbox — same clip technique as CheckboxTagLib.
   Keeps keyboard focus and screen-reader support without being visible. */
.dm-toggle-input {
    position          : absolute;
    width             : 1px;
    height            : 1px;
    margin            : -1px;
    padding           : 0;
    overflow          : hidden;
    clip              : rect(0, 0, 0, 0);
    white-space       : nowrap;
    border            : 0;
    appearance        : none;
    -webkit-appearance: none;
}

/* Visual dot indicator */
.dm-toggle-dot {
    width         : 8px;
    height        : 8px;
    border-radius : 50%;
    background    : rgba(255,255,255,.22);
    flex-shrink   : 0;
    transition    : background .25s, box-shadow .25s;
    pointer-events: none;
}

/* ── Checked state — driven by CSS adjacent-sibling selector ──────── */
/* Dot glow — white */
.dm-toggle-input:checked + .dm-toggle-dot {
    background: #ffffff;
    box-shadow: 0 0 8px #ffffff, 0 0 16px rgba(255,255,255,.40);
}

/* Pill active appearance */
.dm-toggle:has(.dm-toggle-input:checked) {
    color        : rgba(200,210,255,.80);
    border-color : rgba(255,255,255,.45);
}

/* ── Focus ring — shown on the dot when the hidden input is focused ── */
.dm-toggle-input:focus + .dm-toggle-dot {
    outline   : none;
    box-shadow: 0 0 0 3px rgba(167,139,250,.30);
}

/* ── Disabled state ───────────────────────────────────────────────── */
.dm-toggle-input:disabled + .dm-toggle-dot {
    opacity: 0.45;
    cursor : not-allowed;
}
.dm-toggle:has(.dm-toggle-input:disabled) {
    cursor : not-allowed;
    opacity: 0.60;
}
/* ── END GlassDotMesh ─────────────────────────────────────────────── */
</style>
'''
  }

  // ── 2. <glass:dotMeshOverlay/> ─────────────────────────────────────────
  def dotMeshOverlay = { attrs ->
    def dotSize = attrs.dotSize ?: '1px'
    def spacing = attrs.spacing ?: '36px 36px'
    def dotColor = attrs.dotColor ?: 'rgba(255,255,255,.09)'

    out << """<div class="background-mesh" style="--dm-dot-size:${dotSize};--dm-spacing:${spacing};--dm-dot-color:${dotColor};"></div>"""
  }

  // ── 3. <glass:dotMesh/> ────────────────────────────────────────────────
  def dotMesh = { attrs ->
    def targetId = attrs.targetId ?: 'background'
    def label = attrs.label ?: 'Dot mesh'
    def dotSize = attrs.dotSize ?: '1px'
    def spacing = attrs.spacing ?: '36px 36px'
    def dotColor = attrs.dotColor ?: 'rgba(255,255,255,.09)'
    def initialOn = (attrs.initialOn?.toString() == 'true')
    def disabled = (attrs.disabled?.toString() == 'true')

    def checkboxId = attrs.id ?: ''
    def checkboxName = attrs.name ?: ''
    def idAttr = checkboxId ? " id=\"${checkboxId}\"" : ''
    def nameAttr = checkboxName ? " name=\"${checkboxName}\"" : ''
    def checkedAttr = initialOn ? ' checked' : ''
    def disabledAttr = disabled ? ' disabled' : ''

    // ── Value contract ────────────────────────────────────────────────────
    // HTML inputs cannot hold a native boolean — .value is always a string.
    // The boolean lives in .checked on the checkbox element itself.
    // Read it as:  document.getElementById('id').checked  → true / false (Boolean)
    // Never read   .value — it will always be the string "true".
    def onchangeJs = """(function(cb){
               var root = document.getElementById('${targetId}');
               if (!root) return;
               root.classList.toggle('show-mesh', cb.checked);
               var overlay = root.querySelector('.background-mesh');
               if (overlay) {
                   overlay.style.setProperty('--dm-dot-size',  '${dotSize}');
                   overlay.style.setProperty('--dm-spacing',   '${spacing}');
                   overlay.style.setProperty('--dm-dot-color', '${dotColor}');
               }
           })(this)"""

    out << """
<label class="dm-toggle" title="Toggle dot-mesh overlay">
    <input type="checkbox"${idAttr}${nameAttr}${checkedAttr}${disabledAttr}
           class="dm-toggle-input"
           value="true"
           aria-label="${label}"
           onchange="${onchangeJs}"/>
    <span class="dm-toggle-dot" aria-hidden="true"></span>
    ${label.encodeAsHTML()}
</label>
<script>
/* GlassDotMesh boot — apply initialOn to background-root on page load */
(function () {
    var on = ${initialOn};
    if (!on) return;
    var root = document.getElementById('${targetId}');
    if (root) root.classList.add('show-mesh');
    var overlay = root && root.querySelector('.background-mesh');
    if (overlay) {
        overlay.style.setProperty('--dm-dot-size',  '${dotSize}');
        overlay.style.setProperty('--dm-spacing',   '${spacing}');
        overlay.style.setProperty('--dm-dot-color', '${dotColor}');
    }
})();
</script>
"""
  }
}
