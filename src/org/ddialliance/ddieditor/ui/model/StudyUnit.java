package org.ddialliance.ddieditor.ui.model;

/**
 * Study Unit model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddi_3_0.xml.xmlbeans.studyunit.StudyUnitDocument;
import org.ddialliance.ddieditor.persistenceaccess.SchemeQueryResult;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class StudyUnit extends Model {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, StudyUnit.class);
	private StudyUnitDocument studyUnitDocument;
//	private StudyUnitTypeImpl studyUnitTypeImpl;
	private SchemeQueryResult schemeQueryResult;


	public StudyUnit(StudyUnitDocument studyUnitDocument, String parentId, String parentVersion,
			SchemeQueryResult schemeQueryResult) {
		super(studyUnitDocument.getStudyUnit().getId(), studyUnitDocument.getStudyUnit().getVersion(), parentId,
				parentVersion);
		this.studyUnitDocument = studyUnitDocument;
		this.schemeQueryResult = schemeQueryResult;
	}
	
	public String getAttribute(String attributeName) {
		for (int i = 0; i < schemeQueryResult.getElementNames().length; i++) {
			if (schemeQueryResult.getElementNames()[i].equals(attributeName)) {
				log.debug("StudyUnit.getAttribute(): attributeName: " + attributeName + " value: '"
						+ schemeQueryResult.getElements()[i].toString() + "");
				return schemeQueryResult.getElements()[i].toString();
			}
		}
		return "";
	}
	
	/**
	 * Get Study Unit Document of Study Unit.
	 * 
	 * @return StudyUnitDocument
	 */
	public StudyUnitDocument getStudyUnitDocument() {
		return studyUnitDocument;
	}
}
