This is a small application acting as an illustration to a blog post. It uses:

- Kotlin as the language
- Ktor to serve HTTP requests
- JUnit 5 for testing

You can read more about the reasoning and setup here: https://medium.com/porterbuddy/rolling-your-own-dependency-injection-7045f8b64403

This repo has also been extended to encompass some other examples of code. To see all our blogposts go to https://medium.com/porterbuddy

# Building and running

To build a ShadowJAR as a single binary distributable do:

	./gradlew build shadowJar

Then to run the application:

	java -jar ./build/libs/di-app.jar

# Caveats

- The KTor setup with tests does not support parallel running. You might want to look into JUnits extensions and extensioncontext to handle that.
- We normally integrate Liquibase verification into the startup code of our app. Just do that as you wire the DI context together.
