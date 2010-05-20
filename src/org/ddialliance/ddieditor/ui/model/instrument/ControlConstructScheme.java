package org.ddialliance.ddieditor.ui.model.instrument;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ControlConstructSchemeDocument;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class ControlConstructScheme extends Model {
	ControlConstructSchemeDocument doc;

	public ControlConstructScheme(ControlConstructSchemeDocument doc, String parentId,
			String parentVersion) {
		super(doc, parentId, parentVersion);
		if (doc == null) {
			this.doc = ControlConstructSchemeDocument.Factory.newInstance();
			// add id and version
			setId("");
			setVersion("");
		} else {
			this.doc = doc;
		}
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		// TODO Auto-generated method stub
		return null;
	}

}
