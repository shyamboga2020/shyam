package sollecitom.examples.contract_testing.web

import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import sollecitom.examples.contract_testing.application.Contact
import sollecitom.examples.contract_testing.application.ContactsRegistry
import sollecitom.examples.contract_testing.application.NewContact

private const val ENDPOINT = "/contacts"
private const val SPECIFIC_ID = "/{id}"
private const val ID = "id"

private const val ID_FIELD = "id"
private const val FIRST_NAME_FIELD = "firstName"
private const val LAST_NAME_FIELD = "lastName"
private const val PHONE_NUMBER_FIELD = "phoneNumber"

@RequestMapping(ENDPOINT)
@Controller
class ContactEndpoint @Autowired constructor(private val registry: ContactsRegistry) {

    @GetMapping
    fun retrieveAll(): ResponseEntity<String> {

        return ResponseEntity(registry.toJsonArray().toString(), HttpStatus.OK)
    }

    @PostMapping
    fun createNew(@RequestBody payload: String, uriBuilder: UriComponentsBuilder): ResponseEntity<Unit> {

        val contact = payload.parseContact()
        val id = registry.add(contact)
        val resourceLocation = uriBuilder.path("$ENDPOINT/{}}").buildAndExpand(id).toUri()
        return ResponseEntity.created(resourceLocation).build()
    }

    @GetMapping(SPECIFIC_ID)
    fun retrieveOne(@PathVariable(ID) id: String): ResponseEntity<String?> {

        val contact = registry[id]
        if (contact != null) {
            return ResponseEntity.ok(contact.toJsonArray().toString())
        }
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping(SPECIFIC_ID)
    fun removeOne(@PathVariable(ID) id: String): ResponseEntity<Unit> {

        val removed = registry.remove(id)
        return ResponseEntity.status(if (removed) HttpStatus.NO_CONTENT else HttpStatus.NOT_FOUND).build()
    }

    private fun Contact.toJsonArray(): JSONObject = JSONObject().put(ID_FIELD, id).put(FIRST_NAME_FIELD, firstName).put(LAST_NAME_FIELD, lastName).put(PHONE_NUMBER_FIELD, phoneNumber)

    private fun Iterable<Contact>.toJsonArray(): JSONArray = map { it.toJsonArray() }.fold(JSONArray()) { array, obj -> array.put(obj) }

    private fun String.parseContact(): NewContact {

        val json = JSONObject(this)
        return NewContact(json.getString(FIRST_NAME_FIELD), json.getString(LAST_NAME_FIELD), json.getString(PHONE_NUMBER_FIELD))
    }
}