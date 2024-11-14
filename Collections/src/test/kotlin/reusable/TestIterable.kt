package reusable

import collections.atLeast
import collections.atMost
import collections.exactly
import org.junit.jupiter.api.assertDoesNotThrow
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