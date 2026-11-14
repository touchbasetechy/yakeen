class UrlMappings {
  static mappings = {
    // Home page
    "/"(controller: 'home', action: 'index')
    "/forgot-password"(controller: 'passwordReset', action: 'forgotPassword')
    "/reset-password"(controller: 'passwordReset', action: 'requestReset', method: 'POST')
    "/reset-password/$token"(controller: 'passwordReset', action: 'reset')
    "/update-password"(controller: 'passwordReset', action: 'updatePassword', method: 'POST')

    // Login routes - consolidate to use auth controller
    "/login/auth"(controller: "auth", action: "authenticate")
    "/sign-in"(controller: "auth", action: "login")

    // Authentication routes
    "/register"(controller: "auth", action: "register")
    "/user/activate"(controller: "PostEmail", action: "activate")

    "/u/$slot/search"(controller: 'search', action: 'index')
    "/oauth2/google/success"(controller: 'home', action: 'index')
    "/oauth2/google/failure"(controller: 'login', action: 'auth')

    // OAuth2 mappings (these are optional with the generic mapping above)
    "/oauth2/google"(controller: "OAuth2", action: "google")
    "/oauth2/callback/google"(controller: "OAuth2", action: "callback")
    "/oauth2/success"(controller: "OAuth2", action: "success")
    "/oauth2/error"(controller: "OAuth2", action: "error")

    "/oauth2/github"(controller: "OAuth2", action: "gitHub")
    "/oauth2/callback/github"(controller: "OAuth2", action: "gitHubCallback")

    // Dashboard
    //    "/dashboard"(controller: "dashboard", action: "index")

    // Profile management
    "/profile"(controller: "person", action: "profile")
    "/profile/edit"(controller: "person", action: "edit")
    "/profile/change-password"(controller: "person", action: "changePassword")

    // Admin routes
    "/admin"(controller: "admin", action: "index")
    "/admin/users"(controller: "admin", action: "index")
    "/admin/user/$id"(controller: "admin", action: "userDetails")

    "/admin/**"(controller: 'admin', action: 'index') {
      filters = ['springSecurityInterceptor']
      constraints {
        roles = ['ROLE_ADMIN']
      }
    }

    // Interest-related URLs
    "/interests"(controller: "interest", action: "index")
    "/interests/received"(controller: "interest", action: "received")
    "/interests/sent"(controller: "interest", action: "sent")
    "/interests/send/$receiverId"(controller: "interest", action: "send")
    "/interests/respond/$id"(controller: "interest", action: "respond")
    "/interests/withdraw/$id"(controller: "interest", action: "withdraw")

    // AJAX endpoints
    "/api/interests/send"(controller: "interest", action: "sendAjax")
    "/api/interests/respond"(controller: "interest", action: "respondAjax")

    // Test endpoints
    "/test/email"(controller: "login", action: "sendEmail")
    "/test/simple-email"(controller: "login", action: "sendSimpleEmail")

    // Shortlist mappings
    "/shortlist"(controller: "shortlist", action: "index")
    "/shortlist/add"(controller: "shortlist", action: "add", method: "POST")
    "/shortlist/remove"(controller: "shortlist", action: "remove", method: "POST")
    "/shortlist/check"(controller: "shortlist", action: "checkStatus")
    "/shortlist/notes"(controller: "shortlist", action: "updateNotes", method: "POST")
    "/shortlist/clear"(controller: "shortlist", action: "clear", method: "POST")
    "/shortlist/count"(controller: "shortlist", action: "count")
    "/shortlist/shortlisted-by"(controller: "shortlist", action: "shortlistedBy")

    // RESTful shortlist API
    "/api/shortlist"(resources: "shortlist", namespace: "api") {
      collection {
        "/clear"(controller: "shortlist", action: "clear", method: "DELETE")
        "/count"(controller: "shortlist", action: "count", method: "GET")
      }
      member {
        "/notes"(controller: "shortlist", action: "updateNotes", method: "PUT")
      }
    }

    // ── Chat routes ───────────────────────────────────────────────────────
    "/chat"(controller: 'chat', action: 'index')
    "/chat/room/$roomId"(controller: 'chat', action: 'room')
    "/chat/history/$roomId"(controller: 'chat', action: 'history')
    "/chat/message/$id"(controller: 'chat', action: 'deleteMessage', method: 'DELETE')

    "/addAccount"(controller: 'login', action: 'addAccount')
    // ── Multi-identity (Gmail-style concurrent login) routes ───────────────
    // Slot-scoped catch-all: any authenticated controller/action, addressed
    // per browser-tab identity. Must be declared BEFORE the generic
    // "/$controller/$action?/$id?" mapping below, since Grails matches
    // mappings in declaration order and the more specific pattern needs
    // first refusal.
    "/u/$slot/$controller/$action?/$id?(.${format})?" {
      constraints {
        slot(matches: /\d+/)
      }
    }

    "/u/$slot/logout"(controller: 'login', action: 'logoutSlot') {
      constraints { slot(matches: /\d+/) }
    }
    "/logoutAll"(controller: 'login', action: 'logoutAll', method: 'POST')

    // Default controller/action mapping (non-slot-scoped routes above still work as-is)
    "/$controller/$action?/$id?(.${format})?" {
      constraints {
        // apply constraints here
      }
    }

    // Error handling routes
    "500"(controller: "error", action: "serverError")
    "503"(controller: 'error', action: 'dbDown')
    "404"(controller: "error", action: "notFound")
    "403"(controller: "error", action: "forbidden")
    "401"(controller: "error", action: "unauthorized")

    // Catch-all for undefined URLs
    "/**"(controller: 'error', action: 'notFound')
  }
}
