package org.ddialliance.ddieditor.ui.dialogs;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewDDI3FileDialog  extends Dialog {
	public String fileName;
	Text fileNameText = null;

	public NewDDI3FileDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent, Messages
				.getString("newddi3fileaction.dialog.group"));
		group.setLayoutData(new GridData(500, 150));
		this.getShell().setText(
				Messages.getString("newddi3fileaction.dialog.title"));

		// file name
		fileNameText = editor.createTextInput(group, Messages
				.getString("newddi3fileaction.dialog.filetxt"), "", null);
		fileNameText.setData(true);
		fileNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text text = ((Text)e.getSource());
				
				// do not change text on resource change selection
				if (!(Boolean)text.getData()) {
					text.setData(true);
					return;
				}
				fileName = text.getText();
			}
		});
		return null;
	}
}
