all:
	cat Makefile

clean:
	./gradlew clean

javadoc:
	./gradlew javadoc

test:
	./gradlew test

owasp:
	./gradlew dependencyCheckAnalyze

sonar: clean test
	./gradlew sonar

updates:
	./gradlew dependencyUpdates
