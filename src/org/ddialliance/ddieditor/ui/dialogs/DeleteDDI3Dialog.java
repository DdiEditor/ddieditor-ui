package org.ddialliance.ddieditor.ui.dialogs;

import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class DeleteDDI3Dialog extends Dialog {
	List<DDIResourceType> resources = null;
	DDIResourceType result = null;

	public DeleteDDI3Dialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor("");
		Group group = editor.createGroup(parent, Messages
				.getString("deleteddi3resourceaction.dialog.group"));
		group.setLayoutData(new GridData(400, 100));
		this.getShell().setText(
				Messages.getString("deleteddi3resourceaction.dialog.title"));

		// loaded resources
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Messages.getString("ErrorTitle"), e
					.getMessage());
		}

		String[] options = new String[resources.size()];
		int count = 0;
		for (DDIResourceType resource : resources) {
			options[count] = resource.getOrgName();
			count++;
		}
		editor.createLabel(group, "Select loaded resource");
		Combo combo = editor.createCombo(group, options);
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Combo c = (Combo) event.getSource();
				result = resources.get(c.getSelectionIndex());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				// do nothing
			}
		});
		return null;
	}

	public DDIResourceType getResult() {
		return result;
	}
}
