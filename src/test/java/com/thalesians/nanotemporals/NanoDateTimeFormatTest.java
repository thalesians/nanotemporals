package com.thalesians.nanotemporals;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import com.thalesians.nanotemporals.NanoDate;
import com.thalesians.nanotemporals.NanoDateTimeFormat;

public class NanoDateTimeFormatTest {
	
	@Test
	public void formatDateTimeTest1() {
		NanoDateTime dateTime = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2019).month(5).day(21).build(),
				NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build());
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyy.MM.dd'T'HH:mm:ss.SSS", false);
		assertEquals("2019.05.21T18:45:07.345", format.format(dateTime));
	}
	
	@Test
	public void parseDateTimeTest1() throws ParseException {
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyy.MM.dd'T'HH:mm:ss.SSS", false);
		assertEquals("2019.05.21T18:45:07.345000000", format.parseObject("2019.05.21T18:45:07.345").toString());
	}
	
	@Test
	public void formatDateTimeTest2() {
		NanoDateTime dateTime = NanoDateTime.fromDateAndTime(
				NanoDate.builder().year(2019).month(5).day(21).build(),
				NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build());
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyyMMdd'T'HH:mm:ss.SSS", false);
		assertEquals("20190521T18:45:07.345", format.format(dateTime));
	}
	
	@Test
	public void parseDateTimeTest2() throws ParseException {
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyyMMdd'T'HH:mm:ss.SSS", false);
		assertEquals("2019.05.21T18:45:07.345000000", format.parseObject("20190521T18:45:07.345").toString());
	}
	
	@Test
	public void formatDateTest1() {
		NanoDate date = NanoDate.builder().year(2019).month(5).day(21).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyy.MM.dd", false);
		assertEquals("2019.05.21", format.format(date));
	}
	
	@Test
	public void parseDateTest1() throws ParseException {
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyy.MM.dd", false);
		assertEquals("2019.05.21T00:00:00.000000000", format.parseObject("2019.05.21").toString());
	}

	@Test
	public void formatDateTest2() {
		NanoDate date = NanoDate.builder().year(2019).month(5).day(21).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyyMMdd", false);
		assertEquals("20190521", format.format(date));
	}
	
	@Test
	public void parseDateTest2() throws ParseException {
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "yyyyMMdd", false);
		assertEquals("2019.05.21T00:00:00.000000000", format.parseObject("20190521").toString());
	}

	@Test
	public void formatTimeTest1() {
		NanoTime time = NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.SSS", false);
		assertEquals("18:45:07.345", format.format(time));
	}

	@Test
	public void parseTimeTest1() throws ParseException {
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.SSS", false);
		assertEquals("1970.01.01T18:45:07.345000000", format.parseObject("18:45:07.345").toString());
	}

	@Test
	public void formatTimeTest2() {
		NanoTime time = NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.NNNNNNNNN", false);
		assertEquals("18:45:07.345920678", format.format(time));
	}
	
	// TODO Learn to parse datetimes and times with nanoseconds

//	@Test
//	public void parseTimeTest2() throws ParseException {
//		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.NNNNNNNNN");
//		assertEquals("1970.01.01T18:45:07.345920678", format.parseObject("18:45:07.345920678").toString());
//	}

	@Test
	public void formatTimeTest3() {
		NanoTime time = NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.NNNNNNNNN.'NNNNNNNNN'.NNNNNNNNN", false);
		assertEquals("18:45:07.345920678.NNNNNNNNN.345920678", format.format(time));
	}

	@Test
	public void formatTimeTest4() {
		NanoTime time = NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.CCCCCC", false);
		assertEquals("18:45:07.345920", format.format(time));
	}

	@Test
	public void formatTimeTest5() {
		NanoTime time = NanoTime.builder().hour(18).minute(45).second(7).nanosecond(345920678).build();
		NanoDateTimeFormat format = NanoDateTimeFormat.of(TemporalArithmetics.UTC, "HH:mm:ss.CCCCCC.'CCCCCC'.NNNNNNNNN.'NNNNNNNNN'", false);
		assertEquals("18:45:07.345920.CCCCCC.345920678.NNNNNNNNN", format.format(time));
	}
}
