package com.thalesians.nanotemporals;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents a datetime that does not incorporate timezone information.
 * <p/>
 * The precision of this class is milliseconds.
 */
public final class NanoDateTime implements Comparable<NanoDateTime>, Serializable {
	private static final long serialVersionUID = 924444546156605079L;

	private static final Cache<Long, NanoDateTime> CACHE = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	private static final long MIN_NANOSECONDS_SINCE_EPOCH = -2208988800000000000L;
	private static final long MAX_NANOSECONDS_SINCE_EPOCH = 9223372036854775807L;
	
	public static final NanoDateTime MIN = NanoDateTime.fromNanosecondsSinceEpoch(MIN_NANOSECONDS_SINCE_EPOCH);  // 1900.01.01T00:00:00.000000000;
	public static final NanoDateTime MAX = NanoDateTime.fromNanosecondsSinceEpoch(MAX_NANOSECONDS_SINCE_EPOCH);  // 2262.04.11T23:47:16.854775807;
	
	private static NanoDateTimeFormat nanoDateTimeFormat =
			NanoDateTimeFormat.of(TemporalArithmetics.UTC, NanoDateTimeFormat.DEFAULT_DATETIME_PATTERN, false);
	
	private final long nanosecondsSinceEpoch;
	
	private String string = null;
	
	private NanoDateTime(long nanosecondsSinceEpoch) {
		this.nanosecondsSinceEpoch = nanosecondsSinceEpoch;
	}
	
	public static NanoDateTime fromNanosecondsSinceEpoch(long nanosecondsSinceEpoch) {
		checkArgument(MIN_NANOSECONDS_SINCE_EPOCH <= nanosecondsSinceEpoch && nanosecondsSinceEpoch <= MAX_NANOSECONDS_SINCE_EPOCH);
		NanoDateTime dt = CACHE.getIfPresent(nanosecondsSinceEpoch);
		if (dt == null) {
			dt = new NanoDateTime(nanosecondsSinceEpoch);
			CACHE.put(nanosecondsSinceEpoch, dt);
		}
		return dt;
	}
	
	public static NanoDateTime fromMillisecondsSinceEpoch(long millisecondsSinceEpoch) {
		return fromNanosecondsSinceEpoch(millisecondsSinceEpoch * TemporalUnit.MILLISECOND.getWholeNanoseconds());
	}
	
	public static NanoDateTime fromDateAndTime(NanoDate date, NanoTime time) {
		return fromNanosecondsSinceEpoch(date.getNanosecondsSinceEpoch() + time.getNanosecondsSinceMidnight());
	}
	
	public static NanoDateTime parse(String dateString) throws ParseException {
		return (NanoDateTime) nanoDateTimeFormat.parseObject(dateString);
	}
	
	public static NanoDateTime parse(String timeZoneId, String dateString, String format) throws ParseException {
		NanoDateTimeFormat customFormat = NanoDateTimeFormat.of(timeZoneId, format, false);
		return (NanoDateTime) customFormat.parseObject(dateString);
	}
	
	public long getNanosecondsSinceEpoch() {
		return nanosecondsSinceEpoch;
	}
	
	public long getMillisecondsSinceEpoch() {
		return nanosecondsSinceEpoch / TemporalUnit.MILLISECOND.getWholeNanoseconds();
	}
	
	public NanoDate getDate() {
		return NanoDate.fromNanosecondsSinceEpoch(nanosecondsSinceEpoch);
	}
	
	public NanoTime getTime() {
		return NanoTime.fromNanosecondsSinceMidnight(nanosecondsSinceEpoch % TemporalUnit.DAY.getWholeNanoseconds());
	}
	
	public boolean isStrictlyBefore(NanoDateTime that) {
		return this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch;
	}
	
	public boolean isStrictlyAfter(NanoDateTime that) {
		return this.nanosecondsSinceEpoch > that.nanosecondsSinceEpoch;
	}
	
	public boolean isBeforeOrEqual(NanoDateTime that) {
		return this.nanosecondsSinceEpoch <= that.nanosecondsSinceEpoch;
	}
	
	public boolean isAfterOrEqual(NanoDateTime that) {
		return this.nanosecondsSinceEpoch >= that.nanosecondsSinceEpoch;
	}
	
	@Override public int compareTo(NanoDateTime that) {
		return this.nanosecondsSinceEpoch == that.nanosecondsSinceEpoch
				? 0
				: this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch ? -1 : 1;
	}
	
	@Override public int hashCode() {
		return (int) (nanosecondsSinceEpoch ^ (nanosecondsSinceEpoch >>> 32));
	}
	
	@Override public boolean equals(Object that) {
		if (NanoDateTime.class == that.getClass()) {
			return this.nanosecondsSinceEpoch == ((NanoDateTime) that).nanosecondsSinceEpoch;
		}
		return false;
	}
	
	public String toString(String timeZoneId) {
		return String.format("%04d.%02d.%02dT%02d:%02d:%02d.%09d",
				TemporalArithmetics.getYear(timeZoneId, getDate()),
				TemporalArithmetics.getMonth(timeZoneId, getDate()),
				TemporalArithmetics.getDayOfMonth(timeZoneId, getDate()),
				getTime().getHour(), getTime().getMinute(), getTime().getSecond(), getTime().getNanosecond());
	}

	@Override public String toString() {
		if (string == null) {
			string = toString(TemporalArithmetics.UTC);
		}
		return string;
	}
}
