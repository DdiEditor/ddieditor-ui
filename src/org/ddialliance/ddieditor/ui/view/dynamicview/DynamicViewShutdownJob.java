package org.ddialliance.ddieditor.ui.view.dynamicview;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class DynamicViewShutdownJob implements Runnable {
	public void run() {
		// Get active page
		IWorkbenchWindow windows[] = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			for (IViewReference iViewReference : windows[i].getActivePage()
					.getViewReferences()) {
				IViewPart viewPart = windows[i].getActivePage().findView(
						iViewReference.getId());
				if (viewPart != null
						&& viewPart.getClass() == DynamicView.class) {
					((DynamicView) viewPart).cleanUp();
				}
			}
		}
	}
}
