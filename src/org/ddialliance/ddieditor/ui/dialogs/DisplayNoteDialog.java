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
	String title;
	String group;
	String label;

	public DisplayNoteDialog(Shell parentShell, String title, String group,
			String label, String note) {
		super(parentShell);
		this.note = note;
		this.title = title;
		this.group = group;
		this.label = label;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		this.getShell().setText(title);

		// group
		Editor editor = new Editor();
		Group swtGroup = editor.createGroup(parent, group);
		swtGroup.setLayoutData(new GridData(800, 400));

		editor.createTextAreaInput(swtGroup, label, note, null);
		return null;
	}
}
