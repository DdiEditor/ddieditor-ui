package org.ddialliance.ddieditor.ui.view;

/**
 * Category View.
 * 
 */
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectDragListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;

public class CategoryView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.CategoryView";

	public CategoryView() {
		super(
				ViewContentType.CategoryContent,
				Messages
						.getString("CategoryView.label.categoryNavigationLabel.CategoryNavigation"),
				Messages
						.getString("CategoryView.label.selectLabel.NavigatorDescription"),
				Messages.getString("CategoryView.label.categoryLabel.Category"),
				ElementType.CATEGORY_SCHEME,
				Messages
						.getString("CategoryView.label.categoryTreeGroup.CategoryStructure"), ID);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// dnd
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		Transfer[] transferTypes = new Transfer[] { LightXmlObjectTransfer
				.getInstance() };
		// jface
		this.treeViewer.addDragSupport(operations, transferTypes,
				new LightXmlObjectDragListener(treeViewer, ID));
	}
}
