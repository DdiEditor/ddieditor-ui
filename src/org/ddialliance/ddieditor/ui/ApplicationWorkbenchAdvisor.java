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
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
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
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		if (log.isDebugEnabled()) {
			log.debug("Initialize: "+configurer.toString());
		}
		configurer.setSaveAndRestore(true);
	}
}
