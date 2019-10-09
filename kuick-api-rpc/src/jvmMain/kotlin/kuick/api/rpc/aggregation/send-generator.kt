package kuick.api.rpc.aggregation

import java.io.File

private const val NUM = 10

fun main(args: Array<String>) {
    val baseDir = File(args.getOrElse(0) { "." })
    val path = "kuick-api-rpc/src/jvmMain/kotlin/kuick/api/rpc/aggregation/send.kt"

    println("Using baseDir=$baseDir")

    File(baseDir, path)
        .apply { parentFile.mkdirs() }
        .also { println("Generating ${it.absoluteFile}") }
        .writeText(
            """
package kuick.api.rpc.aggregation

import kuick.api.json.Json
import kuick.api.rpc.Device
import kuick.api.rpc.Request
import kuick.api.rpc.Response
import kuick.api.rpc.toResponse

// TODO - conflicts with single request send from Device when AggregatingDevice is used
// inline fun <reified A : Any, R> AggregatingDevice<R>.send(
//     vararg requests: Request<A>
// ): Device.Result<List<Response<A>>, R> {
//     val result = aggregatedCall(requests.toList())
//     val parsedResult = Json.jsonParser.parse(result.getBodyAsString())
//         .asJsonArray
//         .map { it.toResponse<A>() }
//     return Device.Result(parsedResult, result)
// }

inline fun <reified A : Any, R> AggregatingDevice<R>.send(
    requests: List<Request<A>>
): Device.Result<List<Response<A>>, R> {
    val result = aggregatedCall(requests)
    val parsedResult = Json.jsonParser.parse(result.getBodyAsString())
        .asJsonArray
        .map { it.toResponse<A>() }
    return Device.Result(parsedResult, result)
}
                
${(2..NUM).map { num ->
                "inline fun <${genericTypesList(num).joinToString(", ") { "reified $it : Any" }}, R> AggregatingDevice<R>.send(\n" +

                    (1..num).map { "\t_$it: Request<${genericType(it)}>" }.joinToString(",\n") + "\n" +
                    "): Device.Result<Tuple$num<${(1..num).map {
                        "Response<${genericType(it)}>"
                    }.joinToString(", ")}>, R> { \n" +
                    "\tval result = aggregatedCall(listOf(${variables(num)}))\n" +
                    "\tval responses = Json.jsonParser.parse(result.getBodyAsString())\n" +
                        "\t\t.asJsonArray\n" +
                    "\tval parsedResult = Tuple$num(\n" +
                    (1..num).map {
                        "\t\tresponses[${it-1}].toResponse<${genericType(it)}>()"
                    }.joinToString(",\n") + "\n" +
                    "\t)\n" +
                    "\t return Device.Result(parsedResult, result)\n" +
                    "}"
            }.joinToString("\n\n")}
            """
                .replace("\t", "    ")
                .trimIndent()
        )
}

private fun genericType(it: Int) = 'A' + it - 1
private fun genericTypesList(num: Int) = (1..num).map { genericType(it) }.map { it.toString() }
private fun genericTypes(num: Int) = genericTypesList(num).joinToString(", ")
private fun parameters(num: Int) = parametersList(num).joinToString(", ")
private fun parametersList(num: Int) = (1..num).map { "_$it: ${'A' + it - 1}" }
private fun variables(num: Int) = (1..num).joinToString(", ") { "_$it" }

