package org.ddialliance.ddieditor.ui.editor.category;

/**
 * Category Scheme Editor.
 * 
 */
/*
 * $Author: ddadak $ 
 * $Date: 2010-07-08 13:33:59 +0200 (Thu, 08 Jul 2010) $ 
 * $Revision: 1349 $
 */

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.CategoryPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class CategorySchemeEditor extends LabelDescriptionEditor {
		private static Log log = LogFactory.getLog(LogType.SYSTEM, CategorySchemeEditor.class);
		public static final String ID = "org.ddialliance.ddieditor.ui.editor.category.CategorySchemeEditor";

		public CategorySchemeEditor() {
			super(Messages
					.getString("CategorySchemeEditor.label.categorySchemeEditorLabel.CategorySchemeEditor"), Messages
					.getString("CategorySchemeEditor.label.useTheEditorLabel.Description"), Messages
					.getString("CategorySchemeEditor.label.CategorySchemeTabItem"));
			dao = (IDao) new CategorySchemeDao();
		}

		public String getPreferredPerspectiveId() {
			return CategoryPerspective.ID;
		}

		public String getPerspectiveSwitchDialogText() {
			return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
					.getString("perspective.category"));
		}

	}
