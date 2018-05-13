import org.protokruft.example2.Example2.Address
import org.protokruft.example2.Example2.Person
import org.protokruft.example2.newAddress
import org.protokruft.example2.newPerson

fun before() {
    val person = Person.newBuilder()
            .setName("Hello Kitty")
            .setAddress(
                    Address.newBuilder()
                            .setNumber(123)
                            .setStreet("Hello Kitty Street")
                            .setPostcode("N304SD")
                            .build())
            .build()
}

fun after() {
    val person = newPerson {
        name = "Hello Kitty"
        address = newAddress {
            number = 123
            street = "Hello Kitty Street"
            postcode = "N304SD"
        }
    }
}
