package org.ddialliance.ddieditor.ui.dbxml.code;

/**
 * Code Schemes (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.code.CodeScheme;
import org.ddialliance.ddieditor.ui.model.concept.ConceptScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

import sun.security.action.GetLongAction;


public class CodeSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeDao.class);

	/**
	 * 
	 * Get Light Code Scheme List
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getCodeSchemesLight(String id, String version) throws Exception {

		log.debug("CodeScheme.getCodeSchemesLight(). Id: "+id+" Version: "+version);

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCodeSchemesLight(id, version,
				null, null).getLightXmlObjectList().getLightXmlObjectList();
		return lightXmlObjectTypeList;
	}



	/**
	 * Get Code Scheme by Id
	 * 
	 * @param id
	 * @param parentId
	 * @return CodeSchemeType
	 * @throws Exception
	 */
	public CodeSchemeType getCodeSchemeById(String id, String parentId) throws Exception {

		log.debug("CodeScheme.getCodeSchemeById()");
		return DdiManager.getInstance().getCodeScheme(id, null, parentId, null).getCodeScheme();
	}

	/**
	 * 
	 * Update DBXML Code Scheme corresponding to the given CodeScheme
	 * instance
	 * 
	 * @param CodeScheme
	 *            Code Scheme instance
	 * @throws DDIFtpException
	 */
	static public void update(CodeScheme CodeScheme) throws DDIFtpException {

		// TODO Version Control - not supported
		log.debug("Update DBXML Code Scheme:\n" + CodeScheme.getCodeSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(CodeScheme.getCodeSchemeDocument(), CodeScheme.getId(),
					CodeScheme.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Code Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
	}

	/**
	 * 
	 * Delete DBXML Code Scheme
	 * 
	 * @param id
	 *            Identification
	 * @param version
	 *            Version
	 * @param parentId
	 *            Parent Identification
	 * @param parentVersion
	 *            Parent Version
	 * @throws Exception
	 */
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("Delete DBXML Code Scheme");
		IModel  model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance()
				.deleteElement(model.getDocument(), model.getParentId(),
						model.getParentVersion(), "logicalproduct__LogicalProduct");
	}

	@Override
	public IModel create(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("CodeSchemeDao.create()");
		CodeSchemeDocument doc = CodeSchemeDocument.Factory.newInstance();
		IdentificationManager.getInstance().addIdentification(
				doc.addNewCodeScheme(),
				ElementType.getElementType("CodeScheme").getIdPrefix(), null);
		IdentificationManager.getInstance().addVersionInformation(
				doc.getCodeScheme(), null, null);
		CodeScheme codeScheme = new CodeScheme(doc, parentId, parentVersion);
		return codeScheme;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {

		log.debug("CodeSchemeDao.create()");
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"logicalproduct__LogicalProduct");
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parentCodeScheme) throws Exception {

		log.debug("CodeSchemes.getLightXmlObject()");
		return getLightXmlObject("", "", parentCodeScheme.getId(), parentCodeScheme.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
			throws Exception {

		log.debug("CodeSchemes.getLightXmlObject()");
		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCodeSchemesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();
		return lightXmlObjectTypeList;
	}

	@Override
	public IModel getModel(String id, String version, String parentId, String parentVersion) throws Exception {

		log.debug("CodeSchemeDao.getModel()");
		CodeSchemeDocument codeSchemeDocument = DdiManager.getInstance().getCodeScheme(id, version,
				parentId, parentVersion);
		CodeScheme codeScheme = new CodeScheme(codeSchemeDocument, parentId, parentVersion);
		return codeScheme;
	}

	@Override
	public void update(IModel model) throws DDIFtpException {

		DdiManager.getInstance().updateElement(model.getDocument(),
				model.getId(), model.getVersion());
	}
}
