package kuick.api.rpc.aggregation
                
object Tuple {
    operator fun <A> invoke(_1: A): Tuple1<A> = Tuple1(_1)
    operator fun <A, B> invoke(_1: A, _2: B): Tuple2<A, B> = Tuple2(_1, _2)
    operator fun <A, B, C> invoke(_1: A, _2: B, _3: C): Tuple3<A, B, C> = Tuple3(_1, _2, _3)
    operator fun <A, B, C, D> invoke(_1: A, _2: B, _3: C, _4: D): Tuple4<A, B, C, D> = Tuple4(_1, _2, _3, _4)
    operator fun <A, B, C, D, E> invoke(_1: A, _2: B, _3: C, _4: D, _5: E): Tuple5<A, B, C, D, E> = Tuple5(_1, _2, _3, _4, _5)
    operator fun <A, B, C, D, E, F> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F): Tuple6<A, B, C, D, E, F> = Tuple6(_1, _2, _3, _4, _5, _6)
    operator fun <A, B, C, D, E, F, G> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G): Tuple7<A, B, C, D, E, F, G> = Tuple7(_1, _2, _3, _4, _5, _6, _7)
    operator fun <A, B, C, D, E, F, G, H> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H): Tuple8<A, B, C, D, E, F, G, H> = Tuple8(_1, _2, _3, _4, _5, _6, _7, _8)
    operator fun <A, B, C, D, E, F, G, H, I> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I): Tuple9<A, B, C, D, E, F, G, H, I> = Tuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9)
    operator fun <A, B, C, D, E, F, G, H, I, J> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J): Tuple10<A, B, C, D, E, F, G, H, I, J> = Tuple10(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10)
    operator fun <A, B, C, D, E, F, G, H, I, J, K> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K): Tuple11<A, B, C, D, E, F, G, H, I, J, K> = Tuple11(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> = Tuple12(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> = Tuple13(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = Tuple14(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = Tuple15(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = Tuple16(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = Tuple17(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = Tuple18(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Tuple19(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Tuple20(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Tuple21(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U, _22: V): Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = Tuple22(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U, _22: V, _23: W): Tuple23<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> = Tuple23(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U, _22: V, _23: W, _24: X): Tuple24<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X> = Tuple24(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U, _22: V, _23: W, _24: X, _25: Y): Tuple25<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y> = Tuple25(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24, _25)
    operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z> invoke(_1: A, _2: B, _3: C, _4: D, _5: E, _6: F, _7: G, _8: H, _9: I, _10: J, _11: K, _12: L, _13: M, _14: N, _15: O, _16: P, _17: Q, _18: R, _19: S, _20: T, _21: U, _22: V, _23: W, _24: X, _25: Y, _26: Z): Tuple26<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z> = Tuple26(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24, _25, _26)
}

data class Tuple1<out A>(val _1: A)
data class Tuple2<out A, out B>(val _1: A, val _2: B)
data class Tuple3<out A, out B, out C>(val _1: A, val _2: B, val _3: C)
data class Tuple4<out A, out B, out C, out D>(val _1: A, val _2: B, val _3: C, val _4: D)
data class Tuple5<out A, out B, out C, out D, out E>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E)
data class Tuple6<out A, out B, out C, out D, out E, out F>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F)
data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G)
data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H)
data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I)
data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J)
data class Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K)
data class Tuple12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L)
data class Tuple13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M)
data class Tuple14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N)
data class Tuple15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O)
data class Tuple16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P)
data class Tuple17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q)
data class Tuple18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R)
data class Tuple19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S)
data class Tuple20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T)
data class Tuple21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U)
data class Tuple22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U, val _22: V)
data class Tuple23<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U, val _22: V, val _23: W)
data class Tuple24<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W, out X>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U, val _22: V, val _23: W, val _24: X)
data class Tuple25<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W, out X, out Y>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U, val _22: V, val _23: W, val _24: X, val _25: Y)
data class Tuple26<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W, out X, out Y, out Z>(val _1: A, val _2: B, val _3: C, val _4: D, val _5: E, val _6: F, val _7: G, val _8: H, val _9: I, val _10: J, val _11: K, val _12: L, val _13: M, val _14: N, val _15: O, val _16: P, val _17: Q, val _18: R, val _19: S, val _20: T, val _21: U, val _22: V, val _23: W, val _24: X, val _25: Y, val _26: Z)
            
typealias Pair<A, B> = Tuple2<A, B>
typealias Triple<A, B, C> = Tuple3<A, B, C>