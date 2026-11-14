<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Upload files</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <style>
  .upload-container {
    margin: 30px auto;
    padding: 20px;
    border-radius: 8px;
  }

  .drop-zone {
    border: 2px dashed #9298F5;
    border-radius: 5px;
    padding: 30px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    margin-bottom: 20px;
    background-color: #D1D3FB;
  }

  .drop-zone:hover {
    border-color: #7D85F3;
    background-color: #BCBFF9;
  }

  .drop-zone.active {
    border-color: #6871F1;
    background-color: #A7ACF7;
  }

  .file-list {
    margin-top: 20px;
  }

  .file-item {
    display: flex;
    align-items: center;
    padding: 10px;
    border: 1px solid #dee2e6;
    border-radius: 5px;
    margin-bottom: 10px;
    background: #fff;
  }

  .file-info {
    flex-grow: 1;
    margin-left: 10px;
    overflow: hidden;
  }

  .file-name {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .file-size {
    color: #6c757d;
    font-size: 0.85rem;
  }

  .progress {
    height: 5px;
    margin-top: 5px;
  }

  .remove-btn {
    color: #dc3545;
    cursor: pointer;
    margin-left: 10px;
  }

  .preview-img {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 4px;
  }

  .input-container {
    position: relative;
    margin-bottom: 1rem;
  }

  .input-container input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ced4da;
    border-radius: 4px;
  }

  .input-container label {
    position: absolute;
    top: -10px;
    left: 10px;
    background: white;
    padding: 0 5px;
    font-size: 0.8rem;
    color: #495057;
  }
  </style>
</head>

<body>
<div class="container">
  <div class="upload-container">
    <h2 class="text-center mb-4">Upload Photos</h2>

    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>


    <div id="successAlert" class="alert alert-success alert-dismissible fade show d-none">
      <span id="successMessage"></span>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>

    <div id="errorAlert" class="alert alert-danger alert-dismissible fade show d-none">
      <span id="errorMessage"></span>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>

    <form id="uploadForm" action="${createLink(controller: 'photo', action: 'uploadMultipleFiles')}"
          method="post" enctype="multipart/form-data" class="needs-validation" novalidate>
      <section class="row form-container">
        <div class="mb-3">
          <div class="input-container">
            <input type="text" class="form-control" id="galleryTitle" name="galleryTitle" placeholder="" value=""
                   autofocus required>
            <label for="galleryTitle" class="form-label">Gallery Title</label>
          </div>

          <div class="invalid-feedback">
            Please provide a title for your gallery.
          </div>
        </div>
      </section>

      <div class="mb-3">
        <label for="galleryDescription" class="form-label">Description</label>
        <textarea class="form-control" id="galleryDescription" name="description" rows="3"></textarea>
      </div>

      <div class="drop-zone" id="dropZone">
        <i class="fas fa-cloud-upload-alt fa-3x mb-3 text-muted"></i>
        <h5>Drag & Drop files here</h5>

        <p class="text-muted">or click to browse (Supports JPG, PNG - Max 5MB each)</p>
        <input type="file" class="d-none" id="fileInput" name="files" multiple accept="image/*">
      </div>


      <div id="fileList" class="file-list">
      </div>

      <div class="d-grid gap-2">
        <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
          <span id="submitText">Upload Files</span>
          <span id="spinner" class="spinner-border spinner-border-sm d-none" role="status" aria-hidden="true"></span>
        </button>
      </div>
    </form>

    <div id="progressBar" style="width: 100%; background: #eee; display: none;">
      <div id="progress" style="height: 20px; width: 0%; background: #4CAF50;"></div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
  document.addEventListener('DOMContentLoaded', function () {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const fileList = document.getElementById('fileList');
    const uploadForm = document.getElementById('uploadForm');
    const submitBtn = document.getElementById('submitBtn');
    const submitText = document.getElementById('submitText');
    const spinner = document.getElementById('spinner');

    // Array to store files
    let files = [];

    // Handle drag and drop events
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
      dropZone.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
      e.preventDefault();
      e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
      dropZone.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
      dropZone.addEventListener(eventName, unhighlight, false);
    });

    function highlight() {
      dropZone.classList.add('active');
    }

    function unhighlight() {
      dropZone.classList.remove('active');
    }

    // Handle dropped files
    dropZone.addEventListener('drop', handleDrop, false);

    function handleDrop(e) {
      const dt = e.dataTransfer;
      const droppedFiles = dt.files;
      handleFiles(droppedFiles);
    }

    // Handle clicked files
    dropZone.addEventListener('click', () => {
      fileInput.click();
    });

    fileInput.addEventListener('change', function () {
      handleFiles(this.files);
    });

    // Process selected files
    function handleFiles(selectedFiles) {
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        // Validate file type
        if (!file.type.match('image.*')) {
          showError(`File '+file.name+' is not an image`);
          continue;
        }

        // Validate file size (5MB max)
        if (file.size > 5 * 1024 * 1024) {
          showError(`File '+file.name+' is too large (max 5MB)`);
          continue;
        }

        // Add to files array if not already present
        if (!files.some(f => f.name === file.name && f.size === file.size && f.lastModified === file.lastModified)) {
          files.push(file);
          renderFileItem(file);
        }
      }

      // Update submit button state
      updateSubmitButton();
    }

    // Render file item in the list
    function renderFileItem(file) {
      const fileItem = document.createElement('div');
      fileItem.className = 'file-item';
      fileItem.dataset.fileName = file.name;

      // Create preview (for images)
      const preview = document.createElement('img');
      preview.className = 'preview-img';
      preview.src = URL.createObjectURL(file);

      // File info
      const fileInfo = document.createElement('div');
      fileInfo.className = 'file-info';

      const fileName = document.createElement('div');
      fileName.className = 'file-name';
      fileName.textContent = file.name;

      const fileSize = document.createElement('div');
      fileSize.className = 'file-size';
      fileSize.textContent = formatFileSize(file.size);

      fileInfo.appendChild(fileName);
      fileInfo.appendChild(fileSize);

      // Progress bar (hidden initially)
      const progress = document.createElement('div');
      progress.className = 'progress';
      progress.innerHTML = '<div class="progress-bar" role="progressbar" style="width: 0%"></div>';

      // Remove button
      const removeBtn = document.createElement('i');
      removeBtn.className = 'fas fa-times remove-btn';
      removeBtn.addEventListener('click', () => {
        removeFile(file, fileItem);
      });

      fileItem.appendChild(preview);
      fileItem.appendChild(fileInfo);
      fileItem.appendChild(progress);
      fileItem.appendChild(removeBtn);

      fileList.appendChild(fileItem);
    }

    // Format file size
    function formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes';
      const k = 1024;
      const sizes = ['Bytes', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Remove file from list
    function removeFile(file, fileItem) {
      files = files.filter(f =>
        f.name !== file.name || f.size !== file.size || f.lastModified !== file.lastModified
      );
      fileItem.remove();
      updateSubmitButton();
    }

    // Update submit button state
    function updateSubmitButton() {
      submitBtn.disabled = files.length === 0;
    }

    // Show error message
    function showError(message) {
      const errorAlert = document.getElementById('errorAlert');
      const errorMessage = document.getElementById('errorMessage');

      errorMessage.textContent = message;
      errorAlert.classList.remove('d-none');

      // Hide after 5 seconds
      setTimeout(() => {
        errorAlert.classList.add('d-none');
      }, 5000);
    }

    // Show success message
    function showSuccess(message) {
      const successAlert = document.getElementById('successAlert');
      const successMessage = document.getElementById('successMessage');

      successMessage.textContent = message;
      successAlert.classList.remove('d-none');
    }

    // Form submission
    // Replace the form submit handler with this:
    uploadForm.addEventListener('submit', async function (e) {
      e.preventDefault();

      if (files.length === 0) {
        showError('Please select at least one file to upload');
        return;
      }

      // Validate form
      if (!this.checkValidity()) {
        e.stopPropagation();
        this.classList.add('was-validated');
        return;
      }

      // Disable submit button and show spinner
      submitBtn.disabled = true;
      submitText.textContent = 'Uploading...';
      spinner.classList.remove('d-none');

      // Create FormData
      const formData = new FormData();
      formData.append('galleryTitle', document.getElementById('galleryTitle').value);
      formData.append('description', document.getElementById('galleryDescription').value);

      // Append each file
      files.forEach((file) => {
        formData.append('files', file);
      });

      try {
        const response = await fetch(this.action, {
          method: 'POST',
          body: formData,
          // Don't set Content-Type header - let browser set it with boundary
        });

        if (response.ok) {
          const result = await response.json();
          showSuccess(result.message || "Files uploaded successfully!");
          // Clear form and files
          files = [];
          fileList.innerHTML = '';
          this.reset();
          this.classList.remove('was-validated');
        } else {
          const error = await response.json();
          showError(error.error || "Error uploading files");
        }
      } catch (error) {
        showError("Network error: " + error.message);
      } finally {
        submitText.textContent = 'Upload Files';
        spinner.classList.add('d-none');
        submitBtn.disabled = false;
      }
    });
  });
</script>
</body>
</html>
