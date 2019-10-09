package kuick.api.rpc.remotemonad

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
import com.google.common.reflect.TypeToken
import kuick.api.json.Json

typealias Remote<T> = StateT<ReaderPartialOf<Device>, List<RemoteMonadCommand>, T?>

inline fun <reified T : Any> sendCommand(command: RemoteMonadCommand): Remote<T> =
    StateT<ReaderPartialOf<Device>, List<RemoteMonadCommand>, T?>(Reader.applicative(Id.monad())) { commands ->
        Reader { device ->
            if (T::class.isInstance(Unit)) {
                // device.async(command.serialize())
                Id(Tuple2(commands + command, null))
            } else {
                val result = device.sync((commands + command))
                val parsed = Json.fromJson<T>(result)
                Id(Tuple2(emptyList(), parsed as T?))
            }
        }
    }

inline fun <reified T : Any> sendCommand2(command: RemoteMonadCommand): Remote<List<T>> =
    StateT<ReaderPartialOf<Device>, List<RemoteMonadCommand>, List<T>?>(Reader.applicative(Id.monad())) { commands ->
        Reader { device ->
            if (T::class.isInstance(Unit)) {
                Id(Tuple2(commands + command, null))
            } else {
                val result = device.sync((commands + command))
                val parsed = Json.fromJson<List<T>>(result, object : TypeToken<List<T>>() { }.type)
                Id(Tuple2(emptyList(), parsed))
            }
        }
    }

data class RemoteMonadRequest(
    val type: Type,
    val commands: List<RemoteMonadCommand>,
    val returnBindingName: String?
) {
    enum class Type { SYNC, ASYNC }
}

data class RemoteMonadCommand(
    val method: String,
    val path: String,
    val queryParameters: Map<String, List<String>>,
    val body: String,
    val resultBindingName: String
)

interface Device {
    fun async(packet: List<RemoteMonadCommand>): Unit
    fun sync(packet: List<RemoteMonadCommand>): String
}

fun <T> send(device: Device, remote: Remote<T>): T? {
    val (commands, result) = remote.run(Reader.monad(Id.monad()), emptyList())
        .run(device)
        .fix().extract()
    return if (commands.isNotEmpty()) {
        device.async(commands)
        null
    } else {
        result
    }
}

fun <T> send(
    device: Device,
    block: suspend MonadContinuation<Kind<Kind<ForStateT, ReaderPartialOf<Device>>, List<RemoteMonadCommand>>, *>.() -> T
): T? =
    send(
        device,
        binding<ReaderPartialOf<Device>, List<RemoteMonadCommand>, T?>(
            Reader.monad(Id.monad()),
            block
        ).fix()
    )
