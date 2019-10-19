package examples.contract_testing.web

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import examples.contract_testing.application.Contact
import examples.contract_testing.application.ContactsRegistry
import examples.contract_testing.application.NewContact
import examples.contract_testing.application.PhoneNumber
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

private const val ENDPOINT = "contacts"
private const val ID_FIELD = "id"
private const val FIRST_NAME_FIELD = "firstName"
private const val LAST_NAME_FIELD = "lastName"
private const val PHONE_NUMBER_FIELD = "phoneNumber"

private const val CONTACT_SCHEMA_LOCATION = "/components/Contact.json"
private const val NEW_CONTACT_SCHEMA_LOCATION = "/components/NewContact.json"

private const val LOCALHOST = "http://localhost"

@ExtendWith(SpringExtension::class)
@EnableAutoConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ContactEndpointContractTest.SpringConfiguration::class])
internal class ContactEndpointContractTest {

    @LocalServerPort
    private var port: Int = 0

    @MockBean
    lateinit var registry: ContactsRegistry

    private val contactSchema = jsonSchemaAt(CONTACT_SCHEMA_LOCATION)
    private val newContactSchema = jsonSchemaAt(NEW_CONTACT_SCHEMA_LOCATION)

    @Test
    fun retrieveAll() {

        val contact1 = Contact("1", "Mark", "Dadada", PhoneNumber("+170231902"))
        val contact2 = Contact("2", "Zack", "Test", PhoneNumber("+167219317"))
        val contacts = listOf(contact1, contact2)
        `when`(registry.iterator()).thenReturn(contacts.iterator())

        val (_, response, jsonArray) = "$LOCALHOST:$port/$ENDPOINT".httpGet().responseJsonArray()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
        assertThat(jsonArray).hasSameSizeHas(contacts)
        jsonArray.map { element -> element as JSONObject }.forEachIndexed { index, json ->
            assertThat(json).compliesWith(contactSchema)
            assertThat(json.toContact()).isEqualTo(contacts[index])
        }
    }

    @Test
    fun createNew() {

        val id = "123"
        val newContact = NewContact("Mark", "Dadada", PhoneNumber("+170231902"))
        `when`(registry.add(newContact)).thenReturn(id)

        val jsonPayload = newContact.toJson()
        assertThat(jsonPayload).compliesWith(newContactSchema)

        val (_, response, _) = "$LOCALHOST:$port/$ENDPOINT".httpPost().jsonBody(jsonPayload.toString()).response()

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED.value())
        assertThat(response.header(HttpHeaders.LOCATION).single()).isEqualTo("$LOCALHOST:$port/$ENDPOINT/$id")
    }

    @Test
    fun createNewWithInvalidData() {

        val newContact = NewContact("Mark", "Dadada", PhoneNumber("+170231902"))

        val jsonPayload = newContact.toJson()
        jsonPayload.put("phoneNumber", "+abc231902")

        assertThat(jsonPayload).compliesWith(newContactSchema)

        val (_, response, _) = "$LOCALHOST:$port/$ENDPOINT".httpPost().jsonBody(jsonPayload.toString()).response()

        assertThat(response.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value())
    }

    @Test
    fun retrieveOne() {

        val contact = Contact("123", "Mark", "Dadada", PhoneNumber("+170231902"))
        `when`(registry[eq(contact.id)]).thenReturn(contact)

        val (_, response, json) = "$LOCALHOST:$port/$ENDPOINT/${contact.id}".httpGet().responseJsonObject()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK.value())
        assertThat(json).compliesWith(contactSchema)
        assertThat(json.toContact()).isEqualTo(contact)
    }

    @Test
    fun retrieveOneNotFound() {

        val id = "123"
        `when`(registry[eq(id)]).thenReturn(null)

        val (_, response, _) = "$LOCALHOST:$port/$ENDPOINT/$id".httpGet().response()

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun removeOne() {

        val id = "123"
        `when`(registry.remove(eq(id))).thenReturn(true)

        val (_, response, _) = "$LOCALHOST:$port/$ENDPOINT/$id".httpDelete().response()

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT.value())
    }

    @Test
    fun removeOneNotFound() {

        val id = "123"
        `when`(registry.remove(eq(id))).thenReturn(false)

        val (_, response, _) = "$LOCALHOST:$port/$ENDPOINT/$id".httpDelete().response()

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    private fun JSONObject.toContact(): Contact {

        val id = getString(ID_FIELD)
        val firstName = getString(FIRST_NAME_FIELD)
        val lastName = getString(LAST_NAME_FIELD)
        val phoneNumber = PhoneNumber(getString(PHONE_NUMBER_FIELD))
        return Contact(id, firstName, lastName, phoneNumber)
    }

    private fun NewContact.toJson(): JSONObject {

        return JSONObject().put(FIRST_NAME_FIELD, firstName).put(LAST_NAME_FIELD, lastName).put(PHONE_NUMBER_FIELD, phoneNumber.value)
    }

    @Configuration
    @ComponentScan("examples.contract_testing")
    open class SpringConfiguration
}