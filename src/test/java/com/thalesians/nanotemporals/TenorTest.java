package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

public class TenorTest {
	@Test
	public void parseTest1() throws ParseException {
		Tenor expectedTenor = Tenor.THREE_YEARS;
		Tenor parsedTenor = Tenor.parse("3Y");
		assertEquals(expectedTenor, parsedTenor);
	}
	
	@Test
	public void parseTest2() throws ParseException {
		Tenor expectedTenor = Tenor.create(Tenor.Unit.MONTH, -3);
		Tenor parsedTenor = Tenor.parse("-3m");
		assertEquals(expectedTenor, parsedTenor);
	}
	
	@Test
	public void parseTest3() throws ParseException {
		Tenor expectedTenor = Tenor.create(Tenor.Unit.WEEK, 5);
		Tenor parsedTenor = Tenor.parse("5w");
		assertEquals(expectedTenor, parsedTenor);
	}
	
	@Test
	public void parseTest4() throws ParseException {
		Tenor expectedTenor = Tenor.builder()
				.set(Tenor.Unit.YEAR, 1)
				.set(Tenor.Unit.MONTH, 2)
				.set(Tenor.Unit.WEEK, 3)
				.set(Tenor.Unit.BUSINESS_DAY, -4)
				.build();

		Tenor parsedTenor = Tenor.parse("1y2m3W-4b");
		assertEquals(1, parsedTenor.getUnitLength(Tenor.Unit.YEAR));
		assertEquals(2, parsedTenor.getUnitLength(Tenor.Unit.MONTH));
		assertEquals(3, parsedTenor.getUnitLength(Tenor.Unit.WEEK));
		assertEquals(-4, parsedTenor.getUnitLength(Tenor.Unit.BUSINESS_DAY));
		assertEquals(0, parsedTenor.getUnitLength(Tenor.Unit.DAY));
		
		assertEquals(expectedTenor, parsedTenor);
	}
}
