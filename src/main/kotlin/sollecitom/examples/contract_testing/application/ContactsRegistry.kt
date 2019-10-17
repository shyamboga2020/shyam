package sollecitom.examples.contract_testing.application

interface ContactsRegistry : Iterable<Contact> {

    fun add(contact: NewContact): String

    operator fun get(id: String): Contact?

    fun remove(id: String): Boolean
}
