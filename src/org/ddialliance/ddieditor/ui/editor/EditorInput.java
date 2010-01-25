package org.ddialliance.ddieditor.ui.editor;

/**
 * Generic Editor Input.
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class EditorInput implements IEditorInput {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			EditorInput.class);

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
	 * @param id of DDI identifiable
	 * @param version of DDI versionable
	 * @param parentId of parent DDI identifiable
	 * @param parentVersion of parent DDI versionable
	 * @param elementType @see {@link org.ddialliance.ddieditor.ui.model.ElementType} 
	 * @param mode @see {@link EditorModeType}
	 */
	public EditorInput(String id, String version, String parentId,
			String parentVersion, ElementType elementType, EditorModeType mode) {

		if (mode.equals(EditorModeType.NEW)) {
			// id and version generation handled by model
			
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
		if (obj instanceof EditorInput) {
			return id.equals(((EditorInput) obj).getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String getName() {
		return elementType.getTranslatedDisplayMessageEntry();
	}
}
