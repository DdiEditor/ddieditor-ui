package org.ddialliance.ddieditor.ui.model.translationdialoginput;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;

public class LabelTdI extends TranslationDialogInput {
	public LabelTdI() {
		setTemplateElement(LabelType.Factory.newInstance());
	}
}
