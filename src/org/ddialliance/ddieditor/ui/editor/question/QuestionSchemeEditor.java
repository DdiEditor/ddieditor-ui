package org.ddialliance.ddieditor.ui.editor.question;

import java.text.MessageFormat;
import java.util.List;

import org.ddialliance.ddieditor.ui.IAutoChangePerspective;
import org.ddialliance.ddieditor.ui.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.model.QuestionScheme;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;

public class QuestionSchemeEditor extends Editor implements ISelectionListener, IAutoChangePerspective {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, QuestionSchemeEditor.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor";

	// Member variables:
	private QuestionScheme questionScheme;
	private IEditorSite site;
	private StyledText questionSchemeDescrStyledText;
	private TableViewer tableViewer;

	private enum TABITEM_INDEX {
		QUESTION_SCHEME
	};

	@Override
	public String getPreferredPerspectiveId() {
		return QuestionsPerspective.ID;
	}

	@Override
	public String getPerspectiveSwitchDialogText() {
		return MessageFormat.format(Messages.getString("perspective.switch.dialogtext"), Messages
				.getString("perspective.questions"));
	}

	@Override
	public void createPartControl(Composite parent) {
		log.debug("QuestionSchemeEditor.createPartControl called");

		List<TabItem> tabItemList = super.createStandardPartControl(parent, Messages
				.getString("QuestionSchemeEditor.label.QuestionSchemeEditorLabel.QuestionSchemeEditor"), Messages
				.getString("QuestionSchemeEditor.label.useTheEditorLabel.Description"), TABITEM_INDEX.values().length); //$NON-NLS-1$

		// Question Scheme Tab:
		// --------------

		// - Question Scheme Root Composite:
		final Composite questionSchemeComposite = new Composite(tabFolder, SWT.NONE);
		questionSchemeComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout = new GridLayout();
		questionSchemeComposite.setLayout(gridLayout);
		TabItem questionSchemeTabItem = tabItemList.get(TABITEM_INDEX.QUESTION_SCHEME.ordinal());
		questionSchemeTabItem.setControl(questionSchemeComposite);
		questionSchemeTabItem.setText(Messages
				.getString("QuestionSchemeEditor.label.questionSchemeTabItem.QuestionScheme")); //$NON-NLS-1$

		// - Question Scheme Group
		final Group questionGroup = new Group(questionSchemeComposite, SWT.NONE);
		final GridData gd_questionGroup = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gd_questionGroup.heightHint = 632;
		gd_questionGroup.widthHint = 861;
		questionGroup.setLayoutData(gd_questionGroup);
		questionGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		questionGroup.setLayout(gridLayout_1);
		questionGroup.setText("Question Scheme");

		// Question Scheme Label:
		final Label labelLabel = new Label(questionGroup, SWT.NONE);
		final GridData gd_conceptLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd_conceptLabel.horizontalIndent = 5;
		labelLabel.setLayoutData(gd_conceptLabel);
		labelLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		labelLabel.setText(Messages.getString("QuestionSchemeEditor.label.Label")); //$NON-NLS-1$

		final Text labelText = new Text(questionGroup, SWT.BORDER);
		labelText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Label changed");
				questionScheme.setLabel(labelText.getText());
				editorStatus.setChanged();
			}
		});
		final GridData gd_labelText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelText.setLayoutData(gd_labelText);
		labelText.setText(questionScheme.getLabel());

		// Question Scheme Description:
		final Label questionSchemeDescrLabel = new Label(questionGroup, SWT.NONE);
		final GridData gd_questionSchemeDescrLabel = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		gd_questionSchemeDescrLabel.horizontalIndent = 5;
		questionSchemeDescrLabel.setLayoutData(gd_questionSchemeDescrLabel);
		questionSchemeDescrLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		questionSchemeDescrLabel.setText(Messages.getString("QuestionSchemeEditor.label.DescriptionText.Label")); //$NON-NLS-1$

		questionSchemeDescrStyledText = new StyledText(questionGroup, SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
		questionSchemeDescrStyledText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				log.debug("Description changed");
				questionScheme.setDescr(questionSchemeDescrStyledText.getText());
				editorStatus.setChanged();
			}
		});
		questionSchemeDescrStyledText.setText(questionScheme.getDescr());
		final GridData gd_originalQuestionTextStyledText = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_originalQuestionTextStyledText.heightHint = 154;
		gd_originalQuestionTextStyledText.widthHint = 308;
		questionSchemeDescrStyledText.setLayoutData(gd_originalQuestionTextStyledText);

		// Clean dirt from initialisation
		editorStatus.clearChanged();
	}

	public Viewer getViewer() {
		log.debug("QuestionSchemeEditor.getViewer()");
		return this.tableViewer;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		log.debug("QuestionSchemeEditor.doSave()");
		super.doSave(monitor);

		try {
			questionScheme.validate();
		} catch (Exception e1) {
			String errMess = MessageFormat.format(
					Messages.getString("QuestionSchemeEditor.mess.ValidationError"), e1.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			return;
		}
		try {
			if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
				QuestionSchemes.create(questionScheme);
				editorInput.setEditorMode(EDITOR_MODE_TYPE.EDIT);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)) {
				QuestionSchemes.update(questionScheme);
			} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
				log.debug("*** Saved ignored! ***");
			}
		} catch (Exception e) {
			String errMess = MessageFormat.format(
					Messages.getString("QuestionSchemeEditor.mess.ErrorDuringSave"), e.getMessage()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			return;
		}
		editorInput.getParentView().refreshView();
		editorStatus.clearChanged();
		log.debug("QuestionSchemeEditor.doSave(1): " + editorStatus.getStatus());
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// Initialize the Stander Editor Part:
		super.init(site, input);
		// Initialize Question Scheme Editor Part:
		this.editorInput = (EditorInput) input;
		if (log.isDebugEnabled()) {
			log.debug("QuestionSchemeEditor.init() - Name: " + editorInput.getName());
			log.debug("QuestionSchemeEditor.init() - ID: " + editorInput.getId());
			log.debug("QuestionSchemeEditor.init() - Parent ID: " + editorInput.getParentId());
			log.debug("QuestionSchemeEditor.init() - Editor Mode: " + editorInput.getEditorMode());
		}

		QuestionSchemes.init(((EditorInput) input).getProperties());

		if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.NEW)) {
			try {
				questionScheme = QuestionSchemes.createQuestionScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				log.error("QuestionSchemeEditor.init(): " + e.getMessage());
				String errMess = MessageFormat.format(Messages
						.getString("QuestionSchemeEditor.mess.ErrorDuringCreateNewQuestionScheme"), e.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
				System.exit(0);
			}
		} else if (editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.EDIT)
				|| editorInput.getEditorMode().equals(EDITOR_MODE_TYPE.VIEW)) {
			try {
				questionScheme = QuestionSchemes.getQuestionScheme(editorInput.getId(), editorInput.getVersion(),
						editorInput.getParentId(), editorInput.getParentVersion());
			} catch (Exception e) {
				String errMess = MessageFormat.format(Messages
						.getString("QuestionSchemeEditor.mess.GetQuestionSchemeByIdError"), e.getMessage()); //$NON-NLS-1$
				MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
				System.exit(0);
			}
		} else {
			String errMess = MessageFormat.format(
					Messages.getString("QuestionSchemeEditor.mess.UnknownEditorMode"), editorInput.getEditorMode()); //$NON-NLS-1$
			MessageDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), errMess);
			System.exit(0);
		}

		this.site = site;
		setSite(site);
		setInput(editorInput);
		setPartName(editorInput.getId());
	}

}
