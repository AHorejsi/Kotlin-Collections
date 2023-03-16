package me.alexh.collects

fun <TElement> emptyMutableSet(): MutableSet<TElement> = mutableSetOf()

fun <TElement> emptySortedSet(): SortedSet<TElement> = emptyMutableSortedSet()

fun <TElement> emptyMutableSortedSet(): MutableSortedSet<TElement> = RedBlackSet()
