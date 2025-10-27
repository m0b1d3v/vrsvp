# VRSVP change log

Most recent entries are first.

## 2025-10-26 (v1.11)

- Updated Java version to JDK 25
- Discord client library updated

## 2024-09-02 (v1.10)

- Updated Java version to JDK 22

## 2024-07-16 (v1.9)

- Updated Discord API dependency from beta to stable
- Removed Discord logging webhook as Fly.io maintains logs
- Removed "About" button on generated polls for simplicity

## 2024-05-26 (v1.8)

- Migrated application to run on [Fly.io](https://fly.io)
- Removed logging output JSON indentation for more information density

## 2024-04-14 (v1.7)

- Application can no longer start without a bot token
- Removed library used for logging format to reduce code dependencies
- Fixed exception when displaying error page to HTTP requests

## 2023-12-08 (v1.6)

- Implemented Spring framework for dependency injection
- Added health check endpoint for monitoring

## 2023-12-01 (v1.5)

- Updated JDK from 17 to 21
- Updated "About" link to point to new domain

## 2023-07-18 (v1.4)

- Events are now limited to 2,000 characters

## 2023-07-08 (v1.3)

- Added ability to grant create/edit permissions to users or roles
- Added emojis to RSVP and edit buttons
- Changed event edit button text to be more clear

## 2023-06-08 (v1.2)

- Fixing Discord cache bug by replacing message embed with plain text

## 2023-06-02

- Changing "About" button to point to updated URL

## 2023-05-27 (v1.1)

- Updating dependencies
- Adding exception monitoring startup message for my peace of mind
- Adding permission denied exception messages when bot not given necessary roles/view
- Adding audit trail logging on deleted or limited RSVP interactions

## 2023-05-26

- Ensuring that signups can not be changed after RSVP is disabled
- Adding step between slash command input and messaging the channel so admins can make layout changes

## 2023-05-21 (v1.0)

- Releasing beta version for limited public usage
- Removing concept of multiple embeds (blocks) in favor of just one embed

## 2023-05-20

- Adding audit trail logging on user events
- Adding "About" button link to button row
- Adding administrator ability to toggle RSVP button on and off
- Adding exception monitoring
- Adding signup limits per slot and/or per person

## 2023-05-19

- Adding administrator edit text input validations
- Adding exception handling for a few edge cases
- Simplifying button interactions
- Replaced embed fields usage with embed descriptions

## 2023-05-18

- Simplifying slash command input validation
- Removing slash command autocomplete functionality
- Adding administrator permission checks
- Adding administrator message edit capabilities

## 2023-05-16

- Adding embed message button interactions for toggling user mentions

## 2023-05-14

- Working out the code for Discord embed formatting for slash command output
- Adding slash command input handling with autocomplete

## 2023-05-13

- Removing Javalin framework and all web server code
- Swapping from shadow JAR to Gradle distributions

## 2023-05-09

- Splitting out HttpServer from Main class
- Adding Discord bot shell setup

## 2023-05-08

- Cleaning up test suite by splitting unit and integration tests

## 2023-05-07

- Adding Makefile for common tasks
- Adding Javalin framework with example route
- Swapping from Maven to Gradle

## 2023-04-23

- Setting up dependency update checks, vulnerability checks, test coverage reports, and SonarCloud scanning
- Fighting editor issues with Maven
- Setting up Git repository
- Confirmed project interest and possible names with Xia
