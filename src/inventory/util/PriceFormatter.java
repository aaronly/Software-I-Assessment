package inventory.util;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * A simple formatter utility to convert to and from a double and a string representing currency.
 * @author Aaron Echols
 *
 */
public class PriceFormatter {

	private static NumberFormat format = NumberFormat.getCurrencyInstance();
	
	/**
	 * Convert a double to a string representing currency
	 * @param price a double of the currency amount
	 * @return a string representing a currency amount
	 */
	public static String format(double price){
		return format.format(price);
	}
	
	/**
	 * Convert a string representing currency to a double
	 * @param priceString a string representing a currency amount
	 * @return a double of the currency amount
	 * @throws ParseException if the beginning of the specified string cannot be parsed
	 */
	public static double parse(String priceString) throws ParseException{
		if(priceString == null || priceString.isEmpty()) {
			throw new IllegalArgumentException("String was empty or missing.");
		}
		Number number = format.parse(priceString);
		if(number instanceof Long) {
			//return Double.longBitsToDouble((long)number);
			return (double)(long)number;
		} else {
			return (double)number;
		}
		
	}
}
