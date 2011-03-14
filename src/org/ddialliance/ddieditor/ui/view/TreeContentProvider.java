package org.ddialliance.ddieditor.ui.view;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.conceptual.ConceptualElement;
import org.ddialliance.ddieditor.model.conceptual.ConceptualType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.impl.DDIResourceTypeImpl;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.maintainablelabel.MaintainableLightLabelQueryResult;
import org.ddialliance.ddieditor.ui.dbxml.category.CategoryDao;
import org.ddialliance.ddieditor.ui.dbxml.category.CategorySchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.code.CodeSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptDao;
import org.ddialliance.ddieditor.ui.dbxml.concept.ConceptSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.question.MultipleQuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionItemDao;
import org.ddialliance.ddieditor.ui.dbxml.question.QuestionSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.universe.UniverseSchemeDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableDao;
import org.ddialliance.ddieditor.ui.dbxml.variable.VariableSchemeDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
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
 * Tree viewer content provider
 */
public class TreeContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			TreeContentProvider.class);

	private Map<String, List<ConceptualElement>> conceptualElementCache = new HashMap<String, List<ConceptualElement>>();
	private String currentDdiResource = "";

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

	ViewContentType contentType = null;

	/**
	 * Based on the input an array of containing ddi elements is provided as
	 * content
	 * 
	 * @param inputElement
	 *            view content types
	 * @return array of elements
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		contentType = (ViewContentType) inputElement;
		try {
			return PersistenceManager.getInstance().getResources().toArray();
		} catch (DDIFtpException e) {
			ErrorDialog.openError(site.getShell(), "Error", null, new Status(
					IStatus.ERROR, ID, 0,
					"Error while opening meta data container", e));
		}
		return null;
	}

	private List<ConceptualElement> getConceptualList(
			DDIResourceType ddiResource) {
		List<ConceptualElement> result = conceptualElementCache.get(ddiResource
				.getOrgName());
		// Cach disabled due to missing sync. between views
		// TODO Implement sync. between views.
		result = null;

		if (result == null) {
			try {
				PersistenceManager.getInstance().setWorkingResource(
						ddiResource.getOrgName());

				result = DdiManager.getInstance().getConceptualOverview();
				conceptualElementCache.put(ddiResource.getOrgName(), result);
			} catch (Exception e) {
				displayGetChildrenException(e);
			}
		}
		return result;
	}

	/**
	 * Get child elements of parent.
	 * 
	 * @param parent
	 * @return array of children
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		// ddi resource type
		if (parentElement instanceof DDIResourceType) {
			currentDdiResource = ((DDIResourceType) parentElement).getOrgName();

			// view content types
			try {
				PersistenceManager.getInstance().setWorkingResource(
						currentDdiResource);

				if (contentType.equals(ViewContentType.StudyContent)) {
					List<ConceptualElement> conceptualList = getConceptualList((DDIResourceType) parentElement);
					List<ConceptualType> list = new ArrayList<ConceptualType>();
					for (ConceptualElement conceptualElement : conceptualList) {
						if (!list.contains(conceptualElement.getType())) {
							list.add(conceptualElement.getType());
						}
					}
					ConceptualType[] result = new ConceptualType[list.size()];
					return list.toArray(result);
				}
				if (contentType.equals(ViewContentType.ConceptContent)) {
					return new ConceptSchemeDao().getLightXmlObject(null, null,
							null, null).toArray();
				} else if (contentType.equals(ViewContentType.CategoryContent)) {
					Object[] objs = new CategorySchemeDao().getLightXmlObject(
							null, null, null, null).toArray();
					// addToParentCache(parentElement, objs);
					return objs;
				} else if (contentType.equals(ViewContentType.CodeContent)) {
					return CodeSchemeDao.getCodeSchemesLight(null, null)
							.toArray();
				} else if (contentType.equals(ViewContentType.QuestionContent)) {
					return new QuestionSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();					
				} else if (contentType
						.equals(ViewContentType.InstrumentationContent)) {
					LightXmlObjectListDocument listDoc = DdiManager
							.getInstance().getControlConstructSchemesLight(
									null, null, null, null);
					Object[] result = new Object[listDoc
							.getLightXmlObjectList().getLightXmlObjectList()
							.size()];
					int count = 0;
					for (LightXmlObjectType lightXmlObject : listDoc
							.getLightXmlObjectList().getLightXmlObjectList()) {
						result[count] = DdiManager.getInstance()
								.getInstrumentLabel(lightXmlObject.getId(),
										lightXmlObject.getVersion(),
										lightXmlObject.getParentId(),
										lightXmlObject.getParentVersion());
						count++;
					}
					return result;
				} else if (contentType.equals(ViewContentType.UniverseContent)) {
					return new UniverseSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				} else if (contentType.equals(ViewContentType.VariableContent)) {
					return new VariableSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				} else { // guard
					new DDIFtpException(
							"Not supported, contenttype: " + contentType
									+ ", parentElement: " + parentElement,
							new Throwable());
				}
			} catch (Exception e) {
				DialogUtil.errorDialog(site.getShell(), ID,
						Messages.getString("ErrorTitle"),
						Messages.getString("View.mess.GetElementError"), e);
			}
		}

		// conceptual type
		// TODO Is this double ? Is used ?
		if (parentElement instanceof ConceptualType) {
			try {
				PersistenceManager.getInstance().setWorkingResource(
						currentDdiResource);
				List<ConceptualElement> list = new ArrayList<ConceptualElement>();
				for (ConceptualElement conceptualElement : conceptualElementCache
						.get(currentDdiResource)) {
					if (conceptualElement.getType().equals(parentElement)) {
						list.add(conceptualElement);
					}
				}
				return list.toArray(new ConceptualElement[list.size()]);
			} catch (Exception e) {
				displayGetChildrenException(e);
			}
			return (new Object[0]);
		}

		// light xml objects
		else if (parentElement instanceof LightXmlObjectType) {
			LightXmlObjectType lightXmlObjectType = (LightXmlObjectType) parentElement;
			String lightXmlTypeLocalname = lightXmlObjectType.getElement();
			Object[] contentList = null;

			// Skip leafs as hasChild() does not filter these when view filter
			// used
			if (lightXmlTypeLocalname.indexOf("Scheme") < 0
					&& !lightXmlTypeLocalname.equals("MultipleQuestionItem")) {
				return contentList;
			}

			try {
				// code scheme
				if (lightXmlTypeLocalname.equals("CodeScheme")) {
					// Codes are supported by the Code Scheme Editor
					return (new Object[0]);
				}
				// category scheme
				else if (lightXmlTypeLocalname.equals("CategoryScheme")) {
					contentList = new CategoryDao().getLightXmlObject(
							lightXmlObjectType).toArray();
				}
				// concept scheme
				else if (lightXmlTypeLocalname.equals("ConceptScheme")) {
					contentList = new ConceptDao().getLightXmlObject(
							lightXmlObjectType).toArray();
					// question scheme
				} else if (lightXmlTypeLocalname.equals("QuestionScheme")) {
					List<LightXmlObjectType> list = new MultipleQuestionItemDao()
							.getLightXmlObject(lightXmlObjectType);
					QuestionItemDao questionItemDao = new QuestionItemDao();
					questionItemDao
							.setParentElementType(ElementType.QUESTION_SCHEME);
					list.addAll(questionItemDao
							.getLightXmlObject(lightXmlObjectType));
					contentList = list.toArray();
					// multiple question item
				} else if (lightXmlTypeLocalname.equals("MultipleQuestionItem")) {
					QuestionItemDao questionItemDao = new QuestionItemDao();
					questionItemDao
							.setParentElementType(ElementType.MULTIPLE_QUESTION_ITEM);
					contentList = questionItemDao.getLightXmlObject(
							lightXmlObjectType).toArray();
					// control construct scheme
				} else if (lightXmlTypeLocalname
						.equals("ControlConstructScheme")) {
					contentList = DdiManager
							.getInstance()
							.getQuestionConstructsLight(null, null,
									lightXmlObjectType.getId(),
									lightXmlObjectType.getVersion())
							.getLightXmlObjectList().getLightXmlObjectList()
							.toArray();
				}
				// universe scheme
				else if (lightXmlTypeLocalname.equals("UniverseScheme")) {
					contentList = DdiManager
							.getInstance()
							.getUniversesLight(null, null,
									lightXmlObjectType.getId(),
									lightXmlObjectType.getVersion())
							.getLightXmlObjectList().getLightXmlObjectList()
							.toArray();
				}
				// variable scheme
				else if (lightXmlTypeLocalname.equals("VariableScheme")) {
					contentList = new VariableDao()
							.getLightXmlObjectPlus(null, null,
									lightXmlObjectType.getId(),
									lightXmlObjectType.getVersion())
							.toArray();
				}
				// guard
				if (contentList == null) {
					DDIFtpException e = new DDIFtpException(
							"Not supported type: " + lightXmlTypeLocalname,
							new Throwable());
					DialogUtil
							.errorDialog(site.getShell(), ID,
									Messages.getString("ErrorTitle"),
									e.getMessage(), e);
				}
				// cache for getparent

				return contentList;
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

	Map<Object, Object> childrenParentMap = new HashMap<Object, Object>();

	public void clearChildrenParentMap() {
		childrenParentMap.clear();
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

	private void addToParentCache(Object parentElement, Object[] objs) {
		for (Object object : objs) {
			Object o = childrenParentMap.put(object.toString(), parentElement);
			if (o == null) {
				System.out.println(object);
			}
		}
	}

	@Override
	public Object getParent(Object element) {
		// Implement by: In each case of getChildren cache the result and the
		// parent via:
		// addToParentCache(parentElement, objs);

		// childrenParentMap.get(element.toString());
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
			if (localName.equals("MultipleQuestionItem")) {
				if (getChildren(element).length > 0) {
					result = true;
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

		else if (element instanceof ConceptualType) {
			return true;
		}

		else if (element instanceof ConceptualElement) {
			return true;
		}

		else if (element instanceof DDIResourceTypeImpl) {
			return true;
		}

		// guard
		// TODO - subject to change
		log.debug("***** TreeContentProvider.hasChildren() - save guard activated! *****");
		return getChildren(element).length > 0;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		log.debug("TreeContentProvider.propertyChange()");
	}
}
