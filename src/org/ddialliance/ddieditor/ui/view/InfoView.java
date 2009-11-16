package org.ddialliance.ddieditor.ui.view;

/**
 * Info (Overview) View.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.impl.DDIResourceTypeImpl;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.editor.EditorInput.EditorModeType;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.perspective.ConceptsPerspective;
import org.ddialliance.ddieditor.ui.perspective.InfoPerspective;
import org.ddialliance.ddieditor.ui.perspective.InstrumentPerspective;
import org.ddialliance.ddieditor.ui.perspective.QuestionsPerspective;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.TreeMenu.NEW_TYPE;
import org.ddialliance.ddiftp.util.DDIFtpException;
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
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class InfoView extends View {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, InfoView.class);
	public static final String ID = "org.ddialliance.ddieditor.ui.view.InfoView";
	final Properties properties = new Properties();
	MenuItem openMenuItem = null;
	MenuItem newMenuItem = null;
	MenuItem deleteMenuItem = null;
	MenuItem editMenuItem = null;

	public InfoView() {
		super(ViewContentType.StudyContent, Messages
				.getString("InfoView.label.titleLabel.DDIOverview"), Messages
				.getString("InfoView.lable.selectLabel.Description"), Messages
				.getString("InfoView.lable.maskLabel.Id"), "", Messages
				.getString("InfoView.lable.treeGroup.DDIStructure"), null);

		try {
			properties.load(new FileInputStream("resources" + File.separator
					+ "ddieditor-ui.properties"));
		} catch (IOException e) {
			System.err.println("Error during property load:" + e.getMessage());
			System.exit(0);
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

		// Define OPEN (VIEW) Pop-up Menu Item
		openMenuItem = new MenuItem(menu, SWT.NONE);
		openMenuItem.setSelection(true);
		openMenuItem
				.setText(Messages.getString("View.label.openMenuItem.Open"));
		openMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_persp.gif"));
		openMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				switchPerspective();
			}
		});
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				enableMenu();
			}
		});

		// Define NEW Pop-up Menu Item
		newMenuItem = new MenuItem(menu, SWT.NONE);
		newMenuItem.setSelection(true);
		newMenuItem.setText(Messages
				.getString("View.label.newItemMenuItem.New"));
		newMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/new_wiz.gif"));
		newMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				LightXmlObjectType item = null;

				ISelection selection = treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof ConceptualType) {
					// TODO How to get parent id and version ??????????????????
					ConceptualType conceptualType = (ConceptualType) obj;
					// try {
					// LightXmlObjectListDocument lightXmlObjectListDocument =
					// DdiManager.getInstance().getDdiInstanceLight(null, null,
					// null, null);
					// System.out.println("************: "+lightXmlObjectListDocument.xmlText());
					// } catch (Exception e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
				} else if (obj instanceof ConceptualElement) {
					ConceptualElement conceptualElement = (ConceptualElement) obj;
					item = conceptualElement.getValue();
				} else if (obj instanceof LightXmlObjectType) {
					item = (LightXmlObjectType) obj;
				}
				TreeMenu.newItem(treeViewer, currentView, properties,
						NEW_TYPE.SCHEME, item.getParentId(), item
								.getParentVersion());
			}
		});

		// Define DELETE Pop-up Menu Item
		deleteMenuItem = new MenuItem(menu, SWT.NONE);
		deleteMenuItem.setSelection(true);
		deleteMenuItem.setText(Messages
				.getString("View.label.deleteMenuItem.Delete"));
		deleteMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/delete_obj.gif"));
		deleteMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				switchPerspective();
			}
		});

		// Define EDIT Pop-up Menu Item
		editMenuItem = new MenuItem(menu, SWT.NONE);
		editMenuItem
				.setText(Messages.getString("View.label.editMenuItem.Edit")); //$NON-NLS-1$
		editMenuItem.setImage(ResourceManager.getPluginImage(Activator
				.getDefault(), "icons/editor_area.gif"));
		editMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				TreeItem[] t = treeViewer.getTree().getSelection();
				if (t.length != 1) {
					MessageDialog
							.openInformation(
									currentView.getSite().getShell(),
									Messages.getString("InfoTitle"), Messages.getString("Editor.mess.NotSupported")); //$NON-NLS-1$
					return;
				}
				TreeMenu.openEditor(treeViewer, currentView, properties,
						EditorModeType.EDIT);
			}
		});

		menu.setDefaultItem(openMenuItem);
	}

	private void switchPerspective() {
		String perspectiveId = "";
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof DDIResourceTypeImpl) {
			return;
		} else if (obj instanceof ConceptualElement) {
			String item = ((LightXmlObjectType) ((ConceptualElement) obj)
					.getValue()).getElement();
			try {
				perspectiveId = ElementType.getPerspectiveId(item);
			} catch (DDIFtpException e) {
				MessageDialog.openInformation(getViewSite().getShell(),
						Messages.getString("ErrorTitle"), e.getMessage());
				return;
			}
		} else if (obj instanceof ConceptualType) {
			// mapping between ddieditor.model 
			// and ddieditor-ui.model aka perspective
			ConceptualType conTypeObj = (ConceptualType) obj;
			if (conTypeObj.equals(ConceptualType.STUDY)) {
				perspectiveId = InfoPerspective.ID;
			} else if (conTypeObj.equals(ConceptualType.LOGIC_Universe)) {
				perspectiveId = "";
			} else if (conTypeObj.equals(ConceptualType.LOGIC_concepts)) {
				perspectiveId = ConceptsPerspective.ID;
			} else if (conTypeObj.equals(ConceptualType.LOGIC_questions)) {
				perspectiveId = QuestionsPerspective.ID;
			} else if (conTypeObj.equals(ConceptualType.LOGIC_instumentation)) {
				perspectiveId = InstrumentPerspective.ID;
			}
		}

		if (perspectiveId.equals("")) {
			log.error("No perspective found for element : "+obj.getClass().getName()+", value: "+obj.toString());
			return;
		}
		
		try {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getWorkbenchWindow();
			workbenchWindow.getWorkbench().showPerspective(perspectiveId,
					workbenchWindow);
		} catch (Exception e) {
			log.error("Switch Perspective failed: " + e.getMessage());
			MessageDialog
					.openInformation(
							getViewSite().getShell(),
							Messages.getString("ErrorTitle"),
							Messages
									.getString("View.mess.SwitchPerspectiveFailed") + e.getMessage()); //$NON-NLS-1$
			return;
		}
	}

	private void enableMenu() {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof DDIResourceTypeImpl) {
			openMenuItem.setEnabled(false);
			newMenuItem.setEnabled(false);
			deleteMenuItem.setEnabled(false);
			editMenuItem.setEnabled(false);
		} else if (obj instanceof ConceptualType) {
			String name = ((ConceptualType) obj).name();
			if (name.equals("STUDY")) {
				openMenuItem.setEnabled(false);
			} else {
				openMenuItem.setEnabled(true);
			}
			newMenuItem.setEnabled(true);
			deleteMenuItem.setEnabled(false);
			editMenuItem.setEnabled(false);
		} else if (obj instanceof ConceptualElement) {
			LightXmlObjectType item = (LightXmlObjectType) ((ConceptualElement) obj)
					.getValue();
			if (item.getElement().equals("StudyUnit")) {
				openMenuItem.setEnabled(false);
			} else {
				openMenuItem.setEnabled(true);
			}
			newMenuItem.setEnabled(true);
			deleteMenuItem.setEnabled(true);
			editMenuItem.setEnabled(true);
		}
	}
}
