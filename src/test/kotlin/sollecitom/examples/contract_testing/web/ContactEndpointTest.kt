package sollecitom.examples.contract_testing.web

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit.jupiter.SpringExtension
import sollecitom.examples.contract_testing.application.Contact
import sollecitom.examples.contract_testing.application.ContactsRegistry
import sollecitom.examples.contract_testing.application.NewContact

private const val ENDPOINT = "/contacts"
private const val FIRST_NAME_FIELD = "firstName"
private const val LAST_NAME_FIELD = "lastName"
private const val PHONE_NUMBER_FIELD = "phoneNumber"

@ExtendWith(SpringExtension::class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ContactEndpointTest.SpringConfiguration::class])
internal class ContactEndpointTest {

    @LocalServerPort
    private var port: Int = 0

    @MockBean
    lateinit var registry: ContactsRegistry

    @Test
    fun retrieveAll() {

        val contact1 = Contact("1", "Mark", "Dadada", "+170231902")
        val contact2 = Contact("2", "Zack", "Test", "+167219317")
        `when`(registry.iterator()).thenReturn(listOf(contact1, contact2).iterator())

        // TODO make request for get by id; check that answer is OK, check that the JSON payload complies with schema, check that for each contact the fields of the JSON payload match the first, last and phone.
    }

    @Test
    fun createNew() {

        val id = "123"
        val newContact = NewContact("Mark", "Dadada", "+170231902")
        `when`(registry.add(newContact)).thenReturn(id)

        // TODO make request for post; check that answer is CREATED, check that the Location header is there and contains the `id`.
    }

    @Test
    fun retrieveOne() {

        val id = "123"
        val firstName = "Mark"
        val lastName = "Dadada"
        val phoneNumber = "+170231902"
        val contact = Contact(id, firstName, lastName, phoneNumber)
        `when`(registry[eq(id)]).thenReturn(contact)

        // TODO make request for get by id; check that answer is OK, check that the JSON payload complies with schema, check that the fields of the JSON payload match the first, last and phone.
    }

    @Test
    fun retrieveOneNotFound() {

        val id = "123"
        `when`(registry[eq(id)]).thenReturn(null)

        // TODO make request for get by id; check that answer is NOT_FOUND.
    }

    @Test
    fun removeOne() {

        val id = "123"
        `when`(registry.remove(eq(id))).thenReturn(true)

        // TODO make request for delete by id; check that answer is NO_CONTENT.
    }

    @Test
    fun removeOneNotFound() {

        val id = "123"
        `when`(registry.remove(eq(id))).thenReturn(false)

        // TODO make request for delete by id; check that answer is NOT_FOUND.
    }

    @Configuration
    open class SpringConfiguration
}