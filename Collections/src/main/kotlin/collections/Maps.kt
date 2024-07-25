package collections

import java.io.Serializable

internal class KeySet<TElement>(
    private val base: MutableMap<TElement, *>
) : AbstractCollection<TElement>(), MutableSet<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    override val size: Int
        get() = this.base.size

    override fun add(element: TElement): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: TElement): Boolean = null !== this.base.remove(element)

    override fun clear() = this.base.clear()

    override operator fun contains(element: TElement): Boolean = this.base.containsKey(element)

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val iter = this@KeySet.base.entries.iterator()

        override fun hasNext(): Boolean = this.iter.hasNext()

        override fun next(): TElement = this.iter.next().key

        override fun remove() = this.iter.remove()
    }
}

internal class ValueCollection<TElement>(
    private val base: MutableMap<*, TElement>
) : AbstractCollection<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    override val size: Int
        get() = this.base.size

    override fun add(element: TElement): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: TElement): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() = this.base.clear()

    override operator fun contains(element: TElement): Boolean {
        for (value in this) {
            if (value == element) {
                return true
            }
        }

        return false
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val iter = this@ValueCollection.base.entries.iterator()

        override fun hasNext(): Boolean = this.iter.hasNext()

        override fun next(): TElement = this.iter.next().value

        override fun remove() = this.iter.remove()
    }
}

internal abstract class AbstractEntrySet<TKey, TValue>(
    private val base: MutableMap<TKey, TValue>
) : AbstractCollection<MutableEntry<TKey, TValue>>(), MutableSet<MutableEntry<TKey, TValue>> {
    override val size: Int
        get() = this.base.size

    override fun add(element: MutableEntry<TKey, TValue>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: MutableEntry<TKey, TValue>): Boolean {
        if (element in this) {
            this.base.remove(element.key)

            return true
        }

        return false
    }

    override fun clear() = this.base.clear()

    override operator fun contains(element: MutableEntry<TKey, TValue>): Boolean {
        val value = this.base[element.key]

        if (null === value) {
            return false
        }

        return value == element.value
    }
}
