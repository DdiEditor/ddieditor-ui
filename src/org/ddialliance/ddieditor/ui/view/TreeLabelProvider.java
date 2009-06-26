package org.ddialliance.ddieditor.ui.view;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.lightxmlobject.impl.LightXmlObjectTypeImpl;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

class TreeLabelProvider extends LabelProvider {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, TreeLabelProvider.class);

	public String getText(Object element) {

		// TODO Get Label of default language - not just the first one
		if (element instanceof LightXmlObjectTypeImpl) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) element;
			log.debug("Get Text of Element with Id: " + lightXmlObjectType.getId());
			if (lightXmlObjectType.getLabelList().size() > 0) {
				XmlCursor xmlCursor = lightXmlObjectType.getLabelList().get(0).newCursor();
				xmlCursor.toLastAttribute();
				xmlCursor.toNextToken();
				String result = xmlCursor.getChars();
				xmlCursor.dispose();
				log.debug("Text: " + result);
				return (result);
			} else {
				// No label specified - use ID instead:
				return lightXmlObjectType.getElement()+": "+lightXmlObjectType.getId();
			}
		}
		return new String();
	}

	public Image getImage(Object element) {
		return null;
	}
}
