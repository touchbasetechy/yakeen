package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper

/**
 * Loads seed/matrimony_hobbies.json from the classpath into the
 * hobby_category and hobby tables. Idempotent: safe to run on every startup.
 * Note: no @Slf4j here - Grails injects `log` into services automatically.
 */
@Transactional
class HobbySeedService {

  static final String SEED_FILE = 'seed/matrimony_hobbies.json'

  Map seed() {
    def stream = Thread.currentThread().contextClassLoader.getResourceAsStream(SEED_FILE)
    if (!stream) {
      log.error("Seed file not found on classpath: ${SEED_FILE}")
      return [categories: 0, hobbies: 0]
    }

    def json = stream.withCloseable { new JsonSlurper().parse(it, 'UTF-8') }

    int newCategories = 0
    int newHobbies = 0

    json.categories.eachWithIndex { cat, int idx ->
      String catDescription = cat.description as String

      HobbyCategory category = HobbyCategory.findByCode(cat.id as String)
      if (!category) {
        category = new HobbyCategory(
          code: cat.id,
          name: cat.name,
          description: catDescription,
          displayOrder: idx
        )
        newCategories++
      } else {
        category.name = cat.name
        category.description = catDescription
        category.displayOrder = idx
      }
      category.save(failOnError: true)

      cat.hobbies.each { h ->
        String hobbyDescription = h.description as String

        Hobby hobby = Hobby.findByCode(h.id as String)
        if (!hobby) {
          new Hobby(
            code: h.id,
            name: h.name,
            description: hobbyDescription,
            category: category
          ).save(failOnError: true)
          newHobbies++
        } else if (hobby.name != h.name ||
          hobby.description != hobbyDescription ||
          hobby.category != category) {
          hobby.name = h.name
          hobby.description = hobbyDescription
          hobby.category = category
          hobby.save(failOnError: true)
        }
      }
    }

    log.info("Hobby seed complete: ${newCategories} new categories, ${newHobbies} new hobbies " +
      "(total ${HobbyCategory.count()} categories, ${Hobby.count()} hobbies)")
    [categories: newCategories, hobbies: newHobbies]
  }
}
