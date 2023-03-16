package me.alexh.collects

fun <TElement> emptyMutableList(): MutableList<TElement> = mutableListOf()

fun <TElement> List<TElement>.wrapGet(index: Int): TElement = this[actualIndex(index, this.size)]

fun <TElement> MutableList<TElement>.wrapSet(index: Int, element: TElement): TElement = this.set(actualIndex(index, this.size), element)

private fun actualIndex(index: Int, size: Int): Int = (index + size) % size

fun <TElement> List<TElement>.getOrNull(index: Int): TElement? = this.tryGet(index).getOrNull()

fun <TElement> MutableList<TElement>.setOrNull(index: Int, element: TElement): TElement? = this.trySet(index, element).getOrNull()

fun <TElement> List<TElement>.tryGet(index: Int): Result<TElement> =
    if (index < 0 || index >= this.size)
        Result.failure(IndexOutOfBoundsException())
    else
        Result.success(this[index])

fun <TElement> MutableList<TElement>.trySet(index: Int, element: TElement): Result<TElement> =
    if (index < 0 || index >= this.size)
        Result.failure(IndexOutOfBoundsException())
    else
        Result.success(this.set(index, element))

fun <TElement> List<TElement>.indexOf(element: @UnsafeVariance TElement, fromIndex: Int): Int = indexSearch(fromIndex until this.size, element, this)

fun <TElement> List<TElement>.lastIndexOf(element: @UnsafeVariance TElement, fromIndex: Int): Int = indexSearch(fromIndex downTo 0, element, this)

private fun <TElement> indexSearch(indices: IntProgression, element: @UnsafeVariance TElement, list: List<@UnsafeVariance TElement>): Int {
    for (index in indices) {
        if (list[index] == element) {
            return index
        }
    }

    return -1
}

fun <TElement> MutableList<TElement>.removeRange(fromIndex: Int, toIndex: Int) {
    if (fromIndex < 0 || fromIndex >= this.size || toIndex < 0 || toIndex > this.size) {
        throw IndexOutOfBoundsException()
    }
    if (toIndex < fromIndex) {
        throw IllegalArgumentException()
    }

    val distance = toIndex - fromIndex

    shiftForRemoval(this, toIndex, distance)
    removeTrail(this, distance)
}

private fun <TElement> shiftForRemoval(list: MutableList<TElement>, toIndex: Int, distance: Int) {
    for (removeIndex in toIndex until list.size) {
        list[removeIndex] = list[removeIndex - distance]
    }
}

private fun <TElement> removeTrail(list: MutableList<TElement>, distance: Int) {
    val rightIndex = list.lastIndex
    val leftIndex = rightIndex - distance

    for (index in rightIndex downTo leftIndex) {
        list.removeAt(index)
    }
}

fun <TElement> MutableList<TElement>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}

inline fun <TElement> MutableList<TElement>.resize(newSize: Int, factory: Factory<out TElement> = { throw NotImplementedError() }) {
    if (newSize < this.size) {
        this.removeRange(newSize + 1, this.size)
    }
    else {
        while (this.size < newSize) {
            val newElem = factory()

            this.add(newElem)
        }
    }
}

fun <TElement> List<TElement>.subList(fromIndex: Int): List<TElement> = this.subList(fromIndex, this.size)

fun <TElement> MutableList<TElement>.subList(fromIndex: Int): MutableList<TElement> = this.subList(fromIndex, this.size)
