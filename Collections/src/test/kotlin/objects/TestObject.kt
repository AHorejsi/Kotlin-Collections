package objects

class TestObject(var value: Int) {
    override fun equals(other: Any?): Boolean {
        if (other !is TestObject) {
            return false
        }

        return other.value == this.value
    }

    override fun hashCode(): Int =
        this.value.hashCode()

    override fun toString(): String =
        this.value.toString()
}
