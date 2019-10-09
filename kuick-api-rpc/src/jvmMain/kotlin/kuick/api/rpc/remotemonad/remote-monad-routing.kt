package kuick.api.rpc.remotemonad

import com.google.inject.Injector
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import kuick.api.json.Json
import kuick.api.rpc.RpcHandleMap
import kuick.api.rpc.handleRpcRequest

data class RemoteMonadRouting(
    val parent: Route,
    val injector: Injector,
    // val config: Configuration = Configuration()
    val rpcHandleMap: RpcHandleMap
) {
    fun register() {

        val path = "/remoteMonad"
        println("RPC Remote Monad: $path") // logging

        parent.post(path) {
            val requestBindings: MutableMap<String, Any> = mutableMapOf()

            val remoteMonadRequest: RemoteMonadRequest = Json.fromJson(call.receiveText())
            val remoteMonadRequestBindings: Map<RemoteMonadCommand, String> = remoteMonadRequest.commands
                .map { it to it.resultBindingName }
                .toMap()

            if (remoteMonadRequest.type == RemoteMonadRequest.Type.SYNC) {
                remoteMonadRequest.commands.forEach { remoteMonadCommand ->
                    val (api, method, config) = rpcHandleMap[remoteMonadCommand.path]!! // TODO
                    val result = handleRpcRequest(
                        method,
                        remoteMonadCommand.body,
                        remoteMonadCommand.queryParameters,
                        api,
                        config
                    )

                    remoteMonadRequestBindings[remoteMonadCommand]?.let {
                        requestBindings[it] = result
                    }
                }

                call.respondText(
                    Json.toJson(requestBindings[remoteMonadRequest.returnBindingName]!!), //TODO
                    ContentType.Application.Json
                )
            } else {
                //TODO handle async calls
            }
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


