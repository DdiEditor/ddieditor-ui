package org.ddialliance.ddieditor.ui.view;

/**
 * Tree Label Provider.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.lightxmlobject.impl.LightXmlObjectTypeImpl;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
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
		} else if (element instanceof DDIResourceType) {
			return ((DDIResourceType) element).getOrgName();
		} else if (element instanceof StorageType) {
			return ((StorageType) element).getId();
		} else if (element instanceof ConceptualType) {
			return ((ConceptualType) element).name();
		} else if (element instanceof ConceptualElement) {
			List<org.ddialliance.ddieditor.model.lightxmlobject.LabelType> labels = ((ConceptualElement) element)
					.getValue().getLabelList();
			if (!labels.isEmpty()) {
				return XmlBeansUtil.getTextOnMixedElement(((ConceptualElement) element).getValue().getLabelList()
						.get(0));
			} else {
				return ((ConceptualElement) element).getValue().getId();
			}
		} 
		return new String();
	}

	public Image getImage(Object element) {
		return null;
	}
}
