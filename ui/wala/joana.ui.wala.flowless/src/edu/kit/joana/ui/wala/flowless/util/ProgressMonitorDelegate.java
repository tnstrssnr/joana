/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.kit.joana.ui.wala.flowless.util;

import com.ibm.wala.util.MonitorUtil.IProgressMonitor;

/**
 * A Wrapper around an Eclipse IProgressMonitor
 */
public class ProgressMonitorDelegate implements IProgressMonitor {

  public static ProgressMonitorDelegate createProgressMonitorDelegate(org.eclipse.core.runtime.IProgressMonitor d) {
    if (d == null) {
      throw new IllegalArgumentException("d is null");
    }
    return new ProgressMonitorDelegate(d);
  }

  private final org.eclipse.core.runtime.IProgressMonitor delegate;

  private ProgressMonitorDelegate(org.eclipse.core.runtime.IProgressMonitor d) {
    this.delegate = d;
  }

  public void beginTask(String task, int totalWork) {
    delegate.beginTask(task, totalWork);

  }

  public boolean isCanceled() {
    return delegate.isCanceled();
  }

  public void done() {
    delegate.done();
  }

  public void worked(int units) {
    delegate.worked(units);
  }

/** BEGIN Custom change: subtasks and canceling */
  public void subTask(String subTask) {
    delegate.subTask(subTask);
  }

  public void cancel() {
    delegate.setCanceled(true);
  }
/** END Custom change: subtasks and canceling */

  @Override
  public String getCancelMessage() {
	return "canceled.";
  }
  
}
