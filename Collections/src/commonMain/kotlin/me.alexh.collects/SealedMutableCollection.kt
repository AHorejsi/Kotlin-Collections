package me.alexh.collects

sealed class SealedMutableCollection<TElement> : MutableCollection<TElement> {
    override fun isEmpty(): Boolean = 0 == this.size

    override fun addAll(elements: Collection<TElement>): Boolean {
        var change = false

        for (item in elements) {
            if (this.add(item)) {
                change = true
            }
        }

        return change
    }

    override fun containsAll(elements: Collection<@UnsafeVariance TElement>): Boolean {
        for (item in elements) {
            if (item !in this) {
                return false
            }
        }

        return true
    }

    override fun removeAll(elements: Collection<@UnsafeVariance TElement>): Boolean {
        var change = false

        for (item in elements) {
            if (this.remove(item)) {
                change = true

                if (this.isEmpty()) {
                    break
                }
            }
        }

        return change
    }

    override fun retainAll(elements: Collection<TElement>): Boolean = this.removeIf{ !elements.contains(it) }
}
