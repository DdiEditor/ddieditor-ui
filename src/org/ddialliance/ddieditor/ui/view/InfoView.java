package org.ddialliance.ddieditor.ui.view;

/**
 * Info View.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.model.ElementType;

public class InfoView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";

	public InfoView() {
		super(ViewContentType.StudyContent, Messages
				.getString("InfoView.label.titleLabel.DDIOverview"), Messages
				.getString("InfoView.label.selectLabel.Description"), Messages
				.getString("InfoView.label.maskLabel.Id"),  ElementType.FILE, Messages
				.getString("InfoView.label.treeGroup.DDIStructure"));
	}
}
