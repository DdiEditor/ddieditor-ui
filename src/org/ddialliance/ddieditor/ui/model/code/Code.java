package org.ddialliance.ddieditor.ui.model.code;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class Code extends Model {

	public Code(String id, String version, String parentId,
			String parentVersion, String agency) {
		super(id, version, parentId, parentVersion, agency);
		// TODO Auto-generated constructor stub
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
