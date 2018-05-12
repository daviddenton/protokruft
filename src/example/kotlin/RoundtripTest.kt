import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

class RoundtripTest {
    @Test
    fun canRoundtripToAndFromProto() {
        val car = Car(Engine(123, 456))
        assertThat(car.toProto().fromProto(), equalTo(car))
    }
}