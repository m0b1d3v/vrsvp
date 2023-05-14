package com.mobiusk.vrsvp.input;

import com.mobiusk.vrsvp.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscordBotInputsValidationUnitTest extends TestBase {

	private final DiscordBotInputs inputs = new DiscordBotInputs();

	@BeforeEach
	public void beforeEach() {
		inputs.setBlocks(1);
		inputs.setSlots(1);
		inputs.setDurationInMinutes(1);
		inputs.setStartTimestamp(1);
	}

	@Test
	void utilityClass() throws NoSuchMethodException {
		assertUtilityClass(DiscordBotInputsValidation.class);
	}

	// Blocks

	@Test
	void blocksMaximum() {
		inputs.setBlocks(DiscordBotInputsValidation.BLOCKS_MAXIMUM + 1);
		assertValidation("The maximum amount of blocks allowed in VRSVP is 9 due to a Discord limitation, as 1 is reserved for event information. Please retry this command with a smaller block count, or split your RSVP into more than one form.");
	}

	@Test
	void blocksMinimum() {
		inputs.setBlocks(0);
		assertValidation("The minimum amount of blocks required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger block count.");
	}

	@Test
	void blocksValid() {

		inputs.setBlocks(DiscordBotInputsValidation.BLOCKS_MAXIMUM);
		assertValidation("");

		inputs.setBlocks(1);
		assertValidation("");
	}

	// Duration

	@Test
	void durationMaximum() {
		inputs.setDurationInMinutes(DiscordBotInputsValidation.TIMESTAMP_MAXIMUM + 1);
		assertValidation("The maximum duration in minutes for each slot in VRSVP is equal to one week. This is a mostly-arbitrary decision on our part to guard from any programming errors. If you have a legitimate need for this edge case, we are willing to adjust it. Please retry this command with a smaller duration.");
	}

	@Test
	void durationMinimum() {
		inputs.setDurationInMinutes(0);
		assertValidation("The minimum duration in minutes for each slot in VRSVP is one minute. Please retry this command with a larger duration.");
	}

	@Test
	void durationValid() {

		inputs.setDurationInMinutes(DiscordBotInputsValidation.DURATION_IN_MINUTES_MAXIMUM);
		assertValidation("");

		inputs.setDurationInMinutes(1);
		assertValidation("");
	}

	// Slots

	@Test
	void slotsMaximum() {

		var expectedMessage = "The maximum amount of (blocks * slots) allowed in VRSVP is 25 due to a Discord limitation. Please retry this command with a smaller total block/slot count, or split your RSVP into more than one form.";

		inputs.setSlots(DiscordBotInputsValidation.BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM + 1);
		assertValidation(expectedMessage);

		inputs.setBlocks(2);
		inputs.setSlots(13);
		assertValidation(expectedMessage);
	}

	@Test
	void slotsMinimum() {
		inputs.setSlots(0);
		assertValidation("The minimum amount of slots required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger slots count.");
	}

	@Test
	void slotsValid() {

		inputs.setSlots(DiscordBotInputsValidation.BLOCKS_AND_SLOTS_PRODUCT_MAXIMUM);
		assertValidation("");

		inputs.setBlocks(5);
		inputs.setSlots(5);
		assertValidation("");
	}

	// Timestamp

	@Test
	void startTimestampMaximum() {
		inputs.setStartTimestamp(DiscordBotInputsValidation.TIMESTAMP_MAXIMUM + 1);
		assertValidation("The maximum start timestamp in VRSVP equates to 2035-01-01. This is a mostly-arbitrary decision on our part to guard from any programming errors. If you have a legitimate need for this edge case, we are willing to adjust it. Please retry this command with a smaller start timestamp.");
	}

	@Test
	void startTimestampMinimum() {
		inputs.setStartTimestamp(-1);
		assertValidation("The minimum start timestamp in VRSVP is 0, which equates to 1970-01-01. This is to ensure compatibility with Discord timestamp formatting. Please retry this command with a larger start timestamp.");
	}

	@Test
	void startTimestampValid() {

		inputs.setStartTimestamp(DiscordBotInputsValidation.TIMESTAMP_MAXIMUM);
		assertValidation("");

		inputs.setStartTimestamp(1);
		assertValidation("");
	}

	// Multiple validations stitching

	@Test
	void validationErrorsCanBeCombined() {

		inputs.setBlocks(0);
		inputs.setSlots(0);

		assertValidation("""
			The minimum amount of blocks required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger block count.
			The minimum amount of slots required in VRSVP is 1, otherwise there is nothing to RSVP for. Please retry this command with a larger slots count.""");
	}

	// Test utility method(s)

	private void assertValidation(String expectation) {
		assertEquals(expectation, DiscordBotInputsValidation.buildValidationErrorMessage(inputs));
	}

}
