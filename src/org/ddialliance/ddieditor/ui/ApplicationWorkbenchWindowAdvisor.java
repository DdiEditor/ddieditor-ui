package org.ddialliance.ddieditor.ui;

/**
 * Application Workbench Window Advisor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ApplicationWorkbenchWindowAdvisor.class);

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		if (log.isDebugEnabled()) {
			log
					.debug("ApplicationWorkbenchWindowAdvisor.ApplicationWorkbenchWindowAdvisor()");
		}
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		if (log.isDebugEnabled()) {
			log
					.debug("ApplicationWorkbenchWindowAdvisor.createActionBarAdvisor()");
		}
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		if (log.isDebugEnabled()) {
			log.debug("ApplicationWorkbenchWindowAdvisor.preWindowOpen()");
		}
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1300, 1200));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("DDI Editor");
		configurer.setShowPerspectiveBar(true);
		configurer.setShowFastViewBars(true);

		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR,
				IWorkbenchPreferenceConstants.TOP_RIGHT);
	}
}
