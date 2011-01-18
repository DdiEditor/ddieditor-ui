package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

/**
 * Question Item View.
 * 
 */
public class QuestionItemView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.QuestionItemView";

	public QuestionItemView() {
		super(
				ViewContentType.QuestionContent,
				Messages.getString("QuestionItemView.label.questionItemNavigationLabel.QuestionItemNavigation"),
				Messages.getString("QuestionItemView.label.selectLabel.NavigatorDescription"),
				Messages.getString("QuestionItemView.label.questionItemLabel.QuestionItem"),
				ElementType.QUESTION_SCHEME,
				Messages.getString("QuestionItemView.label.questionItemsTreeGroup.QuestionItemStructure"));
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// dnd
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] { LightXmlObjectTransfer
				.getInstance() };

		// drag
		this.treeViewer.addDragSupport(operations, transferTypes,
				new LightXmlObjectDragListener(treeViewer, ID));
	}
}
