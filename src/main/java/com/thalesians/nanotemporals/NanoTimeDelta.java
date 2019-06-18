package com.thalesians.nanotemporals;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

/**
 * Represents a time duration, e.g. 1 minute.
 * <p/>
 * Since duration is always defined unambiguously, it is possible to check for equality of and compare durations.
 * <p/>
 * The precision of this class is milliseconds.
 */
public class NanoTimeDelta implements Serializable, Comparable<NanoTimeDelta> {
	private static final long serialVersionUID = -6583506313873589077L;
	
	public static final NanoTimeDelta MAX = new NanoTimeDelta(Long.MAX_VALUE);
	public static final NanoTimeDelta MIN = new NanoTimeDelta(Long.MIN_VALUE);
	public static final NanoTimeDelta ZERO = new NanoTimeDelta(0);
	
	private static final String PARSE_UNIT_REGEX = Joiner.on("|").join(TemporalUnit.ALL_ALIASES);
	private static final String PARSE_CHECK_REGEX = "^(-?\\d+(" + PARSE_UNIT_REGEX + "))+$";
	private static final String PARSE_MATCH_REGEX = "(-?\\d+)(" + PARSE_UNIT_REGEX + "{1}+)";
		
	private final long nanoseconds;

	private NanoTimeDelta(long nanoseconds) {
		this.nanoseconds = nanoseconds;
	}
	
	public static NanoTimeDelta fromNanoseconds(long nanoseconds) {
		return new NanoTimeDelta(nanoseconds);
	}
	
	public static NanoTimeDelta fromMicroseconds(long microseconds) {
		return new NanoTimeDelta(microseconds * TemporalUnit.MICROSECOND.getWholeNanoseconds());
	}
	
	public static NanoTimeDelta fromMilliseconds(long milliseconds) {
		return new NanoTimeDelta(milliseconds * TemporalUnit.MILLISECOND.getWholeNanoseconds());
	}
	
	public static NanoTimeDelta fromSeconds(long seconds) {
		return new NanoTimeDelta(seconds * TemporalUnit.SECOND.getWholeNanoseconds());
	}
	
	public static NanoTimeDelta create(TemporalUnit type, long length) {
		return new Builder().add(type, length).build();
	}
	
	public static NanoTimeDelta parse(String period) throws ParseException {
		period = period.toLowerCase();
		if (!period.matches(PARSE_CHECK_REGEX)) {
			throw new ParseException("Bad period expression " + period, 0);
		}
		Builder b = new Builder();
		Pattern p = Pattern.compile(PARSE_MATCH_REGEX);
		Matcher m = p.matcher(period);
		while (m.find()) {
			int length = Integer.valueOf(m.group(1));
			TemporalUnit type = TemporalUnit.fromString(m.group(2));
			b.add(type, length);
		}
		return b.build();
	}
	
	public long toWholeDays() {
		return nanoseconds / TemporalUnit.DAY.getWholeNanoseconds();
	}
	
	public long toWholeHours() {
		return nanoseconds / TemporalUnit.HOUR.getWholeNanoseconds();
	}
	
	public long toWholeMinutes() {
		return nanoseconds / TemporalUnit.MINUTE.getWholeNanoseconds();
	}
	
	public long toWholeSeconds() {
		return nanoseconds / TemporalUnit.SECOND.getWholeNanoseconds();
	}
	
	public long toWholeMilliseconds() {
		return nanoseconds / TemporalUnit.MILLISECOND.getWholeNanoseconds();
	}
	
	public long toWholeMicroseconds() {
		return nanoseconds / TemporalUnit.MICROSECOND.getWholeNanoseconds();
	}
	
	public long toWholeNanoseconds() {
		return nanoseconds;
	}
	
	public double toDays() {
		return ((double) nanoseconds) / TemporalUnit.DAY.getNanoseconds();
	}
	
	public double toHours() {
		return ((double) nanoseconds) / TemporalUnit.HOUR.getNanoseconds();
	}
	
	public double toMinutes() {
		return ((double) nanoseconds) / TemporalUnit.MINUTE.getNanoseconds();
	}
	
	public double toSeconds() {
		return ((double) nanoseconds) / TemporalUnit.SECOND.getNanoseconds();
	}
	
	public double toMilliseconds() {
		return ((double) nanoseconds) / TemporalUnit.MILLISECOND.getNanoseconds();
	}
	
	public double toMicroseconds() {
		return ((double) nanoseconds) / TemporalUnit.MICROSECOND.getNanoseconds();
	}
	
	public double toNanoseconds() {
		return (double) nanoseconds;
	}
	
	public boolean isPositive() {
		return nanoseconds > 0L;
	}
	
	public boolean isNegative() {
		return nanoseconds < 0L;
	}
	
	@Override public int hashCode() {
		return (int) (nanoseconds ^ (nanoseconds >>> 32));
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NanoTimeDelta)) return false;
		NanoTimeDelta that = (NanoTimeDelta) o;
		return this.nanoseconds == that.nanoseconds;
	}
	
	@Override
	public int compareTo(NanoTimeDelta rhs) {
		return this.nanoseconds == rhs.nanoseconds ? 0 : this.nanoseconds < rhs.nanoseconds ? -1 : 1;
	}
	
	@Override
	public String toString() {
		return nanoseconds + "ns";
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private long nanoseconds = 0L;
		
		public Builder add(TemporalUnit type, long duration) {
			nanoseconds += duration * type.getNanoseconds();
			return this;
		}
		
		public NanoTimeDelta build() {
			return NanoTimeDelta.fromNanoseconds(nanoseconds);
		}
	}	
}
