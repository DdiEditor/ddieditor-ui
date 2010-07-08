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

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.code.CodeScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;


public class CodeSchemeDao implements IDao {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemeDao.class);

	/**
	 * Get Light Code Scheme List
	 * 
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getCodeSchemesLight() throws Exception {
		return getCodeSchemesLight("", "");
	}

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

		DdiManager ddiManager = DdiManager.getInstance();

		LightXmlObjectListDocument listDoc = ddiManager.getCodeSchemesLight(id, version, null, null);

		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();

		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();

		return listLightXmlObjectListType;
	}

	/**
	 * 
	 * Get Light Code List
	 * 
	 * - get children (Codes) of given Code Scheme
	 * 
	 * @param id
	 * @param version
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	public static List<LightXmlObjectType> getCodesLight(String id, String version) throws Exception {
		log.debug("CodeScheme.getCodesLight(). Id: "+id+" Version: "+version);

//		DdiManager ddiManager = DdiManager.getInstance();
//
//		LightXmlObjectListDocument listDoc = ddiManager.getCodeLight("", "", id, version);
//
//		LightXmlObjectListType lightXmlObjectListType = listDoc.getLightXmlObjectList();
//
//		List<LightXmlObjectType> listLightXmlObjectListType = lightXmlObjectListType.getLightXmlObjectList();
//
//		return listLightXmlObjectListType;
		return null;
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
	 * Create Code Scheme object
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return CodeScheme
	 * @throws Exception
	 */
	static public CodeScheme createCodeScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("CodeSchemes.createCodeScheme()");
		
		CodeSchemeDocument codeSchemeDocument = CodeSchemeDocument.Factory.newInstance();

		CodeSchemeType CodeSchemeType = codeSchemeDocument.addNewCodeScheme();
		CodeSchemeType.setId(id);
		if (version != null) {
			CodeSchemeType.setVersion(version);
		}

		CodeScheme codeScheme = new CodeScheme(codeSchemeDocument, parentId, parentVersion);

		return codeScheme;
	}

	/**
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return CodeScheme
	 * @throws Exception
	 */
	static public CodeScheme getCodeScheme(String id, String version, String parentId, String parentVersion)
			throws Exception {
		log.debug("CodeSchemes.getCodeScheme()");

		CodeSchemeDocument CodeSchemeDocument = DdiManager.getInstance().getCodeScheme(id, version,
				parentId, parentVersion);

		CodeScheme CodeScheme = new CodeScheme(CodeSchemeDocument, parentId, parentVersion);

		return CodeScheme;
	}

	/**
	 * Create new DBXML Code Scheme
	 * 
	 * @param CodeScheme
	 *            Code scheme instance
	 * @param parentId
	 *            Id. of Logical Product
	 * @param parentVersion
	 *            Version of Logical Product
	 * @throws DDIFtpException
	 */
	static public void create(CodeScheme CodeScheme) throws DDIFtpException {
		try {
			DdiManager.getInstance().createElement(CodeScheme.getCodeSchemeDocument(),
					CodeScheme.getParentId(), CodeScheme.getParentVersion(), "logicalproduct__LogicalProduct");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Code Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
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
		CodeScheme CodeScheme = getCodeScheme(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(CodeScheme.getCodeSchemeDocument(), CodeScheme.getParentId(),
					CodeScheme.getParentVersion(), "logicalproduct__LogicalProduct");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Code Scheme error: " + e.getMessage());
			
			throw new DDIFtpException(e.getMessage());
		}
	}

	@Override
	public IModel create(String id, String version, String parentId, String parentVersion) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		// TODO Auto-generated method stub
		
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
