all:
	cat Makefile

build:
	./gradlew assembleBootDist

buildToolUpdate:
	./gradlew wrapper --gradle-version latest

checkSource: clean test
	./gradlew sonar

checkUpdates:
	./gradlew dependencyUpdates

checkVulnerabilities:
	./gradlew dependencyCheckAnalyze

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

transfer:
	scp build/distributions/*.zip projects:/srv/vrsvp/private/
	ssh projects 'cd /srv/vrsvp/private && unzip *.zip && rm *.zip'
