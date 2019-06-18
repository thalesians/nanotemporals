package com.thalesians.nanotemporals;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A {@code Tenor} represents a time period meaningful for a particular calendar: the number of calendar and/or business
 * days, weeks, months, years.
 * <p/>
 * It can be used to represent the expiry (tenor) of a financial instrument.
 * <p/>
 * It is not possible to meaningfully compare tenors without reference to a particular start date. For example, are four
 * weeks greater than one month?
 * <p/>
 * For the same reason, a thirty-day tenor is not regarded as equal to a one-month tenor. Moreover, nor do we regard a
 * seven-day tenor as equal to one-week.
 */
public class Tenor implements Serializable {
	private static final long serialVersionUID = 9020259624016612745L;
	
	private static final String PARSE_CHECK_REGEX = "^(-?\\d+[dbwmy])+$";
	private static final String PARSE_MATCH_REGEX = "-?\\d+[dbwmy]";
	
	public static final Tenor ONE_DAY = Tenor.create(Unit.DAY, 1);
	public static final Tenor TWO_DAYS = Tenor.create(Unit.DAY, 2);
	public static final Tenor THREE_DAYS = Tenor.create(Unit.DAY, 3);
	public static final Tenor FOUR_DAYS = Tenor.create(Unit.DAY, 4);
	public static final Tenor FIVE_DAYS = Tenor.create(Unit.DAY, 5);
	public static final Tenor SIX_DAYS = Tenor.create(Unit.DAY, 6);
	public static final Tenor SEVEN_DAYS = Tenor.create(Unit.DAY, 7);
	public static final Tenor EIGHT_DAYS = Tenor.create(Unit.DAY, 8);
	public static final Tenor NINE_DAYS = Tenor.create(Unit.DAY, 9);
	public static final Tenor TEN_DAYS = Tenor.create(Unit.DAY, 10);
	public static final Tenor ELEVEN_DAYS = Tenor.create(Unit.DAY, 11);
	public static final Tenor TWELVE_DAYS = Tenor.create(Unit.DAY, 12);
	
	public static final Tenor ONE_BUSINESS_DAY = Tenor.create(Unit.BUSINESS_DAY, 1);
	public static final Tenor TWO_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 2);
	public static final Tenor THREE_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 3);
	public static final Tenor FOUR_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 4);
	public static final Tenor FIVE_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 5);
	public static final Tenor SIX_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 6);
	public static final Tenor SEVEN_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 7);
	public static final Tenor EIGHT_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 8);
	public static final Tenor NINE_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 9);
	public static final Tenor TEN_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 10);
	public static final Tenor ELEVEN_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 11);
	public static final Tenor TWELVE_BUSINESS_DAYS = Tenor.create(Unit.BUSINESS_DAY, 12);
	
	public static final Tenor ONE_WEEK = Tenor.create(Unit.WEEK, 1);
	public static final Tenor TWO_WEEKS = Tenor.create(Unit.WEEK, 2);
	public static final Tenor THREE_WEEKS = Tenor.create(Unit.WEEK, 3);
	public static final Tenor FOUR_WEEKS = Tenor.create(Unit.WEEK, 4);
	public static final Tenor FIVE_WEEKS = Tenor.create(Unit.WEEK, 5);
	public static final Tenor SIX_WEEKS = Tenor.create(Unit.WEEK, 6);
	public static final Tenor SEVEN_WEEKS = Tenor.create(Unit.WEEK, 7);
	public static final Tenor EIGHT_WEEKS = Tenor.create(Unit.WEEK, 8);
	public static final Tenor NINE_WEEKS = Tenor.create(Unit.WEEK, 9);
	public static final Tenor TEN_WEEKS = Tenor.create(Unit.WEEK, 10);
	public static final Tenor ELEVEN_WEEKS = Tenor.create(Unit.WEEK, 11);
	public static final Tenor TWELVE_WEEKS = Tenor.create(Unit.WEEK, 12);
	
	public static final Tenor ONE_MONTH = Tenor.create(Unit.MONTH, 1);
	public static final Tenor TWO_MONTHS = Tenor.create(Unit.MONTH, 2);
	public static final Tenor THREE_MONTHS = Tenor.create(Unit.MONTH, 3);
	public static final Tenor FOUR_MONTHS = Tenor.create(Unit.MONTH, 4);
	public static final Tenor FIVE_MONTHS = Tenor.create(Unit.MONTH, 5);
	public static final Tenor SIX_MONTHS = Tenor.create(Unit.MONTH, 6);
	public static final Tenor SEVEN_MONTHS = Tenor.create(Unit.MONTH, 7);
	public static final Tenor EIGHT_MONTHS = Tenor.create(Unit.MONTH, 8);
	public static final Tenor NINE_MONTHS = Tenor.create(Unit.MONTH, 9);
	public static final Tenor TEN_MONTHS = Tenor.create(Unit.MONTH, 10);
	public static final Tenor ELEVEN_MONTHS = Tenor.create(Unit.MONTH, 11);
	public static final Tenor TWELVE_MONTHS = Tenor.create(Unit.MONTH, 12);
	
	public static final Tenor ONE_YEAR = Tenor.create(Unit.YEAR, 1);
	public static final Tenor TWO_YEARS = Tenor.create(Unit.YEAR, 2);
	public static final Tenor THREE_YEARS = Tenor.create(Unit.YEAR, 3);
	public static final Tenor FOUR_YEARS = Tenor.create(Unit.YEAR, 4);
	public static final Tenor FIVE_YEARS = Tenor.create(Unit.YEAR, 5);
	public static final Tenor SIX_YEARS = Tenor.create(Unit.YEAR, 6);
	public static final Tenor SEVEN_YEARS = Tenor.create(Unit.YEAR, 7);
	public static final Tenor EIGHT_YEARS = Tenor.create(Unit.YEAR, 8);
	public static final Tenor NINE_YEARS = Tenor.create(Unit.YEAR, 9);
	public static final Tenor TEN_YEARS = Tenor.create(Unit.YEAR, 10);
	public static final Tenor ELEVEN_YEARS = Tenor.create(Unit.YEAR, 11);
	public static final Tenor TWELVE_YEARS = Tenor.create(Unit.YEAR, 12);
	public static final Tenor THIRTEEN_YEARS = Tenor.create(Unit.YEAR, 13);
	public static final Tenor FOURTEEN_YEARS = Tenor.create(Unit.YEAR, 14);
	public static final Tenor FIFTEEN_YEARS = Tenor.create(Unit.YEAR, 15);
	public static final Tenor SIXTEEN_YEARS = Tenor.create(Unit.YEAR, 16);
	public static final Tenor SEVENTEEN_YEARS = Tenor.create(Unit.YEAR, 17);
	public static final Tenor EIGHTEEN_YEARS = Tenor.create(Unit.YEAR, 18);
	public static final Tenor NINETEEN_YEARS = Tenor.create(Unit.YEAR, 19);
	public static final Tenor TWENTY_YEARS = Tenor.create(Unit.YEAR, 20);
	public static final Tenor TWENTY_ONE_YEARS = Tenor.create(Unit.YEAR, 21);
	public static final Tenor TWENTY_TWO_YEARS = Tenor.create(Unit.YEAR, 22);
	public static final Tenor TWENTY_THREE_YEARS = Tenor.create(Unit.YEAR, 23);
	public static final Tenor TWENTY_FOUR_YEARS = Tenor.create(Unit.YEAR, 24);
	public static final Tenor TWENTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 25);
	public static final Tenor THIRTY_YEARS = Tenor.create(Unit.YEAR, 30);
	public static final Tenor THIRTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 35);
	public static final Tenor FORTY_YEARS = Tenor.create(Unit.YEAR, 40);
	public static final Tenor FORTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 45);
	public static final Tenor FIFTY_YEARS = Tenor.create(Unit.YEAR, 50);
	public static final Tenor FIFTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 55);
	public static final Tenor SIXTY_YEARS = Tenor.create(Unit.YEAR, 60);
	public static final Tenor SIXTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 65);
	public static final Tenor SEVENTY_YEARS = Tenor.create(Unit.YEAR, 70);
	public static final Tenor SEVENTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 75);
	public static final Tenor EIGHTY_YEARS = Tenor.create(Unit.YEAR, 80);
	public static final Tenor EIGHTY_FIVE_YEARS = Tenor.create(Unit.YEAR, 85);
	public static final Tenor NINETY_YEARS = Tenor.create(Unit.YEAR, 90);
	public static final Tenor NINETY_FIVE_YEARS = Tenor.create(Unit.YEAR, 95);
	public static final Tenor ONE_HUNDRED_YEARS = Tenor.create(Unit.YEAR, 100);

	public enum Unit {
		BUSINESS_DAY, DAY, WEEK, MONTH, YEAR
	}
	
	private final ImmutableMap<Unit, Integer> periodMap;
	
	private Tenor(ImmutableMap<Unit, Integer> periodMap) {
		this.periodMap = periodMap;
	}
	
	public static Tenor create(Unit unit, int length) {
		return new Tenor(length == 0 ? ImmutableMap.<Unit, Integer>of() : ImmutableMap.of(unit, length));
	}
	
	private static String unitToString(Unit unit) {
		switch (unit) {
		case BUSINESS_DAY: return "b";
		case DAY: return "d";
		case WEEK: return "w";
		case MONTH: return "m";
		case YEAR: return "y";
		default: throw new IllegalArgumentException("Illegal unit");
		}
	}
	
	private static Unit stringToUnit(char unit) {
		switch (unit) {
		case 'b': return Unit.BUSINESS_DAY;
		case 'd': return Unit.DAY;
		case 'w': return Unit.WEEK;
		case 'm': return Unit.MONTH;
		case 'y': return Unit.YEAR;
		default: throw new IllegalArgumentException("Illegal unit");
		}
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		Map<Unit, Integer> periodMapBuilder = Maps.newHashMap();
		
		public Builder set(Unit unit, int length) {
			if (length == 0) {
				periodMapBuilder.remove(unit);
			} else {
				periodMapBuilder.put(unit, length);
			}
			return this;
		}
		
		private boolean isSet(Unit unit) {
			return periodMapBuilder.containsKey(unit);
		}
		
		public Tenor build() {
			return new Tenor(ImmutableMap.copyOf(periodMapBuilder));
		}
	}	

	public static Tenor parse(String tenor) throws ParseException {
		tenor = tenor.toLowerCase();
		if (!tenor.matches(PARSE_CHECK_REGEX)) {
			throw new ParseException("Bad period expression " + tenor, 0);
		}
		Builder b = new Builder();
		Pattern p = Pattern.compile(PARSE_MATCH_REGEX);
		Matcher m = p.matcher(tenor);
		while (m.find()) {
			String periodString = tenor.substring(m.start(), m.end());
			int length = Integer.valueOf(periodString.substring(0, periodString.length() - 1));
			Unit unit = stringToUnit(periodString.charAt(periodString.length() - 1));
			if (b.isSet(unit)) {
				throw new ParseException("Duplicate unit '" + unit + "' in tenor string '" + tenor + "'", 0);
			}
			b.set(unit, length);
		}
		return b.build();
	}
	
	public int getUnitLength(Unit unit) {
		Integer length = periodMap.get(unit);
		return length == null ? 0 : length;
	}
		
	@Override
	public int hashCode() {
		return periodMap.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tenor)) return false;
		Tenor that = (Tenor) o;
		return this.periodMap.equals(that.periodMap);
	}	

	@Override
	public String toString() {
		if (periodMap.isEmpty()) {
			return "0d";
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<Unit, Integer> e : periodMap.entrySet()) {
			builder.append(e.getValue()).append(unitToString(e.getKey()));
		}
		return builder.toString();
	}	
}
