package org.ddialliance.ddieditor.ui.view;

/**
 * Universe View.
 * 
 */
import java.util.Arrays;

import org.ddialliance.ddieditor.ui.model.ElementType;

public class UniverseView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.UniverseView";

	public UniverseView() {
		super(ViewContentType.UniverseContent, Messages
				.getString("UniverseView.label.universeNavigationLabel.UniverseNavigation"), Messages
				.getString("UniverseView.label.selectLabel.NavigatorDescription"), Messages
				.getString("UniverseView.label.universeLabel.Universe"), ElementType.UNIVERSE_SCHEME, Messages
				.getString("UniverseView.label.universeTreeGroup.UniverseStructure"), Arrays
				.asList(ElementType.UNIVERSE));
	}
}
