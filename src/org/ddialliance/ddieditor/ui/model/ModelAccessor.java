package org.ddialliance.ddieditor.ui.model;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.IDType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class ModelAccessor {
	public static ReferenceType setReference(ReferenceType reference,
			LightXmlObjectType refered) {
		IDType id = null;
		if (reference.getIDList().isEmpty()) {
			reference.addNewID();
		} else {
			id = reference.getIDList().get(0);
		}
		XmlBeansUtil.setTextOnMixedElement(id, refered.getId());
		
		return reference;
	}
}
