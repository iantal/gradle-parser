package io.gradleparser.gradleparser.grpc

import io.gradleparser.gradleparser.GradleParseServiceGrpcKt
import io.gradleparser.gradleparser.Library
import io.gradleparser.gradleparser.ParseRequest
import io.gradleparser.gradleparser.ParseResponse
import io.gradleparser.gradleparser.Project
import io.gradleparser.gradleparser.core.DependencyExtractor
import io.gradleparser.gradleparser.core.LibraryType
import java.util.*
import java.util.logging.Logger


class ParseService : GradleParseServiceGrpcKt.GradleParseServiceCoroutineImplBase() {
    val log: Logger = Logger.getLogger(this::class.java.simpleName)

    override suspend fun parse(request: ParseRequest): ParseResponse {
        log.info("Received gradle parse request")

        val decodedData = decodeBase64(request.data)
        val parser = DependencyExtractor(decodedData)
        val extractedData = parser.extract()

        val projects = mutableListOf<Project>()

        for ((project, subproject) in extractedData.projects) {
            val libs = mutableSetOf<Library>()

            for ((sourceType, nodes) in subproject.sourceSet) {
                val scope: String = when {
                    sourceType.startsWith("test") -> "test"
                    sourceType.startsWith("compile") -> "compile"
                    sourceType.contains("runtime") -> "runtime"
                    else -> sourceType.toLowerCase()
                }

                for (node in nodes) {
                    var name = node.text
                    if (name != null && name.contains(" ")) {
                        val splits = node.text?.split(" ")
                        if (splits?.size!! > 1) {
                            name = splits[0].trim()
                        }
                    }
                    val type = if (node.level == 0) LibraryType.DIRECT else LibraryType.TRANSITIVE

                    libs.add(Library.newBuilder()
                            .setName(name?.trim())
                            .setType(type.toString().toLowerCase())
                            .setScope(scope)
                            .build())
                }
            }

            var projectName = project
            if (project.contains(" ")) {
                projectName = project.split(" ")[0].trim()
            }

            projects.add(Project.newBuilder()
                    .setName(projectName)
                    .addAllLibraries(libs)
                    .build())
        }

        return ParseResponse.newBuilder()
                .addAllProjects(projects)
                .build()
    }

    private fun decodeBase64(data: String): String {
        val decoder = Base64.getDecoder()
        val decoded = decoder.decode(data)
        return String(decoded, Charsets.UTF_8)
    }
}