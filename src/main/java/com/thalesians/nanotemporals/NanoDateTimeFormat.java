package com.thalesians.nanotemporals;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * The following class adds the following patterns to the ones supported by {@link SimpleDateFormat}: NNNNNNNNN
 * (nanoseconds since the last second), and CCCCCC (microseconds since the last second).
 */
public class NanoDateTimeFormat extends Format {
	
	private static final long serialVersionUID = 6528420125008414463L;
	
	public static final String DEFAULT_DATE_PATTERN = "yyyy.MM.dd";
	
	// TODO Once we support parsing (not just formatting) for nanoseconds, we should change this to "HH:mm:ss.NNNNNNNNN" 
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss.SSS";
	
	// TODO Once we support parsing (not just formatting) for nanoseconds, we should change this to
	// "yyyy.MM.dd'T'HH:mm:ss.NNNNNNNNN" 
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy.MM.dd'T'HH:mm:ss.SSS";
	
	private static final String NNNNNNNNN = "NNNNNNNNN";
	private static final char NNNNNNNNNchar = 'N';
	private static final String CCCCCC = "CCCCCC";
	private static final char CCCCCCchar = 'C';
	
	private final SimpleDateFormat simpleDateFormat;
	private final boolean zonedTemporalTypes;
	
	private static final class PatternInfo {
		private final Optional<String> pattern;
		private final ImmutableList<Boolean> NNNNNNNNNsQuoted;
		private final ImmutableList<Boolean> CCCCCCsQuoted;
		
		private PatternInfo(Optional<String> pattern, ImmutableList<Boolean> NNNNNNNNNsQuoted,
				ImmutableList<Boolean> CCCCCCsQuoted) {
			this.pattern = pattern;
			this.NNNNNNNNNsQuoted = NNNNNNNNNsQuoted;
			this.CCCCCCsQuoted = CCCCCCsQuoted;
		}
	}
	
	private final PatternInfo patternInfo;

	private Calendar calendar;
	
	private NanoDateTimeFormat(String timeZoneId, boolean zonedTemporalTypes) {
		this.patternInfo = preprocessPattern(null);
		this.simpleDateFormat = new SimpleDateFormat();
		this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		this.zonedTemporalTypes = zonedTemporalTypes;
	}
	
	private NanoDateTimeFormat(String timeZoneId, String pattern, boolean zonedTemporalTypes) {
		this.patternInfo = preprocessPattern(pattern);
		this.simpleDateFormat =
				new SimpleDateFormat(this.patternInfo.pattern.isPresent() ? this.patternInfo.pattern.get() : null);
		this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		this.zonedTemporalTypes = zonedTemporalTypes;
	}
	
	private NanoDateTimeFormat(String timeZoneId, String pattern, DateFormatSymbols formatSymbols, boolean zonedTemporalTypes) {
		this.patternInfo = preprocessPattern(pattern);
		this.simpleDateFormat =
				new SimpleDateFormat(this.patternInfo.pattern.isPresent()
						? this.patternInfo.pattern.get()
						: null, formatSymbols);
		this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		this.zonedTemporalTypes = zonedTemporalTypes;
	}
	
	private NanoDateTimeFormat(String timeZoneId, String pattern, Locale locale, boolean zonedTemporalTypes) {
		this.patternInfo = preprocessPattern(pattern);
		this.simpleDateFormat = new SimpleDateFormat(
				this.patternInfo.pattern.isPresent() ? this.patternInfo.pattern.get() : null, locale);
		this.simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		this.zonedTemporalTypes = zonedTemporalTypes;
	}
	
	public static NanoDateTimeFormat of(String timeZoneId, boolean zonedTemporalTypes) {
		return new NanoDateTimeFormat(timeZoneId, zonedTemporalTypes);
	}
	
	public static NanoDateTimeFormat of(String timeZoneId, String pattern, boolean zonedTemporalTypes) {
		return new NanoDateTimeFormat(timeZoneId, pattern, zonedTemporalTypes);
	}
	
	public static NanoDateTimeFormat of(String timeZoneId, String pattern, DateFormatSymbols formatSymbols, boolean zonedTemporalTypes) {
		return new NanoDateTimeFormat(timeZoneId, pattern, formatSymbols, zonedTemporalTypes);
	}
	
	public static NanoDateTimeFormat of(String timeZoneId, String pattern, Locale locale, boolean zonedTemporalTypes) {
		return new NanoDateTimeFormat(timeZoneId, pattern, locale, zonedTemporalTypes);
	}

	private Calendar getCalendar() {
		TimeZone timeZone = simpleDateFormat.getTimeZone();
		if (calendar == null) {
			calendar = Calendar.getInstance(timeZone);
		}
		calendar.setLenient(false);
		calendar.clear();
	    calendar.setTimeZone(timeZone);
		return calendar;
	}
	
	private PatternInfo preprocessPattern(String pattern) {
		ImmutableList<Boolean> NNNNNNNNNsQuoted;
		ImmutableList<Boolean> CCCCCCsQuoted;
		Optional<String> processedPattern;
		if (pattern == null) {
			processedPattern = Optional.absent();
			NNNNNNNNNsQuoted = ImmutableList.of();
			CCCCCCsQuoted = ImmutableList.of();
		} else {
			ImmutableList.Builder<Boolean> builderOfNNNNNNNNNs = ImmutableList.builder();
			ImmutableList.Builder<Boolean> builderOfCCCCCCs = ImmutableList.builder();
			boolean quoted = false;
			int countOfN = 0;
			int countOfC = 0;
			for (int i = 0, c = pattern.length(); i < c; ++i) {
				if (pattern.charAt(i) == '\'') {
					quoted = !quoted;
					countOfN = 0;
					countOfC = 0;
				} else if (pattern.charAt(i) == NNNNNNNNNchar) {
					countOfC = 0;
					countOfN++;
					if (countOfN == NNNNNNNNN.length()) {
						builderOfNNNNNNNNNs.add(quoted);
						countOfN = 0;
					}
				} else if (pattern.charAt(i) == CCCCCCchar) {
					countOfN = 0;
					countOfC++;
					if (countOfC == CCCCCC.length()) {
						builderOfCCCCCCs.add(quoted);
						countOfC = 0;
					}
				} else {
					countOfN = 0;
					countOfC = 0;
				}
			}
			
			NNNNNNNNNsQuoted = builderOfNNNNNNNNNs.build();
			StringBuilder processedPatternBuilder1 = new StringBuilder();
			int lastIndex = 0;
			int count = 0;
			while (true) {
				int li = pattern.indexOf(NNNNNNNNN, lastIndex);
				if (li != -1) {
					processedPatternBuilder1.append(pattern.substring(lastIndex, li));
					if (NNNNNNNNNsQuoted.get(count)) {
						processedPatternBuilder1.append(NNNNNNNNN);
					} else {
						processedPatternBuilder1.append("'").append(NNNNNNNNN).append("'");
					}
					lastIndex = li;
					count++;
					lastIndex += NNNNNNNNN.length();
				} else {
					processedPatternBuilder1.append(pattern.substring(lastIndex, pattern.length()));
					break;
				}
			}
			String pattern1 = processedPatternBuilder1.toString();
			
			CCCCCCsQuoted = builderOfCCCCCCs.build();
			StringBuilder processedPatternBuilder2 = new StringBuilder();
			lastIndex = 0;
			count = 0;
			while (true) {
				int li = pattern1.indexOf(CCCCCC, lastIndex);
				if (li != -1) {
					processedPatternBuilder2.append(pattern1.substring(lastIndex, li));
					if (CCCCCCsQuoted.get(count)) {
						processedPatternBuilder2.append(CCCCCC);
					} else {
						processedPatternBuilder2.append("'").append(CCCCCC).append("'");
					}
					lastIndex = li;
					count++;
					lastIndex += CCCCCC.length();
				} else {
					processedPatternBuilder2.append(pattern1.substring(lastIndex, pattern1.length()));
					break;
				}
			}

			processedPattern = Optional.of(processedPatternBuilder2.toString());
		}
		return new PatternInfo(processedPattern, NNNNNNNNNsQuoted, CCCCCCsQuoted);
	}
	
	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		StringBuffer sb;
		long nanos = 0L;
		boolean postprocessPatterns = true;
		if (obj instanceof NanoDateTime) {
			sb = simpleDateFormat.format(((NanoDateTime) obj).getMillisecondsSinceEpoch(), toAppendTo, pos);
			nanos = ((NanoDateTime) obj).getNanosecondsSinceEpoch() % TemporalUnit.SECOND.getWholeNanoseconds();
		} else if (obj instanceof ZonedNanoDateTime) {
			sb = simpleDateFormat.format(((ZonedNanoDateTime) obj).getMillisecondsSinceEpoch(), toAppendTo, pos);
			nanos = ((ZonedNanoDateTime) obj).getNanosecondsSinceEpoch() % TemporalUnit.SECOND.getWholeNanoseconds();
		} else if (obj instanceof NanoDate) {
			sb = simpleDateFormat.format(((NanoDate) obj).getMillisecondsSinceEpoch(), toAppendTo, pos);
			nanos = ((NanoDate) obj).getNanosecondsSinceEpoch() % TemporalUnit.SECOND.getWholeNanoseconds();
		} else if (obj instanceof ZonedNanoDate) {
			sb = simpleDateFormat.format(((ZonedNanoDate) obj).getMillisecondsSinceEpoch(), toAppendTo, pos);
			nanos = ((ZonedNanoDate) obj).getNanosecondsSinceEpoch() % TemporalUnit.SECOND.getWholeNanoseconds();
		} else if (obj instanceof NanoTime) {
			Calendar calendar = getCalendar();
		    calendar.set(Calendar.YEAR, 1900);
		    calendar.set(Calendar.MONTH, 0);
		    calendar.set(Calendar.DATE, 1);
			sb = simpleDateFormat.format(calendar.getTimeInMillis() +
					((NanoTime) obj).getMillisecondsSinceMidnight(), toAppendTo, pos);
			nanos = ((NanoTime) obj).getNanosecondsSinceMidnight() % TemporalUnit.SECOND.getWholeNanoseconds(); 
		} else {
			sb = simpleDateFormat.format(obj, toAppendTo, pos);
			postprocessPatterns = false;
		}
		
		if (postprocessPatterns) {
			String string = sb.toString();
			int lastIndex = 0;
			int count = 0;
			while (true) {
				int li = string.indexOf(NNNNNNNNN, lastIndex);
				if (li != -1) {
					lastIndex = li;
					if (!patternInfo.NNNNNNNNNsQuoted.get(count)) {
						long ns = nanos;
						for (int i = NNNNNNNNN.length() - 1; i >= 0; i--) {
							sb.setCharAt(li + i, Long.toString(ns % 10).charAt(0));
							ns /= 10;
						}
					}
					count++;
					lastIndex += NNNNNNNNN.length();
				} else {
					break;
				}
			}
			
			string = sb.toString();
			lastIndex = 0;
			count = 0;
			while (true) {
				int li = string.indexOf(CCCCCC, lastIndex);
				if (li != -1) {
					lastIndex = li;
					if (!patternInfo.CCCCCCsQuoted.get(count)) {
						long ns = nanos / TemporalUnit.MICROSECOND.getWholeNanoseconds();
						for (int i = CCCCCC.length() - 1; i >= 0; i--) {
							sb.setCharAt(li + i, Long.toString(ns % 10).charAt(0));
							ns /= 10;
						}
					}
					count++;
					lastIndex += CCCCCC.length();
				} else {
					break;
				}
			}
		}
		
		return sb;
	}

	@Override
	public Object parseObject(String obj, ParsePosition pos) {
		java.util.Date javaUtilDate = (java.util.Date) simpleDateFormat.parseObject(obj, pos);
		Object result;
		if (javaUtilDate == null) {
			result = null;
		} else {
			if (zonedTemporalTypes) {
				result = TemporalArithmetics.toZonedNanoDateTimeForUTCEquivalentOf(simpleDateFormat.getTimeZone().getID(), javaUtilDate);
			} else {
				result = TemporalArithmetics.toNanoDateTimeForUTCEquivalentOf(javaUtilDate);
			}
		}
		return result;
	}
}
