package org.ddialliance.ddieditor.ui.model.universe;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescription;

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