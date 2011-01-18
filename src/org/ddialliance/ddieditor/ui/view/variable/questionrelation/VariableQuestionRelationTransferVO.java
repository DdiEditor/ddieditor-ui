package org.ddialliance.ddieditor.ui.view.variable.questionrelation;


public class VariableQuestionRelationTransferVO {
	public String rcpPartId;
	public int dragedFromPosition;
	public VariableQuestionRelation variQueiRelation;

	public VariableQuestionRelationTransferVO() {
	}

	public VariableQuestionRelationTransferVO(String rcpPartId, int dragedFromPosition,
			VariableQuestionRelation variQueiRelation) {
		this.rcpPartId = rcpPartId;
		this.dragedFromPosition = dragedFromPosition;
		this.variQueiRelation = variQueiRelation;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("DragedFromPosition: ");
		result.append(dragedFromPosition);
		result.append(", variQueiRelation: ");
		result.append(variQueiRelation);
		result.append(", rcpPartId: ");
		result.append(rcpPartId);
		return result.toString();
	}
}
