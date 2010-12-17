package org.ddialliance.ddieditor.ui.model;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.IDType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.ReflectionUtil;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class ModelAccessor {
	public static ReferenceType setReference(ReferenceType reference,
			LightXmlObjectType refered) {
		IDType id = null;
		if (reference.getIDList().isEmpty()) {
			id = reference.addNewID();
		} else {
			id = reference.getIDList().get(0);
		}
		id.setStringValue(refered.getId());

		return reference;
	}
	
	public static ReferenceType setReference(List<?> refList, ReferenceType reference,
			LightXmlObjectType refered) {
		if (refered.getId().equals("")) {
			if (!refList.isEmpty()) {
				refList.remove(0);
			}
			return null;
		} else {
			IDType id = null;
			if (reference.getIDList().isEmpty()) {
				id = reference.addNewID();
			} else {
				id = reference.getIDList().get(0);
			}
			id.setStringValue(refered.getId());
		}
		return reference;
	}

	public static LightXmlObjectListDocument resolveReference(
			ReferenceType reference, String localName) throws DDIFtpException {
		StringBuilder operation = new StringBuilder("get");
		operation.append(localName);
		operation.append("sLight");

		LightXmlObjectListDocument lightXmlObjectList = null;
		try {
			lightXmlObjectList = (LightXmlObjectListDocument) ReflectionUtil
					.invokeMethod(DdiManager.getInstance(), operation
							.toString(), false, new Object[] {
							XmlBeansUtil.getTextOnMixedElement(reference.getIDArray(0)), "", "",
							"" });
		} catch (Exception e) {
			throw new DDIFtpException(e);
		}
		return lightXmlObjectList;
	}
}
