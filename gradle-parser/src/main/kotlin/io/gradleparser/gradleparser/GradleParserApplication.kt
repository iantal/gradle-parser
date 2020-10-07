package io.gradleparser.gradleparser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GradleParserApplication

fun main(args: Array<String>) {
	runApplication<GradleParserApplication>(*args)
}
