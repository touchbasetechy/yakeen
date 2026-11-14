package com.murad.app.matrimony.card

/**
 * Modern Card Styles TagLib
 *
 * Provides CSS injection for modern glassmorphism cards.
 *
 * Usage:
 * <glass:modernCardLinkStyles />
 */
class ModernCardStylesTagLib {

  static namespace = "glass"

  /**
   * Injects the required CSS for modern glassmorphic cards
   * Place this in your GSP <head> section or main layout
   *
   * @attr includeAnimations - Include advanced animations (default: true)
   */
  def modernCardLinkStyles = { attrs ->
    def includeAnimations = attrs.includeAnimations != 'false'

    out << '''
<style>
/* Modern Glass Card Base */
.glass-card {
    position: relative;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.08);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1.5px solid rgba(255, 255, 255, 0.18);
    border-radius: 24px;
    overflow: hidden;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.12),
        inset 0 1px 0 rgba(255, 255, 255, 0.1);
    cursor: pointer;
    text-decoration: none;
    color: inherit;
}

.glass-card-body {
    position: relative;
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    flex: 1;
    z-index: 1;
}

/* Card Image */
.glass-card-img {
    width: 100%;
    max-width: 120px;
    height: auto;
    aspect-ratio: 1;
    object-fit: cover;
    border-radius: 16px;
    margin-bottom: 1rem;
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Card Icon */
.glass-card-icon-wrapper {
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 20px;
    margin-bottom: 1rem;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-card-icon {
    font-size: 2.5rem;
    color: rgba(255, 255, 255, 0.9);
    transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Card Title */
.glass-card-title {
    font-family: 'DM Sans', -apple-system, BlinkMacSystemFont, sans-serif;
    font-size: 1.125rem;
    font-weight: 600;
    color: #ffffff;
    text-align: center;
    margin-bottom: 0.5rem;
    letter-spacing: 0.01em;
}

/* Card Description */
.glass-card-description {
    font-size: 0.875rem;
    color: rgba(255, 255, 255, 0.7);
    text-align: center;
    margin-bottom: 0.75rem;
    line-height: 1.5;
}

/* Card Content */
.glass-card-content {
    font-size: 0.9375rem;
    color: rgba(255, 255, 255, 0.8);
    text-align: center;
    line-height: 1.6;
}

/* Card Footer */
.glass-card-footer {
    font-size: 0.8125rem;
    color: rgba(255, 255, 255, 0.6);
    text-align: center;
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* Card Badge */
.glass-card-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 0.375rem 0.75rem;
    background: rgba(99, 102, 241, 0.3);
    border: 1px solid rgba(99, 102, 241, 0.5);
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 600;
    color: #ffffff;
    z-index: 10;
    backdrop-filter: blur(8px);
}

.glass-card-badge-primary { background: rgba(99, 102, 241, 0.3); border-color: rgba(99, 102, 241, 0.5); }
.glass-card-badge-success { background: rgba(16, 185, 129, 0.3); border-color: rgba(16, 185, 129, 0.5); }
.glass-card-badge-danger { background: rgba(239, 68, 68, 0.3); border-color: rgba(239, 68, 68, 0.5); }
.glass-card-badge-warning { background: rgba(245, 158, 11, 0.3); border-color: rgba(245, 158, 11, 0.5); }
.glass-card-badge-info { background: rgba(6, 182, 212, 0.3); border-color: rgba(6, 182, 212, 0.5); }

/* Shine Effect */
.glass-card-shine {
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
}

/* Size Variants */
.glass-card-sm {
    border-radius: 16px;
}

.glass-card-sm .glass-card-body {
    padding: 1rem;
}

.glass-card-sm .glass-card-img {
    max-width: 80px;
}

.glass-card-sm .glass-card-icon-wrapper {
    width: 60px;
    height: 60px;
}

.glass-card-sm .glass-card-icon {
    font-size: 1.75rem;
}

.glass-card-sm .glass-card-title {
    font-size: 0.9375rem;
}

.glass-card-md {
    border-radius: 24px;
}

.glass-card-md .glass-card-body {
    padding: 1.5rem;
}

.glass-card-lg {
    border-radius: 32px;
}

.glass-card-lg .glass-card-body {
    padding: 2rem;
}

.glass-card-lg .glass-card-img {
    max-width: 160px;
}

.glass-card-lg .glass-card-icon-wrapper {
    width: 100px;
    height: 100px;
}

.glass-card-lg .glass-card-icon {
    font-size: 3rem;
}

.glass-card-lg .glass-card-title {
    font-size: 1.375rem;
}

.glass-card-xl {
    border-radius: 40px;
}

.glass-card-xl .glass-card-body {
    padding: 2.5rem;
}

.glass-card-xl .glass-card-img {
    max-width: 200px;
}

.glass-card-xl .glass-card-icon-wrapper {
    width: 120px;
    height: 120px;
}

.glass-card-xl .glass-card-icon {
    font-size: 3.5rem;
}

.glass-card-xl .glass-card-title {
    font-size: 1.5rem;
}

/* Color Variants */
.glass-card-default {
    background: rgba(255, 255, 255, 0.08);
}

.glass-card-primary {
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(129, 140, 248, 0.1));
    border-color: rgba(99, 102, 241, 0.3);
}

.glass-card-success {
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(52, 211, 153, 0.1));
    border-color: rgba(16, 185, 129, 0.3);
}

.glass-card-danger {
    background: linear-gradient(135deg, rgba(239, 68, 68, 0.15), rgba(248, 113, 113, 0.1));
    border-color: rgba(239, 68, 68, 0.3);
}

.glass-card-warning {
    background: linear-gradient(135deg, rgba(245, 158, 11, 0.15), rgba(251, 191, 36, 0.1));
    border-color: rgba(245, 158, 11, 0.3);
}

.glass-card-info {
    background: linear-gradient(135deg, rgba(6, 182, 212, 0.15), rgba(34, 211, 238, 0.1));
    border-color: rgba(6, 182, 212, 0.3);
}

.glass-card-gradient {
    background: linear-gradient(135deg,
        rgba(236, 72, 153, 0.2) 0%,
        rgba(168, 85, 247, 0.2) 50%,
        rgba(99, 102, 241, 0.2) 100%);
    border: 1.5px solid transparent;
    background-clip: padding-box;
}

/* Hover Effects */
.glass-card-hover-lift:hover {
    transform: translateY(-8px) scale(1.02);
    box-shadow:
        0 20px 60px rgba(0, 0, 0, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.3);
}

.glass-card-hover-lift:hover .glass-card-shine {
    left: 100%;
}

.glass-card-hover-lift:hover .glass-card-img {
    transform: scale(1.05);
}

.glass-card-hover-lift:hover .glass-card-icon {
    transform: scale(1.1) rotate(5deg);
}

.glass-card-hover-glow {
    position: relative;
}

.glass-card-hover-glow::after {
    content: '';
    position: absolute;
    inset: -2px;
    border-radius: inherit;
    padding: 2px;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), transparent);
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    opacity: 0;
    transition: opacity 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
}

.glass-card-hover-glow:hover::after {
    opacity: 1;
}

.glass-card-hover-glow:hover {
    transform: scale(1.02);
    box-shadow:
        0 16px 48px rgba(99, 102, 241, 0.4),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.glass-card-hover-tilt {
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-card-hover-tilt:hover {
    transform: perspective(1000px) rotateX(2deg) rotateY(-2deg) scale(1.02);
    box-shadow:
        0 20px 60px rgba(0, 0, 0, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

/* Glow Effect */
.glass-card-glow {
    box-shadow:
        0 8px 32px rgba(0, 0, 0, 0.12),
        0 0 40px rgba(99, 102, 241, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

/* Pulse Animation */
@keyframes cardPulse {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.9; transform: scale(0.98); }
}

.glass-card-pulse {
    animation: cardPulse 2s ease-in-out infinite;
}

/* Shimmer Effect */
@keyframes shimmer {
    0% { background-position: -1000px 0; }
    100% { background-position: 1000px 0; }
}

.glass-card-shimmer::before {
    content: '';
    position: absolute;
    inset: 0;
    background: linear-gradient(
        90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.1) 50%,
        transparent 100%
    );
    background-size: 1000px 100%;
    animation: shimmer 2s infinite;
    pointer-events: none;
}

/* Card Grid */
.glass-card-grid {
    display: grid;
    width: 100%;
}

.glass-card-grid-cols-1 { grid-template-columns: repeat(1, 1fr); }
.glass-card-grid-cols-2 { grid-template-columns: repeat(2, 1fr); }
.glass-card-grid-cols-3 { grid-template-columns: repeat(3, 1fr); }
.glass-card-grid-cols-4 { grid-template-columns: repeat(4, 1fr); }
.glass-card-grid-cols-5 { grid-template-columns: repeat(5, 1fr); }
.glass-card-grid-cols-6 { grid-template-columns: repeat(6, 1fr); }

.glass-card-grid-gap-sm { gap: 1rem; }
.glass-card-grid-gap-md { gap: 1.5rem; }
.glass-card-grid-gap-lg { gap: 2rem; }
.glass-card-grid-gap-xl { gap: 3rem; }

/* Icon Card Specific */
.glass-card-icon .glass-card-icon-wrapper {
    margin-bottom: 1.25rem;
}

/* Stat Card Specific */
.glass-card-stat .glass-card-body {
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    text-align: left;
}

.glass-card-stat-icon {
    font-size: 2.5rem;
    color: rgba(255, 255, 255, 0.9);
    margin-right: 1.5rem;
}

.glass-card-stat-content {
    flex: 1;
}

.glass-card-stat-title {
    font-size: 0.875rem;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 0.25rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
}

.glass-card-stat-value {
    font-size: 2rem;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 0.5rem;
}

.glass-card-stat-trend {
    font-size: 0.8125rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.glass-trend-up {
    color: #10b981;
}

.glass-trend-down {
    color: #ef4444;
}

.glass-trend-neutral {
    color: rgba(255, 255, 255, 0.6);
}

/* Responsive */
@media (max-width: 1200px) {
    .glass-card-grid-cols-6 { grid-template-columns: repeat(4, 1fr); }
    .glass-card-grid-cols-5 { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 992px) {
    .glass-card-grid-cols-6,
    .glass-card-grid-cols-5,
    .glass-card-grid-cols-4 { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
    .glass-card-grid-cols-6,
    .glass-card-grid-cols-5,
    .glass-card-grid-cols-4,
    .glass-card-grid-cols-3 { grid-template-columns: repeat(2, 1fr); }

    .glass-card-stat .glass-card-body {
        flex-direction: column;
        text-align: center;
    }

    .glass-card-stat-icon {
        margin-right: 0;
        margin-bottom: 1rem;
    }
}

@media (max-width: 576px) {
    .glass-card-grid { grid-template-columns: 1fr; }
}
</style>
'''
  }
}
