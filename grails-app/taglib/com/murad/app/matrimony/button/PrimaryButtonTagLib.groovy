package com.murad.app.matrimony.button
/**
 * Tag library for rendering a glassmorphism-styled primary button
 * with gradient background and hover effects.
 *
 * Usage:
 * <glass:primaryButton text="Sign In" type="submit" />
 *
 * CSS is injected automatically — no need to call <glass:buttonStyles /> separately.
 */
class PrimaryButtonTagLib {

  static namespace = "glass"

  def primaryButton = { attrs ->

    if (!attrs.text) {
      throwTagError("Tag [primaryButton] is missing required attribute [text]")
    }

    /* ── Inject CSS once per page ──────────────────────────────── */
    if (!pageScope["__glassButtonCssWritten__"]) {
      pageScope["__glassButtonCssWritten__"] = true
      out << '''<style id="glass-primary-button-styles">
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap');

:root {
  --glass-accent-primary      : #00d4ff;
  --glass-accent-secondary    : #7c3aed;
  --glass-button-shadow       : rgba(0, 212, 255, 0.3);
  --glass-button-shadow-hover : rgba(0, 212, 255, 0.4);
}

.btn-glass-primary {
  width            : 100%;
  padding          : 16px;
  background       : linear-gradient(135deg, var(--glass-accent-primary) 0%, var(--glass-accent-secondary) 100%);
  border           : none;
  border-radius    : 12px;
  color            : #ffffff;
  font-size        : 16px;
  font-weight      : 600;
  font-family      : 'Outfit', sans-serif;
  cursor           : pointer;
  transition       : all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position         : relative;
  overflow         : hidden;
  box-shadow       : 0 4px 15px var(--glass-button-shadow);
  display          : inline-flex;
  align-items      : center;
  justify-content  : center;
  gap              : 8px;
  text-decoration  : none;
  outline          : none;
}

.btn-glass-primary::before {
  content    : '';
  position   : absolute;
  top        : 0;
  left       : -100%;
  width      : 100%;
  height     : 100%;
  background : linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition : left 0.5s ease;
}

.btn-glass-primary:hover {
  transform  : translateY(-2px);
  box-shadow : 0 6px 20px var(--glass-button-shadow-hover);
}

.btn-glass-primary:hover::before {
  left : 100%;
}

.btn-glass-primary:active {
  transform : translateY(0);
}

.btn-glass-primary:disabled {
  opacity   : 0.6;
  cursor    : not-allowed;
  transform : none !important;
}

.btn-glass-primary:disabled:hover::before {
  left : -100%;
}

/* Size variations */
.btn-glass-primary.btn-sm { padding: 12px 20px; font-size: 14px; }
.btn-glass-primary.btn-lg { padding: 18px 24px; font-size: 18px; }

/* Width variations */
.btn-glass-primary.btn-auto  { width: auto; }
.btn-glass-primary.btn-block { width: 100%; }

/* Icon */
.btn-glass-primary .btn-icon       { font-size: 1.2em; line-height: 1; }
.btn-glass-primary .btn-icon-left  { margin-right: 8px; }
.btn-glass-primary .btn-icon-right { margin-left: 8px; }

/* Loading */
.btn-glass-primary.loading {
  pointer-events : none;
  opacity        : 0.8;
}
.btn-glass-primary.loading::after {
  content       : '';
  position      : absolute;
  width         : 16px;
  height        : 16px;
  border        : 2px solid rgba(255,255,255,0.3);
  border-top-color : white;
  border-radius : 50%;
  animation     : btn-spin 0.6s linear infinite;
  margin-left   : 8px;
}

@keyframes btn-spin {
  to { transform: rotate(360deg); }
}

/* Button group */
.btn-glass-group {
  display   : flex;
  gap       : 12px;
  flex-wrap : wrap;
}
.btn-glass-group .btn-glass-primary {
  flex      : 1;
  min-width : fit-content;
}
</style>'''
    }

    /* ── Read attributes ─────────────────────────────────────── */
    String text = attrs.text
    String type = attrs.type ?: 'button'
    String id = attrs.id ?: ''
    String name = attrs.name ?: ''
    String value = attrs.value ?: ''
    String size = attrs.size ?: 'md'
    String icon = attrs.icon ?: ''
    String iconPosition = attrs.iconPosition ?: 'left'
    String additionalClass = attrs.'class' ?: ''
    String onclick = attrs.onclick ?: ''
    String title = attrs.title ?: ''
    String ariaLabel = attrs.ariaLabel ?: ''
    String url = attrs.url ?: ''
    String controller = attrs.controller ?: ''
    String action = attrs.action ?: ''
    Map params = attrs.params ?: [:]

    boolean block = attrs.block?.toString()?.toBoolean() != false
    boolean disabled = attrs.disabled?.toString()?.toBoolean() ?: false
    boolean loading = attrs.loading?.toString()?.toBoolean() ?: false

    /* ── Build class list ────────────────────────────────────── */
    List<String> classes = ['btn-glass-primary']
    if (size == 'sm') classes << 'btn-sm'
    else if (size == 'lg') classes << 'btn-lg'
    classes << (block ? 'btn-block' : 'btn-auto')
    if (loading) classes << 'loading'
    if (additionalClass) classes << additionalClass
    String classStr = classes.join(' ')

    /* ── Render ──────────────────────────────────────────────── */
    boolean isLink = url || controller || action

    if (isLink) {
      String href = url ?: createLink(controller: controller, action: action, params: params)
      out << "<a href=\"${href.encodeAsHTML()}\""
      if (id) out << " id=\"${id.encodeAsHTML()}\""
      out << " class=\"${classStr.encodeAsHTML()}\""
      if (title) out << " title=\"${title.encodeAsHTML()}\""
      if (ariaLabel) out << " aria-label=\"${ariaLabel.encodeAsHTML()}\""
      if (onclick) out << " onclick=\"${onclick.encodeAsHTML()}\""
      out << '>'
    } else {
      out << "<button type=\"${type.encodeAsHTML()}\""
      if (id) out << " id=\"${id.encodeAsHTML()}\""
      if (name) out << " name=\"${name.encodeAsHTML()}\""
      if (value) out << " value=\"${value.encodeAsHTML()}\""
      out << " class=\"${classStr.encodeAsHTML()}\""
      if (disabled || loading) out << ' disabled'
      if (title) out << " title=\"${title.encodeAsHTML()}\""
      if (ariaLabel) out << " aria-label=\"${ariaLabel.encodeAsHTML()}\""
      if (onclick) out << " onclick=\"${onclick.encodeAsHTML()}\""
      out << '>'
    }

    if (icon && iconPosition == 'left') out << "<span class=\"btn-icon btn-icon-left\">${icon}</span>"
    out << text.encodeAsHTML()
    if (icon && iconPosition == 'right') out << "<span class=\"btn-icon btn-icon-right\">${icon}</span>"

    out << (isLink ? '</a>' : '</button>')
  }

  /**
   * Keep buttonStyles as a no-op for backwards compatibility —
   * styles are now injected automatically by primaryButton.
   */
  def buttonStyles = { attrs -> }

  def buttonGroup = { attrs, body ->
    String extra = attrs.'class' ?: ''
    String classes = "btn-glass-group ${extra}".trim()
    out << "<div class=\"${classes.encodeAsHTML()}\">"
    out << body()
    out << '</div>'
  }
}
