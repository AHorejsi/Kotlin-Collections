package me.alexh.collects

open class LinkedListNode<out TElement>(
    open val element: TElement,
    open val source: LinkedList<TElement>? = null,
    open val next: LinkedListNode<TElement>? = null,
    open val prev: LinkedListNode<TElement>? = null
)

class MutableLinkedListNode<TElement>(
    override var element: TElement,
    source: MutableLinkedList<TElement>? = null,
    next: MutableLinkedListNode<TElement>? = null,
    prev: MutableLinkedListNode<TElement>? = null
) : LinkedListNode<TElement>(element, source, next, prev) {
    override var source: MutableLinkedList<TElement>? = source
        internal set
    override var next: MutableLinkedListNode<TElement>? = next
        internal set
    override var prev: MutableLinkedListNode<TElement>? = prev
        internal set

    internal fun insertBefore(node: MutableLinkedListNode<TElement>) {
        if (null !== node.source) {
            throw IllegalArgumentException()
        }

        node.next = this
        node.prev = this.prev?.prev
        this.prev = node
    }

    internal fun insertAfter(node: MutableLinkedListNode<TElement>) {
        if (null !== node.source) {
            throw IllegalArgumentException()
        }

        node.prev = this
        node.next = this.next?.next
        this.next = node
    }

    internal fun remove() {
        val prevNode = this.prev
        val nextNode = this.next

        prevNode?.next = nextNode
        nextNode?.prev = prevNode

        this.prev = null
        this.next = null
        this.source = null
    }
}

interface LinkedList<out TElement> : Collection<TElement> {
    val first: LinkedListNode<TElement>?

    val last: LinkedListNode<TElement>?

    override operator fun contains(element: @UnsafeVariance TElement): Boolean = null !== this.find(element)

    fun find(element: @UnsafeVariance TElement): LinkedListNode<TElement>? {
        var node = this.first

        while (null !== node) {
            val item = node.element

            if (item == element) {
                return node
            }

            node = node.next
        }

        return null
    }

    fun findLast(element: @UnsafeVariance TElement): LinkedListNode<TElement>? {
        var node = this.last

        while (null !== node) {
            val item = node.element

            if (item == element) {
                return node
            }

            node = node.prev
        }

        return null
    }

    override fun iterator(): Iterator<TElement> = this.front()

    fun front(): ListIterator<TElement>

    fun back(): ListIterator<TElement>
}

interface MutableLinkedList<TElement> : LinkedList<TElement>, MutableCollection<TElement> {
    override val first: MutableLinkedListNode<TElement>?

    override val last: MutableLinkedListNode<TElement>?

    override fun add(element: TElement): Boolean {
        this.addLast(element)

        return true
    }

    fun addFirst(element: TElement) {
        val newNode = MutableLinkedListNode(element)

        this.addFirst(newNode)
    }

    fun addFirst(newNode: MutableLinkedListNode<TElement>)

    fun addLast(element: TElement) {
        val newNode = MutableLinkedListNode(element)

        this.addLast(newNode)
    }

    fun addLast(newNode: MutableLinkedListNode<TElement>)

    fun addBefore(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addBefore(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = MutableLinkedListNode(element)

        this.addBefore(node, newNode)
    }

    fun addAfter(node: MutableLinkedListNode<TElement>, newNode: MutableLinkedListNode<TElement>)

    fun addAfter(node: MutableLinkedListNode<TElement>, element: TElement) {
        val newNode = MutableLinkedListNode(element)

        this.addAfter(node, newNode)
    }

    override fun find(element: TElement): MutableLinkedListNode<TElement>? = super.find(element) as MutableLinkedListNode?

    override fun findLast(element: TElement): MutableLinkedListNode<TElement>? = super.find(element) as MutableLinkedListNode?

    override fun remove(element: TElement): Boolean =
        this.find(element)?.let {
            this.remove(it)

            return true
        } ?: false

    fun remove(node: MutableLinkedListNode<TElement>)

    fun removeFirst(): TElement = this.tryRemoveFirst().getOrThrow()

    fun removeFirstOrNull(): TElement? = this.tryRemoveFirst().getOrNull()

    fun tryRemoveFirst(): Result<TElement> =
        this.first?.let {
            val old = it.element

            this.remove(it)

            return Result.success(old)
        } ?: Result.failure(NoSuchElementException())

    fun removeLast(): TElement = this.tryRemoveLast().getOrThrow()

    fun removeLastOrNull(): TElement? = this.tryRemoveLast().getOrNull()

    fun tryRemoveLast(): Result<TElement> =
        this.last?.let {
            val old = it.element

            this.remove(it)

            return Result.success(old)
        } ?: Result.failure(NoSuchElementException())

    override fun iterator(): MutableIterator<TElement> = this.front()

    override fun front(): MutableListIterator<TElement>

    override fun back(): MutableListIterator<TElement>
}
