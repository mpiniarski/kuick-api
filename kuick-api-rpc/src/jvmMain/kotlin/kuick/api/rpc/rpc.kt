package kuick.api.rpc

import com.google.inject.Injector
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.AttributeKey
import kuick.api.core.buildArgsFromArray
import kuick.json.Json.gson
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions


data class RpcRouting(
        val parent: Route,
        val api: Any,
        val injector: Injector,
        val config: Configuration = Configuration()
) {
    fun registerAll() {
        api.visitRPC { srvName, method ->
            val path = "/rpc/$srvName/${method.name}"
            println("RPC: $path -> $method") // logging
            parent.post(path) {
                val args = buildArgsFromArray(
                        method,
                        gson.toJsonTree(call.receiveText()),
                        call.attributes.allKeys.map { it.name to call.attributes[it as AttributeKey<Any>] }.toMap()
                )

                val result = method.callSuspend(api, *args.toTypedArray())
                val jsonResult = gson.toJsonTree(result)

                call.respondText(jsonResult.toString(), ContentType.Application.Json)
            }
        }
    }

    fun Any.visitRPC(opAction: (String, KFunction<*>) -> Unit) {
        val srvName = javaClass.simpleName
        javaClass.kotlin.memberFunctions.forEach { function ->
            try {
                opAction(srvName, function)
            } catch (exception: Throwable) {
                println("WARN: invalid public method in controller: $function")
                exception.printStackTrace()
            }
        }
    }

    class Configuration
}


