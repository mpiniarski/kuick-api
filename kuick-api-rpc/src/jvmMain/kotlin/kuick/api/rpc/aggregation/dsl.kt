package kuick.api.rpc.aggregation

import com.google.inject.Injector
import io.ktor.routing.Route
import kuick.api.rpc.RpcRouting

inline fun Route.aggregationRoute(injector: Injector): AggregationRouting {
    return AggregationRouting(this, injector, RpcRouting.handleMap)
        // .apply { config(this.config) }
        .also { it.register() }
}