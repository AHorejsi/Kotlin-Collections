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
    }

    private val data: MutableList<TElement> = VectorList(initialCapacity)

    override val size: Int
        get() = this.data.size

    override fun push(element: TElement) {
        this.data.add(element)

        this.heapifyInsertion()
    }

    private fun heapifyInsertion() {
        var currentIndex = this.data.lastIndex
        var parentIndex = this.parent(currentIndex)

        while (parentIndex >= 0) {
            if (this.comparator(this.data[currentIndex], this.data[parentIndex]) <= 0) {
                break
            }

            this.data.swap(currentIndex, parentIndex)

            currentIndex = parentIndex
            parentIndex = this.parent(currentIndex)
        }
    }

    private fun parent(currentIndex: Int): Int =
        (currentIndex - 1) / 2

    override fun pop(): TElement {
        if (this.isEmpty()) {
            empty(BinaryHeap::class)
        }

        val item = this.data.removeLast()

        this.heapifyRemoval()

        return item
    }

    private fun heapifyRemoval() {
        var currentIndex = this.data.lastIndex

        while (true) {
            var indexOfLargest = currentIndex
            val leftIndex = 2 * currentIndex + 1
            val rightIndex = 2 * currentIndex + 2

            if (leftIndex < this.size && this.comparator(this.data[leftIndex], this.data[indexOfLargest]) > 0) {
                indexOfLargest = leftIndex
            }

            if (rightIndex < this.size && this.comparator(this.data[rightIndex], this.data[indexOfLargest]) > 0) {
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

    override fun peek(): TElement =
        if (this.isEmpty())
            empty(BinaryHeap::class)
        else
            this.data.first()

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
