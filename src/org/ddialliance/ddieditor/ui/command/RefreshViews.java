package org.ddialliance.ddieditor.ui.command;

import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Command to refresh open views.<br>
 * <br>
 * Current implementation is using depending on:
 * 
 * @see org.ddialliance.ddieditor.ui.view.AutoUpdateView
 */
public class RefreshViews extends org.eclipse.core.commands.AbstractHandler {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			RefreshViews.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow windows[] = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		IWorkbenchPage page = null;
		for (int i = 0; i < windows.length; i++) {
			page = windows[i].getActivePage();
		}

		// guard
		if (page == null) {
			LogFactory.getLog(LogType.EXCEPTION,
					RefreshViews.class).error("Refresh error", new Throwable());
		}
		
		IViewReference[] viewRefs = page.getViewReferences();
		for (int j = 0; j < viewRefs.length; j++) {
			IViewPart viewPart = page.findView(viewRefs[j].getId());
			if (viewPart != null && viewPart instanceof View) {
				// ((View) viewPart).refreshView();

				// deactivate - activate - to hook into auto refresh view
				try {
					page.hideView(viewPart);
					page.showView(viewRefs[j].getId());
				} catch (Exception e) {
					LogFactory.getLog(LogType.EXCEPTION,
							RefreshViews.class).error("Refresh error", e);
				}
				if (log.isDebugEnabled()) {
					log.debug(viewPart.getClass().getName());
				}
			}
		}
		return null;
	}
	// dev code to be deleted when used execute method is found valid 20100901

	// public Object executeDev(ExecutionEvent event) throws ExecutionException
	// {
	// // workbench - window - page - view
	// IWorkbench iWorkbench = PlatformUI.getWorkbench();
	//
	// // window
	// IWorkbenchWindow[] workbenchWindows = iWorkbench.getWorkbenchWindows();
	//
	// // view ids
	// IViewDescriptor[] views = PlatformUI.getWorkbench().getViewRegistry()
	// .getViews();
	// log.debug("Views. " + views.length);
	//
	// IWorkbenchPage[] pages = null;
	// IViewReference viewRef = null;
	// IViewReference[] viewRefs = null;
	// IViewPart view = null;
	//
	// pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
	// log.debug("Pages: " + pages.length);
	//
	// WorkbenchWindow w;
	// // loop pages
	// for (int b = 0; b < pages.length; b++) {
	// log.debug("Page: " + pages[b].getLabel());
	// IPerspectiveDescriptor[] perspectives = pages[b]
	// .getOpenPerspectives();
	// log.debug("Perspectives: " + perspectives.length);
	//
	// viewRefs = pages[b].getViewReferences();
	// for (int i = 0; i < perspectives.length; i++) {
	// log.debug(perspectives[i].getId());
	// for (int j = 0; j < viewRefs.length; j++) {
	// IViewPart viewPart = pages[b].findView(viewRefs[j].getId());
	// if (viewPart != null) {
	// IViewPart[] viewParts = PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow().getActivePage()
	// .getViewStack(viewPart);
	// log.debug("viewParts.size: " + viewParts.length);
	// }
	// w = ((WorkbenchWindow) PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow());
	//
	// w.firePerspectiveChanged(PlatformUI.getWorkbench()
	// .getActiveWorkbenchWindow().getActivePage(),
	// perspectives[i], viewRefs[j],
	// IWorkbenchPage.CHANGE_EDITOR_OPEN);
	// log.debug("ViewRef refresh: " + viewRefs[j].getId());
	// }
	// }
	// }
	// return null;
	// }
}
