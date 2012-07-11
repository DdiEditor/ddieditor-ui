package org.ddialliance.ddieditor.ui.command;

import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.ConceptualComponentType;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseSchemeType;
import org.ddialliance.ddi3.xml.xmlbeans.conceptualcomponent.UniverseType;
import org.ddialliance.ddi3.xml.xmlbeans.datacollection.DataCollectionType;
import org.ddialliance.ddi3.xml.xmlbeans.instance.DDIInstanceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.BaseLogicalProductType;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.LogicalProductDocument;
import org.ddialliance.ddi3.xml.xmlbeans.logicalproduct.LogicalProductType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.IdentifiedStructuredStringType;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.LabelType;
import org.ddialliance.ddi3.xml.xmlbeans.studyunit.StudyUnitType;
import org.ddialliance.ddieditor.logic.identification.IdentificationManager;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.util.DdiEditorConfig;
import org.ddialliance.ddieditor.util.LightXmlObjectUtil;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * RCP entry point to create a ddi-l minimal study unit
 */
public class NewDDI3File extends org.eclipse.core.commands.AbstractHandler {
	private Log log = LogFactory.getLog(LogType.EXCEPTION, NewDDI3File.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// new instance
			DDIInstanceDocument ddiInstanceDoc = DDIInstanceDocument.Factory
					.newInstance();
			ddiInstanceDoc.addNewDDIInstance();
			IdentificationManager.getInstance().addIdentification(
					ddiInstanceDoc.getDDIInstance(), null, null);
			IdentificationManager.getInstance().addVersionInformation(
					ddiInstanceDoc.getDDIInstance(), null, null);
			XmlBeansUtil.addXsiAttributes(ddiInstanceDoc);

			// study unit
			StudyUnitType studyUnitType = ddiInstanceDoc.getDDIInstance()
					.addNewStudyUnit();
			IdentificationManager.getInstance().addIdentification(
					studyUnitType, ElementType.STUDY_UNIT.getIdPrefix(), null);
			IdentificationManager.getInstance().addVersionInformation(
					studyUnitType, null, null);

			// citation
			studyUnitType.addNewCitation().addNewTitle();

			// abstract
			IdentifiedStructuredStringType abstractType = studyUnitType
					.addNewAbstract();
			IdentificationManager.getInstance().addIdentification(abstractType,
					ElementType.ABSTRACT.getIdPrefix(), null);
			abstractType.addNewContent();

			// purpose
			IdentifiedStructuredStringType purposeType = studyUnitType
					.addNewPurpose();
			IdentificationManager.getInstance().addIdentification(purposeType,
					ElementType.PURPOSE.getIdPrefix(), null);
			purposeType.addNewContent();

			//
			// conceptual component
			//
			ConceptualComponentType comp = studyUnitType
					.addNewConceptualComponent();
			IdentificationManager.getInstance().addIdentification(comp,
					ElementType.CONCEPTUAL_COMPONENT.getIdPrefix(), null);
			// IdentificationManager.getInstance().addVersionInformation(comp,
			// null, null);

			// default universe
			UniverseSchemeType unis = comp.addNewUniverseScheme();
			IdentificationManager.getInstance().addIdentification(unis,
					ElementType.UNIVERSE_SCHEME.getIdPrefix(), null);
			// IdentificationManager.getInstance().addVersionInformation(unis,
			// null, null);

			UniverseType univ = unis.addNewUniverse();
			IdentificationManager.getInstance().addIdentification(univ,
					ElementType.UNIVERSE.getIdPrefix(), null);
			IdentificationManager.getInstance().addVersionInformation(univ,
					null, null);
			LabelType label = univ.addNewLabel();
			XmlBeansUtil.addTranslationAttributes(label,
					DdiEditorConfig.get(DdiEditorConfig.DDI_LANGUAGE), false,
					true);
			XmlBeansUtil.setTextOnMixedElement(label, "Default");

			// stud universe reference
			IdentificationManager.getInstance().addReferenceInformation(
					studyUnitType.addNewUniverseReference(),
					LightXmlObjectUtil.createLightXmlObject(unis.getAgency(),
							unis.getId(), unis.getVersion(), univ.getId(),
							univ.getVersion(),
							ElementType.UNIVERSE.getElementName()));
			//
			// data collection
			//
			DataCollectionType datacType = studyUnitType.addNewDataCollection();
			IdentificationManager.getInstance().addIdentification(datacType,
					ElementType.DATA_COLLECTION.getIdPrefix(), null);
			// IdentificationManager.getInstance().addVersionInformation(
			// datacType, null, null);
			
			//
			// logical product
			//
			BaseLogicalProductType baseLogipType = studyUnitType
					.addNewBaseLogicalProduct();
			LogicalProductType logipType = (LogicalProductType) baseLogipType
					.substitute(LogicalProductDocument.type
							.getDocumentElementName(), LogicalProductType.type);
			IdentificationManager.getInstance().addIdentification(logipType,
					ElementType.LOGICAL_PRODUCT.getIdPrefix(), null);
			// IdentificationManager.getInstance().addVersionInformation(
			// logipType, null, null);

			CommandHelper.createResource(ddiInstanceDoc);
		} catch (Exception e) {
			throw new ExecutionException(e.getLocalizedMessage());
		}
		return null;
	}
}
