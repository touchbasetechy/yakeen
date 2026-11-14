package spring

import com.app.matrimony.CustomSecurityDebugFilter
import com.app.matrimony.JwtAuthenticationFilter
import com.app.matrimony.StartupDataListener
import com.murad.app.matrimony.*
import com.murad.app.matrimony.config.WebSocketConfig
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

beans = {
  dbConnectivityStartupChecker(DbConnectivityStartupChecker) {
    dataSource = ref('dataSource')
  }

  dbHealthInterceptor(DbHealthInterceptor) {
    dbConnectivityStartupChecker = ref('dbConnectivityStartupChecker')
  }

  fallbackServerCleanupListener(FallbackServerCleanupListener)

  startupDataListener(StartupDataListener)
  exceptionHandler(GlobalExceptionHandler)
  customSecurityDebugFilter(CustomSecurityDebugFilter)
  webSocketConfig(WebSocketConfig)
  userDetailsService(UserService)

  jwtTokenService(JwtTokenService) {
    grailsApplication = ref('grailsApplication')
  }

  passwordEncoder(BCryptPasswordEncoder, 12)

  jwtAuthenticationFilter(JwtAuthenticationFilter) {
    jwtTokenService = ref('jwtTokenService')
  }

  jwtFilterRegistration(FilterRegistrationBean) {
    filter = ref('jwtAuthenticationFilter')
    urlPatterns = ['/api/*']
    order = 10
    name = 'jwtAuthenticationFilter'
  }

  // Register the custom logout handler bean — the plugin wires it in via handlerNames config
  signInLogLogoutHandler(SignInLogLogoutHandler)
}
