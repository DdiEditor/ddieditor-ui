package org.ddialliance.ddieditor.ui.dbxml;

import java.io.File;

import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.persistenceaccess.dbxml.DbXmlManager;
import org.ddialliance.ddieditor.persistenceaccess.filesystem.FilesystemManager;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;

public class DbXml {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, DbXml.class);

	public static final String FULLY_DECLARED_NS_DOC = "large-doc.xml";
	
	
	public static void open() throws Exception {
		log.debug("DbXml.open()");
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

			PersistenceManager.getInstance();
			DdiManager.getInstance();

			// add resources
			try {
				FilesystemManager.getInstance().addResource(new File("resources" + File.separator + FULLY_DECLARED_NS_DOC));				
			} catch (Exception e) {
				// TODO: handle exception
			}
			PersistenceManager.getInstance().setWorkingResource(FULLY_DECLARED_NS_DOC);

			// no commit
			PersistenceManager.getInstance().exportResourceList(new File(PersistenceManager.RESOURCE_LIST_FILE));
			
		} catch (Exception e) {
			throw e;
		}
		log.debug("DbXml.open(Done)");
	}

	public static void open(String fileName) throws Exception {
		log.debug("DbXml.open(" + fileName + ")");
		try {
//			// Clean up DbXml
//			File[] files = new File(DbXmlManager.ENVIROMENT_HOME).listFiles();
//			for (int i = 0; i < files.length; i++) {
//				if (files[i].getName().contains("__")) {
//					log.debug("Deleting: " + files[i].getAbsolutePath());
//					files[i].delete();
//				}
//				if (files[i].getName().contains("log.")) {
//					log.debug("Deleting: " + files[i].getAbsolutePath());
//					files[i].delete();
//				}
//				if (files[i].getName().contains("dbxml")) {
//					log.debug("Deleting: " + files[i].getAbsolutePath());
//					files[i].delete();
//				}
//			}
			
			PersistenceManager.getInstance();
			DdiManager.getInstance();

			// add resources
			File file = new File(fileName);
			FilesystemManager.getInstance().addResource(file);
			PersistenceManager.getInstance().setWorkingResource(file.getName());

			// commit
			PersistenceManager.getInstance().exportResourceList(new File(PersistenceManager.RESOURCE_LIST_FILE));
			
		} catch (Exception e) {
			throw e;
		}
		log.debug("DbXml.open("+fileName+") - Done");
	}
	
	@Override
	public void finalize() throws Exception {
		log.debug("Shutdown ...");
		
		PersistenceManager.getInstance().close();
	}
}
