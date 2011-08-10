package org.ddialliance.ddieditor.ui.editor.file;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddiftp.util.Translator;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class FileEditor extends Editor implements IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.file.FileEditor";

	public FileEditor() {
		super(Translator.trans("ddi3file.editor.title"), Translator
				.trans("ddi3file.editor.description"), ID);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator.trans("ddi3file.label"));
		Group group = createGroup(tabItem, Translator.trans("ddi3file.label"));

		Text fileNameTxt = createTextInput(group,
				Translator.trans("ddi3file.label"), getEditorInputImpl()
						.getId(), false);
		fileNameTxt.setEditable(false);
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.overview"));
	}
}
