package org.ddialliance.ddieditor.ui.editor.instrument;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.model.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class SequencePropertiesDialog extends Dialog {
	static Log log = LogFactory.getLog(LogType.SYSTEM,
			SequencePropertiesDialog.class);
	Editor editor;
	Sequence modelImpl;
	String title;

	public SequencePropertiesDialog(Shell parentShell, String title,
			Editor editor) {
		super(parentShell);
		this.title = title;
		this.editor = editor;
		this.modelImpl = (Sequence) editor.getModel();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// dialog setup
		this.getShell().setText(title);

		// group
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(800, 400));
		editor.createTabFolder(parent);

		// description tab
		TabItem tabItem2 = editor.createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = editor.createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			// label
			Text txt = editor.createLabelInput(group2,
					Translator.trans("editor.label.label"), modelImpl
							.getDocument().getSequence().getLabelList(),
					modelImpl.getDocument().getSequence().getId());
			editor.createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getSequence().getLabelList(),
					new LabelTdI(), "", txt);

			// description
			StyledText styledText = editor.createStructuredStringInput(group2,
					Translator.trans("editor.label.description"), modelImpl
							.getDocument().getSequence().getDescriptionList(),
					modelImpl.getDocument().getSequence().getId());
			editor.createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getSequence().getDescriptionList(),
					new DescriptionTdI(), "", styledText);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(editor.getEditorSite(), editor.ID, null,
					e.getMessage(), e);
		}

		// id tab
		editor.createPropertiesTab(editor.getTabFolder());

		// xml tab
		editor.createXmlTab(modelImpl);

		// preview tab
		editor.createPreviewTab(modelImpl);

		return null;
	}

}
