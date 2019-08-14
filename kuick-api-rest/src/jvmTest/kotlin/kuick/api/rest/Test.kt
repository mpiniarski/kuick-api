package kuick.api.rest

import com.google.inject.Guice.createInjector
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.AttributeKey
import junit.framework.Assert.assertEquals
import kuick.api.core.toJson
import org.junit.Test
import javax.inject.Singleton

class Test {

    data class Resource(
        val id: String,
        val field1: String,
        val field2: Int,
        val otherResource: String? = null
    )

    data class OtherResource(
        val id: String,
        val field1: String,
        val field2: Int,
        val otherResource2: String? = null
    )

    data class OtherResource2(
        val id: String,
        val field1: String
    )

    @Singleton
    class ResourceApi {
        private val map = mapOf(
            "resource-id-1" to Resource(
                id = "resource-id-1",
                field1 = "test1",
                field2 = 10
            ),
            "resource-id-2" to Resource(
                id = "resource-id-2",
                field1 = "test2",
                field2 = 11,
                otherResource = "other-resource-id-2"
            )
        )

        fun getOne(id: String): Resource = map[id] ?: throw RuntimeException("404")

        fun getAll(): List<Resource> = map.values.toList()
        fun test(): String = "TEST"
    }

    @Singleton
    class OtherResourceApi {
        private val map = mapOf(
            "other-resource-id-2" to OtherResource(
                id = "other-resource-id-2",
                field1 = "test1",
                field2 = 10,
                otherResource2 = "other-resource-2-id-1"
            )
        )

        fun getOne(id: String): OtherResource = map[id] ?: throw RuntimeException("404")
    }

    @Singleton
    class OtherResource2Api {
        private val map = mapOf(
            "other-resource-2-id-1" to OtherResource2(
                id = "other-resource-2-id-1",
                field1 = "test1"
            )
        )

        fun getOne(id: String): OtherResource2 = map[id] ?: throw RuntimeException("404")
    }

    //TODO Pipeline : discuss
    private fun Route.withSomeCheck(path: String = "", build: Route.() -> Unit) = route(path) {
        intercept(ApplicationCallPipeline.Call) {
            if (false) {// e.g. if (!ADMIN_USERS.contains(session.userId)) {
                call.respond(HttpStatusCode.Forbidden)
                finish()
            }
        }
        build()
    }

    //TODO Providing additional parameters (later passed to handler method) discuss
    private fun Route.withSomeAdditionalParameter(path: String = "", build: Route.() -> Unit) = route(path) {
        intercept(ApplicationCallPipeline.Call) {
            call.attributes.put(AttributeKey("test"), "test")
        }
        build()
    }

    private fun restTest(block: TestApplicationEngine.() -> Unit) {
        val injector = createInjector()
        withTestApplication {

            application.routing {
                restRoute<ResourceApi>(injector, "resources") {

                    withSomeCheck {
                        withSomeAdditionalParameter {
                            get(ResourceApi::getAll) {}
                        }
                    }
                }

            }

            block()
        }
    }

    @Test
    fun test() = restTest {
        assertEquals(
            listOf(
                mapOf(
                    "id" to "resource-id-1",
                    "field1" to "test1",
                    "field2" to 10
                ),
                mapOf(
                    "id" to "resource-id-2",
                    "field1" to "test2",
                    "field2" to 11,
                    "otherResource" to "other-resource-id-2"
                )
            ).toJson(),
            handleRequest(HttpMethod.Get, "/resources").response.content
        )
    }

}


