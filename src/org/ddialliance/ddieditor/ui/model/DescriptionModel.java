package org.ddialliance.ddieditor.ui.model;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class DescriptionModel extends Model {

	public DescriptionModel(XmlObject xmlObject, String parentId, String parentVersion) {
		super(xmlObject, parentId, parentVersion);
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
