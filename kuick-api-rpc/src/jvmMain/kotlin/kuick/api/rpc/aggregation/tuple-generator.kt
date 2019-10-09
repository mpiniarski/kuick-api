package kuick.api.rpc.aggregation

import java.io.File

private const val TUPLES = 26

fun main(args: Array<String>) {
    val baseDir = File(args.getOrElse(0) { "." })
    val path = "kuick-api-rpc/src/jvmMain/kotlin/kuick/api/rpc/aggregation/tuple.kt"

    println("Using baseDir=$baseDir")

    File(baseDir, path)
        .apply { parentFile.mkdirs() }
        .also { println("Generating ${it.absoluteFile}") }
        .writeText(
            """
package kuick.api.rpc.aggregation
                
object Tuple {
${(1..TUPLES).map { num ->

                "\toperator fun <${genericTypes(num)}> " +
                    "invoke(${parameters(num)})" +
                    ": Tuple$num<${genericTypes(num)}> = Tuple$num(${variables(num)})"

            }.joinToString("\n")}
}

${(1..TUPLES).map { num ->

                "data class Tuple$num<${genericTypesList(num).joinToString(", ") { "out $it" }}>" +
                    "(${parametersList(num).joinToString(", ") { "val $it" }})"

            }.joinToString("\n")}
            """
                .replace("\t", "    ")
                .trimIndent()
        )
}

private fun genericTypes(num: Int) = genericTypesList(num).joinToString(", ")
private fun genericTypesList(num: Int) = (1..num).map { 'A' + it - 1 }.map { it.toString() }
private fun parameters(num: Int) = parametersList(num).joinToString(", ")
private fun parametersList(num: Int) = (1..num).map { "_$it: ${'A' + it - 1}" }
private fun variables(num: Int) = (1..num).joinToString(", ") { "_$it" }

