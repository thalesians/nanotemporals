package com.thalesians.nanotemporals;

import com.google.common.collect.ImmutableList;

/**
 * The static methods of this class serve as utilities for dealing with temporal objects, such as {@link NanoDateTime},
 * {@link NanoDate}, {@link NanoTime}, and {@link NanoTimeDelta}.
 */
public class NanoTemporals {
	public static long MILLISECONDS_IN_DAY = 86400000;
	
	/**
	 * Since this is a class solely of static methods, its instantiation should not be attempted, otherwise an {@link
	 * AssertionError} will be thrown.
	 */
	private NanoTemporals() {
		throw new AssertionError("This class may not be instantiated");
	}
	
	public static NanoDateTime min(NanoDateTime left, NanoDateTime right) {
		return left.isStrictlyBefore(right) ? left : right;
	}
	
	public static NanoDateTime min(ImmutableList<NanoDateTime> datetimes) {
		NanoDateTime min = null;
		for (NanoDateTime datetime : datetimes) {
			if (min == null || (datetime != null && datetime.isStrictlyBefore(min))) {
				min = datetime;
			}
		}
		return min;
	}
	
	public static NanoDateTime max(NanoDateTime left, NanoDateTime right) {
		return right.isStrictlyAfter(left) ? right : left;
	}
	
	public static NanoDateTime max(ImmutableList<NanoDateTime> datetimes) {
		NanoDateTime max = null;
		for (NanoDateTime datetime : datetimes) {
			if (max == null || (datetime != null && datetime.isStrictlyAfter(max))) {
				max = datetime;
			}
		}
		return max;
	}
	
	public static boolean between(NanoDateTime datetime, NanoDateTime lower, NanoDateTime upper) {
		return !datetime.isStrictlyBefore(lower) && datetime.isStrictlyBefore(upper);
	}

	public static boolean between(NanoDate date, NanoDate lower, NanoDate upper) {
		return !date.isStrictlyBefore(lower) && date.isStrictlyBefore(upper);
	}
	
	public static boolean between(NanoTime time, NanoTime lower, NanoTime upper) {
		return !time.isStrictlyBefore(lower) && time.isStrictlyBefore(upper);
	}	
}
