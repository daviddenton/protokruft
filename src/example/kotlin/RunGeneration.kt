import org.protokruft.GenerateProtobufDsl
import org.protokruft.ScanClasspath
import java.io.File

fun main(args: Array<String>) {
    GenerateProtobufDsl
            .generate(ScanClasspath("org.protokruft"), "dsl")
            .forEach { it.writeTo(File("src/example/kotlin")) }
}