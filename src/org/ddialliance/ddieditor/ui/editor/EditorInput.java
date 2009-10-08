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

import java.util.Properties;
import java.util.Random;

import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.preference.PreferenceConstants;
import org.ddialliance.ddieditor.ui.view.View;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class EditorInput implements IEditorInput {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			EditorInput.class);

	private final String id;
	private final String version;
	private final String parentId;
	private final String parentVersion;
	private View parentView;
	private Properties properties;

	public static enum EditorModeType {
		NEW, EDIT, VIEW
	};

	public EditorModeType mode;

	private String genID(String prefix, String agency) {
		// TODO Generate ID according to standard
		return prefix + "-" + agency + "-" + Math.abs(new Random().nextInt())
				% 1000000000;
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param version
	 * @param parentId
	 * @param parentVersion
	 * @param type
	 * @param mode
	 * @param parentView
	 * @param properties
	 */
	public EditorInput(String id, String version, String parentId,
			String parentVersion, ElementType type, EditorModeType mode,
			View parentView, Properties properties) {

		// TODO ID generation
		if (mode.equals(EditorModeType.NEW)) {
			IPreferenceStore store = Activator.getDefault()
					.getPreferenceStore();
			id = genID(type.getIdPrefix(), store
					.getString(PreferenceConstants.DDI_AGENCY));
		}
		this.id = id;
		this.version = version;
		this.parentId = parentId;
		this.parentVersion = parentVersion;
		this.mode = mode;
		this.parentView = parentView;
		this.properties = properties;
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

	final public void setEditorMode(EditorModeType mode) {
		this.mode = mode;
	}

	final public EditorModeType getEditorMode() {
		return mode;
	}

	public String getUser() {
		if (mode.equals(EditorModeType.NEW)) {
			return System.getProperty("user.name");
		}
		// TODO Get User identification from XML
		return "Dummy";
	}

	public Properties getProperties() {
		return properties;
	}

	public View getParentView() {
		return parentView;
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
		return "DDI Editor";
	}
}
