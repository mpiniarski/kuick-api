package kuick.api.rpc.aggregation

data class Request<T>(
    val method: String,
    val path: String,
    val queryParameters: Map<String, List<String>>,
    val body: String
)

sealed class Response<T> {
    data class Success<T>(val result: T, val status: String, val message: String? = null) : Response<T>()
    data class Failure<T>(val status: String, val message: String? = null) : Response<T>()
}

interface Device {
    fun aggregatedSend(packet: List<Request<*>>): String
}

