<!-- grails-app/views/layouts/_navigation.gsp -->
<!-- Update your existing navigation template to include shortlist link -->

<sec:ifLoggedIn>
  <nav class="main-navigation">
    <ul class="nav-menu">
      <li>
        <g:link controller="home" action="index" class="nav-link">
          <i class="fas fa-home"></i> Home
        </g:link>
      </li>

      <li>
        <g:link controller="profile" action="search" class="nav-link">
          <i class="fas fa-search"></i> Search Profiles
        </g:link>
      </li>

      <li class="nav-shortlist">
        <g:link controller="shortlist" action="index" class="nav-link">
          <i class="fas fa-heart"></i> My Shortlist
          <span class="shortlist-count badge" style="display: none;">0</span>
        </g:link>
      </li>

      <li>
        <g:link controller="shortlist" action="shortlistedBy" class="nav-link">
          <i class="fas fa-users"></i> Interested in Me
        </g:link>
      </li>

      <li>
        <g:link controller="messages" action="index" class="nav-link">
          <i class="fas fa-envelope"></i> Messages
        </g:link>
      </li>

      <li>
        <g:link controller="profile" action="show" class="nav-link">
          <i class="fas fa-user"></i> My Profile
        </g:link>
      </li>

      <li class="dropdown">
        <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown">
          <i class="fas fa-cog"></i> Settings
        </a>
        <ul class="dropdown-menu">
          <li><g:link controller="user" action="edit">Edit Profile</g:link></li>
          <li><g:link controller="user" action="preferences">Preferences</g:link></li>
          <li><g:link controller="user" action="privacy">Privacy Settings</g:link></li>
          <li class="divider"></li>
          <li><g:link controller="logout">Logout</g:link></li>
        </ul>
      </li>
    </ul>
  </nav>
</sec:ifLoggedIn>

<!-- Add this CSS to your main layout -->
<style>
.nav-shortlist {
  position: relative;
}

.shortlist-count.badge {
  background: #dc3545;
  color: white;
  border-radius: 50%;
  padding: 2px 6px;
  font-size: 10px;
  font-weight: bold;
  position: absolute;
  top: -5px;
  right: -8px;
  min-width: 16px;
  text-align: center;
  line-height: 1.2;
}

.nav-menu {
  list-style: none;
  display: flex;
  gap: 20px;
  margin: 0;
  padding: 0;
}

.nav-link {
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.nav-link:hover {
  background-color: #f8f9fa;
  text-decoration: none;
}

.nav-link i {
  width: 16px;
  text-align: center;
}
</style>
