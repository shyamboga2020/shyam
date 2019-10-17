package sollecitom.examples.contract_testing.application

data class NewContact(val firstName: String, val lastName: String, val phoneNumber: String) {

    fun withId(id: String): Contact = Contact(id, firstName, lastName, phoneNumber)
}
