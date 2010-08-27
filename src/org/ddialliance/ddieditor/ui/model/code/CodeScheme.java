package org.ddialliance.ddieditor.ui.model.code;

/**
 * Code Scheme model.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.impl.CodeSchemeTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CodeScheme extends LabelDescription {
	private static Log log = LogFactory
			.getLog(LogType.SYSTEM, CodeScheme.class);

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
	public CodeScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		super(id, version, parentId, parentVersion, "TODO", maintainableLabelQueryResult);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}

	/**
	 * Validates the Code Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("CodeScheme validation performed");

		// No error found:
		return;
	}

	/**
	 * Provides the Code Scheme Document.
	 */
	@Override
	public CodeSchemeDocument getDocument() throws DDIFtpException {

		CodeSchemeDocument doc = CodeSchemeDocument.Factory.newInstance();
		CodeSchemeType type = doc.addNewCodeScheme();

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
