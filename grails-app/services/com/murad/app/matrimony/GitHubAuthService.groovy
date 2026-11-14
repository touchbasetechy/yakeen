package com.murad.app.matrimony


import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import groovy.json.JsonSlurper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@Transactional
class GitHubAuthService {

 // def grailsApplication
  RestTemplate restTemplate = new RestTemplate()

  @Secured(['permitAll'])
  def createOrUpdateUserFromGitHub(Map githubUserData, String accessToken) {
    String githubId = githubUserData.id.toString()
    String githubUsername = githubUserData.login
    String name = githubUserData.name ?: githubUsername
    String email = githubUserData.email

    // If email is not public, fetch from GitHub API
    if (!email) {
      email = fetchPrimaryEmail(accessToken)
    }

    def user = Person.findByGithubId(githubId)

    if (!user) {
      user = Person.findByEMail(email)
    }

    if (!user) {
      user = new Person(
        firstName: githubUsername,
        lastName: ".",
        password: generateRandomPassword(),
        eMail: email,
        githubId: githubId,
        githubUsername: githubUsername,
//        name: name,
        enabled: true
      )
    } else {
      // Update existing user
      user.githubId = githubId
      user.githubUsername = githubUsername
//      user.name = name
      if (email && !user.eMail) {
        user.eMail = email
      }
    }

    // Assign default role
    def defaultRole = PersonType.findByPersonTypeName('ROLE_USER') ?:
      PersonType.findByPersonTypeName('USER') ?:
        PersonType.list()?.first()

    user.addToPersonTypes(defaultRole)

    def person
    try {
      person = user.save(flush: true)
    } catch (ValidationException ve) {
      log.error("User validation failed:", ve)
      return
    } catch (Exception e) {
      log.error("Unable to create user:", e)
      return
    }

    // Assign default role if user doesn't have any
//    if (person && (!person.personTypes)) {
//      PersonType userRole = PersonType.findByPersonTypeName('ROLE_USER') ?:
//        new PersonType(authority: 'ROLE_USER').save(flush: true)
//      new PersonPersonType(user: person, role: userRole).save(flush: true)
//    }

    return person
  }

  @Secured(['permitAll'])
  def fetchPrimaryEmail(String accessToken) {
    try {
      HttpHeaders headers = new HttpHeaders()
      headers.set('Authorization', "Bearer ${accessToken}")
      headers.set('Accept', 'application/vnd.github.v3+json')

      HttpEntity<String> entity = new HttpEntity<>(headers)
      ResponseEntity<String> response = restTemplate.exchange(
        'https://api.github.com/user/emails',
        HttpMethod.GET,
        entity,
        String.class
      )

      if (response.statusCode.is2xxSuccessful()) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        List emails = jsonSlurper.parseText(response.body) as List
        Map primaryEmail = emails.find { it.primary == true }
        return primaryEmail?.email
      }
    } catch (Exception e) {
      log.error("Error fetching GitHub user emails: ${e.message}")
    }
    return null
  }

  @Secured(['permitAll'])
  def generateRandomPassword() {
    return UUID.randomUUID().toString().replaceAll('-', '')
  }
}
