package org.ddialliance.ddieditor.ui.dialogs;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DisplayNoteDialog extends Dialog {
	String note;

	public DisplayNoteDialog(Shell parentShell, String note) {
		super(parentShell);
		this.note = note;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		this.getShell().setText("Release note");

		// group
		Editor editor = new Editor();
		Group group = editor.createGroup(parent, "Release note");
		group.setLayoutData(new GridData(800, 400));

		editor.createTextAreaInput(group, "Release note", note, null);
		return null;
	}
}
