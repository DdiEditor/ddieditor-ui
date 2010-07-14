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

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategoryDao;
import org.ddialliance.ddieditor.ui.editor.LabelDescriptionEditor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.perspective.CategoryPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class CategoryEditor extends LabelDescriptionEditor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CategoryEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.category.CategoryEditor";

	public CategoryEditor() {
		super(Messages.getString("CategoryEditor.label.categoryEditorLabel.CategoryEditor"), Messages
				.getString("CategoryEditor.label.useTheEditorLabel.Description"), Messages
				.getString("CategoryEditor.label.CategoryTabItem"));
		dao = (IDao) new CategoryDao();
	}

	public String getPreferredPerspectiveId() {
		return CategoryPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.category"));
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		Text valueText = createTextInput(super.labelDescriptionTabGroup, "Value:", "", null);
		valueText.setData(true);
		valueText.addModifyListener(new TextStyledTextModyfiListener(model, ProgrammingLanguageCodeType.class,
				getEditorIdentification()));

	}
}