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

    constructor(
        initialCapacity: Int = BinaryHeap.DEFAULT_CAPACITY,
        compObj: Comparator<TElement>? = null
    ) : this(initialCapacity, compObj.function)

    init {
        require(initialCapacity >= 0)
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

            this.swap(currentIndex, parentIndex)

            currentIndex = parentIndex
            parentIndex = this.parent(currentIndex)
        }
    }

    private fun parent(currentIndex: Int): Int = (currentIndex - 1) / 2

    override fun pop(): TElement {
        if (this.isEmpty()) {
            throw NoSuchElementException()
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
                this.swap(currentIndex, indexOfLargest)

                currentIndex = indexOfLargest
            }
        }
    }

    private fun swap(index1: Int, index2: Int) {
        val temp = this.data[index1]
        this.data[index1] = this.data[index2]
        this.data[index2] = temp
    }

    override fun peek(): TElement =
        if (this.isEmpty())
            throw NoSuchElementException()
        else
            this.data.first()

    override fun clear() = this.data.clear()
}

fun Heap<*>.isEmpty(): Boolean = 0 == this.size

fun <TElement> Heap<TElement>.tryPop(): Result<TElement> = runCatching { this.pop() }

fun <TElement> Heap<TElement>.tryPeek(): Result<TElement> = runCatching { this.peek() }
