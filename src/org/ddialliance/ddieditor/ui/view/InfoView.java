package org.ddialliance.ddieditor.ui.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.impl.DDIResourceTypeImpl;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EDITOR_MODE_TYPE;
import org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor;
import org.ddialliance.ddieditor.ui.util.Entity;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class InfoView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, InfoView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";
	final Properties properties = new Properties();

	public InfoView() {
		super(ViewContentType.StudyContent, Messages.getString("InfoView.label.titleLabel.DDIOverview"), Messages
				.getString("InfoView.lable.selectLabel.Description"),
				Messages.getString("InfoView.lable.maskLabel.Id"), "", Messages.getString("View.lable.questionItemsTreeGroup.QuestionItemStructure"), null);

		try {
			properties.load(new FileInputStream("resources" + File.separator + "ddieditor-ui.properties"));
		} catch (IOException e) {
			System.err.println("Error during property load:" + e.getMessage());
			System.exit(0);
		}
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
	
	private void openEditor(EDITOR_MODE_TYPE mode) {
		LightXmlObjectType item = null;

		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof ConceptualElement) {
			ConceptualElement conceptElement = (ConceptualElement) obj;
			item =conceptElement.getValue();
		} else {
			log.error("Unexpected object type: "+obj.toString());
			MessageDialog.openInformation(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
					.getString("View.mess.EditorOpenError"));
			return;			
		}
		EditorInput.ENTITY_TYPE entityType = Entity.getEntityType(item);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		EditorInput input = new EditorInput(item.getId(), item.getVersion(), item.getParentId(), item
				.getParentVersion(), entityType, mode, currentView, properties);
		try {
			switch (entityType) {
			case STUDY_UNIT:
				page.openEditor(input, StudyUnitEditor.ID);
				break;
			default:
				// TODO error handling
				log.error("Editor Type not supported: " + entityType);
				MessageDialog.openInformation(getViewSite().getShell(), Messages.getString("ErrorTitle"), Messages
						.getString("View.mess.NotSupported"));
				System.exit(0);
				break;
			}

			// Notify any listeners of the view with the actual data of the view
			treeViewer.setSelection(treeViewer.getSelection());
		} catch (PartInitException ex) {
			MessageDialog.openError(currentView.getSite().getShell(), Messages.getString("ErrorTitle"), Messages
					.getString("View.mess.EditorOpenError") + "\n" + ex.getMessage()); //$NON-NLS-1$
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
		
		final MenuItem editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				if (t.length != 1) {
					MessageDialog.openInformation(currentView.getSite().getShell(),
							Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}
				openEditor(EDITOR_MODE_TYPE.EDIT);
			}
		});
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof DDIResourceTypeImpl) {
					editMenuItem.setEnabled(false);
				} else if (obj instanceof ConceptualType) {
					editMenuItem.setEnabled(false);
				} else if (obj instanceof ConceptualElement) {
					LightXmlObjectType item = (LightXmlObjectType) ((ConceptualElement) obj).getValue();
					if (item.getElement().equals("studyunit__StudyUnit")) {
						editMenuItem.setEnabled(true);
					} else {
						editMenuItem.setEnabled(false);
					}
				}
			}
		});


		menu.setDefaultItem(openMenuItem);
	}
}
