package org.ddialliance.ddieditor.ui.model.instrument;

import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConditionalTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.ConditionalTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.StatementItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.ModelAccessor;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.util.LanguageUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

public class StatementItem extends Model {
	private StatementItemDocument doc;

	public StatementItem(StatementItemDocument doc, String parentId,
			String parentVersion) throws Exception {
		super(doc, parentId, parentVersion);

		if (doc == null) {
			this.doc = StatementItemDocument.Factory.newInstance();
			// add id and version
			setId("");
			setVersion("");
		} else {
			this.doc = doc;
		}
	}

	public StatementItemDocument getDocument() {
		return doc;
	}

	@Override
	public void executeChange(Object value, Class<?> type) throws Exception {
		// Text
		if (type.equals(TextType.class)) {
			XmlBeansUtil.setTextOnMixedElement(getText(), (String) value);
		}

		// Code
		if (type.equals(ProgrammingLanguageCodeType.class)) {
			getProgrammingLanguageCode().setStringValue((String) value);
		}

		// ProgrammingLanguage
		if (type.equals(ModelIdentifingType.Type_A.class)) {
			getProgrammingLanguageCode().setProgrammingLanguage((String) value);
		}

		// ReferenceType
		if (type.equals(ReferenceType.class)) {
			ReferenceType ref = getSourceQuestionReference();
			ModelAccessor.setReference(ref, (LightXmlObjectType) value);
		}
	}

	public ProgrammingLanguageCodeType getProgrammingLanguageCode() throws DDIFtpException {
		CodeType codeType = getCodeType();
		if (codeType == null) {
			return null;
		}
		return getProgrammingLanguageCode(codeType);
	}

	private CodeType getCodeType() throws DDIFtpException {
		DynamicTextType dynamicText = getDisplay();
		if (dynamicText == null) {
			return null;
		}
		ConditionalTextType conditionalText = getConditionalText(dynamicText);
		if (conditionalText == null) {
			return null;
		}
		return getExpression(conditionalText);
	}

	public DynamicTextType getDisplay() throws DDIFtpException {
		DynamicTextType dynamicText = null;
		if (doc.getStatementItem().getDisplayTextList().isEmpty() && create) {
			dynamicText = doc.getStatementItem().addNewDisplayText();
			XmlBeansUtil.addTranslationAttributes(dynamicText, Translator
					.getLocaleLanguage(), false, true);
		} else {
			dynamicText = (DynamicTextType) XmlBeansUtil.getLangElement(LanguageUtil.getDisplayLanguage(), doc
					.getStatementItem().getDisplayTextList());
		}
		return dynamicText;
	}

	public StructuredStringType getText() throws DDIFtpException {
		DynamicTextType dynamicText = getDisplay();
		if (dynamicText == null) {
			return null;
		}
		return getText(dynamicText);
	}

	public StructuredStringType getText(DynamicTextType dynamicText)
			throws DDIFtpException {
		List<LiteralTextType> result = new ArrayList<LiteralTextType>();
		for (TextType test : dynamicText.getTextList()) {
			if (test instanceof LiteralTextType) {
				result.add((LiteralTextType) test);
			}
		}
		StructuredStringType text = null;

		// literal text
		for (LiteralTextType literalTextType : result) {
			if (literalTextType.getText() != null) {
				text = literalTextType.getText();
			}
		}
		if (text == null && create) {
			LiteralTextType literalText = LiteralTextType.Factory.newInstance();
			text = literalText.addNewText();
			doc.getStatementItem().getDisplayTextList().get(0).getTextList()
					.add(literalText);
		}
		return text;
	}

	public ConditionalTextType getConditionalText(DynamicTextType dynamicText) {
		ConditionalTextType condition = null;
		for (TextType test : dynamicText.getTextList()) {
			if (test instanceof ConditionalTextType) {
				condition = (ConditionalTextType) test;
			}
		}
		if (condition == null && create) {
			condition = ConditionalTextDocument.Factory.newInstance()
					.addNewConditionalText();
			doc.getStatementItem().getDisplayTextList().get(0).getTextList()
					.add(condition);
		}
		return condition;
	}

	public CodeType getExpression(ConditionalTextType conditionalText) {
		if (conditionalText.getExpression() == null) {
			return create ? conditionalText.addNewExpression() : null;
		}
		return conditionalText.getExpression();
	}

	public ProgrammingLanguageCodeType getProgrammingLanguageCode(
			CodeType codeType) {
		if (codeType.getCodeList().isEmpty()) {
			return create ? codeType.addNewCode() : null;
		} else {
			return codeType.getCodeList().get(0);
		}
	}

	public ReferenceType getSourceQuestionReference() throws DDIFtpException {
		CodeType codeType = getCodeType();
		if (codeType == null) {
			return null;
		}
		if (codeType.getSourceQuestionReferenceList().isEmpty()) {
			if (create) {
				ReferenceType ref = codeType.addNewSourceQuestionReference();
				ref.addNewID();
				return ref;
			}
			return null;
		} else {
			return codeType.getSourceQuestionReferenceList().get(0);
		}
	}
}
