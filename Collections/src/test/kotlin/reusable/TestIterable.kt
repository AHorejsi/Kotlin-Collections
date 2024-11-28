package reusable

import collections.*
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

fun testAtLeast(amount: Int, lesser: Iterable<*>, equal: Iterable<*>, greater: Iterable<*>) {
    val lesserResult = assertDoesNotThrow{ lesser.atLeast(amount) }
    val equalResult = assertDoesNotThrow{ equal.atLeast(amount) }
    val greaterResult = assertDoesNotThrow{ greater.atLeast(amount) }

    assertTrue(!lesserResult)
    assertTrue(equalResult)
    assertTrue(greaterResult)
}

fun testAtMost(amount: Int, lesser: Iterable<*>, equal: Iterable<*>, greater: Iterable<*>) {
    val lesserResult = assertDoesNotThrow{ lesser.atMost(amount) }
    val equalResult = assertDoesNotThrow{ equal.atMost(amount) }
    val greaterResult = assertDoesNotThrow{ greater.atMost(amount) }

    assertTrue(lesserResult)
    assertTrue(equalResult)
    assertTrue(!greaterResult)
}

fun testExactly(amount: Int, lesser: Iterable<*>, equal: Iterable<*>, greater: Iterable<*>) {
    val lesserResult = assertDoesNotThrow{ lesser.exactly(amount) }
    val equalResult = assertDoesNotThrow{ equal.exactly(amount) }
    val greaterResult = assertDoesNotThrow{ greater.exactly(amount) }

    assertTrue(!lesserResult)
    assertTrue(equalResult)
    assertTrue(!greaterResult)
}

fun testTryFirst(iterable: Iterable<Int>, predicate: (Int) -> Boolean, first: Int, predicatedFirst: Int) {
    val result1 = assertDoesNotThrow{ iterable.tryFirst() }
    val result2 = assertDoesNotThrow{ iterable.tryFirst(predicate) }

    val item1 = assertDoesNotThrow{ result1.getOrThrow() }
    val item2 = assertDoesNotThrow{ result2.getOrThrow() }

    assertEquals(first, item1)
    assertEquals(predicatedFirst, item2)
}

fun testTryFirstOnEmpty(iterable: Iterable<Int>, predicate: (Int) -> Boolean) {
    val result1 = assertDoesNotThrow{ iterable.tryFirst() }
    val result2 = assertDoesNotThrow{ iterable.tryFirst(predicate) }

    assertFailsWith<NoSuchElementException>{ result1.getOrThrow() }
    assertFailsWith<NoSuchElementException>{ result2.getOrThrow() }
}

fun testTryLast(iterable: Iterable<Int>, predicate: (Int) -> Boolean, last: Int, predicatedLast: Int) {
    val result1 = assertDoesNotThrow{ iterable.tryLast() }
    val result2 = assertDoesNotThrow{ iterable.tryLast(predicate) }

    val item1 = assertDoesNotThrow{ result1.getOrThrow() }
    val item2 = assertDoesNotThrow{ result2.getOrThrow() }

    assertEquals(last, item1)
    assertEquals(predicatedLast, item2)
}

fun testTryLastOnEmpty(iterable: Iterable<Int>, predicate: (Int) -> Boolean) {
    val result1 = assertDoesNotThrow{ iterable.tryLast() }
    val result2 = assertDoesNotThrow{ iterable.tryLast(predicate) }

    assertFailsWith<NoSuchElementException>{ result1.getOrThrow() }
    assertFailsWith<NoSuchElementException>{ result2.getOrThrow() }
}

fun testIteratorConstruction(iterable: Iterable<Int>) {
    assertDoesNotThrow{ iterable.iterator() }
}