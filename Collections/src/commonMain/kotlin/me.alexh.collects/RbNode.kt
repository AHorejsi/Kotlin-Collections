package me.alexh.collects

internal enum class RbColor {
    RED,
    BLACK
}

internal class RbNode<TElement>(
    var element: TElement,
    var color: RbColor = RbColor.RED,
    var parent: RbNode<TElement>? = null,
    var left: RbNode<TElement>? = null,
    var right: RbNode<TElement>? = null
) {
    fun isLeftChild(): Boolean {
        val parent = this.parent

        return null !== parent && parent.left === this
    }

    fun isRightChild(): Boolean {
        val parent = this.parent

        return null !== parent && parent.right === this
    }

    fun sibling(): RbNode<TElement>? {
        val parent = this.parent

        return if (null === parent) {
            null
        }
        else if (this.isLeftChild()) {
            parent.right
        }
        else {
            parent.left
        }
    }
}

internal var RbNode<*>?.color: RbColor
    get() = this?.color ?: RbColor.BLACK
    set(value) {
        this?.color = value
    }

internal fun <TElement> RbNode<TElement>?.parent(): RbNode<TElement>? = this?.parent

internal fun <TElement> RbNode<TElement>?.left(): RbNode<TElement>? = this?.left

internal fun <TElement> RbNode<TElement>?.right(): RbNode<TElement>? = this?.right

internal fun <TElement> RbNode<TElement>?.sibling(): RbNode<TElement>? = this?.sibling()
