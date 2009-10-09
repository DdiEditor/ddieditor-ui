package org.ddialliance.ddieditor.ui.model.instrument;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionConstructDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.QuestionConstructType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.util.FixedIn31;

public class QuestionConstruct extends LabelDescription {
	private QuestionConstructDocument doc;
	private QuestionConstructType type;

	public QuestionConstruct(QuestionConstructDocument doc, String parentId,
			String parentVersion) throws Exception {
		super(doc.getQuestionConstruct().getId(), doc.getQuestionConstruct().getVersion(),
				parentId, parentVersion, FixedIn31.getLabelList(), FixedIn31
						.getDesrcList());

		if (doc == null) {
			this.doc = QuestionConstructDocument.Factory.newInstance();
		}
		this.doc = doc;
		this.type = doc.getQuestionConstruct();
	}

	public XmlObject getDocument() {
		return doc;
	}
}
