package org.ddialliance.ddieditor.ui.view;

import java.beans.PropertyChangeEvent;

import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.QuestionItems;
import org.ddialliance.ddieditor.ui.dbxml.QuestionSchemes;
import org.ddialliance.ddieditor.ui.view.View.ViewContentType;
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
		 * 		Root Element e.g. ConceptualScheme, QuestionScheme, etc. 
	 * @return Object[]
	 */
	public Object[] getElements(Object inputElement) {
		
		ViewContentType contentType = (ViewContentType )inputElement;

		try {
			if (contentType.equals(ViewContentType.ConceptContent)) {
				// Return all Light Question Schemes:
				return ConceptSchemes.getConceptSchemesLight(null, null).toArray();
			} else if (contentType.equals(ViewContentType.QuestionContent)) {
				// Return all Light Question Schemes:
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
		log.debug("Get Child of Parent Id: " + ((LightXmlObjectType) parentElement).getId());
		if (parentElement instanceof LightXmlObjectType) {
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
