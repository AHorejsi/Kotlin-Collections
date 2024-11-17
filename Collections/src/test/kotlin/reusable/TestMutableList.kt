package reusable

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.math.max
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

fun testSizeAfterAdd(list: MutableList<Int>, newItem: Int) {
    val oldSize = assertDoesNotThrow{ list.size }

    list.add(newItem)

    val newSize = assertDoesNotThrow{ list.size }

    assertEquals(newSize, oldSize + 1)
}

fun testSizeAfterAddAll(list: MutableList<Int>, amountToAdd: Int) {
    val items = (1 .. amountToAdd).toList()
    val oldSize = assertDoesNotThrow{ list.size }

    list.addAll(items)

    val newSize = assertDoesNotThrow{ list.size }

    assertEquals(newSize, oldSize + amountToAdd)
}

fun testSizeAfterRemoveLast(list: MutableList<Int>) {
    val oldSize = assertDoesNotThrow{ list.size }

    list.removeLast()

    val newSize = assertDoesNotThrow{ list.size }

    assertEquals(newSize, oldSize - 1)
}

fun testSizeAfterRemoveFromBack(list: MutableList<Int>, amountToRemove: Int) {
    val oldSize = assertDoesNotThrow{ list.size }

    list.removeFromBack(amountToRemove)

    val newSize = assertDoesNotThrow{ list.size }

    assertEquals(newSize, oldSize - amountToRemove)
}

fun testIsEmpty(list: MutableList<Int>) {
    val onCreate = assertDoesNotThrow{ list.isEmpty() }
    assertTrue(onCreate)

    list.add(0)
    val onFirstAdd = assertDoesNotThrow{ list.isEmpty() }
    assertTrue(!onFirstAdd)

    list.add(1)
    val onSecondAdd = assertDoesNotThrow{ list.isEmpty() }
    assertTrue(!onSecondAdd)

    list.removeLast()
    val onFirstRemove = assertDoesNotThrow{ list.isEmpty() }
    assertTrue(!onFirstRemove)

    list.removeLast()
    val onSecondRemove = assertDoesNotThrow{ list.isEmpty() }
    assertTrue(onSecondRemove)
}

fun testGetAfterAdding(list: MutableList<Int>, newItem: Int) {
    list.add(newItem)

    val endItem = assertDoesNotThrow{ list[list.lastIndex] }

    assertEquals(endItem, list[list.lastIndex])
}

fun testGetAfterAddingAt(list: MutableList<Int>, index: Int, newItem: Int) {
    val endInserted = index == list.size

    val initial = list.tryGet(index)

    list.add(index, newItem)

    val currentItem = assertDoesNotThrow{ list[index] }
    val old = list.tryGet(index + 1)

    assertEquals(currentItem, newItem)

    if (endInserted) {
        assertTrue(initial.isFailure)
        assertTrue(old.isFailure)
    }
    else {
        val initialItem = initial.getOrThrow()
        val oldItem = old.getOrThrow()

        assertEquals(initialItem, oldItem)
    }
}

fun testGetAfterRemovingAt(list: MutableList<Int>, index: Int) {
    val endRemoved = list.lastIndex == index

    val initialItem = assertDoesNotThrow{ list[index] }
    val new = list.tryGet(index + 1)

    val removedItem = list.removeAt(index)
    val current = list.tryGet(index)

    assertEquals(initialItem, removedItem)

    if (endRemoved) {
        assertTrue(new.isFailure)
        assertTrue(current.isFailure)
    }
    else {
        val newItem = new.getOrThrow()
        val currentItem = current.getOrThrow()

        assertEquals(newItem, currentItem)
    }
}

fun testGetOutOfBounds(list: MutableList<Int>) {
    @Suppress("KotlinConstantConditions")
    run {
        assertFailsWith<IndexOutOfBoundsException>{ list[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ list[list.size] }
    }
}

fun testSetAt(list: MutableList<Int>, index: Int, newItem: Int) {
    val initialItem = list[index]

    val oldItem = assertDoesNotThrow{ list.set(index, newItem) }

    val currentItem = list[index]

    assertEquals(oldItem, initialItem)
    assertEquals(newItem, currentItem)
}

fun testSetOutOfBounds(list: MutableList<Int>, newItem: Int) {
    @Suppress("KotlinConstantConditions")
    run {
        assertFailsWith<IndexOutOfBoundsException>{ list[-1] = newItem }
        assertFailsWith<IndexOutOfBoundsException>{ list[list.size] = newItem }
    }
}

fun testWrapSetAt(list: MutableList<Int>, index: Int, newItem: Int) {
    val size = list.size

    val beforeItem1 = list.wrapGet(index - size)
    val insideItem1 = list.wrapGet(index)
    val afterItem1 = list.wrapGet(index + size)

    val oldItem = assertDoesNotThrow{ list.wrapSet(index, newItem) }

    val beforeItem2 = list.wrapGet(index - size)
    val insideItem2 = list.wrapGet(index)
    val afterItem2 = list.wrapGet(index + size)

    assertEquals(oldItem, beforeItem1)
    assertEquals(oldItem, insideItem1)
    assertEquals(oldItem, afterItem1)

    assertEquals(newItem, beforeItem2)
    assertEquals(newItem, insideItem2)
    assertEquals(newItem, afterItem2)
}

fun testWrapSetOnEmpty(list: MutableList<Int>, newItem: Int) {
    assertFailsWith<IllegalStateException>{ list.wrapSet(0, newItem) }
}

fun testTrySetAt(list: MutableList<Int>, index: Int, newItem: Int) {
    val initialItem = list[index]

    val old = assertDoesNotThrow{ list.trySet(index, newItem) }
    val oldItem = assertDoesNotThrow{ old.getOrThrow() }

    val currentItem = list[index]

    assertEquals(oldItem, initialItem)
    assertEquals(newItem, currentItem)
}

fun testTrySetOutOfBounds(list: MutableList<Int>, newItem: Int) {
    val early = assertDoesNotThrow{ list.trySet(-1, newItem) }
    val late = assertDoesNotThrow{ list.trySet(list.size, newItem) }

    assertFailsWith<IndexOutOfBoundsException>{ early.getOrThrow() }
    assertFailsWith<IndexOutOfBoundsException>{ late.getOrThrow() }
}

fun testAddConsecutively(list: MutableList<Int>, amount: Int, subAmount: Int) {
    repeat(amount) { adjuster ->
        val initialSize = list.size

        repeat(subAmount) { item ->
            val oldSize = list.size
            val element = item + adjuster

            val change = assertDoesNotThrow{ list.add(element) }

            assertTrue(change)
            assertEquals(oldSize + 1, list.size)
            assertEquals(element, list.last())
        }

        assertEquals(initialSize + subAmount, list.size)
        assertEquals(adjuster + subAmount - 1, list.last())
    }
}

fun testIndexedAdd(list: MutableList<Int>, index: Int, newItem: Int) {
    val endInserted = list.size == index

    val old = list.tryGet(index)
    val oldSize = list.size

    assertDoesNotThrow{ list.add(index, newItem) }

    val currentItem = list[index]
    val currentSize = list.size
    val next = list.tryGet(index + 1)

    assertEquals(oldSize + 1, currentSize)
    assertEquals(newItem, currentItem)

    if (endInserted) {
        assertFailsWith<IndexOutOfBoundsException>{ old.getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ next.getOrThrow() }
    }
    else {
        val oldItem = assertDoesNotThrow{ old.getOrThrow() }
        val nextItem = assertDoesNotThrow{ next.getOrThrow() }

        assertEquals(oldItem, nextItem)
    }
}

fun testIndexedAddOutOfBounds(list: MutableList<Int>, index: Int, newItem: Int) {
    val oldSize = list.size

    assertFailsWith<IndexOutOfBoundsException>{ list.add(index, newItem) }

    val newSize = list.size

    assertEquals(oldSize, newSize)
    assertTrue(newItem !in list)
    assertTrue(index !in list.indices)
}

fun testAddAllWithOther(list: MutableList<Int>, other: Collection<Int>) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(other) }

    assertTrue(change)
    assertEquals(oldSize + other.size, list.size)

    val iter = other.iterator()

    for (index in oldSize until list.size) {
        val listItem = list[index]
        val otherItem = iter.next()

        assertEquals(listItem, otherItem)
    }
}

fun testAddAllWithEmpty(list: MutableList<Int>, empty: Collection<Int>) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(empty) }

    assertTrue(!change)
    assertEquals(oldSize, list.size)
}

fun testAddAllWithSelf(list: MutableList<Int>) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(list) }

    assertTrue(change)
    assertEquals(2 * oldSize, list.size)

    for (index in 0 until oldSize) {
        val oldItem = list[index]
        val newItem = list[index + oldSize]

        assertEquals(oldItem, newItem)
    }
}

fun testIndexedAddAllWithOther(list: MutableList<Int>, index: Int, other: Collection<Int>) {
    val copy = list.toList()
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(index, other) }

    assertTrue(change)
    assertEquals(oldSize + other.size, list.size)

    val range = index up other.size
    val copyIter = copy.iterator()
    val otherIter = other.iterator()

    for (listIndex in list.indices) {
        val listElem = list[listIndex]
        val iterElem =
            if (listIndex in range)
                otherIter.next()
            else
                copyIter.next()

        assertEquals(listElem, iterElem)
    }
}

fun testIndexedAddAllWithEmpty(list: MutableList<Int>, index: Int, empty: Collection<Int>) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(index, empty) }

    assertTrue(!change)
    assertEquals(oldSize, list.size)
}

fun testIndexedAddAllWithSelf(list: MutableList<Int>, index: Int) {
    val copy = list.toList()
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.addAll(index, list) }

    assertTrue(change)
    assertEquals(2 * oldSize, list.size)

    val range = index up oldSize
    val iter1 = copy.iterator()
    val iter2 = copy.iterator()

    for (listIndex in list.indices) {
        val listElem = list[listIndex]
        val copyElem =
            if (listIndex in range)
                iter1.next()
            else
                iter2.next()

        assertEquals(listElem, copyElem)
    }
}

fun testIndexedAddAllOutOfBounds(list: MutableList<Int>, index: Int, other: Collection<Int>) {
    assertFailsWith<IndexOutOfBoundsException>{ list.addAll(index, other) }
}

fun testInsert(list: MutableList<Int>, other: Collection<Int>) {
    val oldSize = list.size

    val amountAdded = assertDoesNotThrow{ list.insert(other) }

    assertEquals(list.size, oldSize + amountAdded)
}

fun testResizeByIncrease(list: MutableList<Int>, newSize: Int, value: Int) {
    val oldSize = list.size

    assertDoesNotThrow{ list.resize(newSize) { value } }

    assertEquals(newSize, list.size)

    assertEquals(oldSize, list.indexOf(value))
    assertEquals(list.lastIndex, list.lastIndexOf(value))
}

fun testResizeByDecrease(list: MutableList<Int>, newSize: Int, value: Int) {
    assertDoesNotThrow{ list.resize(newSize) { value } }

    assertEquals(newSize, list.size)

    assertEquals(-1, list.indexOf(value))
}

fun testInvalidResize(list: MutableList<Int>, newSize: Int, value: Int) {
    val oldSize = list.size

    assertFailsWith<IllegalArgumentException>{ list.resize(newSize) { value } }

    assertEquals(oldSize, list.size)

    assertEquals(-1, list.indexOf(value))
}

fun testRemoveByElement(list: MutableList<Int>, item: Int, succeeds: Boolean) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.remove(item) }

    assertEquals(succeeds, change)

    if (succeeds) {
        assertEquals(oldSize - 1, list.size)
    }
    else {
        assertEquals(oldSize, list.size)
    }
}

fun testRemoveAllByElements(list: MutableList<Int>, other: Collection<Int>, succeeds: Boolean) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.removeAll(other) }

    assertEquals(succeeds, change)
    assertTrue(oldSize - other.size <= list.size)
}

fun testDeleteWithOther(list: MutableList<Int>, other: Collection<Int>, removalAmount: Int) {
    val copy = list.toMutableList()

    val oldSize = list.size

    val change = copy.removeAll(other)
    val amountRemoved = assertDoesNotThrow{ list.delete(other) }

    val newSize = list.size

    assertEquals(list, copy)
    assertTrue(change)
    assertEquals(amountRemoved, removalAmount)
    assertTrue(amountRemoved <= other.size)
    assertEquals(newSize, oldSize - amountRemoved)
}

fun testRemoveByIndex(list: MutableList<Int>, index: Int) {
    val endRemoved = list.lastIndex == index

    val initialItem = list[index]
    val new = list.tryGet(index + 1)
    val initialSize = list.size

    val oldItem = assertDoesNotThrow{ list.removeAt(index) }
    val current = list.tryGet(index)

    assertEquals(oldItem, initialItem)
    assertEquals(initialSize - 1, list.size)

    if (endRemoved) {
        assertFailsWith<IndexOutOfBoundsException>{ new.getOrThrow() }
        assertFailsWith<IndexOutOfBoundsException>{ current.getOrThrow() }
    }
    else {
        val newItem = new.getOrThrow()
        val currentItem = current.getOrThrow()

        assertEquals(newItem, currentItem)
    }
}

fun testRemoveByIndexOutOfBounds(list: MutableList<Int>, index: Int) {
    val oldSize = list.size

    assertFailsWith<IndexOutOfBoundsException>{ list.removeAt(index) }

    assertEquals(oldSize, list.size)
}

fun testRemoveAllByElement(list: MutableList<Int>, item: Int, removalAmount: Int) {
    val oldSize = list.size

    val amountRemoved = assertDoesNotThrow{ list.removeAllOf(item) }

    assertTrue(item !in list)

    assertEquals(list.size, oldSize - amountRemoved)
    assertEquals(removalAmount, amountRemoved)
}

fun testRemoveAllByPredicate(list: MutableList<Int>, predicate: (Int) -> Boolean, removalAmount: Int) {
    val oldSize = list.size

    val amountRemoved = assertDoesNotThrow{ list.removeAllOf(predicate) }

    assertTrue(list.none(predicate))

    assertEquals(list.size, oldSize - amountRemoved)
    assertEquals(removalAmount, amountRemoved)
}

fun testRemoveAmountByElement(list: MutableList<Int>, item: Int, amountToRemove: Int, removalAmount: Int) {
    val oldSize = list.size
    val oldCount = list.count{ it == item }

    val amountRemoved = assertDoesNotThrow{ list.removeAmount(amountToRemove, item) }

    val newSize = list.size
    val newCount = list.count{ it == item }

    assertEquals(oldSize, newSize + amountRemoved)
    assertEquals(oldCount, newCount + amountRemoved)

    assertEquals(removalAmount, amountRemoved)
}

fun testRemoveNegativeAmountByElement(list: MutableList<Int>, item: Int, amountToRemove: Int) {
    val oldSize = list.size
    val oldCount = list.count{ it == item }

    assertFailsWith<IllegalArgumentException>{ list.removeAmount(amountToRemove, item) }

    val newSize = list.size
    val newCount = list.count{ it == item }

    assertEquals(oldSize, newSize)
    assertEquals(oldCount, newCount)
}

fun testRemoveAmountByPredicate(list: MutableList<Int>, predicate: (Int) -> Boolean, amountToRemove: Int, removalAmount: Int) {
    val oldSize = list.size
    val oldCount = list.count(predicate)

    val amountRemoved = assertDoesNotThrow{ list.removeAmount(amountToRemove, predicate) }

    val newSize = list.size
    val newCount = list.count(predicate)

    assertEquals(oldSize, newSize + amountRemoved)
    assertEquals(oldCount, newCount + amountRemoved)

    assertEquals(removalAmount, amountRemoved)
}

fun testRemoveNegativeAmount(list: MutableList<Int>, predicate: (Int) -> Boolean, amountToRemove: Int) {
    val oldSize = list.size
    val oldCount = list.count(predicate)

    assertFailsWith<IllegalArgumentException>{ list.removeAmount(amountToRemove, predicate) }

    val newSize = list.size
    val newCount = list.count(predicate)

    assertEquals(oldSize, newSize)
    assertEquals(oldCount, newCount)
}

fun testRetainAllWithOther(list: MutableList<Int>, other: Collection<Int>) {
    val change = assertDoesNotThrow{ list.retainAll(other) }

    assertTrue(change)
    assertTrue(list.size <= other.size)
    assertTrue(other.containsAll(list))
}

fun testRetainAllWithSelf(list: MutableList<Int>) {
    val oldSize = list.size

    val change = assertDoesNotThrow{ list.retainAll(list) }

    val newSize = list.size

    assertTrue(!change)
    assertEquals(oldSize, newSize)
}

fun testRetainAllWithEmpty(list: MutableList<Int>, empty: Collection<Int>) {
    val change = assertDoesNotThrow{ list.retainAll(empty) }

    assertTrue(change)
    assertEquals(0, list.size)
}

fun testClear(list: MutableList<Int>) {
    assertDoesNotThrow{ list.clear() }

    assertTrue(list.isEmpty())
    assertEquals(0, list.size)

    assertFailsWith<IndexOutOfBoundsException>{ list[0] }
}

fun testRemoveFromBack(list: MutableList<Int>, amountToRemove: Int) {
    val expectedSize = max(0, list.size - amountToRemove)
    val removalAmount = list.size - expectedSize

    val amountRemoved = assertDoesNotThrow{ list.removeFromBack(amountToRemove) }

    assertEquals(expectedSize, list.size)
    assertEquals(removalAmount, amountRemoved)
}

fun testRemoveFromBackWithNegativeAmount(list: MutableList<Int>, amountToRemove: Int) {
    val oldSize = list.size

    assertFailsWith<IllegalArgumentException>{ list.removeFromBack(amountToRemove) }

    assertEquals(oldSize, list.size)
}

fun testRemoveRange(list: MutableList<Int>, fromIndex: Int, toIndex: Int) {
    val range = list.subList(fromIndex, toIndex).toList()
    val expectedSize = list.size - (toIndex - fromIndex)

    assertDoesNotThrow{ list.removeRange(fromIndex, toIndex) }

    assertEquals(expectedSize, list.size)
    assertTrue(!list.containsAll(range))
}

fun testRemoveInvalidRange(list: MutableList<Int>, fromIndex: Int, toIndex: Int) {
    assertFailsWith<IndexOutOfBoundsException>{ list.removeRange(fromIndex, toIndex) }
}

fun testKeepWithOther(list: MutableList<Int>, other: Collection<Int>) {
    val oldSize = list.size

    val amountRemoved = assertDoesNotThrow{ list.keep(other) }

    assertTrue(amountRemoved <= oldSize)
    assertTrue(list.size <= other.size)
    assertTrue(other.containsAll(list))
}

fun testKeepWithSelf(list: MutableList<Int>) {
    val oldSize = list.size

    val amountRemoved = assertDoesNotThrow{ list.keep(list) }

    assertEquals(0, amountRemoved)
    assertEquals(oldSize, list.size)
}

fun testKeepWithEmpty(list: MutableList<Int>, empty: Collection<Int>) {
    val oldSize = list.size

    val amountRemoved = assertDoesNotThrow{ list.keep(empty) }

    assertEquals(oldSize, amountRemoved)
    assertEquals(0, list.size)
}
