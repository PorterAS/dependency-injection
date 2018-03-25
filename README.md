This is a small application acting as an illustration to a blog post. It uses:

- Kotlin as the language
- Spark Framework to serve HTTP requests
- JUnit 5 for testing

I'll update repo with link to article when I publish.

# Building and running

To build a ShadowJAR as a single binary distributable do:

	./gradlew build shadowJar

Then to run the application:

	java -jar ./build/libs/di-app.jar

