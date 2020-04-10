import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondOutputStream
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

fun main(args: Array<String>) {
    // We load the config first resolving and checking if we have all the values we need.
    val config = loadConfig()

    // Then we wire up the application into objects. The variables are translated into "rich" objects like
    // DataSources, Services and Repositories.
    val app = DependencyInjectionApplicationContext(config).create()

    // Then you start the main application code.
    app.start()
}

/**
 * What we need to know to create the application.
 */
data class DependencyInjectionApplicationConfig(
        val port: Int,
        val databaseUrl: String,
        val databaseUsername: String?
)

/**
 * The class responsible for wiring it all together
 */
class DependencyInjectionApplicationContext(
        private val configuration: DependencyInjectionApplicationConfig,
        private val businessRepository: BusinessRepository? = null) {

    fun create(): DependencyInjectionApplication {
        // Create DataSource(s)
        // ...

        // Create any services and put inject any repositories
        val businessService = BusinessServiceImpl(businessRepository ?: BusinessRepositoryImpl())

        // Create the main application and inject whatever you've created above.
        return DependencyInjectionApplication(
                configuration.port,
                businessService
        )
    }

}

/**
 * The main application. Try to keep any wiring / startup logic out of this. If/Else at the top level should only be
 * related to business decisons.
 */
class DependencyInjectionApplication(private val port: Int, private val businessService: BusinessService) {

    private val server: ApplicationEngine = embeddedServer(Netty, port = port) {
        install(DefaultHeaders)
        install(Compression)
        install(ContentNegotiation) {
            jackson {}
        }

        routing {
            get {
                this.call.respond(businessService.getData("id"))
            }

            get("list") {
                this.call.respondOutputStream {

                }
            }
        }
    }


    fun start(): String {
        server.start()
        return "http://localhost:${port}"
    }

    fun stop() {
        server.stop(1000, 5000)
    }

}


fun loadConfig(): DependencyInjectionApplicationConfig {
    val properties = loadRuntimeProperties()

    return DependencyInjectionApplicationConfig(
            requiredEnv(properties, "PORT", "5000").toInt(),
            requiredEnv(properties, "DATABASE_URL"),
            env(properties, "DATABASE_USER")
    )
}

private fun propertyName(name: String) = name.toLowerCase().replace("_", ".")

fun requiredEnv(properties: Properties?, name: String, defaultValue: String? = null): String {
    return env(properties, name, defaultValue)
            ?: throw IllegalStateException("Missing env variable: $name and no default value given. (Property name: ${propertyName(name)})")
}

fun env(properties: Properties?, name: String, defaultValue: String? = null): String? {
    return when {
        System.getenv(name) != null -> {
            System.getenv(name)
        }
        properties?.getProperty(propertyName(name)) != null -> {
            properties.getProperty(propertyName(name))
        }
        else -> defaultValue
    }
}

fun loadRuntimeProperties(): Properties? {
    return try {
        Properties().apply {
            FileInputStream("./etc/application.properties").use { fis ->
                load(fis)
            }
        }
    } catch (e: FileNotFoundException) {
        null
    }
}
