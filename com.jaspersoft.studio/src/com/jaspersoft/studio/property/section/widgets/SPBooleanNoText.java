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
package com.jaspersoft.studio.property.section.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.jaspersoft.studio.property.section.AbstractSection;

public class SPBooleanNoText<T extends IPropertyDescriptor> extends SPBoolean<T> {
	
	public SPBooleanNoText(Composite parent, AbstractSection section, T pDescriptor) {
		super(parent, section, pDescriptor);
	}
	

	public void createComponent(Composite parent) {
		super.createComponent(parent);
		cmb3Bool.setText("");
	}

}
