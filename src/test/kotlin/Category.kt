import com.elluid.collections.Node

data class Category(val name: String, private val id: Int = 0, private val parentId: Int = 0) : Node<Category>, Comparable<Category> {

    private val children = mutableListOf<Category>()
    private var level = 0

    override fun getId(): Int {
        return id
    }

    override fun getParentId(): Int {
        return parentId
    }

    override fun addChild(child: Category) {
        children.add(child)
    }

    override fun getChildren(): MutableList<Category> {
        return children
    }

    override fun setLevel(level: Int) {
        this.level = level
    }

    fun printTreeFriendly() {
        repeat(level) {
            print("\t")
        }
        println("==> Name: $name")
    }

    override fun compareTo(other: Category): Int {
        return name.compareTo(other.name)
    }
}