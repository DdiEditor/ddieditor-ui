package org.ddialliance.ddieditor.ui.dbxml.profile;

import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddiftp.util.DDIFtpException;

public class ProfileDao implements IDao {

	@Override
	public void create(IModel arg0) throws DDIFtpException {
		// not implemented		
	}

	@Override
	public IModel create(String arg0, String arg1, String arg2, String arg3)
			throws Exception {
		// not implemented
		return null;
	}

	@Override
	public void delete(String arg0, String arg1, String arg2, String arg3)
			throws Exception {
		// not implemented		
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType arg0)
			throws Exception {
		// not implemented
		return null;
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String arg0, String arg1,
			String arg2, String arg3) throws Exception {
		// not implemented
		return null;
	}

	@Override
	public IModel getModel(String arg0, String arg1, String arg2, String arg3)
			throws Exception {
		// not implemented
		return null;
	}

	@Override
	public void update(IModel arg0) throws DDIFtpException {
		// not implemented		
	}
}
