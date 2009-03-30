package org.ddialliance.ddieditor.ui;

import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;


public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ApplicationWorkbenchAdvisor.class);
	
	private static final String PERSPECTIVE_ID = Perspective.ID;

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		log.debug("ApplicationWorkbenchAdvisor.getInitialWindowPerspectiveId("+PERSPECTIVE_ID+")");
		return PERSPECTIVE_ID;
//		return null; // No initial perspective
	}
	
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		log.debug("ApplicationWorkbenchAdvisor.initialize()");
		configurer.setSaveAndRestore(true);
	}
}
