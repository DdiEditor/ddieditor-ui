package org.ddialliance.ddieditor.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageUtil {
	public static String[][] getAvailableLanguages() {
		Locale[] availLocales = Locale.getAvailableLocales();

		// sort
		Map<String, Locale> localMap = new HashMap<String, Locale>();
		for (int i = 0; i < availLocales.length; i++) {
			localMap.put(availLocales[i].getDisplayLanguage(), availLocales[i]);
		}
		ArrayList<String> toSort = new ArrayList<String>(localMap.keySet());
		Collections.sort(toSort);

		// display
		String[][] entryNamesAndValues = new String[toSort.size()][2];
		int index = 0;
		for (String langCode : toSort) {
			if (localMap.get(langCode).getDisplayLanguage() == null) {
				continue;
			}
			entryNamesAndValues[index][0] = localMap.get(langCode)
					.getDisplayLanguage();
			entryNamesAndValues[index][1] = localMap.get(langCode)
					.getISO3Language();
			index++;
		}
		return entryNamesAndValues;
	}
}
