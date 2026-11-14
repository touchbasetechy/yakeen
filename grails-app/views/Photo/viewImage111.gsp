<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>View Image - ${photo?.title ?: 'Untitled'}</title>
  <asset:stylesheet src="lightgallery.css"/>
  <asset:javascript src="lightgallery.js"/>
  <style>
  .image-container {
    max-width: 100%;
    margin: 20px auto;
    text-align: center;
  }

  .image-container img {
    max-width: 100%;
    max-height: 80vh;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }

  .image-meta {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    margin-top: 20px;
  }

  .image-actions {
    margin-top: 20px;
  }
  </style>
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
      <div class="image-viewer">
        <h1>${photo?.title ?: 'Untitled Photo'}</h1>

        <div id="lightgallery">
          <a href="${createLink(controller: 'photo', action: 'serveImage', id: params.id)}">
            <img src="${createLink(controller: 'photo', action: 'serveImage', id: params.id)}"
                 alt="${photo?.title ?: 'Photo'}">
          </a>
        </div>

        <div class="image-info">
          <div class="info-row">
            <span class="info-label">Title:</span>
            <span>${photo?.title ?: 'Untitled'}</span>
          </div>

          <div class="info-row">
            <span class="info-label">Description:</span>
            <span>${photo?.description ?: 'No description available'}</span>
          </div>

          <div class="info-row">
            <span class="info-label">File Name:</span>
            <span>${photo?.originalFilename}</span>
          </div>

          <div class="info-row">
            <span class="info-label">File Size:</span>
            <span>${photo?.fileSize ? (photo.fileSize / 1024).toInteger() + ' KB' : 'Unknown'}</span>
          </div>

          <div class="info-row">
            <span class="info-label">Uploaded:</span>
            <span><g:formatDate date="${photo?.dateCreated}" format="yyyy-MM-dd HH:mm"/></span>
          </div>
        </div>

        <div class="action-buttons" style="margin-top: 20px;">
          <g:link controller="photo" action="index" class="btn btn-primary">
            <i class="fas fa-images"></i> Gallery
          </g:link>
          <g:link controller="photo" action="download" id="${photo?.id}" class="btn btn-success">
            <i class="fas fa-download"></i> Download Original
          </g:link>
          <button onclick="window.history.back()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Back
          </button>
        </div>
      </div>

    </section>
  </div>
</div>
<script>
  document.addEventListener('DOMContentLoaded', function () {
    lightGallery(document.getElementById('lightgallery'), {
      selector: 'a',
      download: false,
      zoom: true,
      share: false
    });
  });
</script>
</body>
</html>
