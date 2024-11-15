package reusable

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
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
