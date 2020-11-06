package io.gradleparser.gradleparser

import io.gradleparser.gradleparser.grpc.ParseService
import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.logging.Logger

@SpringBootApplication
class GradleParserApplication

fun main(args: Array<String>) = runBlocking {
	val log = Logger.getLogger(this::class.java.simpleName)

	val server: Server = ServerBuilder
			.forPort(Constants.port)
			.addService(ParseService())
			.build()
	Runtime.getRuntime().addShutdownHook(
			Thread {
				log.info("Shutting down gRPC server since JVM is shutting down")
				server.shutdown()
			}
	)
	log.info("Server started, listening on ${Constants.port}")

	server.start()
	server.awaitTermination()
}
