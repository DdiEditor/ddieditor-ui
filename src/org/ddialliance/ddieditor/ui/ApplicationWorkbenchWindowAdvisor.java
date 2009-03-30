package org.ddialliance.ddieditor.ui;

import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, ApplicationWorkbenchWindowAdvisor.class);
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        log.debug("ApplicationWorkbenchWindowAdvisor.ApplicationWorkbenchWindowAdvisor()");
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
    	log.debug("ApplicationWorkbenchWindowAdvisor.createActionBarAdvisor()");
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
    	log.debug("ApplicationWorkbenchWindowAdvisor.preWindowOpen()");
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(400, 300));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle("DDI Editor");
    }
}
