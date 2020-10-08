package io.gradleparser.gradleparser.core


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

class Node(
        var text: String? = null,
        var level: Int = 0,
        var parent: Node? = null
)