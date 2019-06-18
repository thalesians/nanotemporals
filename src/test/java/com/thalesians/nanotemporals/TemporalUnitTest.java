package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TemporalUnitTest {
	
	@Test
	public void compareToTest() {
		assertTrue(TemporalUnit.DAY.compareTo(TemporalUnit.HOUR) > 0);
		assertTrue(TemporalUnit.HOUR.compareTo(TemporalUnit.MINUTE) > 0);
		assertTrue(TemporalUnit.MINUTE.compareTo(TemporalUnit.SECOND) > 0);
		assertTrue(TemporalUnit.SECOND.compareTo(TemporalUnit.MILLISECOND) > 0);
		assertTrue(TemporalUnit.MILLISECOND.compareTo(TemporalUnit.MICROSECOND) > 0);
		assertTrue(TemporalUnit.MICROSECOND.compareTo(TemporalUnit.NANOSECOND) > 0);
	}
	
	@Test
	public void toStringTest() {
		assertEquals("NANOSECOND", TemporalUnit.NANOSECOND.toString());
		assertEquals("MICROSECOND", TemporalUnit.MICROSECOND.toString());
		assertEquals("MILLISECOND", TemporalUnit.MILLISECOND.toString());
		assertEquals("SECOND", TemporalUnit.SECOND.toString());
		assertEquals("HOUR", TemporalUnit.HOUR.toString());
		assertEquals("MINUTE", TemporalUnit.MINUTE.toString());
		assertEquals("DAY", TemporalUnit.DAY.toString());
	}
}
