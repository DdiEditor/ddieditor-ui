package org.ddialliance.ddieditor.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.ddialliance.ddieditor.ui.editor.category.CategoryEditor;
import org.ddialliance.ddieditor.ui.editor.category.CategorySchemeEditor;
import org.ddialliance.ddieditor.ui.editor.code.CodeSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptEditor;
import org.ddialliance.ddieditor.ui.editor.concept.ConceptSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.ComputationItemEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.ControlConstructSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.IfThenElseEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.InstrumentEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.LoopEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.QuestionConstructEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.RepeatUntilEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.RepeatWhileEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.SequenceEditor;
import org.ddialliance.ddieditor.ui.editor.instrument.StatementItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.MultipleQuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionItemEditor;
import org.ddialliance.ddieditor.ui.editor.question.QuestionSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.study.StudyUnitEditor;
import org.ddialliance.ddieditor.ui.editor.universe.UniverseEditor;
import org.ddialliance.ddieditor.ui.editor.universe.UniverseSchemeEditor;
import org.ddialliance.ddieditor.ui.editor.variable.VariableEditor;
import org.ddialliance.ddieditor.ui.editor.variable.VariableSchemeEditor;
import org.ddialliance.ddieditor.ui.view.variable.questionrelation.VariableQuestionView;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ViewManager {
	private static Log log = LogFactory.getLog(LogType.SYSTEM,
			ViewManager.class);
	private static Hashtable<String, List<String>> editorWiews = new Hashtable<String, List<String>>();
	private static ViewManager instance;
	private List<String> viewUpdateList;

	private ViewManager() {
		// Initialise views of editor:
		editorWiews.put(CategorySchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, CategoryView.ID)));	
		editorWiews.put(CategoryEditor.ID, new ArrayList<String>(Arrays.asList(CategoryView.ID)));
		editorWiews.put(CodeSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, CodeView.ID)));
		editorWiews.put(ConceptSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, ConceptView.ID)));
		editorWiews.put(ConceptEditor.ID, new ArrayList<String>(Arrays.asList(ConceptView.ID)));
		editorWiews.put(ComputationItemEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(ControlConstructSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(IfThenElseEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(InstrumentEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, InstrumentView.ID)));
		editorWiews.put(LoopEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(QuestionConstructEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(RepeatUntilEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(RepeatWhileEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(SequenceEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(StatementItemEditor.ID, new ArrayList<String>(Arrays.asList(InstrumentView.ID)));
		editorWiews.put(QuestionSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, QuestionItemView.ID)));
		editorWiews.put(MultipleQuestionItemEditor.ID, new ArrayList<String>(Arrays.asList(QuestionItemView.ID)));
		editorWiews.put(QuestionItemEditor.ID, new ArrayList<String>(Arrays.asList(QuestionItemView.ID, VariableQuestionView.ID)));
		editorWiews.put(StudyUnitEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID)));
		editorWiews.put(UniverseSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, UniverseView.ID)));
		editorWiews.put(UniverseEditor.ID, new ArrayList<String>(Arrays.asList(UniverseView.ID)));
		editorWiews.put(VariableSchemeEditor.ID, new ArrayList<String>(Arrays.asList(InfoView.ID, VariableView.ID)));
		editorWiews.put(VariableEditor.ID, new ArrayList<String>(Arrays.asList(VariableView.ID)));
	}

	public static synchronized ViewManager getInstance() {
		if (instance == null) {
			instance = new ViewManager();
			instance.viewUpdateList = new ArrayList<String>();
		}
		return instance;
	}
	
	private List<String> getViewsOfEditor(String editorID) {
		// Get views of editor:
		List<String> viewList = editorWiews.get(editorID);
		return viewList;
	}

	public void addViewsToRefresh(String editorID) {
		
		// add view to refresh list
		List<String> views = getViewsOfEditor(editorID);
		for (int i = 0; i < views.size(); i++) {
			viewUpdateList.add(views.get(i));
		}
	}
	
	public void refesh(String iD) {
		for (Iterator<String> iterator = viewUpdateList.iterator(); iterator
				.hasNext();) {
			String updateViewID = iterator.next();

			// find views
			IWorkbenchWindow windows[] = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			IWorkbenchPage page = null;
			for (int i = 0; i < windows.length; i++) {
				page = windows[i].getActivePage();
			}

			// guard
			if (page == null) {
				return;
			}

			// refresh views
			IViewReference[] viewRefs = page.getViewReferences();
			for (int j = 0; j < viewRefs.length; j++) {
				IViewPart viewPart = page.findView(viewRefs[j].getId());
				if (viewPart != null && viewRefs[j].getId().equals(updateViewID) &&
						(viewPart instanceof View || viewPart instanceof VariableQuestionView)) {
					RefreshRunnable longJob = new RefreshRunnable(viewPart);
					BusyIndicator.showWhile(PlatformUI.getWorkbench()
							.getDisplay(), longJob);
					iterator.remove();
					if (log.isDebugEnabled()) {
						log.debug("View refreshed: "+ viewPart.getClass().getName());
					}
				}
			}
		}
	}
	
	/**
	 * Runnable wrapping view refresh to enable RCP busy indicator
	 */
	class RefreshRunnable implements Runnable {
		IWorkbenchPart part;

		RefreshRunnable(IWorkbenchPart part) {
			this.part = part;
		}

		@Override
		public void run() {
			if (part instanceof View) {
				((View) part).refreshView();
			} else if (part instanceof VariableQuestionView) {
				((VariableQuestionView) part).refreshView();
			}

		}
	}

}
