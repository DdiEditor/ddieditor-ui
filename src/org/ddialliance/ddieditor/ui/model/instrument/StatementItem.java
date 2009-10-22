package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.StatementItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.StatementItemType;
import org.ddialliance.ddieditor.ui.model.Model;

public class StatementItem extends Model {
	private StatementItemDocument doc;
	private StatementItemType type;

	public StatementItem(StatementItemDocument doc, String parentId,
			String parentVersion) throws Exception {
		super(doc.getStatementItem().getId(), doc.getStatementItem()
				.getVersion(), parentId, parentVersion);

		if (doc == null) {
			this.doc = StatementItemDocument.Factory.newInstance();

		} else {
			this.doc = doc;
		}
		this.type = doc.getStatementItem();
	}

	public StatementItemDocument getDocument() {
		return doc;
	}
}
