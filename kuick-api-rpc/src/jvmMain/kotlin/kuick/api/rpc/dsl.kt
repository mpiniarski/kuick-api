package kuick.api.rpc

import com.google.inject.Injector
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.request.ApplicationRequest
import io.ktor.routing.Route
import io.ktor.util.AttributeKey
import kotlin.reflect.KProperty

inline fun <reified T> Route.rpcRoute(injector: Injector, build: Route.() -> Unit = {}): Route =
    rpcRoute(injector, T::class.java, build)

inline fun <T> Route.rpcRoute(
    injector: Injector,
    clazz: Class<T>,
    build: Route.() -> Unit = {}
): Route {
    val api = injector.getInstance(clazz)!!
    return RpcRouting(this, api, injector)
        .registerAll()
        .apply(build)
}

const val EXTRA_ARG_PREFIX = "\$extraArg_"

fun <T : Any> Route.withParameter(name: String, body: ApplicationRequest.() -> T) {
    intercept(ApplicationCallPipeline.Call) {
        call.attributes.put(AttributeKey<T>("$EXTRA_ARG_PREFIX$name"), body(call.request))
    }
}

const val FIELDS_ATTRIBUTE_NAME = "\$fields"
val FIELDS_ATTRIBUTE_KEY = AttributeKey<String>(FIELDS_ATTRIBUTE_NAME)

fun Route.withFieldsParameter() {
    intercept(ApplicationCallPipeline.Call) {
        call.request.queryParameters[FIELDS_ATTRIBUTE_NAME]?.let {
            call.attributes.put(FIELDS_ATTRIBUTE_KEY, it)
        }
    }
}

const val INCLUDE_ATTRIBUTE_NAME = "\$include"
val INCLUDE_ATTRIBUTE_KEY = AttributeKey<String>(INCLUDE_ATTRIBUTE_NAME)
val INCLUDE_CONFIG_ATTRIBUTE_KEY = AttributeKey<Map<String, suspend (id: String) -> Any>>("\$include_config")

fun Route.withIncludeParameter(vararg configuration: Pair<KProperty<Any?>, suspend (id: String) -> Any>) {
    this.attributes.put(INCLUDE_CONFIG_ATTRIBUTE_KEY, configuration.map { it.first.name to it.second }.toMap())
    intercept(ApplicationCallPipeline.Call) {
        call.request.queryParameters[INCLUDE_ATTRIBUTE_NAME]?.let {
            call.attributes.put(INCLUDE_ATTRIBUTE_KEY, it)
        }
    }
}
