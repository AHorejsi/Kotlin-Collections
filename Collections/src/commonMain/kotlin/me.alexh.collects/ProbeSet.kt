package me.alexh.collects

import kotlin.jvm.Transient
import kotlin.math.sqrt

private sealed interface ProbeItem<out TElement> {
    class Valid<TElem>(val element: TElem) : ProbeItem<TElem>
    object Empty : ProbeItem<Nothing>
    object Delete : ProbeItem<Nothing>
}

class ProbeSet<TElement>(
    val hasher: DoubleHasher<TElement> = QuadraticHasher()
) : SealedMutableCollection<TElement>(), MutableSet<TElement> {
    private companion object {
        const val DEFAULT_CAPACITY: Int = 17
        const val MAX_LOAD_FACTOR: Float = 0.5F
    }

    @Transient
    private var modCount: Int = 0
    private var elementData: Array<ProbeItem<TElement>> = Array(ProbeSet.DEFAULT_CAPACITY) { ProbeItem.Empty }
    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.elementData.size

    val loadFactor: Float
        get() = this.size.toFloat() / this.capacity.toFloat()

    override fun add(element: TElement): Boolean {
        this.resizeIfNeeded()

        val index = this.findIndexForInsertion(element, this.elementData)

        if (null !== index) {
            this.elementData[index] = ProbeItem.Valid(element)

            ++(this.size)
            ++(this.modCount)

            return true
        }

        return false
    }

    private fun resizeIfNeeded() {
        val nextSize = (this.size + 1).toFloat()
        val cap = this.capacity

        if (nextSize / cap > ProbeSet.MAX_LOAD_FACTOR) {
            val newCap = this.findNextPrime(cap * 2 + 1)
            val newData = Array<ProbeItem<TElement>>(newCap) { ProbeItem.Empty }

            for (elem in this) {
                val index = this.findIndexForInsertion(elem, newData)!!

                newData[index] = ProbeItem.Valid(elem)
            }

            this.elementData = newData
        }
    }

    private fun findNextPrime(minCapacity: Int): Int {
        var newCap = minCapacity

        while (!this.isPrime(minCapacity)) {
            ++newCap
        }

        return newCap
    }

    private fun isPrime(value: Int): Boolean {
        val end = sqrt(value.toFloat()).toInt()

        for (div in 2 .. end) {
            if (0 == value % div) {
                return false
            }
        }

        return true
    }

    private fun findIndexForInsertion(element: TElement, elementData: Array<ProbeItem<TElement>>): Int? {
        val cap = this.capacity
        val hash1 = this.hasher.hashCode1(element)
        var jump = 1
        var index = hash1 % cap

        while (true) {
            val item = elementData[index]

            when (item) {
                is ProbeItem.Empty, is ProbeItem.Delete -> {
                    return index
                }
                is ProbeItem.Valid<TElement> -> {
                    if (this.hasher.equals(item.element, element)) {
                        return null
                    }
                }
            }

            index = hash1 + jump * this.hasher.hashCode2(element, jump)
            ++jump
        }
    }

    override operator fun contains(element: TElement): Boolean = null !== this.findIndex(element)

    override fun remove(element: TElement): Boolean {
        val index = this.findIndex(element)

        if (null !== index) {
            this.elementData[index] = ProbeItem.Delete

            --(this.size)
            ++(this.modCount)

            return true
        }

        return false
    }

    private fun findIndex(element: TElement): Int? {
        val cap = this.capacity
        val hash1 = this.hasher.hashCode1(element)
        var jump = 1
        var index = hash1 % cap

        while (true) {
            val item = this.elementData[index]

            if (item is ProbeItem.Empty) {
                return null
            }
            if (item is ProbeItem.Valid<TElement> && this.hasher.equals(item.element, element)) {
                return index
            }

            index = hash1 + jump * this.hasher.hashCode2(element, jump)
            ++jump
        }
    }

    override fun clear() {
        this.elementData = Array(ProbeSet.DEFAULT_CAPACITY) { ProbeItem.Empty }
        this.size = 0
        ++(this.modCount)
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val modCount: Int = this@ProbeSet.modCount
        private var lastIndex: Int? = null
        private var currentIndex: Int = this.findNext(0)

        private fun findNext(index: Int): Int {
            var newIndex = index

            while (this@ProbeSet.elementData[newIndex] !is ProbeItem.Valid<TElement>) {
                ++newIndex
            }

            return newIndex
        }

        override fun hasNext(): Boolean {
            this.checkForConcurrentModification()

            return this@ProbeSet.capacity != this.currentIndex
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val elem = (this@ProbeSet.elementData[this.currentIndex] as ProbeItem.Valid<TElement>).element

            this.lastIndex = this.currentIndex
            this.currentIndex = this.findNext(this.currentIndex + 1)

            return elem
        }

        override fun remove() {
            this.checkForConcurrentModification()

            this.lastIndex?.let {
                this@ProbeSet.elementData[it] = ProbeItem.Delete
                this.lastIndex = null

                --(this@ProbeSet.size)
            } ?: throw IllegalStateException()
        }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@ProbeSet.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}
