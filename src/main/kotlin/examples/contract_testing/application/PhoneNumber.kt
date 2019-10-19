package examples.contract_testing.application

data class PhoneNumber(val value: String) {

    init {
        require(value.isNotBlank())
        require(value[0] == '+')
        require(value.subSequence(1, value.length).all(Char::isDigit))
    }
}