package org.ddialliance.ddieditor.ui.editor.universe;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddiftp.util.Translator;
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
				Translator
						.trans("UniverseSchemeEditor.label.universeSchemeEditorLabel.UniverseSchemeEditor"),
				Translator
						.trans("UniverseSchemeEditor.label.useTheEditorLabel.Description"),
				Translator
						.trans("UniverseSchemeEditor.label.UniverseSchemeTabItem"),
				ID);
		dao = (IDao) new UniverseSchemeDao();
	}

	public String getPreferredPerspectiveId() {
		return UniversePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.universe"));
	}
}
