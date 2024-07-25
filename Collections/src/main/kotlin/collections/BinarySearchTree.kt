package collections

import java.io.Serializable

open class BstNode<TKey, TValue>(var entry: MutableEntry<TKey, TValue>) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    var parent: BstNode<TKey, TValue>? = null
    var left: BstNode<TKey, TValue>? = null
    var right: BstNode<TKey, TValue>? = null

    val key: TKey
        get() = this.entry.key
    val value: TValue
        get() = this.entry.value

    val isLeftChild: Boolean
        get() = this === this.parent?.left
    val isRightChild: Boolean
        get() = this === this.parent?.right
}

val BstNode<*, *>?.isLeftChild: Boolean
    get() = this?.isLeftChild ?: false

val BstNode<*, *>?.isRightChild: Boolean
    get() = this?.isRightChild ?: false

class BinarySearchTree<TKey, TValue>(val comp: (TKey, TKey) -> Int) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    var root: BstNode<TKey, TValue>? = null
        private set

    fun firstNode(): BstNode<TKey, TValue>? {
        var parent: BstNode<TKey, TValue>? = null
        var node = this.root

        while (null !== node) {
            parent = node
            node = node.left
        }

        return parent
    }

    fun lastNode(): BstNode<TKey, TValue>? {
        var parent: BstNode<TKey, TValue>? = null
        var node = this.root

        while (null !== node) {
            parent = node
            node = node.right
        }

        return parent
    }

    fun addLeaf(parent: BstNode<TKey, TValue>?, newNode: BstNode<TKey, TValue>) {
        parent?.let {
            if (this.comp(it.key, newNode.key) < 0) {
                it.left = newNode
            }
            else {
                it.right = newNode
            }

            newNode.parent = parent
        } ?: run {
            this.root = newNode
        }
    }

    fun removeNodeWithOneChildOrLess(node: BstNode<TKey, TValue>) {
        val child = node.left ?: node.right

        child?.parent = node.parent

        if (node.isLeftChild) {
            node.parent?.left = child
        }
        else {
            node.parent?.right = child
        }
    }

    fun deleteTree() {
        this.root = null
    }

    fun containsKey(key: TKey): Boolean {
        val (_, node) = this.findNode(key)

        return null !== node
    }

    fun lesserNode(key: TKey, inclusive: Boolean): BstNode<TKey, TValue>? {
        val (parent, node) = this.findNode(key)

        return node?.let {
            if (inclusive && 0 == this.comp(key, it.key))
                it
            else
                this.predecessor(it, it.key)
        } ?: run {
            if (null !== parent && this.comp(key, parent.key) < 0)
                this.predecessor(parent, key)
            else
                parent
        }
    }

    private fun predecessor(node: BstNode<TKey, TValue>, key: TKey): BstNode<TKey, TValue>? =
        if (null === node.left)
            this.predecessorGoDown(node)
        else
            this.predecessorGoUp(node, key)


    private fun predecessorGoDown(node: BstNode<TKey, TValue>): BstNode<TKey, TValue>? {
        var parent = node
        var current = node.left

        return current?.let {
            while (null !== current) {
                parent = it
                current = it.right
            }

            return parent
        }
    }

    private fun predecessorGoUp(node: BstNode<TKey, TValue>, key: TKey): BstNode<TKey, TValue> {
        var current = node.parent

        while (null !== current && this.comp(key, current.key) > 0) {
            current = current.parent
        }

        return current ?: throw NoSuchElementException()
    }

    fun greaterNode(key: TKey, inclusive: Boolean): BstNode<TKey, TValue>? {
        val (parent, node) = this.findNode(key)

        return node?.let {
            if (inclusive && 0 == this.comp(key, it.key))
                it
            else
                this.successor(it, it.key)
        } ?: run {
            if (null !== parent && this.comp(key, parent.key) < 0)
                this.successor(parent, key)
            else
                parent
        }
    }

    fun successor(node: BstNode<TKey, TValue>, key: TKey): BstNode<TKey, TValue>? =
        if (null === node.right)
            this.successorGoDown(node)
        else
            this.successorGoUp(node, key)

    private fun successorGoDown(node: BstNode<TKey, TValue>): BstNode<TKey, TValue>? {
        var parent = node
        var current = node.right

        return current?.let {
            while (null !== current) {
                parent = it
                current = it.left
            }

            return parent
        }
    }

    private fun successorGoUp(node: BstNode<TKey, TValue>, key: TKey): BstNode<TKey, TValue> {
        var current = node.parent

        while (null !== current && this.comp(key, current.key) < 0) {
            current = current.parent
        }

        return current ?: throw NoSuchElementException()
    }

    fun findNode(key: TKey): Pair<BstNode<TKey, TValue>?, BstNode<TKey, TValue>?> {
        var parent: BstNode<TKey, TValue>? = null
        var node = this.root

        while (null !== node) {
            val comparison = this.comp(key, node.key)

            parent = node
            node = if (comparison < 0)
                node.left
            else if (comparison > 0)
                node.right
            else
                break
        }

        return parent to node
    }

    fun leftRotate(node: BstNode<TKey, TValue>) {
        val child = node.right!!
        val grandchild = child.left

        child.left = node
        node.right = grandchild
        node.parent = child
        grandchild?.parent = node
    }

    fun rightRotate(node: BstNode<TKey, TValue>) {
        val child = node.left!!
        val grandchild = child.right

        child.right = node
        node.left = grandchild
        node.parent = child
        grandchild?.parent = node
    }
}
