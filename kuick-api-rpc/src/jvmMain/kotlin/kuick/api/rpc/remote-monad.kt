package kuick.api.rpc


import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.monad.monad
import arrow.core.fix
import arrow.data.ForKleisli
import arrow.data.Reader
import arrow.data.extensions.kleisli.monad.binding
import arrow.data.fix
import arrow.data.flatMap
import arrow.typeclasses.MonadContinuation
import kuick.api.core.buildArgsFromArray
import kuick.api.core.toJson
import kuick.api.core.toJsonArray
import kuick.json.Json
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.jvmName

typealias Remote = Reader<Device, String?>

private inline fun <reified T : Any> sendCommand(command: Command<T>): Remote = Remote { device ->
    Id(
            if (T::class.isInstance(Unit)) {
                device.async(command.serialize())
                null
            } else {
//        Json.fromJson(
                device.sync(command.serialize())
//        )
            }
    )
}

class Command<T>(
        val serviceClass: KClass<out Any>,
        val method: KFunction<T>,
        val params: Collection<Any?>
) {
    fun serialize(): String = "${((method as FunctionReference).owner as KClass<*>).jvmName}/${method.name}/${params.map { "$it;" }}"

    companion object {
        fun <T> deserialize(value: String): Command<T> {
            val (serviceClassName, methodName, paramsString) = value.split("/")
            val service = Class.forName(serviceClassName.substringBefore("Client")).kotlin
            val method = service.declaredFunctions.find { it.name == methodName } as KFunction<T>
                    ?: throw RuntimeException("TODO") // TODO
            val paramsJson = paramsString.split(";").toJson().toJsonArray() //TODO
            val params = buildArgsFromArray(method, paramsJson, emptyMap())
            return Command(service, method, params)
        }
    }
}

interface Device {
    fun async(command: String): Unit
    fun sync(command: String): String //TODO change to T
}

fun send(device: Device, remote: Remote): String? = remote.run(device).fix().extract()

fun send(device: Device, block: suspend MonadContinuation<Kind<Kind<ForKleisli, ForId>, Device>, *>.() -> String?): String? =
        send(device, binding(Id.monad(), block).fix())

fun <T : Any> getInstance(clazz: KClass<T>): T =
        when (clazz) {
            ResourceApi::class -> ResourceApi()
            else -> throw RuntimeException("Service not found")
        } as T

class MockDevice : Device {
    override fun sync(command: String): String {
        val command = Command.deserialize<Any>(command)
        val api = getInstance(command.serviceClass)
        return Json.toJson(
                command.method.call(api, *command.params.toTypedArray())
        )
    }

    override fun async(command: String): Unit {
        val command = Command.deserialize<Any>(command)
        val api = getInstance(command.serviceClass)
        command.method.call(api, *command.params.toTypedArray())
    }

}


data class Resource(
        val id: Int,
        val value: String
)

class ResourceApi {
    fun getOne(): Resource {
        return Resource(1, "someTest")
    }

    fun printSmth(s: String): Unit {
        println("Smth $s")
    }
}


open class ResourceApiClient {
    fun getOne(): Remote = sendCommand(Command(this::class, ResourceApi::getOne, emptyList()))
    fun printSmth(s: String): Remote = sendCommand(Command(this::class, ResourceApi::printSmth, listOf(s)))
}

class RemoteMonadResourceApiClient(private val api: ResourceApiClient) {
    suspend fun MonadContinuation<Kind<Kind<ForKleisli, ForId>, Device>, *>.getOne(): Resource? =
            bind { api.getOne() }?.let { Json.fromJson(it) }

    suspend fun MonadContinuation<Kind<Kind<ForKleisli, ForId>, Device>, *>.printSmth(s: String) {
        bind { api.printSmth(s) }
    }

}


suspend fun main() {
    val device = MockDevice()

    val api = ResourceApiClient()

    send(device,
            api.printSmth("1").flatMap {
                api.printSmth("2")
            }
    )

    println()
    println()

    println(
            send(device, api.getOne())
    )

    println()
    println()

    println(
            send(device) {
                val (a) = api.getOne()
                bind { api.printSmth("1") }
                a
            }
    )


}

