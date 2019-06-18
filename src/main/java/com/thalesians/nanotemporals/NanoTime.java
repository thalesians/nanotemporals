package com.thalesians.nanotemporals;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents a time that does not incorporate the timezone information.
 * <p/>
 * The precision of this class is milliseconds.
 */
public final class NanoTime implements Comparable<NanoTime>, Serializable {
	private static final long serialVersionUID = -7141551799514771196L;
	
	private static final Cache<Long, NanoTime> CACHE = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();

	public static final NanoTime MIDNIGHT = new NanoTime(0L);
	public static final NanoTime MIDDAY = new NanoTime(TemporalUnit.DAY.getWholeNanoseconds() / 2);
	public static final NanoTime MIDNIGHT_NEXT_DAY = new NanoTime(TemporalUnit.DAY.getWholeNanoseconds());
	
	private static final NanoDateTimeFormat nanoDateTimeFormat
			= NanoDateTimeFormat.of(NanoDateTimeFormat.DEFAULT_TIME_PATTERN, false);
	
	private final long nanosecondsSinceMidnight;
	
	private String string = null;
	
	private NanoTime(long nanosecondsSinceMidnight) {
		this.nanosecondsSinceMidnight = nanosecondsSinceMidnight;
	}
	
	public static NanoTime fromNanosecondsSinceMidnight(long nanosecondsSinceMidnight) {
		NanoTime time = CACHE.getIfPresent(nanosecondsSinceMidnight);
		if (time == null) {
			time = new NanoTime(nanosecondsSinceMidnight);
			CACHE.put(nanosecondsSinceMidnight, time);
		}
		return time;
	}
	
	public static NanoTime fromMillisecondsSinceMidnight(long millisecondsSinceMidnight) {
		return fromNanosecondsSinceMidnight(millisecondsSinceMidnight * TemporalUnit.MILLISECOND.getWholeNanoseconds());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private long nanosecondsSinceMidnight = 0L;
		
		private Builder() {
		}
		
		public Builder hour(int hour) {
			nanosecondsSinceMidnight += TemporalUnit.HOUR.getWholeNanoseconds() * hour;
			return this;
		}
		
		public Builder minute(int minute) {
			nanosecondsSinceMidnight += TemporalUnit.MINUTE.getWholeNanoseconds() * minute;
			return this;
		}
		
		public Builder second(int second) {
			nanosecondsSinceMidnight += TemporalUnit.SECOND.getWholeNanoseconds() * second;
			return this;
		}
		
		public Builder millisecond(int millisecond) {
			nanosecondsSinceMidnight += TemporalUnit.MILLISECOND.getWholeNanoseconds() * millisecond;
			return this;
		}
		
		public Builder microsecond(int microsecond) {
			nanosecondsSinceMidnight += TemporalUnit.MICROSECOND.getWholeNanoseconds() * microsecond;
			return this;
		}
		
		public Builder nanosecond(int nanosecond) {
			nanosecondsSinceMidnight += nanosecond;
			return this;
		}
		
		public NanoTime build() {
			return new NanoTime(nanosecondsSinceMidnight);
		}
	}
	
	public static NanoTime parse(String timeString) throws ParseException {
		NanoDateTime nanoDateTime = (NanoDateTime) nanoDateTimeFormat.parseObject(timeString);
		return nanoDateTime == null ? null : nanoDateTime.getTime();
	}
	
	public static NanoTime parse(String timeZoneId, String timeString, String format) throws ParseException {
		NanoDateTimeFormat customFormat = NanoDateTimeFormat.of(timeZoneId, format, false);
		NanoDateTime nanoDateTime = (NanoDateTime) customFormat.parseObject(timeString);
		return nanoDateTime == null ? null : nanoDateTime.getTime();
	}
	
	public long getNanosecondsSinceMidnight() {
		return nanosecondsSinceMidnight;
	}

	public long getMillisecondsSinceMidnight() {
		return nanosecondsSinceMidnight / TemporalUnit.MILLISECOND.getWholeNanoseconds();
	}

	public int getHour() {
		return (int) (nanosecondsSinceMidnight / TemporalUnit.HOUR.getWholeNanoseconds());
	}
	
	public int getMinute() {
		return (int) ((nanosecondsSinceMidnight / TemporalUnit.MINUTE.getWholeNanoseconds())
				% TemporalUnit.HOUR.getWholeMinutes());
	}
	
	public int getSecond() {
		return (int) ((nanosecondsSinceMidnight / TemporalUnit.SECOND.getWholeNanoseconds())
				% TemporalUnit.MINUTE.getWholeSeconds());
	}
	
	public int getMillisecond() {
		return (int) ((nanosecondsSinceMidnight / TemporalUnit.MILLISECOND.getWholeNanoseconds())
				% TemporalUnit.SECOND.getWholeMilliseconds());
	}
	
	public int getNanosecond() {
		return (int) (nanosecondsSinceMidnight % TemporalUnit.SECOND.getWholeNanoseconds());
	}
	
	public boolean isStrictlyBefore(NanoTime that) {
		return this.nanosecondsSinceMidnight < that.nanosecondsSinceMidnight;
	}
	
	public boolean isStrictlyAfter(NanoTime that) {
		return this.nanosecondsSinceMidnight > that.nanosecondsSinceMidnight;
	}
	
	public boolean isBeforeOrEqual(NanoTime that) {
		return this.nanosecondsSinceMidnight <= that.nanosecondsSinceMidnight;
	}
	
	public boolean isAfterOrEqual(NanoTime that) {
		return this.nanosecondsSinceMidnight >= that.nanosecondsSinceMidnight;
	}
	
	@Override public int compareTo(NanoTime that) {
		return this.nanosecondsSinceMidnight == that.nanosecondsSinceMidnight
				? 0
				: this.nanosecondsSinceMidnight < that.nanosecondsSinceMidnight ? -1 : 1;
	}
	
	@Override public int hashCode() {
		return (int) (nanosecondsSinceMidnight ^ (nanosecondsSinceMidnight >>> 32));
	}
	
	@Override public boolean equals(Object obj) {
		if (NanoTime.class != obj.getClass()) return false;
		NanoTime that = ((NanoTime) obj);
		return this.nanosecondsSinceMidnight == that.nanosecondsSinceMidnight;
	}
	
	@Override public String toString() {
		if (string == null) {
			string = String.format("%02d:%02d:%02d.%09d", getHour(), getMinute(), getSecond(), getNanosecond());
		}
		return string;
	}	
}
