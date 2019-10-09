package kuick.api.rpc

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kuick.api.json.Json

interface Device<R> {
    fun call(request: Request<*>): R
    fun R.getBody(): String

    data class Result<T, R>(
        val parsed: T,
        val original: R
    )
}

data class Request<T>(
    val path: String,
    val queryParameters: Map<String, List<String>>,
    val body: String
)

sealed class Response<T> {
    data class Success<T>(
        val result: T,
        val status: String,
        val message: String? = null
    ) : Response<T>()

    data class Failure<T>(
        val status: String,
        val message: String? = null
    ) : Response<T>()
}

inline fun <reified T> Request<*>.modify(vararg queryParam: Pair<String, List<String>>) = Request<T>(
    path = path,
    queryParameters = queryParameters.toMutableMap().apply { putAll(queryParam) },
    body = body
)

inline fun <reified A : Any> JsonElement.toResponse(): Response<A> {
    val asString = this.toString()
    val status = this.asJsonObject["status"].asString
    return if (status.startsWith("2")) {
        val type = TypeToken.getParameterized(Response.Success::class.java, A::class.java).type
        Json.fromJson<Response.Success<A>>(asString, type)
    } else
        Json.fromJson<Response.Failure<A>>(asString)
}

inline fun <reified A : Any, R> Device<R>.send(request: Request<A>)
    : Device.Result<Response<A>, R>
{
    val result = call(request)
    val parsedResult = Response.Success<A>(
        Json.fromJson(result.getBody()),
        "200"
    )
    return Device.Result(parsedResult, result)
}
