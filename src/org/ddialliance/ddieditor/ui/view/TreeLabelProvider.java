package org.ddialliance.ddieditor.ui.view;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * Tree Label Provider
 */
class TreeLabelProvider extends LabelProvider {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeLabelProvider.class);

	@Override
	public String getText(Object element) {
		if (element instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) element;
			if (lightXmlObjectType.getLabelList().size() > 0) {
				try {
					Object obj = XmlBeansUtil.getLangElement(
							LanguageUtil.getDisplayLanguage(),
							lightXmlObjectType.getLabelList());
					return XmlBeansUtil.getTextOnMixedElement((XmlObject) obj);
				} catch (DDIFtpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// No label specified - use ID instead:
				return lightXmlObjectType.getElement() + ": "
						+ lightXmlObjectType.getId();
			}
		} else if (element instanceof DDIResourceType) {
			return ((DDIResourceType) element).getOrgName();
		} else if (element instanceof StorageType) {
			return ((StorageType) element).getId();
		} else if (element instanceof ConceptualType) {
			return Messages.getString(((ConceptualType) element).name());
		} else if (element instanceof ConceptualElement) {
			List<org.ddialliance.ddieditor.model.lightxmlobject.LabelType> labels = ((ConceptualElement) element)
					.getValue().getLabelList();
			if (!labels.isEmpty()) {
				return XmlBeansUtil
						.getTextOnMixedElement(((ConceptualElement) element)
								.getValue().getLabelList().get(0));
			} else {
				return ((ConceptualElement) element).getValue().getId();
			}
		} else if (element instanceof MaintainableLightLabelQueryResult) {
			// TODO refactor MaintainableLightLabelQueryResult to include labels
			// for target element
			StringBuilder result = new StringBuilder();
			result.append(((MaintainableLightLabelQueryResult) element)
					.getMaintainableTarget());
			if (((MaintainableLightLabelQueryResult) element).getId() != null) {
				result.append(": ");
				result.append(((MaintainableLightLabelQueryResult) element)
						.getId());
			}
			return result.toString();
		}
		// java.util.List
		else if (element instanceof List<?>) {
			if (!((List<?>) element).isEmpty()) {
				Object obj = ((List<?>) element).get(0);
				// light xml objects
				if (obj instanceof LightXmlObjectType) {
					String label = ((LightXmlObjectType) obj).getElement();
					return label;
				}
			} else {
				// System.out.println("Empty list");
			}
		}

		// guard
		else {
			if (log.isWarnEnabled()) {
				log.warn(element.getClass().getName() + "is not supported",
						new Throwable());
			}
		}
		return new String();
	}
}
