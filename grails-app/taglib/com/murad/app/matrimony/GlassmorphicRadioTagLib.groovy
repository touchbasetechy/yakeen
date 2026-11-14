package com.murad.app.matrimony

class GlassmorphicRadioTagLib {
  static namespace = "glass"

  /**
   * Creates a glassmorphic radio button with title and description
   *
   * Example usage:
   * <glass:radio name="delivery" value="standard" id="opt1"
   *              title="Standard Delivery"
   *              description="5-7 business days • Free"
   *              checked="true"
   *              disabled="true"/>
   *
   * Attributes:
   * - name:        Radio input name (required)
   * - value:       Radio value (required)
   * - id:          Input ID (required, used for label association)
   * - title:       Main title text (required)
   * - description: Description text (optional)
   * - checked:     Whether checked (optional, default: false)
   * - disabled:    Whether disabled (optional, default: false)
   *                Accepts: true / false / "true" / "false" / 1 / 0 / "yes"
   *                Disabled cards are dimmed, show a not-allowed cursor on
   *                hover (same as dp:picker's disabled trigger), and have no
   *                hover lift, shimmer or active feedback.
   *
   * FONT DETAILS:
   * Title and description use the same font details as glass:text defaults:
   * font-family inherit, 16px, normal weight, #ffffff, opacity 0.6.
   *
   * NOTE ON HEIGHT MATCHING (--gform-height):
   * .radio-glass is pinned to the shared --gform-height token (also
   * declared in glass:select's stylesheet and dp:picker's stylesheet, and
   * sourced from glass:input's shell height) so this control renders at
   * the same height as a text input field. --gform-height is a
   * SINGLE-LINE height, so this only looks right when `description` is
   * omitted — a two-line title+description card needs more vertical room
   * than a single-line text input, so using `description` alongside
   * height-matched siblings will either clip the description or require
   * raising --gform-height everywhere (which would also make the text
   * input taller). Prefer omitting `description` for radios placed
   * alongside height-matched inputs/selects/pickers.
   */
  def radio = { attrs ->
    // ── Validate required attributes ─────────────────────────────────────────
    if (!attrs.name) {
      throwTagError("Attribute 'name' is required")
    }
    if (!attrs.value) {
      throwTagError("Attribute 'value' is required")
    }
    if (!attrs.id) {
      throwTagError("Attribute 'id' is required")
    }
    if (!attrs.title) {
      throwTagError("Attribute 'title' is required")
    }
    // ────────────────────────────────────────────────────────────────────────

    // Extract attributes
    String name = attrs.name
    String value = attrs.value
    String id = attrs.id
    String title = attrs.title
    String description = attrs.description ?: ''

    // ── Robust boolean coercion ──────────────────────────────────────────────
    boolean checked = isTruthy(attrs.checked)
    boolean disabled = isTruthy(attrs.disabled)
    // ────────────────────────────────────────────────────────────────────────

    // Build conditional HTML attributes
    String checkedAttr = checked ? ' checked' : ''
    String disabledAttr = disabled ? ' disabled' : ''
    String ariaDisabled = disabled ? ' aria-disabled="true"' : ''
    String tabIndex = disabled ? ' tabindex="-1"' : ''

    // ── Wrapper class carries both states so CSS can target combinations ─────
    // is-disabled          → disabled, not checked
    // is-disabled is-checked → disabled AND checked
    List<String> wrapperClasses = ['radio-glass']
    if (disabled) wrapperClasses << 'is-disabled'
    if (checked) wrapperClasses << 'is-checked'
    String wrapperClass = wrapperClasses.join(' ')
    // ────────────────────────────────────────────────────────────────────────

    // Generate HTML
    out << """
        <div class="${wrapperClass}">
            <input type="radio"
                   id="${id}"
                   name="${name}"
                   value="${value}"${checkedAttr}${disabledAttr}${ariaDisabled}${tabIndex}>
            <label for="${id}">
                <div class="glass-title">${title}</div>
                ${description ? "<div class=\"glass-desc\">${description}</div>" : ''}
            </label>
        </div>
        """
  }

  /**
   * Treats the following as TRUE (case-insensitive where applicable):
   *   Boolean true, Integer/Long 1, String "true" / "yes" / "on" / "1"
   * Everything else (null, false, 0, "false", "no", "off", "0") → false.
   */
  private static boolean isTruthy(Object val) {
    if (val == null) return false
    if (val instanceof Boolean) return val
    if (val instanceof Number) return val.intValue() == 1
    switch (val.toString().trim().toLowerCase()) {
      case 'true': case 'yes': case 'on': case '1': return true
      default: return false
    }
  }

  /**
   * Creates the required CSS for glassmorphic radio buttons
   * Call this once in your GSP head section
   *
   * Usage: <glass:styles/>
   */
  def styles = { attrs ->
    out << """
        <style>
            /* ── Shared cross-taglib height token ──────────────────────────
               Same value as glass:textInputStyles' .input-glass, glass:select's
               .gsl-wrapper, and dp:picker's .dp-trigger — keeps every glass
               form control the same height. Redeclaring :root here is safe;
               this stylesheet may be emitted independently of the others.
            ─────────────────────────────────────────────────────────────── */
            :root {
                --gform-height: 50px;
            }

            .radio-glass {
                position: relative;
                margin-bottom: 15px;
                /* Pins this control to the same height as the text-input
                   shell (.input-glass). Correct for single-line usage only
                   — see the note above the `radio` closure for the
                   two-line (title + description) case. */
                height: var(--gform-height);
                box-sizing: border-box;
            }

            /* ── Hidden native input ────────────────────────────────────────
               z-index: -1 keeps the input behind the label so clicks always
               land on the label — prevents disabled inputs being toggled.
            ─────────────────────────────────────────────────────────────── */
            .radio-glass input {
                position : absolute;
                opacity  : 0;
                cursor   : pointer;
                width    : 100%;
                height   : 100%;
                top      : 0;
                left     : 0;
                margin   : 0;
                z-index  : -1;
            }

            .radio-glass label {
                display: -webkit-inline-box;
                display: -webkit-inline-flex;
                display: -ms-inline-flexbox;
                display: inline-flex;
                -webkit-flex-direction: column;
                    -ms-flex-direction: column;
                        flex-direction: column;
                /* Vertically center the title (and description, if it still
                   fits) within the fixed-height card. */
                -webkit-box-pack: center;
                    -ms-flex-pack: center;
                        justify-content: center;
                height: 100%;
                box-sizing: border-box;
                padding: 0 25px;
                -webkit-border-radius: 16px;
                        border-radius: 16px;
                cursor: pointer;
                -webkit-transition: all 0.4s ease;
                        transition: all 0.4s ease;
                background: rgba(255, 255, 255, 0.1);
                -webkit-backdrop-filter: blur(10px);
                        backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                -webkit-box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                /* Font details match glass:text defaults */
                font-family: inherit;
                font-size: 16px;
                font-weight: normal;
                color: #ffffff;
                position: relative;
                z-index: 1;
                overflow: hidden;
            }

            /* Shimmer effect */
            .radio-glass label::before {
                content: '';
                position: absolute;
                top: 0;
                left: -100%;
                width: 100%;
                height: 100%;
                background: -webkit-linear-gradient(left, transparent, rgba(255, 255, 255, 0.2), transparent);
                background:         linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
                -webkit-transition: left 0.5s ease;
                        transition: left 0.5s ease;
            }

            .radio-glass label:hover::before {
                left: 100%;
            }

            /* Hover state */
            .radio-glass label:hover {
                background: rgba(255, 255, 255, 0.15);
                border-color: rgba(255, 255, 255, 0.3);
                -webkit-transform: translateY(-2px);
                        transform: translateY(-2px);
                -webkit-box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
                        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
            }

            /* Checked state (interactive) */
            .radio-glass input:checked + label {
                background: rgba(255, 255, 255, 0.25);
                border-color: rgba(255, 255, 255, 0.7);
                border-width: 2px;
                -webkit-box-shadow: 0 12px 40px rgba(255, 255, 255, 0.1),
                                    inset 0 0 0 1px rgba(255, 255, 255, 0.3);
                        box-shadow: 0 12px 40px rgba(255, 255, 255, 0.1),
                                    inset 0 0 0 1px rgba(255, 255, 255, 0.3);
            }

            /* ── Title / description: same font details as glass:text defaults
               (16px, normal weight, #ffffff, opacity 0.6) ───────────────── */
            .glass-title {
                font-family: inherit;
                font-size: 16px;
                font-weight: normal;
                color: #ffffff;
                opacity: 0.6;
                margin-bottom: 5px;
            }

            .glass-desc {
                font-family: inherit;
                font-size: 16px;
                font-weight: normal;
                color: #ffffff;
                opacity: 0.6;
            }

            /* Focus state for accessibility */
            .radio-glass input:focus + label {
                outline: 2px solid rgba(255, 255, 255, 0.5);
                outline-offset: 3px;
            }

            /* ── Disabled state (unchecked) ───────────────────────────────────
               Mirrors dp:picker's disabled trigger: dimmed, cursor not-allowed,
               no hover/active feedback. pointer-events is intentionally NOT
               set to none — that would stop the label from receiving the
               pointer, so the not-allowed cursor would never display. The
               native `disabled` attribute on the input already prevents the
               label click from selecting the radio.
            ─────────────────────────────────────────────────────────────── */
            .radio-glass input[disabled] + label,
            .radio-glass.is-disabled:not(.is-checked) label {
                opacity           : 0.42;
                cursor            : not-allowed;
                -webkit-filter    : grayscale(35%);
                        filter    : grayscale(35%);
                -webkit-transform : none !important;
                        transform : none !important;
                -webkit-box-shadow: none !important;
                        box-shadow: none !important;
                border-color      : rgba(255, 255, 255, 0.12) !important;
                background        : rgba(255, 255, 255, 0.05) !important;
            }

            /* Children inherit the not-allowed cursor explicitly */
            .radio-glass input[disabled] + label *,
            .radio-glass.is-disabled label * {
                cursor: not-allowed;
            }

            /* Kill shimmer on disabled (unchecked) */
            .radio-glass input[disabled] + label::before,
            .radio-glass.is-disabled:not(.is-checked) label::before {
                display: none !important;
            }

            /* ── Disabled + Checked state ─────────────────────────────────────
               The item is locked but its selected state must remain visible.
               Retains the checked card appearance (background, border, glow)
               while still communicating non-interactivity (reduced opacity,
               muted glow, no shimmer, not-allowed cursor, no hover lift).
            ─────────────────────────────────────────────────────────────── */
            .radio-glass.is-disabled.is-checked label {
                /* Checked visuals — dimmed to signal non-interactivity */
                background        : rgba(255, 255, 255, 0.18) !important;
                border-color      : rgba(255, 255, 255, 0.5)  !important;
                border-width      : 2px                        !important;
                -webkit-box-shadow: 0 8px 28px rgba(255, 255, 255, 0.07),
                                    inset 0 0 0 1px rgba(255, 255, 255, 0.2) !important;
                        box-shadow: 0 8px 28px rgba(255, 255, 255, 0.07),
                                    inset 0 0 0 1px rgba(255, 255, 255, 0.2) !important;

                /* Non-interactive signals (pointer-events left enabled so
                   the not-allowed cursor shows, as with dp:picker) */
                opacity           : 0.65;
                cursor            : not-allowed;
                -webkit-filter    : grayscale(20%);
                        filter    : grayscale(20%);
                -webkit-transform : none !important;
                        transform : none !important;
            }

            /* Lock icon overlay — communicates "selected but locked" clearly */
            .radio-glass.is-disabled.is-checked label::after {
                content     : '\\1F512';   /* 🔒 */
                position    : absolute;
                top         : 8px;
                right       : 10px;
                font-size   : 11px;
                opacity     : 0.7;
                line-height : 1;
            }

            /* Kill shimmer on disabled+checked */
            .radio-glass.is-disabled.is-checked label::before {
                display: none !important;
            }

            /* Belt-and-braces: lock the hidden input itself */
            .radio-glass input[disabled],
            .radio-glass.is-disabled input {
                pointer-events : none;
                cursor         : not-allowed;
            }

            .radio-group {
                display: -webkit-box;
                display: -webkit-flex;
                display: -ms-flexbox;
                display: flex;
                -webkit-flex-direction: column;
                    -ms-flex-direction: column;
                        flex-direction: column;
                gap: 15px;
            }
        </style>
        """
  }

  /**
   * Creates a container with glassmorphic background
   *
   * Usage:
   * <glass:container>
   *     <!-- your radio buttons here -->
   * </glass:container>
   */
  def container = { attrs, body ->
    String cssClass = attrs.'class' ?: ''

    out << """
        <style>
            .container-glass {
                background: rgba(255, 255, 255, 0.1);
                -webkit-backdrop-filter: blur(10px);
                        backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.2);
                -webkit-box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
                -webkit-border-radius: 20px;
                        border-radius: 20px;
                padding: 40px;
                max-width: 600px;
                width: 100%;
            }

            .container-glass h2 {
                color: white;
                margin-bottom: 25px;
                font-size: 1.8rem;
                text-align: center;
                text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
            }
        </style>
        <div class="container-glass ${cssClass}">
            ${body()}
        </div>
        """
  }
}
