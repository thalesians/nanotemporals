package com.thalesians.nanotemporals.business;

import java.io.IOException;
import java.text.ParseException;

public class SimpleBusinessCalendar extends AbstractBusinessCalendar {
	
	private SimpleBusinessCalendar(String timeZoneId) throws IOException, ParseException {
		super(timeZoneId);
	}
	
	public static SimpleBusinessCalendar create(String timeZoneId) throws IOException, ParseException {
		return new SimpleBusinessCalendar(timeZoneId);
	}
}
