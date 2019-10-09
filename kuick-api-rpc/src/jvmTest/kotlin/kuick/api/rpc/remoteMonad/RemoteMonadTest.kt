package kuick.api.rpc.remoteMonad

import com.google.inject.Guice
import io.ktor.http.HttpMethod
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import junit.framework.Assert.assertEquals
import kuick.api.json.Json
import kuick.api.rpc.remotemonad.Device
import kuick.api.rpc.remotemonad.Remote
import kuick.api.rpc.remotemonad.RemoteMonadCommand
import kuick.api.rpc.remotemonad.RemoteMonadRequest
import kuick.api.rpc.remotemonad.remoteMonadRoute
import kuick.api.rpc.remotemonad.send
import kuick.api.rpc.remotemonad.sendCommand
import kuick.api.rpc.remotemonad.sendCommand2
import kuick.api.rpc.rpcRoute
import org.junit.Test
import javax.inject.Singleton

class RemoteMonadTest {

    data class Resource(
        val id: String,
        val field1: String,
        var field2: Int,
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
        fun incField2(id: String) {
            map[id]?.let {
                it.field2 += 1
            }
        }
    }

    class ResourceApiClient {
        fun getOne(id: String): Remote<Resource> = sendCommand(
            RemoteMonadCommand(
                "getOne",
                "/rpc/ResourceApi/getOne",
                emptyMap(),
                body = "[\"$id\"]",
                resultBindingName = "last"
            )
        )

        fun getAll(): Remote<List<Resource>> = sendCommand2(
            RemoteMonadCommand(
                "getAll",
                "/rpc/ResourceApi/getAll",
                emptyMap(),
                body = "",
                resultBindingName = "last"
            )
        )

        fun incField2(id: String): Remote<Unit> = sendCommand(
            RemoteMonadCommand(
                "incField2",
                "/rpc/ResourceApi/incField2",
                emptyMap(),
                body = "[\"$id\"]",
                resultBindingName = "last"
            )
        )
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

    class OtherResourceApiClient {
        fun getOne(id: String): Remote<OtherResource> = sendCommand(
            RemoteMonadCommand(
                "getOne",
                "/rpc/OtherResourceApi/getOne",
                emptyMap(),
                body = "[\"$id\"]",
                resultBindingName = "last"
            )
        )
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

    private val resourceApi = ResourceApiClient()
    private val otherResourceApi = OtherResourceApiClient()

    private val TestApplicationEngine.device: TestDevice
        get() {
            return TestDevice(this)
        }

    class TestDevice(val engine: TestApplicationEngine) : Device {

        override fun async(packet: List<RemoteMonadCommand>) {
            val request = RemoteMonadRequest(
                type = RemoteMonadRequest.Type.ASYNC,
                commands = packet,
                returnBindingName = null
            )
            engine.handleRequest(HttpMethod.Post, "/rpc/remoteMonad") {
                setBody(Json.toJson(request))
            }.response.content
        }

        override fun sync(packet: List<RemoteMonadCommand>): String {
            val request = RemoteMonadRequest(
                type = RemoteMonadRequest.Type.SYNC,
                commands = packet,
                returnBindingName = "last"
            )
            return engine.handleRequest(HttpMethod.Post, "/rpc/remoteMonad") {
                setBody(Json.toJson(request))
            }.response.content!!
        }
    }

    private fun remoteMonadTest(block: TestApplicationEngine.() -> Unit) {
        val injector = Guice.createInjector()
        withTestApplication {
            application.routing {
                route("/rpc") {
                    rpcRoute<ResourceApi>(injector)
                    rpcRoute<OtherResourceApi>(injector)
                    remoteMonadRoute(injector)
                }
            }
            block()
        }
    }

    @Test
    fun test() = remoteMonadTest {
        assertEquals(
            listOf(
                Resource(
                    "resource-id-1",
                    "test1",
                    10
                ),
                Resource(
                    "resource-id-2",
                    "test2",
                    11,
                    "other-resource-id-2"
                )
            ),
            send(device, resourceApi.getAll())
        )
    }

    @Test
    fun test2() = remoteMonadTest {
        assertEquals(
            Resource(
                "resource-id-1",
                "test1",
                10
            ),
            send(device, resourceApi.getOne("resource-id-1"))
        )
    }

    @Test
    fun test3() = remoteMonadTest {
        assertEquals(
            Resource(
                "resource-id-1",
                "test1",
                13
            ),
            send(device) {
                val a = bind { resourceApi.getOne("resource-id-1") }!!
                bind { resourceApi.incField2(a.id) }
                bind { resourceApi.incField2(a.id) }
                bind { resourceApi.incField2(a.id) }
                val b = bind { resourceApi.getOne(a.id) }!!
                b
            }
        )
    }

    @Test
    fun test4() = remoteMonadTest {

        data class Resource2(
            val id: String,
            val field1: String,
            var field2: Int,
            val otherResource: OtherResource? = null
        )

        val list = send(device) { bind { resourceApi.getAll() } }!!

        val list2 = send(device) {
            list.map {
                Resource2(
                    it.id,
                    it.field1,
                    it.field2,
                    it.otherResource?.let { bind { otherResourceApi.getOne(it) } })
            }
        }
    }
}

