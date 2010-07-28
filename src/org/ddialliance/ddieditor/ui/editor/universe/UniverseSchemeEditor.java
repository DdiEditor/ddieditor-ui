package org.ddialliance.ddieditor.ui.editor.universe;

	/**
	 * Universe Scheme Editor.
	 * 
	 */
	/*
	 * $Author: ddadak $ 
	 * $Date: 2010-07-08 13:33:59 +0200 (Thu, 08 Jul 2010) $ 
	 * $Revision: 1349 $
	 */

	import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.perspective.UniversePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

		public class UniverseSchemeEditor extends LabelDescriptionEditor {
			private static Log log = LogFactory.getLog(LogType.SYSTEM, UniverseSchemeEditor.class);
			public static final String ID = "org.ddialliance.ddieditor.ui.editor.universe.UniverseSchemeEditor";

			public UniverseSchemeEditor() {
				super(Messages
						.getString("UniverseSchemeEditor.label.universeSchemeEditorLabel.UniverseSchemeEditor"), Messages
						.getString("UniverseSchemeEditor.label.useTheEditorLabel.Description"), Messages
						.getString("UniverseSchemeEditor.label.UniverseSchemeTabItem"));
				dao = (IDao) new UniverseSchemeDao();
			}

			public String getPreferredPerspectiveId() {
				return UniversePerspective.ID;
			}

			public String getPerspectiveSwitchDialogText() {
				return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
						.getString("perspective.universe"));
			}

		}
