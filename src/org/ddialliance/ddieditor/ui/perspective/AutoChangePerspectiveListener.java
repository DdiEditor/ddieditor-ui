package org.ddialliance.ddieditor.ui.perspective;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.Translator;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Auto change perspective listener
 */
public class AutoChangePerspectiveListener implements IPartListener, IStartup {
	private static final Log log = LogFactory.getLog(LogType.SYSTEM,
			AutoChangePerspectiveListener.class);
	final String KEY_TOGGLE = "perspective.toggle";
	final String KEY_YES_NO = "perspective.open";

	@Override
	public void partActivated(IWorkbenchPart part) {
		// guard
		if (!(part instanceof IAutoChangePerspective)) {
			return;
		}

		// check preferences
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (!store.getBoolean(PreferenceConstants.AUTO_CHANGE_PERSPECTIVE)) {
			return;
		}

		// current perspective
		final IWorkbenchWindow workbenchWindow = part.getSite().getPage()
				.getWorkbenchWindow();
		IPerspectiveDescriptor currentPerspective = workbenchWindow
				.getActivePage().getPerspective();
		final String dedicatedPerspectiveId = ((IAutoChangePerspective) part)
				.getPreferredPerspectiveId();
		// guard
		if (dedicatedPerspectiveId == null) {
			return;
		}

		// change
		if (currentPerspective == null
				|| !currentPerspective.getId().equals(dedicatedPerspectiveId)) {
			// load settings
			String keyToggle = dedicatedPerspectiveId + KEY_TOGGLE;
			boolean propertyToggle = store.getBoolean(keyToggle);
			String keyYesNo = dedicatedPerspectiveId + KEY_YES_NO;
			Integer propertyYesNo = -1;

			// always ask
			if (!propertyToggle) {
				MessageDialogWithToggle dialog = MessageDialogWithToggle
						.openYesNoQuestion(part.getSite().getShell(),
								Translator.trans("perspective.switch"),
								((IAutoChangePerspective) part)
										.getPerspectiveSwitchDialogText(),
								Translator.trans("perspective.switch.toogle"),
								false, store, keyYesNo);
				propertyYesNo = dialog.open();
				store.setValue(keyToggle, dialog.getToggleState());
			}

			if (propertyToggle || propertyYesNo.equals(IDialogConstants.YES_ID)) {
				// open perspective
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						try {
							workbenchWindow.getWorkbench().showPerspective(
									dedicatedPerspectiveId, workbenchWindow);
						} catch (WorkbenchException e) {
							DialogUtil.errorDialog(PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getShell(),
									dedicatedPerspectiveId, null,
									e.getMessage(), e);
						}
					}
				});
			}
		}
	}

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.addPartListener(
									new AutoChangePerspectiveListener());
				} catch (Exception e) {
					DialogUtil.errorDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(), "", null,
							e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// nothing to do
	}
}
