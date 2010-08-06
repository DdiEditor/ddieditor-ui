package org.ddialliance.ddieditor.ui.model.category;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategoryDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategoryType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class Category extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, Category.class);

	private CategoryDocument categoryDocument;
	private CategoryType categoryTypeImpl;

	/**
	 * Constructor
	 * 
	 * @param categoryDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public Category(CategoryDocument categoryDocument, String parentId,
			String parentVersion) throws Exception {

		super(categoryDocument.getCategory().getId(), categoryDocument
				.getCategory().getVersion(), parentId, parentVersion,
				categoryDocument.getCategory().getLabelList(), categoryDocument
						.getCategory().getDescriptionList());

		if (categoryDocument == null) {
			// TODO Create new Category
			this.categoryDocument = null;
		}
		this.categoryDocument = categoryDocument;
		this.categoryTypeImpl = (CategoryType) categoryDocument.getCategory();
	}

	/**
	 * Get Category Document of Category.
	 * 
	 * @return CategoryDocument
	 */
	public CategoryDocument getCategoryDocument() {
		return categoryDocument;
	}

	/**
	 * Set Display Label of Category.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setDisplayLabel(String string) {

		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			categoryTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}

	/**
	 * Set Display Description of Category.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDisplayDescr(String string) {

		StructuredStringType descriptionType = super.setDisplayDescr(string);
		if (descriptionType != null) {
			categoryTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}

	/**
	 * Validates the Category before it is saved. It e.g. checks if all mandatory
	 * attributes has been given.
	 * 
	 * @throws Exception
	 */
	public void validate() throws Exception {
		log.debug("Category validation performed");

		// No error found:
		return;
	}

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return this.categoryDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}

}
