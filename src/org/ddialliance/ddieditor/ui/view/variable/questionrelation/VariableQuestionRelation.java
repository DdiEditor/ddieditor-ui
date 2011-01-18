package org.ddialliance.ddieditor.ui.view.variable.questionrelation;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;

public class VariableQuestionRelation {
	LightXmlObjectType vari;
	LightXmlObjectType quei;

	public VariableQuestionRelation() {
	}

	public VariableQuestionRelation(LightXmlObjectType vari,
			LightXmlObjectType quei) {
		super();
		this.vari = vari;
		this.quei = quei;
	}
}
