package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Transactional
class PhotoService {

  def grailsApplication

  def uploadFile(MultipartFile file) {
    if (file.empty) {
      flash.error = "Please select a file"
      redirect(action: 'upload')
      return
    }

    // Validate file type
    def allowedTypes = ['image/jpeg', 'image/png']
    if (!allowedTypes.contains(file.contentType)) {
      flash.error = "Only JPEG, PNG files are allowed"
      redirect(action: 'upload')
      return
    }

    // Validate file size (5MB max)
    if (file.size > 512 * 1024 * 1024) {
      flash.error = "File size must be less than 512MB"
      redirect(action: 'upload')
      return
    }

    // Sanitize filename
    String sanitizedFilename = file.originalFilename.replaceAll(/[^a-zA-Z0-9\.\-]/, '_')

    try {
      String uploadDir = grailsApplication.config.getProperty('app.upload.dir', String, 'uploads')
      String uploadPath = "/${sanitizedFilename}"
      Path uploadPath1 = Paths.get(uploadDir)
      // Create directory if it doesn't exist
      if (!Files.exists(uploadPath1)) {
        Files.createDirectories(uploadPath1)
      }
      println(sanitizedFilename)
      println(uploadDir)
      println(uploadPath)
      println(uploadPath1)
      file.transferTo(new File(uploadPath1))
      flash.message = "File uploaded successfully"
    } catch (Exception e) {
      flash.error = "Error uploading file: ${e.message}"
    }

    redirect(action: 'index')
  }

  Photo savePhoto(MultipartFile file, String title, String description) {
    // Create upload directory if it doesn't exist
    def uploadDir = grailsApplication.config.getProperty('app.upload.dir', String, 'uploads')
    def uploadPath = Paths.get(uploadDir)
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath)
    }

    // Save original file
    String originalFilename = file.originalFilename
    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'))
    String uniqueFilename = "${System.currentTimeMillis()}${fileExtension}"
    Path filePath = uploadPath.resolve(uniqueFilename)
    file.transferTo(filePath.toFile())

    // Generate thumbnail
    String thumbnailFilename = "thumb_${uniqueFilename}"
    Path thumbnailPath = uploadPath.resolve(thumbnailFilename)
    generateThumbnail(filePath.toFile(), thumbnailPath.toFile(), 200)

    // Save to database
    Photo photo = new Photo(
      title: title,
      description: description,
      originalFilename: originalFilename,
      filePath: filePath.toString(),
      thumbnailPath: thumbnailPath.toString()
    ).save(failOnError: true)

    return photo
  }

  private void generateThumbnail(File source, File target, int width) {
    BufferedImage originalImage = ImageIO.read(source)
    BufferedImage thumbnail = Scalr.resize(originalImage, width)
    ImageIO.write(thumbnail, "jpg", target)
  }

  List<Photo> listPhotos() {
    Photo.listOrderByDateCreated(order: 'desc')
  }

  void deletePhoto(Photo photo) {
    // Delete files
    Files.deleteIfExists(Paths.get(photo.filePath))
    if (photo.thumbnailPath) {
      Files.deleteIfExists(Paths.get(photo.thumbnailPath))
    }
    // Delete from database
    photo.delete()
  }
}
