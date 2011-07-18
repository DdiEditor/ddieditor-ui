package org.ddialliance.ddieditor.ui.editor;

import java.util.List;

import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Specific editor input for DDI content
 * 
 * <p>
 * The input is light weight and does not hold the model to drive the editor.
 * The editor input holds identification of the model.<br>
 * <br>
 * 
 * IMPORTENT it is to UPDATE the opened Editor.editorInput when model
 * identification changes: id, version, parentId, parentVersion<br>
 * <br>
 * 
 * Update is performed via Editor.setInput
 * 
 * @see org.eclipse.ui.IEditorInput
 * @see org.eclipse.ui.part.EditorPart#setInput
 * @see org.ddialliance.ddieditor.ui.model.IModel
 * @see org.ddialliance.ddieditor.ui.editor.Editor
 */
public class EditorInput implements IEditorInput {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			EditorInput.class);

	private String id;
	private String version;
	private String parentId;
	private String parentVersion;
	private ElementType elementType;
	private ElementType parentEntityType;
	private String resourceId;
	private List<LabelType> labelList;

	/**
	 * State of editor based on persistence and access
	 * <ul>
	 * <li>NEW - new element not created before</li>
	 * <li>EDIT - editable element retrieved from persistence store</li>
	 * <li>NEW - viewable element retrieved from persistence store</li>
	 * </ul>
	 */
	public static enum EditorModeType {
		NEW, EDIT, VIEW
	};

	public EditorModeType mode;

	/**
	 * Constructor
	 * 
	 * @param labelList
	 *            list of labels
	 * @param resourceId
	 *            id of resource of the input
	 * @param id
	 *            of DDI identifiable
	 * @param version
	 *            of DDI versionable
	 * @param parentId
	 *            of parent DDI identifiable
	 * @param parentVersion
	 *            of parent DDI versionable
	 * @param elementType
	 * @see {@link org.ddialliance.ddieditor.ui.model.ElementType}
	 * @param mode
	 * @see {@link EditorModeType}
	 */
	public EditorInput(List<LabelType> labelList, String resourceId, String id,
			String version, String parentId, String parentVersion,
			ElementType elementType, ElementType parentEntityType,
			EditorModeType mode) {
		this.resourceId = resourceId;
		this.labelList = labelList;
		if (mode.equals(EditorModeType.NEW)) {
			// id and version generation handled by model
			this.id = null;
			// clean version
			this.version = null;
		} else {
			this.id = id;
			this.version = version;
		}
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.mode = mode;
		this.elementType = elementType;
		this.parentEntityType = parentEntityType;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String getParentId() {
		return parentId;
	}

	public String getParentVersion() {
		return parentVersion;
	}

	public void setEditorMode(EditorModeType mode) {
		this.mode = mode;
	}

	public EditorModeType getEditorMode() {
		return mode;
	}

	public String getUser() {
		if (mode.equals(EditorModeType.NEW)) {
			return System.getProperty("user.name");
		}
		// TODO Get User identification from XML
		return "Dummy";
	}

	public ElementType getElementType() {
		return elementType;
	}

	public EditorModeType getMode() {
		return mode;
	}

	public void setMode(EditorModeType mode) {
		this.mode = mode;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected void setVersion(String version) {
		this.version = version;
	}

	protected void setParentId(String parentId) {
		this.parentId = parentId;
	}

	protected void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}

	protected void setParentElementType(ElementType parentElementType) {
		this.parentEntityType = parentElementType;
	}

	public ElementType getParentElementType() {
		return this.parentEntityType;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		String toolTip = "";
		if (getElementType()!= null) {
			toolTip = Messages.getString(getElementType()
					.getDisplayMessageEntry());
		}
		return toolTip;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}

		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		EditorInput test = (EditorInput) obj;
		boolean result = (id == test.id || (id != null && id.equals(test.id)))
				&& (version == test.version || (version != null && version
						.equals(test.version)))
				&& (parentId == test.parentId || (parentId != null && parentId
						.equals(test.parentId)))
				&& (parentVersion == test.parentVersion || (parentVersion != null && parentVersion
						.equals(test.parentVersion)))
				&& (elementType == test.elementType || (elementType != null && elementType
						.equals(test.elementType)));
		return result;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode() + version.hashCode() + parentId.hashCode()
				+ parentVersion.hashCode() + elementType.hashCode();
		return result;
	}

	@Override
	public String getName() {
		String name = elementType.getTranslatedDisplayMessageEntry();
		if (name == null) {
			new DDIFtpException("Name null, check up on: "
					+ elementType.getTranslatedDisplayMessageEntry());
		}
		return name;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public List<LabelType> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<LabelType> labelList) {
		this.labelList = labelList;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("ResourceId: ");
		result.append(resourceId);
		result.append(", id: ");
		result.append(id);
		result.append(", version: ");
		result.append(version);
		result.append(", parentId: ");
		result.append(parentId);
		result.append(", parentVersion: ");
		result.append(parentVersion);
		result.append(", elementType: ");
		result.append(elementType.getElementName());
		return result.toString();
	}
}
