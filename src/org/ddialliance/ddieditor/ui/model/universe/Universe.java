package org.ddialliance.ddieditor.ui.model.universe;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class Universe extends LabelDescription implements IModel {
	private UniverseDocument doc;
	private UniverseType type;

	public Universe(UniverseDocument doc, String parentId, String parentVersion)
			throws Exception {
		super(doc.getUniverse().getId(), doc.getUniverse().getVersion(),
				parentId, parentVersion, doc.getUniverse().getLabelList(), doc
						.getUniverse().getHumanReadableList());

		if (doc == null) {
			this.doc = UniverseDocument.Factory.newInstance();

		} else {
			this.doc = doc;
		}
		this.type = doc.getUniverse();
	}

	public List<String> getStudyUnitReference() throws Exception {

		LightXmlObjectListDocument listDoc = DdiManager.getInstance()
				.getStudyUnitsLight("", "", "", "");

		List<LightXmlObjectType> listLightXmlObjectListType = listDoc
				.getLightXmlObjectList().getLightXmlObjectList();

		List<String> result = new ArrayList<String>();
		// for each Study Unit
		for (LightXmlObjectType lightXmlObjectType : listLightXmlObjectListType) {
			MaintainableLabelQueryResult studyUnitMaintainableLabel = DdiManager
					.getInstance().getStudyLabel(lightXmlObjectType.getId(),
							lightXmlObjectType.getVersion(),
							lightXmlObjectType.getParentId(),
							lightXmlObjectType.getParentVersion());
			XmlObject[] xmlObjs = studyUnitMaintainableLabel
					.getSubElement("UniverseReference");
			// for each Universe Reference
			for (XmlObject xmlObject : xmlObjs) {
				String universeRef = XmlBeansUtil
						.getTextOnMixedElement(xmlObject);
				result.add(universeRef);
			}
		}
		return result;
	}

	public LabelType setDisplayLabel(String string) {
		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			type.getLabelList().add(labelType);
		}
		return null;
	}

	public LabelType setDisplayDescr(String string) {
		StructuredStringType descr = super.setDisplayDescr(string);
		if (descr != null) {
			type.getHumanReadableList().add(descr);
		}
		return null;
	}

	public UniverseDocument getDocument() {
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// not implemented
	}
}