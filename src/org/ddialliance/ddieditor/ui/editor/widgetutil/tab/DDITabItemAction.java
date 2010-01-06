package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.eclipse.swt.custom.StyledText;

public class DDITabItemAction extends TabItemAction {
	IModel model;
	StyledText styledText;
	static XmlOptions xmlOptions = new XmlOptions();
	
	public DDITabItemAction(String id, IModel model, StyledText styledText) {
		super(id);
		this.model = model;
		this.styledText = styledText;
		xmlOptions.put(XmlOptions.SAVE_PRETTY_PRINT);
	}

	public void setModel(IModel model) {
		this.model = model;
	}
	
	public void setStyledText(StyledText styledText) {
		this.styledText = styledText;
	}
	
	@Override
	public Object action() throws DDIFtpException {
		styledText.setText(model.getDocument().xmlText(xmlOptions));
		return null;
	}
}
