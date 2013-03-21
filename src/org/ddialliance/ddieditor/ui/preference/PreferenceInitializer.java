package org.ddialliance.ddieditor.ui.preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.ddialliance.ddieditor.model.resource.DDIResourceType;
import org.ddialliance.ddieditor.model.resource.StorageType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.command.CommandHelper;
import org.ddialliance.ddieditor.ui.editor.Editor;
import org.ddialliance.ddieditor.ui.view.ViewManager;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddieditor.util.DdiEditorRefUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Initialise default preference values and handle advanced changes
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {
	private Log log = LogFactory.getLog(LogType.EXCEPTION,
			PreferenceInitializer.class);
	final Properties uiProp = new Properties();
	File uiPropFile = new File("resources/ddieditor-ui.properties");

	@Override
	public void initializeDefaultPreferences() {
		// load ui properties
		try {
			uiProp.load(new FileInputStream(uiPropFile));
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}

		final IPreferenceStore store = Activator.getDefault()
				.getPreferenceStore();

		// ddi
		store.setDefault(DdiEditorConfig.DDI_AGENCY,
				DdiEditorConfig.get(DdiEditorConfig.DDI_AGENCY));
		store.setDefault(DdiEditorConfig.DDI_AGENCY_NAME,
				DdiEditorConfig.get(DdiEditorConfig.DDI_AGENCY_NAME));
		store.setDefault(DdiEditorConfig.DDI_AGENCY_DESCRIPTION,
				DdiEditorConfig.get(DdiEditorConfig.DDI_AGENCY_DESCRIPTION));
		store.setDefault(DdiEditorConfig.DDI_AGENCY_IDENTIFIER,
				DdiEditorConfig.get(DdiEditorConfig.DDI_AGENCY_IDENTIFIER));
		store.setDefault(DdiEditorConfig.DDI_AGENCY_HP,
				DdiEditorConfig.get(DdiEditorConfig.DDI_AGENCY_HP));

		store.setDefault(DdiEditorConfig.DDI_LANGUAGE,
				DdiEditorConfig.get(DdiEditorConfig.DDI_LANGUAGE));
		store.setDefault(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG,
				DdiEditorConfig
						.get(DdiEditorConfig.DDI_INSTRUMENT_PROGRAM_LANG));

		// backend
		store.setDefault(DdiEditorConfig.DBXML_ENVIROMENT_HOME,
				DdiEditorConfig.get(DdiEditorConfig.DBXML_ENVIROMENT_HOME));

		// ddieditor-ui
		store.setDefault(PreferenceConstants.DDIEDITORUI_LANGUAGE,
				uiProp.getProperty(PreferenceConstants.DDIEDITORUI_LANGUAGE));
		store.setDefault(PreferenceConstants.FONT_SIZE_TABLE_SIZE,
				uiProp.getProperty(PreferenceConstants.FONT_SIZE_TABLE_SIZE));

		// auto change perspective
		store.setDefault(PreferenceConstants.AUTO_CHANGE_PERSPECTIVE,
				uiProp.getProperty(PreferenceConstants.AUTO_CHANGE_PERSPECTIVE));

		// confirm DDI Editor exit
		store.setDefault(PreferenceConstants.CONFIRM_DDIEDITOR_EXIT,
				uiProp.getProperty(PreferenceConstants.CONFIRM_DDIEDITOR_EXIT));

		// spss constance
		store.setDefault(DdiEditorConfig.SPSS_IMPORT_CHARSET,
				DdiEditorConfig.get(DdiEditorConfig.SPSS_IMPORT_CHARSET));
		store.setDefault(DdiEditorConfig.SPSS_IMPORT_DECIMAL_SEPERATOR,
				DdiEditorConfig
						.get(DdiEditorConfig.SPSS_IMPORT_DECIMAL_SEPERATOR));

		// apply changes
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						Editor ed = new Editor();
						boolean check = true;

						// check if files are present in dbxml env home
						try {
							PersistenceManager.getInstance().getResourceList(); // guard
						} catch (Exception e) {
							check = false;
						}

						// apply new dbxml env home - 1 - close editors
						if (event.getProperty().equals(
								DdiEditorConfig.DBXML_ENVIROMENT_HOME)) {
							if (check) {
								try {
									List<DDIResourceType> resources = PersistenceManager
											.getInstance().getResources();
									CommandHelper.closeEditors(resources);
								} catch (Exception e) {
									ed.showError(e);
								}
							}
						}

						// store ddiftp properties to file
						if (DdiEditorConfig.get(event.getProperty()) != null
								|| !DdiEditorConfig.get(event.getProperty())
										.equals("")) {
							DdiEditorConfig.set(event.getProperty(),
									store.getString(event.getProperty()));
							try {
								DdiEditorConfig.save();
							} catch (DDIFtpException e) {
								ed.showError(e);
							}
						}

						// store ddieditor-ui properties to file
						if (uiProp.containsKey(event.getProperty())) {
							uiProp.setProperty(event.getProperty(),
									store.getString(event.getProperty()));
							try {
								uiProp.store(new FileOutputStream(uiPropFile),
										"");
							} catch (FileNotFoundException e) {
								ed.showError(e);
							} catch (IOException e) {
								ed.showError(e);
							}
						}

						// apply new dbxml env home - 2 - reset persist stores
						if (event.getProperty().equals(
								DdiEditorConfig.DBXML_ENVIROMENT_HOME)) {

							// reset persistence stores
							if (check) {
								try {
									for (StorageType storage : PersistenceManager
											.getInstance().getResourceList()
											.getResourceList().getStorageList()) {
										Object obj = DdiEditorRefUtil
												.invokeStaticMethod(
														storage.getManager(),
														"getInstance", null);
										DdiEditorRefUtil.invokeMethod(obj,
												"reset", false, null);
									}
								} catch (Exception e) {
									ed.showError(e);
								}
							}

							// reset persistence manager
							try {
								PersistenceManager.getInstance().reset();
							} catch (Exception e) {
								ed.showError(e);
							}

							// refresh views
							ViewManager.getInstance().addAllViewsToRefresh();
							ViewManager.getInstance().refesh();
						}
					}
				});

	}
}
