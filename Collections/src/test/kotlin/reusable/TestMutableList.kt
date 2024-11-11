package reusable

private fun checkIfNonEmpty(list: List<*>) {
    @Suppress("ReplaceNegatedIsEmptyWithIsNotEmpty")
    if (!list.isEmpty()) {
        throw InternalError("List must be empty")
    }
}

fun testSize(list: MutableList<Int>) {
    checkIfNonEmpty(list)


}