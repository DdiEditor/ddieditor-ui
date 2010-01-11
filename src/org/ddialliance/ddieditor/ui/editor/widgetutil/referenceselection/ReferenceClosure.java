package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.GenericGetSetClosure;

public class ReferenceClosure implements GenericGetSetClosure {

	@Override
	public Object getObject(Object obj) {
		return obj;
	}

	@Override
	public String getStringValue(Object obj) {
		ReferenceType ref = (ReferenceType)obj;
		if (ref.getIDList().isEmpty()) {
			return null;
		}else {
			return ref.getIDList().get(0).getStringValue();
		}
	}

	@Override
	public void setStringValue(Object obj, Object text) {
		ReferenceType ref = (ReferenceType)obj;
		if (ref.getIDList().isEmpty()) {
			ref.addNewID().setStringValue((String)text);
		}else {
			ref.getIDList().get(0).setStringValue((String)text);
		}
	}
}
