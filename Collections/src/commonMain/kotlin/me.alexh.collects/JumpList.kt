package me.alexh.collects

import kotlin.jvm.Transient

private class OrgNode<TElement>(
    val element: TElement,
    var next: OrgNode<TElement>? = null,
    var prev: OrgNode<TElement>? = null
)

class JumpInfo<TElement>(
    index: Int,
    element: TElement
) {
    var index: Int = index
        internal set
    var element: TElement = element
        internal set
}

typealias Jumper<TElement> = (JumpInfo<TElement>, JumpInfo<TElement>) -> Boolean

class JumpList<TElement>(
    var jumper: Jumper<TElement>
) : SealedMutableCollection<TElement>(), SelfOrgList<TElement> {
    @Transient
    private var modCount: Int = 0
    private var head: OrgNode<TElement>? = null
    private var tail: OrgNode<TElement>? = null
    override var size: Int = 0
        private set

    override operator fun get(index: Int): TElement = this.getNode(index).element

    private fun getNode(targetIndex: Int): OrgNode<TElement> {
        if (targetIndex < 0 || targetIndex >= this.size) {
            throw IndexOutOfBoundsException()
        }

        var node = this.head
        var currentIndex = 0

        while (currentIndex != targetIndex) {
            ++currentIndex
            node = node!!.next
        }

        return node!!
    }

    override fun add(element: TElement): Boolean {
        val newNode = OrgNode(element)

        newNode.prev = this.tail
        this.tail?.next = newNode

        this.tail = newNode

        if (null === this.head) {
            this.head = newNode
        }

        ++(this.size)
        ++(this.modCount)

        return true
    }

    override fun find(predicate: Predicate<in TElement>): ListIterator<TElement> {
        if (super.isEmpty()) {
            return this.front()
        }

        var currentNode = this.head
        var backNode = this.head

        val current = JumpInfo(0, this.first())
        val back = JumpInfo(0, this.first())

        while (true) {
            val elem = currentNode!!.element

            if (predicate(elem)) {
                jumpNode(currentNode, backNode!!)

                return this.listIterator(backNode, back.index)
            }

            if (this.jumper(back, current)) {
                back.index = current.index
                back.element = current.element
                backNode = currentNode
            }

            currentNode.next?.let {
                currentNode = it
                current.element = it.element
                ++(current.index)
            } ?: break
        }

        return this.back()
    }

    override fun count(predicate: Predicate<in TElement>): Int {
        if (super.isEmpty()) {
            return 0
        }

        var amount = 0

        var currentNode = this.head
        var backNode = this.head

        val current = JumpInfo(0, this.first())
        val back = JumpInfo(0, this.first())

        while (true) {
            val elem = currentNode!!.element

            if (predicate(elem)) {
                this.jumpNode(currentNode, backNode!!)

                ++amount
            }

            if (this.jumper(back, current)) {
                back.index = current.index
                back.element = current.element
                backNode = currentNode
            }

            currentNode.next?.let {
                currentNode = it
                current.element = it.element
                ++(current.index)
            } ?: break
        }

        return amount
    }

    private fun jumpNode(currentNode: OrgNode<TElement>, backNode: OrgNode<TElement>) {
        TODO("Not yet implemented")
    }

    override fun remove(element: TElement): Boolean {
        val iter = this.iterator()

        while (iter.hasNext()) {
            val item = iter.next()

            if (item == element) {
                iter.remove()

                return true
            }
        }

        return false
    }

    private fun remove(node: OrgNode<TElement>) {
        val prevNode = node.prev
        val nextNode = node.next

        prevNode?.next = nextNode
        nextNode?.prev = prevNode

        --(this.size)
        ++(this.modCount)
    }

    override fun clear() {
        this.head = null
        this.tail = null
        this.size = 0
        ++(this.modCount)
    }

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private var modCount: Int = this@JumpList.modCount
        private var node: OrgNode<TElement>? = this@JumpList.head

        override fun hasNext(): Boolean {
            this.checkForConcurrentModification()

            return null !== this.node
        }

        override fun next(): TElement =
            this.node?.let {
                val elem = it.element

                this.node = it.next

                return elem
            } ?: throw NoSuchElementException()

        override fun remove() {
            this.checkForConcurrentModification()

            this@JumpList.remove(this.prevNode())

            ++(this.modCount)
        }

        private fun prevNode(): OrgNode<TElement> =
            this.node?.let {
                return it.prev!!
            } ?: run {
                return this@JumpList.tail!!
            }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@JumpList.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }

    override fun front(): ListIterator<TElement> = this.listIterator(this.head, 0)

    override fun back(): ListIterator<TElement> = this.listIterator(this.tail, this.size)

    private fun listIterator(node: OrgNode<TElement>?, index: Int): ListIterator<TElement> = object : ListIterator<TElement> {
        private var modCount: Int = this@JumpList.modCount
        private var node: OrgNode<TElement>? = node
        private var index: Int = index

        override fun nextIndex(): Int {
            this.checkForConcurrentModification()

            return this.index
        }

        override fun previousIndex(): Int {
            this.checkForConcurrentModification()

            return this.index - 1
        }

        override fun hasNext(): Boolean = this@JumpList.size != this.nextIndex()

        override fun hasPrevious(): Boolean = -1 != this.previousIndex()

        override fun next(): TElement =
            this.node?.let {
                val elem = it.element

                this.node = it.next
                ++(this.index)

                return elem
            } ?: throw NoSuchElementException()

        override fun previous(): TElement =
            this.previousNode()?.let {
                val elem = it.element

                this.node = it.prev
                --(this.index)

                return elem
            } ?: throw NoSuchElementException()

        private fun previousNode(): OrgNode<TElement>? = this.node?.prev ?: this@JumpList.tail

        private fun checkForConcurrentModification() {
            if (this.modCount != this@JumpList.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}
