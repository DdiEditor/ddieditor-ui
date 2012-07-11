package org.ddialliance.ddieditor.ui.view;

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
import org.ddialliance.ddiftp.util.Translator;
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

	public static final String ID = "org.ddialliance.ddieditor.ui.view.TreeContentProvider";
	private IViewSite site;

	private String currentDdiResource = "";
	private ViewContentType contentType = null;
	private Map<String, List<ConceptualElement>> conceptualElementCache = new HashMap<String, List<ConceptualElement>>();
	private Map<String, String> parentElemenIdToResourceId = new HashMap<String, String>();

	public TreeContentProvider(IViewSite site) {
		this.site = site;
	}

	private List<ConceptualElement> getConceptualList(
			DDIResourceType ddiResource) throws Exception {
		// TODO Implement sync. between views.

		// Reset cache due to missing sync. between views
		// But cache used on this.getChildren(ConceptualType)
		List<ConceptualElement> result = null; // conceptualElementCache.get(ddiResource.getOrgName());
		if (result == null) {
			try {
				result = DdiManager.getInstance().getConceptualOverview();
				conceptualElementCache.put(ddiResource.getOrgName(), result);
			} catch (Exception e) {
				displayGetChildrenException(e);
			}
		}
		return result;
	}

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
			DialogUtil.errorDialog(site.getShell(), ID, null,
					"Error while opening meta data container", e);
		}
		return null;
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

				Object[] result = null;

				// info view
				if (contentType.equals(ViewContentType.StudyLevelContent)) {
					List<ConceptualElement> conceptualList = getConceptualList((DDIResourceType) parentElement);
					List<ConceptualType> list = new ArrayList<ConceptualType>();
					for (ConceptualElement conceptualElement : conceptualList) {
						if (!list.contains(conceptualElement.getType())) {
							list.add(conceptualElement.getType());
						}
					}
					result = list.toArray();
				}

				// concept
				if (contentType.equals(ViewContentType.ConceptContent)) {
					result = new ConceptSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				}

				// category
				else if (contentType.equals(ViewContentType.CategoryContent)) {
					result = new CategorySchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				}

				// code
				else if (contentType.equals(ViewContentType.CodeContent)) {
					result = CodeSchemeDao.getCodeSchemesLight(null, null)
							.toArray();
				}

				// question
				else if (contentType.equals(ViewContentType.QuestionContent)) {
					result = new QuestionSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				}

				// instrument
				else if (contentType
						.equals(ViewContentType.InstrumentationContent)) {
					LightXmlObjectListDocument instListDoc = DdiManager
							.getInstance().getInstrumentsLight(null, null,
									null, null);

					LightXmlObjectListDocument listDoc = DdiManager
							.getInstance().getControlConstructSchemesLight(
									null, null, null, null);

					// result
					result = new Object[listDoc.getLightXmlObjectList()
							.getLightXmlObjectList().size()
							+ instListDoc.getLightXmlObjectList()
									.getLightXmlObjectList().size()];
					int count = 0;

					// instruments
					for (LightXmlObjectType lightXmlObject : instListDoc
							.getLightXmlObjectList().getLightXmlObjectList()) {
						MaintainableLightLabelQueryResult mLightLabelQueryResult = new MaintainableLightLabelQueryResult(
								lightXmlObject.getElement(),
								lightXmlObject.getId(),
								lightXmlObject.getVersion(),
								lightXmlObject.getAgency(),
								lightXmlObject.getParentId(),
								lightXmlObject.getParentVersion());
						mLightLabelQueryResult.setLabelList(lightXmlObject.getLabelList());
						result[count] = mLightLabelQueryResult;
						count++;
					}

					// control construct schemes
					for (LightXmlObjectType lightXmlObject : listDoc
							.getLightXmlObjectList().getLightXmlObjectList()) {
						result[count] = 
								DdiManager.getInstance()
								.getInstrumentLabel(lightXmlObject.getId(),
										lightXmlObject.getVersion(),
										lightXmlObject.getParentId(),
										lightXmlObject.getParentVersion());
						count++;
					}
				}

				// universe
				else if (contentType.equals(ViewContentType.UniverseContent)) {
					result = new UniverseSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				}

				// variable
				else if (contentType.equals(ViewContentType.VariableContent)) {
					result = new VariableSchemeDao().getLightXmlObject(null,
							null, null, null).toArray();
				}

				// log guard and parent element id to resource id caching
				if (result == null) { // log guard only
					new DDIFtpException(
							"Not supported, contenttype: " + contentType
									+ ", parentElement: " + parentElement,
							new Throwable());
				} else if (result.length > 0
						&& result[0] instanceof LightXmlObjectType) {
					for (int i = 0; i < result.length; i++) {
						parentElemenIdToResourceId.put(
								((LightXmlObjectType) result[i]).getId(),
								currentDdiResource);
					}
				}

				return result;
			} catch (Exception e) {
				DialogUtil.errorDialog(site.getShell(), ID,
						Translator.trans("ErrorTitle"),
						Translator.trans("View.mess.GetElementError"), e);
			}
		}

		// conceptual type
		else if (parentElement instanceof ConceptualType) {
			try {
				List<ConceptualElement> list = new ArrayList<ConceptualElement>();
				for (ConceptualElement conceptualElement : conceptualElementCache
						.get(((ConceptualType) parentElement).getResourceId())) {
					if (conceptualElement.getType().equals(parentElement)) {
						list.add(conceptualElement);
					}
				}
				return list.toArray();
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

			// skip leafs as hasChild() does not filter those when view filter
			// used
			if (lightXmlTypeLocalname.indexOf("Scheme") < 0
					&& !lightXmlTypeLocalname.equals("MultipleQuestionItem")) {
				return contentList;
			}

			try {
				// change to parent resource via cache
				String cacheResource = parentElemenIdToResourceId
						.get(lightXmlObjectType.getId());
				if (site.getId().equals(InfoView.ID)
						|| (cacheResource == null && lightXmlObjectType
								.getElement().equals(
										ElementType.MULTIPLE_QUESTION_ITEM
												.getElementName()))) {
					// multiple question item guard
					// do nothing
				} else if (!cacheResource.equals(currentDdiResource)) {
					PersistenceManager.getInstance().setWorkingResource(
							cacheResource);
					currentDdiResource = cacheResource;
				}

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
				}

				// question scheme
				else if (lightXmlTypeLocalname.equals("QuestionScheme")) {
					List<LightXmlObjectType> list = new MultipleQuestionItemDao()
							.getLightXmlObject(lightXmlObjectType);

					QuestionItemDao questionItemDao = new QuestionItemDao();
					questionItemDao
							.setParentElementType(ElementType.QUESTION_SCHEME);
					list.addAll(questionItemDao
							.getLightXmlObject(lightXmlObjectType));

					contentList = list.toArray();
				}

				// multiple question item
				else if (lightXmlTypeLocalname.equals("MultipleQuestionItem")) {
					QuestionItemDao questionItemDao = new QuestionItemDao();
					questionItemDao
							.setParentElementType(ElementType.MULTIPLE_QUESTION_ITEM);
					contentList = questionItemDao.getLightXmlObject(
							lightXmlObjectType).toArray();
				}

				// control construct scheme
				else if (lightXmlTypeLocalname.equals("ControlConstructScheme")) {
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
					contentList = new VariableDao().getLightXmlObjectPlus(null,
							null, lightXmlObjectType.getId(),
							lightXmlObjectType.getVersion()).toArray();
				}

				// guard
				if (contentList == null) {
					throw new DDIFtpException("Not supported type: "
							+ lightXmlTypeLocalname, new Throwable());
				}

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

	@Override
	public boolean hasChildren(Object element) {
		// light xml object
		if (element instanceof LightXmlObjectType) {
			String localName = ((LightXmlObjectType) element).getElement();
			boolean result = false;

			// scheme
			if (localName.indexOf("Scheme") > -1) {
				result = true;

				if (site.getId().equals(InfoView.ID)) {
					result = false;
				}
				if (localName.indexOf("CodeScheme") > -1) {
					result = false;
				}
			}

			// multiple question item
			if (localName.equals("MultipleQuestionItem")) {
				if (getChildren(element).length > 0) {
					result = true;
				}
			}
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
		return getChildren(element).length > 0;
	}

	private void displayGetChildrenException(Exception e) {
		String errMess = Translator.trans("View.mess.GetChildError"); //$NON-NLS-1$
		// log exception
		if (!(e instanceof DDIFtpException)) {
			Log elog = LogFactory.getLog(LogType.EXCEPTION,
					TreeContentProvider.class);
			elog.error(errMess, e);
		}
		ErrorDialog.openError(site.getShell(), Translator.trans("ErrorTitle"),
				null, new Status(IStatus.ERROR, ID, 0, errMess, e));
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}
}
