package org.ddialliance.ddieditor.ui.editor.universe;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class UniverseEditor  extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.universe.UniverseEditor";

	public UniverseEditor() {
		super(Messages
				.getString("UniverseEditor.label.universeEditorLabel.UniverseEditor"), Messages
				.getString("UniverseEditor.label.useTheEditorLabel.Description"), Messages
				.getString("UniverseEditor.label.UniverseTabItem"), ID);
		dao = (IDao) new UniverseDao();
	}

	public String getPreferredPerspectiveId() {
		return UniversePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.universe"));
	}
}
