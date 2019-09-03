package kuick.api.rpc.aggregation

import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kuick.json.Json

inline fun <reified A : Any> JsonElement.toResponse(): Response<A> {
    val clazz = TypeToken.getParameterized(Response.Success::class.java, A::class.java).type
    val asString = this.toString()
    return with(this.asJsonObject["status"].asString) {
        if (startsWith("2")) {
            Json.fromJson<Response.Success<A>>(asString, clazz)
        } else Json.fromJson<Response.Failure<A>>(asString)
    }
}

inline fun <reified A : Any> send(
    device: Device,
    vararg requests: Request<A>
): List<Response<A>> {
    val result = device.send(requests.toList())
    return Json.jsonParser.parse(result).asJsonArray
        .map { it.toResponse<A>() }
}

inline fun <reified A : Any> send(
    device: Device,
    requests: List<Request<A>>
): List<Response<A>> {
    val result = device.send(requests)
    return Json.jsonParser.parse(result).asJsonArray
        .map { it.toResponse<A>() }
}

inline fun <reified A : Any> send(
    device: Device,
    _1: Request<A>
): Tuple1<Response<A>> {
    val result = device.send(listOf(_1))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple1(
        responses[0].toResponse<A>()
    )
}

inline fun <reified A : Any, reified B : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>
): Tuple2<Response<A>, Response<B>> {
    val result = device.send(listOf(_1, _2))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple2(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>
): Tuple3<Response<A>, Response<B>, Response<C>> {
    val result = device.send(listOf(_1, _2, _3))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple3(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>
): Tuple4<Response<A>, Response<B>, Response<C>, Response<D>> {
    println(A::class)
    println(B::class)
    println(C::class)
    println(D::class)
    val result = device.send(listOf(_1, _2, _3, _4))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple4(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>
): Tuple5<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple5(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>
): Tuple6<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5, _6))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple6(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>
): Tuple7<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5, _6, _7))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple7(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>,
    _8: Request<H>
): Tuple8<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5, _6, _7, _8))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple8(
        responses[0].toResponse<A>(),
        responses[1].toResponse<B>(),
        responses[2].toResponse<C>(),
        responses[3].toResponse<D>(),
        responses[4].toResponse<E>(),
        responses[5].toResponse<F>(),
        responses[6].toResponse<G>(),
        responses[7].toResponse<H>()
    )
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any> send(
    device: Device,
    _1: Request<A>,
    _2: Request<B>,
    _3: Request<C>,
    _4: Request<D>,
    _5: Request<E>,
    _6: Request<F>,
    _7: Request<G>,
    _8: Request<H>,
    _9: Request<I>
): Tuple9<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>, Response<I>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple9(
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
}

inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any, reified G : Any, reified H : Any, reified I : Any, reified J : Any> send(
    device: Device,
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
): Tuple10<Response<A>, Response<B>, Response<C>, Response<D>, Response<E>, Response<F>, Response<G>, Response<H>, Response<I>, Response<J>> {
    val result = device.send(listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10))
    val responses = Json.jsonParser.parse(result).asJsonArray
    return Tuple10(
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
}