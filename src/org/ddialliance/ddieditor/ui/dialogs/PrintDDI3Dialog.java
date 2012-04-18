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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class PrintDDI3Dialog extends Dialog {

	List<DDIResourceType> resources = new ArrayList<DDIResourceType>();
	public DDIResourceType ddiResource;
	public boolean numVarStatisticBoolean = false;// null;

	public PrintDDI3Dialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		Editor editor = new Editor();
		Group group = editor.createGroup(parent,
				Translator.trans("PrintDDI3Action.properties"));
		group.setLayoutData(new GridData(500, 70));
		this.getShell().setText(
				Translator.trans("PrintDDI3Action.menu.PrintDDI3"));

		// print resource
		try {
			resources = PersistenceManager.getInstance().getResources();
		} catch (DDIFtpException e) {
			String errMess = MessageFormat
					.format(Translator
							.trans("PrintDDI3Action.mess.PrintDDI3Error"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), Translator.trans("ErrorTitle"), errMess);
		}
		String[] comboOptions = new String[resources.size()];
		for (int i = 0; i < comboOptions.length; i++) {
			comboOptions[i] = resources.get(i).getOrgName();
		}
		editor.createLabel(group,
				Translator.trans("PrintDDI3Action.resource.choose"));
		final Combo resouceCombo = editor.createCombo(group, comboOptions);
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


		return null;
	}
}
