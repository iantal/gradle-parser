package io.gradleparser.gradleparser.core

import java.util.regex.Matcher
import java.util.regex.Pattern

class DependencyExtractor(val data: String) {

    companion object {
        val TREE_LINE: Pattern = "\\\\|\\+---".toPattern()
        val PROJECT_PATTERN: Pattern = "[-]*\\n(.*?)\\n[-]*\\n\\n".toPattern()
    }

    fun extract(): DependencyDescriptor {
        val dependenciesDescriptor = DependencyDescriptor()

        val firstProjectMatcher: Matcher = PROJECT_PATTERN.matcher(data)
        var projectIndex = 0
        while (firstProjectMatcher.find(projectIndex)) {
            projectIndex = firstProjectMatcher.start()

            var projectName: String = firstProjectMatcher.group(1)
            if (projectName.startsWith("ProjectTag :")) {
                projectName = projectName.substring(9)
            }

            val secondProjectMatcher: Matcher = PROJECT_PATTERN.matcher(data)
            val foundAnotherProject: Boolean = secondProjectMatcher.find(firstProjectMatcher.end())
            val dependenciesData: String = if (foundAnotherProject) {
                data.substring(projectIndex, secondProjectMatcher.start())
            } else {
                data.substring(projectIndex)
            }

            val subproject: Subproject = fillSourceSets(dependenciesData)
            dependenciesDescriptor.addProject(projectName, subproject)
            projectIndex = firstProjectMatcher.end()
        }
        return dependenciesDescriptor
    }

    private fun fillSourceSets(projectDepsData: String): Subproject {
        val subproject = Subproject()
        val matcher = TREE_LINE.matcher(projectDepsData)
        var sourceIndex = 0
        while (matcher.find(sourceIndex)) {
            val treeLineStart = matcher.start()

            val titleIndex = projectDepsData.lastIndexOf("\n\n", treeLineStart)
            if (titleIndex == -1) {
                throw ParseException("Cannot find source set description $sourceIndex")
            }

            var sourceSet = projectDepsData.substring(titleIndex + 2, projectDepsData.indexOf('\n', titleIndex + 3))
            if (sourceSet.contains(" - ")) {
                sourceSet = sourceSet.substring(0, sourceSet.lastIndexOf(" - "))
            }
            val sourceSetParser = SourceParser(projectDepsData.substring(treeLineStart))

            try {
                sourceSetParser.walkTree()
            } catch (e: Exception) {
                throw ParseException("Cannot extract source set: " + e.message)
            }

            subproject.addSource(sourceSet, sourceSetParser.nodes)
            sourceIndex = projectDepsData.indexOf("\n\n", treeLineStart)
        }
        return subproject
    }
}