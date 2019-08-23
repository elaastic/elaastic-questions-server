package org.elaastic.questions.lti

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentRepository
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tsaap.lti.tp.ToolProvider
import java.text.Normalizer
import java.util.*
import java.util.logging.Logger
import java.util.regex.Pattern
import javax.persistence.EntityManager


@Service
class LmsService(
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val lmsUserRepository: LmsUserRepository,
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val lmsAssignmentRepository: LmsAssignmentRepository,
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val assignmentService: AssignmentService,
        @Autowired val entityManager: EntityManager
) {

    internal val logger = Logger.getLogger(LmsService::class.java.name)

    /**
     * Get lms user based on tool provider information. Create a new one if required
     *
     * @param tp the tool provider
     * @return the lms user
     */
    fun getLmsUser(tp: ToolProvider): LmsUser {
        val lms = ltiConsumerRepository.findById(tp.consumer.key).get()
        var lmsUser = lmsUserRepository.findByLmsUserIdAndAndLms(tp.user.idForDefaultScope, lms)
        if (lmsUser == null) {
            createUserBasedOnToolProviderInformation(tp).let { user ->
                lmsUser = createLmsUserBasedOnToolProviderInformationLmsAndUser(tp, lms, user)
            }
        }
        return lmsUser!!
    }

    /**
     * Get lms assignment based on tool provider information. Create a new one if required
     *
     * @param tp the tool provider
     * @param lmsUser the lms user
     * @return the lms assignment
     */
    fun getLmsAssignment(tp: ToolProvider, lmsUser: LmsUser): LmsAssignment {
        var lmsAssignment = lmsAssignmentRepository.findByLmsActivityIdAndLmsCourseIdAndLms(
                tp.resourceLink.id,
                tp.resourceLink.ltiContextId,
                lmsUser.lms
        )
        if (lmsAssignment == null) {
            if (!lmsUser.user.isTeacher()) {
                logger.severe("Try to create an assignment with no teacher role: ${lmsUser.user.username}")
                throw IllegalArgumentException("Only teacher can create an assignment")
            }
            findOrCreateAssignmentBasedOnToolProvideInformation(tp, lmsUser).let {
                lmsAssignment = createLmsAssignment(tp, lmsUser.lms, it)
            }
        }
        return lmsAssignment!!
    }

    /**
     * Generate a password (not encoded)
     * @return the password
     */
    fun generatePassword(): String {
        var password = ""
        val alphabet = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789"
        val rand = Random()
        for (i in 0..7) {
            password += alphabet[rand.nextInt(alphabet.length)]
        }
        return password
    }

    /**
     * Generate username from firstname and lastname
     * @param sql the sql connection to check existing username
     * @param firstName the firstname
     * @param lastName the lastname
     * @return the username
     */
    fun generateUsername(firstName: String, lastName: String): String {
        val indexLastname = MAX_INDEX_LASTNAME.coerceAtMost(lastName.length)
        val indexFirstName = MAX_INDEX_FIRSTNAME.coerceAtMost(firstName.length)
        var username = replaceAccent(firstName.replace("\\s".toRegex(), "").toLowerCase().substring(0, indexFirstName)) +
                replaceAccent(lastName.replace("\\s".toRegex(), "").toLowerCase().substring(0, indexLastname))
        val existingUsername = findMostRecentUsernameStartingWithUsername(username)
        if (existingUsername != null) {
            "[0-9]+".toRegex().let {
                it.findAll(existingUsername)
            }.let {
                username += if (it.count() == 0) {
                    2
                } else {
                    Integer.parseInt(it.iterator().next().value) + 1
                }
            }
        }
        return username
    }

    private val MAX_INDEX_LASTNAME = 4
    private val MAX_INDEX_FIRSTNAME = 3

    /**
     * Replace accents in a string
     * @param str the string to modify
     * @return
     */
    fun replaceAccent(str: String): String {
        val nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    /**
     * Get the most recent user who begin with username param
     * @param sql the sql object
     * @param username the username
     * @return a username if found else null
     */
    fun findMostRecentUsernameStartingWithUsername(username: String): String? {
        val userNameLike = "^$username[0-9]*$"
        val queryString = "SELECT username FROM user WHERE username RLIKE '$userNameLike' ORDER BY username DESC"
        logger.finest("Generated query string: $queryString")
        val query = entityManager.createNativeQuery(queryString)
        val result = query.resultList
        return when (result.isEmpty()) {
            true -> null
            false -> result.first().toString()
        }
    }

    private fun createLmsAssignment(tp: ToolProvider, lms: LtiConsumer, assignment: Assignment): LmsAssignment {
        LmsAssignment(
                lms,
                tp.resourceLink.id,
                tp.resourceLink.ltiContextId,
                assignment
        ).let {
            return lmsAssignmentRepository.save(it)
        }
    }

    private fun findOrCreateAssignmentBasedOnToolProvideInformation(tp: ToolProvider, lmsUser: LmsUser): Assignment {
        var assignment = findAssignmentWithIdProvidedByTooProvider(tp)
        if (assignment == null) {
            Assignment(tp.resourceLink.title, lmsUser.user).let {
                assignment = assignmentService.save(it)
            }
        }
        return assignment!!
    }

    private fun findAssignmentWithIdProvidedByTooProvider(tp: ToolProvider): Assignment? {
        val globalId = tp.request.getParameter("custom_assignmentid") ?: return null
        assignmentRepository.findByGlobalId(globalId).let {
            if (it == null) {
                logger.severe("No assignment found for global id $globalId")
                throw IllegalArgumentException("No assignment found for global id $globalId")
            }
            return it
        }
    }

    private fun createLmsUserBasedOnToolProviderInformationLmsAndUser(tp: ToolProvider, lms: LtiConsumer, user: User): LmsUser {
        LmsUser(tp.user.idForDefaultScope, lms, user).let {
            return lmsUserRepository.save(it)
        }
    }

    private fun createUserBasedOnToolProviderInformation(tp: ToolProvider): User {
        User(
                tp.user.firstname,
                tp.user.lastname,
                generateUsername(tp.user.firstname, tp.user.lastname),
                generatePassword(),
                tp.user.email
        ).let {
            when {
                tp.user.isAdmin -> it.addRole(roleService.roleTeacher())
                tp.user.isLearner -> it.addRole(roleService.roleStudent())
                tp.user.isStaff -> it.addRole(roleService.roleTeacher())
                else -> it.addRole(roleService.roleStudent())
            }
        }.let {
            userService.addUser(it, "fr", false, false)
            return it
        }
    }

}
