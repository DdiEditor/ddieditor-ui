package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.custom.StyledText;

public class DDITabItemAction extends TabItemAction {
	IModel model;
	StyledText styledText;

	public DDITabItemAction(String id, IModel model, StyledText styledText) {
		super(id);
		this.model = model;
		this.styledText = styledText;
	}

	public void setModel(IModel model) {
		this.model = model;
	}

	public void setStyledText(StyledText styledText) {
		this.styledText = styledText;
	}

	@Override
	public Object action() throws DDIFtpException {
		styledText.setText(DdiManager
				.getInstance()
				.getDdi3NamespaceHelper()
				.substitutePrefixesFromElements(
						model.getDocument().xmlText(
								DdiManager.getInstance().getXmlOptions())));
		return null;
	}
}
