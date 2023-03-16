package me.alexh.collects

internal class SimpleEntry<TKey, TValue>(
    override val key: TKey,
    override var value: TValue
) : MutableMap.MutableEntry<TKey, TValue> {
    override fun setValue(newValue: TValue): TValue {
        val old = this.value
        this.value = newValue

        return old
    }
}
