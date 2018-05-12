import org.protokruft.example1.Example1
import org.protokruft.example1.newCar
import org.protokruft.example1.newEngine

data class Engine(private val cc: Int, private val bhp: Int) {
    fun toProto(): Example1.Engine = newEngine.let {
        it.cc = cc
        it.bhp = bhp
    }
}
data class Car(private val engine: Engine) {
    fun toProto(): Example1.Car = newCar.let {
        it.engine = engine.toProto()
    }
}

fun Example1.Car.fromProto(): Car = Car(engine.fromProto())
fun Example1.Engine.fromProto(): Engine = Engine(cc, bhp)