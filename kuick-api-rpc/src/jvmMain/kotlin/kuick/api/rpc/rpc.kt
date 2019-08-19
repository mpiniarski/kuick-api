package kuick.api.rpc

import com.google.gson.JsonElement
import com.google.inject.Injector
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import kuick.api.core.buildArgsFromArray
import kuick.api.core.clazz
import kuick.api.core.getAsTree
import kuick.api.core.parameters.include.IncludeParam
import kuick.api.core.parameters.include.includeRelatedResources
import kuick.api.core.parameters.preserve.FieldsParam
import kuick.api.core.parameters.preserve.preserveFields
import kuick.json.Json
import kuick.json.Json.gson
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType

typealias RpcHandleMap = MutableMap<String, Triple<Any, KFunction<*>, RpcRouting.Configuration>>

data class RpcRouting(
    val parent: Route,
    val api: Any,
    val injector: Injector,
    val config: Configuration = Configuration()
) {
    companion object {
        val handleMap: RpcHandleMap = mutableMapOf()
    }

    fun registerAll() {
        api.visitRPC { srvName, method ->
            val path = "/rpc/$srvName/${method.name}"
            println("RPC: $path -> $method") // logging
            parent.post(path) {
                val jsonResult =
                    handleRpcRequest(
                        method,
                        call.receiveText(),
                        call.request.queryParameters.toMap(),
                            // .map { it.key to it.value.first() }.toMap(),
                        api,
                        config
                    )

                call.respondText(jsonResult.toString(), ContentType.Application.Json)

            }
            handleMap[path] = Triple(api, method, config)
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

    class Configuration {
        var withFieldsParameter: Boolean = false
            private set
        var includeParameterConfiguration: Map<String, suspend (id: String) -> Any>? = null
            private set

        fun withFieldsParameter() {
            withFieldsParameter = true
        }

        fun withIncludeParameter(vararg configuration: Pair<KProperty<Any?>, suspend (id: String) -> Any>) {
            includeParameterConfiguration = configuration
                .map { it.first.name to it.second }.toMap()
        }
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.handleRpcRequest(
    method: KFunction<*>,
    body: String,
    queryParameters: Map<String, List<String>>,
    api: Any,
    config: RpcRouting.Configuration
): JsonElement {
    val args = buildArgsFromArray(
        method,
        Json.jsonParser.parse(body),
        call.attributes.allKeys.map { it.name to call.attributes[it as AttributeKey<Any>] }.toMap()
    )

    val responseClass = method.returnType.run {
        return@run if (isSubtypeOf(List::class.starProjectedType)) {
            this.arguments[0].type!!.clazz
        } else {
            this.clazz
        }
    }.java

    val result = method.callSuspend(api, *args.toTypedArray())
    val jsonResult = gson.toJsonTree(result)

    //TODO handle case when we include smth just to cut it out when filtering
    if (config.includeParameterConfiguration != null && "\$include" in queryParameters) {
        val configuration = config.includeParameterConfiguration!!
        val includeParam =
            IncludeParam.create(queryParameters.getAsTree("\$include"), responseClass, configuration)
        jsonResult.includeRelatedResources(includeParam, configuration)
    }

    if (config.withFieldsParameter && "\$fields" in queryParameters) {
        val fieldsParam = FieldsParam.create(queryParameters.getAsTree("\$fields"), responseClass)
        jsonResult.preserveFields(fieldsParam)
    }
    return jsonResult
}

