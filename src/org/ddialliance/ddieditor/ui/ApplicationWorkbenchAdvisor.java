package org.ddialliance.ddieditor.ui;

/**
 * Application Workbench Advisor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ApplicationWorkbenchAdvisor.class);

	private static final String INITIAL_PERSPECTIVE_ID = InfoPerspective.ID;

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		if (log.isDebugEnabled()) {
			log.debug(INITIAL_PERSPECTIVE_ID);
		}
		return INITIAL_PERSPECTIVE_ID;
	}

	@Override
	public boolean preShutdown() {
		// check preferences
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (!store.getBoolean(PreferenceConstants.CONFIRM_DDIEDITOR_EXIT)) {
			return true;
		}

		// always ask
		MessageDialogWithToggle dialog = MessageDialogWithToggle
				.openYesNoQuestion(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(),
						Messages.getString("ExitDDIEditor.dialog.title"),
						Messages.getString("ExitDDIEditor.dialog.mess.DoYouReallyWantToExitTheDDIEditor"),
						Messages.getString("ExitDDIEditor.Dialog.label.ConfirmDDIEditorExit"),
						store.getBoolean(PreferenceConstants.CONFIRM_DDIEDITOR_EXIT),
						null, PreferenceConstants.CONFIRM_DDIEDITOR_EXIT);
		store.setValue(PreferenceConstants.CONFIRM_DDIEDITOR_EXIT,
				dialog.getToggleState());
		if (dialog.getReturnCode() == 2) {
			return true;
		}
		return false;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		if (log.isDebugEnabled()) {
			log.debug("Initialize: " + configurer.toString());
		}
		configurer.setSaveAndRestore(true);
	}
}
