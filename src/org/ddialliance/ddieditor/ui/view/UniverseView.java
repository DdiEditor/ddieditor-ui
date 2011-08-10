package org.ddialliance.ddieditor.ui.view;

/**
 * Universe View.
 * 
 */
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.Translator;

public class UniverseView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.UniverseView";

	public UniverseView() {
		super(
				ViewContentType.UniverseContent,
				Translator
						.trans("UniverseView.label.universeNavigationLabel.UniverseNavigation"),
				Translator
						.trans("UniverseView.label.selectLabel.NavigatorDescription"),
				Translator.trans("UniverseView.label.universeLabel.Universe"),
				ElementType.UNIVERSE_SCHEME,
				Translator
						.trans("UniverseView.label.universeTreeGroup.UniverseStructure"),
				ID);
	}
}
