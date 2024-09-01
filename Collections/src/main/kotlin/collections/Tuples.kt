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


