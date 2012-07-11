package org.ddialliance.ddieditor.ui.editor.instrument;

/**
 * Instrument Editor.
 * 
 */

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.SoftwareType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.instrument.InstrumentDao;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.DescriptionTdI;
import org.ddialliance.ddieditor.ui.dialogs.translationdialoginput.LabelTdI;
import org.ddialliance.ddieditor.ui.editor.DateTimeWidget;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.Instrument;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.StyledText;
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

public class InstrumentEditor extends Editor {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.InstrumentEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			InstrumentEditor.class);
	private Instrument modelImpl;

	public InstrumentEditor() {
		super(
				Translator
						.trans("InstrumentEditor.label.InstrumentEditorLabel.InstrumentEditor"),
				Translator
						.trans("InstrumentEditor.label.useTheEditorLabel.Description"),
				ID);

		dao = new InstrumentDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		modelImpl = (Instrument) model;
	}

	/**
	 * Create contents of the editor part
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		parent.setLayout(new GridLayout());
		createTabFolder(getComposite_1());

		// main tab
		TabItem tabItem = createTabItem(Translator
				.trans("InstrumentEditor.label.InstrumentTabItem"));
		Group group = createGroup(tabItem,
				Translator.trans("InstrumentEditor.label.InstrumentTabItem"));

		// main sequence ref
		List<LightXmlObjectType> sequenceRefList = new ArrayList<LightXmlObjectType>();
		try {
			sequenceRefList = DdiManager.getInstance()
					.getSequencesLight(null, null, null, null)
					.getLightXmlObjectList().getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID, null,
					e.getMessage(), e);
		}

		ReferenceSelectionCombo sequenceRefSelectCombo = createRefSelection(
				group,
				Translator.trans("InstrumentEditor.software.mainsequence"),
				Translator.trans("InstrumentEditor.software.mainsequence"),
				modelImpl.getDocument().getInstrument()
						.getControlConstructReference(), sequenceRefList, false, ElementType.SEQUENCE);
		sequenceRefSelectCombo.addSelectionListener(Translator
				.trans("InstrumentEditor.software.mainsequence"),
				new ReferenceSelectionAdapter(sequenceRefSelectCombo,
						modelImpl, ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// software tab
		// commet out - jvj - 20120530
		// for (SoftwareType software : modelImpl.getDocument().getInstrument()
		// .getSoftwareList()) {
		// createSoftwareTab(software);
		// }

		// label - description - tab
		TabItem tabItem2 = createTabItem(Translator
				.trans("editor.label.description"));
		Group group2 = createGroup(tabItem2,
				Translator.trans("editor.label.description"));

		try {
			Text txt = createLabelInput(group2,
					Translator.trans("editor.label.label"), modelImpl
							.getDocument().getInstrument().getLabelList(),
					modelImpl.getDocument().getInstrument().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"), modelImpl
							.getDocument().getInstrument()
							.getInstrumentNameList(), new LabelTdI(), "", txt);

			StyledText styledText = createStructuredStringInput(group2,
					Translator.trans("editor.label.description"),
					modelImpl.getDocument().getInstrument()
							.getDescriptionList(), modelImpl.getDocument()
							.getInstrument().getId());
			createTranslation(group2,
					Translator.trans("editor.button.translate"),
					modelImpl.getDocument().getInstrument()
							.getDescriptionList(), new DescriptionTdI(), "",
					styledText);
		} catch (DDIFtpException e) {
			DialogUtil
					.errorDialog(getEditorSite(), ID, null, e.getMessage(), e);
		}

		// id - version tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(modelImpl);

		// preview tab
		createPreviewTab(modelImpl);

		editorStatus.clearChanged();
	}

	private void createSoftwareTab(final SoftwareType software) {
		TabItem tabItem = createTabItem(Translator
				.trans("InstrumentEditor.software"));
		Group softwareGroup = createGroup(tabItem,
				Translator.trans("InstrumentEditor.software"));

		// name
		// final Text name = null;
		// createTextInput(softwareGroup, Translator
		// .trans("InstrumentEditor.software.namelabel"), software
		// .getNameList().get(0).getStringValue(), name,
		// new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// editorStatus.setChanged();
		// software.getNameList().get(0).setStringValue(
		// name.getText());
		// }
		// });
		//
		// // version
		// final Text version = null;
		// createTextInput(softwareGroup, Translator
		// .trans("InstrumentEditor.software.versionlabel"), software
		// .getVersion(), version, new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// editorStatus.setChanged();
		// software.setVersion(version.getText());
		// }
		// });
		//
		// // description
		// final Text description = null;
		// createTextAreaInput(softwareGroup, Translator
		// .trans("InstrumentEditor.software.descriptionlabel"),
		// XmlBeansUtil.getTextOnMixedElement(software
		// .getDescriptionList().get(0)), description,
		// new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// editorStatus.setChanged();
		// XmlBeansUtil.setTextOnMixedElement(software
		// .getDescriptionList().get(0), description
		// .getText());
		// }
		// });

		// date
		final DateTimeWidget dateTimeWidget = null;
		try {
			createDateInput(softwareGroup,
					Translator.trans("InstrumentEditor.software.datelabel"),
					software.getDate().getSimpleDate().toString(),
					dateTimeWidget, new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							editorStatus.setChanged();
							software.setDate(getDate(dateTimeWidget
									.getSelection()));
						}
					});
		} catch (Exception e) {
			DialogUtil.errorDialog(getSite().getShell(), ID,
					Translator.trans("ErrorTitle"), e.getMessage(), e);
		}
	}

	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(
				Translator.trans("perspective.switch.dialogtext"),
				Translator.trans("perspective.instruments"));
	}
}
