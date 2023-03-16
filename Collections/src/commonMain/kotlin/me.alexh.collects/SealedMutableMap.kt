package me.alexh.collects

sealed class SealedMutableMap<TKey, TValue> : MutableMap<TKey, TValue> {
    override fun isEmpty(): Boolean = 0 == this.size

    override fun putAll(from: Map<out TKey, TValue>) {
        for (entry in from) {
            this[entry.key] = entry.value
        }
    }

    override fun containsValue(value: TValue): Boolean {
        for (current in this.values) {
            if (current == value) {
                return true
            }
        }

        return false
    }
}


