package com.thalesians.nanotemporals.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Splitter;
import com.thalesians.nanotemporals.NanoDateTimeFormat;
import com.thalesians.nanotemporals.TemporalArithmetics;
import com.thalesians.nanotemporals.ZonedNanoDate;

public class AbstractBusinessCalendar implements BusinessCalendar {

	private final String timeZoneId;
	private final LinkedHashMap<ZonedNanoDate, Holiday> holidays;
	private ZonedNanoDate firstDate = null;
	private ZonedNanoDate lastDate = null;

	public AbstractBusinessCalendar(String timeZoneId) throws IOException, ParseException {
		this.timeZoneId = timeZoneId;
		holidays = loadHolidays(timeZoneId);
	}
	
	private LinkedHashMap<ZonedNanoDate, Holiday> loadHolidays(String timeZoneId) throws IOException, ParseException {
		String fileName = timeZoneId.replaceAll("\\/", "-");
		URL resource = getClass().getClassLoader().getResource("business-calendar/" + fileName + ".csv");
		if (resource == null) {
			throw new IllegalArgumentException(
					"No business calendar information for time zone id '" + timeZoneId + "'");
		}
		File file = new File(resource.getFile());
		FileReader reader = new FileReader(file);
		LinkedHashMap<ZonedNanoDate, Holiday> holidays = new LinkedHashMap<ZonedNanoDate, Holiday>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(reader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				List<String> substrings = Splitter.on("|").trimResults().splitToList(line);
				ZonedNanoDate date = ZonedNanoDate.parse(timeZoneId, substrings.get(0), NanoDateTimeFormat.DEFAULT_DATE_PATTERN);
				HolidayType type = HolidayType.valueOf(substrings.get(1));
				String name = substrings.get(2);
				holidays.put(date, new Holiday() {
					@Override
					public HolidayType getHolidayType() {
						return type;
					}
					
					@Override
					public String getHolidayName() {
						return name;
					}
				});
			}
		} finally {
			if (bufferedReader != null) bufferedReader.close();
		}		
		return holidays;
	}
	
	private void checkHolidaysNotEmpty() {
		if (holidays.isEmpty()) {
			throw new IllegalStateException("No holidays have been loaded");
		}		
	}
	
	private void checkDate(ZonedNanoDate date) {
		// TODO The following check doesn't work as "London" somehow changes into "GMT"
//		if (!date.getTimeZoneId().equals(timeZoneId)) {
//			throw new IllegalArgumentException("The given date has an incorrect time zone: expected = " + timeZoneId + ", actual = " + date.getTimeZoneId());
//		}
		if (!date.isAfterOrEqual(firstDay()) && date.isBeforeOrEqual(lastDay())) {
			throw new IllegalArgumentException("The given date is outside the range for this business calendar");
		}
	}
	
	@Override
	public ZonedNanoDate firstDay() {
		if (firstDate == null) {
			checkHolidaysNotEmpty();
			firstDate = holidays.entrySet().iterator().next().getKey();
		}
		return firstDate;
	}

	@Override
	public ZonedNanoDate lastDay() {
		if (lastDate == null) {
			checkHolidaysNotEmpty();
			ZonedNanoDate date = null;
			Iterator<Entry<ZonedNanoDate, Holiday>> iterator = holidays.entrySet().iterator();
			while (holidays.entrySet().iterator().hasNext()) {
				date = iterator.next().getKey();
			}
			lastDate = date;
		}
		return lastDate;
	}

	@Override
	public boolean isHoliday(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		return holidays.keySet().contains(date);
	}

	@Override
	public Holiday getHoliday(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		return holidays.get(date);
	}

	@Override
	public ZonedNanoDate prevHoliday(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.minusDays(date, 1);
		} while (!isHoliday(date));
		return date;
	}

	@Override
	public ZonedNanoDate nextHoliday(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.plusDays(date, 1);
		} while (!isHoliday(date));
		return date;
	}

	@Override
	public boolean isWeekendDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		int dayOfWeek = TemporalArithmetics.getDayOfWeek(date);
		return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
	}

	@Override
	public ZonedNanoDate prevWeekendDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.minusDays(date, 1);
		} while (!isWeekendDay(date));
		return date;
	}

	@Override
	public ZonedNanoDate nextWeekendDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.plusDays(date, 1);
		} while (!isWeekendDay(date));
		return date;
	}

	@Override
	public boolean isBusinessDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		return !(isHoliday(date) || isWeekendDay(date));
	}

	@Override
	public ZonedNanoDate prevBusinessDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.minusDays(date, 1);
		} while (!isBusinessDay(date));
		return date;
	}

	@Override
	public ZonedNanoDate nextBusinessDay(ZonedNanoDate date) {
		checkHolidaysNotEmpty();
		checkDate(date);
		do {
			date = TemporalArithmetics.plusDays(date, 1);
		} while (!isBusinessDay(date));
		return date;
	}

	@Override
	public ZonedNanoDate plusBusinessDays(ZonedNanoDate date, int quantity) {
		int count = 0;
		int increment = quantity >= 0 ? 1 : -1;
		while (count < quantity) {
			date = TemporalArithmetics.plusDays(date, 1);
			if (isBusinessDay(date)) count += increment;
		}
		return date;
	}

	@Override
	public ZonedNanoDate minusBusinessDays(ZonedNanoDate date, int quantity) {
		return plusBusinessDays(date, -quantity);
	}
}
