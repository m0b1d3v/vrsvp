all:
	cat Makefile

build:
	./gradlew assembleDist

clean:
	./gradlew clean

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

transfer:
	scp build/distributions/*.zip projects:/srv/vrsvp/private/
