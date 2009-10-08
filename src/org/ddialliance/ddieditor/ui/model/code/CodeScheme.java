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

import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.CodeSchemeDocument;
import org.ddialliance.ddi_3_0.xml.xmlbeans.logicalproduct.impl.CodeSchemeTypeImpl;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.Language;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CodeScheme extends LabelDescription {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeScheme.class);

	private CodeSchemeDocument codeSchemeDocument;
	private CodeSchemeTypeImpl codeSchemeTypeImpl;
	
	/**
	 * Constructor of Code Scheme
	 * 
	 * @param codeSchemeDocument
	 * @param parentId
	 * @param parentVersion
	 * @throws Exception
	 */
	public CodeScheme(CodeSchemeDocument codeSchemeDocument, String parentId, String parentVersion)
			throws Exception {
		
		super(codeSchemeDocument.getCodeScheme().getId(), codeSchemeDocument.getCodeScheme().getVersion(),
				parentId, parentVersion, codeSchemeDocument.getCodeScheme().getLabelList(),
				codeSchemeDocument.getCodeScheme().getDescriptionList());
		
		if (codeSchemeDocument == null) {
			// TODO Create new CodeScheme
			this.codeSchemeDocument = null;
		}
		this.codeSchemeDocument = codeSchemeDocument;
		this.codeSchemeTypeImpl = (CodeSchemeTypeImpl) codeSchemeDocument.getCodeScheme();
	}
	
	/**
	 * Get Code Scheme Document of Code Scheme.
	 * 
	 * @return CodeSchemeDocument
	 */
	public CodeSchemeDocument getCodeSchemeDocument() {
		return codeSchemeDocument;
	}
		
	/**
	 * Set label of Code Scheme.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setLabel(String string, Language language) {
		
		LabelType labelType = super.setLabel(string, language);
		if (labelType != null) {
			codeSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}
	
	/**
	 * Set Original Label of Code Scheme.
	 * 'Original' means not translated.
	 * 
	 * @param string
	 * @return LabelType (always null)
	 */
	public LabelType setLabel(String string) {

		LabelType labelType = super.setLabel(string);
		if (labelType != null) {
			codeSchemeTypeImpl.getLabelList().add(labelType);
		}
		return null;
	}
	
	/**
	 * Set Description of Code Scheme.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDescr(String string, Language language) {
		StructuredStringType descriptionType = super.setDescr(string, language);
		if (descriptionType != null) {
			codeSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
	}
	
	/**
	 * Set Original Description of Code Scheme.
	 * Original means not translated.
	 * 
	 * @param string
	 * @return StructuredStringType (always null)
	 */
	public StructuredStringType setDescr(String string) {

		StructuredStringType descriptionType = super.setDescr(string);
		if (descriptionType != null) {
			codeSchemeTypeImpl.getDescriptionList().add(descriptionType);
		}
		return null;
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

}
