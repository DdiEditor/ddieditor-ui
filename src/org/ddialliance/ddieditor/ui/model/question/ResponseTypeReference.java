package org.ddialliance.ddieditor.ui.model.question;

import org.ddialliance.ddieditor.ui.model.question.QuestionItem.RESPONSE_TYPES;

public class ResponseTypeReference {

	private String label;
	private RESPONSE_TYPES responseDomain;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            Label of Combo Box
	 * @param org
	 *            .ddialliance.ddieditor.ui.editor.ResponseType.RESPONSE_TYPES
	 */
	public ResponseTypeReference(String label, RESPONSE_TYPES representationType) {
		this.label = label;
		this.responseDomain = representationType;
	}

	/**
	 * Returns label e.g. for Combo Box
	 */
	public String toString() {
		return label;
	}

	/**
	 * Returns Response Domain of reference
	 * 
	 * @return
	 */
	public RESPONSE_TYPES getResponseDomain() {
		return responseDomain;
	}
}
