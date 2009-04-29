package org.ddialliance.ddieditor.ui.editor;

import java.util.Properties;
import java.util.Random;

import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class EditorInput implements IEditorInput {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, EditorInput.class);

	private final String id;
	private final String version;
	private final String parentId;
	private final String parentVersion;
	private View parentView;
	private Properties properties;
	public static enum EDITOR_TYPE {QUESTION_SCHEME, QUESTION_ITEM};
	public static enum EDITOR_MODE_TYPE {NEW, EDIT, VIEW};
	public EDITOR_MODE_TYPE mode;
	
	private String genID(String prefix, String agency) {
		// TODO Generate ID according to standard
		return prefix+"-"+agency+"-"+ Math.abs(new Random().nextInt()) % 1000000000;
	}

	public EditorInput(String id, String version, String parentId, String parentVersion, EDITOR_TYPE type,
			EDITOR_MODE_TYPE mode, View parentView, Properties properties) {
		log.debug("");
		if (mode.equals(EDITOR_MODE_TYPE.NEW)) {
			// Get agency from property file
			// TODO - at startup time!!!
			String prefix;
			if (type.equals(EDITOR_TYPE.QUESTION_SCHEME)) {
				prefix = "qs";
			} else {
				prefix = "qi";
			}
			id = genID(prefix, properties.getProperty("agency", "unknown"));
		}
		this.id = id;
		this.version = version;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.mode = mode;
		this.parentView = parentView;
		this.properties = properties;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	final public String getId() {
		return id;
	}

	final public String getVersion() {
		return version;
	}

	final public String getParentId() {
		return parentId;
	}

	final public String getParentVersion() {
		return parentVersion;
	}

	final public void setEditorMode(EDITOR_MODE_TYPE mode) {
		this.mode = mode;
	}

	final public EDITOR_MODE_TYPE getEditorMode() {
		return mode;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "DDI Editor";
	}

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

	public String getName() {
		return "DDI Editor";
	}
	
	public String getUser() {
		if (mode.equals(EDITOR_MODE_TYPE.NEW)) {
			return System.getProperty("user.name");			
		}
		// TODO Get User identification from XML
		return "Dummy";
	}
	
	public View getParentView() {
		return parentView;
	}

	public Properties getProperties() {
		return properties;
	}
}
