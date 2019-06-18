package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;

import org.junit.Test;

import com.thalesians.nanotemporals.NanoTime;

public class NanoTimeTest {
	
	@Test
	public void basicTest() {
		NanoTime time = NanoTime.builder().hour(21).minute(32).second(49).millisecond(195).build();
		assertEquals(21, time.getHour());
		assertEquals(32, time.getMinute());
		assertEquals(49, time.getSecond());
		assertEquals(195, time.getMillisecond());
		assertEquals(77569195, time.getMillisecondsSinceMidnight());
		
		NanoTime midday = NanoTime.builder().hour(12).build();
		assertFalse(time.isStrictlyBefore(midday));
		assertTrue(time.isStrictlyAfter(midday));
		assertFalse(time.isBeforeOrEqual(midday));
		assertTrue(time.isAfterOrEqual(midday));
		
		assertTrue(time.compareTo(midday) > 0);
		
		assertEquals("21:32:49.195000000", time.toString());
		
		assertEquals(77569195L, time.getMillisecondsSinceMidnight());
		assertEquals(77569195000000L, time.getNanosecondsSinceMidnight());
	}

	@Test
	public void formatterTest() {
		SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
		NanoTime time = NanoTime.builder().hour(21).minute(32).second(49).millisecond(195).build();
		System.out.println(timeFormatter.format(time.getMillisecondsSinceMidnight()));
	}
}
