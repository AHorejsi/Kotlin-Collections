package me.alexh.collects

fun <TElement> emptyLinkedList(): LinkedList<TElement> = emptyMutableLinkedList()

fun <TElement> emptyMutableLinkedList(): MutableLinkedList<TElement> = BidirectionalList()

fun <TElement> LinkedList<TElement>.first(): TElement = this.tryFirst().getOrThrow()

fun <TElement> LinkedList<TElement>.last(): TElement = this.tryLast().getOrThrow()

fun <TElement> LinkedList<TElement>.firstOrNull(): TElement? = this.tryFirst().getOrNull()

fun <TElement> LinkedList<TElement>.lastOrNull(): TElement? = this.tryLast().getOrNull()

fun <TElement> LinkedList<TElement>.tryFirst(): Result<TElement> =
    this.first?.let {
        return Result.success(it.element)
    } ?: run {
        return Result.failure(NoSuchElementException())
    }

fun <TElement> LinkedList<TElement>.tryLast(): Result<TElement> =
    this.last?.let {
        return Result.success(it.element)
    } ?: run {
        return Result.failure(NoSuchElementException())
    }
