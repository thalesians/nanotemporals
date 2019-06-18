package com.thalesians.nanotemporals;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Represents a datetime that does incorporate timezone information.
 * <p/>
 * The precision of this class is milliseconds.
 */
public final class ZonedNanoDateTime implements Comparable<ZonedNanoDateTime>, Serializable {
	private static final long serialVersionUID = 924444546156605079L;

	private static final Cache<Long, ZonedNanoDateTime> CACHE = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();
	
	private static final long MIN_NANOSECONDS_SINCE_EPOCH = -2208988800000000000L;
	private static final long MAX_NANOSECONDS_SINCE_EPOCH = 9223372036854775807L;
	
	public static final ZonedNanoDateTime MIN =
			ZonedNanoDateTime.fromNanosecondsSinceEpoch(TemporalArithmetics.UTC, MIN_NANOSECONDS_SINCE_EPOCH); // 1900.01.01T00:00:00.000000000;
	public static final ZonedNanoDateTime MAX =
			ZonedNanoDateTime.fromNanosecondsSinceEpoch(TemporalArithmetics.UTC, MAX_NANOSECONDS_SINCE_EPOCH); // 2262.04.11T23:47:16.854775807;
	
	private final String timeZoneId;
	private final long nanosecondsSinceEpoch;
	
	private String string = null;
	
	private ZonedNanoDateTime(String timeZoneId, long nanosecondsSinceEpoch) {
		this.timeZoneId = timeZoneId;
		this.nanosecondsSinceEpoch = nanosecondsSinceEpoch;
	}
	
	public static ZonedNanoDateTime fromNanosecondsSinceEpoch(String timeZoneId, long nanosecondsSinceEpoch) {
		checkArgument(MIN_NANOSECONDS_SINCE_EPOCH <= nanosecondsSinceEpoch &&
				nanosecondsSinceEpoch <= MAX_NANOSECONDS_SINCE_EPOCH);
		ZonedNanoDateTime dt = CACHE.getIfPresent(nanosecondsSinceEpoch);
		if (dt == null) {
			dt = new ZonedNanoDateTime(timeZoneId, nanosecondsSinceEpoch);
			CACHE.put(nanosecondsSinceEpoch, dt);
		}
		return dt;
	}
	
	public static ZonedNanoDateTime fromMillisecondsSinceEpoch(String timeZoneId, long millisecondsSinceEpoch) {
		return fromNanosecondsSinceEpoch(timeZoneId, millisecondsSinceEpoch * TemporalUnit.MILLISECOND.getWholeNanoseconds());
	}
	
	public static ZonedNanoDateTime fromDateAndTime(String timeZoneId, NanoDate date, NanoTime time) {
		return fromNanosecondsSinceEpoch(timeZoneId,
				date.getNanosecondsSinceEpoch() + time.getNanosecondsSinceMidnight());
	}

	public static ZonedNanoDateTime fromDateAndTime(ZonedNanoDate date, NanoTime time) {
		return fromNanosecondsSinceEpoch(date.getTimeZoneId(),
				date.getNanosecondsSinceEpoch() + time.getNanosecondsSinceMidnight());
	}
	
	public static ZonedNanoDateTime fromDateTime(String timeZoneId, NanoDateTime dateTime) {
		return fromNanosecondsSinceEpoch(timeZoneId, dateTime.getNanosecondsSinceEpoch());
	}
	
	public static ZonedNanoDateTime parse(String timeZoneId, String dateString, String format) throws ParseException {
		NanoDateTimeFormat customFormat = NanoDateTimeFormat.of(timeZoneId, format, true);
		return (ZonedNanoDateTime) customFormat.parseObject(dateString);
	}
	
	public static ZonedNanoDateTime parse(String timeZoneId, String dateString) throws ParseException {
		return parse(timeZoneId, dateString, NanoDateTimeFormat.DEFAULT_DATETIME_PATTERN);
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
	
	public ZonedNanoDate getDate() {
		return ZonedNanoDate.fromNanosecondsSinceEpoch(timeZoneId, nanosecondsSinceEpoch);
	}
	
	public NanoTime getTime() {
		return NanoTime.fromNanosecondsSinceMidnight(nanosecondsSinceEpoch % TemporalUnit.DAY.getWholeNanoseconds());
	}
	
	public NanoDateTime toDateTime() {
		return NanoDateTime.fromNanosecondsSinceEpoch(nanosecondsSinceEpoch);
	}
	
	public boolean isStrictlyBefore(ZonedNanoDateTime that) {
		return this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch;
	}
	
	public boolean isStrictlyAfter(ZonedNanoDateTime that) {
		return this.nanosecondsSinceEpoch > that.nanosecondsSinceEpoch;
	}
	
	public boolean isBeforeOrEqual(ZonedNanoDateTime that) {
		return this.nanosecondsSinceEpoch <= that.nanosecondsSinceEpoch;
	}
	
	public boolean isAfterOrEqual(ZonedNanoDateTime that) {
		return this.nanosecondsSinceEpoch >= that.nanosecondsSinceEpoch;
	}
	
	@Override public int compareTo(ZonedNanoDateTime that) {
		return this.nanosecondsSinceEpoch == that.nanosecondsSinceEpoch
				? 0
				: this.nanosecondsSinceEpoch < that.nanosecondsSinceEpoch ? -1 : 1;
	}
	
	@Override public int hashCode() {
		return (int) (nanosecondsSinceEpoch ^ (nanosecondsSinceEpoch >>> 32));
	}
	
	@Override public boolean equals(Object that) {
		if (ZonedNanoDateTime.class == that.getClass()) {
			return this.timeZoneId.equals(((ZonedNanoDateTime) that).timeZoneId) &&
					this.nanosecondsSinceEpoch == ((ZonedNanoDateTime) that).nanosecondsSinceEpoch;
		}
		return false;
	}
	
	public String toString(String timeZoneId) {
		return String.format("%04d.%02d.%02dT%02d:%02d:%02d.%09d",
				TemporalArithmetics.getYear(getDate()),
				TemporalArithmetics.getMonth(getDate()),
				TemporalArithmetics.getDayOfMonth(getDate()),
				getTime().getHour(), getTime().getMinute(), getTime().getSecond(), getTime().getNanosecond());
	}

	@Override public String toString() {
		if (string == null) {
			string = toString(TemporalArithmetics.UTC);
		}
		return string;
	}
}
