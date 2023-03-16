package me.alexh.collects

interface Hasher<in TElement> {
    fun equals(left: TElement, right: TElement): Boolean

    fun hashCode(item: TElement): Int
}

class DefaultHasher<in TElement> : Hasher<TElement> {
    override fun equals(left: TElement, right: TElement): Boolean = left == right

    override fun hashCode(item: TElement): Int = item.hashCode()
}

interface DoubleHasher<in TElement> {
    fun equals(left: TElement, right: TElement): Boolean

    fun hashCode1(item: TElement): Int

    fun hashCode2(item: TElement, jump: Int): Int
}

open class LinearHasher<in TElement> : DoubleHasher<TElement> {
    override fun equals(left: TElement, right: TElement): Boolean = left == right

    override fun hashCode1(item: TElement): Int = item.hashCode()

    override fun hashCode2(item: TElement, jump: Int): Int = 1
}

open class QuadraticHasher<in TElement> : DoubleHasher<TElement> {
    override fun equals(left: TElement, right: TElement): Boolean = left == right

    override fun hashCode1(item: TElement): Int = item.hashCode()

    override fun hashCode2(item: TElement, jump: Int): Int = jump
}
