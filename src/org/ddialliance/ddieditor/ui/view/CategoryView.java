package org.ddialliance.ddieditor.ui.view;

/**
 * Category View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;

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
						.getString("CategoryView.label.categoryTreeGroup.CategoryStructure"));
	}
}
