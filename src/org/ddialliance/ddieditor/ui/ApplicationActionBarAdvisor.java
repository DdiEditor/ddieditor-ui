package org.ddialliance.ddieditor.ui;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

/**
 * Application Action Bar Advisor.
 * 
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		String[] unWantedActions = new String[] {
				"org.eclipse.search.searchActionSet",
				"org.eclipse.ui.edit.text.actionSet.annotationNavigation",
				"org.eclipse.ui.edit.text.actionSet.navigation",
				"org.eclipse.ui.actionSet.openFiles" };

		// hack to remove unwanted activities 
		// TODO at later stage refactor by defining ddieditor activities
		// and remove unwanted activities in plugin.xml
		ActionSetRegistry reg = WorkbenchPlugin.getDefault()
				.getActionSetRegistry();
		IActionSetDescriptor[] actionSets = reg.getActionSets();
		for (int i = 0; i < actionSets.length; i++) {
			// System.out.println("Id: " + actionSets[i].getId()
			// + ", description: " + actionSets[i].getDescription()
			// + ", label:" + actionSets[i].getLabel());
			for (int j = 0; j < unWantedActions.length; j++) {
				if (unWantedActions[j].equals(actionSets[i].getId())) {
					IExtension ext = actionSets[i].getConfigurationElement()
							.getDeclaringExtension();
					// removes the unwanted action
					reg.removeExtension(ext, new Object[] { actionSets[i] });
				}
			}
		}
	}

	protected void makeActions(IWorkbenchWindow window) {
		register(ActionFactory.SAVE.create(window));
		register(ActionFactory.SAVE_ALL.create(window));
	}
}
