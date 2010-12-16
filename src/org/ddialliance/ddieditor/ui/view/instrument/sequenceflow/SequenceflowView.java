package org.ddialliance.ddieditor.ui.view.instrument.sequenceflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ddialliance.ddi3.xml.xmlbeans.datacollection.SequenceDocument;
import org.ddialliance.ddi3.xml.xmlbeans.reusable.ReferenceType;
import org.ddialliance.ddieditor.logic.urn.ddi.ReferenceResolution;
import org.ddialliance.ddieditor.model.DdiManager;
import org.ddialliance.ddieditor.model.lightxmlobject.LabelType;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectListDocument;
import org.ddialliance.ddieditor.model.lightxmlobject.LightXmlObjectType;
import org.ddialliance.ddieditor.ui.Activator;
import org.ddialliance.ddieditor.ui.util.swtdesigner.ResourceManager;
import org.ddialliance.ddieditor.ui.view.Messages;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.ActivityFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.Chart;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.ConnectionFigure;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.FigureFactory;
import org.ddialliance.ddieditor.ui.view.instrument.sequenceflow.figureutil.MouseFigureAction;
import org.ddialliance.ddiftp.util.xml.XmlBeansUtil;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.PrintOperation;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class SequenceflowView extends ViewPart {
	FigureCanvas figureCanvas;
	private Chart chart;
	private Composite parent;
	private Combo combo;

	@Override
	public void init(IViewSite site) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site);
	}

	// @Override
	// public IViewSite getViewSite() {
	// try {
	// ((SequenceflowView)super.getViewSite()).intCombo();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return super.getViewSite();
	// }

	@Override
	public void createPartControl(Composite parent) {
		// swt
		GC gc = new GC(parent.getShell());
		gc.setAntialias(SWT.ON);
		this.parent = parent;

		// canvas
		figureCanvas = new FigureCanvas(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		FreeformViewport freeformViewport = new FreeformViewport();
		figureCanvas.setViewport(freeformViewport);

		// menu
		// Menu menu = new Menu (figureCanvas.getShell(), SWT.POP_UP);
		// figureCanvas.setMenu(menu);
		// // for leaf menu item
		// MenuItem itemA = new MenuItem(figureCanvas.getMenu(), SWT.PUSH);
		// itemA.setText("ItemA");
		// itemA.addListener(SWT.Selection, new Listener() {
		// @Override
		// public void handleEvent(Event event) {
		// }
		// });
		//
		// // for hierarchical menu item
		// MenuItem itemB = new MenuItem(menu, SWT.CASCADE);
		// itemB.setText("ItemB");
		// final Menu subMenuB = new Menu(figureCanvas.getShell(),
		// SWT.DROP_DOWN);
		// itemB.setMenu(subMenuB);
		// itemB.addListener(SWT.Show, new Listener() {
		// @Override
		// public void handleEvent(Event event) {
		// MenuItem item = new MenuItem(subMenuB, SWT.PUSH);
		// }
		// });
		//
		// // diagram
		// chart = new Chart();
		// freeformViewport.setContents(chart);

		// actions
		initActions();
	}

	private void initActions() {
		// define actions
		// sequence selection
		IContributionItem seqChooser = new ControlContribution("id") {

			@Override
			protected Control createControl(Composite parent) {
				combo = new Combo(parent, SWT.READ_ONLY);
				combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
						false, 1, 1));
				try {
					intCombo();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// listeners
				combo.addFocusListener(new FocusListener() {
					@Override
					public void focusLost(FocusEvent e) {
						// do nothing
					}

					@Override
					public void focusGained(FocusEvent e) {
						try {
							intCombo();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				combo.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						List<LightXmlObjectType> seqs = ((List<LightXmlObjectType>) (combo
								.getData()));
						if (seqs.isEmpty()) { // guad
							return;
						}
						LightXmlObjectType seq = seqs.get(combo
								.getSelectionIndex());
						if (seq == null) { // guad
							return;
						}
						try {
							drawDiagram(seq);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// do nothing
					}
				});
				return combo;
			}

			@Override
			public boolean isDynamic() {
				return true;
			}
		};

		// zoom in
		Action zoomInAction = new Action(
				Messages.getString("sequence.diagram.zoomin")) { //$NON-NLS-1$)
			public void run() {
				if (chart.getScale() < 5d) {
					chart.setScale(chart.getScale() + 0.1d);
				}
			}
		};
		zoomInAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/instrument-icon/increasewdt.gif"));

		// zoom out
		Action zoomOutAction = new Action(
				Messages.getString("sequence.diagram.zoomout")) { //$NON-NLS-1$)
			public void run() {
				if (chart.getScale() > 0.1d) {
					chart.setScale(chart.getScale() - 0.1d);
				}
			}
		};
		zoomOutAction.setImageDescriptor(ResourceManager
				.getPluginImageDescriptor(Activator.getDefault(),
						"icons/instrument-icon/decreasewdt.gif"));

		// zoom reset
		Action zoomReset = new Action(
				Messages.getString("sequence.diagram.zoomreset")) { //$NON-NLS-1$)
			public void run() {
				chart.setScale(1d);
			}
		};
		zoomReset.setImageDescriptor(ResourceManager.getPluginImageDescriptor(
				Activator.getDefault(), "icons/instrument-icon/resetwdt.gif"));

		// print
		Action print = new Action(Messages.getString("sequence.diagram.print")) { //$NON-NLS-1$)
			public void run() {
				PrintDialog printDialog = new PrintDialog(parent.getShell(),
						SWT.NONE);
				printDialog.setText("Print");
				PrinterData printerData = printDialog.open();

				if (!(printerData == null)) {
					Printer p = new Printer(printerData);
					PrintOperation op = new PrintFigureOperation(p, chart);
					op.setPrintMargin(new Insets(0, 0, 0, 0));
					op.run("Test"); // "Test" is the print job name
					p.dispose();
				}
			}
		};
		print.setImageDescriptor(ResourceManager.getPluginImageDescriptor(
				Activator.getDefault(), "icons/sourceEditor.gif"));

		// toolbar
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(seqChooser);
		toolbarManager.add(zoomReset);
		toolbarManager.add(zoomOutAction);
		toolbarManager.add(zoomInAction);
		toolbarManager.add(print);
	}

	public final void intCombo() throws Exception {
		// delete old value
		combo.setItems(new String[] {});
		combo.setData(null);

		// read seqs
		LightXmlObjectListDocument seqList = DdiManager.getInstance()
				.getSequencesLight(null, null, null, null);
		if (!seqList.getLightXmlObjectList().getLightXmlObjectList().isEmpty()) {
			// define combo options
			String[] options = new String[seqList.getLightXmlObjectList()
					.sizeOfLightXmlObjectArray()];
			int count = 0;
			for (LightXmlObjectType seq : seqList.getLightXmlObjectList()
					.getLightXmlObjectList()) {
				//
				String label;
				if (seq.getLabelList().isEmpty()) {
					label = seq.getId();
				} else {
					LabelType labelLang = ((LabelType) (XmlBeansUtil
							.getDefaultLangElement(seq.getLabelList())));
					label = XmlBeansUtil.getTextOnMixedElement(labelLang);
				}
				options[count] = label;
				count++;
			}
			combo.setItems(options);
			combo.setData(seqList.getLightXmlObjectList()
					.getLightXmlObjectList());
		} else {
			combo.setItems(new String[] {""});
		}
	}

	private void drawDiagram(LightXmlObjectType lightXmlObject)
			throws Exception {
		SequenceDocument seq = DdiManager.getInstance().getSequence(
				lightXmlObject.getId(), lightXmlObject.getVersion(),
				lightXmlObject.getParentId(), lightXmlObject.getVersion());

		// clear figures
		chart.removeAll();

		// create figures		
		ActivityFigure prev = null;
		List<ActivityFigure> prevList = new ArrayList<ActivityFigure>();
		FigureFactory figureFactory = new FigureFactory();
		Map<String, LightXmlObjectType> allCcsMap = DdiManager.getInstance()
				.getControlConstructsLightasMap();
		for (ReferenceType ccRef : seq.getSequence()
				.getControlConstructReferenceList()) {
			// ref to cc
			LightXmlObjectType cc = allCcsMap
					.get(new ReferenceResolution(ccRef).getId());

			// figure
			ActivityFigure current = figureFactory.createControlConstruct(cc);
			new MouseFigureAction(current);
			if (current != null) { // guard
				// location
				current.setBounds(new Rectangle(x, y, width, height));
				increaseY(current.getBounds());
				
				if (current instanceof IfThenElseFigure) {
					chart.add(current);
					createConnections(prev, current);
					//prev.clear();
					
					IfThenElseFigure ifth = (IfThenElseFigure)current;		
					
					// then
					ifth.then.setBounds(new Rectangle(x-sideMove, y, width, height));
					new MouseFigureAction(ifth.then);
					chart.add(ifth.then);
					createConnections(ifth, ifth.then);
					prev=ifth.then;
					
					// else
					if (ifth.elze!=null) {
						ifth.elze.setBounds(new Rectangle(x+sideMove, y, width, height));
						new MouseFigureAction(ifth.elze);
						chart.add(ifth.elze);
						createConnections(ifth, ifth.elze);
						prevList.add(ifth.elze);
						prevList.add(ifth.then);
						prev= null;
					} else {
						prevList.add(ifth);
						prevList.add(ifth.then);
						prev= null;
					}
					increaseY(ifth.then.getBounds());
					continue;
				}

				// connection
				if (prevList.isEmpty()) {
					createConnections(prev, current);	
				} else {
					createConnections(prevList.get(0), current);	
					createConnections(prevList.get(1), current);
					prevList.clear();
				}
				//prev.clear();
				
				// add
//				prev.add(current);
				prev = current;
				chart.add(current);
			}
		}
		
		// getting ready for new chart
		resetLocation();
	}
	
	int x= 200, y = 0, width=275, height=100, yNext=100, sideMove=(width/3)*2;
	private void increaseY(Rectangle rect){
		y = y + rect.height + yNext;
	}
	
	private void resetLocation() {
		x= 200; y = 0; width=275; height=100; yNext=100;
	}

//	private void createConnections(ActivityFigure prev, ActivityFigure current) {
//		List<ActivityFigure> prevList = new ArrayList<ActivityFigure>();
//		createConnections(prevList, current);
//	}
	
	private void createConnections(ActivityFigure prev, ActivityFigure current) {
		// connections
		ConnectionFigure con = null;
		if (prev != null) { // guard
			con = new ConnectionFigure();

			// in anchor
			con.setTargetAnchor(current.inAnchor);

			// out anchor
			//for (ActivityFigure activityFigure : prev) {
				con.setSourceAnchor(prev.outAnchor);	
			//}
		}

		// add
		if (con != null) {
			chart.add(con);
		}
	}

	@Override
	public void setFocus() {
		// do nothing
	}
}
