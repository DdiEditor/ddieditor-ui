package org.ddialliance.ddieditor.ui.dbxml.variable;

import java.util.List;

import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.VariableDocument;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.dbxml.IDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.IModel;
import org.ddialliance.ddieditor.ui.model.Model;
import org.ddialliance.ddieditor.ui.model.variable.Variable;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;

/**
 * Variable DAO ;- )
 */
public class VariableDao implements IDao {
	@Override
	public List<LightXmlObjectType> getLightXmlObject(LightXmlObjectType parent) throws Exception {
		return getLightXmlObject("", "", parent.getId(), parent.getVersion());
	}

	@Override
	public List<LightXmlObjectType> getLightXmlObject(String id, String version, String parentId, String parentVersion)
			throws Exception {
		return DdiManager.getInstance().getVariablesLight(id, version, parentId, parentVersion).getLightXmlObjectList()
				.getLightXmlObjectList();
	}
	
	public List<LightXmlObjectType> getLightXmlObjectPlus(String id,
			String version, String parentId, String parentVersion)
			throws Exception {
		return DdiManager.getInstance()
				.getVariablesLightPlus(id, version, parentId, parentVersion)
				.getLightXmlObjectList().getLightXmlObjectList();
	}

	@Override
	public Variable getModel(String id, String version, String parentId, String parentVersion) throws Exception {
		VariableDocument doc = DdiManager.getInstance().getVariable(id, version, parentId, parentVersion);
		return new Variable(doc, parentId, parentVersion);
	}

	@Override
	public Variable create(String id, String version, String parentId, String parentVersion) throws Exception {
		VariableDocument doc = VariableDocument.Factory.newInstance();
		doc.addNewVariable();
		IdentificationManager.getInstance().addIdentification(doc.getVariable(),
				ElementType.getElementType("Variable").getIdPrefix(), "");
		IdentificationManager.getInstance().addVersionInformation(doc.getVariable(), null, null);
		return new Variable(doc, parentId, parentVersion);
	}

	@Override
	public void create(IModel model) throws DDIFtpException {
		DdiManager.getInstance().createElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"VariableScheme");
	}

	@Override
	public void update(IModel model) throws DDIFtpException {
		// IdentificationManager.getInstance().addVersionInformation(
		// ((VariableDocument) model.getDocument()).getVariable(), null,
		// null);

		DdiManager.getInstance().updateElement(model.getDocument(), model.getId(), model.getVersion());
		((Model) model).setVersion(XmlBeansUtil.getXmlAttributeValue(model.getDocument().xmlText(), "version=\""));
	}

	@Override
	public void delete(String id, String version, String parentId, String parentVersion) throws Exception {
		Model model = getModel(id, version, parentId, parentVersion);
		DdiManager.getInstance().deleteElement(model.getDocument(), model.getParentId(), model.getParentVersion(),
				"VariableScheme");
	}
}
