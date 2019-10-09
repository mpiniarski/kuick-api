package kuick.api.rpc.aggregation

import kuick.api.rpc.Device
import kuick.api.rpc.Request

abstract class AggregatingDevice<R>(device: Device<R>) : Device<R> by device {
    abstract fun aggregatedCall(packet: List<Request<*>>): R
}

// TODO normal Device without <R>
// abstract class Device {
//     abstract fun send(request: Request<*>): String
//     abstract fun aggregatedSend(packet: List<Request<*>>): String
// }
