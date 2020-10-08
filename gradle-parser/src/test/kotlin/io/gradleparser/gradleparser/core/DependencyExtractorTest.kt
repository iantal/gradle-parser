package io.gradleparser.gradleparser.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DependencyExtractorTest {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun readFile(fileName: String): String {
        return this::class.java.classLoader.getResource(fileName).readText()
    }

    @Test
    fun parse() {
        val data = readFile("kafka.txt")
        val parser = DependencyExtractor(data)
        val extractedData = parser.extract()
        extractedData.projects.forEach { (_, v) -> v.sourceSet
                .forEach { (sk, sv) -> println("Key: $sk Value: $sv") }
        }
    }
}