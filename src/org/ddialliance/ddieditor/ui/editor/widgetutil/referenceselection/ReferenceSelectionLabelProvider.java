package org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.viewers.LabelProvider;

public class ReferenceSelectionLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		if (element == null) {
			return null;
		}
		LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
		if (lightXmlObject.getLabelList().size() != 0) {
			return XmlBeansUtil.getTextOnMixedElement(lightXmlObject
					.getLabelList().get(0));
		} else {
			return lightXmlObject.getId();
		}
	}
}
