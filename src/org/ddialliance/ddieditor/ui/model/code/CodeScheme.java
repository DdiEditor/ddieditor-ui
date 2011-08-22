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

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.SchemeReferenceType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class CodeScheme extends Model implements IModel {
	private static Log log = LogFactory
			.getLog(LogType.SYSTEM, CodeScheme.class);
	private CodeSchemeDocument doc;

	/**
	 * Constructor
	 * 
	 * @param questionItemDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public CodeScheme(CodeSchemeDocument doc, String parentId,
			String parentVersion) throws Exception {

		super(doc.getCodeScheme(), parentId, parentVersion);

		if (doc == null) {
			this.doc = CodeSchemeDocument.Factory.newInstance();
			// add id and version
			setId("");
			setVersion("");
		} else {
			this.doc = doc;
		}
	}

	public SchemeReferenceType getCategorySchemeReference() {
		return doc.getCodeScheme().getCategorySchemeReference();
	}

	/**
	 * 
	 * @param index
	 *            index in list of code
	 * @param categoryReference
	 * @param value
	 */
	public void addCode(int index, String categoryReference, String value) {

		if (doc.getCodeScheme() == null) {
			doc.addNewCodeScheme();
		}

		if ((index < 0)
				|| (doc.getCodeScheme().getCodeList().size() == 0 && index != 0)
				|| (doc.getCodeScheme().getCodeList().size() > index)) {
			// TODO error reporting
			System.out.println("Index error");
		}
		CodeType codeType = CodeType.Factory.newInstance();
		codeType.addNewCategoryReference().addNewID()
				.setStringValue(categoryReference);
		codeType.setValue(value);
		doc.getCodeScheme().getCodeList().add(index, codeType);
	}

	public void addCode(String categoryReference, String value) {
		int index = -1;
		if (doc.getCodeScheme() == null) {
			index = 0;
		} else {
			index = doc.getCodeScheme().getCodeList().size();
		}
		addCode(index, categoryReference, value);
	}

	public List<CodeType> getCodes() {
		return doc.getCodeScheme().getCodeList();
	}

	@Override
	public void validate() throws Exception {
		// not implemented
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		XmlObject xml = (XmlObject) value;
		if (type.equals(ReferenceType.class)) {
			SchemeReferenceType ref = doc.getCodeScheme()
					.getCategorySchemeReference();
			String id = XmlBeansUtil.getXmlAttributeValue(xml.xmlText(),
					"id=\"");
			if (ref == null) {
				doc.getCodeScheme().addNewCategorySchemeReference().addNewID()
						.setStringValue(id);
			} else {
				if (ref.getIDList().size() == 0) {
					ref.addNewID();
				}
				if (id == null) {
					// remove ref
					doc.getCodeScheme().unsetCategorySchemeReference();
				} else {
					// set ref
					ref.getIDList().get(0).setStringValue(id);
				}
			}
		} else {
			log.debug("******** Class type not supported: " + type
					+ " ********");
		}
	}

	@Override
	public CodeSchemeDocument getDocument() throws DDIFtpException {
		return doc;
	}
}
