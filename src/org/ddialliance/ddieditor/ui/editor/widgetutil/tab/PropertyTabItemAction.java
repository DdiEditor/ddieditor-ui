package org.ddialliance.ddieditor.ui.editor.widgetutil.tab;

import javax.xml.namespace.QName;

import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.logic.identification.VersionInformation;
import org.ddialliance.ddieditor.logic.urn.ddi.Urn2Util;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.xml.Urn;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PropertyTabItemAction extends TabItemAction {
	IModel model;
	Shell shell;
	StyledText urnText;
	Text agencyText;
	Text idText;
	Text versionText;
	Text versionResponsibilityText;
	Text versionDate;
	StyledText versionRationaleText;

	public PropertyTabItemAction(String id, IModel model, Shell shell,
			StyledText urnText, Text agencyText, Text idText, Text versionText,
			Text versionResponsibilityText, Text versionDate,
			StyledText versionRationaleText) {
		super(id);
		this.model = model;
		this.shell = shell;
		this.urnText = urnText;
		this.agencyText = agencyText;
		this.idText = idText;
		this.versionText = versionText;
		this.versionResponsibilityText = versionResponsibilityText;
		this.versionDate = versionDate;
		this.versionRationaleText = versionRationaleText;
	}

	@Override
	public Object action() throws DDIFtpException {
		boolean isMaintainable = false;
		try {
			isMaintainable = DdiManager.getInstance().getDdi3NamespaceHelper()
					.isMaintainable(model.getDocument());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(shell, id, "Maintainable check error",
					e.getMessage(), e);
		}

		// action
		// actionCombo

		// urn
		Urn urn = null;
		String urnStr = "";
		try {
			QName qName = model.getDocument().schemaType()
					.getDocumentElementName();
			urn = Urn2Util.getUrn(qName.getLocalPart(), model.getId(),
					model.getVersion(), model.getParentId(),
					model.getParentVersion(), model.getAgency());
			urnStr = urn.toUrnString();
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(shell, id, "URN error", e.getMessage(), e);
		}
		urnText.setText(urnStr);

		// agency
		if (isMaintainable) {
			agencyText.setText(model.getAgency() == null ? (urn == null ? ""
					: urn.getIdentifingAgency()) : model.getAgency());
		}

		// id
		idText.setText(model.getId());

		// version
		boolean isVersionable = false;
		try {
			isVersionable = DdiManager.getInstance().getDdi3NamespaceHelper()
					.isVersionable(model.getDocument());
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(shell, id, "Versionable check error",
					e.getMessage(), e);
		}
		if (isVersionable) {
			// version
			versionText.setText(model.getVersion() == null ? "" : model
					.getVersion());

			VersionInformation versionInformation = null;
			try {
				versionInformation = IdentificationManager.getInstance()
						.getVersionInformation(model.getDocument());
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(shell, id, "Error", e.getMessage(), e);
			}

			// version responsibility
			versionResponsibilityText
					.setText(versionInformation.versionResponsibility == null ? ""
							: versionInformation.versionResponsibility);

			// version date
			String versionDateStr = "";
			try {
				versionDateStr = XmlBeansUtil.getXmlAttributeValue(model
						.getDocument().xmlText(), "versionDate=\"");
			} catch (DDIFtpException e) {
				DialogUtil.errorDialog(shell, id, "Error", e.getMessage(), e);
			}
			versionDate.setText(versionDateStr);

			// version rationale
			// remove listener to avoid the modification event in following set
			Listener[] listeners = versionRationaleText
					.getListeners(SWT.Modify);
			versionRationaleText.removeListener(SWT.Modify, listeners[0]);

			// set text
			versionRationaleText
					.setText(versionInformation.versionRationaleList.isEmpty() ? ""
							: versionInformation.versionRationaleList.get(
									versionInformation.versionRationaleList
											.size() - 1).getStringValue());

			// reattach listener after set text
			versionRationaleText.addListener(SWT.Modify, listeners[0]);
		}
		return null;
	}
}
