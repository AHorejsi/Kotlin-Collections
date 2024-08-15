package collections

internal class AsList<TElement>(private val base: Array<TElement>) : MutableList<TElement>, RandomAccess {
    override val size: Int
        get() = this.base.size

    override fun isEmpty(): Boolean =
        this.base.isEmpty()

    override operator fun get(index: Int): TElement =
        this.base[index]

    override operator fun set(index: Int, element: TElement): TElement {
        val old = this.base[index]

        this.base[index] = element

        return old
    }

    override fun add(element: TElement): Boolean =
        this.add(this.size, element)


    override fun add(index: Int, element: TElement) =
        unsupported(this.javaClass.enclosingMethod.name, this.javaClass.name)

    override fun addAll(elements: Collection<TElement>): Boolean =
        this.addAll(this.size, elements)

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun removeAt(index: Int): TElement =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun remove(element: TElement): Boolean =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun removeAll(elements: Collection<TElement>): Boolean =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun retainAll(elements: Collection<TElement>): Boolean =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun clear(): Unit =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override operator fun contains(element: TElement): Boolean =
        this.base.contains(element)

    override fun containsAll(elements: Collection<TElement>): Boolean = elements.all(this.base::contains)

    override fun indexOf(element: TElement): Int =
        this.base.indexOf(element)

    override fun lastIndexOf(element: TElement): Int =
        this.base.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<TElement> =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun listIterator(): MutableListIterator<TElement> =
        this.listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<TElement> =
        unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val iter: Iterator<TElement> = this@AsList.base.iterator()

        override fun hasNext(): Boolean = this.iter.hasNext()

        override fun next(): TElement = this.iter.next()

        override fun remove(): Nothing =
            unsupported(this.javaClass.name, this.javaClass.enclosingMethod.name)
    }
}