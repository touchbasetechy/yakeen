package com.murad.app.matrimony.card

class FeatureCardTagLib {
  static namespace = "glass"  // mc = Murad Components

  /**
   * Renders a single feature card with glassmorphic styling
   *
   * @attr icon REQUIRED - The emoji or icon to display
   * @attr title REQUIRED - The card title
   * @attr description REQUIRED - The card description text
   * @attr customClass - Optional additional CSS classes
   */
  def featureCard = { attrs, body ->
    def icon = attrs.icon ?: '✨'
    def title = attrs.title ?: 'Feature'
    def description = attrs.description ?: ''
    def customClass = attrs.customClass ?: ''

    out << """
<div class="feature-card ${customClass}">
  <div class="feature-icon">${icon}</div>
  <h3>${title.encodeAsHTML()}</h3>
  <p>${description.encodeAsHTML()}</p>
</div>
        """.trim()
  }

  /**
   * Renders a container for multiple feature cards
   *
   * @attr customClass - Optional additional CSS classes
   */
  def featureCardsContainer = { attrs, body ->
    def customClass = attrs.customClass ?: ''

    out << """
<div class="features ${customClass}">
${body()}
</div>
        """.trim()
  }

  /**
   * Renders the complete feature cards CSS
   * Include this in your <head> section or main CSS file
   */
  def featureCardsStyles = { attrs ->
    out << """
<style>
  /* Feature cards */
  .features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 25px;
    animation: fadeInUp 0.8s ease-out 0.2s backwards;
  }

  .feature-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    border-radius: 25px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    padding: 40px 30px;
    text-align: center;
    transition: all 0.3s ease;
    box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
    position: relative;
    overflow: hidden;
  }

  .feature-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }

  .feature-card:hover::before {
    opacity: 1;
  }

  .feature-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 15px 45px 0 rgba(31, 38, 135, 0.25);
    border-color: rgba(255, 255, 255, 0.4);
  }

  .feature-icon {
    font-size: 3rem;
    margin-bottom: 20px;
    display: inline-block;
    position: relative;
    z-index: 1;
  }

  .feature-card h3 {
    color: white;
    font-size: 1.5rem;
    margin-bottom: 15px;
    font-weight: 600;
    position: relative;
    z-index: 1;
  }

  .feature-card p {
    color: rgba(255, 255, 255, 0.85);
    font-size: 1rem;
    line-height: 1.6;
    position: relative;
    z-index: 1;
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Responsive */
  @media (max-width: 768px) {
    .features {
      grid-template-columns: 1fr;
    }
  }
</style>
        """.trim()
  }

  /**
   * Renders the JavaScript for feature card animations
   * Include this before closing </body> tag
   */
  def featureCardsScript = { attrs ->
    out << """
<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Add entrance animations to feature cards
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = '1';
          entry.target.style.transform = 'translateY(0)';
        }
      });
    }, {
      threshold: 0.1
    });

    document.querySelectorAll('.feature-card').forEach(card => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(20px)';
      card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
      observer.observe(card);
    });
  });
</script>
        """.trim()
  }
}
