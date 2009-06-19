package org.ddialliance.ddieditor.ui.dbxml;

import java.io.File;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.ConceptDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.ConceptType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.CodeSchemeType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.impl.CodeSchemeTypeImpl;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.model.CodeScheme;
import org.ddialliance.ddieditor.ui.model.Concept;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;


public class CodeSchemes extends XmlEntities {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemes.class);

	/**
	 * Get Code Schemes - light version
	 * 
	 * @param parentConceptScheme
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getCodeSchemesLight(LightXmlObjectType parentConceptScheme) throws Exception {

		log.debug("CodeSchemes.getCodeSchemesLight()");

		return CodeSchemes.getCodeSchemesLight("", "", parentConceptScheme.getId(), parentConceptScheme.getVersion());
	}

	/**
	 * 
	 * Get Light version of Code Schemes
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getCodeSchemesLight(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.debug("CodeSchemes.getCodeSchemesLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCodeSchemesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		return lightXmlObjectTypeList;
	}
	
	/**
	 * Create Code Schemes
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

		CodeSchemeType conceptType = codeSchemeDocument.addNewCodeScheme();
		conceptType.setId(id);
		if (version != null) {
			conceptType.setVersion(version);
		}
		
		CodeScheme codeScheme = new CodeScheme(codeSchemeDocument, parentId, parentVersion);

		return codeScheme;
	}

	/**
	 * 
	 * Get Code Scheme
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @return CodeScheme
	 * @throws Exception
	 */
	static public CodeScheme getCodeScheme(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("CodeSchemes.getCodeScheme("+id+")");

		CodeSchemeDocument codeSchemeDocument = DdiManager.getInstance().getCodeScheme(id, version, parentId, parentVersion);
		CodeScheme codeScheme = new CodeScheme(codeSchemeDocument, parentId, parentVersion);
		
		return codeScheme;
	}

	/**
	 * Create new DBXML Code Scheme
	 * 
	 * @param codeScheme
	 *            Code Scheme instance
	 * @param parentId
	 *            Id. of Code Scheme
	 * @param parentVersion
	 *            Version of Code Scheme
	 * @throws DDIFtpException
	 */
	static public void create(CodeScheme codeScheme) throws DDIFtpException {

		try {
//			DdiManager.getInstance().createElement(codeScheme.getCodeSchemeDocument(),
//					codeScheme.getParentId(), codeScheme.getParentVersion(), "datacollection__DataCollection");
			DdiManager.getInstance().createElement(codeScheme.getCodeSchemeDocument(),
					codeScheme.getParentId(), codeScheme.getParentVersion(), "ConceptualComponent");
		} catch (DDIFtpException e) {
			log.error("Create DBXML Code Scheme error: " + e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}

	/**
	 * 
	 * Update DBXML Code Scheme corresponding to the given Code Scheme instance
	 * 
	 * @param codeScheme
	 *            Code Scheme instance
	 * @throws DDIFtpException
	 */
	static public void update(CodeScheme codeScheme) throws DDIFtpException {
		// TODO Version Control - not supported
		log.debug("Update DBXML Code Scheme:\n" + codeScheme.getCodeSchemeDocument());
		try {
			DdiManager.getInstance().updateElement(codeScheme.getCodeSchemeDocument(), codeScheme.getId(),
					codeScheme.getVersion());
		} catch (DDIFtpException e) {
			log.error("Update DBXML Code Scheme error: " + e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
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
	static public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		log.debug("Delete DBXML CodeScheme Scheme");
		CodeScheme codeScheme = getCodeScheme(id, version, parentId, parentVersion);
		try {
			DdiManager.getInstance().deleteElement(codeScheme.getCodeSchemeDocument(), codeScheme.getParentId(),
					codeScheme.getParentVersion(), "CodeSchemeScheme");
		} catch (DDIFtpException e) {
			log.error("Delete DBXML Code Scheme Scheme error: " + e.getMessage());
			PersistenceManager.getInstance().rollbackWorkingResource();
			throw new DDIFtpException(e.getMessage());
		}
		PersistenceManager.getInstance().commitWorkingResource();
		// TODO When is xml-file updated - when object saved?
		if (xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
