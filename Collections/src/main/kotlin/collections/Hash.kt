package collections

import kotlin.math.sqrt

internal sealed class Probe<out TElement> {
    data object Empty : Probe<Nothing>()
    data object Deleted : Probe<Nothing>()
    class Valid<TKey>(val item: TKey) : Probe<TKey>()
}

@Suppress("RemoveRedundantQualifierName")
internal sealed class Chain<TElement> {
    class Head<TKey>(override var next: Chain.Valid<TKey>? = null) : Chain<TKey>()
    class Valid<TKey>(val item: TKey, override var next: Chain.Valid<TKey>? = null) : Chain<TKey>()

    abstract var next: Chain.Valid<TElement>?
}

interface Hasher {
    object Linear : Hasher {
        override val initialCapacity: Int = 16

        override val loadFactor: Float = 0.75f

        override fun hashCode2(index: Int, count: Int): Int =
            index + count

        override fun findNextCapacity(currentCapacity: Int): Int =
            currentCapacity * 2
    }

    object Quadratic : Hasher {
        override val initialCapacity: Int = 17

        override val loadFactor: Float = 0.5f

        override fun hashCode2(index: Int, count: Int): Int =
            index + count * count

        override fun findNextCapacity(currentCapacity: Int): Int {
            var newCapacity = currentCapacity + 2

            while (!isPrime(newCapacity)) {
                newCapacity += 2
            }

            return newCapacity
        }

        private fun isPrime(capacity: Int): Boolean {
            val end = sqrt(capacity.toFloat()).toInt()

            for (divisor in 2 .. end) {
                if (0 == capacity % divisor) {
                    return true
                }
            }

            return false
        }
    }

    val initialCapacity: Int

    val loadFactor: Float

    fun hashCode2(index: Int, count: Int): Int

    fun findNextCapacity(currentCapacity: Int): Int
}

interface EqualityComparator<in TElement> {
    object Default : EqualityComparator<Any?> {
        override fun equals(left: Any?, right: Any?): Boolean =
            left == right

        override fun hashCode(item: Any?): Int =
            item.hashCode()
    }

    fun equals(left: TElement, right: TElement): Boolean

    fun hashCode(item: TElement): Int
}

internal typealias MutableEntry<TKey, TValue> = MutableMap.MutableEntry<TKey, TValue>

internal class CustomEntry<TKey, TValue>(
    newKey: TKey,
    newValue: TValue
) : MutableEntry<TKey, TValue> {
    override val key: TKey = newKey

    override var value: TValue = newValue
        private set

    override fun setValue(newValue: TValue): TValue {
        val old = this.value

        this.value = newValue

        return old
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other is CustomEntry<*, *>) {
            return this.key == other.key && this.value == other.value
        }

        return false
    }

    override fun hashCode(): Int {
        var hashValue = 31

        hashValue = 31 * hashValue + this.key.hashCode()
        hashValue = 31 * hashValue + this.value.hashCode()

        return hashValue
    }

    override fun toString(): String =
        "Entry[${this.key}] = ${this.value}"
}