package collections

import asserts.assertLessEqual
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

class DequeListTest {
    @Test
    fun testPrimaryConstructor() {
        assertDoesNotThrow{ DequeList<Int>(0) }
        assertFailsWith<IllegalArgumentException>{ DequeList<Int>(-1) }
    }

    @Test
    fun testFillConstructor() {
        val size = 30
        val vec = assertDoesNotThrow{ DequeList(size) { 0 } }

        assertEquals(size, vec.size)
        assertLessEqual(size, vec.capacity)
        assertTrue(vec.all(0::equals))
    }

    @Test
    fun testDequeListOf() {
        TODO()
    }

    @Test
    fun testToDequeList() {
        TODO()
    }

    @Test
    fun testIsRandomAccess() {
        TODO()
    }

    @Test
    fun testWithIndex() {
        TODO()
    }

    @Test
    fun testAtLeast() {
        TODO()
    }

    @Test
    fun testAtMost() {
        TODO()
    }

    @Test
    fun testExactly() {
        TODO()
    }

    @Test
    fun testSize() {
        TODO()    }

    @Test
    fun testIsEmpty() {
        TODO()
    }

    @Test
    fun testCapacity() {
        TODO()
    }

    @Test
    fun testEnsureCapacity() {
        TODO()
    }

    @Test
    fun testTrimToSize() {
        TODO()
    }

    @Test
    fun testFirst() {
        TODO()
    }

    @Test
    fun testTryFirst() {
        TODO()
    }

    @Test
    fun testLast() {
        TODO()
    }

    @Test
    fun testTryLast() {
        TODO()
    }

    @Test
    fun testGet() {
        TODO()
    }

    @Test
    fun testWrapGet() {
        TODO()
    }

    @Test
    fun testTryGet() {
        TODO()
    }

    @Test
    fun testSet() {
        TODO()
    }

    @Test
    fun testWrapSet() {
        TODO()
    }

    @Test
    fun testTrySet() {
        TODO()
    }

    @Test
    fun testAdd() {
        TODO()
    }

    @Test
    fun testAddFirst() {
        TODO()
    }

    @Test
    fun testAddLast() {
        TODO()
    }

    @Test
    fun testAddAll() {
        TODO()
    }

    @Test
    fun testAddToFront() {
        TODO()
    }

    @Test
    fun testAddToBack() {
        TODO()
    }

    @Test
    fun testInsert() {
        TODO()
    }

    @Test
    fun testResize() {
        TODO()
    }

    @Test
    fun testRemove() {
        TODO()
    }

    @Test
    fun testRemoveAll() {
        TODO()
    }

    @Test
    fun testDelete() {
        TODO()
    }

    @Test
    fun testRemoveAt() {
        TODO()
    }

    @Test
    fun testRemoveFirst() {
        TODO()
    }

    @Test
    fun testRemoveLast() {
        TODO()
    }

    @Test
    fun testRemoveFromFront() {
        TODO()
    }

    @Test
    fun testRemoveFromBack() {
        TODO()
    }

    @Test
    fun testRemoveRange() {
        TODO()
    }

    @Test
    fun testRemoveAllOf() {
        TODO()
    }

    @Test
    fun testRemoveAmount() {
        TODO()
    }

    @Test
    fun testRetainAll() {
        TODO()
    }

    @Test
    fun testKeep() {
        TODO()
    }

    @Test
    fun testClear() {
        TODO()
    }

    @Test
    fun testContains() {
        TODO()
    }

    @Test
    fun testContainsAll() {
        TODO()
    }

    @Test
    fun testIndexOf() {
        TODO()
    }

    @Test
    fun testLastIndexOf() {
        TODO()
    }

    @Test
    fun testSwap() {
        TODO()
    }

    @Test
    fun testIsSorted() {
        TODO()
    }

    @Test
    fun testIsSortedUntil() {
        TODO()
    }

    @Test
    fun testEquals() {
        TODO()
    }

    @Test
    fun testHashCode() {
        TODO()
    }

    @Test
    fun testIsPermutationOf() {
        TODO()
    }

    @Test
    fun testNext() {
        TODO()
    }

    @Test
    fun testPrev() {
        TODO()
    }

    @Test
    fun testSeparationPoint() {
        TODO()
    }

    @Test
    fun testSeparate() {
        TODO()
    }

    @Test
    fun testStableSeparate() {
        TODO()
    }

    @Test
    fun testRotate() {
        TODO()
    }

    @Test
    fun testIntersperse() {
        TODO()
    }

    @Test
    fun testCompare() {
        TODO()
    }

    @Test
    fun testToString() {
        TODO()
    }
}

class DequeIteratorTest {
    @Test
    fun testConstructor() {
        TODO()
    }

    @Test
    fun testHasNext() {
        TODO()
    }

    @Test
    fun testNext() {
        TODO()
    }

    @Test
    fun testRemove() {
        TODO()
    }
}

class DequeListListIteratorTest {
    @Test
    fun testConstructor() {
        TODO()
    }

    @Test
    fun testPreviousIndex() {
        TODO()
    }

    @Test
    fun testNextIndex() {
        TODO()
    }

    @Test
    fun testHasPrevious() {
        TODO()
    }

    @Test
    fun testHasNext() {
        TODO()
    }

    @Test
    fun testPrevious() {
        TODO()
    }

    @Test
    fun testNext() {
        TODO()
    }

    @Test
    fun testSet() {
        TODO()
    }

    @Test
    fun testRemove() {
        TODO()
    }

    @Test
    fun testAdd() {
        TODO()
    }

    @Test
    fun testConcurrentModification() {
        TODO()
    }
}

class DequeSublistTest {
    @Test
    fun testConstructor() {
        TODO()
    }

    @Test
    fun testIsRandomAccess() {
        TODO()
    }

    @Test
    fun testWithIndex() {
        TODO()
    }

    @Test
    fun testAtLeast() {
        TODO()
    }

    @Test
    fun testAtMost() {
        TODO()
    }

    @Test
    fun testExactly() {
        TODO()
    }

    @Test
    fun testSize() {
        TODO()
    }

    @Test
    fun testIsEmpty() {
        TODO()
    }

    @Test
    fun testCapacity() {
        TODO()
    }

    @Test
    fun testEnsureCapacity() {
        TODO()
    }

    @Test
    fun testTrimToSize() {
        TODO()
    }

    @Test
    fun testFirst() {
        TODO()
    }

    @Test
    fun testTryFirst() {
        TODO()
    }

    @Test
    fun testLast() {
        TODO()
    }

    @Test
    fun testTryLast() {
        TODO()
    }

    @Test
    fun testGet() {
        TODO()
    }

    @Test
    fun testWrapGet() {
        TODO()
    }

    @Test
    fun testTryGet() {
        TODO()
    }

    @Test
    fun testSet() {
        TODO()
    }

    @Test
    fun testWrapSet() {
        TODO()
    }

    @Test
    fun testTrySet() {
        TODO()
    }

    @Test
    fun testAdd() {
        TODO()
    }

    @Test
    fun testAddAll() {
        TODO()
    }

    @Test
    fun testInsert() {
        TODO()
    }

    @Test
    fun testResize() {
        TODO()
    }

    @Test
    fun testRemove() {
        TODO()
    }

    @Test
    fun testRemoveAll() {
        TODO()
    }

    @Test
    fun testDelete() {
        TODO()
    }

    @Test
    fun testRemoveAt() {
        TODO()
    }

    @Test
    fun testRemoveFromBack() {
        TODO()
    }

    @Test
    fun testRemoveRange() {
        TODO()
    }

    @Test
    fun testRemoveAllOf() {
        TODO()
    }

    @Test
    fun testRemoveAmount() {
        TODO()
    }

    @Test
    fun testRetainAll() {
        TODO()
    }

    @Test
    fun testKeep() {
        TODO()
    }

    @Test
    fun testClear() {
        TODO()
    }

    @Test
    fun testContains() {
        TODO()
    }

    @Test
    fun testContainsAll() {
        TODO()
    }

    @Test
    fun testIndexOf() {
        TODO()
    }

    @Test
    fun testLastIndexOf() {
        TODO()
    }

    @Test
    fun testSwap() {
        TODO()
    }

    @Test
    fun testIsSorted() {
        TODO()
    }

    @Test
    fun testIsSortedUntil() {
        TODO()
    }

    @Test
    fun testEquals() {
        TODO()
    }

    @Test
    fun testHashCode() {
        TODO()
    }

    @Test
    fun testIsPermutationOf() {
        TODO()
    }

    @Test
    fun testNext() {
        TODO()
    }

    @Test
    fun testPrev() {
        TODO()
    }

    @Test
    fun testSeparationPoint() {
        TODO()
    }

    @Test
    fun testSeparate() {
        TODO()
    }

    @Test
    fun testStableSeparate() {
        TODO()
    }

    @Test
    fun testRotate() {
        TODO()
    }

    @Test
    fun testIntersperse() {
        TODO()
    }

    @Test
    fun testCompare() {
        TODO()
    }

    @Test
    fun testToString() {
        TODO()
    }
}
