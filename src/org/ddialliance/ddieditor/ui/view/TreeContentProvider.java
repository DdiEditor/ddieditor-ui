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
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemes;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemes;
import org.ddialliance.ddieditor.ui.dbxml.concept.Concepts;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
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
public class TreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeContentProvider.class);
	private List<ConceptualElement> conceptualList;

	public static final String ID = "org.ddialliance.ddieditor.ui.view.TreeContentProvider";
	private IViewSite site;

	public TreeContentProvider(IViewSite site) {
		this.site = site;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		log.debug("TreeContentProvider.inputChanged()");
	}

	@Override
	public void dispose() {
		// do nothing
	}

	/**
	 * Get Question Scheme root elements.
	 * 
	 * @param Input
	 *            Element Root Element e.g. StudyContent, ConceptualScheme,
	 *            QuestionScheme, etc.
	 * @return Object[]
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		ViewContentType contentType = (ViewContentType) inputElement;
		try {
			if (contentType.equals(ViewContentType.StudyContent)) {
				return PersistenceManager.getInstance().getResources()
						.toArray();
			} else if (contentType.equals(ViewContentType.ConceptContent)) {
				return ConceptSchemes.getConceptSchemesLight(null, null)
						.toArray();
			} else if (contentType.equals(ViewContentType.CodeContent)) {
				return CodeSchemes.getCodeSchemesLight(null, null).toArray();
			} else if (contentType.equals(ViewContentType.QuestionContent)) {
				return new QuestionSchemeDao().getLightXmlObject(null, null)
						.toArray();
			} else if (contentType
					.equals(ViewContentType.InstrumentationContent)) {
				MaintainableLightLabelQueryResult maLightLabelQueryResult = DdiManager
						.getInstance().getInstrumentLabel(null, null, null,
								null);
				return new Object[] { maLightLabelQueryResult };
			}
		} catch (Exception e) {
			String errMess = Messages.getString("View.mess.GetElementError"); //$NON-NLS-1$
			ErrorDialog.openError(site.getShell(), Messages
					.getString("ErrorTitle"), null, new Status(IStatus.ERROR,
					ID, 0, errMess, e));
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
	@Override
	public Object[] getChildren(Object parentElement) {
		// conceptual overview elements
		if (parentElement instanceof DDIResourceType) {
			if (conceptualList == null) {
				try {
					PersistenceManager.getInstance().setWorkingResource(
							((DDIResourceType) parentElement).getOrgName());

					conceptualList = DdiManager.getInstance()
							.getConceptualOverview();
				} catch (Exception e) {
					displayGetChildrenException(e);
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
				displayGetChildrenException(e);
			}
			return (new Object[0]);
		}
		// light xml objects
		else if (parentElement instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) parentElement;
			String lightXmlTypeLocalname = lightXmlObjectType.getElement();
			if (log.isDebugEnabled()) {
				log.debug("Retrieve sub elements of: " + lightXmlTypeLocalname
						+ ", id:" + lightXmlObjectType.getId());
			}

			// logging no children - guard
			if (log.isDebugEnabled()) {
				if (lightXmlTypeLocalname.equals("Concept")) {
					log.debug("Parent is Concept - no child!");
					return (new Object[0]);
				} else if (lightXmlTypeLocalname.equals("QuestionItem")) {
					log.debug("Parent is Question Item - no child!");
					return (new Object[0]);
				}
			}

			try {
				if (lightXmlTypeLocalname.equals("ConceptScheme")) {
					return Concepts.getConceptsLight(lightXmlObjectType)
							.toArray();
				} else if (lightXmlTypeLocalname.equals("QuestionScheme")) {
					return new QuestionItemDao().getLightXmlObject(
							lightXmlObjectType).toArray();
				} else if (lightXmlTypeLocalname
						.equals("ControlConstructScheme")) {
					return DdiManager.getInstance().getQuestionConstructsLight(
							null, null, lightXmlObjectType.getId(),
							lightXmlObjectType.getVersion())
							.getLightXmlObjectList().getLightXmlObjectList()
							.toArray();
				}
			} catch (Exception e) {
				displayGetChildrenException(e);
			}
		}

		// maintainable light label
		if (parentElement instanceof MaintainableLightLabelQueryResult) {
			MaintainableLightLabelQueryResult maLightLabelQueryResult = (MaintainableLightLabelQueryResult) parentElement;
			return maLightLabelQueryResult.getResult().values().toArray();
		} else if (parentElement instanceof List<?>) {
			return ((List<?>) parentElement).toArray();
		}
		
		// guard
		return (new Object[0]);
	}

	private void displayGetChildrenException(Exception e) {
		String errMess = Messages.getString("View.mess.GetChildError"); //$NON-NLS-1$
		// log exception
		if (!(e instanceof DDIFtpException)) {
			Log elog = LogFactory.getLog(LogType.EXCEPTION,
					TreeContentProvider.class);
			elog.error(errMess, e);
		}
		ErrorDialog.openError(site.getShell(),
				Messages.getString("ErrorTitle"), null, new Status(
						IStatus.ERROR, ID, 0, errMess, e));
	}

	/**
	 * @param element
	 *            Element
	 * @return Object Parent Element
	 */
	@Override
	public Object getParent(Object element) {
		log.debug("Not implemented return: null");
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// light xml beans
		if (element instanceof LightXmlObjectType) {
			String localName = ((LightXmlObjectType) element).getElement();
			boolean result = false;

			// scheme
			if (localName.indexOf("Scheme") > -1) {
				result = true;
				if (log.isDebugEnabled()) {
					log.debug(localName + ": " + result);
				}
			}
			// TODO check up on this, might add other *Names maybe in property
			// file or something
			return result;
		}
		// maintainable light label
		else if (element instanceof MaintainableLightLabelQueryResult) {
			return true;
		}
		// sub elements of maintainable light label
		else if (element instanceof List<?>) {
			if (!((List<?>) element).isEmpty()) {
				return true;
			}
		}

		// guard
		// TODO - subject to change
		return getChildren(element).length > 0;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		log.debug("TreeContentProvider.propertyChange()");
	}
}
