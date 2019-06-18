package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import com.thalesians.nanotemporals.business.BusinessCalendar;
import com.thalesians.nanotemporals.business.SimpleBusinessCalendar;

public class SimpleBusinessCalendarTest {

	@Test
	public void basicTest() throws IOException, ParseException {
		String timeZoneId = "London";
		BusinessCalendar businessCalendar = SimpleBusinessCalendar.create(timeZoneId);
		
		ZonedNanoDate someDate = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(5).day(31).build();

		ZonedNanoDate expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(5).day(30).build();
		ZonedNanoDate actual = businessCalendar.prevBusinessDay(someDate);
		assertEquals(expected, actual);

		expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(6).day(3).build();
		actual = businessCalendar.nextBusinessDay(someDate);
		assertEquals(expected, actual);

		expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(5).day(26).build();
		actual = businessCalendar.prevWeekendDay(someDate);
		assertEquals(expected, actual);

		expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(6).day(1).build();
		actual = businessCalendar.nextWeekendDay(someDate);
		assertEquals(expected, actual);

		expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(5).day(27).build();
		actual = businessCalendar.prevHoliday(someDate);
		assertEquals(expected, actual);

		expected = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(8).day(26).build();
		actual = businessCalendar.nextHoliday(someDate);
		assertEquals(expected, actual);
	}	
}
