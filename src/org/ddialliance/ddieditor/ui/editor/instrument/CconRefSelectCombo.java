package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;

public class CconRefSelectCombo extends ReferenceSelectionCombo{

	public CconRefSelectCombo(boolean isNew) {
		super(isNew);
	}

	@Override
	public List<LightXmlObjectType> getReferenceList() {
		List<LightXmlObjectType> result = new ArrayList<LightXmlObjectType>();
		try {
			result = DdiManager.getInstance()
					.getControlConstructsLight();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		referenceList = addedWithEmpty(result); 
		return referenceList;
	}
}
