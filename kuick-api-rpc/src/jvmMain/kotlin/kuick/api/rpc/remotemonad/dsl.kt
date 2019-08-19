package kuick.api.rpc.remotemonad

import com.google.inject.Injector
import io.ktor.routing.Route
import kuick.api.rpc.RpcRouting

inline fun Route.remoteMonadRoute(injector: Injector): RemoteMonadRouting {
    return RemoteMonadRouting(this, injector, RpcRouting.handleMap)
        // .apply { config(this.config) }
        .also { it.register() }
}