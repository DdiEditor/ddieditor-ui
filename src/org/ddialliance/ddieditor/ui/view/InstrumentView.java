package org.ddialliance.ddieditor.ui.view;

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class InstrumentView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InstrumentView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InstrumentView";
	public static final List<String> newMenuLabelList = Arrays.asList(
			"Concept Scheme", "Concept");

	public InstrumentView() {
		super(
				ViewContentType.InstrumentationContent,				
				Messages
						.getString("InstrumentView.label.navigation"),
				Messages
						.getString("InstrumentView.label.label.Instrument"),
				Messages.getString("InstrumentView.lable.conceptLabel.Instrument"),
				"Instument",
				Messages
						.getString("InstrumentView.label.treeGroup"),
				newMenuLabelList);
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
}