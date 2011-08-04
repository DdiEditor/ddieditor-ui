package org.ddialliance.ddieditor.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class PrintDDI3Dialog extends Dialog {
	
	List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
	Text fileNameText = null;
	public DDIResourceType ddiResource;


	public PrintDDI3Dialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent, Messages
				.getString("PrintDDI3Action.properties"));
		group.setLayoutData(new GridData(500, 50));
		this.getShell().setText(
				Messages.getString("PrintDDI3Action.menu.PrintDDI3"));
		
		// print resource
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] comboOptions = new String[resources.size()];
		for (int i = 0; i < comboOptions.length; i++) {
			comboOptions[i] = resources.get(i).getOrgName();
		}
		editor.createLabel(group, Messages
				.getString("PrintDDI3Action.resource.choose"));
		final Combo resouceCombo = editor.createCombo(group, comboOptions);
		if (comboOptions.length == 1) {
			resouceCombo.select(0);
			ddiResource = resources.get(0);
		} else {
			resouceCombo.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = ((Combo) e.getSource()).getSelectionIndex();
					fileNameText.setData(false);
					fileNameText.setText(resouceCombo.getItem(selection));
					ddiResource = resources.get(selection);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}
			});
		}
		
		return null;
	}
}
