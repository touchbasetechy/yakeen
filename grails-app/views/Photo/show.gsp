<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <section class="row">
      <h1>${photo.title}</h1>

      <div class="photo-detail">
        <div class="photo-container">
          <img src="${createLink(action: 'serveImage', id: photo.id)}"
               alt="${photo.title}"
               class="img-fluid"/>
        </div>

        <div class="photo-meta">
          <p><strong>Description:</strong> ${photo.description ?: 'No description'}</p>

          <p><strong>Uploaded:</strong> <g:formatDate date="${photo.dateCreated}" format="yyyy-MM-dd HH:mm"/></p>

          <p><strong>File:</strong> ${photo.originalFilename} (${photo.fileSize / 1024} KB)</p>
        </div>

        <div class="photo-actions">
          <g:link action="index" class="btn btn-primary">Back to Gallery</g:link>
        </div>
      </div>
    </section>
  </div>
</div>

</body>
</html>
