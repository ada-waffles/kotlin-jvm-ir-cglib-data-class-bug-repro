package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@SpringBootApplication
@EnableConfigurationProperties(
	NonValidatedProperties::class,
	ValidatedProperties::class,
	ManualComponentsProperties::class
)
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@ConfigurationProperties("demo")
@ConstructorBinding
data class NonValidatedProperties(val test1: String, val test2: String)

@ConfigurationProperties("demo")
@ConstructorBinding
@Validated
data class ValidatedProperties(val test1: String, val test2: String)

@ConfigurationProperties("demo")
@ConstructorBinding
@Validated
class ManualComponentsProperties(val test1: String, val test2: String) {
	operator fun component1() = test1

	operator fun component2() = test2
}

@Component
class DemoApplicationListener(
	private val nonValidatedProperties: NonValidatedProperties,
	private val validatedProperties: ValidatedProperties,
	private val manualComponentsProperties: ManualComponentsProperties
) {
	@EventListener
	fun onApplicationStarted(event: ApplicationStartedEvent) {
		println(nonValidatedProperties)

		val (nv1, nv2) = nonValidatedProperties
		println("Destructured: ($nv1, $nv2)")

		try {
			nonValidatedProperties.copy()
		}
		catch (ex: Throwable) {
			println("Error copying NonValidatedProperties")
			println(ex)
		}

		println()


		println(validatedProperties)

		val (v1, v2) = validatedProperties
		println("Destructured: ($v1, $v2)") //This one will print (null, null)

		try {
			validatedProperties.copy() //This will also throw a NullPointerException
		}
		catch (ex: Throwable) {
			println("Error copying ValidatedProperties")
			println(ex)
		}

		println()


		println(manualComponentsProperties)

		val (mc1, mc2) = manualComponentsProperties
		println("Destructured: ($mc1, $mc2)") //This one is fine, though, so it's just the auto-generated methods
	}
}
