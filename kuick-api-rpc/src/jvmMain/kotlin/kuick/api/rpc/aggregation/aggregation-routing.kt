package kuick.api.rpc.aggregation

import com.google.inject.Injector
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import kuick.api.rpc.RpcHandleMap
import kuick.api.rpc.handleRpcRequest
import kuick.json.Json

data class AggregationRouting(
    val parent: Route,
    val injector: Injector,
    // val config: Configuration = Configuration()
    val rpcHandleMap: RpcHandleMap
) {
    fun register() {

        val path = "/rpc/aggregation"
        println("RPC aggregation: $path") // logging

        parent.post(path) {
            val requests: Array<Request<Any>> = Json.fromJson(call.receiveText())

            val results = requests.map { request ->
                try {
                    val (api, method, config) = rpcHandleMap[request.path]!! // TODO
                    val result = handleRpcRequest(
                        method,
                        request.body,
                        request.queryParameters,
                        api,
                        config
                    )
                    mapOf(
                        "status" to "200",
                        "result" to result
                    )
                } catch (exception: Throwable) { //TODO other exceptions handling
                    mapOf(
                        "status" to "500",
                        "message" to exception.message
                    )
                }
            }

            call.respondText(
                Json.toJson(results),
                ContentType.Application.Json
            )
        }
    }
}

// class Configuration {
//     var withFieldsParameter: Boolean = false
//         private set
//     var includeParameterConfiguration: Map<String, suspend (id: String) -> Any>? = null
//         private set
//
//     var withRemoteMonad: Boolean = false
//         private set
//
//     fun withFieldsParameter() {
//         withFieldsParameter = true
//     }
//
//     fun withIncludeParameter(vararg configuration: Pair<KProperty<Any?>, suspend (id: String) -> Any>) {
//         includeParameterConfiguration = configuration
//             .map { it.first.name to it.second }.toMap()
//     }
//
//     fun withRemoteMonad() {
//         withRemoteMonad = true
//     }
// }


