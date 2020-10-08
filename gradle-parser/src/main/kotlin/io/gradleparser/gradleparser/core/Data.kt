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

enum class LibraryType {
    DIRECT,
    TRANSITIVE
}

data class Project(var name: String, val libraries: MutableList<Library> = mutableListOf()) {
    fun addLibrary(library: Library) {
        libraries.add(library)
    }
}

data class Library(
        var name: String,
        val type: String,
        val scope: String
)