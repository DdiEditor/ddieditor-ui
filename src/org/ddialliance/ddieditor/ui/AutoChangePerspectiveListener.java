package org.ddialliance.ddieditor.ui;

import org.ddialliance.ddieditor.ui.view.Messages;
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

public class AutoChangePerspectiveListener implements IPartListener, IStartup {
	private static final Log log = LogFactory.getLog(LogType.EXCEPTION,
			AutoChangePerspectiveListener.class);

	@Override
	public void partActivated(IWorkbenchPart part) {
		refresh(part);
	}

	public static void refresh(final IWorkbenchPart part) {
		if (!(part instanceof IAutoChangePerspective)) {
			return;
		}

		final IWorkbenchWindow workbenchWindow = part.getSite().getPage()
				.getWorkbenchWindow();

		IPerspectiveDescriptor currentPerspective = workbenchWindow
				.getActivePage().getPerspective();
		final String dedicatedPerspectiveId = ((IAutoChangePerspective) part)
				.getPreferredPerspectiveId();

		if (dedicatedPerspectiveId == null) {
			return;
		}

		if (currentPerspective == null
				|| !currentPerspective.getId().equals(dedicatedPerspectiveId)) {
			// load settings
			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			String keyToggle = dedicatedPerspectiveId + "perspective.toggle";
			String propertyToggle = store.getString(keyToggle);
			String keyYesNo = dedicatedPerspectiveId + "perspective.open";
			Integer propertyYesNo = null;

			// always ask
			if (!propertyToggle.equals(MessageDialogWithToggle.ALWAYS)) {
				MessageDialogWithToggle dialog = MessageDialogWithToggle
						.openYesNoQuestion(
								part.getSite().getShell(),
								Messages.getString("perspective.switch"),
								((IAutoChangePerspective) part)
										.getPerspectiveSwitchDialogText(),
								Messages.getString("perspective.switch.toogle"),
								false, store, keyYesNo);
				propertyYesNo = dialog.getReturnCode();
				store.setValue(keyYesNo, propertyYesNo);
			} else {
				propertyYesNo = store.getInt(keyYesNo);
			}

			if (propertyYesNo.equals(IDialogConstants.YES_ID)) {
				// open perspective
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						try {
							workbenchWindow.getWorkbench().showPerspective(
									dedicatedPerspectiveId, workbenchWindow);
						} catch (WorkbenchException e) {
							log.error(
									"Could not switch to dedicated perspective "
											+ dedicatedPerspectiveId + " for "
											+ part.getClass(), e);
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
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().addPartListener(
									new AutoChangePerspectiveListener());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
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
