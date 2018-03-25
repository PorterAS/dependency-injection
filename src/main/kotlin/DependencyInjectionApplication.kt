import spark.Service
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

    private val sparkServer = Service.ignite()!!

    fun start(): String {
        with(sparkServer) {
            port(port)

            get("*") { _, _ -> businessService.getData("id") }

            awaitInitialization()
        }

        return "http://localhost:${sparkServer.port()}"
    }

    fun stop() {
        sparkServer.stop()
    }

}


fun loadConfig(randomPort: Boolean = false): DependencyInjectionApplicationConfig {
    val properties = loadRuntimeProperties()

    return DependencyInjectionApplicationConfig(
            if (randomPort) 0 else requiredEnv(properties, "PORT", "5000").toInt(),
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
    return if (System.getenv(name) != null) {
        System.getenv(name)
    } else if (properties?.getProperty(propertyName(name)) != null) {
        properties.getProperty(propertyName(name))
    } else defaultValue
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
