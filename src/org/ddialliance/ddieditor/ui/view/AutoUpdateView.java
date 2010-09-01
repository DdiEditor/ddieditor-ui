package org.ddialliance.ddieditor.ui.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ddialliance.ddieditor.persistenceaccess.PersistenceManager;
import org.ddialliance.ddieditor.ui.util.DialogUtil;
import org.ddialliance.ddiftp.util.DDIFtpException;
import org.ddialliance.ddiftp.util.log.Log;
import org.ddialliance.ddiftp.util.log.LogFactory;
import org.ddialliance.ddiftp.util.log.LogType;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Behind the scenes auto update of view when no. of resources change.
 * Implemented via part listener.
 */
public class AutoUpdateView implements IPartListener, IStartup {
	private static final Log log = LogFactory.getLog(LogType.SYSTEM,
			AutoUpdateView.class);
	Map<String, Integer> resourceViewMap = Collections.synchronizedMap(new HashMap<String, Integer>());
	String currentViewId = null;
	Integer count = -1;

	@Override
	public void partActivated(IWorkbenchPart part) {
		checkRefresh(part);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		checkRefresh(part);
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (!(part instanceof View)) {
			return;
		}
		resourceViewMap.remove(part.getClass().getName());
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// do nothing
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		// do nothing
		log.debug(part);
	}

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().addPartListener(
									new AutoUpdateView());
				} catch (Exception e) {
					DialogUtil.errorDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getShell(), "", null, e
							.getMessage(), e);
				}
			}
		});
	}

	private synchronized void checkRefresh(IWorkbenchPart part) {
		if (!(part instanceof View)) {
			return;
		}
		currentViewId = part.getClass().getName();
		count = resourceViewMap.get(currentViewId);
		if (count == null) {
			doInit(currentViewId);
			return;
		}

		int tmpResourceCount = 0;
		try {
			tmpResourceCount = PersistenceManager.getInstance().getResources()
					.size();
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), currentViewId,
					null, e.getMessage(), e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Part: " + currentViewId + ", count: " + count
					+ ", tmpResourceCount: " + tmpResourceCount);
		}
		if (count != tmpResourceCount) {
			resourceViewMap.put(currentViewId, tmpResourceCount);
			RefreshRunnable longJob = new RefreshRunnable(part);
			BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(),
					longJob);
		}
	}

	private void doInit(String id) {
		try {
			count = PersistenceManager.getInstance().getResources().size();
			resourceViewMap.put(id, count);
		} catch (DDIFtpException e) {
			DialogUtil.errorDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), id, null, e
					.getMessage(), e);
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
			((View) part).refreshView();
		}
	}
}
