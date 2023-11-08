# VRSVP

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/overall?id=mobiusk_vrsvp)

## About

This is a Discord bot for a virtual RSVP system for Discord-based communities.
Server administrators or permitted individuals can create RSVP signups, edit event descriptions, and set signup limits.
Server members can then RSVP for certain times of the event.

A photo gallery of examples can be seen [here](https://adobe.ly/3spl8He).

## Adding to your server

The bot can be added to your server with this [OAuth link](https://discord.com/api/oauth2/authorize?client_id=1109977583522689066&permissions=0&scope=applications.commands%20bot).
It can also be self-hosted using the source code here.

## RSVP command options

These are used to generate the RSVP form within the Discord client, starting with `/vrsvp`.

- `start`: [HammerTime](https://hammertime.cyou/) timestamp
- `slots`: Timestamp slot count
- `duration`: Timestamp slot length in minutes
- `rsvp-limit-per-person`: Optional, a person cannot sign up for more slots than this
- `rsvp-limit-per-slot`: Optional, a slot cannot have more people signed up than this

## RSVP form editing

The event description can be edited from within Discord by administrators or through permission grants.
This allows correcting timestamps, giving more information, re-arranging individuals, and more.
It is helpful to be aware of [Discord text formatting](https://support.discord.com/hc/en-us/articles/210298617-Markdown-Text-101-Chat-Formatting-Bold-Italic-Underline-).

These lines in event descriptions set RSVP limits:
- `Maximum number of people that can RSVP for a single slot: (limit)`
- `Maximum number of slots a person can RSVP for: (limit)`

Any line that starts with this is a slot: `> #`.

## Bot permissions

By default, only server administrators can create or edit events.
Other individuals can be granted these powers through the Discord Privilege system.
To access this go to Server Settings > Integrations > VRSVP > Manage > Commands > /vrsvp.

A photo gallery walking through these steps can be seen [here](https://adobe.ly/3splfCE).

## Discord limitations

Don't forget to add the VRSVP bot to the channels you wish to use it in.
It needs to be treated the same as anyone added to said channels to RSVP in it.

Discord also limits us to event descriptions of up to 2,000 characters and 25 timestamp slots.
These limits can be avoided by splitting very large/long events into multiple VRSVP forms.
It can also help to put extra event information in a separate message and only use VRSVP for signups.
Generally between 50 and 75 signups can be handled per form.
If you need more, please get in contact with me and we'll work something out.

## Extra information

Project updates can be found in the [change log](README-CHANGELOG.md).
