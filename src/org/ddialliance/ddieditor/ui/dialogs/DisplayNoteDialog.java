package org.ddialliance.ddieditor.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class DisplayNoteDialog extends Dialog {
	String title;
	String group;
	Map<String, String> items = new HashMap<String, String>();
	Group swtGroup;

	public DisplayNoteDialog(Shell parentShell, String title, String group,
			String label, String note) {
		super(parentShell);
		this.title = title;
		this.group = group;
		this.items.put(label, note);
	}

	public DisplayNoteDialog(Shell parentShell, String title, String group,
			Map<String, String> items) {
		super(parentShell);
		this.title = title;
		this.group = group;
		this.items = items;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		this.getShell().setText(title);

		// group
		Editor editor = new Editor();
		swtGroup = editor.createGroup(parent, group);
		swtGroup.setLayoutData(new GridData(800, 400));
		for (Entry<String, String> entry : items.entrySet()) {
			editor.createTextAreaInput(swtGroup, entry.getKey(),
					entry.getValue(), null);
		}

		return swtGroup;
	}
}
