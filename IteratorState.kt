package com.alexh.collects

internal enum class IteratorState {
    INITIALIZED,
    CALLED_NEXT,
    CALLED_REMOVE
}

internal enum class ListIteratorState {
    INITIALIZED,
    CALLED_NEXT,
    CALLED_PREVIOUS,
    CALLED_MUTATION
}
