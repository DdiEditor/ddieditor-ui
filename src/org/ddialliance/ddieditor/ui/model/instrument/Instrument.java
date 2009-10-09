package org.ddialliance.ddieditor.ui.model.instrument;

import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.InstrumentDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.datacollection.InstrumentType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.util.FixedIn31;

public class Instrument extends LabelDescription implements IModel {
	private InstrumentDocument doc;
	private InstrumentType type;

	public Instrument(InstrumentDocument doc, String parentId,
			String parentVersion) throws Exception {
		super(doc.getInstrument().getId(), doc.getInstrument().getVersion(),
				parentId, parentVersion, FixedIn31.getLabelList(), FixedIn31
						.getDesrcList());

		if (doc == null) {
			this.doc = InstrumentDocument.Factory.newInstance();

		} else {
			this.doc = doc;
		}
		this.type = doc.getInstrument();
	}

	public InstrumentDocument getDocument() {
		return doc;
	}
}
