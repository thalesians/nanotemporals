package com.thalesians.nanotemporals;

import java.util.Calendar;
import java.util.TimeZone;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TemporalArithmetics {
	
	public static final String UTC = "UTC";
	
	private TemporalArithmetics() {
		throw new IllegalStateException("This class may not be instantiated");
	}
	
	private static final ThreadLocal<Calendar> utcCalendar = new ThreadLocal<Calendar>() {
		@Override protected Calendar initialValue() {
			return Calendar.getInstance(TimeZone.getTimeZone(UTC));
		}
	};
	
	private static final ThreadLocal<Cache<String, Calendar>> CALENDAR_CACHE =
			new ThreadLocal<Cache<String, Calendar>>() {
		@Override protected Cache<String, Calendar> initialValue() {
			return CacheBuilder.newBuilder()
					.maximumSize(250)
					.build();
		}
	};
	
	private static Calendar getCalendar(String timeZoneId) {
		// The most common case, so we optimise for it:
		Calendar calendar;
		if (timeZoneId.equals(UTC)) {
			calendar = utcCalendar.get();
		} else {
			Cache<String, Calendar> cache = CALENDAR_CACHE.get();
			calendar = cache.getIfPresent(timeZoneId);
			if (calendar == null) {
				calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
				cache.put(timeZoneId, calendar);
			}
		}
		calendar.clear();
		calendar.setLenient(false);
		calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		return calendar;
	}
	
	public static NanoDate yearMonthDayToDate(String timeZoneId, int year, int month, int day) {
		Calendar calendar = getCalendar(timeZoneId);
	    calendar.set(Calendar.YEAR, year);
	    calendar.set(Calendar.MONTH, month - 1);
	    calendar.set(Calendar.DATE, day);
	    calendar.set(Calendar.HOUR, 0);    // Essential, otherwise these fields will be initialised to current time
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);    // Essential, otherwise may get noon
		return NanoDate.fromMillisecondsSinceEpoch(calendar.getTimeInMillis());
	}
	
	public static ZonedNanoDate yearMonthDayToZonedDate(String timeZoneId, int year, int month, int day) {
		Calendar calendar = getCalendar(timeZoneId);
	    calendar.set(Calendar.YEAR, year);
	    calendar.set(Calendar.MONTH, month - 1);
	    calendar.set(Calendar.DATE, day);
	    calendar.set(Calendar.HOUR, 0);    // Essential, otherwise these fields will be initialised to current time
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    calendar.set(Calendar.AM_PM, Calendar.AM);    // Essential, otherwise may get noon
		return ZonedNanoDate.fromMillisecondsSinceEpoch(timeZoneId, calendar.getTimeInMillis());
	}

	public static int getYear(String timeZoneId, NanoDate date) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.YEAR);
	}

	public static int getYear(ZonedNanoDate date) {
		Calendar calendar = getCalendar(date.getTimeZoneId());
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * @return  the month: 1 indicates January, 2 February, etc.
	 */
	public static int getMonth(String timeZoneId, NanoDate date) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * @return  the month: 1 indicates January, 2 February, etc.
	 */
	public static int getMonth(ZonedNanoDate date) {
		Calendar calendar = getCalendar(date.getTimeZoneId());
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static int getDayOfMonth(String timeZoneId, NanoDate date) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.DAY_OF_MONTH);
	}	
	
	public static int getDayOfMonth(ZonedNanoDate date) {
		Calendar calendar = getCalendar(date.getTimeZoneId());
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.DAY_OF_MONTH);
	}	
	
	private static long plusCalendarUnit_(String timeZoneId, NanoDateTime dateTime, int calendarUnit, int quantity) {
		long nanos = dateTime.getNanosecondsSinceEpoch() % TemporalUnit.MILLISECOND.getWholeNanoseconds();
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		calendar.add(calendarUnit, quantity);
		return calendar.getTimeInMillis() * TemporalUnit.MILLISECOND.getWholeNanoseconds() + nanos;	
	}
	
	private static long plusCalendarUnit_(ZonedNanoDateTime dateTime, int calendarUnit, int quantity) {
		long nanos = dateTime.getNanosecondsSinceEpoch() % TemporalUnit.MILLISECOND.getWholeNanoseconds();
		Calendar calendar = getCalendar(dateTime.getTimeZoneId());
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		calendar.add(calendarUnit, quantity);
		return calendar.getTimeInMillis() * TemporalUnit.MILLISECOND.getWholeNanoseconds() + nanos;	
	}
	
	public static NanoDateTime plusCalendarUnit(String timeZoneId, NanoDateTime dateTime, int calendarUnit,
			int quantity) {
		return NanoDateTime.fromNanosecondsSinceEpoch(plusCalendarUnit_(timeZoneId, dateTime, calendarUnit, quantity));	
	}
	
	public static ZonedNanoDateTime plusCalendarUnit(ZonedNanoDateTime dateTime, int calendarUnit,
			int quantity) {
		return ZonedNanoDateTime.fromNanosecondsSinceEpoch(dateTime.getTimeZoneId(),
				plusCalendarUnit_(dateTime, calendarUnit, quantity));	
	}

	public static NanoDateTime plusCalendarUnit(NanoDateTime dateTime, int calendarUnit, int quantity) {
		return plusCalendarUnit(UTC, dateTime, calendarUnit, quantity);
	}
	
	public static NanoDateTime plusTemporalUnit(String timeZoneId, NanoDateTime dateTime, TemporalUnit temporalUnit,
			int quantity) {
		int calendarUnit;
		long newNanos = 0L;
		if (temporalUnit.toCalendarUnit().isPresent()) {
			calendarUnit = temporalUnit.toCalendarUnit().get();
		} else {
			if (temporalUnit.equals(TemporalUnit.MICROSECOND)) {
				calendarUnit = TemporalUnit.MILLISECOND.toCalendarUnit().get();
				newNanos = (quantity % TemporalUnit.MILLISECOND.getWholeMicroseconds()) *
						TemporalUnit.MICROSECOND.getWholeNanoseconds();
				quantity /= TemporalUnit.MILLISECOND.getWholeMicroseconds();
			} else {
				assert temporalUnit.equals(TemporalUnit.NANOSECOND);
				calendarUnit = TemporalUnit.MILLISECOND.toCalendarUnit().get();
				newNanos = quantity % TemporalUnit.MILLISECOND.getWholeNanoseconds();
				quantity /= TemporalUnit.MILLISECOND.getWholeNanoseconds();
			}
		}
		return NanoDateTime.fromNanosecondsSinceEpoch(plusCalendarUnit_(timeZoneId, dateTime, calendarUnit, quantity)
				+ newNanos);	
	}
	
	public static ZonedNanoDateTime plusTemporalUnit(ZonedNanoDateTime dateTime, TemporalUnit temporalUnit,
			int quantity) {
		int calendarUnit;
		long newNanos = 0L;
		if (temporalUnit.toCalendarUnit().isPresent()) {
			calendarUnit = temporalUnit.toCalendarUnit().get();
		} else {
			if (temporalUnit.equals(TemporalUnit.MICROSECOND)) {
				calendarUnit = TemporalUnit.MILLISECOND.toCalendarUnit().get();
				newNanos = (quantity % TemporalUnit.MILLISECOND.getWholeMicroseconds()) *
						TemporalUnit.MICROSECOND.getWholeNanoseconds();
				quantity /= TemporalUnit.MILLISECOND.getWholeMicroseconds();
			} else {
				assert temporalUnit.equals(TemporalUnit.NANOSECOND);
				calendarUnit = TemporalUnit.MILLISECOND.toCalendarUnit().get();
				newNanos = quantity % TemporalUnit.MILLISECOND.getWholeNanoseconds();
				quantity /= TemporalUnit.MILLISECOND.getWholeNanoseconds();
			}
		}
		return ZonedNanoDateTime.fromNanosecondsSinceEpoch(dateTime.getTimeZoneId(),
				plusCalendarUnit_(dateTime, calendarUnit, quantity) + newNanos);	
	}

	public static NanoDateTime plusTemporalUnit(NanoDateTime dateTime, TemporalUnit temporalUnit, int quantity) {
		return plusTemporalUnit(UTC, dateTime, temporalUnit, quantity);
	}
	
	public static NanoDateTime plusDays(String timeZoneId, NanoDateTime dateTime, long days) {
		return plusCalendarUnit(timeZoneId, dateTime, Calendar.DAY_OF_MONTH, (int) days);
	}
	
	public static ZonedNanoDateTime plusDays(ZonedNanoDateTime dateTime, long days) {
		return plusCalendarUnit(dateTime, Calendar.DAY_OF_MONTH, (int) days);
	}
	
	public static NanoDateTime plusDays(NanoDateTime dateTime, long days) {
		return plusDays(UTC, dateTime, days);
	}

	public static NanoDateTime minusDays(String timeZoneId, NanoDateTime dateTime, long days) {
		return plusDays(timeZoneId, dateTime, -days);
	}
	
	public static ZonedNanoDateTime minusDays(ZonedNanoDateTime dateTime, long days) {
		return plusDays(dateTime, -days);
	}
	
	public static NanoDateTime minusDays(NanoDateTime dateTime, long days) {
		return minusDays(UTC, dateTime, days);
	}
	
	public static NanoDateTime plusMonths(String timeZoneId, NanoDateTime dateTime, long months) {
		return plusCalendarUnit(timeZoneId, dateTime, Calendar.MONTH, (int) months);
	}
	
	public static ZonedNanoDateTime plusMonths(ZonedNanoDateTime dateTime, long months) {
		return plusCalendarUnit(dateTime, Calendar.MONTH, (int) months);
	}
	
	public static NanoDateTime plusMonths(NanoDateTime dateTime, long months) {
		return plusMonths(UTC, dateTime, months);
	}

	public static NanoDateTime minusMonths(String timeZoneId, NanoDateTime dateTime, long months) {
		return plusMonths(timeZoneId, dateTime, -months);
	}
	
	public static ZonedNanoDateTime minusMonths(ZonedNanoDateTime dateTime, long months) {
		return plusMonths(dateTime, -months);
	}
	
	public static NanoDateTime minusMonths(NanoDateTime dateTime, long months) {
		return minusMonths(UTC, dateTime, months);
	}

	public static NanoDateTime plusYears(String timeZoneId, NanoDateTime dateTime, long years) {
		return plusCalendarUnit(timeZoneId, dateTime, Calendar.YEAR, (int) years);
	}
	
	public static ZonedNanoDateTime plusYears(ZonedNanoDateTime dateTime, long years) {
		return plusCalendarUnit(dateTime, Calendar.YEAR, (int) years);
	}
	
	public static NanoDateTime plusYears(NanoDateTime dateTime, long years) {
		return plusYears(UTC, dateTime, years);
	}
	
	public static NanoDateTime minusYears(String timeZoneId, NanoDateTime dateTime, long years) {
		return plusYears(timeZoneId, dateTime, -years);
	}
	
	public static ZonedNanoDateTime minusYears(ZonedNanoDateTime dateTime, long years) {
		return plusYears(dateTime, -years);
	}
	
	public static NanoDateTime minusYears(NanoDateTime dateTime, long years) {
		return minusYears(UTC, dateTime, years);
	}
	
	public static NanoDateTime plusSeconds(String timeZoneId, NanoDateTime dateTime, long seconds) {
		return plusCalendarUnit(timeZoneId, dateTime, Calendar.SECOND, (int) seconds);
	}
	
	public static ZonedNanoDateTime plusSeconds(ZonedNanoDateTime dateTime, long seconds) {
		return plusCalendarUnit(dateTime, Calendar.SECOND, (int) seconds);
	}
	
	public static NanoDateTime plusSeconds(NanoDateTime dateTime, long seconds) {
		return plusSeconds(UTC, dateTime, seconds);
	}
	
	public static NanoDateTime minusSeconds(String timeZoneId, NanoDateTime dateTime, long seconds) {
		return plusSeconds(timeZoneId, dateTime, -seconds);
	}
	
	public static ZonedNanoDateTime minusSeconds(ZonedNanoDateTime dateTime, long seconds) {
		return plusSeconds(dateTime, -seconds);
	}
	
	public static NanoDateTime minusSeconds(NanoDateTime dateTime, long seconds) {
		return minusSeconds(UTC, dateTime, seconds);
	}
	
	public static NanoDateTime plusMilliseconds(String timeZoneId, NanoDateTime dateTime, long milliseconds) {
		return plusCalendarUnit(timeZoneId, dateTime, Calendar.MILLISECOND, (int) milliseconds);
	}
	
	public static ZonedNanoDateTime plusMilliseconds(ZonedNanoDateTime dateTime, long milliseconds) {
		return plusCalendarUnit(dateTime, Calendar.MILLISECOND, (int) milliseconds);
	}
	
	public static NanoDateTime plusMilliseconds(NanoDateTime dateTime, long milliseconds) {
		return plusMilliseconds(UTC, dateTime, milliseconds);
	}
	
	public static NanoDateTime minusMilliseconds(String timeZoneId, NanoDateTime dateTime, long milliseconds) {
		return plusMilliseconds(timeZoneId, dateTime, -milliseconds);
	}
	
	public static ZonedNanoDateTime minusMilliseconds(ZonedNanoDateTime dateTime, long milliseconds) {
		return plusMilliseconds(dateTime, -milliseconds);
	}
	
	public static NanoDateTime minusMilliseconds(NanoDateTime dateTime, long milliseconds) {
		return minusMilliseconds(UTC, dateTime, milliseconds);
	}
	
	public static NanoDateTime plusMicroseconds(String timeZoneId, NanoDateTime dateTime, long microseconds) {
		return plusTemporalUnit(timeZoneId, dateTime, TemporalUnit.MICROSECOND, (int) microseconds);
	}
	
	public static ZonedNanoDateTime plusMicroseconds(ZonedNanoDateTime dateTime, long microseconds) {
		return plusTemporalUnit(dateTime, TemporalUnit.MICROSECOND, (int) microseconds);
	}
	
	public static NanoDateTime plusMicroseconds(NanoDateTime dateTime, long microseconds) {
		return plusMicroseconds(UTC, dateTime, microseconds);
	}
	
	public static NanoDateTime minusMicroseconds(String timeZoneId, NanoDateTime dateTime, long microseconds) {
		return plusMicroseconds(timeZoneId, dateTime, -microseconds);
	}
	
	public static ZonedNanoDateTime minusMicroseconds(ZonedNanoDateTime dateTime, long microseconds) {
		return plusMicroseconds(dateTime, -microseconds);
	}
	
	public static NanoDateTime minusMicroseconds(NanoDateTime dateTime, long microseconds) {
		return minusMicroseconds(UTC, dateTime, microseconds);
	}
	
	public static NanoDateTime plusNanoseconds(String timeZoneId, NanoDateTime dateTime, long nanoseconds) {
		return plusTemporalUnit(timeZoneId, dateTime, TemporalUnit.NANOSECOND, (int) nanoseconds);
	}
	
	public static ZonedNanoDateTime plusNanoseconds(ZonedNanoDateTime dateTime, long nanoseconds) {
		return plusTemporalUnit(dateTime, TemporalUnit.NANOSECOND, (int) nanoseconds);
	}
	
	public static NanoDateTime plusNanoseconds(NanoDateTime dateTime, long nanoseconds) {
		return plusNanoseconds(UTC, dateTime, nanoseconds);
	}
	
	public static NanoDateTime minusNanoseconds(String timeZoneId, NanoDateTime dateTime, long nanoseconds) {
		return plusNanoseconds(timeZoneId, dateTime, -nanoseconds);
	}
	
	public static ZonedNanoDateTime minusNanoseconds(ZonedNanoDateTime dateTime, long nanoseconds) {
		return plusNanoseconds(dateTime, -nanoseconds);
	}
	
	public static NanoDateTime minusNanoseconds(NanoDateTime dateTime, long nanoseconds) {
		return minusNanoseconds(UTC, dateTime, nanoseconds);
	}
	
	public static NanoDate plusCalendarUnit(String timeZoneId, NanoDate date, int calendarUnit, int quantity) {
		return plusCalendarUnit(timeZoneId,
				NanoDateTime.fromDateAndTime(date, NanoTime.MIDDAY), calendarUnit, quantity).getDate();
	}

	public static ZonedNanoDate plusCalendarUnit(ZonedNanoDate date, int calendarUnit, int quantity) {
		return plusCalendarUnit(ZonedNanoDateTime.fromDateAndTime(date, NanoTime.MIDDAY),
				calendarUnit, quantity).getDate();
	}

	public static NanoDate plusCalendarUnit(NanoDate date, int calendarUnit, int quantity) {
		return plusCalendarUnit(UTC, date, calendarUnit, quantity);
	}

	public static NanoDate plusTemporalUnit(String timeZoneId, NanoDate date, TemporalUnit temporalUnit, int quantity) {
		return plusTemporalUnit(timeZoneId,
				NanoDateTime.fromDateAndTime(date, NanoTime.MIDDAY), temporalUnit, quantity).getDate();
	}

	public static ZonedNanoDate plusTemporalUnit(ZonedNanoDate date, TemporalUnit temporalUnit, int quantity) {
		return plusTemporalUnit(
				ZonedNanoDateTime.fromDateAndTime(date, NanoTime.MIDDAY), temporalUnit, quantity).getDate();
	}

	public static NanoDate plusTemporalUnit(NanoDate date, TemporalUnit temporalUnit, int quantity) {
		return plusTemporalUnit(UTC, date, temporalUnit, quantity);
	}

	public static NanoDate plusDays(String timeZoneId, NanoDate date, long days) {
		return plusCalendarUnit(timeZoneId, date, Calendar.DAY_OF_MONTH, (int) days);
	}
	
	public static ZonedNanoDate plusDays(ZonedNanoDate date, long days) {
		return plusCalendarUnit(date, Calendar.DAY_OF_MONTH, (int) days);
	}
	
	public static NanoDate plusDays(NanoDate date, long days) {
		return plusDays(UTC, date, days);
	}
	
	public static NanoDate minusDays(String timeZoneId, NanoDate date, long days) {
		return minusDays(timeZoneId, date, days);
	}
	
	public static ZonedNanoDate minusDays(ZonedNanoDate date, long days) {
		return plusDays(date, -days);
	}
	
	public static NanoDate minusDays(NanoDate date, long days) {
		return minusDays(UTC, date, days);
	}
	
	public static NanoDate plusMonths(String timeZoneId, NanoDate date, long months) {
		return plusCalendarUnit(timeZoneId, date, Calendar.MONTH, (int)months);
	}
	
	public static ZonedNanoDate plusMonths(ZonedNanoDate date, long months) {
		return plusCalendarUnit(date, Calendar.MONTH, (int)months);
	}
	
	public static NanoDate plusMonths(NanoDate date, long months) {
		return plusMonths(UTC, date, months);
	}
	
	public static NanoDate minusMonths(String timeZoneId, NanoDate date, long months) {
		return plusMonths(timeZoneId, date, -months);
	}
	
	public static ZonedNanoDate minusMonths(ZonedNanoDate date, long months) {
		return plusMonths(date, -months);
	}
	
	public static NanoDate minusMonths(NanoDate date, long months) {
		return minusMonths(UTC, date, months);
	}
	
	public static NanoDate plusYears(String timeZoneId, NanoDate date, int years) {
		return plusCalendarUnit(timeZoneId, date, Calendar.YEAR, (int)years);
	}
	
	public static ZonedNanoDate plusYears(ZonedNanoDate date, int years) {
		return plusCalendarUnit(date, Calendar.YEAR, (int)years);
	}
	
	public static NanoDate plusYears(NanoDate date, int years) {
		return plusYears(UTC, date, years);
	}
	
	public static NanoDate minusYears(String timeZoneId, NanoDate date, int years) {
		return plusYears(timeZoneId, date, -years);
	}
	
	public static ZonedNanoDate minusYears(ZonedNanoDate date, int years) {
		return plusYears(date, -years);
	}
	
	public static NanoDate minusYears(NanoDate date, int years) {
		return minusYears(UTC, date, years);
	}
	
	public static NanoTime plusTemporalUnit(NanoTime time, TemporalUnit temporalUnit, long quantity) {
		return NanoTime.fromNanosecondsSinceMidnight(
				time.getNanosecondsSinceMidnight() + quantity * temporalUnit.getWholeNanoseconds());
	}
	
	public static NanoTime plusCalendarUnit(NanoTime time, int calendarUnit, long quantity) {
		switch (calendarUnit) {
		case Calendar.HOUR:
			return plusTemporalUnit(time, TemporalUnit.HOUR, quantity);
		case Calendar.MINUTE:
			return plusTemporalUnit(time, TemporalUnit.MINUTE, quantity);
		case Calendar.SECOND:
			return plusTemporalUnit(time, TemporalUnit.SECOND, quantity);
		case Calendar.MILLISECOND:
			return plusTemporalUnit(time, TemporalUnit.MILLISECOND, quantity);
		default:
			throw new IllegalArgumentException("Unsupported calendar unit: " + calendarUnit);
		}
	}

	public static NanoTime plusSeconds(NanoTime time, long seconds) {
		return plusTemporalUnit(time, TemporalUnit.SECOND, seconds);
	}
	
	public static NanoTime minusSeconds(NanoTime time, long seconds) {
		return plusSeconds(time, -seconds);
	}
	
	public static NanoTime plusMilliseconds(NanoTime time, long milliseconds) {
		return plusTemporalUnit(time, TemporalUnit.MILLISECOND, milliseconds);
	}
	
	public static NanoTime minusMilliseconds(NanoTime time, long milliseconds) {
		return plusMilliseconds(time, -milliseconds);
	}
	
	public static NanoTime plusMicroseconds(NanoTime time, long microseconds) {
		return plusTemporalUnit(time, TemporalUnit.MICROSECOND, microseconds);
	}
	
	public static NanoTime minusMicroseconds(NanoTime time, long microseconds) {
		return plusMicroseconds(time, -microseconds);
	}
	
	public static NanoTime plusNanoseconds(NanoTime time, long nanoseconds) {
		return plusTemporalUnit(time, TemporalUnit.NANOSECOND, nanoseconds);
	}
	
	public static NanoTime minusNanoseconds(NanoTime time, long nanoseconds) {
		return plusNanoseconds(time, -nanoseconds);
	}
	
	public static NanoTimeDelta difference(NanoDateTime start, NanoDateTime end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}
	
	public static NanoTimeDelta difference(ZonedNanoDateTime start, ZonedNanoDateTime end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}
	
	public static NanoTimeDelta difference(ZonedNanoDateTime start, NanoDateTime end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}
	
	public static NanoTimeDelta difference(NanoDateTime start, ZonedNanoDateTime end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}
	
	public static NanoTimeDelta difference(NanoDate start, NanoDate end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}
	
	public static NanoTimeDelta difference(ZonedNanoDate start, ZonedNanoDate end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}

	public static NanoTimeDelta difference(ZonedNanoDate start, NanoDate end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}

	public static NanoTimeDelta difference(NanoDate start, ZonedNanoDate end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceEpoch() - start.getNanosecondsSinceEpoch());
	}

	public static NanoTimeDelta difference(NanoTime start, NanoTime end) {
		return NanoTimeDelta.fromNanoseconds(end.getNanosecondsSinceMidnight() - start.getNanosecondsSinceMidnight());
	}

	public static NanoDateTime plusDuration(String timeZoneId, NanoDateTime start, NanoTimeDelta duration) {
		return duration == NanoTimeDelta.MAX ? NanoDateTime.MAX :
			(duration == NanoTimeDelta.MIN ? NanoDateTime.MIN :
				plusNanoseconds(timeZoneId, start, duration.toWholeNanoseconds()));
	}
	
	public static ZonedNanoDateTime plusDuration(String timeZoneId, ZonedNanoDateTime start, NanoTimeDelta duration) {
		return duration == NanoTimeDelta.MAX ? ZonedNanoDateTime.MAX :
			(duration == NanoTimeDelta.MIN ? ZonedNanoDateTime.MIN :
				plusNanoseconds(start, duration.toWholeNanoseconds()));
	}
	
	public static NanoDateTime plusDuration(NanoDateTime start, NanoTimeDelta duration) {
		return plusDuration(UTC, start, duration);
	}
	
	public static NanoDateTime plusDuration(ZonedNanoDateTime start, NanoTimeDelta duration) {
		return plusDuration(start, duration);
	}
	
	public static NanoDateTime minusDuration(String timeZoneId, NanoDateTime start, NanoTimeDelta duration) {
		return duration == NanoTimeDelta.MAX ? NanoDateTime.MIN :
			(duration == NanoTimeDelta.MIN ? NanoDateTime.MAX :
				minusNanoseconds(timeZoneId, start, duration.toWholeMilliseconds()));
	}
	
	public static ZonedNanoDateTime minusDuration(ZonedNanoDateTime start, NanoTimeDelta duration) {
		return duration == NanoTimeDelta.MAX ? ZonedNanoDateTime.MIN :
			(duration == NanoTimeDelta.MIN ? ZonedNanoDateTime.MAX :
				minusNanoseconds(start, duration.toWholeMilliseconds()));
	}
	
	public static NanoDateTime minusDuration(NanoDateTime start, NanoTimeDelta duration) {
		return minusDuration(UTC, start, duration);
	}
	
	public static java.util.Date toJavaUtilDate(String timeZoneId, NanoDateTime dateTime) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		return calendar.getTime();
	}
	
	public static java.util.Date toJavaUtilDate(ZonedNanoDateTime dateTime) {
		Calendar calendar = getCalendar(dateTime.getTimeZoneId());
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		return calendar.getTime();
	}
	
	public static java.util.Date toJavaUtilDate(NanoDateTime dateTime) {
		return toJavaUtilDate(UTC, dateTime);
	}
	
	@SuppressWarnings("deprecation")
	public static java.sql.Date toJavaSqlDate(String timeZoneId, NanoDateTime dateTime) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		return new java.sql.Date(calendar.get(Calendar.YEAR) - 1900,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@SuppressWarnings("deprecation")
	public static java.sql.Date toJavaSqlDate(ZonedNanoDateTime dateTime) {
		Calendar calendar = getCalendar(dateTime.getTimeZoneId());
		calendar.setTimeInMillis(dateTime.getMillisecondsSinceEpoch());
		return new java.sql.Date(calendar.get(Calendar.YEAR) - 1900,
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static java.sql.Time toJavaSqlTime(String timeZoneId, NanoTime time) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(time.getMillisecondsSinceMidnight());
		long ms = calendar.get(Calendar.MILLISECOND)
				+ 1000 * (calendar.get(Calendar.SECOND)
				+ 60 * (calendar.get(Calendar.MINUTE)
				+ 60 * calendar.get(Calendar.HOUR_OF_DAY)));
		return new java.sql.Time(ms);
	}
	
	public static java.sql.Timestamp toJavaSqlTimestamp(NanoDateTime dateTime) {
		return new java.sql.Timestamp(dateTime.getMillisecondsSinceEpoch());
	}
	
	public static java.sql.Timestamp toJavaSqlTimestamp(ZonedNanoDateTime dateTime) {
		return new java.sql.Timestamp(dateTime.getMillisecondsSinceEpoch());
	}

	public static NanoDateTime toNanoDateTime(java.sql.Date date, java.sql.Time time) {
		return NanoDateTime.fromMillisecondsSinceEpoch(date.getTime() + time.getTime());
	}
	
	public static ZonedNanoDateTime toNanoDateTime(String timeZoneId, java.sql.Date date, java.sql.Time time) {
		return ZonedNanoDateTime.fromMillisecondsSinceEpoch(timeZoneId, date.getTime() + time.getTime());
	}

	public static NanoDateTime toNanoDateTimeForUTCEquivalentOf(java.util.Date javaUtilDate) {
		return NanoDateTime.fromMillisecondsSinceEpoch(javaUtilDate.getTime());
	}
	
	public static ZonedNanoDateTime toZonedNanoDateTimeForUTCEquivalentOf(String timeZoneId,
			java.util.Date javaUtilDate) {
		return ZonedNanoDateTime.fromMillisecondsSinceEpoch(timeZoneId, javaUtilDate.getTime());
	}
	
	public static NanoDateTime toNanoDateTime(java.sql.Timestamp timestamp) {
		return NanoDateTime.fromMillisecondsSinceEpoch(timestamp.getTime());
	}
	
	public static ZonedNanoDateTime toZonedNanoDateTime(String timeZoneId, java.sql.Timestamp timestamp) {
		return ZonedNanoDateTime.fromMillisecondsSinceEpoch(timeZoneId, timestamp.getTime());
	}

	@SuppressWarnings("deprecation")
	public static NanoDate toNanoDate(java.util.Date date) {
		return NanoDate.builder().year(date.getYear() + 1900).month(date.getMonth() + 1).day(date.getDate()).build();
	}

	@SuppressWarnings("deprecation")
	public static ZonedNanoDate toZonedNanoDate(String timeZoneId, java.util.Date date) {
		return ZonedNanoDate.builder()
				.setTimeZoneId(timeZoneId)
				.year(date.getYear() + 1900).month(date.getMonth() + 1).day(date.getDate())
				.build();
	}

	@SuppressWarnings("deprecation")
	public static NanoDate toNanoDate(java.sql.Date date) {
		return NanoDate.builder()
				.year(date.getYear() + 1900).month(date.getMonth() + 1).day(date.getDate())
				.build();
	}
	
	@SuppressWarnings("deprecation")
	public static ZonedNanoDate toZonedNanoDate(String timeZoneId, java.sql.Date date) {
		return ZonedNanoDate.builder()
				.setTimeZoneId(timeZoneId)
				.year(date.getYear() + 1900).month(date.getMonth() + 1).day(date.getDate())
				.build();
	}

	@SuppressWarnings("deprecation")
	public static NanoTime toNanoTime(java.sql.Time time) {
		return NanoTime.builder()
				.hour(time.getHours()).minute(time.getMinutes()).second(time.getSeconds())
				.build();
	}
	
	public static int getDayOfWeek(String timeZoneId, NanoDate date) {
		Calendar calendar = getCalendar(timeZoneId);
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeek(NanoDate date) {
		return getDayOfWeek(UTC, date);
	}

	public static int getDayOfWeek(ZonedNanoDate date) {
		Calendar calendar = getCalendar(date.getTimeZoneId());
		calendar.setTimeInMillis(date.getMillisecondsSinceEpoch());
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
}
