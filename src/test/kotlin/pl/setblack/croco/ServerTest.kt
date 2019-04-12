package pl.setblack.croco

import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.Charset
import java.time.Clock
import java.time.Instant
import java.time.ZoneId


class ServerTest: BehaviorSpec({
    val testTime = "2015-03-04T13:00:01Z"
    given("a server") {
        val clock = Clock.fixed(Instant.parse(testTime), ZoneId.of("UTC"))
        val server = Server(clock)
        val route = server.createRoute()

        `when`("I ask for time") {
            val testClient = WebTestClient.bindToRouterFunction(route)
            val time = testClient.build().get().uri("/time").exchange()
            then("I should get $testTime") {
                time.expectBody().consumeWith {
                    it.responseBody.toString(Charset.defaultCharset()) shouldBe testTime
                }
            }
        }

    }
})