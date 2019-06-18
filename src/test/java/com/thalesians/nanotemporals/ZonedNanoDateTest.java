package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ZonedNanoDateTest {
	@Test
	public void basicTest() {
		// NB Using "London", not "Europe/London" as per https://stackoverflow.com/questions/12499318/how-do-i-get-british-summertime-offset-bst-in-java
		String timeZoneId = "London";
		ZonedNanoDate actual = ZonedNanoDate.builder().setTimeZoneId(timeZoneId).year(2019).month(5).day(31).build();
		assertEquals("2019.05.31", actual.toString());
	}
}
