<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Photo Gallery</title>
  <asset:javascript src="application.js"/>

  <!-- Glightbox CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/glightbox/dist/css/glightbox.min.css">
  <style>
  .gallery-title {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
  }

  .photo-card {
    position: relative; /* Add this */
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
  }

  .photo-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
  }

  .photo-img-container {
    height: 200px;
    overflow: hidden;
    position: relative;
    z-index: 1;
  }

  .photo-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .photo-card:hover .photo-img {
    transform: scale(1.05);
  }

  .photo-info {
    position: absolute;
    bottom: -100%; /* Start completely hidden */
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    padding: 15px;
    transition: transform 0.3s ease;
    backdrop-filter: blur(5px);
    z-index: 2;
    transform-origin: bottom center;
  }


  .photo-title {
    font-weight: bold;
    margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .photo-description {
    color: #666;
    font-size: 0.9em;
    height: 2em;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
  }

  /* Photo Info Panel - initially hidden */
  .photo-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.5); /* Black with 70% opacity */
    color: white;
    padding: 15px;
    backdrop-filter: blur(5px); /* Optional frosted glass effect */
  }

  /* Title styles */
  .photo-info h4 {
    margin: 0 0 5px 0;
    font-size: 16px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  /* Description styles */
  .photo-info p {
    margin: 0;
    font-size: 14px;
    display: -webkit-box;
    -webkit-line-clamp: 2; /* Limit to 2 lines */
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .photo-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
  }

  .photo-actions {
    display: flex;
    gap: 10px;
  }

  .view-btn, .download-btn {
    color: white;
    font-size: 18px;
    transition: all 0.3s ease;
    padding: 5px;
    tabindex: -1;
  }

  .view-btn:hover {
    color: #06e70f; /* Green color for view */
    transform: scale(1.2);
  }

  .download-btn:hover {
    color: #0f72fa; /* Blue color for download */
    transform: scale(1.2);
  }

  /* Ensure lightbox works with view icon */
  .view-btn[data-gallery] {
    text-decoration: none;


  }

  /* Custom styles for our controls */
  .glightbox-clean .gslide-description {
    background: linear-gradient(to right, rgb(0, 0, 0, 0.9), rgb(0, 0, 0, 0.4)) !important;
    color: white !important; /* Ensure text is readable */
  }

  .glightbox-clean .gslide-title {
    color: white !important; /* Green color */
  }

  .glightbox-controls {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 10px;
    z-index: 9999;
    background: transparent !important;
    /*background: rgba(0, 0, 0, 0.3);*/
    padding: 10px;
    border-radius: 25px; /* More rounded for modern look */
    backdrop-filter: blur(10px); /* Enhanced blur effect */
  }

  .glightbox-controls button {
    background: inherit;
    backdrop-filter: blur(5px);
    color: white;
    border: none;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
  }

  .glightbox-controls button:hover {
    /*background: rgba(0, 0, 0, 0.5);*/
    background: linear-gradient(to right, #333, #555);
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(5px);
    /*color: aquamarine;*/
    /*box-shadow: 0 0 10px white;*/
  }

  .glightbox-controls button i {
    font-size: 18px;
  }

  /* Style for transformed images */
  .glightbox-container img.glightbox-transformed {
    transition: transform 0.3s ease;
  }

  /* Adjust main lightbox content to accommodate sidebar */
  .glightbox-open .gslide-media {
    margin-right: 0 !important;
  }

  .glightbox-open .gslide-description {
    margin-right: 140px !important;
  }

  /* Navigation arrows positioning adjustment */
  .gslide-navigation .gslide-arrow.gslide-next {
    right: 150px !important;
  }

  /* Responsive design */
  @media (max-width: 768px) {

    .glightbox-open .gslide-media {
      margin-right: 90px !important;
    }

    .glightbox-open .gslide-description {
      margin-right: 90px !important;
    }

    .gslide-navigation .gslide-arrow.gslide-next {
      right: 100px !important;
    }
  }

  /* Hide thumbnail sidebar on very small screens */
  @media (max-width: 480px) {
    .glightbox-open .gslide-media {
      margin-right: 0 !important;
    }

    .glightbox-open .gslide-description {
      margin-right: 0 !important;
    }

    .gslide-navigation .gslide-arrow.gslide-next {
      right: 15px !important;
    }
  }

  /* Replace the sidebar styles with floating toolbar styles */
  .glightbox-floating-toolbar {
    position: fixed;
    top: 20px;
    right: 20px;
    background: rgba(0, 0, 0, 0.8);
    backdrop-filter: blur(15px);
    border-radius: 12px;
    padding: 10px;
    z-index: 99998;
    transform: translateY(-100px);
    opacity: 0;
    transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(255, 255, 255, 0.1);
    max-width: 300px;
  }

  /* Show toolbar when lightbox is open */
  .glightbox-open .glightbox-floating-toolbar {
    transform: translateY(0);
    opacity: 1;
  }

  /* Toolbar header */
  .toolbar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .toolbar-title {
    color: white;
    font-size: 14px;
    font-weight: 600;
    margin: 0;
  }

  .toolbar-counter {
    color: #007acc;
    font-size: 12px;
    background: rgba(0, 122, 204, 0.2);
    padding: 2px 8px;
    border-radius: 10px;
  }

  .toolbar-close {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    transition: background-color 0.2s;
  }

  .toolbar-close:hover {
    background: rgba(255, 255, 255, 0.1);
  }

  /* Thumbnail container */
  .toolbar-thumbnails {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(60px, 1fr));
    gap: 8px;
    max-height: 200px;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.3) transparent;
  }

  /* Custom scrollbar for webkit browsers */
  .toolbar-thumbnails::-webkit-scrollbar {
    width: 4px;
  }

  .toolbar-thumbnails::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 2px;
  }

  .toolbar-thumbnails::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 2px;
  }

  .toolbar-thumbnails::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.5);
  }

  /* Individual thumbnail items */
  .toolbar-thumbnail {
    position: relative;
    width: 90px;
    height: 55px;
    margin-left: 10px;
    border-radius: 6px;
    border: 2px solid transparent;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    opacity: 0.6;
  }

  .toolbar-thumbnail:hover {
    opacity: 0.9;
    transform: scale(1.05);
    border-color: rgba(255, 255, 255, 0.3);
  }

  .toolbar-thumbnail.active {
    opacity: 1;
    border-color: #007acc;
    transform: scale(1.1);
    box-shadow: 0 0 15px rgba(0, 122, 204, 0.5);
  }

  .toolbar-thumbnail img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .toolbar-thumbnail .thumb-number {
    position: absolute;
    top: 2px;
    left: 2px;
    background: rgba(0, 0, 0, 0.8);
    color: white;
    font-size: 9px;
    padding: 1px 4px;
    border-radius: 3px;
    font-weight: bold;
    line-height: 1;
  }

  /* Toolbar actions */
  .toolbar-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
  }

  .toolbar-nav-buttons {
    display: flex;
    gap: 8px;
  }

  .toolbar-nav-button {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    color: white;
    width: 32px;
    height: 32px;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    font-size: 12px;
  }

  .toolbar-nav-button:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-1px);
  }

  .toolbar-nav-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .toolbar-toggle {
    background: rgba(0, 122, 204, 0.3);
    border: none;
    color: white;
    padding: 4px 8px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 11px;
    transition: all 0.2s;
  }

  .toolbar-toggle:hover {
    background: rgba(0, 122, 204, 0.5);
  }

  /* Minimized state */
  .glightbox-floating-toolbar.minimized {
    transform: translateY(-100%) translateX(50%);
    right: 50%;
    top: 50px;
    padding: 8px 12px;
    border-radius: 20px;
    max-width: none;
    width: auto;
  }

  .glightbox-floating-toolbar.minimized .toolbar-content {
    display: none;
  }

  .glightbox-floating-toolbar.minimized .toolbar-minimized {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .toolbar-minimized {
    display: none;
  }

  .toolbar-expand {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  /* Responsive design */
  @media (max-width: 768px) {
    .glightbox-floating-toolbar {
      top: 10px;
      right: 10px;
      left: 10px;
      max-width: none;
    }

    .toolbar-thumbnails {
      grid-template-columns: repeat(auto-fit, minmax(50px, 1fr));
      max-height: 150px;
    }

    .toolbar-thumbnail {
      width: 50px;
      height: 38px;
    }

    .toolbar-thumbnail .thumb-number {
      font-size: 8px;
      padding: 1px 3px;
    }
  }

  @media (max-width: 480px) {
    .glightbox-floating-toolbar {
      top: 60px; /* Account for mobile browsers */
    }

    .toolbar-thumbnails {
      grid-template-columns: repeat(4, 1fr);
      max-height: 120px;
    }
  }

  /* Animation for thumbnail loading */
  .toolbar-thumbnail.loading {
    background: rgba(255, 255, 255, 0.1);
  }

  .toolbar-thumbnail.loading::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 16px;
    height: 16px;
    margin: -8px;
    border: 2px solid transparent;
    border-top: 2px solid #007acc;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  /* Remove the old sidebar styles - these would be deleted from your original CSS */
  .glightbox-thumbnail-sidebar {
    display: none !important;
  }

  /* Adjust main lightbox content - remove sidebar margin adjustments */
  .glightbox-open .gslide-media {
    margin-right: 0 !important;
  }

  .glightbox-open .gslide-description {
    margin-right: 0 !important;
  }

  .gslide-navigation .gslide-arrow.gslide-next {
    right: 15px !important;
  }

  /* Progress bar styles */
  .glightbox-progress-container {
    position: fixed;
    bottom: 80px;
    left: 50%;
    transform: translateX(-50%);
    width: 80%;
    max-width: 500px;
    height: 6px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 3px;
    z-index: 9999;
    display: none;
  }

  .glightbox-progress-bar {
    height: 100%;
    width: 0%;
    background: linear-gradient(to right, #4facfe, #00f2fe);
    border-radius: 3px;
    transition: width 0.3s ease;
  }

  .glightbox-progress-text {
    position: absolute;
    right: -40px;
    top: 50%;
    transform: translateY(-50%);
    color: white;
    font-size: 12px;
  }

  /* Add this to your CSS */
  @keyframes progressPulse {
    0% {
      opacity: 0.8;
    }
    50% {
      opacity: 1;
    }
    100% {
      opacity: 0.8;
    }
  }

  .glightbox-progress-container.uploading {
    animation: progressPulse 1.5s infinite;
  }

  /* Method 1: Force horizontal centering */
  .glightbox-container .gslide-inner-content {
    justify-content: center !important;
    align-items: center !important;
    display: flex !important;
  }

  .glightbox-container .gslide-image {
    margin: 0 auto !important;
    display: block !important;
  }

  /* Method 2: Alternative approach */
  .glightbox-container .gslide {
    display: flex !important;
    justify-content: center !important;
    align-items: center !important;
  }

  .glightbox-container .gslide-inner-content {
    width: 100% !important;
    text-align: center !important;
  }

  /* Method 3: For responsive centering */
  .glightbox-container .gslide-media {
    position: relative !important;
    left: 50% !important;
    transform: translateX(-50%) !important;
  }

  /* Force description to show */
  .glightbox-container .gslide-title,
  .glightbox-container .gslide-desc {
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
  }

  .glightbox-container .gslide-description {
    position: absolute !important;
    bottom: 0 !important;
    left: 0 !important;
    right: 0 !important;
    /*background: linear-gradient(transparent, rgba(0, 0, 0, 0.8)) !important;*/
    background: transparent !important;
    padding: 40px 20px 20px !important;
    color: white !important;
    text-align: center !important;
    z-index: 1000 !important;
  }

  .glightbox-container .gdesc-inner {
    max-width: 800px !important;
    margin: 0 auto !important;
  }

  /*floating upload button*/
  .floating-upload-btn {
    position: fixed;
    bottom: 30px;
    right: 30px;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    border-radius: 50%;
    color: white;
    font-size: 24px;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    text-decoration: none;
  }

  .floating-upload-btn:hover {
    transform: scale(1.1);
    box-shadow: 0 6px 25px rgba(0, 0, 0, 0.4);
    color: white;
    text-decoration: none;
  }

  @media (max-width: 768px) {
    .floating-upload-btn {
      bottom: 20px;
      right: 20px;
      width: 50px;
      height: 50px;
      font-size: 20px;
    }
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

<div id="content" role="main" aria-hidden="false">
  <div class="container">
    <section class="row">
      <div class="gallery-container">
        <h1 class="gallery-title">Gallery</h1>

        <g:each in="${groupedPhotos.sort { it.key }}" var="group">
          <div class="category-group">
            <h2 class="category-title">${group.key}</h2>

            <div class="overflow-auto">
              <div class="flex-container">
                <g:each in="${group.value.sort { it.title }}" var="photo">
                  <div class="photo-card flex-item">
                    <div class="photo-img-container">
                      <img src="${createLink(controller: 'photo', action: 'serveImage', id: photo.id)}"
                           alt="${photo.title}"
                           class="photo-img"/>
                    </div>

                    <div class="photo-info">
                      <h4>${photo.title.encodeAsHTML()}</h4>
                      <g:if test="${photo.description}">
                        <p class="photo-description">${photo.description.encodeAsHTML()}</p>
                      </g:if>
                      <g:else>
                        <p class="photo-description">No description</p>
                      </g:else>

                      <div class="photo-meta">
                        <div class="file-info">
                          <p class="file-name">${photo.originalFilename.encodeAsHTML()}</p>
                          <span class="file-size" data-size="${photo.fileSize}"></span>
                        </div>

                        <div class="photo-actions">

                          <a href="#"
                             class="view-btn glightbox"
                             title="View full size"
                             data-id=photo.id
                             data-title="${photo.originalFilename.encodeAsHTML()}"
                             data-description="${photo.description.encodeAsHTML()}"
                             data-size="${photo.fileSize}"
                             data-type="image"
                             data-href="${createLink(controller: 'photo', action: 'serveImage', id: photo.id)}">
                            <i class="fas fa-eye"></i>
                          </a>
                          <a href="${createLink(controller: 'photo', action: 'download', id: photo.id)}"
                             class="download-btn"
                             title="Download">
                            <i class="fas fa-download"></i>
                          </a>
                        </div>
                      </div>
                      <!-- This will be populated by JavaScript -->
                    </p>
                    </div>
                  </div>
                </g:each>
              </div>
            </div>
          </div>
        </g:each>
      </div>

      <!-- Floating Toolbar Template -->
      <div class="glightbox-floating-toolbar" id="floatingToolbar">
        <div class="toolbar-content">
          <div class="toolbar-header">
            <h4 class="toolbar-title">Gallery</h4>
            <span class="toolbar-counter" id="toolbarCounter">1 / 6</span>
            <button class="toolbar-close" id="toolbarClose">
              <i class="fas fa-times"></i>
            </button>
          </div>

          <div class="toolbar-thumbnails" id="toolbarThumbnails">
            <!-- Thumbnails will be dynamically generated here -->
          </div>

          <div class="toolbar-actions">
            <div class="toolbar-nav-buttons">
              <button class="toolbar-nav-button" id="toolbarPrev" title="Previous">
                <i class="fas fa-chevron-left"></i>
              </button>
              <button class="toolbar-nav-button" id="toolbarNext" title="Next">
                <i class="fas fa-chevron-right"></i>
              </button>
            </div>
            <button class="toolbar-toggle" id="toolbarMinimize">
              Minimize
            </button>
          </div>
        </div>

        <div class="toolbar-minimized">
          <button class="toolbar-expand" id="toolbarExpand">
            <i class="fas fa-images"></i>
            <span id="minimizedCounter">1/6</span>
          </button>
        </div>
      </div>
    </section>
  </div>
</div>

<!-- Controls template that will be injected into Glightbox -->
<div id="glightbox-controls-template" style="display: none;" aria-hidden="false">
  <div class="glightbox-controls">
    <button class="flip-horizontal" title="Flip Horizontal" type="button"><i class="fas fa-arrows-alt-h"></i></button>
    <button class="flip-vertical" title="Flip Vertical" type="button"><i class="fas fa-arrows-alt-v"></i></button>
    <button class="zoom-in" title="Zoom In" type="button"><i class="fas fa-search-plus"></i></button>
    <button class="zoom-out" title="Zoom Out" type="button"><i class="fas fa-search-minus"></i></button>
    <button class="rotate-left" title="Rotate Left" type="button"><i class="fas fa-undo"></i></button>
    <button class="rotate-right" title="Rotate Right" type="button"><i class="fas fa-redo"></i></button>
    <button class="reset-transform" title="Reset" type="button"><i class="fas fa-sync-alt"></i></button>
    <button class="download-btn" title="Download" type="button"><i class="fas fa-download"></i></button>
    <!-- Add this progress container -->
    <div class="glightbox-progress-container" id="downloadProgressContainer">
      <div class="glightbox-progress-bar" id="downloadProgressBar"></div>
      <span class="glightbox-progress-text" id="downloadProgressText">0%</span>
    </div>
  </div>

</div>
<script src="https://cdn.jsdelivr.net/npm/glightbox/dist/js/glightbox.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/hammer.js/2.0.8/hammer.min.js"></script>
<script type="text/javascript">
  // Transformation state
  let transformState = {
    scale: 1,
    rotation: 0,
    flipH: 1,
    flipV: 1
  };
  let photoId
  let currentIndex
  let lightbox
  document.addEventListener('DOMContentLoaded', function () {
    // Initialize Glightbox
    lightbox = GLightbox({
      selector: '.glightbox',
      touchNavigation: true,
      keyboardNavigation: true,
      loop: true,
      openEffect: 'zoom',
      closeEffect: 'zoom',
      closeOnOutsideClick: false, // Add this to prevent accidental closes
      // This might help prevent the aria-hidden issue
      plyr: {
        config: {
          hideControls: false
        }
      },
      onOpen: () => {
        // Add our controls when lightbox opens
        const controlsTemplate = document.getElementById('glightbox-controls-template').cloneNode(true);
        controlsTemplate.style.display = 'block';
        const lightBoxContainer = document.querySelector('.glightbox-container');
        lightBoxContainer.appendChild(controlsTemplate);

        const thumbnailSlider = document.querySelector("#floatingToolbar");
        lightBoxContainer.appendChild(thumbnailSlider);

        initImageTransformations();

        window.floatingToolbar.onLightboxOpen();
        // window.glightboxThumbnails.onLightboxOpen();
      },
      onClose: () => window.floatingToolbar.onLightboxClose()
      // window.glightboxThumbnails.onLightboxClose(),
    });

    lightbox.on('slide_changed', (data) => {
      currentIndex = lightbox.index + 1;

      initImageTransformations();
      window.floatingToolbar.onSlideChange(data);
      //  window.glightboxThumbnails.onSlideChange(data);
    });

    // window.glightboxThumbnails = new GLightboxWithThumbnails(lightbox);
    window.floatingToolbar = new GLightboxFloatingToolbar(lightbox);

    // Initialize image transformations
    function initImageTransformations() {
      const imgContainer = lightbox.getActiveSlide();
      if (!imgContainer) return;

      const img = imgContainer.querySelector('img');
      if (!img) return;

      photoId = img.src.split('/').pop();
      // Add our transformed class
      img.classList.add('glightbox-transformed');

      // Reset any existing transformations
      img.style.transform = '';

      transformState = {
        scale: 1,
        rotation: 0,
        flipH: 1,
        flipV: 1
      };

      // Get controls container
      const controls = document.querySelector('.glightbox-container .glightbox-controls');
      if (!controls) return;

      // Set up event listeners for controls with proper event handling
      const buttons = {
        '.flip-horizontal': () => applyTransform(img, 'flipH'),
        '.flip-vertical': () => applyTransform(img, 'flipV'),
        '.zoom-in': () => applyTransform(img, 'zoomIn'),
        '.zoom-out': () => applyTransform(img, 'zoomOut'),
        '.rotate-left': () => applyTransform(img, 'rotateLeft'),
        '.rotate-right': () => applyTransform(img, 'rotateRight'),
        '.reset-transform': () => resetTransform(img),
        '.download-btn': () => downloadPhoto()
      };

      // Add event listeners with proper event handling
      Object.keys(buttons).forEach(selector => {
        const button = controls.querySelector(selector);
        if (button) {
          // Remove any existing listeners
          button.replaceWith(button.cloneNode(true));
          const newButton = controls.querySelector(selector);

          newButton.addEventListener('click', function (e) {
            // Prevent event from bubbling up
            e.preventDefault();
            e.stopPropagation();
            e.stopImmediatePropagation();

            // Execute the transformation
            buttons[selector]();

            // Prevent default action
            return false;
          }, true); // Use capture phase
        }
      });
    }

    // Apply transformations to image
    function applyTransform(img, transformType) {
      switch (transformType) {
        case 'flipH':
          transformState.flipH *= -1;
          break;
        case 'flipV':
          transformState.flipV *= -1;
          break;
        case 'zoomIn':
          transformState.scale *= 1.2;
          break;
        case 'zoomOut':
          transformState.scale *= 0.8;
          break;
        case 'rotateLeft':
          transformState.rotation -= 90;
          break;
        case 'rotateRight':
          transformState.rotation += 90;
          break;
      }

      // Apply all transformations
      img.style.transform = "scale(" + transformState.scale + ") " +
        "rotate(" + transformState.rotation + "deg) " +
        "scaleX(" + transformState.flipH + ") " +
        "scaleY(" + transformState.flipV + ")";
    }

    function resetTransform(img) {
      transformState.scale = 1;
      transformState.rotation = 0;
      transformState.flipH = 1;
      transformState.flipV = 1;
      initImageTransformations();
    }

    async function downloadPhoto() {
      try {
        const progressContainer = document.getElementById('downloadProgressContainer');
        const progressBar = document.getElementById('downloadProgressBar');
        const progressText = document.getElementById('downloadProgressText');

        progressContainer.style.display = 'block';
        progressBar.style.width = '0%';
        progressText.textContent = '0%';

        // Add uploading class if we can't determine content length
        progressContainer.classList.add('uploading');
        // Make request to Grails controller
        const response = await fetch('/photo/downloadImage?id=' + photoId, {
          method: 'GET',
          headers: {
            'Accept': 'application/octet-stream' // Important for file downloads
          }
        });

        if (!response.ok) {
          throw new Error(`
        HTTP
        error
        !status
      :
        ${response.status}`);
        }

        // Get filename from Content-Disposition header
        const contentDisposition = response.headers.get('Content-Disposition');
        const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
        const filename = filenameMatch ? filenameMatch[1] : `
        photo - ${photoId}`;

        // Convert response to blob
        const blob = await response.blob();

        // Create download link
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();

        // Cleanup
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        // Remove uploading class when done
        progressContainer.classList.remove('uploading');
      } catch (error) {
        // Also remove uploading class on error
        document.getElementById('downloadProgressContainer').classList.remove('uploading');
      }
    }

    function updateProgress(value) {
      const progressBar = document.getElementById('downloadProgressBar');

      // Update width and ARIA attributes
      progressBar.style.width = value + '%';
      progressBar.setAttribute('aria-valuenow', value);

      // Optional: Update text if you have any
      progressBar.textContent = value + '%';
    }

    // Format file sizes
    // $('.file-size').each(function () {
    //   const size = $(this).data('size');
    //   $(this).text(formatFileSize(size, true, 2)); // SI units, 2 decimal places
    // });

    // Get all photo cards
    const $photoCards = $('.photo-card');

    // Add event handlers to each card
    $photoCards.each(function () {
      const $card = $(this);
      const $photoInfo = $card.find('.photo-info');
      $photoInfo.slideUp('slow');
      // Mouse enter and focus events
      $card.on('mouseenter focus', function () {
        $photoInfo.slideDown('slow');
      });

      // Mouse leave and blur events
      $card.on('mouseleave blur', function () {
        $photoInfo.slideUp('slow');
      });
    });


  });

  class GLightboxFloatingToolbar {
    constructor(lightboxInstance) {
      this.lightbox = lightboxInstance;
      this.currentIndex = 0;
      this.totalSlides = 0;
      this.thumbnails = [];
      this.isMinimized = false;

      this.toolbar = document.getElementById('floatingToolbar');
      this.thumbnailContainer = document.getElementById('toolbarThumbnails');
      this.counter = document.getElementById('toolbarCounter');
      this.minimizedCounter = document.getElementById('minimizedCounter');

      this.initToolbar();
      this.bindEvents();
    }

    initToolbar() {
      // Get all gallery images
      const glightboxElements = document.querySelectorAll('.gallery-container img');
      this.totalSlides = glightboxElements.length;

      console.log("glightboxElements", glightboxElements);
      console.log("this.totalSlides", this.totalSlides);

      // Clear existing thumbnails
      this.thumbnailContainer.innerHTML = '';
      this.thumbnails = [];

      // Create thumbnails
      glightboxElements.forEach((img, index) => {
        const thumbnailDiv = document.createElement('div');
        thumbnailDiv.className = 'toolbar-thumbnail';
        thumbnailDiv.dataset.index = index;

        thumbnailDiv.innerHTML = "<img src=" + img.src + " alt=" + img.alt + " loading='lazy'><div class='thumb-number'>" + (index + 1) + "</div>";

        // Add click handler
        thumbnailDiv.addEventListener('click', () => {
          this.goToSlide(index);
        });

        this.thumbnailContainer.appendChild(thumbnailDiv);
        this.thumbnails.push(thumbnailDiv);
      });

      this.updateCounter();
    }

    bindEvents() {
      // Close toolbar
      document.getElementById('toolbarClose').addEventListener('click', () => {
        this.lightbox.close();
      });

      // Navigation buttons
      document.getElementById('toolbarPrev').addEventListener('click', () => {
        this.lightbox.prevSlide();
      });

      document.getElementById('toolbarNext').addEventListener('click', () => {
        this.lightbox.nextSlide();
      });

      // Minimize/expand toggle
      document.getElementById('toolbarMinimize').addEventListener('click', () => {
        this.toggleMinimized();
      });

      document.getElementById('toolbarExpand').addEventListener('click', () => {
        this.toggleMinimized();
      });

      // Keyboard navigation
      document.addEventListener('keydown', (e) => {
        if (!document.body.classList.contains('glightbox-open')) return;

        switch (e.key) {
          case 'ArrowUp':
            e.preventDefault();
            const prevIndex = (this.currentIndex - 1 + this.totalSlides) % this.totalSlides;
            this.goToSlide(prevIndex);
            break;
          case 'ArrowDown':
            e.preventDefault();
            const nextIndex = (this.currentIndex + 1) % this.totalSlides;
            this.goToSlide(nextIndex);
            break;
          case 'h':
          case 'H':
            e.preventDefault();
            this.toggleMinimized();
            break;
        }
      });
    }

    onLightboxOpen() {
      document.body.classList.add('glightbox-open');
      this.updateActiveThumbnail();
      this.scrollToActiveThumbnail();
    }

    onLightboxClose() {
      document.body.classList.remove('glightbox-open');
      this.isMinimized = false;
      this.toolbar.classList.remove('minimized');
    }

    onSlideChange(slideData) {
      this.currentIndex = slideData.current.index;
      this.updateActiveThumbnail();
      this.updateCounter();
      this.scrollToActiveThumbnail();
    }

    updateActiveThumbnail() {
      // Remove active class from all thumbnails
      this.thumbnails.forEach(thumb => thumb.classList.remove('active'));

      // Add active class to current thumbnail
      if (this.thumbnails[this.currentIndex]) {
        this.thumbnails[this.currentIndex].classList.add('active');
      }
    }

    updateCounter() {
      const current = this.currentIndex + 1;
      const total = this.totalSlides;
      this.counter.textContent = current + " / " + total;
      this.minimizedCounter.textContent = current + "/" + total;
    }

    scrollToActiveThumbnail() {
      const activeThumbnail = this.thumbnails[this.currentIndex];

      if (activeThumbnail && !this.isMinimized) {
        activeThumbnail.scrollIntoView({
          behavior: 'smooth',
          block: 'nearest',
          inline: 'center'
        });
      }
    }

    toggleMinimized() {
      this.isMinimized = !this.isMinimized;
      this.toolbar.classList.toggle('minimized', this.isMinimized);
    }

    goToSlide(index) {
      if (index >= 0 && index < this.totalSlides) {
        this.lightbox.goToSlide(index);
      }
    }
  }
</script>
<g:link controller="photo" action="upload" class="floating-upload-btn" title="Upload Images">
  <i class="fas fa-plus"></i>
</g:link>
</body>
</html>
