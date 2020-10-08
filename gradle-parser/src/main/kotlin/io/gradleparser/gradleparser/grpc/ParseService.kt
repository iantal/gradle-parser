package io.gradleparser.gradleparser.grpc

import io.gradleparser.gradleparser.GradleParseServiceGrpcKt
import io.gradleparser.gradleparser.ParseRequest
import io.gradleparser.gradleparser.ParseResponse
import io.gradleparser.gradleparser.Project
import java.util.*
import java.util.logging.Logger


class ParseService : GradleParseServiceGrpcKt.GradleParseServiceCoroutineImplBase() {
    val log: Logger = Logger.getLogger(this::class.java.simpleName)

    override suspend fun parse(request: ParseRequest): ParseResponse {
        log.info("Received gradle parse request")

        val decodedData = decodeBase64(request.data)
        println(decodedData)
//        val parser = Parser(StateManager())
//        val parsedData = parser.parse(decodedData)
//
//        val projects = mutableListOf<Project>()
//        for (project in parsedData) {
//            val libs = mutableListOf<Library>()
//            for (library in project?.libraries!!) {
//                libs.add(Library.newBuilder()
//                        .setName(library.name)
//                        .setType(library.type.toString().toLowerCase())
//                        .setScope(library.scope.toString().toLowerCase())
//                        .build())
//            }
//            projects.add(Project.newBuilder()
//                    .setName(project.name)
//                    .addAllLibraries(libs)
//                    .build())
//        }

        return ParseResponse.newBuilder()
                .addAllProjects(mutableListOf(Project.newBuilder()
                        .setName("abc")
                        .build()))
                .build()
    }

    private fun decodeBase64(data: String) : String{
        val decoder = Base64.getDecoder()
        val decoded = decoder.decode(data)
        return String(decoded, Charsets.UTF_8)
    }
}