/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.data.widget;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;

public interface IDataAdapterRunnable {
	
	public boolean isNotRunning();

	public void runReport(DataAdapterDescriptor myDataAdapter);
	public void runReport(DataAdapterDescriptor myDataAdapter, boolean prmDirty);
	
	/**
	 * Return the JasperReportsConfiguration of the loaded report
	 * 
	 * @return a JasperReportsConfiguration
	 */
	public JasperReportsConfiguration getConfiguration();
}
