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
import io.ktor.routing.route
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.toMap
import kuick.api.core.asTree
import kuick.api.core.buildArgsFromArray
import kuick.api.core.clazz
import kuick.api.core.getAsTree
import kuick.api.core.parameters.include.IncludeConfiguration
import kuick.api.core.parameters.include.IncludeParam
import kuick.api.core.parameters.include.includeRelatedResources
import kuick.api.core.parameters.preserve.FieldsParam
import kuick.api.core.parameters.preserve.preserveFields
import kuick.json.Json
import kuick.json.Json.gson
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

typealias RpcHandleMap = MutableMap<String, Triple<Any, KFunction<*>, RpcRouting.Configuration>>

data class RpcRouting(
    val parent: Route,
    val api: Any,
    val injector: Injector
) {
    companion object {
        val handleMap: RpcHandleMap = mutableMapOf()
    }


    fun registerAll(): Route {
        val config = Configuration(
            parent.attributes.getOrNull(INCLUDE_CONFIG_ATTRIBUTE_KEY)
                ?.groupBy { it.first.instanceParameter!!.type.clazz.java }
                ?.map { it.key to it.value.map { it.first.name to it.second }.toMap() }
                ?.toMap()
        )
        return parent.route("/rpc/${api.javaClass.simpleName}") {
            api.visitRPC { srvName, method ->
                val path = "/${method.name}"
                val fullPath = "$this$path"
                println("RPC: $fullPath -> $method") // logging

                post(path) {
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

                handleMap[fullPath] = Triple(api, method, config)
            }
        }
    }

    private fun Any.visitRPC(opAction: (String, KFunction<*>) -> Unit) {
        val srvName = javaClass.simpleName
        javaClass.kotlin.declaredMemberFunctions.forEach { function ->
            try {
                opAction(srvName, function)
            } catch (exception: Throwable) {
                println("WARN: invalid public method in controller: $function")
                exception.printStackTrace()
            }
        }
    }

    data class Configuration(
        val includeParameterConfiguration: IncludeConfiguration?
    )
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
        call.attributes.allKeys
            .filter { it.name.startsWith(EXTRA_ARG_PREFIX) }
            .map { it.name.substringAfter(EXTRA_ARG_PREFIX) to call.attributes[it as AttributeKey<Any>] }
            .toMap()
    )

    val responseClass = method.returnType.run {
        return@run if (isSubtypeOf(Collection::class.starProjectedType) or isSubtypeOf(Array<Any>::class.starProjectedType)) {
            this.arguments[0].type!!.clazz
        } else {
            this.clazz
        }
    }.java

    val result = method.callSuspend(api, *args.toTypedArray())
    val jsonResult = gson.toJsonTree(result)

    //TODO handle case when we include smth just to cut it out when filtering
    call.attributes.getOrNull(INCLUDE_ATTRIBUTE_KEY)?.let {
        val configuration = config.includeParameterConfiguration!!
        val includeParam =
            IncludeParam.create(queryParameters.getAsTree("\$include"), responseClass, configuration)
        jsonResult.includeRelatedResources(includeParam, configuration, responseClass)
    }

    call.attributes.getOrNull(FIELDS_ATTRIBUTE_KEY)?.let {
        val fieldsParam = FieldsParam.create(it.asTree(), responseClass)
        jsonResult.preserveFields(fieldsParam)
    }
    return jsonResult
}

