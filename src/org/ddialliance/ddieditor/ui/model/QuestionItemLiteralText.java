package org.ddialliance.ddieditor.ui.model;

public class QuestionItemLiteralText {
	private String text;
	private String languageCode;
	private boolean translated;
	private boolean translatable;

	/**
	 * Constructor
	 * 
	 * @param text			Question Text.
	 * @param languageCode	Question Language Code 
	 * @param translated	Translated - true og false
	 * @param translatable	Translatable - true og false
	 */
	public QuestionItemLiteralText(String text, String languageCode, boolean translated, boolean translatable) {
		setText(text);
		setLanguageCode(languageCode);
		setTranslated(translated);
		setTranslatable(translatable);
	}

	/**
	 * Set Question Text.
	 * 
	 * @param text	Question Text.
	 */
	public void setText(String text) {
         this.text = text;
    }

	/**
	 * Get Question Text.
	 * 
	 * @return	Question Text string.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set Language Code
	 * 
	 * @param Language Code
	 */
	public void setLanguageCode(String language) {
		this.languageCode = language;
	}

	/**
	 * Get Language Code
	 * 
	 * @return Language
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * Set Translated flag
	 * 
	 * @param translated - true og false.
	 */
	public void setTranslated(boolean translated) {
		this.translated = translated;
	}

	/**
	 * Get Translated flag.
	 * 
	 * @return true og false.
	 */
	public boolean getTranslated() {
		return translated;
	}

	/**
	 * Set Translatable flag
	 * 
	 * @param translatable - true og false
	 */
	public void setTranslatable(boolean translatable) {
		this.translatable = translatable;
	}

	/**
	 * Get Translatable flag.
	 * 
	 * @return true or false
	 */
	public boolean getTranslatable() {
		return translatable;
	}
}
