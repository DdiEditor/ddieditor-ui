package org.ddialliance.ddieditor.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.preference.PreferenceUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.PlatformUI;

public class PrintDDI3Dialog extends Dialog {
	List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
	public DDIResourceType ddiResource;
	public boolean numVarStatisticBoolean = false;
	public boolean universerefOnVariablesBoolean = false;
	public boolean addNaviagtionBarBoolean = false;
	public boolean savePrintAsZipBoolean = false;
	public String savePrintAsZipPath = null;

	public PrintDDI3Dialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("PrintDDI3Action.properties"));
		this.getShell().setText(
				Translator.trans("PrintDDI3Action.menu.PrintDDI3"));

		// save as zip package
		editor.createLabel(group,
				Translator.trans("PrintDDI3Action.saveas.zip"));

		// comment out 20130204
		// savePrintAsZipPath = PreferenceUtil.getLastBrowsedPath()
		// .getAbsolutePath();
		Button pathBrowse = editor.createButton(group,
				Translator.trans("PrintDDI3Action.saveas.zipselect"));
		pathBrowse.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1));
		pathBrowse.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirChooser = new DirectoryDialog(PlatformUI
						.getWorkbench().getDisplay().getActiveShell());
				dirChooser.setText(Translator
						.trans("ExportDDI3Action.filechooser.title"));
				PreferenceUtil.setPathFilter(dirChooser);
				savePrintAsZipPath = dirChooser.open();
				if (savePrintAsZipPath != null) {
					PreferenceUtil.setLastBrowsedPath(savePrintAsZipPath);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// selectable resources
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("PrintDDI3Action.mess.PrintDDI3Error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}

		// label
		editor.createLabel(group,
				Translator.trans("PrintDDI3Action.resource.choose"));

		// resource combo
		String[] comboOptions = new String[resources.size()];
		for (int i = 0; i < comboOptions.length; i++) {
			comboOptions[i] = resources.get(i).getOrgName();
		}
		final Combo resouceCombo = editor.createCombo(group, comboOptions);

		// resource selection
		if (comboOptions.length == 1) {
			resouceCombo.select(0);
			ddiResource = resources.get(0);
		} else {
			resouceCombo.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int selection = ((Combo) e.getSource()).getSelectionIndex();
					ddiResource = resources.get(selection);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}
			});
		}

		// create statistics on numeric variables
		Button numVarStatistic = editor.createCheckBox(group, "",
				Translator.trans("PrintDDI3Action.numeric.statistics"));
		numVarStatistic.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				numVarStatisticBoolean = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// create universe ref on variables
		Button universerefOnVariables = editor.createCheckBox(group, "",
				Translator.trans("PrintDDI3Action.variable.univesereference"));
		universerefOnVariables.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				universerefOnVariablesBoolean = ((Button) e.widget)
						.getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		// toggle toc or navigation bar
		Button addNaviagtionBarButton = editor.createCheckBox(group, "",
				Translator.trans("PrintDDI3Action.navigation.add"));
		addNaviagtionBarButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addNaviagtionBarBoolean = ((Button) e.widget).getSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}
		});

		return null;
	}
}