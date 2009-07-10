package org.ddialliance.ddieditor.ui.view;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceDocument;
import org.ddialliance.ddieditor.model.resource.impl.DDIResourceTypeImpl;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.util.ResourceManager;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class InfoView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, InfoView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";

	public InfoView() {
		super(ViewContentType.StudyContent, Messages.getString("InfoView.label.titleLabel.DDIOverview"), Messages
				.getString("InfoView.lable.selectLabel.Description"),
				Messages.getString("InfoView.lable.maskLabel.Id"), "", Messages.getString("View.lable.questionItemsTreeGroup.QuestionItemStructure"), null);
	}
	
	private void switchPerspective() {
		String perspectiveId = "";
		
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof DDIResourceTypeImpl) {
			return;
		} else if (obj instanceof ConceptualType) {
			String name = ((ConceptualType) obj).name();
			if (name.equals("LOGIC_concepts")) {
				perspectiveId = ConceptsPerspective.ID;
			} else if (name.equals("LOGIC_questions")) {
				perspectiveId = QuestionsPerspective.ID;
			} else {
				log.error("Switch Perspective failed: Conceptual name '" + ((ConceptualType) obj).name()
						+ "' currently not supported.");
				MessageDialog.openInformation(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.ConceptualNameCurrentlyNotSupported") + ((ConceptualType) obj).name() + "'"); //$NON-NLS-1$
				return;
			}
		} else if (obj instanceof ConceptualElement) {
			LightXmlObjectType item = (LightXmlObjectType) ((ConceptualElement) obj).getValue();
			if (item.getElement().equals("ConceptScheme")) {
				perspectiveId = ConceptsPerspective.ID;
			} else if (item.getElement().equals("QuestionScheme")) {
				perspectiveId = QuestionsPerspective.ID;
			} else {
				log.error("Switch Perspective failed: Element type '" + item.getElement() + "' not supported.");
				MessageDialog.openInformation(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.ElementTypeCurrentlyNotSupported") + item.getElement() + "'"); //$NON-NLS-1$
				return;
			}
		}

		try {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getWorkbenchWindow();
			workbenchWindow.getWorkbench().showPerspective(perspectiveId, workbenchWindow);
		} catch (Exception e) {
			log.error("Switch Perspective failed: " + e.getMessage());
			MessageDialog.openInformation(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
					.getString("View.mess.SwitchPerspectiveFailed") + e.getMessage()); //$NON-NLS-1$
			return;
		}
	}

	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		
		// Create Pop-up Menu:
		Menu menu = new Menu(treeViewer.getTree());
		// Assign double click to edit function
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				switchPerspective();
			}
		});

		// Define Tree Pop-up Menu
		treeViewer.getTree().setMenu(menu);

		// Define OPEN Pop-up Menu Item
		final MenuItem openMenuItem = new MenuItem(menu, SWT.NONE);
		openMenuItem.setSelection(true);
		openMenuItem.setText(Messages.getString("View.label.openMenuItem.Open"));
		openMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/new_persp.gif"));
		openMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				switchPerspective();
			}
		});
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof DDIResourceTypeImpl) {
					openMenuItem.setEnabled(false);
				} else if (obj instanceof ConceptualType) {
					String name = ((ConceptualType) obj).name();
					if (name.equals("STUDY") || name.equals("LOGIC_universe") || name.equals("LOGIC_instumentation")) {
						openMenuItem.setEnabled(false);
					} else {
						openMenuItem.setEnabled(true);
					}
				} else if (obj instanceof ConceptualElement) {
					LightXmlObjectType item = (LightXmlObjectType) ((ConceptualElement) obj).getValue();
					if (item.getElement().equals("studyunit__StudyUnit")) {
						openMenuItem.setEnabled(false);
					} else {
						openMenuItem.setEnabled(true);
					}
				}
			}
		});

		menu.setDefaultItem(openMenuItem);
	}
}
