package com.thalesians.nanotemporals;

import java.util.Calendar;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public enum TemporalUnit implements Comparable<TemporalUnit> {
	NANOSECOND(1L, Optional.absent(), ImmutableList.of("ns", "nanos", "nanosecond", "nanoseconds")),
	MICROSECOND(1000L, Optional.absent(), ImmutableList.of("us", "micros", "microsecond", "microseconds")),
	MILLISECOND(1000000L, Optional.of(Calendar.MILLISECOND), ImmutableList.of("ms", "millis", "millisecond", "milliseconds")),
	SECOND(1000000000L, Optional.of(Calendar.SECOND), ImmutableList.of("s", "sec", "second", "seconds")),
	MINUTE(60000000000L, Optional.of(Calendar.MINUTE), ImmutableList.of("m", "min", "minute", "minutes")),
	HOUR(3600000000000L, Optional.of(Calendar.HOUR), ImmutableList.of("h", "hour", "hours")),
	DAY(86400000000000L, Optional.of(Calendar.DAY_OF_MONTH), ImmutableList.of("d", "day", "days"));
	
	private final long nanoseconds;
	private final ImmutableList<String> aliases;
	private final Optional<Integer> calendarUnit;
	private NanoTimeDelta nanoTimeDelta = null;
	
	// The ordering of the aliases is important here to ensure "ms" matches before "m":
	public final static ImmutableList<String> ALL_ALIASES = ImmutableList.<String>builder()
			.addAll(NANOSECOND.aliases)
			.addAll(MICROSECOND.aliases)
			.addAll(MILLISECOND.aliases)
			.addAll(SECOND.aliases)
			.addAll(MINUTE.aliases)
			.addAll(HOUR.aliases)
			.addAll(DAY.aliases)
			.build();

	private TemporalUnit(long nanoseconds, Optional<Integer> calendarUnit, ImmutableList<String> aliases) {
		this.nanoseconds = nanoseconds;
		this.calendarUnit = calendarUnit;
		this.aliases = aliases;
	}
	
	public static TemporalUnit fromString(String unit) {
		unit = unit.trim().toLowerCase();
		if (DAY.aliases.contains(unit)) return TemporalUnit.DAY;
		else if (HOUR.aliases.contains(unit)) return TemporalUnit.HOUR;
		else if (MINUTE.aliases.contains(unit)) return TemporalUnit.MINUTE;
		else if (SECOND.aliases.contains(unit)) return TemporalUnit.SECOND;
		else if (MILLISECOND.aliases.contains(unit)) return TemporalUnit.MILLISECOND;
		else if (MICROSECOND.aliases.contains(unit)) return TemporalUnit.MICROSECOND;
		else if (NANOSECOND.aliases.contains(unit)) return TemporalUnit.NANOSECOND;
		throw new IllegalStateException("Illegal type");    // Should never get here
	}

	public long getWholeNanoseconds() {
		return nanoseconds;
	}
	
	public long getWholeMicroseconds() {
		return nanoseconds / MICROSECOND.getWholeNanoseconds();
	}
	
	public long getWholeMilliseconds() {
		return nanoseconds / MILLISECOND.getWholeNanoseconds();
	}
	
	public long getWholeSeconds() {
		return nanoseconds / SECOND.getWholeNanoseconds();
	}
	
	public long getWholeMinutes() {
		return nanoseconds / MINUTE.getWholeNanoseconds();
	}
	
	public long getWholeHours() {
		return nanoseconds / HOUR.getWholeNanoseconds();
	}
	
	public long getWholeDays() {
		return nanoseconds / DAY.getWholeNanoseconds();
	}

	public double getNanoseconds() {
		return (double) nanoseconds;
	}
	
	public double getMicroseconds() {
		return ((double) nanoseconds) / MICROSECOND.getNanoseconds();
	}
	
	public double getMilliseconds() {
		return ((double) nanoseconds) / MILLISECOND.getNanoseconds();
	}
	
	public double getSeconds() {
		return ((double) nanoseconds) / SECOND.getNanoseconds();
	}
	
	public double getMinutes() {
		return ((double) nanoseconds) / MINUTE.getNanoseconds();
	}
	
	public double getHours() {
		return ((double) nanoseconds) / HOUR.getNanoseconds();
	}
	
	public double getDays() {
		return ((double) nanoseconds) / DAY.getNanoseconds();
	}
	
	public NanoTimeDelta toNanoTimeDelta() {
		if (nanoTimeDelta == null) {
			nanoTimeDelta = NanoTimeDelta.create(this, 1L);
		}
		return nanoTimeDelta;
	}
	
	public Optional<Integer> toCalendarUnit() {
		return calendarUnit;
	}	
}
