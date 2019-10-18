package sollecitom.examples.contract_testing.web

import assertk.Assert
import assertk.assertions.support.fail
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.json.responseJson
import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import org.mockito.Mockito

// Used to prevent a problem with nullability and vanilla Mockito + Kotlin.
internal fun <T> eq(obj: T): T = Mockito.eq<T>(obj)

fun Assert<JSONObject>.compliesWith(schema: Schema) = given { actual ->

    try {
        schema.validate(actual)
    } catch (e: ValidationException) {
        fail("JSON object does not comply with JSON schema $schema.\nErrors were ${e.allMessages}.", e)
    }
}

inline fun <reified T> T.jsonSchemaAt(location: String): Schema {

    return T::class.java.getResourceAsStream(location).use {
        val definition = JSONObject(JSONTokener(it))
        SchemaLoader.load(definition)
    }
}

fun Request.responseJsonObject(): Triple<Request, Response, JSONObject> {

    val (request, response, result) = responseJson()
    return Triple(request, response, result.get().obj())
}

fun Request.responseJsonArray(): Triple<Request, Response, JSONArray> {

    val (request, response, result) = responseJson()
    return Triple(request, response, result.get().array())
}