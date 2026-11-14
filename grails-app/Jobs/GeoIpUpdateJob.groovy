import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Slf4j
class GeoIpUpdateJob {

  def grailsApplication
  def geoLocationService

  static triggers = {
    // Run every Wednesday at 3am (MaxMind updates Tuesdays/Fridays)
    cron name: 'geoIpUpdateTrigger', cronExpression: '0 0 3 ? * WED'
  }

  def execute() {
    log.info("Starting GeoLite2 database update job")

    String licenseKey = grailsApplication.config.getProperty('geoip.maxmind.licenseKey')
    String editionId = grailsApplication.config.getProperty('geoip.maxmind.editionId', String, 'GeoLite2-City')
    String downloadUrl = grailsApplication.config.getProperty('geoip.maxmind.downloadUrl')
    String dbPath = grailsApplication.config.getProperty('geoip.database.path')
    String tempPath = grailsApplication.config.getProperty('geoip.database.tempPath')

    if (!licenseKey) {
      log.error("No MaxMind license key configured, skipping GeoIP update")
      return
    }

    try {
      String fullUrl = "${downloadUrl}?edition_id=${editionId}&license_key=${licenseKey}&suffix=tar.gz"
      File tarGzFile = File.createTempFile("geolite2", ".tar.gz")
      File extractDir = File.createTempFile("geolite2extract", "")
      extractDir.delete()
      extractDir.mkdirs()

      log.info("Downloading GeoLite2 database update...")
      new URL(fullUrl).withInputStream { input ->
        tarGzFile.withOutputStream { output ->
          output << input
        }
      }

      extractTarGz(tarGzFile, extractDir)

      // MaxMind ships the .mmdb inside a versioned subfolder, e.g. GeoLite2-City_20250115/
      File mmdbFile = findMmdbFile(extractDir)
      if (!mmdbFile) {
        log.error("Could not find .mmdb file in downloaded archive")
        return
      }

      // Copy to temp path first, then atomically move into place
      File tempTarget = new File(tempPath)
      Files.copy(mmdbFile.toPath(), tempTarget.toPath(), StandardCopyOption.REPLACE_EXISTING)

      File finalTarget = new File(dbPath)
      Files.move(tempTarget.toPath(), finalTarget.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)

      log.info("GeoLite2 database updated successfully at ${dbPath}")

      // Tell the service to reload its reader with the new file
      geoLocationService.reload()

      // Cleanup
      tarGzFile.delete()
      extractDir.deleteDir()

    } catch (Exception e) {
      log.error("GeoLite2 update failed: ${e.message}", e)
    }
  }

  private void extractTarGz(File tarGzFile, File destDir) {
    // Simple shell-out to tar since Java's built-in tar handling is verbose;
    // works fine on Linux deployment targets
    def process = ["tar", "-xzf", tarGzFile.absolutePath, "-C", destDir.absolutePath].execute()
    process.waitFor()
    if (process.exitValue() != 0) {
      throw new RuntimeException("tar extraction failed: ${process.err.text}")
    }
  }

  private File findMmdbFile(File dir) {
    File found = null
    dir.eachFileRecurse { file ->
      if (file.name.endsWith(".mmdb")) {
        found = file
      }
    }
    found
  }
}
