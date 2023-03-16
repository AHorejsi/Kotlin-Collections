package me.alexh.collects

class M2FList<TElement> : SealedMutableCollection<TElement>(), SelfOrgList<TElement> {
    private val under: JumpList<TElement> = JumpList{ _, _ -> false }

    override val size: Int
        get() = this.under.size

    override operator fun get(index: Int): TElement = this.under[index]

    override fun add(element: TElement): Boolean = this.under.add(element)

    override fun find(predicate: Predicate<in TElement>): ListIterator<TElement> = this.under.find(predicate)

    override fun count(predicate: Predicate<in TElement>): Int = this.under.count(predicate)

    override fun remove(element: TElement): Boolean = this.under.remove(element)

    override fun clear() = this.under.clear()

    override fun iterator(): MutableIterator<TElement> = this.under.iterator()

    override fun front(): ListIterator<TElement> = this.under.front()

    override fun back(): ListIterator<TElement> = this.under.back()
}
