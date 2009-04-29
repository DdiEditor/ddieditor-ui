package org.ddialliance.ddieditor.ui.dbxml;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.ddialliance.ddi_3_0.xml.xmlbeans.conceptualcomponent.impl.ConceptTypeImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.impl.CodeSchemeTypeImpl;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.CodeScheme;
import org.ddialliance.ddieditor.ui.model.Concept;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;


public class CodeSchemes extends XmlEntities {

	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeSchemes.class);

	/**
	 * Get CodeSchemes - light version
	 * 
	 * @param parentLogicalProduct
	 * @return List<LightXmlObjectType>
	 * @throws Exception
	 */
	static public List<LightXmlObjectType> getCodeSchemesLight(LightXmlObjectType parentLogicalProduct) throws Exception {

		log.debug("CodeSchemes.getCodeSchemesLight()");

		return CodeSchemes.getCodeSchemesLight("", "", parentLogicalProduct.getId(), parentLogicalProduct.getVersion());
	}

	static public List<LightXmlObjectType> getCodeSchemesLight(String id, String version, String parentId,
			String parentVersion) throws Exception {

		log.debug("CodeSchemes.getCodeSchemesLight()");

		List<LightXmlObjectType> lightXmlObjectTypeList = DdiManager.getInstance().getCodeSchemesLight(id, version,
				parentId, parentVersion).getLightXmlObjectList().getLightXmlObjectList();

		if (log.isDebugEnabled()) {
			for (LightXmlObjectType l : lightXmlObjectTypeList) {
				log.debug("CodeScheme Id: " + l.getId());
				int nLabel = l.getLabelList().size();
				log.debug("Number of Labels: " + nLabel);
				if (nLabel > 0) {
					XmlCursor xmlCursor = l.getLabelArray(0).newCursor();
					xmlCursor.toLastAttribute();
					xmlCursor.toNextToken();
					String result = xmlCursor.getChars();
					xmlCursor.dispose();
					log.debug("CodeScheme Label: " + result);
				}
			}
		}

		return lightXmlObjectTypeList;
	}
	
	static public CodeScheme getCodeScheme(String id, String version, String parentId, String parentVesion) throws Exception {
		log.debug("CodeSchemes.getCodeSheme("+id+")");

		CodeSchemeTypeImpl codeSchemeTypeImpl = (CodeSchemeTypeImpl) DdiManager.getInstance().getCodeScheme(id, version, parentId,
				parentVesion).getCodeScheme();

		CodeScheme codeScheme = new CodeScheme(codeSchemeTypeImpl);

		return codeScheme;
	}
}
