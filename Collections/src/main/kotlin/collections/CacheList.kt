package collections

import java.io.Serializable

class CacheList<TElement>(
    private val cacheSize: Int
) : AbstractLinkedList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    init {
        this.fillCache()
    }

    constructor(cacheSize: Int, elements: Sequence<TElement>) : this(cacheSize, elements.asIterable())

    constructor(cacheSize: Int, elements: Collection<TElement>) : this(cacheSize) {
        this.addToBack(elements)
    }

    constructor(cacheSize: Int, elements: Iterable<TElement>) : this(cacheSize) {
        this.addAll(elements)
    }

    constructor(cacheSize: Int, vararg elements: TElement) : this(cacheSize) {
        this.addAll(elements)
    }

    private val nodeCache: MutableList<MutableLinkedListNode<TElement?>> = ArrayList(this.cacheSize)

    private fun getNewNode(element: TElement): MutableLinkedListNode<TElement> {
        if (this.nodeCache.isEmpty()) {
            this.fillCache()
        }

        val newNode = this.nodeCache.removeLast()
        newNode.item = element

        @Suppress("UNCHECKED_CAST")
        return newNode as MutableLinkedListNode<TElement>
    }

    private fun fillCache() {
        while (this.nodeCache.size < this.cacheSize) {
            val newNode = MutableLinkedListNode<TElement?>(null)

            this.nodeCache.add(newNode)
        }
    }

    override fun addFirst(element: TElement) {
        val newNode = this.getNewNode(element)

        super.addFirst(newNode)
    }

    override fun addLast(element: TElement) {
        val newNode = this.getNewNode(element)

        super.addLast(newNode)
    }

    override fun addBefore(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = this.getNewNode(element)

        super.addBefore(node, newNode)
    }

    override fun addAfter(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = this.getNewNode(element)

        super.addAfter(node, newNode)
    }

    override fun remove(node: MutableLinkedListNode<TElement>) {
        if (this !== node.list) {
            throw IllegalArgumentException()
        }

        super.remove(node)

        @Suppress("UNCHECKED_CAST")
        this.nodeCache.add(node as MutableLinkedListNode<TElement?>)
    }

    override fun clear() {
        var node = super.head

        while (null !== node) {
            val nextNode = node.next

            @Suppress("UNCHECKED_CAST")
            this.nodeCache.add(node as MutableLinkedListNode<TElement?>)

            node = nextNode
        }

        super.clear()
    }
}

fun <TElement> Iterable<TElement>.toCacheList(cacheSize: Int): CacheList<TElement> = CacheList(cacheSize, this)

fun <TElement> Collection<TElement>.toCacheList(cacheSize: Int): CacheList<TElement> = CacheList(cacheSize, this)

fun <TElement> Sequence<TElement>.toCacheList(cacheSize: Int): CacheList<TElement> = CacheList(cacheSize, this)

fun <TElement> Array<out TElement>.toCacheList(cacheSize: Int) = CacheList(cacheSize, *this)
