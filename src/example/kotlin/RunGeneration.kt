import org.protokruft.GenerateProtobufDsl
import org.protokruft.ScanClasspath
import java.io.File

fun main(args: Array<String>) {
    val generated = GenerateProtobufDsl.generate(ScanClasspath("org.protokruft"), "dsl")
    generated.forEach { it.writeTo(File("src/example/kotlin")) }
}