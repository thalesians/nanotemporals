package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class NanoDateTimeConverterTest {

	@Test
	public void testJavaUtilDateToNanoDateTime_UTC() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    calendar.set(Calendar.YEAR, 2019);
	    calendar.set(Calendar.MONTH, Calendar.MAY);
	    calendar.set(Calendar.DATE, 21);
	    calendar.set(Calendar.HOUR, 0);    // Essential, otherwise these fields will be initialised to current time
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);    // Essential, otherwise may get noon of 2019.05.21
	    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		java.util.Date javaUtilDate = calendar.getTime();
		NanoDateTime nanoDateTime = TemporalArithmetics.toNanoDateTimeForUTCEquivalentOf(javaUtilDate);
		assertEquals("2019.05.21T00:00:00.000000000", nanoDateTime.toString());
	}
	
	@Test
	public void testJavaUtilDateToNanoDateTime_London() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	    calendar.set(Calendar.YEAR, 2019);
	    calendar.set(Calendar.MONTH, Calendar.MAY);
	    calendar.set(Calendar.DATE, 21);
	    calendar.set(Calendar.HOUR, 0);    // Essential, otherwise these fields will be initialised to current time
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);    // Essential, otherwise may get noon of 2019.05.21
	    calendar.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		java.util.Date javaUtilDate = calendar.getTime();
		NanoDateTime nanoDateTime = TemporalArithmetics.toNanoDateTimeForUTCEquivalentOf(javaUtilDate);
		assertEquals("2019.05.20T23:00:00.000000000", nanoDateTime.toString());
	}
}
