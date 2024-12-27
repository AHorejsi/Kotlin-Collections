package reusable

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

fun testContains(collection: Collection<Int>, element: Int, expected: Boolean) {
    val result = assertDoesNotThrow{ element in collection }

    assertEquals(expected, result)
}

fun testContainsAll(collection: Collection<Int>, other: Collection<Int>, expected: Boolean) {
    val result = assertDoesNotThrow{ collection.containsAll(other) }

    assertEquals(expected, result)
}

fun testEquals(collection: Collection<Int>, other: Collection<Int>, expected: Boolean) {
    val result = assertDoesNotThrow{ collection == other }

    assertEquals(expected, result)
}

fun testHashCode(collection: Collection<Int>, other: Collection<Int>) {
    val result1 = assertDoesNotThrow{ collection.hashCode() }
    val result2 = assertDoesNotThrow{ other.hashCode() }

    if (collection == other) {
        assertEquals(result1, result2)
    }
}

fun testToString(collection: Collection<Int>, expected: String) {
    val result = assertDoesNotThrow{ collection.toString() }

    assertEquals(expected, result)
}
