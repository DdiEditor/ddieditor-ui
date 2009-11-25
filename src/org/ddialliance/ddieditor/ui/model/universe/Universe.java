package org.ddialliance.ddieditor.ui.model.universe;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.util.FixedIn31;

public class Universe extends LabelDescription implements IModel {
		private UniverseDocument doc;
		private UniverseType type;

		public Universe(UniverseDocument doc, String parentId,
				String parentVersion) throws Exception {
			super(doc.getUniverse().getId(), doc.getUniverse().getVersion(),
					parentId, parentVersion, FixedIn31.getLabelList(), FixedIn31
							.getDesrcList());

			if (doc == null) {
				this.doc = UniverseDocument.Factory.newInstance();

			} else {
				this.doc = doc;
			}
			this.type = doc.getUniverse();
		}

		public UniverseDocument getDocument() {
			return doc;
		}
	}
