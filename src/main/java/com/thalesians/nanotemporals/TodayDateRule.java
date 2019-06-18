package com.thalesians.nanotemporals;

public final class TodayDateRule implements DateRule {
	
	public TodayDateRule() {
	}
	
	@Override
	public NanoDate getDate(NanoDateTime now) {
		return now.getDate();
	}
}
