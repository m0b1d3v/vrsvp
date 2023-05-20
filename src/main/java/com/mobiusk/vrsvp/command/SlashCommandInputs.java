package com.mobiusk.vrsvp.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SlashCommandInputs {

    private Integer blocks;
    private Integer durationInMinutes;
    private Integer slots;
    private Integer startTimestamp;

}
