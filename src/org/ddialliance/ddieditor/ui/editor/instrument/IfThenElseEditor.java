package org.ddialliance.ddieditor.ui.editor.instrument;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlOptions;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ProgrammingLanguageCodeType;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.editor.widgetutil.genericmodifylistener.TextStyledTextModyfiListener;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionAdapter;
import org.ddialliance.ddieditor.ui.editor.widgetutil.referenceselection.ReferenceSelectionCombo;
import org.ddialliance.ddieditor.ui.model.ModelIdentifingType;
import org.ddialliance.ddieditor.ui.model.instrument.IfThenElse;
import org.ddialliance.ddieditor.ui.perspective.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

public class IfThenElseEditor extends Editor implements IAutoChangePerspective {
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.instrument.IfThenElseEditor";
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			IfThenElseEditor.class);
	private IfThenElse model;
	private IfThenElseDao dao;
	private IEditorSite site;
	private List<LightXmlObjectType> questionRefList;

	public IfThenElseEditor() {
		super(
				Messages
						.getString("IfThenElseEditor.label.IfThenElseEditorLabel.IfThenElseEditor"),
				Messages
						.getString("IfThenElseEditor.label.useTheEditorLabel.Description"));
		dao = new IfThenElseDao();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO formalize boiler plate code ...
		this.editorInput = (EditorInput) input;

		if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
			try {
				model = dao.create(editorInput.getId(), editorInput
						.getVersion(), editorInput.getParentId(), editorInput
						.getParentVersion());
			} catch (Exception e) {
				log.error("IfThenElseEditor.init(): " + e.getMessage());
				String errMess = Messages
						.getString("IfThenElseEditor.mess.ErrorDuringCreateNewInstrument"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages
						.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)
				|| editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
			try {
				model = dao.getModel(editorInput.getId(), editorInput
						.getVersion(), editorInput.getParentId(), editorInput
						.getParentVersion());
			} catch (Exception e) {
				String errMess = Messages
						.getString("IfThenElseEditor.mess.GetInstrumentByIdError"); //$NON-NLS-1$
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

		// main tab
		TabItem tabItem = createTabItem(Messages
				.getString("IfThenElse.editor.tabdisplaytext"));
		Group group = createGroup(tabItem, Messages
				.getString("IfThenElse.editor.groupdisplaytext"));

		// if condition
		Composite error = createErrorComposite(group, "");
		ProgrammingLanguageCodeType ifProgrammingLanguageCode = model
				.getIfCondition();
		Text conditionTxt = createTextInput(group, Messages
				.getString("IfThenElse.editor.if"),
				ifProgrammingLanguageCode == null ? ""
						: ifProgrammingLanguageCode.getStringValue(), false);
		conditionTxt.addModifyListener(new TextStyledTextModyfiListener(model,
				ProgrammingLanguageCodeType.class, getEditorIdentification()));

		// if condition lang
		String programmingLanguage = ifProgrammingLanguageCode == null ? ""
				: ifProgrammingLanguageCode.getProgrammingLanguage();
		if (programmingLanguage == null) {
			programmingLanguage = "";
		}
		Text programmingLanguageTxt = createTextInput(group, Messages
				.getString("StatementItem.editor.programlang"),
				programmingLanguage, false);
		programmingLanguageTxt
				.addModifyListener(new TextStyledTextModyfiListener(model,
						ModelIdentifingType.Type_A.class,
						getEditorIdentification()));

		// if question ref
		try {
			questionRefList = DdiManager.getInstance().getQuestionItemsLight(
					null, null, null, null).getLightXmlObjectList()
					.getLightXmlObjectList();
		} catch (Exception e) {
			DialogUtil.errorDialog(site, ID, e.getMessage(), e);
		}
		ReferenceSelectionCombo questionRefSelectCombo = createRefSelection(
				group, Messages.getString("IfThenElse.editor.ifquestionref"),
				Messages.getString("IfThenElse.editor.ifquestionref"), model
						.getIfQuestionReference(), questionRefList, false);
		questionRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.ifquestionref"), questionRefList,
				new ReferenceSelectionAdapter(questionRefSelectCombo, model,
						ModelIdentifingType.Type_B.class,
						getEditorIdentification()));

		// then ref
		MaintainableLightLabelQueryResult controlConstructRefListTemp = null;
		try {
			controlConstructRefListTemp = DdiManager.getInstance()
					.getInstrumentLabel(null, null, null, null);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(site, ID, e.getMessage(), e);
		}
		List<LightXmlObjectType> controlConstructRefList = new ArrayList<LightXmlObjectType>();
		for (LinkedList<LightXmlObjectType> lightXmlObjectList : controlConstructRefListTemp
				.getResult().values()) {
			controlConstructRefList.addAll(lightXmlObjectList);
		}
		controlConstructRefListTemp = null;

		ReferenceSelectionCombo thenRefSelectCombo = createRefSelection(group,
				Messages.getString("IfThenElse.editor.thenref"), Messages
						.getString("IfThenElse.editor.thenref"), model
						.getThenReference(), controlConstructRefList, false);
		thenRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.thenref"),
				controlConstructRefList, new ReferenceSelectionAdapter(
						thenRefSelectCombo, model, ModelIdentifingType.Type_C
								.class, getEditorIdentification()));

		// else ref
		ReferenceSelectionCombo elseRefSelectCombo = createRefSelection(group,
				Messages.getString("IfThenElse.editor.elseref"), Messages
						.getString("IfThenElse.editor.elseref"), model
						.getElseReference(), controlConstructRefList, false);
		elseRefSelectCombo.addSelectionListener(Messages
				.getString("IfThenElse.editor.elseref"),
				controlConstructRefList, new ReferenceSelectionAdapter(
						elseRefSelectCombo, model, ModelIdentifingType.Type_D
								.class, getEditorIdentification()));

		// description tab
		// name
		TabItem tabItem2 = createTabItem(Messages
				.getString("editor.label.description"));
		Group group2 = createGroup(tabItem2, Messages
				.getString("editor.label.description"));

		createNameInput(group2, Messages.getString("editor.label.name"), model
				.getDocument().getIfThenElse().getConstructNameList(), model
				.getDocument().getIfThenElse().getId());

		createStructuredStringInput(group2, Messages
				.getString("editor.label.description"), model.getDocument()
				.getIfThenElse().getDescriptionList(), model.getDocument()
				.getIfThenElse().getId());

		// id tab
		createPropertiesTab(getTabFolder());

		// xml tab
		createXmlTab(model);

		editorStatus.clearChanged();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		XmlOptions ops = new XmlOptions();
		ops.setSavePrettyPrint();
		try {
			if (editorInput.getEditorMode().equals(EditorModeType.NEW)) {
				dao.create(model);
				editorInput.setEditorMode(EditorModeType.EDIT);
			} else if (editorInput.getEditorMode().equals(EditorModeType.EDIT)) {
				dao.update(model);
			} else if (editorInput.getEditorMode().equals(EditorModeType.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = Messages
					.getString("IfThenElseEditor.mess.ErrorDuringSave"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
	}

	@Override
	public String getPreferredPerspectiveId() {
		return InstrumentPerspective.ID;
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages
				.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.instruments"));
	}
}
