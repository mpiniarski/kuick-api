package kuick.api.rpc

import com.google.inject.Singleton
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationResponse
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import kuick.api.json.Json
import kuick.api.rpc.aggregation.AggregatingDevice

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
        "/rpc/ResourceApi/getOne",
        emptyMap(),
        body = "[\"$id\"]"
    )

    fun getAll(): Request<List<Resource>> = Request(
        "/rpc/ResourceApi/getAll",
        emptyMap(),
        body = ""
    )

    fun incField2(id: String): Request<Unit> = Request(
        "/rpc/ResourceApi/incField2",
        emptyMap(),
        body = "[\"$id\"]"
    )
}

class OtherResourceApiClient {
    fun getOne(id: String): Request<OtherResource> = Request(
        "/rpc/OtherResourceApi/getOne",
        emptyMap(),
        body = "[\"$id\"]"
    )
}

val resourceApi = ResourceApiClient()
val otherResourceApi = OtherResourceApiClient()

val TestApplicationEngine.device: Device<TestApplicationResponse>
    get() = object : Device<TestApplicationResponse> {
        override fun call(request: Request<*>): TestApplicationResponse =
            handleRequest(HttpMethod.Post, "${request.path}?" +
                request.queryParameters.entries.joinToString("&") { "${it.key}=${it.value}" }) {
                setBody(request.body)
            }.let {
                if (!it.requestHandled) throw RuntimeException("Request not handled")
                else if(it.response.status() != HttpStatusCode.OK){
                    throw RuntimeException("Request handled with error: ${it.response.status()}")
                }
                it.response
            }

        override fun TestApplicationResponse.getBody(): String = this.content!!
    }

val TestApplicationEngine.aggregatingDevice: AggregatingDevice<TestApplicationResponse>
    get() = object : AggregatingDevice<TestApplicationResponse>(device) {
        override fun aggregatedCall(packet: List<Request<*>>): TestApplicationResponse =
            handleRequest(HttpMethod.Post, "/rpc/aggregation") {
                setBody(Json.toJson(packet))
            }.response
    }


