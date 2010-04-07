package org.ddialliance.ddieditor.ui.editor.file;

import java.text.MessageFormat;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class FileEditor extends Editor  implements IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.file.FileEditor";
	public FileEditor() {
		super(
				Messages
						.getString("ddi3file.editor.title"),
				Messages
						.getString("ddi3file.editor.description"));
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		this.editorInput = (EditorInput) input;
		setInput(editorInput);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);
		createTabFolder(getComposite_1());
		
		// main tab
		TabItem tabItem = createTabItem(Messages
				.getString("ddi3file.label"));
		Group group = createGroup(tabItem, Messages
				.getString("ddi3file.label"));
		
		Text fileNameTxt = createTextInput(group, Messages
				.getString("ddi3file.label"),
				editorInput.getId(), false);
		fileNameTxt.setEditable(false);
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.overview"));
	}
}
