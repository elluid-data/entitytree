import com.elluid.collections.Behavior
import com.elluid.collections.EntityTree
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.DisplayName

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.util.*


class ForestTest {

    @Test
    @DisplayName("Create tree with 12 entities.")
    fun createAndPrintTree() {
        val categories = createEntities()
        val tree = EntityTree<Category>()
        categories.forEach { tree.add(it) }

        val listFromTree: List<Category> = tree.getFlatList()
        for(cat in listFromTree) {
            cat.printTreeFriendly()
        }

        assertEquals(12, listFromTree.size)
        assertEquals(tree[100], Category("Switch Lite", 100, 52))
        assertEquals(tree.get(1100), Category("Test",1100))

        tree.remove(120) // Remove id 120
        assertEquals(11, tree.getFlatList().size)
    }

    @Test
    @DisplayName("Exception when adding illegal child when using Behavior.STRICT")
    fun addIllegalChild() {
        val categories = createEntities()
        val tree = EntityTree<Category>(Behavior.EXCEPTION_ON_ILLEGAL_PARENT_ID)
        for (cat in categories) {
            tree.add(cat)
        }
        assertThrows<IllegalArgumentException> {
            tree.add(Category("Testing Illegal",34, 999))
        }

    }


    private fun createEntities(): List<Category> {
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
        return categories
    }

    @Test
    @DisplayName("Replace entity with same id")
    fun replaceCategoryWithSameId() {
        val entities = createEntities()
        val tree = EntityTree<Category>()
        entities.forEach { tree.add(it) }
        tree.add(Category("Fruits - New!",10))
        assertEquals(tree.get(10), Category("Fruits - New!", 10))
        tree.sort()
        tree.forEach { it.printTreeFriendly() }
    }
}