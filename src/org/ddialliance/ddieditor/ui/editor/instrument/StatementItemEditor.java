package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;

import org.ddialliance.ddi_3_0.xml.xmlbeans.reusable.SoftwareType;
import org.ddialliance.ddieditor.ui.dbxml.Instruments;
import org.ddialliance.ddieditor.ui.editor.DateTimeWidget;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.LabelDescription;
import org.ddialliance.ddieditor.ui.model.instrument.Instrument;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class StatementItemEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.StatementItemEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			StatementItemEditor.class);
	private Instrument instrument;
	private IEditorSite site;

	public StatementItemEditor() {
		super(
				Messages
						.getString("StatementItemEditor.label.StatementItemEditorLabel.StatementItemEditor"),
				Messages
						.getString("StatementItemEditor.label.useTheEditorLabel.Description"));
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO formalize boiler plate code ...
		this.editorInput = (EditorInput) input;
		Instruments.init(((EditorInput) input).getProperties());
		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				instrument = Instruments.createInstrument(editorInput.getId(),
						editorInput.getVersion(), editorInput.getParentId(),
						editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("StatementItemEditor.init(): " + e.getMessage());
				String errMess = Messages
						.getString("StatementItemEditor.mess.ErrorDuringCreateNewInstrument"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				instrument = Instruments.getModel(editorInput.getId(),
						editorInput.getVersion(), editorInput.getParentId(),
						editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages
						.getString("StatementItemEditor.mess.GetInstrumentByIdError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat
					.format(
							Messages
									.getString("InstrumentSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), errMess);
			System.exit(0);
		}
		super.init(site, input);

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		super.createPartControl(parent);

		createTabFolder(getComposite_1());
		createLabelDescriptionTab(getTabFolder(), Messages
				.getString("StatementItemEditor.label.InstrumentTabItem"),
				(LabelDescription) instrument);

		for (SoftwareType software : instrument.getDoc().getInstrument()
				.getSoftwareList()) {
			createSoftwareTab(software);
		}
		createPropertiesTab(getTabFolder());
		editorStatus.clearChanged();
	}

	private void createSoftwareTab(final SoftwareType software) {
		TabItem tabItem = createTabItem(Messages
				.getString("StatementItemEditor.software"));
		Group softwareGroup = createGroup(tabItem, Messages
				.getString("StatementItemEditor.software"));

		// name
		final Text name = null;
		createTextInput(softwareGroup, Messages
				.getString("StatementItemEditor.software.namelabel"), software
				.getNameList().get(0).getStringValue(), name,
				new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						editorStatus.setChanged();
						software.getNameList().get(0).setStringValue(
								name.getText());
					}
				});

		// version
		final Text version = null;
		createTextInput(softwareGroup, Messages
				.getString("StatementItemEditor.software.versionlabel"),
				software.getVersion(), version, new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						editorStatus.setChanged();
						software.setVersion(version.getText());
					}
				});

		// description
		final Text description = null;
		createTextAreaInput(softwareGroup, Messages
				.getString("StatementItemEditor.software.descriptionlabel"),
				XmlBeansUtil.getTextOnMixedElement(software
						.getDescriptionList().get(0)), description,
				new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						editorStatus.setChanged();
						XmlBeansUtil.setTextOnMixedElement(software
								.getDescriptionList().get(0), description
								.getText());
					}
				});

		// date
		final DateTimeWidget dateTimeWidget = null;
		createDateInput(softwareGroup, Messages
				.getString("StatementItemEditor.software.datelabel"), software
				.getDate().getSimpleDate().toString(), dateTimeWidget,
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						editorStatus.setChanged();
						software
								.setDate(getDate(dateTimeWidget.getSelection()));
					}
				});
	}

	public String getPreferredPerspectiveId() {
		return InstrumentPerspective.ID;
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.instruments"));
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);

		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				Instruments.create(instrument);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				Instruments.update(instrument);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages
					.getString("StatementItemEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
	}
}
