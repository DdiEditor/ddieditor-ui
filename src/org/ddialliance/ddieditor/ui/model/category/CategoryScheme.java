package org.ddialliance.ddieditor.ui.model.category;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CategoryScheme extends LabelDescription {
	private static Log log = LogFactory
			.getLog(LogType.SYSTEM, CategoryScheme.class);

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
	public CategoryScheme(String id, String version, String parentId,
			String parentVersion, String agency,
			MaintainableLabelQueryResult maintainableLabelQueryResult)
			throws DDIFtpException {
		
		super(id, version, parentId, parentVersion, "TODO", maintainableLabelQueryResult);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}

	/**
	 * Validates the Category Scheme before it is saved. It e.g. checks if all
	 * mandatory attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("CategoryScheme validation performed");

		// No error found:
		return;
	}

	/**
	 * Provides the Category Scheme Document.
	 */
	@Override
	public CategorySchemeDocument getDocument() throws DDIFtpException {

		CategorySchemeDocument doc = CategorySchemeDocument.Factory.newInstance();
		CategorySchemeType type = doc.addNewCategoryScheme();

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
