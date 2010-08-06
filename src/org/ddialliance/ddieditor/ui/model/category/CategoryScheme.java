package org.ddialliance.ddieditor.ui.model.category;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CategorySchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.impl.CategorySchemeTypeImpl;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CategoryScheme extends LabelDescription {
	private static Log log = LogFactory
			.getLog(LogType.SYSTEM, CategoryScheme.class);

	private CategorySchemeDocument categorySchemeDocument;
	private CategorySchemeTypeImpl categorySchemeTypeImpl;

	/**
	 * Constructor of Category Scheme
	 * 
	 * @param categorySchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public CategoryScheme(CategorySchemeDocument categorySchemeDocument, String parentId,
			String parentVersion) throws Exception {

		super(categorySchemeDocument.getCategoryScheme().getId(), categorySchemeDocument
				.getCategoryScheme().getVersion(), parentId, parentVersion,
				categorySchemeDocument.getCategoryScheme().getLabelList(),
				categorySchemeDocument.getCategoryScheme().getDescriptionList());

		if (categorySchemeDocument == null) {
			// TODO Create new CategoryScheme
			this.categorySchemeDocument = null;
		}
		this.categorySchemeDocument = categorySchemeDocument;
		this.categorySchemeTypeImpl = (CategorySchemeTypeImpl) categorySchemeDocument
				.getCategoryScheme();
	}

	/**
	 * Get Category Scheme Document of Category Scheme.
	 * 
	 * @return CategorySchemeDocument
	 */
	public CategorySchemeDocument getCategorySchemeDocument() {
		return categorySchemeDocument;
	}

	/**
	 * Set Display Label of Category Scheme.	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setDisplayLabel(String string) {

		LabelType labelType = super.setDisplayLabel(string);
		if (labelType != null) {
			categorySchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
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

	@Override
	public XmlObject getDocument() throws DDIFtpException {
		return categorySchemeDocument;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// TODO Auto-generated method stub

	}
}
