package collections

fun <TElement> TElement.infinitely(): Sequence<TElement> = sequence {
    while (true) {
        yield(this@infinitely)
    }
}

fun <TElement> TElement.replicate(amount: Int): Sequence<TElement> = sequence {
    repeat(amount) {
        yield(this@replicate)
    }
}

fun <TElement> Sequence<TElement>.cycle(): Sequence<TElement> = sequence {
    while (true) {
        for (item in this@cycle) {
            yield(item)
        }
    }
}
