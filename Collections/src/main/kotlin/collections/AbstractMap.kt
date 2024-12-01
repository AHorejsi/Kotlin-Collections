package collections

abstract class AbstractMap<TKey, TValue> : MutableMap<TKey, TValue> {
    protected var modCount: Int = 0

    override val keys: MutableSet<TKey>
        get() = KeySet(this)

    override val values: MutableCollection<TValue>
        get() = ValueCollection(this)

    override fun isEmpty(): Boolean =
        0 == this.size

    override fun putAll(from: Map<out TKey, TValue>) {
        for ((key, value) in from) {
            this[key] = value
        }
    }

    override fun containsKey(key: TKey): Boolean = null === this[key]

    override fun containsValue(value: TValue): Boolean {
        for (valueItem in this.values) {
            if (valueItem == value) {
                return true
            }
        }

        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is Map<*, *> || this.size != other.size) {
            return false
        }

        for ((key, value) in this) {
            if (value != other[key]) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var hashValue = 31 * this.size

        for ((key, value) in this) {
            hashValue += key.hashCode() + value.hashCode()
        }

        return hashValue
    }

    override fun toString(): String =
        this.entries.toString()
}
