package org.ddialliance.ddieditor.ui.view;

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class ConceptView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ConceptView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.ConceptView";
	public static final List<String> newMenuLabelList = Arrays.asList("Concept Scheme", "Concept");

	public ConceptView() {
		super(ViewContentType.ConceptContent,
				Messages.getString("ConceptView.label.conceptNavigationLabel.ConceptNavigation"), 
				Messages.getString("ConceptView.lable.selectLabel.NavigatorDescription"), 
				Messages.getString("ConceptView.lable.conceptLabel.Concept"), 
				"ConceptScheme",
				Messages.getString("ConceptView.lable.conceptTreeGroup.ConceptStructure"),
				newMenuLabelList);
	}

	public void createPartControl(Composite parent) {

		log.debug("");

		super.createPartControl(parent);

	}
}
