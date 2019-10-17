package sollecitom.examples.contract_testing.application

class Contact(val id: String, val firstName: String, val lastName: String, val phoneNumber: String) {

    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {

        return id.hashCode()
    }
}
