package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NanoDateTest {
	
	@Test
	public void basicTest() {
		NanoDate date = NanoDate.builder().year(2019).month(5).day(18).build();
		
		assertEquals(2019, TemporalArithmetics.getYear(TemporalArithmetics.UTC, date));
		assertEquals(5, TemporalArithmetics.getMonth(TemporalArithmetics.UTC, date));
		assertEquals(18, TemporalArithmetics.getDayOfMonth(TemporalArithmetics.UTC, date));
		
		NanoDate first = NanoDate.builder().year(2019).month(5).day(1).build();
		assertFalse(date.isStrictlyBefore(first));
		assertTrue(date.isStrictlyAfter(first));
	}
}
