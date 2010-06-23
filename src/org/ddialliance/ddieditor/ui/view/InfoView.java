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

import java.util.Arrays;
import java.util.List;

import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

public class InfoView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InfoView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";

	public InfoView() {
		super(ViewContentType.StudyContent, Messages
				.getString("InfoView.label.titleLabel.DDIOverview"), Messages
				.getString("InfoView.label.selectLabel.Description"), Messages
				.getString("InfoView.label.maskLabel.Id"),  ElementType.FILE, Messages
				.getString("InfoView.label.treeGroup.DDIStructure"), 
				Arrays.asList(ElementType.STUDY_UNIT));
	}
}
