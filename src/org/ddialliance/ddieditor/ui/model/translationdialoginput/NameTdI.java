package org.ddialliance.ddieditor.ui.model.translationdialoginput;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.NameType;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;

public class NameTdI extends TranslationDialogInput {
	public NameTdI() {
		setTemplateElement(NameType.Factory.newInstance());
	}
}
