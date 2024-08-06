package collections

import java.io.Serializable
import kotlin.math.max

private class TreeNode<TElement>(
    var item: TElement,
    var relativePosition: Int,
    var left: TreeNode<TElement>?,
    var right: TreeNode<TElement>?,
) : Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L
    }

    var leftIsPrev: Boolean = true
    var rightIsNext: Boolean = true
    var height: Int = 0

    fun find(index: Int): TreeNode<TElement>? {
        val indexRelative = index - this.relativePosition

        if (0 == indexRelative) {
            return this
        }

        val nextNode =
            if (indexRelative < 0)
                this.leftSubtree()
            else
                this.rightSubtree()

        return nextNode?.find(indexRelative)
    }

    fun insert(index: Int, element: TElement): TreeNode<TElement> {
        val indexRelative = index - this.relativePosition

        return if (indexRelative <= 0)
            this.insertOnLeft(indexRelative, element)
        else
            this.insertOnRight(indexRelative, element)
    }

    private fun insertOnLeft(indexRelative: Int, element: TElement): TreeNode<TElement> {
        val leftSubtree = this.leftSubtree()

        val node =
            if (null === leftSubtree)
                TreeNode(element, -1, this, left)
            else
                leftSubtree.insert(indexRelative, element)

        this.setLeft(node, null)

        if (this.relativePosition >= 0) {
            ++(this.relativePosition)
        }

        val ret = this.rebalance()

        this.recalcHeight()

        return ret
    }

    private fun insertOnRight(indexRelative: Int, element: TElement): TreeNode<TElement> {
        val rightSubtree = this.rightSubtree()

        val node =
            if (null === rightSubtree)
                TreeNode(element, 1, this.right, this)
            else
                rightSubtree.insert(indexRelative, element)

        this.setRight(node, null)

        if (this.relativePosition >= 0) {
            --(this.relativePosition)
        }

        val ret = this.rebalance()

        this.recalcHeight()

        return ret
    }

    fun delete(index: Int): TreeNode<TElement>? {
        val indexRelative = index - this.relativePosition

        if (0 == indexRelative) {
            return this.deleteSelf()
        }

        if (indexRelative < 0) {
            val other = this.left!!.delete(indexRelative)

            this.setLeft(other, this.left!!.left)

            if (this.relativePosition > 0) {
                --(this.relativePosition)
            }
        }
        else {
            val other = this.right!!.delete(indexRelative)

            this.setRight(other, this.right!!.right)

            if (this.relativePosition < 0) {
                ++(this.relativePosition)
            }
        }

        this.recalcHeight()

        return this.rebalance()
    }

    private fun deleteSelf(): TreeNode<TElement>? {
        val left = this.leftSubtree()
        val right = this.rightSubtree()

        if (null === left && null === right) {
            return null
        }
        else if (null === right) {
            if (this.relativePosition > 0) {
                this.left!!.relativePosition += this.relativePosition
            }

            this.left!!.max().setRight(null, this.right)

            return this.left
        }
        else if (null === left) {
            this.right!!.relativePosition += this.relativePosition - (if (this.relativePosition < 0) 0 else 1)
            this.right!!.min().setLeft(null, this.left)

            return this.right
        }

        return this.minOrMax()
    }

    private fun minOrMax(): TreeNode<TElement> {
        if (this.balance() < 0) {
            val rightMin = this.right!!.min()

            this.item = rightMin.item

            if (this.leftIsPrev) {
                this.left = rightMin.left
            }

            this.right = this.right!!.removeMin()

            if (this.relativePosition < 0) {
                ++(this.relativePosition)
            }
        }
        else {
            val leftMax = this.left!!.max()

            this.item = leftMax.item

            if (this.rightIsNext) {
                this.right = leftMax.right
            }

            val leftPrev = this.left!!.left

            this.left = this.left!!.removeMax()

            if (null === this.left) {
                this.left = leftPrev
                this.leftIsPrev = true
            }

            if (this.relativePosition > 0) {
                --(this.relativePosition)
            }
        }

        this.recalcHeight()

        return this
    }

    private fun min(): TreeNode<TElement> =
        if (null === this.leftSubtree())
            this
        else
            this.left!!.min()

    private fun max(): TreeNode<TElement> =
        if (null === this.rightSubtree())
            this
        else
            this.right!!.max()

    private fun removeMin(): TreeNode<TElement> {
        if (null === this.leftSubtree()) {
            return this.deleteSelf()!!
        }

        this.setLeft(this.left!!.removeMin(), this.right!!.right)

        if (this.relativePosition > 0) {
            --(this.relativePosition)
        }

        this.recalcHeight()

        return this.rebalance()
    }

    private fun removeMax(): TreeNode<TElement> {
        if (null === this.rightSubtree()) {
            return this.deleteSelf()!!
        }

        this.setRight(this.right!!.removeMax(), this.left!!.left)

        if (this.relativePosition < 0) {
            ++(this.relativePosition)
        }

        this.recalcHeight()

        return this.rebalance()
    }

    private fun setLeft(node: TreeNode<TElement>?, prev: TreeNode<TElement>?) {
        this.leftIsPrev = null === node
        this.left = if (this.leftIsPrev) prev else node

        this.recalcHeight()
    }

    private fun leftSubtree(): TreeNode<TElement>? =
        if (this.leftIsPrev)
            null
        else
            this.left

    private fun setRight(node: TreeNode<TElement>?, next: TreeNode<TElement>?) {
        this.rightIsNext = null === node
        this.right = if (this.rightIsNext) next else node

        this.recalcHeight()
    }

    private fun rightSubtree(): TreeNode<TElement>? =
        if (this.rightIsNext)
            null
        else
            this.right

    private fun rebalance(): TreeNode<TElement> {
        val balance = this.balance()

        return when (balance) {
            -1, 0, 1 -> this
            -2 -> {
                val left = this.left!!

                if (left.balance() > 0) {
                    this.setLeft(left.rotateLeft(), null)
                }

                this.rotateRight()
            }
            2 -> {
                val right = this.right!!

                if (right.balance() < 0) {
                    this.setRight(right.rotateRight(), null)
                }

                this.rotateLeft()
            }
            else -> throw IllegalStateException()
        }
    }

    private fun balance(): Int = this.getHeight(this.rightSubtree()) - this.getHeight(this.leftSubtree())

    private fun getHeight(node: TreeNode<TElement>?): Int = node?.height ?: -1

    private fun recalcHeight() {
        val left = this.getHeight(this.leftSubtree())
        val right = this.getHeight(this.rightSubtree())

        this.height = max(left, right) + 1
    }

    private fun rotateLeft(): TreeNode<TElement> {
        val newTop = this.right!!
        val movedNode = this.leftSubtree()!!.rightSubtree()

        val newTopPos = this.relativePosition + this.getOffset(newTop)
        val myNewPos = -(newTop.relativePosition)
        val movedPos = this.getOffset(newTop) + this.getOffset(movedNode)

        this.setRight(movedNode, newTop)
        newTop.setLeft(this, null)

        this.setOffset(newTop, newTopPos)
        this.setOffset(this, myNewPos)
        this.setOffset(movedNode, movedPos)

        return newTop
    }

    private fun rotateRight(): TreeNode<TElement> {
        val newTop = this.left!!
        val movedNode = this.leftSubtree()!!.rightSubtree()

        val newTopPos = this.relativePosition + getOffset(newTop)
        val myNewPos = -(newTop.relativePosition)
        val movedPos = getOffset(newTop) + getOffset(movedNode)

        setLeft(movedNode, newTop)
        newTop.setRight(this, null)

        setOffset(newTop, newTopPos)
        setOffset(this, myNewPos)
        setOffset(movedNode, movedPos)

        return newTop
    }

    private fun getOffset(node: TreeNode<TElement>?): Int {
        if (null === node) {
            return 0
        }

        return node.relativePosition
    }

    private fun setOffset(node: TreeNode<TElement>?, newOffset: Int): Int {
        if (null === node) {
            return 0
        }

        val old = this.getOffset(node)

        node.relativePosition = newOffset

        return old
    }
}

@Suppress("RemoveRedundantQualifierName")
class TreeList<TElement> internal constructor() : AbstractList<TElement>(), Serializable {
    private companion object {
        @Suppress("ConstPropertyName")
        const val serialVersionUID: Long = 1L

        fun checkIndexWithSizeExcluded(index: Int, size: Int) {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException("0 <= index < size. Index = $index, size = $size")
            }
        }

        fun checkIndexWithSizeIncluded(index: Int, size: Int) {
            if (index < 0 || index > size) {
                throw IndexOutOfBoundsException("0 <= index <= size. Index = $index, size = $size")
            }
        }
    }

    private var root: TreeNode<TElement>? = null

    override var size: Int = 0
        private set

    override operator fun get(index: Int): TElement {
        TreeList.checkIndexWithSizeIncluded(index, this.size)

        return this.root!!.find(index)!!.item
    }

    override operator fun set(index: Int, element: TElement): TElement {
        TreeList.checkIndexWithSizeIncluded(index, this.size)

        val node = this.root!!.find(index)!!

        val old = node.item
        node.item = element

        return old
    }

    override fun add(index: Int, element: TElement) {
        TreeList.checkIndexWithSizeExcluded(index, this.size)

        this.root?.let {
            this.root = it.insert(index, element)
        } ?: run {
            this.root = TreeNode(element, index, null, null)
        }

        ++(this.size)
        ++(super.modCount)
    }

    override fun addAll(index: Int, elements: Collection<TElement>): Boolean {
        for ((offset, item) in elements.withIndex()) {
            this.add(index + offset, item)
        }

        return true
    }

    override fun removeAt(index: Int): TElement {
        val item = this[index]

        this.root = this.root!!.delete(index)

        --(this.size)
        ++(super.modCount)

        return item
    }

    override fun clear() {
        this.root = null
        this.size = 0
        ++(super.modCount)
    }
}

fun <TElement> treeListOf(): TreeList<TElement> = TreeList()

fun <TElement> treeListOf(vararg elements: TElement): TreeList<TElement> = elements.toTreeList()

fun <TElement> Iterable<TElement>.toTreeList(): TreeList<TElement> {
    val tree = TreeList<TElement>()

    for (item in this) {
        tree.add(item)
    }

    return tree
}

fun <TElement> Sequence<TElement>.toTreeList(): TreeList<TElement> {
    val tree = TreeList<TElement>()

    for (item in this) {
        tree.add(item)
    }

    return tree
}

fun <TElement> Array<out TElement>.toTreeList(): TreeList<TElement> {
    val tree = TreeList<TElement>()

    tree.addAll(this)

    return tree
}
