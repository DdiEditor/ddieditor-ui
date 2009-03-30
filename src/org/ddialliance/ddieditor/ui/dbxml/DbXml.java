package org.ddialliance.ddieditor.ui.dbxml;

import java.io.File;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.dbxml.DbXmlManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddieditor.ui.editor.EditorInput;
import org.ddialliance.ddieditor.ui.model.QuestionItem;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class DbXml {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, DbXml.class);

	public static final String FULLY_DECLARED_NS_DOC = "large-doc.xml";

	// Perform setup normally done by the DDI Document Manager
	public static void open() throws Exception {
		log.info("DbXml.open()");

		DdiManager.getInstance();

		try {
			// Clean up DbXml
			File[] files = new File(DbXmlManager.ENVIROMENT_HOME).listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().contains("__")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
				if (files[i].getName().contains("log.")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
				if (files[i].getName().contains("dbxml")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
			}

			// add resources
			FilesystemManager.getInstance().addResource(new File("resources" + File.separator + FULLY_DECLARED_NS_DOC));
			PersistenceManager.getInstance().setWorkingResource(FULLY_DECLARED_NS_DOC);

			// no commit
			PersistenceManager.getInstance().rebuildResources();
			PersistenceManager.getInstance().exportResourceList(new File(PersistenceManager.RESOURCE_LIST_FILE));
			PersistenceManager.getInstance().commitWorkingResource();
		} catch (Exception e) {
			throw e;
		}
		log.info("DbXml.open(Done)");
	}

	public static void open(String fileName) throws Exception {
		log.info("DbXml.open(" + fileName + ")");

		DdiManager.getInstance();

		try {
			// Clean up DbXml
			File[] files = new File(DbXmlManager.ENVIROMENT_HOME).listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().contains("__")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
				if (files[i].getName().contains("log.")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
				if (files[i].getName().contains("dbxml")) {
					log.debug("Deleting: " + files[i].getAbsolutePath());
					files[i].delete();
				}
			}

			// add resources
			File file = new File(fileName);
			FilesystemManager.getInstance().addResource(file);
			PersistenceManager.getInstance().setWorkingResource(file.getName());

			// no commit
			PersistenceManager.getInstance().rebuildResources();
			PersistenceManager.getInstance().exportResourceList(new File(PersistenceManager.RESOURCE_LIST_FILE));
			PersistenceManager.getInstance().commitWorkingResource();
		} catch (Exception e) {
			throw e;
		}
		log.info("DbXml.open("+fileName+") - Done");
	}
	
	public void finalize() throws Exception {
		log.debug("Shutdown ...");
		PersistenceManager.getInstance().rollbackAllResources();
		PersistenceManager.getInstance().close();
	}

}
