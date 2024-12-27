package reusable

import asserts.assertGreater
import asserts.assertLess
import asserts.assertNotContains
import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

fun testConstructionOnSublist(list: List<Int>) {
    for (startIndex in list.indices) {
        for (endIndex in 0 .. list.size) {
            testConstructionOnSublist(list, startIndex, endIndex)
        }
    }

    assertFailsWith<IndexOutOfBoundsException>{ list.subList(-1, list.size) }
    assertFailsWith<IndexOutOfBoundsException>{ list.subList(0, list.size + 1) }
}

private fun testConstructionOnSublist(list: List<Int>, startIndex: Int, endIndex: Int) {
    if (endIndex < startIndex) {
        assertFailsWith<IllegalArgumentException>{ list.subList(startIndex, endIndex) }
    }
    else {
        assertDoesNotThrow{ list.subList(startIndex, endIndex) }
    }
}

fun testIsRandomAccessOnSublist(base: List<*>) {
    val sub = base.subList(0, 0)

    val result1 = assertDoesNotThrow{ base.isRandomAccess }
    val result2 = assertDoesNotThrow{ sub.isRandomAccess }

    assertEquals(result1, result2)
}

fun testWithIndexOnSublists(base: List<*>) {
    for (startIndex in 0 until base.size) {
        for (endIndex in startIndex .. base.size) {
            val sub = base.subList(startIndex, endIndex)

            testWithIndex(sub)
        }
    }
}

fun testSizeOnSublistConstruction(base: List<Int>, startIndex: Int, endIndex: Int) {
    val sub = base.subList(startIndex, endIndex)

    val initialSize = assertDoesNotThrow{ sub.size }
    assertEquals(endIndex - startIndex, initialSize)
}

fun testSizeAfterAddingOnSublist(base: MutableList<Int>, startIndex: Int, endIndex: Int, other: Collection<Int>) {
    val sub = base.subList(startIndex, endIndex)

    val initialSize = assertDoesNotThrow{ base.size }
    val initialSubSize = assertDoesNotThrow{ sub.size }

    sub.addAll(other)

    val finalSize = assertDoesNotThrow{ base.size }
    val finalSubSize = assertDoesNotThrow{ sub.size }

    assertEquals(finalSize, initialSize + other.size)
    assertEquals(finalSubSize, initialSubSize + other.size)
}

fun testSizeAfterRemovingOnSublist(base: MutableList<Int>, startIndex: Int, endIndex: Int, amountToRemove: Int) {
    val sub = base.subList(startIndex, endIndex)

    val initialSize = assertDoesNotThrow{ sub.size }
    val initialSubSize = assertDoesNotThrow{ sub.size }

    sub.removeFromBack(amountToRemove)

    val finalSize = assertDoesNotThrow{ sub.size }
    val finalSubSize = assertDoesNotThrow{ sub.size }

    assertEquals(finalSize, initialSize - amountToRemove)
    assertEquals(finalSubSize, initialSubSize - amountToRemove)
}

fun testIsEmptyAfterConstructionOnSublist(list: MutableList<Int>) {
    for (startIndex in list.indices) {
        for (endIndex in startIndex .. list.size) {
            testEmptyConstruction(list, startIndex, endIndex)
        }
    }
}

private fun testEmptyConstruction(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)
    val result = assertDoesNotThrow{ sub.isEmpty() }

    if (startIndex == endIndex) {
        assertTrue(result)
    }
    else {
        assertTrue(!result)
    }
}

fun testIsEmptyAfterModificationOnSublist(list: MutableList<Int>, index: Int) {
    val sub = list.subList(index, index)
    val onConstruct = assertDoesNotThrow{ sub.isEmpty() }
    assertTrue(onConstruct)

    sub.add(-1)
    val onAdd = assertDoesNotThrow{ sub.isEmpty() }
    assertTrue(!onAdd)

    sub.removeLast()
    val onRemove = assertDoesNotThrow{ sub.isEmpty() }
    assertTrue(onRemove)
}

fun testGetOnSublist(list: List<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)

    for (subIndex in sub.indices) {
        val listIndex = subIndex + startIndex

        val vecItem = assertDoesNotThrow{ list[listIndex] }
        val subItem = assertDoesNotThrow{ sub[subIndex] }

        assertEquals(subItem, vecItem)
    }

    @Suppress("KotlinConstantConditions")
    run {
        assertFailsWith<IndexOutOfBoundsException>{ sub[-1] }
        assertFailsWith<IndexOutOfBoundsException>{ sub[sub.size] }
    }
}

fun testSetOnSublist(list: MutableList<Int>, sub: MutableList<Int>, startIndex: Int, index: Int, newItem: Int) {
    val initialItem = list[index + startIndex]

    val oldItem = assertDoesNotThrow{ sub.set(index, newItem) }

    val currentItem = list[index + startIndex]

    assertEquals(initialItem, oldItem)
    assertEquals(currentItem, newItem)
}

fun testAddOnSublist(list: MutableList<Int>, sub: MutableList<Int>, endIndex: Int, newItem: Int) {
    val oldSize = list.size
    val oldSubSize = sub.size

    val result = assertDoesNotThrow{ sub.add(newItem) }

    assertTrue(result)
    assertEquals(newItem, sub[sub.lastIndex])
    assertEquals(newItem, list[endIndex])

    assertEquals(list.size, oldSize + 1)
    assertEquals(sub.size, oldSubSize + 1)
}

fun testIndexedAddOnSublist(
    list: MutableList<Int>,
    sub: MutableList<Int>,
    startIndex: Int,
    insertIndex: Int,
    newItem: Int
) {
    val oldSize = list.size
    val oldSubSize = sub.size

    assertDoesNotThrow{ sub.add(insertIndex, newItem) }

    assertEquals(newItem, sub[insertIndex])
    assertEquals(newItem, list[startIndex + insertIndex])

    assertEquals(list.size, oldSize + 1)
    assertEquals(sub.size, oldSubSize + 1)
}

fun testAddAllWithOtherOnSublist(list: MutableList<Int>, other: Collection<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(other) }

    assertTrue(change)
    assertEquals(list.size, other.size + oldSize)
    assertEquals(sub.size, other.size + oldSubSize)

    testElementsAfterAddingOther(list, sub, other, startIndex, endIndex)
}

fun testInsertWithOtherOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int, other: Collection<Int>) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val amountAdded = assertDoesNotThrow{ sub.insert(other) }

    assertEquals(other.size, amountAdded)
    assertEquals(list.size, oldSize + amountAdded)
    assertEquals(sub.size, oldSubSize + amountAdded)

    testElementsAfterAddingOther(list, sub, other, startIndex, endIndex)
}

private fun testElementsAfterAddingOther(
    list: MutableList<Int>,
    sub: MutableList<Int>,
    other: Collection<Int>,
    startIndex: Int,
    endIndex: Int
) {
    val iter = other.iterator()

    for (index in endIndex up other.size) {
        val listItem = list[index]
        val subItem = sub[index - startIndex]
        val otherItem = iter.next()

        assertEquals(listItem, otherItem)
        assertEquals(otherItem, subItem)
    }
}

fun testAddAllWithEmptyOnSublist(list: MutableList<Int>, empty: Collection<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(empty) }

    assertTrue(!change)
    assertEquals(list.size, oldSize)
    assertEquals(sub.size, oldSubSize)
}

fun testInsertWithEmptyOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int, other: Collection<Int>) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val amountAdded = assertDoesNotThrow{ sub.insert(other) }

    assertEquals(0, amountAdded)
    assertEquals(list.size, oldSize)
    assertEquals(sub.size, oldSubSize)
}

fun testAddAllWithSelfOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(sub) }

    assertTrue(change)
    assertEquals(list.size, oldSize + oldSubSize)
    assertEquals(sub.size, 2 * oldSubSize)

    testElementsAfterAddingSelf(list, sub, oldSubSize, startIndex, endIndex)
}

fun testInsertWithSelfOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val amountAdded = assertDoesNotThrow{ sub.insert(sub) }

    assertEquals(oldSubSize, amountAdded)
    assertEquals(list.size, oldSize + oldSubSize)
    assertEquals(sub.size, 2 * oldSubSize)

    testElementsAfterAddingSelf(list, sub, oldSubSize, startIndex, endIndex)
}

private fun testElementsAfterAddingSelf(
    list: MutableList<Int>,
    sub: MutableList<Int>,
    oldSubSize: Int,
    startIndex: Int,
    endIndex: Int
) {
    for (index in startIndex until endIndex) {
        val subIndex = index - startIndex

        val listItem = list[index]
        val subItem = sub[subIndex]
        val newSubItem = sub[subIndex + oldSubSize]

        assertEquals(subItem, listItem)
        assertEquals(listItem, newSubItem)
    }
}

fun testAddAllWithBaseOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)
    val copy = list.toList()

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(list) }

    val newSize = 2 * oldSize
    val newSubSize = oldSubSize + oldSize

    assertTrue(change)
    assertEquals(list.size, newSize)
    assertEquals(sub.size, newSubSize)

    testElementsAfterAddingBase(list, oldSize, copy, sub, startIndex, endIndex)
}

fun testInsertWithBaseOnSublist(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)
    val copy = list.toList()

    val oldSize = list.size
    val oldSubSize = sub.size

    val amountAdded = assertDoesNotThrow{ sub.insert(list) }

    val newSize = 2 * oldSize
    val newSubSize = oldSubSize + oldSize

    assertEquals(oldSize, amountAdded)
    assertEquals(list.size, newSize)
    assertEquals(sub.size, newSubSize)

    testElementsAfterAddingBase(list, oldSize, copy, sub, startIndex, endIndex)
}

private fun testElementsAfterAddingBase(
    list: MutableList<Int>,
    oldSize: Int,
    copy: List<Int>,
    sub: MutableList<Int>,
    startIndex: Int,
    endIndex: Int
) {
    val iter = copy.iterator()

    for (index in list.indices) {
        val listItem = list[index]

        if (index in startIndex up sub.size) {
            val subItem = sub[index - startIndex]

            assertEquals(listItem, subItem)
        }

        if (index in endIndex up oldSize) {
            val copyItem = copy[index - endIndex]

            assertEquals(listItem, copyItem)
        }
        else {
            val iterItem = iter.next()

            assertEquals(listItem, iterItem)
        }
    }
}

fun testAddAllWithSublistOnBaseList(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)
    val copy = sub.toList().iterator()

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ list.addAll(sub) }

    val newSize = oldSize + oldSubSize

    assertTrue(change)
    assertEquals(list.size, newSize)
    assertFailsWith<ConcurrentModificationException>{ sub.size }

    testElementsAfterAddingSublistIntoBaseList(list, oldSize, copy)
}

fun testInsertWithSublistOnBaseList(list: MutableList<Int>, startIndex: Int, endIndex: Int) {
    val sub = list.subList(startIndex, endIndex)
    val copy = sub.toList().iterator()

    val oldSize = list.size
    val oldSubSize = sub.size

    val amountAdded = assertDoesNotThrow{ list.insert(sub) }

    val newSize = oldSize + oldSubSize

    assertEquals(oldSubSize, amountAdded)
    assertEquals(list.size, newSize)
    assertFailsWith<ConcurrentModificationException>{ sub.size }

    testElementsAfterAddingSublistIntoBaseList(list, oldSize, copy)
}

private fun testElementsAfterAddingSublistIntoBaseList(list: MutableList<Int>, oldSize: Int, copy: Iterator<Int>) {
    for (index in oldSize until list.size) {
        val listItem = list[index]
        val subItem = copy.next()

        assertEquals(listItem, subItem)
    }
}

fun testIndexedAddAllWithOtherOnSublist(
    list: MutableList<Int>,
    other: Collection<Int>,
    startIndex: Int,
    endIndex: Int,
    insertIndex: Int
) {
    val sub = list.subList(startIndex, endIndex)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(insertIndex, other) }

    assertTrue(change)
    assertEquals(list.size, oldSize + other.size)
    assertEquals(sub.size, oldSubSize + other.size)

    val iter = other.iterator()

    for (index in list.indices) {
        val listItem = list[index]

        if (index in startIndex up sub.size) {
            val subItem = sub[index - startIndex]

            assertEquals(listItem, subItem)
        }

        if (index in insertIndex + startIndex up other.size) {
            val iterItem = iter.next()

            assertEquals(listItem, iterItem)
        }
    }
}

fun testIndexedAddAllWithSelfOnSublist(
    list: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    indexSupplier: (List<Int>) -> Int) {
    val sub = list.subList(startIndex, endIndex)
    val insertIndex = indexSupplier(sub)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(insertIndex, sub) }

    assertTrue(change)
    assertEquals(list.size, oldSize + oldSubSize)
    assertEquals(sub.size, 2 * oldSubSize)

    for (index in startIndex up sub.size) {
        val listItem = list[index]
        val subItem = sub[index - startIndex]

        assertEquals(listItem, subItem)
    }
}

fun testIndexedAddAllWithBaseOnSublist(
    list: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    indexSupplier: (List<Int>) -> Int
) {
    val sub = list.subList(startIndex, endIndex)
    val copy = list.toList()
    val insertIndex = indexSupplier(sub)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ sub.addAll(insertIndex, list) }

    val newSize = 2 * oldSize
    val newSubSize = oldSubSize + oldSize

    assertTrue(change)
    assertEquals(list.size, newSize)
    assertEquals(sub.size, newSubSize)

    val iter = copy.iterator()

    for (index in list.indices) {
        val listItem = list[index]

        if (index in startIndex up sub.size) {
            val subItem = sub[index - startIndex]

            assertEquals(listItem, subItem)
        }

        if (index in startIndex + insertIndex up oldSize) {
            val copyItem = copy[index - insertIndex - startIndex]

            assertEquals(listItem, copyItem)
        }
        else {
            val iterItem = iter.next()

            assertEquals(listItem, iterItem)
        }
    }
}

fun testIndexedAddAllWithSublistOnBaseList(
    list: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    indexSupplier: (List<Int>) -> Int
) {
    val sub = list.subList(startIndex, endIndex)
    val copy = sub.toList()
    val insertIndex = indexSupplier(sub)

    val oldSize = list.size
    val oldSubSize = sub.size

    val change = assertDoesNotThrow{ list.addAll(insertIndex, sub) }

    val newSize = oldSize + oldSubSize

    assertTrue(change)
    assertEquals(list.size, newSize)
    assertFailsWith<ConcurrentModificationException>{ sub.size }

    val iter = copy.iterator()

    for (index in insertIndex up oldSubSize) {
        val listItem = list[index]
        val subItem = iter.next()

        assertEquals(listItem, subItem)
    }
}

fun testSwapOnSublist(list: MutableList<String>, sub: MutableList<String>, startIndex: Int, index1: Int, index2: Int) {
    val adjustedIndex1 = startIndex + index1
    val adjustedIndex2 = startIndex + index2

    val oldItem1 = list[adjustedIndex1]
    val oldItem2 = list[adjustedIndex2]

    assertDoesNotThrow{ sub.swap(index1, index2) }

    assertNotSame(list[adjustedIndex1], list[adjustedIndex2])
    assertSame(list[adjustedIndex1], oldItem2)
    assertSame(list[adjustedIndex2], oldItem1)
    assertNotSame(oldItem1, oldItem2)
}

fun testResizeByIncreaseOnSublist(
    list: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    amountToAdd: Int,
    value: Int
) {
    val sub = list.subList(startIndex, endIndex)

    val newSize = list.size + amountToAdd
    val newSubSize = sub.size + amountToAdd

    val sizeChange = assertDoesNotThrow{ sub.resize(newSubSize) { value } }

    assertEquals(amountToAdd, sizeChange)
    assertEquals(sub.size, newSubSize)
    assertEquals(list.size, newSize)

    for (index in list.indices)  {
        val listItem = list[index]

        if (index in startIndex up sub.size) {
            val subItem = sub[index - startIndex]

            assertEquals(listItem, subItem)
        }

        if (index in endIndex up amountToAdd) {
            assertEquals(value, listItem)
        }
    }
}

fun testResizeByDecreaseOnSublist(
    list: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    amountToAdd: Int,
    value: Int
) {
    val sub = list.subList(startIndex, endIndex)

    val newSize = list.size - amountToAdd
    val newSubSize = sub.size - amountToAdd

    val sizeChange = assertDoesNotThrow{ sub.resize(newSubSize) { value } }

    assertEquals(-amountToAdd, sizeChange)
    assertEquals(sub.size, newSubSize)
    assertEquals(list.size, newSize)

    assertNotContains(sub, value)
}
