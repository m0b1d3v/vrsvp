gradle = cd development && ./gradlew
read = cat --number

ifeq ($(OS), Windows_NT)
	# Overwrite default Linux tasks for Windows users
	gradle = cd development && gradlew.bat
	read = type
endif

all:
	$(read) Makefile

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

deploy:
	fly deploy \
		--local-only \
		--config deployment/fly.toml \
		--dockerfile deployment/Dockerfile \
		--ignorefile .dockerignore

logs:
	fly logs -a vrsvp
