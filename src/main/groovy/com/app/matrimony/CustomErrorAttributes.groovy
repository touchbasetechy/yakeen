package com.app.matrimony

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class CustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options)

    // Add custom attributes
    errorAttributes.put('application', 'Yakeen Matrimony')
    errorAttributes.put('supportContact', 'support@yakeen.in')

    // Remove sensitive information in production
    if (grails.util.Environment.current == grails.util.Environment.PRODUCTION) {
      errorAttributes.remove('exception')
      errorAttributes.remove('trace')
    }

    return errorAttributes
  }
}
