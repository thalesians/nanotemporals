package com.thalesians.nanotemporals;

/**
 * A rule mapping a given datetime to a date.
 * <p/>
 * For example, it may return the settlement date corresponding to the purchase time of a product.
 */
public interface DateRule {
	/**
	 * Returns a date associated by this {@code DateRule} with the datetime <tt>datetime</tt>. 
	 * 
	 * @param   datetime   the input datetime.
	 * @return  the date corresponding to <tt>datetime</tt>.
	 */
	NanoDate getDate(NanoDateTime datetime);
}
