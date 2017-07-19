package com.froggermtp.chh_data_collector;

/**
 * This interface contains methods for replacing characters in strings with more convenient or human 
 * readable versions.
 * <p>
 * Due to the magic of character encodings and other sorcery, some of the characters have multiple
 * different versions, or a character is represented by several ambiguous characters.
 * The methods in this interface convert the weird, fringe characters into the standard characters
 * determined by the project.
 */
public interface StringHelper {
	/**
	 * Converts all the dashes with the unicode 8211 to dashes with unicode 45 in the input String.
	 * 
	 * @param input  the string in which all the dashes are replaced, not null
	 * @return a string that will only contain dashes with unicode 45, not null
	 */
	static String replaceDashes(String input) {
		final String UNICODE_8211 = "–";
		final String UNICODE_45 = "-";
		
		return input.replace(UNICODE_8211, UNICODE_45);
	}
}
