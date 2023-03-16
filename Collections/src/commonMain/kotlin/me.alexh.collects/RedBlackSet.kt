package me.alexh.collects

import kotlin.jvm.Transient
import kotlin.math.log2

class RedBlackSet<TElement>(
    override val comparator: Comparator<in TElement>? = null
) : SealedMutableCollection<TElement>(), MutableSortedSet<TElement> {
    @Transient
    private var modCount: Int = 0
    private var root: RbNode<TElement>? = null
    @Suppress("UNCHECKED_CAST")
    private val actualComparator: Comparator<in TElement> = this.comparator ?: Comparator{ left, right -> (left as Comparable<TElement>).compareTo(right) }
    override var size: Int = 0
        private set

    override fun tryMin(): Result<TElement> =
        this.minNode()?.let {
            return Result.success(it.element)
        } ?: run {
            return Result.failure(NoSuchElementException())
        }

    private fun minNode(): RbNode<TElement>? {
        var node = this.root
        var parent: RbNode<TElement>? = null

        while (null !== node) {
            parent = node
            node = node.left
        }

        return parent
    }

    override fun tryMax(): Result<TElement> =
        this.maxNode()?.let {
            return Result.success(it.element)
        } ?: run {
            return Result.failure(NoSuchElementException())
        }

    private fun maxNode(): RbNode<TElement>? {
        var node = this.root
        var parent: RbNode<TElement>? = null

        while (null !== node) {
            parent = node
            node = node.right
        }

        return parent
    }

    override fun add(element: TElement): Boolean {
        val newNode = this.doInsert(element)

        if (null === newNode) {
            return false
        }
        else {
            ++(this.size)
            ++(this.modCount)

            this.rebalance(newNode)

            return true
        }
    }

    private fun doInsert(element: TElement): RbNode<TElement>? {
        if (null === this.root) {
            this.root = RbNode(element, RbColor.BLACK)

            return this.root
        }
        else {
            val (parent, node) = this.search(element)

            if (null !== node) {
                return null
            }
            else {
                val comparison = this.actualComparator.compare(element, parent!!.element)
                val newNode = RbNode(element)

                if (comparison < 0) {
                    parent.left = newNode
                }
                else {
                    parent.right = newNode
                }

                newNode.parent = parent

                return newNode
            }
        }
    }

    private fun rebalance(node: RbNode<TElement>) {
        var currentNode = node
        var parent = currentNode.parent

        while (parent.color == RbColor.RED) {
            val uncle = parent.sibling()
            val grandparent = parent.parent()

            if (uncle.color == RbColor.RED) {
                parent.color = RbColor.BLACK
                uncle.color = RbColor.BLACK

                if (grandparent !== this.root) {
                    grandparent.color = RbColor.RED

                    currentNode = grandparent!!
                    parent = grandparent.parent
                }
            }
            else {
                if (parent!!.isLeftChild()) {
                    if (currentNode.isRightChild()) {
                        this.leftRotate(parent)

                        parent = currentNode
                    }

                    this.rightRotate(grandparent!!)
                }
                else {
                    if (currentNode.isLeftChild()) {
                        this.rightRotate(parent)

                        parent = node
                    }

                    this.leftRotate(grandparent!!)
                }

                parent.color = RbColor.BLACK
                grandparent.color = RbColor.RED

                break
            }
        }
    }

    private fun leftRotate(node: RbNode<TElement>) {
        val parent = node.parent
        val rightChild = node.right!!

        node.right = rightChild.left
        rightChild.left?.parent = node
        rightChild.left = node
        node.parent = rightChild

        this.replaceChildOfParent(parent, node, rightChild)
    }

    private fun rightRotate(node: RbNode<TElement>) {
        val parent = node.parent
        val leftChild = node.left!!

        node.left = leftChild.right
        leftChild.right?.parent = node
        leftChild.right = node
        node.parent = leftChild

        this.replaceChildOfParent(parent, node, leftChild)
    }

    private fun replaceChildOfParent(parent: RbNode<TElement>?, oldChild: RbNode<TElement>, newChild: RbNode<TElement>) {
        if (null === parent) {
            this.root = newChild
        }
        else if (parent.left === oldChild) {
            parent.left = newChild
        }
        else {
            parent.right = newChild
        }

        newChild.parent = parent
    }

    override operator fun contains(element: TElement): Boolean {
        val (_, node) = this.search(element)

        return null !== node && node.element == element
    }

    override fun remove(element: TElement): Boolean {
        val (_, node) = this.search(element)

        if (null === node) {
            return false
        }
        else {
            this.remove(node)

            --(this.size)
            ++(this.modCount)

            return true
        }
    }

    private fun remove(node: RbNode<TElement>) {
        val replacement = this.replacementNode(node)

        if (null === replacement) {
            this.handleNoChild(node)
        }
        else if ((null === node.left) xor (null === node.right)) {
            this.handleOneChild(node, replacement)
        }
        else {
            this.handleTwoChildren(node, replacement)
        }
    }

    private fun replacementNode(node: RbNode<TElement>): RbNode<TElement>? =
        when ((null === node.left) to (null === node.right)) {
            true to true -> null
            true to false -> node.right
            false to true -> node.left
            else -> this.successor(node)
        }

    private fun successor(node: RbNode<TElement>): RbNode<TElement> {
        var temp = node

        while (null !== temp.left) {
            temp = temp.left!!
        }

        return temp
    }

    private fun handleNoChild(node: RbNode<TElement>) {
        if (node === this.root) {
            this.root = null
        }
        else {
            if (RbColor.BLACK == node.color) {
                this.fixDoubleBlack(node)
            }
            else {
                node.sibling().color = RbColor.RED
            }
        }

        if (node.isLeftChild()) {
            node.parent!!.left = null
        }
        else {
            node.parent!!.right = null
        }
    }

    private fun handleOneChild(node: RbNode<TElement>, replacement: RbNode<TElement>) {
        if (node === this.root) {
            node.element = replacement.element

            node.left = null
            node.right = null
        }
        else {
            if (node.isLeftChild()) {
                node.parent!!.left = null
            }
            else {
                node.parent!!.right = null
            }

            replacement.parent = node.parent

            if (RbColor.BLACK == node.color && RbColor.BLACK == replacement.color) {
                this.fixDoubleBlack(node)
            }
            else {
                replacement.color = RbColor.BLACK
            }
        }
    }

    private fun handleTwoChildren(node: RbNode<TElement>, replacement: RbNode<TElement>) {
        val temp = replacement.element
        replacement.element = node.element
        node.element = temp

        this.remove(replacement)
    }

    private fun fixDoubleBlack(node: RbNode<TElement>) {
        if (node !== this.root) {
            val sibling = node.sibling()
            val parent = node.parent!!

            if (null === sibling) {
                this.fixDoubleBlack(parent)
            }
            else {
                if (RbColor.RED == sibling.color) {
                    this.handleRedSibling(sibling, parent)
                    this.fixDoubleBlack(node)
                }
                else {
                    if (RbColor.RED == sibling.left.color) {
                        this.handleRedLeftNephew(sibling, parent)
                    }
                    else if (RbColor.RED == sibling.right.color) {
                        this.handleRedRightNephew(sibling, parent)
                    }
                    else {
                        this.handleBlackNephews(sibling, parent)
                    }
                }
            }
        }
    }

    private fun handleRedSibling(sibling: RbNode<TElement>, parent: RbNode<TElement>) {
        parent.color = RbColor.RED
        sibling.color = RbColor.BLACK

        if (sibling.isLeftChild()) {
            this.rightRotate(parent)
        }
        else {
            this.leftRotate(parent)
        }
    }

    private fun handleRedLeftNephew(sibling: RbNode<TElement>, parent: RbNode<TElement>) {
        if (sibling.isLeftChild()) {
            sibling.left.color = sibling.color
            sibling.color = parent.color

            this.rightRotate(parent)
        }
        else {
            sibling.left.color = parent.color

            this.rightRotate(sibling)
            this.leftRotate(parent)
        }
    }

    private fun handleRedRightNephew(sibling: RbNode<TElement>, parent: RbNode<TElement>) {
        if (sibling.isLeftChild()) {
            sibling.right.color = sibling.color

            this.leftRotate(sibling)
            this.rightRotate(parent)
        }
        else {
            sibling.right.color = sibling.color
            sibling.color = parent.color

            this.leftRotate(parent)
        }
    }

    private fun handleBlackNephews(sibling: RbNode<TElement>, parent: RbNode<TElement>) {
        sibling.color = RbColor.RED

        if (RbColor.BLACK == parent.color) {
            this.fixDoubleBlack(parent)
        }
        else {
            parent.color = RbColor.BLACK
        }
    }

    private fun search(element: TElement): Pair<RbNode<TElement>?, RbNode<TElement>?> {
        var node = this.root
        var parent = this.root

        while (null !== node) {
            val comparison = this.actualComparator.compare(element, node.element)

            parent = node
            node =
                if (comparison < 0)
                    node.left
                else if (comparison > 0)
                    node.right
                else
                    break
        }

        return parent to node
    }

    override fun clear() {
        this.root = null
        this.size = 0
        ++(this.modCount)
    }

    override fun tryLesser(max: TElement, inclusive: Boolean): Result<TElement> {
        var node = this.root

        while (null !== node) {
            val current = node.element
            val comparison = this.actualComparator.compare(max, current)

            if (comparison < 0) {
                node = node.right
            }
            else if (comparison > 0) {
                return Result.success(current)
            }
            else {
                return Result.success(
                    if (inclusive)
                        current
                    else
                        this.inorderPredecessor(node, max)
                )
            }
        }

        return Result.failure(NoSuchElementException())
    }

    private fun inorderPredecessor(node: RbNode<TElement>, max: TElement): TElement =
        node.left?.let {
            return it.element
        } ?: run {
            var current = node

            while (this.actualComparator.compare(max, current.element) > 0) {
                current = current.parent!!
            }

            return current.element
        }

    override fun tryGreater(min: TElement, inclusive: Boolean): Result<TElement> {
        var node = this.root

        while (null !== node) {
            val current = node.element
            val comparison = this.actualComparator.compare(min, current)

            if (comparison > 0) {
                node = node.left
            }
            else if (comparison < 0) {
                return Result.success(current)
            }
            else {
                return Result.success(if (inclusive) (current) else (this.inorderSuccessor(node, min)))
            }
        }

        return Result.failure(NoSuchElementException())
    }

    private fun inorderSuccessor(node: RbNode<TElement>, element: TElement): TElement =
        node.right?.let {
            return it.element
        } ?: run {
            var current = node

            while (this.actualComparator.compare(element, current.element) < 0) {
                current = current.parent!!
            }

            return current.element
        }

    override fun subSet(
        min: TElement,
        minInclusive: Boolean,
        max: TElement,
        maxInclusive: Boolean
    ): MutableSortedSet<TElement> {
        if (this.actualComparator.compare(min, max) > 0) {
            throw IllegalArgumentException()
        }

        var actualMin = this.tryGreater(min, minInclusive)
        var actualMax = this.tryLesser(max, maxInclusive)

        if (this.actualComparator.compare(actualMin.getOrThrow(), actualMax.getOrThrow()) > 0) {
            actualMin = Result.failure(NoSuchElementException())
            actualMax = Result.failure(NoSuchElementException())
        }

        return SortedSubset(this, actualMin, actualMax)
    }

    override fun reverseSet(): MutableSortedSet<TElement> = ReverseSortedSet(this)

    override fun iterator(): MutableIterator<TElement> = object : MutableIterator<TElement> {
        private var modCount: Int = this@RedBlackSet.modCount
        private var nodeForRemoval: RbNode<TElement>? = null
        private var stack: Stack<RbNode<TElement>>

        init {
            val capacity = (2 * log2(this@RedBlackSet.size.toFloat())).toInt()
            this.stack = Stack(capacity)

            this.insertLeftmostBranch(this@RedBlackSet.root)
        }

        override fun hasNext(): Boolean {
            this.checkForConcurrentModification()

            return !this.stack.isEmpty()
        }

        override fun next(): TElement {
            if (!this.hasNext()) {
                throw NoSuchElementException()
            }

            val node = this.stack.pop()
            this.insertLeftmostBranch(node.right)

            this.nodeForRemoval = node

            return node.element
        }

        private fun insertLeftmostBranch(node: RbNode<TElement>?) {
            var followNode = node

            while (null !== followNode) {
                stack.push(followNode)

                followNode = followNode.left
            }
        }

        override fun remove() {
            this.checkForConcurrentModification()

            val nodeToRemove = this.nodeForRemoval

            if (null === nodeToRemove) {
                throw IllegalStateException()
            }
            else {
                this@RedBlackSet.remove(nodeToRemove)
                this.nodeForRemoval = null
            }

            ++(this.modCount)
        }

        private fun checkForConcurrentModification() {
            if (this.modCount != this@RedBlackSet.modCount) {
                throw ConcurrentModificationException()
            }
        }
    }
}