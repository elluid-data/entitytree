package com.elluid.collections

import kotlin.collections.ArrayDeque

class EntityTree<T>(val mode : Behavior = Behavior.NO_INSERT_ON_ILLEGAL_PARENT_ID) : Iterable<T> where T : Node<T>, T : Comparable<T> {
    val rootNodes = mutableListOf<T>()
    val idSet = mutableSetOf<Int>() // All primary ids.

    fun add(entity: T) {
        replaceIfExists(entity)

        if (entity.getParentId() == 0) {
            rootNodes.add(entity)
            idSet.add(entity.getId())
        } else { // Add to a parent
            // Throws exception if Behavior.EXCEPTION_ON_ILLEGAL_PARENT_ID is set
            //  Otherwise just exits function.
            if(!parentIdExists(entity.getParentId())) return

            var insertComplete = false
            for (root in rootNodes) {
                insertComplete = addChild(root, entity)
                if(insertComplete) {
                    break
                }
            }
        }
    }

    private fun addChild(parent: T, entity: T) : Boolean {
        var isAdded = false
        when {

            parent.getId() == entity.getParentId() -> {
                parent.addChild(entity)
                idSet.add(entity.getId())
                return true
            }

            parent.getChildren().isEmpty() -> return false

            else -> {
                for (node in parent.getChildren()) {
                    isAdded = addChild(node, entity)
                    if(isAdded)
                        break
                }

            }
        }
        return isAdded
    }

    operator fun get(id: Int): T? {
        var data: T? = null

        if(contains(id)) {
            for (node in rootNodes) {
                if (node.getId() == id) {
                    return node
                } else {
                    if (node.getChildren().isNotEmpty()) {
                        data = getLeaf(node, id)
                        if (data != null) return data
                    }
                }
            }
        }
        return data
    }

    fun remove(id: Int): Boolean {
        if(contains(id)) {
            for (index in rootNodes.indices) {
                if (rootNodes[index].getId() == id) {
                    // Remove node and id's from hashset
                    removeChildrenFromSet(rootNodes[index])
                    rootNodes.removeAt(index)
                    break
                } else {
                    if (rootNodes[index].getChildren().isNotEmpty()) {
                        val node = getLeaf(rootNodes[index], id)
                        if (node != null) {
                            removeChildrenFromSet(node)
                            removeChild(node, rootNodes[index])
                            break
                        }
                    }
                }
            }

        }
        return true
    }

    fun sort() {
        rootNodes.sort()
        this.forEach {
            if(it.getChildren().isNotEmpty()) {
                it.getChildren().sort()
            }
        }
    }

    private fun removeChildrenFromSet(node: T) {
        val idList = mutableListOf<Int>()
        idList.add(node.getId()) // No
        val completeList = getChildrenIds(node, idList)
        completeList.forEach { idSet.remove(it) }
    }

    private fun removeChild(nodeToDelete: T, parentNode: T) {
        if(parentNode.getChildren().contains(nodeToDelete)) {
                parentNode.getChildren().remove(nodeToDelete)
                return
        }
        else {
            for(node in parentNode.getChildren()) {
                if(node.getChildren().isNotEmpty()) {
                    removeChild(nodeToDelete, node)
                }
            }
        }
    }

    private fun getChildrenIds(node: T, idList: MutableList<Int>): MutableList<Int> {
        for (child in node.getChildren()) {
            idList.add(child.getId())
            if(child.getChildren().isNotEmpty()) {
                getChildrenIds(child, idList)
            }
        }
        return idList
    }


    // If same id found, add children to new entity and delete previous entity
    private fun replaceIfExists(entity: T) {
        if(contains(entity.getId())) {
            val entityToReplace = get(entity.getId())
            val children = entityToReplace?.getChildren()
            children?.forEach { entity.addChild(it) }
            entityToReplace?.getId()?.let { remove(it) }
        }
    }

    private fun getLeaf(tree: T, id: Int): T? {
        var result: T? = null
        for (node in tree.getChildren()) {
            if (node.getId() == id) return node else {
                result = getLeaf(node, id)
                if (result != null) return result
            }
        }
        return result
    }

    fun getFlatList() : List<T> {
        val entities = mutableListOf<T>()
        this.iterator().forEach {
            entities.add(it) }
        return entities
    }

    override fun iterator(): Iterator<T> {

        return object : Iterator<T> {
            var currentIndex = 0
            var que: ArrayDeque<T> = ArrayDeque()

            override fun hasNext(): Boolean {
                if (currentIndex < rootNodes.size) {
                    return true
                }
                return !que.isEmpty()
            }

            override fun next(): T {
                lateinit var current : T
                if (que.isEmpty()) {
                    current = rootNodes[currentIndex++]
                    getLeafs(current, 1)
                } else {
                    current = que.removeFirst()
                }
                return current
            }

            private fun getLeafs(entity: T, depth: Int) {
                if (entity.getChildren().isEmpty()) return

                for (leaf in entity.getChildren()) {
                    leaf.setLevel(depth)
                    que.addLast(leaf)
                    getLeafs(leaf, depth + 1)
                }
            }
        }
    }

    fun contains(id: Int) : Boolean {
        return idSet.contains(id)
    }

    private fun parentIdExists(id: Int) : Boolean {
        val parentExists = contains(id)
        if(!parentExists && mode == Behavior.EXCEPTION_ON_ILLEGAL_PARENT_ID) {
            throw IllegalArgumentException("parentId not found")
        }

        return parentExists
    }



}

enum class Behavior {
    EXCEPTION_ON_ILLEGAL_PARENT_ID,
    NO_INSERT_ON_ILLEGAL_PARENT_ID
}