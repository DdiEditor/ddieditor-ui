package org.ddialliance.ddieditor.ui.editor.universe;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

/**
 * Universe Scheme Editor
 */
public class UniverseSchemeEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			UniverseSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.universe.UniverseSchemeEditor";

	public UniverseSchemeEditor() {
		super(
				Messages
						.getString("UniverseSchemeEditor.label.universeSchemeEditorLabel.UniverseSchemeEditor"),
				Messages
						.getString("UniverseSchemeEditor.label.useTheEditorLabel.Description"),
				Messages
						.getString("UniverseSchemeEditor.label.UniverseSchemeTabItem"), ID);
		dao = (IDao) new UniverseSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return UniversePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.universe"));
	}
}
