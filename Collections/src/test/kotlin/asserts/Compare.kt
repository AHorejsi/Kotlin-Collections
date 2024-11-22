package asserts

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.asserter

fun <TType : Comparable<TType>> assertLess(left: TType, right: TType) {
    assertLess(left, right) { first, second -> first.compareTo(second) }
}

fun <TType> assertLess(left: TType, right: TType, comp: Comparator<TType>) {
    val comparison = assertDoesNotThrow{ comp.compare(left, right) }

    if (comparison >= 0) {
        asserter.fail("<$left> must be less than <$right>")
    }
}

fun <TType : Comparable<TType>> assertLessEqual(left: TType, right: TType) {
    assertLessEqual(left, right) { first, second -> first.compareTo(second) }
}

fun <TType> assertLessEqual(left: TType, right: TType, comp: Comparator<TType>) {
    val comparison = assertDoesNotThrow{ comp.compare(left, right) }

    if (comparison > 0) {
        asserter.fail("<$left> must be less than or equal to <$right>")
    }
}

fun <TType : Comparable<TType>> assertGreater(left: TType, right: TType) {
    assertGreater(left, right) { first, second -> first.compareTo(second) }
}

fun <TType> assertGreater(left: TType, right: TType, comp: Comparator<TType>) {
    val comparison = assertDoesNotThrow{ comp.compare(left, right) }

    if (comparison <= 0) {
        asserter.fail("<$left> must be greater than <$right>")
    }
}

fun <TType : Comparable<TType>> assertGreaterEqual(left: TType, right: TType) {
    assertGreaterEqual(left, right) { first, second -> first.compareTo(second) }
}

fun <TType> assertGreaterEqual(left: TType, right: TType, comp: Comparator<TType>) {
    val comparison = assertDoesNotThrow{ comp.compare(left, right) }

    if (comparison < 0) {
        asserter.fail("<$left> must be greater than or equal to <$right>")
    }
}