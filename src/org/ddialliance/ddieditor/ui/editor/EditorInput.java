package org.ddialliance.ddieditor.ui.editor;

import org.ddialliance.ddieditor.ui.model.ElementType;
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
	private static Log log = LogFactory.getLog(LogType.SYSTEM, EditorInput.class);

	private String id;
	private String version;
	private String parentId;
	private String parentVersion;
	private ElementType elementType;

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
	public EditorInput(String id, String version, String parentId, String parentVersion, ElementType elementType,
			EditorModeType mode) {

		if (mode.equals(EditorModeType.NEW)) {
			// id and version generation handled by model
			this.id = "";
			// clean version
			this.version = "";
		} else {
			this.id = id;
			this.version = version;
		}
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.mode = mode;
		this.elementType = elementType;
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
		return "DDI Editor";
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
				&& (version == test.version || (version != null && version.equals(test.version)))
				&& (parentId == test.parentId || (parentId != null && parentId.equals(test.parentId)))
				&& (parentVersion == test.parentVersion || (parentVersion != null && parentVersion
						.equals(test.parentVersion)))
				&& (elementType == test.elementType || (elementType != null && elementType.equals(test.elementType)));
		if (log.isDebugEnabled()) {
			log.debug("This id: " + id + ", obj id: " + ((EditorInput) obj).id + ", result: " + result);
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode() + version.hashCode() + parentId.hashCode() + parentVersion.hashCode()
				+ elementType.hashCode();
		return result;
	}

	@Override
	public String getName() {
		return elementType.getTranslatedDisplayMessageEntry();
	}
}
