package com.murad.app.matrimony.card

class SoftGlassCardTagLib {
  static namespace = "glass"

  /**
   * Soft Glassmorphic Card Tag with Bright Gradients
   *
   * @attr title REQUIRED The card title
   * @attr subtitle Optional card subtitle
   * @attr gradient Gradient theme: peach, mint, lavender, sky, coral, lemon, rose, aqua (default: peach)
   * @attr blur Blur intensity: light, medium, heavy (default: medium)
   * @attr icon Optional icon class (e.g., 'bi bi-star-fill')
   * @attr width Optional card width (default: 100%)
   * @attr height Optional card height (default: auto)
   * @attr bodyClass Optional additional CSS classes for card body
   * @attr shimmer Enable shimmer effect (true/false) - default is true
   */
  def softCard = { attrs, body ->
    def title = attrs.title ?: ''
    def subtitle = attrs.subtitle ?: ''
    def gradient = attrs.gradient ?: 'peach'
    def blur = attrs.blur ?: 'medium'
    def icon = attrs.icon ?: ''
    def width = attrs.width ?: '100%'
    def height = attrs.height ?: 'auto'
    def bodyClass = attrs.bodyClass ?: ''
    def shimmer = attrs.shimmer != 'false'

    // Unique ID for this card instance
    def cardId = "glass-card-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    // Define soft bright gradient themes
    def gradients = [
      'peach'   : 'linear-gradient(135deg, #FFE5D4 0%, #FFB5A7 25%, #FCD5CE 50%, #FEC89A 75%, #FFD7BA 100%)',
      'mint'    : 'linear-gradient(135deg, #D4F4DD 0%, #B7E4C7 25%, #95D5B2 50%, #74C69D 75%, #D8F3DC 100%)',
      'lavender': 'linear-gradient(135deg, #E7D2FF 0%, #D4BBFF 25%, #C8B6FF 50%, #B8A7F7 75%, #EAE2FF 100%)',
      'sky'     : 'linear-gradient(135deg, #D0E8FF 0%, #B4D7FF 25%, #A6C8FF 50%, #8FB7FF 75%, #E0F0FF 100%)',
      'coral'   : 'linear-gradient(135deg, #FFD4D4 0%, #FFB5C5 25%, #FFA7C3 50%, #FF8FAB 75%, #FFE0E9 100%)',
      'lemon'   : 'linear-gradient(135deg, #FFF9D0 0%, #FFF4A3 25%, #FFED85 50%, #FFE66D 75%, #FFFBEB 100%)',
      'rose'    : 'linear-gradient(135deg, #FFE4F0 0%, #FFC9E0 25%, #FFB4D5 50%, #FF9FCE 75%, #FFF0F7 100%)',
      'aqua'    : 'linear-gradient(135deg, #D0F4F7 0%, #B4E9ED 25%, #9ADEE3 50%, #7FD3D9 75%, #E5F9FA 100%)'
    ]

    def blurValues = [
      'light' : '12px',
      'medium': '20px',
      'heavy' : '30px'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['peach']
    def selectedBlur = blurValues[blur] ?: blurValues['medium']

    out << """
        <style>
            #${cardId} {
                position: relative;
                width: ${width};
                height: ${height};
                border-radius: 28px;
                overflow: hidden;
                backdrop-filter: blur(${selectedBlur}) saturate(180%);
                -webkit-backdrop-filter: blur(${selectedBlur}) saturate(180%);
                background: rgba(255, 255, 255, 0.7);
                box-shadow:
                    0 8px 32px 0 rgba(31, 38, 135, 0.15),
                    inset 0 1px 0 0 rgba(255, 255, 255, 0.9);
                transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
            }

            /* Gradient Border Effect */
            #${cardId}::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                border-radius: 28px;
                padding: 2px;
                background: ${selectedGradient};
                -webkit-mask:
                    linear-gradient(#fff 0 0) content-box,
                    linear-gradient(#fff 0 0);
                -webkit-mask-composite: xor;
                mask-composite: exclude;
                opacity: 0.6;
                z-index: 0;
                transition: opacity 0.4s ease;
            }

            /* Gradient Background Overlay */
            #${cardId}::after {
                content: '';
                position: absolute;
                top: 2px;
                left: 2px;
                right: 2px;
                bottom: 2px;
                background: ${selectedGradient};
                opacity: 0.25;
                border-radius: 26px;
                z-index: 0;
                transition: opacity 0.4s ease;
                pointer-events: none;
            }

            ${shimmer ? """
            #${cardId}:hover::before {
                opacity: 0.9;
                animation: borderPulse-${cardId} 2s ease-in-out infinite;
            }

            @keyframes borderPulse-${cardId} {
                0%, 100% { opacity: 0.6; }
                50% { opacity: 1; }
            }
            """ : """
            #${cardId}:hover::before {
                opacity: 0.9;
            }
            """}

            #${cardId}:hover::after {
                opacity: 0.4;
            }

            #${cardId}:hover {
                transform: translateY(-8px) scale(1.02);
                box-shadow:
                    0 16px 48px 0 rgba(31, 38, 135, 0.25),
                    0 0 0 2px rgba(255, 255, 255, 0.3),
                    inset 0 1px 0 0 rgba(255, 255, 255, 1);
            }

            #${cardId} .card-body-glass {
                position: relative;
                z-index: 2;
                padding: 2rem;
                color: rgba(60, 60, 60, 0.95);
            }

            #${cardId} .card-icon-glass {
                width: 64px;
                height: 64px;
                border-radius: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 2rem;
                margin-bottom: 1.5rem;
                background: rgba(255, 255, 255, 0.6);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                box-shadow:
                    0 4px 16px rgba(31, 38, 135, 0.1),
                    inset 0 1px 0 rgba(255, 255, 255, 0.8);
                border: 1px solid rgba(255, 255, 255, 0.7);
                transition: transform 0.3s ease;
            }

            #${cardId}:hover .card-icon-glass {
                transform: scale(1.1) rotate(5deg);
            }

            #${cardId} .card-title-glass {
                font-size: 1.75rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                color: rgba(40, 40, 40, 0.95);
                letter-spacing: -0.5px;
            }

            #${cardId} .card-subtitle-glass {
                font-size: 0.875rem;
                color: rgba(80, 80, 80, 0.8);
                margin-bottom: 1.5rem;
                font-weight: 500;
                text-transform: uppercase;
                letter-spacing: 1px;
            }

            #${cardId} .card-content-glass {
                color: rgba(60, 60, 60, 0.85);
                line-height: 1.7;
            }

            /* Responsive adjustments */
            @media (max-width: 768px) {
                #${cardId} .card-body-glass {
                    padding: 1.5rem;
                }

                #${cardId} .card-title-glass {
                    font-size: 1.5rem;
                }
            }
        </style>

        <div id="${cardId}" class="glass-card">
            <div class="card-body-glass ${bodyClass}">
                ${icon ? """
                <div class="card-icon-glass">
                    <i class="${icon}"></i>
                </div>
                """ : ''}
                ${title ? """<h5 class="card-title-glass">${title}</h5>""" : ''}
                ${subtitle ? """<h6 class="card-subtitle-glass">${subtitle}</h6>""" : ''}
                <div class="card-content-glass">
                    ${body()}
                </div>
            </div>
        </div>
        """
  }

  /**
   * Soft Glass Button Component
   *
   * @attr label REQUIRED Button text
   * @attr gradient Button gradient: peach, mint, lavender, sky, coral (default: peach)
   * @attr size Button size: sm, md, lg (default: md)
   * @attr type Button type: button, submit, reset (default: button)
   * @attr onclick Optional onclick handler
   */
  def softButton = { attrs ->
    def label = attrs.label ?: 'Button'
    def gradient = attrs.gradient ?: 'peach'
    def size = attrs.size ?: 'md'
    def type = attrs.type ?: 'button'
    def onclick = attrs.onclick ?: ''

    def buttonId = "glass-btn-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    def gradients = [
      'peach'   : 'linear-gradient(135deg, #FFE5D4, #FFB5A7)',
      'mint'    : 'linear-gradient(135deg, #D4F4DD, #74C69D)',
      'lavender': 'linear-gradient(135deg, #E7D2FF, #B8A7F7)',
      'sky'     : 'linear-gradient(135deg, #D0E8FF, #8FB7FF)',
      'coral'   : 'linear-gradient(135deg, #FFD4D4, #FF8FAB)'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['peach']

    def padding = size == 'sm' ? '0.5rem 1rem' : (size == 'lg' ? '1rem 2rem' : '0.75rem 1.5rem')
    def fontSize = size == 'sm' ? '0.875rem' : (size == 'lg' ? '1.125rem' : '1rem')

    out << """
        <style>
            #${buttonId} {
                padding: ${padding};
                font-size: ${fontSize};
                font-weight: 600;
                border: none;
                border-radius: 16px;
                background: rgba(255, 255, 255, 0.7);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                color: rgba(60, 60, 60, 0.95);
                box-shadow:
                    0 4px 16px rgba(31, 38, 135, 0.15),
                    inset 0 1px 0 rgba(255, 255, 255, 0.8);
                border: 1px solid rgba(255, 255, 255, 0.7);
                cursor: pointer;
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            #${buttonId}::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: ${selectedGradient};
                opacity: 0.4;
                transition: opacity 0.3s ease;
            }

            #${buttonId}:hover {
                transform: translateY(-2px);
                box-shadow:
                    0 8px 24px rgba(31, 38, 135, 0.25),
                    inset 0 1px 0 rgba(255, 255, 255, 1);
            }

            #${buttonId}:hover::before {
                opacity: 0.6;
            }

            #${buttonId} span {
                position: relative;
                z-index: 1;
            }
        </style>

        <button id="${buttonId}" type="${type}" ${onclick ? "onclick=\"${onclick}\"" : ''}>
            <span>${label}</span>
        </button>
        """
  }

  /**
   * Soft Glass Button Component (No Border/Fill by Default)
   * Transparent by default, fills with gradient on hover/focus/active
   *
   * @attr label REQUIRED Button text
   * @attr gradient Button gradient: peach, mint, lavender, sky, coral (default: peach)
   * @attr size Button size: sm, md, lg (default: md)
   * @attr type Button type: button, submit, reset (default: button)
   * @attr onclick Optional onclick handler
   */
  def softNoBorderButton = { attrs ->
    def label = attrs.label ?: 'Button'
    def gradient = attrs.gradient ?: 'peach'
    def size = attrs.size ?: 'md'
    def type = attrs.type ?: 'button'
    def onclick = attrs.onclick ?: ''

    def buttonId = "glass-noborder-btn-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    def gradients = [
      'peach'   : 'linear-gradient(135deg, #FFE5D4, #FFB5A7)',
      'mint'    : 'linear-gradient(135deg, #D4F4DD, #74C69D)',
      'lavender': 'linear-gradient(135deg, #E7D2FF, #B8A7F7)',
      'sky'     : 'linear-gradient(135deg, #D0E8FF, #8FB7FF)',
      'coral'   : 'linear-gradient(135deg, #FFD4D4, #FF8FAB)'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['peach']

    def padding = size == 'sm' ? '0.5rem 1rem' : (size == 'lg' ? '1rem 2rem' : '0.75rem 1.5rem')
    def fontSize = size == 'sm' ? '0.875rem' : (size == 'lg' ? '1.125rem' : '1rem')

    out << """
        <style>
            #${buttonId} {
                padding: ${padding};
                font-size: ${fontSize};
                font-weight: 600;
                border-radius: 16px;
                background: transparent;
                color: rgba(60, 60, 60, 0.85);
                border: 2px solid transparent;
                cursor: pointer;
                transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
                position: relative;
                overflow: hidden;
                backdrop-filter: none;
                -webkit-backdrop-filter: none;
            }

            #${buttonId}::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: ${selectedGradient};
                opacity: 0;
                transition: opacity 0.3s ease;
                z-index: -1;
            }

            /* Border gradient effect */
            #${buttonId}::after {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                border-radius: 16px;
                padding: 2px;
                background: ${selectedGradient};
                -webkit-mask:
                    linear-gradient(#fff 0 0) content-box,
                    linear-gradient(#fff 0 0);
                -webkit-mask-composite: xor;
                mask-composite: exclude;
                opacity: 0;
                transition: opacity 0.3s ease;
            }

            /* Hover state */
            #${buttonId}:hover {
                transform: translateY(-2px);
                color: rgba(40, 40, 40, 0.95);
                box-shadow: 0 8px 24px rgba(31, 38, 135, 0.25);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
            }

            #${buttonId}:hover::before {
                opacity: 0.5;
            }

            #${buttonId}:hover::after {
                opacity: 0.8;
            }

            /* Focus state */
            #${buttonId}:focus {
                outline: none;
                transform: translateY(-2px);
                color: rgba(40, 40, 40, 0.95);
                box-shadow:
                    0 8px 24px rgba(31, 38, 135, 0.25),
                    0 0 0 3px rgba(255, 255, 255, 0.3);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
            }

            #${buttonId}:focus::before {
                opacity: 0.5;
            }

            #${buttonId}:focus::after {
                opacity: 0.8;
            }

            /* Active/Clicked state */
            #${buttonId}:active {
                transform: translateY(0);
                color: rgba(40, 40, 40, 0.95);
                box-shadow:
                    0 4px 12px rgba(31, 38, 135, 0.2),
                    inset 0 2px 4px rgba(0, 0, 0, 0.1);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
            }

            #${buttonId}:active::before {
                opacity: 0.7;
            }

            #${buttonId}:active::after {
                opacity: 1;
            }

            #${buttonId} span {
                position: relative;
                z-index: 1;
            }
        </style>

        <button id="${buttonId}" type="${type}" ${onclick ? "onclick=\"${onclick}\"" : ''}>
            <span>${label}</span>
        </button>
        """
  }

  /**
   * Soft Glass Tag Component
   *
   * @attr label REQUIRED Tag text
   * @attr gradient Tag gradient: peach, mint, lavender, sky, coral (default: peach)
   */
  def softTag = { attrs ->
    def label = attrs.label ?: ''
    def gradient = attrs.gradient ?: 'peach'

    def tagId = "glass-tag-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    def gradients = [
      'peach'   : 'linear-gradient(135deg, #FFE5D4, #FFB5A7)',
      'mint'    : 'linear-gradient(135deg, #D4F4DD, #74C69D)',
      'lavender': 'linear-gradient(135deg, #E7D2FF, #B8A7F7)',
      'sky'     : 'linear-gradient(135deg, #D0E8FF, #8FB7FF)',
      'coral'   : 'linear-gradient(135deg, #FFD4D4, #FF8FAB)',
      'lemon'   : 'linear-gradient(135deg, #FFF9D0, #FFE66D)',
      'rose'    : 'linear-gradient(135deg, #FFE4F0, #FF9FCE)',
      'aqua'    : 'linear-gradient(135deg, #D0F4F7, #7FD3D9)'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['peach']

    out << """
        <style>
            #${tagId} {
                display: inline-block;
                padding: 0.5rem 1rem;
                border-radius: 20px;
                font-size: 0.75rem;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                background: rgba(255, 255, 255, 0.6);
                backdrop-filter: blur(8px);
                -webkit-backdrop-filter: blur(8px);
                color: rgba(60, 60, 60, 0.9);
                box-shadow:
                    0 2px 8px rgba(31, 38, 135, 0.1),
                    inset 0 1px 0 rgba(255, 255, 255, 0.7);
                border: 1px solid rgba(255, 255, 255, 0.6);
                transition: all 0.3s ease;
                position: relative;
                overflow: hidden;
            }

            #${tagId}::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: ${selectedGradient};
                opacity: 0.35;
                z-index: 0;
            }

            #${tagId}:hover {
                transform: translateY(-2px);
                box-shadow:
                    0 4px 12px rgba(31, 38, 135, 0.2),
                    inset 0 1px 0 rgba(255, 255, 255, 0.9);
            }

            #${tagId}:hover::before {
                opacity: 0.5;
            }

            #${tagId} span {
                position: relative;
                z-index: 1;
            }
        </style>

        <span id="${tagId}">
            <span>${label}</span>
        </span>
        """
  }

  /**
   * Tag Group Container
   */
  def softTagGroup = { attrs, body ->
    out << """<div style="display: flex; flex-wrap: wrap; gap: 0.5rem; margin-top: 1rem;">${body()}</div>"""
  }

  /**
   * Soft Glass Progress Bar
   *
   * @attr value REQUIRED Progress value (0-100)
   * @attr gradient Progress gradient: peach, mint, lavender, sky, coral (default: peach)
   */
  def softProgressBar = { attrs ->
    def value = attrs.value ?: 0
    def gradient = attrs.gradient ?: 'peach'

    def progressId = "glass-progress-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    def gradients = [
      'peach'   : 'linear-gradient(90deg, #FFE5D4, #FFB5A7)',
      'mint'    : 'linear-gradient(90deg, #D4F4DD, #74C69D)',
      'lavender': 'linear-gradient(90deg, #E7D2FF, #B8A7F7)',
      'sky'     : 'linear-gradient(90deg, #D0E8FF, #8FB7FF)',
      'coral'   : 'linear-gradient(90deg, #FFD4D4, #FF8FAB)'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['peach']

    out << """
        <style>
            #${progressId} {
                height: 12px;
                background: rgba(255, 255, 255, 0.4);
                backdrop-filter: blur(8px);
                -webkit-backdrop-filter: blur(8px);
                border-radius: 20px;
                overflow: hidden;
                margin-top: 1rem;
                box-shadow: inset 0 2px 4px rgba(31, 38, 135, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.5);
            }

            #${progressId} .progress-fill {
                height: 100%;
                border-radius: 20px;
                background: ${selectedGradient};
                width: ${value}%;
                transition: width 1s ease;
                position: relative;
                box-shadow: 0 2px 8px rgba(31, 38, 135, 0.2);
            }

            #${progressId} .progress-fill::after {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                bottom: 0;
                right: 0;
                background: linear-gradient(
                    90deg,
                    transparent,
                    rgba(255, 255, 255, 0.5),
                    transparent
                );
                animation: progress-shine-${progressId} 2s infinite;
            }

            @keyframes progress-shine-${progressId} {
                0% { transform: translateX(-100%); }
                100% { transform: translateX(100%); }
            }
        </style>

        <div id="${progressId}">
            <div class="progress-fill"></div>
        </div>
        """
  }

  /**
   * Include Soft Glass Card Base Styles
   * Call this once in your GSP <head> section
   */
  def softStyles = { attrs ->
    out << '''
        <style>
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
                min-height: 100vh;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            }

            .glass-card-container {
                padding: 2rem 0;
            }
        </style>
        '''
  }
}
