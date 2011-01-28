package org.ddialliance.ddieditor.ui.view;

/**
 * Concept View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;

public class ConceptView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.ConceptView";

	public ConceptView() {
		super(
				ViewContentType.ConceptContent,
				Messages
						.getString("ConceptView.label.conceptNavigationLabel.ConceptNavigation"),
				Messages
						.getString("ConceptView.label.selectLabel.NavigatorDescription"),
				Messages.getString("ConceptView.label.conceptLabel.Concept"),
				ElementType.CONCEPT_SCHEME,
				Messages
						.getString("ConceptView.label.conceptTreeGroup.ConceptStructure"), ID);
	}
}
