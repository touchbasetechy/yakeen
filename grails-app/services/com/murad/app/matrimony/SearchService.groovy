package com.murad.app.matrimony

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import groovy.util.logging.Slf4j

import javax.sql.DataSource

@Slf4j
@Transactional
class SearchService {

  private static final String DOESNT_MATTER = "Doesn't Matter"

  DataSource dataSource

  def search(Map params) {
    // Early validation
    if (!params) {
      log.warn "Search called with null/empty params"
      return []
    }

    def criteria = buildSearchCriteria(params)

    if (!criteria.isValid) {
      log.warn "Invalid search criteria: ${criteria.errors}"
      return []
    }

    def sql = new Sql(dataSource)

    try {
      def query = buildQuery(criteria)
      def queryParams = buildQueryParams(criteria)

      log.debug "Executing with search query: ${query}"
      log.debug "Executing search query with params: ${queryParams}"

      return sql.rows(query, queryParams)

    } catch (Exception e) {
      log.error "Error executing search query", e
      throw e
    } finally {
      sql.close()
    }
  }

  private Map buildSearchCriteria(Map params) {
    def criteria = [
      isValid: true,
      errors : []
    ]

    // Parse and validate age range
    criteria.minAge = parseAge(params.minAge, 18) // Default min age 18
    criteria.maxAge = parseAge(params.maxAge, 100) // Default max age 100

    if (criteria.minAge > criteria.maxAge) {
      criteria.isValid = false
      criteria.errors << "minAge cannot be greater than maxAge"
    }

    // Validate required fields
    Person person = Person.get(request.getAttribute("currentUser") as Long)
    if (person?.personDetails?.gender == "Female") {
      criteria.gender = "Male"
    } else if (person?.personDetails?.gender == "Male") {
      {
        criteria.gender = "Female"
      }

      // "Doesn't Matter" (or blank) means no filter on that field
      criteria.religion = filterableValue(params.religion)
      criteria.caste = filterableValue(params.caste)
      criteria.smokingHabit = filterableValue(params.smokingHabit)
      criteria.drinkingHabit = filterableValue(params.drinkingHabit)
      criteria.maritalStatus = filterableValue(params.maritalStatus)
      criteria.children = filterableValue(params.children)
      criteria.childrenStatus = filterableValue(params.childrenStatus)
      criteria.height = filterableValue(params.height)
      criteria.weight = filterableValue(params.weight)
      criteria.familyStatus = filterableValue(params.familyStatus)
      criteria.familyType = filterableValue(params.familyType)
      criteria.familyValues = filterableValue(params.familyValues)
      criteria.disabilities = filterableValue(params.disabilities)
      criteria.education = filterableValue(params.education)
      criteria.profession = filterableValue(params.profession)
      criteria.salary = parseSalary(filterableValue(params.salary))
      criteria.salaryFor = filterableValue(params.salaryFor)
      criteria.workLocation = filterableValue(params.workLocation)
      criteria.workingCountry = filterableValue(params.workingCountry)

      return criteria
    }
  }
  /**
   * Normalizes a raw form value to a query-ready filter value, or null when
   * the field shouldn't be filtered on (blank / "Doesn't Matter"). Centralizing
   * this check keeps buildQuery()'s placeholders and buildQueryParams()'s
   * bind values in lockstep — previously each method applied a different
   * truthiness check for smokingHabit/drinkingHabit, so picking "Doesn't
   * Matter" added a `?` placeholder in the query without a matching bound
   * param, misaligning every param after it.
   */
  private String filterableValue(def value) {
    (value && value != DOESNT_MATTER) ? value as String : null
  }

  /**
   * Parses a formatted salary value (e.g. "1,200,000") into a plain Long for
   * numeric comparison. Returns null (no filter) when blank or unparseable —
   * "Salary" is treated as a minimum threshold (>=), not an exact match, so
   * it must bind as a number, not a string.
   */
  private Long parseSalary(String salaryParam) {
    if (!salaryParam) return null

    try {
      return salaryParam.replaceAll(/[^0-9]/, '').toLong()
    } catch (NumberFormatException e) {
      log.warn "Invalid salary parameter: ${salaryParam}, ignoring salary filter"
      return null
    }
  }

  private Integer parseAge(def ageParam, Integer defaultValue) {
    if (!ageParam || (ageParam instanceof String && ageParam.isEmpty())) {
      return defaultValue
    }

    try {
      return ageParam.toInteger()
    } catch (NumberFormatException e) {
      log.warn "Invalid age parameter: ${ageParam}, using default: ${defaultValue}"
      return defaultValue
    }
  }

  private String buildQuery(Map criteria) {
    def whereClauses = []

    whereClauses << "pd.gender = ?"
    whereClauses << "pd.age BETWEEN ? AND ?"

    if (criteria.religion) {
      whereClauses << "pd.religion = ?"
    }

    if (criteria.caste) {
      whereClauses << "pd.caste = ?"
    }

    if (criteria.smokingHabit) {
      whereClauses << "pd.smoking_habit = ?"
    }

    if (criteria.drinkingHabit) {
      whereClauses << "pd.drinking_habit = ?"
    }

    if (criteria.maritalStatus) {
      whereClauses << "pd.marital_status = ?"
    }

    if (criteria.children) {
      whereClauses << "pd.children = ?"
    }

    if (criteria.childrenStatus) {
      whereClauses << "pd.children_status = ?"
    }

    if (criteria.height) {
      whereClauses << "pd.height = ?"
    }

    if (criteria.weight) {
      whereClauses << "pd.weight = ?"
    }

    if (criteria.familyStatus) {
      whereClauses << "pd.family_status = ?"
    }

    if (criteria.familyType) {
      whereClauses << "pd.family_type = ?"
    }

    if (criteria.familyValues) {
      whereClauses << "pd.family_values = ?"
    }

    if (criteria.disabilities) {
      whereClauses << "pd.disabilities = ?"
    }

    if (criteria.education) {
      whereClauses << "pd.education = ?"
    }

    if (criteria.profession) {
      whereClauses << "pd.profession = ?"
    }

    if (criteria.salary) {
      whereClauses << "pd.salary >= ?"
    }

    if (criteria.salaryFor) {
      whereClauses << "pd.salary_for = ?"
    }

    if (criteria.workLocation) {
      whereClauses << "pd.work_location = ?"
    }

    if (criteria.workingCountry) {
      whereClauses << "pd.working_country = ?"
    }

    return """
            SELECT
                p.id,
                p.first_name,
                p.last_name,
                p.e_mail,
                pd.gender,
                pd.religion,
                pd.caste,
                pd.smoking_habit,
                pd.drinking_habit,
                pd.marital_status,
                pd.children,
                pd.children_status,
                pd.height,
                pd.weight,
                pd.family_status,
                pd.family_type,
                pd.family_values,
                pd.disabilities,
                pd.education,
                pd.profession,
                pd.salary,
                pd.salary_for,
                pd.work_location,
                pd.working_country
            FROM person p
            INNER JOIN person_details pd ON p.id = pd.person_id
            WHERE ${whereClauses.join(' AND ')}
        """.stripIndent()
  }

  private List buildQueryParams(Map criteria) {
    def params = [
      criteria.gender,
      criteria.minAge,
      criteria.maxAge
    ]

    if (criteria.religion) {
      params << criteria.religion
    }

    if (criteria.caste) {
      params << criteria.caste
    }

    if (criteria.smokingHabit) {
      params << criteria.smokingHabit
    }

    if (criteria.drinkingHabit) {
      params << criteria.drinkingHabit
    }

    if (criteria.maritalStatus) {
      params << criteria.maritalStatus
    }

    if (criteria.children) {
      params << criteria.children
    }

    if (criteria.childrenStatus) {
      params << criteria.childrenStatus
    }

    if (criteria.height) {
      params << criteria.height
    }

    if (criteria.weight) {
      params << criteria.weight
    }

    if (criteria.familyStatus) {
      params << criteria.familyStatus
    }

    if (criteria.familyType) {
      params << criteria.familyType
    }

    if (criteria.familyValues) {
      params << criteria.familyValues
    }

    if (criteria.disabilities) {
      params << criteria.disabilities
    }

    if (criteria.education) {
      params << criteria.education
    }

    if (criteria.profession) {
      params << criteria.profession
    }

    if (criteria.salary) {
      params << criteria.salary
    }

    if (criteria.salaryFor) {
      params << criteria.salaryFor
    }

    if (criteria.workLocation) {
      params << criteria.workLocation
    }

    if (criteria.workingCountry) {
      params << criteria.workingCountry
    }

    return params
  }
}
