package com.thalesians.nanotemporals.business;

import com.thalesians.nanotemporals.ZonedNanoDate;

public interface BusinessCalendar {
	enum HolidayType {
		LOCAL_HOLIDAY,
		BANK_HOLIDAY
	}
	
	static interface Holiday {
		public HolidayType getHolidayType();
		public String getHolidayName();
	}
	
	ZonedNanoDate firstDay();
	ZonedNanoDate lastDay();
	
	boolean isHoliday(ZonedNanoDate date);
	Holiday getHoliday(ZonedNanoDate date);
	ZonedNanoDate prevHoliday(ZonedNanoDate date);
	ZonedNanoDate nextHoliday(ZonedNanoDate date);
	
	boolean isWeekendDay(ZonedNanoDate date);
	ZonedNanoDate prevWeekendDay(ZonedNanoDate date);
	ZonedNanoDate nextWeekendDay(ZonedNanoDate date);

	boolean isBusinessDay(ZonedNanoDate date);
	ZonedNanoDate prevBusinessDay(ZonedNanoDate date);
	ZonedNanoDate nextBusinessDay(ZonedNanoDate date);
	
	ZonedNanoDate plusBusinessDays(ZonedNanoDate date, int quantity);
	ZonedNanoDate minusBusinessDays(ZonedNanoDate date, int quantity);
}
