package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

@Slf4j
class HobbyController {

  @Secured('ROLE_ADMIN')
  def addHobby() {}

  @Transactional
  def saveHobby() {
    def createdAt = LocalDateTime.now()
    def hobbyToSave = new Hobby(name: params?.name, description: params?.description, createdBy: session?.person, createdAt: createdAt)
    try {
      def hobbySaved = hobbyToSave.save(flush: true)
      if (hobbySaved) {
        flash.message = "Hobby with name '${hobbySaved?.name}' created successfully!"
        if (params?.ac == 'an') {
          forward(controller: 'AdminDBoard', action: 'index', params: [ac: params?.ac])
        } else if (params?.ac == 'l') {
          forward(controller: 'Hobby', action: 'listHobbiesForEdit', params: [ac: params?.ac])
        } else {
          forward(controller: 'Person', action: 'index', params: [ac: params?.ac])
        }
      } else {
        flash.error = "Failed creating Hobby!"
        forward(controller: 'Hobby', action: 'addHobby', params: [ac: params?.ac])
      }
    } catch (ValidationException e) {
      flash.error = "Hobby not created: ${e.errors}"
      redirect(controller: 'Hobby', action: 'addHobby', params: [ac: params?.ac])
    }
  }

  def listHobbiesForEdit() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    def hobbies = Hobby.list(params)

    [hobbies: hobbies]
  }

  def listMoreHobbies() {
    params.offset = params?.offset ? params.int('offset') : 0
    params.max = params?.max ? params.int('max') : 10

    Person person = request.getAttribute('currentPerson')

    def associatedHobbyIds = person?.hobbies?.collect { it.id } ?: []
    def hobbies, totalCount

    if (associatedHobbyIds) {
      hobbies = Hobby.withCriteria {
        not { 'in'('id', associatedHobbyIds) }
        maxResults(params.max)
        firstResult(params.offset)
      }
      totalCount = Hobby.withCriteria {
        not { 'in'('id', associatedHobbyIds) }
        projections { count('id') }
      }?.get(0) ?: 0
    } else {
      hobbies = Hobby.withCriteria { maxResults(params.max); firstResult(params.offset) }
      totalCount = Hobby.count()
    }

    respond([success: true, hobbies: hobbies, totalCount: totalCount],
      status: HttpStatus.OK, formats: ['json'])
  }

  def editHobby() {
    def hobbyToEdit = Hobby.findById(params?.id)

    [hobby: hobbyToEdit]
  }

  @Transactional
  def updateAndSaveHobby() {
    def hobbyToUpdate = Hobby.get(params?.id)

    hobbyToUpdate.name = params?.name
    if (params?.description) {
      hobbyToUpdate.description = params?.description
    }

    def updatedHobby = hobbyToUpdate.save(flush: true)

    if (updatedHobby) {
      flash.message = "Successfully updated hobby details!"
      forward(action: "listHobbiesForEdit")
    } else {
      flash.error = "Failed updating hobby details!"
      forward(action: "editHobby")
    }
  }

  @Transactional
  def deleteHobby() {
    def hobbyToDelete = Hobby?.get(params?.id)

    if (hobbyToDelete) {
      hobbyToDelete.delete(flush: true)
      flash.message = "Hobby deleted successfully!"
    } else {
      flash.error = "Hobby not found!!"
    }

    forward(action: "listHobbiesForEdit")
  }

  // Accepts either
  //   - fetch/AJAX:  POST  JSON body {"hobbyCategoryId": 3, "offset": 0, "max": 10}  -> JSON response
  //   - page request: GET  ?categoryId=3&offset=0&max=10                              -> view model (as before)
  @Secured(['ROLE_USER'])
  def listHobbiesForCategory() {
    Map body = (request.JSON instanceof Map) ? (Map) request.JSON : [:]

    int offset = Math.max(toInt(body.offset ?: params.offset, 0), 0)
    int max = Math.min(Math.max(toInt(body.max ?: params.max, 10), 1), 100)
    Long categoryId = toLong(body.hobbyCategoryId ?: params.categoryId)

    Person person = request.getAttribute('currentPerson') as Person

    // ───────────── JSON / fetch mode ─────────────
    if (!person) {
      respond([success: false, message: 'User not found!'],
        status: HttpStatus.NOT_FOUND, formats: ['json'])
      return
    }
    if (!categoryId) {
      respond([success: false, message: 'Hobby category is required'],
        status: HttpStatus.BAD_REQUEST, formats: ['json'])
      return
    }
    HobbyCategory category = HobbyCategory.get(categoryId)
    if (!category) {
      respond([success: false, message: 'Hobby category not found!'],
        status: HttpStatus.NOT_FOUND, formats: ['json'])
      return
    }

    Map result = unassignedHobbies(categoryId, person, offset, max)

    response.status = 200

    respond([
      success        : true,
      hobbyCategoryId: categoryId,
      offset         : offset,
      max            : max,
      count          : result.hobbies.size(),
      totalCount     : result.totalCount,
      hasMore        : (offset + result.hobbies.size()) < result.totalCount,
      empty          : result.totalCount == 0,      // nothing left to add in this category
      hobbies        : result.hobbies.collect { Hobby h ->
        [id: h.id, name: h.name, description: h.description ?: '']
      }
    ], status: HttpStatus.OK, formats: ['json'])
    return
  }

/** Active hobbies of a category NOT yet assigned to the person, paged, plus the total count. */
  private Map unassignedHobbies(Long categoryId, Person person, int offset, int max) {
    Map args = [catId: categoryId, personId: person?.id ?: -1L]

    String fromWhere = """
      from Hobby h
      where h.category.id = :catId
        and h.active = true
        and not exists (
            select 1
            from Person p
            join p.hobbies ph
            where p.id = :personId
              and ph.id = h.id
        )
  """

    List<Hobby> hobbies = Hobby.executeQuery(
      "select h ${fromWhere} order by h.name asc".toString(),
      args, [offset: offset, max: max]) as List<Hobby>

    long totalCount = (Hobby.executeQuery("select count(h) ${fromWhere}".toString(), args)[0] ?: 0) as long

    [hobbies: hobbies, totalCount: totalCount]
  }

  private List<Long> assignedHobbyIds(Long personId) {
    Hobby.executeQuery('select h.id from Person p join p.hobbies h where p.id = :pid', [pid: personId]) as List<Long>
  }

  private static int toInt(def v, int dflt) {
    try {
      v == null || v == '' ? dflt : (v as String).toInteger()
    } catch (ignored) {
      dflt
    }
  }

  private static Long toLong(def v) {
    try {
      v == null || v == '' ? null : (v as String).toLong()
    } catch (ignored) {
      null
    }
  }

  @Transactional
  def addHobbyToUser() {
    Person currentPerson = request.getAttribute("currentPerson") as Person
    Person person = currentPerson ? Person.get(currentPerson.id) : null
    Hobby hobbyToAdd = Hobby.get(request.JSON?.id as Long)

    if (!person) {
      respond([success: false, message: "User not found!"], status: HttpStatus.NOT_FOUND, formats: ['json'])
      return
    }
    if (!hobbyToAdd) {
      respond([success: false, message: "Hobby not found!"], status: HttpStatus.NOT_FOUND, formats: ['json'])
      return
    }
    if (person.hobbies?.contains(hobbyToAdd)) {
      respond([success: false, message: "User already has hobby " + hobbyToAdd.name],
        status: HttpStatus.CONFLICT, formats: ['json'])
      return
    }

    person.addToHobbies(hobbyToAdd)   // owner side only; GORM syncs hobbyToAdd.persons

    if (!person.save(flush: true)) {
      transactionStatus.setRollbackOnly()
      log.warn "Failed to add hobby: ${person.errors}"
      respond([success: false, message: "Failed to add hobby " + hobbyToAdd.name + " to user!"],
        status: HttpStatus.UNPROCESSABLE_ENTITY, formats: ['json'])
      return
    }

    respond([
      success    : true,
      message    : "Hobby " + hobbyToAdd.name + " added to user!",
      redirectUrl: createLink(controller: 'DBoard', action: 'listHobbiesToAdd',
        params: [slot: "${request.getAttribute('currentSlot')}"])
    ], status: HttpStatus.OK, formats: ['json'])
  }
}
