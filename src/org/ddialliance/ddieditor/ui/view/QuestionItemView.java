package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class QuestionItemView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionItemView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.QuestionItemView";

	public void createPartControl(Composite parent) {

		log.debug("");

		super.createPartControl(parent);

	}
}
