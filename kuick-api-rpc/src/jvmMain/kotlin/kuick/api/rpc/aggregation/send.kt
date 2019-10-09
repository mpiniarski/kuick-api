package kuick.api.rpc.aggregation

import kuick.api.json.Json
import kuick.api.rpc.Device
import kuick.api.rpc.Request
import kuick.api.rpc.Response
import kuick.api.rpc.toResponse

// TODO - conflicts with single request send from Device when AggregatingDevice is used
// inline fun <reified A : Any, R> AggregatingDevice<R>.send(
//     vararg requests: Request<A>
// ): Device.Result<List<Response<A>>, R> {
//     val result = aggregatedCall(requests.toList())
//     val parsedResult = Json.jsonParser.parse(result.getBodyAsString())
//         .asJsonArray
//         .map { it.toResponse<A>() }
//     return Device.Result(parsedResult, result)
// }

inline fun <reified A : Any, R> AggregatingDevice<R>.send(
    requests: List<Request<A>>
): Device.Result<List<Response<A>>, R> {
    val result = aggregatedCall(requests)
    val parsedResult = Json.jsonParser.parse(result.getBody())
        .asJsonArray
        .map { it.toResponse<A>() }
    return Device.Result(parsedResult, result)
}
                
inline fun <reified A : Any, reified B : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>
): Device.Result<Tuple2<Response<A>, Response<B>>, R> { 
    val result = aggregatedCall(listOf(_1, _2))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple2(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>
): Device.Result<Tuple3<Response<A>, Response<B>, Response<C>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple3(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>
): Device.Result<Tuple4<Response<A>, Response<B>, Response<C>, Response<D>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple4(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>
): Device.Result<Tuple5<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple5(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>
): Device.Result<Tuple6<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5, _6))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple6(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>
): Device.Result<Tuple7<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5, _6, _7))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple7(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>,
    _8: Request<H>
): Device.Result<Tuple8<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5, _6, _7, _8))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple8(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>(),
        responses[7].toResponse<H>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>,
    _8: Request<H>,
    _9: Request<I>
): Device.Result<Tuple9<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>, Response<I>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple9(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>(),
        responses[7].toResponse<H>(),
        responses[8].toResponse<I>()
    )
     return Device.Result(parsedResult, result)
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any, reified J : Any, R> AggregatingDevice<R>.send(
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>,
    _8: Request<H>,
    _9: Request<I>,
    _10: Request<J>
): Device.Result<Tuple10<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>, Response<I>, Response<J>>, R> { 
    val result = aggregatedCall(listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10))
    val responses = Json.jsonParser.parse(result.getBody())
        .asJsonArray
    val parsedResult = Tuple10(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>(),
        responses[7].toResponse<H>(),
        responses[8].toResponse<I>(),
        responses[9].toResponse<J>()
    )
     return Device.Result(parsedResult, result)
}