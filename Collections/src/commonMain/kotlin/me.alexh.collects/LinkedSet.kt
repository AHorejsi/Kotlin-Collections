package me.alexh.collects

class LinkedSet<TElement>(
    private val underMap: MutableMap<TElement, MutableLinkedListNode<TElement>> = emptyMutableMap(),
    private val underList: MutableLinkedList<TElement> = emptyMutableLinkedList()
) : SealedMutableCollection<TElement>(), MutableSet<TElement> {
    override val size: Int
        get() = this.underMap.size

    override fun add(element: TElement): Boolean {
        if (this.contains(element)) {
            return false
        }
        else {
            this.underList.addLast(element)
            this.underMap[element] = this.underList.last!!

            return true
        }
    }

    override operator fun contains(element: TElement): Boolean = this.underMap.containsKey(element)

    override fun remove(element: TElement): Boolean {
        val node = this.underMap.remove(element)

        if (null !== node) {
            this.underList.remove(node)

            return true
        }

        return false
    }

    override fun clear() {
        this.underMap.clear()
        this.underList.clear()
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private val under: MutableIterator<TElement> = this@LinkedSet.underList.iterator()
        private var elem: TElement? = null

        override fun hasNext(): Boolean = this.under.hasNext()

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val elem = this.under.next()

            this.elem = elem

            return elem
        }

        override fun remove() {
            this.elem?.let {
                val node = this@LinkedSet.underMap.remove(it)
                this@LinkedSet.underList.remove(node!!)

                this.elem = null
            } ?: throw IllegalStateException()
        }
    }
}
