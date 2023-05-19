package com.mobiusk.vrsvp.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SlashCommandInputs {

    private int blocks;
    private int slots;
    private int durationInMinutes;
    private int startTimestamp;

}
