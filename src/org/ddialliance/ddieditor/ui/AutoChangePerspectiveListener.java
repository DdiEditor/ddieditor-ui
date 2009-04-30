package org.ddialliance.ddieditor.ui;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class AutoChangePerspectiveListener implements IPartListener, IStartup {
    private static final Log log = LogFactory.getLog(LogType.EXCEPTION, AutoChangePerspectiveListener.class);
    
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

        IPerspectiveDescriptor currentPerspective = workbenchWindow.getActivePage().getPerspective();
        final String dedicatedPerspectiveId = ((IAutoChangePerspective) part)
                .getPreferredPerspectiveId();

        if (dedicatedPerspectiveId == null) {
            return;
        }

        if (currentPerspective == null || !currentPerspective.getId().equals(dedicatedPerspectiveId)) {
            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    try {
                        workbenchWindow.getWorkbench().showPerspective(dedicatedPerspectiveId,
                                workbenchWindow);
                    } catch (WorkbenchException e) {
                        log.error("Could not switch to dedicated perspective "
                                + dedicatedPerspectiveId + " for " + part.getClass(), e);
                    }
                }

            });
        }
    }

    @Override
    public void earlyStartup() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .addPartListener(new AutoChangePerspectiveListener());
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
