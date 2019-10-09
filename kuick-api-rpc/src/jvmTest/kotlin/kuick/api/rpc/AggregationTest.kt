package kuick.api.rpc

import com.google.inject.Guice
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import kuick.api.rpc.aggregation.Tuple
import kuick.api.rpc.aggregation.Tuple4
import kuick.api.rpc.aggregation.aggregationRoute
import kuick.api.rpc.aggregation.send
import org.junit.Test

class AggregationTest {
    private fun rpcAggregationTest(block: TestApplicationEngine.() -> Unit) {
        val injector = Guice.createInjector()
        withTestApplication {
            application.routing {
                route("/rpc") {
                    rpcRoute<ResourceApi>(injector)
                    rpcRoute<OtherResourceApi>(injector)
                    aggregationRoute(injector)
                }
            }

            block()
        }
    }

    @Test
    fun `should handle aggregation of multiple resources query`() = rpcAggregationTest {
        val device = aggregatingDevice


        val (result, _) = device.send(
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
        val device = aggregatingDevice

        val (otherResourcesResult, _) = device.send(
            otherResourceApi.getOne("other-resource-id-1"),
            otherResourceApi.getOne("other-resource-id-2")
        )

        assertEquals(
            Tuple(
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
