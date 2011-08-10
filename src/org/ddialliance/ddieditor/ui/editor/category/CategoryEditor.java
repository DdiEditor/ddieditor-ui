package org.ddialliance.ddieditor.ui.editor.category;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategoryDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.CategoryPerspective;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Composite;

/**
 * Category Scheme Editor
 */
public class CategoryEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			CategoryEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.category.CategoryEditor";

	public CategoryEditor() {
		super(
				Translator.trans("CategoryEditor.label.categoryEditorLabel.CategoryEditor"),
				Translator.trans("CategoryEditor.label.useTheEditorLabel.Description"),
				Translator.trans("CategoryEditor.label.CategoryTabItem"), ID);
		dao = (IDao) new CategoryDao();
	}

	public String getPreferredPerspectiveId() {
		return CategoryPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.category"));
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}
}