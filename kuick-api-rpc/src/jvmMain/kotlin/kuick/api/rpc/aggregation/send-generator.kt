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

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kuick.json.Json

inline fun <reified A : Any> JsonElement.toResponse(): Response<A> {
    val asString = this.toString()
    val status = this.asJsonObject["status"].asString
    return if (status.startsWith("2")) {
        val type = TypeToken.getParameterized(Response.Success::class.java, A::class.java).type
        Json.fromJson<Response.Success<A>>(asString, type)
    } else
        Json.fromJson<Response.Failure<A>>(asString)
}

inline fun <reified A : Any> Device.send(
    vararg requests: Request<A>
): List<Response<A>> {
    val result = aggregatedSend(requests.toList())
    return Json.jsonParser.parse(result).asJsonArray
        .map { it.toResponse<A>() }
}

inline fun <reified A : Any> Device.send(
    requests: List<Request<A>>
): List<Response<A>> {
    val result = aggregatedSend(requests)
    return Json.jsonParser.parse(result).asJsonArray
        .map { it.toResponse<A>() }
}
                
${(1..NUM).map { num ->
                "inline fun <${genericTypesList(num).joinToString(", ") { "reified $it : Any" }}> Device.send(\n" +

                    (1..num).map { "\t_$it: Request<${genericType(it)}>" }.joinToString(",\n") + "\n" +
                    "): Tuple$num<${(1..num).map {
                        "Response<${genericType(it)}>"
                    }.joinToString(", ")}> { \n" +
                    "\tval result = aggregatedSend(listOf(${variables(num)}))\n" +
                    "\tval responses = Json.jsonParser.parse(result).asJsonArray\n" +
                    "\treturn Tuple$num(\n" +
                    (1..num).map {
                        "\t\tresponses[${it-1}].toResponse<${genericType(it)}>()"
                    }.joinToString(",\n") + "\n" +
                    "\t)\n" +
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

