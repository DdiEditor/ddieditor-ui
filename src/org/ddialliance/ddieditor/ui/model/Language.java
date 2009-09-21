package org.ddialliance.ddieditor.ui.model;

/**
 * Language utilities.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

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
	
	/**
	 * Get default language
	 * 
	 * @return String
	 */
	public static String getDefaultLanguage() {
		return LANGUAGES[DEFAULT_LANGUAGE_INDEX];
	}

	/**
	 * Get default language code
	 * 
	 * @return String
	 */
	public static String getDefaultLanguageCode() {
		return LANGUAGE_CODES[DEFAULT_LANGUAGE_INDEX];
	}

	/**
	 * Get default language excluding original language.
	 * 
	 * @param orginalLanguageCode
	 * @return String
	 */
	public static String[] getLanguagesExcludingOrginalLanguage(String orginalLanguageCode) {
		List<String> languages = new ArrayList<String>();

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (!LANGUAGE_CODES[i].equals(orginalLanguageCode)) {
				languages.add(LANGUAGES[i]);
			}
		}
		return (languages.toArray(new String[0]));
	}

	/** 
	 * Get default language code excluding original language code.
	 * 
	 * @param orginalLanguageCode
	 * @return String[]
	 */
	public static String[] getLanguageCodesExcludingOrginalLanguage(String orginalLanguageCode) {
		List<String> languages = new ArrayList<String>();

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (!LANGUAGE_CODES[i].equals(orginalLanguageCode)) {
				languages.add(LANGUAGE_CODES[i]);
			}
		}
		return (languages.toArray(new String[0]));
	}

	/**
	 * Get language index
	 * 
	 * @param languageCode
	 * @return
	 */
	public static int getLanguageIndex(String languageCode) {

		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (LANGUAGE_CODES[i].equals(languageCode)) {
				return i;
			}
		}
		log.error("Language Index of Language Code '"+languageCode+"' not found");
		return -1;
	}
	
	/**
	 * Get language
	 * 
	 * @param languageCode
	 * @return String
	 */
	public static String getLanguage(String languageCode) {
		
		for (int i = 0; i < LANGUAGE_CODES.length; i++) {
			if (LANGUAGE_CODES[i].equals(languageCode)) {
				return LANGUAGES[i];
			}
		}
		log.error("Language of Language Code '"+languageCode+"' not found");
		return null;
	}
	
	/**
	 * Get languages.
	 * 
	 * @return String[]
	 */
	public static String[] getLanguages() {
		return LANGUAGES;
	}

	/**
	 * Get language codes
	 * 
	 * @return String[]
	 */
	public static String[] getLanguageCodes() {
		return LANGUAGE_CODES;
	}
	
	/**
	 * Get language code
	 * 
	 * @return String[]
	 */
	public static String getLanguageCode(String language) {
		for (int i = 0; i < LANGUAGES.length; i++) {
			if (LANGUAGES[i].equals(language)) {
				return LANGUAGE_CODES[i];
			}
		}
		log.error("Language Code of Language '"+language+"' not found");
		return null;
	}

}