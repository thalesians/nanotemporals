package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import com.thalesians.nanotemporals.NanoDate;
import com.thalesians.nanotemporals.NanoDateTime;
import com.thalesians.nanotemporals.NanoTime;

public class NanoDateTimeTest {
	
	@Test
	public void basicTest() {
		NanoDateTime datetime = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).millisecond(357).build());
		
		NanoDate date = datetime.getDate();
		assertEquals(2018, TemporalArithmetics.getYear(TemporalArithmetics.UTC, date));
		assertEquals(1, TemporalArithmetics.getMonth(TemporalArithmetics.UTC, date));
		assertEquals(31, TemporalArithmetics.getDayOfMonth(TemporalArithmetics.UTC, date));
		
		NanoTime time = datetime.getTime();
		assertEquals(16, time.getHour());
		assertEquals(54, time.getMinute());
		assertEquals(3, time.getSecond());
		assertEquals(357, time.getMillisecond());
		
		assertEquals(1517417643357L, datetime.getMillisecondsSinceEpoch());
		assertEquals(1517417643357000000L, datetime.getNanosecondsSinceEpoch());
		
		NanoDateTime first = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(1).build(),
				NanoTime.builder().hour(16).minute(54).second(3).millisecond(357).build());
		
		assertFalse(datetime.isStrictlyBefore(first));
		assertTrue(datetime.isStrictlyAfter(first));
	}
	
	@Test
	public void parseTest() throws ParseException {
		NanoDateTime expected = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2018).month(1).day(31).build(),
				NanoTime.builder().hour(16).minute(54).second(3).millisecond(357).build());
		NanoDateTime actual = NanoDateTime.parse("2018.01.31T16:54:03.357");
		assertEquals(expected, actual);
	}
}
