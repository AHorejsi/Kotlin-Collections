package reusable

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

fun testEquals(collection: Collection<Int>, other: Collection<Int>, expectedResult: Boolean) {
    val result = assertDoesNotThrow{ collection == other }

    assertEquals(expectedResult, result)
}

fun testHashCode(collection: Collection<Int>, other: Collection<Int>) {
    val result1 = assertDoesNotThrow{ collection.hashCode() }
    val result2 = assertDoesNotThrow{ other.hashCode() }

    if (collection == other) {
        assertEquals(result1, result2)
    }
}

fun testToStringOnEmpty(collection: Collection<Int>) {
    val result = assertDoesNotThrow{ collection.toString() }

    assertEquals("[]", result)
}

fun testToString(collection: Collection<Int>, expected: String) {
    val result = assertDoesNotThrow{ collection.toString() }

    assertEquals(expected, result)
}
