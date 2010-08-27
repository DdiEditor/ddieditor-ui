package org.ddialliance.ddieditor.ui.model.concept;

/**
 * Concept Scheme model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptSchemeType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class ConceptScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ConceptScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;

	
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param maintainableLabelQueryResult
	 * @throws XmlException
	 * @throws DDIFtpException
	 */
	public ConceptScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		super(id, version, parentId, parentVersion, "TODO", maintainableLabelQueryResult);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}



	/**
	 * Validates the Concept Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Concept Scheme validation performed");

		// No error found:
		return;
	}

	/**
	 * Provides the Concept Scheme Document.
	 */
	@Override
	public ConceptSchemeDocument getDocument() throws DDIFtpException {

		ConceptSchemeDocument doc = ConceptSchemeDocument.Factory.newInstance();
		ConceptSchemeType type = doc.addNewConceptScheme();

		super.getDocument(maintainableLabelQueryResult, type);
		
		type.setLabelArray(super.getLabels());
		type.setDescriptionArray(super.getDescrs());
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
