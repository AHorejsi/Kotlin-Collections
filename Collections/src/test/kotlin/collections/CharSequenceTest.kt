package collections

import asserts.assertGreater
import asserts.assertLess
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("SameParameterValue")
class CharSequenceTest {
    @Test
    fun testDuplicate() {
        val sb = StringBuilder("abc")

        testDuplicateOn(sb, 5)

        sb.reverse()

        testDuplicateOn(sb, 3)
    }

    private fun testDuplicateOn(sb: StringBuilder, amount: Int) {
        val oldStr = sb.toString()
        val oldLength = oldStr.length

        assertDoesNotThrow{ sb.duplicate(amount) }

        var index = 0

        while (index < sb.length) {
            val rangeStr = sb.substring(index, index + oldLength)

            assertEquals(oldStr, rangeStr)

            index += oldLength
        }
    }

    @Test
    fun testInterspersed() {
        val str = "aaaaa"

        testNewStringOnIntersperse(str, "")
        testNewStringOnIntersperse(str, "b")
        testNewStringOnIntersperse(str, "cd")
        testNewStringOnIntersperse(str, "efg")
    }

    private fun testNewStringOnIntersperse(str: String, separator: String) {
        val newStr = assertDoesNotThrow{ str.interspersed(separator) }

        var index = 0
        var newIndex = 0
        val newLength = newStr.length
        val separatorLength = separator.length

        while (newIndex < newLength) {
            assertEquals(str[index], newStr[newIndex])

            ++index
            ++newIndex

            if (newIndex == newLength) {
                break
            }

            for (separatorIndex in 0 until separatorLength) {
                assertEquals(newStr[newIndex], separator[separatorIndex])

                ++newIndex
            }
        }
    }

    @Test
    fun testStartsWithIgnoreCase() {
        val str = "AbCdEf"

        testStartsWithIgnoreCaseOn(str, "aBc", true)
        testStartsWithIgnoreCaseOn(str, "AbD", false)
        testStartsWithIgnoreCaseOn(str, "", true)
        testStartsWithIgnoreCaseOn(str, "AbCdEfG", false)

        for (length in str.indices) {
            val sub = str.substring(0, length)

            testStartsWithIgnoreCaseOn(str, sub, true)
        }
    }

    private fun testStartsWithIgnoreCaseOn(str: String, prefix: String, expected: Boolean) {
        val result = assertDoesNotThrow{ str.startsWithIgnoreCase(prefix) }

        assertEquals(expected, result)
    }

    @Test
    fun testEndsWithIgnoreCase() {
        val str = "AbCdEf"

        testEndsWithIgnoreCaseOn(str, "DeF", true)
        testEndsWithIgnoreCaseOn(str, "feC", false)
        testEndsWithIgnoreCaseOn(str, "", true)
        testEndsWithIgnoreCaseOn(str, "AbCdEfG", false)

        for (length in str.indices) {
            val sub = str.substring(length)

            testEndsWithIgnoreCaseOn(str, sub, true)
        }
    }

    private fun testEndsWithIgnoreCaseOn(str: String, prefix: String, expected: Boolean) {
        val result = assertDoesNotThrow{ str.endsWithIgnoreCase(prefix) }

        assertEquals(expected, result)
    }

    @Test
    fun testCompareIgnoreCase() {
        val str1 = "aaa"
        val str2 = "aab"
        val str3 = "aac"

        assertLess(str1, str2, ::compareIgnoreCase)
        assertLess(str1, str3, ::compareIgnoreCase)
        assertGreater(str1, null, ::compareIgnoreCase)

        assertGreater(str2, str1, ::compareIgnoreCase)
        assertLess(str2, str3, ::compareIgnoreCase)
        assertGreater(str2, null, ::compareIgnoreCase)

        assertGreater(str3, str1, ::compareIgnoreCase)
        assertGreater(str3, str2, ::compareIgnoreCase)
        assertGreater(str3, null, ::compareIgnoreCase)

        assertLess(null, str1, ::compareIgnoreCase)
        assertLess(null, str2, ::compareIgnoreCase)
        assertLess(null, str3, ::compareIgnoreCase)
    }
}