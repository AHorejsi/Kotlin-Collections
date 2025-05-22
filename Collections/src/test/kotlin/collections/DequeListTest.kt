package collections

import asserts.assertLessEqual
import org.junit.jupiter.api.assertDoesNotThrow
import reusable.*
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
        val value = 1

        val deque = assertDoesNotThrow{ DequeList(size) { value } }

        assertEquals(size, deque.size)
        assertEquals(size, deque.capacity)
        assertTrue(deque.all(value::equals))
    }

    @Test
    fun testDequeListOf() {
        val deque1 = dequeListOf<Int>()
        val deque2 = dequeListOf(3, 0, 2, 6)

        assertEquals(0, deque1.size)
        assertEquals(4, deque2.size)

        println(deque1)
        println(deque2)

        assertEquals(3, deque2[0])
        assertEquals(0, deque2[1])
        assertEquals(2, deque2[2])
        assertEquals(6, deque2[3])
    }

    @Test
    fun testToDequeList() {
        val set = hashSetOf(8, 4, 1, 6, 3, 0, 5, 1, -1)
        val seq = sequenceOf(0, 5, 2, -2, 7, -3)
        val iter = (-3 until 17 step 4).asIterable()
        val array = arrayOf(7, -6, 2, 0, -3, 9, 0)

        val dequeFromSet = assertDoesNotThrow{ set.toDequeList() }
        val dequeFromSeq = assertDoesNotThrow{ seq.toDequeList() }
        val dequeFromIter = assertDoesNotThrow{ iter.toDequeList() }
        val dequeFromArray = assertDoesNotThrow{ array.toDequeList() }

        testIteratorEquality(set.iterator(), dequeFromSet.iterator())
        testIteratorEquality(seq.iterator(), dequeFromSeq.iterator())
        testIteratorEquality(iter.iterator(), dequeFromIter.iterator())
        testIteratorEquality(array.iterator(), dequeFromArray.iterator())

        assertLessEqual(set.size, dequeFromSet.capacity)
        assertLessEqual(seq.count(), dequeFromSeq.capacity)
        assertLessEqual(iter.count(), dequeFromIter.capacity)
        assertLessEqual(array.size, dequeFromArray.capacity)
    }

    @Test
    fun testIsRandomAccess() {
        val deque = dequeListOf<Int>()

        testIsRandomAccess(deque, true)
    }

    @Test
    fun testWithIndex() {
        TODO()
    }

    @Test
    fun testAtLeast() {
        val amount = 100

        val lesser = assertDoesNotThrow{ DequeList(amount - 1) { 0 } }
        val equal = assertDoesNotThrow{ DequeList(amount) { 0 } }
        val greater = assertDoesNotThrow{ DequeList(amount + 1) { 0 } }

        testAtLeast(amount, lesser, equal, greater)
    }

    @Test
    fun testAtMost() {
        val amount = 100

        val lesser = assertDoesNotThrow{ DequeList(amount - 1) { 0 } }
        val equal = assertDoesNotThrow{ DequeList(amount) { 0 } }
        val greater = assertDoesNotThrow{ DequeList(amount + 1) { 0 } }

        testAtMost(amount, lesser, equal, greater)
    }

    @Test
    fun testExactly() {
        val amount = 100

        val lesser = assertDoesNotThrow{ DequeList(amount - 1) { 0 } }
        val equal = assertDoesNotThrow{ DequeList(amount) { 0 } }
        val greater = assertDoesNotThrow{ DequeList(amount + 1) { 0 } }

        testExactly(amount, lesser, equal, greater)
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
        val deques = this.createDeques()

        for (deq in deques) {
            testGetDuringIteration(deq)

            testGetAfterAdding(deq, -1)
            testGetAfterAdding(deq, -2)

            testGetAt(deq, deq.size - 2, -1)

            testGetAfterAddingAt(deq, 0, -3)
            testGetAfterAddingAt(deq, deq.size, -4)
            testGetAfterAddingAt(deq, deq.size / 2, -5)

            testGetAfterRemovingAt(deq, 0)
            testGetAfterRemovingAt(deq, deq.size / 4)
            testGetAfterRemovingAt(deq, 3 * deq.size / 4)
            testGetAfterRemovingAt(deq, deq.lastIndex)

            testGetDuringIteration(deq)

            testGetOutOfBounds(deq)
        }
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

    private fun createDeques(): List<DequeList<Int>> {
        // Create a deque that has its elements arranged like a VectorList
        val deque1 = (1 .. 100).toDequeList()

        // Create a deque that has its elements in the center of the underlying and empty slots on the edges
        val deque2 = deque1.toDequeList()

        repeat(10) {
            deque2.removeFirst()
        }
        repeat(20) {
            deque2.removeLast()
        }

        // Create a deque that has its elements arranged with a cross-over from the end to the beginning of the underlying array
        val deque3 = deque1.toDequeList()

        repeat(15) {
            deque3.removeFirst()
        }
        repeat(10) {
            deque3.removeLast()
        }
        repeat(5) {
            deque3.addFirst(it)
        }

        return listOf(deque1, deque2, deque3)
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
