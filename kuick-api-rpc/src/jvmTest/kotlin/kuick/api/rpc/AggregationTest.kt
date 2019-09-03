package kuick.api.rpc

import com.google.inject.Guice
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import kuick.api.rpc.aggregation.Device
import kuick.api.rpc.aggregation.Request
import kuick.api.rpc.aggregation.Response
import kuick.api.rpc.aggregation.Tuple4
import kuick.api.rpc.aggregation.aggregationRoute
import kuick.api.rpc.aggregation.send
import kuick.json.Json
import org.junit.Test
import javax.inject.Singleton

class AggregationTest {

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
                otherResource = "other-resource-id-1"
            ),
            "resource-id-3" to Resource(
                id = "resource-id-3",
                field1 = "test3",
                field2 = 12,
                otherResource = "other-resource-id-2"
            )
        )

        fun getOne(id: String): Resource = map[id] ?: throw RuntimeException("404")

        fun getAll(): List<Resource> = map.values.toList()
        fun test(test: String): String = test
    }

    @Singleton
    class OtherResourceApi {
        private val map = mapOf(
            "other-resource-id-1" to OtherResource(
                id = "other-resource-id-1",
                field1 = "test1",
                field2 = 10,
                otherResource2 = "other-resource-2-id-1"
            ),
            "other-resource-id-2" to OtherResource(
                id = "other-resource-id-2",
                field1 = "test2",
                field2 = 11,
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

    class ResourceApiClient {
        fun getOne(id: String): Request<Resource> = Request(
            "getOne",
            "/rpc/ResourceApi/getOne",
            emptyMap(),
            body = "[\"$id\"]"
        )

        fun getAll(): Request<List<Resource>> = Request(
            "getAll",
            "/rpc/ResourceApi/getAll",
            emptyMap(),
            body = ""
        )

        fun incField2(id: String): Request<Unit> = Request(
            "incField2",
            "/rpc/ResourceApi/incField2",
            emptyMap(),
            body = "[\"$id\"]"
        )
    }

    class OtherResourceApiClient {
        fun getOne(id: String): Request<OtherResource> = Request(
            "getOne",
            "/rpc/OtherResourceApi/getOne",
            emptyMap(),
            body = "[\"$id\"]"
        )
    }

    private fun rpcAggregationTest(block: TestApplicationEngine.() -> Unit) {
        val injector = Guice.createInjector()
        withTestApplication {
            application.routing {
                rpcRoute<ResourceApi>(injector)
                rpcRoute<OtherResourceApi>(injector)
                aggregationRoute(injector)
            }

            block()
        }
    }

    private val TestApplicationEngine.device: Device
        get() = object : Device {
            override fun send(packet: List<Request<*>>): String =
                handleRequest(HttpMethod.Post, "/rpc/aggregation") {
                    setBody(Json.toJson(packet))
                }.response.content!! //TODO
        }

    private val resourceApi = ResourceApiClient()
    private val otherResourceApi = OtherResourceApiClient()

    @Test
    fun `should handle aggregation of multiple resources query`() = rpcAggregationTest {

        val result = send(
            device,
            resourceApi.getOne("resource-id-1"),
            resourceApi.getOne("resource-id-2"),
            otherResourceApi.getOne("other-resource-id-1"),
            otherResourceApi.getOne("other-resource-id-2")
        )

        assertEquals(
            Tuple4(
                Response.Success(
                    Resource(
                        id = "resource-id-1",
                        field1 = "test1",
                        field2 = 10
                    ),
                    "200"
                ),
                Response.Success(
                    Resource(
                        id = "resource-id-2",
                        field1 = "test2",
                        field2 = 11,
                        otherResource = "other-resource-id-1"
                    ),
                    "200"
                ),
                Response.Success(
                    OtherResource(
                        id = "other-resource-id-1",
                        field1 = "test1",
                        field2 = 10,
                        otherResource2 = "other-resource-2-id-1"
                    ),
                    "200"
                ),
                Response.Success(
                    OtherResource(
                        id = "other-resource-id-2",
                        field1 = "test2",
                        field2 = 11,
                        otherResource2 = "other-resource-2-id-1"
                    ),
                    "200"
                )
            ),
            result
        )
    }

    @Test
    fun `should handle aggregation of batch query`() = rpcAggregationTest {

        val otherResourcesResult = send(
            device,
            otherResourceApi.getOne("other-resource-id-1"),
            otherResourceApi.getOne("other-resource-id-2")
        )

        assertEquals(
            listOf(
                Response.Success(
                    OtherResource(
                        id = "other-resource-id-1",
                        field1 = "test1",
                        field2 = 10,
                        otherResource2 = "other-resource-2-id-1"
                    ),
                    "200"
                ),
                Response.Success(
                    OtherResource(
                        id = "other-resource-id-2",
                        field1 = "test2",
                        field2 = 11,
                        otherResource2 = "other-resource-2-id-1"
                    ),
                    "200"
                )
            ),
            otherResourcesResult
        )
    }
}

// TODO example
// val resources = send(device, resourceApi.getAll()).let { (response) ->
//     when (response) {
//         is Response.Success ->
//             response.result
//         is Response.Failure ->
//             throw java.lang.RuntimeException("Error")
//     }
// }
