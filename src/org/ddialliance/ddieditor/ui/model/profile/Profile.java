package org.ddialliance.ddieditor.ui.model.profile;

import org.ddialliance.ddi3.xml.xmlbeans.ddiprofile.DDIProfileDocument;
import org.ddialliance.ddi3.xml.xmlbeans.ddiprofile.DDIProfileType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class Profile extends Model {
	private DDIProfileDocument doc;
	private DDIProfileType type;

	public Profile(String id, String version, String parentId,
			String parentVersion) {
		super(id, version, parentId, parentVersion);
		// TODO Auto-generated constructor stub
	}

	@Override
	public DDIProfileDocument getDocument() throws DDIFtpException {
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
