package org.ddialliance.ddieditor.ui.model.category;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLabelQueryResult;
import org.ddialliance.ddieditor.ui.model.LabelDescriptionScheme;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Category scheme model
 */
public class CategoryScheme extends LabelDescriptionScheme {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			CategoryScheme.class);

	private MaintainableLabelQueryResult maintainableLabelQueryResult;

	/**
	 * Constructor
	 * @param maintainableLabelQueryResult
	 * @param parentId
	 * @param parentVersion
	 * @throws DDIFtpException
	 */
	public CategoryScheme(
			MaintainableLabelQueryResult maintainableLabelQueryResult,
			String parentId, String parentVersion) throws DDIFtpException {
		super(maintainableLabelQueryResult, parentId, parentVersion);
		this.maintainableLabelQueryResult = maintainableLabelQueryResult;
	}

	@Override
	public void validate() throws Exception {
		log.debug("CategoryScheme validation performed");

		// No error found:
		return;
	}

	@Override
	public CategorySchemeDocument getDocument() throws DDIFtpException {

		CategorySchemeDocument doc = CategorySchemeDocument.Factory
				.newInstance();
		CategorySchemeType type = doc.addNewCategoryScheme();

		super.getDocument(maintainableLabelQueryResult, type);

		type.setLabelArray(super.getLabelsAsArray());
		type.setDescriptionArray(super.getDescrsAsArray());
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
