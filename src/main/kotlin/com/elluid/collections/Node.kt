package com.elluid.collections

interface Node<T> {
    fun addChild(child: T)
    fun getChildren() : MutableList<T>
    fun getParentId() : Int
    fun getId() : Int
    fun setLevel(level: Int)
}