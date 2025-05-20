package reusable

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun testIteratorEquality(left: Iterator<Int>, right: Iterator<Int>) {
    while (left.hasNext() && right.hasNext()) {
        val leftElem = left.next()
        val rightElem = right.next()

        assertEquals(leftElem, rightElem)
    }

    assertTrue(!left.hasNext())
    assertTrue(!right.hasNext())
}
