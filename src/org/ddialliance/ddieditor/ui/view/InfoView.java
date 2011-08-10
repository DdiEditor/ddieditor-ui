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
import org.ddialliance.ddiftp.util.Translator;

public class InfoView extends View {
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";

	public InfoView() {
		super(ViewContentType.StudyContent, Translator
				.trans("InfoView.label.titleLabel.DDIOverview"), Translator
				.trans("InfoView.label.selectLabel.Description"), Translator
				.trans("InfoView.label.maskLabel.Id"), ElementType.FILE,
				Translator.trans("InfoView.label.treeGroup.DDIStructure"), ID);
	}
}
