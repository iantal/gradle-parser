package io.gradleparser.gradleparser.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DependencyExtractorTest {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun readFile(fileName: String): String {
        return this::class.java.classLoader.getResource(fileName).readText()
    }

    @Test
    fun `test project extraction and its sources`() {
        val data = readFile("kafka.txt")
        val parser = DependencyExtractor(data)

        val extractedData = parser.extract()

        val projects = extractedData.projects
        assertThat(projects.size).isEqualTo(1)
        assertThat(projects["core"]?.sourceSet?.size).isEqualTo(12)
    }

    @Test
    fun `test extracted sources and the number of their nodes`() {
        val data = readFile("kafka.txt")
        val parser = DependencyExtractor(data)

        val extractedData = parser.extract()

        val projects = extractedData.projects
        projects["core"]?.sourceSet?.forEach { (name, nodes) ->
            when (name) {
                "compileClasspath" -> assertThat(nodes.size).isEqualTo(67)
                "compileOnly" -> assertThat(nodes.size).isEqualTo(1)
                "testCompileClasspath" -> assertThat(nodes.size).isEqualTo(396)
                "incrementalScalaAnalysisFortest" -> assertThat(nodes.size).isEqualTo(31)
                "zinc" -> assertThat(nodes.size).isEqualTo(96)
                "testRuntimeClasspath" -> assertThat(nodes.size).isEqualTo(396)
            }
        }
    }

    @Test
    fun `test extracted libraries and their children`() {
        val data = readFile("kafka.txt")
        val parser = DependencyExtractor(data)

        val extractedData = parser.extract()

        val projects = extractedData.projects
        val nodes : List<Node>? = projects["core"]?.sourceSet?.get("compileClasspath")

        val levelZeroNodes = nodes?.filter { node -> node.level == 0 }?.count()
        val levelOneNodes = nodes?.filter { node -> node.level == 1 }?.count()
        val levelTwoNodes = nodes?.filter { node -> node.level == 2 }?.count()
        val levelThreeNodes = nodes?.filter { node -> node.level == 3 }?.count()
        val levelFourNodes = nodes?.filter { node -> node.level == 4 }?.count()

        assertThat(levelZeroNodes).isEqualTo(16)
        assertThat(levelOneNodes).isEqualTo(28)
        assertThat(levelTwoNodes).isEqualTo(12)
        assertThat(levelThreeNodes).isEqualTo(11)
        assertThat(levelFourNodes).isEqualTo(0)
    }

    @Test
    fun `test extracted library`() {
        val data = readFile("kafka.txt")
        val parser = DependencyExtractor(data)

        val extractedData = parser.extract()

        val projects = extractedData.projects
        val nodes : List<Node>? = projects["core"]?.sourceSet?.get("compileClasspath")

        assertThat(nodes?.get(1)?.text).isEqualTo("com.github.luben:zstd-jni:1.4.5-2")
    }
}