package com.murad.app.matrimony.card

class GlassmorphicCardTagLib {
  static namespace = "glass"

  /**
   * Glassmorphic Card Component
   * Based on Murad Matrimony design styles
   *
   * @attr title Optional card title
   * @attr icon Optional icon/emoji
   * @attr animation Animation style: fadeInUp, slideIn, scaleIn, rotateIn, bounceIn (default: fadeInUp)
   * @attr delay Animation delay in seconds (default: 0)
   * @attr gradient Background gradient overlay: pink, blue, orange, purple, green (default: none)
   * @attr hoverEffect Hover effect: lift, glow, tilt, pulse (default: lift)
   * @attr padding Card padding: sm, md, lg (default: md)
   * @attr textAlign Text alignment: left, center, right (default: left)
   */
  def card = { attrs, body ->
    def title = attrs.title ?: ''
    def icon = attrs.icon ?: ''
    def animation = attrs.animation ?: 'fadeInUp'
    def delay = attrs.delay ?: '0'
    def gradient = attrs.gradient ?: 'none'
    def hoverEffect = attrs.hoverEffect ?: 'lift'
    def padding = attrs.padding ?: 'md'
    def textAlign = attrs.textAlign ?: 'left'

    // Unique ID for this card
    def cardId = "glass-card-${System.currentTimeMillis()}-${Math.random().toString().substring(2, 8)}"

    // Gradient overlays
    def gradients = [
      'pink'  : 'linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 107, 107, 0) 100%)',
      'blue'  : 'linear-gradient(135deg, rgba(72, 219, 251, 0.1) 0%, rgba(72, 219, 251, 0) 100%)',
      'orange': 'linear-gradient(135deg, rgba(253, 203, 110, 0.1) 0%, rgba(253, 203, 110, 0) 100%)',
      'purple': 'linear-gradient(135deg, rgba(118, 75, 162, 0.1) 0%, rgba(118, 75, 162, 0) 100%)',
      'green' : 'linear-gradient(135deg, rgba(0, 255, 136, 0.1) 0%, rgba(0, 255, 136, 0) 100%)',
      'none'  : 'none'
    ]

    def selectedGradient = gradients[gradient] ?: gradients['none']

    // Padding sizes
    def paddingMap = [
      'sm': '20px',
      'md': '40px 30px',
      'lg': '60px 50px'
    ]

    def selectedPadding = paddingMap[padding] ?: paddingMap['md']

    out << """
        <style>
            #${cardId} {
                background: rgba(255, 255, 255, 0.1);
                backdrop-filter: blur(20px);
                -webkit-backdrop-filter: blur(20px);
                border-radius: 25px;
                border: 1px solid rgba(255, 255, 255, 0.2);
                padding: ${selectedPadding};
                text-align: ${textAlign};
                box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.15);
                position: relative;
                overflow: hidden;
                transition: all 0.3s ease;
            }

            /* Gradient overlay */
            ${selectedGradient != 'none' ? """
            #${cardId}::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: ${selectedGradient};
                opacity: 0;
                transition: opacity 0.3s ease;
                z-index: 0;
            }

            #${cardId}:hover::before {
                opacity: 1;
            }
            """ : ''}

            /* Content positioning */
            #${cardId} > * {
                position: relative;
                z-index: 1;
            }

            /* Title styling */
            #${cardId} .glass-card-title {
                color: white;
                font-size: 1.5rem;
                margin-bottom: 15px;
                font-weight: 600;
            }

            /* Icon styling */
            #${cardId} .glass-card-icon {
                font-size: 3rem;
                margin-bottom: 20px;
                display: inline-block;
            }

            /* Text styling */
            #${cardId} .glass-card-content {
                color: rgba(255, 255, 255, 0.85);
                font-size: 1rem;
                line-height: 1.6;
            }

            /* Animation: fadeInUp */
            ${animation == 'fadeInUp' ? """
            @keyframes fadeInUp-${cardId} {
                from {
                    opacity: 0;
                    transform: translateY(30px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            #${cardId} {
                animation: fadeInUp-${cardId} 0.8s ease-out ${delay}s backwards;
            }
            """ : ''}

            /* Animation: slideIn */
            ${animation == 'slideIn' ? """
            @keyframes slideIn-${cardId} {
                from {
                    opacity: 0;
                    transform: translateX(-50px);
                }
                to {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            #${cardId} {
                animation: slideIn-${cardId} 0.8s ease-out ${delay}s backwards;
            }
            """ : ''}

            /* Animation: scaleIn */
            ${animation == 'scaleIn' ? """
            @keyframes scaleIn-${cardId} {
                from {
                    opacity: 0;
                    transform: scale(0.8);
                }
                to {
                    opacity: 1;
                    transform: scale(1);
                }
            }

            #${cardId} {
                animation: scaleIn-${cardId} 0.6s ease-out ${delay}s backwards;
            }
            """ : ''}

            /* Animation: rotateIn */
            ${animation == 'rotateIn' ? """
            @keyframes rotateIn-${cardId} {
                from {
                    opacity: 0;
                    transform: rotate(-10deg) scale(0.9);
                }
                to {
                    opacity: 1;
                    transform: rotate(0deg) scale(1);
                }
            }

            #${cardId} {
                animation: rotateIn-${cardId} 0.8s ease-out ${delay}s backwards;
            }
            """ : ''}

            /* Animation: bounceIn */
            ${animation == 'bounceIn' ? """
            @keyframes bounceIn-${cardId} {
                0% {
                    opacity: 0;
                    transform: scale(0.3);
                }
                50% {
                    opacity: 1;
                    transform: scale(1.05);
                }
                70% {
                    transform: scale(0.9);
                }
                100% {
                    transform: scale(1);
                }
            }

            #${cardId} {
                animation: bounceIn-${cardId} 0.8s ease-out ${delay}s backwards;
            }
            """ : ''}

            /* Hover Effect: lift */
            ${hoverEffect == 'lift' ? """
            #${cardId}:hover {
                transform: translateY(-10px);
                box-shadow: 0 15px 45px 0 rgba(31, 38, 135, 0.25);
                border-color: rgba(255, 255, 255, 0.4);
            }
            """ : ''}

            /* Hover Effect: glow */
            ${hoverEffect == 'glow' ? """
            #${cardId}:hover {
                box-shadow:
                    0 15px 45px 0 rgba(31, 38, 135, 0.25),
                    0 0 30px rgba(255, 255, 255, 0.3),
                    inset 0 0 20px rgba(255, 255, 255, 0.1);
                border-color: rgba(255, 255, 255, 0.5);
            }
            """ : ''}

            /* Hover Effect: tilt */
            ${hoverEffect == 'tilt' ? """
            #${cardId} {
                transform-style: preserve-3d;
                perspective: 1000px;
            }

            #${cardId}:hover {
                transform: rotateY(5deg) rotateX(-5deg);
                box-shadow: 0 20px 50px 0 rgba(31, 38, 135, 0.3);
            }
            """ : ''}

            /* Hover Effect: pulse */
            ${hoverEffect == 'pulse' ? """
            @keyframes pulse-${cardId} {
                0%, 100% {
                    transform: scale(1);
                }
                50% {
                    transform: scale(1.05);
                }
            }

            #${cardId}:hover {
                animation: pulse-${cardId} 1s ease-in-out infinite;
                box-shadow: 0 15px 45px 0 rgba(31, 38, 135, 0.25);
            }
            """ : ''}

            /* Responsive */
            @media (max-width: 768px) {
                #${cardId} {
                    padding: 30px 20px;
                }

                #${cardId} .glass-card-title {
                    font-size: 1.3rem;
                }

                #${cardId} .glass-card-icon {
                    font-size: 2.5rem;
                }
            }
        </style>

        <div id="${cardId}" class="glassmorphic-card">
            ${icon ? """<div class="glass-card-icon">${icon}</div>""" : ''}
            ${title ? """<h3 class="glass-card-title">${title}</h3>""" : ''}
            <div class="glass-card-content">
                ${body()}
            </div>
        </div>
        """
  }

  /**
   * Include base styles (call once in <head>)
   */
  def cardStyles = { attrs ->
    out << '''
        <style>
            .glassmorphic-card * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
        </style>
        '''
  }
}
