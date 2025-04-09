package org.elaastic.auth.cas

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.unmockkAll
import org.elaastic.auth.UserLink
import org.elaastic.auth.UserLinkRepository
import org.elaastic.auth.UserLinkService
import org.elaastic.user.*
import org.jasig.cas.client.authentication.AttributePrincipalImpl
import org.jasig.cas.client.validation.AssertionImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Profile
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.EntityManager

/**
 * Test class for [CasAuthenticationUserDetailService].
 *
 * With the attribut `classes` I can tell SpringBoot to only load the Bean selected, and so reducing the amount of Bean
 * loaded. It also can be used to avoid Beans that have `@PostConstruct` method.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = [
        UserLinkService::class
    ]
)
@Profile("test")
class CasAuthenticationUserDetailServiceTest(
    @Autowired val userLinkService: UserLinkService,
) {
    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var userLinkRepository: UserLinkRepository

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var roleRepository: RoleRepository

    @MockBean
    lateinit var entityManager: EntityManager

    @MockBean
    lateinit var roleService: RoleService

    private val defaultPassword = "1234"

    @AfterEach
    fun cleanUp() {
        unmockkAll()
    }

    @BeforeEach
    fun setUp() {
        whenever(roleService.roleForName(any(), any()))
            .thenReturn(Role("roleName"))
        whenever(userLinkRepository.save(any<UserLink>()))
            .thenAnswer { it.getArgument<UserLink>(0) }
        whenever(userService.generateUsername(any<String>(), any<String>()))
            .thenAnswer { generateUsername(it.getArgument(0), it.getArgument(1)) }
        whenever(userService.generatePassword())
            .thenAnswer { defaultPassword }
        whenever(userService.addUser(any<User>(), any<String>(), any<Boolean>(), any<Boolean>(), any<Boolean>()))
            .thenAnswer {
                it.getArgument<User>(0).also { user ->
                    user.password = user.plainTextPassword
                }
            }
    }

    @Test
    fun `test loadUserDetails when user isn't in the database`() {
        /** The cas provider uses in this test */
        val casProvider = SupportedCasProvider.Kosmos
        val casAuthenticationUserDetailService = CasAuthenticationUserDetailService(
            userLinkService = userLinkService,
            casKey = "casKey",
            casProvider = casProvider.name
        )

        // Given a username
        val userName = UserInformation(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@mail.com"
        )

        // When loadUserDetails is called
        val userDetails = casAuthenticationUserDetailService.loadUserDetails(
            getCasAssertionAuthenticationToken(userName, casProvider)
        )

        // Then the user details should not be null
        assertNotNull(userDetails) { "User details should not be null" }
        assertEquals(userName.username, userDetails.username) { "Username should be the same" }
        assertEquals(defaultPassword, userDetails.password) { "Password should be the default one" }
    }

    /**
     * Create a [CasAssertionAuthenticationToken] with a [AssertionImpl] and a [AttributePrincipalImpl].
     */
    private fun getCasAssertionAuthenticationToken(
        user: UserInformation,
        casProvider: SupportedCasProvider
    ): CasAssertionAuthenticationToken {
        return CasAssertionAuthenticationToken(
            AssertionImpl(
                AttributePrincipalImpl(
                    user.username,
                    buildAttribute(user, casProvider)
                )
            ),
            null
        )
    }

    /**
     * Build the attribute for the [AttributePrincipalImpl] with the user information.
     *
     * The information depends on the CAS provider.
     */
    private fun buildAttribute(user: UserInformation, casProvider: SupportedCasProvider): Map<String, String> {
        return when (casProvider) {
            SupportedCasProvider.Kosmos -> mapOf(
                "prenom" to user.firstName,
                "nom" to user.lastName,
                "mail" to user.email,
                "profil" to "Eleve"
            )

            SupportedCasProvider.Edifice -> TODO("Specify the map attribute for Edifice")
        }
    }

    data class UserInformation(
        val firstName: String,
        val lastName: String,
        val email: String
    ) {
        val username: String = generateUsername(
            firstName = firstName,
            lastName = lastName
        )
    }
}

fun generateUsername(firstName: String, lastName: String) =
    "$firstName.$lastName"