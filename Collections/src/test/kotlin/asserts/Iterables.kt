package asserts

import kotlin.test.asserter

fun <TType> assertNotContains(iterable: Iterable<TType>, element: TType) {
    if (element in iterable) {
        asserter.fail("Expected the collection to NOT contain the element.\nCollection <$iterable>, element <$element>")
    }
}

fun <TType> assertContainsAll(iterable: Iterable<TType>, other: Iterable<TType>) {
    for (element in other) {
        if (element !in iterable) {
            asserter.fail("$other is NOT a subset of $iterable. Element <$element>")
        }
    }
}

fun <TType> assertNotContainsAll(iterable: Iterable<TType>, other: Iterable<TType>) {
    for (element in other) {
        if (element in iterable) {
            asserter.fail("$other shares an element with $iterable. Element <$element>")
        }
    }
}
