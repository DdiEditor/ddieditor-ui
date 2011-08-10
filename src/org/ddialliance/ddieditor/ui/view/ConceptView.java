package org.ddialliance.ddieditor.ui.view;

/**
 * Concept View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.Translator;

public class ConceptView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.ConceptView";

	public ConceptView() {
		super(
				ViewContentType.ConceptContent,
				Translator
						.trans("ConceptView.label.conceptNavigationLabel.ConceptNavigation"),
				Translator
						.trans("ConceptView.label.selectLabel.NavigatorDescription"),
				Translator.trans("ConceptView.label.conceptLabel.Concept"),
				ElementType.CONCEPT_SCHEME,
				Translator
						.trans("ConceptView.label.conceptTreeGroup.ConceptStructure"),
				ID);
	}
}
