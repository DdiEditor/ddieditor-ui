package org.ddialliance.ddieditor.ui;

/**
 * Application Action Bar Advisor.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.util.OpenFileAction;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction undoAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction helpContentsAction;
    private IWorkbenchAction quitAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction printAction;
    private IWorkbenchAction refreshAction;
    private IWorkbenchAction renameAction;
    private IWorkbenchAction moveAction;
    private IWorkbenchAction exportAction;
    private IWorkbenchAction importAction;
    private IWorkbenchAction revertAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction saveAsAction;
    private IWorkbenchAction saveAction;
//    private IWorkbenchAction closeAllSavedAction;  - see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236398
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction showViewAction;
    private IWorkbenchAction openFileAction;
    private IWorkbenchAction prefs;
    
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(IWorkbenchWindow window) {
		{
			openFileAction = new OpenFileAction(window);
			register(openFileAction);
		}
		{
			closeAction = ActionFactory.CLOSE.create(window);
			register(closeAction);
		}
		{
			closeAllAction = ActionFactory.CLOSE_ALL.create(window);
			register(closeAllAction);
		}
//		{
//			closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(window);
//			register(closeAllSavedAction);
//		}
		{
			saveAction = ActionFactory.SAVE.create(window);
			register(saveAction);
		}
		{
			saveAsAction = ActionFactory.SAVE_AS.create(window);
			register(saveAsAction);
		}
		{
			saveAllAction = ActionFactory.SAVE_ALL.create(window);
			register(saveAllAction);
		}
		{
			revertAction = ActionFactory.REVERT.create(window);
			register(revertAction);
		}
		{
			importAction = ActionFactory.IMPORT.create(window);
			register(importAction);
		}
		{
			exportAction = ActionFactory.EXPORT.create(window);
			register(exportAction);
		}
		{
			moveAction = ActionFactory.MOVE.create(window);
			register(moveAction);
		}
		{
			renameAction = ActionFactory.RENAME.create(window);
			register(renameAction);
		}
		{
			refreshAction = ActionFactory.REFRESH.create(window);
			register(refreshAction);
		}
		{
			printAction = ActionFactory.PRINT.create(window);
			register(printAction);
		}
		{
			propertiesAction = ActionFactory.PROPERTIES.create(window);
			register(propertiesAction);
		}
		{
			quitAction = ActionFactory.QUIT.create(window);
			quitAction.setToolTipText("Leave application");
			register(quitAction);
		}
		{
			helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
			register(helpContentsAction);
		}
		{
			cutAction = ActionFactory.CUT.create(window);
			register(cutAction);
		}
		{
			copyAction = ActionFactory.COPY.create(window);
			register(copyAction);
		}
		{
			pasteAction = ActionFactory.PASTE.create(window);
			register(pasteAction);
		}
		{
			deleteAction = ActionFactory.DELETE.create(window);
			register(deleteAction);
		}
		{
			undoAction = ActionFactory.UNDO.create(window);
			register(undoAction);
		}
		{
			aboutAction = ActionFactory.ABOUT.create(window);
			register(aboutAction);
		}
		{
			showViewAction = ActionFactory.SHOW_VIEW_MENU.create(window);
			register(showViewAction);
		}
		{
			prefs = ActionFactory.PREFERENCES.create(window);
			register(prefs);
		}		
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	final MenuManager fileMenuManager = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.menu.File"));
    	menuBar.add(fileMenuManager);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(openFileAction);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(closeAction);

    	fileMenuManager.add(closeAllAction);

//    	fileMenuManager.add(closeAllSavedAction);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(saveAction);

    	fileMenuManager.add(saveAsAction);

    	fileMenuManager.add(saveAllAction);

    	fileMenuManager.add(revertAction);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(importAction);

    	fileMenuManager.add(exportAction);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(moveAction);

    	fileMenuManager.add(renameAction);

    	fileMenuManager.add(refreshAction);

    	fileMenuManager.add(new Separator());

    	fileMenuManager.add(printAction);

    	fileMenuManager.add(propertiesAction);

    	fileMenuManager.add(quitAction);
    	
    	final MenuManager windowMenuManager = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.menu.Window"));
    	menuBar.add(windowMenuManager);

    	windowMenuManager.add(new Separator());

    	windowMenuManager.add(showViewAction);

    	final MenuManager helpMenuManager = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.menu.Help"));
    	menuBar.add(helpMenuManager);
    	helpMenuManager.add(new Separator());
    	helpMenuManager.add(prefs);
    	helpMenuManager.add(aboutAction);
    }
    
	protected void fillCoolBar(ICoolBarManager coolBar) {
		final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		coolBar.add(toolBarManager);

		toolBarManager.add(undoAction);

		toolBarManager.add(saveAction);

		toolBarManager.add(helpContentsAction);

		toolBarManager.add(new Separator());

		toolBarManager.add(cutAction);

		toolBarManager.add(copyAction);

		toolBarManager.add(pasteAction);

		toolBarManager.add(deleteAction);

		toolBarManager.add(new Separator());
	} 
}
