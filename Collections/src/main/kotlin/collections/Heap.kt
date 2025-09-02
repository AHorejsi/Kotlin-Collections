package collections

import java.io.Serializable

interface Heap<TElement> {
    val size: Int

    val comparator: (TElement, TElement) -> Int

    fun push(element: TElement)

    fun pop(): TElement

    fun peek(): TElement

    fun clear()
}

@Suppress("RemoveRedundantQualifierName")
class BinaryHeap<TElement>(
    initialCapacity: Int = BinaryHeap.DEFAULT_CAPACITY,
    override val comparator: (TElement, TElement) -> Int
) : Heap<TElement>, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16

        fun parent(index: Int): Int =
            (index - 1) / 2

        fun leftChild(index: Int): Int =
            2 * index + 1

        fun rightChild(index: Int): Int =
            2 * index + 2
    }

    private val data: MutableList<TElement> = VectorList(initialCapacity)

    override val size: Int
        get() = this.data.size

    @Suppress("UNCHECKED_CAST")
    constructor(initialCapacity: Int = BinaryHeap.DEFAULT_CAPACITY)
            : this(initialCapacity, { left, right -> (left as Comparable<TElement>).compareTo(right) })

    override fun push(element: TElement) {
        this.data.add(element)

        this.heapifyForInsertion()
    }

    private fun heapifyForInsertion() {
        var currentIndex = this.data.lastIndex
        var parentIndex = BinaryHeap.parent(currentIndex)

        while (currentIndex > 0 && this.comparator(this.data[currentIndex], this.data[parentIndex]) < 0) {
            this.data.swap(currentIndex, parentIndex)

            currentIndex = parentIndex
            parentIndex = BinaryHeap.parent(currentIndex)
        }
    }

    override fun pop(): TElement {
        val item = this.peek()

        this.data[0] = this.data[this.data.lastIndex]

        this.heapifyForRemoval()
        this.data.removeLast()

        return item
    }

    private fun heapifyForRemoval() {
        var currentIndex = 0

        while (true) {
            var indexOfLargest = currentIndex
            val leftIndex = BinaryHeap.leftChild(indexOfLargest)
            val rightIndex = BinaryHeap.rightChild(indexOfLargest)

            if (this.inBounds(leftIndex) && this.comparator(this.data[leftIndex], this.data[indexOfLargest]) < 0) {
                indexOfLargest = leftIndex
            }

            if (this.inBounds(rightIndex) && this.comparator(this.data[rightIndex], this.data[indexOfLargest]) < 0) {
                indexOfLargest = rightIndex
            }

            if (currentIndex == indexOfLargest) {
                break
            }
            else {
                this.data.swap(currentIndex, indexOfLargest)

                currentIndex = indexOfLargest
            }
        }
    }

    private fun inBounds(index: Int): Boolean =
        index < this.size

    override fun peek(): TElement =
        if (this.isEmpty())
            empty(BinaryHeap::class)
        else
            this.data[0]

    override fun clear() =
        this.data.clear()
}

fun Heap<*>.isEmpty(): Boolean =
    0 == this.size

fun <TElement> Heap<TElement>.popOrNull(): TElement? =
    this.tryPop().getOrNull()

fun <TElement> Heap<TElement>.tryPop(): Result<TElement> =
    runCatching{ this.pop() }

fun <TElement> Heap<TElement>.peekOrNull(): TElement? =
    this.tryPeek().getOrNull()

fun <TElement> Heap<TElement>.tryPeek(): Result<TElement> =
    runCatching{ this.peek() }
