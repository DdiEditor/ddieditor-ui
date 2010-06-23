package org.ddialliance.ddieditor.ui.dialogs;

import org.apache.xmlbeans.XmlObject;

public class TranslationDialogInput {
	private XmlObject templateElement = null;
	private String textElement = null;

	public TranslationDialogInput() {
	}

	public TranslationDialogInput(XmlObject templateElement, String textElement) {
		this.templateElement = templateElement;
		this.textElement = textElement;
	}

	public XmlObject getTemplateElement() {
		return templateElement;
	}

	public void setTemplateElement(XmlObject templateElement) {
		this.templateElement = templateElement;
	}

	public String getTextElement() {
		return textElement;
	}

	public void setTextElement(String textElement) {
		this.textElement = textElement;
	}
}
