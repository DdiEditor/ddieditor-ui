package org.ddialliance.ddieditor.ui.model.question;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.QuestionItemDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;

public class QuestionItemTdI extends TranslationDialogInput {
	public QuestionItemTdI() {
		setTextElement("Text");

		QuestionItemDocument qDoc = QuestionItemDocument.Factory.newInstance();
		DynamicTextType dynamicText = qDoc.addNewQuestionItem()
				.addNewQuestionText();
		TextType textType = dynamicText.addNewText();
		LiteralTextType lTextType = (LiteralTextType) textType.substitute(
				LiteralTextDocument.type.getDocumentElementName(),
				LiteralTextType.type);
		lTextType.addNewText();
		setTemplateElement(qDoc.getQuestionItem().getQuestionTextArray(0));
	}
}
