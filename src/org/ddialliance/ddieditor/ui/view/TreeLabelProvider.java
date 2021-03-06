package org.ddialliance.ddieditor.ui.view;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.CustomType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddieditor.util.LightXmlObjectUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
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
		// LightXmlObjectType
		if (element instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObject = (LightXmlObjectType) element;
			if (lightXmlObject.getElement().equals("Variable")) {
				StringBuilder result = new StringBuilder();
				
				// name
				for (CustomType cus : LightXmlObjectUtil.getCustomListbyType(
						lightXmlObject, "Name")) {
					result.append(XmlBeansUtil.getTextOnMixedElement(cus));
				}

				// label
				try {
					String l = XmlBeansUtil
							.getTextOnMixedElement((XmlObject) XmlBeansUtil
									.getDefaultLangElement(lightXmlObject
											.getLabelList()));
					if (l != null && !l.equals("")) {
						result.append(" ");
						result.append(l);
					}

				} catch (Exception e) {
					Editor.showError(e, getClass().getName());
				}

				if (result.length() == 0) {
					result.append(lightXmlObject.getElement() + ": "
							+ lightXmlObject.getId());
				}
				return result.toString();
			}
			if (lightXmlObject.getLabelList().size() > 0) {
				try {
					Object obj = XmlBeansUtil.getLangElement(
							LanguageUtil.getDisplayLanguage(),
							lightXmlObject.getLabelList());
					return XmlBeansUtil.getTextOnMixedElement((XmlObject) obj);
				} catch (DDIFtpException e) {
					Editor.showError(e, getClass().getName());
				}

			} else {
				// No label specified - use ID instead:
				return lightXmlObject.getElement() + ": "
						+ lightXmlObject.getId();
			}
		} 
		
		// DDIResourceType
		else if (element instanceof DDIResourceType) {
			return ((DDIResourceType) element).getOrgName();
		} 
		
		// StorageType
		else if (element instanceof StorageType) {
			return ((StorageType) element).getId();
		} 
		
		// ConceptualType
		else if (element instanceof ConceptualType) {
			return Translator.trans(((ConceptualType) element).name());
		} 
		
		// ConceptualElement
		else if (element instanceof ConceptualElement) {
			List<org.ddialliance.ddieditor.model.lightxmlobject.LabelType> labels = ((ConceptualElement) element)
					.getValue().getLabelList();
			if (!labels.isEmpty()) {
				return XmlBeansUtil
						.getTextOnMixedElement(((ConceptualElement) element)
								.getValue().getLabelList().get(0));
			} else {
				return ((ConceptualElement) element).getValue().getId();
			}
		} 
		
		// MaintainableLightLabelQueryResult
		else if (element instanceof MaintainableLightLabelQueryResult) {
			try {
				return ((MaintainableLightLabelQueryResult) element)
						.getTargetLabel();
			} catch (DDIFtpException e) {
				Editor.showError(e, getClass().getName());
			}
			return "";
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
				DDIFtpException e = new DDIFtpException("Empty list",
						new Throwable());
				Editor.showError(e, getClass().getName());
			}
		}

		// guard
		else {
			if (log.isWarnEnabled()) {
				DDIFtpException e = new DDIFtpException(element.getClass()
						.getName() + "is not supported", new Throwable());
				Editor.showError(e, getClass().getName());
			}
		}
		return new String();
	}
}
