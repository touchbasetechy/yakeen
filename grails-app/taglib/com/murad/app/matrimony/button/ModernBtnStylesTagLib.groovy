package com.murad.app.matrimony.button

/**
 * Modern Button Styles TagLib
 *
 * Provides CSS and style injection for modern glassmorphism buttons.
 * This TagLib handles all styling, fonts, and visual effects.
 *
 * Usage:
 * <glass:modernBtnStyles />
 * <glass:modernBtnStyles includeOrbs="false" />
 * <glass:modernBtnStyles includeFonts="false" />
 */
class ModernBtnStylesTagLib {

  static namespace = "glass"

  /**
   * Injects the required CSS for modern glassmorphism buttons
   * Place this in your GSP <head> section or main layout
   *
   * @attr includeOrbs - Include animated background orbs (default: true)
   * @attr includeFonts - Include Google Fonts (default: true)
   */
  def modernBtnStyles = { attrs ->
    def includeOrbs = attrs.includeOrbs != 'false'
    def includeFonts = attrs.includeFonts != 'false'

    if (includeFonts) {
      out << """
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:wght@400;500;700&family=JetBrains+Mono:wght@400;600&display=swap" rel="stylesheet">
"""
    }

    out << """
<style>
:root {
    --primary: #6366f1;
    --primary-light: #818cf8;
    --secondary: #ec4899;
    --success: #10b981;
    --danger: #ef4444;
    --warning: #f59e0b;
    --info: #06b6d4;
    --dark: #0f172a;
    --light: #f8fafc;

    --glass-background: rgba(255, 255, 255, 0.08);
    --glass-border: rgba(255, 255, 255, 0.18);
    --glass-shadow: rgba(0, 0, 0, 0.1);

    --blur: 16px;
    --transition: cubic-bezier(0.4, 0, 0.2, 1);
}

/* Modern Glass Button Base */
.glass-btn {
    position: relative;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.625rem;
    font-family: 'DM Sans', -apple-system, BlinkMacSystemFont, sans-serif;
    font-weight: 600;
    font-size: 0.9375rem;
    letter-spacing: 0.01em;
    text-decoration: none;
    border: 1.5px solid var(--glass-border);
    background: var(--glass-background);
    backdrop-filter: blur(var(--blur));
    -webkit-backdrop-filter: blur(var(--blur));
    cursor: pointer;
    transition: all 0.4s var(--transition);
    overflow: hidden;
    white-space: nowrap;
    padding: 0.875rem 1.75rem;
    border-radius: 16px;
    box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.12),
        inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

/* Shimmer effect */
.glass-btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(
        90deg,
        transparent,
        rgba(255, 255, 255, 0.2),
        transparent
    );
    transition: left 0.6s var(--transition);
}

.glass-btn:hover::before {
    left: 100%;
}

/* Glow effect on hover */
.glass-btn::after {
    content: '';
    position: absolute;
    inset: -2px;
    border-radius: 16px;
    padding: 2px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), transparent);
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    opacity: 0;
    transition: opacity 0.4s var(--transition);
    pointer-events: none;
}

.glass-btn:hover {
    transform: translateY(-2px) scale(1.02);
    border-color: rgba(255, 255, 255, 0.3);
    box-shadow:
        0 16px 48px rgba(0, 0, 0, 0.2),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn:hover::after {
    opacity: 1;
}

.glass-btn:active {
    transform: translateY(0) scale(0.98);
    transition: all 0.1s var(--transition);
}

.glass-btn:disabled {
    opacity: 0.4;
    cursor: not-allowed;
    pointer-events: none;
}

/* Size Variants */
.glass-btn-sm {
    padding: 0.625rem 1.25rem;
    font-size: 0.875rem;
    border-radius: 12px;
}

.glass-btn-md {
    padding: 0.875rem 1.75rem;
    font-size: 0.9375rem;
    border-radius: 16px;
}

.glass-btn-lg {
    padding: 1.125rem 2.25rem;
    font-size: 1.0625rem;
    border-radius: 20px;
}

.glass-btn-xl {
    padding: 1.375rem 2.75rem;
    font-size: 1.25rem;
    border-radius: 24px;
    font-weight: 700;
}

/* Color Variants with Modern Gradients */
.glass-btn-default {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.08);
}

.glass-btn-default:hover {
    background: rgba(255, 255, 255, 0.12);
    box-shadow:
        0 16px 48px rgba(255, 255, 255, 0.08),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn-primary {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.3), rgba(129, 140, 248, 0.2));
    border-color: rgba(99, 102, 241, 0.5);
}

.glass-btn-primary:hover {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.5), rgba(129, 140, 248, 0.3));
    box-shadow:
        0 16px 48px rgba(99, 102, 241, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn-success {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.3), rgba(52, 211, 153, 0.2));
    border-color: rgba(16, 185, 129, 0.5);
}

.glass-btn-success:hover {
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.5), rgba(52, 211, 153, 0.3));
    box-shadow:
        0 16px 48px rgba(16, 185, 129, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn-danger {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.3), rgba(248, 113, 113, 0.2));
    border-color: rgba(239, 68, 68, 0.5);
}

.glass-btn-danger:hover {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.5), rgba(248, 113, 113, 0.3));
    box-shadow:
        0 16px 48px rgba(239, 68, 68, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn-warning {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.3), rgba(251, 191, 36, 0.2));
    border-color: rgba(245, 158, 11, 0.5);
}

.glass-btn-warning:hover {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.5), rgba(251, 191, 36, 0.3));
    box-shadow:
        0 16px 48px rgba(245, 158, 11, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-btn-info {
    color: #ffffff;
    background: linear-gradient(135deg, rgba(6, 182, 212, 0.3), rgba(34, 211, 238, 0.2));
    border-color: rgba(6, 182, 212, 0.5);
}

.glass-btn-info:hover {
    background: linear-gradient(135deg, rgba(6, 182, 212, 0.5), rgba(34, 211, 238, 0.3));
    box-shadow:
        0 16px 48px rgba(6, 182, 212, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

/* Gradient variant */
.glass-btn-gradient {
    background: linear-gradient(135deg,
        rgba(236, 72, 153, 0.4) 0%,
        rgba(168, 85, 247, 0.4) 50%,
        rgba(99, 102, 241, 0.4) 100%);
    border: 1.5px solid transparent;
    background-clip: padding-box;
    position: relative;
}

.glass-btn-gradient::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    padding: 1.5px;
    background: linear-gradient(135deg,
        rgba(236, 72, 153, 0.6),
        rgba(168, 85, 247, 0.6),
        rgba(99, 102, 241, 0.6));
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    z-index: -1;
}

.glass-btn-gradient:hover {
    background: linear-gradient(135deg,
        rgba(236, 72, 153, 0.6) 0%,
        rgba(168, 85, 247, 0.6) 50%,
        rgba(99, 102, 241, 0.6) 100%);
}

/* Pill variant */
.glass-btn-pill {
    border-radius: 9999px;
}

.glass-btn-pill::after {
    border-radius: 9999px;
}

/* Icon buttons */
.glass-btn-icon {
    padding: 0.875rem;
    aspect-ratio: 1;
}

.glass-btn-sm.glass-btn-icon {
    padding: 0.625rem;
}

.glass-btn-lg.glass-btn-icon {
    padding: 1.125rem;
}

.glass-btn-xl.glass-btn-icon {
    padding: 1.375rem;
}

.glass-btn i {
    font-size: 1em;
    transition: transform 0.3s var(--transition);
}

.glass-btn:hover i {
    transform: scale(1.1);
}

/* Button groups */
.glass-btn-group {
    display: inline-flex;
    gap: 0.75rem;
}

.glass-btn-group-vertical {
    display: inline-flex;
    flex-direction: column;
    gap: 0.75rem;
}

/* Outline variant */
.glass-btn-outline {
    background: transparent;
    border-width: 2px;
}

.glass-btn-outline:hover {
    background: var(--glass-background);
}

/* Loading animation */
@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.glass-btn.loading i {
    animation: spin 1s linear infinite;
}

/* Pulse effect */
@keyframes pulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.8; transform: scale(0.98); }
}

.glass-btn.pulse {
    animation: pulse 2s ease-in-out infinite;
}

/* Section styling */
.section {
    background: var(--glass-background);
    backdrop-filter: blur(var(--blur));
    -webkit-backdrop-filter: blur(var(--blur));
    border: 1px solid var(--glass-border);
    border-radius: 32px;
    padding: 3rem;
    margin-bottom: 3rem;
    position: relative;
    overflow: hidden;
}

.section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.4) 50%,
        transparent 100%);
}

.section-title {
    font-family: 'Syne', sans-serif;
    font-size: clamp(1.75rem, 3vw, 2.25rem);
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 2rem;
    letter-spacing: -0.01em;
}

.section-description {
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 2rem;
    font-size: 1rem;
    line-height: 1.6;
}

.tag {
    display: inline-block;
    padding: 0.375rem 0.875rem;
    background: rgba(99, 102, 241, 0.2);
    border: 1px solid rgba(99, 102, 241, 0.4);
    border-radius: 8px;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.05em;
    text-transform: uppercase;
    color: #c7d2fe;
    margin-bottom: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
    .section {
        padding: 2rem 1.5rem;
        border-radius: 24px;
    }

    .glass-btn-group {
        flex-wrap: wrap;
    }
}
</style>
"""

    if (includeOrbs) {
      out << """
<style>
/* Animated gradient background */
body::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background:
        radial-gradient(circle at 20% 50%, rgba(236, 72, 153, 0.15) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
        radial-gradient(circle at 40% 20%, rgba(168, 85, 247, 0.1) 0%, transparent 50%);
    animation: gradientShift 15s ease infinite;
    pointer-events: none;
    z-index: 0;
}

@keyframes gradientShift {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.8; }
}

/* Floating orbs */
.glass-orb {
    position: fixed;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.4;
    pointer-events: none;
    animation: float 20s ease-in-out infinite;
    z-index: 0;
}

.glass-orb-1 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(236, 72, 153, 0.6), transparent);
    top: 10%;
    left: 10%;
    animation-delay: 0s;
}

.glass-orb-2 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(99, 102, 241, 0.5), transparent);
    bottom: 10%;
    right: 10%;
    animation-delay: 7s;
}

.glass-orb-3 {
    width: 250px;
    height: 250px;
    background: radial-gradient(circle, rgba(168, 85, 247, 0.5), transparent);
    top: 50%;
    right: 20%;
    animation-delay: 14s;
}

@keyframes float {
    0%, 100% { transform: translate(0, 0) scale(1); }
    25% { transform: translate(30px, -30px) scale(1.1); }
    50% { transform: translate(-20px, 20px) scale(0.9); }
    75% { transform: translate(20px, 30px) scale(1.05); }
}
</style>
"""
    }
  }
}
