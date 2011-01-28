package org.ddialliance.ddieditor.ui.editor.instrument;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class SequenceMenuPopupAddDialog extends Dialog {
	static Log log = LogFactory.getLog(LogType.SYSTEM,
			SequenceMenuPopupAddDialog.class);
	Combo combo;
	ReferenceSelectionCombo selectCombo;
	LightXmlObjectType result;
	int beforeAfter;

	String title;
	String label;
	List<LightXmlObjectType> refs;
	IModel modelImpl;

	public SequenceMenuPopupAddDialog(Shell parentShell, String title,
			String label, List<LightXmlObjectType> refs, IModel modelImpl) {
		super(parentShell);
		this.title = title;
		this.label = label;
		this.refs = refs;
		this.modelImpl = modelImpl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		this.getShell().setText(title);

		// group
		Editor editor = new Editor(SequenceEditor.ID);
		Group group = editor.createGroup(parent, label);
		group.setLayoutData(new GridData(800, 400));

		// before - after
		editor.createLabel(group, Messages
				.getString("SequenceEditor.adddialog.beforeafterselection"));
		combo = editor.createCombo(group, new String[] {
				Messages.getString("SequenceEditor.adddialog.after"),
				Messages.getString("SequenceEditor.adddialog.before") });
		combo.select(0);

		// selection
		selectCombo = editor.createRefSelection(group, Messages
				.getString("SequenceEditor.adddialog.label"), Messages
				.getString("SequenceEditor.adddialog.label"),
				ReferenceType.Factory.newInstance(), refs, false);
		selectCombo.addSelectionListener(Messages
				.getString("SequenceEditor.adddialog.label"),
				new SelectionAdapter(this));

		return null;
	}

	public LightXmlObjectType getResult() {
		return selectCombo.getResult();
	}
}

class SelectionAdapter implements SelectionListener {
	SequenceMenuPopupAddDialog dialog;

	public SelectionAdapter(SequenceMenuPopupAddDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		dialog.result = dialog.selectCombo.getResult();
		dialog.beforeAfter = dialog.combo.getSelectionIndex();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// do nothing
	}
}
