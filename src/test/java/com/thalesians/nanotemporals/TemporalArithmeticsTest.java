package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TemporalArithmeticsTest {
	
	@Test
	public void dateTimeTest() {
		NanoDateTime datetime = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).millisecond(357).build());

		NanoDateTime expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(48).second(6).millisecond(357).build());
		assertEquals(expected, TemporalArithmetics.minusSeconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).build());
		assertEquals(expected, TemporalArithmetics.minusMilliseconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).microsecond(356643).build());
		assertEquals(expected, TemporalArithmetics.minusMicroseconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).nanosecond(356999643).build());
		assertEquals(expected, TemporalArithmetics.minusNanoseconds(datetime, 357));

		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(17).millisecond(357).build());
		assertEquals(expected, TemporalArithmetics.plusSeconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).millisecond(714).build());
		assertEquals(expected, TemporalArithmetics.plusMilliseconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).microsecond(357357).build());
		assertEquals(expected, TemporalArithmetics.plusMicroseconds(datetime, 357));
		expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).nanosecond(357000357).build());
		assertEquals(expected, TemporalArithmetics.plusNanoseconds(datetime, 357));

	}
	
	@Test
	public void timeTest() {
		NanoTime time = NanoTime.builder().hour(21).minute(32).second(49).millisecond(195).build();

		assertEquals(
				NanoTime.builder().hour(21).minute(26).second(52).millisecond(195).build(),
				TemporalArithmetics.minusSeconds(time, 357L));
		assertEquals(
				NanoTime.builder().hour(21).minute(38).second(46).millisecond(195).build(),
				TemporalArithmetics.plusSeconds(time, 357L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(48).millisecond(838).build(),
				TemporalArithmetics.minusMilliseconds(time, 357L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(49).millisecond(552).build(),
				TemporalArithmetics.plusMilliseconds(time, 357L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(48).microsecond(837247).build(),
				TemporalArithmetics.minusMicroseconds(time, 357753L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(49).microsecond(552753).build(),
				TemporalArithmetics.plusMicroseconds(time, 357753L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(48).nanosecond(837246643).build(),
				TemporalArithmetics.minusNanoseconds(time, 357753357L));
		assertEquals(
				NanoTime.builder().hour(21).minute(32).second(49).nanosecond(552753357).build(),
				TemporalArithmetics.plusNanoseconds(time, 357753357L));
	}

}
