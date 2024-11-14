package collections

import java.io.Serializable

data class Quad<out T1, out T2, out T3, out T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }
}

data class Quint<out T1, out T2, out T3, out T4, out T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }
}

fun <T1, T2, T3, T4> quad(first: T1, second: T2, third: T3, fourth: T4): Quad<T1, T2, T3, T4> =
    Quad(first, second, third, fourth)

fun <T1, T2, T3, T4, T5> quint(first: T1, second: T2, third: T3, fourth: T4, fifth: T5): Quint<T1, T2, T3, T4, T5> =
    Quint(first, second, third, fourth, fifth)

fun <TInput1, TInput2, TOutput1, TOutput2> Pair<TInput1, TInput2>.map(
    transform1: (TInput1) -> TOutput1,
    transform2: (TInput2) -> TOutput2
): Pair<TOutput1, TOutput2> {
    val newFirst = transform1(this.first)
    val newSecond = transform2(this.second)

    return Pair(newFirst, newSecond)
}

fun <TInput1, TInput2, TInput3, TOutput1, TOutput2, TOutput3> Triple<TInput1, TInput2, TInput3>.map(
    transform1: (TInput1) -> TOutput1,
    transform2: (TInput2) -> TOutput2,
    transform3: (TInput3) -> TOutput3
): Triple<TOutput1, TOutput2, TOutput3> {
    val newFirst = transform1(this.first)
    val newSecond = transform2(this.second)
    val newThird = transform3(this.third)

    return Triple(newFirst, newSecond, newThird)
}

fun <TInput1, TInput2, TInput3, TInput4, TOutput1, TOutput2, TOutput3, TOutput4>
Quad<TInput1, TInput2, TInput3, TInput4>.map(
    transform1: (TInput1) -> TOutput1,
    transform2: (TInput2) -> TOutput2,
    transform3: (TInput3) -> TOutput3,
    transform4: (TInput4) -> TOutput4
): Quad<TOutput1, TOutput2, TOutput3, TOutput4> {
    val newFirst = transform1(this.first)
    val newSecond = transform2(this.second)
    val newThird = transform3(this.third)
    val newFourth = transform4(this.fourth)

    return Quad(newFirst, newSecond, newThird, newFourth)
}

fun <TInput1, TInput2, TInput3, TInput4, TInput5, TOutput1, TOutput2, TOutput3, TOutput4, TOutput5>
Quint<TInput1, TInput2, TInput3, TInput4, TInput5>.map(
    transform1: (TInput1) -> TOutput1,
    transform2: (TInput2) -> TOutput2,
    transform3: (TInput3) -> TOutput3,
    transform4: (TInput4) -> TOutput4,
    transform5: (TInput5) -> TOutput5
): Quint<TOutput1, TOutput2, TOutput3, TOutput4, TOutput5> {
    val newFirst = transform1(this.first)
    val newSecond = transform2(this.second)
    val newThird = transform3(this.third)
    val newFourth = transform4(this.fourth)
    val newFifth = transform5(this.fifth)

    return Quint(newFirst, newSecond, newThird, newFourth, newFifth)
}
