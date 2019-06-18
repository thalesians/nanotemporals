package com.thalesians.nanotemporals;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents a date incorporating timezone information.
 */
public final class NanoDate implements Comparable<NanoDate>, Serializable {
	
	private static final long serialVersionUID = 6873451660018062436L;
	
	private static final Cache<Long, NanoDate> CACHE = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	private static final NanoDateTimeFormat nanoDateTimeFormat =
			NanoDateTimeFormat.of(NanoDateTimeFormat.DEFAULT_DATE_PATTERN, false);
	
	private final long nanosecondsSinceEpoch;	
	
	private String string = null;
	
	private NanoDate(long nanosecondsSinceEpoch) {
		this.nanosecondsSinceEpoch = nanosecondsSinceEpoch;
	}
	
	public static NanoDate fromNanosecondsSinceEpoch(long nanosecondsSinceEpoch) {
		nanosecondsSinceEpoch = nanosecondsSinceEpoch -
				(nanosecondsSinceEpoch % TemporalUnit.DAY.getWholeNanoseconds());
		NanoDate date = CACHE.getIfPresent(nanosecondsSinceEpoch);
		if (date == null) {
			date = new NanoDate(nanosecondsSinceEpoch);
			CACHE.put(nanosecondsSinceEpoch, date);
		}
		return date;
	}
	
	public static NanoDate fromMillisecondsSinceEpoch(long millisecondsSinceEpoch) {
		return fromNanosecondsSinceEpoch(millisecondsSinceEpoch * TemporalUnit.MILLISECOND.getWholeNanoseconds());
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
		
		public NanoDate build() {
			return TemporalArithmetics.yearMonthDayToDate(timeZoneId, year, month, day);
		}
	}
	
	public static NanoDate parse(String dateString) throws ParseException {
		NanoDateTime nanoDateTime = (NanoDateTime) nanoDateTimeFormat.parseObject(dateString);
		return nanoDateTime == null ? null : nanoDateTime.getDate();
	}
	
	public static NanoDate parse(String timeZoneId, String dateString, String format) throws ParseException {
		NanoDateTimeFormat customFormat = NanoDateTimeFormat.of(timeZoneId, format, false);
		NanoDateTime nanoDateTime = (NanoDateTime) customFormat.parseObject(dateString);
		return nanoDateTime == null ? null : nanoDateTime.getDate();
	}
	
	public long getNanosecondsSinceEpoch() {
		return nanosecondsSinceEpoch;
	}

	public long getMillisecondsSinceEpoch() {
		return nanosecondsSinceEpoch / TemporalUnit.MILLISECOND.getWholeNanoseconds();
	}
	
	public boolean isStrictlyBefore(NanoDate that) {
		return this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch;
	}
	
	public boolean isStrictlyAfter(NanoDate that) {
		return this.nanosecondsSinceEpoch > that.nanosecondsSinceEpoch;
	}
	
	public boolean isBeforeOrEqual(NanoDate that) {
		return this.nanosecondsSinceEpoch <= that.nanosecondsSinceEpoch;
	}
	
	public boolean isAfterOrEqual(NanoDate that) {
		return this.nanosecondsSinceEpoch >= that.nanosecondsSinceEpoch;
	}
	
	@Override
	public int compareTo(NanoDate that) {
		return this.nanosecondsSinceEpoch == that.nanosecondsSinceEpoch
				? 0
				: this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch ? -1 : 1;
	}
	
	@Override
	public int hashCode() {
		return (int) (nanosecondsSinceEpoch ^ (nanosecondsSinceEpoch >>> 32));
	}
	
	@Override
	public boolean equals(Object that) {
		if (NanoDate.class != that.getClass()) return false;
		return this.nanosecondsSinceEpoch == ((NanoDate) that).nanosecondsSinceEpoch;
	}
	
	public String toString(String timeZoneId) {
		return String.format("%04d.%02d.%02d",
				TemporalArithmetics.getYear(timeZoneId, this),
				TemporalArithmetics.getMonth(timeZoneId, this),
				TemporalArithmetics.getDayOfMonth(timeZoneId, this));
	}
	
	@Override
	public String toString() {
		if (string == null) {
			string = toString(TemporalArithmetics.UTC);
		}
		return string;
	}
}
