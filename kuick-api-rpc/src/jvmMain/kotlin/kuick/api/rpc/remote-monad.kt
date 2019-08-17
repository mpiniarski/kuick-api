package kuick.api.rpc

import arrow.Kind
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.id.monad.monad
import arrow.core.fix
import arrow.data.ForStateT
import arrow.data.Reader
import arrow.data.ReaderPartialOf
import arrow.data.StateT
import arrow.data.extensions.kleisli.applicative.applicative
import arrow.data.extensions.kleisli.monad.monad
import arrow.data.extensions.statet.monad.binding
import arrow.data.fix
import arrow.data.run
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

// typealias Remote<T> = ReaderT<ForState, Device, State<List<Command<T>>, T?>>
typealias Remote<T> = StateT<ReaderPartialOf<Device>, List<Command<T>>, T?>

private inline fun <reified T : Any> sendCommand(command: Command<T>): Remote<T> =
    StateT<ReaderPartialOf<Device>, List<Command<T>>, T?>(Reader.applicative(Id.monad())) { commands ->
        Reader { device ->
            if (T::class.isInstance(Unit)) {
                // device.async(command.serialize())
                Id(Tuple2(commands + command, null))
            } else {
                Id(
                    Tuple2(
                        emptyList(),
                        Json.fromJson(
                            device.sync((commands + command).serialize())
                        )
                    )
                )
            }
        }
    }

class Command<T>(
    val serviceClass: KClass<out Any>,
    val method: KFunction<T>,
    val params: Collection<Any?>
) {
    fun serialize(): String =
        "${((method as FunctionReference).owner as KClass<*>).jvmName}/${method.name}/${params.map { "$it;" }}"

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

private const val COMMAND_SEPARATOR = "///"

fun <T> List<Command<T>>.serialize() = this.joinToString(separator = COMMAND_SEPARATOR) { it.serialize() }

interface Device {
    fun async(packet: String): Unit
    fun sync(packet: String): String
}

fun <T> send(device: Device, remote: Remote<T>): T? {
    val (commands, result) = remote.run(Reader.monad(Id.monad()), emptyList())
        .run(device)
        .fix().extract()
    return if (commands.isNotEmpty()) {
        device.async(commands.serialize())
        null
    } else {
        result
    }
}

fun <T> send(
    device: Device,
    block: suspend MonadContinuation<Kind<Kind<ForStateT, ReaderPartialOf<Device>>, List<Command<*>>>, *>.() -> T
): T? =
    send(
        device,
        binding<ReaderPartialOf<Device>, List<Command<*>>, T?>(Reader.monad(Id.monad()), block).fix() as Remote<T>
    )

fun <T : Any> getInstance(clazz: KClass<T>): T =
    when (clazz) {
        ResourceApi::class -> ResourceApi()
        else -> throw RuntimeException("Service not found")
    } as T

class MockDevice :
    Device { // normally device should send over http and on server side there should be a way to deserialize and handle
    override fun sync(packet: String): String =
        handleCommands(packet)
            .last()
            .let { Json.toJson(it) }

    override fun async(packet: String): Unit {
        handlePacket(packet) { command ->
            val api = getInstance(command.serviceClass)
            command.method.call(api, *command.params.toTypedArray())
        }
    }

    private fun handlePacket(command: String, handleCommand: (Command<Any>) -> Any): List<Any> =
        command.split(COMMAND_SEPARATOR)
            .map { Command.deserialize<Any>(it) }
            .map(handleCommand)

    private fun handleCommands(packet: String): List<Any> =
        handlePacket(packet) { command ->
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
    fun getOne(): Remote<Resource> = sendCommand(Command(this::class, ResourceApi::getOne, emptyList()))
    fun printSmth(s: String): Remote<Unit> = sendCommand(Command(this::class, ResourceApi::printSmth, listOf(s)))
}

// class RemoteMonadResourceApiClient(private val api: ResourceApiClient) {
//     suspend fun MonadContinuation<Kind<Kind<ForKleisli, ForId>, Device>, *>.getOne(): Resource? =
//         bind { api.getOne() }
//
//     suspend fun MonadContinuation<Kind<Kind<ForKleisli, ForId>, Device>, *>.printSmth(s: String) {
//         bind { api.printSmth(s) }
//     }
// }

suspend fun main() {
    val device = MockDevice()

    val api = ResourceApiClient()

    send(device, api.printSmth("test"))
    println(send(device, api.getOne()))

    println(
        send(device) {
            bind { api.printSmth("1") }
            bind { api.printSmth("2") }
            bind { api.printSmth("3") }
        }
    )
}

