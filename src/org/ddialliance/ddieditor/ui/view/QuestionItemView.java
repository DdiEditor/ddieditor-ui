package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.Translator;
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
				Translator
						.trans("QuestionItemView.label.questionItemNavigationLabel.QuestionItemNavigation"),
				Translator
						.trans("QuestionItemView.label.selectLabel.NavigatorDescription"),
				Translator
						.trans("QuestionItemView.label.questionItemLabel.QuestionItem"),
				ElementType.QUESTION_SCHEME,
				Translator
						.trans("QuestionItemView.label.questionItemsTreeGroup.QuestionItemStructure"),
				ID);
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
