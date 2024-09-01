package collections

import java.io.Serializable

private class RbNode<TKey, TValue>(entry: MutableEntry<TKey, TValue>) : BstNode<TKey, TValue>(entry), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    var isBlack: Boolean = false

    val isRed: Boolean
        get() = !this.isBlack

    val parentRb: RbNode<TKey, TValue>?
        get() = super.parent as RbNode<TKey, TValue>?
    val leftRb: RbNode<TKey, TValue>?
        get() = super.left as RbNode<TKey, TValue>?
    val rightRb: RbNode<TKey, TValue>?
        get() = super.right as RbNode<TKey, TValue>?
}

private val RbNode<*, *>?.isBlack: Boolean
    get() = this?.isBlack ?: true

private val RbNode<*, *>?.isRed: Boolean
    get() = this?.isRed ?: false

private val <TKey, TValue> RbNode<TKey, TValue>?.siblingRb: RbNode<TKey, TValue>?
    get() = this?.let {
        if (it.isLeftChild)
            it.parentRb?.rightRb
        else
            it.parentRb?.leftRb
    }

private val <TKey, TValue> RbNode<TKey, TValue>.grandparentRb: RbNode<TKey, TValue>?
    get() = this.parentRb?.parentRb

private val <TKey, TValue> RbNode<TKey, TValue>.uncleRb: RbNode<TKey, TValue>?
    get() = this.parentRb.siblingRb

class RbMap<TKey, TValue>(
    comparator: (TKey, TKey) -> Int
) : AbstractSortedMap<TKey, TValue>(comparator, ::RbNode), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    constructor(compObj: Comparator<TKey>? = null) : this(compObj.function)

    constructor(
        elements: Sequence<Map.Entry<TKey, TValue>>,
        compObj: Comparator<TKey>? = null
    ) : this(elements.asIterable(), compObj)

    constructor(
        elements: Map<TKey, TValue>,
        compObj: Comparator<TKey>? = null
    ) : this(elements.asIterable(), compObj)

    constructor(
        elements: Collection<Map.Entry<TKey, TValue>>,
        compObj: Comparator<TKey>? = null
    ) : this(elements.asIterable(), compObj)

    constructor(
        elements: Iterable<Map.Entry<TKey, TValue>>,
        compObj: Comparator<TKey>? = null
    ) : this(compObj) {
        for ((key, value) in elements) {
            this[key] = value
        }
    }

    override fun balanceTreeAfterInsertion(node: BstNode<TKey, TValue>) {
        val newNode = node as RbNode<TKey, TValue>

        if (newNode === super.tree.root) {
            newNode.isBlack = true
        }
        else {
            if (newNode.parentRb.isRed) {
                this.handleParentBeingRed(newNode, newNode.parentRb!!)
            }
        }
    }

    private fun handleParentBeingRed(current: RbNode<TKey, TValue>, parent: RbNode<TKey, TValue>) {
        val grand = current.grandparentRb!!
        val uncle = current.uncleRb

        if (uncle.isBlack) {
            this.doRotations(current, parent, grand)
        }
        else {
            parent.isBlack = true
            uncle!!.isBlack = true
            grand.isBlack = false

            this.balanceTreeAfterInsertion(grand)
        }
    }

    private fun doRotations(current: RbNode<TKey, TValue>, parent: RbNode<TKey, TValue>, grand: RbNode<TKey, TValue>) {
        if (parent.isLeftChild) {
            if (current.isRightChild) {
                super.tree.leftRotate(parent)
            }

            super.tree.rightRotate(grand)
        }
        else {
            if (current.isLeftChild) {
                super.tree.rightRotate(parent)
            }

            super.tree.leftRotate(grand)
        }

        parent.isBlack = true
        grand.isBlack = false
    }

    override fun removeNode(node: BstNode<TKey, TValue>) {
        if (node === super.tree.root) {
            super.tree.deleteTree()
        }

        val final = this.deleteWhileTwoChildren(node) as RbNode<TKey, TValue>
        val child = final.leftRb ?: final.rightRb

        if (final.isBlack && child.isBlack) {
            this.removeTwoBlack(final)
        }
        else {
            this.removeOneRed(final, child)
        }

        --(this.size)
        ++(super.modCount)
    }

    private fun deleteWhileTwoChildren(node: BstNode<TKey, TValue>): BstNode<TKey, TValue> {
        var current = node

        while (null !== current.left && null !== current.right) {
            val successor = super.tree.successor(node, node.key)

            node.entry = successor!!.entry

            current = successor
        }

        return current
    }

    private fun removeOneRed(node: BstNode<TKey, TValue>, child: RbNode<TKey, TValue>?) {
        child?.isBlack = true

        super.tree.removeNodeWithOneChildOrLess(node)
    }

    private fun removeTwoBlack(node: RbNode<TKey, TValue>) {
        if (node === super.tree.root) {
            return
        }

        val sibling = node.siblingRb
        val parent = node.parentRb!!

        super.tree.removeNodeWithOneChildOrLess(node)

        if (sibling.isRed) {
            this.handleSiblingBeingRed(sibling, parent)
        }
        else {
            val left = sibling?.leftRb
            val right = sibling?.rightRb

            if (left.isRed || right.isRed) {
                this.handleSiblingBeingBlackAndOneRedChild(sibling!!, parent, left, right)
            }
            else {
                this.handleSiblingBeingBlackAndTwoBlackChildren(sibling, parent)
            }
        }
    }

    private fun handleSiblingBeingRed(sibling: RbNode<TKey, TValue>?, parent: RbNode<TKey, TValue>) {
        parent.isBlack = false
        sibling?.isBlack = true

        if (sibling.isLeftChild) {
            super.tree.rightRotate(parent)
        }
        else {
            super.tree.leftRotate(parent)
        }
    }

    private fun handleSiblingBeingBlackAndOneRedChild(
        sibling: RbNode<TKey, TValue>,
        parent: RbNode<TKey, TValue>,
        left: RbNode<TKey, TValue>?,
        right: RbNode<TKey, TValue>?
    ) {
        if (left.isRed) {
            sibling.isBlack = false

            super.tree.rightRotate(sibling)
        }

        if (right.isRed) {
            sibling.isBlack = parent.isBlack
            parent.isBlack = false

            super.tree.leftRotate(parent)
        }
    }

    private fun handleSiblingBeingBlackAndTwoBlackChildren(sibling: RbNode<TKey, TValue>?, parent: RbNode<TKey, TValue>) {
        sibling?.isBlack = false

        if (parent.isBlack) {
            this.removeTwoBlack(parent)
        }
    }
}

fun <TKey, TValue> Map<TKey, TValue>.toRbMap(): RbMap<TKey, TValue> = RbMap(this)

fun <TKey, TValue> Collection<Map.Entry<TKey, TValue>>.toRbMap(): RbMap<TKey, TValue> = RbMap(this)

fun <TKey, TValue> Iterable<Map.Entry<TKey, TValue>>.toRbMap(): RbMap<TKey, TValue> = RbMap(this)

fun <TKey, TValue> Sequence<Map.Entry<TKey, TValue>>.toRbMap(): RbMap<TKey, TValue> = RbMap(this)
