import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("mainLogger")

fun main(args: Array<String>) {
    // Then we wire up the application into objects. The variables are translated into "rich" objects like
    // DataSources, Services and Repositories.
    try {
        // We load the config first resolving and checking if we have all the values we need.
        val config = loadConfig()
        val app = DependencyInjectionApplicationContext(config).create()
        // Then you start the main application code.
        app.start()
    } catch (e: Exception) {
        logger.error("Could not load application", e)
        exitProcess(1)
    }
}

/**
 * What we need to know to create the application.
 */
data class DependencyInjectionApplicationConfig(
        val port: Int,
        val databaseUrl: String,
        val databaseUsername: String,
        val databasePassord: String
)

/**
 * The class responsible for wiring it all together
 */
class DependencyInjectionApplicationContext(
        private val configuration: DependencyInjectionApplicationConfig,
        private val orderRepository: OrderRepository? = null
) {

    fun create(): DependencyInjectionApplication {
        // Create DataSource(s)
        val dataSource = HikariDataSource().apply {
            jdbcUrl = configuration.databaseUrl
            username = configuration.databaseUsername
            password = configuration.databasePassord
            connectionInitSql = "set time zone 'UTC'"
        }
        val jdbi = Jdbi.create(dataSource)
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(PostgresPlugin())

        // Create any services and put inject any repositories
        val businessService = OrderServiceImpl(orderRepository ?: OrderRepositoryImpl())

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
class DependencyInjectionApplication(private val port: Int, private val orderService: OrderService) {

    private val server: ApplicationEngine = embeddedServer(Netty, port = port) {
        install(DefaultHeaders)
        install(Compression)
        install(ContentNegotiation) {
            jackson {}
        }

        routing {
            get {
                this.call.respond(orderService.getOrder(UUID.randomUUID()))
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
            requiredEnv(properties, "DATABASE_USER"),
            requiredEnv(properties, "DATABASE_PASSWORD")
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
