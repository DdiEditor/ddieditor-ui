package org.ddialliance.ddieditor.ui.editor.widgetutil;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * RCP transfer class for light XML objects
 */
public class LightXmlObjectTransfer extends ByteArrayTransfer {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			LightXmlObjectTransfer.class);
	private static final String LIGHTXMLOBJECT = "LIGHTXMLOBJECT"; //$NON-NLS-1$
	private static final int LIGHTXMLOBJECT_ID = registerType(LIGHTXMLOBJECT);
	private static LightXmlObjectTransfer _instance = new LightXmlObjectTransfer();

	private LightXmlObjectTransfer() {
	}

	/**
	 * Returns the singleton instance
	 * 
	 * @return the singleton instance
	 */
	public static LightXmlObjectTransfer getInstance() {
		return _instance;
	}

	@Override
	protected int[] getTypeIds() {
		int[] types = { LIGHTXMLOBJECT_ID };
		// if (log.isDebugEnabled()) {
		// log.debug("Types: " + types[0]);
		// }
		return types;
	}

	@Override
	protected String[] getTypeNames() {
		String[] typeNames = { LIGHTXMLOBJECT };
		// if (log.isDebugEnabled()) {
		// log.debug("TypeNames: " + typeNames);
		// }
		return typeNames;
	}

	@Override
	public TransferData[] getSupportedTypes() {
		int[] types = getTypeIds();
		TransferData[] data = new TransferData[types.length];
		for (int i = 0; i < types.length; i++) {
			data[i] = new TransferData();
			data[i].type = types[i];
			// if (log.isDebugEnabled()) {
			// log.debug("Added: " + data[i].type);
			// }
		}
		return data;
	}

	@Override
	public boolean isSupportedType(TransferData transferData) {
		if (transferData == null) {
			if (log.isWarnEnabled()) {
				log.warn("TransferData is null!");
			}
			return false;
		}
		int[] types = getTypeIds();
		for (int i = 0; i < types.length; i++) {
			// if (log.isDebugEnabled()) {
			// log.debug("TransferData: " + transferData.type + " ~ types["
			// + i + "]: " + types[i]);
			// }
			if (transferData.type == types[i]) {
				// if (log.isDebugEnabled()) {
				// log.debug("true");
				// }
				return true;
			}
		}
		if (log.isWarnEnabled()) {
			log.warn("TransferData:" + transferData + " is not supported!");
		}
		return false;
	}

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof LightXmlObjectType)) {
			if (log.isDebugEnabled()) {
				log.debug(object.getClass().getName());
			}
			return;
		}

		if (isSupportedType(transferData)) {
			byte[] buffer = ((LightXmlObjectType) object).xmlText().getBytes();
			super.javaToNative(buffer, transferData);
			if (log.isDebugEnabled()) {
				log.debug("JavaToNative performed ;- )");
			}
		}
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null) {
				if (log.isWarnEnabled()) {
					log.warn("Buffer is null :- (");
				}
				return null;
			}

			LightXmlObjectType lightXmlObject = null;
			// String xml = new String(buffer);
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				lightXmlObject = LightXmlObjectType.Factory.parse(in);
				in.close();
			} catch (IOException e) {
				new DDIFtpException(e);
				return null;
			} catch (XmlException e) {
				new DDIFtpException(e);
				return null;
			}
			if (log.isDebugEnabled()) {
				log.debug("NativeToJava performed: " + lightXmlObject);
			}
			return lightXmlObject;
		}
		return null;
	}
}
