package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured

class PhotoController {

//  def index() {
//    // Get all photos sorted by some criteria (e.g., date created)
//    def photos = Photo.list(order: 'desc', sort: 'id')
//    [photos: photos]
//  }

  @Secured('ROLE_USER')
  def index() {
    def photos = Photo.list() // or your existing query
    def groupedPhotos = photos.groupBy { photo ->
      // Group by first letter of title (uppercase)
      photo.title?.toUpperCase()?.substring(0, 1) ?: 'Other'
    }
    [groupedPhotos: groupedPhotos]
  }

  def show(Long id) {
    def photo = Photo.get(id)
    if (!photo) {
      flash.error = "Photo not found"
      redirect(action: "index")
      return
    }
    [photo: photo]
  }

  def create() {
    [photo: new Photo(params)]
  }

  def edit(Long id) {
    def photo = Photo.get(id)
    if (!photo) {
      flash.error = "Photo not found"
      redirect(action: "index")
      return
    }
    [photo: photo]
  }

  def update() {
    def photo = Photo.get(params.id)
    if (!photo) {
      flash.error = "Photo not found"
      redirect(action: "index")
      return
    }
    photo.properties = params
    if (!photo.save(flush: true)) {
      render(view: "edit", model: [photo: photo])
      return
    }
    flash.message = "Photo updated successfully"
    redirect(action: "index")
  }

  def delete(Long id) {
    def photo = Photo.get(id)
    if (!photo) {
      flash.error = "Photo not found"
      redirect(action: "index")
      return
    }
    try {
      photo.delete(flush: true)
      flash.message = "Photo deleted successfully"
    } catch (Exception e) {
      flash.error = "Error deleting photo ${e}"
    }
    redirect(action: "index")
  }


  @Secured('ROLE_USER')
  def upload() {}

  @Transactional
  def uploadMultipleFiles() {
    try {
      // Get form data
      String title = params.galleryTitle
      String description = params.description

      // Validate required fields
      if (!title) {
        render(status: 400, contentType: "application/json") {
          [success: false, error: "Gallery title is required"]
        }
        return
      }

      // Get uploaded files
      def files = request.getFiles('files')

      // Validate files
      if (files.empty) {
        render(status: 400, contentType: "application/json") {
          [success: false, error: "Please select at least one file to upload"]
        }
        return
      }

      def savedFiles = []
      def errors = []

      // Process each file
      files.each { file ->
        if (!file.empty) {
          // Validate file type and size
          if (!['image/jpeg', 'image/png'].contains(file.contentType)) {
            errors << "File ${file.originalFilename}: Only JPG and PNG images are allowed"
            return // continue to next file
          }

          if (file.size > 5 * 1024 * 1024) {
            errors << "File ${file.originalFilename} is too large (max 10MB)"
            return // continue to next file
          }

          // Process the file
          def photo = new Photo(
            title: title,
            description: description,
            originalFilename: file.originalFilename,
            contentType: file.contentType,
            fileSize: file.size,
            fileData: file.bytes
          )

          if (photo.validate() && photo.save(flush: true)) {
            savedFiles << [id: photo.id, filename: photo.originalFilename]
          } else {
            errors << "Error saving photo ${file.originalFilename}: ${photo.errors}"
          }
        }
      }

      if (errors) {
        render(status: errors.size() == files.size() ? 400 : 207, // 207 Multi-Status if some succeeded
          contentType: "application/json") {
          [
            success   : errors.size() != files.size(),
            savedFiles: savedFiles,
            errors    : errors,
            message   : "Processed ${files.size()} file(s), ${savedFiles.size()} succeeded"
          ]
        }
      } else {
        render(status: 200, contentType: "application/json") {
          [
            success   : true,
            savedFiles: savedFiles,
            message   : "${files.size()} photo(s) uploaded successfully!"
          ]
        }
      }

    } catch (Exception e) {
      render(status: 500, contentType: "application/json") {
        [success: false, error: "Error uploading file(s): ${e.message}"]
      }
    }
  }

  def serveImage(Long id) {
    def photo = Photo.get(id)
    if (!photo) {
      response.sendError(404)
      return
    }

    // Set appropriate content type and headers
    response.contentType = photo.contentType
    response.contentLength = photo.fileSize.intValue()
    response.outputStream << photo.fileData
    response.outputStream.flush()
  }

  def download(Long id) {
    Long photoId = id

    def photo = Photo.get(photoId)
    if (!photo) {
      response.sendError(404)
      return
    }

    response.setContentType(photo.contentType)
    response.setHeader("Content-disposition", "attachment;filename=${photo.originalFilename}")
    response.outputStream << photo.fileData
    response.outputStream.flush()
  }

  def downloadImage() {
    Long photoId = params.long('id')
    def photo = Photo.get(photoId)

    if (!photo) {
      response.sendError(404)
      return
    }

    // Set headers
    response.setContentType(photo.contentType)
    response.setHeader("Content-disposition", "attachment;filename=${photo.originalFilename}")
    response.setHeader("Content-Length", photo.fileData.length.toString())
    response.setHeader("Accept-Ranges", "bytes") // Support for resumable downloads

    // Use a buffer for writing
    int bufferSize = 1024 * 16 // 16KB buffer
    byte[] buffer = new byte[bufferSize]
    ByteArrayInputStream inputStream = new ByteArrayInputStream(photo.fileData)
    OutputStream outputStream = response.outputStream

    try {
      int bytesRead
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead)
        outputStream.flush()
      }
    } catch (Exception e) {
      log.error("Download interrupted", e)
      if (!response.committed) {
        response.sendError(500, "Download interrupted")
      }
    } finally {
      try {
        inputStream.close()
        outputStream.close()
      } catch (IOException e) {
        log.warn("Error closing streams", e)
      }
    }
  }


}
