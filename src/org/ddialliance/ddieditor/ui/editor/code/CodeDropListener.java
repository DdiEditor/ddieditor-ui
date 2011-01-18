package org.ddialliance.ddieditor.ui.editor.code;

import java.util.Arrays;
import java.util.Collections;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.CodeType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.impl.CodeTypeImpl;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor.CodeTableContentProvider;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransfer;
import org.ddialliance.ddieditor.ui.editor.widgetutil.lightxmlobjectdnd.LightXmlObjectTransferVO;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * JFace drop listener/ viewer drop adapter for:<br>
 * Category references transfered as light XML objects
 */
public class CodeDropListener extends ViewerDropAdapter {
	private static Log log = LogFactory.getLog(LogType.SYSTEM, CodeDropListener.class);

	CodeSchemeEditor codeSchemeEditor;
	CodeTableContentProvider stcp;

	public CodeDropListener(CodeSchemeEditor codeSchemeEditor) {
		super(codeSchemeEditor.getViewer());
		this.codeSchemeEditor = codeSchemeEditor;
		stcp = ((CodeSchemeEditor.CodeTableContentProvider) ((TableViewer) getViewer()).getContentProvider());
	}

	@Override
	public boolean performDrop(Object data) {
		if (data == null) { // guard
			new DDIFtpException("Data is null", new Throwable());
			return false;
		}
		
		// Use of transfer:
		// transfer contains LightXmlObjets
		// LightXmlObject@Id = Category Id.
		// LightXmlObject.label = Category label (if from view) or
		//                        code value (if from the editor i.e. move)
		LightXmlObjectTransferVO[] transfers = (LightXmlObjectTransferVO[]) data;
		if (transfers.length < 1) { // guard
			new DDIFtpException("No transfers :- (", new Throwable());
			return false;
		}

		// TODO Element type not defined for move - transfers[0].lightXmlObject.getElement() is null!
		if (transfers[0].lightXmlObject.getElement() != null &&
				!transfers[0].lightXmlObject.getElement().equals("Category")) {
			log.warn("Only Categories supported");
			return false;
		}
		Table table = (Table) ((TableViewer) getViewer()).getControl();

		// logic flow:
		// 1. determine insert position
		// 2. delete item - if source is the editor itself
		// 3. add item
		// 4. refresh table and table viewer

		// 1. determine insert position
		int relativePosition = -1;
		if (getCurrentLocation() == LOCATION_BEFORE) {
			relativePosition = 0;
		} else if (getCurrentLocation() == LOCATION_AFTER) {
			relativePosition = 1;
		} else if (getCurrentLocation() == LOCATION_ON) {
			relativePosition = 0;
		} else if (getCurrentLocation() == LOCATION_NONE) {
			return false;
		}
		// - selected code object
		String sourceId = transfers[0].lightXmlObject.getId();
		boolean sourceFound = false;
		Object selectedLightXmlObject = getCurrentTarget();
		int insertPosition = -2;
		for (int i = 0; i < table.getItems().length; i++) {
			// log.debug("items Data(" + i +
			// "):"+table.getItems()[i].getData());
			// log.debug("selectedLightXmlObject: " + selectedLightXmlObject);
			String currentId = XmlBeansUtil.getXmlAttributeValue((String) table.getItems()[i].getData().toString(),
					"id=\"");
			if (sourceId.equals(currentId)) {
				sourceFound = true;
			}
			if (table.getItems()[i].getData().equals(selectedLightXmlObject)) {
				insertPosition = i + relativePosition;
				// log.debug("relativePosition: " + relativePosition);
				// log.debug("insertPosition: " + insertPosition);
				break;
			}
		}
		if (sourceFound) {
			insertPosition--; // if source found before tager - decrement position
		}
		if (insertPosition < 0) {
			insertPosition = 0;
		}
		if (log.isDebugEnabled()) {
			log.debug("Final insert position: " + insertPosition);
		}

		// 2. delete item - if source is the editor itself
		if (transfers[0].rcpPartId.equals(codeSchemeEditor.ID)) {
			// delete rcp
			int[] indices = table.getSelectionIndices();
			table.remove(indices);
			table.update();

			// resort indices descending
			Integer[] reverseIndices = new Integer[indices.length];
			for (int i = 0; i < indices.length; i++) {
				reverseIndices[i] = indices[i];
			}
			Arrays.sort(reverseIndices, Collections.reverseOrder());
			log.debug("Reverse Indices: "+reverseIndices);

			// delete xml item
			for (int i = 0; i < reverseIndices.length; i++) {
				// delete editor items xml (light xml object)
				stcp.getItems().remove(reverseIndices[i].intValue());

				// delete model xml (reference xml)
				try {
					codeSchemeEditor.modelImpl.getDocument().getCodeScheme().getCodeList()
							.remove(reverseIndices[i].intValue());
				} catch (DDIFtpException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (log.isDebugEnabled()) {
				StringBuilder info = new StringBuilder();
				for (int i = 0; i < reverseIndices.length; i++) {
					info.append(reverseIndices[i]);
					info.append(", ");
				}
				log.debug("Deleted: " + info.toString());
			}
		}

		// 3. add item - Code based on transferred Category
		for (int i = 0; i < transfers.length; i++) {
			// add items ligth xml object
			LightXmlObjectType lightXmlObject = LightXmlObjectType.Factory.newInstance();
			String codeValue = "";
			if (transfers[0].rcpPartId.equals(codeSchemeEditor.ID)) {
				codeValue = XmlBeansUtil.getTextOnMixedElement(transfers[i].lightXmlObject.getLabelList().get(0));
			}
			if (!transfers[0].rcpPartId.equals(codeSchemeEditor.ID) &&
					getCurrentLocation() == LOCATION_ON) {
				codeValue = 
					XmlBeansUtil.getTextOnMixedElement(((LightXmlObjectType)stcp.getItems().get(insertPosition)).getLabelList().get(0));
			}
			XmlBeansUtil.setTextOnMixedElement(lightXmlObject.addNewLabel(), codeValue);
			lightXmlObject.setId(XmlBeansUtil.getXmlAttributeValue(transfers[i].lightXmlObject.xmlText(), "id=\""));

			// add table item
			TableItem item = new TableItem(table, SWT.NONE, insertPosition);
			item.setData(lightXmlObject);

			// add to table (Light XML Object)
			if (getCurrentLocation() == LOCATION_ON) {
				stcp.getItems().set(insertPosition, lightXmlObject);
			} else {
				stcp.getItems().add(insertPosition, lightXmlObject);
			}
			// add to model
			CodeType codeType = CodeType.Factory.newInstance();
			codeType.addNewCategoryReference().addNewID()
					.setStringValue(XmlBeansUtil.getXmlAttributeValue(transfers[i].lightXmlObject.xmlText(), "id=\""));
			String value = null;
			if (getCurrentLocation() == LOCATION_ON) {
				LightXmlObjectType xml = (LightXmlObjectType) stcp.getItems().get(insertPosition);
				value = XmlBeansUtil.getTextOnMixedElement(xml.getLabelList().get(0));
			} else {
				value = "";
			}
			codeType.setValue(value);
			
			try {
				if (getCurrentLocation() == LOCATION_ON) {
					codeSchemeEditor.modelImpl.getDocument().getCodeScheme().getCodeList()
							.set(insertPosition, codeType);
				} else {
					codeSchemeEditor.modelImpl.getDocument().getCodeScheme().getCodeList()
							.add(insertPosition, codeType);
				}
			} catch (DDIFtpException e) {
				new DDIFtpException(Messages.getString("CodeSchemeEditor.mess.CodeSchemeRetreiveError"), e);
				return false;
			}
		}
		
		// 4. refresh table
		if (transfers.length > 0) {
			if (log.isDebugEnabled()) {
				try {
					log.debug("Table updated, codes: "
							+ codeSchemeEditor.modelImpl.getDocument().getCodeScheme().getCodeList().size()
							+ ", table: " + table.getItemCount() + ", contentProvider: " + stcp.getItems().size());
				} catch (DDIFtpException e) {
					new DDIFtpException(Messages.getString("CodeSchemeEditor.mess.CodeSchemeRefreshError"), e);
					return false;
				}
			}

			stcp.inputChanged(getViewer(), null, stcp.getItems());
			((TableViewer) getViewer()).refresh(true);
			codeSchemeEditor.editorStatus.setChanged();

		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		boolean result = LightXmlObjectTransfer.getInstance().isSupportedType(transferType)
				&& target instanceof CodeTypeImpl;
		if (!result) {
			log.warn("Not validating, target: " + target + ", transferType: " + transferType.type + ", operation: "
					+ operation);
		}
		return true;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		super.dragEnter(event);
		// do nothing
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		super.dragOver(event);
		// do nothing
	}
	
    public void dropAccept(DropTargetEvent event) {
    	super.dropAccept(event);
    	// do nothing
    }

}
