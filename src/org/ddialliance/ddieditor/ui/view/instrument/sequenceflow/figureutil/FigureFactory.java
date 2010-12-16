package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.dbxml.instrument.IfThenElseDao;
import org.ddialliance.ddieditor.ui.model.ElementType;
import org.ddialliance.ddieditor.ui.model.instrument.IfThenElse;
import org.ddialliance.ddieditor.ui.model.instrument.Sequence;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.IfThenElseFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.QuestionConstructFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.SequenceFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.StatementItemFigure;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class FigureFactory {
	static Font labelFont = new Font(null, "Arial", 10, SWT.None);
	ElementType elmentType;
	Map<String, LightXmlObjectType> allCcsMap;

	public FigureFactory() throws Exception {
		// TODO change this when reference resolution is resolved to use id,
		// version, parentid and parentversion
		allCcsMap = DdiManager.getInstance().getControlConstructsLightasMap();
		// DrawerFigure
	}

	public ActivityFigure createControlConstruct(
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		this.elmentType = getElementType(lightXmlObject.getElement());

		if (elmentType.equals(ElementType.QUESTION_CONSTRUCT)) {
			return createQuestionConstructFigure(lightXmlObject);
		} else if (elmentType.equals(ElementType.IF_THEN_ELSE)) {
			return createIfThenElseFigure(lightXmlObject);
		} else if (elmentType.equals(ElementType.SEQUENCE)) {
			return createSequenceFigure(lightXmlObject);
		} else if (elmentType.equals(ElementType.STATEMENT_ITEM)) {
			return createStatementItemFigure(lightXmlObject);
		}
		return null;
	}

	public QuestionConstructFigure createQuestionConstructFigure(
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		// label
		QuestionConstructFigure figure = new QuestionConstructFigure(
				getHeaderLabel(), getAttributeLabels(lightXmlObject),
				lightXmlObject);

		return figure;
	}

	public IfThenElseFigure createIfThenElseFigure(
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		// label
		IfThenElseFigure figure = new IfThenElseFigure(getHeaderLabel(),
				getAttributeLabels(lightXmlObject), lightXmlObject);

		// dessions
		IfThenElse ifThenElse = null;
		try {
			ifThenElse = (IfThenElse) (new IfThenElseDao().getModel(
					lightXmlObject.getId(), lightXmlObject.getVersion(),
					lightXmlObject.getParentId(),
					lightXmlObject.getParentVersion()));

			// refs
			if (ifThenElse != null) { // guard
				// then
				ReferenceType then = ifThenElse.getThenReference();
				LightXmlObjectType thenLight = resolveCcReference(then);
				figure.then = createControlConstruct(thenLight);
				setIfThenElseLabel(figure.then, "Then");

				// else
				ReferenceType elze = ifThenElse.getElseReference();
				if (elze != null) {
					LightXmlObjectType elseLight = resolveCcReference(elze);
					figure.elze = createControlConstruct(elseLight);
					setIfThenElseLabel(figure.elze, "Else");
				}

				// TODO elseif in ddi-3.2
			}

		} catch (Exception e) {
			throw new DDIFtpException(e);
		}
		return figure;
	}

	private void setIfThenElseLabel(IFigure activityFigure, String prefix) {
		for (IFigure iFigure : (List<IFigure>) activityFigure.getChildren()) {
			if (iFigure instanceof Label) {
				String str = ((Label) iFigure).getText();
				((Label) iFigure).setText(prefix + ": " + str);
			}
		}
	}

	public SequenceFigure createSequenceFigure(LightXmlObjectType lightXmlObject)
			throws DDIFtpException {
		// label
		SequenceFigure figure = new SequenceFigure(getHeaderLabel(),
				getAttributeLabels(lightXmlObject), lightXmlObject);

		return figure;
	}

	private StatementItemFigure createStatementItemFigure(
			LightXmlObjectType lightXmlObject) throws DDIFtpException {
		// label
		StatementItemFigure figure = new StatementItemFigure(getHeaderLabel(),
				getAttributeLabels(lightXmlObject), lightXmlObject);

		return figure;
	}

	private ElementType getElementType(String localname) throws DDIFtpException {
		return ElementType.getElementType(localname);
	}

	private LightXmlObjectType resolveCcReference(ReferenceType ref) {
		ReferenceResolution refResolve = new ReferenceResolution(ref);
		return allCcsMap.get(refResolve.getId());
	}

	private Label getHeaderLabel() throws DDIFtpException {
		Label label = new Label();
		label.setIcon(getImage());
		label.setFont(labelFont);
		label.setText(elmentType.getTranslatedDisplayMessageEntry());
		// label.setTextAlignment(PositionConstants.CENTER);
		return label;
	}

	private List<Label> getAttributeLabels(LightXmlObjectType lightXmlObject)
			throws DDIFtpException {
		List<Label> result = new ArrayList<Label>();

		// identification
		String type;
		String label;
		if (lightXmlObject.getLabelList().isEmpty()) {
			type = "ID";
			label = lightXmlObject.getId();
		} else {
			LabelType labelLang = ((LabelType) (XmlBeansUtil
					.getDefaultLangElement(lightXmlObject.getLabelList())));
			label = XmlBeansUtil.getTextOnMixedElement(labelLang);
			type = "Label";
		}
		result.add(getAttibuteLabel(type, label));

		return result;
	}

	private Label getAttibuteLabel(String type, String text)
			throws DDIFtpException {
		Label label = new Label();
		label.setFont(labelFont);
		label.setText(type + ": " + text);
		// label.setIcon(getImage());

		return label;
	}

	private Image getImage() {
		String imagePath = "";
		if (this.elmentType.equals(ElementType.QUESTION_CONSTRUCT)) {
			imagePath = "icons/instrument-icon/Activity.gif";
		} else if (this.elmentType.equals(ElementType.IF_THEN_ELSE)) {
			imagePath = "icons/instrument-icon/mul_decision_palette.gif";
		} else if (this.elmentType.equals(ElementType.SEQUENCE)) {
			imagePath = "icons/instrument-icon/SubProcess.gif";
		} else if (this.elmentType.equals(ElementType.STATEMENT_ITEM)) {
			imagePath = "icons/instrument-icon/DataObject.gif";
		}
		return ResourceManager.getPluginImageDescriptor(Activator.getDefault(),
				imagePath).createImage();
	}
}
