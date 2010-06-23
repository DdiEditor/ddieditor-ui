package org.ddialliance.ddieditor.ui.model.translationdialoginput;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.StructuredStringType;
import org.ddialliance.ddieditor.ui.dialogs.TranslationDialogInput;

public class DescriptionTdI extends TranslationDialogInput {
	public DescriptionTdI() {
		setTemplateElement(StructuredStringType.Factory.newInstance());
	}
}
