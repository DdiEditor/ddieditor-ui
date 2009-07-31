package org.ddialliance.ddieditor.ui.view;


/**
 * Tree Content Provider.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.dbxml.CodeSchemes;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.view.View.ViewContentType;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

/**
 * Light XML Object - Tree Viewer Content provider
 */
class TreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, TreeContentProvider.class);
	private List<ConceptualElement> conceptualList;
	
	public static final String ID = "org.ddialliance.ddieditor.ui.view.TreeContentProvider";
	private IViewSite site;
	
	public TreeContentProvider(IViewSite site) {
		
		this.site = site;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		log.debug("TreeContentProvider.inputChanged()");
	}

	public void dispose() {

	}

	/**
	 * Get Question Scheme root elements.
	 * 
	 * @param Input Element
		 * 		Root Element e.g. StudyContent, ConceptualScheme, QuestionScheme, etc. 
	 * @return Object[]
	 */
	public Object[] getElements(Object inputElement) {
		
		ViewContentType contentType = (ViewContentType )inputElement;

		try {
			if (contentType.equals(ViewContentType.StudyContent)) {
				List<DDIResourceType> list = PersistenceManager.getInstance().getResources();
				DDIResourceType[] array = new DDIResourceType[list.size()];
				array = list.toArray(array);
				return array;
			} else if (contentType.equals(ViewContentType.ConceptContent)) {
				return ConceptSchemes.getConceptSchemesLight(null, null).toArray();
			} if (contentType.equals(ViewContentType.CodeContent)) {
				return CodeSchemes.getCodeSchemesLight(null, null).toArray();
			} else if (contentType.equals(ViewContentType.QuestionContent)) {
				return QuestionSchemes.getQuestionSchemesLight(null, null).toArray();
			}
		} catch (Exception e) {
			String errMess = Messages.getString("View.mess.GetElementError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
					IStatus.ERROR, ID, 0, errMess, e));
		}
		return null;
	}

	/**
	 * Get child elements of parent.
	 * 
	 * @param Parent
	 *            Object (LightXmlObjectType)
	 * @return Object[]
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DDIResourceType) {
			log.debug("Get Child of Parent Id: " + ((DDIResourceType) parentElement).getOrgName());
			if (conceptualList == null) {
				try {
					PersistenceManager.getInstance().setWorkingResource(((DDIResourceType) parentElement).getOrgName());

					conceptualList = DdiManager.getInstance().getConceptualOverview();
				} catch (DDIFtpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			List<ConceptualType> list = new ArrayList<ConceptualType>();
			for (ConceptualElement conceptualElement : conceptualList) {
				if (!list.contains(conceptualElement.getType())) {
					list.add(conceptualElement.getType());
				}
			}
			ConceptualType[] result = new ConceptualType[list.size()];
			return list.toArray(result);
		} else if (parentElement instanceof ConceptualType) {
			// list Conceptual Elements e.g STUDY, LOGIC_
			try {
				List<ConceptualElement> list = new ArrayList<ConceptualElement>();
				for (ConceptualElement conceptualElement : conceptualList) {
					if (conceptualElement.getType().equals(parentElement)) {
						list.add(conceptualElement);
					}
				}
				ConceptualElement[] array = new ConceptualElement[list.size()];
				return list.toArray(array);
			} catch (Exception e) {
				// TODO msg view
				e.printStackTrace();
			}
			return (new Object[0]);		
		} else if (parentElement instanceof LightXmlObjectType) {
			log.debug("Get Child of Parent Id: " + ((LightXmlObjectType) parentElement).getId());
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) parentElement;
			
			if (lightXmlObjectType.getElement().equals("Concept")) {
				log.debug("Parent is Concept - no child!");
				return (new Object[0]);
			} else if (lightXmlObjectType.getElement().equals("QuestionItem")) {
				log.debug("Parent is Question Item - no child!");
				return (new Object[0]);
			}

			try {
				if (lightXmlObjectType.getElement().equals("ConceptScheme")) {
					return Concepts.getConceptsLight(lightXmlObjectType).toArray();
				} else if (lightXmlObjectType.getElement().equals("QuestionScheme")) {
					return QuestionItems.getQuestionItemsLight(lightXmlObjectType).toArray();
				}
			} catch (Exception e) {
				String errMess = Messages.getString("View.mess.GetChildError"); //$NON-NLS-1$
				ErrorDialog.openError(site.getShell(), Messages.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
			}
		}
		return (new Object[0]);
	}

	/**
	 * @param element
	 *            Element
	 * @return Object Parent Element
	 */
	public Object getParent(Object element) {
		log.debug("TreeContentProvider.getParent()");
		return null;
	}

	public boolean hasChildren(Object element) {
		log.debug("TreeContentProvider.hasChildren()");
		// TODO Do not access DB again
		return getChildren(element).length > 0;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		log.debug("TreeContentProvider.propertyChange()");
	}
}
