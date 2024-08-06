package collections

import java.io.Serializable

@Suppress("RemoveRedundantQualifierName")
class DequeList<TElement>(
    initialCapacity: Int = DequeList.DEFAULT_CAPACITY
) : AbstractList<TElement>(), RandomAccess, Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        const val DEFAULT_CAPACITY: Int = 16
    }

    init {
        require(initialCapacity >= 0)
    }

    private var data: Array<Any?> = arrayOfNulls(initialCapacity)
    private var startIndex: Int = 0

    override var size: Int = 0
        private set

    val capacity: Int
        get() = this.data.size

    override fun get(index: Int): TElement {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        @Suppress("UNCHECKED_CAST")
        return this.data[this.actualIndex(index)] as TElement
    }

    override fun set(index: Int, element: TElement): TElement {
        if (index < 0 || index >= this.size) {
            throw IndexOutOfBoundsException()
        }

        val actualIndex = this.actualIndex(index)

        val old = this.data[actualIndex]
        this.data[actualIndex] = element

        @Suppress("UNCHECKED_CAST")
        return old as TElement
    }

    fun addFirst(element: TElement) = super.add(0, element)

    fun addLast(element: TElement) = super.add(this.size, element)

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        if (index < 0 || index > this.size) {
            throw IndexOutOfBoundsException()
        }

        val amountToAdd = elements.size
        val newSize = this.size + amountToAdd

        this.resizeIfNeeded(newSize)
        this.shiftForInsertion(index, amountToAdd)
        this.insertElements(elements, index)

        this.size = newSize
        ++(super.modCount)

        return true
    }

    fun addToFront(elements: Collection<TElement>) {
        this.addAll(0, elements)
    }

    fun addToBack(elements: Collection<TElement>) {
        this.addAll(this.size, elements)
    }

    private fun resizeIfNeeded(newSize: Int) {
        if (newSize > this.capacity) {
            this.reallocate(newSize * 3 / 2)
        }
    }

    private fun shiftForInsertion(index: Int, amountToAdd: Int) {
        val distanceFromStart = index
        val distanceFromEnd = this.size - index

        if (distanceFromStart > distanceFromEnd) {
            this.shiftRightForInsertion(index, amountToAdd)
        }
        else {
            this.shiftLeftForInsertion(index, amountToAdd)

            this.startIndex = (this.startIndex - amountToAdd) % this.capacity
        }
    }

    private fun shiftLeftForInsertion(insertIndex: Int, amountToAdd: Int) {
        for (index in 0 until insertIndex) {
            val newIndex = this.actualIndex(index - amountToAdd)
            val oldIndex = this.actualIndex(index)

            this.data[newIndex] = this.data[oldIndex]
        }
    }

    private fun shiftRightForInsertion(insertIndex: Int, amountToAdd: Int) {
        for (index in this.lastIndex downTo insertIndex) {
            val newIndex = this.actualIndex(index + amountToAdd)
            val oldIndex = this.actualIndex(index)

            this.data[newIndex] = this.data[oldIndex]
        }
    }

    private fun insertElements(elements: Collection<TElement>, index: Int) {
        var insertIndex = index

        for (item in elements) {
            this.data[insertIndex] = item
            insertIndex = (insertIndex + 1) % this.capacity
        }
    }

    fun removeFirst(): TElement = this.removeAt(0)

    fun removeLast(): TElement = this.removeAt(this.lastIndex)

    fun removeFromFront(amount: Int): Int {
        if (amount >= this.size) {
            val oldSize = this.size

            this.clear()

            return oldSize
        }
        else {
            this.startIndex = (this.startIndex + amount) % this.capacity
            this.size -= amount
            ++(super.modCount)

            return amount
        }
    }

    fun removeFromBack(amount: Int): Int {
        if (amount >= this.size) {
            val oldSize = this.size
            this.clear()

            return oldSize
        }
        else {
            this.size -= amount
            ++(super.modCount)

            return amount
        }
    }

    override fun removeAt(index: Int): TElement {
        val item = this[index]

        this.shiftForRemoval(index)

        --(this.size)
        ++(super.modCount)

        return item
    }

    private fun shiftForRemoval(index: Int) {
        val distanceFromStart = index
        val distanceFromEnd = this.size - index

        if (distanceFromStart > distanceFromEnd) {
            this.shiftFromRightForRemoval(index)
        }
        else {
            this.shiftFromLeftForRemoval(index)

            this.startIndex = (this.startIndex + 1) % this.capacity
        }
    }

    private fun shiftFromLeftForRemoval(removalIndex: Int) {
        for (index in 1 .. removalIndex) {
            this[index] = this[index - 1]
        }
    }

    private fun shiftFromRightForRemoval(removalIndex: Int) {
        for (index in removalIndex until this.lastIndex) {
            this[index] = this[index + 1]
        }
    }

    override fun clear() {
        this.size = 0
        this.startIndex = 0
        ++(super.modCount)
    }

    private fun actualIndex(index: Int) = (this.startIndex + index).mod(this.capacity)

    fun ensureCapacity(newCapacity: Int) {
        if (newCapacity > this.capacity) {
            this.reallocate(newCapacity)
        }
    }

    fun trimToSize() {
        if (this.size < this.capacity) {
            this.reallocate(this.size)
        }
    }

    private fun reallocate(newCapacity: Int) {
        val newData = arrayOfNulls<Any>(newCapacity)

        for (index in this.indices) {
            newData[index] = this[index]
        }

        this.data = newData
        this.startIndex = 0
    }
}

fun <TElement> dequeListOf(): DequeList<TElement> = DequeList()

fun <TElement> dequeListOf(vararg elements: TElement): DequeList<TElement> = elements.toDequeList()

fun <TElement> Iterable<TElement>.toDequeList(): DequeList<TElement> {
    val size = this.count()
    val deque = DequeList<TElement>(size)

    for (item in this) {
        deque.addLast(item)
    }

    return deque
}

fun <TElement> Sequence<TElement>.toDequeList(): DequeList<TElement> {
    val deque = DequeList<TElement>()

    for (item in this) {
        deque.addLast(item)
    }

    return deque
}

fun <TElement> Collection<TElement>.toDequeList(): DequeList<TElement> {
    val deque = DequeList<TElement>(this.size)

    deque.addAll(this)

    return deque
}

fun <TElement> Array<out TElement>.toDequeList(): DequeList<TElement> {
    val deque = DequeList<TElement>(this.size)

    deque.addAll(this)

    return deque
}
