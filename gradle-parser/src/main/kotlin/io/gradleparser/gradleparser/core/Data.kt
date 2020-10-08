package io.gradleparser.gradleparser.core

import java.util.*
import kotlin.collections.HashMap


class DependencyDescriptor {
    val projects: HashMap<String, Subproject> = HashMap()

    fun addProject(projectName: String, subproject: Subproject) {
        projects[projectName] = subproject
    }
}

class Subproject {
    val sourceSet: HashMap<String, List<Node>> = HashMap()

    fun addSource(source: String, nodes: List<Node>) {
        sourceSet[source] = nodes
    }
}

class Node {
    var text: String? = null
    var level = 0
    var parent: Node? = null
    private var children: MutableList<Node>? = null

    constructor() {}
    constructor(text: String?, level: Int) {
        this.text = text
        this.level = level
    }

    constructor(text: String?, level: Int, parent: Node?) {
        this.text = text
        this.parent = parent
        this.level = level
    }

    fun addChild(childNode: Node) {
        if (children == null) {
            children = ArrayList()
        }
        children!!.add(childNode)
    }

    fun getNodesList(list: MutableList<Node?>): List<Node?> {
        if (level == 0) {
            list.add(this)
        }
        if (children != null) {
            for (n in children!!) {
                list.add(n)
                n.getNodesList(list)
            }
        }
        return list
    }
}