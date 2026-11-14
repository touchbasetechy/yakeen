package com.murad.app.matrimony.page
/**
 * Glassmorphic Navbar Tag Library
 * Provides a beautiful, modern navigation bar with glassmorphism design
 */
class GlassmorphicNavbarTagLib {

  static namespace = "glass"

  /**
   * Renders a glassmorphic navigation bar
   *
   * @attr brand Brand name or logo text
   * @attr brandUrl URL for the brand/logo (default: "/")
   * @attr brandImage URL to brand logo image
   * @attr brandIcon SVG or HTML icon to display before brand text
   * @attr position Position of navbar: "fixed-top", "fixed-bottom", "sticky-top" (default: "fixed-top")
   * @attr transparent Boolean to make navbar fully transparent initially (default: false)
   * @attr theme Color theme: "light", "dark", "auto" (default: "auto")
   * @attr height Height of navbar (default: "70px")
   * @attr blurAmount Blur amount (default: "12px")
   * @attr cssClass Additional CSS classes to apply
   * @attr showSearch Boolean to show search bar (default: false)
   * @attr searchPlaceholder Placeholder for search input (default: "Search...")
   * @attr searchAction Search form action URL
   * @attr container Container type: "fluid", "fixed" (default: "fluid")
   */
  def navbar = { attrs, body ->

    String brand = attrs.brand ?: ''
    String brandUrl = attrs.brandUrl ?: '/'
    String brandImage = attrs.brandImage ?: ''
    String brandIcon = attrs.brandIcon ?: ''
    String position = attrs.position ?: 'fixed-top'
    String theme = attrs.theme ?: 'auto'
    String height = attrs.height ?: '70px'
    String blurAmount = attrs.blurAmount ?: '12px'
    String cssClass = attrs.cssClass ?: ''
    String searchPlaceholder = attrs.searchPlaceholder ?: 'Search...'
    String searchAction = attrs.searchAction ?: '/search'
    String container = attrs.container ?: 'fluid'

    // FIX: GSP always passes attrs as Strings — instanceof Boolean always false
    boolean transparent = attrs.transparent == true || attrs.transparent == 'true'
    boolean showSearch = attrs.showSearch == true || attrs.showSearch == 'true'

    // Unique ID scopes every CSS rule under this navbar instance,
    // ensuring our selectors beat Bootstrap's a, .nav-link, etc.
    String navbarId = "glass-navbar-${System.currentTimeMillis()}"

    out << """
<nav class="glass-navbar ${position} ${theme} ${transparent ? 'transparent' : ''} ${cssClass}" id="${navbarId}">
<style>
/* ══════════════════════════════════════════════════════════════════════
   All rules scoped under #${navbarId} — beats Bootstrap without !important
   where possible; !important used only on color/text-decoration to
   override Bootstrap's a:hover colour inheritance.
   ══════════════════════════════════════════════════════════════════════ */

/* ── Navbar shell ─────────────────────────────────────────────────── */
#${navbarId} {
    width: 100%;
    height: ${height};
    display: flex;
    align-items: center;
    z-index: 1000;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
    background: transparent;
    backdrop-filter: none;
    -webkit-backdrop-filter: none;
    border: none !important;
    outline: none !important;
    box-shadow: none;
    opacity: 0;
    transition: opacity 0.4s ease, background 0.4s ease, backdrop-filter 0.4s ease;
}
#${navbarId}.fixed-top    { position: fixed;  top: 0;    left: 0; }
/* Belt-and-braces: kill any border Bootstrap's .navbar, .navbar-expand-* etc inject */
#${navbarId}.glass-navbar  { border: none !important; border-top: none !important; border-bottom: none !important; border-left: none !important; border-right: none !important; outline: none !important; }
#${navbarId}.fixed-bottom { position: fixed;  bottom: 0; left: 0; }
#${navbarId}.sticky-top   { position: sticky; top: 0; }
#${navbarId}.transparent  { background: transparent !important; backdrop-filter: none !important; -webkit-backdrop-filter: none !important; border: none !important; box-shadow: none !important; }
#${navbarId}.scrolled {
    background: rgba(255, 255, 255, 0.08) !important;
    backdrop-filter: blur(${blurAmount}) !important;
    -webkit-backdrop-filter: blur(${blurAmount}) !important;
    border: none !important;
    box-shadow: none !important;
    opacity: 1 !important;
}

/* ── Container ────────────────────────────────────────────────────── */
#${navbarId} .glass-navbar-container {
    width: 100%;
    max-width: ${container == 'fluid' ? '100%' : '1200px'};
    margin: 0 auto;
    padding: 0 1.5rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

/* ── Entrance animations ──────────────────────────────────────────── */
@keyframes gnb-fadeUp-${navbarId} {
    from { opacity: 0; transform: translateY(8px); }
    to   { opacity: 1; transform: translateY(0);   }
}
#${navbarId} .glass-navbar-brand  { animation: gnb-fadeUp-${navbarId} 0.7s 0.2s cubic-bezier(0.16,1,0.3,1) both; }
#${navbarId} .glass-navbar-menu   { animation: gnb-fadeUp-${navbarId} 0.7s 0.3s cubic-bezier(0.16,1,0.3,1) both; }
#${navbarId} .glass-navbar-toggle { animation: gnb-fadeUp-${navbarId} 0.7s 0.2s cubic-bezier(0.16,1,0.3,1) both; }

/* ── Brand ────────────────────────────────────────────────────────── */
#${navbarId} .glass-navbar-brand,
#${navbarId} a.glass-navbar-brand,
#${navbarId} a.glass-navbar-brand:link,
#${navbarId} a.glass-navbar-brand:visited {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    text-decoration: none !important;
    font-size: 1.5rem;
    font-weight: 700;
    color: #ffffff !important;
    transition: all 0.3s ease;
    outline: none;
    border: none;
}
#${navbarId} .glass-navbar-brand:hover,
#${navbarId} .glass-navbar-brand:focus,
#${navbarId} a.glass-navbar-brand:hover,
#${navbarId} a.glass-navbar-brand:focus {
    opacity: 0.9;
    transform: translateY(-2px);
    outline: none;
    text-decoration: none !important;
    color: #ffffff !important;
}
#${navbarId} .glass-navbar-brand:active { transform: translateY(0); }

#${navbarId} .glass-navbar-brand-icon { width: 32px; height: 32px; flex-shrink: 0; display: flex; align-items: center; justify-content: center; }
#${navbarId} .glass-navbar-brand-icon svg { width: 100%; height: 100%; }
#${navbarId} .glass-navbar-brand-image { height: 40px; width: auto; object-fit: contain; }

#${navbarId} .glass-navbar-brand-text {
    font-size: 1.5rem;
    font-weight: 700;
    letter-spacing: -0.025em;
    background: linear-gradient(135deg, #ffffff 0%, #ffffff 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    transition: all 0.6s ease;
}
#${navbarId} .glass-navbar-brand:hover .glass-navbar-brand-text,
#${navbarId} .glass-navbar-brand:focus .glass-navbar-brand-text,
#${navbarId} .glass-navbar-brand:active .glass-navbar-brand-text {
    background: linear-gradient(135deg, #ffffff 0%, #54CCF5 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

/* ── Layout ───────────────────────────────────────────────────────── */
#${navbarId} .glass-navbar-content {
    display: flex;
    align-items: center;
    gap: 2rem;
    flex: 1;
    justify-content: flex-end;
}
#${navbarId} .glass-navbar-menu {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    list-style: none;
    margin: 0;
    padding: 0;
}

/* ── Search — wrapper is a <li> so it flows naturally in the <ul> ─── */
#${navbarId} .glass-navbar-search-item {
    display: flex;
    align-items: center;
}
#${navbarId} .glass-navbar-search {
    display: contents; /* form itself invisible to flex; li is the flex item */
}
#${navbarId} .glass-navbar-search-wrapper {
    position: relative;
    display: inline-flex;
    align-items: center;
}

/* Base — identical appearance for ALL input states */
#${navbarId} .glass-navbar-search-input,
#${navbarId} .glass-navbar-search-input:hover,
#${navbarId} .glass-navbar-search-input:focus,
#${navbarId} .glass-navbar-search-input:active {
    padding: 0.5rem 2rem 0.5rem 1rem;
    width: 200px;
    font-size: 0.875rem;
    color: #ffffff !important;
    -webkit-text-fill-color: #ffffff !important;
    caret-color: #54CCF5 !important;
    background: rgba(255, 255, 255, 0.12) !important;
    background-color: rgba(255, 255, 255, 0.12) !important;
    border: none !important;
    border-radius: 20px !important;
    backdrop-filter: blur(16px) saturate(180%) !important;
    -webkit-backdrop-filter: blur(16px) saturate(180%) !important;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15),
                inset 0 1px 0 rgba(255, 255, 255, 0.25) !important;
    outline: none !important;
    transition: width 0.3s ease, box-shadow 0.3s ease !important;
}

/* Focus/active — expand + accent glow only */
#${navbarId} .glass-navbar-search-input:focus,
#${navbarId} .glass-navbar-search-input:active {
    width: 260px !important;
    background: rgba(255, 255, 255, 0.18) !important;
    background-color: rgba(255, 255, 255, 0.18) !important;
    box-shadow: 0 0 0 3px rgba(84, 204, 245, 0.25),
                0 0 16px rgba(84, 204, 245, 0.15),
                inset 0 1px 0 rgba(255, 255, 255, 0.3) !important;
}

#${navbarId} .glass-navbar-search-input::placeholder {
    color: rgba(255, 255, 255, 0.5);
    -webkit-text-fill-color: rgba(255, 255, 255, 0.5);
}

/* Autofill — WebKit/Blink: box-shadow paints over the injected background */
#${navbarId} .glass-navbar-search-input:-webkit-autofill,
#${navbarId} .glass-navbar-search-input:-webkit-autofill:hover,
#${navbarId} .glass-navbar-search-input:-webkit-autofill:focus,
#${navbarId} .glass-navbar-search-input:-webkit-autofill:active {
    -webkit-box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.12) inset !important;
    box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.12) inset !important;
    -webkit-text-fill-color: #ffffff !important;
    caret-color: #54CCF5 !important;
    border: none !important;
    border-radius: 20px !important;
    background-color: transparent !important;
    outline: none !important;
    transition: background-color 999999s ease-in-out 0s, width 0.3s ease !important;
}

/* Autofill — Firefox */
#${navbarId} .glass-navbar-search-input:autofill,
#${navbarId} .glass-navbar-search-input:autofill:hover,
#${navbarId} .glass-navbar-search-input:autofill:focus {
    -webkit-box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.12) inset !important;
    box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.12) inset !important;
    -webkit-text-fill-color: #ffffff !important;
    caret-color: #54CCF5 !important;
    border: none !important;
    border-radius: 20px !important;
    outline: none !important;
    transition: background-color 999999s ease-in-out 0s, width 0.3s ease !important;
}

/* Clear button */
#${navbarId} .glass-navbar-search-clear {
    position: absolute;
    right: 0.6rem;
    top: 50%;
    transform: translateY(-50%);
    display: none;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
    background: rgba(255, 255, 255, 0.2);
    border: none;
    border-radius: 50%;
    cursor: pointer;
    padding: 0;
    line-height: 1;
    color: rgba(255, 255, 255, 0.8);
    font-size: 11px;
    transition: background 0.2s ease, color 0.2s ease;
    outline: none;
}
#${navbarId} .glass-navbar-search-clear:hover {
    background: rgba(84, 204, 245, 0.35);
    color: #ffffff;
}
#${navbarId} .glass-navbar-search-clear.visible { display: inline-flex; }

/* ── Hamburger toggle ─────────────────────────────────────────────── */
#${navbarId} .glass-navbar-toggle {
    display: none;
    flex-direction: column;
    gap: 4px;
    background: rgba(255, 255, 255, 0.15);
    border: none;
    border-radius: 8px;
    padding: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
}
#${navbarId} .glass-navbar-toggle:hover { background: rgba(255, 255, 255, 0.25); }
#${navbarId} .glass-navbar-toggle span { display: block; width: 24px; height: 2px; background: #ffffff; border-radius: 2px; transition: all 0.3s ease; }
#${navbarId} .glass-navbar-toggle.active span:nth-child(1) { transform: rotate(45deg) translateY(7px); }
#${navbarId} .glass-navbar-toggle.active span:nth-child(2) { opacity: 0; }
#${navbarId} .glass-navbar-toggle.active span:nth-child(3) { transform: rotate(-45deg) translateY(-7px); }

/* ══════════════════════════════════════════════════════════════════════
   NAV ITEMS (glass:navItem) — white default, #54CCF5 hover/focus/active
   ══════════════════════════════════════════════════════════════════════ */
#${navbarId} .glass-nav-item,
#${navbarId} a.glass-nav-item,
#${navbarId} a.glass-nav-item:link,
#${navbarId} a.glass-nav-item:visited {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    text-decoration: none !important;
    color: #ffffff !important;
    font-weight: 500;
    font-size: 0.9375rem;
    border-radius: 12px;
    transition: color 0.3s ease, background 0.3s ease, transform 0.3s ease;
    position: relative;
    white-space: nowrap;
    outline: none !important;
}
#${navbarId} .glass-nav-item:hover,
#${navbarId} .glass-nav-item:focus,
#${navbarId} a.glass-nav-item:hover,
#${navbarId} a.glass-nav-item:focus {
    color: #54CCF5 !important;
    text-decoration: none !important;
    outline: none !important;
    background: rgba(84, 204, 245, 0.1);
    transform: translateY(-2px);
}
#${navbarId} .glass-nav-item:active,
#${navbarId} a.glass-nav-item:active {
    color: #54CCF5 !important;
    text-decoration: none !important;
    transform: translateY(0);
}
#${navbarId} .glass-nav-item.active,
#${navbarId} a.glass-nav-item.active {
    color: #54CCF5 !important;
    background: rgba(84, 204, 245, 0.12);
}
#${navbarId} .glass-nav-item.active::after {
    content: '';
    position: absolute;
    bottom: 0; left: 50%;
    transform: translateX(-50%);
    width: 60%; height: 3px;
    background: linear-gradient(90deg, transparent, #54CCF5, transparent);
    border-radius: 2px 2px 0 0;
}

#${navbarId} .glass-nav-item-icon-fa { font-size: 0.95em; flex-shrink: 0; }
#${navbarId} .glass-nav-item-icon    { width: 18px; height: 18px; flex-shrink: 0; }

#${navbarId} .glass-nav-item-badge {
    padding: 0.125rem 0.5rem;
    font-size: 0.75rem;
    font-weight: 600;
    border-radius: 12px;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
}
#${navbarId} .glass-nav-item-badge.primary { background: rgba(84,204,245,0.2);  color: #54CCF5; }
#${navbarId} .glass-nav-item-badge.success { background: rgba(34,197,94,0.2);   color: #4ade80; }
#${navbarId} .glass-nav-item-badge.warning { background: rgba(251,146,60,0.2);  color: #fb923c; }
#${navbarId} .glass-nav-item-badge.danger  { background: rgba(239,68,68,0.2);   color: #f87171; }
#${navbarId} .glass-nav-item-badge.info    { background: rgba(59,130,246,0.2);  color: #60a5fa; }

/* ══════════════════════════════════════════════════════════════════════
   BARE <li> ITEMS — plain <a>, <i>, text without glass-nav-item class
   White default → #54CCF5 on hover / focus / active
   ══════════════════════════════════════════════════════════════════════ */
#${navbarId} .glass-navbar-menu > li {
    display: flex;
    align-items: center;
    color: #ffffff !important;
    transition: color 0.3s ease;
}
#${navbarId} .glass-navbar-menu > li i,
#${navbarId} .glass-navbar-menu > li svg {
    color: #ffffff !important;
    fill: #ffffff;
    transition: color 0.3s ease, fill 0.3s ease;
}
#${navbarId} .glass-navbar-menu > li > a,
#${navbarId} .glass-navbar-menu > li > a:link,
#${navbarId} .glass-navbar-menu > li > a:visited {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    padding: 0.75rem 1rem;
    color: #ffffff !important;
    text-decoration: none !important;
    font-weight: 500;
    font-size: 0.9375rem;
    border-radius: 12px;
    transition: color 0.3s ease, background 0.3s ease, transform 0.3s ease;
    outline: none !important;
    white-space: nowrap;
}
#${navbarId} .glass-navbar-menu > li > a:hover,
#${navbarId} .glass-navbar-menu > li > a:focus {
    color: #54CCF5 !important;
    text-decoration: none !important;
    outline: none !important;
    background: rgba(84, 204, 245, 0.1);
    transform: translateY(-2px);
}
#${navbarId} .glass-navbar-menu > li > a:active {
    color: #54CCF5 !important;
    text-decoration: none !important;
    transform: translateY(0);
}
#${navbarId} .glass-navbar-menu > li > a i,
#${navbarId} .glass-navbar-menu > li > a svg {
    color: inherit !important;
    fill: currentColor;
    transition: color 0.3s ease, fill 0.3s ease;
}
#${navbarId} .glass-navbar-menu > li:hover,
#${navbarId} .glass-navbar-menu > li:focus-within {
    color: #54CCF5 !important;
}
#${navbarId} .glass-navbar-menu > li:hover i,
#${navbarId} .glass-navbar-menu > li:focus-within i,
#${navbarId} .glass-navbar-menu > li:hover svg,
#${navbarId} .glass-navbar-menu > li:focus-within svg {
    color: #54CCF5 !important;
    fill: #54CCF5;
}

/* ══════════════════════════════════════════════════════════════════════
   DROPDOWN TOGGLE — white default, #54CCF5 hover/focus
   ══════════════════════════════════════════════════════════════════════ */
#${navbarId} .glass-nav-dropdown { position: relative; }

#${navbarId} .glass-nav-dropdown-toggle {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: transparent;
    border: none;
    color: #ffffff !important;
    font-weight: 500;
    font-size: 0.9375rem;
    border-radius: 12px;
    cursor: pointer;
    transition: color 0.3s ease, background 0.3s ease;
    white-space: nowrap;
    outline: none !important;
}
#${navbarId} .glass-nav-dropdown-toggle:hover,
#${navbarId} .glass-nav-dropdown-toggle:focus {
    color: #54CCF5 !important;
    background: rgba(84, 204, 245, 0.1);
    outline: none !important;
}

#${navbarId} .glass-nav-dropdown-icon-fa { font-size: 0.95em; flex-shrink: 0; }
#${navbarId} .glass-nav-dropdown-icon    { width: 18px; height: 18px; flex-shrink: 0; }
#${navbarId} .glass-nav-dropdown-arrow   { width: 16px; height: 16px; transition: transform 0.3s ease; }
#${navbarId} .glass-nav-dropdown.active .glass-nav-dropdown-arrow { transform: rotate(180deg); }

#${navbarId} .glass-nav-dropdown-menu {
    position: absolute;
    top: calc(100% + 0.5rem); left: 0;
    min-width: 200px;
    background: rgba(10, 20, 40, 0.92);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(84, 204, 245, 0.2);
    border-radius: 12px;
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
    list-style: none;
    padding: 0.5rem;
    margin: 0;
    opacity: 0;
    visibility: hidden;
    transform: translateY(-10px);
    transition: all 0.3s ease;
    z-index: 1001;
}
#${navbarId} .glass-nav-dropdown.active .glass-nav-dropdown-menu {
    opacity: 1; visibility: visible; transform: translateY(0);
}

#${navbarId} .glass-nav-dropdown-item,
#${navbarId} a.glass-nav-dropdown-item,
#${navbarId} a.glass-nav-dropdown-item:link,
#${navbarId} a.glass-nav-dropdown-item:visited {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    color: #ffffff !important;
    text-decoration: none !important;
    border-radius: 8px;
    transition: color 0.3s ease, background 0.3s ease;
    font-size: 0.9375rem;
    outline: none !important;
}
#${navbarId} .glass-nav-dropdown-item:hover,
#${navbarId} .glass-nav-dropdown-item:focus,
#${navbarId} a.glass-nav-dropdown-item:hover,
#${navbarId} a.glass-nav-dropdown-item:focus {
    color: #54CCF5 !important;
    background: rgba(84, 204, 245, 0.1);
    text-decoration: none !important;
    outline: none !important;
}
#${navbarId} .glass-nav-dropdown-item:active,
#${navbarId} a.glass-nav-dropdown-item:active {
    color: #54CCF5 !important;
    text-decoration: none !important;
}

/* ── Responsive ───────────────────────────────────────────────────── */
@media (max-width: 768px) {
    #${navbarId} .glass-navbar-toggle { display: flex; }

    #${navbarId} .glass-navbar-content {
        position: fixed;
        top: ${height}; left: 0; right: 0;
        flex-direction: column;
        align-items: stretch;
        gap: 0;
        background: rgba(10, 20, 40, 0.96);
        backdrop-filter: blur(${blurAmount});
        -webkit-backdrop-filter: blur(${blurAmount});
        border: none;
        box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3);
        max-height: 0;
        overflow: hidden;
        transition: max-height 0.3s ease;
    }
    #${navbarId} .glass-navbar-content.active { max-height: calc(100vh - ${height}); }

    #${navbarId} .glass-navbar-menu { flex-direction: column; width: 100%; gap: 0; padding: 1rem 0; }

    #${navbarId} .glass-navbar-search-item { width: 100%; padding: 0.75rem 1.5rem; border-top: 1px solid rgba(255,255,255,0.1); }
    #${navbarId} .glass-navbar-search-item .glass-navbar-search-input       { width: 100%; }
    #${navbarId} .glass-navbar-search-item .glass-navbar-search-input:focus { width: 100%; }

    #${navbarId} .glass-nav-item,
    #${navbarId} a.glass-nav-item,
    #${navbarId} a.glass-nav-item:link,
    #${navbarId} a.glass-nav-item:visited { width: 100%; border-radius: 0; color: #ffffff !important; }
    #${navbarId} .glass-nav-item:hover,
    #${navbarId} .glass-nav-item:focus,
    #${navbarId} a.glass-nav-item:hover,
    #${navbarId} a.glass-nav-item:focus { transform: translateY(0); color: #54CCF5 !important; background: rgba(84,204,245,0.08); }
    #${navbarId} .glass-nav-item.active::after { left: 0; transform: none; width: 4px; height: 100%; top: 0; bottom: auto; background: #54CCF5; border-radius: 0 2px 2px 0; }

    #${navbarId} .glass-nav-dropdown-menu {
        position: static; opacity: 1; visibility: visible; transform: none;
        box-shadow: none; border: none; background: transparent;
        backdrop-filter: none; -webkit-backdrop-filter: none;
        max-height: 0; overflow: hidden; transition: max-height 0.3s ease;
    }
    #${navbarId} .glass-nav-dropdown.active .glass-nav-dropdown-menu { max-height: 500px; }
    #${navbarId} .glass-nav-dropdown-toggle { width: 100%; justify-content: space-between; border-radius: 0; }
}
</style>

    <div class="glass-navbar-container">
        ${(brand || brandImage || brandIcon) ? """
        <a href="${brandUrl}" class="glass-navbar-brand">
            ${brandIcon ? "<span class=\"glass-navbar-brand-icon\">${brandIcon}</span>" : ''}
            ${brandImage ? "<img src=\"${brandImage}\" alt=\"${brand}\" class=\"glass-navbar-brand-image\" />" : ''}
            ${brand ? "<span class=\"glass-navbar-brand-text\">${brand.encodeAsHTML()}</span>" : ''}
        </a>
        """ : ''}

        <button class="glass-navbar-toggle" onclick="document.getElementById('${navbarId}').querySelector('.glass-navbar-content').classList.toggle('active'); this.classList.toggle('active');">
            <span></span>
            <span></span>
            <span></span>
        </button>

        <div class="glass-navbar-content">
            <ul class="glass-navbar-menu">
                ${showSearch ? """
                <li class="glass-navbar-search-item">
                    <form action="${searchAction}" method="get" class="glass-navbar-search">
                        <div class="glass-navbar-search-wrapper">
                            <input type="text" name="q" id="${navbarId}-search-input"
                                   class="glass-navbar-search-input"
                                   placeholder="${searchPlaceholder.encodeAsHTML()}"
                                   autocomplete="off" />
                            <button type="button" class="glass-navbar-search-clear"
                                    id="${navbarId}-search-clear"
                                    aria-label="Clear search" tabindex="-1">&#x2715;</button>
                        </div>
                    </form>
                </li>
                """ : ''}
                ${body()}
            </ul>
        </div>
    </div>

    <script>
        (function() {
            function initNavbar() {
                var navbar = document.getElementById('${navbarId}');
                if (!navbar) return;

                // Fade navbar in on page load
                requestAnimationFrame(function() {
                    navbar.style.opacity = '1';
                });

                // Scroll: restore glass background after page scrolls
                window.addEventListener('scroll', function() {
                    if (window.scrollY > 50) {
                        navbar.classList.add('scrolled');
                    } else {
                        navbar.classList.remove('scrolled');
                    }
                });

                // Search clear button
                var searchInput = document.getElementById('${navbarId}-search-input');
                var searchClear = document.getElementById('${navbarId}-search-clear');
                if (searchInput && searchClear) {
                    var toggleClear = function() {
                        if (searchInput.value.length > 0) {
                            searchClear.classList.add('visible');
                        } else {
                            searchClear.classList.remove('visible');
                        }
                    };
                    searchInput.addEventListener('input', toggleClear);
                    searchInput.addEventListener('change', toggleClear);

                    // mousedown preventDefault keeps input focused so click fires reliably
                    searchClear.addEventListener('mousedown', function(e) { e.preventDefault(); });
                    searchClear.addEventListener('click', function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        searchInput.value = '';
                        // Dispatch input event so toggleClear hides the button
                        searchInput.dispatchEvent(new Event('input', { bubbles: true }));
                        searchInput.focus();
                    });
                }
            }

            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', initNavbar);
            } else {
                initNavbar();
            }
        })();
    </script>
</nav>
"""
  }

  /**
   * Renders a navigation menu item
   *
   * @attr href URL for the link (required)
   * @attr active Boolean indicating if this is the active page
   * @attr icon Icon HTML or SVG (legacy)
   * @attr faIcon Font Awesome class string e.g. "fa-solid fa-house"
   * @attr badge Badge text
   * @attr badgeColor Badge color variant (primary/success/warning/danger/info)
   * @attr target Link target
   * @attr cssClass Additional CSS classes
   * @attr controller Grails controller for auto active-state detection
   * @attr action Grails action for auto active-state detection
   */
  def navItem = { attrs, body ->

    String href = attrs.href ?: '#'
    String icon = attrs.icon ?: ''
    String faIcon = attrs.faIcon ?: ''
    String badge = attrs.badge ?: ''
    String badgeColor = attrs.badgeColor ?: 'primary'
    String target = attrs.target ?: ''
    String cssClass = attrs.cssClass ?: ''
    String controller = attrs.controller ?: ''
    String action = attrs.action ?: ''

    // FIX: GSP passes attrs as Strings
    boolean active = attrs.active == true || attrs.active == 'true'

    if (!active && (controller || action)) {
      def currentController = pageScope.variables?.controllerName ?: ''
      def currentAction = pageScope.variables?.actionName ?: ''
      active = (controller && controller == currentController) || (action && action == currentAction)
    }

    // faIcon takes priority over legacy icon
    def iconMarkup = ''
    if (faIcon) {
      iconMarkup = "<i class=\"${faIcon} glass-nav-item-icon-fa\"></i>"
    } else if (icon) {
      iconMarkup = "<span class=\"glass-nav-item-icon\">${icon}</span>"
    }

    out << """
<li>
    <a href="${href}" class="glass-nav-item ${active ? 'active' : ''} ${cssClass}" ${target ? "target=\"${target}\"" : ''}>
        ${iconMarkup}
        <span>${body()}</span>
        ${badge ? "<span class=\"glass-nav-item-badge ${badgeColor}\">${badge.encodeAsHTML()}</span>" : ''}
    </a>
</li>
"""
  }

  /**
   * Renders a dropdown menu in the navbar
   *
   * @attr label Dropdown trigger label (required)
   * @attr icon Icon HTML or SVG (legacy)
   * @attr faIcon Font Awesome class string e.g. "fa-solid fa-user"
   * @attr cssClass Additional CSS classes
   */
  def navDropdown = { attrs, body ->

    String label = attrs.label ?: 'Dropdown'
    String icon = attrs.icon ?: ''
    String faIcon = attrs.faIcon ?: ''
    String cssClass = attrs.cssClass ?: ''

    String dropdownId = "dropdown-${System.currentTimeMillis()}"

    def iconMarkup = ''
    if (faIcon) {
      iconMarkup = "<i class=\"${faIcon} glass-nav-dropdown-icon-fa\"></i>"
    } else if (icon) {
      iconMarkup = "<span class=\"glass-nav-dropdown-icon\">${icon}</span>"
    }

    out << """
<li class="glass-nav-dropdown ${cssClass}" id="${dropdownId}">
    <button class="glass-nav-dropdown-toggle" onclick="document.getElementById('${dropdownId}').classList.toggle('active'); event.stopPropagation();">
        ${iconMarkup}
        <span>${label.encodeAsHTML()}</span>
        <svg class="glass-nav-dropdown-arrow" viewBox="0 0 20 20" fill="currentColor">
            <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd"/>
        </svg>
    </button>
    <ul class="glass-nav-dropdown-menu">
        ${body()}
    </ul>
</li>

<script>
    document.addEventListener('click', function(event) {
        var dropdown = document.getElementById('${dropdownId}');
        if (dropdown && !dropdown.contains(event.target)) {
            dropdown.classList.remove('active');
        }
    });
</script>
"""
  }

  /**
   * Renders a dropdown menu item
   *
   * @attr href URL for the link (required)
   * @attr faIcon Font Awesome class string e.g. "fa-solid fa-gear"
   * @attr target Link target
   * @attr cssClass Additional CSS classes
   */
  def dropdownItem = { attrs, body ->

    String href = attrs.href ?: '#'
    String faIcon = attrs.faIcon ?: ''
    String target = attrs.target ?: ''
    String cssClass = attrs.cssClass ?: ''

    def iconMarkup = faIcon ? "<i class=\"${faIcon} glass-nav-dropdown-icon-fa\"></i>" : ''

    out << """
<li>
    <a href="${href}" class="glass-nav-dropdown-item ${cssClass}" ${target ? "target=\"${target}\"" : ''}>
        ${iconMarkup}
        ${body()}
    </a>
</li>
"""
  }
}
