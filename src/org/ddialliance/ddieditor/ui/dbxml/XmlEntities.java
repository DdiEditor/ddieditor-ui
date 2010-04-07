package org.ddialliance.ddieditor.ui.dbxml;

/**
 * XML Entities (DBXML).
 * 
 */
/*
 * $Author$ 
 * $Date$ 
 * $Revision$
 */

import java.io.File;
import java.util.Properties;

import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;

public class XmlEntities {
	protected static String xml_export_filename = "large-doc-out.xml";
	
	protected static void dumpXml (Log log) throws DDIFtpException {
		if (log.isDebugEnabled() && xml_export_filename.length() > 0) {
			File outFile = new File("resources" + File.separator + xml_export_filename);
			PersistenceManager.getInstance().exportResoure(DbXml.FULLY_DECLARED_NS_DOC, outFile);
		}
	}
}
