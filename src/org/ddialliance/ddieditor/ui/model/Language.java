package org.ddialliance.ddieditor.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Language {

	/**
	 * Note: Language is e.g. 'Danish'
	 *       Language Code is e.g. 'da'
	 */
	
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Language.class);

	private static final String[] LANGUAGES = { 
		Messages.getString("Language.label.Danish"),
		Messages.getString("Language.label.English"), 
		Messages.getString("Language.label.Norwegian"),
		Messages.getString("Language.label.Swedish") };

	public static final String[] LANGUAGE_CODES = { "da", "en", "no", "se" };

	private enum LANGUAGE_INDEX {
		DA, EN, NO, SE
	};
	
	private static final int DEFAULT_LANGUAGE_INDEX = 0;
	
	public static String getDefaultLanguage() {
		return LANGUAGES[DEFAULT_LANGUAGE_INDEX];
	}

	public static String getDefaultLanguageCode() {
		return LANGUAGE_CODES[DEFAULT_LANGUAGE_INDEX];
	}

	public static String[] getLanguagesExcludingOrginalLanguage(String orginalLanguageCode) {
		List<String> languages = new ArrayList<String>();

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (!LANGUAGE_CODES[i].equals(orginalLanguageCode)) {
				languages.add(LANGUAGES[i]);
			}
		}
		return (languages.toArray(new String[0]));
	}

	public static String[] getLanguageCodesExcludingOrginalLanguage(String orginalLanguageCode) {
		List<String> languages = new ArrayList<String>();

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (!LANGUAGE_CODES[i].equals(orginalLanguageCode)) {
				languages.add(LANGUAGE_CODES[i]);
			}
		}
		return (languages.toArray(new String[0]));
	}

	public static int getLanguageIndex(String languageCode) {

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (LANGUAGE_CODES[i].equals(languageCode)) {
				return i;
			}
		}
		log.error("Language Index of Language Code '"+languageCode+"' not found");
		return -1;
	}
	
	public static String getLanguage(String languageCode) {
		
		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (LANGUAGE_CODES[i].equals(languageCode)) {
				return LANGUAGES[i];
			}
		}
		log.error("Language of Language Code '"+languageCode+"' not found");
		return null;
	}
	
	public static String[] getLanguages() {
		return LANGUAGES;
	}

	public static String[] getLanguageCodes() {
		return LANGUAGE_CODES;
	}
}