package kuick.api.rpc

import com.google.inject.Guice
import io.ktor.http.HttpMethod
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import kuick.api.core.parameters.include.InvalidIncludeParamException
import kuick.api.core.parameters.preserve.InvalidFieldParamException
import org.junit.Test

class Test {

    private fun rpcTest(block: TestApplicationEngine.() -> Unit) {
        val injector = Guice.createInjector()
        withTestApplication {
            application.routing {
                route("/rpc") {
                    withFieldsParameter()
                    withIncludeParameter(
                        Resource::otherResource to { id ->
                            injector.getInstance(OtherResourceApi::class.java).getOne(id)
                        },
                        OtherResource::otherResource2 to { id ->
                            injector.getInstance(OtherResource2Api::class.java).getOne(id)
                        }
                    )
                    rpcRoute<ResourceApi>(injector) {
                        withParameter("test") { "test" }
                    }
                    rpcRoute<OtherResourceApi>(injector) {
                        withParameter("test2") { "test2" }
                    }
                }
            }

            block()
        }
    }

    @Test
    fun test() = rpcTest {
        assertEquals(
            Response.Success(
                listOf(
                    Resource(
                        id = "resource-id-1",
                        field1 = "test1",
                        field2 = 10
                    ),
                    Resource(
                        id = "resource-id-2",
                        field1 = "test2",
                        field2 = 11,
                        otherResource = "other-resource-id-1"
                    ),
                    Resource(
                        id = "resource-id-3",
                        field1 = "test3",
                        field2 = 12,
                        otherResource = "other-resource-id-2"
                    )
                ),
                "200"
            ),
            device.send(
                resourceApi.getAll()
            ).parsed
        )
    }

    //TODO for some reason doesn't work if this class is inside field_test()
    data class FieldTestResult(
        val field1: String
    )

    @Test
    fun field_test() = rpcTest {
        assertEquals(
            Response.Success(
                listOf(
                    FieldTestResult("test1"),
                    FieldTestResult("test2"),
                    FieldTestResult("test3")
                ),
                "200"
            ),
            device.send(
                resourceApi.getAll().modify<List<FieldTestResult>>("\$fields" to listOf("field1"))
            ).parsed
        )
    }

    data class IncludeTestResult(
        val id: String,
        val otherResource: OtherResource? = null
    )

    @Test
    fun include_test() = rpcTest {
        assertEquals(
            Response.Success(
                listOf(
                    IncludeTestResult("resource-id-1"),
                    IncludeTestResult(
                        "resource-id-2", OtherResource(
                            "other-resource-id-1",
                            "test1",
                            10,
                            "other-resource-2-id-1"
                        )
                    ),
                    IncludeTestResult(
                        "resource-id-3", OtherResource(
                            "other-resource-id-2",
                            "test2",
                            11,
                            "other-resource-2-id-1"
                        )
                    )
                ),
                "200"
            ),
            device.send(
                resourceApi.getAll().modify<List<IncludeTestResult>>("\$include" to listOf("otherResource"))
            ).parsed
        )
    }

    @Test(expected = InvalidFieldParamException::class)
    fun `should throw exception on wrongly defined fields parameter - when trying to preserve field of nested resource without preserving field itself`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$fields" to listOf("id", "otherResource.id"))
            )
        }

    @Test(expected = InvalidFieldParamException::class)
    fun `should throw exception on wrongly defined fields parameter - when trying to preserve field that don't exist in a model`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$fields" to listOf("id", "someField"))
            )
        }

    @Test(expected = InvalidFieldParamException::class)
    fun `should throw exception on wrongly defined fields parameter - on nested resource when trying to preserve field of nested resource without preserving field itself`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$fields" to listOf("id", "otherField.otherField2.id"))
            )
        }

    @Test(expected = InvalidIncludeParamException::class)
    fun `should throw exception on wrongly defined include parameter - when include is not supported for specified field`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$include" to listOf("id"))
            )
        }

    @Test(expected = InvalidIncludeParamException::class)
    fun `should throw exception on wrongly defined include parameter - when trying to include field of nested resource without including resource itself`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$include" to listOf("otherResource.id"))
            )
        }

    @Test(expected = InvalidIncludeParamException::class)
    fun `should throw exception on wrongly defined include parameter - on nested resource when include is not supported for specified field`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$include" to listOf("otherResource", "otherResource.id"))
            )
        }

    @Test(expected = InvalidIncludeParamException::class)
    fun `should throw exception on wrongly defined include parameter - when trying to include field that don't exist in a model`() =
        rpcTest {
            device.send(
                resourceApi.getAll().modify("\$include" to listOf("id", "someField"))
            )
        }

    data class NestedFieldsInFieldsTestResult(
        val id: String,
        val otherResource: OtherResource? = null
    ) {
        data class OtherResource(val id: String)
    }

    @Test
    fun `should handle nested fields in fields parameter`() = rpcTest {
        assertEquals(
            Response.Success(
                listOf(
                    NestedFieldsInFieldsTestResult("resource-id-1"),
                    NestedFieldsInFieldsTestResult(
                        "resource-id-2", NestedFieldsInFieldsTestResult.OtherResource(
                            "other-resource-id-1"
                        )
                    ),
                    NestedFieldsInFieldsTestResult(
                        "resource-id-3", NestedFieldsInFieldsTestResult.OtherResource(
                            "other-resource-id-2"
                        )
                    )
                ),
                "200"
            ),
            device.send(
                resourceApi.getAll().modify<List<NestedFieldsInFieldsTestResult>>(
                    "\$fields" to listOf("id", "otherResource", "otherResource.id"),
                    "\$include" to listOf("otherResource")
                )
            ).parsed
        )
    }

    data class NestedFieldsInIncludeTestResult(
        val id: String,
        val otherResource: OtherResource? = null
    ) {
        data class OtherResource(
            val id: String,
            val field1: String,
            val field2: Int,
            val otherResource2: OtherResource2? = null
        )
    }

    @Test
    fun `should handle nested fields in include parameter`() = rpcTest {
        assertEquals(
            Response.Success(
                listOf(
                    NestedFieldsInIncludeTestResult("resource-id-1"),
                    NestedFieldsInIncludeTestResult(
                        "resource-id-2", NestedFieldsInIncludeTestResult.OtherResource(
                            "other-resource-id-1",
                            "test1",
                            10,
                            OtherResource2(
                                "other-resource-2-id-1",
                                "test1"
                            )
                        )
                    ),
                    NestedFieldsInIncludeTestResult(
                        "resource-id-3", NestedFieldsInIncludeTestResult.OtherResource(
                            "other-resource-id-2",
                            "test2",
                            11,
                            OtherResource2(
                                "other-resource-2-id-1",
                                "test1"
                            )
                        )
                    )
                ),
                "200"
            ),
            device.send(
                resourceApi.getAll().modify<List<NestedFieldsInIncludeTestResult>>(
                    "\$fields" to listOf("id", "otherResource"),
                    "\$include" to listOf("otherResource", "otherResource.otherResource2")
                )
            ).parsed
        )
    }

    @Test
    fun `should handle additional parameters`() = rpcTest {
        assertEquals(
            "\"test\"",
            handleRequest(
                HttpMethod.Post,
                "/rpc/ResourceApi/test"
            ).response.content
        )
    }
}
