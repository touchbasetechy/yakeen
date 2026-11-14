package com.murad.app.matrimony.button

class GlassmorphicAdvancedCardButtonStylesTagLib {
  static namespace = "glass"

  /**
   * Include base styles for glassmorphic components (call once in <head>)
   * Provides common reset styles and font families for cards and buttons
   *
   * Usage Example:
   * <html>
   * <head>
   *     <glass:adCardButtonStyles />
   * </head>
   * <body>
   *     <glass:adCard title="Welcome">...</glass:adCard>
   *     <glass:adButton label="Click Me" />
   * </body>
   * </html>
   */
  def adCardButtonStyles = { attrs ->
    out << '''
        <style>
            /* Reset styles for glassmorphic components */
            .glassmorphic-card *,
            .glassmorphic-button {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            /* Font family for buttons */
            .glassmorphic-button {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            }

            /* Focus styles for buttons */
            .glassmorphic-button:focus {
                outline: none;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
            }
        </style>
        '''
  }
}
