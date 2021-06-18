# entitytree

Tree with multiple rootnodes, children know their parents and their depth level.

The **type** must implement the **Node interface** and **Comparable** for sorting.

> Node Example 

```
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
```

> Usage

```
val tree = EntityTree<Category>()
val categories: MutableList<Category> = ArrayList()
        categories.add(Category("Fruits",10))
        categories.add(Category("Banana",111, 10))
        categories.add(Category("Yellow Banana",112, 111))
        categories.add(Category("Apple", 113, 10))
        categories.add(Category("Videogames", 5))
        categories.add(Category("Switch", 52, 5))
        categories.add(Category("PS4",39, 5))
        categories.add(Category("Switch Lite",100, 52))
        categories.add(Category("Switch Mini",110, 52))
        categories.add(Category("Switch Mini Mini",120, 110))
        categories.add(Category("Test",1100))
        categories.add(Category("Peeled Yellow Banan", 115, 112))
categories.forEach { tree.add(it) }
tree.sort()
tree.forEach { it.printTreeFriendly() }
```
> Output

```
==> Name: Fruits
	==> Name: Apple
	==> Name: Banana
		==> Name: Yellow Banana
			==> Name: Peeled Yellow Banan
==> Name: Test
==> Name: Videogames
	==> Name: PS4
	==> Name: Switch
		==> Name: Switch Lite
		==> Name: Switch Mini
			==> Name: Switch Mini Mini
```
