package collections

fun <TElement> ListIterator<TElement>.tryPrevious(): Result<TElement> =
    runCatching{ this.previous() }

fun <TElement> ListIterator<TElement>.previousOrNull(): TElement? =
    this.tryPrevious().getOrNull()

fun <TElement> Iterator<TElement>.tryNext(): Result<TElement> =
    runCatching{ this.next() }

fun <TElement> Iterator<TElement>.nextOrNull(): TElement? =
    this.tryNext().getOrNull()

fun <TElement> MutableListIterator<TElement>.trySet(element: TElement): Result<Unit> =
    runCatching{ this.set(element) }

fun MutableIterator<*>.tryRemove(): Result<Unit> =
    runCatching{ this.remove() }

fun <TElement> MutableListIterator<TElement>.tryAdd(element: TElement): Result<Unit> =
    runCatching{ this.add(element) }
