package com.murad.app.matrimony.table
/**
 * GlassModalTable GSP Taglib
 * Glassmorphic data table — Mobile-first, cross-browser, GlassModal-compatible
 *
 * ─── QUICK-START ────────────────────────────────────────────────────────────────
 *
 *  1. Include assets (once, after Bootstrap CSS / GlassModal assets):
 *       <glass:mtableAssets/>
 *
 *  2. Basic table:
 *       <glass:mtable id="myTable" caption="Users" sortable="true" searchable="true"
 *                 variant="dark" striped="true" hover="true">
 *         <glass:mthead>
 *           <glass:mth field="name"  label="Name"   sortable="true"/>
 *           <glass:mth field="email" label="Email"  sortable="true"/>
 *           <glass:mth field="role"  label="Role"   align="center"/>
 *           <glass:mth label="Actions" align="center" sortable="false"/>
 *         </glass:mthead>
 *         <glass:mtbody>
 *           <g:each in="${users}" var="u">
 *             <glass:mtr>
 *               <glass:mtd>${u.name}</glass:mtd>
 *               <glass:mtd type="email">${u.email}</glass:mtd>
 *               <glass:mtd type="badge" badgeVariant="success">${u.role}</glass:mtd>
 *               <glass:mtd align="center">
 *                 <glass:mtableAction label="Edit"   icon="✎" variant="primary" onclick="editUser(${u.id})"/>
 *                 <glass:mtableAction label="Delete" icon="✕" variant="danger"  onclick="deleteUser(${u.id})"/>
 *               </glass:mtd>
 *             </glass:mtr>
 *           </g:each>
 *         </glass:mtbody>
 *       </glass:mtable>
 *
 *  3. Inside a GlassModal:
 *       <glass:modal id="usersModal" title="Users" icon="👥" size="lg">
 *         <glass:mtable id="modalTable" variant="modal" compact="true"
 *                   searchable="true" sortable="true" paginate="true" pageSize="5">
 *           ... (same thead/tbody pattern)
 *         </glass:mtable>
 *       </glass:modal>
 *
 * ─── TAG REFERENCE ──────────────────────────────────────────────────────────────
 *
 *  <glass:mtableAssets nonce="..."/>
 *    Injects CSS + JS. Place once after Bootstrap/GlassModal assets.
 *
 *  <glass:mtable>  — wrapper
 *    id          (opt)  HTML id; auto-generated if omitted
 *    caption     (opt)  Visible table caption / toolbar title
 *    variant     (opt)  dark (def) | light | modal | minimal
 *    size        (opt)  sm | md (def) | lg
 *    striped     (opt)  true | false (def)
 *    hover       (opt)  true (def) | false
 *    sortable    (opt)  true | false (def)  — enables client-side column sort
 *    searchable  (opt)  true | false (def)  — live search filter
 *    paginate    (opt)  true | false (def)  — client-side pagination
 *    pageSize    (opt)  integer (def 10)
 *    compact     (opt)  true | false (def)  — tighter padding (ideal inside modals)
 *    responsive  (opt)  true (def) | false  — card-stack on mobile
 *    stickyHead  (opt)  true | false (def)  — freeze <thead> on scroll
 *    exportCsv   (opt)  true | false (def)  — show CSV export button
 *    emptyText   (opt)  Message when no rows (def: "No records found")
 *    emptyIcon   (opt)  Emoji for empty state (def: ⊘)
 *    showEmpty   (opt)  true (def) | false  — set false to completely suppress
 *                       the empty-state row (no message shown when table is empty)
 *
 *  <glass:mthead>  — wraps <glass:mth> tags; no extra attrs
 *
 *  <glass:mth>
 *    field       (opt)  data-field key used for sorting/search; defaults to label
 *    label       (req)  Column header text
 *    sortable    (opt)  true (def if parent table sortable) | false
 *    align       (opt)  left (def) | center | right
 *    width       (opt)  CSS width e.g. "120px", "20%"
 *    tooltip     (opt)  Title tooltip on the header
 *
 *  <glass:mtbody>  — wraps <glass:mtr> tags; no extra attrs
 *
 *  <glass:mtr>
 *    highlight   (opt)  true | false — accent-glow row
 *    extraClass  (opt)  Additional CSS classes
 *
 *  <glass:mtd>
 *    type        (opt)  text (def) | number | currency | email | date |
 *                       badge | status | code | avatar
 *    align       (opt)  left (def) | center | right
 *    badgeVariant(opt)  default | success | danger | warning | info  (type=badge/status)
 *    avatarSrc   (opt)  Image URL  (type=avatar)
 *    nowrap      (opt)  true | false
 *    colspan     (opt)  integer
 *    title       (opt)  Tooltip
 *    extraClass  (opt)  Additional CSS classes
 *
 *  <glass:mtableAction>  — icon+label action button, ideal inside <glass:mtd>
 *    label       (opt)  Accessible label (also tooltip)
 *    icon        (opt)  Emoji / character
 *    variant     (opt)  ghost (def) | primary | success | danger | warning
 *    onclick     (opt)  Inline JS
 *    href        (opt)  Renders as <a> instead of <button>
 *    extraClass  (opt)  Additional CSS classes
 *    disabled    (opt)  true | false
 *
 * ─── KEYBOARD SUPPORT ────────────────────────────────────────────────────────────
 *
 *  SCROLL REGION  (outer Tab stop)
 *    Tab / Shift-Tab  Navigate into / out of the table
 *    ↓  or  Enter     Move focus to the first visible body row
 *    ↑                Move focus to the last visible body row
 *    / or Ctrl+F      Focus the search input  (if searchable="true")
 *    ← / →            Previous / next page    (if paginate="true")
 *    PageUp/PageDown  Previous / next page    (if paginate="true")
 *
 *  SORTABLE HEADER CELLS
 *    ← / →            Move between sortable headers
 *    Enter / Space    Trigger sort on the focused column
 *    ↓                Move focus into the first body row
 *    Escape           Return focus to the scroll region
 *
 *  BODY ROWS
 *    ↑ / ↓            Previous / next visible row
 *    Home             First visible row on current page
 *    End              Last visible row on current page
 *    PageUp           Previous page, focus first row  (if paginate="true")
 *    PageDown         Next page, focus first row       (if paginate="true")
 *    ← / →            Previous / next page (when focus is on the TR itself)
 *    Enter            Activate the first action button/link in the row
 *    Space            Announce row content to screen readers
 *    Tab              Move focus into action buttons within the row
 *    Escape           Return focus to the scroll region
 *
 *  ACTION BUTTONS (inside rows)
 *    Tab / Shift-Tab  Cycle between action buttons within the row
 *    Enter / Space    Activate the button (native behaviour)
 *    Escape           Return focus to the parent row TR
 *
 *  SEARCH INPUT
 *    Enter            Commit search; focus first visible body row
 *    Escape           Clear search; return focus to scroll region
 *
 * ─────────────────────────────────────────────────────────────────────────────────
 */
class GlassModalTableTagLib {

  static namespace = 'glass'
  static defaultEncodeAs = [taglib: 'none']

  // ═══════════════════════════════════════════════════════════════════════════
  // ASSETS
  // ═══════════════════════════════════════════════════════════════════════════

  def mtableAssets = { attrs ->
    def nonce = attrs.nonce ? " nonce=\"${attrs.nonce}\"" : ''
    out << $/
<style${nonce}>
/* ── GlassModalTable System ──────────────────────────────────────────────────── */

:root {
  --gt-blur:           var(--gm-blur,          18px);
  --gt-blur-sm:        var(--gm-blur-sm,       10px);
  --gt-background-dark:        var(--gm-background-dark,       rgba(10, 10, 20, 0.55));
  --gt-background-light:       var(--gm-background-light,      rgba(255, 255, 255, 0.20));
  --gt-border-dark:    var(--gm-border-dark,   rgba(255, 255, 255, 0.14));
  --gt-border-light:   var(--gm-border-light,  rgba(255, 255, 255, 0.55));
  --gt-text-dark:      var(--gm-text-dark,     #e8eaf6);
  --gt-text-light:     var(--gm-text-light,    #1a1a2e);
  --gt-accent:         var(--gm-accent,        #7c83ff);
  --gt-accent-glow:    var(--gm-accent-glow,   rgba(124, 131, 255, 0.45));
  --gt-success:        var(--gm-success,       #4ecca3);
  --gt-danger:         var(--gm-danger,        #ff6b8a);
  --gt-warning:        var(--gm-warning,       #ffd166);
  --gt-info:                                   #60b8ff;
  --gt-radius:         var(--gm-radius,        20px);
  --gt-radius-sm:      var(--gm-radius-sm,     12px);
  --gt-radius-xs:                              6px;
  --gt-shadow:         var(--gm-shadow,        0 8px 40px rgba(0,0,0,0.45), 0 2px 8px rgba(0,0,0,0.25));
  --gt-ease:           var(--gm-ease,          cubic-bezier(0.4, 0, 0.2, 1));
  --gt-font:           var(--gm-font-body,     'DM Sans', 'Segoe UI', system-ui, sans-serif);
  --gt-font-mono:                              'JetBrains Mono', 'Fira Code', 'Cascadia Code', monospace;
  --gt-transition:     var(--gm-transition,    cubic-bezier(0.34, 1.56, 0.64, 1));
  --gt-cell-pad-y:     14px;
  --gt-cell-pad-x:     18px;
  --gt-cell-pad-y-sm:  9px;
  --gt-cell-pad-x-sm:  12px;
  --gt-stripe-dark:    rgba(124, 131, 255, 0.04);
  --gt-stripe-light:   rgba(26,  26,  46,  0.05);
  --gt-hover-dark:     rgba(124, 131, 255, 0.09);
  --gt-hover-light:    rgba(124, 131, 255, 0.07);
  --gt-head-dark:      rgba(124, 131, 255, 0.07);
  --gt-head-light:     rgba(26,  26,  46,  0.06);
  --gt-divider-dark:   rgba(124, 131, 255, 0.10);
  --gt-divider-light:  rgba(26,  26,  46,  0.10);
}

/* ═══════════════════════════════════════════════════════════════════════════
   NUCLEAR BORDER RESET — defeats browser defaults AND Bootstrap 5 table
   borders in one block. Everything that follows only adds back what we want
   via box-shadow (inset divider lines).
   ═══════════════════════════════════════════════════════════════════════════ */
.gt-wrapper table,
.gt-wrapper table > thead,
.gt-wrapper table > tbody,
.gt-wrapper table > tfoot,
.gt-wrapper table > thead > tr,
.gt-wrapper table > tbody > tr,
.gt-wrapper table > tfoot > tr,
.gt-wrapper table > thead > tr > th,
.gt-wrapper table > thead > tr > td,
.gt-wrapper table > tbody > tr > th,
.gt-wrapper table > tbody > tr > td,
.gt-wrapper table > tfoot > tr > th,
.gt-wrapper table > tfoot > tr > td {
  border: 0 !important;
  border-top: 0 !important;
  border-right: 0 !important;
  border-bottom: 0 !important;
  border-left: 0 !important;
  border-color: transparent !important;
  border-style: none !important;
  border-width: 0 !important;
  outline: 0 !important;
  box-shadow: none !important;
  /* Zero out every Bootstrap 5 table CSS custom property */
  --bs-table-background:                   transparent;
  --bs-table-accent-background:            transparent;
  --bs-table-striped-background:           transparent;
  --bs-table-striped-color:        inherit;
  --bs-table-active-background:            transparent;
  --bs-table-active-color:         inherit;
  --bs-table-hover-background:             transparent;
  --bs-table-hover-color:          inherit;
  --bs-table-color:                inherit;
  --bs-table-border-color:         transparent;
  --bs-table-background-state:             transparent;
  --bs-table-background-type:              transparent;
  --bs-table-color-state:          inherit;
  --bs-table-color-type:           inherit;
  --bs-border-color:               transparent;
  --bs-border-color-translucent:   transparent;
}

/* Bootstrap 5.3 uses `> :not(caption) > * > *` with border-width: 1px —
   override it here with max specificity so it can never bleed through. */
.gt-wrapper .gt-table > :not(caption) > * {
  border-width: 0 !important;
  background-color: transparent !important;
  --bs-table-color-state: inherit;
  --bs-table-background-state: transparent;
}
.gt-wrapper .gt-table > :not(caption) > * > * {
  border-width: 0 !important;
  border-color: transparent !important;
  background-color: transparent !important;
  color: inherit !important;
  box-shadow: none !important;
}

/* ── Wrapper — nuke all Bootstrap table CSS vars ──────────────────────────── */
.gt-wrapper {
  font-family: var(--gt-font);
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  color: var(--gt-text-dark);
  --bs-table-background:                   transparent;
  --bs-table-accent-background:            transparent;
  --bs-table-striped-background:           transparent;
  --bs-table-striped-color:        var(--gt-text-dark);
  --bs-table-active-background:            transparent;
  --bs-table-active-color:         var(--gt-text-dark);
  --bs-table-hover-background:             transparent;
  --bs-table-hover-color:          var(--gt-text-dark);
  --bs-table-color:                var(--gt-text-dark);
  --bs-table-border-color:         transparent;
  --bs-table-background-state:             transparent;
  --bs-table-background-type:              transparent;
  --bs-table-color-state:          var(--gt-text-dark);
  --bs-table-color-type:           var(--gt-text-dark);
  --bs-border-color:               transparent;
  --bs-border-color-translucent:   transparent;
  --bs-link-color:                 var(--gt-accent);
  --bs-link-hover-color:           var(--gt-accent);
  --bs-emphasis-color:             var(--gt-text-dark);
}
.gt-wrapper-light {
  color: var(--gt-text-light);
  --bs-table-striped-color:  var(--gt-text-light);
  --bs-table-active-color:   var(--gt-text-light);
  --bs-table-hover-color:    var(--gt-text-light);
  --bs-table-color:          var(--gt-text-light);
  --bs-table-color-state:    var(--gt-text-light);
  --bs-table-color-type:     var(--gt-text-light);
  --bs-emphasis-color:       var(--gt-text-light);
}

/* ── Toolbar ──────────────────────────────────────────────────────────────── */
.gt-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
}
.gt-caption {
  font-size: clamp(0.9rem, 2.2vw, 1.05rem);
  font-weight: 700;
  letter-spacing: -0.02em;
  opacity: 0.9;
  flex: 1 1 auto;
}
.gt-toolbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

/* Search */
.gt-search-wrap { position: relative; display: flex; align-items: center; }
.gt-search-icon {
  position: absolute; left: 10px;
  font-size: 13px; opacity: 0.5; pointer-events: none; line-height: 1;
}
.gt-search {
  padding: 7px 12px 7px 30px;
  border-radius: var(--gt-radius-xs);
  border: 1px solid rgba(124, 131, 255, 0.22);
  background: rgba(124, 131, 255, 0.08);
  color: var(--gt-text-dark);
  font-family: var(--gt-font);
  font-size: 0.8125rem;
  width: clamp(150px, 22vw, 240px);
  transition: border-color 200ms var(--gt-ease), background 200ms var(--gt-ease);
  outline: none;
  -webkit-tap-highlight-color: transparent;
}
.gt-search::placeholder { opacity: 0.45; color: var(--gt-text-dark); }
.gt-search:focus {
  border-color: var(--gt-accent);
  background: rgba(124, 131, 255, 0.13);
  box-shadow: 0 0 0 3px rgba(124, 131, 255, 0.18);
}
.gt-wrapper-light .gt-search {
  border-color: rgba(26, 26, 46, 0.18);
  background: rgba(26, 26, 46, 0.06);
  color: var(--gt-text-light);
}
.gt-wrapper-light .gt-search::placeholder { color: var(--gt-text-light); }
.gt-wrapper-light .gt-search:focus {
  border-color: var(--gt-accent);
  background: rgba(26, 26, 46, 0.10);
}

/* Export button */
.gt-export-btn {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 7px 13px;
  border-radius: var(--gt-radius-xs);
  border: 1px solid rgba(124, 131, 255, 0.22);
  background: rgba(124, 131, 255, 0.08);
  color: var(--gt-text-dark);
  font-family: var(--gt-font); font-size: 0.8125rem; font-weight: 600;
  cursor: pointer;
  transition: background 200ms var(--gt-ease);
  white-space: nowrap;
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}
.gt-export-btn:hover { background: rgba(124, 131, 255, 0.16); }
.gt-export-btn:active { transform: scale(0.97); }
.gt-wrapper-light .gt-export-btn {
  border-color: rgba(26, 26, 46, 0.18);
  background: rgba(26, 26, 46, 0.06);
  color: var(--gt-text-light);
}
.gt-wrapper-light .gt-export-btn:hover { background: rgba(26, 26, 46, 0.12); }

/* ── Scroll container ─────────────────────────────────────────────────────── */
.gt-scroll {
  width: 100%; position: relative;
  overflow-x: auto; -webkit-overflow-scrolling: touch;
  border-radius: var(--gt-radius);
  box-shadow: var(--gt-shadow);
  scrollbar-width: thin;
  scrollbar-color: rgba(124, 131, 255, 0.25) transparent;
}
.gt-scroll::-webkit-scrollbar { height: 5px; }
.gt-scroll::-webkit-scrollbar-thumb { background: rgba(124, 131, 255, 0.25); border-radius: 99px; }
.gt-wrapper-modal .gt-scroll { box-shadow: none; border-radius: var(--gt-radius-sm); }
.gt-wrapper-minimal .gt-scroll { box-shadow: 0 2px 12px rgba(0,0,0,0.28); }

/* ── Table element ────────────────────────────────────────────────────────── */
.gt-table {
  width: 100%;
  border-collapse: collapse !important;
  border-spacing: 0 !important;
  font-size: clamp(0.8rem, 1.8vw, 0.875rem);
  line-height: 1.5;
  min-width: 400px;
  border: 0 !important;
  outline: 0 !important;
  box-shadow: none !important;
  color: inherit;
  background: transparent !important;
  background-color: transparent !important;
}

/* ── Full Bootstrap override — all structural elements ───────────────────── */
.gt-wrapper .gt-scroll,
.gt-wrapper .gt-table,
.gt-wrapper .gt-table thead,
.gt-wrapper .gt-table tbody,
.gt-wrapper .gt-table tfoot,
.gt-wrapper .gt-table tr,
.gt-wrapper .gt-table th,
.gt-wrapper .gt-table td {
  background-color: transparent !important;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  outline: 0 !important;
  box-shadow: none !important;
  --bs-table-background: transparent;
  --bs-table-accent-background: transparent;
  --bs-table-striped-background: transparent;
  --bs-table-active-background: transparent;
  --bs-table-hover-background: transparent;
  --bs-table-background-state: transparent;
  --bs-table-background-type: transparent;
  --bs-table-color-state: inherit;
  --bs-table-color-type: inherit;
  --bs-border-color: transparent;
  --bs-border-color-translucent: transparent;
}

/* Hover resets */
.gt-wrapper .gt-table thead tr:hover,
.gt-wrapper .gt-table thead tr:hover > th,
.gt-wrapper .gt-table thead th:hover,
.gt-wrapper .gt-table tbody tr:hover,
.gt-wrapper .gt-table tbody tr:hover > td,
.gt-wrapper .gt-table tbody tr:hover > th,
.gt-wrapper .gt-table tfoot tr:hover,
.gt-wrapper .gt-table tfoot tr:hover > td {
  --bs-table-hover-background: transparent;
  --bs-table-background-state: transparent;
  --bs-table-color-state: inherit;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
}

.gt-wrapper .gt-table th:hover,
.gt-wrapper .gt-table td:hover,
.gt-wrapper .gt-table td.gt-type-text:hover,
.gt-wrapper .gt-table td.gt-type-number:hover,
.gt-wrapper .gt-table td.gt-type-currency:hover,
.gt-wrapper .gt-table td.gt-type-email:hover,
.gt-wrapper .gt-table td.gt-type-date:hover,
.gt-wrapper .gt-table td.gt-type-badge:hover,
.gt-wrapper .gt-table td.gt-type-status:hover,
.gt-wrapper .gt-table td.gt-type-code:hover,
.gt-wrapper .gt-table td.gt-type-avatar:hover {
  background-color: transparent !important;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
}

.gt-wrapper .gt-table thead,
.gt-wrapper .gt-table tbody,
.gt-wrapper .gt-table tfoot,
.gt-wrapper .gt-table tr,
.gt-wrapper .gt-table th,
.gt-wrapper .gt-table td { color: inherit; }

/* Cell-level var zero (static + hover) */
.gt-wrapper .gt-table td,
.gt-wrapper .gt-table td.gt-type-text,
.gt-wrapper .gt-table td.gt-type-number,
.gt-wrapper .gt-table td.gt-type-currency,
.gt-wrapper .gt-table td.gt-type-email,
.gt-wrapper .gt-table td.gt-type-date,
.gt-wrapper .gt-table td.gt-type-badge,
.gt-wrapper .gt-table td.gt-type-status,
.gt-wrapper .gt-table td.gt-type-code,
.gt-wrapper .gt-table td.gt-type-avatar,
.gt-wrapper .gt-table td:hover,
.gt-wrapper .gt-table td.gt-type-text:hover,
.gt-wrapper .gt-table td.gt-type-number:hover,
.gt-wrapper .gt-table td.gt-type-currency:hover,
.gt-wrapper .gt-table td.gt-type-email:hover,
.gt-wrapper .gt-table td.gt-type-date:hover,
.gt-wrapper .gt-table td.gt-type-badge:hover,
.gt-wrapper .gt-table td.gt-type-status:hover,
.gt-wrapper .gt-table td.gt-type-code:hover,
.gt-wrapper .gt-table td.gt-type-avatar:hover {
  background-color: transparent !important;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  --bs-table-background: transparent;
  --bs-table-accent-background: transparent;
  --bs-table-striped-background: transparent;
  --bs-table-active-background: transparent;
  --bs-table-hover-background: transparent;
  --bs-table-background-state: transparent;
  --bs-table-background-type: transparent;
  --bs-table-color-state: inherit;
  --bs-table-color-type: inherit;
}

/* Modal variant */
.gt-wrapper-modal,
.gt-wrapper-modal .gt-scroll,
.gt-wrapper-modal .gt-table,
.gt-wrapper-modal .gt-table thead,
.gt-wrapper-modal .gt-table tbody,
.gt-wrapper-modal .gt-table tfoot,
.gt-wrapper-modal .gt-table tr,
.gt-wrapper-modal .gt-table th,
.gt-wrapper-modal .gt-table td,
.gt-wrapper-modal .gt-table thead th:hover,
.gt-wrapper-modal .gt-table tbody tr:hover > td,
.gt-wrapper-modal .gt-table tbody tr:hover > th {
  background-color: transparent !important;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  outline: 0 !important;
  box-shadow: none !important;
}
.gt-wrapper-modal { border: 0 !important; box-shadow: none !important; }

/* ── Noise grain overlay ──────────────────────────────────────────────────── */
.gt-scroll::before {
  content: ''; position: absolute; inset: 0; border-radius: inherit;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='200' height='200' filter='url(%23n)' opacity='0.03'/%3E%3C/svg%3E");
  pointer-events: none; z-index: 0;
}

/* Accent top strip */
.gt-scroll::after {
  content: ''; position: absolute;
  top: 0; left: 10%; right: 10%; height: 1px;
  background: linear-gradient(90deg, transparent, var(--gt-accent), transparent);
  opacity: 0.6; pointer-events: none; z-index: 2; border-radius: 1px;
}
.gt-wrapper-modal .gt-scroll::after,
.gt-wrapper-minimal .gt-scroll::after { display: none; }

/* ── Theme backgrounds ────────────────────────────────────────────────────── */
.gt-wrapper-dark .gt-scroll {
  background: var(--gt-background-dark) !important;
  border: 1px solid var(--gt-border-dark) !important;
  color: var(--gt-text-dark);
  -webkit-backdrop-filter: blur(var(--gt-blur)) saturate(1.6) brightness(1.05);
          backdrop-filter: blur(var(--gt-blur)) saturate(1.6) brightness(1.05);
}
.gt-wrapper-light .gt-scroll {
  background: var(--gt-background-light) !important;
  border: 1px solid var(--gt-border-light) !important;
  color: var(--gt-text-light);
  -webkit-backdrop-filter: blur(var(--gt-blur)) saturate(1.8) brightness(1.15);
          backdrop-filter: blur(var(--gt-blur)) saturate(1.8) brightness(1.15);
}
.gt-wrapper-light .gt-scroll::-webkit-scrollbar-thumb { background: rgba(26,26,46,0.20); }
.gt-wrapper-light .gt-scroll { scrollbar-color: rgba(26,26,46,0.20) transparent; }
.gt-wrapper-modal .gt-scroll {
  background: transparent !important;
  border: 0 !important;
  color: inherit;
  -webkit-backdrop-filter: none; backdrop-filter: none;
}
.gt-wrapper-modal .gt-scroll::before { display: none; }
.gt-wrapper-minimal .gt-scroll {
  background: rgba(10, 10, 20, 0.48) !important;
  border: 1px solid rgba(124, 131, 255, 0.14) !important;
  color: var(--gt-text-dark);
  -webkit-backdrop-filter: blur(10px); backdrop-filter: blur(10px);
}

/* ── thead ────────────────────────────────────────────────────────────────── */
.gt-table thead tr {
  position: relative;
  background: transparent !important;
  background-color: transparent !important;
}
.gt-table th {
  padding: var(--gt-cell-pad-y) var(--gt-cell-pad-x);
  text-align: left; font-size: 0.72rem; font-weight: 700;
  letter-spacing: 0.08em; text-transform: uppercase; white-space: nowrap;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  /* Inset box-shadow provides the bottom divider line without any real border */
  box-shadow: inset 0 -1px 0 var(--gt-divider-dark) !important;
  background: var(--gt-head-dark) !important;
  position: relative; user-select: none; -webkit-user-select: none; color: inherit;
}
.gt-wrapper-light .gt-table th {
  background: var(--gt-head-light) !important;
  box-shadow: inset 0 -1px 0 var(--gt-divider-light) !important;
}
.gt-wrapper-modal .gt-table th {
  background: rgba(124, 131, 255, 0.05) !important;
  box-shadow: inset 0 -1px 0 rgba(124, 131, 255, 0.10) !important;
}
.gt-wrapper-compact .gt-table th,
.gt-wrapper-compact .gt-table td { padding: var(--gt-cell-pad-y-sm) var(--gt-cell-pad-x-sm); }
.gt-wrapper-sticky .gt-table th {
  position: sticky; top: 0; z-index: 10;
  -webkit-backdrop-filter: blur(16px) saturate(1.5);
          backdrop-filter: blur(16px) saturate(1.5);
}

/* Sort indicators */
.gt-table th[data-sortable="true"] { cursor: pointer; }
.gt-table th[data-sortable="true"]:hover {
  color: var(--gt-accent);
  background-color: transparent !important;
  background-image: none !important;
}
.gt-sort-icon {
  display: inline-flex; flex-direction: column; gap: 1px;
  margin-left: 5px; vertical-align: middle;
  opacity: 0.35; font-size: 8px; line-height: 1;
  transition: opacity 150ms;
}
th[data-sortable="true"]:hover .gt-sort-icon { opacity: 0.7; }
th[data-sort="asc"]  .gt-sort-icon,
th[data-sort="desc"] .gt-sort-icon { opacity: 1; color: var(--gt-accent); }
th[data-sort="asc"]  .gt-sort-icon-up   { color: var(--gt-accent); }
th[data-sort="desc"] .gt-sort-icon-down { color: var(--gt-accent); }

/* ── tbody ────────────────────────────────────────────────────────────────── */
.gt-table tbody { background: transparent !important; }
.gt-table td {
  padding: var(--gt-cell-pad-y) var(--gt-cell-pad-x);
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  /* Inset box-shadow provides the bottom divider line without any real border */
  box-shadow: inset 0 -1px 0 var(--gt-divider-dark) !important;
  vertical-align: middle;
  transition: background 150ms var(--gt-ease);
  background: transparent !important; color: inherit;
}
.gt-wrapper-light .gt-table td { box-shadow: inset 0 -1px 0 var(--gt-divider-light) !important; }
.gt-table tbody tr:last-child td { box-shadow: none !important; }

/* Striped */
.gt-wrapper-striped .gt-table tbody tr:nth-child(even) {
  background: var(--gt-stripe-dark) !important;
}
.gt-wrapper-light.gt-wrapper-striped .gt-table tbody tr:nth-child(even) {
  background: var(--gt-stripe-light) !important;
}
.gt-wrapper-striped .gt-table tbody tr:nth-child(even) td {
  background: transparent !important;
}

/* Row hover */
.gt-wrapper-hover .gt-table tbody tr:hover {
  background: var(--gt-hover-dark) !important;
  cursor: default;
}
.gt-wrapper-light.gt-wrapper-hover .gt-table tbody tr:hover {
  background: var(--gt-hover-light) !important;
}
.gt-wrapper-hover .gt-table tbody tr:hover > td,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-text,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-number,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-currency,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-email,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-date,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-badge,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-status,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-code,
.gt-wrapper-hover .gt-table tbody tr:hover > td.gt-type-avatar {
  background: transparent !important;
  background-color: transparent !important;
  background-image: none !important;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  --bs-table-hover-background: transparent;
  --bs-table-background-state: transparent;
  --bs-table-color-state: inherit;
}

/* thead th hover background restoration */
.gt-wrapper .gt-table thead th:hover {
  background: var(--gt-head-dark) !important;
}
.gt-wrapper-light .gt-table thead th:hover { background: var(--gt-head-light) !important; }
.gt-wrapper-modal .gt-table thead th:hover { background: rgba(124, 131, 255, 0.05) !important; }

/* Highlighted row */
.gt-table tbody tr.gt-row-highlight {
  background: rgba(124, 131, 255, 0.09) !important;
  box-shadow: inset 3px 0 0 var(--gt-accent) !important;
}
.gt-table tbody tr.gt-row-highlight td {
  background: transparent !important;
  box-shadow: inset 0 -1px 0 var(--gt-divider-dark) !important;
}
.gt-table tbody tr.gt-row-highlight:last-child td { box-shadow: none !important; }

/* Hidden rows */
.gt-table tbody tr[hidden] { display: none; }

/* ── Alignment ────────────────────────────────────────────────────────────── */
.gt-align-left   { text-align: left; }
.gt-align-center { text-align: center; }
.gt-align-right  { text-align: right; }

/* ── Cell types ───────────────────────────────────────────────────────────── */
.gt-type-number, .gt-type-currency {
  font-family: var(--gt-font-mono);
  font-variant-numeric: tabular-nums;
  text-align: right; letter-spacing: -0.01em;
}
.gt-type-email a {
  color: var(--gt-accent); text-decoration: none;
  font-size: 0.85em; opacity: 0.85;
  transition: opacity 150ms;
}
.gt-type-email a:hover { opacity: 1; text-decoration: underline; }
.gt-type-date {
  font-family: var(--gt-font-mono); font-size: 0.8125rem;
  opacity: 0.75; white-space: nowrap;
}
.gt-type-code code {
  font-family: var(--gt-font-mono); font-size: 0.8em;
  padding: 2px 6px; border-radius: 5px;
  background: rgba(124, 131, 255, 0.12) !important;
  border: 1px solid rgba(124, 131, 255, 0.22);
  color: var(--gt-accent); letter-spacing: 0.01em; white-space: nowrap;
}
.gt-avatar-cell { display: flex; align-items: center; gap: 10px; }
.gt-avatar {
  width: 32px; height: 32px; border-radius: 50%; object-fit: cover;
  border: 2px solid rgba(124, 131, 255, 0.35); flex-shrink: 0;
  background: rgba(124, 131, 255, 0.15) !important;
}
.gt-avatar-placeholder {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, rgba(124,131,255,0.4), rgba(78,204,163,0.3)) !important;
  border: 2px solid rgba(124, 131, 255, 0.30);
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700; flex-shrink: 0; text-transform: uppercase;
  color: var(--gt-text-dark);
}

/* ── Badges ───────────────────────────────────────────────────────────────── */
.gt-badge {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 3px 10px; border-radius: 99px;
  font-size: 0.72rem; font-weight: 700;
  letter-spacing: 0.04em; text-transform: uppercase;
  white-space: nowrap; border: 1px solid transparent;
}
.gt-badge-default  { background: rgba(124,131,255,0.10) !important; border-color: rgba(124,131,255,0.20); color: rgba(232,234,246,0.80); }
.gt-badge-success  { background: rgba(78,204,163,0.15)  !important; border-color: rgba(78,204,163,0.35);  color: var(--gt-success); }
.gt-badge-danger   { background: rgba(255,107,138,0.15) !important; border-color: rgba(255,107,138,0.35); color: var(--gt-danger); }
.gt-badge-warning  { background: rgba(255,209,102,0.15) !important; border-color: rgba(255,209,102,0.35); color: var(--gt-warning); }
.gt-badge-info     { background: rgba(96,184,255,0.15)  !important; border-color: rgba(96,184,255,0.35);  color: var(--gt-info); }
.gt-status-dot { width: 7px; height: 7px; border-radius: 50%; display: inline-block; flex-shrink: 0; }
.gt-badge-success .gt-status-dot { background: var(--gt-success); box-shadow: 0 0 6px rgba(78,204,163,0.6); }
.gt-badge-danger  .gt-status-dot { background: var(--gt-danger);  box-shadow: 0 0 6px rgba(255,107,138,0.6); }
.gt-badge-warning .gt-status-dot { background: var(--gt-warning); box-shadow: 0 0 6px rgba(255,209,102,0.6); }
.gt-badge-info    .gt-status-dot { background: var(--gt-info);    box-shadow: 0 0 6px rgba(96,184,255,0.6); }
.gt-badge-default .gt-status-dot { background: rgba(124,131,255,0.5); }

/* ── Action buttons ───────────────────────────────────────────────────────── */
.gt-actions { display: inline-flex; align-items: center; gap: 5px; flex-wrap: wrap; justify-content: center; }
.gt-action-btn {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 5px 10px; border-radius: var(--gt-radius-xs);
  border: 1px solid transparent;
  font-family: var(--gt-font); font-size: 0.75rem; font-weight: 600;
  cursor: pointer;
  transition: background 180ms var(--gt-ease), box-shadow 180ms var(--gt-ease), transform 140ms;
  white-space: nowrap; text-decoration: none;
  -webkit-tap-highlight-color: transparent; touch-action: manipulation;
}
.gt-action-btn:focus-visible { outline: 2px solid var(--gt-accent); outline-offset: 2px; }
.gt-action-btn:active { transform: scale(0.95); }
.gt-action-btn:disabled, .gt-action-btn[disabled] { opacity: 0.4; pointer-events: none; }
.gt-action-ghost    { background: rgba(124,131,255,0.08) !important; border-color: rgba(124,131,255,0.18); color: var(--gt-text-dark); }
.gt-action-ghost:hover { background: rgba(124,131,255,0.16) !important; }
.gt-action-primary  { background: rgba(124,131,255,0.18) !important; border-color: rgba(124,131,255,0.35); color: var(--gt-accent); }
.gt-action-primary:hover  { background: rgba(124,131,255,0.28) !important; box-shadow: 0 3px 14px rgba(124,131,255,0.25); }
.gt-action-success  { background: rgba(78,204,163,0.15)  !important; border-color: rgba(78,204,163,0.30);  color: var(--gt-success); }
.gt-action-success:hover  { background: rgba(78,204,163,0.25)  !important; box-shadow: 0 3px 14px rgba(78,204,163,0.20); }
.gt-action-danger   { background: rgba(255,107,138,0.15) !important; border-color: rgba(255,107,138,0.30); color: var(--gt-danger); }
.gt-action-danger:hover   { background: rgba(255,107,138,0.25) !important; box-shadow: 0 3px 14px rgba(255,107,138,0.20); }
.gt-action-warning  { background: rgba(255,209,102,0.15) !important; border-color: rgba(255,209,102,0.30); color: var(--gt-warning); }
.gt-action-warning:hover  { background: rgba(255,209,102,0.25) !important; box-shadow: 0 3px 14px rgba(255,209,102,0.15); }
.gt-action-icon { font-style: normal; font-size: 0.8em; }

/* ── Empty state ──────────────────────────────────────────────────────────── */
.gt-empty td {
  text-align: center; padding: 40px 20px;
  opacity: 0.45; font-size: 0.875rem; letter-spacing: 0.02em;
  border: 0 !important;
  border-color: transparent !important;
  border-width: 0 !important;
  box-shadow: none !important;
  background: transparent !important;
}
.gt-empty-icon { display: block; font-size: 2rem; margin-bottom: 8px; opacity: 0.6; }

/* ── Pagination ───────────────────────────────────────────────────────────── */
.gt-pagination {
  display: flex; align-items: center;
  justify-content: space-between; flex-wrap: wrap;
  gap: 8px; font-size: 0.8125rem;
}
.gt-page-info { opacity: 0.55; white-space: nowrap; }
.gt-page-btns { display: flex; gap: 4px; align-items: center; flex-wrap: wrap; }
.gt-page-btn {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 32px; height: 32px; padding: 0 8px;
  border-radius: var(--gt-radius-xs);
  border: 1px solid rgba(124,131,255,0.18);
  background: rgba(124,131,255,0.07) !important;
  color: var(--gt-text-dark);
  font-family: var(--gt-font); font-size: 0.8rem; font-weight: 600;
  cursor: pointer; transition: background 160ms, border-color 160ms;
  -webkit-tap-highlight-color: transparent; touch-action: manipulation;
}
.gt-page-btn:hover:not(:disabled) { background: rgba(124,131,255,0.16) !important; }
.gt-page-btn.gt-page-active {
  background: rgba(124,131,255,0.22) !important;
  border-color: rgba(124,131,255,0.45); color: var(--gt-accent);
}
.gt-page-btn:disabled { opacity: 0.3; cursor: default; }
.gt-wrapper-light .gt-page-btn {
  border-color: rgba(26,26,46,0.15);
  background: rgba(26,26,46,0.05) !important;
  color: var(--gt-text-light);
}
.gt-wrapper-light .gt-page-btn:hover:not(:disabled) { background: rgba(26,26,46,0.10) !important; }
.gt-wrapper-light .gt-page-btn.gt-page-active { color: var(--gt-accent); }

/* ── Mobile card stacking ─────────────────────────────────────────────────── */
@media (max-width: 600px) {
  .gt-wrapper-responsive .gt-scroll { overflow-x: visible; border-radius: var(--gt-radius-sm); }
  .gt-wrapper-responsive .gt-table,
  .gt-wrapper-responsive .gt-table tbody,
  .gt-wrapper-responsive .gt-table tr,
  .gt-wrapper-responsive .gt-table td { display: block; width: 100%; }
  .gt-wrapper-responsive .gt-table thead { display: none; }
  .gt-wrapper-responsive .gt-table tbody tr {
    border: 1px solid rgba(124,131,255,0.12) !important;
    border-radius: var(--gt-radius-sm);
    margin-bottom: 10px; overflow: hidden;
    background: rgba(124,131,255,0.04) !important;
  }
  .gt-wrapper-responsive .gt-table tbody tr:last-child { margin-bottom: 0; }
  .gt-wrapper-responsive .gt-table td {
    display: flex; align-items: center;
    justify-content: space-between; gap: 12px;
    padding: 10px 14px;
    border: 0 !important;
    border-color: transparent !important;
    border-width: 0 !important;
    box-shadow: inset 0 -1px 0 rgba(124,131,255,0.09) !important;
    text-align: right; background: transparent !important;
  }
  .gt-wrapper-responsive .gt-table td:last-child { box-shadow: none !important; }
  .gt-wrapper-responsive .gt-table td::before {
    content: attr(data-label);
    font-size: 0.72rem; font-weight: 700; letter-spacing: 0.06em;
    text-transform: uppercase; opacity: 0.45; flex-shrink: 0; text-align: left;
  }
  .gt-wrapper-responsive.gt-wrapper-striped .gt-table tbody tr:nth-child(even) td { background: transparent !important; }
  .gt-wrapper-responsive.gt-wrapper-striped .gt-table tbody tr:nth-child(even) { background: rgba(124,131,255,0.06) !important; }
  .gt-wrapper-responsive .gt-table tr.gt-empty td::before { display: none; }
  .gt-wrapper-responsive .gt-table tr.gt-empty td { justify-content: center; text-align: center; box-shadow: none !important; }
  .gt-search { width: 100%; }
  .gt-toolbar { flex-direction: column; align-items: stretch; }
  .gt-toolbar-actions { justify-content: flex-end; }
}

/* ── Backdrop-filter fallback ─────────────────────────────────────────────── */
@supports not ((-webkit-backdrop-filter: blur(1px)) or (backdrop-filter: blur(1px))) {
  .gt-wrapper-dark    .gt-scroll { background: rgba(12,12,26,0.94) !important; }
  .gt-wrapper-light   .gt-scroll { background: rgba(210,215,240,0.96) !important; }
  .gt-wrapper-minimal .gt-scroll { background: rgba(12,12,26,0.90) !important; }
}

/* ── Keyboard focus styles ────────────────────────────────────────────────── */
.gt-scroll:focus,
.gt-scroll:focus-visible {
  outline: none;
  box-shadow:
    var(--gt-shadow),
    0 0 0 3px rgba(124,131,255,0.50),
    0 0 0 5px rgba(124,131,255,0.18) !important;
}
.gt-table th[data-sortable="true"]:focus,
.gt-table th[data-sortable="true"]:focus-visible {
  outline: none !important;
  box-shadow: inset 0 0 0 2px var(--gt-accent), inset 0 -1px 0 var(--gt-divider-dark) !important;
  color: var(--gt-accent);
  background: var(--gt-head-dark) !important;
}
.gt-wrapper-light .gt-table th[data-sortable="true"]:focus,
.gt-wrapper-light .gt-table th[data-sortable="true"]:focus-visible {
  box-shadow: inset 0 0 0 2px var(--gt-accent), inset 0 -1px 0 var(--gt-divider-light) !important;
  background: var(--gt-head-light) !important;
}
/* Focused body row */
.gt-table tbody tr[data-gt-focused="true"],
.gt-table tbody tr:focus,
.gt-table tbody tr:focus-visible {
  outline: none !important;
  box-shadow:
    inset 3px 0 0 var(--gt-accent),
    inset 0 0 0 1px rgba(124,131,255,0.35),
    0 0 14px rgba(124,131,255,0.12) !important;
  background: rgba(124,131,255,0.10) !important;
}
.gt-table tbody tr[data-gt-focused="true"] > td,
.gt-table tbody tr:focus > td,
.gt-table tbody tr:focus-visible > td {
  background: transparent !important;
}
.gt-table tbody tr .gt-action-btn:focus,
.gt-table tbody tr .gt-action-btn:focus-visible {
  outline: none !important;
  box-shadow: 0 0 0 2px var(--gt-accent), 0 0 8px rgba(124,131,255,0.30) !important;
}
.gt-page-btn:focus,
.gt-page-btn:focus-visible {
  outline: none !important;
  box-shadow: 0 0 0 2px var(--gt-accent), 0 0 8px rgba(124,131,255,0.25) !important;
}
.gt-search:focus,
.gt-search:focus-visible {
  border-color: var(--gt-accent) !important;
  background: rgba(124,131,255,0.13) !important;
  box-shadow: 0 0 0 3px rgba(124,131,255,0.22), 0 0 0 1px var(--gt-accent) !important;
}
.gt-export-btn:focus,
.gt-export-btn:focus-visible {
  outline: none !important;
  border-color: var(--gt-accent) !important;
  box-shadow: 0 0 0 2px var(--gt-accent), 0 0 8px rgba(124,131,255,0.25) !important;
}

/* Screen-reader live region */
.gt-live-region {
  position: absolute; width: 1px; height: 1px;
  padding: 0; margin: -1px; overflow: hidden;
  clip: rect(0,0,0,0); white-space: nowrap; border: 0;
}

/* ── Reduced motion ───────────────────────────────────────────────────────── */
@media (prefers-reduced-motion: reduce) {
  .gt-table td, .gt-action-btn, .gt-page-btn,
  .gt-search, .gt-export-btn { transition-duration: 1ms !important; }
}
</style>
<script${nonce}>
(function () {
  'use strict';

  /* ─── Init single table ─────────────────────────────────────────────────── */
  function initTable(wrapper) {
    var tableId  = wrapper.getAttribute('data-gt-id');
    var table    = wrapper.querySelector('.gt-table');
    var tbody    = table && table.querySelector('tbody');
    if (!table || !tbody) return;

    var sortable   = wrapper.getAttribute('data-gt-sortable')   === 'true';
    var searchable = wrapper.getAttribute('data-gt-searchable') === 'true';
    var paginate   = wrapper.getAttribute('data-gt-paginate')   === 'true';
    var pageSize   = parseInt(wrapper.getAttribute('data-gt-pagesize'), 10) || 10;
    var exportCsv  = wrapper.getAttribute('data-gt-export')     === 'true';
    var responsive = wrapper.getAttribute('data-gt-responsive') !== 'false';

    var allRows  = [];
    var filtered = [];
    var curPage  = 1;
    var sortCol  = -1;
    var sortDir  = 'asc';

    /* ── ARIA setup ─────────────────────────────────────────────────────── */
    table.setAttribute('role', 'grid');
    table.setAttribute('aria-rowcount', '0');

    var scrollEl = wrapper.querySelector('.gt-scroll');
    if (scrollEl) {
      scrollEl.setAttribute('role', 'region');
      scrollEl.setAttribute('tabindex', '0');
    }

    var heads    = table.querySelectorAll('thead th');
    var theadRow = table.querySelector('thead tr');
    if (theadRow) {
      theadRow.setAttribute('role', 'row');
      theadRow.setAttribute('aria-rowindex', '1');
    }
    heads.forEach(function (th) {
      th.setAttribute('role', 'columnheader');
      th.setAttribute('aria-sort', 'none');
      if (th.getAttribute('data-sortable') === 'true') {
        th.setAttribute('tabindex', '-1');
      }
    });

    /* Cache body rows (skip empty-state placeholder) */
    tbody.querySelectorAll('tr').forEach(function (tr, i) {
      if (tr.classList.contains('gt-empty')) return;
      allRows.push(tr);
      tr.setAttribute('role', 'row');
      tr.setAttribute('tabindex', '-1');
      tr.setAttribute('aria-rowindex', String(i + 2));
      if (responsive) {
        tr.querySelectorAll('td').forEach(function (td, ci) {
          var lbl = heads[ci] ? heads[ci].textContent.trim() : '';
          td.setAttribute('data-label', lbl);
        });
      }
    });
    tbody.querySelectorAll('td').forEach(function (td) {
      td.setAttribute('role', 'gridcell');
    });

    filtered = allRows.slice();

    /* ── Keyboard focus state ───────────────────────────────────────────── */
    var focusedRowIdx = -1;
    var inRowMode     = false;

    function visibleRows() {
      return allRows.filter(function (tr) { return !tr.hidden; });
    }

    function clearRowFocus() {
      allRows.forEach(function (tr) {
        tr.removeAttribute('data-gt-focused');
        tr.setAttribute('tabindex', '-1');
      });
      table.removeAttribute('aria-activedescendant');
      inRowMode     = false;
      focusedRowIdx = -1;
    }

    function focusRow(idx) {
      var rows = visibleRows();
      if (!rows.length) return;
      idx = Math.max(0, Math.min(idx, rows.length - 1));

      /* Step 1 — clear previous state without resetting the target row */
      allRows.forEach(function (tr) {
        tr.removeAttribute('data-gt-focused');
        tr.setAttribute('tabindex', '-1');
      });

      /* Step 2 — mark and focus target */
      inRowMode     = true;
      focusedRowIdx = idx;
      var tr = rows[idx];
      tr.setAttribute('data-gt-focused', 'true');
      tr.setAttribute('tabindex', '0');
      if (tr.id) table.setAttribute('aria-activedescendant', tr.id);
      tr.focus({ preventScroll: false });
      tr.scrollIntoView({ block: 'nearest' });
    }

    function focusScrollRegion() {
      clearRowFocus();
      if (scrollEl) scrollEl.focus({ preventScroll: true });
    }

    /* ── Live-region announcer ──────────────────────────────────────────── */
    var liveRegion = wrapper.querySelector('.gt-live-region');
    function announce(msg) {
      if (!liveRegion) return;
      liveRegion.textContent = '';
      setTimeout(function () { liveRegion.textContent = msg; }, 50);
    }

    /* ── Search ─────────────────────────────────────────────────────────── */
    var searchEl = null;
    if (searchable) {
      searchEl = wrapper.querySelector('.gt-search');
      if (searchEl) {
        searchEl.addEventListener('input', debounce(function () {
          var q = searchEl.value.toLowerCase().trim();
          filtered = allRows.filter(function (tr) {
            return !q || tr.textContent.toLowerCase().indexOf(q) !== -1;
          });
          curPage = 1;
          render();
        }, 180));

        searchEl.addEventListener('keydown', function (e) {
          if (e.key === 'Enter') {
            e.preventDefault();
            searchEl.blur();
            var rows = visibleRows();
            if (rows.length) { focusRow(0); }
            else { focusScrollRegion(); }
          }
          if (e.key === 'Escape') {
            e.preventDefault();
            searchEl.value = '';
            searchEl.dispatchEvent(new Event('input'));
            focusScrollRegion();
          }
        });
      }
    }

    /* ── Sort ───────────────────────────────────────────────────────────── */
    function doSort(ci, th) {
      if (sortCol === ci) {
        sortDir = sortDir === 'asc' ? 'desc' : 'asc';
      } else {
        sortCol = ci; sortDir = 'asc';
      }
      heads.forEach(function (h) {
        h.removeAttribute('data-sort');
        h.setAttribute('aria-sort', 'none');
      });
      th.setAttribute('data-sort', sortDir);
      th.setAttribute('aria-sort', sortDir === 'asc' ? 'ascending' : 'descending');

      var numeric = filtered.every(function (tr) {
        var td = tr.querySelectorAll('td')[ci];
        return td && !isNaN(parseFloat(td.textContent.trim().replace(/[^0-9.\-]/g, '')));
      });

      filtered.sort(function (a, b) {
        var ta = (a.querySelectorAll('td')[ci] || {}).textContent || '';
        var tb = (b.querySelectorAll('td')[ci] || {}).textContent || '';
        ta = ta.trim(); tb = tb.trim();
        var cmp = numeric
          ? parseFloat(ta.replace(/[^0-9.\-]/g, '')) - parseFloat(tb.replace(/[^0-9.\-]/g, ''))
          : ta.localeCompare(tb, undefined, { sensitivity: 'base' });
        return sortDir === 'asc' ? cmp : -cmp;
      });

      curPage = 1;
      render();
    }

    if (sortable) {
      heads.forEach(function (th, ci) {
        if (th.getAttribute('data-sortable') !== 'true') return;
        th.addEventListener('click', function () { doSort(ci, th); });

        th.addEventListener('keydown', function (e) {
          var sortableHeads = Array.prototype.slice.call(heads).filter(function (h) {
            return h.getAttribute('data-sortable') === 'true';
          });
          var myIdx = sortableHeads.indexOf(th);

          switch (e.key) {
            case 'Enter':
            case ' ':
              e.preventDefault(); doSort(ci, th); break;
            case 'ArrowRight':
              e.preventDefault();
              if (myIdx < sortableHeads.length - 1) sortableHeads[myIdx + 1].focus();
              break;
            case 'ArrowLeft':
              e.preventDefault();
              if (myIdx > 0) sortableHeads[myIdx - 1].focus();
              break;
            case 'ArrowDown':
              e.preventDefault(); focusRow(0); break;
            case 'Escape':
              e.preventDefault(); focusScrollRegion(); break;
          }
        });
      });
    }

    /* ── Pagination ─────────────────────────────────────────────────────── */
    function goPage(p) {
      var pages = Math.max(1, Math.ceil(filtered.length / pageSize));
      curPage = Math.max(1, Math.min(p, pages));
      render();
    }

    /* ── Render ─────────────────────────────────────────────────────────── */
    function render() {
      var start = paginate ? (curPage - 1) * pageSize : 0;
      var end   = paginate ? start + pageSize          : filtered.length;

      allRows.forEach(function (tr) { tr.hidden = true; });
      var visible = filtered.slice(start, end);
      visible.forEach(function (tr) { tr.hidden = false; });

      table.setAttribute('aria-rowcount', String(filtered.length));

      var emptyRow = tbody.querySelector('.gt-empty');
      if (emptyRow) emptyRow.hidden = visible.length > 0;

      /* Keep focus index in bounds after filter/page change */
      if (inRowMode) {
        var newVisible = visibleRows();
        if (!newVisible.length) {
          clearRowFocus();
        } else {
          focusedRowIdx = Math.min(focusedRowIdx, newVisible.length - 1);
          allRows.forEach(function (tr) { tr.removeAttribute('data-gt-focused'); });
          newVisible[focusedRowIdx].setAttribute('data-gt-focused', 'true');
        }
      }

      if (paginate) renderPagination();
    }

    function renderPagination() {
      var pgEl = wrapper.querySelector('.gt-pagination');
      if (!pgEl) return;
      var total = filtered.length;
      var pages = Math.max(1, Math.ceil(total / pageSize));
      curPage   = Math.min(curPage, pages);
      var start = (curPage - 1) * pageSize + 1;
      var end   = Math.min(curPage * pageSize, total);

      var info = pgEl.querySelector('.gt-page-info');
      if (info) {
        info.textContent = total === 0 ? 'No records' : start + '\u2013' + end + ' of ' + total;
      }

      var btnsEl = pgEl.querySelector('.gt-page-btns');
      if (!btnsEl) return;
      btnsEl.innerHTML = '';

      function mkBtn(label, page, disabled, active) {
        var b = document.createElement('button');
        b.className = 'gt-page-btn' + (active ? ' gt-page-active' : '');
        b.textContent = label;
        b.disabled = disabled;
        b.setAttribute('aria-label', 'Page ' + label);
        b.setAttribute('aria-current', active ? 'true' : 'false');
        if (!disabled && !active) {
          b.addEventListener('click', function () { curPage = page; render(); });
        }
        return b;
      }

      btnsEl.appendChild(mkBtn('\u2039', curPage - 1, curPage === 1, false));
      var lo = Math.max(1, curPage - 2);
      var hi = Math.min(pages, lo + 4);
      lo     = Math.max(1, hi - 4);
      for (var p = lo; p <= hi; p++) {
        btnsEl.appendChild(mkBtn(p, p, false, p === curPage));
      }
      btnsEl.appendChild(mkBtn('\u203a', curPage + 1, curPage === pages, false));
    }

    /* ── CSV export ─────────────────────────────────────────────────────── */
    if (exportCsv) {
      var expBtn = wrapper.querySelector('.gt-export-btn');
      if (expBtn) {
        expBtn.addEventListener('click', function () {
          var lines = [];
          var hRow  = [];
          table.querySelectorAll('thead th').forEach(function (th) { hRow.push(csvCell(th.textContent)); });
          lines.push(hRow.join(','));
          filtered.forEach(function (tr) {
            var row = [];
            tr.querySelectorAll('td').forEach(function (td) { row.push(csvCell(td.textContent)); });
            lines.push(row.join(','));
          });
          var blob = new Blob([lines.join('\n')], { type: 'text/csv' });
          var a    = document.createElement('a');
          a.href   = URL.createObjectURL(blob);
          a.download = (tableId || 'table') + '.csv';
          a.click();
          URL.revokeObjectURL(a.href);
        });
      }
    }

    /* ── Keyboard: scroll region ─────────────────────────────────────────
     * This is the single outer Tab stop. Keyboard shortcuts are processed
     * only when the scrollEl itself has focus — not when focus is inside
     * a search box, action button, or row.
     * ──────────────────────────────────────────────────────────────────── */
    if (scrollEl) {
      scrollEl.addEventListener('keydown', function (e) {
        /* Only handle when the scroll container itself is focused,
           not a child element (search, button, row, etc.) */
        if (e.target !== scrollEl) return;

        var rows = visibleRows();

        /* Search shortcut: '/'  or  Ctrl/Cmd + F */
        var isSearchShortcut = (e.key === '/') ||
          ((e.ctrlKey || e.metaKey) && (e.key === 'f' || e.key === 'F'));

        if (isSearchShortcut && searchEl) {
          e.preventDefault();
          searchEl.focus();
          searchEl.select();
          return;
        }

        switch (e.key) {
          case 'ArrowDown':
          case 'Enter':
            e.preventDefault();
            if (rows.length) focusRow(0);
            break;
          case 'ArrowUp':
            e.preventDefault();
            if (rows.length) focusRow(rows.length - 1);
            break;
          case 'ArrowRight':
          case 'PageDown':
            if (paginate) { e.preventDefault(); goPage(curPage + 1); }
            break;
          case 'ArrowLeft':
          case 'PageUp':
            if (paginate) { e.preventDefault(); goPage(curPage - 1); }
            break;
        }
      });

      scrollEl.addEventListener('focus', function (e) {
        if (e.target !== scrollEl) return;
        var parts = ['Table focused.'];
        if (searchable)  parts.push('Press / to search.');
        parts.push('Press \u2193 to navigate rows.');
        if (sortable)    parts.push('Tab to sortable columns.');
        if (paginate)    parts.push('\u2190\u2192 to change pages.');
        announce(parts.join(' '));
      });
    }

    /* ── Keyboard: body row navigation ──────────────────────────────────
     * Single listener on tbody — covers both row-level and
     * action-button-level Escape in one place (no capture-phase conflict).
     * ──────────────────────────────────────────────────────────────────── */
    tbody.addEventListener('keydown', function (e) {
      var target = e.target;
      var tr     = target.closest('tr.gt-row');
      if (!tr || tr.classList.contains('gt-empty')) return;

      var rows = visibleRows();
      var idx  = rows.indexOf(tr);

      /* ── Escape: action button → row TR, or row TR → scroll region ── */
      if (e.key === 'Escape') {
        e.preventDefault();
        if (target !== tr) {
          /* Focus was inside an action button — return to row */
          tr.setAttribute('tabindex', '0');
          tr.focus();
        } else {
          /* Focus was on the row itself — return to scroll region */
          focusScrollRegion();
        }
        return;
      }

      /* ── When focus is inside an action button, let the browser handle
         Tab, Enter, Space — only intercept navigation keys ── */
      if (target !== tr) {
        /* Tab inside a row: cycle through action buttons */
        if (e.key === 'Tab') {
          var actions = Array.prototype.slice.call(
            tr.querySelectorAll('.gt-action-btn, a[href], button:not([disabled])')
          );
          if (!actions.length) return; /* let Tab exit naturally */

          var aIdx = actions.indexOf(target);
          if (!e.shiftKey && aIdx === actions.length - 1) {
            /* Last action + Tab forward → wrap to first */
            e.preventDefault();
            actions[0].focus();
          } else if (e.shiftKey && aIdx === 0) {
            /* First action + Shift-Tab → return focus to TR */
            e.preventDefault();
            tr.setAttribute('tabindex', '0');
            tr.focus();
          }
          /* Otherwise: let Tab move between actions naturally */
          return;
        }
        return; /* all other keys inside action buttons: default behaviour */
      }

      /* ── Focus is on the TR itself ── */
      switch (e.key) {
        case 'ArrowDown':
          e.preventDefault();
          if (idx < rows.length - 1) {
            focusRow(idx + 1);
          } else if (paginate) {
            var prevPage = curPage;
            goPage(curPage + 1);
            if (curPage !== prevPage) focusRow(0);
          }
          break;

        case 'ArrowUp':
          e.preventDefault();
          if (idx > 0) {
            focusRow(idx - 1);
          } else if (paginate && curPage > 1) {
            goPage(curPage - 1);
            focusRow(visibleRows().length - 1);
          } else if (sortable) {
            /* Climb into first sortable header */
            var firstSortable = Array.prototype.slice.call(heads).filter(function (h) {
              return h.getAttribute('data-sortable') === 'true';
            })[0];
            if (firstSortable) { clearRowFocus(); firstSortable.focus(); }
          }
          break;

        case 'Home':
          e.preventDefault(); focusRow(0); break;

        case 'End':
          e.preventDefault(); focusRow(rows.length - 1); break;

        case 'PageDown':
          if (paginate) { e.preventDefault(); goPage(curPage + 1); focusRow(0); }
          break;

        case 'PageUp':
          if (paginate) { e.preventDefault(); goPage(curPage - 1); focusRow(0); }
          break;

        case 'ArrowRight':
          if (paginate) { e.preventDefault(); goPage(curPage + 1); focusRow(0); }
          break;

        case 'ArrowLeft':
          if (paginate) { e.preventDefault(); goPage(curPage - 1); focusRow(visibleRows().length - 1); }
          break;

        case 'Enter': {
          /* Move focus into the first action button / link in the row */
          e.preventDefault();
          var firstAction = tr.querySelector('.gt-action-btn, a[href], button:not([disabled])');
          if (firstAction) {
            firstAction.focus();
          } else {
            var rowText = Array.prototype.slice.call(tr.querySelectorAll('td'))
              .map(function (td) { return td.textContent.trim(); })
              .filter(Boolean).join(', ');
            announce('Row ' + (tr.getAttribute('aria-rowindex') || '') + ': ' + rowText);
          }
          break;
        }

        case ' ':
          /* Announce row content to screen readers */
          e.preventDefault();
          var rowText2 = Array.prototype.slice.call(tr.querySelectorAll('td'))
            .map(function (td) { return td.textContent.trim(); })
            .filter(Boolean).join(', ');
          announce(rowText2);
          break;

        case 'Tab':
          /* Tab on TR → move into first action button */
          if (!e.shiftKey) {
            var actions2 = tr.querySelectorAll('.gt-action-btn, a[href], button:not([disabled])');
            if (actions2.length) { e.preventDefault(); actions2[0].focus(); }
            /* else: let Tab exit the table naturally */
          }
          break;
      }
    });

    /* ── Sync focusedRowIdx when anything inside tbody gains focus ─────── */
    tbody.addEventListener('focusin', function (e) {
      var tr = e.target.closest('tr.gt-row');
      if (!tr || tr.classList.contains('gt-empty')) return;
      var rows = visibleRows();
      var idx  = rows.indexOf(tr);
      if (idx === -1) return;
      allRows.forEach(function (r) { r.removeAttribute('data-gt-focused'); });
      tr.setAttribute('data-gt-focused', 'true');
      focusedRowIdx = idx;
      inRowMode = true;
    });

    /* ── '/' shortcut from anywhere inside the wrapper ───────────────────
     * Captures '/' pressed while focus is NOT already in an input.
     * ──────────────────────────────────────────────────────────────────── */
    wrapper.addEventListener('keydown', function (e) {
      if (e.key !== '/' && !((e.ctrlKey || e.metaKey) && (e.key === 'f' || e.key === 'F'))) return;
      var tag = (document.activeElement || {}).tagName;
      if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return;
      if ((document.activeElement || {}).isContentEditable) return;
      if (searchEl) { e.preventDefault(); searchEl.focus(); searchEl.select(); }
    });

    /* ── Helpers ────────────────────────────────────────────────────────── */
    function csvCell(s) {
      s = (s || '').trim().replace(/\s+/g, ' ');
      return s.indexOf(',') !== -1 || s.indexOf('"') !== -1 || s.indexOf('\n') !== -1
        ? '"' + s.replace(/"/g, '""') + '"' : s;
    }
    function debounce(fn, ms) {
      var t;
      return function () { clearTimeout(t); t = setTimeout(fn, ms); };
    }

    render();
  }

  /* ─── Auto-init ──────────────────────────────────────────────────────── */
  function initAll() {
    document.querySelectorAll('.gt-wrapper:not([data-gt-init])').forEach(function (w) {
      w.setAttribute('data-gt-init', '1');
      initTable(w);
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAll);
  } else {
    initAll();
  }

  window.GlassModalTable = {
    init: function (wrapperId) {
      var w = document.getElementById(wrapperId);
      if (w && w.classList.contains('gt-wrapper')) {
        w.removeAttribute('data-gt-init');
        initTable(w);
      }
    }
  };
})();
</script>
/$
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TABLE
  // ═══════════════════════════════════════════════════════════════════════════

  /**
   * @attr showEmpty  (optional) true (default) | false
   *                  Set false to completely suppress the empty-state row.
   *                  No message or icon will appear when the table has no rows.
   */
  def mtable = { attrs, body ->
    def id = attrs.id ?: ('gt-' + System.currentTimeMillis())
    def caption = attrs.caption ?: ''
    def variant = attrs.variant ?: 'dark'
    def size = attrs.size ?: 'md'
    def striped = attrs.striped == 'true'
    def hover = attrs.hover != 'false'
    def sortable = attrs.sortable == 'true'
    def searchable = attrs.searchable == 'true'
    def paginate = attrs.paginate == 'true'
    def pageSize = attrs.pageSize ?: '10'
    def compact = attrs.compact == 'true'
    def responsive = attrs.responsive != 'false'
    def sticky = attrs.stickyHead == 'true'
    def exportCsv = attrs.exportCsv == 'true'
    def emptyText = attrs.emptyText ?: 'No records found'
    def emptyIcon = attrs.emptyIcon ?: '⊘'
    def showEmpty = attrs.showEmpty != 'false'   // default true

    def validVariants = ['dark', 'light', 'modal', 'minimal']
    if (!(variant in validVariants)) variant = 'dark'

    def wrapperClasses = [
      'gt-wrapper',
      "gt-wrapper-${variant}",
      striped ? 'gt-wrapper-striped' : '',
      hover ? 'gt-wrapper-hover' : '',
      compact ? 'gt-wrapper-compact' : '',
      responsive ? 'gt-wrapper-responsive' : '',
      sticky ? 'gt-wrapper-sticky' : '',
    ].findAll { it }.join(' ')

    def showToolbar = caption || searchable || exportCsv

    out << """
<!-- ── GlassModalTable: #${id} ── -->
<div id="${id}"
     class="${wrapperClasses}"
     data-gt-id="${id}"
     data-gt-sortable="${sortable}"
     data-gt-searchable="${searchable}"
     data-gt-paginate="${paginate}"
     data-gt-pagesize="${pageSize}"
     data-gt-export="${exportCsv}"
     data-gt-responsive="${responsive}">
"""
    if (showToolbar) {
      out << "  <div class=\"gt-toolbar\">\n"
      if (caption) {
        out << "    <span class=\"gt-caption\">${caption.encodeAsHTML()}</span>\n"
      } else {
        out << "    <span></span>\n"
      }
      out << "    <div class=\"gt-toolbar-actions\">\n"
      if (searchable) {
        out << """      <div class="gt-search-wrap">
        <span class="gt-search-icon" aria-hidden="true">&#x2315;</span>
        <input class="gt-search" type="search" placeholder="Search\u2026" aria-label="Search table">
      </div>\n"""
      }
      if (exportCsv) {
        out << "      <button class=\"gt-export-btn\" type=\"button\" aria-label=\"Export as CSV\">&#x2913; Export</button>\n"
      }
      out << "    </div>\n  </div>\n"
    }

    out << "  <div class=\"gt-scroll\" role=\"region\" aria-label=\"${caption ? caption.encodeAsHTML() : 'Data table'}\" tabindex=\"0\">\n"
    out << "    <div class=\"gt-live-region\" aria-live=\"polite\" aria-atomic=\"true\"></div>\n"
    out << "    <table class=\"gt-table\" role=\"grid\">\n"

    request.setAttribute('gt.emptyText', emptyText)
    request.setAttribute('gt.emptyIcon', emptyIcon)
    request.setAttribute('gt.showEmpty', showEmpty)
    out << body()

    out << "    </table>\n  </div>\n"

    if (paginate) {
      out << """  <div class="gt-pagination" role="navigation" aria-label="Table pagination">
    <span class="gt-page-info"></span>
    <div class="gt-page-btns"></div>
  </div>\n"""
    }

    out << "</div>\n<!-- ── /GlassModalTable: #${id} ── -->\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // THEAD
  // ═══════════════════════════════════════════════════════════════════════════
  def mthead = { attrs, body ->
    out << "<thead>\n  <tr>\n"
    out << body()
    out << "  </tr>\n</thead>\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TH
  // ═══════════════════════════════════════════════════════════════════════════
  def mth = { attrs ->
    def label = attrs.label ?: ''
    def field = attrs.field ?: label.toLowerCase().replaceAll(/\s+/, '_')
    def sortable = attrs.sortable ?: 'true'
    def align = attrs.align ?: 'left'
    def width = attrs.width ?: ''
    def tooltip = attrs.tooltip ?: ''

    def styleAttr = width ? " style=\"width:${width.encodeAsHTML()}\"" : ''
    def titleAttr = tooltip ? " title=\"${tooltip.encodeAsHTML()}\"" : ''
    def alignClass = "gt-align-${align}"

    out << "    <th data-field=\"${field.encodeAsHTML()}\" data-sortable=\"${sortable}\" class=\"${alignClass}\"${styleAttr}${titleAttr} scope=\"col\">"
    out << label.encodeAsHTML()
    if (sortable == 'true') {
      out << "<span class=\"gt-sort-icon\" aria-hidden=\"true\"><span class=\"gt-sort-icon-up\">\u25B4</span><span class=\"gt-sort-icon-down\">\u25BE</span></span>"
    }
    out << "</th>\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TBODY
  // ═══════════════════════════════════════════════════════════════════════════

  /**
   * Appends the empty-state row only when showEmpty="true" (the default).
   * When showEmpty="false" is set on the parent <glass:mtable>, the empty row
   * is omitted entirely — no message, no icon, no blank space.
   */
  def mtbody = { attrs, body ->
    def emptyText = request.getAttribute('gt.emptyText') ?: 'No records found'
    def emptyIcon = request.getAttribute('gt.emptyIcon') ?: '⊘'
    def showEmpty = request.getAttribute('gt.showEmpty')
    if (showEmpty == null) showEmpty = true   // default
    def colspan = attrs.colspan ?: '99'

    out << "<tbody>\n"
    out << body()
    if (showEmpty) {
      out << """<tr class="gt-empty">
  <td colspan="${colspan}">
    <span class="gt-empty-icon" aria-hidden="true">${emptyIcon.encodeAsHTML()}</span>
    ${emptyText.encodeAsHTML()}
  </td>
</tr>\n"""
    }
    out << "</tbody>\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TR
  // ═══════════════════════════════════════════════════════════════════════════
  def mtr = { attrs, body ->
    def highlight = attrs.highlight == 'true'
    def extraClass = attrs.extraClass ?: ''
    def cls = ['gt-row', highlight ? 'gt-row-highlight' : '', extraClass].findAll { it }.join(' ')
    out << "  <tr class=\"${cls.trim()}\">\n"
    out << body()
    out << "  </tr>\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TD
  // ═══════════════════════════════════════════════════════════════════════════
  def mtd = { attrs, body ->
    def type = attrs.type ?: 'text'
    def align = attrs.align ?: (type in ['number', 'currency'] ? 'right' : 'left')
    def badgeVariant = attrs.badgeVariant ?: 'default'
    def avatarSrc = attrs.avatarSrc ?: ''
    def nowrap = attrs.nowrap == 'true'
    def colspan = attrs.colspan ?: ''
    def tooltip = attrs.title ?: ''
    def extraClass = attrs.extraClass ?: ''

    def typeClass = "gt-type-${type}"
    def alignClass = "gt-align-${align}"
    def tdClass = [typeClass, alignClass, nowrap ? 'text-nowrap' : '', extraClass].findAll { it }.join(' ')
    def colspanAttr = colspan ? " colspan=\"${colspan}\"" : ''
    def titleAttr = tooltip ? " title=\"${tooltip.encodeAsHTML()}\"" : ''

    out << "    <td class=\"${tdClass.trim()}\"${colspanAttr}${titleAttr}>\n"
    def content = body().trim()

    switch (type) {
      case 'email':
        out << "      <a href=\"mailto:${content.encodeAsHTML()}\">${content.encodeAsHTML()}</a>\n"
        break
      case 'badge':
        out << "      <span class=\"gt-badge gt-badge-${badgeVariant.encodeAsHTML()}\">${content.encodeAsHTML()}</span>\n"
        break
      case 'status':
        out << "      <span class=\"gt-badge gt-badge-${badgeVariant.encodeAsHTML()}\"><span class=\"gt-status-dot\"></span>${content.encodeAsHTML()}</span>\n"
        break
      case 'code':
        out << "      <code>${content.encodeAsHTML()}</code>\n"
        break
      case 'avatar':
        def initials = content.trim().take(2).toUpperCase()
        out << "      <div class=\"gt-avatar-cell\">\n"
        if (avatarSrc) {
          out << "        <img class=\"gt-avatar\" src=\"${avatarSrc.encodeAsHTML()}\" alt=\"\" aria-hidden=\"true\" loading=\"lazy\">\n"
        } else {
          out << "        <div class=\"gt-avatar-placeholder\" aria-hidden=\"true\">${initials.encodeAsHTML()}</div>\n"
        }
        out << "        <span>${content.encodeAsHTML()}</span>\n"
        out << "      </div>\n"
        break
      default:
        out << "      ${content}\n"
        break
    }
    out << "    </td>\n"
  }

  // ═══════════════════════════════════════════════════════════════════════════
  // TABLE ACTION BUTTON
  // ═══════════════════════════════════════════════════════════════════════════
  def mtableAction = { attrs ->
    def label = attrs.label ?: 'Action'
    def icon = attrs.icon ?: ''
    def variant = attrs.variant ?: 'ghost'
    def onclick = attrs.onclick ?: ''
    def href = attrs.href ?: ''
    def extraClass = attrs.extraClass ?: ''
    def disabled = attrs.disabled == 'true'

    def validVariants = ['ghost', 'primary', 'success', 'danger', 'warning']
    if (!(variant in validVariants)) variant = 'ghost'
    def cls = "gt-action-btn gt-action-${variant} ${extraClass}".trim()

    if (href && !disabled) {
      out << "<a href=\"${href.encodeAsHTML()}\" class=\"${cls}\" title=\"${label.encodeAsHTML()}\" aria-label=\"${label.encodeAsHTML()}\">"
    } else {
      def onclickAttr = onclick ? " onclick=\"${onclick.encodeAsHTML()}\"" : ''
      def disabledAttr = disabled ? ' disabled' : ''
      out << "<button type=\"button\" class=\"${cls}\" title=\"${label.encodeAsHTML()}\" aria-label=\"${label.encodeAsHTML()}\"${onclickAttr}${disabledAttr}>"
    }
    if (icon) out << "<span class=\"gt-action-icon\" aria-hidden=\"true\">${icon.encodeAsHTML()}</span>"
    out << "<span>${label.encodeAsHTML()}</span>"
    if (href && !disabled) {
      out << "</a>"
    } else {
      out << "</button>"
    }
  }
}
