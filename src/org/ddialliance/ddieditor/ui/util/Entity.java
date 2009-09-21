package org.ddialliance.ddieditor.ui.util;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.EditorInput;

public class Entity {
	
	/**
	 * Return entity type of selected item
	 * 
	 * @return EditorInput.ENTITY_TYPE
	 */
	public static EditorInput.ENTITY_TYPE getEntityType(LightXmlObjectType item) {

		if (item.getElement().equals("studyunit__StudyUnit")) {
			return EditorInput.ENTITY_TYPE.STUDY_UNIT;
		} else if (item.getElement().equals("ConceptScheme")) {
			return EditorInput.ENTITY_TYPE.CONCEPT_SCHEME;
		} else if (item.getElement().equals("Concept")) {
			return EditorInput.ENTITY_TYPE.CONCEPT;
		} else if (item.getElement().equals("CodeScheme")) {
			return EditorInput.ENTITY_TYPE.CODE_SCHEME;
		} else if (item.getElement().equals("QuestionScheme")) {
			return EditorInput.ENTITY_TYPE.QUESTION_SCHEME;
		} else if (item.getElement().equals("QuestionItem")) {
			return EditorInput.ENTITY_TYPE.QUESTION_ITEM;
		} else {
			// TODO Error handling
			System.err.println("Element Type not supported: " + item.getElement());
			System.exit(0);
		}
		return null;
	}


}
