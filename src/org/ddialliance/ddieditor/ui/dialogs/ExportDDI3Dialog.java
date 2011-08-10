package org.ddialliance.ddieditor.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class ExportDDI3Dialog extends Dialog {
	public String path;
	public String fileName;
	public DDIResourceType ddiResource;

	List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
	Text fileNameText = null;

	public ExportDDI3Dialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("ExportDDI3Action.properties"));
		group.setLayoutData(new GridData(500, 150));
		this.getShell().setText(
				Translator.trans("ExportDDI3Action.menu.ExportDDI3"));

		// export resource
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("ExportDDI3Action.mess.ExportDDI3Error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}
		String[] comboOptions = new String[resources.size()];
		for (int i = 0; i < comboOptions.length; i++) {
			comboOptions[i] = resources.get(i).getOrgName();
		}
		editor.createLabel(group,
				Translator.trans("ExportDDI3Action.resource.choose"));
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

		// export path
		editor.createLabel(group,
				Translator.trans("ExportDDI3Action.filechooser.title"));
		final Text pathText = editor.createText(group, "", false);
		Button pathBrowse = editor.createButton(group,
				Translator.trans("ExportDDI3Action.filechooser.browse"));
		pathBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				dirChooser.setText(Translator
						.trans("ExportDDI3Action.filechooser.title"));
				path = dirChooser.open();
				if (path != null) {
					pathText.setText(path);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// file name
		fileNameText = editor.createTextInput(group,
				Translator.trans("ExportDDI3Action.filename"), "", null);
		fileNameText.setData(true);
		if (comboOptions.length == 1) {
			fileNameText.setData(false);
			fileNameText.setText(resouceCombo.getItem(0));
		}
		fileNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Text text = ((Text) e.getSource());

				// do not change text on resource change selection
				if (!(Boolean) text.getData()) {
					text.setData(true);
					return;
				}
				fileName = text.getText();
			}
		});
		return null;
	}
}
