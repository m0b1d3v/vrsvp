gradle = cd development && ./gradlew

all:
	cat --number Makefile

build:
	$(gradle) assembleBootDist

buildToolUpdate:
	$(gradle) wrapper --gradle-version latest

checkDependencies:
	$(gradle) dependencyUpdates

checkSource: clean test
	$(gradle) sonar

checkVulnerabilities:
	$(gradle) dependencyCheckAnalyze

clean:
	$(gradle) clean

run:
	$(gradle) run

tasks:
	$(gradle) tasks

test:
	$(gradle) test

testI:
	$(gradle) test --tests '*IntegrationTest'

testU:
	$(gradle) test --tests '*UnitTest'

transfer:
	scp build/distributions/*.zip projects:/srv/vrsvp/private/
	ssh projects 'cd /srv/vrsvp/private && unzip *.zip && rm *.zip'
