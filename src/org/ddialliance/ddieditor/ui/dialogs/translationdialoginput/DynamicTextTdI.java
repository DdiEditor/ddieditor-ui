package org.ddialliance.ddieditor.ui.dialogs.translationdialoginput;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DynamicTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextDocument;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.LiteralTextType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.TextType;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;

public class DynamicTextTdI extends TranslationDialogInput {
	public DynamicTextTdI() {
		setTextElement("Text");
		DynamicTextType dynamicText = DynamicTextType.Factory.newInstance();
		TextType textType = dynamicText.addNewText();
		LiteralTextType lTextType = (LiteralTextType) textType.substitute(
				LiteralTextDocument.type.getDocumentElementName(),
				LiteralTextType.type);
		lTextType.addNewText();
		setTemplateElement(dynamicText);
	}
}
