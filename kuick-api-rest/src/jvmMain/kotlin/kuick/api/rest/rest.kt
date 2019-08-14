package kuick.api.rest


import com.google.inject.Injector
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.util.AttributeKey
import kuick.api.core.buildArgsFromObject
import kuick.json.Json.gson
import kuick.json.Json.jsonParser
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend

data class RestRouting(
        val parent: Route,
        val resourceName: String,
        val api: Any,
        val injector: Injector
) {

    //TODO try to delete the need of passing R type
    fun registerRoute(route: RestRoute): Route =
            parent.route(resourceName, method = route.httpMethod) {
                println("REST: ${route.httpMethod.value} /$resourceName -> ${route.handler}") // logging

                handle {

                    val args = buildArgsFromObject(
                            route.handler,
                            jsonParser.parse(call.receiveText()),
                            call.attributes.allKeys.map { it.name to call.attributes[it as AttributeKey<Any>] }.toMap()
                    )

                    val result = route.handler.callSuspend(api, *args.toTypedArray())

                    val jsonResult = gson.toJsonTree(result)

                    call.respondText(jsonResult.toString(), ContentType.Application.Json) // serialization
                }
            }
}

class RestRoute(
        val httpMethod: HttpMethod,
        val handler: KFunction<*>,
        val config: Configuration = Configuration()
) {

    class Configuration
}


