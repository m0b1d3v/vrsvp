all:
	cat Makefile

clean:
	./gradlew clean

jar:
	./gradlew shadowJar

javadoc:
	./gradlew javadoc

run:
	./gradlew run

tasks:
	./gradlew tasks

test:
	./gradlew test

testI:
	./gradlew test --tests '*IntegrationTest'

testU:
	./gradlew test --tests '*UnitTest'

owasp:
	./gradlew dependencyCheckAnalyze

sonar: clean test
	./gradlew sonar

updates:
	./gradlew dependencyUpdates
