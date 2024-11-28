package reusable

import collections.isRandomAccess
import collections.removeFromBack
import collections.swap
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
