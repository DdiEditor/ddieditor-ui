package org.ddialliance.ddieditor.ui.editor.universe;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseDao;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.universe.Universe;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class UniverseEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			UniverseEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.universe.UniverseEditor";

	public UniverseEditor() {
		super(
				Translator
						.trans("UniverseEditor.label.universeEditorLabel.UniverseEditor"),
				Translator
						.trans("UniverseEditor.label.useTheEditorLabel.Description"),
				Translator.trans("UniverseEditor.label.UniverseTabItem"), ID);
		dao = (IDao) new UniverseDao();
	}

	public String getPreferredPerspectiveId() {
		return UniversePerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.universe"));
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		// TODO Only make Universe check if it is a DDA installation
		// Check if Universe derives from Journal DB
		EditorInput editorInput = (EditorInput) input;
		if (editorInput.getParentElementType().equals(
				ElementType.UNIVERSE_SCHEME)) {
			// TODO check if this Univer is references by Study Unit
			try {
				List<String> ids = ((Universe) model).getStudyUnitReference();
				for (String id : ids) {
					if (id.equals(editorInput.getId())) {
						// if so - update not allowed in DDI Editor - disable
						// update
						editorInput.setEditorMode(EditorModeType.VIEW);
					}
				}
			} catch (Exception e) {
				new PartInitException(e.getMessage());
			}
		}
	}
}
