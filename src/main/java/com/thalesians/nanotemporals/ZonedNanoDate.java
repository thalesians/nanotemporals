package com.thalesians.nanotemporals;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents a date.
 */
public final class ZonedNanoDate implements Comparable<ZonedNanoDate>, Serializable {
	
	private static final long serialVersionUID = 6873451660018062436L;
	
	private static final Cache<Long, ZonedNanoDate> CACHE = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	private final String timeZoneId;
	private final long nanosecondsSinceEpoch;	
	
	private String string = null;
	
	private ZonedNanoDate(String timeZoneId, long nanosecondsSinceEpoch) {
		this.timeZoneId = timeZoneId;
		this.nanosecondsSinceEpoch = nanosecondsSinceEpoch;
	}
	
	public static ZonedNanoDate fromNanosecondsSinceEpoch(String timeZoneId, long nanosecondsSinceEpoch) {
		nanosecondsSinceEpoch = nanosecondsSinceEpoch -
				(nanosecondsSinceEpoch % TemporalUnit.DAY.getWholeNanoseconds());
		ZonedNanoDate date = CACHE.getIfPresent(nanosecondsSinceEpoch);
		if (date == null) {
			date = new ZonedNanoDate(timeZoneId, nanosecondsSinceEpoch);
			CACHE.put(nanosecondsSinceEpoch, date);
		}
		return date;
	}
	
	public static ZonedNanoDate fromMillisecondsSinceEpoch(String timeZoneId, long millisecondsSinceEpoch) {
		return fromNanosecondsSinceEpoch(timeZoneId,
				millisecondsSinceEpoch * TemporalUnit.MILLISECOND.getWholeNanoseconds());
	}
	
	public static ZonedNanoDate fromDate(String timeZoneId, NanoDate date) {
		return fromNanosecondsSinceEpoch(timeZoneId, date.getNanosecondsSinceEpoch());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private String timeZoneId = TemporalArithmetics.UTC;
		private int year;
		private int month = 1;
		private int day = 1;
		
		private Builder() {
		}
		
		public Builder setTimeZoneId(String timeZoneId) {
			this.timeZoneId = timeZoneId;
			return this;
		}
		
		public Builder year(int year) {
			this.year = year;
			return this;
		}
		
		public Builder month(int month) {
			this.month = month;
			return this;
		}
		
		public Builder day(int day) {
			this.day = day;
			return this;
		}
		
		public ZonedNanoDate build() {
			return TemporalArithmetics.yearMonthDayToZonedDate(timeZoneId, year, month, day);
		}
	}
	
	public static ZonedNanoDate parse(String timeZoneId, String dateString, String format) throws ParseException {
		NanoDateTimeFormat customFormat = NanoDateTimeFormat.of(timeZoneId, format, true);
		ZonedNanoDateTime zonedNanoDateTime = (ZonedNanoDateTime) customFormat.parseObject(dateString);
		return zonedNanoDateTime == null ? null : zonedNanoDateTime.getDate();
	}
	
	public static ZonedNanoDate parse(String timeZoneId, String dateString) throws ParseException {
		return parse(timeZoneId, dateString, NanoDateTimeFormat.DEFAULT_DATE_PATTERN);
	}
	
	public String getTimeZoneId() {
		return timeZoneId;
	}
	
	public long getNanosecondsSinceEpoch() {
		return nanosecondsSinceEpoch;
	}

	public long getMillisecondsSinceEpoch() {
		return nanosecondsSinceEpoch / TemporalUnit.MILLISECOND.getWholeNanoseconds();
	}
	
	public boolean isStrictlyBefore(ZonedNanoDate that) {
		return this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch;
	}
	
	public boolean isStrictlyAfter(ZonedNanoDate that) {
		return this.nanosecondsSinceEpoch > that.nanosecondsSinceEpoch;
	}
	
	public boolean isBeforeOrEqual(ZonedNanoDate that) {
		return this.nanosecondsSinceEpoch <= that.nanosecondsSinceEpoch;
	}
	
	public boolean isAfterOrEqual(ZonedNanoDate that) {
		return this.nanosecondsSinceEpoch >= that.nanosecondsSinceEpoch;
	}
	
	@Override
	public int compareTo(ZonedNanoDate that) {
		return this.nanosecondsSinceEpoch == that.nanosecondsSinceEpoch
				? 0
				: this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch ? -1 : 1;
	}
	
	@Override
	public int hashCode() {
		return (int) (nanosecondsSinceEpoch ^ (nanosecondsSinceEpoch >>> 32));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (ZonedNanoDate.class != obj.getClass()) return false;
		return this.nanosecondsSinceEpoch == ((ZonedNanoDate) obj).nanosecondsSinceEpoch;
	}
	
	public String toString(String timeZoneId) {
		return String.format("%04d.%02d.%02d",
				TemporalArithmetics.getYear(this),
				TemporalArithmetics.getMonth(this),
				TemporalArithmetics.getDayOfMonth(this));
	}
	
	@Override
	public String toString() {
		if (string == null) {
			string = toString(TemporalArithmetics.UTC);
		}
		return string;
	}
}
