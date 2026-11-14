package com.murad.app.matrimony.button

class GlassmorphicButtonTagLib {
  static namespace = "glass"

  /**
   * Glassmorphic Button Component
   * Based on Murad Matrimony design styles
   *
   * @attr label REQUIRED Button text
   * @attr icon Optional icon/emoji (displayed before label)
   * @attr href Optional link URL
   * @attr onclick Optional JavaScript onclick handler
   * @attr type Button type: button, submit, reset (default: button)
   * @attr style Button style: primary, secondary, glass (default: primary)
   * @attr animation Entry animation: slideInLeft, slideInRight, fadeIn, zoomIn, flipIn (default: fadeIn)
   * @attr hoverEffect Hover effect: lift, glow, ripple, shake, bounce (default: lift)
   * @attr size Button size: sm, md, lg (default: md)
   * @attr fullWidth Full width button: true, false (default: false)
   */
  def button = { attrs ->
    def label = attrs.label ?: 'Button'
    def icon = attrs.icon ?: ''
    def href = attrs.href ?: ''
    def onclick = attrs.onclick ?: ''
    def type = attrs.type ?: 'button'
    def style = attrs.style ?: 'primary'
    def animation = attrs.animation ?: 'fadeIn'
    def hoverEffect = attrs.hoverEffect ?: 'lift'
    def size = attrs.size ?: 'md'
    def fullWidth = attrs.fullWidth == 'true'

    // Unique ID for this button
    def buttonId = "glass-btn-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    // Size mappings
    def sizeMap = [
      'sm': ['padding': '10px 24px', 'fontSize': '0.875rem'],
      'md': ['padding': '14px 32px', 'fontSize': '1rem'],
      'lg': ['padding': '18px 40px', 'fontSize': '1.125rem']
    ]

    def selectedSize = sizeMap[size] ?: sizeMap['md']

    // Element type
    def element = href ? 'a' : 'button'
    def typeAttr = href ? '' : "type=\"${type}\""
    def hrefAttr = href ? "href=\"${href}\"" : ''

    out << """
        <style>
            #${buttonId} {
                padding: ${selectedSize.padding};
                border-radius: 50px;
                font-size: ${selectedSize.fontSize};
                font-weight: 600;
                text-decoration: none;
                cursor: pointer;
                border: none;
                display: inline-flex;
                align-items: center;
                gap: 10px;
                position: relative;
                overflow: hidden;
                transition: all 0.3s ease;
                ${fullWidth ? 'width: 100%; justify-content: center;' : ''}
            }

            /* Style: primary */
            ${style == 'primary' ? """
            #${buttonId} {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
            }
            """ : ''}

            /* Style: secondary */
            ${style == 'secondary' ? """
            #${buttonId} {
                background: rgba(255, 255, 255, 0.2);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                border: 2px solid rgba(255, 255, 255, 0.3);
                color: white;
            }
            """ : ''}

            /* Style: glass (OAuth style) */
            ${style == 'glass' ? """
            #${buttonId} {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                -webkit-backdrop-filter: blur(10px);
                border: 1px solid rgba(255, 255, 255, 0.3);
                color: #3c4043;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            }
            """ : ''}

            /* Icon styling */
            #${buttonId} .btn-icon {
                font-size: 1.2em;
                line-height: 1;
            }

            /* Label styling */
            #${buttonId} .btn-label {
                position: relative;
                z-index: 1;
            }

            /* Animation: slideInLeft */
            ${animation == 'slideInLeft' ? """
            @keyframes slideInLeft-${buttonId} {
                from {
                    opacity: 0;
                    transform: translateX(-30px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            #${buttonId} {
                animation: slideInLeft-${buttonId} 0.6s ease-out;
            }
            """ : ''}

            /* Animation: slideInRight */
            ${animation == 'slideInRight' ? """
            @keyframes slideInRight-${buttonId} {
                from {
                    opacity: 0;
                    transform: translateX(30px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            #${buttonId} {
                animation: slideInRight-${buttonId} 0.6s ease-out;
            }
            """ : ''}

            /* Animation: fadeIn */
            ${animation == 'fadeIn' ? """
            @keyframes fadeIn-${buttonId} {
                from {
                    opacity: 0;
                }
                to {
                    opacity: 1;
                }
            }

            #${buttonId} {
                animation: fadeIn-${buttonId} 0.5s ease-out;
            }
            """ : ''}

            /* Animation: zoomIn */
            ${animation == 'zoomIn' ? """
            @keyframes zoomIn-${buttonId} {
                from {
                    opacity: 0;
                    transform: scale(0.5);
                }
                to {
                    opacity: 1;
                    transform: scale(1);
                }
            }

            #${buttonId} {
                animation: zoomIn-${buttonId} 0.5s ease-out;
            }
            """ : ''}

            /* Animation: flipIn */
            ${animation == 'flipIn' ? """
            @keyframes flipIn-${buttonId} {
                from {
                    opacity: 0;
                    transform: rotateX(-90deg);
                }
                to {
                    opacity: 1;
                    transform: rotateX(0);
                }
            }

            #${buttonId} {
                animation: flipIn-${buttonId} 0.6s ease-out;
                transform-style: preserve-3d;
            }
            """ : ''}

            /* Hover Effect: lift */
            ${hoverEffect == 'lift' ? """
            #${buttonId}:hover {
                transform: translateY(-2px);
                ${style == 'primary' ? 'box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);' : ''}
                ${style == 'secondary' ? 'background: rgba(255, 255, 255, 0.3);' : ''}
                ${style == 'glass' ? 'box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);' : ''}
            }
            """ : ''}

            /* Hover Effect: glow */
            ${hoverEffect == 'glow' ? """
            #${buttonId}:hover {
                ${style == 'primary' ? """
                box-shadow:
                    0 15px 40px rgba(102, 126, 234, 0.5),
                    0 0 20px rgba(102, 126, 234, 0.6);
                """ : ''}
                ${style == 'secondary' ? """
                box-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
                border-color: rgba(255, 255, 255, 0.5);
                """ : ''}
                ${style == 'glass' ? """
                box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15), 0 0 20px rgba(102, 126, 234, 0.3);
                """ : ''}
            }
            """ : ''}

            /* Hover Effect: ripple */
            ${hoverEffect == 'ripple' ? """
            #${buttonId}::after {
                content: '';
                position: absolute;
                top: 50%;
                left: 50%;
                width: 0;
                height: 0;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.5);
                transform: translate(-50%, -50%);
                transition: width 0.6s ease, height 0.6s ease;
            }

            #${buttonId}:hover::after {
                width: 300px;
                height: 300px;
            }

            #${buttonId}:hover {
                ${style == 'primary' ? 'box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);' : ''}
            }
            """ : ''}

            /* Hover Effect: shake */
            ${hoverEffect == 'shake' ? """
            @keyframes shake-${buttonId} {
                0%, 100% { transform: translateX(0); }
                10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
                20%, 40%, 60%, 80% { transform: translateX(5px); }
            }

            #${buttonId}:hover {
                animation: shake-${buttonId} 0.5s ease-in-out;
                ${style == 'primary' ? 'box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);' : ''}
            }
            """ : ''}

            /* Hover Effect: bounce */
            ${hoverEffect == 'bounce' ? """
            @keyframes bounce-${buttonId} {
                0%, 100% { transform: translateY(0); }
                50% { transform: translateY(-10px); }
            }

            #${buttonId}:hover {
                animation: bounce-${buttonId} 0.6s ease-in-out;
                ${style == 'primary' ? 'box-shadow: 0 15px 40px rgba(102, 126, 234, 0.5);' : ''}
            }
            """ : ''}

            /* Active state */
            #${buttonId}:active {
                transform: scale(0.98);
            }

            /* Responsive */
            @media (max-width: 768px) {
                #${buttonId} {
                    ${fullWidth ? '' : 'width: 100%;'}
                    justify-content: center;
                }
            }
        </style>

        <${element} id="${buttonId}" class="glassmorphic-button" ${typeAttr} ${hrefAttr} ${onclick ? "onclick=\"${onclick}\"" : ''}>
            ${icon ? """<span class="btn-icon">${icon}</span>""" : ''}
            <span class="btn-label">${label}</span>
        </${element}>
        """
  }

  /**
   * Include base styles (call once in <head>)
   */
  def buttonStyles = { attrs ->
    out << '''
        <style>
            .glassmorphic-button {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            }

            .glassmorphic-button:focus {
                outline: none;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
            }
        </style>
        '''
  }
}
