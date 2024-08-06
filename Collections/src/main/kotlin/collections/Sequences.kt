package collections

fun <TElement> Sequence<TElement>.prepend(item: TElement): Sequence<TElement> = sequence {
    yield(item)

    for (elem in this@prepend) {
        yield(elem)
    }
}

fun <TElement> Sequence<TElement>.append(item: TElement): Sequence<TElement> = sequence {
    for (elem in this@append) {
        yield(elem)
    }

    yield(item)
}

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

fun <TElement> Sequence<TElement>.cycle(amount: Int): Sequence<TElement> = sequence {
    repeat(amount) {
        for (item in this@cycle) {
            yield(item)
        }
    }
}
